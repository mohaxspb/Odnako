/*
 19.10.2014
ActivityMain.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import java.util.ArrayList;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.download.ParseForAllAuthors;
import ru.kuchanov.odnako.download.ParseForAllCategories;
import ru.kuchanov.odnako.download.ParseForAllCategoriesImages;
import ru.kuchanov.odnako.fragments.ArticlesListFragment;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.ArticleViewPagerAdapter;
import ru.kuchanov.odnako.lists_and_utils.ArtsListViewPagerAdapter;
import ru.kuchanov.odnako.lists_and_utils.ZoomOutPageTransformer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class ActivityMain extends ActivityBase
{
	ViewPager pager;
	PagerAdapter pagerAdapter;

	ViewPager artsListPager;
	PagerAdapter artsListPagerAdapter;

	private int backPressedQ;
	
	ImageView topImgCover;
	ImageView topImg;

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

		Bundle stateFromIntent = this.getIntent().getExtras();
		if (stateFromIntent != null)
		{
			this.restoreState(stateFromIntent);

			//			int[] intArr;
			//			intArr = stateFromIntent.getIntArray("groupChildPosition");
			//			System.out.println("childGroupPos: " + intArr[0] + "/ " + intArr[1]);

			this.restoreGroupChildPosition(stateFromIntent);
			//			System.out.println("childGroupPos: " + this.groupChildPosition[0] + "/ " + this.groupChildPosition[1]);
		}
		else if (savedInstanceState != null)
		{
			this.restoreState(savedInstanceState);
			this.restoreGroupChildPosition(savedInstanceState);

		}
		//all is null, so start request for info
		else
		{
			// TODO
			System.out.println("ActivityArticle: all bundles are null, so make request for info");
		}

		this.setContentView(R.layout.activity_main);

		//setNavDraw
		this.setNavDrawer();
		//End of setNavDraw
		
		//onMain if we don't use twoPane mode we'll set alpha for action bar
		//we'll do it after setNavDrawer, cause we find toolbar in it
		//onMain if we don't use twoPane mode we'll set alpha for action bar
		//we'll do it after setNavDrawer, cause we find toolbar in it
		if (android.os.Build.VERSION.SDK_INT >= 11 && this.twoPane == false)
		{
			toolbar.getBackground().setAlpha(0);
		}
		else if (this.pref.getBoolean("animate_lists", false))
		{
			toolbar.getBackground().setAlpha(255);

		}
		//setTopImageCover
		topImgCover = (ImageView) this.findViewById(R.id.top_img_cover);
		if (this.pref.getString("theme", "dark").equals("dark"))
		{
			topImgCover.setBackgroundResource(R.drawable.top_img_cover_grey_dark);
		}
		else
		{
			topImgCover.setBackgroundResource(R.drawable.top_img_cover_grey_light);
		}
		this.topImg=(ImageView)this.findViewById(R.id.top_img);
		this.topImg.setImageResource(R.drawable.odnako);

		

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
		//////////////////
		this.artsListPager = (ViewPager) this.findViewById(R.id.arts_list_container);
		this.artsListPagerAdapter = new ArtsListViewPagerAdapter(this.getSupportFragmentManager(), act);
		this.artsListPager.setAdapter(artsListPagerAdapter);
		this.artsListPager.setPageTransformer(true, new ZoomOutPageTransformer());
		this.artsListPager.setCurrentItem(11, true);
		this.artsListPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				String title = ((ArtsListViewPagerAdapter) artsListPagerAdapter).getAllCategoriesMenuNames()[position];
				setTitle(title);

				int group;
				int child;
				//3 is not a magic number! It's a quontity of categories in authors menu items in drawer
				if (position > 3)
				{
					group = 1;
					child = position - 4;
				}
				else
				{
					group = 0;
					child = position;
				}
				setGroupChildPosition(group, child);
			}
		});

		//////////

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

		this.saveGroupChildPosition(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		System.out.println("ActivityMain onRestoreInstanceState");

		this.restoreState(savedInstanceState);
		this.restoreGroupChildPosition(savedInstanceState);
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
				//download all categories images
//				String[] allCatUrls=this.getResources().getStringArray(R.array.all_categories_urls);
//				for(int i=0; i<allCatUrls.length; i++)
//				{
//					ParseForAllCategoriesImages parse=new ParseForAllCategoriesImages(act);
//					parse.execute(allCatUrls[i]);
//				}
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

	public ArrayList<ArtInfo> getAllArtsInfo()
	{
		return this.allArtsInfo;
	}

	public void setAllArtsInfo(ArrayList<ArtInfo> allArtsInfo)
	{
		this.allArtsInfo = allArtsInfo;
	}

	public ArtInfo getCUR_ART_INFO()
	{
		return curArtInfo;
	}

	public void setCUR_ART_INFO(ArtInfo cUR_ARTS_INFO)
	{
		this.curArtInfo = cUR_ARTS_INFO;
	}

	/* Called whenever we call supportInvalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);
		menu.findItem(R.id.action_settings_all).setVisible(!drawerOpen);
		menu.findItem(R.id.refresh).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onBackPressed()
	{
		if (this.backPressedQ == 1)
		{
			this.backPressedQ = 0;
			super.onBackPressed();
			this.finish();
		}
		else
		{
			//			if (drawerOpened)
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
		//Обнуление счётчика через 5 секунд
	}

}
