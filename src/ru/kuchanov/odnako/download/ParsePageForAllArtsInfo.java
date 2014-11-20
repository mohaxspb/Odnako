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
import android.widget.Toast;

public class ParsePageForAllArtsInfo extends AsyncTask<Void, Void, ArrayList<ArtInfo>>
{
	/////test CallBack
	AllArtsInfoCallback callback;
	/////////
	
	String category;
	int page;

	ActionBarActivity act;
	Context ctx;

	/**
	 * 
	 */
	//	public ParsePageForAllArtsInfo(String category, int page, ActionBarActivity act)
	public ParsePageForAllArtsInfo(String category, int page, Context ctx, AllArtsInfoCallback callback)
	{
		this.callback=callback;
		
		this.category = category;
		this.page = page;

		this.ctx = ctx;
	}

	protected ArrayList<ArtInfo> doInBackground(Void... arg)
	{
		System.out.println("ParsePageForAllArtsInfo: doInBackground");
		ArrayList<ArtInfo> output = null;
		String link = "http://" + category + "/page-" + String.valueOf(this.page) + "/";
		System.out.println(link);
		try
		{
			HtmlHelper hh = new HtmlHelper(new URL(link));
			if(hh.isAuthor())
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
		System.out.println("ParseBlogsPageNew: onPostExecute");
		//check internet
		if (output != null)
		{
			callback.doSomething(output, this.category);
		}
		//NO internet
		else
		{
			Toast.makeText(ctx, "Ошибка соединения \n Проверьте соединение с интернетом", Toast.LENGTH_LONG).show();
		}
	}// Событие по окончанию парсинга

}
