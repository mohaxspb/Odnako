package ru.kuchanov.odnako;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlCleanerException;
import org.htmlcleaner.TagNode;

import android.text.Html;

public class HtmlHelper
{
	TagNode rootNode;
	public String htmlString;
	
	public static int NUM_OF_ARTS_ON_CUR_PAGE;

	// Конструктор
	//public HtmlHelper(URL htmlPage) throws IOException
	public HtmlHelper(URL htmlPage) throws IOException
	{
		// Создаём объект HtmlCleaner
		HtmlCleaner cleaner = new HtmlCleaner();
		// Загружаем html код сайта
		try
		{
			System.out.println("HtmlHelper constructor URL: " + htmlPage.toString());
			
			rootNode = cleaner.clean(htmlPage);
			htmlString = cleaner.getInnerHtml(rootNode);
		} catch (HtmlCleanerException e)
		{
			//System.out.println(e.getMessage());
			System.out.println("Error in HtmlHelper while try to clean HTML. May be FileNot found or NOconnection exceotion");
		} catch (FileNotFoundException e)
		{
			System.out.println("FileNotFoundException at HtmlHelper");
			System.out.println(e.getMessage());
			System.out.println("FileNotFoundException at HtmlHelper");
		}
	}

	public List<TagNode> getInfo()
	{
		List<TagNode> linkList = new ArrayList<TagNode>();
		String CSSClassname = "news-wrap clearfix clearfix l-3col packery block l-tripple-cols";

		TagNode linkElements[] = rootNode.getElementsByName("ul", true);

		for (int i = 0; linkElements != null && i < linkElements.length; i++)
		{
			// получаем атрибут по имени
			String classType = linkElements[i].getAttributeByName("class");
			// если атрибут есть и он эквивалентен искомому, то добавляем в
			// список
			if (classType != null && classType.equals(CSSClassname))
			{
				TagNode linkElements1[] = linkElements[i].getElementsByName("li", true);
				for (int a = 0; a < linkElements1.length; a++)
				{
					linkList.add(linkElements1[a]);
				}

			}
		}

		return linkList;
	}

	public TagNode[] getBlogsInfo()
	{
		TagNode[] liElements;
		TagNode blogsUlEl = null;
		String CSSClassname = "news-wrap clearfix clearfix l-3col packery block l-tripple-cols";

		//TagNode linkElements[] = rootNode.getElementsByName("ul", true);
		//blogsUlEl=rootNode.findElementByAttValue("class", CSSClassname, true, false);
		//blogsUlEl=rootNode.findElementByAttValue("class", CSSClassname, true, false);
		TagNode[] blogsUlElALL = null;
		try
		{
			//TagNode[] blogsUlElALL = rootNode.getElementsByAttValue("class", CSSClassname, true, false);
			blogsUlElALL = rootNode.getElementsByAttValue("class", CSSClassname, true, false);
		} catch (Exception e)
		{
			System.out.println("NullPointerException at HtmlHelper");
		}

		if (blogsUlElALL.length > 1)
		{
			blogsUlEl = rootNode.getElementsByAttValue("class", CSSClassname, true, false)[1];
		}
		else
		{
			blogsUlEl = rootNode.findElementByAttValue("class", CSSClassname, true, false);
		}

		liElements = blogsUlEl.getElementsByName("li", true);
		return liElements;
	}

	public TagNode[] getAuthorsInfo()
	{
		TagNode[] liElements;
		TagNode blogsUlEl = null;
		String CSSClassname = "news-wrap clearfix clearfix l-3col packery block block-top l-tripple-cols";
		
		blogsUlEl = rootNode.findElementByAttValue("class", CSSClassname, true, false);
		
		liElements = blogsUlEl.getElementsByName("li", true);
		return liElements;
	}

