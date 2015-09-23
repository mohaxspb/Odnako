/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.download;

import java.util.ArrayList;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.DataBaseHelper;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class ParseRSSForDateAndPreview extends AsyncTask<Void, Void, ArrayList<Article>>
{
	private final static String LOG = ParseRSSForDateAndPreview.class.getSimpleName() + "/";

	private String categoryToLoad;

	private Context ctx;

	private DataBaseHelper h;

	public ParseRSSForDateAndPreview(Context ctx, String category, DataBaseHelper h)
	{
		this.categoryToLoad = category;

		this.ctx = ctx;

		this.h = h;
	}

	protected ArrayList<Article> doInBackground(Void... arg)
	{
		//System.out.println("ParsePageForAllArtsInfo: doInBackground");
		ArrayList<Article> output = null;
		String link;
		if (this.categoryToLoad.startsWith("http://"))
		{
			link = this.categoryToLoad + "/rss/";
		}
		else
		{
			link = "http://" + this.categoryToLoad + "/rss/";
		}
		//there are no rss for odnako.org/blogs, so check if categoryToLoad equals this case
		//and change it to odnako.org/rss
		if (this.categoryToLoad.contains("odnako.org/blogs"))
		{
			link = "http://www.odnako.org/rss/";
		}

		try
		{
			HtmlHelper hh = new HtmlHelper(link);
			output = hh.getDataFromRSS();
			for (Article a : output)
			{
				Article artInDB = Article.getArticleByURL(h, a.getUrl());
				if (artInDB != null)
				{
					Article.updatePreview(h, artInDB.getId(), a.getPreview());
					Article.updatePubDate(h, artInDB.getId(), a.getPubDate());
				}
			}
		} catch (Exception e)
		{
			Log.e(LOG + categoryToLoad, "Catched Exception: " + e.toString());
			e.printStackTrace();
		}
		return output;
	}

	protected void onPostExecute(ArrayList<Article> output)
	{
		//check internet
		if (output != null)
		{
			//			Intent intent = new Intent(categoryToLoad + "_rss");
			//			intent.putExtra(Article.KEY_ALL_ART_INFO, output);
			//			LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
			for (Article a : output)
			{
				//Send intent with article to another activities to be able to update their data
				Intent intentGlobal = new Intent(Const.Action.ARTICLE_CHANGED);
				intentGlobal.putExtra(Article.KEY_CURENT_ART, a);
				intentGlobal.putExtra(Const.Action.ARTICLE_CHANGED, Const.Action.ARTICLE_LOADED);
				LocalBroadcastManager.getInstance(ctx).sendBroadcast(intentGlobal);
			}
		}
		//NO internet
		else
		{
			//do noting, because this data isn't critical
			Log.e(LOG + categoryToLoad, Const.Error.CONNECTION_ERROR);
		}
	}// Событие по окончанию парсинга

	@Override
	protected void onCancelled()
	{
		Log.d(LOG + categoryToLoad, String.format("mAsyncTask - onCancelled: isCancelled = %b: ", this.isCancelled()));
		super.onCancelled();
	}
}