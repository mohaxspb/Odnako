/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.util.ArrayList;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.download.GetInfoService;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.ArtsListAdapter;
import ru.kuchanov.odnako.lists_and_utils.ArtsListVIEWAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FragmentArticlesListView extends Fragment
{
	private ListView artsList;
	private ArtsListVIEWAdapter artsListAdapter;

	ActionBarActivity act;
	SharedPreferences pref;

	private String categoryToLoad;
	ArrayList<ArtInfo> allArtsInfo;
	ArtInfo curArtInfo;
	private int position = 0;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		//				System.out.println("ArticlesListFragment onCreate");
		super.onCreate(savedInstanceState);

		this.act = (ActionBarActivity) this.getActivity();
		this.pref = PreferenceManager.getDefaultSharedPreferences(act);

		Bundle fromArgs = this.getArguments();
		if (fromArgs != null)
		{
			this.setCategoryToLoad(fromArgs.getString("categoryToLoad"));
		}
		else
		{
			System.out.println("empty fromArgs!");
		}

		//restore topImg and toolbar prop's
		if (savedInstanceState != null)
		{
			this.categoryToLoad = savedInstanceState.getString("categoryToLoad");
			this.restoreState(savedInstanceState);
		}
		// Register to receive messages.
		// We are registering an observer (mMessageReceiver) to receive Intents
		// with actions named "custom-event-name".
		LocalBroadcastManager.getInstance(this.act).registerReceiver(mMessageReceiver,
		new IntentFilter(this.getCategoryToLoad()));

		//reciver for scrolling and highligting selected position
		//only if there is twoPane MODE and there is allAuthors list currently selected
		if(((ActivityMain)act).getCurentCategoryPosition()==4 && this.pref.getBoolean("twoPane", false))
		{
			LocalBroadcastManager.getInstance(this.act).registerReceiver(artSelectedReceiver,
			new IntentFilter(this.getCategoryToLoad() + "art_position"));
		}
		

		//reciver for notify when frag selected
		LocalBroadcastManager.getInstance(this.act).registerReceiver(fragSelectedReceiver,
		new IntentFilter(this.getCategoryToLoad() + "_notify_that_selected"));
	}

	private BroadcastReceiver fragSelectedReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.i(categoryToLoad, "fragSelectedReceiver onReceive called");
			artsListAdapter.notifyDataSetChanged();
		}
	};

	private BroadcastReceiver artSelectedReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.i(categoryToLoad, "artSelectedReceiver onReceive called");
			position = intent.getIntExtra("position", 0);

			setActivatedPosition(position);
			artsListAdapter.notifyDataSetChanged();
		}
	};

	// Our handler for received Intents. This will be called whenever an Intent
	// with an action named "custom-event-name" is broadcasted.
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.i(categoryToLoad, "mMessageReceiver onReceive called");
			// Get extra data included in the Intent
			ArrayList<ArtInfo> newAllArtsInfo = ArtInfo.restoreAllArtsInfoFromBundle(intent.getExtras(), act);

			if (newAllArtsInfo != null)
			{
				allArtsInfo.clear();
				allArtsInfo.addAll(newAllArtsInfo);
				Log.i("frag ", "categoryToLoad artsInfo.size() "+categoryToLoad+": "+allArtsInfo.size());

//				((ActivityMain) act).updateAllCatArtsInfo(categoryToLoad, newAllArtsInfo);
				
//				artsListAdapter.updateData(allArtsInfo);
				artsListAdapter.notifyDataSetChanged();
			}
			else
			{
				System.out.println("ArrayList<ArtInfo> someResult=NNULL!!!");
			}

		}
	};

	@Override
	public void onDestroy()
	{
		// If the DownloadStateReceiver still exists, unregister it and set it to null
		if (artSelectedReceiver != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(artSelectedReceiver);
			artSelectedReceiver = null;
		}
		if (mMessageReceiver != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(mMessageReceiver);
			mMessageReceiver = null;
		}
		if (fragSelectedReceiver != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(fragSelectedReceiver);
			fragSelectedReceiver = null;
		}
		// Must always call the super method at the end.
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//				System.out.println("ArticlesListFragment onCreateView");
		//inflate root view
		View v;

		v = inflater.inflate(R.layout.fragment_arts_list_view, container, false);

		this.artsList = (ListView) v.findViewById(R.id.arts_list_view);

		if (this.allArtsInfo == null)
		{
			this.getAllArtsInfo(this.getCategoryToLoad());

			ArrayList<ArtInfo> def = ArtInfo.getDefaultAllArtsInfo(act);
			this.allArtsInfo = def;

			this.artsListAdapter = new ArtsListVIEWAdapter(act, R.layout.article_card, this.allArtsInfo);
			this.artsListAdapter.setArgs(this, artsList);

			this.artsList.setAdapter(artsListAdapter);

		}
		else
		{
			this.artsListAdapter = new ArtsListVIEWAdapter(act, R.layout.article_card, this.allArtsInfo);
			this.artsList.setAdapter(artsListAdapter);
			this.artsListAdapter.setArgs(this, artsList);
			//			this.artsListAdapter.notifyDataSetChanged();
		}

		///////

		//set onScrollListener
		//		if (android.os.Build.VERSION.SDK_INT >= 11)
		//		{
		//			this.artsList.setOnScrollListener(new RecyclerViewOnScrollListenerALLAUTHORS(act, this.categoryToLoad));
		//		}
		//		else if (this.pref.getBoolean("animate_lists", false) == true)
		//		{
		//			this.artsList.setOnScrollListener(new RecyclerViewOnScrollListenerPreHONEYCOMB(act));
		//		}
		//		else
		//		{
		//			SwipeRefreshLayout STR = (SwipeRefreshLayout) this.artsList.getParent();
		//			STR.setPadding(0, 0, 0, 0);
		//			LayoutParams lp = (LayoutParams) STR.getLayoutParams();
		//			lp.setMargins(0, 50, 0, 0);
		//			STR.setLayoutParams(lp);
		//		}

		return v;
	}

	private void getAllArtsInfo(String categoryToLoad2)
	{
		//		Log.i(categoryToLoad, "getAllArtsInfo called");
		// TODO Auto-generated method stub
		Intent intent = new Intent(this.act, GetInfoService.class);
		Bundle b = new Bundle();
		b.putString("categoryToLoad", this.getCategoryToLoad());
		b.putInt("pageToLaod", 1);
		intent.putExtras(b);
		this.act.startService(intent);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		//				System.out.println("ArticlesListFragment onViewCreated");
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		//		System.out.println("ArticlesListFragment onSaveInstanceState");
		super.onSaveInstanceState(outState);

		//category saving
		outState.putString("categoryToLoad", categoryToLoad);

		//save topImg and toolbar prop's

		outState.putInt("position", this.position);
		ArtInfo.writeAllArtsInfoToBundle(outState, allArtsInfo, curArtInfo);
	}

	public void setActivatedPosition(int position)
	{
		System.out.println("setActivatedPosition(int position: " + position);
		this.position = position;

		scrollToActivatedPosition();
	}

	public void scrollToActivatedPosition()
	{
		//		this.artsList.scrollToPosition(ArtsListAdapter.getPositionInRecyclerView(position));
		this.artsList.smoothScrollToPosition(ArtsListAdapter.getPositionInRecyclerView(position));
	}

	public int getMyActivatedPosition()
	{
		return this.position;
	}

	protected void restoreState(Bundle state)
	{
		//		System.out.println("restoring state from " + this.getClass().getSimpleName());

		if (state.containsKey("curArtInfo"))
		{
			this.curArtInfo = new ArtInfo(state.getStringArray("curArtInfo"));
		}
		else
		{
			//			System.out.println("this.curArtInfo in Bundle in " + this.getClass().getSimpleName() + " =null");
		}
		if (state.containsKey("position"))
		{
			this.position = state.getInt("position");
		}
		else
		{
			//			System.out.println("this.position in Bundle in " + this.getClass().getSimpleName() + " =null");
		}
		this.allArtsInfo = ArtInfo.restoreAllArtsInfoFromBundle(state, act);

	}

	public String getCategoryToLoad()
	{
		return categoryToLoad;
	}

	public void setCategoryToLoad(String categoryToLoad)
	{
		this.categoryToLoad = categoryToLoad;
	}
	
	public ArrayList<ArtInfo> getArtsInfo()
	{
		return this.allArtsInfo;
	}
}
