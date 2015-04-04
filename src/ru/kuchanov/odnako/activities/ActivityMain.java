/*
 19.10.2014
ActivityMain.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Locale;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.animations.RotationPageTransformer;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.db.Category;
import ru.kuchanov.odnako.db.DataBaseHelper;
import ru.kuchanov.odnako.fragments.FragmentArticle;
import ru.kuchanov.odnako.fragments.FragmentComments;
import ru.kuchanov.odnako.lists_and_utils.PagerAdapterAllCategories;
import ru.kuchanov.odnako.lists_and_utils.PagerAdapterArtsLists;
import ru.kuchanov.odnako.lists_and_utils.CatData;
import ru.kuchanov.odnako.lists_and_utils.PagerAdapterAllAuthors;
import ru.kuchanov.odnako.lists_and_utils.PagerListenerAllAuthors;
import ru.kuchanov.odnako.lists_and_utils.PagerAdapterSingleCategory;
import ru.kuchanov.odnako.lists_and_utils.PagerListenerAllCategories;
import ru.kuchanov.odnako.lists_and_utils.PagerListenerSingleCategory;
import ru.kuchanov.odnako.utils.DipToPx;
import ru.kuchanov.odnako.lists_and_utils.PagerListenerMenu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class ActivityMain extends ActivityBase
{
	final private static String LOG = ActivityMain.class.getSimpleName();

	public final static int PAGER_TYPE_MENU = 0;
	public final static int PAGER_TYPE_AUTHORS = 1;
	public final static int PAGER_TYPE_CATEGORIES = 2;
	public final static int PAGER_TYPE_SINGLE = 3;

	public final static String KEY_PAGER_TYPE = "pager type key";
	private int pagerType = PAGER_TYPE_MENU;

	//ViewPager and it's adapter for articles/comments
	ViewPager artCommsPager;
	PagerAdapter pagerAdapter;

	//ViewPager and it's adapter for artsLists
	ViewPager artsListPager;

	Toolbar toolbarRight;

	//curent displayed info
	//AllArtsList Arrays for author's and categories links
	//HashMap<String, ArrayList<ArtInfo>> allCatArtsInfo;

	//curent cat position
	//we also have int[2] with group/child position for expListIn navDrawer
	//known as "groupChildPosition"
	//	int curentCategoryPosition = 11;

	//curent article position for curent artsListPosition
	//int curArtPosition
	//ArrayList<ArtInfo> curAllArtsInfo=allCatArtsInfo.get(
	//cur ArtInfo =curAllArtsInfo.get(curArtPosition)

	//int array in hashMap to store top img and toolbar Ycoord for each category
	//we'll change it at runtime from fragment and restore it and get it from activity
	private HashMap<String, int[]> allCatToolbarTopImgYCoord;

	//int in hashMap to store SelectedArtPosition
	//we'll change it at runtime selecting artsCards and restore it and get it from activity
	//def value is zero for all
	/**
	 * HashMap for storing position of selected (via right pager or clicking on
	 * item) item in list. Only in twoPane mode.
	 */
	private HashMap<String, Integer> allCatListsSelectedArtPosition;
	private final static String KEY_ALL_SELECTED_POSITIONS = "allCatListsSelectedArtPosition";

	private String queryToSave;
	private boolean isKeyboardOpened = false;
	private final static String KEY_IS_KEYBOARD_OPENED = "isKeyboardOpened";

	protected void onCreate(Bundle savedInstanceState)
	{
		Log.e(LOG, "ActivityMain onCreate called");
		this.act = this;

		//get default settings to get all settings later
		PreferenceManager.setDefaultValues(this, R.xml.pref, true);
		this.pref = PreferenceManager.getDefaultSharedPreferences(this);
		//end of get default settings to get all settings later

		this.twoPane = this.pref.getBoolean("twoPane", false);

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
		/////
		//check intents extras and set pagerType and other params
		if (this.getIntent().hasExtra(KEY_PAGER_TYPE))
		{
			this.pagerType = this.getIntent().getIntExtra(KEY_PAGER_TYPE, PAGER_TYPE_MENU);
			//here we can switch between pagerTypes and set curentCategoryPosition for AuthorsPager and so on
			switch (this.pagerType)
			{
				case PAGER_TYPE_MENU:
					int[] groupChild = this.getIntent().getIntArrayExtra(KEY_GROUP_CHILD_POSITION);
					this.setCurentCategoryPosition(this.getCurentPositionByGroupChildPosition(groupChild[0],
					groupChild[1]));
				break;
				case PAGER_TYPE_AUTHORS:
					this.setCurentCategoryPosition(this.getIntent().getIntExtra(KEY_CURRENT_CATEGORY_POSITION, 0));
				break;
				case PAGER_TYPE_SINGLE:
					this.setCurrentCategory(this.getIntent().getStringExtra(KEY_CURRENT_CATEGORY));
				break;
				case PAGER_TYPE_CATEGORIES:
					this.setCurentCategoryPosition(this.getIntent().getIntExtra(KEY_CURRENT_CATEGORY_POSITION, 0));
				break;
			}
			//set savedInstanceState to null to prevent restoring previous state
			savedInstanceState = null;
		}

		if (savedInstanceState != null)
		{
			this.setCurArtPosition(savedInstanceState.getInt("position"));
			this.curAllArtsInfo = savedInstanceState.getParcelableArrayList(Article.KEY_ALL_ART_INFO);
			this.currentCategoryPosition = savedInstanceState.getInt("curentCategoryPosition");
			this.setCurrentCategory(savedInstanceState.getString("currentCategory"));
			this.pagerType = savedInstanceState.getInt(KEY_PAGER_TYPE);

			this.allAuthorsList = savedInstanceState.getParcelableArrayList(KEY_ALL_AUTHORS_LIST);
			this.allCategoriesList = savedInstanceState.getParcelableArrayList(KEY_ALL_CATEGORIES_LIST);
			this.allCatAndAutURLs = savedInstanceState.getStringArrayList(KEY_ALL_CAT_AND_AUT_URLS_LIST);

			this.restoreGroupChildPosition(savedInstanceState);
			this.restoreAllCatToolbartopImgYCoord(savedInstanceState);
			this.restoreAllCatArtsInfo(savedInstanceState);
			this.restoreAllCatListsSelectedArtPosition(savedInstanceState);

			if (savedInstanceState.containsKey("searchText"))
			{
				setSearchText(savedInstanceState.getString("searchText"));
			}
			this.isKeyboardOpened = savedInstanceState.getBoolean(KEY_IS_KEYBOARD_OPENED);
		}

		//get artsInfo data from DB
		if (this.allCatArtsInfo == null)
		{
			this.allCatArtsInfo = new HashMap<String, ArrayList<Article>>();
		}
		//get all Authors and categories and their urls from DB if they are null
		this.getAllAuthorsList();
		this.getAllCategoriesList();
		this.getAllCatAndAutURLs();

		//set selected pos for all cats if they are null (first launch without any state)
		if (this.allCatListsSelectedArtPosition == null)
		{
			this.allCatListsSelectedArtPosition = new HashMap<String, Integer>();
			for (int i = 0; i < this.getAllCatAndAutURLs().size(); i++)
			{
				this.allCatListsSelectedArtPosition.put(this.getAllCatAndAutURLs().get(i), 0);
			}
			//also we must add allAuthors(3) & allCategories(13)
			String[] allMenuLinks = CatData.getMenuLinks(act);
			this.allCatListsSelectedArtPosition.put(allMenuLinks[3], 0);
			this.allCatListsSelectedArtPosition.put(allMenuLinks[13], 0);
		}

		//set coords of topImg and toolbar if they are null (first launch without any state)
		if (allCatToolbarTopImgYCoord == null)
		{
			setAllCatToolbarTopImgYCoord(new HashMap<String, int[]>());
			String[] allCategoriesMenuLinks = CatData.getMenuLinks(act);
			for (int i = 0; i < allCategoriesMenuLinks.length; i++)
			{
				getAllCatToolbarTopImgYCoord().put(allCategoriesMenuLinks[i],
				new int[] { 0, 0, (int) DipToPx.convert(165 - 56, act), (int) DipToPx.convert(165 - 56, act) });
			}
		}
		//setLayout
		if (this.twoPane)
		{
			this.setContentView(R.layout.activity_main_large);
		}
		else
		{
			this.setContentView(R.layout.activity_main);
		}
		/////////////
		////////find all views
		this.toolbar = (Toolbar) this.findViewById(R.id.toolbar);
		this.setSupportActionBar(toolbar);

		this.artsListPager = (ViewPager) this.findViewById(R.id.pager_left);
		if (this.twoPane)
		{
			this.artCommsPager = (ViewPager) this.findViewById(R.id.pager_right);
			this.toolbarRight = (Toolbar) this.findViewById(R.id.toolbar_right);
		}

		/////////////////////////////
		//setNavDraw
		this.setNavDrawer();
		//End of setNavDraw

		//set arts lists viewPager
		Log.e(LOG, "pagerType: " + this.getPagerType());
		OnPageChangeListener listener = new PagerListenerMenu(this);
		switch (this.getPagerType())
		{
			case PAGER_TYPE_MENU:
				PagerAdapterArtsLists artsListPagerAdapter = new PagerAdapterArtsLists(
				this.getSupportFragmentManager(), act);
				this.artsListPager.setAdapter(artsListPagerAdapter);
				listener = new PagerListenerMenu(this);
				this.artsListPager.setOnPageChangeListener(listener);
				this.artsListPager.setCurrentItem(this.currentCategoryPosition);
			break;
			case PAGER_TYPE_AUTHORS:
				PagerAdapterAllAuthors authorsPagerAdapter = new PagerAdapterAllAuthors(
				act.getSupportFragmentManager(), this);
				this.artsListPager.setAdapter(authorsPagerAdapter);
				listener = new PagerListenerAllAuthors(this, authorsPagerAdapter.getAllAuthorsList());
				this.artsListPager.setOnPageChangeListener(listener);
				this.artsListPager.setCurrentItem(this.currentCategoryPosition);
				this.setGroupChildPosition(-1, -1);
			break;
			case PAGER_TYPE_SINGLE:
				final String singleCategoryUrl = this.getCurrentCategory();
				this.artsListPager.setAdapter(new PagerAdapterSingleCategory(act.getSupportFragmentManager(), act,
				singleCategoryUrl));
				listener = new PagerListenerSingleCategory((ActivityMain) act);
				this.artsListPager.setOnPageChangeListener(listener);
				this.artsListPager.setCurrentItem(this.currentCategoryPosition);
				this.setGroupChildPosition(-1, -1);
			break;
			case PAGER_TYPE_CATEGORIES:
				PagerAdapterAllCategories categoriesPagerAdapter = new PagerAdapterAllCategories(
				act.getSupportFragmentManager(), this);
				this.artsListPager.setAdapter(categoriesPagerAdapter);
				listener = new PagerListenerAllCategories(this, categoriesPagerAdapter.getAllCategoriesList());
				this.artsListPager.setOnPageChangeListener(listener);
				this.artsListPager.setCurrentItem(this.currentCategoryPosition);
				this.setGroupChildPosition(-1, -1);
			break;
		}
		this.artsListPager.setPageTransformer(true, new RotationPageTransformer());
		//try notify pager that item selected if it's 0 item
		if (currentCategoryPosition == 0)
		{
			listener.onPageSelected(currentCategoryPosition);
		}

		if (this.twoPane)
		{
			toolbar.getBackground().setAlpha(0);
			toolbarRight.getBackground().setAlpha(255);
		}
		else
		{
			toolbar.getBackground().setAlpha(0);
		}

		//here we check if there is article or comments fragment in fm
		//and show back btn in right toolbar
		if (this.twoPane)
		{
			if (this.getSupportFragmentManager().findFragmentByTag(FragmentComments.LOG) != null)
			{
				//we are on main activity, so we must set toggle to rightToolbar
				toolbarRight.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
				toolbarRight.setNavigationOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						act.onBackPressed();
					}
				});
			}
			else if (this.getSupportFragmentManager().findFragmentByTag(FragmentArticle.LOG) != null)
			{
				//we are on main activity, so we must set toggle to rightToolbar
				toolbarRight.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
				toolbarRight.setNavigationOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						act.onBackPressed();
					}
				});
			}
		}
		//adMob
		this.AddAds();

		if (this.getSearchText() != null)
		{
			this.queryToSave = this.getSearchText();
		}
	}

	private void saveAllCatListsSelectedArtPosition(Bundle b)
	{
		b.putSerializable(KEY_ALL_SELECTED_POSITIONS, allCatListsSelectedArtPosition);
	}

	@SuppressWarnings("unchecked")
	private void restoreAllCatListsSelectedArtPosition(Bundle b)
	{
		this.allCatListsSelectedArtPosition = (HashMap<String, Integer>) b
		.getSerializable(KEY_ALL_SELECTED_POSITIONS);
	}

	@SuppressWarnings("unchecked")
	private void restoreAllCatToolbartopImgYCoord(Bundle savedInstanceState)
	{
		this.allCatToolbarTopImgYCoord = (HashMap<String, int[]>) savedInstanceState
		.getSerializable("allCatToolbarTopImgYCoord");
	}

	private void saveAllCatToolbartopImgYCoord(Bundle savedInstanceState)
	{
		savedInstanceState.putSerializable("allCatToolbarTopImgYCoord", allCatToolbarTopImgYCoord);
	}

	public HashMap<String, Integer> getAllCatListsSelectedArtPosition()
	{
		return allCatListsSelectedArtPosition;
	}

	public int getCurentPositionByGroupChildPosition(int group, int child)
	{
		int firstCategoryChildrenQuontity = act.getResources().getStringArray(R.array.authors_links).length;
		int curPos = -1;
		if (group == 0)
		{
			curPos = child;
		}
		else if (group == 1)
		{
			curPos = firstCategoryChildrenQuontity + child;
		}
		return curPos;
	}

	public int getPagerType()
	{
		return pagerType;
	}

	public void setPagerType(int pagerType)
	{
		this.pagerType = pagerType;
		//TODO here we'll clear groupChilpPosition for drawer menu, update it
		switch (pagerType)
		{
			case PAGER_TYPE_MENU:
			break;
			default:
				this.setGroupChildPosition(-1, -1);
				this.expAdapter.notifyDataSetChanged();
			break;
		}
	}

	@Override
	protected void onResume()
	{
		//Log.e(LOG, "onResume");
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		this.saveGroupChildPosition(outState);

		outState.putInt("curentCategoryPosition", getCurentCategoryPosition());
		outState.putString("currentCategory", this.getCurrentCategory());

		outState.putInt(KEY_PAGER_TYPE, getPagerType());

		//save toolbar and topImg Y coord
		saveAllCatToolbartopImgYCoord(outState);

		//save all cat arts info
		saveAllCatArtsInfo(outState);

		//save selected pos
		this.saveAllCatListsSelectedArtPosition(outState);

		if (this.getSearchText() != null)
		{
			outState.putString("searchText", this.getSearchText());
		}
		outState.putBoolean(KEY_IS_KEYBOARD_OPENED, isKeyboardOpened);

		//allAuthors allCategories and their urls lists
		outState.putParcelableArrayList(KEY_ALL_AUTHORS_LIST, allAuthorsList);
		outState.putParcelableArrayList(KEY_ALL_CATEGORIES_LIST, allCategoriesList);
		outState.putStringArrayList(KEY_ALL_CAT_AND_AUT_URLS_LIST, allCatAndAutURLs);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		//Log.e(LOG, "onCreateOptionsMenu called");
		getMenuInflater().inflate(R.menu.menu_main, menu);

		///searchView setting
		final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView) searchMenuItem.getActionView();

		final MenuItem refresh = menu.findItem(R.id.refresh);
		final MenuItem allSettings = menu.findItem(R.id.action_settings_all);

		MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener()
		{
			@Override
			public boolean onMenuItemActionExpand(MenuItem item)
			{
				//System.out.println("onMenuItemActionExpand");
				if (getSearchText() != null)
				{
					queryToSave = new StringBuffer(getSearchText()).toString();
				}
				isKeyboardOpened = true;

				allSettings.setVisible(false);
				refresh.setVisible(false);
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item)
			{
				//System.out.println("onMenuItemActionCollapse");
				allSettings.setVisible(true);
				refresh.setVisible(true);

				isKeyboardOpened = false;
				queryToSave = null;
				setSearchText(null);
				searchView.setQuery("", true);
				return true;
			}
		});

		searchView.setOnQueryTextListener(new OnQueryTextListener()
		{
			@Override
			public boolean onQueryTextChange(String newText)
			{
				if (mDrawerLayout.isDrawerOpen(Gravity.LEFT))
				{
					return true;
				}
				switch (pagerType)
				{
					case PAGER_TYPE_MENU:
						//this is interesting way:
						String intentAction = (currentCategoryPosition == 3) ? CatData.getMenuLinks(act)[3]
						: (currentCategoryPosition == 13) ? CatData.getMenuLinks(act)[13] : null;
						intentAction = intentAction + "_set_filter";
						if (TextUtils.isEmpty(newText))
						{
							setSearchText(null);
							Intent intentToListFrag = new Intent(intentAction);
							//we need to do it synchroniously to prevent filtering adapters after setting new pagers
							//LocalBroadcastManager.getInstance(act).sendBroadcast(intentToListFrag);
							LocalBroadcastManager.getInstance(act).sendBroadcastSync(intentToListFrag);
						}
						else
						{
							setSearchText(newText);
							queryToSave = getSearchText();
							Intent intentToListFrag = new Intent(intentAction);
							intentToListFrag.putExtra("filter_text", newText);
							//we need to do it synchroniously to prevent filtering adapters after setting new pagers
							//LocalBroadcastManager.getInstance(act).sendBroadcast(intentToListFrag);
							LocalBroadcastManager.getInstance(act).sendBroadcastSync(intentToListFrag);
						}
					break;
					case PAGER_TYPE_AUTHORS:
						if (TextUtils.isEmpty(newText))
						{
							setSearchText(null);
							//initialyse allAuthorsList with default authors list
							PagerAdapterAllAuthors adapter = new PagerAdapterAllAuthors(
							act.getSupportFragmentManager(), (ActivityMain) act);
							artsListPager.setAdapter(adapter);
							OnPageChangeListener listener = new PagerListenerAllAuthors((ActivityMain) act, adapter
							.getAllAuthorsList());
							artsListPager.setOnPageChangeListener(listener);
							listener.onPageSelected(0);
						}
						else
						{
							setSearchText(newText);
							queryToSave = getSearchText();
							//initialyse allAuthorsList
							ArrayList<Author> filteredList = new ArrayList<Author>();
							//filter it
							for (int i = 0; i < getAllAuthorsList().size(); i++)
							{
								Author item = getAllAuthorsList().get(i);
								if (item.getName().toLowerCase(new Locale("RU_ru"))
								.contains(newText.toLowerCase(new Locale("RU_ru"))))
								{
									filteredList.add(item);
								}
							}
							//update allAuthorsAdapter of left pager
							PagerAdapterAllAuthors adapter = new PagerAdapterAllAuthors(act
							.getSupportFragmentManager(), (ActivityMain) act);
							adapter.updateData(filteredList);
							artsListPager.setAdapter(adapter);
							OnPageChangeListener listener = new PagerListenerAllAuthors((ActivityMain) act, adapter
							.getAllAuthorsList());
							artsListPager.setOnPageChangeListener(listener);
							listener.onPageSelected(0);
						}
					break;
					case PAGER_TYPE_CATEGORIES:
						//TODO
						if (TextUtils.isEmpty(newText))
						{
							setSearchText(null);
							//initialyse allAuthorsList with default authors list
							PagerAdapterAllCategories adapter = new PagerAdapterAllCategories(
							act.getSupportFragmentManager(), (ActivityMain) act);
							artsListPager.setAdapter(adapter);
							OnPageChangeListener listener = new PagerListenerAllCategories((ActivityMain) act, adapter
							.getAllCategoriesList());
							artsListPager.setOnPageChangeListener(listener);
							listener.onPageSelected(0);
						}
						else
						{
							setSearchText(newText);
							queryToSave = getSearchText();
							//initialyse allAuthorsList
							ArrayList<Category> filteredList = new ArrayList<Category>();
							//filter it
							for (int i = 0; i < getAllCategoriesList().size(); i++)
							{
								Category item = getAllCategoriesList().get(i);
								if (item.getTitle().toLowerCase(new Locale("RU_ru"))
								.contains(newText.toLowerCase(new Locale("RU_ru"))))
								{
									filteredList.add(item);
								}
							}
							//update allCategoriesAdapter of left pager
							PagerAdapterAllCategories adapter = new PagerAdapterAllCategories(act
							.getSupportFragmentManager(), (ActivityMain) act);
							adapter.updateData(filteredList);
							artsListPager.setAdapter(adapter);
							OnPageChangeListener listener = new PagerListenerAllCategories((ActivityMain) act, adapter
							.getAllCategoriesList());
							artsListPager.setOnPageChangeListener(listener);
							listener.onPageSelected(0);
						}
					break;
				}
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String query)
			{
				//Log.d(LOG, "onQueryTextSubmit");
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

				isKeyboardOpened = false;
				return false;
			}
		});

		//set on focus change listener
		searchView.setOnFocusChangeListener(new OnFocusChangeListener()
		{

			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (hasFocus)
				{
					isKeyboardOpened = true;
				}
				else
				{
					isKeyboardOpened = false;
				}
			}
		});

		//save Menu to var to access it some where
		this.menu = menu;
		if (this.queryToSave != null)
		{
			if (this.isKeyboardOpened)
			{
				searchView.onActionViewExpanded();
				MenuItemCompat.expandActionView(searchMenuItem);
				searchView.setQuery(queryToSave, false);
			}
			else
			{
				MenuItemCompat.expandActionView(searchMenuItem);

				searchView.setQuery(queryToSave, true);

				this.isKeyboardOpened = false;
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
				searchView.clearFocus();
			}
			allSettings.setVisible(false);
			refresh.setVisible(false);
		}
		else
		{
			//Log.e(LOG, "searchText==null");
		}
		return true;
	}

	/* Called whenever we call supportInvalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		//Log.e(LOG, "onPrepareOptionsMenu called");
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

		MenuItem refresh = menu.findItem(R.id.refresh);
		MenuItem settingsAll = menu.findItem(R.id.action_settings_all);
		MenuItem search = menu.findItem(R.id.action_search);

		if (drawerOpen)
		{
			settingsAll.setVisible(false);
			refresh.setVisible(false);
			search.setVisible(false);
		}
		else
		{
			//if expanded
			if (((SearchView) menu.findItem(R.id.action_search).getActionView()).isIconified() == false)
			{
				//setvisibility depending on category
				int position = this.getCurentCategoryPosition();
				switch (this.pagerType)
				{
					case PAGER_TYPE_MENU:
						if (position == 3 || position == 13)
						{
							search.setVisible(true);
							refresh.setVisible(false);
							settingsAll.setVisible(false);
						}
						else
						{
							search.setVisible(false);
							refresh.setVisible(true);
							settingsAll.setVisible(true);
						}
					break;
					case PAGER_TYPE_CATEGORIES:
					case PAGER_TYPE_AUTHORS:
						search.setVisible(true);
						refresh.setVisible(false);
						settingsAll.setVisible(false);
					break;
					case PAGER_TYPE_SINGLE:
						search.setVisible(false);
						refresh.setVisible(true);
						settingsAll.setVisible(true);
					break;
				}
			}
			else
			{
				//searchView is collapsed
				//setvisibility depending on category
				int position = this.getCurentCategoryPosition();
				switch (this.pagerType)
				{
					case PAGER_TYPE_MENU:
						if (position == 3 || position == 13)
						{
							search.setVisible(true);
							refresh.setVisible(false);
							settingsAll.setVisible(true);
						}
						else
						{
							search.setVisible(false);
							refresh.setVisible(true);
							settingsAll.setVisible(true);
						}
					break;
					case PAGER_TYPE_CATEGORIES:
					case PAGER_TYPE_AUTHORS:
						search.setVisible(true);
						refresh.setVisible(true);
						settingsAll.setVisible(true);
					break;
					case PAGER_TYPE_SINGLE:
						search.setVisible(false);
						refresh.setVisible(true);
						settingsAll.setVisible(true);
					break;
				}
			}
		}
		return super.onPrepareOptionsMenu(menu);
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
			case android.R.id.home:
				//called when the up affordance/carat in actionbar is pressed
				onBackPressed();
				return true;
			case R.id.action_settings_all:
				//set theme btn checked if theme is dark
				MenuItem themeMenuItem = item.getSubMenu().findItem(R.id.theme_dark);
				String curTheme = pref.getString("theme", "dark");
				if (curTheme.equals("dark"))
				{
					themeMenuItem.setChecked(true);
				}
				return true;
			case R.id.refresh:
				Log.e(LOG, "refresh");
				DataBaseHelper h = new DataBaseHelper(act);
				int DBVersion = h.getWritableDatabase().getVersion();
				h.onUpgrade(h.getWritableDatabase(), DBVersion, DBVersion + 1);
				// TODO
				//				Intent intent = new Intent(this.act, ServiceDB.class);
				//				Bundle b = new Bundle();
				//				b.putString("categoryToLoad", "odnako.org/blogs");
				//				b.putLong("timeStamp", System.currentTimeMillis());
				//				b.putBoolean("startDownload", true);
				//				intent.putExtras(b);
				//				this.startService(intent);
				//XXX
				//				PagerAdapterArtsLists adapter=(PagerAdapterArtsLists)this.artsListPager.getAdapter();
				//				FragmentAllAuthorsList allAuthorsFrag=(FragmentAllAuthorsList) (adapter.getItem(currentCategoryPosition));
				//				RecyclerView rec=allAuthorsFrag.getArtsList();
				//
				//				this.setAllAuthorsList(Collections.sort(getAllAuthorsList()));
				//				rec.swapAdapter(new AllAuthorsListAdapter(this, rec, allAuthorsFrag), true);
				//((AllAuthorsListAdapter)rec.getAdapter()).getFilter().filter("Мараховский");
				//				rec.setFilterText("Мараховский");

				//				List<Author> allAutList = this.getAllAuthorsList();
				//				Collections.sort(allAutList, new Author.AuthorNameComparator(/*"Василий"*/));
				//				Collections.sort(allAutList, new Author.AuthorDescriptionComparator());
				//				Collections.sort(allAutList, new Author.AuthorWhoComparator());
				//				this.setAllAuthorsList(allAutList);
				//				Intent intentToListFrag = new Intent(CatData.getAllCategoriesMenuLinks(act)[3]
				//				+ "_set_filter");
				//				LocalBroadcastManager.getInstance(act).sendBroadcast(intentToListFrag);

				return true;
			case R.id.action_settings:
				item.setIntent(new Intent(this, ActivityPreference.class));
				return super.onOptionsItemSelected(item);
			case R.id.theme_dark:
				String theme = pref.getString("theme", "dark");
				if (theme.equals("dark"))
				{
					this.pref.edit().putString("theme", "light").commit();
				}
				else
				{
					this.pref.edit().putString("theme", "dark").commit();
				}

				this.recreate();
				return super.onOptionsItemSelected(item);
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public HashMap<String, int[]> getAllCatToolbarTopImgYCoord()
	{
		return allCatToolbarTopImgYCoord;
	}

	public void setAllCatToolbarTopImgYCoord(HashMap<String, int[]> allCatToolbarTopImgYCoord)
	{
		this.allCatToolbarTopImgYCoord = allCatToolbarTopImgYCoord;
	}

	public void updateAllCatToolbarTopImgYCoord(String category, int[] coords)
	{
		this.allCatToolbarTopImgYCoord.put(category, coords);
	}

	@Override
	public void setCurentCategoryPosition(int curentCategoryPosition)
	{
		this.currentCategoryPosition = curentCategoryPosition;

		//if it's not MenuPager, we must return,
		//to avoid setting groupChildPosition for drawer menu
		if (this.pagerType != PAGER_TYPE_MENU)
		{
			return;
		}

		int[] groupChild = this.getGroupChildPositionByCurentPosition(curentCategoryPosition);

		this.setGroupChildPosition(groupChild[0], groupChild[1]);
	}

	@Override
	protected void restoreGroupChildPosition(Bundle state)
	{
		if (state.containsKey("groupChildPosition"))
		{
			this.groupChildPosition = state.getIntArray("groupChildPosition");
			//if it's not MenuPager, we must return,
			//to avoid setting currentCategoryPosition
			if (this.pagerType != PAGER_TYPE_MENU)
			{
				return;
			}
			currentCategoryPosition = this.getCurentPositionByGroupChildPosition(groupChildPosition[0],
			groupChildPosition[1]);
		}
		else
		{
			System.out.println("restoring groupChildPosition FAILED from " + this.getClass().getSimpleName()
			+ " groupChildPosition=null");
		}
	}

	@Override
	public void onBackPressed()
	{
		MenuItem searchMenuItem = this.menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) searchMenuItem.getActionView();
		if (searchView.isIconified() == false)
		{
			Log.d(LOG, "searchView.isIconified()==false");
			searchMenuItem.collapseActionView();
			return;
		}
		if (mDrawerLayout.isDrawerOpen(Gravity.START))
		{
			this.mDrawerLayout.closeDrawer(Gravity.LEFT);
			return;
		}

		if (this.getSupportFragmentManager().findFragmentByTag(FragmentComments.LOG) != null)
		{
			//check if we have also article frag in manager and if not - show hamburger
			if (this.getSupportFragmentManager().findFragmentByTag(FragmentArticle.LOG) == null)
			{
				toolbarRight.setNavigationIcon(null);
			}

			Fragment artFrag = this.getSupportFragmentManager().findFragmentByTag(FragmentComments.LOG);
			this.getSupportFragmentManager().beginTransaction().remove(artFrag).commit();
			return;
		}
		if (this.getSupportFragmentManager().findFragmentByTag(FragmentArticle.LOG) != null)
		{
			toolbarRight.setNavigationIcon(null);
			Fragment artFrag = this.getSupportFragmentManager().findFragmentByTag(FragmentArticle.LOG);
			this.getSupportFragmentManager().beginTransaction().remove(artFrag).commit();
			return;
		}

		//check left pagerType
		//if so - we must show initial state of app
		//else - check if it's second time of pressing back
		switch (this.getPagerType())
		{
			case PAGER_TYPE_MENU:
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
			break;
			//				case PAGER_TYPE_AUTHORS:
			default:
				//reset pagerType to MENU
				//					this.setPagerType(PAGER_TYPE_MENU);
				this.pagerType = PAGER_TYPE_MENU;

				this.setCurentCategoryPosition(11);
				PagerAdapterArtsLists artsListPagerAdapter = new PagerAdapterArtsLists(
				this.getSupportFragmentManager(), act);
				this.artsListPager.setAdapter(artsListPagerAdapter);
				this.artsListPager.setPageTransformer(true, new RotationPageTransformer());
				this.artsListPager.setOnPageChangeListener(new PagerListenerMenu(this));
				this.artsListPager.setCurrentItem(currentCategoryPosition, true);
			break;
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

	/**
	 * sets pagers depend on type and tablet mode can be used in click listeners
	 * 
	 * @param pagerType
	 */
	public void setPagers(int pagerType, int curentCategoryPosition)
	{
		switch (pagerType)
		{
			case PAGER_TYPE_MENU:
				//reset pagerType to MENU
				this.setPagerType(PAGER_TYPE_MENU);

				this.setCurentCategoryPosition(curentCategoryPosition);

				PagerAdapterArtsLists artsListPagerAdapter = new PagerAdapterArtsLists(
				this.getSupportFragmentManager(), act);
				this.artsListPager.setAdapter(artsListPagerAdapter);
				this.artsListPager.setPageTransformer(true, new RotationPageTransformer());
				OnPageChangeListener listener = new PagerListenerMenu(this);
				this.artsListPager.setOnPageChangeListener(listener);

				this.artsListPager.setCurrentItem(currentCategoryPosition, true);

				if (this.currentCategoryPosition == 0)
				{
					listener.onPageSelected(curentCategoryPosition);
				}
			break;
			//				case PAGER_TYPE_AUTHORS:
			default:

			break;
		}
	}
}
