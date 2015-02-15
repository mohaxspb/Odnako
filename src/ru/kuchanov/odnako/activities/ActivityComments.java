/*
 21.10.2014
ActivityArticle.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import java.util.ArrayList;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.animations.ZoomOutPageTransformer;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.CommentInfo;
import ru.kuchanov.odnako.lists_and_utils.CommentsViewPagerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityComments extends ActivityBase//ActionBarActivity
{
	ViewPager pager;
	PagerAdapter pagerAdapter;

	ArrayList<CommentInfo> curArtCommentsInfoList;
	ArrayList<ArrayList<CommentInfo>> allArtsCommentsInfo;

	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ActivityComments onCreate");

		this.act = this;
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

		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_comments);

		//restore state
		Bundle stateFromIntent = this.getIntent().getExtras();
		if (stateFromIntent != null)
		{
			System.out.println("childGroupPos: " + this.groupChildPosition[0] + "/ " + this.groupChildPosition[1]);
			this.restoreState(stateFromIntent);
			this.restoreGroupChildPosition(stateFromIntent);

			System.out.println("childGroupPos: " + this.groupChildPosition[0] + "/ " + this.groupChildPosition[1]);
			//			((ExpListAdapter) this.mDrawer.getExpandableListAdapter()).notifyDataSetChanged();
		}
		if (savedInstanceState != null)
		{
			this.restoreState(savedInstanceState);
			this.restoreGroupChildPosition(savedInstanceState);
			//			((ExpListAdapter) this.mDrawer.getExpandableListAdapter()).notifyDataSetChanged();
		}
		//all is null, so start request for info
		else
		{
			// TODO
			System.out.println("ActivityComments: all bundles are null, so make request for info");
		}

		//set NavigationDrawer
		this.setNavDrawer();

		//def all comms info setting
		this.allArtsCommentsInfo = CommentInfo.getDefaultAllArtsCommentsInfo(this.curAllArtsInfo.size(), 15);
		////

		this.pager = (ViewPager) this.findViewById(R.id.comments_container);
		this.pagerAdapter = new CommentsViewPagerAdapter(this.getSupportFragmentManager(), this.curAllArtsInfo,
		this.allArtsCommentsInfo, act);
		this.pager.setAdapter(pagerAdapter);
		this.pager.setCurrentItem(curArtPosition, true);
		this.pager.setPageTransformer(true, new ZoomOutPageTransformer());

		//adMob
		this.AddAds();
		//end of adMob
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_comments, menu);

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
			case R.id.action_settings:
				item.setIntent(new Intent(this, ActivityPreference.class));
				return super.onOptionsItemSelected(item);
			case R.id.theme:
				MenuItem ligthThemeMenuItem = item.getSubMenu().findItem(R.id.theme_ligth);
				MenuItem darkThemeMenuItem = item.getSubMenu().findItem(R.id.theme_dark);
				String curTheme = pref.getString("theme", "dark");
				System.out.println(curTheme);
				if (!curTheme.equals("dark"))
				{
					ligthThemeMenuItem.setChecked(true);
				}
				else
				{
					darkThemeMenuItem.setChecked(true);
				}
				return true;
			case R.id.theme_ligth:
				this.pref.edit().putString("theme", "ligth").commit();
				System.out.println("theme_ligth");
				this.recreate();
				return true;
			case R.id.theme_dark:
				System.out.println("theme_dark");
				this.pref.edit().putString("theme", "dark").commit();

				this.recreate();
				return super.onOptionsItemSelected(item);
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/* Called whenever we call supportInvalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);
		menu.findItem(R.id.action_settings_all).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onResume()
	{
		//		System.out.println("ActivityComments onResume");
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		outState.putParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO, curAllArtsInfo);
		this.saveGroupChildPosition(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		System.out.println("ActivityArticle onRestoreInstanceState");

		this.restoreState(savedInstanceState);
		this.restoreGroupChildPosition(savedInstanceState);
	}

}
