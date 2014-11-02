package ru.kuchanov.odnako.download;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlCleanerException;
import org.htmlcleaner.TagNode;

public class HtmlHelper
{
	TagNode rootNode;
	public String htmlString;

	public static int NUM_OF_ARTS_ON_CUR_PAGE;

	public HtmlHelper(URL htmlPage) throws IOException
	{
		HtmlCleaner cleaner = new HtmlCleaner();
		try
		{
			System.out.println("HtmlHelper constructor URL: " + htmlPage.toString());

			rootNode = cleaner.clean(htmlPage);
			htmlString = cleaner.getInnerHtml(rootNode);
		} catch (HtmlCleanerException e)
		{
			//System.out.println(e.getMessage());
			System.out
			.println("Error in HtmlHelper while try to clean HTML. May be FileNot found or NOconnection exceotion");
		} catch (FileNotFoundException e)
		{
			System.out.println("FileNotFoundException at HtmlHelper");
			System.out.println(e.getMessage());
			System.out.println("FileNotFoundException at HtmlHelper");
		}
	}

	ArrayList<ArrayList<String>> getAllCategoriesAsList()
	{
		ArrayList<ArrayList<String>> allCategoriesAsList=new ArrayList<ArrayList<String>>();
		TagNode[] liElements;
		TagNode allCategoriesUl = null;
		String CSSClassname = "l-full-col outlined-hard-flipped";

		allCategoriesUl = rootNode.findElementByAttValue("class", CSSClassname, true, false);

		liElements = allCategoriesUl.getChildTags();
		
		for(int i=0; i<liElements.length; i++)
		{
			ArrayList<String> category=new ArrayList<String>();
			category.add(liElements[i].findElementByName("a", true).getAttributeByName("title"));
			category.add(liElements[i].findElementByName("a", true).getAttributeByName("href"));
			
			allCategoriesAsList.add(category);
		}

		return allCategoriesAsList;
	}
	
	ArrayList<ArrayList<String>> getAllAuthorsAsList()
	{
		ArrayList<ArrayList<String>> allAuthorsAsList=new ArrayList<ArrayList<String>>();
		TagNode[] liElements;
		TagNode allAuthorsUl = null;
		String CSSClassname = "news-wrap clearfix clearfix l-3col packery block block-top l-tripple-cols";

		allAuthorsUl = rootNode.findElementByAttValue("class", CSSClassname, true, false);

		liElements = allAuthorsUl.getChildTags();
		
		
		
		for(int i=0; i<liElements.length; i++)
		{
			ArrayList<String> authorInfo=new ArrayList<String>();
			
			authorInfo.add(liElements[i].findElementByName("a", true).getAttributeByName("title"));
			authorInfo.add(liElements[i].findElementByName("a", true).getAttributeByName("href"));
			if(liElements[i].findElementByName("img", true)!=null)
			{
				authorInfo.add(liElements[i].findElementByName("img", true).getAttributeByName("src"));
			}
			else
			{
				authorInfo.add("empty");
			}
			if(liElements[i].findElementByName("p", true).getText().toString()!="")
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

}