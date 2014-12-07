/*
 07.12.2014
ServiceDB.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.services;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import ru.kuchanov.odnako.db.DBCategoriesHelper;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class ServiceDB extends Service
{

	final String LOG_TAG = "ServiceDB";

	public void onCreate()
	{
		super.onCreate();
		Log.d(LOG_TAG, "onCreate");
	}

	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.d(LOG_TAG, "onStartCommand");
		if (intent != null)
		{
			//gain category
			String catToLoad;
			catToLoad = intent.getStringExtra("categoryToLoad");
			Log.d(LOG_TAG, catToLoad);
			//get timeStamp
			Long timeStamp;
			timeStamp = intent.getLongExtra("timeStamp", System.currentTimeMillis());
			Calendar cal = Calendar.getInstance(TimeZone.getDefault(), new Locale("ru"));
			cal.setTimeInMillis(timeStamp);
			//get startDownload flag
			boolean startDownload;
			startDownload = intent.getBooleanExtra("startDownload", false);
			//			Log.d(LOG_TAG,
			//			cal.get(Calendar.YEAR) + "." + cal.get(Calendar.MONTH) + "." + cal.get(Calendar.DAY_OF_MONTH) + "."
			//			+ cal.get(Calendar.HOUR_OF_DAY) + "." + cal.get(Calendar.MINUTE));
			//			Log.d(LOG_TAG, cal.getTime().toString());
			//			Log.d(LOG_TAG,
			//			cal.get(Calendar.DAY_OF_MONTH) + " " + cal.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("ru"))
			//			+ " " + cal.get(Calendar.YEAR));

			//get info from appDB. 
			//if it's null - start load and notify caller about it
			//if not, check timeStamp of appBD; 
			//if it's more than 15 min less then given timeStamp return appDB info and notify, that download starts
			//otherwise return appDB and notify that it is upToDate
			//maybe we need FLAG here to start downLoad in all cases
			this.getInfoFromDB(catToLoad, timeStamp, startDownload);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void getInfoFromDB(String catToLoad, Long timeStamp, boolean startDownload)
	{
		//delete
//		DBCategoriesHelper dbCategoriesHelper1=new DBCategoriesHelper(this);
//	    SQLiteDatabase db1 = dbCategoriesHelper1.getWritableDatabase();
//	    db1.delete("categoriestable", null, null);
		this.deleteDatabase("myDB");
		
		//TODO request to DB, now return default data with zero timeStamp
		// подключаемся к БД
		DBCategoriesHelper dbCategoriesHelper=new DBCategoriesHelper(this);
	    SQLiteDatabase db = dbCategoriesHelper.getWritableDatabase();
	    
	    
		
	}

	public void onDestroy()
	{
		super.onDestroy();
		Log.d(LOG_TAG, "onDestroy");
	}

	public IBinder onBind(Intent intent)
	{
		Log.d(LOG_TAG, "onBind");
		return null;
	}

}
