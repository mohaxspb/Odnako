/*
 07.12.2014
ServiceComments.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.util.ArrayList;

import ru.kuchanov.odnako.callbacks.CallbackComments;
import ru.kuchanov.odnako.download.CommentInfo;
import ru.kuchanov.odnako.download.ParseComments;
import ru.kuchanov.odnako.fragments.FragmentComments;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class ServiceComments extends Service implements CallbackComments
{
	final private static String LOG = ServiceComments.class.getSimpleName() + "/";

	Context ctx;

	public void onCreate()
	{
		Log.d(LOG, "onCreate");
		super.onCreate();

		this.ctx = this;
	}

	public int onStartCommand(Intent intent, int flags, int startId)
	{
		//Log.d(LOG, "onStartCommand");
		if (intent == null)
		{
			Log.e(LOG, "intent=null!!! WTF?!");
			return super.onStartCommand(intent, flags, startId);
		}
		//firstly: if we load from top or not? Get it by pageToLoad
		int pageToLoad = intent.getIntExtra(FragmentComments.KEY_PAGE_TO_LOAD, 1);
		String articlesUrl = intent.getStringExtra(FragmentComments.KEY_URL_TO_LOAD);

		this.startDownLoad(articlesUrl, pageToLoad);
		return super.onStartCommand(intent, flags, startId);
	}

	private void startDownLoad(String articlesUrl, final int pageToLoad)
	{
		//Log.d(LOG, "startDownLoad " + catToLoad + "/page-" + pageToLoad);
		ParseComments parse = new ParseComments(articlesUrl, pageToLoad, this);
		parse.execute();
	}

	@Override
	public void onDoneLoadingComments(String resultMessage, ArrayList<CommentInfo> dataFromWeb, String articlesUrl,
	int pageToLoad)
	{
		Log.d(LOG, "onDoneLoadingComments " + articlesUrl + "/page-" + pageToLoad);

		Intent intent = new Intent(articlesUrl + FragmentComments.LOG);
		intent.putExtra(Msg.MSG, resultMessage);
		intent.putExtra(FragmentComments.KEY_PAGE_TO_LOAD, pageToLoad);
		intent.putExtra(FragmentComments.KEY_COMMENTS_DATA, dataFromWeb);
		LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.d(LOG, "onDestroy");
	}

	public IBinder onBind(Intent intent)
	{
		Log.d(LOG, "MyService onBind");
		return null;
	}
}