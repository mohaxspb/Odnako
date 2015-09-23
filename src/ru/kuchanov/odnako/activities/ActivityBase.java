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
import java.util.Map;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.db.Category;
import ru.kuchanov.odnako.db.DataBaseHelper;
import ru.kuchanov.odnako.db.Favorites;
import ru.kuchanov.odnako.db.ServiceDB;
import ru.kuchanov.odnako.db.ServiceDB.LocalBinder;
import ru.kuchanov.odnako.lists_and_utils.DrawerGroupClickListener;
import ru.kuchanov.odnako.lists_and_utils.DrawerItemClickListener;
import ru.kuchanov.odnako.lists_and_utils.ExpListAdapter;
import ru.kuchanov.odnako.lists_and_utils.FillMenuList;
import ru.kuchanov.odnako.lists_and_utils.RecyclerAdapterDrawerRight;
import ru.kuchanov.odnako.utils.CheckTimeToAds;
import ru.kuchanov.odnako.utils.DipToPx;
import ru.kuchanov.odnako.utils.MyUIL;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

public class ActivityBase extends AppCompatActivity
{
	static final String LOG = ActivityBase.class.getSimpleName();
	protected ActivityBase act;
	protected boolean twoPane;
	protected SharedPreferences pref;

	//	AdView adView;
	protected CheckTimeToAds checkTimeAds;

	//Toolbar
	Toolbar toolbar;

	//drawer
	public DrawerLayout mDrawerLayout;
	protected ExpandableListView mDrawer;
	protected ExpListAdapter expAdapter;

	public RecyclerView drawerRightRecyclerView;
	public SwipeRefreshLayout drawerRightSwipeRefreshLayout;

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
	//TODO delete it and refactor the whole app!1!!!!!!!!1111
	public ArrayList<Article> curAllArtsInfo = null;

	protected int backPressedQ;

	protected Menu menu;
	private String searchText;

	boolean bound = false;
	ServiceDB serviceDB;

	public ServiceDB getServiceDB()
	{
		return this.serviceDB;
	}

	public ServiceConnection sConn = new ServiceConnection()
	{
		public void onServiceConnected(ComponentName name, IBinder binder)
		{
			Log.d(LOG, "onServiceConnected");
			bound = true;
			LocalBinder localBinder = (LocalBinder) binder;
			serviceDB = (ServiceDB) localBinder.getService();

			if (allCatArtsInfo == null)
			{
				allCatArtsInfo = new HashMap<>();
			}

			ArrayList<String> keySet = new ArrayList<String>(serviceDB.getAllCatArtsInfo().keySet());
			for (int i = 0; i < serviceDB.getAllCatArtsInfo().size(); i++)
			{
				if (serviceDB.getAllCatArtsInfo().get(keySet.get(i)) != null)
				{
					allCatArtsInfo.put(keySet.get(i), serviceDB.getAllCatArtsInfo().get(keySet.get(i)));
				}
			}

			//allCatArtsInfo = serviceDB.getAllCatArtsInfo();
		}

		public void onServiceDisconnected(ComponentName name)
		{
			Log.d(LOG, "onServiceDisconnected");
			bound = false;
			serviceDB = null;
		}
	};

	public void onDestroy()
	{
		Log.d(LOG, "onDestroy");
		if (bound)
		{
			this.act.unbindService(sConn);
			bound = false;
		}
		//		if (this.adView != null)
		//		{
		//			adView.destroy();
		//		}
		super.onDestroy();
	}

	public void bindService()
	{
		Intent intentBind = new Intent(this, ServiceDB.class);
		this.bindService(intentBind, sConn, BIND_AUTO_CREATE);
	}

	protected void AddAds()
	{
		//adMob
		//XXX remove now;
		//		adView = (AdView) this.findViewById(R.id.adView);
		//		AddAds addAds = new AddAds(this, this.adView);
		//		addAds.addAd();
		//		//end of adMob
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
		//		adView.pause();
		super.onPause();
	}

