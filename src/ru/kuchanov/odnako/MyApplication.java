/*
 06.04.2015
MyApplication.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako;

import org.acra.*;
import org.acra.annotation.*;

import android.app.Application;

/**
 * here we initialize ACRA crah reporter
 * @see https://github.com/ACRA/acra
 */
@ReportsCrashes(
formKey = "", // This is required for backward compatibility but not used
mailTo = "mohax.spb@gmail.com",
mode = ReportingInteractionMode.TOAST,
resToastText = R.string.hello_world)
public class MyApplication extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();

		// The following line triggers the initialization of ACRA
		ACRA.init(this);
	}
}