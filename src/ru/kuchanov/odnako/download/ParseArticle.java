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

public class ParseArticle extends AsyncTask<Void, Void,Article>
{
	private final static String LOG = ParseArticle.class.getSimpleName() + "/";

	private String url;
	private Context ctx;
	private DataBaseHelper h;

	public ParseArticle(Context ctx, String url, DataBaseHelper h)
	{
		this.ctx = ctx;
		this.url = url;
		this.h = h;
	}

	protected Article doInBackground(Void... arg)
	{
		Article output = null;
		String link;
		if (this.url.startsWith("http://"))
		{
			link = this.url;
		}
		else
		{
			link = "http://" + this.url;
		}

		try
		{
			HtmlHelper hh = new HtmlHelper(link);
			output = hh.parseArticle();
//			for (Article a : output)
//			{
//				Article artInDB = Article.getArticleByURL(h, a.getUrl());
//				if (artInDB != null)
//				{
//					Article.updatePreview(h, artInDB.getId(), a.getPreview());
//					Article.updatePubDate(h, artInDB.getId(), a.getPubDate());
//				}
//			}
			Log.e(LOG + url, output.toString());
			
		} catch (Exception e)
		{
			Log.e(LOG + url, "Catched Exception: " + e.toString());
			e.printStackTrace();
		}
		return output;
	}

	protected void onPostExecute(Article output)
	{
		//check internet
		if (output != null)
		{
			Intent intent = new Intent(url);
			intent.putExtra(Article.KEY_ALL_ART_INFO, output);
			LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
		}
		//NO internet
		else
		{
			//do noting, because this data isn't critical
			Log.e(LOG + url, Const.Error.CONNECTION_ERROR);
		}
	}// Событие по окончанию парсинга

	@Override
	protected void onCancelled()
	{
		Log.d(LOG + url, String.format("onCancelled"));
		super.onCancelled();
	}
}