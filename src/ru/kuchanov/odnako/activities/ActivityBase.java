/*
 01.11.2014
ActivityBase.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import java.util.ArrayList;
import java.util.HashMap;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.DrawerGroupClickListener;
import ru.kuchanov.odnako.lists_and_utils.DrawerItemClickListener;
import ru.kuchanov.odnako.lists_and_utils.ExpListAdapter;
import ru.kuchanov.odnako.lists_and_utils.FillMenuList;
import ru.kuchanov.odnako.utils.AddAds;
import ru.kuchanov.odnako.utils.DipToPx;
import ru.kuchanov.odnako.utils.UniversalImageLoader;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ActivityBase extends ActionBarActivity
{
	private static final String LOG_TAG = ActivityBase.class.getSimpleName();
	protected ActionBarActivity act;
	protected boolean twoPane;
	protected SharedPreferences pref;

	AdView adView;

	//Toolbar
	Toolbar toolbar;

	///drawer
	protected DrawerLayout mDrawerLayout;
	protected ExpandableListView mDrawer;
	protected ExpListAdapter expAdapter;
	protected boolean drawerOpened;
	protected ActionBarDrawerToggle mDrawerToggle;

	protected int[] groupChildPosition = new int[] { 1, 7 };
	///drawer

	HashMap<String, ArrayList<ArtInfo>> allCatArtsInfo;

	int currentCategoryPosition = 11;

	protected ArtInfo curArtInfo = null;
	//	protected int curArtPosition = -1;
	protected int curArtPosition = 0;
	protected ArrayList<ArtInfo> curAllArtsInfo = null;

	private int backPressedQ;

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

		// As we're using a Toolbar, we should retrieve it and set it
		// to be our ActionBar
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// Now retrieve the DrawerLayout so that we can set the status bar color.
		// This only takes effect on Lollipop, or when using translucentStatusBar
		// on KitKat.
		//	    mDrawerLayout.setStatusBarBackgroundColor(Color.BLUE);

		////////////////////////////////

		// ActionBarDrawerToggle provides convenient helpers for tying together the
		// prescribed interactions between a top-level sliding drawer and the action bar.
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close)
		{

			public void onDrawerClosed(View view)
			{
				supportInvalidateOptionsMenu();
				drawerOpened = false;
			}

			public void onDrawerOpened(View drawerView)
			{
				supportInvalidateOptionsMenu();
				drawerOpened = true;
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		// The drawer title must be set in order to announce state changes when
		// accessibility is turned on. This is typically a simple description,
		// e.g. "Navigation".
		mDrawerLayout.setDrawerTitle(GravityCompat.START, getString(R.string.drawer_open));
		//setHeader BEFORE setting adapter
		this.addHeaderForDrawer();
		////
		this.expAdapter = new ExpListAdapter(act, FillMenuList.getGroups(act));
		mDrawer.setAdapter(expAdapter);
		mDrawer.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);
		mDrawer.setOnChildClickListener(new DrawerItemClickListener(mDrawerLayout, mDrawer, act));
		mDrawer.setOnGroupClickListener(new DrawerGroupClickListener(mDrawerLayout, mDrawer, act));

		mDrawer.expandGroup(1);
		((ExpListAdapter) this.mDrawer.getExpandableListAdapter()).notifyDataSetChanged();

		////End of drawer settings
	}

	private void addHeaderForDrawer()
	{
		View header = (View) this.getLayoutInflater().inflate(R.layout.drawer_header, this.mDrawer, false);
		ImageView ava = (ImageView) header.findViewById(R.id.ava_img);
		//		ava.setImageResource(R.drawable.dev_ava);
		ImageLoader imgLoader = UniversalImageLoader.get(act);
		imgLoader.displayImage("drawable://" + R.drawable.dev_ava, ava,
		UniversalImageLoader.getTransparentBackgroundROUNDOptions(act));
		ava.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Toast.makeText(act, R.string.feature_login, Toast.LENGTH_LONG).show();
			}
		});
		this.mDrawer.addHeaderView(header);
	}

	public int[] getGroupChildPosition()
	{
		return this.groupChildPosition;
	}

	public void setGroupChildPosition(int group, int child)
	{
		groupChildPosition = new int[] { group, child };
		if (this.expAdapter != null)
		{
			this.expAdapter.notifyDataSetChanged();
		}
	}

	protected void saveGroupChildPosition(Bundle state)
	{
		state.putIntArray("groupChildPosition", groupChildPosition);
	}

	protected void saveCurentCategoryPosition(Bundle state)
	{
		state.putInt("curentCategoryPosition", currentCategoryPosition);
	}

	protected void restoreGroupChildPosition(Bundle state)
	{
		if (state.containsKey("groupChildPosition"))
		{
			this.groupChildPosition = state.getIntArray("groupChildPosition");
		}
		else
		{
			System.out.println("restoring groupChildPosition FAILED from " + this.getClass().getSimpleName()
			+ " groupChildPosition=null");
		}
	}

	protected void restoreState(Bundle state)
	{
		//		System.out.println("restoring state from " + this.getClass().getSimpleName());

		if (state.containsKey("curArtInfo"))
		{
			this.curArtInfo = new ArtInfo(state.getStringArray("curArtInfo"));
		}
		else
		{
			//			System.out.println("this.curArtInfo in Bundle in " + this.getClass().getSimpleName() + " =null");
		}
		if (state.containsKey("position"))
		{
			this.curArtPosition = state.getInt("position");
		}
		else
		{
			//			System.out.println("this.position in Bundle in " + this.getClass().getSimpleName() + " =null");
		}
		this.curAllArtsInfo = ArtInfo.restoreAllArtsInfoFromBundle(state, LOG_TAG);

		if (state.containsKey("curentCategoryPosition"))
		{
			this.currentCategoryPosition = state.getInt("curentCategoryPosition");
		}
	}

	public HashMap<String, ArrayList<ArtInfo>> getAllCatArtsInfo()
	{
		return this.allCatArtsInfo;
	}

	public void updateAllCatArtsInfo(String category, ArrayList<ArtInfo> newData)
	{
		this.allCatArtsInfo.put(category, newData);
	}
	
	public int getCurentCategoryPosition()
	{
		return currentCategoryPosition;
	}

	public void setCurentCategoryPosition(int curentCategoryPosition)
	{
		this.currentCategoryPosition = curentCategoryPosition;

		int[] groupChild = this.getGroupChildPositionByCurentPosition(curentCategoryPosition);

		this.setGroupChildPosition(groupChild[0], groupChild[1]);
	}
	
	public int[] getGroupChildPositionByCurentPosition(int curentPosition)
	{
		int firstCategoryChildrenQuontity = act.getResources().getStringArray(R.array.authors_links).length;

		int group = -1;
		int child = -1;

		if (curentPosition >= firstCategoryChildrenQuontity)
		{
			group = 1;
			child = curentPosition - firstCategoryChildrenQuontity;
		}
		else
		{
			group = 0;
			child = curentPosition;
		}
		return new int[] { group, child };
	}

	@Override
	public void onBackPressed()
	{
		if (this.act.getClass().getSimpleName().equals("ActivityMain"))
		{
			if (this.backPressedQ == 1)
			{
				this.backPressedQ = 0;
				super.onBackPressed();
				this.finish();
			}
			else
			{
				if (mDrawerLayout.isDrawerOpen(Gravity.START))
				{
					this.mDrawerLayout.closeDrawer(Gravity.LEFT);
				}
				else
				{
					this.backPressedQ++;
					Toast.makeText(this, "Нажмите ещё раз, чтобы выйти", Toast.LENGTH_SHORT).show();
				}
			}
			//Обнуление счётчика через 5 секунд
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					// Do something after 5s = 5000ms
					backPressedQ = 0;
					//checkNew();
				}
			}, 5000);
		}
		else
		{
			if (mDrawerLayout.isDrawerOpen(Gravity.START))
			{
				this.mDrawerLayout.closeDrawer(Gravity.LEFT);
			}
			else
			{
				super.onBackPressed();
			}
		}
	}
}
