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
					//get info from appDB. 
					//if it's null - start load and notify caller about it
					//if not, check timeStamp of appBD; 
					//if it's more than 15 min less then given timeStamp return appDB info and notify, that download starts
					//otherwise return appDB and notify that it is upToDate

					//method will return result of searching throw DB
					DBActions dbActions = new DBActions(this, this.getHelper());
//					try
//					{
//						if (Category.getCategoryIdByURL(getHelper(), catToLoad) == 84)
//						{
//							dbActions.test(catToLoad);
//						}
//					} catch (Exception e)
//					{
//					}
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
				//if pageToLoad!=1 we load from bottom
				Log.d(LOG_TAG, "LOAD FROM BOTTOM!");
				//TODO DO NOT NEED????!!! here we ask DB  if it's sinked
				////aks db for arts
//				List<ArtCatTable> allArts=ArtCatTable
				//////if we have no arts, we load them from web
				//////if we have >30 we pass 30 to fragment
				//////else we ask category if it has firstArtURL
				////////if so we pass arts to fragment
				////////else we must load arts from web
				//////////if we get <30 we set last art's URL as first art of category and write arts to db(Article and ArtCat)
				//////////else simply write arts to db(Article and ArtCat)
				
				
				////TODO DO NOT NEED????!!!  if unsinked we load arts from web
				////XXX match gained arts with ArtCat entries from id>30*pageToLoad
				/////XXX if match we mark category as sinked and write arts to db between id>30*pageToLoad and first match
				/////XXX else write arts to db after id>30*pageToLoad
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
			//we don need to update refreshed Date, cause we do it only when loading from top
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
