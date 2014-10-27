package ru.kuchanov.odnako.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadReciver extends BroadcastReceiver
{
	final String LOG_TAG = "myLogs";

	Context context;

	public void onReceive(Context context, Intent intent)
	{
		Log.d(LOG_TAG, "onReceive " + intent.getAction());

		this.context = context;
	}
}