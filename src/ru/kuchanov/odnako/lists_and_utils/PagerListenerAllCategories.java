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
import ru.kuchanov.odnako.animations.RotationPageTransformer;
import ru.kuchanov.odnako.db.Category;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class PagerListenerAllCategories extends ViewPager.SimpleOnPageChangeListener
{
	final static String LOG = PagerListenerAllCategories.class.getSimpleName() + "/";

	private ActivityMain act;

	private boolean twoPane;

	private boolean isInRightPager;

	//ViewPager and it's adapter for articles/comments
	private ViewPager pagerRight;
	private PagerAdapter pagerRightAdapter;

	private Toolbar toolbarRight;
	private Toolbar toolbar;

	private int currentCategoryPosition;

	private final ArrayList<Category> allCategories;

	private final ArrayList<String> allCategoriesUrls;

	public PagerListenerAllCategories(final ActivityMain act, final ArrayList<Category> allCategories)
	{
		this.act = act;

		this.allCategories = allCategories;

		this.twoPane = PreferenceManager.getDefaultSharedPreferences(this.act).getBoolean("twoPane", false);

		this.pagerRight = (ViewPager) act.findViewById(R.id.pager_right);

		this.toolbar = (Toolbar) act.findViewById(R.id.toolbar);
		this.toolbarRight = (Toolbar) act.findViewById(R.id.toolbar_right);

		this.currentCategoryPosition = this.act.getCurentCategoryPosition();

		this.isInRightPager = (this.currentCategoryPosition == 13 && act.getPagerType() == ActivityMain.PAGER_TYPE_MENU && twoPane);

		this.allCategoriesUrls = new ArrayList<String>();
		for (Category a : allCategories)
		{
			allCategoriesUrls.add(a.getUrl());
		}
	}

	@Override
	public void onPageSelected(int position)
	{
//		Log.d(LOG, "select pagerListenerAllAuthors position = " + position);
		if (isInRightPager)
		{
			//is in right (MenuPager, 3 position)
			String[] menuUrls = CatData.getMenuLinks(act);

			this.act.getAllCatListsSelectedArtPosition().put(menuUrls[13], position);

			this.toolbarRight.setTitle(this.allCategories.get(position).getTitle());

			//notify allCategories frag about author selected
			Intent intentToAllAuthorsFrag = new Intent(menuUrls[13] + "art_position");
			intentToAllAuthorsFrag.putExtra("position", position);
			LocalBroadcastManager.getInstance(act).sendBroadcast(intentToAllAuthorsFrag);
		}
		else
		{
			//is in left, so it isn't MenuPager
			//update currentCategoryPosition
			this.act.setCurentCategoryPosition(position);
			this.currentCategoryPosition = position;

			this.toolbar.setTitle(this.allCategories.get(position).getTitle());
			Menu menu = toolbar.getMenu();
			MenuItem search = menu.findItem(R.id.action_search);
			MenuItem refresh = menu.findItem(R.id.refresh);
			if (search == null)
			{
				//can be if menu populated after on resume and we hide elements there
			}
			else
			{
				search.setVisible(true);
				refresh.setVisible(true);
			}

			//if twoPane we must set rightPager
			if (twoPane)
			{
				String curentCategory = allCategoriesUrls.get(currentCategoryPosition);

				pagerRightAdapter = new PagerAdapterArticles(act.getSupportFragmentManager(),
				curentCategory, act);
				pagerRight.setAdapter(pagerRightAdapter);
				pagerRight.setPageTransformer(true, new RotationPageTransformer());
				OnPageChangeListener listener = new PagerListenerArticle(act, curentCategory);
				pagerRight.setOnPageChangeListener(listener);

				int curPos = this.act.getAllCatListsSelectedArtPosition().get(curentCategory);
				pagerRight.setCurrentItem(curPos, true);

				if (curPos == 0)
				{
					listener.onPageSelected(curPos);
				}
			}
		}
	}
}
