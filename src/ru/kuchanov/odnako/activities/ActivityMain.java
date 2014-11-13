/*
 19.10.2014
ActivityMain.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.download.ParseForAllAuthors;
import ru.kuchanov.odnako.download.ParseForAllCategories;
import ru.kuchanov.odnako.fragments.ArticlesListFragment;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.ArticleViewPagerAdapter;
import ru.kuchanov.odnako.lists_and_utils.ArtsListViewPagerAdapter;
import ru.kuchanov.odnako.lists_and_utils.ZoomOutPageTransformer;
import ru.kuchanov.odnako.utils.UniversalImageLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class ActivityMain extends ActivityBase
{
	ViewPager pager;
	PagerAdapter pagerAdapter;

	ViewPager artsListPager;
	PagerAdapter artsListPagerAdapter;

	private int curentCategoryPosition = 11;

	private int backPressedQ;

	ImageView topImgCover;
	ImageView topImg;
	
	private ArticlesListFragment curArtsListFrag;

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
			this.restoreGroupChildPosition(stateFromIntent);
		}
		else if (savedInstanceState != null)
		{
			this.restoreState(savedInstanceState);
			this.restoreGroupChildPosition(savedInstanceState);

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
		this.topImg = (ImageView) this.findViewById(R.id.top_img);
		this.topImg.setImageResource(R.drawable.odnako);

		//Set unreaded num of arts to zero
		//it's for new arts motification
		SharedPreferences prefsNumOfArts = this.getSharedPreferences("saveNumOfUnReadedArts", 0);
		SharedPreferences.Editor editor = prefsNumOfArts.edit();
		editor.putInt("quontityOfUnreadedArts", 0);
		editor.commit();
		//end of Set unreaded num of arts to zero

		//set arts lists viewPager
		this.artsListPager = (ViewPager) this.findViewById(R.id.arts_list_container);
		this.artsListPagerAdapter = new ArtsListViewPagerAdapter(this.getSupportFragmentManager(), act);
		this.artsListPager.setAdapter(artsListPagerAdapter);
		this.artsListPager.setPageTransformer(true, new ZoomOutPageTransformer());
		//
		if (savedInstanceState != null)
			setCurentCategoryPosition(savedInstanceState.getInt("curentCategoryPosition"));
		///
		this.artsListPager.setCurrentItem(getCurentCategoryPosition(), true);
		this.artsListPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				System.out.println("select page; position= " + position);
				//setCurArtsListFrag
				curArtsListFrag=(ArticlesListFragment) ((ArtsListViewPagerAdapter) artsListPagerAdapter)
				.getRegisteredFragment(position);
				
				int firstCategoryChildrenQuontity = act.getResources().getStringArray(R.array.authors_links).length;

				String title = ((ArtsListViewPagerAdapter) artsListPagerAdapter).getAllCategoriesMenuNames()[position];
				setTitle(title);

				//change topImg
				if (position >= firstCategoryChildrenQuontity)
				{
					String defPackage = act.getPackageName();
					String[] catImgsFilesNames = act.getResources().getStringArray(R.array.categories_imgs_files_names);
					String resName = catImgsFilesNames[position - firstCategoryChildrenQuontity].substring(0,
					catImgsFilesNames[position - firstCategoryChildrenQuontity].length() - 4);
					int resId = act.getResources().getIdentifier(resName, "drawable", defPackage);
					ImageLoader imgLoader = UniversalImageLoader.get(act);
					imgLoader.displayImage("drawable://" + resId, topImg);
				}
				int group;
				int child;
				//4 is not a magic number! It's a quontity of categories in authors menu items in drawer

				if (position >= firstCategoryChildrenQuontity)
				{
					group = 1;
					child = position - firstCategoryChildrenQuontity;
				}
				else
				{
					group = 0;
					child = position;
				}
				setGroupChildPosition(group, child);
				//notify drawer adapter to show initial (lenta obnovleniy) selected category
				expAdapter.notifyDataSetChanged();

				//save curent category position
				setCurentCategoryPosition(position);
				
				///////set new adapter to ArticleViewPAger
				pagerAdapter=new ArticleViewPagerAdapter(act.getSupportFragmentManager(), allArtsInfo, act);
				pager.setAdapter(pagerAdapter);
				///////////

				//show toolbar when switch category to show it's title
				if (android.os.Build.VERSION.SDK_INT >= 11)
				{
					//restore and set topImg position
					ArticlesListFragment frag = (ArticlesListFragment) ((ArtsListViewPagerAdapter) artsListPagerAdapter)
					.getRegisteredFragment(position);
					topImg.setY(frag.getTopImgYCoord());

					toolbar.setY(0);

					if (frag.getToolbarYCoord() < 0)
					{
						toolbar.getBackground().setAlpha(255);
					}
					else
					{
						LinearLayoutManager listManager = (LinearLayoutManager) frag.getArtsListView()
						.getLayoutManager();
						try
						{
							if (listManager.findFirstVisibleItemPosition() == 0)
							{
								View firstArtViewInRecyclerView = listManager.findViewByPosition(1);
								int initialDistance = frag.getInitialDistance();
								int curDistance = (int) (firstArtViewInRecyclerView.getY() - toolbar.getHeight());
								float percent = (float) curDistance / (float) initialDistance;
								float gradient = 1f - percent;
								int newAlpha = (int) (255 * gradient);
								toolbar.getBackground().setAlpha(newAlpha);
							}
							else
							{
								System.out.println("catchEdPositonChenged NPE in LAyoutManager");
								toolbar.getBackground().setAlpha(255);
							}
						}
						catch (Exception e)
						{
							toolbar.getBackground().setAlpha(255);
						}
					}
				}
			}
		});

		///////////////
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

			this.pager = (ViewPager) this.findViewById(R.id.article_comments_container);

//			this.pagerAdapter = new ArticleViewPagerAdapter(this.getSupportFragmentManager(), allArtsInfo, this);
			//defAllArtsInfo set to artFrag
			this.pagerAdapter = new ArticleViewPagerAdapter(this.getSupportFragmentManager(), ArtInfo.getDefaultAllArtsInfo(act), this);
			this.pager.setAdapter(pagerAdapter);
			this.pager.setPageTransformer(true, new ZoomOutPageTransformer());
			this.pager.setCurrentItem(position, true);
			this.pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
			{
				@Override
				public void onPageSelected(int position)
				{
//					ArticlesListFragment artsListFrag = (ArticlesListFragment) getSupportFragmentManager()
//					.findFragmentById(R.id.arts_list_container);
//					//setActivPosition to curItem of pager
//					artsListFrag.setActivatedPosition(position);
//					artsListFrag.scrollToActivatedPosition();
					curArtsListFrag = (ArticlesListFragment) ((ArtsListViewPagerAdapter) artsListPagerAdapter)
					.getRegisteredFragment(getCurentCategoryPosition());
					if(curArtsListFrag!=null)
					{
//						curArtsListFrag.setActivateOnItemClick(true);
//						position = curArtsListFrag.getMyActivatedPosition();
						
						if (position == ListView.INVALID_POSITION)
						{
							position = 0;
						}
						curArtsListFrag.setActivatedPosition(position);
						curArtsListFrag.scrollToActivatedPosition();
					}
				}
			});

			//////////////
			//get position from listFrag
			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
//			ArticlesListFragment curArtsListFrag = (ArticlesListFragment) ((ArtsListViewPagerAdapter) this.artsListPagerAdapter)
//			.getRegisteredFragment(curentCategoryPosition);
//			((ArticlesListFragment) getSupportFragmentManager().findFragmentById(R.id.arts_list_container))
//			.setActivateOnItemClick(true);
			
			////////
//			this.position = ((ArticlesListFragment) getSupportFragmentManager().findFragmentById(
//			R.id.arts_list_container)).getMyActivatedPosition();
			curArtsListFrag = (ArticlesListFragment) ((ArtsListViewPagerAdapter) this.artsListPagerAdapter)
			.getRegisteredFragment(getCurentCategoryPosition());
			if(curArtsListFrag!=null)
			{
				curArtsListFrag.setActivateOnItemClick(true);
				this.position = curArtsListFrag.getMyActivatedPosition();
				if (this.position == ListView.INVALID_POSITION)
				{
					this.position = 0;
				}
				curArtsListFrag.setActivatedPosition(position);
			}
			
//			((ArticlesListFragment) getSupportFragmentManager().findFragmentById(R.id.arts_list_container))
//			.setActivatedPosition(position);
			////////////
		}
		//////////
		//		setTitle by pagerPOsition
		String title = ((ArtsListViewPagerAdapter) artsListPagerAdapter).getAllCategoriesMenuNames()[this.getCurentCategoryPosition()];
		setTitle(title);
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

		outState.putInt("curentCategoryPosition", getCurentCategoryPosition());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		System.out.println("ActivityMain onRestoreInstanceState");

		this.restoreState(savedInstanceState);
		this.restoreGroupChildPosition(savedInstanceState);

		setCurentCategoryPosition(savedInstanceState.getInt("curentCategoryPosition"));
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

	public ArticlesListFragment getCurArtsListFrag()
	{
		return curArtsListFrag;
	}

	public void setCurArtsListFrag(ArticlesListFragment curArtsListFrag)
	{
		this.curArtsListFrag = curArtsListFrag;
	}

	public int getCurentCategoryPosition()
	{
		return curentCategoryPosition;
	}

	public void setCurentCategoryPosition(int curentCategoryPosition)
	{
		this.curentCategoryPosition = curentCategoryPosition;
	}

}
