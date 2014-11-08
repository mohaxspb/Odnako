/*
 01.11.2014
ActivityBase.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.DrawerGroupClickListener;
import ru.kuchanov.odnako.lists_and_utils.DrawerItemClickListener;
import ru.kuchanov.odnako.lists_and_utils.ExpListAdapter;
import ru.kuchanov.odnako.lists_and_utils.FillMenuList;
import ru.kuchanov.odnako.utils.AddAds;
import ru.kuchanov.odnako.utils.DipToPx;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ExpandableListView;

import com.google.android.gms.ads.AdView;

public class ActivityBase extends ActionBarActivity
{
	protected ActionBarActivity act;
	protected boolean twoPane;
	protected SharedPreferences pref;

	AdView adView;
	
	//Toolbar
	Toolbar toolbar;
	////End of Toolbar

	///drawer
	protected DrawerLayout mDrawerLayout;
	protected ExpandableListView mDrawer;
//	protected ActionBarHelper mActionBar;
	protected boolean drawerOpened;
	protected ActionBarDrawerToggle mDrawerToggle;
	protected Bundle additionalBundle;
	protected int[] groupChildPosition = new int[] { -1, -1 };
	///drawer

	protected ArtInfo curArtInfo = null;
	protected int position = -1;
	protected ArrayList<ArtInfo> allArtsInfo = null;

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

	/* Called whenever we call supportInvalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(Gravity.START);
		menu.findItem(R.id.action_settings_all).setVisible(!drawerOpen);
		//		menu.findItem(R.id.comments).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
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
		int actionBarHeight = 56;//TypedValue.complexToDimensionPixelSize(56, getResources().getDisplayMetrics());//=this.getSupportActionBar().getHeight();
		// Calculate ActionBar height
		TypedValue tv = new TypedValue();
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
		}
		DrawerLayout.LayoutParams lp = (android.support.v4.widget.DrawerLayout.LayoutParams) mDrawer.getLayoutParams();
		int drawerWidth;
		this.twoPane = this.pref.getBoolean("twoPane", false);
		if (this.twoPane)
		{
			drawerWidth = displayMetrics.widthPixels / 3;
			if (drawerWidth < DipToPx.convert(320, act))
			{
				drawerWidth = (int) DipToPx.convert(320, act);
			}
		}
		else
		{
			drawerWidth = displayWidth - actionBarHeight;
		}
		lp.width = drawerWidth;
		mDrawer.setLayoutParams(lp);
		////end of set Drawer width
//		mActionBar = createActionBarHelper();
//		mActionBar.init();
		
		///////////////////////////////////////////////
		// As we're using a Toolbar, we should retrieve it and set it
	    // to be our ActionBar
	    toolbar = (Toolbar) findViewById(R.id.toolbar);
	    setSupportActionBar(toolbar);
	    toolbar.getBackground().setAlpha(0);
//	    ColorDrawable CD=new ColorDrawable(R.color.material_grey_800);
//	    CD.setAlpha(0);
//	    this.getSupportActionBar().setBackgroundDrawable(CD);
	    
//	    this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(R.color.material_blue_grey_800));
//	    this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	    

	    // Now retrieve the DrawerLayout so that we can set the status bar color.
	    // This only takes effect on Lollipop, or when using translucentStatusBar
	    // on KitKat.
//	    DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	    mDrawerLayout.setStatusBarBackgroundColor(Color.BLUE);
	    
	    ////////////////////////////////
	    
		// ActionBarDrawerToggle provides convenient helpers for tying together the
		// prescribed interactions between a top-level sliding drawer and the action bar.
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close)
		{
			

			public void onDrawerClosed(View view)
			{
				//				getSupportActionBar().setTitle(mTitle);
//				drawerOpened=false;
				supportInvalidateOptionsMenu();
				drawerOpened=false;
			}

			public void onDrawerOpened(View drawerView)
			{
				//				getSupportActionBar().setTitle(mDrawerTitle);
				
				supportInvalidateOptionsMenu();
				drawerOpened=true;
			}
		};
//		mDrawerLayout.setDrawerListener(new DemoDrawerListener(this.mDrawerToggle));
		mDrawerLayout.setDrawerListener(mDrawerToggle);
//		mDrawerLayout.setDrawerListener(new DemoDrawerListener(this.mActionBar, this.mDrawerToggle));
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
		((ExpListAdapter) this.mDrawer.getExpandableListAdapter()).notifyDataSetChanged();

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

	public int[] getGroupChildPosition()
	{
		return this.groupChildPosition;
	}

	public void setGroupChildPosition(int group, int child)
	{
		groupChildPosition = new int[] { group, child };
	}

	protected void saveGroupChildPosition(Bundle state)
	{
		state.putIntArray("groupChildPosition", groupChildPosition);
	}

	protected void restoreGroupChildPosition(Bundle state)
	{
		//		groupChildPosition[0] = state.getInt("groupPosition");
		//		groupChildPosition[1] = state.getInt("childPosition");
		this.groupChildPosition = state.getIntArray("groupChildPosition");
	}

	protected void restoreState(Bundle state)
	{
		if (state.containsKey("curArtInfo"))
		{
			this.curArtInfo = new ArtInfo(state.getStringArray("curArtInfo"));
		}
		if (state.containsKey("position"))
		{
			this.position = state.getInt("position");
		}
		if (state.containsKey("allArtsInfo_00"))
		{
			//restore AllArtsInfo
			this.allArtsInfo = new ArrayList<ArtInfo>();
			Set<String> keySet = state.keySet();
			ArrayList<String> keySetSortedArrList = new ArrayList<String>(keySet);
			Collections.sort(keySetSortedArrList);
			for (int i = 0; i < keySetSortedArrList.size(); i++)
			{
				if (keySetSortedArrList.get(i).startsWith("allArtsInfo_"))
				{
					if (i < 10)
					{
						this.allArtsInfo.add(new ArtInfo(state.getStringArray("allArtsInfo_0"
						+ String.valueOf(i))));
					}
					else
					{
						this.allArtsInfo.add(new ArtInfo(state.getStringArray("allArtsInfo_"
						+ String.valueOf(i))));
					}

				}
				else
				{
					break;
				}
			}
		}

	}
}
