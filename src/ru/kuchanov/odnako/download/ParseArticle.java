/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.download;


import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.DataBaseHelper;
import ru.kuchanov.odnako.db.ServiceArticle;

import android.content.Context;
import android.os.AsyncTask;
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
		this.setUrl(url);
		this.h = h;
	}

	protected Article doInBackground(Void... arg)
	{
		Article output = null;
		String link;
		if (this.getUrl().startsWith("http://"))
		{
			link = this.getUrl();
		}
		else
		{
			link = "http://" + this.getUrl();
		}

		try
		{
			HtmlHelper hh = new HtmlHelper(link);
			if(hh.isLoadSuccessfull())
			{
				output = hh.parseArticle();
			}
//			for (Article a : output)
//			{
//				Article artInDB = Article.getArticleByURL(h, a.getUrl());
//				if (artInDB != null)
//				{
//					Article.updatePreview(h, artInDB.getId(), a.getPreview());
//					Article.updatePubDate(h, artInDB.getId(), a.getPubDate());
//				}
//			}
//			Log.e(LOG + url, output.toString());
//			output.printAllInfo();
			
		} catch (Exception e)
		{
			Log.e(LOG + getUrl(), "Catched Exception: " + e.toString());
			e.printStackTrace();
		}
		return output;
	}

	protected void onPostExecute(Article output)
	{
		//check internet
		if (output != null)
		{
			ServiceArticle.sendDownloadedData(ctx, output, url);
		}
		//NO internet
		else
		{
			ServiceArticle.sendErrorMsg(ctx, url, Const.Error.CONNECTION_ERROR);
			Log.e(LOG + getUrl(), Const.Error.CONNECTION_ERROR);
		}
	}// Событие по окончанию парсинга

	@Override
	protected void onCancelled()
	{
		Log.d(LOG + getUrl(), String.format("onCancelled"));
		super.onCancelled();
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}
}