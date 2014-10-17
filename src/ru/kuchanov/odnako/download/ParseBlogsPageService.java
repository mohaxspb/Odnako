package ru.kuchanov.odnako.download;

import java.net.URL;

import org.htmlcleaner.TagNode;

import ru.kuchanov.odnako.HtmlHelper;
import ru.kuchanov.odnako.MainActivityNew;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.widget.Toast;

public class ParseBlogsPageService extends AsyncTask<Void, Void, String[][]>
{
	ActionBarActivity act;
	ProgressDialog pd;
	Context context;

	NotificationManager notificationManager;

	String categoryToLoad;
	int numToLoad;
	int numOfPagesToLoad;
	int curPageToLoad;
	
	String linkToWrite;
	String data;

	//public ParseBlogsPageService(ActionBarActivity act, String categoryToLoad, int pageToLoad)
	public ParseBlogsPageService(Context context, String categoryToLoad, int numToLoad, int numOfPagesToLoad, int curPageToLoad)
	{
		//this.act = act;
		this.context = context;
		this.categoryToLoad = categoryToLoad;
		this.numToLoad = numToLoad;
		this.numOfPagesToLoad = numOfPagesToLoad;
		this.curPageToLoad = curPageToLoad;
	}

	@Override
	protected void onPreExecute()
	{
		System.out.println("ParseBlogsPageService: onPreExecute");

//		String formatedCategory;
//		formatedCategory = this.categoryToLoad.replace("-", "_");
//		formatedCategory = formatedCategory.replace("/", "_");
//		formatedCategory = formatedCategory.replace(":", "_");
//		formatedCategory = formatedCategory.replace(".", "_");
//		String formatedPageName;
//		formatedPageName = this.categoryToLoad + "/page-1/";
//		formatedPageName = formatedPageName.replace("-", "_");
//		formatedPageName = formatedPageName.replace("/", "_");
//		formatedPageName = formatedPageName.replace(":", "_");
//		formatedPageName = formatedPageName.replace(".", "_");
//		linkToWrite = "file:///" + context.getFilesDir().getAbsolutePath() + "/" + formatedCategory + "/" + "http___" + formatedPageName;
	}

	@Override
	protected String[][] doInBackground(Void... arg)
	{
		System.out.println("ParseBlogsPageService: doInBackground");
		String[][] output = null;
		String link = "http://" + this.categoryToLoad + "/page-" + String.valueOf(this.curPageToLoad) + "/";
		System.out.println(link);
		try
		{
			HtmlHelper hh = new HtmlHelper(new URL(link));
			TagNode[] liElemsArr = hh.getBlogsInfo();
			
			
			linkToWrite=link;
			data = hh.htmlString;

			output = new String[MainActivityNew.DEFAULT_NUM_OF_ARTS_ON_PAGE][MainActivityNew.NUM_OF_ELEMS_IN_DIV];
			for (int i = 0; i < liElemsArr.length; i++)
			{
				TagNode element = liElemsArr[i];
				TagNode element1 = element.findElementByAttValue("class", "m-news-info", true, true);
				TagNode element2 = element1.findElementByAttValue("class", "m-news-text", true, true);
				TagNode element3 = element2.findElementByName("a", true);

				TagNode[] imgEl = element.getElementsByName("img", true);

				TagNode author = element.findElementByAttValue("class", "m-news-author-wrap", true, false);
				TagNode[] author1 = author.getElementsByName("a", true);

				output[i][0] = element3.getAttributeByName("href").toString();
				output[i][1] = Html.fromHtml(element3.getAttributeByName("title").toString()).toString();
				//System.out.println(output[i][1]);

				if (imgEl.length == 0)
				{
					output[i][2] = "default";
				}
				else
				{
					output[i][2] = imgEl[0].getAttributeByName("src").toString();
				}
				if (author1.length == 0)
				{
					output[i][3] = "default";
					output[i][4] = "default";
				}
				else
				{
					output[i][3] = author1[0].getAttributeByName("href");
					output[i][4] = Html.fromHtml(author1[0].getAttributeByName("title")).toString();
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return output;
	}

	@Override
	protected void onPostExecute(String[][] output)
	{
		System.out.println("ParseBlogsPageNew: onPostExecute");
		//check internet
		if (output != null)
		{
			System.out.println(output[0][0]);
			if (this.curPageToLoad < this.numOfPagesToLoad)
			{
				this.curPageToLoad += 1;
				ParseBlogsPageService parseBlogs = new ParseBlogsPageService(context, this.categoryToLoad, this.numOfPagesToLoad, numOfPagesToLoad, curPageToLoad);
				parseBlogs.execute();
			}
			else
			{
				WriteFileService write = new WriteFileService(context);
				write.setVars(linkToWrite, data, categoryToLoad, 0, 1);
				write.execute();
				
				startLoadArticles(output);
			}
		}
		//NO internet
		else
		{
			Toast.makeText(context, "Ошибка соединения \n Проверьте соединение с интернетом", Toast.LENGTH_LONG).show();
			notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			int notifyID = 1;
			notificationManager.cancel(notifyID);
		}

	}// Событие по окончанию парсинга

	void startLoadArticles(String[][] output)
	{
		for(int i=0; i<this.numToLoad; i++)
		{
			ParseArticleService parseArt=new ParseArticleService(context);
			parseArt.setVars(output[i][0], this.categoryToLoad, i+1, numToLoad);
			parseArt.execute();
		}
	}
}
