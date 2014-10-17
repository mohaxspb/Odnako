package ru.kuchanov.odnako.onboot;

import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

public class CheckNewService extends Service
{
	NotificationManager notificationManager;
	Context context;

	final String LOG_TAG = "myLogs";

	@Override
	public void onCreate()
	{
		Log.d(LOG_TAG, "CheckNewService onCreate");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.d(LOG_TAG, "CheckNewService onStartCommand");
		context = this;

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		int notifPeriod = Integer.valueOf(pref.getString("notif_period", "30"));

		Timer myTimer = new Timer(); // Создаем таймер
		myTimer.schedule(new TimerTask()
		{ // Определяем задачу
		//			@Override
		//			public void run()
		//			{
		//				checkForNew();
		//			};

			Handler handler = new Handler(Looper.getMainLooper());

			@Override
			public void run()
			{
				handler.post(new Runnable()
				{
					@Override
					public void run()
					{
						checkForNew();
					}
				});
			}
		}, 60L * 1000, 60L * 1000 * notifPeriod);
		// интервал - 60000 миллисекунд (минута)*число минут, 30 000 миллисекунд до первого запуска.

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy()
	{
		Log.d(LOG_TAG, "CheckNewService onDestroy");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	void checkForNew()
	{
		Log.d(LOG_TAG, "CheckNewService checkForNew");
		try
		{
			ParceBlogsForNew parce = new ParceBlogsForNew(context, "www.odnako.org/blogs", 1);
			parce.execute();
		} catch (Exception e)
		{
			System.out.println("ERROR in CheckNewService!");
			e.printStackTrace();
		}
	}

}
