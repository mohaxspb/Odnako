package ru.kuchanov.odnako.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class TimerReciver extends BroadcastReceiver
{

	final String LOG_TAG = "myLogs";
	Context context;
	SharedPreferences pref;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		System.out.println("TimerReciver called!");
		this.context = context;

		pref = PreferenceManager.getDefaultSharedPreferences(context);
		download();

		Log.d(LOG_TAG, "onReceive");
		Log.d(LOG_TAG, "action = " + intent.getAction());
		Log.d(LOG_TAG, "extra = " + intent.getStringExtra("extra"));
	}

	protected void download()
	{
		System.out.println("Downloadings download");
		Intent serviceIntent = new Intent(context, ru.kuchanov.odnako.download.MyService.class);

		String CAT_TO_LOAD=pref.getString(Downloadings.EXTRA_CATEGORY, "");
		Integer ARTICLES_TO_LOAD=pref.getInt(Downloadings.EXTRA_NUM_TO_LOAD, 5);
		Integer ARTICLES_PAGES_TO_LOAD=pref.getInt(Downloadings.EXTRA_NUM_OF_PAGES_TO_LOAD, 1);

		serviceIntent.putExtra(Downloadings.EXTRA_CATEGORY, CAT_TO_LOAD);
		serviceIntent.putExtra(Downloadings.EXTRA_NUM_TO_LOAD, ARTICLES_TO_LOAD);
		serviceIntent.putExtra(Downloadings.EXTRA_NUM_OF_PAGES_TO_LOAD, ARTICLES_PAGES_TO_LOAD);
		context.startService(serviceIntent);
	}
}