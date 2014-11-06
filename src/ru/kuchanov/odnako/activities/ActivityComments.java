/*
 21.10.2014
ActivityArticle.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import com.google.android.gms.ads.AdView;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.CommentInfo;
import ru.kuchanov.odnako.lists_and_utils.CommentsViewPagerAdapter;
import ru.kuchanov.odnako.lists_and_utils.ZoomOutPageTransformer;
import ru.kuchanov.odnako.utils.AddAds;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityComments extends ActionBarActivity
{
	ActionBarActivity act;
	SharedPreferences pref;

	AdView adView;

	ViewPager pager;
	PagerAdapter pagerAdapter;

	ArtInfo curArtInfo;
	int position;
	ArrayList<ArtInfo> allArtsInfo;

	ArrayList<CommentInfo> curArtCommentsInfoList;
	ArrayList<ArrayList<CommentInfo>> allArtsCommentsInfo;

	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ActivityArticle onCreate");

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
			this.restoreState(stateFromIntent);
		}
		else if (savedInstanceState != null)
		{
			this.restoreState(savedInstanceState);
		}
		//all is null, so start request for info
		else
		{
			// TODO
			System.out.println("ActivityArticle: all bundles are null, so make request for info");
		}

		//def all comms info setting
		this.allArtsCommentsInfo = CommentInfo.getDefaultAllArtsCommentsInfo(this.allArtsInfo.size(), 15);
		////

		this.pager = (ViewPager) this.findViewById(R.id.comments_container);
		this.pagerAdapter = new CommentsViewPagerAdapter(this.getSupportFragmentManager(), this.allArtsInfo,
		this.allArtsCommentsInfo, act);
		this.pager.setAdapter(pagerAdapter);
		this.pager.setCurrentItem(position, true);
		this.pager.setPageTransformer(true, new ZoomOutPageTransformer());

		//adMob
		adView = (AdView) this.findViewById(R.id.adView);
		AddAds addAds = new AddAds(this, this.adView);
		addAds.addAd();
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
				this.myRecreate();
				return true;
			case R.id.theme_dark:
				System.out.println("theme_dark");
				this.pref.edit().putString("theme", "dark").commit();

				this.myRecreate();
				return super.onOptionsItemSelected(item);
			default:
				return super.onOptionsItemSelected(item);
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

	@Override
	protected void onResume()
	{
		System.out.println("ActivityArticle onResume");
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		System.out.println("ActivityArticle: onSaveInstanceState");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		System.out.println("ActivityArticle onRestoreInstanceState");
	}

	private void restoreState(Bundle state)
	{
		this.curArtInfo = new ArtInfo(state.getStringArray("curArtInfo"));
		this.position = state.getInt("position");
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
