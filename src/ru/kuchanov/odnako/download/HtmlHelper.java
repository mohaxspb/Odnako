package ru.kuchanov.odnako.download;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlCleanerException;
import org.htmlcleaner.TagNode;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.db.DataBaseHelper;
import ru.kuchanov.odnako.utils.DateParse;

public class HtmlHelper
{
	private final static String LOG = HtmlHelper.class.getSimpleName();

	public final static String DOMAIN_MAIN = "http://odnako.org";

	TagNode rootNode;
	public String htmlString;

	private String url;

	HtmlCleaner cleaner;

	//vars for HTMLCleaner methods params
	private boolean isRecursive = true;
	private boolean isCaseSensitive = false;

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
		
		if(this.url.contains("_"))
		{
			this.url=this.url.replaceAll("_", "%5F");
			this.url=this.url.replaceAll("1", "%31");
			this.url=this.url.replaceAll("4", "%34");
			this.url=this.url.replaceAll("-", "%2D");
		}
		url = URLDecoder.decode(url, "utf-8");

		try
		{
			cleaner = new HtmlCleaner();
			CleanerProperties props = cleaner.getProperties();
			props.setAllowHtmlInsideAttributes(false);
			props.setAllowMultiWordAttributes(true);
			props.setRecognizeUnicodeChars(true);
			props.setOmitComments(true);			
			rootNode = cleaner.clean(new URL(url));
//			String htmlStr=connect(url);
//			rootNode = cleaner.clean(htmlStr);
			htmlString = cleaner.getInnerHtml(rootNode);
		} catch (HtmlCleanerException e)
		{
			//System.out.println(e.getMessage());
			Log.e(LOG, "Error in HtmlHelper while try to clean HTML. May be FileNot found or NOconnection exception");
		}
	}	

	public static String connect(String url)
	{
		String result=null;
		HttpClient httpclient = new DefaultHttpClient();

		// Prepare a request object
		HttpGet httpget = new HttpGet(url);

		// Execute the request
		HttpResponse response;
		try
		{
			response = httpclient.execute(httpget);
			// Examine the response status
			Log.i("Praeda", response.getStatusLine().toString());

			// Get hold of the response entity
			HttpEntity entity = response.getEntity();
			// If the response does not enclose an entity, there is no need
			// to worry about connection release

			if (entity != null)
			{

				// A Simple JSON Response Read
				InputStream instream = entity.getContent();
				result = convertStreamToString(instream);
				// now you have the string representation of the HTML request
				instream.close();
			}

		} catch (Exception e)
		{
		}
		return result;
	}

	private static String convertStreamToString(InputStream is)
	{
		/* To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String. */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try
		{
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				is.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return sb.toString();
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

	public ArrayList<Article> getAllArtsInfoFromPage()
	{
		ArrayList<Article> allArtsInfo = new ArrayList<Article>();

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
			Article a = new Article();

			TagNode element = liElements[i];
			TagNode element1 = element.findElementByAttValue("class", "m-news-info", true, true);
			TagNode element2 = element1.findElementByAttValue("class", "m-news-text", true, true);
			TagNode element3 = element2.findElementByName("a", true);

			TagNode[] imgEl = element.getElementsByName("img", true);

			TagNode author = element.findElementByAttValue("class", "m-news-author-wrap", true, false);
			TagNode[] author1 = author.getElementsByName("a", true);

			a.setUrl(element3.getAttributeByName("href").toString());
			a.setTitle(Html.fromHtml(element3.getAttributeByName("title").toString()).toString());

			//here we can check if it's author or article image
			if (imgEl.length == 0)
			{
				a.setImgArt("empty");
			}
			else
			{
				String imgSrc = imgEl[0].getAttributeByName("src").toString();
				if (imgSrc.startsWith("/i/"))
				{
					imgSrc = DOMAIN_MAIN + imgSrc;
				}
				a.setImgArt(imgSrc);
			}
			if (author1.length == 0)
			{
				//blogURL
				a.setAuthorBlogUrl("empty");
				//name
				a.setAuthorName("empty");
			}
			else
			{
				//blogURL
				a.setAuthorBlogUrl(author1[0].getAttributeByName("href"));
				//name
				a.setAuthorName(Html.fromHtml(author1[0].getAttributeByName("title")).toString());
			}
			try
			{
				TagNode element4 = element2.findElementByAttValue("class", "m-news-date", true, true);
				a.setPubDate(DateParse.parse(element4.getText().toString().replaceAll(" ", "")));
			} catch (Exception e)
			{
				a.setPubDate(new Date(0));
			}

			allArtsInfo.add(a);
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

	public ArrayList<Article> getAllArtsInfoFromAUTHORPage()
	{
		ArrayList<Article> allArtsInfo = new ArrayList<Article>();

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
			Article a = new Article();

			TagNode element = liElements[i];
			TagNode element1 = element.findElementByAttValue("class", "m-news-info", true, true);
			TagNode element2 = element1.findElementByAttValue("class", "m-news-text", true, true);
			TagNode element3 = element2.findElementByName("a", true);

			TagNode imgEl = element.findElementByName("img", true);

			a.setUrl(element3.getAttributeByName("href").toString());
			a.setTitle(Html.fromHtml(element3.getAttributeByName("title").toString()).toString());

			if (imgEl == null)
			{
				a.setImgArt("empty");
			}
			else
			{
				String imgSrc = imgEl.getAttributeByName("src").toString();
				if (imgSrc.isEmpty())
				{
					a.setImgArt("empty");
				}
				else
				{
					if (imgSrc.startsWith("/i/"))
					{
						imgSrc = DOMAIN_MAIN + imgSrc;
						a.setImgArt(imgSrc);
					}
					else
					{
						a.setImgArt(imgSrc);
					}
				}
			}
			//blogURL
			a.setAuthorBlogUrl(url.substring(0, this.url.indexOf("/page-")));
			//name
			a.setAuthorName(rootNode.findElementByName("h1", true).getText().toString());

			try
			{
				TagNode element4 = element2.findElementByAttValue("class", "m-news-date", true, true);
				a.setPubDate(DateParse.parse(element4.getText().toString().replaceAll(" ", "")));
			} catch (Exception e)
			{
				a.setPubDate(new Date(0));
			}
			allArtsInfo.add(a);
		}
		//TODO Check here situation when we parse last page of category/ author and get 30 arts
		//if so we can't setInitial arts URL in DBActions, so we can get access to DB here
		//and do it here
		return allArtsInfo;
	}

	/**
	 * from RSS we get URL (for finding articles in DB), and preview with
	 * pubDate for updating founded arts
	 * 
	 * @return List of Article with filled URL, pubDate and preview fields
	 */
	public ArrayList<Article> getDataFromRSS()
	{
		ArrayList<Article> artsList = new ArrayList<Article>();
		TagNode[] items = this.rootNode.findElementByName("channel", true).getElementsByName("item", true);
		for (TagNode t : items)
		{
			Article a = new Article();
			TagNode guid = t.findElementByName("guid", true);
			a.setUrl(guid.getText().toString());
			a.setPubDate(DateParse.parse(t.findElementByName("pubDate", true).getText().toString()));
			TagNode preview = t.findElementByName("description", true);
			String previewString = Html.fromHtml(preview.getText().toString()).toString();
			previewString = previewString.substring(previewString.indexOf("justify") + 9);
			if (previewString.startsWith("<img"))
			{
				previewString = previewString.substring(previewString.indexOf(">") + 1);
			}
			previewString = previewString.substring(0, previewString.indexOf("<p>"));
			a.setPreview(previewString);
			artsList.add(a);
		}
		return artsList;
	}

	public Article parseArticle(DataBaseHelper h)
	{
		Article a = new Article();
		//id, url, title, img_art, authorBlogUrl, authorName, preview, pubDate, 
		//refreshed, numOfComments, numOfSharings, artText, authorDescr, tegs_main, 
		//tegs_all, share_quont, to_read_main, to_read_more, img_author, author

		String preview = this.rootNode
		.findElementByAttValue("property", "og:description", isRecursive, isCaseSensitive)
		.getAttributeByName("content");
		String title = this.rootNode
		.findElementByAttValue("property", "og:title", isRecursive, isCaseSensitive).getAttributeByName("content");
		//IMAGE
		//<meta property="og:image" content="http://www.odnako.org/i/335_245/blogs/48584/ekonomisti-dengi-mvf-kievu-ne-pomogut-a-ukrainci-ih-ne-uvidyat-1482-48584.jpg" />
		//there can be "/blogs/" for article image; "/users/" for author ava and "/img/" for empty
		String imgArt = this.rootNode
		.findElementByAttValue("property", "og:image", isRecursive, isCaseSensitive).getAttributeByName("content");
		if (imgArt.contains("/blogs/"))
		{
			imgArt = imgArt.replace("/335_245/", "/450_240/");
		}
		else
		{
			imgArt = Const.EMPTY_STRING;
		}

		//<div class="date l-t-right l-right">12 марта 2015</div>
		Date pubDate = DateParse.parse(this.rootNode
		.findElementByAttValue("class", "date l-t-right l-right", isRecursive, isCaseSensitive).getText().toString());

		//AUTHOR
		//<div class="author-teaser clearfix">
		TagNode author = this.rootNode.findElementByAttValue("class", "author-teaser clearfix", isRecursive,
		isCaseSensitive);
		//<div class="image"><img src="/i/36_36/users/103975/103975-1481-103975.jpg" height="36" width="36" alt="Марина Юденич" class="image"></div>
		TagNode imgDiv = author.findElementByAttValue("class", "image", isRecursive, isCaseSensitive);
		String imgAuthor = "empty";
		if (imgDiv.hasChildren())
		{
			TagNode imgTag = imgDiv.findElementByName("img", isRecursive);
			if (imgTag != null)
			{
				imgAuthor = imgTag.getAttributeByName("src");
				if (imgAuthor.startsWith("/"))
				{
					imgAuthor = DOMAIN_MAIN + imgAuthor;
				}
				imgAuthor = imgAuthor.replace("/36_36/", "/75_75/");
			}
		}
		//<div class="name"> <a href="http://marinayudenich.odnako.org/">Марина Юденич</a></div>
		String authorName = "empty";
		String authorBlogUrl = "empty";
		TagNode nameDiv = author.findElementByAttValue("class", "name", isRecursive, isCaseSensitive);
		if (nameDiv != null)
		{
			TagNode aTag = nameDiv.findElementByName("a", isRecursive);
			authorName = aTag.getText().toString();
			authorBlogUrl = aTag.getAttributeByName("href");
		}
		//authors who
		String authorWho = "empty";
		TagNode whoDiv = author.findElementByAttValue("class", "who", isRecursive, isCaseSensitive);
		if (whoDiv != null)
		{
			authorWho = whoDiv.getText().toString();
		}
		//author's Description
		//<div hidden class="author-teaser-expander clearfix">
		TagNode authorDescrDiv = this.rootNode.findElementByAttValue("class", "author-teaser-expander clearfix",
		isRecursive, isCaseSensitive);
		String authorDescr = "empty";
		if (!TextUtils.isEmpty(authorDescrDiv.getText()))
		{
			authorDescr = authorDescrDiv.getText().toString();
		}
		Author aut = null;
		if (!authorBlogUrl.equals(Const.EMPTY_STRING))
		{
			//and set Author obj to Article obj		
			aut = Author.getAuthorByURL(h, authorBlogUrl);
			if (aut == null)
			{
				aut = new Author(authorBlogUrl, authorName, authorDescr, authorWho, imgAuthor, imgAuthor.replace(
				"/75_75/", "/335_245/"), new Date(0), new Date(0));
			}
		}

		//TAGS
		//main
		//<div class="biggest-tag l-left">
		//<div class="m-news-tag-wrap"><a href="http://mirovoykrizis.odnako.org/" title="МИРОВОЙ КРИЗИС" class="m-news-tag">МИРОВОЙ КРИЗИС</a>
		//</div>
		TagNode mainTagDiv = this.rootNode.findElementByAttValue("class", "biggest-tag l-left", isRecursive,
		isCaseSensitive);
		String tagMain = Const.EMPTY_STRING;
		if (mainTagDiv.hasChildren())
		{
			TagNode[] allTagsArr = mainTagDiv.getElementsByName("a", isRecursive);
			tagMain = "";
			for (int i = 0; i < allTagsArr.length; i++)
			{
				TagNode aTag = allTagsArr[i];
				String url = aTag.getAttributeByName("href");
				String tagTitle = aTag.getAttributeByName("title");
				tagMain = tagMain.concat(url);
				tagMain = tagMain.concat(Article.DIVIDER);
				tagMain = tagMain.concat(tagTitle);
				//and add group divider if it's not last element
				if (i != allTagsArr.length - 1)
				{
					tagMain = tagMain.concat(Article.DIVIDER_GROUP);
				}
			}
		}
		//article TEXT
		//post-content l-post-text-offset break l-white clearfix outlined-hard-bot
		TagNode articlesTextTagNode = this.rootNode
		.findElementByAttValue("class", "post-content l-post-text-offset break l-white clearfix outlined-hard-bot",
		isRecursive, isCaseSensitive);
		TagNode[] artTextTagNodeChildren = articlesTextTagNode.getChildTags();
		for (int i = 0; i < artTextTagNodeChildren.length; i++)
		{
			if (artTextTagNodeChildren[i].getName().equals("aside"))
			{
				articlesTextTagNode.removeChild(articlesTextTagNode.getChildTags()[i]);
			}
		}
		String artText = this.cleaner.getInnerHtml(articlesTextTagNode);
		artText = artText.replaceAll("<br />", "<br>").replaceAll("<br/>", "<br>").replaceAll("<strong>", "<b>")
		.replaceAll("</strong>", "</b>");
		////TEGS ALL
		//<nav class="m-breadcrumbs m-breadcrumbs-tags">
		TagNode allTagsElement = this.rootNode.findElementByAttValue("class", "m-breadcrumbs m-breadcrumbs-tags",
		isRecursive, isCaseSensitive);
		String allTags = Const.EMPTY_STRING;
		if (allTagsElement != null)
		{
			TagNode[] allTagsArr = allTagsElement.getElementsByName("a", isRecursive);
			allTags = "";
			for (int i = 0; i < allTagsArr.length; i++)
			{
				TagNode aTag = allTagsArr[i];
				String url = aTag.getAttributeByName("href");
				String tagTitle = aTag.getAttributeByName("title");
				allTags = allTags.concat(url);
				allTags = allTags.concat(Article.DIVIDER);
				allTags = allTags.concat(tagTitle);
				//and add group divider if it's not last element
				if (i != allTagsArr.length - 1)
				{
					allTags = allTags.concat(Article.DIVIDER_GROUP);
				}
			}
		}
		else
		{
			Log.e(LOG, "allTagsElement is  null!");
		}

		a.setUrl(url);
		a.setPreview(preview);
		a.setTitle(title);
		a.setImgArt(imgArt);
		a.setPubDate(pubDate);
		a.setRefreshed(new Date(System.currentTimeMillis()));
		//author
		a.setAuthorName(authorName);
		a.setAuthorBlogUrl(authorBlogUrl);
		a.setImgAuthor(imgAuthor);
		a.setAuthorDescr(authorDescr);
		a.setAuthor(aut);
		//tags
		a.setTagsMain(tagMain);
		//artText
		a.setArtText(artText);
		a.setTagsAll(allTags);
		return a;
	}

	public boolean isLoadSuccessfull()
	{
		return (this.rootNode == null) ? false : true;
	}
}