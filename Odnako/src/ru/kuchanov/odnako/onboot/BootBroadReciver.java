package ru.kuchanov.odnako.onboot;

import java.util.Calendar;
import java.util.Date;

import ru.kuchanov.odnako.download.TimerReciver;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootBroadReciver extends BroadcastReceiver
{
	final String LOG_TAG = "myLogs";

	Context context;

	public void onReceive(Context context, Intent intent)
	{
		Log.d(LOG_TAG, "onReceive " + intent.getAction());

		this.context = context;

		//launch checkForNew Service
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		boolean notifOn = pref.getBoolean("notification", false);

		if (notifOn)
		{
			context.startService(new Intent(context, CheckNewService.class));
		}
		//launch loadOnTimer Service
		//		boolean loadOnTimerPref = pref.getBoolean("load_on_time", false);
		//		Intent intent2;
		//		PendingIntent pIntent2;
		//		AlarmManager am;
		//		intent2 = createIntent("action_2", "extra 2");
		//		pIntent2 = PendingIntent.getBroadcast(context, 0, intent2, 0);
		//		if (loadOnTimerPref)
		//		{
		//			am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		//			am.setInexactRepeating(AlarmManager.RTC_WAKEUP, 3000, AlarmManager.INTERVAL_DAY, pIntent2);
		//		}

		
		//launch loadOnTimer Service with alarmManager
		boolean loadOnTimerOn = pref.getBoolean("load_on_time", false);
		Intent intent2;
		intent2 = new Intent(context, TimerReciver.class);
		intent2.setAction("action_2");
		intent2.putExtra("extra", "extra_from_main");
		PendingIntent pIntent2;
		AlarmManager am;
		pIntent2 = PendingIntent.getBroadcast(context, 0, intent2, 0);

		if (loadOnTimerOn)
		{
			am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			long timeToUpdate = pref.getLong("timepref_auto_backup", 0);
			///Curent calendar to parce cur date
			Calendar cur_cal = Calendar.getInstance();
			cur_cal.setTimeInMillis(System.currentTimeMillis());
			//end
			//Calendar to user time
			Calendar userCal = Calendar.getInstance();
			Date d = new Date(timeToUpdate);
			userCal.setTime(d);
			//end
			//Calendar final
			Calendar cal = Calendar.getInstance();
			cal.set(cur_cal.get(Calendar.YEAR), cur_cal.get(Calendar.MONTH), cur_cal.get(Calendar.DATE), userCal.get(Calendar.HOUR_OF_DAY), userCal.get(Calendar.MINUTE));
			System.out.println("curent timeToFirstLoad: "+cal.getTime());
			//end
			//Check if first time has already gone
			if(cal.getTimeInMillis()<cur_cal.getTimeInMillis())
			{
				cal.add(Calendar.DATE, 1);
			}
			System.out.println("new timeToFirstLoad: "+cal.getTime());
			am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pIntent2);
			pref.edit().putBoolean("alarm_seted", true).commit();
		}
		///////////////////////
	}

	Intent createIntent(String action, String extra)
	{
		//Intent intent = new Intent(context, CheckNewService.class);TimerReciver

		Intent intent = new Intent(context, TimerReciver.class);
		intent.setAction(action);
		intent.putExtra("extra", extra);
		return intent;
	}
}