	//set Navigatin drawer
	protected void setNavDrawer()
	{
		//XXX
		this.drawerRightSwipeRefreshLayout = (SwipeRefreshLayout) this.act.findViewById(R.id.drawer_right);
		this.drawerRightRecyclerView = (RecyclerView) this.act.findViewById(R.id.drawer_right_recycler_view);

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
		DrawerLayout.LayoutParams drawerRightLayPar = (android.support.v4.widget.DrawerLayout.LayoutParams) this.drawerRightSwipeRefreshLayout
		.getLayoutParams();
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
		drawerRightLayPar.width = drawerWidth;
		this.drawerRightSwipeRefreshLayout.setLayoutParams(drawerRightLayPar);
		////end of set Drawer width

		// As we're using a Toolbar, we should retrieve it and set it
		// to be our ActionBar
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Now retrieve the DrawerLayout so that we can set the status bar color.
		// This only takes effect on Lollipop, or when using translucentStatusBar
		// on KitKat.
		//mDrawerLayout.setStatusBarBackgroundColor(Color.BLUE);

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

		//XXX
		this.drawerRightRecyclerView.setLayoutManager(new LinearLayoutManager(act));
		this.drawerRightRecyclerView.setAdapter(new RecyclerAdapterDrawerRight(act, null));
		int[] textSizeAttr = new int[] { ru.kuchanov.odnako.R.attr.colorPrimary };
		int indexOfAttrTextSize = 0;
		TypedValue typedValue = new TypedValue();
		TypedArray a = act.obtainStyledAttributes(typedValue.data, textSizeAttr);
		int colorPrimary = a.getColor(indexOfAttrTextSize, 0xFFFFFF);
		a.recycle();
		this.drawerRightSwipeRefreshLayout.setBackgroundColor(colorPrimary);
		this.drawerRightSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				Log.i(LOG, "DrawerRight onRefresh called");
				Favorites.showFavsToFromServerDialog(act);
			}
		});
		////End of drawer settings
	}

	private void addHeaderForDrawer()
	{
		View header = (View) this.getLayoutInflater().inflate(R.layout.drawer_header, this.mDrawer, false);
		ImageView ava = (ImageView) header.findViewById(R.id.ava_img);
		ImageLoader imgLoader = MyUIL.get(act);
		//		imgLoader.displayImage("http://www.odnako.org/i/75_75/users/7160/7160-1481-7160.jpg", ava,
		//		MyUIL.getTransparentBackgroundROUNDOptions(act));
		imgLoader
		.displayImage("drawable://" + R.drawable.dev_ava, ava, MyUIL.getTransparentBackgroundROUNDOptions(act));
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
				.orderBy(Author.FIELD_NAME, true).query();
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
				.orderBy(Category.FIELD_TITLE, true)
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

	/**
	 * search in allPrefs Map for text scale prefs, checks if their type is
	 * String, and if so removes it and add new pref with same key but with
	 * float type
	 */
	protected void reorganizeTextSizePreferences()
	{
		this.pref = (this.pref == null) ? PreferenceManager.getDefaultSharedPreferences(act) : this.pref;
		Map<String, ?> allPrefs = pref.getAll();
		ArrayList<String> textSizeKeys = new ArrayList<String>();
		ArrayList<String> textSizeValues = new ArrayList<String>();
		for (Map.Entry<String, ?> entry : allPrefs.entrySet())
		{
			if (ActivityPreference.PREF_KEY_SCALE_UI.equals(entry.getKey()) ||
			ActivityPreference.PREF_KEY_SCALE_ARTICLE.equals(entry.getKey()) ||
			ActivityPreference.PREF_KEY_SCALE_COMMENTS.equals(entry.getKey()))
			{
				if (entry.getValue() instanceof String)
				{
					// put keys and values to arrayList and after looping remove and put values to prevent modifiing this MAP;
					textSizeKeys.add(entry.getKey());
					textSizeValues.add((String) entry.getValue());
				}
			}
		}
		for (int i = 0; i < textSizeKeys.size(); i++)
		{
			String key = textSizeKeys.get(i);
			this.pref.edit().remove(key).commit();
			this.pref.edit().putFloat(key, Float.valueOf(textSizeValues.get(i))).commit();
		}
	}
}