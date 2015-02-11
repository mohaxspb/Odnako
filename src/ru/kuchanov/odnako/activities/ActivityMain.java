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
import ru.kuchanov.odnako.lists_and_utils.PagerAuthorsListsAdapter;
import ru.kuchanov.odnako.lists_and_utils.CatData;
import ru.kuchanov.odnako.utils.DipToPx;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityMain extends ActivityBase
{
	final private static String LOG_TAG = ActivityMain.class.getSimpleName();

	//ViewPager and it's adapter for articles/comments
	ViewPager artCommsPager;
	PagerAdapter pagerAdapter;

	//ViewPager and it's adapter for artsLists
	ViewPager artsListPager;
	PagerAdapter artsListPagerAdapter;

	//art's list top image and it's gradient cover
	//	ImageView topImgCover;
	//	ImageView topImg;

	Toolbar toolbarMain;

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
		//		System.out.println("ActivityMain onCreate");
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

		//		Bundle stateFromIntent = this.getIntent().getExtras();
		//		if (stateFromIntent != null)
		//		{
		//			this.restoreState(stateFromIntent);
		//			this.restoreGroupChildPosition(stateFromIntent);
		//		}
		if (savedInstanceState != null)
		{
			this.curArtPosition = savedInstanceState.getInt("position");
			this.curAllArtsInfo = savedInstanceState.getParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO);
			this.currentCategoryPosition = savedInstanceState.getInt("curentCategoryPosition");
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
		/////////
		/////////
		////////find all views
		this.toolbar = (Toolbar) this.findViewById(R.id.toolbar);
//XXX		this.topImgCover = (ImageView) this.findViewById(R.id.top_img_cover);
//		this.topImg = (ImageView) this.findViewById(R.id.top_img);
		this.artsListPager = (ViewPager) this.findViewById(R.id.arts_list_container);
		if (this.twoPane)
		{
			this.artCommsPager = (ViewPager) this.findViewById(R.id.article_comments_container);
		}

		/////////////////////////////
		//setNavDraw
		this.setNavDrawer();
		//End of setNavDraw

		//set arts lists viewPager
		this.artsListPagerAdapter = new PagerArtsListsAdapter(this.getSupportFragmentManager(), act);
		this.artsListPager.setAdapter(artsListPagerAdapter);
		this.artsListPager.setPageTransformer(true, new RotationPageTransformer());
		this.artsListPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				//Log.d(LOG_TAG, "select artsListPager position= " + position);
				//this will set current pos, and adapters group/child pos
				setCurentCategoryPosition(position);

				setTitleDrawerItemToolbarTopImgETC(position);

				//test sending intent to listfrag to notify it's adapter to fix issue
				//when there is only 1-st art is shown and other can be shown only from articlesPager
				//when switching articles
				String[] allCatsLinks = CatData.getAllCategoriesMenuLinks(act);
				Intent intentToListFrag = new Intent(allCatsLinks[currentCategoryPosition] + "_notify_that_selected");
				LocalBroadcastManager.getInstance(act).sendBroadcast(intentToListFrag);
				if (twoPane)
				{
					if (currentCategoryPosition != 3 && currentCategoryPosition != 13)
					{
						pagerAdapter = new PagerArticlesAdapter(act.getSupportFragmentManager(), CatData
						.getAllCategoriesMenuLinks(act)[currentCategoryPosition], act);
						artCommsPager.setAdapter(pagerAdapter);
						artCommsPager.setPageTransformer(true, new RotationPageTransformer());
						artCommsPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
						{
							@Override
							public void onPageSelected(int position)
							{
								//move topImg and toolBar while scrolling left list
								toolbar.setY(0 - toolbar.getHeight());
//	XXX							topImg.setY(0 - topImg.getHeight());
								System.out.println("onPageSelected in articlePager; position: " + position);
								String[] allCatsLinks = CatData.getAllCategoriesMenuLinks(act);
								allCatListsSelectedArtPosition.put(allCatsLinks[currentCategoryPosition], position);

								Intent intentToListFrag = new Intent(allCatsLinks[currentCategoryPosition]
								+ "art_position");
								Bundle b = new Bundle();
								b.putInt("position", position);
								intentToListFrag.putExtras(b);

								LocalBroadcastManager.getInstance(act).sendBroadcast(intentToListFrag);
							}
						});
						int curPos = allCatListsSelectedArtPosition.get(allCatsLinks[currentCategoryPosition]);
						artCommsPager.setCurrentItem(curPos, true);
					}
					else if (currentCategoryPosition == 3)
					{
						//show all authors adapters
						pagerAdapter = new PagerAuthorsListsAdapter(act.getSupportFragmentManager(), act);
						artCommsPager.setAdapter(pagerAdapter);
						artCommsPager.setPageTransformer(true, new RotationPageTransformer());
						artCommsPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
						{
							@Override
							public void onPageSelected(int position)
							{
								//move topImg and toolBar while scrolling left list
								toolbar.setY(0 - toolbar.getHeight());
//	XXX							topImg.setY(0 - topImg.getHeight());
								System.out.println("onPageSelected in articlePager; position: " + position);
								String[] allCatsLinks = CatData.getAllCategoriesMenuLinks(act);
								allCatListsSelectedArtPosition.put(allCatsLinks[currentCategoryPosition], position);

								Intent intentToListFrag = new Intent(allCatsLinks[currentCategoryPosition]
								+ "art_position");
								Bundle b = new Bundle();
								b.putInt("position", position);
								intentToListFrag.putExtras(b);

								LocalBroadcastManager.getInstance(act).sendBroadcast(intentToListFrag);
							}
						});
						int curPos = allCatListsSelectedArtPosition.get(allCatsLinks[currentCategoryPosition]);
						artCommsPager.setCurrentItem(curPos, true);
					}
					else if (currentCategoryPosition == 13)
					{
						//TODO show all categories adapters
					}
				}
			}
		});
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
		if (this.twoPane == false)
		{
			toolbar.getBackground().setAlpha(0);
		}
		//setTopImageCover

