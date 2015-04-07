/*
 07.04.2015
TimerReceiver.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.receivers;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.db.ServiceDB;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiverTimer extends BroadcastReceiver
{
	private static final String LOG = ReceiverTimer.class.getSimpleName();
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.d(LOG, "onReceive " + intent.getAction());

		this.context = context;
		download();
	}

	protected void download()
	{
		Intent serviceIntent = new Intent(context, ServiceDB.class);
		serviceIntent.setAction(Const.Action.DATA_REQUEST);
		serviceIntent.putExtra("pageToLoad", 1);
		serviceIntent.putExtra("categoryToLoad", "http://odnako.org/blogs");
		serviceIntent.putExtra("startDownload", true);
		serviceIntent.putExtra("notify", true);
		context.startService(serviceIntent);
	}
}