	public String[][] getAuthorsArtsList(TagNode[] liElements, String authorName)
	{
		String[][] output;
		output = new String[liElements.length][MainActivityNew.NUM_OF_ELEMS_IN_DIV];

		for (int i = 0; i < liElements.length; i++)
		{
			TagNode[] element = liElements[i].getElementsByName("div", true);
			//System.out.println(element.length);
			TagNode element2;
			
			////imgs
			TagNode imgEl=null;// = element[0].findElementByName("a", true).findElementByName("img", true);
			TagNode[] allElementsOfLi=liElements[i].getAllElements(true);
			for(int ii=0; ii<allElementsOfLi.length; ii++)
			{
				if(allElementsOfLi[ii].getName().equals("img"))
				{
					imgEl=allElementsOfLi[ii];
					ii=allElementsOfLi.length;
				}
				else
				{
					imgEl=null;
				}
			}
			if (imgEl != null)
			{
				if (imgEl.getAttributeByName("src").equals(""))
				{
					output[i][2] = "default";
				}
				else
				{
					output[i][2] = imgEl.getAttributeByName("src").toString();
				}
			}
			else
			{
				output[i][2] = "default";
			}
			///imgs
			
			if (element.length == 1)
			{
				try
				{
					element2 = element[0].getAllElements(true)[0].getElementsByName("a", true)[0];
				} catch (Exception e)
				{
					element2 = element[0].getElementsByName("a", true)[0];
				}
			}
			else
			{
				try
				{
					element2 = element[0].getAllElements(true)[0].getElementsByName("a", true)[0];
				} catch (Exception e)
				{
					try
					{
						element2 = element[0].getElementsByName("a", true)[0];
					} catch (Exception ee)
					{
						try
						{
							element2 = element[1].getAllElements(true)[0].getElementsByName("a", true)[0];
						} catch (Exception eee)
						{
							element2 = element[1].getElementsByName("a", true)[0];
						}
					}
				}
			}
			output[i][0] = element2.getAttributeByName("href").toString();
			output[i][1] = Html.fromHtml(element2.getAttributeByName("title").toString()).toString();
			//System.out.println(element2.getAttributes().toString());
			output[i][3] = "default";
			output[i][4] = authorName;
		}
		////set curNumOfArtsOnPage to set num of items in list on main
		HtmlHelper.NUM_OF_ARTS_ON_CUR_PAGE=liElements.length;
		
		return output;
	}
	
	public TagNode[] getAllAuthorsInfo()
	{
		TagNode[] liElements;
		TagNode blogsUlEl = null;
		String CSSClassname = "news-wrap clearfix clearfix l-3col packery block block-top l-tripple-cols";
		
		blogsUlEl = rootNode.findElementByAttValue("class", CSSClassname, true, false);
		
		liElements = blogsUlEl.getElementsByName("li", true);
		return liElements;
	}

	public String[][] getAllAuthorsArtsList(TagNode[] liElements, String authorName)
	{
		String[][] output;
		output = new String[liElements.length][MainActivityNew.NUM_OF_ELEMS_IN_DIV];

		for (int i = 0; i < liElements.length; i++)
		{
			TagNode[] element = liElements[i].getElementsByName("div", true);
			//System.out.println(element.length);
			TagNode element2;
			
			////imgs
			TagNode imgEl=null;// = element[0].findElementByName("a", true).findElementByName("img", true);
			TagNode[] allElementsOfLi=liElements[i].getAllElements(true);
			for(int ii=0; ii<allElementsOfLi.length; ii++)
			{
				if(allElementsOfLi[ii].getName().equals("img"))
				{
					imgEl=allElementsOfLi[ii];
					ii=allElementsOfLi.length;
				}
				else
				{
					imgEl=null;
				}
			}
			if (imgEl != null)
			{
				if (imgEl.getAttributeByName("src").equals(""))
				{
					output[i][2] = "default";
				}
				else
				{
					output[i][2] = imgEl.getAttributeByName("src").toString();
				}
			}
			else
			{
				output[i][2] = "default";
			}
			///imgs
			
			if (element.length == 1)
			{
				try
				{
					element2 = element[0].getAllElements(true)[0].getElementsByName("a", true)[0];
				} catch (Exception e)
				{
					element2 = element[0].getElementsByName("a", true)[0];
				}
			}
			else
			{
				try
				{
					element2 = element[0].getAllElements(true)[0].getElementsByName("a", true)[0];
				} catch (Exception e)
				{
					try
					{
						element2 = element[0].getElementsByName("a", true)[0];
					} catch (Exception ee)
					{
						try
						{
							element2 = element[1].getAllElements(true)[0].getElementsByName("a", true)[0];
						} catch (Exception eee)
						{
							element2 = element[1].getElementsByName("a", true)[0];
						}
					}
				}
			}
			output[i][0] = element2.getAttributeByName("href").toString();
			if(output[i][0].startsWith("http://"))
			{
				output[i][0]=output[i][0].replace("http://", "");
			}
			output[i][1] = Html.fromHtml(element2.getAttributeByName("title").toString()).toString();
			//System.out.println(element2.getAttributeByName("title"));
			output[i][3] = "allAuthors";
			for(int y=0; y<element.length; y++)
			{
				TagNode pElement=element[y].findElementByName("p", true);
				if(pElement!=null)
				{
					output[i][4] = pElement.getText().toString();
					y=element.length;
				}
				else
				{
					output[i][4] = "default";
				}
			}
			
			//output[i][4] = element[0].findElementByName("p", true).getText().toString();
		}
		////set curNumOfArtsOnPage to set num of items in list on main
		HtmlHelper.NUM_OF_ARTS_ON_CUR_PAGE=liElements.length;
		
		return output;
	}

