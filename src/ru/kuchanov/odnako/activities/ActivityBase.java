/*
 01.11.2014
ActivityBase.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import com.google.android.gms.ads.AdView;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.utils.AddAds;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.ExpandableListView;

public class ActivityBase extends ActionBarActivity
{
	protected ActionBarActivity act;
	protected boolean twoPane;
	protected SharedPreferences pref;

	AdView adView;

	///drawer
	protected DrawerLayout mDrawerLayout;
	protected ExpandableListView mDrawer;
	protected ActionBarHelper mActionBar;
	protected ActionBarDrawerToggle mDrawerToggle;
	protected boolean drawerOpened=false;
	///drawer
	

	protected void AddAds()
	{
		//adMob
		adView = (AdView) this.findViewById(R.id.adView);
		AddAds addAds = new AddAds(this, this.adView);
		addAds.addAd();
		//end of adMob
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);

		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onPause()
	{
		adView.pause();
		super.onPause();
	}

	@Override
	public void onDestroy()
	{
		adView.destroy();
		super.onDestroy();
	}

	/**
	 * A drawer listener can be used to respond to drawer events such as
	 * becoming fully opened or closed. You should always prefer to perform
	 * expensive operations such as drastic relayout when no animation is
	 * currently in progress, either before or after the drawer animates.
	 * 
	 * When using ActionBarDrawerToggle, all DrawerLayout listener methods
	 * should be forwarded if the ActionBarDrawerToggle is not used as the
	 * DrawerLayout listener directly.
	 */
	protected class DemoDrawerListener implements DrawerLayout.DrawerListener
	{
		@Override
		public void onDrawerOpened(View drawerView)
		{
			mDrawerToggle.onDrawerOpened(drawerView);
			mActionBar.onDrawerOpened();
		}

		@Override
		public void onDrawerClosed(View drawerView)
		{
			mDrawerToggle.onDrawerClosed(drawerView);
			mActionBar.onDrawerClosed();
		}

		@Override
		public void onDrawerSlide(View drawerView, float slideOffset)
		{
			mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
		}

		@Override
		public void onDrawerStateChanged(int newState)
		{
			mDrawerToggle.onDrawerStateChanged(newState);
		}
	}

	@SuppressLint("NewApi")
	protected void myRecreate()
	{
		if (android.os.Build.VERSION.SDK_INT >= 11)
		{
			super.recreate();
		}
		else
		{
			finish();
			startActivity(getIntent());
		}
	}

	/**
	 * This list item click listener implements very simple view switching by
	 * changing the primary content text. The drawer is closed when a selection
	 * is made.
	 */
//	protected class DrawerItemClickListener implements ListView.OnItemClickListener
//	{
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//		{
//			//           mContent.setText(Shakespeare.DIALOGUE[position]);
//			mActionBar.setTitle(Shakespeare.TITLES[position]);
//			mDrawerLayout.closeDrawer(mDrawer);
//		}
//	}

	/**
	 * Create a compatible helper that will manipulate the action bar if
	 * available.
	 */
	protected ActionBarHelper createActionBarHelper()
	{
		return new ActionBarHelper();
	}

	/**
	 * Action bar helper for use on ICS and newer devices.
	 */
	protected class ActionBarHelper
	{
		private final ActionBar mActionBar;
		private CharSequence mDrawerTitle;
		private CharSequence mTitle;
		

		ActionBarHelper()
		{
			mActionBar = getSupportActionBar();
		}

		public void init()
		{
			mActionBar.setDisplayHomeAsUpEnabled(true);
			mActionBar.setDisplayShowHomeEnabled(false);
			mTitle = mDrawerTitle = getTitle();
		}

		/**
		 * When the drawer is closed we restore the action bar state reflecting
		 * the specific contents in view.
		 */
		public void onDrawerClosed()
		{
			mActionBar.setTitle(mTitle);
			drawerOpened=false;
		}

		/**
		 * When the drawer is open we set the action bar to a generic title. The
		 * action bar should only contain data relevant at the top level of the
		 * nav hierarchy represented by the drawer, as the rest of your content
		 * will be dimmed down and non-interactive.
		 */
		public void onDrawerOpened()
		{
			mActionBar.setTitle(mDrawerTitle);
			drawerOpened=true;
		}

		public void setTitle(CharSequence title)
		{
			mTitle = title;
		}
	}

	
}
