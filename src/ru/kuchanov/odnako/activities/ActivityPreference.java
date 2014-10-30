/*
 29.10.2014
ActivityPreference.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import ru.kuchanov.odnako.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ActivityPreference extends PreferenceActivity
{
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.
		addPreferencesFromResource(R.xml.pref);
		//        addPreferencesFromResource(R.xml.other);
	}
}
