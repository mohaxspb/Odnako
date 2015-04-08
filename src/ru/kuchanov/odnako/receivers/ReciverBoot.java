package ru.kuchanov.odnako.receivers;

import ru.kuchanov.odnako.activities.ActivityPreference;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ReciverBoot extends BroadcastReceiver
{
	final String LOG = ReciverBoot.class.getSimpleName();

	Context context;
	SharedPreferences pref;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.d(LOG, "onReceive " + intent.getAction());

		this.context = context;
		pref = PreferenceManager.getDefaultSharedPreferences(context);
		boolean notifOn = pref.getBoolean("notification", false);

		if (notifOn)
		{
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intentToTimerReceiver = new Intent(context.getApplicationContext(), ReceiverTimer.class);
			intent.setAction("ru.kuchanov.odnako.RECEIVER_TIMER");
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentToTimerReceiver,
			PendingIntent.FLAG_UPDATE_CURRENT);

			long checkPeriod = Long.valueOf(this.pref.getString(ActivityPreference.PREF_KEY_NOTIF_PERIOD, "60")) * 60L * 1000L;
			//test less interval in 1 min
			checkPeriod = 60 * 1000;

			am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), checkPeriod, pendingIntent);
		}
	}
}