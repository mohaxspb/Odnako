package ru.kuchanov.odnako.download;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlCleanerException;
import org.htmlcleaner.TagNode;

import android.text.Html;
import android.util.Log;

import ru.kuchanov.odnako.lists_and_utils.ArtInfo;

public class HtmlHelper
{
	String tag = "debug";

	TagNode rootNode;
	public String htmlString;

	HtmlCleaner cleaner;

	//	public static int NUM_OF_ARTS_ON_CUR_PAGE;

	public HtmlHelper(URL htmlPage) throws IOException
	{
		cleaner = new HtmlCleaner();
		try
		{
			//			System.out.println("HtmlHelper constructor URL: " + htmlPage.toString());
			rootNode = cleaner.clean(htmlPage);
			htmlString = cleaner.getInnerHtml(rootNode);
		} catch (HtmlCleanerException e)
		{
			//System.out.println(e.getMessage());
			System.out
			.println("Error in HtmlHelper while try to clean HTML. May be FileNot found or NOconnection exception");
		} catch (FileNotFoundException e)
		{
			System.out.println("FileNotFoundException at HtmlHelper");
			System.out.println(e.getMessage());
		}
	}

	public boolean isAuthor()
	{
		String attrValue = "description";
		String attrName = "itemprop";
		TagNode tag = this.rootNode.findElementByAttValue(attrName, attrValue, true, false);
		if (tag.getAttributeByName("content").equals("Публикации по тегу")
		|| tag.getAttributeByName("content").equals("Блоги"))
		{
			return false;
		}
		//so it's author
		else
		{
			return true;
		}
	}

	ArrayList<ArrayList<String>> getAllCategoriesAsList()
	{
		ArrayList<ArrayList<String>> allCategoriesAsList = new ArrayList<ArrayList<String>>();
		TagNode[] liElements;
		TagNode allCategoriesUl = null;
		String CSSClassname = "l-full-col outlined-hard-flipped";

		allCategoriesUl = rootNode.findElementByAttValue("class", CSSClassname, true, false);

		liElements = allCategoriesUl.getChildTags();

		for (int i = 0; i < liElements.length; i++)
		{
			ArrayList<String> category = new ArrayList<String>();
			category.add(liElements[i].findElementByName("a", true).getAttributeByName("title"));
			category.add(liElements[i].findElementByName("a", true).getAttributeByName("href"));

			allCategoriesAsList.add(category);
		}

		return allCategoriesAsList;
	}

	ArrayList<ArrayList<String>> getAllAuthorsAsList()
	{
		ArrayList<ArrayList<String>> allAuthorsAsList = new ArrayList<ArrayList<String>>();
		TagNode[] liElements;
		TagNode allAuthorsUl = null;
		String CSSClassname = "news-wrap clearfix clearfix l-3col packery block block-top l-tripple-cols";

		allAuthorsUl = rootNode.findElementByAttValue("class", CSSClassname, true, false);

		liElements = allAuthorsUl.getChildTags();

		for (int i = 0; i < liElements.length; i++)
		{
			ArrayList<String> authorInfo = new ArrayList<String>();

			authorInfo.add(liElements[i].findElementByName("a", true).getAttributeByName("title"));
			authorInfo.add(liElements[i].findElementByName("a", true).getAttributeByName("href"));
			if (liElements[i].findElementByName("img", true) != null)
			{
				authorInfo.add(liElements[i].findElementByName("img", true).getAttributeByName("src"));
			}
			else
			{
				authorInfo.add("empty");
			}
			if (liElements[i].findElementByName("p", true).getText().toString() != "")
			{
				authorInfo.add(liElements[i].findElementByName("p", true).getText().toString());
			}
			else
			{
				authorInfo.add("empty");
			}
			allAuthorsAsList.add(authorInfo);
		}

		return allAuthorsAsList;
	}

