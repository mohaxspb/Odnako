/*
 27.10.2014
ViewPagerAdapter.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.fragments.FragmentArtsRecyclerList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;

/**
 * PagerAdapter for artsList of Authors arts. We use here fragments with
 * ListView instead of RecyclerView
 */
public class PagerAdapterAuthorsLists extends FragmentStatePagerAdapter
{
	ActionBarActivity act;
	ArrayList<Author> allAuthorsInfo;

	public PagerAdapterAuthorsLists(FragmentManager fm, ActivityMain act)
	{
		super(fm);
		this.act = act;
		this.allAuthorsInfo = (ArrayList<Author>) act.getAllAuthorsList();
	}

	/**
	 * updates allAluthors list by giveb and notify about changes
	 * 
	 * @param allAuthorsInfo
	 */
	public void updateData(ArrayList<Author> allAuthorsInfo)
	{
		if (this.allAuthorsInfo == null)
		{
			this.allAuthorsInfo = new ArrayList<Author>();
		}
		this.allAuthorsInfo.clear();
		this.allAuthorsInfo.addAll(allAuthorsInfo);
		this.notifyDataSetChanged();
	}

	public ArrayList<Author> getAllAuthorsList()
	{
		return allAuthorsInfo;
	}

	public ArrayList<String> getAllAuthorsURLsList()
	{
		ArrayList<String> allAuthorsURLsList = new ArrayList<String>();
		for (Author a : allAuthorsInfo)
		{
			allAuthorsURLsList.add(a.getBlog_url());
		}
		return allAuthorsURLsList;
	}

	@Override
	public Fragment getItem(int position)
	{
		//		FragmentArtsListView frag=new FragmentArtsListView();
		//		Bundle b = new Bundle();
		//		b.putString("categoryToLoad", allAuthorsInfo.get(position).blogLink);
		//		b.putInt("pageToLoad", 1);
		//		frag.setArguments(b);
		//		return frag;
		FragmentArtsRecyclerList frag = new FragmentArtsRecyclerList();
		Bundle b = new Bundle();
		//		b.putString("categoryToLoad", allAuthorsInfo.get(position).blogLink);
		b.putString("categoryToLoad", allAuthorsInfo.get(position).getBlog_url());
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
