package ru.kuchanov.odnako.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class CheckIfServiceIsRunning
{
//	Context context;
//	String checkedServiceName;

	public static boolean check(Context context, String checkedServiceName)
	{
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
		{
			if (checkedServiceName.equals(service.service.getClassName()))
			{
				return true;
			}
		}
		return false;
	}
}
