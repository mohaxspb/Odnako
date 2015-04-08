/*
 01.11.2014
ActivityBase.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.db.Category;
import ru.kuchanov.odnako.db.DataBaseHelper;
import ru.kuchanov.odnako.fragments.FragmentArticle;
import ru.kuchanov.odnako.fragments.FragmentComments;
import ru.kuchanov.odnako.lists_and_utils.DrawerGroupClickListener;
import ru.kuchanov.odnako.lists_and_utils.DrawerItemClickListener;
import ru.kuchanov.odnako.lists_and_utils.ExpListAdapter;
import ru.kuchanov.odnako.lists_and_utils.FillMenuList;
import ru.kuchanov.odnako.lists_and_utils.PagerListenerArticle;
import ru.kuchanov.odnako.utils.AddAds;
import ru.kuchanov.odnako.utils.DipToPx;
import ru.kuchanov.odnako.utils.MyUIL;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ActivityBase extends ActionBarActivity
{
	static final String LOG = ActivityBase.class.getSimpleName();
	protected ActionBarActivity act;
	protected boolean twoPane;
	protected SharedPreferences pref;

	AdView adView;

	//Toolbar
	Toolbar toolbar;

	//drawer
	protected DrawerLayout mDrawerLayout;
	protected ExpandableListView mDrawer;
	protected ExpListAdapter expAdapter;
	protected boolean drawerOpened;
	public ActionBarDrawerToggle mDrawerToggle;

	protected int[] groupChildPosition = new int[] { 1, 7 };
	public static final String KEY_GROUP_CHILD_POSITION = "groupChildPosition";
	////drawer

	/**
	 * map with lists of articles info for all categories and authors, witch
	 * keys gets from BD
	 */
	HashMap<String, ArrayList<Article>> allCatArtsInfo;

	/**
	 * List of all authors from DB
	 */
	protected ArrayList<Author> allAuthorsList;
	protected static final String KEY_ALL_AUTHORS_LIST = "allAuthorsList";

	/**
	 * List of all categories from DB
	 */
	protected ArrayList<Category> allCategoriesList;
	protected static final String KEY_ALL_CATEGORIES_LIST = "allCategoriesList";

	protected ArrayList<String> allCatAndAutURLs;
	protected static final String KEY_ALL_CAT_AND_AUT_URLS_LIST = "allCatAndAutURLs";

	protected ArrayList<String> allCatAndAutTitles;
	protected static final String KEY_ALL_CAT_AND_AUT_TITLES_LIST = "allCatAndAutTitles";

	int currentCategoryPosition = 11;
	public static final String KEY_CURRENT_CATEGORY_POSITION = "currentCategoryPosition";
	private String currentCategory = "odnako.org/blogs";
	public static final String KEY_CURRENT_CATEGORY = "currentCategory";

	//	protected ArtInfo curArtInfo = null;
	//	protected int curArtPosition = -1;
	private int curArtPosition = 0;
	protected ArrayList<Article> curAllArtsInfo = null;

	protected int backPressedQ;

	protected Menu menu;
	private String searchText;

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
		if (this.adView != null)
		{
			adView.destroy();
		}
		super.onDestroy();
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
		mDrawerLayout.setStatusBarBackgroundColor(Color.BLUE);

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
		//		mDrawerLayout.setDrawerTitle(GravityCompat.START, getString(R.string.drawer_open));
		//setHeader BEFORE setting adapter
		if (this.mDrawer.getHeaderViewsCount() == 0)
		{
			this.addHeaderForDrawer();
		}
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
		ImageLoader imgLoader = MyUIL.get(act);
		imgLoader.displayImage("drawable://" + R.drawable.dev_ava, ava,
		MyUIL.getTransparentBackgroundROUNDOptions(act));
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
		state.putIntArray(KEY_GROUP_CHILD_POSITION, groupChildPosition);
	}

	protected void saveCurentCategoryPosition(Bundle state)
	{
		state.putInt("curentCategoryPosition", currentCategoryPosition);
	}

	protected void restoreGroupChildPosition(Bundle state)
	{
		if (state.containsKey(KEY_GROUP_CHILD_POSITION))
		{
			this.groupChildPosition = state.getIntArray(KEY_GROUP_CHILD_POSITION);
		}
		else
		{
			System.out.println("restoring groupChildPosition FAILED from " + this.getClass().getSimpleName()
			+ " groupChildPosition=null");
		}
	}

	public HashMap<String, ArrayList<Article>> getAllCatArtsInfo()
	{
		return this.allCatArtsInfo;
	}

	public int getCurentCategoryPosition()
	{
		return currentCategoryPosition;
	}

	@SuppressWarnings("unchecked")
	protected void restoreAllCatArtsInfo(Bundle b)
	{
		this.allCatArtsInfo = (HashMap<String, ArrayList<Article>>) b.getSerializable("all_cats_art_info");
	}

	protected void saveAllCatArtsInfo(Bundle b)
	{
		b.putSerializable("all_cats_art_info", this.allCatArtsInfo);
	}

	public void setCurentCategoryPosition(int curentCategoryPosition)
	{
		this.currentCategoryPosition = curentCategoryPosition;

		int[] groupChild = this.getGroupChildPositionByCurentPosition(curentCategoryPosition);

		this.setGroupChildPosition(groupChild[0], groupChild[1]);
	}

	public String getCurrentCategory()
	{
		return currentCategory;
	}

	public void setCurrentCategory(String currentCategory)
	{
		this.currentCategory = currentCategory;
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

	public int getCurentPositionByGroupChildPosition(int group, int child)
	{
		int firstCategoryChildrenQuontity = act.getResources().getStringArray(R.array.authors_links).length;

		int curentPosition = 11;

		if (group == 0)
		{
			curentPosition = child;
		}
		else if (group == 1)
		{
			curentPosition = firstCategoryChildrenQuontity + child;
		}

		return curentPosition;
	}

	@Override
	public void onBackPressed()
	{
		if (this.act.getClass().getSimpleName().equals("ActivityMain"))
		{
			if (mDrawerLayout.isDrawerOpen(Gravity.START))
			{
				this.mDrawerLayout.closeDrawer(Gravity.LEFT);
			}
			else
			{
				//check if we have only one page in Left ViewPager
				//if so - we must show initial state of app
				//else - check if it's second time of pressing back
				ViewPager leftPager = (ViewPager) this.act.findViewById(R.id.pager_left);
				if (leftPager.getAdapter().getCount() == 1)
				{
					this.act.finish();
					Intent intent = new Intent(this.act, ActivityMain.class);
					this.act.startActivity(intent);
				}
				else
				{
					//else - check if it's second time of pressing back
					if (this.backPressedQ == 1)
					{
						this.backPressedQ = 0;
						super.onBackPressed();
						this.finish();
					}
					else
					{
						this.backPressedQ++;
						Toast.makeText(this, "Нажмите ещё раз, чтобы выйти", Toast.LENGTH_SHORT).show();
					}
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
			if (this.getSupportFragmentManager().findFragmentByTag(FragmentComments.LOG) != null)
			{
				final Toolbar toolbar;
				toolbar = (Toolbar) act.findViewById(R.id.toolbar);
				//check if we have also article frag in manager and if not - show hamburger
				if (this.getSupportFragmentManager().findFragmentByTag(FragmentArticle.LOG) == null)
				{
					mDrawerToggle.setDrawerIndicatorEnabled(true);
					this.mDrawerToggle.syncState();
					//restore title of toolbar via calling to onPageSelected of pager's listener
					PagerListenerArticle.setTitleToToolbar(currentCategory, this, twoPane, this.getCurArtPosition());
				}
				else
				{
					//set toolbar title
					toolbar.setTitle("Статья");
				}
				//remove comments fragment
				Fragment artFrag = this.getSupportFragmentManager().findFragmentByTag(FragmentComments.LOG);
				this.getSupportFragmentManager().beginTransaction().remove(artFrag).commit();
				//show previously hided comments and share buttons				
				Menu menu = toolbar.getMenu();
				MenuItem comments = menu.findItem(R.id.comments);
				MenuItem share = menu.findItem(R.id.share);
				comments.setVisible(true);
				share.setVisible(true);

				return;
			}
			if (this.getSupportFragmentManager().findFragmentByTag(FragmentArticle.LOG) != null)
			{
				mDrawerToggle.setDrawerIndicatorEnabled(true);
				this.mDrawerToggle.syncState();

				Fragment artFrag = this.getSupportFragmentManager().findFragmentByTag(FragmentArticle.LOG);
				this.getSupportFragmentManager().beginTransaction().remove(artFrag).commit();
				//restore toolbars title
				PagerListenerArticle.setTitleToToolbar(currentCategory, this, twoPane, this.getCurArtPosition());
				return;
			}
			if (mDrawerLayout.isDrawerOpen(Gravity.START))
			{
				this.mDrawerLayout.closeDrawer(Gravity.LEFT);
				return;
			}
			else
			{
				super.onBackPressed();
			}
		}
	}

	public int getCurArtPosition()
	{
		return curArtPosition;
	}

	public void setCurArtPosition(int curArtPosition)
	{
		this.curArtPosition = curArtPosition;
	}

	/**
	 * 
	 * @return all authors from allAuthorsList variable or from DB if it's null
	 */
	public List<Author> getAllAuthorsList()
	{
		if (this.allAuthorsList == null)
		{
			DataBaseHelper h = new DataBaseHelper(this);
			try
			{
				this.allAuthorsList = (ArrayList<Author>) h.getDaoAuthor().queryBuilder()
				.orderBy(Author.NAME_FIELD_NAME, true).query();
			} catch (SQLException e)
			{
				e.printStackTrace();
			} finally
			{
				h.close();
			}
		}
		return allAuthorsList;
	}

	public List<Category> getAllCategoriesList()
	{
		if (this.allCategoriesList == null)
		{
			DataBaseHelper h = new DataBaseHelper(this);
			try
			{
				this.allCategoriesList = (ArrayList<Category>) h.getDaoCategory().queryBuilder()
				.orderBy(Category.TITLE_FIELD_NAME, true)
				.query();
			} catch (SQLException e)
			{
				e.printStackTrace();
			} finally
			{
				h.close();
			}
		}
		return allCategoriesList;
	}

	public String getSearchText()
	{
		return searchText;
	}

	public void setSearchText(String searchText)
	{
		this.searchText = searchText;
	}

	public ArrayList<String> getAllCatAndAutURLs()
	{
		if (this.allCatAndAutURLs == null)
		{
			DataBaseHelper h = new DataBaseHelper(this);
			allCatAndAutURLs = h.getAllCatAndAutUrls();
			h.close();
		}
		return allCatAndAutURLs;
	}

	public ArrayList<String> getAllCatAndAutTitles()
	{
		if (this.allCatAndAutTitles == null)
		{
			DataBaseHelper h = new DataBaseHelper(this);
			allCatAndAutTitles = h.getAllCatAndAutTitles();
			h.close();
		}
		return allCatAndAutTitles;
	}
}
