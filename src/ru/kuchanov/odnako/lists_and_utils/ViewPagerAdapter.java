/*
 27.10.2014
ViewPagerAdapter.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;


import java.util.ArrayList;

import ru.kuchanov.odnako.fragments.ArticleFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter
{
	ArrayList<ArtInfo> allArtsInfo;
	ArtInfo curArtInfo;
	int position;

	public ViewPagerAdapter(FragmentManager fm, ArrayList<ArtInfo> allArtsInfo,ArtInfo curArtInfo,int position)
	{
		super(fm);
		this.allArtsInfo=allArtsInfo;
		this.curArtInfo=curArtInfo;
		this.position=position;
	}

	@Override
	public Fragment getItem(int position)
	{
//		this.position=this.allArtsInfo.indexOf(curArtInfo);
		
		this.position=position;
		// TODO setposition in List
		ArticleFragment artFrag=new ArticleFragment();
		Bundle b=new Bundle();
		ArtInfo.writeAllArtsInfoToBundle(b, allArtsInfo, this.allArtsInfo.get(position));
		b.putInt("position", position);
		artFrag.setArguments(b);
		
		return artFrag;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return this.allArtsInfo.size();
	}


}
