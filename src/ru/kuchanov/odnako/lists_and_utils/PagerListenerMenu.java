/*
 12.02.2015
PagerListenerMenu.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.animations.RotationPageTransformer;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class PagerListenerMenu extends ViewPager.SimpleOnPageChangeListener
{
	final static String LOG = PagerListenerMenu.class.getSimpleName() + "/";

	ActivityMain act;

	boolean twoPane;

	ViewPager pagerRight;

	ViewPager pagerLeft;

	Toolbar toolbarRight;
	Toolbar toolbar;

	int currentCategoryPosition;

	public PagerListenerMenu(ActivityMain act)
	{
		this.act = act;
		this.twoPane = PreferenceManager.getDefaultSharedPreferences(this.act).getBoolean("twoPane", false);

		this.pagerLeft = (ViewPager) act.findViewById(R.id.pager_left);

		this.pagerRight = (ViewPager) act.findViewById(R.id.pager_right);

		this.toolbar = (Toolbar) act.findViewById(R.id.toolbar);
		this.toolbarRight = (Toolbar) act.findViewById(R.id.toolbar_right);

		this.currentCategoryPosition = this.act.getCurentCategoryPosition();
	}

	@Override
	public void onPageSelected(int position)
	{
		Log.d(LOG, "select artsListPager position= " + position);
		//this will set current pos, and adapters group/child pos
		this.act.setCurentCategoryPosition(position);
		this.currentCategoryPosition = position;

		//setTitle to toolbar

		setTitleDrawerItemToolbarTopImgETC(position);

		//		//sending intent to listfrag to notify it's adapter to fix issue
		//		//when there is only 1-st art is shown and other can be shown only from articlesPager
		//		//when switching articles
		String[] allCatsLinks = CatData.getAllCategoriesMenuLinks(act);
		Intent intentToListFrag = new Intent(allCatsLinks[currentCategoryPosition] + "_notify_that_selected");
		LocalBroadcastManager.getInstance(act).sendBroadcast(intentToListFrag);
		if (twoPane)
		{
			if (currentCategoryPosition != 3 && currentCategoryPosition != 13)
			{
//				toolbarRight.setTitle("");

				String categoryForRightPager = CatData.getAllCategoriesMenuLinks(act)[currentCategoryPosition];
				PagerAdapterArticles adapterLeft = new PagerAdapterArticles(act.getSupportFragmentManager(),
				categoryForRightPager,
				act);
				pagerRight.setAdapter(adapterLeft);
				pagerRight.setPageTransformer(true, new RotationPageTransformer());
				OnPageChangeListener listener = new PagerListenerArticle(this.act, categoryForRightPager);
				pagerRight.setOnPageChangeListener(listener);
				int curPos = act.getAllCatListsSelectedArtPosition().get(allCatsLinks[currentCategoryPosition]);
				pagerRight.setCurrentItem(curPos, true);

				if (curPos == 0)
				{
					listener.onPageSelected(curPos);
				}
			}
			else if (currentCategoryPosition == 3)
			{
				//show all authors adapters
//				int curPos = act.getAllCatListsSelectedArtPosition().get(allCatsLinks[currentCategoryPosition]);
//				Log.e(LOG, "selectedArtPosition: "+curPos);
				PagerAdapterAuthorsLists pagerRightAdapter = new PagerAdapterAuthorsLists(
				act.getSupportFragmentManager(), act);
				pagerRight.setAdapter(pagerRightAdapter);
				pagerRight.setPageTransformer(true, new RotationPageTransformer());
				OnPageChangeListener listener = new PagerListenerAllAuthors(act, pagerRightAdapter.getAllAuthorsList());
				pagerRight.setOnPageChangeListener(listener);
				int curPos = act.getAllCatListsSelectedArtPosition().get(allCatsLinks[currentCategoryPosition]);
				Log.e(LOG, "selectedArtPosition: "+curPos);
				pagerRight.setCurrentItem(curPos, true);

				if (curPos == 0)
				{
					listener.onPageSelected(curPos);
				}
			}
			else if (currentCategoryPosition == 13)
			{
				//TODO show all categories adapters
			}
		}
	}

	private void setTitleDrawerItemToolbarTopImgETC(int position)
	{
		//		this.act.setTitle(CatData.getAllCategoriesMenuNames(act)[position]); XXX
		this.toolbar.setTitle(CatData.getAllCategoriesMenuNames(act)[position]);

		//show toolbar when switch category to show it's title
		//restore and set topImg position
		String[] allMenuCatsLinks = CatData.getAllCategoriesMenuLinks(act);
		String curCatLink = allMenuCatsLinks[position];
		int toolbarY = this.act.getAllCatToolbarTopImgYCoord().get(curCatLink)[0];
		//		int topImgY = allCatToolbarTopImgYCoord.get(curCatLink)[1];
		int initialDistance = this.act.getAllCatToolbarTopImgYCoord().get(curCatLink)[2];
		int currentDistance = this.act.getAllCatToolbarTopImgYCoord().get(curCatLink)[3];
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
}
