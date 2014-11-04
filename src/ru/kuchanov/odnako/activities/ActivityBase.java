/*
 01.11.2014
ActivityBase.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.lists_and_utils.DemoDrawerListener;
import ru.kuchanov.odnako.lists_and_utils.DrawerGroupClickListener;
import ru.kuchanov.odnako.lists_and_utils.DrawerItemClickListener;
import ru.kuchanov.odnako.lists_and_utils.ExpListAdapter;
import ru.kuchanov.odnako.lists_and_utils.FillMenuList;
import ru.kuchanov.odnako.utils.AddAds;
import ru.kuchanov.odnako.utils.DipToPx;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ExpandableListView;

import com.google.android.gms.ads.AdView;

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
	protected boolean drawerOpened = false;
	protected Bundle additionalBundle;
	protected int[] groupChildPosition;

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
	
	//set Navigatin drawer
	protected void setNavDrawer()
	{
		//drawer settings
		mDrawerLayout = (DrawerLayout) this.act.findViewById(R.id.drawer_layout);
		mDrawer = (ExpandableListView) findViewById(R.id.start_drawer);
		//set Drawer width
		DisplayMetrics displayMetrics = act.getResources().getDisplayMetrics();
		int displayWidth = displayMetrics.widthPixels;
		int actionBarHeight = TypedValue.complexToDimensionPixelSize(56, getResources().getDisplayMetrics());//=this.getSupportActionBar().getHeight();
		// Calculate ActionBar height
		TypedValue tv = new TypedValue();
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
		}
		DrawerLayout.LayoutParams lp = (android.support.v4.widget.DrawerLayout.LayoutParams) mDrawer.getLayoutParams();
		int drawerWidth;
		this.twoPane=this.pref.getBoolean("twoPane", false);
		if (this.twoPane)
		{
			drawerWidth=displayMetrics.widthPixels/3;
			if(drawerWidth<DipToPx.convert(320, act))
			{
				drawerWidth=(int) DipToPx.convert(320, act);
			}
		}
		else
		{
			drawerWidth = displayWidth - actionBarHeight;
		}
		lp.width = drawerWidth;
		mDrawer.setLayoutParams(lp);
		////end of set Drawer width
		mActionBar = createActionBarHelper();
		mActionBar.init();
		// ActionBarDrawerToggle provides convenient helpers for tying together the
		// prescribed interactions between a top-level sliding drawer and the action bar.
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
		mDrawerLayout.setDrawerListener(new DemoDrawerListener(this.mActionBar, this.mDrawerToggle));
		// The drawer title must be set in order to announce state changes when
		// accessibility is turned on. This is typically a simple description,
		// e.g. "Navigation".
		mDrawerLayout.setDrawerTitle(GravityCompat.START, getString(R.string.drawer_open));
		ExpListAdapter expAdapter = new ExpListAdapter(act, FillMenuList.getGroups(act));
		mDrawer.setAdapter(expAdapter);
		mDrawer.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);
		mDrawer.setOnChildClickListener(new DrawerItemClickListener(mDrawerLayout, mDrawer, act));
		mDrawer.setOnGroupClickListener(new DrawerGroupClickListener(mDrawerLayout, mDrawer, act));
		mDrawer.expandGroup(1);
		
		
		////End of drawer settings
	}
	/**
	 * Create a compatible helper that will manipulate the action bar if
	 * available.
	 */
	protected ActionBarHelper createActionBarHelper()
	{
		return new ActionBarHelper(act, this.mDrawer);
	}

	
	
	//add some info to Bundle
	public Bundle getActivityBundleToWriteSomething()
	{
		if(this.additionalBundle==null)
		{
			this.additionalBundle=new Bundle();
		}
		return additionalBundle;
	}
	
	public int[] getGroupChildPosition()
	{
		if(groupChildPosition==null)
		{
			groupChildPosition=new int[]{-1, -1};
			groupChildPosition=new int[]{1, 3};
		}
		return groupChildPosition;
	}

}
