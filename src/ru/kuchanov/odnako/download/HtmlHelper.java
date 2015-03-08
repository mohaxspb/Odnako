package ru.kuchanov.odnako.download;

import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlCleanerException;
import org.htmlcleaner.TagNode;

import android.text.Html;
import android.util.Log;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;

public class HtmlHelper
{
	private final static String TAG = HtmlHelper.class.getSimpleName();

	public final static String DOMAIN_MAIN = "http://odnako.org";

	TagNode rootNode;
	public String htmlString;

	private String url;

	HtmlCleaner cleaner;

	//	public static int NUM_OF_ARTS_ON_CUR_PAGE;

	//	public HtmlHelper(URL htmlPage) throws IOException
	public HtmlHelper(String htmlPage) throws Exception
	{
		String subDomain = htmlPage.substring(htmlPage.indexOf("://") + 3, htmlPage.indexOf("."));
		String regularExpression = "^[а-яА-ЯёЁ]+$";
		if (subDomain.matches(regularExpression))
		{
			String encoded = URLEncoder.encode(subDomain, "utf-8");
			url = htmlPage;
			url = url.replace(subDomain, encoded);
			// throw error here and FUCK it!
			throw new Exception(Const.Error.CYRILLIC_ERROR);
		}
		else
		{
			url = URLDecoder.decode(htmlPage, "utf-8");
		}

		try
		{
			cleaner = new HtmlCleaner();
			rootNode = cleaner.clean(new URL(url));
			htmlString = cleaner.getInnerHtml(rootNode);
		} catch (HtmlCleanerException e)
		{
			//System.out.println(e.getMessage());
			Log.e(TAG, "Error in HtmlHelper while try to clean HTML. May be FileNot found or NOconnection exception");
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

			if (imgEl.length == 0)
			{
				info[2] = "empty";
			}
			else
			{
				String imgSrc = imgEl[0].getAttributeByName("src").toString();
				if (imgSrc.startsWith("/i/"))
				{
					imgSrc = DOMAIN_MAIN + imgSrc;
				}
				info[2] = imgSrc;
			}
			if (author1.length == 0)
			{
				//blogURL
				info[3] = "empty";
				//name
				info[4] = "empty";
			}
			else
			{
				info[3] = author1[0].getAttributeByName("href");
				info[4] = Html.fromHtml(author1[0].getAttributeByName("title")).toString();
			}
			allArtsInfo.add(new ArtInfo(info));
		}
		//TODO Check here situation when we parse last page of category/ author and get 30 arts
		//if so we can't setInitial arts URL in DBActions, so we can get access to DB here
		//and do it here
		return allArtsInfo;
	}

	public String getCategoryImage()
	{
		String imageUrl;

		TagNode imageTag = this.rootNode.findElementByAttValue("class", "l-left header-news-banner-item-pic", true,
		false);

		imageUrl = imageTag.getAttributeByName("src");
		if (imageUrl.startsWith("/i/"))
		{
			imageUrl = DOMAIN_MAIN + imageUrl;
		}

		return imageUrl;
	}

	public String getAuthorsWho()
	{
		TagNode descrTag = rootNode.findElementByAttValue("class", "who", true, false);
		String who = "empty";
		if (descrTag != null)
		{
			who = descrTag.getText().toString();//cleaner.getInnerHtml(innerDiv);
		}
		return who;
	}

	public String getAuthorsImage()
	{
		TagNode descrTag = rootNode.findElementByAttValue("class", "section first", true, false);
		TagNode imgTag = descrTag.findElementByName("img", true);
		String imgUrl = imgTag.getAttributeByName("src");
		if (imgUrl.startsWith("/i/"))
		{
			imgUrl = DOMAIN_MAIN + imgUrl;
		}
		return imgUrl;
	}

	public String getAuthorsName()
	{
		TagNode descrTag = rootNode.findElementByAttValue("class", "section first", true, false);
		TagNode nameTag = descrTag.findElementByName("h1", true);
		String name = nameTag.getText().toString();
		return name;
	}

	public String getAuthorsDescription()
	{
		String className = "section last";
		TagNode descrTag = rootNode.findElementByAttValue("class", className, true, false);
		String description = "empty";
		try
		{
			TagNode innerDiv = descrTag.getChildTags()[0];
			description = cleaner.getInnerHtml(innerDiv);
		} catch (Exception e)
		{

		}
		return description;
	}

	public String getAuthorsBigImg()
	{
		TagNode bigImgTag = rootNode.findElementByAttValue("itemprop", "image", true, false);
		String bigImg = bigImgTag.getAttributeByName("content");
		if (bigImg.startsWith("/i/"))
		{
			bigImg = DOMAIN_MAIN + bigImg;
		}
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

			TagNode imgEl = element.findElementByName("img", true);

			info[0] = element3.getAttributeByName("href").toString();
			info[1] = Html.fromHtml(element3.getAttributeByName("title").toString()).toString();

			if (imgEl == null)
			{
				info[2] = "empty";
			}
			else
			{
				String imgSrc = imgEl.getAttributeByName("src").toString();
				if (imgSrc.isEmpty())
				{
					info[2] = "empty";
				}
				else
				{
					if (imgSrc.startsWith("/i/"))
					{
						imgSrc = DOMAIN_MAIN + imgSrc;
						info[2] = imgSrc;
					}
					else
					{
						info[2] = imgSrc;
					}
				}
			}
			//			Log.d(tag, "info[2]: '" + info[2]+"'");

			//blogURL
			info[3] = url.substring(0, this.url.indexOf("/page-"));
			//name
			info[4] = rootNode.findElementByName("h1", true).getText().toString();

			allArtsInfo.add(new ArtInfo(info));
		}
		//TODO Check here situation when we parse last page of category/ author and get 30 arts
		//if so we can't setInitial arts URL in DBActions, so we can get access to DB here
		//and do it here
		return allArtsInfo;
	}
}