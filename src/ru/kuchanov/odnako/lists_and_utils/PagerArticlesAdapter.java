/*
 27.10.2014
ViewPagerAdapter.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.fragments.FragArtUPD;
import ru.kuchanov.odnako.fragments.FragmentArticle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

public class PagerArticlesAdapter extends FragmentStatePagerAdapter
{
	static String LOG_TAG = PagerArticlesAdapter.class.getSimpleName();

	ArrayList<ArtInfo> allArtsInfo;

	String category;

	ActionBarActivity act;

	public PagerArticlesAdapter(FragmentManager fm, String category, ActionBarActivity act)
	{
		super(fm);
		System.out.println("ArticlesPagerAdapter called");
		this.category = category;
		this.act = act;

		this.allArtsInfo = ((ActivityBase) act).getAllCatArtsInfo().get(category);

		LocalBroadcastManager.getInstance(this.act).registerReceiver(artsDataReceiver, new IntentFilter(category));
	}

	private BroadcastReceiver artsDataReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.i(LOG_TAG, "artsDataReceiver onReceive called");
			ArrayList<ArtInfo> newAllArtsInfo;
			newAllArtsInfo = intent.getParcelableArrayListExtra(ArtInfo.KEY_ALL_ART_INFO);

			//TODO switch by pageToLoad and add or clear list
			if (newAllArtsInfo != null)
			{
				if (allArtsInfo != null)
				{
					allArtsInfo.clear();
				}
				else
				{
					allArtsInfo = new ArrayList<ArtInfo>();
					allArtsInfo.clear();
				}
				allArtsInfo.addAll(newAllArtsInfo);
				notifyDataSetChanged();
			}
			else
			{
				System.out.println("ArrayList<ArtInfo> someResult=NNULL!!!");
			}

		}
	};

	@Override
	public Fragment getItem(int position)
	{
		FragmentArticle artFrag = new FragmentArticle();
		Bundle b = new Bundle();
		if (this.allArtsInfo == null)
		{
			b.putParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO, null);
			b.putParcelable(ArtInfo.KEY_CURENT_ART, null);

			ArrayList<ArtInfo> def = new ArrayList<ArtInfo>();
			def.add(new ArtInfo("empty", "Статьи загружаются, подождите пожалуйста", "empty", "empty", "empty"));
			this.allArtsInfo = def;
		}
		else
		{
			b.putParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO, this.allArtsInfo);
			b.putParcelable(ArtInfo.KEY_CURENT_ART, this.allArtsInfo.get(position));
		}

		b.putInt("position", position);
		artFrag.setArguments(b);

		return artFrag;
	}

	@Override
	public int getCount()
	{
		if (this.allArtsInfo == null)
		{
			return 1;
		}
		else
		{
			return this.allArtsInfo.size();
		}
	}

	@Override
	public int getItemPosition(Object object)
	{
		if (object instanceof FragArtUPD)
		{
			if(((Fragment) object).isAdded())
			{
				((FragArtUPD) object).update(this.allArtsInfo);
			}
			else
			{
				Log.e(LOG_TAG, "Fragment not added");
			}
		}
		//don't return POSITION_NONE, avoid fragment recreation. 
		return super.getItemPosition(object);
	}
}
