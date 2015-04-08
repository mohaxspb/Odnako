/*
 10.03.2015
ServiceRSS.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.util.ArrayList;
import java.util.List;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.download.ParseArticle;
import ru.kuchanov.odnako.fragments.FragmentArticle;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class ServiceArticle extends Service
{
	final private static String LOG = ServiceArticle.class.getSimpleName() + "/";

	private DataBaseHelper dataBaseHelper;

	private static List<ParseArticle> currentTasks = new ArrayList<ParseArticle>();

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

		String action = intent.getAction();
		url = intent.getStringExtra(FragmentArticle.ARTICLE_URL);
		if (action.equals(Const.Action.IS_LOADING))
		{
			for (ParseArticle a : ServiceArticle.currentTasks)
			{
				if (url.equals(a.getUrl()) && (a.getStatus() == AsyncTask.Status.RUNNING))
				{
					Intent intentIsLoading = new Intent(url + Const.Action.IS_LOADING);
					intentIsLoading.putExtra(Const.Action.IS_LOADING, true);
					LocalBroadcastManager.getInstance(this).sendBroadcast(intentIsLoading);
					return super.onStartCommand(intent, flags, startId);
				}
			}
			Intent intentIsLoading = new Intent(url + Const.Action.IS_LOADING);
			intentIsLoading.putExtra(Const.Action.IS_LOADING, false);
			LocalBroadcastManager.getInstance(this).sendBroadcast(intentIsLoading);
			return super.onStartCommand(intent, flags, startId);
		}

		this.startDownLoad(url, intent.getBooleanExtra("startDownload", false));
		return super.onStartCommand(intent, flags, startId);
	}

	private void startDownLoad(String url, boolean forceDownLoad)
	{
		//check for url equality and don't start loading if true
		for (ParseArticle a : currentTasks)
		{
			if (a.getUrl().equals(url))
			{
				Log.e(LOG, url + " is already running");
				return;
			}
		}
		if (currentTasks.size() < 4)
		{
			//Everything is OK. Just add it and execute
			ParseArticle articleParser = new ParseArticle(ctx, url, getHelper(), forceDownLoad);
			articleParser.execute();
			currentTasks.add(articleParser);
			Log.e(LOG, "start download article: " + url);
		}
		else
		{

			//cancel 1-st and add given to the end
			ParseArticle removedParse = currentTasks.remove(0);
			//test fixing canceling already finished task
			if (removedParse.getStatus() == AsyncTask.Status.RUNNING)
			{
				Log.e(LOG, removedParse.getUrl() + " :" + AsyncTask.Status.RUNNING.toString());
				removedParse.cancel(true);
				sendErrorMsg(ctx, removedParse.getUrl(), Const.Error.CANCELLED_ERROR);
			}
			ParseArticle articleParser = new ParseArticle(ctx, url, getHelper(), forceDownLoad);
			articleParser.execute();
			currentTasks.add(articleParser);
			Log.e(LOG, "start download article: " + url);
		}
	}

	public static void sendDownloadedData(Context ctx, Article a, String url)
	{
		for (int i = 0; i < currentTasks.size(); i++)
		{
			ParseArticle parse = currentTasks.get(i);
			if (parse.getUrl().equals(url))
			{
				ParseArticle removedParse = currentTasks.remove(i);
				//test fixing canceling already finished task
				if (removedParse.getStatus() == AsyncTask.Status.RUNNING)
				{
					removedParse.cancel(true);
				}
				removedParse = null;
			}
		}
		Intent intent = new Intent(url);
		intent.putExtra(Article.KEY_CURENT_ART, a);
		LocalBroadcastManager.getInstance(ctx).sendBroadcastSync(intent);

		//Send intent with article to another activities to be able to update their data
		Intent intentGlobal = new Intent(Const.Action.ARTICLE_LOADED);
		intentGlobal.putExtra(Article.KEY_CURENT_ART, a);
		LocalBroadcastManager.getInstance(ctx).sendBroadcastSync(intentGlobal);
	}

	public static void sendErrorMsg(Context ctx, String url, String errorMsg)
	{
		for (int i = 0; i < currentTasks.size(); i++)
		{
			ParseArticle parse = currentTasks.get(i);
			if (parse.getUrl().equals(url))
			{
				ParseArticle removedParse = currentTasks.remove(i);
				//test fixing canceling already finished task
				if (removedParse.getStatus() == AsyncTask.Status.RUNNING)
				{
					removedParse.cancel(true);
				}
				removedParse = null;
			}
		}
		Intent intent = new Intent(url);
		intent.putExtra(Msg.ERROR, errorMsg);
		LocalBroadcastManager.getInstance(ctx).sendBroadcastSync(intent);
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
