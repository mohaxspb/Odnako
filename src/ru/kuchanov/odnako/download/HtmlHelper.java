package ru.kuchanov.odnako.download;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlCleanerException;
import org.htmlcleaner.TagNode;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.db.Category;
import ru.kuchanov.odnako.db.DataBaseHelper;
import ru.kuchanov.odnako.utils.DateParse;
import android.text.Html;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class HtmlHelper
{
	private final static String LOG = HtmlHelper.class.getSimpleName();

	public final static String DOMAIN_MAIN = "http://odnako.org";

	public TagNode rootNode;
	public String htmlString;
	private String url;
	HtmlCleaner cleaner;

	//vars for HTMLCleaner methods params
	private boolean isRecursive = true;
	private boolean isCaseSensitive = false;

	public HtmlHelper(TagNode rootNode)
	{
		cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();
		props.setAllowHtmlInsideAttributes(false);
		props.setAllowMultiWordAttributes(true);
		props.setRecognizeUnicodeChars(true);
		props.setOmitComments(true);
		this.rootNode = rootNode;
		htmlString = cleaner.getInnerHtml(rootNode);
	}

	public HtmlHelper(String htmlPage) throws Exception
	{
		String subDomain = htmlPage.substring(htmlPage.indexOf("://") + 3, htmlPage.indexOf("."));
		String regularExpression = "^[а-яА-ЯёЁ]+$";
		if (subDomain.matches(regularExpression))
		{
			//			String encoded = URLEncoder.encode(subDomain, "utf-8");
			//			url = htmlPage;
			//			url = url.replace(subDomain, encoded);
			// throw error here and FUCK it!
			throw new Exception(Const.Error.CYRILLIC_ERROR);
		}
		else
		{
			//			url = URLDecoder.decode(htmlPage, "utf-8");
		}

		this.url = htmlPage;

		try
		{
			cleaner = new HtmlCleaner();

			CleanerProperties props = cleaner.getProperties();
			props.setAllowHtmlInsideAttributes(false);
			props.setAllowMultiWordAttributes(true);
			props.setRecognizeUnicodeChars(true);
			props.setOmitComments(true);

			//XXX TEST
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder()
			.url(htmlPage)
			.build();
			Response response = client.newCall(request).execute();
			String htmlBody = response.body().string();
			rootNode = cleaner.clean(htmlBody);
			/////////////////

			//			rootNode = cleaner.clean(new URL(url));
			htmlString = cleaner.getInnerHtml(rootNode);

			//Log.e(LOG, htmlString);
		} catch (HtmlCleanerException e)
		{
			//System.out.println(e.getMessage());
			Log.e(LOG, "Error in HtmlHelper while try to clean HTML. May be FileNot found or NOconnection exception");
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

	public Category getCategoryFromHtml()
	{
		//String url, String title, String description, String img_url,
		//String img_file_name, Date refreshed, Date lastArticleDate);

		//<meta property="og:title" content="День Победы" />
		String title = this.rootNode.findElementByAttValue("property", "og:title", isRecursive, isCaseSensitive)
		.getAttributeByName("content");
		//<meta property="og:url" content="http://about_denpobedi114.odnako.org" />
		String url = this.rootNode.findElementByAttValue("property", "og:url", isRecursive, isCaseSensitive)
		.getAttributeByName("content");

		//row news-body clearfix
		String description = Const.EMPTY_STRING;
		String img_url = this.getCategoryImage();
//		String img_file_name = Const.EMPTY_STRING;
		Date refreshed = new Date(System.currentTimeMillis());
		Date lastArticleDate = new Date(0);

		return new Category(url, title, description, img_url, /*img_file_name,*/ refreshed, lastArticleDate);
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
		String CSSClassname2 = "news-wrap clearfix clearfix l-3col packery block l-tripple-cols even";

		allArtsUl = rootNode.getElementsByAttValue("class", CSSClassname, true, false);
		//in some cases (i.e. while loading via webView) we have another class value
		if (allArtsUl.length == 0)
		{
			allArtsUl = rootNode.getElementsByAttValue("class", CSSClassname2, true, false);
		}

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
		String imgUrl;
		if (imgTag != null)
		{
			imgUrl = imgTag.getAttributeByName("src");
			if (imgUrl.startsWith("/i/"))
			{
				imgUrl = DOMAIN_MAIN + imgUrl;
			}
		}
		else
		{
			imgUrl = Const.EMPTY_STRING;
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
			//preview
			TagNode preview = t.findElementByName("description", true);
			String previewString = Html.fromHtml(preview.getText().toString()).toString();
			previewString = previewString.substring(previewString.indexOf("justify") + 9);
			if (previewString.startsWith("<img"))
			{
				previewString = previewString.substring(previewString.indexOf(">") + 1);
			}
			previewString = previewString.substring(0, previewString.indexOf("<p>"));
			a.setPreview(previewString);
			//			Log.e(LOG, previewString);
			//End of preview

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

		//		String preview = this.rootNode
		//		.findElementByAttValue("property", "og:description", isRecursive, isCaseSensitive)
		//		.getAttributeByName("content");
		//Replace to another tag, because in one case it do not contains '"' symbol and HTMLCleaner do not cut attr
		String preview = this.rootNode
		.findElementByAttValue("name", "Description", isRecursive, isCaseSensitive)
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
		if (imgArt.startsWith("/"))
		{
			imgArt = DOMAIN_MAIN + imgArt;
		}

		//<div class="date l-t-right l-right">12 марта 2015</div>
		TagNode tagDate = this.rootNode.findElementByAttValue("class", "date l-t-right l-right", isRecursive,
		isCaseSensitive);
		Date pubDate = (tagDate == null) ? new Date(0) : DateParse.parse(tagDate.getText().toString());

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
		String authorDescr = Const.EMPTY_STRING;
		Pattern p = Pattern.compile("[а-яА-ЯёЁ]");
		boolean hasSpecialChar = p.matcher(authorDescrDiv.getText().toString()).find();
		if (hasSpecialChar)
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
		if (mainTagDiv != null && mainTagDiv.hasChildren())
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
		if (articlesTextTagNode == null)
		{
			articlesTextTagNode = this.rootNode.findElementByAttValue("class",
			"post-content l-post-text-offset l-white clearfix", isRecursive, isCaseSensitive);
		}
		TagNode[] artTextTagNodeChildren = articlesTextTagNode.getChildTags();
		ArrayList<TagNode> tagsToRemove = new ArrayList<TagNode>();
		for (int i = 0; i < artTextTagNodeChildren.length; i++)
		{
			if (artTextTagNodeChildren[i].getName().equals("aside"))
			{
				tagsToRemove.add(articlesTextTagNode.getChildTags()[i]);
			}
		}
		for (TagNode t : tagsToRemove)
		{
			articlesTextTagNode.removeChild(t);
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
			//Log.e(LOG, "allTagsElement is  null!");
		}

		//ALSO BY THEME
		//<div class="m-sidebar-also l-white outline-bot l-full-col">
		TagNode[] byTheme = this.rootNode.getElementsByAttValue("class",
		"m-sidebar-also l-white outline-bot l-full-col", isRecursive, isCaseSensitive);
		//		String toReadMain = "";
		String toReadMore = "";
		if (byTheme.length > 0 && byTheme[0] != null)
		{
			String title1, url, date;
			TagNode[] divTags = byTheme[0].getElementsByName("div", isRecursive);
			for (int i = 0; i < divTags.length; i++)
			{
				TagNode div = divTags[i];
				TagNode[] aTags = div.getElementsByName("a", isRecursive);
				title1 = aTags[0].getText().toString();
				url = aTags[0].getAttributeByName("href");
				date = aTags[1].getText().toString();
				toReadMore += title1 + Article.DIVIDER + url + Article.DIVIDER + date;
				if (i != divTags.length - 1)
				{
					toReadMore += Article.DIVIDER;
				}
			}
		}
		else
		{
			toReadMore = Const.EMPTY_STRING;
		}
		if (byTheme.length > 1 && byTheme[1] != null)
		{
			String title1, url, date;
			TagNode[] divTags = byTheme[1].getElementsByName("div", isRecursive);
			if (!toReadMore.equals(""))
			{
				toReadMore += Article.DIVIDER;
			}
			for (int i = 0; i < divTags.length; i++)
			{
				TagNode div = divTags[i];
				TagNode[] aTags = div.getElementsByName("a", isRecursive);
				title1 = aTags[0].getText().toString();
				url = aTags[0].getAttributeByName("href");
				date = aTags[1].getText().toString();
				toReadMore += title1 + Article.DIVIDER + url + Article.DIVIDER + date;
				if (i != divTags.length - 1)
				{
					toReadMore += Article.DIVIDER;
				}
			}
		}
		else
		{
			//			toReadMore = Const.EMPTY_STRING;
		}
		//LIKES
		//<ul class="social-likes" data-url="" data-counters="yes">
		//THERE is no way for me to get num of sharings..
		//that's sad, because it spend a lot of time to draw card for it...
		//so we'll just replace it with button with context menu with
		//options as "share arts text or URL"
		//		TagNode shareUlTag = this.rootNode.findElementByAttValue("class", "social-likes", isRecursive, isCaseSensitive);
		//		String innerHtmlUlTag = this.cleaner.getInnerHtml(shareUlTag);
		//		Log.e(LOG, innerHtmlUlTag);
		//
		//		TagNode[] likesLiTags = shareUlTag.getChildTags();
		//		String shareQuont = "";// "0" + Article.DIVIDER + "0" + Article.DIVIDER + "0" + Article.DIVIDER + "0"
		//		//+ Article.DIVIDER + "0" + Article.DIVIDER + "0" + Article.DIVIDER;
		//		for (int i = 0; i < likesLiTags.length; i++)
		//		{
		//			TagNode li = likesLiTags[i];
		//			shareQuont += li.getText();
		//			if (i != likesLiTags.length - 1)
		//			{
		//				shareQuont += Article.DIVIDER;
		//			}
		//		}

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
		a.setTagsAll(allTags);
		//artText
		a.setArtText(artText);

		//toReadMore
		//		a.setToReadMain(toReadMain);
		a.setToReadMore(toReadMore);
		//		a.setShareQuont(shareQuont);

		a.setRefreshed(new Date(System.currentTimeMillis()));

		return a;
	}

	public boolean isLoadSuccessfull()
	{
		return (this.rootNode == null) ? false : true;
	}
}