package ru.kuchanov.odnako.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadReciver extends BroadcastReceiver
{
	final String LOG_TAG = BootBroadReciver.class.getSimpleName();

	Context context;

	public void onReceive(Context context, Intent intent)
	{
		Log.e(LOG_TAG, "onReceive " + intent.getAction());
		
//		String action = intent.getAction();
//		Log.d(LOG_TAG, "onReceive " + intent.getAction());
//		Toast.makeText(context, intent.getAction(), Toast.LENGTH_LONG).show();

		this.context = context;
	}
}