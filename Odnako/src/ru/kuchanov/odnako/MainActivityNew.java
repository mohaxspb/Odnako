package ru.kuchanov.odnako;

import java.util.ArrayList;
import java.util.List;

import ru.kuchanov.odnako.download.Downloadings;
import ru.kuchanov.odnako.utils.AddAds;
import ru.kuchanov.odnako.utils.AppRater;
import ru.kuchanov.odnako.utils.CheckAlarmAndServiceAreRunning;
import ru.kuchanov.odnako.utils.EnableAdsDialog;
import ru.kuchanov.odnako.utils.NewVersionFeachersDialog;
import ru.kuchanov.odnako.utils.ShareApp;

import com.google.android.gms.ads.AdView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivityNew extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
	public static int DEFAULT_NUM_OF_ARTS_ON_PAGE = 30;
	public static int NUM_OF_ELEMS_IN_DIV = 5;

	public static int CURRENT_PAGE = 1;
	public static int CUR_PAGE_TO_LOAD = 1;

	public static String[] CUR_ART_INFO;
	public final static String EXTRA_MESSAGE = "extra_message";

	public static String[][][] ALL_ARTS_ARR;
	public static ArrayList<MainInfo> ARTS_INFO_ARR_LIST;
	//public static MainListAdapter MAIN_ADAPTER;
	public static MainListAdapterNew MAIN_ADAPTER;

	private DrawerLayout mDrawerLayout;
	private ExpandableListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	ArrayList<ArrayList<String>> groups = new ArrayList<ArrayList<String>>();
	ArrayList<String> children1 = new ArrayList<String>();
	ArrayList<String> children2 = new ArrayList<String>();
	ArrayList<String> children3 = new ArrayList<String>();
	ArrayList<String> children4 = new ArrayList<String>();

	ArrayList<ArrayList<String>> groupsLinks = new ArrayList<ArrayList<String>>();
	ArrayList<String> children1Links = new ArrayList<String>();
	ArrayList<String> children2Links = new ArrayList<String>();

	LinearLayout mainLay;
	public PullToRefreshListView pullToRefreshView;

	SharedPreferences pref;
	Boolean refresh;

	int backPressedQ = 0;

	Parcelable state;

	public static String CATEGORY_TO_LOAD = "www.odnako.org/blogs";

	AdView adView;

	//static String app_ver = "";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("MainActivityNew onCreate");
		super.onCreate(savedInstanceState);

		//get default settings to get all settings later
		PreferenceManager.setDefaultValues(this, R.xml.pref, true);
		//end of get default settings to get all settings later

		//Set unreaded num of arts to zero
		//it's for new arts motification
		SharedPreferences prefsNumOfArts = this.getSharedPreferences("saveNumOfUnReadedArts", 0);
		SharedPreferences.Editor editor = prefsNumOfArts.edit();
		editor.putInt("quontityOfUnreadedArts", 0);
		editor.commit();
		//end of Set unreaded num of arts to zero

		//Start checkNewService if it isn't started yet && Check if autoload alarm is seted
		CheckAlarmAndServiceAreRunning.checkAndRun(this);
		//END OF Start checkNewService if it isn't started yet && Check if autoload alarm is seted

		//Задаёт дефолт настройки из xml и вешает листенер их изменения
		PreferenceManager.setDefaultValues(this, R.xml.pref, false);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
		this.refresh = pref.getBoolean("refresh", false);

		//set theme (ligth or dark)
		this.setAppearence();
		//set Navigation Drawer sliding menu
		setNavDraw();
		//end of Navigation Drawer sliding menu

		mainLay = (LinearLayout) findViewById(R.id.main_new_lin);
		pullToRefreshView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview_main);
		pullToRefreshView.setClickable(true);
		pullToRefreshView.getRefreshableView().setTextFilterEnabled(true);
		pullToRefreshView.setMode(Mode.BOTH);
		pullToRefreshView.getLoadingLayoutProxy().setPullLabel("Потяните для загрузки");
		pullToRefreshView.getLoadingLayoutProxy().setRefreshingLabel("Загружаю...");
		pullToRefreshView.getLoadingLayoutProxy().setReleaseLabel("Отпустите для загрузки");
		///refreshing animation on first load, while adapter isn't seted
		/*	*/List<String> noDataList = new ArrayList<String>();
		/*	*/noDataList.add("");
		/*	*/ArrayAdapter noDataAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, noDataList);
		/*	*/pullToRefreshView.setAdapter(noDataAdapter);
		///end
		pullToRefreshView.setOnRefreshListener(new OnRefreshListener2()
		{
			public void onPullDownToRefresh(PullToRefreshBase refreshView)
			{
				System.out.println("MainActivityNew onPullDownToRefresh");
				refresh(MainActivityNew.CATEGORY_TO_LOAD, 1, true, false);
			}

			public void onPullUpToRefresh(PullToRefreshBase refreshView)
			{
				System.out.println("MainActivityNew onPullUpToRefresh");
				MainActivityNew.CUR_PAGE_TO_LOAD += 1;
				refresh(MainActivityNew.CATEGORY_TO_LOAD, MainActivityNew.CUR_PAGE_TO_LOAD, false, true);
			}
		});
		/////launch from navDraw from another activity
		Intent intent = getIntent();
		if (intent.hasExtra(ArticleActivity.EXTRA_MESSAGE_TO_MAIN))
		{
			System.out.println("MainActivityNew ArticleActivity.EXTRA_MESSAGE_TO_MAIN");
			MainActivityNew.CATEGORY_TO_LOAD = intent.getStringArrayExtra(ArticleActivity.EXTRA_MESSAGE_TO_MAIN)[0];
			this.getSupportActionBar().setTitle(intent.getStringArrayExtra(ArticleActivity.EXTRA_MESSAGE_TO_MAIN)[1]);
			this.mTitle = intent.getStringArrayExtra(ArticleActivity.EXTRA_MESSAGE_TO_MAIN)[1];
			MainActivityNew.ALL_ARTS_ARR = null;
			MainActivityNew.ARTS_INFO_ARR_LIST = null;
			MainActivityNew.MAIN_ADAPTER = null;
			MainActivityNew.CURRENT_PAGE = 1;
			MainActivityNew.CUR_PAGE_TO_LOAD = 1;

			CommentsActivityNew.EXTRA_MESSAGE_TO_MAIN = null;
			ArticleActivity.EXTRA_MESSAGE_TO_MAIN = null;
		}
		else if (intent.hasExtra(CommentsActivityNew.EXTRA_MESSAGE_TO_MAIN))
		{
			System.out.println("MainActivityNew CommentsActivityNew.EXTRA_MESSAGE_TO_MAIN");
			MainActivityNew.CATEGORY_TO_LOAD = intent.getStringArrayExtra(CommentsActivityNew.EXTRA_MESSAGE_TO_MAIN)[0];
			this.getSupportActionBar().setTitle(intent.getStringArrayExtra(CommentsActivityNew.EXTRA_MESSAGE_TO_MAIN)[1]);
			this.mTitle = intent.getStringArrayExtra(CommentsActivityNew.EXTRA_MESSAGE_TO_MAIN)[1];
			MainActivityNew.ALL_ARTS_ARR = null;
			MainActivityNew.ARTS_INFO_ARR_LIST = null;
			MainActivityNew.MAIN_ADAPTER = null;
			MainActivityNew.CURRENT_PAGE = 1;
			MainActivityNew.CUR_PAGE_TO_LOAD = 1;

			CommentsActivityNew.EXTRA_MESSAGE_TO_MAIN = null;
			ArticleActivity.EXTRA_MESSAGE_TO_MAIN = null;

		}
		/////end of launch from another activity

		//Ads option in this version
		EnableAdsDialog.app_launched(this);
		//end of Ads option in this version
		//// RATE
		AppRater.app_launched(this);
		//SHARE
		ShareApp.app_launched(this);
		//NewVersion
		NewVersionFeachersDialog.app_launched(this);

		//adMob
		adView = (AdView) this.findViewById(R.id.adView);
		AddAds addAds = new AddAds(this, this.adView);
