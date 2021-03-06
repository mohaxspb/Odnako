/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.download;

import java.util.ArrayList;
import java.util.Date;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.callbacks.AllArtsInfoCallback;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.db.Category;
import ru.kuchanov.odnako.db.DataBaseHelper;
import android.os.AsyncTask;
import android.util.Log;

public class ParsePageForAllArtsInfo extends AsyncTask<Void, Void, ArrayList<Article>>
{
	private final static String LOG = ParsePageForAllArtsInfo.class.getSimpleName() + "/";

	private AllArtsInfoCallback callback;

	String link;

	private String categoryToLoad;
	private int page;

	private DataBaseHelper h;

	//	public HttpGet get;

	private boolean cyrillicError = false;

	public ParsePageForAllArtsInfo(String category, int page, AllArtsInfoCallback callback,
	DataBaseHelper h)
	{
		this.callback = callback;
		this.categoryToLoad = category;
		this.page = page;
		this.h = h;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	protected ArrayList<Article> doInBackground(Void... arg)
	{
		//System.out.println("ParsePageForAllArtsInfo: doInBackground");
		ArrayList<Article> output = null;
		String link;
		if (this.getCategoryToLoad().startsWith("http://"))
		{
			link = getCategoryToLoad() + "/page-" + String.valueOf(this.page) + "/";
		}
		else
		{
			link = "http://" + getCategoryToLoad() + "/page-" + String.valueOf(this.page) + "/";
		}
		if (this.link != null)
		{
			link = this.link;
			Log.d(getCategoryToLoad(), link);
		}

		try
		{
			HtmlHelper hh = new HtmlHelper(link);
			if (hh.isAuthor())
			{
				output = hh.getAllArtsInfoFromAUTHORPage();
				//write new Author to DB if it don't exists
				if (Category.isCategory(h, getCategoryToLoad()) == null)
				{
					Author a = new Author(getCategoryToLoad(), hh.getAuthorsName(), hh.getAuthorsDescription(),
					hh.getAuthorsWho(), hh.getAuthorsImage(), hh.getAuthorsBigImg(), new Date(
					System.currentTimeMillis()), new Date(0));
					h.getDaoAuthor().create(a);
					//refresh daos to add new raws to them
					h.refreshDaos();
				}
			}
			else
			{
				output = hh.getAllArtsInfoFromPage();
				//write new Author if it don't exists
				if (Category.isCategory(h, getCategoryToLoad()) == null)
				{
					Log.i(LOG, "Create new category");
					
//					Log.e(LOG, "before size: "+h.getAllCategoriesUrls().size());
					
					//create new entry in Category
					Category c = hh.getCategoryFromHtml();
					h.getDaoCategory().create(c);
					
//					Log.e(LOG, "after create size: "+h.getAllCategoriesUrls().size());
//					//refresh daos to add new raws to them
//					h.refreshDaos();
//					Log.e(LOG, "after refresh size: "+h.getAllCategoriesUrls().size());
				}
			}
		} catch (Exception e)
		{
			Log.e(LOG + categoryToLoad, "Catched Exception: " + e.toString());
			if (e.toString().equals(e.getClass().getName() + ": " + Const.Error.CYRILLIC_ERROR))
			{
				this.cyrillicError = true;
			}
			e.printStackTrace();
		}
		return output;
	}

	protected void onPostExecute(ArrayList<Article> output)
	{
		//Log.d(LOG, "ParseBlogsPageNew: onPostExecute");

		//check internet
		if (output != null)
		{
			callback.sendDownloadedData(output, this.getCategoryToLoad(), this.page);
		}
		//NO internet
		else
		{
			if (this.cyrillicError)
			{
				callback.onError(Const.Error.CYRILLIC_ERROR, this.getCategoryToLoad(), this.page);
				Log.e(LOG + categoryToLoad, Const.Error.CYRILLIC_ERROR);
			}
			else
			{
				callback.onError(Const.Error.CONNECTION_ERROR, this.getCategoryToLoad(), this.page);
				Log.e(LOG + categoryToLoad, Const.Error.CONNECTION_ERROR);
			}

		}
	}// Событие по окончанию парсинга

	@Override
	protected void onCancelled()
	{
		Log.d(LOG + categoryToLoad, String.format("mAsyncTask - onCancelled: isCancelled = %b: ", this.isCancelled()));
		super.onCancelled();
	}

	public String getCategoryToLoad()
	{
		return categoryToLoad;
	}

	public int getPageToLoad()
	{
		return page;
	}
}
