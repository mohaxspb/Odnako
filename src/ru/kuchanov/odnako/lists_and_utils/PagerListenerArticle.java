/*
 12.02.2015
PagerListenerMenu.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityArticle;
import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.activities.ActivityMain;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class PagerListenerArticle extends ViewPager.SimpleOnPageChangeListener
{
	final static String LOG = PagerListenerArticle.class.getSimpleName();

	private ActivityBase act;

	private boolean twoPane;

	//ViewPager and it's adapter for articles/comments
	private ViewPager artCommsPager;

	private Toolbar toolbarRight;
	private Toolbar toolbar;

	private String categoryToLoad;

	public PagerListenerArticle(ActivityBase act, String categoryToLoad)
	{
		this.act = act;

		this.categoryToLoad = categoryToLoad;

		this.twoPane = PreferenceManager.getDefaultSharedPreferences(this.act).getBoolean("twoPane", false);

		this.artCommsPager = (ViewPager) act.findViewById(R.id.pager_right);

		this.toolbar = (Toolbar) act.findViewById(R.id.toolbar);
		this.toolbarRight = (Toolbar) act.findViewById(R.id.toolbar_right);
	}

	@Override
	public void onPageSelected(int position)
	{
		Log.d(LOG, "onPageSelected in articlePager; position: " + position);
		//on MainActivity it can be shown ONLY in tablet mode
		//else it's ActivityArticle!
		if (this.twoPane)
		{
			ActivityMain mainActivity = (ActivityMain) this.act;
			//move topImg and toolBar while scrolling left list
			//			toolbar.setY(0 - toolbar.getHeight());
			toolbar.setY(0);
			toolbar.getBackground().setAlpha(255);

			toolbarRight.setTitle("Статья " + String.valueOf(position + 1) + "/"
			+ artCommsPager.getAdapter().getCount());

			mainActivity.getAllCatListsSelectedArtPosition().put(categoryToLoad, position);

			Intent intentToListFrag = new Intent(categoryToLoad + "art_position");
			intentToListFrag.putExtra("position", position);
			LocalBroadcastManager.getInstance(act).sendBroadcast(intentToListFrag);
		}
		else
		{
			ActivityArticle articleActivity = (ActivityArticle) this.act;
			String categotiesTitle = "";

			for (int i = 0; i < articleActivity.getAllCatAndAutURLs().size(); i++)
			{
				String s = articleActivity.getAllCatAndAutURLs().get(i);
				if (s.equals(this.categoryToLoad))
				{
					categotiesTitle = articleActivity.getAllCatAndAutTitles().get(i);
					break;
				}
			}
//			Log.d(LOG, "this.toolbar.getTitle(): " + this.toolbar.getTitle());
			this.toolbar.setTitle(categotiesTitle + " " + String.valueOf(position + 1) + "/"
			+ articleActivity.getAllCatArtsInfo().get(categoryToLoad).size());
//			Log.d(LOG, "this.toolbar.getTitle(): " + this.toolbar.getTitle());
//			Log.d(LOG, "this.act.getSupportActionBar().getTitle(): " + this.act.getSupportActionBar().getTitle());
			articleActivity.setCurArtPosition(position);
		}
	}
}
