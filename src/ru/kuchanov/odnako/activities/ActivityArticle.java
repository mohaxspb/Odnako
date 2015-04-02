/*
 21.10.2014
ActivityArticle.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import java.util.ArrayList;
import java.util.HashMap;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.animations.RotationPageTransformer;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.fragments.FragmentArticle;
import ru.kuchanov.odnako.lists_and_utils.Actions;
import ru.kuchanov.odnako.lists_and_utils.PagerAdapterArticles;
import ru.kuchanov.odnako.lists_and_utils.PagerListenerArticle;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;

public class ActivityArticle extends ActivityBase
{
	static final String LOG = ActivityArticle.class.getSimpleName();

	private ViewPager pager;
	private PagerAdapter pagerAdapter;

	private String categoryToLoad = Const.EMPTY_STRING;

	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ActivityArticle onCreate");

		this.act = this;
		//get default settings to get all settings later
		PreferenceManager.setDefaultValues(act, R.xml.pref, true);
		this.pref = PreferenceManager.getDefaultSharedPreferences(act);
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

		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_article);

		//restore state
		Bundle stateFromIntent = this.getIntent().getExtras();
		if (stateFromIntent != null)
		{

			this.curAllArtsInfo = stateFromIntent.getParcelableArrayList(Article.KEY_ALL_ART_INFO);
			this.categoryToLoad = stateFromIntent.getString("categoryToLoad");
			this.setCurArtPosition(stateFromIntent.getInt("position"));
			this.currentCategoryPosition = stateFromIntent.getInt("curentCategoryPosition");

			this.allCatArtsInfo = new HashMap<String, ArrayList<Article>>();
			this.allCatArtsInfo.put(categoryToLoad, curAllArtsInfo);

			this.restoreGroupChildPosition(stateFromIntent);
		}
		/* else */if (savedInstanceState != null)
		{
			this.categoryToLoad = savedInstanceState.getString("categoryToLoad");
			this.setCurArtPosition(savedInstanceState.getInt("position"));
			this.currentCategoryPosition = savedInstanceState.getInt("curentCategoryPosition");

			this.restoreAllCatArtsInfo(savedInstanceState);

			this.curAllArtsInfo = this.allCatArtsInfo.get(categoryToLoad);

			this.allCatAndAutTitles = savedInstanceState.getStringArrayList(KEY_ALL_CAT_AND_AUT_TITLES_LIST);
			this.allCatAndAutURLs = savedInstanceState.getStringArrayList(KEY_ALL_CAT_AND_AUT_URLS_LIST);

			this.restoreGroupChildPosition(savedInstanceState);
		}

		//drawer settings
		this.setNavDrawer();
		////End of drawer settings

		//setting toolbar
		this.toolbar.getBackground().setAlpha(255);

		this.getAllCatAndAutURLs();
		this.getAllCatAndAutTitles();

		this.pager = (ViewPager) this.findViewById(R.id.article_container);
		this.pager.setPageTransformer(true, new RotationPageTransformer());
		this.pagerAdapter = new PagerAdapterArticles(this.getSupportFragmentManager(), categoryToLoad, act);
		this.pager.setAdapter(pagerAdapter);
		PagerListenerArticle listener = new PagerListenerArticle(this, categoryToLoad);
		this.pager.setOnPageChangeListener(listener);
		this.pager.setCurrentItem(getCurArtPosition(), true);
		if (getCurArtPosition() == 0)
		{
			listener.onPageSelected(0);
		}

		//adMob
		this.AddAds();
		//end of adMob
	}

	@Override
	public void onResume()
	{
		super.onResume();
		//TODO find why here we have default title of ActionBar;
		ActivityArticle articleActivity = (ActivityArticle) this.act;
		String categotiesTitle = "";

		for (int i = 0; i < articleActivity.getAllCatAndAutURLs().size(); i++)
		{
			String s = articleActivity.getAllCatAndAutURLs().get(i);
			if (s.equals(this.categoryToLoad))
			{
				categotiesTitle = articleActivity.getAllCatAndAutTitles().get(i);
				this.toolbar.setTitle(categotiesTitle + " " + String.valueOf(this.getCurArtPosition() + 1) + "/"
				+ articleActivity.getAllCatArtsInfo().get(categoryToLoad).size());
				break;
			}
		}
		if (this.getSupportFragmentManager().findFragmentByTag(FragmentArticle.LOG) != null)
		{
			//set arrowDownIcon by theme
			//			int[] attrs = new int[] { R.attr.arrowBackIcon };
			//			TypedArray ta = act.obtainStyledAttributes(attrs);
			//			Drawable drawableArrowBack = ta.getDrawable(0);
			//			ta.recycle();
			//			toolbar.setNavigationIcon(drawableArrowBack);
			//			toolbar.setNavigationOnClickListener(new View.OnClickListener()
			//			{
			//				@Override
			//				public void onClick(View v)
			//				{
			//					//					toolbar.setNavigationIcon(null);
			//					act.onBackPressed();
			//				}
			//			});
		}
	}

	/** Called whenever we call supportInvalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);
		menu.findItem(R.id.action_settings_all).setVisible(!drawerOpen);
		menu.findItem(R.id.comments).setVisible(!drawerOpen);
		menu.findItem(R.id.share).setVisible(!drawerOpen);

		if (this.getSupportFragmentManager().findFragmentByTag(FragmentArticle.LOG) != null)
		{
			this.mDrawerToggle.setDrawerIndicatorEnabled(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_article, menu);

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
			case R.id.comments:
				Actions.showComments(curAllArtsInfo, getCurArtPosition(), act);
				return true;
			case R.id.share:
				Actions.shareUrl(this.curAllArtsInfo.get(this.getCurArtPosition()).getUrl(), this.act);
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

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		//		outState.putParcelableArrayList(Article.KEY_ALL_ART_INFO, curAllArtsInfo);
		outState.putString("categoryToLoad", categoryToLoad);
		outState.putInt("position", this.getCurArtPosition());
		outState.putInt("currentCategoryPosition", this.currentCategoryPosition);
		this.saveAllCatArtsInfo(outState);

		outState.putStringArrayList(KEY_ALL_CAT_AND_AUT_TITLES_LIST, allCatAndAutTitles);
		outState.putStringArrayList(KEY_ALL_CAT_AND_AUT_URLS_LIST, allCatAndAutURLs);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		this.categoryToLoad = savedInstanceState.getString("categoryToLoad");
		this.setCurArtPosition(savedInstanceState.getInt("position"));
		currentCategoryPosition = savedInstanceState.getInt("currentCategoryPosition");
		this.restoreAllCatArtsInfo(savedInstanceState);

		this.allCatAndAutTitles = savedInstanceState.getStringArrayList(KEY_ALL_CAT_AND_AUT_TITLES_LIST);
		this.allCatAndAutURLs = savedInstanceState.getStringArrayList(KEY_ALL_CAT_AND_AUT_URLS_LIST);
	}

	public static enum ActionDrawableState
	{
		BURGER, ARROW
	}

	public static void toggleActionBarIcon(ActionDrawableState state, final ActionBarDrawerToggle toggle,
	boolean animate)
	{
		if (animate)
		{
			float start = state == ActionDrawableState.BURGER ? 0f : 1.0f;
			float end = Math.abs(start - 1);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			{
				ValueAnimator offsetAnimator = ValueAnimator.ofFloat(start, end);
				offsetAnimator.setDuration(300);
				offsetAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
				offsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
				{
					@Override
					public void onAnimationUpdate(ValueAnimator animation)
					{
						float offset = (Float) animation.getAnimatedValue();
						toggle.onDrawerSlide(null, offset);
					}
				});
				offsetAnimator.start();
			}
			else
			{
				//do the same with nine-old-androids lib :)
			}
		}
		else
		{
			if (state == ActionDrawableState.BURGER)
			{
				toggle.onDrawerClosed(null);
			}
			else
			{
				toggle.onDrawerOpened(null);
			}
		}
	}
}
