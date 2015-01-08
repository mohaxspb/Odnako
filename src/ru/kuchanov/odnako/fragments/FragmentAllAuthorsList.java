/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.animations.RecyclerViewOnScrollListenerALLAUTHORS;
import ru.kuchanov.odnako.animations.RecyclerViewOnScrollListenerPreHONEYCOMB;
import ru.kuchanov.odnako.lists_and_utils.AllAuthorsListAdapter;
import ru.kuchanov.odnako.lists_and_utils.ArtsListAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;

public class FragmentAllAuthorsList extends Fragment
{
	private RecyclerView artsList;
	AllAuthorsListAdapter adapter;

	String categoryToLoad = "odnako.org/authors";

	ActionBarActivity act;
	SharedPreferences pref;

	private int position = 0;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		//				System.out.println("ArticlesListFragment onCreate");
		super.onCreate(savedInstanceState);

		this.act = (ActivityMain) this.getActivity();
		this.pref = PreferenceManager.getDefaultSharedPreferences(act);

		//restore topImg and toolbar prop's
		if (savedInstanceState != null)
		{
			this.position = savedInstanceState.getInt("position");
		}
		// Register to receive messages.

		//reciver for scrolling and highligting selected position
		LocalBroadcastManager.getInstance(this.act).registerReceiver(artSelectedReceiver,
		new IntentFilter(this.getCategoryToLoad() + "art_position"));

		//reciver for notify when frag selected
		LocalBroadcastManager.getInstance(this.act).registerReceiver(fragSelectedReceiver,
		new IntentFilter(this.getCategoryToLoad() + "_notify_that_selected"));
	}

	private BroadcastReceiver fragSelectedReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			adapter.notifyDataSetChanged();
		}
	};

	private BroadcastReceiver artSelectedReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			position = intent.getIntExtra("position", 0);

			setActivatedPosition(position);
			adapter.notifyDataSetChanged();
		}
	};

	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//		System.out.println("ArticlesListFragment onCreateView");
		//inflate root view
		View v;

		v = inflater.inflate(R.layout.fragment_all_authors_list, container, false);

		this.artsList = (RecyclerView) v.findViewById(R.id.arts_list_view);

		this.adapter = new AllAuthorsListAdapter(act, this.artsList, this);
		this.artsList.setAdapter(adapter);

		this.artsList.setItemAnimator(new DefaultItemAnimator());
		this.artsList.setLayoutManager(new LinearLayoutManager(act));

		///////

		//set onScrollListener
		if (android.os.Build.VERSION.SDK_INT >= 11)
		{
			this.artsList.setOnScrollListener(new RecyclerViewOnScrollListenerALLAUTHORS(act, this.categoryToLoad));
		}
		else if (this.pref.getBoolean("animate_lists", false) == true)
		{
			this.artsList.setOnScrollListener(new RecyclerViewOnScrollListenerPreHONEYCOMB(act));
		}
		else
		{
			SwipeRefreshLayout STR = (SwipeRefreshLayout) this.artsList.getParent();
			STR.setPadding(0, 0, 0, 0);
			LayoutParams lp = (LayoutParams) STR.getLayoutParams();
			lp.setMargins(0, 50, 0, 0);
			STR.setLayoutParams(lp);
		}

		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		//		System.out.println("ArticlesListFragment onViewCreated");
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		//		System.out.println("ArticlesListFragment onSaveInstanceState");
		super.onSaveInstanceState(outState);

		//category saving
		outState.putString("categoryToLoad", categoryToLoad);

		outState.putInt("position", this.position);
	}

	public void setActivatedPosition(int position)
	{
		System.out.println("setActivatedPosition(int position: " + position);
		this.position = position;

		scrollToActivatedPosition();
	}

	public void scrollToActivatedPosition()
	{
		this.artsList.scrollToPosition(ArtsListAdapter.getPositionInRecyclerView(position));
	}

	public int getMyActivatedPosition()
	{
		return this.position;
	}

	public String getCategoryToLoad()
	{
		return categoryToLoad;
	}

	public void setCategoryToLoad(String categoryToLoad)
	{
		this.categoryToLoad = categoryToLoad;
	}
	
	@Override
	public void onDestroy()
	{
		// If the DownloadStateReceiver still exists, unregister it and set it to null
		if (artSelectedReceiver != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(artSelectedReceiver);
			artSelectedReceiver = null;
		}
		if (fragSelectedReceiver != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(fragSelectedReceiver);
			fragSelectedReceiver = null;
		}
		// Must always call the super method at the end.
		super.onDestroy();
	}
}
