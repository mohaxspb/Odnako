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
import android.view.MenuItem;

public class ActivityPreference extends PreferenceActivity implements
SharedPreferences.OnSharedPreferenceChangeListener
{

	private SharedPreferences pref;

	protected Method mLoadHeaders = null;
	protected Method mHasHeaders = null;

	int themeIconId;
	int vibrationIconId;
	int systemSettingsIconId;
	int aboutIconId;

	private List<Header> headersList;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ActivityPreference onCreate");

		//get default settings to get all settings later
		PreferenceManager.setDefaultValues(this, R.xml.pref_design, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_notifications, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_system, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_about, true);
		this.pref = PreferenceManager.getDefaultSharedPreferences(this);
		//end of get default settings to get all settings later

		//set theme before super and set content to apply it
		//		if (pref.getString("theme", "dark").equals("dark"))
		if (this.pref.getBoolean("night_mode", false))
		{
			this.setTheme(R.style.ThemeDarkPreference);
		}
		else
		{
			this.setTheme(R.style.ThemeLightPreference);
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

		///set title and icon to actionbar
		this.getActionBar().setTitle(R.string.settings);
		//set themeDependedIconsIDs
		int[] attrs = new int[] { R.attr.settingsIcon };
		TypedArray ta = this.obtainStyledAttributes(attrs);
		int settingsIconId = ta.getResourceId(0, R.drawable.ic_color_lens_grey600_48dp);
		ta.recycle();
		this.getActionBar().setIcon(settingsIconId);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);

		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				//called when the up affordance/carat in actionbar is pressed
				onBackPressed();
				return true;
			default:
				return false;
		}
	}

	private void setHeadersIcons()
	{

		for (Header header : headersList)
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
	}

	private void getThemeDependedIconsIDs()
	{
		//set themeDependedIconsIDs
		int[] attrs = new int[] { R.attr.themeIcon };
		TypedArray ta = this.obtainStyledAttributes(attrs);
		themeIconId = ta.getResourceId(0, R.drawable.ic_color_lens_grey600_48dp);
		ta.recycle();

		attrs = new int[] { R.attr.vibrationIcon };
		ta = this.obtainStyledAttributes(attrs);
		vibrationIconId = ta.getResourceId(0, R.drawable.ic_color_lens_grey600_48dp);
		ta.recycle();

		attrs = new int[] { R.attr.systemSettingsIcon };
		ta = this.obtainStyledAttributes(attrs);
		systemSettingsIconId = ta.getResourceId(0, R.drawable.ic_color_lens_grey600_48dp);
		ta.recycle();

		attrs = new int[] { R.attr.aboutIcon };
		ta = this.obtainStyledAttributes(attrs);
		aboutIconId = ta.getResourceId(0, R.drawable.ic_color_lens_grey600_48dp);
		ta.recycle();
	}

	@Override
	public void onBuildHeaders(List<Header> aTarget)
	{
		try
		{
			mLoadHeaders.invoke(this, new Object[] { R.xml.pref_headers, aTarget });

		} catch (IllegalArgumentException e)
		{
		} catch (IllegalAccessException e)
		{
		} catch (InvocationTargetException e)
		{
		} finally
		{
			this.headersList = aTarget;
			this.getThemeDependedIconsIDs();
			this.setHeadersIcons();
		}
	}

	@Override
	protected boolean isValidFragment(String fragmentName)
	{
		return FragmentPreference.class.getName().equals(fragmentName)
		|| FragmentPreferenceAbout.class.getName().equals(fragmentName);
	}

	/**
	 * As I remember it's for applying theme (backgroundColor) for dialogs
	 */
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

	//to ALWAYS twoPane mode
	@Override
	public boolean onIsMultiPane()
	{
		if (this.getResources().getBoolean(R.bool.isTablet))
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	//here we will:
	//change theme by restarting activity
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		System.out.println("key: " + key);
		if (key.equals("theme"))
		{
			System.out.println("key.equals('theme'): " + String.valueOf(key.equals("theme")));
			this.recreate();
		}
		///Запускаем\ отключаем сервис
		//
		//		if (key.equals("notification"))
		//		{
		//			boolean notifOn = sharedPreferences.getBoolean(key, false);
		//
		//			if (notifOn)
		//			{
		//				Intent serviceIntent = new Intent(this, ru.kuchanov.odnako.onboot.CheckNewService.class);
		//				startService(serviceIntent);
		//			}
		//			else
		//			{
		//				Intent serviceIntent = new Intent(this, ru.kuchanov.odnako.onboot.CheckNewService.class);
		//				stopService(serviceIntent);
		//			}
		//		}
	}
}
