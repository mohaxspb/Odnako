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

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.db.DBActions.Msg;
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
		if (intent != null)
		{
			//gain category
			String catToLoad;
			catToLoad = intent.getStringExtra("categoryToLoad");
			//get startDownload flag
			boolean startDownload;
			startDownload = intent.getBooleanExtra("startDownload", false);
			//firstly: if we load from top or not? Get it by pageToLoad
			//get pageToLoad
			int pageToLoad;
			pageToLoad = intent.getIntExtra("pageToLoad", 1);
			if (pageToLoad == 1)
			{
				//if pageToLoad=1 we load from top
				//get timeStamp
				Long timeStamp;
				timeStamp = intent.getLongExtra("timeStamp", System.currentTimeMillis());
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
					String DBRezult = dbActions.getInfoFromDB(catToLoad, cal, pageToLoad);
					Log.d(LOG_TAG, DBRezult);

					Intent i = new Intent(catToLoad + DBActions.Msg.MSG);
					switch (DBRezult)
					{
						case DBActions.DB_ANSWER_SQLEXCEPTION_CAT:
							intent.putExtra(DBActions.Msg.MSG, Msg.DB_ANSWER_SQLEXCEPTION_CAT);
							LocalBroadcastManager.getInstance(this).sendBroadcast(i);
						break;
						case DBActions.DB_ANSWER_SQLEXCEPTION_AUTHOR:
							intent.putExtra(DBActions.Msg.MSG, Msg.DB_ANSWER_SQLEXCEPTION_AUTHOR);
							LocalBroadcastManager.getInstance(this).sendBroadcast(i);
						break;
						case DBActions.DB_ANSWER_SQLEXCEPTION_ARTS:
							intent.putExtra(DBActions.Msg.MSG, Msg.DB_ANSWER_SQLEXCEPTION_ARTS);
							LocalBroadcastManager.getInstance(this).sendBroadcast(i);
						break;
						case DBActions.DB_ANSWER_SQLEXCEPTION_ARTCAT:
							intent.putExtra(DBActions.Msg.MSG, Msg.DB_ANSWER_SQLEXCEPTION_ARTCAT);
							LocalBroadcastManager.getInstance(this).sendBroadcast(i);
						break;
						case DBActions.DB_ANSWER_NEVER_REFRESHED:
							//was never refreshed, so start to refresh
							//so start download category with 1-st page
							this.startDownLoad(catToLoad, pageToLoad);
						break;
						case DBActions.DB_ANSWER_REFRESH_BY_PERIOD:
							//was refreshed more than max period, so start to refresh
							//so start download category with 1-st page
							//but firstly we must show old articles
							this.startDownLoad(catToLoad, pageToLoad);
						break;

						case DBActions.DB_ANSWER_NO_ENTRY_OF_ARTS:
							//no arts in DB (why?)
							//we get it if there is no need to refresh by period, so we have one successful load in past...
							//but no art's in db... that's realy strange! =)
							//so start download from web
							this.startDownLoad(catToLoad, pageToLoad);
						break;
						case DBActions.DB_ANSWER_UNKNOWN_CATEGORY:
						//TODO here we must create new entry in Category (or Author) table
						//and start download arts of this category

						break;
						case DBActions.DB_ANSWER_INFO_SENDED_TO_FRAG:
						//here we have nothing to do... Cause there is no need to load somthing from web,
						//and arts have been already sended to frag
						break;
					}
				}
			}
			else
			{
				//TODO switch by category or author
				//if pageToLoad!=1 we load from bottom
				Log.d(LOG_TAG, "LOAD FROM BOTTOM!");
				////aks db for arts
				int categoryId = Category.getCategoryIdByURL(getHelper(), catToLoad);
				List<ArtCatTable> allArtsFromFirst;
				//pageToLoad-1 because here we do not need next arts, only arts, that already showed
				allArtsFromFirst = ArtCatTable.getListFromTop(getHelper(), categoryId, pageToLoad - 1);

				//firstly, if we have <30 arts from top, there is initial art in this list, so we must DO NOTHING!
				if (allArtsFromFirst.size() < 30)
				{
					//TODO DO NOTHING, return, that it's start of list
				}
				//so now we have first 30*pageToLoad-1 arts. Now get next 30 by passing last id to same method
				List<ArtCatTable> allArts;
				int lastId = allArtsFromFirst.get(allArtsFromFirst.size() - 1).getId();
				allArts = ArtCatTable.getArtCatTableListByCategoryIdFromGivenId(getHelper(), categoryId, lastId, false);

				//////if we have no arts, we load them from web
				if (allArts == null)
				{
					//load from web
					Log.d(LOG_TAG, "No arts at all, load from web");
					this.startDownLoad(catToLoad, pageToLoad);
				}
				else
				{
					if (allArts.size() == 30)
					{
						//////if we have 30, so we pass 30 to fragment
						Log.d(LOG_TAG, "we have 30, so we pass 30 to fragment");

						//set ArtCatTable obj to ArtInfo
						//firstly get Article by id then create new ArtInfo obj and add it to list, that we'll send
						ArrayList<ArtInfo> data = new ArrayList<ArtInfo>();
						for (ArtCatTable a : allArts)
						{
							Article art = Article.getArticleById(getHelper(), a.getArticleId());
							ArtInfo artInfoObj = new ArtInfo(art.getAsStringArray());
							data.add(artInfoObj);
						}
						//send directly, cause it's from DB and we do not need to do something with this data
						Intent intentWithData = new Intent(catToLoad);
						intentWithData.putParcelableArrayListExtra(ArtInfo.KEY_ALL_ART_INFO, data);

						LocalBroadcastManager.getInstance(this).sendBroadcast(intentWithData);
					}
					else
					{
						//////else we ask category if it has firstArtURL
						String firstArtInCatURL = null;
						firstArtInCatURL = Category.getFirstArticleURLById(getHelper(), categoryId);
						if (firstArtInCatURL == null)
						{
							////////if so we load from web
							Log.d(LOG_TAG, "we have LESS than 30, but no initial art. So load from web");
							Log.d(LOG_TAG, "allArts.size(): " + allArts.size());
							this.startDownLoad(catToLoad, pageToLoad);
							//if we get <30 we set last art's URL as first art of Category
							//and write arts to db(Article and ArtCat)
							////else simply write arts to db(Article and ArtCat)
						}
						else
						{
							//check matching last of gained URL with initial (first)
							String lastInListArtsUrl = Article.getArticleUrlById(getHelper(),
							allArts.get(allArts.size() - 1).getArticleId());
							if (firstArtInCatURL.equals(lastInListArtsUrl))
							{
								Log.d(LOG_TAG,
								"we have LESS than 30, and have match to initial. So send all and never load more");
								Log.d(LOG_TAG, "allArts.size(): " + allArts.size());
								//TODO so it is real end of all arts in category
								//send arts to frag
								//notify not to load (may be we can pass initial art to sharedPrefs...)
							}
							else
							{
								////////else we must load arts from web
								Log.d(LOG_TAG, "we have LESS than 30, and have NO match to initial. So load from web");
								Log.d(LOG_TAG, "allArts.size(): " + allArts.size());
								this.startDownLoad(catToLoad, pageToLoad);
								//if we get <30 we set last art's URL as first art of Category
								//and write arts to db(Article and ArtCat)
								////else simply write arts to db(Article and ArtCat)
							}
						}
					}
				}
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

	// Send intent with ArtInfo to recivers
	private void sendMessage(ArrayList<ArtInfo> someResult, String categoryToLoad, int pageToLoad)
	{
		Intent intent = new Intent(categoryToLoad);
		Bundle b = new Bundle();
		if (someResult.size() != 0)
		{
			b.putParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO, someResult);
			//before sending message to listener (frag) we must write gained info to DB
			new DBActions(this, this.getHelper()).writeArtsToDB(someResult, categoryToLoad, pageToLoad);
		}
		else
		{
			String[] artInfoArr = new String[] { "empty", "Ни одной статьи не обнаружено.", "empty", "empty", "empty" };
			someResult.add(new ArtInfo(artInfoArr));
			b.putParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO, someResult);
		}
		intent.putExtras(b);

		//now update REFRESHED field of Category or Author entry in table if we load from top
		if (pageToLoad == 1)
		{
			new DBActions(this, this.getHelper()).updateRefreshedDate(categoryToLoad);
		}
		else
		{
			//we don't need to update refreshed Date, cause we do it only when loading from top
		}

		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	@Override
	public void doSomething(ArrayList<ArtInfo> someResult, String categoryToLoad, int pageToLoad)
	{
		this.sendMessage(someResult, categoryToLoad, pageToLoad);
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
}

//Log.d(LOG_TAG,
//			cal.get(Calendar.YEAR) + "." + cal.get(Calendar.MONTH) + "." + cal.get(Calendar.DAY_OF_MONTH) + "."
//			+ cal.get(Calendar.HOUR_OF_DAY) + "." + cal.get(Calendar.MINUTE));
//			Log.d(LOG_TAG, cal.getTime().toString());
//			Log.d(LOG_TAG,
//			cal.get(Calendar.DAY_OF_MONTH) + " " + cal.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("ru"))
//			+ " " + cal.get(Calendar.YEAR));
