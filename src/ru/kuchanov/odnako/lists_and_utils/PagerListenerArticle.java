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
	final static String LOG_TAG = PagerListenerArticle.class.getSimpleName();

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

		this.artCommsPager = (ViewPager) act.findViewById(R.id.article_comments_container);

		this.toolbar = (Toolbar) act.findViewById(R.id.toolbar);
		this.toolbarRight = (Toolbar) act.findViewById(R.id.toolbar_right);

		this.currentCategoryPosition = this.act.getCurentCategoryPosition();
	}

	@Override
	public void onPageSelected(int position)
	{
		//on MainActivity it can we shown ONLY in tablet mode
		//else it's ActivityArticle!
		if (this.twoPane)
		{
			ActivityMain mainActivity = (ActivityMain) this.act;
			//move topImg and toolBar while scrolling left list
			toolbar.setY(0 - toolbar.getHeight());

			toolbarRight.setTitle("Статья " + String.valueOf(position + 1) + "/" + artCommsPager.getAdapter().getCount());
			Log.d(LOG_TAG, "onPageSelected in articlePager; position: " + position);

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

	//	private void setTitleDrawerItemToolbarTopImgETC(int position)
	//	{
	//		String title = "";
	//		title = CatData.getAllCategoriesMenuNames(act)[position];
	//		this.act.setTitle(title);
	//
	//
	//		//show toolbar when switch category to show it's title
	//		//restore and set topImg position
	//		String[] allMenuCatsLinks = CatData.getAllCategoriesMenuLinks(act);
	//		String curCatLink = allMenuCatsLinks[position];
	//		int toolbarY = this.act.getAllCatToolbarTopImgYCoord().get(curCatLink)[0];
	//		int initialDistance = this.act.getAllCatToolbarTopImgYCoord().get(curCatLink)[2];
	//		int currentDistance = this.act.getAllCatToolbarTopImgYCoord().get(curCatLink)[3];
	//
	//		if (toolbarY < 0)
	//		{
	//			toolbar.getBackground().setAlpha(255);
	//			toolbar.setY(0);
	//		}
	//		else
	//		{
	//			toolbar.setY(0);
	//
	//			float percent = (float) currentDistance / (float) initialDistance;
	//			float gradient = 1f - percent;
	//			int newAlpha = (int) (255 * gradient);
	//			toolbar.getBackground().setAlpha(newAlpha);
	//		}
	//	}
}
