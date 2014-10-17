package ru.kuchanov.odnako;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

public class LockOrientation
{
	Activity act;

	public LockOrientation(Activity act)
	{
		this.act=act;
		
	}
	@SuppressLint("InlinedApi")
	public void lock()
	{
		
		switch (act.getResources().getConfiguration().orientation)
		{
			case Configuration.ORIENTATION_PORTRAIT:
				if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO)
				{
					act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}
				else
				{
					int rotation = act.getWindowManager().getDefaultDisplay().getRotation();
					if (rotation == android.view.Surface.ROTATION_90 || rotation == android.view.Surface.ROTATION_180)
					{
						act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
					}
					else
					{
						act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					}
				}
			break;

			case Configuration.ORIENTATION_LANDSCAPE:
				if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO)
				{
					act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				}
				else
				{
					int rotation = act.getWindowManager().getDefaultDisplay().getRotation();
					if (rotation == android.view.Surface.ROTATION_0 || rotation == android.view.Surface.ROTATION_90)
					{
						act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					}
					else
					{
						act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
					}
				}
			break;
		}
	}
}
