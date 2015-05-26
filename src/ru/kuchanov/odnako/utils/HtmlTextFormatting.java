/*
 17.03.2015
HtmlTextFormatting.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import java.util.ArrayList;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import ru.kuchanov.odnako.db.Article;
import android.text.Html;

/**
 * class for formatting Html and extracting some tags from p tag;
 */
public class HtmlTextFormatting
{
	static final String LOG = HtmlTextFormatting.class.getSimpleName();

	private static final String TAG_IMG = "img";
	private static final String TAG_INPUT = "input";
	private static final String TAG_A = "a";
	private static final String TAG_IFRAME = "iframe";

	private static final String ARROW_OPEN = "<";
	private static final String ARROW_CLOSE = ">";
	private static final String DOMAIN_NAME = "http://odnako.org";

	private static TagNode formatedArticle;
	private static String tagHtml;

	/**
	 * Loop through given tags and if found input or img tags in p tag extract
	 * it to top level of document structure, converting it to image tag, that
	 * make allowed to show their images in ImageView.
	 * 
	 * Also convert iframe tag to a tag.
	 * 
	 * @param tags
	 * @return TagNode with formated tags. I.e. extracting img tags to the top
	 *         level from p tag, dividing it into 2 parts
	 */
	public static TagNode format(TagNode[] tags)
	{
		formatedArticle = new TagNode("div");
		HtmlCleaner cleaner = new HtmlCleaner();
		for (int i = 0; i < tags.length; i++)
		{
			TagNode curTag = tags[i];
			if (curTag.getName().equals("p") || curTag.getName().equals("div")
			)
			{
				if (curTag.getChildTags().length != 0)
				{
					tagHtml = cleaner.getInnerHtml(curTag);

					for (int u = 0; u < curTag.getChildTags().length; u++)
					{
						TagNode curInnerTag = curTag.getChildTags()[u];
						if (curInnerTag.getName().equals(TAG_INPUT))
						{
							extractImgTags(curInnerTag, TAG_INPUT);
						}//if input tag
						else if (curInnerTag.getName().equals(TAG_IMG))
						{
							extractImgTags(curInnerTag, TAG_IMG);
						}//if img tag
						else if (curInnerTag.getName().equals(TAG_IFRAME))
						{
							extractIframeTags(curInnerTag, TAG_IFRAME);
						}//if img tag
						else if (curInnerTag.getName().equals(TAG_A)
						&& curInnerTag.getChildTags().length != 0
						&& curInnerTag.getChildTags()[0].getName().equals(TAG_IMG))
						{
							//we must here create 2 new TagNodes with text before and after img or input node	
							extractImgTags(curInnerTag.getChildTags()[0], TAG_IMG);
						}//if we have img tag inside of a tag. I.e. in developers article. =)

						if (u == curTag.getChildTags().length - 1)
						{
							TagNode firstTag = new TagNode("p");
							firstTag.addChild(new ContentNode(tagHtml));
							formatedArticle.addChild(firstTag);
						}
					}//loop of p tag children
				}//if has children
				else
				{
					formatedArticle.addChild(curTag);
				}
			}//if tag is p or div
			else if(curTag.getName().equals("h1")
			|| curTag.getName().equals("h2")
			|| curTag.getName().equals("h3")
			|| curTag.getName().equals("h4")
			|| curTag.getName().equals("h5")
			|| curTag.getName().equals("h6"))
			{
				formatedArticle.addChild(curTag);
			}
			else
			{
				//place tag inside of p tag
				TagNode pTag = new TagNode("p");
				pTag.addChild(curTag);
				TagNode brTag = new TagNode("br");
				pTag.addChild(brTag);
				formatedArticle.addChild(pTag);
				//formatedArticle.addChild(curTag);
			}
		}//loop of articles tags
		return formatedArticle;
	}

	private static void extractImgTags(TagNode curInnerTag, String tagType)
	{
		//we must here create 2 new TagNodes with text before and after img or input node						
		String subStrStartsWithInput = tagHtml.substring(tagHtml.indexOf(ARROW_OPEN + tagType));
		String subStrWithInput = subStrStartsWithInput.substring(0,
		subStrStartsWithInput.indexOf(ARROW_CLOSE) + 1);
		tagHtml = tagHtml.replaceFirst(subStrWithInput, Article.DIVIDER);
		String[] dividedTag = tagHtml.split(Article.DIVIDER);
		//create new p tag with innerHtml of parent p tag from 0 to input tag
		//check if inputTag is not first in parent
		if (dividedTag.length != 0)
		{
			TagNode firstTag = new TagNode("p");
			firstTag.addChild(new ContentNode(dividedTag[0]));
			if (dividedTag.length == 1)
			{
				//and finally set innerHtml of our parent p tag to second part of our array
				tagHtml = dividedTag[0];
			}
			else
			{
				//and finally set innerHtml of our parent p tag to second part of our array
				tagHtml = dividedTag[1];
			}
			//add them to our formated tag
			formatedArticle.addChild(firstTag);
		}
		else
		{
			//and finally set innerHtml of our parent p tag to second part of our array
			tagHtml = "";
		}
		//create img tag with info from input
		TagNode imgTag = new TagNode("img");
		String imgUrl = curInnerTag.getAttributeByName("src");
		if (imgUrl.startsWith("/"))
		{
			imgUrl = DOMAIN_NAME + imgUrl;
		}
		imgTag.addAttribute("src", imgUrl);
		imgTag.addAttribute("style", curInnerTag.getAttributeByName("style"));
		//add them to our formated tag
		formatedArticle.addChild(imgTag);
	}