	public ArrayList<ArtInfo> getAllArtsInfoFromPage()
	{
		ArrayList<ArtInfo> allArtsInfo = new ArrayList<ArtInfo>();

		TagNode[] liElements = null;
		TagNode[] allArtsUl = null;
		//		String CSSClassname = "news-wrap clearfix clearfix l-3col packery block block-top l-tripple-cols";
		String CSSClassname = "news-wrap clearfix clearfix l-3col packery block l-tripple-cols";

		allArtsUl = rootNode.getElementsByAttValue("class", CSSClassname, true, false);

		if (allArtsUl.length > 1)
		{
			liElements = allArtsUl[1].getChildTags();
		}
		else
		{
			liElements = allArtsUl[0].getChildTags();
		}

		for (int i = 0; i < liElements.length; i++)
		{
			String[] info = new String[5];

			TagNode element = liElements[i];
			TagNode element1 = element.findElementByAttValue("class", "m-news-info", true, true);
			TagNode element2 = element1.findElementByAttValue("class", "m-news-text", true, true);
			TagNode element3 = element2.findElementByName("a", true);

			TagNode[] imgEl = element.getElementsByName("img", true);

			TagNode author = element.findElementByAttValue("class", "m-news-author-wrap", true, false);
			TagNode[] author1 = author.getElementsByName("a", true);

			info[0] = element3.getAttributeByName("href").toString();
			info[1] = Html.fromHtml(element3.getAttributeByName("title").toString()).toString();
			//System.out.println(output[i][1]);

			if (imgEl.length == 0)
			{
				info[2] = "empty";
			}
			else
			{
				String imgSrc = imgEl[0].getAttributeByName("src").toString();
				if (imgSrc.startsWith("/i/"))
				{
					imgSrc = "http://odnako.org" + imgSrc;
				}
				info[2] = imgSrc;
			}
			if (author1.length == 0)
			{
				info[3] = "empty";
				info[4] = "empty";
			}
			else
			{
				info[3] = author1[0].getAttributeByName("href");
				info[4] = Html.fromHtml(author1[0].getAttributeByName("title")).toString();
			}

			allArtsInfo.add(new ArtInfo(info));
		}

		return allArtsInfo;
	}

	public String getCategoryImage()
	{
		String ImageUrl;

		TagNode imageTag = this.rootNode.findElementByAttValue("class", "l-left header-news-banner-item-pic", true,
		false);

		ImageUrl = imageTag.getAttributeByName("src");

		return ImageUrl;
	}

	public String getAuthorsWho()
	{
		//		String className="Description";
		//		TagNode descrTag=rootNode.findElementByAttValue("name", className, true, false);
		//		String who=descrTag.getAttributeByName("content");
		String className = "section last";
		TagNode descrTag = rootNode.findElementByAttValue("class", className, true, false);
		TagNode innerDiv = descrTag.getChildTags()[0];
		String who = cleaner.getInnerHtml(innerDiv);
		return who;
	}

	public String getAuthorsBigImg()
	{
		TagNode bigImgTag = rootNode.findElementByAttValue("itemprop", "image", true, false);
		String bigImg = bigImgTag.getAttributeByName("content");
		return bigImg;
	}

	public ArrayList<ArtInfo> getAllArtsInfoFromAUTHORPage()
	{
		ArrayList<ArtInfo> allArtsInfo = new ArrayList<ArtInfo>();

		TagNode[] liElements = null;
		TagNode[] allArtsUl = null;
		String CSSClassname = "news-wrap clearfix clearfix l-3col packery block block-top l-tripple-cols";

		allArtsUl = rootNode.getElementsByAttValue("class", CSSClassname, true, false);

		if (allArtsUl.length > 1)
		{
			liElements = allArtsUl[1].getChildTags();
		}
		else
		{
			liElements = allArtsUl[0].getChildTags();
		}

		for (int i = 0; i < liElements.length; i++)
		{
			String[] info = new String[5];

			TagNode element = liElements[i];
			TagNode element1 = element.findElementByAttValue("class", "m-news-info", true, true);
			TagNode element2 = element1.findElementByAttValue("class", "m-news-text", true, true);
			TagNode element3 = element2.findElementByName("a", true);

//			TagNode imgDiv=element.findElementByAttValue("class", "m-news-pic-wrap", true, false);
			TagNode imgEl = element.findElementByName("img", true);

			//			TagNode author = element.findElementByAttValue("class", "m-news-author-wrap", true, false);
			//			TagNode[] author1 = author.getElementsByName("a", true);

			info[0] = element3.getAttributeByName("href").toString();
//			Log.d(tag, "info[0]" + info[0]);
			info[1] = Html.fromHtml(element3.getAttributeByName("title").toString()).toString();
//			Log.d(tag, "info[1]" + info[1]);

			if (imgEl==null)
			{
				info[2] = "empty";
			}
			else
			{
				String imgSrc = imgEl.getAttributeByName("src").toString();
				Log.d(tag, "imgSrc: '" + imgSrc+"'");
				Log.d(tag, "imgSrc.isEmpty(): " + String.valueOf(imgSrc.isEmpty()));
				if(imgSrc.isEmpty())
				{
					info[2] = "empty";
				}
				else
				{
					if (imgSrc.startsWith("/i/"))
					{
						imgSrc = "http://odnako.org" + imgSrc;
						info[2] = imgSrc;
					}
					else
					{
						info[2] = imgSrc;
					}
				}
				
			}
			
			Log.d(tag, "info[2]: '" + info[2]+"'");

			info[3] = "empty";
			info[4] = "empty";
//			Log.d(tag, "info[3]" + info[3]);
//			Log.d(tag, "info[4]" + info[4]);

			allArtsInfo.add(new ArtInfo(info));
		}
		return allArtsInfo;
	}

}