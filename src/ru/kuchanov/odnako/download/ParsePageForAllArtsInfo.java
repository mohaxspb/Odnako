/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.download;

import java.net.URL;
import java.util.ArrayList;

import ru.kuchanov.odnako.fragments.callbacks.AllArtsInfoCallback;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

public class ParsePageForAllArtsInfo extends AsyncTask<Void, Void, ArrayList<ArtInfo>>
{
	private final static String LOG = ParsePageForAllArtsInfo.class.getSimpleName();

	AllArtsInfoCallback callback;

	String link;

	String category;
	int page;

	ActionBarActivity act;
	Context ctx;

	public ParsePageForAllArtsInfo(String category, int page, Context ctx, AllArtsInfoCallback callback)
	{
		this.callback = callback;

		this.category = category;
		this.page = page;

		this.ctx = ctx;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	protected ArrayList<ArtInfo> doInBackground(Void... arg)
	{
		//		System.out.println("ParsePageForAllArtsInfo: doInBackground");
		ArrayList<ArtInfo> output = null;
		String link;
		if (this.category.startsWith("http://"))
		{
			link = category + "/page-" + String.valueOf(this.page) + "/";
		}
		else
		{
			link = "http://" + category + "/page-" + String.valueOf(this.page) + "/";
		}
		if (this.link != null)
		{
			link = this.link;
			Log.d(category, link);
		}

		try
		{
			HtmlHelper hh = new HtmlHelper(new URL(link));
			if (hh.isAuthor())
			{
				output = hh.getAllArtsInfoFromAUTHORPage();
			}
			else
			{
				output = hh.getAllArtsInfoFromPage();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return output;
	}

	protected void onPostExecute(ArrayList<ArtInfo> output)
	{
		//Log.d(LOG, "ParseBlogsPageNew: onPostExecute");
		//check internet
		if (output != null)
		{
			callback.sendDownloadedData(output, this.category, this.page);
		}
		//NO internet
		else
		{
			callback.onError("Ошибка соединения \n Проверьте соединение с интернетом", this.category, this.page);
			Log.e(LOG, "Ошибка соединения \n Проверьте соединение с интернетом");
//			Toast.makeText(ctx, "Ошибка соединения \n Проверьте соединение с интернетом", Toast.LENGTH_LONG).show();
		}
	}// Событие по окончанию парсинга
}
