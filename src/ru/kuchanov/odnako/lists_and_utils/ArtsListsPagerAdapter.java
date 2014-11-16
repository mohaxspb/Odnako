/*
 27.10.2014
ViewPagerAdapter.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import ru.kuchanov.odnako.fragments.ArticlesListFragment;
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
		ArticlesListFragment artsListFrag = new ArticlesListFragment();
		Bundle b = new Bundle();
		//		b.putString("categoryToLoad", this.allCategoriesMenuLinks[position]);
		b.putString("categoryToLoad", CatData.getAllCategoriesMenuLinks(act)[position]);
		b.putInt("pageToLoad", 1);
		artsListFrag.setArguments(b);

		return artsListFrag;
	}

	@Override
	public int getCount()
	{
		//		return this.categories.size();
		return CatData.getAllCategoriesMenuLinks(act).length;
	}
}
