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

public class ArticlesPagerAdapter extends FragmentStatePagerAdapter
{
	static String LOG_TAG = ArticlesPagerAdapter.class.getSimpleName();

	ArrayList<ArtInfo> allArtsInfo;

	String category;

	ActionBarActivity act;

	public ArticlesPagerAdapter(FragmentManager fm, String category, ActionBarActivity act)
	{
		super(fm);
		System.out.println("ArticlesPagerAdapter called");
		this.category = category;
		this.act = act;

		this.allArtsInfo = ((ActivityBase) act).getAllCatArtsInfo().get(category);

		//		System.out.println("ArticlesPagerAdapter called allArtsInfo.size(): "+allArtsInfo.size());

		// Register to receive messages.
		// We are registering an observer (mMessageReceiver) to receive Intents
		// with actions named "custom-event-name".
		LocalBroadcastManager.getInstance(this.act).registerReceiver(mMessageReceiver, new IntentFilter(category));
	}

	// Our handler for received Intents. This will be called whenever an Intent
	// with an action named "custom-event-name" is broadcasted.
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.i(LOG_TAG, "artsDataReceiver onReceive called");
			ArrayList<ArtInfo> newAllArtsInfo;
			newAllArtsInfo = intent.getParcelableArrayListExtra(ArtInfo.KEY_ALL_ART_INFO);

			Log.i(LOG_TAG, "allArtsInfo!=null: " + String.valueOf(allArtsInfo != null));

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
			((FragArtUPD) object).update(this.allArtsInfo);
		}
		//don't return POSITION_NONE, avoid fragment recreation. 
		return super.getItemPosition(object);
	}
}
