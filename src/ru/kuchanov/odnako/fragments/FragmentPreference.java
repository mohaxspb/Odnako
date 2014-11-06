package ru.kuchanov.odnako.fragments;

import ru.kuchanov.odnako.activities.ActivityPreference;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

@SuppressLint("NewApi")
public class FragmentPreference extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
{

	@Override
	public void onCreate(Bundle aSavedState)
	{
		super.onCreate(aSavedState);
		Context ctx = getActivity().getApplicationContext();
		int thePrefRes = ctx.getResources().getIdentifier(getArguments().getString("pref-resource"),
		"xml", ctx.getPackageName());
		addPreferencesFromResource(thePrefRes);
	}
	
	//here we will:
		//change theme by restarting activity
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
		{
			
			System.out.println("key: "+key);
			if (key.equals("theme"))
			{
				System.out.println("key.equals('theme'): "+String.valueOf(key.equals("theme")));
				((ActivityPreference)this.getActivity()).myRecreate();
			}
		}
}