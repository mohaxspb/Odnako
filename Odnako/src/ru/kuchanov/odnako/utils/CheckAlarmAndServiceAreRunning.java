/*
 16.05.2014
CheckAlarmAndServiceAreRunning.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import java.util.Calendar;
import java.util.Date;

import ru.kuchanov.odnako.download.TimerReciver;
import ru.kuchanov.odnako.onboot.CheckNewService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class CheckAlarmAndServiceAreRunning
{
	public static void checkAndRun(Context ctx)
	{
		System.out.println("CheckAlarmAndServiceAreRunning started");
		//Start checkNewService if it isn't started yet
		SharedPreferences prefForNew = PreferenceManager.getDefaultSharedPreferences(ctx);
		boolean notifOn = prefForNew.getBoolean("notification", false);
		if (notifOn)
		{
			System.out.println("Notifications turned ON, so check if CheckNewService is running already:");
			CheckIfSeerviceIsRunning checkService = new CheckIfSeerviceIsRunning();
			String checkedServiceName = CheckNewService.class.getName();
			boolean serviceIsRunning = checkService.isMyServiceRunning(ctx, checkedServiceName);
			if (!serviceIsRunning)
			{
				System.out.println("CheckNewService isn't running, so start it...");
				ctx.startService(new Intent(ctx, CheckNewService.class));
			}
			else
			{
				System.out.println("CheckNewService is already running!");
			}
		}
		else
		{
			System.out.println("Notifications turned OFF, do not run CheckNewService!");
		}
		//end of Start checkNewService if it isn't started yet

		//Check if autoload alarm is seted
		Intent intent2check;
		intent2check = new Intent(ctx, TimerReciver.class);
		intent2check.setAction("action_2");
		intent2check.putExtra("extra", "extra_from_main");
		boolean alarmUp = (PendingIntent.getBroadcast(ctx, 0, intent2check, PendingIntent.FLAG_NO_CREATE) != null);

		if (alarmUp)
		{
			//Log.d("myTag", "Alarm is already active");
			System.out.println("Alarm is already active");
		}
		else
		{
			System.out.println("Alarm isn't already active;");
			boolean loadOnTimerOn = prefForNew.getBoolean("load_on_time", false);
			Intent intent2;
			intent2 = new Intent(ctx, TimerReciver.class);
			intent2.setAction("action_2");
			intent2.putExtra("extra", "extra_from_main");
			PendingIntent pIntent2;
			AlarmManager am;
			pIntent2 = PendingIntent.getBroadcast(ctx, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);//
			if (loadOnTimerOn)
			{
				System.out.println("And it turned on in settings, so creating new...");
				am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
				long timeToUpdate = prefForNew.getLong("timepref_auto_backup", 0);
				///Curent calendar to parce cur date
				Calendar cur_cal = Calendar.getInstance();
				cur_cal.setTimeInMillis(System.currentTimeMillis());
				//Calendar to user time
				Calendar userCal = Calendar.getInstance();
				Date d = new Date(timeToUpdate);
				userCal.setTime(d);
				//Calendar final
				Calendar cal = Calendar.getInstance();
				cal.set(cur_cal.get(Calendar.YEAR), cur_cal.get(Calendar.MONTH), cur_cal.get(Calendar.DATE), userCal.get(Calendar.HOUR_OF_DAY), userCal.get(Calendar.MINUTE));
				System.out.println("curent timeToFirstLoad: " + cal.getTime());
				//Check if first time has already gone
				if (cal.getTimeInMillis() < cur_cal.getTimeInMillis())
				{
					cal.add(Calendar.DATE, 1);
				}
				System.out.println("new timeToFirstLoad: " + cal.getTime());
				am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pIntent2);
			}
			else
			{
				System.out.println("And it turned off in settings, so do noting");
			}
		}
		///////////////////////
		System.out.println("CheckAlarmAndServiceAreRunning finished");
	}

}
