package ru.kuchanov.odnako.download;

import java.util.ArrayList;

import ru.kuchanov.odnako.fragments.callbacks.AllArtsInfoCallback;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class GetInfoService extends Service implements AllArtsInfoCallback
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
		if (intent != null)
		{
			String catToLoad;
			catToLoad = intent.getStringExtra("categoryToLoad");
			System.out.println(catToLoad);
			int pageToLoad = intent.getIntExtra("pageToLoad", 1);
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
		System.out.println("startDownLoad "+catToLoad +"page-"+pageToLoad);
		Context context = getApplicationContext();
		ParsePageForAllArtsInfo parse = new ParsePageForAllArtsInfo(catToLoad, pageToLoad, context, this);
		parse.execute();
	}

	// Send an Intent with an action named "custom-event-name". The Intent sent should 
	// be received by the ReceiverActivity.
	private void sendMessage(ArrayList<ArtInfo> someResult, String categoryToLoad)
	{
//		Log.d("sender", "Broadcasting message");
		Intent intent = new Intent(categoryToLoad);
		Bundle b=new Bundle();
		ArtInfo.writeAllArtsInfoToBundle(b, someResult, someResult.get(0));
		intent.putExtras(b);
		
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	@Override
	public void doSomething(ArrayList<ArtInfo> someResult, String categoryToLoad)
	{
		this.sendMessage(someResult, categoryToLoad);
	}
}