//	XXX	if (this.pref.getString("theme", "dark").equals("dark"))
//		{
//			topImgCover.setBackgroundResource(R.drawable.top_img_cover_grey_dark);
//		}
//		else
//		{
//			topImgCover.setBackgroundResource(R.drawable.top_img_cover_grey_light);
//		}
		////////////////

		//adMob
		this.AddAds();
		//end of adMob
	}

	private void restoreAllCatListsSelectedArtPosition(Bundle b)
	{
		this.allCatListsSelectedArtPosition = new HashMap<String, Integer>();
		String[] catLinks = CatData.getAllCategoriesMenuLinks(act);
		for (int i = 0; i < catLinks.length; i++)
		{
			this.allCatListsSelectedArtPosition.put(catLinks[i],
			b.getInt("allCatListsSelectedArtPosition_" + String.valueOf(i)));
		}
	}

	private void setTitleDrawerItemToolbarTopImgETC(int position)
	{
//		int firstCategoryChildrenQuontity = act.getResources().getStringArray(R.array.authors_links).length;

		String title = CatData.getAllCategoriesMenuNames(act)[position];
		setTitle(title);

		//change topImg
//	XXX	if (position >= firstCategoryChildrenQuontity)
//		{
//			String defPackage = act.getPackageName();
//			String[] catImgsFilesNames = act.getResources().getStringArray(R.array.categories_imgs_files_names);
//			String fullResName = catImgsFilesNames[position - firstCategoryChildrenQuontity];
//			String resName = fullResName.substring(0, fullResName.length() - 4);
//			int resId = act.getResources().getIdentifier(resName, "drawable", defPackage);
//			ImageLoader imgLoader = UniversalImageLoader.get(act);
//			imgLoader.displayImage("drawable://" + resId, topImg,
//			UniversalImageLoader.getTransparentBackgroundOptions());
//			//			topImg.setImageResource(resId);
//		}

		//show toolbar when switch category to show it's title
		//restore and set topImg position
		String[] allMenuCatsLinks = CatData.getAllCategoriesMenuLinks(act);
		String curCatLink = allMenuCatsLinks[position];
		int toolbarY = allCatToolbarTopImgYCoord.get(curCatLink)[0];
//		int topImgY = allCatToolbarTopImgYCoord.get(curCatLink)[1];
		int initialDistance = allCatToolbarTopImgYCoord.get(curCatLink)[2];
		int currentDistance = allCatToolbarTopImgYCoord.get(curCatLink)[3];
//	XXX	topImg.setY(topImgY);

		if (toolbarY < 0)
		{
			toolbar.getBackground().setAlpha(255);
			toolbar.setY(0);
		}
		else
		{
			toolbar.setY(0);

			float percent = (float) currentDistance / (float) initialDistance;
			float gradient = 1f - percent;
			int newAlpha = (int) (255 * gradient);
			toolbar.getBackground().setAlpha(newAlpha);
		}
	}

	private void restoreAllCatToolbartopImgYCoord(Bundle savedInstanceState)
	{
		this.setAllCatToolbarTopImgYCoord(null);
		if (savedInstanceState.containsKey("allCatToolbarTopImgYCoord_0"))
		{
			setAllCatToolbarTopImgYCoord(new HashMap<String, int[]>());
			String[] allCategoriesMenuLinks = CatData.getAllCategoriesMenuLinks(act);
			for (int i = 0; i < allCategoriesMenuLinks.length; i++)
			{
				getAllCatToolbarTopImgYCoord().put(allCategoriesMenuLinks[i],
				savedInstanceState.getIntArray("allCatToolbarTopImgYCoord_" + String.valueOf(i)));
			}
		}
		else
		{
			System.out.println("allCatToolbarTopImgYCoord=null from saved state. WTF?!");
		}
	}

	private void saveAllCatToolbartopImgYCoord(Bundle savedInstanceState)
	{
		String[] allCategoriesMenuLinks = CatData.getAllCategoriesMenuLinks(act);
		for (int i = 0; i < allCategoriesMenuLinks.length; i++)
		{
			savedInstanceState.putIntArray("allCatToolbarTopImgYCoord_" + String.valueOf(i),
			getAllCatToolbarTopImgYCoord().get(allCategoriesMenuLinks[i]));
		}
	}

	private void saveAllCatListsSelectedArtPosition(Bundle b)
	{
		String[] allLinks = CatData.getAllCategoriesMenuLinks(act);
		for (int i = 0; i < allLinks.length; i++)
		{
			b.putInt("allCatListsSelectedArtPosition_" + String.valueOf(i),
			allCatListsSelectedArtPosition.get(allLinks[i]));
		}
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
				this.myRecreate();
				return true;
			case R.id.theme_dark:
				this.pref.edit().putString("theme", "dark").commit();
				this.myRecreate();
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
}
