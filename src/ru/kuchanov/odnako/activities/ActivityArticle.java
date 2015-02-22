/*
 21.10.2014
ActivityArticle.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.animations.ZoomOutPageTransformer;
import ru.kuchanov.odnako.lists_and_utils.Actions;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityArticle extends ActivityBase
{
	ViewPager pager;
	PagerAdapter pagerAdapter;

	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ActivityArticle onCreate");

		this.act = this;
		//get default settings to get all settings later
		PreferenceManager.setDefaultValues(act, R.xml.pref, true);
		this.pref = PreferenceManager.getDefaultSharedPreferences(act);
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

		this.setContentView(R.layout.activity_article);

		//restore state
		Bundle stateFromIntent = this.getIntent().getExtras();
		if (stateFromIntent != null)
		{
			this.restoreState(stateFromIntent);
			
			int[] intArr;
			intArr=stateFromIntent.getIntArray("groupChildPosition");
			System.out.println("childGroupPos: " + intArr[0] + "/ " + intArr[1]);
			
			this.restoreGroupChildPosition(stateFromIntent);
//			((ExpListAdapter) this.mDrawer.getExpandableListAdapter()).notifyDataSetChanged();
//			System.out.println("childGroupPos: " + this.groupChildPosition[0] + "/ " + this.groupChildPosition[1]);
		}
		else if (savedInstanceState != null)
		{
			this.restoreState(savedInstanceState);
			this.restoreGroupChildPosition(savedInstanceState);
//			((ExpListAdapter) this.mDrawer.getExpandableListAdapter()).notifyDataSetChanged();
		}
		//all is null, so start request for info
		else
		{
			// TODO
			System.out.println("ActivityArticle: all bundles are null, so make request for info");
		}

		//drawer settings
		this.setNavDrawer();
		////End of drawer settings

		this.pager = (ViewPager) this.findViewById(R.id.article_container);
//		this.pagerAdapter = new ArticlesPagerAdapter(this.getSupportFragmentManager(), this.curAllArtsInfo, this);
		this.pager.setAdapter(pagerAdapter);
		this.pager.setCurrentItem(getCurArtPosition(), true);
		this.pager.setPageTransformer(true, new ZoomOutPageTransformer());

		//adMob
		this.AddAds();
		//end of adMob
	}
	
	/* Called whenever we call supportInvalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);
		menu.findItem(R.id.action_settings_all).setVisible(!drawerOpen);
		menu.findItem(R.id.comments).setVisible(!drawerOpen);
		menu.findItem(R.id.share).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_article, menu);

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
			case R.id.comments:
				Actions.showComments(curAllArtsInfo, getCurArtPosition(), act);//.showComments(allArtsInfo, position, act);
				return true;
			case R.id.share:
//				Actions.shareUrl(this.curArtInfo.url, this.act);
				Actions.shareUrl(this.curAllArtsInfo.get(this.getCurArtPosition()).url, this.act);
				return true;
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

		//save allArtsInfo
//		ArtInfo.writeAllArtsInfoToBundle(outState, curAllArtsInfo, curArtInfo);
		outState.putParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO, curAllArtsInfo);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		System.out.println("ActivityArticle onRestoreInstanceState");

		this.restoreState(savedInstanceState);
	}
}
