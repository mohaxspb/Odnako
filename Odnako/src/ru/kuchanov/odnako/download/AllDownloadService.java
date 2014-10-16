package ru.kuchanov.odnako.download;

import ru.kuchanov.odnako.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class AllDownloadService extends Service
{
	NotificationManager notificationManager;
	Context ctx;
	Intent intent;

	public void onCreate()
	{
		super.onCreate();
		System.out.println("AllDownloadService onCreate");
		this.ctx = this;
	}

	public int onStartCommand(Intent intent, int flags, int startId)
	{
		System.out.println("AllDownloadService onStartCommand");
		if (intent != null)
		{
			this.intent=intent;
			
			int icon = R.drawable.ic_launcher;
	        Intent notificationIntent = new Intent(ctx, Downloadings.class);
	        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);        
	        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);

	        Notification notification = new NotificationCompat.Builder(ctx)
	        .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), icon))
	        .setContentText("Начинаю загрузку")
	        .setContentTitle("Загрузка статей")
	        .setAutoCancel(true)
	        .setContentIntent(contentIntent).build();
	        
			this.startForeground(10, notification);
			
			String catToLoad[] = intent.getStringArrayExtra(Downloadings.ALL_CATEGORY);
			int numToLoad = intent.getIntExtra(Downloadings.ALL_NUM_TO_LOAD, 30);
			int numOfPagesToLoad = intent.getIntExtra(Downloadings.ALL_NUM_OF_PAGES_TO_LOAD, 1);
			startDownLoad(catToLoad, numToLoad, numOfPagesToLoad);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public void onDestroy()
	{
		super.onDestroy();
		System.out.println("AllDownloadService onDestroy");
	}

	public IBinder onBind(Intent intent)
	{
		System.out.println("AllDownloadService onBind");
		return null;
	}

	@Override
	public boolean onUnbind(Intent intent)
	{
		// All clients have unbound with unbindService()  
		System.out.println("AllDownloadService onUnbind");
		return true;
	}

	@Override
	public void onRebind(Intent intent)
	{
		// A client is binding to the service with Re-bindService(),  
		System.out.println("AllDownloadService onRebind");

	}

	void startDownLoad(String[] catToLoad, int numToLoad, int numOfPagesToLoad)
	{
		System.out.println("startDownLoad");
		DownAllCategories downAllAsynk = new DownAllCategories(ctx, catToLoad, numToLoad, numOfPagesToLoad, this.intent);
		downAllAsynk.execute();
	}
}