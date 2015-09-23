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
import ru.kuchanov.odnako.lists_and_utils.PagerAdapterAllAuthors;
import ru.kuchanov.odnako.lists_and_utils.PagerListenerAllAuthors;
import ru.kuchanov.odnako.lists_and_utils.RecyclerAdapterAllAuthors;
import ru.kuchanov.odnako.lists_and_utils.RecyclerAdapterArtsListFragment;
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

public class FragmentAllAuthors extends Fragment
{
	private static final String LOG = FragmentAllAuthors.class.getSimpleName() + "/";

	private ImageView topImg;
	private ImageView topImgCover;
	private float topImgCoord;

	private SharedPreferences pref;

	private RecyclerView recyclerView;
	private RecyclerAdapterAllAuthors adapter;

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
		//		LocalBroadcastManager.getInstance(this.act).registerReceiver(fragSelectedReceiver,
		//		new IntentFilter(this.getCategoryToLoad() + "_notify_that_selected"));

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
			PagerAdapterAllAuthors pagerAdapter;// = (PagerAdapterAllAuthors) pagerRight.getAdapter();
			//here we can somehow restore previous state and select author, but it's hard, because of 
			//calling onCreateOpteionMenu and setting "" query to searchView on its expanding...
			//so f*ck it now!
			//Just set adapter for right pager
			if (filterText != null)
			{
				//in case of no authors we must set empty pager adapter
				//to prevent exceptions
				if (adapter.getCurAllAuthorsList().size() == 0)
				{
					pagerRight.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager())
					{

						@Override
						public int getCount()
						{
							// TODO Auto-generated method stub
							return 0;
						}

						@Override
						public Fragment getItem(int arg0)
						{
							// TODO Auto-generated method stub
							return null;
						}
					});
					//and we must update right toolbar
					Toolbar toolbarRight = (Toolbar) act.findViewById(R.id.toolbar_right);
					toolbarRight.setTitle("Ни одного автора не обнаружено");
				}
				else
				{
					pagerAdapter = new PagerAdapterAllAuthors(act.getSupportFragmentManager(), mainActivity);
					pagerAdapter.updateData(adapter.getCurAllAuthorsList());
					pagerRight.setAdapter(pagerAdapter);
					PagerListenerAllAuthors listener = new PagerListenerAllAuthors(mainActivity,
					adapter.getCurAllAuthorsList());
					pagerRight.addOnPageChangeListener(listener);
					listener.onPageSelected(0);
				}
			}
			else
			{
				pagerAdapter = new PagerAdapterAllAuthors(act.getSupportFragmentManager(), mainActivity);
				//				pagerAdapter.updateData(adapter.getCurAllAuthorsList());
				pagerRight.setAdapter(pagerAdapter);
				PagerListenerAllAuthors listener = new PagerListenerAllAuthors(mainActivity,
				adapter.getCurAllAuthorsList());
				pagerRight.addOnPageChangeListener(listener);
				listener.onPageSelected(0);
			}

			//			if (filterText != null)
			//			{
			//				//there is some text so check if there is some filter in adapter
			//				//by matching counts
			//				if(adapter.getItemCount()==pagerAdapter.getCount())
			//				{
			//					PagerListenerAllAuthors listener = new PagerListenerAllAuthors(mainActivity,
			//					adapter.getCurAllAuthorsList());
			//					pagerRight.setOnPageChangeListener(listener);
			//					int curPos = mainActivity.getAllCatListsSelectedArtPosition().get(
			//					CatData.getAllCategoriesMenuLinks(mainActivity)[3]);
			//					pagerRight.setCurrentItem(curPos);
			//					if (curPos == 0)
			//					{
			//						listener.onPageSelected(0);
			//					}
			//					//and check if given equals to existing
			////					if (adapter.getFilter().equals(filterText))
			////					{
			////						//filters equal to each other,
			////						//so nothing to do? //TODO check it!
			////					}
			////					else
			////					{
			//						//Mismatching so set new adapter
			////						pagerAdapter = new PagerAdapterAuthorsLists(act.getSupportFragmentManager(), mainActivity);
			////						pagerAdapter.updateData(adapter.getCurAllAuthorsList());
			////						pagerRight.setAdapter(pagerAdapter);
			////						PagerListenerAllAuthors listener = new PagerListenerAllAuthors(mainActivity,
			////						adapter.getCurAllAuthorsList());
			////						pagerRight.setOnPageChangeListener(listener);
			////						listener.onPageSelected(0);
			//						
			////						pagerAdapter = new PagerAdapterAuthorsLists(act.getSupportFragmentManager(), mainActivity);
			////						pagerAdapter.updateData(adapter.getCurAllAuthorsList());
			////						pagerRight.setAdapter(pagerAdapter);
			////						PagerListenerAllAuthors listener = new PagerListenerAllAuthors(mainActivity,
			////						adapter.getCurAllAuthorsList());
			////						pagerRight.setOnPageChangeListener(listener);
			////						int curPos = mainActivity.getAllCatListsSelectedArtPosition().get(
			////						CatData.getAllCategoriesMenuLinks(mainActivity)[3]);
			////						pagerRight.setCurrentItem(curPos);
			////						if (curPos == 0)
			////						{
			////							listener.onPageSelected(0);
			////						}
			////					}
			//				}
			//				else
			//				{
			//					//so just set new adapter to right pager
			//					pagerAdapter = new PagerAdapterAuthorsLists(act.getSupportFragmentManager(), mainActivity);
			//					pagerAdapter.updateData(adapter.getCurAllAuthorsList());
			//					pagerRight.setAdapter(pagerAdapter);
			//					PagerListenerAllAuthors listener = new PagerListenerAllAuthors(mainActivity,
			//					adapter.getCurAllAuthorsList());
			//					pagerRight.setOnPageChangeListener(listener);
			//					listener.onPageSelected(0);
			//				}
			//			}
			//			else
			//			{
			//				//given filter is null,
			//				//so we must reset right adaper
			//				//if it has some filter. We check it by matching their count
			////				pagerAdapter=(PagerAdapterAuthorsLists) pagerRight.getAdapter();
			//				if(adapter.getItemCount()==pagerAdapter.getCount())
			//				{
			//					//they are equal, do noting
			//				}
			//				else
			//				{
			//					//reset
			//					pagerAdapter = new PagerAdapterAuthorsLists(act.getSupportFragmentManager(), mainActivity);
			//					pagerAdapter.updateData(adapter.getCurAllAuthorsList());
			//					pagerRight.setAdapter(pagerAdapter);
			//					PagerListenerAllAuthors listener = new PagerListenerAllAuthors(mainActivity,
			//					adapter.getCurAllAuthorsList());
			//					pagerRight.setOnPageChangeListener(listener);
			//					listener.onPageSelected(0);
			//				}
			//				
			////				if (adapter.getFilter()!=null)
			////				{
			////					pagerAdapter = new PagerAdapterAuthorsLists(act.getSupportFragmentManager(), mainActivity);
			////					pagerAdapter.updateData(adapter.getCurAllAuthorsList());
			////					pagerRight.setAdapter(pagerAdapter);
			////					PagerListenerAllAuthors listener = new PagerListenerAllAuthors(mainActivity,
			////					adapter.getCurAllAuthorsList());
			////					pagerRight.setOnPageChangeListener(listener);
			////					listener.onPageSelected(0);
			////				}
			//			}
		}

	};

	//	private BroadcastReceiver fragSelectedReceiver = new BroadcastReceiver()
	//	{
	//		@Override
	//		public void onReceive(Context context, Intent intent)
	//		{
	//			Log.d(categoryToLoad, "fragSelectedReceiver onReceive called");
	//
	//			adapter.notifyDataSetChanged();
	//		}
	//	};

	private BroadcastReceiver artSelectedReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			int newPosition = intent.getIntExtra("position", 0);
