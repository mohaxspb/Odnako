/*
 19.10.2014
ActivityMain.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.download.ParseForAllAuthors;
import ru.kuchanov.odnako.download.ParseForAllCategories;
import ru.kuchanov.odnako.fragments.ArticlesListFragment;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.ArticleViewPagerAdapter;
import ru.kuchanov.odnako.lists_and_utils.ExpListAdapter;
import ru.kuchanov.odnako.lists_and_utils.FillMenuList;
import ru.kuchanov.odnako.lists_and_utils.ZoomOutPageTransformer;
import ru.kuchanov.odnako.utils.DipToPx;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ListView;

public class ActivityMain extends ActivityBase
{
	ViewPager pager;
	PagerAdapter pagerAdapter;

	private ArrayList<ArtInfo> allArtsInfo;
	private ArtInfo curArtInfo;
	int position;

	ExpandableListView mDrawer;

	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ActivityMain onCreate");
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

		//drawer settings
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_new);
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
		mDrawerLayout.setDrawerListener(new DemoDrawerListener());
		// The drawer title must be set in order to announce state changes when
		// accessibility is turned on. This is typically a simple description,
		// e.g. "Navigation".
		mDrawerLayout.setDrawerTitle(GravityCompat.START, getString(R.string.drawer_open));
		ExpListAdapter expAdapter = new ExpListAdapter(act, FillMenuList.getGroups(act));
		mDrawer.setAdapter(expAdapter);
		mDrawer.setOnItemClickListener(new DrawerItemClickListener());
		mDrawer.expandGroup(1);
		mActionBar = createActionBarHelper();
		mActionBar.init();
		// ActionBarDrawerToggle provides convenient helpers for tying together the
		// prescribed interactions between a top-level sliding drawer and the action bar.
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
		////End of drawer settings

		//Set unreaded num of arts to zero
		//it's for new arts motification
		SharedPreferences prefsNumOfArts = this.getSharedPreferences("saveNumOfUnReadedArts", 0);
		SharedPreferences.Editor editor = prefsNumOfArts.edit();
		editor.putInt("quontityOfUnreadedArts", 0);
		editor.commit();
		//end of Set unreaded num of arts to zero

		//check if there is two fragments. If so, set flag (twoPane) to true
		if (findViewById(R.id.article_comments_container) != null)
		{
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			twoPane = true;

			//save it to pref, to be able to read it without calling Activity
			this.pref.edit().putBoolean("twoPane", twoPane).commit();

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((ArticlesListFragment) getSupportFragmentManager().findFragmentById(R.id.articles_list))
			.setActivateOnItemClick(true);
			//
			//
			this.pager = (ViewPager) this.findViewById(R.id.article_comments_container);

			//get position from listFrag
			this.position = ((ArticlesListFragment) getSupportFragmentManager().findFragmentById(R.id.articles_list))
			.getMyActivatedPosition();
			if (this.position == ListView.INVALID_POSITION)
			{
				this.position = 0;
			}
			((ArticlesListFragment) getSupportFragmentManager().findFragmentById(R.id.articles_list))
			.setActivatedPosition(position);
			this.pagerAdapter = new ArticleViewPagerAdapter(this.getSupportFragmentManager(), allArtsInfo, this);
			this.pager.setAdapter(pagerAdapter);
			this.pager.setPageTransformer(true, new ZoomOutPageTransformer());
			this.pager.setCurrentItem(position, true);
			this.pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
			{
				@Override
				public void onPageSelected(int position)
				{
					ArticlesListFragment artsListFrag = (ArticlesListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.articles_list);
					//setActivPosition to curItem of pager
					artsListFrag.setActivatedPosition(position);
					artsListFrag.scrollToActivatedPosition();
				}
			});
		}
		//adMob
		this.AddAds();
		//end of adMob
	}

	@Override
	protected void onResume()
	{
		System.out.println("ActivityMain onResume");
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		System.out.println("ActivityMain: onSaveInstanceState");

		//save allArtsInfo
		ArtInfo.writeAllArtsInfoToBundle(outState, allArtsInfo, curArtInfo);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		System.out.println("ActivityMain onRestoreInstanceState");

		//restore All_arts_info
		Set<String> keySet = savedInstanceState.keySet();
		ArrayList<String> keySetSortedArrList = new ArrayList<String>(keySet);
		Collections.sort(keySetSortedArrList);
		if (keySet.contains("allArtsInfo_00"))
		{
			this.allArtsInfo = new ArrayList<ArtInfo>();
			for (int i = 0; i < keySetSortedArrList.size(); i++)
			{
				String s = keySetSortedArrList.get(i);
				if (s.startsWith("allArtsInfo_"))
				{
					if (i < 10)
					{
						this.allArtsInfo.add(new ArtInfo(savedInstanceState.getStringArray("allArtsInfo_0"
						+ String.valueOf(i))));
					}
					else
					{
						this.allArtsInfo.add(new ArtInfo(savedInstanceState.getStringArray("allArtsInfo_"
						+ String.valueOf(i))));
					}
				}
			}
		}
		else
		{
			System.out.println("ActivityMain: onRestoreInstanceState. allArtsInfo=null");
		}

		//restore curArtInfo
		if (keySet.contains("curArtInfo"))
		{
			this.curArtInfo = new ArtInfo(savedInstanceState.getStringArray("curArtInfo"));
		}
		else
		{
			System.out.println("ActivityMain: onRestoreInstanceState. curArtInfo=null");
		}
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

	/**
	 * @return the curArtInfo
	 */
	public ArrayList<ArtInfo> getAllArtsInfo()
	{
		return this.allArtsInfo;
	}

	/**
	 * @param curArtInfo
	 *            the curArtInfo to set
	 */
	public void setAllArtsInfo(ArrayList<ArtInfo> allArtsInfo)
	{
		this.allArtsInfo = allArtsInfo;
	}

	/**
	 * @return the cUR_ARTS_INFO
	 */
	public ArtInfo getCUR_ART_INFO()
	{
		return curArtInfo;
	}

	/**
	 * @param cUR_ARTS_INFO
	 *            the cUR_ARTS_INFO to set
	 */
	public void setCUR_ART_INFO(ArtInfo cUR_ARTS_INFO)
	{
		this.curArtInfo = cUR_ARTS_INFO;
	}

}
