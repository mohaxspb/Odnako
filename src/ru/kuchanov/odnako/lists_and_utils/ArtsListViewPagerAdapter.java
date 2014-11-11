/*
 27.10.2014
ViewPagerAdapter.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.fragments.ArticlesListFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;

public class ArtsListViewPagerAdapter extends FragmentStatePagerAdapter
{
	//	ArrayList<ArtInfo> allArtsInfo;

	ActionBarActivity act;
	SharedPreferences pref;
	boolean twoPane;

	/////test
	HashMap<String, String> categories;

	String[] authorsMenuNames;
	String[] authorsMenuLinks;

	String[] categoriesMenuNames;
	String[] categoriesMenuLinks;

	String[] allCategoriesMenuNames;
	String[] allCategoriesMenuLinks;

	//////

	public String[] concatArrays(String[] first, String[] second)
	{
		List<String> both = new ArrayList<String>(first.length + second.length);
		Collections.addAll(both, first);
		Collections.addAll(both, second);
		return both.toArray(new String[both.size()]);
	}
	
	public String[] getAllCategoriesMenuNames()
	{
		return this.allCategoriesMenuNames;
	}
	
	public String[] getAllCategoriesMenuLinks()
	{
		return this.allCategoriesMenuLinks;
	}

	public void fillAllMenuCategories(ActionBarActivity act)
	{
		categories=new HashMap<String, String>();
		
		authorsMenuNames=act.getResources().getStringArray(R.array.authors);
		authorsMenuLinks=act.getResources().getStringArray(R.array.authors_links);
		
		categoriesMenuNames=act.getResources().getStringArray(R.array.categories);
		categoriesMenuLinks=act.getResources().getStringArray(R.array.categories_links);
		
		for(int i=0; i<authorsMenuNames.length; i++)
		{
			categories.put(authorsMenuNames[i], authorsMenuLinks[i]);
		}
		for(int i=0; i<categoriesMenuNames.length; i++)
		{
			categories.put(categoriesMenuNames[i], categoriesMenuLinks[i]);
		}
		
		this.allCategoriesMenuNames=this.concatArrays(authorsMenuNames, categoriesMenuNames);
		this.allCategoriesMenuLinks=this.concatArrays(authorsMenuLinks, categoriesMenuLinks);
	}

	public ArtsListViewPagerAdapter(FragmentManager fm, ActionBarActivity act)
	{
		super(fm);
		this.act = act;

		this.fillAllMenuCategories(act);

		this.pref = PreferenceManager.getDefaultSharedPreferences(act);
		this.twoPane = pref.getBoolean("twoPane", false);
	}

	@Override
	public Fragment getItem(int position)
	{
		ArticlesListFragment artsListFrag = new ArticlesListFragment();
		Bundle b = new Bundle();
		b.putString("categoryToLoad", this.allCategoriesMenuLinks[position]);
		b.putInt("pageToLoad", 1);
		artsListFrag.setArguments(b);

		return artsListFrag;
	}

	@Override
	public int getCount()
	{
		return this.categories.size();
	}

}
