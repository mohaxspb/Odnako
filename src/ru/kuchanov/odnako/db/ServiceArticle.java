/*
 10.03.2015
ServiceRSS.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.download.ParseArticle;
import ru.kuchanov.odnako.fragments.FragmentArticle;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ServiceArticle extends Service
{
	final private static String LOG = ServiceArticle.class.getSimpleName() + "/";

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
		String url;
		if (intent == null)
		{
			Log.e(LOG, "intent=null!");
			return super.onStartCommand(intent, flags, startId);
		}
		url = intent.getStringExtra(FragmentArticle.ARTICLE_URL);
		Log.e(LOG, "start download article: "+url);
		ParseArticle articleParser = new ParseArticle(ctx, url, getHelper());
		articleParser.execute();

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
