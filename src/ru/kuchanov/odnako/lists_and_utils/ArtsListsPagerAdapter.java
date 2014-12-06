/*
 27.10.2014
ViewPagerAdapter.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import ru.kuchanov.odnako.fragments.AllAuthorsListFragment;
import ru.kuchanov.odnako.fragments.FragmentArtsRecyclerList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;

public class ArtsListsPagerAdapter extends FragmentStatePagerAdapter
{
	ActionBarActivity act;

	public ArtsListsPagerAdapter(FragmentManager fm, ActionBarActivity act)
	{
		super(fm);
		this.act = act;
	}

	@Override
	public Fragment getItem(int position)
	{
		if (position != 3 && position != 13)
		{
			FragmentArtsRecyclerList artsListFrag = new FragmentArtsRecyclerList();
			Bundle b = new Bundle();
			b.putString("categoryToLoad", CatData.getAllCategoriesMenuLinks(act)[position]);
			b.putInt("pageToLoad", 1);
			artsListFrag.setArguments(b);
			return artsListFrag;
		}
		else if(position==3)
		{
			AllAuthorsListFragment frag=new AllAuthorsListFragment();
			return frag;
		}
		else
		{
			return null;
		}
	}

	@Override
	public int getCount()
	{
		//		return this.categories.size();
		return CatData.getAllCategoriesMenuLinks(act).length;
	}
}