//			Log.d(LOG + categoryToLoad, "setActivatedPosition: " + newPosition);
			setActivatedPosition(newPosition);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//System.out.println("ArticlesListFragment onCreateView");
		View v = inflater.inflate(R.layout.fragment_all_authors_categories, container, false);

		this.topImg = (ImageView) v.findViewById(R.id.top_img);

		this.topImg.setY(topImgCoord);
		this.topImgCover = (ImageView) v.findViewById(R.id.top_img_cover);
		boolean nightModeIsOn = this.pref.getBoolean("night_mode", false);
		if (nightModeIsOn)
		{
			topImgCover.setBackgroundResource(R.drawable.top_img_cover_dark);
		}
		else
		{
			topImgCover.setBackgroundResource(R.drawable.top_img_cover_light);
		}

		this.recyclerView = ((RecyclerView) v.findViewById(R.id.arts_list_view));

		this.adapter = new RecyclerAdapterAllAuthors(act, this);
		this.recyclerView.setAdapter(adapter);

		this.recyclerView.setItemAnimator(new DefaultItemAnimator());
		this.recyclerView.setLayoutManager(new LinearLayoutManager(act));

		//set onScrollListener
		this.recyclerView.addOnScrollListener(new RecyclerViewOnScrollListenerALLAUTHORS(act, this.categoryToLoad,
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
		this.recyclerView.scrollToPosition(RecyclerAdapterArtsListFragment.getPositionInRecyclerView(position));
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
		//		if (fragSelectedReceiver != null)
		//		{
		//			LocalBroadcastManager.getInstance(act).unregisterReceiver(fragSelectedReceiver);
		//			fragSelectedReceiver = null;
		//		}
		// Must always call the super method at the end.
		super.onDestroy();
	}
}
