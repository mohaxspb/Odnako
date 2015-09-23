/*
 21.10.2014
ActivityArticle.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import java.util.ArrayList;
import java.util.HashMap;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Favorites;
import ru.kuchanov.odnako.fragments.FragmentArticle;
import ru.kuchanov.odnako.fragments.FragmentComments;
import ru.kuchanov.odnako.fragments.FragmentDialogTextAppearance;
import ru.kuchanov.odnako.lists_and_utils.Actions;
import ru.kuchanov.odnako.lists_and_utils.PagerAdapterArticles;
import ru.kuchanov.odnako.lists_and_utils.PagerListenerArticle;
import ru.kuchanov.odnako.utils.CheckTimeToAds;
import ru.kuchanov.odnako.utils.DialogShare;
import ru.kuchanov.odnako.utils.ServiceTTS;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;
import com.yandex.metrica.YandexMetrica;
//import ru.kuchanov.odnako.animations.RotationPageTransformer;

public class ActivityArticle extends ActivityBase
{
	private static final String LOG = ActivityArticle.class.getSimpleName();

	private ViewPager pager;
	private PagerAdapter pagerAdapter;

	InterstitialAd mInterstitialAd;

	protected void onCreate(Bundle savedInstanceState)
	{
		Log.i(LOG, "onCreate");

		this.act = this;
		//get default settings to get all settings later
		PreferenceManager.setDefaultValues(this, R.xml.pref_design, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_notifications, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_system, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_about, true);
		this.pref = PreferenceManager.getDefaultSharedPreferences(act);
		//change text size prefs from String type to float
		this.reorganizeTextSizePreferences();

		// bind to service
		this.bindService();

		//ADS
		if (!this.pref.contains(CheckTimeToAds.PREF_KEY_MAX_IN_APP_PERIOD))
		{
			CheckTimeToAds.setMaxInAppPeriod(act, 60L * 60L * 1000L);
		}
		this.checkTimeAds = new CheckTimeToAds(this, mInterstitialAd);

		//set theme before super and set content to apply it
		boolean nightModeIsOn = this.pref.getBoolean(ActivityPreference.PREF_KEY_NIGHT_MODE, false);
		int themeID = R.style.ThemeLight;
		switch (this.pref.getString(ActivityPreference.PREF_KEY_THEME, ActivityPreference.THEME_GREY))
		{
			case ActivityPreference.THEME_GREY:
				themeID = (nightModeIsOn) ? R.style.ThemeDark : R.style.ThemeLight;
			break;
			case ActivityPreference.THEME_INDIGO:
				themeID = (nightModeIsOn) ? R.style.ThemeDarkIndigo : R.style.ThemeLightIndigo;
			break;
			case ActivityPreference.THEME_RED:
				themeID = (nightModeIsOn) ? R.style.ThemeDarkRed : R.style.ThemeLightRed;
			break;
			case ActivityPreference.THEME_TEAL:
				themeID = (nightModeIsOn) ? R.style.ThemeDarkTeal : R.style.ThemeLightTeal;
			break;
			case ActivityPreference.THEME_GREEN:
				themeID = (nightModeIsOn) ? R.style.ThemeDarkGreen : R.style.ThemeLightGreen;
			break;
			case ActivityPreference.THEME_AMBER:
				themeID = (nightModeIsOn) ? R.style.ThemeDarkAmber : R.style.ThemeLightAmber;
			break;
		}
		this.setTheme(themeID);

		super.onCreate(savedInstanceState);

		////CHECK HERE SITUATION WHEN WE LAUNCH THIS ACTIVITY WITH TWOPANE MODE
		//that's can be if we open settings from article activity
		//and enable twoPane
		//		remove to support catching urls
		//		if (this.pref.getBoolean("twoPane", false))
		//		{
		//			Intent intent = new Intent(act, ActivityMain.class);
		//			//set flags to prevent restoring activity from backStack and create really new instance
		//			//with given categories number
		//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		//			act.startActivity(intent);
		//			this.finish();
		//			return;
		//		}

		this.setContentView(R.layout.activity_article);

		//restore state
		Bundle stateFromIntent = this.getIntent().getExtras();
		if (stateFromIntent != null)
		{
			this.curAllArtsInfo = stateFromIntent.getParcelableArrayList(Article.KEY_ALL_ART_INFO);
			//			this.categoryToLoad = stateFromIntent.getString("categoryToLoad");
			this.setCurrentCategory(stateFromIntent.getString("categoryToLoad"));
			this.setCurArtPosition(stateFromIntent.getInt("position"));
			this.currentCategoryPosition = stateFromIntent.getInt("curentCategoryPosition");

			this.allCatArtsInfo = new HashMap<String, ArrayList<Article>>();
			this.allCatArtsInfo.put(this.getCurrentCategory(), curAllArtsInfo);

			this.restoreGroupChildPosition(stateFromIntent);

			Log.d(LOG, "stateFromIntent");
			Log.i(LOG, "allCatArtsInfo.size(): " + allCatArtsInfo.size());
			for (int i = 0; i < allCatArtsInfo.size(); i++)
			{
				Log.i(LOG, allCatArtsInfo.get(this.getCurrentCategory()).get(0).getTitle());
			}
		}
		if (savedInstanceState != null)
		{
			this.setCurrentCategory(savedInstanceState.getString("categoryToLoad"));
			this.setCurArtPosition(savedInstanceState.getInt("position"));
			this.currentCategoryPosition = savedInstanceState.getInt("curentCategoryPosition");

			this.restoreAllCatArtsInfo(savedInstanceState);

			this.curAllArtsInfo = this.allCatArtsInfo.get(getCurrentCategory());

			this.allCatAndAutTitles = savedInstanceState.getStringArrayList(KEY_ALL_CAT_AND_AUT_TITLES_LIST);
			this.allCatAndAutURLs = savedInstanceState.getStringArrayList(KEY_ALL_CAT_AND_AUT_URLS_LIST);

			this.restoreGroupChildPosition(savedInstanceState);

			Log.d(LOG, "savedInstanceState");
			Log.i(LOG, "allCatArtsInfo.size(): " + allCatArtsInfo.size());
			for (int i = 0; i < allCatArtsInfo.size(); i++)
			{
				Log.i(LOG, allCatArtsInfo.get(this.getCurrentCategory()).get(0).getTitle());
			}
		}

		//drawer settings
		this.setNavDrawer();
		//set arrow; It'll be better
		((ActivityBase) act).mDrawerToggle.setDrawerIndicatorEnabled(false);
		act.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		////End of drawer settings

		//setting toolbar
		this.toolbar.getBackground().setAlpha(255);

		this.getAllCatAndAutURLs();
		this.getAllCatAndAutTitles();

		this.pager = (ViewPager) this.findViewById(R.id.pager_right);
		//		this.pager.setPageTransformer(true, new RotationPageTransformer());
		this.pagerAdapter = new PagerAdapterArticles(this.getSupportFragmentManager(), getCurrentCategory(), act);
		this.pager.setAdapter(pagerAdapter);
		PagerListenerArticle listener = new PagerListenerArticle(this, getCurrentCategory());
		this.pager.addOnPageChangeListener(listener);
		this.pager.setCurrentItem(getCurArtPosition(), true);
		if (getCurArtPosition() == 0)
		{
			listener.onPageSelected(0);
		}

		//check if it first launch (no saved state) and intents bundle contains command to open comments 
		if (savedInstanceState == null && stateFromIntent.containsKey(FragmentComments.LOG))
		{
			Actions.showComments(curAllArtsInfo, this.getCurArtPosition(), getCurrentCategory(), act);
		}

		//adMob
		//this.AddAds();
		//end of adMob
	}

	@Override
	public void onPause()
	{
		YandexMetrica.onPauseActivity(this);

		this.checkTimeAds.onPause();
		//		adView.pause();
		super.onPause();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		YandexMetrica.onResumeActivity(this);

		this.checkTimeAds.onResume();

		//TODO find why here we have default title of ActionBar;
		ActivityArticle articleActivity = (ActivityArticle) this.act;
		String categotiesTitle = "";

		for (int i = 0; i < articleActivity.getAllCatAndAutURLs().size(); i++)
		{
			String s = articleActivity.getAllCatAndAutURLs().get(i);
			if (s.equals(this.getCurrentCategory()))
			{
				categotiesTitle = articleActivity.getAllCatAndAutTitles().get(i);
				this.toolbar.setTitle(categotiesTitle + " " + String.valueOf(this.getCurArtPosition() + 1) + "/"
				+ articleActivity.getAllCatArtsInfo().get(getCurrentCategory()).size());
				break;
			}
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

		if (this.getSupportFragmentManager().findFragmentByTag(FragmentComments.LOG) != null)
		{
			mDrawerToggle.setDrawerIndicatorEnabled(false);
			//hide comments and share buttons
			menu.findItem(R.id.comments).setVisible(false);
			menu.findItem(R.id.share).setVisible(false);
		}
		else if (this.getSupportFragmentManager().findFragmentByTag(FragmentArticle.LOG) != null)
		{
			this.mDrawerToggle.setDrawerIndicatorEnabled(false);
			//show previously hided comments and share buttons
			menu.findItem(R.id.comments).setVisible(true);
			menu.findItem(R.id.share).setVisible(true);
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
		boolean nightModeIsOn = this.pref.getBoolean("night_mode", false);
		switch (item.getItemId())
		{
			case android.R.id.home:
				//called when the up affordance/carat in actionbar is pressed
				onBackPressed();
				return true;
			case R.id.action_settings_all:
				//set theme btn checked if theme is dark
				MenuItem themeMenuItem = item.getSubMenu().findItem(R.id.theme_dark);
				if (nightModeIsOn)
				{
					themeMenuItem.setChecked(true);
				}
				return true;
			case R.id.comments:
				if (this.getSupportFragmentManager().findFragmentByTag(FragmentArticle.LOG) != null)
				{
					FragmentArticle upperArtFrag = (FragmentArticle) this.getSupportFragmentManager()
					.findFragmentByTag(FragmentArticle.LOG);
					Actions.addCommentsFrgament(upperArtFrag.getArticle(), act);
				}
				else
				{
					Actions.showComments(curAllArtsInfo, getCurArtPosition(), getCurrentCategory(), act);
				}
				return true;
			case R.id.share:
				if (this.getSupportFragmentManager().findFragmentByTag(FragmentArticle.LOG) != null)
				{
					FragmentArticle upperArtFrag = (FragmentArticle) this.getSupportFragmentManager()
					.findFragmentByTag(FragmentArticle.LOG);
					//					Actions.shareUrl(upperArtFrag.getArticle().getUrl(), this.act);
					DialogShare.showChoiceDialog(act, upperArtFrag.getArticle(), DialogShare.SHARE_TYPE_ALL);
				}
				else
				{
					DialogShare.showChoiceDialog(act,
					this.allCatArtsInfo.get(getCurrentCategory()).get(this.getCurArtPosition()),
					DialogShare.SHARE_TYPE_ALL);
				}
				return true;
			case R.id.add_to_favorites:
				if (this.getSupportFragmentManager().findFragmentByTag(FragmentArticle.LOG) != null)
				{
					FragmentArticle upperArtFrag = (FragmentArticle) this.getSupportFragmentManager()
					.findFragmentByTag(FragmentArticle.LOG);
					if (upperArtFrag.getArticle() != null)
					{
						Favorites.addFavorite(act, Favorites.KEY_ARTICLES, upperArtFrag.getArticle().getUrl(),
						upperArtFrag.getArticle().getTitle());
						this.drawerRightRecyclerView.getAdapter().notifyDataSetChanged();
					}
					else
					{
						Toast.makeText(act, "Добавить в избранное почему-то не получилось", Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Article curArt = this.allCatArtsInfo.get(getCurrentCategory()).get(this.getCurArtPosition());
					if (curArt != null)
					{
						Favorites.addFavorite(act, Favorites.KEY_ARTICLES, curArt.getUrl(),
						curArt.getTitle());
						this.drawerRightRecyclerView.getAdapter().notifyDataSetChanged();
					}
					else
					{
						Toast.makeText(act, "Добавить в избранное почему-то не получилось", Toast.LENGTH_SHORT).show();
					}
				}
				return true;
			case R.id.action_settings:
				item.setIntent(new Intent(this, ActivityPreference.class));
				return super.onOptionsItemSelected(item);
			case R.id.theme_dark:
				if (nightModeIsOn)
				{
					this.pref.edit().putBoolean(ActivityPreference.PREF_KEY_NIGHT_MODE, false).commit();
				}
				else
				{
					this.pref.edit().putBoolean(ActivityPreference.PREF_KEY_NIGHT_MODE, true).commit();
				}
				this.recreate();
				return super.onOptionsItemSelected(item);
			case R.id.text_size:
				FragmentDialogTextAppearance frag = FragmentDialogTextAppearance.newInstance();
				frag.show(act.getFragmentManager(), "TextAppearance");
				return super.onOptionsItemSelected(item);
			case R.id.speeck:
				Intent intentTTS = new Intent(act.getApplicationContext(), ServiceTTS.class);
				intentTTS.setAction("init");
				if (this.getSupportFragmentManager().findFragmentByTag(FragmentArticle.LOG) != null)
				{
					FragmentArticle upperArtFrag = (FragmentArticle) this.getSupportFragmentManager()
					.findFragmentByTag(FragmentArticle.LOG);
					ArrayList<Article> artList = new ArrayList<Article>();
					artList.add(upperArtFrag.getArticle());
					intentTTS.putParcelableArrayListExtra(FragmentArticle.ARTICLE_URL, artList);
					intentTTS.putExtra("position", 0);
				}
				else
				{
					intentTTS.putParcelableArrayListExtra(FragmentArticle.ARTICLE_URL, this.curAllArtsInfo);
					intentTTS.putExtra("position", this.getCurArtPosition());
				}
				act.startService(intentTTS);
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
		outState.putString("categoryToLoad", getCurrentCategory());
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
		this.setCurrentCategory(savedInstanceState.getString("categoryToLoad"));
		this.setCurArtPosition(savedInstanceState.getInt("position"));
		currentCategoryPosition = savedInstanceState.getInt("currentCategoryPosition");
		this.restoreAllCatArtsInfo(savedInstanceState);

		this.allCatAndAutTitles = savedInstanceState.getStringArrayList(KEY_ALL_CAT_AND_AUT_TITLES_LIST);
		this.allCatAndAutURLs = savedInstanceState.getStringArrayList(KEY_ALL_CAT_AND_AUT_URLS_LIST);
	}

	@Override
	public void onBackPressed()
	{
		Log.e(LOG, "onBackPressed");
		if (this.getSupportFragmentManager().findFragmentByTag(FragmentComments.LOG) != null)
		{
			Log.e(LOG, "findFragmentByTag(FragmentComments.LOG) != null");
			final Toolbar toolbar = (Toolbar) act.findViewById(R.id.toolbar);
			//check if we have also article frag in manager and if not - show hamburger
			if (this.getSupportFragmentManager().findFragmentByTag(FragmentArticle.LOG) == null)
			{
				//restore title of toolbar via calling to onPageSelected of pager's listener
				PagerListenerArticle.setTitleToToolbar(this.getCurrentCategory(), this, twoPane,
				this.getCurArtPosition());
			}
			else
			{
				//set toolbar title
				toolbar.setTitle("Статья");
			}

			//show previously hided comments and share buttons				
			Menu menu = toolbar.getMenu();
			MenuItem comments = menu.findItem(R.id.comments);
			MenuItem share = menu.findItem(R.id.share);
			comments.setVisible(true);
			share.setVisible(true);

			super.onBackPressed();
			this.recreate();
			return;
		}
		if (this.getSupportFragmentManager().findFragmentByTag(FragmentArticle.LOG) != null)
		{
			Log.e(LOG, "findFragmentByTag(FragmentArticle.LOG) != null");
			//restore toolbars title
			PagerListenerArticle.setTitleToToolbar(getCurrentCategory(), this, twoPane, this.getCurArtPosition());

			super.onBackPressed();
			this.recreate();
			return;
		}
		if (mDrawerLayout.isDrawerOpen(Gravity.START))
		{
			this.mDrawerLayout.closeDrawer(Gravity.LEFT);
			return;
		}
		else
		{
			Log.e(LOG, "mDrawerLayout.isDrawerOpen(Gravity.START)=false");
			this.finish();
			return;
		}
	}
}