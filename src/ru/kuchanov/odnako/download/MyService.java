package ru.kuchanov.odnako.download;

import ru.kuchanov.odnako.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service
{

	final String LOG_TAG = "myLogs";
	NotificationManager notificationManager;

	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.d(LOG_TAG, "onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.d(LOG_TAG, "onStartCommand");
		if(intent!=null)
		{
			String catToLoad;
			catToLoad=intent.getStringExtra(Downloadings.EXTRA_CATEGORY);
			System.out.println(catToLoad);
			int numToLoad;
			numToLoad=intent.getIntExtra(Downloadings.EXTRA_NUM_TO_LOAD, 5);
			System.out.println(numToLoad);
			int numOfPagesToLoad;
			numOfPagesToLoad=intent.getIntExtra(Downloadings.EXTRA_NUM_OF_PAGES_TO_LOAD, 1);
			System.out.println(numOfPagesToLoad);
			startDownLoad(catToLoad, numToLoad, numOfPagesToLoad);
			notificate();
		}
		

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.d(LOG_TAG, "onDestroy");
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		Log.d(LOG_TAG, "onBind");
		return null;
	}

	void startDownLoad(String catToLoad, int numToLoad, int numOfPagesToLoad)
	{
		System.out.println("startDownLoad");
		Context context = getApplicationContext();
		ParseBlogsPageService parseBlogs=new ParseBlogsPageService(context, catToLoad, numToLoad, numOfPagesToLoad, 1);
		parseBlogs.execute();
	}

	@SuppressWarnings("deprecation")
	void notificate()
	{
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;
		CharSequence notiText = "Загрузка статей";
		long meow = System.currentTimeMillis();

		Notification notification = new Notification(icon, notiText, meow);
		Context context = getApplicationContext();
		CharSequence contentTitle = "Загрузка статей";
		CharSequence contentText = "Статьи загружаются...";
		Intent notificationIntent = new Intent(this, Downloadings.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		int SERVER_DATA_RECEIVED = 1;
		notificationManager.notify(SERVER_DATA_RECEIVED, notification);
	}
}