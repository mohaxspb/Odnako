package ru.kuchanov.odnako.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;

@SuppressLint("NewApi")
public class FragmentPreference extends PreferenceFragment
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
}