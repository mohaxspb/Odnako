package ru.kuchanov.odnako;

import java.util.ArrayList;
import java.util.List;
import ru.kuchanov.odnako.utils.AddAds;

import com.google.android.gms.ads.AdView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class CommentsActivityNew extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
	public static String EXTRA_MESSAGE_TO_MAIN = "extra_message_to_main";

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

	SharedPreferences pref;

	LinearLayout mainLay;
	PullToRefreshListView pullToRefreshView;

	Parcelable state;

	public static int CUR_PAGE_TO_LOAD = 1;

	public static String ART_COMM_LINK;

	public static String[][][] ALL_COMMS_ARR;
	public static ArrayList<CommentInfo> COMMS_INFO_ARR_LIST;
	public static int DEFAULT_COMMS_ON_PAGE = 20;
	public static int QUONTITY_OF_VALUES_IN_ONE_COMM_ARR = 12;
	public static int CURRENT_PAGE = 1;
	public static Integer QUONTITY_OF_COMM_PAGES;

	AdView adView;

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

	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("CommentsActiityNew onCreate");
		super.onCreate(savedInstanceState);

		if (CommentsActivityNew.EXTRA_MESSAGE_TO_MAIN == null)
		{
			CommentsActivityNew.EXTRA_MESSAGE_TO_MAIN = "extra_message_to_main";
		}

		//Задаёт дефолт настройки из xml и вешает листенер их изменения
		SharedPreferences pref;
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		if (pref.getString("theme", "dark").equals("dark"))
		{
			this.setTheme(R.style.Theme_AppCompat_Light_DarkActionBar);
			this.setContentView(R.layout.activity_comments_dark);
		}
		else
		{
			this.setTheme(R.style.Theme_AppCompat_Light);
			this.setContentView(R.layout.activity_comments_ligth);
		}

		setNavDraw();

		mainLay = (LinearLayout) findViewById(R.id.lin_lay_comm_main);

		if (pref.getString("theme", "dark").equals("dark"))
		{
			mainLay.setBackgroundColor(Color.BLACK);
		}
		else
		{
			mainLay.setBackgroundColor(Color.WHITE);
		}

		pullToRefreshView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);

		pullToRefreshView.getLoadingLayoutProxy().setPullLabel("Потяните для загрузки");
		pullToRefreshView.getLoadingLayoutProxy().setRefreshingLabel("Загружаю...");
		pullToRefreshView.getLoadingLayoutProxy().setReleaseLabel("Отпустите для загрузки");
		pullToRefreshView.setMode(Mode.PULL_FROM_END);
		/*	*/List<String> noDataList = new ArrayList<String>();
		/*	*/noDataList.add("");
		/*	*/ArrayAdapter<String> noDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, noDataList);
		/*	*/pullToRefreshView.setAdapter(noDataAdapter);
		pullToRefreshView.setOnRefreshListener(new OnRefreshListener<ListView>()
		{
			public void onRefresh(PullToRefreshBase<ListView> refreshView)
			{
				// Do work to refresh the list here.
				Parcelable state = pullToRefreshView.getRefreshableView().onSaveInstanceState();
				CommentsActivityNew.CUR_PAGE_TO_LOAD += CommentsActivityNew.CURRENT_PAGE;

				System.out.println("onRefresh " + CommentsActivityNew.CUR_PAGE_TO_LOAD);
				if (CommentsActivityNew.QUONTITY_OF_COMM_PAGES != null)
				{
					if (CommentsActivityNew.CUR_PAGE_TO_LOAD <= CommentsActivityNew.QUONTITY_OF_COMM_PAGES)
					{
						if (CommentsActivityNew.ALL_COMMS_ARR[CommentsActivityNew.CUR_PAGE_TO_LOAD - 1] == null)
						{
							CommentsActivityNew.this.loadComments(CommentsActivityNew.CUR_PAGE_TO_LOAD, false, state);
						}
						else
						{
							CommentsActivityNew.this.fillData(CommentsActivityNew.CUR_PAGE_TO_LOAD - 1, false);
							CommentsActivityNew.this.pullToRefreshView.onRefreshComplete();

							CommentsActivityNew.this.pullToRefreshView.getRefreshableView().onRestoreInstanceState(state);
							CommentsActivityNew.CUR_PAGE_TO_LOAD += 1;
						}
					}
					else
					{
						Toast.makeText(CommentsActivityNew.this, "Это последняя страница", Toast.LENGTH_SHORT).show();
						CommentsActivityNew.this.pullToRefreshView.onRefreshComplete();
						CommentsActivityNew.this.pullToRefreshView.getRefreshableView().onRestoreInstanceState(state);
						CommentsActivityNew.this.pullToRefreshView.onRefreshComplete();
						refreshView.onRefreshComplete();
					}
				}
				else
				{
					//firstLoad
					CommentsActivityNew.CUR_PAGE_TO_LOAD = 1;
					pullToRefreshView.setMode(Mode.PULL_FROM_START);
					CommentsActivityNew.this.loadComments(CommentsActivityNew.CUR_PAGE_TO_LOAD, false, state);

				}
			}
		});

		pullToRefreshView.setClickable(true);
		//////////
		//Check if there is no link or its new link
		if (CommentsActivityNew.ART_COMM_LINK == null)
		{
			Intent intent = getIntent();
			CommentsActivityNew.ART_COMM_LINK = intent.getStringExtra(ArticleActivity.EXTRA_MESSAGE);
		}
		else
		{
			Intent intent = getIntent();
			if (!CommentsActivityNew.ART_COMM_LINK.equals(intent.getStringExtra(ArticleActivity.EXTRA_MESSAGE)))
			{
				CommentsActivityNew.ART_COMM_LINK = intent.getStringExtra(ArticleActivity.EXTRA_MESSAGE);
				CommentsActivityNew.ALL_COMMS_ARR = null;
				CommentsActivityNew.COMMS_INFO_ARR_LIST = null;
				CommentsActivityNew.CURRENT_PAGE = 1;
				CommentsActivityNew.CUR_PAGE_TO_LOAD = 1;
				CommentsActivityNew.QUONTITY_OF_COMM_PAGES = null;
			}
		}
		System.out.println("ART_COMM_LINK " + CommentsActivityNew.ART_COMM_LINK);
		//Check if there is no link or its new link
		//////////

		//adMob
		adView = (AdView) this.findViewById(R.id.adView);
		AddAds addAds = new AddAds(this, this.adView);
