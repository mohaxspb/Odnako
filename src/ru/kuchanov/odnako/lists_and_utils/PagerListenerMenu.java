/*
 12.02.2015
PagerListenerMenu.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.animations.RotationPageTransformer;
import ru.kuchanov.odnako.lists_and_utils.AllAuthorsInfo.AuthorInfo;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class PagerListenerMenu extends ViewPager.SimpleOnPageChangeListener
{
	final static String LOG_TAG = PagerListenerMenu.class.getSimpleName();
	
	ActivityMain act;

	boolean twoPane;

	//ViewPager and it's adapter for articles/comments
	ViewPager artCommsPager;
	PagerAdapter pagerAdapter;

	//ViewPager and it's adapter for artsLists
	ViewPager artsListPager;
	PagerAdapter artsListPagerAdapter;

	Toolbar toolbarRight;
	Toolbar toolbar;

	int currentCategoryPosition;

	public PagerListenerMenu(ActivityMain act, ViewPager artCommsPager, PagerAdapter pagerAdapter,
	ViewPager artsListPager, PagerAdapter artsListPagerAdapter, Toolbar toolbarRight,
	Toolbar toolbar)
	{
		this.act = act;
		this.twoPane = PreferenceManager.getDefaultSharedPreferences(this.act).getBoolean("twoPane", false);

		this.artCommsPager = artCommsPager;
		this.pagerAdapter = pagerAdapter;

		this.artsListPager = artsListPager;
		this.artsListPagerAdapter = artsListPagerAdapter;

		this.toolbar = toolbar;
		this.toolbarRight = toolbarRight;

		this.currentCategoryPosition = this.act.getCurentCategoryPosition();
	}

	@Override
	public void onPageSelected(int position)
	{
		Log.d(LOG_TAG, "select artsListPager position= " + position);
		//this will set current pos, and adapters group/child pos
		this.act.setCurentCategoryPosition(position);
		this.currentCategoryPosition=position;

		setTitleDrawerItemToolbarTopImgETC(position);

		//sending intent to listfrag to notify it's adapter to fix issue
		//when there is only 1-st art is shown and other can be shown only from articlesPager
		//when switching articles
		String[] allCatsLinks = CatData.getAllCategoriesMenuLinks(act);
		Intent intentToListFrag = new Intent(allCatsLinks[this.act.getCurentCategoryPosition()]
		+ "_notify_that_selected");
		LocalBroadcastManager.getInstance(act).sendBroadcast(intentToListFrag);
		if (twoPane)
		{
			if (currentCategoryPosition != 3 && currentCategoryPosition != 13)
			{
				toolbarRight.setTitle("");
				
				String categoryForRightPager=CatData.getAllCategoriesMenuLinks(act)[currentCategoryPosition];
				Log.e(LOG_TAG, "categoryForRightPager: "+categoryForRightPager);
				pagerAdapter = new PagerArticlesAdapter(act.getSupportFragmentManager(), categoryForRightPager, act);
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
						String[] allCatsLinks = CatData.getAllCategoriesMenuLinks(act);
						act.getAllCatListsSelectedArtPosition().put(allCatsLinks[currentCategoryPosition], position);

						Intent intentToListFrag = new Intent(allCatsLinks[currentCategoryPosition]
						+ "art_position");
						Bundle b = new Bundle();
						b.putInt("position", position);
						intentToListFrag.putExtras(b);

						LocalBroadcastManager.getInstance(act).sendBroadcast(intentToListFrag);
					}
				});
				int curPos = act.getAllCatListsSelectedArtPosition().get(allCatsLinks[currentCategoryPosition]);
				artCommsPager.setCurrentItem(curPos, true);
			}
			else if (currentCategoryPosition == 3)
			{
				//show all authors adapters
				pagerAdapter = new PagerAuthorsListsAdapter(act.getSupportFragmentManager(), act);
				artCommsPager.setAdapter(pagerAdapter);
				artCommsPager.setPageTransformer(true, new RotationPageTransformer());
				artCommsPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
				{
					@Override
					public void onPageSelected(int position)
					{
						//move topImg and toolBar while scrolling left list
						toolbar.setY(0 - toolbar.getHeight());
						toolbarRight.getBackground().setAlpha(0);

						//XXX							topImg.setY(0 - topImg.getHeight());
						System.out.println("onPageSelected in articlePager; position: " + position);
						String[] allCatsLinks = CatData.getAllCategoriesMenuLinks(act);
						act.getAllCatListsSelectedArtPosition().put(allCatsLinks[currentCategoryPosition], position);

						Intent intentToListFrag = new Intent(allCatsLinks[currentCategoryPosition]
						+ "art_position");
						Bundle b = new Bundle();
						b.putInt("position", position);
						intentToListFrag.putExtras(b);

						LocalBroadcastManager.getInstance(act).sendBroadcast(intentToListFrag);
						//send message to change right toolbar title
						ArrayList<AuthorInfo> allAuthorsInfo = new AllAuthorsInfo(act).getAllAuthorsInfoAsList();
						Intent intentToRightListFrag = new Intent(allAuthorsInfo.get(position).blogLink
						+ "_notify_that_selected");
						LocalBroadcastManager.getInstance(act).sendBroadcast(intentToRightListFrag);
					}
				});
				int curPos = act.getAllCatListsSelectedArtPosition().get(allCatsLinks[currentCategoryPosition]);
				artCommsPager.setCurrentItem(curPos, true);
			}
			else if (currentCategoryPosition == 13)
			{
				//TODO show all categories adapters
			}
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
		title = CatData.getAllCategoriesMenuNames(act)[position];
//		this.act.setTitle(title);
		this.act.setTitle(title);

		//change topImg
		//	XXX	if (position >= firstCategoryChildrenQuontity)
		//		{
		//			String defPackage = act.getPackageName();
		//			String[] catImgsFilesNames = act.getResources().getStringArray(R.array.categories_imgs_files_names);
		//			String fullResName = catImgsFilesNames[position - firstCategoryChildrenQuontity];
		//			String resName = fullResName.substring(0, fullResName.length() - 4);
		//			int resId = act.getResources().getIdentifier(resName, "drawable", defPackage);
		//			ImageLoader imgLoader = UniversalImageLoader.get(act);
		//			imgLoader.displayImage("drawable://" + resId, topImg,
		//			UniversalImageLoader.getTransparentBackgroundOptions());
		//			//			topImg.setImageResource(resId);
		//		}

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
