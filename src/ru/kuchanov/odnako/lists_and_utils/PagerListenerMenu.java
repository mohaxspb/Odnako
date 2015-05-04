/*
 12.02.2015
PagerListenerMenu.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityMain;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class PagerListenerMenu extends ViewPager.SimpleOnPageChangeListener
{
	final static String LOG = PagerListenerMenu.class.getSimpleName() + "/";

	private ActivityMain act;

	private boolean twoPane;

	private ViewPager pagerRight;

	private Toolbar toolbar;

	int currentCategoryPosition;

	public PagerListenerMenu(ActivityMain act)
	{
		this.act = act;
		this.twoPane = PreferenceManager.getDefaultSharedPreferences(this.act).getBoolean("twoPane", false);

		this.pagerRight = (ViewPager) act.findViewById(R.id.pager_right);

		this.toolbar = (Toolbar) act.findViewById(R.id.toolbar);

		this.currentCategoryPosition = this.act.getCurentCategoryPosition();
	}

	@Override
	public void onPageSelected(int position)
	{
		Log.d(LOG, "select position= " + position);
		//this will set current pos, and adapters group/child pos
		this.act.setCurentCategoryPosition(position);
		this.currentCategoryPosition = position;

		setTitleDrawerItemToolbarTopImgETC(position);

		if (twoPane)
		{
			String[] menuLinks = CatData.getMenuLinks(act);
			if (currentCategoryPosition == 3)
			{
				//show all authors adapters
				PagerAdapterAllAuthors pagerRightAdapter = new PagerAdapterAllAuthors(
				act.getSupportFragmentManager(), act);
				pagerRight.setAdapter(pagerRightAdapter);
				//				pagerRight.setPageTransformer(true, new RotationPageTransformer());
				OnPageChangeListener listener = new PagerListenerAllAuthors(act, pagerRightAdapter.getAllAuthorsList());
				pagerRight.setOnPageChangeListener(listener);
				int curPos = act.getAllCatListsSelectedArtPosition().get(menuLinks[currentCategoryPosition]);
				pagerRight.setCurrentItem(curPos, true);

				if (curPos == 0)
				{
					listener.onPageSelected(curPos);
				}
			}
			else if (currentCategoryPosition == 13)
			{
				//show all categories adapters
				PagerAdapterAllCategories pagerRightAdapter = new PagerAdapterAllCategories(
				act.getSupportFragmentManager(), act);
				pagerRight.setAdapter(pagerRightAdapter);
				//				pagerRight.setPageTransformer(true, new RotationPageTransformer());
				OnPageChangeListener listener = new PagerListenerAllCategories(act,
				pagerRightAdapter.getAllCategoriesList());
				pagerRight.setOnPageChangeListener(listener);
				int curPos = act.getAllCatListsSelectedArtPosition().get(menuLinks[currentCategoryPosition]);
				pagerRight.setCurrentItem(curPos, true);

				if (curPos == 0)
				{
					listener.onPageSelected(curPos);
				}
			}//13
			else
			{
				String categoryForRightPager = menuLinks[currentCategoryPosition];

				//Log.d(LOG, "categoryForRightPager= " + categoryForRightPager);

				PagerAdapterArticles adapterLeft = new PagerAdapterArticles(act.getSupportFragmentManager(),
				categoryForRightPager, act);
				
				pagerRight.setAdapter(adapterLeft);
				pagerRight.getAdapter().notifyDataSetChanged();
				//				pagerRight.setPageTransformer(true, new RotationPageTransformer());
				OnPageChangeListener listener = new PagerListenerArticle(this.act, categoryForRightPager);
				pagerRight.setOnPageChangeListener(listener);
				int curPos = act.getAllCatListsSelectedArtPosition().get(categoryForRightPager);
				pagerRight.setCurrentItem(curPos, true);

				if (curPos == 0)
				{
					listener.onPageSelected(curPos);
				}
			}//not 3 neither 13
		}//twoPane
	}//onPageChangeListener

	private void setTitleDrawerItemToolbarTopImgETC(int position)
	{
		this.toolbar.setTitle(CatData.getMenuNames(act)[position]);

		//show toolbar when switch category to show it's title
		//restore and set topImg position
		String[] allMenuCatsLinks = CatData.getMenuLinks(act);
		String curCatLink = allMenuCatsLinks[position];
		int toolbarY = this.act.getAllCatToolbarTopImgYCoord().get(curCatLink)[0];
		int initialDistance = this.act.getAllCatToolbarTopImgYCoord().get(curCatLink)[2];
		int currentDistance = this.act.getAllCatToolbarTopImgYCoord().get(curCatLink)[3];

		if (toolbarY < 0)
		{
			toolbar.setY(0);
			toolbar.getBackground().setAlpha(255);
		}
		else
		{
			toolbar.setY(0);

			float percent = (float) currentDistance / (float) initialDistance;
			float gradient = 1f - percent;
			int newAlpha = (int) (255 * gradient);
			toolbar.getBackground().setAlpha(newAlpha);
		}

		//menuOptions
		Menu menu = toolbar.getMenu();
		MenuItem refresh = menu.findItem(R.id.refresh);
		MenuItem search = menu.findItem(R.id.action_search);
		if (search == null)
		{
			//this may be if onCreateOptionsMenu called after onResume
		}
		else
		{
			if (position == 3 || position == 13)
			{
				search.setVisible(true);
				refresh.setVisible(false);
			}
			else
			{
				search.setVisible(false);
				search.collapseActionView();
				refresh.setVisible(true);
			}
		}
	}
}