//		addAds.addAd("adsOnComments"); old ver
		addAds.addAd();
		//end of adMob
	}

	//end of onCreate

	protected void onResume()
	{
		System.out.println("CommentsActiityNew onResume");
		super.onResume();

		//Check if there is some inf in all_comms_arr and load if no
		if (CommentsActivityNew.ALL_COMMS_ARR == null)
		{
			//loadComments(CommentsActivityNew.CURRENT_PAGE, false, null);
			//CommentsActivityNew.QUONTITY_OF_COMM_PAGES=0;
			pullToRefreshView.setRefreshing();
		}
		else
		{
			if (CommentsActivityNew.QUONTITY_OF_COMM_PAGES != null)
			{
				this.getSupportActionBar().setTitle("Комментарии(" + CommentsActivityNew.CURRENT_PAGE + "/" + CommentsActivityNew.QUONTITY_OF_COMM_PAGES + ")");
			}
			else
			{
				this.getSupportActionBar().setTitle("Комментарии");
			}
			CommentsListAdapter commListAdapter = new CommentsListAdapter(this, CommentsActivityNew.COMMS_INFO_ARR_LIST);
			this.pullToRefreshView.setAdapter(commListAdapter);
			if (state != null)
			{
				pullToRefreshView.getRefreshableView().onRestoreInstanceState(state);
			}

		}
		//Check if there is some inf in all_comms_arr and load if no
	}

	void loadComments(int pageToLoad, boolean fromArrow, Parcelable state)
	{
		ParseCommentsNew parse = new ParseCommentsNew();
		parse.setVars(this, this.pullToRefreshView, fromArrow, state, pageToLoad);

		String link = CommentsActivityNew.ART_COMM_LINK + "comments/" + "page-" + pageToLoad + "/";
		parse.execute(link);
	}

	void fillData(Integer curPage, boolean fromArrow)
	{
		System.out.println("commsAct fillData start");

		if (fromArrow)
		{
			CommentsActivityNew.COMMS_INFO_ARR_LIST = new ArrayList<CommentInfo>();
		}
		for (int i = 0; i < CommentsActivityNew.ALL_COMMS_ARR[curPage].length; i++)
		{
			CommentsActivityNew.COMMS_INFO_ARR_LIST.add(new CommentInfo(CommentsActivityNew.ALL_COMMS_ARR[curPage][i]));
		}

		System.out.println("commsAct fillData end");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		//getMenuInflater().inflate(R.menu.comments_menu_dark, menu);
		SharedPreferences pref;
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		if (pref.getString("theme", "dark").equals("dark"))
		{
			getMenuInflater().inflate(R.menu.comments_menu_dark, menu);
		}
		else
		{
			getMenuInflater().inflate(R.menu.comments_menu_ligth, menu);
		}
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				return super.onOptionsItemSelected(item);
			case R.id.next_comments:
				nextCommPage();
				return true;
			case R.id.prev_comments:
				prevCommPage();
				return true;
			case R.id.action_settings:
				item.setIntent(new Intent(this, PrefActivity.class));
				return super.onOptionsItemSelected(item);
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	protected void nextCommPage()
	{
		System.out.println("nextCommPage");
		if (CommentsActivityNew.QUONTITY_OF_COMM_PAGES != null)
		{
			if (CommentsActivityNew.CURRENT_PAGE < CommentsActivityNew.QUONTITY_OF_COMM_PAGES)
			{
				CommentsActivityNew.CURRENT_PAGE += 1;
				CommentsActivityNew.CUR_PAGE_TO_LOAD = 1;
				System.out.println("nextCommPage " + CommentsActivityNew.ALL_COMMS_ARR[CommentsActivityNew.CURRENT_PAGE - 1] == null);
				if (CommentsActivityNew.ALL_COMMS_ARR[CommentsActivityNew.CURRENT_PAGE - 1] == null)
				{
					System.out.println("nextCommPage false");
					Parcelable stateFromNext = this.pullToRefreshView.getRefreshableView().onSaveInstanceState();
					this.loadComments(CommentsActivityNew.CURRENT_PAGE, true, stateFromNext);
				}
				else
				{
					System.out.println("nextCommPage true");
					this.getSupportActionBar().setTitle("Комментарии(" + CommentsActivityNew.CURRENT_PAGE + "/" + CommentsActivityNew.QUONTITY_OF_COMM_PAGES + ")");
					this.fillData(CommentsActivityNew.CURRENT_PAGE - 1, true);
					CommentsListAdapter commListAdapter = new CommentsListAdapter(this, CommentsActivityNew.COMMS_INFO_ARR_LIST);
					this.pullToRefreshView.setAdapter(commListAdapter);
				}
			}
			else
			{
				Toast.makeText(this, "Это последняя страница", Toast.LENGTH_SHORT).show();
			}
		}
	}

	protected void prevCommPage()
	{
		System.out.println("prevCommPage");
		if (CommentsActivityNew.CURRENT_PAGE > 1)
		{
			CommentsActivityNew.CURRENT_PAGE -= 1;
			CommentsActivityNew.CUR_PAGE_TO_LOAD = 1;
			this.getSupportActionBar().setTitle("Комментарии(" + CommentsActivityNew.CURRENT_PAGE + "/" + CommentsActivityNew.QUONTITY_OF_COMM_PAGES + ")");
			this.fillData(CommentsActivityNew.CURRENT_PAGE - 1, true);
			CommentsListAdapter commListAdapter = new CommentsListAdapter(this, CommentsActivityNew.COMMS_INFO_ARR_LIST);

			//this.commList.setAdapter(commListAdapter);
			this.pullToRefreshView.setAdapter(commListAdapter);
		}
		else
		{
			Toast.makeText(this, "Это первая страница", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		Toast.makeText(getApplicationContext(), "Настройки изменены", Toast.LENGTH_SHORT).show();
	}

	/* Called whenever we call supportInvalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		menu.findItem(R.id.next_comments).setVisible(!drawerOpen);
		menu.findItem(R.id.prev_comments).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void setTitle(CharSequence title)
	{
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
		//mDrawerLayout.setFocusable(false);
	}

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

	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		System.out.println("CommentsActiityNew: onSave");

		state = this.pullToRefreshView.getRefreshableView().onSaveInstanceState();
		outState.putParcelable("listPos", state);
	}

	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		System.out.println("CommentsActiityNew onRestore");

		state = savedInstanceState.getParcelable("listPos");
	}

	protected void setNavDraw()
	{
		/////////TEST NAVDRAW

		//Задаёт дефолт настройки из xml и вешает листенер их изменения
		PreferenceManager.setDefaultValues(this, R.xml.pref, false);
		pref = PreferenceManager.getDefaultSharedPreferences(this);

		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
		//Задаёт дефолт настройки из xmlи вешает листенер их изменения

		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_comments);

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
		//Set on child click listener
		DrawerItemClickListenerNew drawerItemClickListenerNew = new DrawerItemClickListenerNew();
		drawerItemClickListenerNew.setVars(mDrawerLayout, mDrawerList, groups, groupsLinks, this);
		this.mDrawerList.setOnChildClickListener(drawerItemClickListenerNew);
		//Set on group click listener
		DrawerGroupClickListenerNew drawerGroupClickListenerNew = new DrawerGroupClickListenerNew();
		drawerGroupClickListenerNew.setVars(mDrawerLayout, mDrawerList, this);
		this.mDrawerList.setOnGroupClickListener(drawerGroupClickListenerNew);

		mDrawerList.expandGroup(1);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close)
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
		mDrawerToggle.setDrawerIndicatorEnabled(false);
	}
}
