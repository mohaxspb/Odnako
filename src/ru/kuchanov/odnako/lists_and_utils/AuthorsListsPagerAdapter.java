/*
 27.10.2014
ViewPagerAdapter.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.fragments.ArticlesListFragment;
import ru.kuchanov.odnako.lists_and_utils.AllAuthorsInfo.AuthorInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;

public class AuthorsListsPagerAdapter extends FragmentStatePagerAdapter
{
	ActionBarActivity act;
	ArrayList<AuthorInfo> allAuthorsInfo;

	public AuthorsListsPagerAdapter(FragmentManager fm, ActionBarActivity act)//, ArrayList<AuthorInfo> allAuthorsInfo)
	{
		super(fm);
		this.act = act;
		//		this.allAuthorsInfo=allAuthorsInfo;
		this.allAuthorsInfo = new AllAuthorsInfo(act).getAllAuthorsInfoAsList();
	}

	@Override
	public Fragment getItem(int position)
	{
		ArticlesListFragment artsListFrag = new ArticlesListFragment();
		Bundle b = new Bundle();
		b.putString("categoryToLoad", allAuthorsInfo.get(position).blogLink);
		b.putInt("pageToLoad", 1);
		artsListFrag.setArguments(b);
		return artsListFrag;

	}

	@Override
	public int getCount()
	{
		return allAuthorsInfo.size();
	}
}
