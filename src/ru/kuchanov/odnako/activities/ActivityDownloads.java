/*
 19.10.2014
ActivityMain.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.utils.CheckTimeToAds;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.InterstitialAd;

public class ActivityDownloads extends ActivityBase
{
	private static final String LOG = ActivityDownloads.class.getSimpleName();

	InterstitialAd mInterstitialAd;

	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ActivityDownloads onCreate");
		this.act = this;
		
		//get default settings to get all settings later
		PreferenceManager.setDefaultValues(this, R.xml.pref_design, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_notifications, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_system, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_about, true);
		this.pref = PreferenceManager.getDefaultSharedPreferences(this);
		//end of get default settings to get all settings later

		//ADS
		this.checkTimeAds = new CheckTimeToAds(this, this.mInterstitialAd);

		//set theme before super and set content to apply it
		if (pref.getBoolean(ActivityPreference.PREF_KEY_NIGHT_MODE, false) == true)
		{
			this.setTheme(R.style.ThemeDark);
		}
		else
		{
			this.setTheme(R.style.ThemeLight);
		}

		//call super after setTheme to set it 0_0
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_downloads);

		//drawer settings
		this.setNavDrawer();
		////End of drawer settings

		//adMob
		this.AddAds();
		//end of adMob
	}

	@Override
	protected void onResume()
	{
		Log.e(LOG, "onResume");

		this.checkTimeAds.onResume();

		super.onResume();
	}

	@Override
	public void onPause()
	{
		Log.e(LOG, "onPause");

		this.checkTimeAds.onPause();

		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		System.out.println("ActivityDownloads: onSaveInstanceState");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		System.out.println("ActivityDownloads onRestoreInstanceState");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		/* The action bar home/up action should open or close the drawer.
		 * mDrawerToggle will take care of this. */
		if (mDrawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}
		switch (item.getItemId())
		{
//			case R.id.refresh:
//				System.out.println("refresh");
//				ACRA.getErrorReporter().handleSilentException(
//				new RuntimeException("Exception from Downloading activity from refresh button"));
//				return true;
			case R.id.action_search:
				//				String url = "http://kuchanov.ru/acra/test.php";
				//				TestPhp testPhp = new TestPhp(act, url);
				//				testPhp.execute();
				throw new RuntimeException("Exception from Downloading activity from action_search button");
				//return true;
			case R.id.action_settings:
				item.setIntent(new Intent(this, ActivityPreference.class));
				return super.onOptionsItemSelected(item);
			case R.id.theme:
				//TODO
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}