/*
 05.04.2015
AskDBFromTop.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.callbacks.CallbackAskDBFromTop;
import android.content.Context;
import android.os.AsyncTask;

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
public class AsyncTaskAskDBFromTop extends AsyncTask<Void, Void, String>
{
	private Context ctx;
	private DataBaseHelper h;
	private String categoryToLoad;
	private Calendar cal;
	private int pageToLoad;
	private CallbackAskDBFromTop callback;
	ArrayList<Article> dataToSend;

	public AsyncTaskAskDBFromTop(Context ctx, DataBaseHelper dataBaseHelper, String categoryToLoad, Calendar cal,
	int pageToLoad, CallbackAskDBFromTop callback)
	{
		this.ctx = ctx;
		this.h = dataBaseHelper;
		this.categoryToLoad = categoryToLoad;
		this.cal = cal;
		this.pageToLoad = pageToLoad;
		this.callback = callback;
	}

	protected String doInBackground(Void... args)
	{
		//String answer = Msg.DB_ANSWER_REFRESH_BY_PERIOD;
		//check if this is known category or author
		if (Category.isCategory(h, categoryToLoad) == null)
		{
			return Msg.DB_ANSWER_UNKNOWN_CATEGORY;
		}

		//try to find db entry for given catToLoad
		//first in Category
		if (Category.isCategory(h, categoryToLoad))
		{
			Category cat = Category.getCategoryByURL(h, categoryToLoad);
			//first try to know when was last sink
			long lastRefreshedMills = cat.getRefreshed().getTime();
			if (lastRefreshedMills == 0)
			{
				//was never refreshed, so start to refresh
				return Msg.DB_ANSWER_NEVER_REFRESHED;
			}
			else
			{
				//check period from last sink
				int millsInSecond = 1000;
				long millsInMinute = millsInSecond * 60;
				//time in Minutes. Default period between refreshing.
				int checkPeriod = ctx.getResources().getInteger(R.integer.checkPeriod);
				int givenMinutes = (int) (cal.getTimeInMillis() / millsInMinute);
				int refreshedMinutes = (int) (lastRefreshedMills / millsInMinute);
				int periodFromRefreshedInMinutes = givenMinutes - refreshedMinutes;
				if (periodFromRefreshedInMinutes > checkPeriod)
				{
					return Msg.DB_ANSWER_REFRESH_BY_PERIOD;
				}
				else
				{
					//we do not have to refresh by period,
					//so go to check if there is info in db
				}
			}
			int categoryId = cat.getId();
			if (ArtCatTable.categoryArtsExists(h, categoryId))
			{
				//so there is some arts in DB by category, that we can send to frag and show
				List<ArtCatTable> dataFromDBToSend = ArtCatTable.getListFromTop(h, categoryId, pageToLoad);
				ArrayList<Article> data = ArtCatTable.getArticleListFromArtCatList(h, dataFromDBToSend);
				this.dataToSend = data;

				return Msg.DB_ANSWER_INFO_SENDED_TO_FRAG;
			}
			else
			{
				//there are no arts of given category in DB, so start to load it
				return Msg.DB_ANSWER_NO_ENTRY_OF_ARTS;
			}
		}
		else
		{
			//this is Author
			Author aut = Author.getAuthorByURL(h, Author.getURLwithoutSlashAtTheEnd(categoryToLoad));

			//first try to know when was last sink
			long lastRefreshedMills = aut.getRefreshed().getTime();
			if (lastRefreshedMills == 0)
			{
				//was never refreshed, so start to refresh
				return Msg.DB_ANSWER_NEVER_REFRESHED;
			}
			else
			{
				//check period from last sink
				//check period from last sink
				int millsInSecond = 1000;
				long millsInMinute = millsInSecond * 60;
				//time in Minutes. Default period between refreshing.
				int checkPeriod = ctx.getResources().getInteger(R.integer.checkPeriod);
				int givenMinutes = (int) (cal.getTimeInMillis() / millsInMinute);
				int refreshedMinutes = (int) (lastRefreshedMills / millsInMinute);
				int periodFromRefreshedInMinutes = givenMinutes - refreshedMinutes;
				if (periodFromRefreshedInMinutes > checkPeriod)
				{
					return Msg.DB_ANSWER_REFRESH_BY_PERIOD;
				}
				else
				{
					//we do not have to refresh by period,
					//so go to check if there is info in db
				}
			}
			int authorId = aut.getId();
			if (ArtAutTable.authorArtsExists(h, authorId))
			{
				//so there is some arts in DB by category, that we can send to frag and show
				List<ArtAutTable> dataFromDBToSend = ArtAutTable.getListFromTop(h, authorId, pageToLoad);
				ArrayList<Article> data = ArtAutTable.getArtInfoListFromArtAutList(h, dataFromDBToSend);
//				String[] resultMessage = new String[] { Msg.DB_ANSWER_INFO_SENDED_TO_FRAG, null };
				//				ServiceDB.sendBroadcastWithResult(ctx, resultMessage, data, categoryToLoad, pageToLoad);
				//				new ServiceDB().sendBroadcastWithResult(ctx, resultMessage, data, categoryToLoad, pageToLoad);
				this.dataToSend = data;

				return Msg.DB_ANSWER_INFO_SENDED_TO_FRAG;
			}
			else
			{
				//there are no arts of given category in DB, so start to load it
				return Msg.DB_ANSWER_NO_ENTRY_OF_ARTS;
			}
		}
	}

	protected void onPostExecute(String result)
	{
		this.callback.onAnswerFromDBFromTop(result, categoryToLoad, pageToLoad, dataToSend);
	}
}