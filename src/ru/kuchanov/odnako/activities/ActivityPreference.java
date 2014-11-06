/*
 29.10.2014
ActivityPreference.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.fragments.FragmentPreference;
import ru.kuchanov.odnako.fragments.FragmentPreferenceAbout;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class ActivityPreference extends PreferenceActivity
{

	private SharedPreferences pref;

	protected Method mLoadHeaders = null;
	protected Method mHasHeaders = null;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ActivityPreference onCreate");

		//get default settings to get all settings later
		PreferenceManager.setDefaultValues(this, R.xml.pref, true);
		this.pref = PreferenceManager.getDefaultSharedPreferences(this);
		//end of get default settings to get all settings later

		//set theme before super and set content to apply it
		if (pref.getString("theme", "dark").equals("dark"))
		{
			this.setTheme(R.style.ThemeDark);
		}
		else
		{
			this.setTheme(R.style.ThemeLight);
		}

		//onBuildHeaders() will be called during super.onCreate()
		try
		{
			mLoadHeaders = getClass().getMethod("loadHeadersFromResource", int.class, List.class);
			mHasHeaders = getClass().getMethod("hasHeaders");
		} catch (NoSuchMethodException e)
		{
		}

		//call super after setTheme to set it 0_0
		super.onCreate(savedInstanceState);

		if (!isNewV11Prefs())
		{
			addPreferencesFromResource(R.xml.pref);
		}
	}

	/////////////

	/**
	 * Checks to see if using new v11+ way of handling PrefsFragments.
	 * 
	 * @return Returns false pre-v11, else checks to see if using headers.
	 */
	public boolean isNewV11Prefs()
	{
		if (mHasHeaders != null && mLoadHeaders != null)
		{
			try
			{
				return (Boolean) mHasHeaders.invoke(this);
			} catch (IllegalArgumentException e)
			{
			} catch (IllegalAccessException e)
			{
			} catch (InvocationTargetException e)
			{
			}
		}
		return false;
	}

	@Override
	public void onBuildHeaders(List<Header> aTarget)
	{
		try
		{
			mLoadHeaders.invoke(this, new Object[] { R.xml.pref_headers, aTarget });

			//set arrowDownIcon by theme
			int[] attrs = new int[] { R.attr.themeIcon };
			TypedArray ta = this.obtainStyledAttributes(attrs);
			int themeIconId = ta.getResourceId(0, R.drawable.ic_color_lens_grey600_48dp);
			ta.recycle();

			attrs = new int[] { R.attr.vibrationIcon };
			ta = this.obtainStyledAttributes(attrs);
			int vibrationIconId = ta.getResourceId(0, R.drawable.ic_color_lens_grey600_48dp);
			ta.recycle();

			attrs = new int[] { R.attr.systemSettingsIcon };
			ta = this.obtainStyledAttributes(attrs);
			int systemSettingsIconId = ta.getResourceId(0, R.drawable.ic_color_lens_grey600_48dp);
			ta.recycle();

			attrs = new int[] { R.attr.aboutIcon };
			ta = this.obtainStyledAttributes(attrs);
			int aboutIconId = ta.getResourceId(0, R.drawable.ic_color_lens_grey600_48dp);
			ta.recycle();

			for (Header header : aTarget)
			{
				switch (header.titleRes)
				{
					case (R.string.design):
						header.iconRes = themeIconId;
					break;
					case (R.string.notifications):
						header.iconRes = vibrationIconId;
					break;
					case (R.string.system_settings):
						header.iconRes = systemSettingsIconId;
					break;
					case (R.string.about):
						header.iconRes = aboutIconId;
					break;
				}
			}

		} catch (IllegalArgumentException e)
		{
		} catch (IllegalAccessException e)
		{
		} catch (InvocationTargetException e)
		{
		}
	}

	@Override
	protected boolean isValidFragment(String fragmentName)
	{
		return FragmentPreference.class.getName().equals(fragmentName)
		|| FragmentPreferenceAbout.class.getName().equals(fragmentName);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
	{
		super.onPreferenceTreeClick(preferenceScreen, preference);
		if (preference != null)
			if (preference instanceof PreferenceScreen)
				if (((PreferenceScreen) preference).getDialog() != null)
					((PreferenceScreen) preference)
					.getDialog()
					.getWindow()
					.getDecorView()
					.setBackgroundDrawable(
					this.getWindow().getDecorView().getBackground().getConstantState().newDrawable());
		return false;
	}
}
