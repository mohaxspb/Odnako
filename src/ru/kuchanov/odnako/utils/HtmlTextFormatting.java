/*
 17.03.2015
HtmlTextFormatting.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import android.util.Log;

import ru.kuchanov.odnako.db.Article;

/**
 * class for formatting Html and extracting some tags from p tag;
 */
public class HtmlTextFormatting
{
	private static final String TAG_IMG = "img";
	private static final String TAG_INPUT = "input";
	private static final String TAG_A = "a";
	private static final String TAG_IFRAME = "iframe";

	private static final String ARROW_OPEN = "<";
	private static final String ARROW_CLOSE = ">";

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
			if (curTag.getName().equals("p") || curTag.getName().equals("div"))
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
			}//if tag is p
			else
			{
				formatedArticle.addChild(curTag);
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
		imgTag.addAttribute("src", curInnerTag.getAttributeByName("src"));
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
//		TagNode aTag = new TagNode("a");
//		aTag.addAttribute("href", curInnerTag.getAttributeByName("src"));
//		aTag.addChild(new ContentNode("ссылка на видео"));
//		pTag.addChild(aTag);
		String link="<a href='"+curInnerTag.getAttributeByName("src")+"'>"+"ссылка на видео"+"</a>";
		pTag.addChild(new ContentNode(link));
		//add them to our formated tag
		formatedArticle.addChild(pTag);
		
		HtmlCleaner cleaner = new HtmlCleaner();
		String t=cleaner.getInnerHtml(pTag);
		Log.e("djdj", t);
	}
}
