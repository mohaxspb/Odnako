/*
 12.02.2015
PagerListenerMenu.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityMain;
//import ru.kuchanov.odnako.animations.RotationPageTransformer;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.db.Category;
import ru.kuchanov.odnako.db.DataBaseHelper;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Yes, I know that there could be only 1 page, but we want to create right
 * pager here and set toolbars and so on.
 * 
 * So we'll make call to it trough onSelectPage(int page) with zero in arg, to
 * have call to it's onPageSelected.
 */
public class PagerListenerSingleCategory extends ViewPager.SimpleOnPageChangeListener
{
	private final static String LOG = PagerListenerSingleCategory.class.getSimpleName() + "/";

	private ActivityMain act;

	private boolean twoPane;

	private ViewPager pagerRight;
	private Toolbar toolbar;

	private int currentCategoryPosition = 0;
	private final String singleCategoryUrl;

	public PagerListenerSingleCategory(ActivityMain act)
	{
		this.act = act;
		this.twoPane = PreferenceManager.getDefaultSharedPreferences(this.act).getBoolean("twoPane", false);

		this.pagerRight = (ViewPager) act.findViewById(R.id.pager_right);

		this.toolbar = (Toolbar) act.findViewById(R.id.toolbar);
		this.act.setCurentCategoryPosition(this.currentCategoryPosition);
		singleCategoryUrl = this.act.getCurrentCategory();
	}

	@Override
	public void onPageSelected(int position)
	{
		Log.d(LOG + singleCategoryUrl, "onPageSelected position: " + position);

		//find Category or Author name in DB
		DataBaseHelper h = new DataBaseHelper(act);
		if (Category.isCategory(h, singleCategoryUrl) == null)
		{
			this.toolbar.setTitle(singleCategoryUrl);
		}
		else
		{
			String toolbarTitle;
			if (Category.isCategory(h, singleCategoryUrl))
			{
				toolbarTitle = Category.getNameByUrl(h, singleCategoryUrl);
				this.toolbar.setTitle(toolbarTitle);
			}
			else
			{
				toolbarTitle = Author.getNameByUrl(h, singleCategoryUrl);
				this.toolbar.setTitle(toolbarTitle);
			}
			Log.d(LOG + singleCategoryUrl, "toolbarTitle: " + toolbarTitle);
		}
		h.close();

		Menu menu = toolbar.getMenu();
		MenuItem search = menu.findItem(R.id.action_search);
		if (search == null)
		{
			//can be if menu populated after on resume and we hide elements there
		}
		else
		{
			search.setVisible(false);
			MenuItem addToFavs = menu.findItem(R.id.add_to_favorites);
			addToFavs.setVisible(true);
		}

		if (twoPane)
		{
			pagerRight.setAdapter(new PagerAdapterArticles(act.getSupportFragmentManager(),
			singleCategoryUrl, act));
			//			pagerRight.setPageTransformer(true, new RotationPageTransformer());
			OnPageChangeListener listener = new PagerListenerArticle(this.act, singleCategoryUrl);
			pagerRight.addOnPageChangeListener(listener);
			int curPos = 0;
			if (act.getAllCatListsSelectedArtPosition().containsKey(singleCategoryUrl))
			{
				curPos = act.getAllCatListsSelectedArtPosition().get(singleCategoryUrl);
			}
			else
			{
				this.act.getAllCatListsSelectedArtPosition().put(singleCategoryUrl, curPos);
			}
			listener.onPageSelected(curPos);
		}
	}
}