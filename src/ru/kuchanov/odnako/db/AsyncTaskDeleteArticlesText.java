/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.activities.ActivityPreference;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.DataBaseHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class AsyncTaskDeleteArticlesText extends AsyncTask<Void, Void, ArrayList<Article>>
{
	private final static String LOG = AsyncTaskDeleteArticlesText.class.getSimpleName() + "/";

	/**
	 * type for deleting articles if their quont more than 10
	 */
	public final static int REQUEST_TYPE_STANDART = 0;
	/**
	 * type for deleting all arts Text
	 */
	public final static int REQUEST_TYPE_DELETE_ALL = 1;
	/**
	 * type for deleting all arts Text
	 */
	public final static int REQUEST_TYPE_DELETE_SOME = 2;

	private Context ctx;
	private DataBaseHelper h;
	private int type = 0;

	private SharedPreferences pref;

	public AsyncTaskDeleteArticlesText(Context ctx, int requestType)
	{
		this.ctx = ctx;
		this.h = new DataBaseHelper(ctx);
		this.type = requestType;

		this.pref = PreferenceManager.getDefaultSharedPreferences(ctx);
	}

	@Override
	protected ArrayList<Article> doInBackground(Void... arg)
	{
		ArrayList<Article> artsToDelete = new ArrayList<Article>();

		QueryBuilder<Article, Integer> qb;

		int maxArtsToStore = Integer.valueOf(pref.getString(ActivityPreference.PREF_KEY_MAX_ARTICLES_TO_STORE, "10"));
		boolean noLimit = (maxArtsToStore == 42);

		if (noLimit)
		{
			artsToDelete = new ArrayList<Article>();
			return artsToDelete;
		}

		switch (this.type)
		{
			case REQUEST_TYPE_STANDART:
				//find arts with text 
				//if their quont more than 10 sort them by refreshed date
				//and delete old to get no more than 10 downloaded arts;
				try
				{
					qb = h.getDaoArticle().queryBuilder();
					qb.where().ne(Article.FIELD_NAME_ART_TEXT, Const.EMPTY_STRING);
					qb.orderBy(Article.FIELD_NAME_REFRESHED_DATE, false);

					ArrayList<Article> allDownloadedArts = (ArrayList<Article>) qb.query();

					if (allDownloadedArts.size() > maxArtsToStore)
					{
						for (int i = maxArtsToStore; i < allDownloadedArts.size(); i++)
						{
							Article.updateArtText(h, allDownloadedArts.get(i).getId(), Const.EMPTY_STRING);
							Article.updateRefreshedDate(h, allDownloadedArts.get(i).getId(), new Date(0));

							allDownloadedArts.get(i).setArtText(Const.EMPTY_STRING);
							allDownloadedArts.get(i).setRefreshed(new Date(0));
							artsToDelete.add(allDownloadedArts.get(i));
						}
					}
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			break;
			case REQUEST_TYPE_DELETE_ALL:
				try
				{
					qb = h.getDaoArticle().queryBuilder();
					qb.where().ne(Article.FIELD_NAME_ART_TEXT, Const.EMPTY_STRING);
					qb.orderBy(Article.FIELD_NAME_REFRESHED_DATE, false);

					ArrayList<Article> allDownloadedArts = (ArrayList<Article>) qb.query();

					UpdateBuilder<Article, Integer> ub = h.getDaoArticle().updateBuilder();
					ub.updateColumnValue(Article.FIELD_NAME_ART_TEXT, Const.EMPTY_STRING);
					ub.where().ne(Article.FIELD_NAME_ART_TEXT, Const.EMPTY_STRING);
					ub.update();

					artsToDelete = allDownloadedArts;
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			break;
		}
		return artsToDelete;
	}

	protected void onPostExecute(ArrayList<Article> output)
	{
		for (Article a : output)
		{
			//Send intent with article to another activities to be able to update their data
			Intent intentGlobal = new Intent(Const.Action.ARTICLE_CHANGED);
			intentGlobal.putExtra(Article.KEY_CURENT_ART, a);
			intentGlobal.putExtra(Const.Action.ARTICLE_CHANGED, Const.Action.ARTICLE_DELETED);
			LocalBroadcastManager.getInstance(ctx).sendBroadcast(intentGlobal);
		}

		this.h.close();
		this.h = null;
	}

	@Override
	protected void onCancelled()
	{
		Log.d(LOG, String.format("mAsyncTask - onCancelled: isCancelled = %b: ", this.isCancelled()));
		this.h.close();
		this.h = null;
		super.onCancelled();
	}
}