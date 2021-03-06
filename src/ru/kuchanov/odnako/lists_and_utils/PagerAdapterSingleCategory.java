/*
 27.10.2014
ViewPagerAdapter.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.fragments.FragmentArtsListRecycler;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;

/**
 * PagerAdapter for mainActivity, which shows One given category
 */
public class PagerAdapterSingleCategory extends FragmentStatePagerAdapter
{
	AppCompatActivity act;
	String categoryToLoad;

	public PagerAdapterSingleCategory(FragmentManager fm, AppCompatActivity act, String categoryToLoad)
	{
		super(fm);
		this.act = act;
		this.categoryToLoad = categoryToLoad;
	}

	@Override
	public Fragment getItem(int position)
	{
		FragmentArtsListRecycler artsListFrag = new FragmentArtsListRecycler();
		Bundle b = new Bundle();
		b.putString("categoryToLoad", categoryToLoad);
		//we show this pager if we cant find category in DB
		//So we haven't selectedArtPosition
		//So set it
		((ActivityMain) act).getAllCatListsSelectedArtPosition().put(categoryToLoad, 0);
		b.putInt("position", 0);
		artsListFrag.setArguments(b);
		return artsListFrag;
	}

	@Override
	public int getCount()
	{
		return 1;
	}
}