	TagNode[] getCatsInfo()
	{
		TagNode[] liElements;
		TagNode blogsUlEl = null;
		String CSSClassname = "news-wrap clearfix clearfix l-3col packery block l-tripple-cols even";

		//TagNode linkElements[] = rootNode.getElementsByName("ul", true);
		blogsUlEl = rootNode.findElementByAttValue("class", CSSClassname, true, false);

		liElements = blogsUlEl.getChildTags();

		return liElements;
	}

	public TagNode getArcicle()
	{
		TagNode output;// = new TagNode();
		String CSSClassname = "post-content l-post-text-offset break l-white clearfix outlined-hard-bot";
		String CSSClassname1 = "post-content l-post-text-offset l-white clearfix";

		TagNode bodeEl = rootNode.findElementByName("body", true);

		TagNode artDivEl = bodeEl.findElementByAttValue("class", CSSClassname, true, true);

		if (artDivEl == null)
		{
			artDivEl = bodeEl.findElementByAttValue("class", CSSClassname1, true, true);
		}

		output = artDivEl;
		//System.out.println("getArcicle "+output.getText());
		//System.out.println("getArcicle " + output.getChildTags().length);

		return output;
	}

	String[] getArcicleInfo()
	{
		String[] output = new String[6];

		String CSSClassname = "main-article post l-left l-post";
		String classNameOfAutorsInfo = "authors-teaser clearfix outline outline-bot";
		String classNameOfAutorsDescription = "author-teaser-expander clearfix";

		TagNode bodeEl = rootNode.findElementByName("body", true);

		TagNode artTEl = bodeEl.findElementByAttValue("class", CSSClassname, true, true);
		//find title
		TagNode artTitleEl = artTEl.findElementByName("h1", true);
		String title=artTitleEl.getText().toString();
		//end
		//find authorsinfo
		TagNode authorInfoEl=artTEl.findElementByAttValue("class", classNameOfAutorsInfo, true, false);
		//find img
		String imgSRC="default";
		if(authorInfoEl.findElementByName("img", true)!=null)
		{
			imgSRC=authorInfoEl.findElementByName("img", true).getAttributeByName("src");
		}
		//find name, linkToBlog and WHO
		String name="default";
		String authorsBlogLink="default";
		String who="default";
		String autorsDescription="default";
		TagNode nameEl=authorInfoEl.findElementByAttValue("class", "name", true, false);
		if(nameEl!=null)
		{
			name=nameEl.findElementByName("a", true).getText().toString();
			authorsBlogLink=nameEl.findElementByName("a", true).getAttributeByName("href");
			if(authorsBlogLink.endsWith("/"))
			{
				authorsBlogLink=authorsBlogLink.substring(0, authorsBlogLink.length()-1);
			}
		}
		TagNode whoEl=authorInfoEl.findElementByAttValue("class", "who", true, false);
		if(!whoEl.getText().equals(""))
		{
			who=whoEl.getText().toString();
		}
		//find Autor's description
		TagNode AutorsDescription=authorInfoEl.findElementByAttValue("class", classNameOfAutorsDescription, true, false);
		//if(!AutorsDescription.getText().equals(""))
		if(AutorsDescription.findElementByName("p", true)!=null)
		{
			autorsDescription=AutorsDescription.findElementByName("p", true).getText().toString();
		}
		//end
		

		output[0] = title;
		output[1] = imgSRC;
		output[2] = name;
		output[3] = authorsBlogLink;
		output[4] = who;
		output[5] = autorsDescription;
//		for(int i=0; i<output.length; i++)
//		{
//			System.out.println(output[i]);
//		}
		return output;
	}

	List<TagNode> getComments()
	{
		List<TagNode> linkList = new ArrayList<TagNode>();
		String CSSClassname = "ul-comments";
		String divCommPages = "pager clearfix";
		//String lastCommPage="pager-last";

		TagNode bodyElement = rootNode.findElementByName("body", true);

		TagNode ulElement = bodyElement.findElementByAttValue("class", CSSClassname, true, true);

		TagNode divCommPagesEl = bodyElement.findElementByAttValue("class", divCommPages, true, false);
		TagNode lastCommPageEl = null;
		//if(divCommPagesEl.hasChildren())
		if (divCommPagesEl != null)
		{
			if (!divCommPagesEl.isEmpty())
			{
				//System.out.println("HtmlHelper_Comments: "+divCommPagesEl.hasChildren());
				//System.out.println("HtmlHelper_Comments: "+!divCommPagesEl.isEmpty());
				//lastCommPageEl = divCommPagesEl.findElementByAttValue("class", lastCommPage, true, false);
				lastCommPageEl = divCommPagesEl.getElementsByName("a", true)[divCommPagesEl.getChildTags().length - 1];
			}
			else
			{
				lastCommPageEl = divCommPagesEl;
			}
		}
		else
		{
			System.out.println("NO DIV IN HTML check what was been loaded");
		}

		linkList.add(ulElement);
		linkList.add(lastCommPageEl);

		return linkList;
	}

}