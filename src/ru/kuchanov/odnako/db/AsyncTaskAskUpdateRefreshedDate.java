/*
 05.04.2015
AskDBFromTop.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.sql.SQLException;
import java.util.Date;

import com.j256.ormlite.stmt.UpdateBuilder;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Searches trough DB for art, that can be send to caller, or starts downloading
 * from web if there are no needed arts or no so many as caller wish or it's
 * time to refresh DB
 * 
 * @param categoryToLoad
 * @param cal
 *            holder of timeStamp, that is matched to refreshing period
 * @param pageToLoad
 *            to switch between from top/bottom loading
 * @return answer, that is used to switch between next actions (loading from
 *         web/ get data from DB or some SQLException)
 */
public class AsyncTaskAskUpdateRefreshedDate extends AsyncTask<Void, Void, Void>
{
	final private static String LOG = AsyncTaskAskUpdateRefreshedDate.class.getSimpleName();

	private DataBaseHelper h;
	private String categoryToLoad;

	public AsyncTaskAskUpdateRefreshedDate(DataBaseHelper dataBaseHelper, String categoryToLoad)
	{
		this.h = dataBaseHelper;
		this.categoryToLoad = categoryToLoad;
	}

	//write refreshed date to entry of given category
	protected Void doInBackground(Void... args)
	{

		if (Category.isCategory(h, categoryToLoad))
		{
			try
			{
				UpdateBuilder<Category, Integer> updateBuilder = h.getDaoCategory().updateBuilder();
				updateBuilder.updateColumnValue(Category.REFRESHED_FIELD_NAME, new Date(System.currentTimeMillis()));
				updateBuilder.where().eq(Category.URL_FIELD_NAME, categoryToLoad);
				updateBuilder.update();
			} catch (SQLException e)
			{
				Log.e(LOG, "SQLException updateRefreshedDate CATEGORY");
			}
		}
		else
		{
			try
			{
				UpdateBuilder<Author, Integer> updateBuilder = h.getDaoAuthor().updateBuilder();
				updateBuilder.updateColumnValue(Author.REFRESHED_FIELD_NAME, new Date(System.currentTimeMillis()));
				//DO NOT forget, that we can receive categoryToLoad with or without "/" at the and!
				updateBuilder.where().eq(Author.URL_FIELD_NAME, Author.getURLwithoutSlashAtTheEnd(categoryToLoad));
				updateBuilder.update();
			} catch (SQLException e)
			{
				Log.e(LOG, "SQLException updateRefreshedDate AUTHOR");
			}
		}
		return null;
	}

	protected void onPostExecute(Void result)
	{

	}
}