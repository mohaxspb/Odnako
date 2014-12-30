/*
 27.10.2014
ViewPagerAdapter.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.fragments.ArticleFragment;
import ru.kuchanov.odnako.activities.ActivityMain;
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

public class ArticlesPagerAdapter extends FragmentStatePagerAdapter
{
	static String LOG_TAG=ArticlesPagerAdapter.class.getSimpleName();
	
	ArrayList<ArtInfo> allArtsInfo;

	String category;

	ActionBarActivity act;

	public ArticlesPagerAdapter(FragmentManager fm, String category, ActionBarActivity act)
	{
		super(fm);
		System.out.println("ArticlesPagerAdapter called");
		this.category = category;
		this.act = act;

		this.allArtsInfo = ((ActivityMain) act).getAllCatArtsInfo().get(category);

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
			// Get extra data included in the Intent
			ArrayList<ArtInfo> newAllArtsInfo = ArtInfo.restoreAllArtsInfoFromBundle(intent.getExtras(), LOG_TAG);

			if (newAllArtsInfo != null)
			{
				allArtsInfo.clear();
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
		ArticleFragment artFrag = new ArticleFragment();
		Bundle b = new Bundle();
		ArtInfo.writeAllArtsInfoToBundle(b, allArtsInfo, this.allArtsInfo.get(position));
		b.putInt("position", position);
		artFrag.setArguments(b);

		return artFrag;
	}

	@Override
	public int getCount()
	{
		return this.allArtsInfo.size();
	}

}