	private static void extractIframeTags(TagNode curInnerTag, String tagType)
	{
		//<p><iframe allowfullscreen="" frameborder="0" height="315" src="https://www.youtube.com/embed/baZL7eY37-w" width="420"></iframe></p>

		//we must here create 2 new TagNodes with text before and after img or input node						
		String subStrStartsWithInput = tagHtml.substring(tagHtml.indexOf(ARROW_OPEN + tagType));
		String subStrWithInput = subStrStartsWithInput.substring(0,
		subStrStartsWithInput.indexOf(ARROW_CLOSE) + 1);
		tagHtml = tagHtml.replaceFirst(subStrWithInput, Article.DIVIDER);
		String[] dividedTag = tagHtml.split(Article.DIVIDER);
		//create new p tag with innerHtml of parent p tag from 0 to input tag
		//check if inputTag is not first in parent
		if (dividedTag.length != 0)
		{
			TagNode firstTag = new TagNode("p");
			firstTag.addChild(new ContentNode(dividedTag[0]));
			if (dividedTag.length == 1)
			{
				//and finally set innerHtml of our parent p tag to second part of our array
				tagHtml = dividedTag[0];
			}
			else
			{
				//and finally set innerHtml of our parent p tag to second part of our array
				tagHtml = dividedTag[1];
			}
			//add them to our formated tag
			formatedArticle.addChild(firstTag);
		}
		else
		{
			//and finally set innerHtml of our parent p tag to second part of our array
			tagHtml = "";
		}
		//create p tag with a tag from iframes src
		TagNode pTag = new TagNode("p");
		String link = "<a href='" + curInnerTag.getAttributeByName("src") + "'>" + "ссылка на видео" + "</a>";
		pTag.addChild(new ContentNode(link));
		//add them to our formated tag
		formatedArticle.addChild(pTag);
	}

	public static TagNode reduceTagsQuont(TagNode data)
	{
		TagNode[] formatedArticleTagsArr = data.getChildTags();

		formatedArticle = new TagNode("div");

		for (int i = 0; i < formatedArticleTagsArr.length; i++)
		{
			if (formatedArticleTagsArr[i].getName().equals("img"))
			{
				formatedArticle.addChild(formatedArticleTagsArr[i]);
			}
			else
			{
				//add first divTag if final tag has no childs
				if (!formatedArticle.hasChildren())
				{
					TagNode divTag = new TagNode("div");
					formatedArticle.addChild(divTag);
				}
				//check name of last tag
				if (formatedArticle.getChildTags()[formatedArticle.getChildTags().length - 1].getName().equals("img"))
				{
					TagNode divTag = new TagNode("div");
					divTag.addChild(formatedArticleTagsArr[i]);
					formatedArticle.addChild(divTag);
				}
				else
				{
					formatedArticle.getChildTags()[formatedArticle.getChildTags().length - 1]
					.addChild(formatedArticleTagsArr[i]);
				}
			}
		}
		return formatedArticle;
	}

	public static TagNode replaceATags(TagNode tag)
	{
		HtmlCleaner hc = new HtmlCleaner();
		String innerHtml = Html.fromHtml(hc.getInnerHtml(tag), null, new MyHtmlTagHandler()).toString();

		TagNode ttttt = hc.clean(innerHtml);
		ArrayList<TagNode> innerATags = new ArrayList<TagNode>(ttttt.getAllElementsList(true));

		for (TagNode aTag : innerATags)
		{
			if (aTag.getName().equals("a"))
			{
				String attr = aTag.getAttributeByName("href");
				String text = aTag.getText().toString();
				int firstATagIndex = innerHtml.indexOf("<a");
				int firstATagTextIndex = innerHtml.indexOf(text);
				String aTagsStartString = innerHtml.substring(firstATagIndex, firstATagTextIndex + text.length());
				innerHtml = innerHtml.replace(aTagsStartString, text + " (" + attr + ") ");
				innerHtml = innerHtml.replace("<a/>", "");
			}
		}

		TagNode newTagToReturn = new TagNode(tag.getName());
		newTagToReturn.addChild(new ContentNode(innerHtml));

		return newTagToReturn;
	}
}