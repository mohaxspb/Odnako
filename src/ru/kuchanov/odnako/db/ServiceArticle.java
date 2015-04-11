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
import ru.kuchanov.odnako.callbacks.CallbackDownloadArticle;
import ru.kuchanov.odnako.download.ParseArticle;
import ru.kuchanov.odnako.fragments.FragmentArticle;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class ServiceArticle extends Service implements CallbackDownloadArticle
{
	final private static String LOG = ServiceArticle.class.getSimpleName() + "/";

	final static int NOTIFICATION_ARTICLES_DOWNLOAD_ID = 42;

	private DataBaseHelper dataBaseHelper;

	private List<ParseArticle> currentTasks = new ArrayList<ParseArticle>();

	private Context ctx;

	private int numOfErrors = 0;

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

		if (intent == null)
		{
			Log.e(LOG, "intent=null!");
			return super.onStartCommand(intent, flags, startId);
		}

		String action = intent.getAction();
		if (action.equals(Const.Action.DATA_REQUEST_MULTIPLE))
		{
			ArrayList<String> urls = intent.getStringArrayListExtra(FragmentArticle.ARTICLE_URL);
			int quontity = urls.size();
			this.updateNotification(0, quontity);
			for (int i = 0; i < quontity; i++)
			{
				String url = urls.get(i);
				this.startDownLoad(url, intent.getBooleanExtra("startDownload", false), true, i, quontity);
			}
			return super.onStartCommand(intent, flags, startId);
		}
		else
		{
			String url = intent.getStringExtra(FragmentArticle.ARTICLE_URL);
			this.startDownLoad(url, intent.getBooleanExtra("startDownload", false), false, 0, 0);
			return super.onStartCommand(intent, flags, startId);
		}
	}

	private void startDownLoad(String url, boolean forceDownLoad, boolean isMultipleTask, int iterator, int quontity)
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
		if (isMultipleTask)
		{
			ParseArticle articleParser = new ParseArticle(url, getHelper(), forceDownLoad, isMultipleTask, this,
			iterator, quontity);
			articleParser.execute();
		}
		else
		{
			if (currentTasks.size() < 4)
			{
				//Everything is OK. Just add it and execute
				ParseArticle articleParser = new ParseArticle(url, getHelper(), forceDownLoad, isMultipleTask, this,
				iterator, quontity);
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
					this.onErrorWhileDownloadingArticle(Const.Error.CANCELLED_ERROR, removedParse.getUrl(),
					isMultipleTask, iterator, quontity);
				}
				ParseArticle articleParser = new ParseArticle(url, getHelper(), forceDownLoad, isMultipleTask, this,
				iterator, quontity);
				articleParser.execute();
				currentTasks.add(articleParser);
				Log.e(LOG, "start download article: " + url);
			}
		}
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

	@Override
	public void onDoneDownloadingArticle(Article downloadedArticle, boolean isMultipleTask, int iterator, int quontity)
	{
		if (!isMultipleTask)
		{
			for (int i = 0; i < currentTasks.size(); i++)
			{
				ParseArticle parse = currentTasks.get(i);
				if (parse.getUrl().equals(downloadedArticle.getUrl()))
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
		}
		else
		{
			this.updateNotification(iterator, quontity);
		}
		Intent intent = new Intent(downloadedArticle.getUrl());
		intent.putExtra(Article.KEY_CURENT_ART, downloadedArticle);
		LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);

		//Send intent with article to another activities to be able to update their data
		Intent intentGlobal = new Intent(Const.Action.ARTICLE_CHANGED);
		intentGlobal.putExtra(Article.KEY_CURENT_ART, downloadedArticle);
		intentGlobal.putExtra(Const.Action.ARTICLE_CHANGED, Const.Action.ARTICLE_LOADED);
		LocalBroadcastManager.getInstance(ctx).sendBroadcast(intentGlobal);
	}

	private void updateNotification(int iterator, int quontity)
	{
		//update notification
		NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		//check if it was last task
		if (iterator == quontity - 1)
		{
			if (this.numOfErrors == 0)
			{
				mBuilder.setContentTitle("Загрузка завершена!")
				.setContentText("Все статьи загружены")
				.setSmallIcon(R.drawable.ic_file_download_white_48dp);
				mBuilder.setProgress(0, 0, false);
			}
			else
			{
				mBuilder.setContentTitle("Загрузка завершена!")
				.setContentText("Не удалось загрузить " + this.numOfErrors + " статей")
				.setSmallIcon(R.drawable.ic_file_download_white_48dp);
				mBuilder.setProgress(0, 0, false);
				this.numOfErrors = 0;
			}
		}
		else
		{
			mBuilder.setContentTitle("Загрузка статей")
			.setContentText("Загружено: " + String.valueOf(iterator + 1) + "/" + quontity)
			.setSmallIcon(R.drawable.ic_file_download_white_48dp);
			mBuilder.setProgress(quontity, iterator, false);
		}
		// Displays the progress bar for the first time.
		mNotifyManager.notify(NOTIFICATION_ARTICLES_DOWNLOAD_ID, mBuilder.build());
	}

	@Override
	public void onErrorWhileDownloadingArticle(String error, String url, boolean isMultipleTask, int iterator,
	int quontity)
	{
		if (!isMultipleTask)
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
		}
		else
		{
			this.numOfErrors++;
			this.updateNotification(iterator, quontity);
		}
		Intent intent = new Intent(url);
		intent.putExtra(Msg.ERROR, error);
		LocalBroadcastManager.getInstance(ctx).sendBroadcastSync(intent);
	}
}
