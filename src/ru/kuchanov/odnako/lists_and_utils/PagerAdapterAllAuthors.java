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
import ru.kuchanov.odnako.fragments.FragmentArtsListRecycler;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * PagerAdapter for artsList of Authors arts. We use here fragments with
 * ListView instead of RecyclerView
 */
public class PagerAdapterAllAuthors extends FragmentStatePagerAdapter
{
	final static String LOG = PagerAdapterAllAuthors.class.getSimpleName();

	ActivityMain act;
	ArrayList<Author> allAuthorsInfo;

	public PagerAdapterAllAuthors(FragmentManager fm, ActivityMain act)
	{
		super(fm);
		this.act = act;
		if(this.act!=null)
		{
			this.allAuthorsInfo = (ArrayList<Author>) act.getAllAuthorsList();
		}		
	}

	/**
	 * updates allAluthors list by given and notify about changes
	 * 
	 * @param allAuthorsInfo
	 */
	public void updateData(ArrayList<Author> allAuthorsInfo)
	{
		this.allAuthorsInfo = new ArrayList<Author>(allAuthorsInfo);
//		this.allAuthorsInfo.addAll(allAuthorsInfo);
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
		FragmentArtsListRecycler frag = new FragmentArtsListRecycler();
		Bundle b = new Bundle();
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

	@Override
	public int getItemPosition(Object object)
	{
		//		FragmentArtsRecyclerList frag = (FragmentArtsRecyclerList) object;
		//		frag.updateAdapter();
		//		Log.e(LOG, frag.getCategoryToLoad());
		return POSITION_NONE;
		//		if (this.allAuthorsInfo.size()==0)
		//		{
		//			return POSITION_NONE;
		//		}
		//		//don't return POSITION_NONE, avoid fragment recreation. 
		//		return super.getItemPosition(object);
	}
}