//		addAds.addAd("adsOnMain"); old ver
		addAds.addAd();
		//end of adMob
	}

	protected void onResume()
	{
		System.out.println("MainActivityNew onResume");
		super.onResume();
		adView.resume();

		this.getSupportActionBar().setTitle(this.mTitle);

		if (MainActivityNew.ALL_ARTS_ARR != null)
		{
			this.pullToRefreshView.setAdapter(MainActivityNew.MAIN_ADAPTER);
			if (state != null)
			{
				this.pullToRefreshView.getRefreshableView().onRestoreInstanceState(state);
			}
		}
		else
		{
			if (this.refresh)
			{
				pullToRefreshView.setMode(Mode.PULL_FROM_START);
				pullToRefreshView.setRefreshing();
			}
			else
			{
				//check if there was call from another activity and if so start to load from web, not from cache
				Intent intent = getIntent();
				if (intent.hasExtra("extra_message_to_main"))
				{
					pullToRefreshView.setRefreshing();
				}
				else
				{
					System.out.println("intent has no extra message");
					refresh(MainActivityNew.CATEGORY_TO_LOAD, 0, true, false);
				}
			}
		}
	}

	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		System.out.println("MainActivityNew: onSave");

		state = this.pullToRefreshView.getRefreshableView().onSaveInstanceState();
		outState.putParcelable("listPos", state);

		outState.putString("mTitle", this.mTitle.toString());
		outState.putString("mDrawerTitle", this.mDrawerTitle.toString());

		outState.putBoolean("refresh", this.refresh);
	}

	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		System.out.println("MainActivityNew onRestore");

		state = savedInstanceState.getParcelable("listPos");

		this.mTitle = savedInstanceState.getString("mTitle");
		this.mDrawerTitle = savedInstanceState.getString("mDrawerTitle");

		this.refresh = savedInstanceState.getBoolean("refresh");
	}

	/* Called whenever we call supportInvalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		System.out.println("onPrepareOptionsMenu");
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		menu.findItem(R.id.refresh).setVisible(!drawerOpen);
		try
		{
			menu.findItem(1).setVisible(!drawerOpen);
		} catch (Exception e)
		{

		}
		return super.onPrepareOptionsMenu(menu);
	}

	/* Called whenever we call supportInvalidateOptionsMenu() */

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		System.out.println("onCreateOptionsMenu");
		if (pref.getString("theme", "dark").equals("dark"))
		{
			getMenuInflater().inflate(R.menu.main_dark, menu);
		}
		else
		{
			getMenuInflater().inflate(R.menu.main_ligth, menu);
		}

		//test search bar
		//Create the search view
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		{
			//final Menu menuFinal=menu;
			final SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
			searchView.setQueryHint("Поиск");

			int searchIconThemeDependsId;
			if (pref.getString("theme", "dark").equals("dark"))
			{
				searchIconThemeDependsId = R.drawable.ic_action_search_dark;
			}
			else
			{
				searchIconThemeDependsId = R.drawable.ic_action_search_light;
			}

			menu.add(Menu.NONE, 1, 1, "Поиск").setOnActionExpandListener(new OnActionExpandListener()
			{

				@Override
				public boolean onMenuItemActionExpand(MenuItem item)
				{
					System.out.println("onMenuItemActionExpand");
					pullToRefreshView.getRefreshableView().clearTextFilter();
					return true;
				}

				@Override
				public boolean onMenuItemActionCollapse(MenuItem item)
				{
					System.out.println("onMenuItemActionCollapse");
					//pullToRefreshView.getRefreshableView().clearTextFilter();
					return true;
				}
			}).setIcon(searchIconThemeDependsId).setActionView(searchView).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

			searchView.setOnQueryTextListener(new OnQueryTextListener()
			{
				@Override
				public boolean onQueryTextChange(String newText)
				{
					System.out.println("onQueryTextChange");
					if (TextUtils.isEmpty(newText))
					{
						pullToRefreshView.getRefreshableView().clearTextFilter();
					}
					else
					{
						pullToRefreshView.getRefreshableView().setFilterText(newText);
					}
					return true;
				}

				@Override
				public boolean onQueryTextSubmit(String query)
				{
					System.out.println("onQueryTextSubmit");
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

					return false;
				}
			});
		}

		////end search bar
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item))
		{
			return super.onOptionsItemSelected(item);
		}

		SharedPreferences prefForScale = PreferenceManager.getDefaultSharedPreferences(this);
		switch (item.getItemId())
		{
			case R.id.refresh:
				pullToRefreshView.setMode(Mode.PULL_FROM_START);
				pullToRefreshView.setRefreshing();
				return true;
			case R.id.action_settings:
				item.setIntent(new Intent(this, PrefActivity.class));
				return super.onOptionsItemSelected(item);
			case R.id.theme:
				//test sorting
				//pullToRefreshView.setAdapter(MAIN_ADAPTER.sortData());
				////end test sorting
				MenuItem ligthThemeMenuItem = item.getSubMenu().findItem(R.id.theme_ligth);
				MenuItem darkThemeMenuItem = item.getSubMenu().findItem(R.id.theme_dark);
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
				String curTheme = pref.getString("theme", "ligth");
				System.out.println(curTheme);
				if (curTheme.equals("ligth"))
				{
					ligthThemeMenuItem.setChecked(true);
				}
				else
				{
					darkThemeMenuItem.setChecked(true);
				}
				return true;
			case R.id.theme_ligth:
				SharedPreferences prefLigth = PreferenceManager.getDefaultSharedPreferences(this);
				prefLigth.edit().putString("theme", "ligth").commit();
				this.myRecreate();
				return true;
			case R.id.theme_dark:
				SharedPreferences prefDark = PreferenceManager.getDefaultSharedPreferences(this);
				prefDark.edit().putString("theme", "dark").commit();
				this.myRecreate();
				return true;
			case R.id.arts_list_size:
				MenuItem artsListItem = item.getSubMenu().findItem(R.id.artslist_05);
				MenuItem artsListaItem = item.getSubMenu().findItem(R.id.artslist_075);
				MenuItem artsListbItem = item.getSubMenu().findItem(R.id.artslist_1);
				MenuItem artsListcItem = item.getSubMenu().findItem(R.id.artslist_125);
				MenuItem artsListdItem = item.getSubMenu().findItem(R.id.artslist_15);
				MenuItem artsListeItem = item.getSubMenu().findItem(R.id.artslist_175);
				MenuItem artsListgItem = item.getSubMenu().findItem(R.id.artslist_2);

				SharedPreferences prefArtsList = PreferenceManager.getDefaultSharedPreferences(this);
				String curArtsListSize = prefArtsList.getString("scale", "1");
				System.out.println(curArtsListSize);
				if (curArtsListSize.equals("0.5"))
				{
					artsListItem.setChecked(true);
				}
				else if (curArtsListSize.equals("0.75"))
				{
					artsListaItem.setChecked(true);
				}
				else if (curArtsListSize.equals("1"))
				{
					artsListbItem.setChecked(true);
				}
				else if (curArtsListSize.equals("1.25"))
				{
					artsListcItem.setChecked(true);
				}
				else if (curArtsListSize.equals("1.5"))
				{
					artsListdItem.setChecked(true);
				}
				else if (curArtsListSize.equals("1.75"))
				{
					artsListeItem.setChecked(true);
				}
				else if (curArtsListSize.equals("2"))
				{
					artsListgItem.setChecked(true);
				}
				return true;
			case R.id.artslist_05:
				prefForScale.edit().putString("scale", "0.5").commit();
				try	{MainActivityNew.MAIN_ADAPTER.notifyDataSetChanged();}
				catch(Exception e){};
				return true;
			case R.id.artslist_075:
				prefForScale.edit().putString("scale", "0.75").commit();
				try	{MainActivityNew.MAIN_ADAPTER.notifyDataSetChanged();}
				catch(Exception e){};
				return true;
			case R.id.artslist_1:
				prefForScale.edit().putString("scale", "1").commit();
				try	{MainActivityNew.MAIN_ADAPTER.notifyDataSetChanged();}
				catch(Exception e){};
				return true;
			case R.id.artslist_125:
				prefForScale.edit().putString("scale", "1.25").commit();
				try	{MainActivityNew.MAIN_ADAPTER.notifyDataSetChanged();}
				catch(Exception e){};
				return true;
			case R.id.artslist_15:
				prefForScale.edit().putString("scale", "1.5").commit();
				try	{MainActivityNew.MAIN_ADAPTER.notifyDataSetChanged();}
				catch(Exception e){};
				return true;
			case R.id.artslist_175:
				prefForScale.edit().putString("scale", "1.75").commit();
				try	{MainActivityNew.MAIN_ADAPTER.notifyDataSetChanged();}
				catch(Exception e){};
				return true;
			case R.id.artslist_2:
				prefForScale.edit().putString("scale", "2").commit();
				try	{MainActivityNew.MAIN_ADAPTER.notifyDataSetChanged();}
				catch(Exception e){};
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void refresh(String catToLoad, int pageToLoad, boolean firstLoad, boolean fromBottomPull)
	{
		LockOrientation lock = new LockOrientation(this);
		lock.lock();

		if (firstLoad)
		{
			MainActivityNew.CUR_PAGE_TO_LOAD = 1;
			MainActivityNew.ALL_ARTS_ARR = null;
			MainActivityNew.ARTS_INFO_ARR_LIST = null;
			MainActivityNew.MAIN_ADAPTER = null;

		}
		//catToLoad="leontev.odnako.org";
		System.out.println("catToLoad: " + catToLoad);
		ParseBlogsPage parse = new ParseBlogsPage(this, catToLoad, pageToLoad, this.pullToRefreshView, fromBottomPull);
		parse.execute();

	}

	protected void setNavDraw()
	{
		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_new);

		// Находим наш list 
		mDrawerList = (ExpandableListView) findViewById(R.id.exListViewNew);
		mDrawerList.setCacheColorHint(0);

		//Создаем набор данных для адаптера
		FillMenuList fillMenuList = new FillMenuList();
		fillMenuList.setActivity(this);
		this.groups = fillMenuList.getGroups();
		this.groupsLinks = fillMenuList.getGroupsLinks();
		//Создаем адаптер и передаем context и список с данными
		ExpListAdapter adapter = new ExpListAdapter(getApplicationContext(), groups);
		mDrawerList.setAdapter(adapter);
		this.mDrawerList.setOnChildClickListener(new DrawerItemClickListener());
		this.mDrawerList.setOnGroupClickListener(new DrawerGroupClickListener());
		mDrawerList.expandGroup(1);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		)
		{
			public void onDrawerClosed(View view)
			{
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView)
			{
				getSupportActionBar().setTitle(mDrawerTitle);
				supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	/* The click listener for ListView in the navigation drawer */

	private class DrawerItemClickListener implements ExpandableListView.OnChildClickListener
	{
		@Override
		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
		{
			//set pullToRefreshView MODE to fromStart
			pullToRefreshView.setMode(Mode.PULL_FROM_START);
			//end of set pullToRefreshView MODE to fromStart
			mDrawerList.setItemChecked(childPosition, true);
			setTitle(groups.get(groupPosition).get(childPosition));
			MainActivityNew.CATEGORY_TO_LOAD = groupsLinks.get(groupPosition).get(childPosition);
			mDrawerLayout.closeDrawer(mDrawerList);
			pullToRefreshView.setRefreshing();
			return true;
		}
	};

	private class DrawerGroupClickListener implements ExpandableListView.OnGroupClickListener
	{
		@Override
		public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
		{
			//set pullToRefreshView MODE to fromStart
			pullToRefreshView.setMode(Mode.PULL_FROM_START);
			if (groupPosition == 2)
			{
				MainActivityNew.CATEGORY_TO_LOAD = "www.odnako.org/blogs";
				mDrawerList.setItemChecked(groupPosition, true);
				setTitle("Лента обновлений");
				mDrawerLayout.closeDrawer(mDrawerList);
				pullToRefreshView.setRefreshing();
			}
			else if (groupPosition == 3)
			{
				MainActivityNew.CATEGORY_TO_LOAD = "novosti.odnako.org";
				mDrawerList.setItemChecked(groupPosition, true);
				setTitle("Новости");
				mDrawerLayout.closeDrawer(mDrawerList);
				pullToRefreshView.setRefreshing();
			}
			else if (groupPosition == 4)
			{
				mDrawerList.setItemChecked(groupPosition, true);
				mDrawerLayout.closeDrawer(mDrawerList);
				Intent intentToMain = new Intent(MainActivityNew.this, Downloadings.class);
				MainActivityNew.this.startActivity(intentToMain);
			}
			else if (groupPosition == 5)
			{
				mDrawerList.setItemChecked(groupPosition, true);
				mDrawerLayout.closeDrawer(mDrawerList);
				Intent intentToMain = new Intent(MainActivityNew.this, PrefActivity.class);
				MainActivityNew.this.startActivity(intentToMain);
			}
			else
			{
				if (parent.isGroupExpanded(groupPosition))
				{
					parent.collapseGroup(groupPosition);
				}
				else
				{
					parent.expandGroup(groupPosition);
				}
			}
			return true;
		}
	};

	@Override
	public void setTitle(CharSequence title)
	{
		mTitle = title;
		//MainActivityNew.TITLE = String.valueOf(title);

		//getSupportActionBar().setTitle(MainActivityNew.TITLE);
		getSupportActionBar().setTitle(mTitle);
		//mDrawerLayout.setFocusable(false);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1)
	{
		//Toast.makeText(getApplicationContext(), "Настройки изменены", Toast.LENGTH_SHORT).show();
		refresh = pref.getBoolean("refresh", false);
	}

	protected void setAppearence()
	{
		if (pref.getString("theme", "dark").equals("dark"))
		{
			this.setTheme(R.style.Theme_AppCompat_Light_DarkActionBar);
			this.setContentView(R.layout.activity_main_dark);
		}
		else
		{
			this.setTheme(R.style.Theme_AppCompat_Light);
			this.setContentView(R.layout.activity_main_ligth);
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
	public void onBackPressed()
	{
		//super.onBackPressed();
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

}
