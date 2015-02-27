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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FragmentAllAuthorsList extends Fragment
{
	private static final String LOG = FragmentAllAuthorsList.class.getSimpleName() + "/";

	private ImageView topImg;
	private ImageView topImgCover;
	private float topImgCoord;

	private SharedPreferences pref;

	private RecyclerView artsList;
	private AllAuthorsListAdapter adapter;

	private String categoryToLoad = "odnako.org/authors";

	private ActivityMain act;

	private int position = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.act = (ActivityMain) this.getActivity();

		this.pref = PreferenceManager.getDefaultSharedPreferences(act);

		//restore topImg and toolbar prop's
		if (savedInstanceState != null)
		{
			this.position = savedInstanceState.getInt("position");
			this.topImgCoord = savedInstanceState.getFloat("topImgYCoord");
		}
		else
		{
			this.position = this.act.getAllCatListsSelectedArtPosition().get(this.categoryToLoad);
		}
		// Register to receive messages.

		//receiver for scrolling and highligting selected position
		LocalBroadcastManager.getInstance(this.act).registerReceiver(artSelectedReceiver,
		new IntentFilter(this.getCategoryToLoad() + "art_position"));

		//receiver for notify when frag selected
		LocalBroadcastManager.getInstance(this.act).registerReceiver(fragSelectedReceiver,
		new IntentFilter(this.getCategoryToLoad() + "_notify_that_selected"));

		//receiver for notify when we set filter to Authors list
		LocalBroadcastManager.getInstance(this.act).registerReceiver(setFilterReceiver,
		new IntentFilter(this.getCategoryToLoad() + "_set_filter"));
	}

	private BroadcastReceiver setFilterReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.d(LOG + categoryToLoad, "setFilterReceiver called");

			String filterText;
			if (intent.getExtras()!=null)
			{
				filterText = intent.getStringExtra("filter_text");
				adapter.setFilter(filterText);
//				adapter.filterOut(filterText);
			}
			else
			{
				adapter.flushFilter();
			}
		}
	};

	private BroadcastReceiver fragSelectedReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.d(categoryToLoad, "fragSelectedReceiver onReceive called");

			adapter.notifyDataSetChanged();
		}
	};

	private BroadcastReceiver artSelectedReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			int newPosition = intent.getIntExtra("position", 0);
			Log.d(LOG + categoryToLoad, "setActivatedPosition: " + newPosition);
			setActivatedPosition(newPosition);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//System.out.println("ArticlesListFragment onCreateView");
		View v = inflater.inflate(R.layout.fragment_all_authors_list, container, false);

		this.topImg = (ImageView) v.findViewById(R.id.top_img);

		this.topImg.setY(topImgCoord);
		this.topImgCover = (ImageView) v.findViewById(R.id.top_img_cover);
		if (this.pref.getString("theme", "dark").equals("dark"))
		{
			topImgCover.setBackgroundResource(R.drawable.top_img_cover_grey_dark);
		}
		else
		{
			topImgCover.setBackgroundResource(R.drawable.top_img_cover_grey_light);
		}

		this.setArtsList((RecyclerView) v.findViewById(R.id.arts_list_view));

		this.adapter = new AllAuthorsListAdapter(act, /*this.getArtsList(), */this);
		this.getArtsList().setAdapter(adapter);

		this.getArtsList().setItemAnimator(new DefaultItemAnimator());
		this.getArtsList().setLayoutManager(new LinearLayoutManager(act));

		//set onScrollListener
		this.getArtsList().setOnScrollListener(new RecyclerViewOnScrollListenerALLAUTHORS(act, this.categoryToLoad,
		this.topImg));

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

		outState.putFloat("topImgYCoord", this.topImg.getY());

		//category saving
		outState.putString("categoryToLoad", categoryToLoad);

		outState.putInt("position", this.position);
	}

	public void setActivatedPosition(int position)
	{
		this.position = position;
		this.getArtsList().scrollToPosition(ArtsListAdapter.getPositionInRecyclerView(position));
		adapter.notifyDataSetChanged();
	}

	public int getMyActivatedPosition()
	{
		return this.position;
		//		return ArtsListAdapter.getPositionInRecyclerView(position);
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
		// If the Receivers still exists, unregister it and set it to null
		if (setFilterReceiver != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(setFilterReceiver);
			setFilterReceiver = null;
		}
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

	public RecyclerView getArtsList()
	{
		return artsList;
	}

	public void setArtsList(RecyclerView artsList)
	{
		this.artsList = artsList;
	}
}
