/*
 12.02.2015
PagerListenerMenu.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityArticle;
import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.DataBaseHelper;
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

	private Toolbar toolbar;

	private String categoryToLoad;

	public PagerListenerArticle(ActivityBase act, String categoryToLoad)
	{
		this.act = act;

		this.categoryToLoad = categoryToLoad;

		this.twoPane = PreferenceManager.getDefaultSharedPreferences(this.act).getBoolean("twoPane", false);
		this.toolbar = (Toolbar) act.findViewById(R.id.toolbar);
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
			toolbar.setY(0);
			toolbar.getBackground().setAlpha(255);

			mainActivity.getAllCatListsSelectedArtPosition().put(categoryToLoad, position);

			Intent intentToListFrag = new Intent(categoryToLoad + "art_position");
			intentToListFrag.putExtra("position", position);
			LocalBroadcastManager.getInstance(act).sendBroadcast(intentToListFrag);
		}
		else
		{
			ActivityArticle articleActivity = (ActivityArticle) this.act;
			articleActivity.setCurArtPosition(position);
		}
		//notify Article fragment, that it's selected
		//onReceive it prevent first scrolling action

		if (act.getAllCatArtsInfo().get(categoryToLoad) != null)
		{
			//if there are some loaded list of arts with urls send intent with this URL
			//(If it's first launch (without any loaded arts in cache) it'll be null
			String articleUrl = act.getAllCatArtsInfo().get(categoryToLoad).get(position).getUrl();
			Intent intentToArticleFrag = new Intent(articleUrl + "frag_selected");
			LocalBroadcastManager.getInstance(act).sendBroadcast(intentToArticleFrag);

			//Here we also can update Articles isRead field in DB
			//And notify fragment (so as savedState) to change Icon
			//Ho-ho-ho! He can do it from upper intent! But...
			//But we must notify all Fragments, so we can use receiver of savedState of art and switch by action
			//Send intent with article to another activities to be able to update their data
			Article a = act.getAllCatArtsInfo().get(categoryToLoad).get(position);
			DataBaseHelper h = new DataBaseHelper(act);
			Article.updateIsReaden(h, a.getId(), true);
			h.close();
			a.setReaden(true);
			Intent intentGlobal = new Intent(Const.Action.ARTICLE_CHANGED);
			intentGlobal.putExtra(Article.KEY_CURENT_ART, a);
			intentGlobal.putExtra(Const.Action.ARTICLE_CHANGED, Const.Action.ARTICLE_READ);
			LocalBroadcastManager.getInstance(act).sendBroadcast(intentGlobal);
		}

		setTitleToToolbar(categoryToLoad, act, twoPane, position);
	}

	public static void setTitleToToolbar(String categoryToLoad, ActivityBase act, boolean twoPane, int positionInPager)
	{
		if (twoPane)
		{
			ViewPager artCommsPager;
			artCommsPager = (ViewPager) act.findViewById(R.id.pager_right);

			Toolbar toolbarRight;
			toolbarRight = (Toolbar) act.findViewById(R.id.toolbar_right);
			toolbarRight.setTitle("Статья " + String.valueOf(positionInPager + 1) + "/"
			+ artCommsPager.getAdapter().getCount());
			toolbarRight.setY(0);
			toolbarRight.getBackground().setAlpha(255);
		}
		else
		{
			Toolbar toolbar;
			toolbar = (Toolbar) act.findViewById(R.id.toolbar);
			String categoriesTitle = "";

			for (int i = 0; i < act.getAllCatAndAutURLs().size(); i++)
			{
				String s = act.getAllCatAndAutURLs().get(i);
				if (s.equals(categoryToLoad))
				{
					categoriesTitle = act.getAllCatAndAutTitles().get(i);
					break;
				}
			}
			toolbar.setTitle(categoriesTitle + " " + String.valueOf(positionInPager + 1) + "/"
			+ act.getAllCatArtsInfo().get(categoryToLoad).size());
		}
	}
}