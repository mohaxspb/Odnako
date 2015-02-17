/*
 19.10.2014
ActivityMain.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import java.util.HashMap;
import java.util.ArrayList;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.animations.RotationPageTransformer;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.PagerArticlesAdapter;
import ru.kuchanov.odnako.lists_and_utils.PagerArtsListsAdapter;
import ru.kuchanov.odnako.lists_and_utils.CatData;
import ru.kuchanov.odnako.lists_and_utils.PagerAuthorsListsAdapter;
import ru.kuchanov.odnako.lists_and_utils.PagerListenerAllAuthors;
import ru.kuchanov.odnako.lists_and_utils.PagerOneArtsListAdapter;
import ru.kuchanov.odnako.utils.DipToPx;
import ru.kuchanov.odnako.lists_and_utils.PagerListenerMenu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ActivityMain extends ActivityBase
{
	final private static String LOG_TAG = ActivityMain.class.getSimpleName();

	public final static int PAGER_TYPE_MENU = 0;
	public final static int PAGER_TYPE_AUTHORS = 1;
	public final static int PAGER_TYPE_CATEGORIES = 2;
	public final static int PAGER_TYPE_SINGLE = 3;
	private final static String PAGER_TYPE_KEY = "pager type key";
	public int pagerType = PAGER_TYPE_MENU;

	//ViewPager and it's adapter for articles/comments
	ViewPager artCommsPager;
	PagerAdapter pagerAdapter;

	//ViewPager and it's adapter for artsLists
	ViewPager artsListPager;
	PagerAdapter artsListPagerAdapter;

	//art's list top image and it's gradient cover
	//	ImageView topImgCover;
	//	ImageView topImg;

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

	protected void onCreate(Bundle savedInstanceState)
	{
		//System.out.println("ActivityMain onCreate");
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
		if (savedInstanceState != null)
		{
			this.curArtPosition = savedInstanceState.getInt("position");
			this.curAllArtsInfo = savedInstanceState.getParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO);
			this.currentCategoryPosition = savedInstanceState.getInt("curentCategoryPosition");
			this.setCurrentCategory(savedInstanceState.getString("currentCategory"));

			this.pagerType = savedInstanceState.getInt(PAGER_TYPE_KEY);

			this.restoreAllCatArtsInfo(savedInstanceState);

			this.restoreGroupChildPosition(savedInstanceState);
			this.restoreAllCatToolbartopImgYCoord(savedInstanceState);
			this.restoreAllCatArtsInfo(savedInstanceState);
			this.restoreAllCatListsSelectedArtPosition(savedInstanceState);
		}

		//get artsInfo data from DB
		if (this.allCatArtsInfo == null)
		{
			//			this.allCatArtsInfo = CatData.getAllCatArtsInfoFromDB(System.currentTimeMillis(), act);
			this.allCatArtsInfo = new HashMap<String, ArrayList<ArtInfo>>();
		}

		//set selected pos for all cats if they are null (first launch without any state)
		if (this.allCatListsSelectedArtPosition == null)
		{
			String[] catMenuLinks = CatData.getAllCategoriesMenuLinks(act);
			this.allCatListsSelectedArtPosition = new HashMap<String, Integer>();
			for (int i = 0; i < catMenuLinks.length; i++)
			{
				this.allCatListsSelectedArtPosition.put(catMenuLinks[i], 0);
			}
		}

		//set coords of topImg and toolbar if they are null (first launch without any state)
		if (allCatToolbarTopImgYCoord == null)
		{
			setAllCatToolbarTopImgYCoord(new HashMap<String, int[]>());
			String[] allCategoriesMenuLinks = CatData.getAllCategoriesMenuLinks(act);
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

		this.artsListPager = (ViewPager) this.findViewById(R.id.arts_list_container);
		if (this.twoPane)
		{
			this.artCommsPager = (ViewPager) this.findViewById(R.id.article_comments_container);
			this.toolbarRight = (Toolbar) this.findViewById(R.id.toolbar_right);
		}

		/////////////////////////////
		//setNavDraw
		this.setNavDrawer();
		//End of setNavDraw

		//set arts lists viewPager
		Log.e(LOG_TAG, "pagerType: " + this.pagerType);
		switch (this.pagerType)
		{
			case PAGER_TYPE_MENU:
				this.artsListPagerAdapter = new PagerArtsListsAdapter(this.getSupportFragmentManager(), act);
				this.artsListPager.setAdapter(artsListPagerAdapter);

				ViewPager.SimpleOnPageChangeListener menuListener = new PagerListenerMenu(this, artCommsPager,
				pagerAdapter, artsListPager, artsListPagerAdapter, toolbarRight, toolbar);
				this.artsListPager.setOnPageChangeListener(menuListener);
				this.artsListPager.setCurrentItem(this.currentCategoryPosition);
			break;
			case PAGER_TYPE_AUTHORS:
				this.artsListPagerAdapter = new PagerAuthorsListsAdapter(act.getSupportFragmentManager(), act);
				this.artsListPager.setAdapter(artsListPagerAdapter);
				artsListPager.setPageTransformer(true, new RotationPageTransformer());
				this.artsListPager.setOnPageChangeListener(new PagerListenerAllAuthors(this));
				this.artsListPager.setCurrentItem(this.currentCategoryPosition);
			break;
			case PAGER_TYPE_SINGLE:
				final String authorBlogUrl = this.getCurrentCategory();
				this.artsListPager.setAdapter(new PagerOneArtsListAdapter(act.getSupportFragmentManager(), act,
				authorBlogUrl));
				this.artsListPager.setOnPageChangeListener(null);

				if (this.twoPane)
				{
					this.artCommsPager.setAdapter(new PagerArticlesAdapter(act.getSupportFragmentManager(),
					authorBlogUrl, act));
					artCommsPager.setPageTransformer(true, new RotationPageTransformer());
					this.artCommsPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
					{
						@Override
						public void onPageSelected(int position)
						{
							//move topImg and toolBar while scrolling left list
							toolbarRight.setTitle("");
							System.out.println("onPageSelected in articlePager; position: " + position);
							getAllCatListsSelectedArtPosition().put(authorBlogUrl, position);

							Intent intentToListFrag = new Intent(authorBlogUrl + "art_position");
							Bundle b = new Bundle();
							b.putInt("position", position);
							intentToListFrag.putExtras(b);

							LocalBroadcastManager.getInstance(act).sendBroadcast(intentToListFrag);
						}
					});
					if (this.getAllCatListsSelectedArtPosition().get(authorBlogUrl) != null)
					{
						this.artCommsPager.setCurrentItem(this.getAllCatListsSelectedArtPosition().get(
						authorBlogUrl));
					}
				}
				this.pagerType = ActivityMain.PAGER_TYPE_SINGLE;
			break;
		}
		this.artsListPager.setPageTransformer(true, new RotationPageTransformer());

		if (pagerAdapter == null)
		{
			this.artsListPager.setCurrentItem(this.currentCategoryPosition, true);
		}
		else
		{
			Log.e(LOG_TAG, "pagerAdapter!=null");
		}

		///////////////

		//onMain if we don't use twoPane mode we'll set alpha for action bar
		//we'll do it after setNavDrawer, cause we find toolbar in it
		//		if (android.os.Build.VERSION.SDK_INT >= 11 && this.twoPane == false)
		//		{
		//			toolbar.getBackground().setAlpha(0);
		//		}
		//		else if (this.pref.getBoolean("animate_lists", false))
		//		{
		//			toolbar.getBackground().setAlpha(255);
		//		}
		if (this.twoPane)
		{
			toolbar.getBackground().setAlpha(0);
			toolbarRight.getBackground().setAlpha(0);
		}
		else
		{
			toolbar.getBackground().setAlpha(0);
		}

		//adMob
		this.AddAds();
		//end of adMob
	}

	private void saveAllCatListsSelectedArtPosition(Bundle b)
	{
		b.putSerializable("allCatListsSelectedArtPosition", allCatListsSelectedArtPosition);
	}

	@SuppressWarnings("unchecked")
	private void restoreAllCatListsSelectedArtPosition(Bundle b)
	{
		this.allCatListsSelectedArtPosition = (HashMap<String, Integer>) b
		.getSerializable("allCatListsSelectedArtPosition");
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

	@Override
	protected void onResume()
	{
		//		System.out.println("ActivityMain onResume");
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		//		System.out.println("ActivityMain: onSaveInstanceState");

		this.saveGroupChildPosition(outState);

		outState.putInt("curentCategoryPosition", getCurentCategoryPosition());
		outState.putString("currentCategory", this.getCurrentCategory());

		outState.putInt(PAGER_TYPE_KEY, pagerType);

		//save toolbar and topImg Y coord
		saveAllCatToolbartopImgYCoord(outState);

		//save all cat arts info
		saveAllCatArtsInfo(outState);

		//save selected pos
		this.saveAllCatListsSelectedArtPosition(outState);
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
			case R.id.action_settings_all:
				MenuItem themeMenuItem = item.getSubMenu().findItem(R.id.theme_dark);
				String curTheme = pref.getString("theme", "dark");
				if (curTheme.equals("dark"))
				{
					themeMenuItem.setChecked(true);
				}
				Log.e(LOG_TAG, String.valueOf(themeMenuItem.isChecked()));
				return true;
			case R.id.refresh:
				System.out.println("refresh");
				// TODO
				//				Intent intent = new Intent(this.act, ServiceDB.class);
				//				Bundle b = new Bundle();
				//				b.putString("categoryToLoad", "odnako.org/blogs");
				//				b.putLong("timeStamp", System.currentTimeMillis());
				//				b.putBoolean("startDownload", true);
				//				intent.putExtras(b);
				//				this.startService(intent);
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

	public void updateAllCatArtsInfo(String category, ArrayList<ArtInfo> newData)
	{
		this.allCatArtsInfo.put(category, newData);
		//		String curCategoryLink = CatData.getAllCategoriesMenuLinks(act)[currentCategoryPosition];
		//		if (twoPane && category.equals(curCategoryLink))
		//		{
		//			pagerAdapter = new ArticlesPagerAdapter(act.getSupportFragmentManager(),
		//			CatData.getAllCategoriesMenuLinks(act)[currentCategoryPosition], act);
		//			artCommsPager.setAdapter(pagerAdapter);
		//		}
	}

	@Override
	protected void restoreGroupChildPosition(Bundle state)
	{
		if (state.containsKey("groupChildPosition"))
		{
			this.groupChildPosition = state.getIntArray("groupChildPosition");
			//			int curentCategoryPosition = this.getCurentPositionByGroupChildPosition(groupChildPosition[0],
			//			groupChildPosition[1]);
			//			this.setCurentCategoryPosition(curentCategoryPosition);
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

		if (mDrawerLayout.isDrawerOpen(Gravity.START))
		{
			this.mDrawerLayout.closeDrawer(Gravity.LEFT);
		}
		else
		{
			//check left pagerType
			//if so - we must show initial state of app
			//else - check if it's second time of pressing back
			switch (this.pagerType)
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
					this.pagerType = PAGER_TYPE_MENU;

					this.setCurentCategoryPosition(11);
					this.artsListPagerAdapter = new PagerArtsListsAdapter(this.getSupportFragmentManager(), act);
					this.artsListPager.setAdapter(artsListPagerAdapter);
					this.artsListPager.setPageTransformer(true, new RotationPageTransformer());
					ViewPager.SimpleOnPageChangeListener menuListener = new PagerListenerMenu(this, artCommsPager,
					pagerAdapter,
					artsListPager, artsListPagerAdapter, toolbarRight, toolbar);
					this.artsListPager.setOnPageChangeListener(menuListener);
					this.artsListPager.setCurrentItem(currentCategoryPosition, true);
				break;
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
}
