/*
 07.12.2014
ServiceDB.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.download.ParsePageForAllArtsInfo;
import ru.kuchanov.odnako.fragments.callbacks.AllArtsInfoCallback;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

//tag:^(?!dalvikvm) tag:^(?!libEGL) tag:^(?!Open) tag:^(?!Google) tag:^(?!resour) tag:^(?!Chore) tag:^(?!EGL) tag:^(?!SocketStream)
public class ServiceDB extends Service implements AllArtsInfoCallback
{
	final private static String LOG = ServiceDB.class.getSimpleName() + "/";

	private DataBaseHelper dataBaseHelper;

	private List<ParsePageForAllArtsInfo> currentTasks = new ArrayList<ParsePageForAllArtsInfo>();

	//	List<String> categoriesToCheck;

	public void onCreate()
	{
		Log.d(LOG, "onCreate");
		super.onCreate();

		this.getHelper();

		//		categoriesToCheck = new ArrayList<String>();
		//		String[] menuUrls = CatData.getMenuLinks(this);
		//		//Onotole
		//		categoriesToCheck.add(menuUrls[2]);
		//		//Ideology
		//		categoriesToCheck.add(menuUrls[4]);
		//		//first author (Olga A Agarcove) as I remember
		//		Author firstAuthorOrderedByName;
		//		try
		//		{
		//			firstAuthorOrderedByName = this.getHelper().getDaoAuthor().queryBuilder()
		//			.orderBy(Author.NAME_FIELD_NAME, true).queryForFirst();
		//			categoriesToCheck.add(Author.getURLwithoutSlashAtTheEnd(firstAuthorOrderedByName.getBlog_url()));
		//		} catch (SQLException e)
		//		{
		//			e.printStackTrace();
		//		}
	}

	public int onStartCommand(Intent intent, int flags, int startId)
	{
		//Log.d(LOG, "onStartCommand");
		String catToLoad;
		//firstly: if we load from top or not? Get it by pageToLoad
		int pageToLoad;
		if (intent == null)
		{
			Log.e(LOG, "intent=null!!! WTF?!");
			return super.onStartCommand(intent, flags, startId);
		}
		else
		{
			String action = intent.getAction();
			//Log.e(LOG, "intent.getAction(): "+intent.getAction());
			catToLoad = intent.getStringExtra("categoryToLoad");
			pageToLoad = intent.getIntExtra("pageToLoad", 1);
			if (action.equals(Const.Action.IS_LOADING))
			{
				for (ParsePageForAllArtsInfo a : this.currentTasks)
				{
					if (catToLoad.equals(a.getCategoryToLoad()) && (pageToLoad == a.getPageToLoad())
					&& (a.getStatus() == AsyncTask.Status.RUNNING))
					{
						Intent intentIsLoading = new Intent(catToLoad + Const.Action.IS_LOADING);
						intentIsLoading.putExtra(Const.Action.IS_LOADING, true);
						LocalBroadcastManager.getInstance(this).sendBroadcast(intentIsLoading);
						//						Log.e(LOG + catToLoad, "isLoading == true");
						return super.onStartCommand(intent, flags, startId);
					}
				}
				Intent intentIsLoading = new Intent(catToLoad + Const.Action.IS_LOADING);
				intentIsLoading.putExtra(Const.Action.IS_LOADING, false);
				LocalBroadcastManager.getInstance(this).sendBroadcast(intentIsLoading);
				//				Log.e(LOG + catToLoad, "isLoading == false");
				return super.onStartCommand(intent, flags, startId);
			}
		}

		//get startDownload flag
		boolean startDownload = intent.getBooleanExtra("startDownload", false);

		if (pageToLoad == 1)
		{
			//if pageToLoad=1 we load from top
			//get timeStamp
			Long timeStamp = intent.getLongExtra("timeStamp", System.currentTimeMillis());
			Calendar cal = Calendar.getInstance(TimeZone.getDefault(), new Locale("ru"));
			cal.setTimeInMillis(timeStamp);

			//if there is flag to download we do not need to go to DB
			//we simply start download
			if (startDownload)
			{
				this.startDownLoad(catToLoad, pageToLoad);
			}
			else
			{
				DBActions dbActions = new DBActions(this, this.getHelper());
				String DBRezult = dbActions.askDBFromTop(catToLoad, cal, pageToLoad);
				//				Log.d(LOG + catToLoad, DBRezult);

				switch (DBRezult)
				{
					case Msg.DB_ANSWER_NEVER_REFRESHED:
						//was never refreshed, so start to refresh
						//so start download category with 1-st page
						this.startDownLoad(catToLoad, pageToLoad);
					break;
					case Msg.DB_ANSWER_REFRESH_BY_PERIOD:
						//was refreshed more than max period, so start to refresh
						//so start download category with 1-st page
						//but firstly we must show old articles
						this.startDownLoad(catToLoad, pageToLoad);
					break;

					case Msg.DB_ANSWER_NO_ENTRY_OF_ARTS:
						//no arts in DB (why?)
						//we get it if there is no need to refresh by period, so we have one successful load in past...
						//but no art's in db... that's realy strange! =)
						//so start download from web
						this.startDownLoad(catToLoad, pageToLoad);
					break;
					case Msg.DB_ANSWER_UNKNOWN_CATEGORY:
						//TODO here we must create new entry in Category (or Author) table
						//and start download arts of this category
						this.startDownLoad(catToLoad, pageToLoad);
					break;
					case Msg.DB_ANSWER_INFO_SENDED_TO_FRAG:
					//here we have nothing to do... Cause there is no need to load somthing from web,
					//and arts have been already sended to frag
					break;
				}
			}
		}
		else
		{
			//if pageToLoad!=1 we load from bottom
			//			Log.d(LOG, "LOAD FROM BOTTOM!");
			DBActions dbActions = new DBActions(this, this.getHelper());
			String dBRezult = dbActions.askDBFromBottom(catToLoad, pageToLoad);
			//			Log.d(LOG, dBRezult);

			switch (dBRezult)
			{
				case Msg.DB_ANSWER_FROM_BOTTOM_INFO_SENDED_TO_FRAG:
				//all is done, we can go to drink some vodka, Ivan! =)
				break;
				case Msg.DB_ANSWER_FROM_BOTTOM_INITIAL_ART_ALREADY_SHOWN:
				//initial art is shown, do nothing
				break;
				case Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_HAVE_MATCH_TO_INITIAL:
				//arts already send to frag with initial art, do nothing
				break;
				case Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_NO_INITIAL:
					//so load from web
					this.startDownLoad(catToLoad, pageToLoad);
				break;
				case Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_NO_MATCH_TO_INITIAL:
					//so load from web
					this.startDownLoad(catToLoad, pageToLoad);
				break;
				case Msg.DB_ANSWER_FROM_BOTTOM_LESS_THEN_30_FROM_TOP:
				//initial art is shown (less then 30 in category at all), do nothing
				break;
				case Msg.DB_ANSWER_FROM_BOTTOM_NO_ARTS_AT_ALL:
					//no arts except already shown, so load them from web
					this.startDownLoad(catToLoad, pageToLoad);
				break;
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void startDownLoad(String catToLoad, int pageToLoad)
	{
		//Log.d(LOG, "startDownLoad " + catToLoad + "/page-" + pageToLoad);

		//TODO check quontity and allAuthors situation
		//So, we can have MAX quint of tasks=3 (cur pager frag +left and right)
		//but in case of MenuPager on position=3 && twoPane mode we have 4 tasks -
		//next and prev categories and first 2 authors.
		//And in this case 1-st of these authors is last in queue
		//so we must catch this situation and sort list of tasks

		if (this.currentTasks.size() < 4)
		{
			//Everything is OK. Just add it and execute
			ParsePageForAllArtsInfo parse = new ParsePageForAllArtsInfo(catToLoad, pageToLoad, this, this,
			this.getHelper());
			parse.execute();
			this.currentTasks.add(parse);
		}
		else
		{
			//cancel 1-st and add given to the end
			ParsePageForAllArtsInfo removedParse = this.currentTasks.remove(0);
			//test fixing canceling already finished task
			if (removedParse.getStatus() == AsyncTask.Status.RUNNING)
			{
				Log.e(LOG, removedParse.getCategoryToLoad() + " :" + AsyncTask.Status.RUNNING.toString() + "/RUNNING");
				removedParse.cancel(true);
				this.onError("загрузка прервана", removedParse.getCategoryToLoad(), removedParse.getPageToLoad());
			}

			ParsePageForAllArtsInfo parseToAdd = new ParsePageForAllArtsInfo(catToLoad, pageToLoad, this, this,
			this.getHelper());
			parseToAdd.execute();

			this.currentTasks.add(parseToAdd);
		}
	}

	@Override
	public void sendDownloadedData(ArrayList<Article> dataToSend, String categoryToLoad, int pageToLoad)
	{
		//Log.d(LOG + categoryToLoad, "sendDownloadedData");
		//find and remove finished task from list
		for (int i = 0; i < this.currentTasks.size(); i++)
		{
			if (categoryToLoad.equals(this.currentTasks.get(i).getCategoryToLoad())
			&& pageToLoad == this.currentTasks.get(i).getPageToLoad())
			{
				this.currentTasks.remove(i);
			}
		}
		String[] resultMessage;
		if (dataToSend.size() == 0)
		{
			Article a = new Article();
			a.setTitle("Ни одной статьи не обнаружено.");
			dataToSend.add(a);
			resultMessage = new String[] { Msg.DB_ANSWER_NO_ARTS_IN_CATEGORY, null };
		}
		else
		{
			//here we'll write gained arts to Article table 
			dataToSend = Article.writeArtInfoToArticleTable(getHelper(), dataToSend);

			//here if we recive less then 30 arts (const quont of arts on page)
			//we KNOW that last of them is initial art in category (author)
			//so WRITE it to DB!
			if (dataToSend.size() < 30)
			{
				if (Category.isCategory(this.getHelper(), categoryToLoad))
				{
					int categoryId = Category.getCategoryIdByURL(getHelper(), categoryToLoad);
					String initialArtsUrl = dataToSend.get(dataToSend.size() - 1).getUrl();
					Category.setInitialArtsUrl(this.getHelper(), categoryId, initialArtsUrl);
				}
				else
				{
					int authorId = Author.getAuthorIdByURL(getHelper(), categoryToLoad);
					String initialArtsUrl = dataToSend.get(dataToSend.size() - 1).getUrl();
					Author.setInitialArtsUrl(this.getHelper(), authorId, initialArtsUrl);
				}
			}

			//now update REFRESHED field of Category or Author entry in table if we load from top
			//and write downloaded arts to ArtCatTable
			if (pageToLoad == 1)
			{
				new DBActions(this, this.getHelper()).updateRefreshedDate(categoryToLoad);
				resultMessage = new DBActions(this, this.getHelper()).writeArtsToDBFromTop(dataToSend, categoryToLoad);
			}
			else
			{
				//we don't need to update refreshed Date, cause we do it only when loading from top
				resultMessage = new DBActions(this, this.getHelper()).writeArtsToDBFromBottom(dataToSend,
				categoryToLoad,
				pageToLoad);
				Log.d(LOG + categoryToLoad, resultMessage[0]);
			}
		}
		//		Log.d(LOG + "sendDownloadedData", resultMessage[0]/* +"/"+resultMessage[1] */);
		ServiceDB.sendBroadcastWithResult(this, resultMessage, dataToSend, categoryToLoad, pageToLoad);
	}

	@Override
	public void onError(String e, String categoryToLoad, int pageToLoad)
	{
		//find and remove finished task from list
		for (int i = 0; i < this.currentTasks.size(); i++)
		{
			if (categoryToLoad.equals(this.currentTasks.get(i).getCategoryToLoad())
			&& pageToLoad == this.currentTasks.get(i).getPageToLoad())
			{
				/* ParsePageForAllArtsInfo taskToRemove = */this.currentTasks.remove(i);
				//				taskToRemove = null;
			}
		}

		String[] resultMessage = new String[] { Msg.ERROR, e };
		ServiceDB.sendBroadcastWithResult(this, resultMessage, null, categoryToLoad, pageToLoad);
		//here if we loaded from top and get NO_CONNECTION ERROR we can ask DB for arts.
		//And toast if there are no arts in DB (cache);
		//So ask DB with calendar(time of last refresh);
		Calendar calOfLastRefresh = Calendar.getInstance();
		Boolean isCategory = Category.isCategory(getHelper(), categoryToLoad);
		if (isCategory == null)
		{
			resultMessage = new String[] { Msg.ERROR, "Категория в базе данных не обнаружена. И разработчик очень удивится, узнав, что вы читаете это сообщение" };
			ServiceDB.sendBroadcastWithResult(this, resultMessage, null, categoryToLoad, pageToLoad);
			return;
		}
		if (Category.isCategory(getHelper(), categoryToLoad))
		{
			Category c = Category.getCategoryByURL(getHelper(), categoryToLoad);
			calOfLastRefresh.setTime(c.getRefreshed());
		}
		else
		{
			Author a = Author.getAuthorByURL(getHelper(), categoryToLoad);
			calOfLastRefresh.setTime(a.getRefreshed());
		}
		//if category was never refreshed it's=0, so Toast that there are no arts in cache
		if (calOfLastRefresh.getTimeInMillis() == 0)
		{
			resultMessage = new String[] { Msg.ERROR, "Статей в кэше не обнаружено" };
			ServiceDB.sendBroadcastWithResult(this, resultMessage, null, categoryToLoad, pageToLoad);
			return;
		}
		if (pageToLoad == 1)
		{
			switch (new DBActions(this, this.getHelper()).askDBFromTop(categoryToLoad, calOfLastRefresh, pageToLoad))
			{
				case Msg.DB_ANSWER_NEVER_REFRESHED:
				//that's can't be, because we check it before
				break;
				case Msg.DB_ANSWER_REFRESH_BY_PERIOD:
				//that's can't be, because we give same time that we retrieve from category
				break;
				case Msg.DB_ANSWER_NO_ENTRY_OF_ARTS:
					//That's strange... We have non zero REFRESHED time and given Category record in DB,
					//but no arts... Anyway we tell about it;
					resultMessage = new String[] { Msg.ERROR, "Статей в кэше не обнаружено" };
					ServiceDB.sendBroadcastWithResult(this, resultMessage, null, categoryToLoad, pageToLoad);
				break;
				case Msg.DB_ANSWER_UNKNOWN_CATEGORY:
				//that's can't be, because we check it before
				break;
				case Msg.DB_ANSWER_INFO_SENDED_TO_FRAG:
					//Everything is OK. We send arts from DB to fragment
					resultMessage = new String[] { Msg.ERROR, "Статьи загружены из кэша" };
					ServiceDB.sendBroadcastWithResult(this, resultMessage, null, categoryToLoad, pageToLoad);
				break;
			}//switch
		}//if (pageToLoad == 1)
	}//onError

	public IBinder onBind(Intent intent)
	{
		Log.d(LOG, "onBind");
		return null;
	}

	/**
	 * this method simply returns connection (?) (and open it if neccesary) to
	 * DB with version gained from resources
	 * 
	 * @return
	 */
	private DataBaseHelper getHelper()
	{
		if (dataBaseHelper == null)
		{
			int dbVer = this.getResources().getInteger(R.integer.db_version);
			dataBaseHelper = new DataBaseHelper(this, DataBaseHelper.DATABASE_NAME, null, dbVer);
		}
		return dataBaseHelper;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.d(LOG, "onDestroy");
		if (dataBaseHelper != null)
		{
			OpenHelperManager.releaseHelper();
			dataBaseHelper = null;
		}
	}

	/**
	 * Sends intent with message and data to fragment
	 * 
	 * @param ctx
	 * @param resultMessage
	 * @param dataToSend
	 * @param categoryToLoad
	 * @param pageToLoad
	 */
	public static void sendBroadcastWithResult(Context ctx, String[] resultMessage, ArrayList<Article> dataToSend,
	String categoryToLoad, int pageToLoad)
	{
		//Log.d(LOG + categoryToLoad, "sendBroadcastWithResult");

		Intent intent = new Intent(categoryToLoad);

		Bundle b = new Bundle();
		b.putStringArray(Msg.MSG, resultMessage);
		b.putInt("pageToLoad", pageToLoad);
		b.putParcelableArrayList(Article.KEY_ALL_ART_INFO, dataToSend);
		intent.putExtras(b);

		LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
	}
}

//Log.d(LOG_TAG,
//			cal.get(Calendar.YEAR) + "." + cal.get(Calendar.MONTH) + "." + cal.get(Calendar.DAY_OF_MONTH) + "."
//			+ cal.get(Calendar.HOUR_OF_DAY) + "." + cal.get(Calendar.MINUTE));
//			Log.d(LOG_TAG, cal.getTime().toString());
//			Log.d(LOG_TAG,
//			cal.get(Calendar.DAY_OF_MONTH) + " " + cal.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("ru"))
//			+ " " + cal.get(Calendar.YEAR));
