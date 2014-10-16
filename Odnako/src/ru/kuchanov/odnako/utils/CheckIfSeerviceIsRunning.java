package ru.kuchanov.odnako.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class CheckIfSeerviceIsRunning
{
	Context context;
	String checkedServiceName;

	public boolean isMyServiceRunning(Context context, String checkedServiceName)
	{
		this.checkedServiceName = checkedServiceName;
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
