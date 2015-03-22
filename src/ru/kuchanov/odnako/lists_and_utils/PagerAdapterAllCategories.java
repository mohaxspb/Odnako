/*
 27.10.2014
ViewPagerAdapter.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.db.Category;
import ru.kuchanov.odnako.fragments.FragmentArtsListRecycler;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * PagerAdapter for artsList of Categories arts.
 */
public class PagerAdapterAllCategories extends FragmentStatePagerAdapter
{
	final static String LOG = PagerAdapterAllCategories.class.getSimpleName() + "/";

	ActivityMain act;
	ArrayList<Category> allCategories;

	public PagerAdapterAllCategories(FragmentManager fm, ActivityMain act)
	{
		super(fm);
		this.act = act;
		//we can access this from another activity...
		//yes, I know, that it's bad...
		//anyway we can check act for null and set Categories list via updateData() method
		if(act!=null)
		{
			this.allCategories = (ArrayList<Category>) act.getAllCategoriesList();
		}		
	}

	/**
	 * updates allCategories list by given and notify about changes
	 * 
	 * @param allCategories
	 */
	public void updateData(ArrayList<Category> allCategories)
	{
		this.allCategories = new ArrayList<Category>(allCategories);
		this.notifyDataSetChanged();
	}

	public ArrayList<Category> getAllCategoriesList()
	{
		return allCategories;
	}

	public ArrayList<String> getAllCategoriesURLsList()
	{
		ArrayList<String> allAuthorsURLsList = new ArrayList<String>();
		for (Category a : allCategories)
		{
			allAuthorsURLsList.add(a.getUrl());
		}
		return allAuthorsURLsList;
	}

	@Override
	public Fragment getItem(int position)
	{
		FragmentArtsListRecycler frag = new FragmentArtsListRecycler();
		Bundle b = new Bundle();
		b.putString("categoryToLoad", allCategories.get(position).getUrl());
		b.putInt("pageToLoad", 1);
		frag.setArguments(b);
		return frag;
	}

	@Override
	public int getCount()
	{
		return allCategories.size();
	}

	@Override
	public int getItemPosition(Object object)
	{
		return POSITION_NONE;
	}
}
