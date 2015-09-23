/*
 27.10.2014
ViewPagerAdapter.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.fragments.FragmentAllAuthors;
import ru.kuchanov.odnako.fragments.FragmentAllCategories;
import ru.kuchanov.odnako.fragments.FragmentArtsListRecycler;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;

/**
 * PagerAdapter for mainActivity, which shows main menu Categories
 */
public class PagerAdapterMenu extends FragmentStatePagerAdapter
{
	AppCompatActivity act;

	public PagerAdapterMenu(FragmentManager fm, AppCompatActivity act)
	{
		super(fm);
		this.act = act;
	}

	@Override
	public Fragment getItem(int position)
	{
		if (position != 3 && position != 13)
		{
			FragmentArtsListRecycler artsListFrag = new FragmentArtsListRecycler();
			Bundle b = new Bundle();
			String categoryToLoad = CatData.getMenuLinks(act)[position];
			b.putString("categoryToLoad", categoryToLoad);
			//setting position of selected art by asking Activities HashMap for it by category
			int selectedArtPosition = ((ActivityMain) act).getAllCatListsSelectedArtPosition().get(categoryToLoad);
			b.putInt("position", selectedArtPosition);
			artsListFrag.setArguments(b);
			return artsListFrag;
		}
		else if (position == 3)
		{
			FragmentAllAuthors frag = new FragmentAllAuthors();
			return frag;
		}
		else if (position == 13)
		{
			FragmentAllCategories frag = new FragmentAllCategories();
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
		return CatData.getMenuLinks(act).length;
	}
}
