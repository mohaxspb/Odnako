/*
 10.03.2015
ServiceRSS.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.download.ParseRSSForDateAndPreview;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class ServiceRSS extends Service
{
	final private static String LOG = ServiceRSS.class.getSimpleName() + "/";

	private DataBaseHelper dataBaseHelper;

	private Context ctx;

	public void onCreate()
	{
		Log.d(LOG, "onCreate");
		super.onCreate();

		this.ctx = this;
		this.getHelper();
	}

	public int onStartCommand(Intent intent, int flags, int startId)
	{
		//Log.d(LOG, "onStartCommand");
		String categoryToLoad;
		if (intent == null)
		{
			Log.e(LOG, "intent=null!!! WTF?!");
			return super.onStartCommand(intent, flags, startId);
		}
		categoryToLoad = intent.getStringExtra("categoryToLoad");
		ParseRSSForDateAndPreview rssParser = new ParseRSSForDateAndPreview(ctx, categoryToLoad, getHelper());
		rssParser.execute();

		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * this method simply returns connection (?) (and open it if neccecery) to
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

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
}