/*
 07.12.2014
ServiceDB.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.download.ParsePageForAllArtsInfo;
import ru.kuchanov.odnako.fragments.callbacks.AllArtsInfoCallback;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

//tag:^(?!dalvikvm) tag:^(?!libEGL) tag:^(?!Open) tag:^(?!Google) tag:^(?!resour) tag:^(?!Chore) tag:^(?!EGL) tag:^(?!SocketStream)
public class ServiceDB extends Service implements AllArtsInfoCallback
{
	final private static String LOG_TAG = ServiceDB.class.getSimpleName();

	private DataBaseHelper dataBaseHelper;

	public void onCreate()
	{
		Log.d(LOG_TAG, "onCreate");
		super.onCreate();

		this.getHelper();
	}

	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.d(LOG_TAG, "onStartCommand");
		if (intent == null)
		{
			Log.e(LOG_TAG, "intent=null!!! WTF?!");
			return super.onStartCommand(intent, flags, startId);
		}
		//get category
		String catToLoad = intent.getStringExtra("categoryToLoad");
		//get startDownload flag
		boolean startDownload = intent.getBooleanExtra("startDownload", false);
		//firstly: if we load from top or not? Get it by pageToLoad
		int pageToLoad = intent.getIntExtra("pageToLoad", 1);
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
				Log.d(LOG_TAG, DBRezult);

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
			Log.d(LOG_TAG, "LOAD FROM BOTTOM!");
			DBActions dbActions = new DBActions(this, this.getHelper());
			String dBRezult = dbActions.askDBFromBottom(catToLoad, pageToLoad);
			Log.d(LOG_TAG, dBRezult);

			switch (dBRezult)
			{
				case Msg.DB_ANSWER_FROM_BOTTOM_INFO_SENDED_TO_FRAG:
				//all is done, we can go to drink some vodka, Ivan! =)
				break;
				case Msg.DB_ANSWER_FROM_BOTTOM_INITIAL_ART_ALREADY_SHOWN:
				//initial art is shown, do nothing
				break;
				case Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_HAVE_MATCH_TO_INITIAL:
				//arts already sended to frag with initial art, do nothing
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
		Log.d(LOG_TAG, "startDownLoad " + catToLoad + "/page-" + pageToLoad);
		Context context = getApplicationContext();
		ParsePageForAllArtsInfo parse = new ParsePageForAllArtsInfo(catToLoad, pageToLoad, context, this);
		parse.execute();
	}

	@Override
	public void sendDownloadedData(ArrayList<ArtInfo> dataToSend, String categoryToLoad, int pageToLoad)
	{
		//		String[] resultMessage;
		//		if (dataToSend.size() != 0)
		//		{
		//			//before sending message to listener (frag) we must write gained info to DB
		//			resultMessage = new DBActions(this, this.getHelper()).writeArtsToDB(dataToSend, categoryToLoad, pageToLoad);
		//		}
		//		else
		//		{
		//			String[] artInfoArr = new String[] { "empty", "Ни одной статьи не обнаружено.", "empty", "empty", "empty" };
		//			dataToSend.add(new ArtInfo(artInfoArr));
		//			resultMessage = new String[]{Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_WRITE, null};
		//		}
		//		//now update REFRESHED field of Category or Author entry in table if we load from top
		//		if (pageToLoad == 1)
		//		{
		//			new DBActions(this, this.getHelper()).updateRefreshedDate(categoryToLoad);
		//		}
		//		else
		//		{
		//			//we don't need to update refreshed Date, cause we do it only when loading from top
		//		}
		//		ServiceDB.sendBroadcastWithResult(this, resultMessage, dataToSend, categoryToLoad, pageToLoad);
		String[] resultMessage;
		if (dataToSend.size() == 0)
		{
			String[] artInfoArr = new String[] { "empty", "Ни одной статьи не обнаружено.", "empty", "empty", "empty" };
			dataToSend.add(new ArtInfo(artInfoArr));
			resultMessage = new String[] { Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_WRITE, null };
		}
		else
		{
			//here we'll write gained arts to Article table
			Article.writeArtInfoToArticleTable(getHelper(), dataToSend);

			//here if we recive less then 30 arts (const quont of arts on page)
			//we KNOW that last of them is initial art in category (author)
			//so WRITE it to DB!
			if (dataToSend.size() < 30)
			{
				if (Category.isCategory(this.getHelper(), categoryToLoad))
				{
					int categoryId = Category.getCategoryIdByURL(getHelper(), categoryToLoad);
					String initialArtsUrl = dataToSend.get(dataToSend.size() - 1).url;
					Category.setInitialArtsUrl(this.getHelper(), categoryId, initialArtsUrl);
				}
				else
				{
					int authorId = Author.getAuthorIdByURL(getHelper(), categoryToLoad);
					String initialArtsUrl = dataToSend.get(dataToSend.size() - 1).url;
					Author.setInitialArtsUrl(this.getHelper(), authorId, initialArtsUrl);
				}
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
			resultMessage = new DBActions(this, this.getHelper()).writeArtsToDBFromBottom(dataToSend, categoryToLoad,
			pageToLoad);
		}
		ServiceDB.sendBroadcastWithResult(this, resultMessage, dataToSend, categoryToLoad, pageToLoad);
	}

	@Override
	public void onError(String e, String categoryToLoad, int pageToLoad)
	{
		String[] resultMessage = new String[] { Msg.ERROR, e };
		ServiceDB.sendBroadcastWithResult(this, resultMessage, null, categoryToLoad, pageToLoad);
	}

	public IBinder onBind(Intent intent)
	{
		Log.d(LOG_TAG, "onBind");
		return null;
	}

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
		Log.d(LOG_TAG, "onDestroy");
		if (dataBaseHelper != null)
		{
			OpenHelperManager.releaseHelper();
			dataBaseHelper = null;
		}
	}

	public static void sendBroadcastWithResult(Context ctx, String[] resultMessage, ArrayList<ArtInfo> dataToSend,
	String categoryToLoad, int pageToLoad)
	{
		Intent intent = new Intent(categoryToLoad);

		Bundle b = new Bundle();
		b.putStringArray(Msg.MSG, resultMessage);
		b.putInt("pageToLoad", pageToLoad);
		b.putParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO, dataToSend);
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
