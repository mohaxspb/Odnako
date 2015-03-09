/*
 12.02.2015
PagerListenerMenu.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.activities.ActivityMain;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class PagerListenerArticle extends ViewPager.SimpleOnPageChangeListener
{
	final static String LOG = PagerListenerArticle.class.getSimpleName();

	ActivityBase act;

	boolean twoPane;

	//ViewPager and it's adapter for articles/comments
	ViewPager artCommsPager;
	PagerAdapter artCommsPagerAdapter;

	//ViewPager and it's adapter for artsLists
	ViewPager artsListPager;
	PagerAdapter artsListPagerAdapter;

	Toolbar toolbarRight;
	Toolbar toolbar;

	int currentCategoryPosition;

	String categoryToLoad;

	public PagerListenerArticle(ActivityBase act, String categoryToLoad)
	{
		this.act = act;

		this.categoryToLoad = categoryToLoad;

		this.twoPane = PreferenceManager.getDefaultSharedPreferences(this.act).getBoolean("twoPane", false);

		this.artCommsPager = (ViewPager) act.findViewById(R.id.pager_right);

		this.toolbar = (Toolbar) act.findViewById(R.id.toolbar);
		this.toolbarRight = (Toolbar) act.findViewById(R.id.toolbar_right);

		this.currentCategoryPosition = this.act.getCurentCategoryPosition();
	}

	@Override
	public void onPageSelected(int position)
	{
		Log.d(LOG, "onPageSelected in articlePager; position: " + position);
		//on MainActivity it can we shown ONLY in tablet mode
		//else it's ActivityArticle!
		if (this.twoPane)
		{
			ActivityMain mainActivity = (ActivityMain) this.act;
			//move topImg and toolBar while scrolling left list
//			toolbar.setY(0 - toolbar.getHeight());
			toolbar.setY(0);
			toolbar.getBackground().setAlpha(255);

			toolbarRight.setTitle("Статья " + String.valueOf(position + 1) + "/" + artCommsPager.getAdapter().getCount());

			mainActivity.getAllCatListsSelectedArtPosition().put(categoryToLoad, position);

			Intent intentToListFrag = new Intent(categoryToLoad + "art_position");
			intentToListFrag.putExtra("position", position);
			LocalBroadcastManager.getInstance(act).sendBroadcast(intentToListFrag);
		}
		else
		{
			toolbar.setY(0);
		}
		//TODO here we can notify Article fragment, that it's selected
	}
}
