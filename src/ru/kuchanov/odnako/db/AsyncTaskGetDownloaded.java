/*
 05.04.2015
AskDBFromTop.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.sql.SQLException;
import java.util.ArrayList;

import com.j256.ormlite.stmt.QueryBuilder;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.callbacks.CallbackGetDownloaded;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncTaskGetDownloaded extends AsyncTask<Void, Void, ArrayList<Article>>
{
	final private static String LOG = AsyncTaskGetDownloaded.class.getSimpleName();

	private DataBaseHelper h;
	private String categoryToLoad;
	private int pageToLoad;
	private CallbackGetDownloaded callback;
	ArrayList<Article> dataToSend;

	public AsyncTaskGetDownloaded(DataBaseHelper dataBaseHelper, String categoryToLoad,
	int pageToLoad, CallbackGetDownloaded callback)
	{
		this.h = dataBaseHelper;
		this.categoryToLoad = categoryToLoad;
		this.pageToLoad = pageToLoad;
		this.callback = callback;
	}

	protected ArrayList<Article> doInBackground(Void... args)
	{
		try
		{
			QueryBuilder<Article, Integer> qb = h.getDaoArticle().queryBuilder();
			qb.where().ne(Article.FIELD_NAME_ART_TEXT, Const.EMPTY_STRING);
			qb.orderBy(Article.FIELD_NAME_PUB_DATE, false);
			dataToSend = (ArrayList<Article>) qb.query();
		} catch (SQLException e)
		{
			Log.e(LOG, "error in getDownloaded asyncTask");
			e.printStackTrace();
		}
		return dataToSend;
	}

	protected void onPostExecute(ArrayList<Article> dataToSend)
	{
		this.callback.onGetDownloaded(dataToSend, categoryToLoad, pageToLoad);
	}
}