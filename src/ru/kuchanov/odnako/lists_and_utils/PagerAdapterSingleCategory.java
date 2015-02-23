/*
 27.10.2014
ViewPagerAdapter.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import ru.kuchanov.odnako.fragments.FragmentArtsRecyclerList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;

/**
 * PagerAdapter for mainActivity, which shows One given category
 */
public class PagerAdapterSingleCategory extends FragmentStatePagerAdapter
{
	ActionBarActivity act;
	String categoryToLoad;

	public PagerAdapterSingleCategory(FragmentManager fm, ActionBarActivity act, String categoryToLoad)
	{
		super(fm);
		this.act = act;
		this.categoryToLoad = categoryToLoad;
	}

	@Override
	public Fragment getItem(int position)
	{
		FragmentArtsRecyclerList artsListFrag = new FragmentArtsRecyclerList();
		Bundle b = new Bundle();
		b.putString("categoryToLoad", categoryToLoad);
		b.putInt("pageToLoad", 1);
		artsListFrag.setArguments(b);
		return artsListFrag;
	}

	@Override
	public int getCount()
	{
		return 1;
	}
}
