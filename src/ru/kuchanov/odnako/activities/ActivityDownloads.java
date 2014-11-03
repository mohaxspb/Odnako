/*
 19.10.2014
ActivityMain.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;


import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.download.ParseForAllAuthors;
import ru.kuchanov.odnako.download.ParseForAllCategories;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityDownloads extends ActivityBase
{
	
	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ActivityDownloads onCreate");
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

		//call super after setTheme to set it 0_0
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.layout_activity_main);

//		//drawer settings
//		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_new);
//		mDrawer = (ExpandableListView) findViewById(R.id.start_drawer);
//		//set Drawer width
//		DisplayMetrics displayMetrics = act.getResources().getDisplayMetrics();
//		int displayWidth = displayMetrics.widthPixels;
//		int actionBarHeight = TypedValue.complexToDimensionPixelSize(56, getResources().getDisplayMetrics());//=this.getSupportActionBar().getHeight();
//		// Calculate ActionBar height
//		TypedValue tv = new TypedValue();
//		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
//		{
//			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
//		}
//		DrawerLayout.LayoutParams lp = (android.support.v4.widget.DrawerLayout.LayoutParams) mDrawer.getLayoutParams();
//		int drawerWidth;
//		this.twoPane=this.pref.getBoolean("twoPane", false);
//		if (this.twoPane)
//		{
//			drawerWidth=displayMetrics.widthPixels/3;
//			if(drawerWidth<DipToPx.convert(320, act))
//			{
//				drawerWidth=(int) DipToPx.convert(320, act);
//			}
//		}
//		else
//		{
//			drawerWidth = displayWidth - actionBarHeight;
//		}
//		lp.width = drawerWidth;
//		mDrawer.setLayoutParams(lp);
//		////end of set Drawer width
//		mDrawerLayout.setDrawerListener(new DemoDrawerListener());
//		// The drawer title must be set in order to announce state changes when
//		// accessibility is turned on. This is typically a simple description,
//		// e.g. "Navigation".
//		mDrawerLayout.setDrawerTitle(GravityCompat.START, getString(R.string.drawer_open));
//		ExpListAdapter expAdapter = new ExpListAdapter(act, FillMenuList.getGroups(act));
//		mDrawer.setAdapter(expAdapter);
//		mDrawer.setOnItemClickListener(new DrawerItemClickListener());
//		mDrawer.expandGroup(1);
//		mActionBar = createActionBarHelper();
//		mActionBar.init();
//		// ActionBarDrawerToggle provides convenient helpers for tying together the
//		// prescribed interactions between a top-level sliding drawer and the action bar.
//		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
////		mDrawerToggle.
		////End of drawer settings

		//adMob
		this.AddAds();
		//end of adMob
	}

	@Override
	protected void onResume()
	{
		System.out.println("ActivityDownloads onResume");
		super.onResume();
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
			case R.id.refresh:
				System.out.println("refresh");
				// TODO
				System.out.println(this.getResources().getStringArray(R.array.all_authors_imgs)[0]);
				System.out.println(this.getResources().getStringArray(R.array.all_authors_imgs)[1]);
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
				ParseForAllCategories parse = new ParseForAllCategories(act);
				parse.execute("http://odnako.org/");
				ParseForAllAuthors parse1 = new ParseForAllAuthors(act);
				parse1.execute("http://odnako.org/authors/");
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

}
