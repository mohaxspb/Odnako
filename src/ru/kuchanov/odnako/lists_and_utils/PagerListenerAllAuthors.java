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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class PagerListenerAllAuthors extends ViewPager.SimpleOnPageChangeListener
{
	final static String LOG_TAG = PagerListenerAllAuthors.class.getSimpleName();

	private ActivityMain act;

	private boolean twoPane;

	//ViewPager and it's adapter for articles/comments
	private ViewPager artCommsPager;
	private PagerAdapter pagerAdapter;

	private Toolbar toolbarRight;
	private Toolbar toolbar;

	private int currentCategoryPosition;

	public PagerListenerAllAuthors(ActivityMain act)
	{
		this.act = act;
		this.twoPane = PreferenceManager.getDefaultSharedPreferences(this.act).getBoolean("twoPane", false);
		
		
		/////////////
		
		this.artCommsPager = (ViewPager) act.findViewById(R.id.article_comments_container);
		
		this.toolbar = (Toolbar) act.findViewById(R.id.toolbar);
		this.toolbarRight = (Toolbar) act.findViewById(R.id.toolbar_right);;
		
		
		
		/////////////

//		this.artCommsPager = artCommsPager;
//		this.pagerAdapter = pagerAdapter;
//		
//		this.toolbar = toolbar;
//		this.toolbarRight = toolbarRight;

		this.currentCategoryPosition = this.act.getCurentCategoryPosition();
	}

	@Override
	public void onPageSelected(int position)
	{
		Log.d(LOG_TAG, "select artsListPager position= " + position);
		//this will set current pos, and adapters group/child pos
		this.act.setCurentCategoryPosition(position);
		this.currentCategoryPosition = position;

		setTitleDrawerItemToolbarTopImgETC(position);

		//sending intent to listfrag to notify it's adapter to fix issue
		//when there is only 1-st art is shown and other can be shown only from articlesPager
		//when switching articles
		final String[] allCatsLinks = CatData.getAllAuthorsBlogsURLs(act);
		Intent intentToListFrag = new Intent(allCatsLinks[this.currentCategoryPosition]
		+ "_notify_that_selected");
		LocalBroadcastManager.getInstance(act).sendBroadcast(intentToListFrag);
		if (twoPane)
		{
			toolbarRight.setTitle("");

			pagerAdapter = new PagerArticlesAdapter(act.getSupportFragmentManager(),
			allCatsLinks[currentCategoryPosition], act);
			artCommsPager.setAdapter(pagerAdapter);
			artCommsPager.setPageTransformer(true, new RotationPageTransformer());
			artCommsPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
			{
				@Override
				public void onPageSelected(int position)
				{
					//move topImg and toolBar while scrolling left list
					toolbar.setY(0 - toolbar.getHeight());
					toolbarRight.setTitle("");
					System.out.println("onPageSelected in articlePager; position: " + position);
					act.getAllCatListsSelectedArtPosition().put(allCatsLinks[currentCategoryPosition], position);

					Intent intentToListFrag = new Intent(allCatsLinks[currentCategoryPosition]
					+ "art_position");
					Bundle b = new Bundle();
					b.putInt("position", position);
					intentToListFrag.putExtras(b);

					LocalBroadcastManager.getInstance(act).sendBroadcast(intentToListFrag);
				}
			});
			int curPos=0;
//			if(act.getAllCatListsSelectedArtPosition()==null)
//			{
//				Log.e(LOG_TAG, "act.getAllCatListsSelectedArtPosition(): "+null);
//			}
//			else
//			{
//			if(this.act==null)
//			{
//				Log.e(LOG_TAG, "this.act==null: "+String.valueOf(null));
//			}
//				curPos = act.getAllCatListsSelectedArtPosition().get(CatData.getAllAuthorsBlogsURLs(act)[currentCategoryPosition]);
//			}
//			if(this.act==null)
//				{
//					Log.e(LOG_TAG, "this.act==null: "+String.valueOf(null));
//				}
			artCommsPager.setCurrentItem(curPos, true);
		}
	}

	private void setTitleDrawerItemToolbarTopImgETC(int position)
	{
		//		int firstCategoryChildrenQuontity = act.getResources().getStringArray(R.array.authors_links).length;

		//find name of category in all (really all) categories
		//		String[] allAutAndTagsUrls=CatData.concatArrays(CatData.getAllAuthorsBlogsURLs(act), CatData.getAllTagsLinks(act));
		//		String[] allAutAndTagsNames=CatData.concatArrays(CatData.getAllAuthorsNames(act), CatData.getAllTagsNames(act));
		//		String[] reallyAllCatsUrls=CatData.concatArrays(CatData.getAllCategoriesMenuLinks(act), allAutAndTagsUrls);
		//		String[] reallyAllCatsNames=CatData.concatArrays(CatData.getAllCategoriesMenuNames(act), allAutAndTagsNames);
		String title = "";
		//		for(int i=0; i<reallyAllCatsUrls.length; i++)
		//		{
		//			if()
		//			title = CatData.getAllCategoriesMenuNames(act)[position];
		//		}
		title = CatData.getAllAuthorsNames(act)[position];
		//		this.act.setTitle(title);
		this.act.setTitle(title);

		

		//show toolbar when switch category to show it's title
		//restore and set topImg position
		String[] allMenuCatsLinks = CatData.getAllAuthorsNames(act);
		String curCatLink = allMenuCatsLinks[position];
		if(this.act.getAllCatToolbarTopImgYCoord().get(curCatLink)!=null)
		{
			int toolbarY = this.act.getAllCatToolbarTopImgYCoord().get(curCatLink)[0];
			int initialDistance = this.act.getAllCatToolbarTopImgYCoord().get(curCatLink)[2];
			int currentDistance = this.act.getAllCatToolbarTopImgYCoord().get(curCatLink)[3];

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
		else
		{
			toolbar.setY(0);
			toolbar.getBackground().setAlpha(0);
		}
		
	}
}
