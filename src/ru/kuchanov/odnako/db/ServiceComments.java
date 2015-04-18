/*
 07.12.2014
ServiceComments.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.util.ArrayList;

import ru.kuchanov.odnako.R;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class ServiceComments extends Service
{
	final private static String LOG = ServiceComments.class.getSimpleName() + "/";

	Context ctx;

	SharedPreferences pref;

	public void onCreate()
	{
		Log.d(LOG, "onCreate");
		super.onCreate();

		this.ctx = this;

		//get default settings to get all settings later
		PreferenceManager.setDefaultValues(this, R.xml.pref_design, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_notifications, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_system, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_about, true);
		this.pref = PreferenceManager.getDefaultSharedPreferences(ctx);
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
		int pageToLoad = intent.getIntExtra("pageToLoad", 1);

		String categoryToLoad = intent.getStringExtra("categoryToLoad");

		this.startDownLoad(categoryToLoad, pageToLoad);
		return super.onStartCommand(intent, flags, startId);
	}

	private void startDownLoad(final String catToLoad, final int pageToLoad)
	{
		//Log.d(LOG, "startDownLoad " + catToLoad + "/page-" + pageToLoad);
	}

	//	@Override
	//	public void sendDownloadedData(ArrayList<Article> dataToSend, String categoryToLoad, int pageToLoad)
	//	{
	//		Log.d(LOG + categoryToLoad, "sendDownloadedData");
	//
	//		if (dataToSend.size() == 0)
	//		{
	//			Article a = new Article();
	//			a.setTitle("Ни одной статьи не обнаружено.");
	//			dataToSend.add(a);
	//			String[] resultMessage = new String[] { Msg.DB_ANSWER_NO_ARTS_IN_CATEGORY, null };
	//			sendBroadcastWithResult(this, resultMessage, dataToSend, categoryToLoad, pageToLoad);
	//		}
	//		else
	//		{
	//
	//		}
	//	}

	//	@Override
	//	public void onError(String e, String categoryToLoad, int pageToLoad)
	//	{
	//		String[] resultMessage = new String[] { Msg.ERROR, e };
	//		sendBroadcastWithResult(this, resultMessage, null, categoryToLoad, pageToLoad);
	//	}//onError

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.d(LOG, "onDestroy");
	}

	/**
	 * Sends intent with message and data to fragment
	 * 
	 * Also updates hashMap with all articles
	 * 
	 * @param ctx
	 * @param resultMessage
	 * @param dataToSend
	 * @param categoryToLoad
	 * @param pageToLoad
	 */
	public void sendBroadcastWithResult(Context ctx, String[] resultMessage, ArrayList<Article> dataToSend,
	String categoryToLoad, int pageToLoad)
	{
		//Log.d(LOG + categoryToLoad, "sendBroadcastWithResult");

		Intent intent = new Intent(categoryToLoad);
		intent.putExtra(Msg.MSG, resultMessage);
		intent.putExtra("pageToLoad", pageToLoad);
		intent.putExtra(Article.KEY_ALL_ART_INFO, dataToSend);
		LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
	}

	public IBinder onBind(Intent intent)
	{
		Log.d(LOG, "MyService onBind");
		return null;
	}
}