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
import ru.kuchanov.odnako.animations.RecyclerViewOnScrollListener;
import ru.kuchanov.odnako.animations.RecyclerViewOnScrollListenerPreHONEYCOMB;
import ru.kuchanov.odnako.download.GetInfoService;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.ArtsListRecyclerViewAdapter;
import android.app.Activity;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

public class ArticlesListFragment extends Fragment
{

	private RecyclerView artsList;
	ActionBarActivity act;
	SharedPreferences pref;

	String categoryToLoad;
	ArrayList<ArtInfo> allArtsInfo;

	private ArtsListRecyclerViewAdapter artsListAdapter;
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ArticlesListFragment onCreate");
		super.onCreate(savedInstanceState);

		this.act = (ActionBarActivity) this.getActivity();
		this.pref = PreferenceManager.getDefaultSharedPreferences(act);

		Bundle fromArgs = this.getArguments();
		if (fromArgs != null)
		{
			this.allArtsInfo = ArtInfo.restoreAllArtsInfoFromBundle(fromArgs, act);
			this.categoryToLoad = fromArgs.getString("categoryToLoad");
		}
		else if (savedInstanceState != null)
		{
			//			this.allArtsInfo=ArtInfo.restoreAllArtsInfoFromBundle(fromArgs, act);
		}
		else
		{
			System.out.println("empty allArtsInfo, so we need to load it from INTERNET!");
		}

		// Register to receive messages.
		// We are registering an observer (mMessageReceiver) to receive Intents
		// with actions named "custom-event-name".
		LocalBroadcastManager.getInstance(this.act).registerReceiver(mMessageReceiver,
		new IntentFilter(this.categoryToLoad));
	}

	// Our handler for received Intents. This will be called whenever an Intent
	// with an action named "custom-event-name" is broadcasted.
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// Get extra data included in the Intent
			String message = intent.getStringExtra("message");
			Log.d("receiver", "Got message: " + message);
			
			ArrayList<ArtInfo> newAllArtsInfo=ArtInfo.restoreAllArtsInfoFromBundle(intent.getExtras(), act);
			
			if (newAllArtsInfo != null)
			{
				allArtsInfo = newAllArtsInfo;
				
				((ActivityMain) act).setAllArtsInfo(allArtsInfo);

				artsListAdapter = new ArtsListRecyclerViewAdapter(act, allArtsInfo, artsList);
				artsList.setAdapter(artsListAdapter);
				artsList.setItemAnimator(new DefaultItemAnimator());
				artsList.setLayoutManager(new LinearLayoutManager(act));

				artsListAdapter.notifyDataSetChanged();
			}
			else
			{
				System.out.println("ArrayList<ArtInfo> someResult=NNULL!!!");
			}
			
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		System.out.println("ArticlesListFragment onCreateView");
		//inflate root view
		View v;

		//		v = inflater.inflate(R.layout.fragment_arts_list, new LinearLayout(this.getActivity()));
		v = inflater.inflate(R.layout.fragment_arts_list, container, false);

		this.artsList = (RecyclerView) v.findViewById(R.id.arts_list_view);

		if (this.allArtsInfo == null)
		{
			//TODO
			this.getAllArtsInfo(this.categoryToLoad);

			ArrayList<ArtInfo> def = ArtInfo.getDefaultAllArtsInfo(act);
			System.out.println(def.get(1).toString());

			this.artsListAdapter = new ArtsListRecyclerViewAdapter(act, def, artsList);
			this.artsList.setAdapter(artsListAdapter);

			this.artsList.setItemAnimator(new DefaultItemAnimator());
			this.artsList.setLayoutManager(new LinearLayoutManager(act));
		}
		else
		{
			((ActivityMain) act).setAllArtsInfo(allArtsInfo);

			this.artsListAdapter = new ArtsListRecyclerViewAdapter(act, allArtsInfo, artsList);
			this.artsList.setAdapter(artsListAdapter);
			this.artsList.setItemAnimator(new DefaultItemAnimator());
			this.artsList.setLayoutManager(new LinearLayoutManager(act));
		}

		///////

		//set onScrollListener
		if (android.os.Build.VERSION.SDK_INT >= 11)
		{
			this.artsList.setOnScrollListener(new RecyclerViewOnScrollListener(act));
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


	private void getAllArtsInfo(String categoryToLoad2)
	{
		// TODO Auto-generated method stub
		Intent intent = new Intent(this.act, GetInfoService.class);
		Bundle b = new Bundle();
		b.putString("categoryToLoad", this.categoryToLoad);
		b.putInt("pageToLaod", 1);
		intent.putExtras(b);
		this.act.startService(intent);

//		ParsePageForAllArtsInfo parse = new ParsePageForAllArtsInfo(this.categoryToLoad, 1, this.act, this);
//		parse.execute();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		System.out.println("ArticlesListFragment onViewCreated");
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION))
		{
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity)
	{
		System.out.println("ArticlesListFragment onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onDetach()
	{
		System.out.println("ArticlesListFragment onDetach");
		super.onDetach();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		System.out.println("ArticlesListFragment onSaveInstanceState");
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION)
		{
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick)
	{
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		//		artsList.setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
		//		this.artsList.seti
	}

	public void setActivatedPosition(int position)
	{
		System.out.println("setActivatedPosition(int position: " + position);
		if (position == ListView.INVALID_POSITION)
		{
			//			artsList.setItemChecked(mActivatedPosition, false);
		}
		else
		{
			//			artsList.setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	public void scrollToActivatedPosition()
	{
		this.artsList.smoothScrollToPosition(mActivatedPosition);
	}

	public int getMyActivatedPosition()
	{
		return this.mActivatedPosition;
	}

	/**
	 * @return the artsListAdapter
	 */
	public ArtsListRecyclerViewAdapter getArtsListAdapter()
	{
		return artsListAdapter;
	}

	/**
	 * @param artsListAdapter
	 *            the artsListAdapter to set
	 */
	public void ArtsListRecyclerViewAdapter(ArtsListRecyclerViewAdapter artsListAdapter)
	{
		this.artsListAdapter = artsListAdapter;
	}

	public RecyclerView getArtsListView()
	{
		return this.artsList;
	}
}
