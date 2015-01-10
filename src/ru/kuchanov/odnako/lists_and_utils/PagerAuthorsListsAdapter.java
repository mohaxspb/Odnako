/*
 27.10.2014
ViewPagerAdapter.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.fragments.FragmentArtsListView;
import ru.kuchanov.odnako.lists_and_utils.AllAuthorsInfo.AuthorInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;

/**
 * PagerAdapter for artsList of Authors arts. We use here fragments with ListView instead of RecyclerView
 */
public class PagerAuthorsListsAdapter extends FragmentStatePagerAdapter
{
	ActionBarActivity act;
	ArrayList<AuthorInfo> allAuthorsInfo;

	public PagerAuthorsListsAdapter(FragmentManager fm, ActionBarActivity act)
	{
		super(fm);
		this.act = act;
		this.allAuthorsInfo = new AllAuthorsInfo(act).getAllAuthorsInfoAsList();
	}

	@Override
	public Fragment getItem(int position)
	{
		FragmentArtsListView frag=new FragmentArtsListView();
		Bundle b = new Bundle();
		b.putString("categoryToLoad", allAuthorsInfo.get(position).blogLink);
		b.putInt("pageToLoad", 1);
		frag.setArguments(b);
		return frag;

	}

	@Override
	public int getCount()
	{
		return allAuthorsInfo.size();
	}
}
