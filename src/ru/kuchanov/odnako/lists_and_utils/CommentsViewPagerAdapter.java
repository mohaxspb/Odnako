/*
 27.10.2014
ViewPagerAdapter.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.fragments.CommentsFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;

public class CommentsViewPagerAdapter extends FragmentStatePagerAdapter
{
	ArrayList<ArrayList<CommentInfo>> allArtsCommentsInfo;
	
	ArrayList<ArtInfo> allArtsInfo;

	ActionBarActivity act;
	SharedPreferences pref;
	boolean twoPane;

	public CommentsViewPagerAdapter(FragmentManager fm, ArrayList<ArtInfo> allArtsInfo, ArrayList<ArrayList<CommentInfo>> allArtsCommentsInfo,
	ActionBarActivity act)
	{
		super(fm);
		this.allArtsCommentsInfo = allArtsCommentsInfo;
		this.allArtsInfo=allArtsInfo;
		this.act = act;

		this.pref = PreferenceManager.getDefaultSharedPreferences(act);
		this.twoPane = pref.getBoolean("twoPane", false);
	}

	@Override
	public Fragment getItem(int position)
	{
		// TODO setposition in List
		CommentsFragment commrag = new CommentsFragment();
		Bundle b = new Bundle();
		b.putParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO, allArtsInfo);
		b.putInt("position", position);
		commrag.setArguments(b);

		return commrag;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return this.allArtsCommentsInfo.size();
	}

}
