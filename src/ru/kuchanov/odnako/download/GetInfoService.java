package ru.kuchanov.odnako.download;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class GetInfoService extends Service
{

	final String LOG_TAG = "odnakoLogs";

	public void onCreate()
	{
		super.onCreate();
		Log.d(LOG_TAG, "onCreate");
	}

	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.d(LOG_TAG, "onStartCommand");
		if(intent!=null)
		{
			String catToLoad;
			catToLoad=intent.getStringExtra("categoryToLoad");
			System.out.println(catToLoad);
			int pageToLoad=intent.getIntExtra("pageToLoad", 1);
			startDownLoad(catToLoad, pageToLoad);
		}
		return super.onStartCommand(intent, flags, startId);
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

	void startDownLoad(String catToLoad, int pageToLoad)
	{
		System.out.println("startDownLoad");
//		Context context = getApplicationContext();
//		ParsePageForAllArtsInfo parse=new ParsePageForAllArtsInfo(catToLoad, pageToLoad, context);
//		parse.execute();
	}
}