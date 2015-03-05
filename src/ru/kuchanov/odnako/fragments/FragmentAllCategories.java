/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.util.Locale;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.animations.RecyclerViewOnScrollListenerALLAUTHORS;
import ru.kuchanov.odnako.lists_and_utils.AllCategoriesListAdapter;
import ru.kuchanov.odnako.lists_and_utils.ArtsListAdapter;
import ru.kuchanov.odnako.lists_and_utils.PagerAdapterAllCategories;
import ru.kuchanov.odnako.lists_and_utils.PagerListenerAllCategories;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FragmentAllCategories extends Fragment
{
	private static final String LOG = FragmentAllCategories.class.getSimpleName() + "/";

	private ImageView topImg;
	private ImageView topImgCover;
	private float topImgCoord;

	private SharedPreferences pref;

	private RecyclerView artsList;
	private AllCategoriesListAdapter adapter;

	private String categoryToLoad = "all_categiries";

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

			String filterText = null;
			if (intent.getExtras() != null)
			{
				filterText = intent.getStringExtra("filter_text");
				filterText = filterText.toLowerCase(new Locale("RU_ru"));
				adapter.setFilter(filterText);
			}
			else
			{
				adapter.flushFilter();
			}
			//update rightPager if it is (if it's twoPane mode
			if (pref.getBoolean("twoPane", false) == false)
			{
				return;
			}
			ActivityMain mainActivity = (ActivityMain) act;
			ViewPager pagerRight = (ViewPager) mainActivity.findViewById(R.id.pager_right);
			PagerAdapterAllCategories pagerAdapter;// = (PagerAdapterAllCategories) pagerRight.getAdapter();
			//here we can somehow restore previous state and select author, but it's hard, because of 
			//calling onCreateOpteionMenu and setting "" query to searchView on its expanding...
			//so f*ck it now!
			//Just set adapter for right pager
			if (filterText != null)
			{
				if (adapter.getCurAllCategoriesList().size() == 0)
				{
					pagerRight.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager())
					{
						//empty adapter for no results
						@Override
						public int getCount()
						{
							return 0;
						}

						@Override
						public Fragment getItem(int arg0)
						{
							return null;
						}
					});
					//and we must update right toolbar
					Toolbar toolbarRight = (Toolbar) act.findViewById(R.id.toolbar_right);
					toolbarRight.setTitle("Ни одного автора не обнаружено");
				}
				else
				{
					pagerAdapter = new PagerAdapterAllCategories(act.getSupportFragmentManager(), mainActivity);
					pagerAdapter.updateData(adapter.getCurAllCategoriesList());
					pagerRight.setAdapter(pagerAdapter);
					PagerListenerAllCategories listener = new PagerListenerAllCategories(mainActivity,
					adapter.getCurAllCategoriesList());
					pagerRight.setOnPageChangeListener(listener);
					listener.onPageSelected(0);
				}
			}
			else
			{
				pagerAdapter = new PagerAdapterAllCategories(act.getSupportFragmentManager(), mainActivity);
				pagerRight.setAdapter(pagerAdapter);
				PagerListenerAllCategories listener = new PagerListenerAllCategories(mainActivity,
				adapter.getCurAllCategoriesList());
				pagerRight.setOnPageChangeListener(listener);
				listener.onPageSelected(0);
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

		this.adapter = new AllCategoriesListAdapter(act, this);
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
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
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
