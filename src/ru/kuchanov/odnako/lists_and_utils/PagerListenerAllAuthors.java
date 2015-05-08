/*
 12.02.2015
PagerListenerMenu.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.db.Author;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class PagerListenerAllAuthors extends ViewPager.SimpleOnPageChangeListener
{
	private final static String LOG = PagerListenerAllAuthors.class.getSimpleName();

	private ActivityMain act;

	private boolean twoPane;

	private boolean isInRightPager;

	//ViewPager and it's adapter for articles/comments
	private ViewPager pagerRight;
	private PagerAdapter pagerRightAdapter;

	private Toolbar toolbarRight;
	private Toolbar toolbar;

	private int currentCategoryPosition;

	private final ArrayList<Author> allAuthors;

	private final ArrayList<String> allAuthorsUrls;

	public PagerListenerAllAuthors(final ActivityMain act, final ArrayList<Author> allAuthors)
	{
		this.act = act;

		this.allAuthors = allAuthors;

		this.twoPane = PreferenceManager.getDefaultSharedPreferences(this.act).getBoolean("twoPane", false);

		this.pagerRight = (ViewPager) act.findViewById(R.id.pager_right);

		this.toolbar = (Toolbar) act.findViewById(R.id.toolbar);
		this.toolbarRight = (Toolbar) act.findViewById(R.id.toolbar_right);

		this.currentCategoryPosition = this.act.getCurentCategoryPosition();

		this.isInRightPager = (this.currentCategoryPosition == 3 && act.getPagerType() == ActivityMain.PAGER_TYPE_MENU && twoPane);

		this.allAuthorsUrls = new ArrayList<String>();
		for (Author a : allAuthors)
		{
			allAuthorsUrls.add(a.getBlog_url());
		}
	}

	@Override
	public void onPageSelected(int position)
	{
		Log.d(LOG, "select pagerListenerAllAuthors position = " + position);
		if (isInRightPager)
		{
			//is in right (MenuPager, 3 position)
			String[] menuUrls = CatData.getMenuLinks(act);

//			this.act.getAllCatListsSelectedArtPosition().put(allAuthorsUrls.get(position), position);
			this.act.getAllCatListsSelectedArtPosition().put(menuUrls[3], position);

			this.toolbarRight.setTitle(this.allAuthors.get(position).getName());

			//notify allAuthors frag about author selected
			Intent intentToAllAuthorsFrag = new Intent(menuUrls[3] + "art_position");
			intentToAllAuthorsFrag.putExtra("position", position);
			LocalBroadcastManager.getInstance(act).sendBroadcast(intentToAllAuthorsFrag);
		}//isInRightPager
		else
		{
			//is in left, so it isn't MenuPager
			//update currentCategoryPosition
			this.act.setCurentCategoryPosition(position);
			this.currentCategoryPosition = position;

			this.toolbar.setTitle(this.allAuthors.get(position).getName());
			Menu menu = toolbar.getMenu();
			MenuItem search = menu.findItem(R.id.action_search);
//			MenuItem refresh = menu.findItem(R.id.refresh);
			if (search == null)
			{
				//can be if menu populated after on resume and we hide elements there
			}
			else
			{
				search.setVisible(true);
//				refresh.setVisible(true);
			}
			//if twoPane we must set rightPager

			if (twoPane)
			{
				String curentCategory = allAuthorsUrls.get(currentCategoryPosition);

				pagerRightAdapter = new PagerAdapterArticles(act.getSupportFragmentManager(),
				curentCategory, act);
				pagerRight.setAdapter(pagerRightAdapter);
//				pagerRight.setPageTransformer(true, new RotationPageTransformer());
				OnPageChangeListener listener = new PagerListenerArticle(act, curentCategory);
				pagerRight.setOnPageChangeListener(listener);

				int curPos = this.act.getAllCatListsSelectedArtPosition().get(curentCategory);
				pagerRight.setCurrentItem(curPos, true);

				if (curPos == 0)
				{
					listener.onPageSelected(curPos);
				}
			}//if twoPane
		}//!isInRightPager
	}//onPageSelected
}//Class
