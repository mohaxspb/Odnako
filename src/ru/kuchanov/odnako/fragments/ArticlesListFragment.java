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
import ru.kuchanov.odnako.lists_and_utils.ArtsListAdapter;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

public class ArticlesListFragment extends Fragment
{
	private int topImgYCoord = 0;
	private int toolbarYCoord = 0;
	private int initialDistance;

	private RecyclerView artsList;
	private ArtsListAdapter artsListAdapter;

	ActionBarActivity act;
	SharedPreferences pref;

	String categoryToLoad;
	ArrayList<ArtInfo> allArtsInfo;
	ArtInfo curArtInfo;
	private int position = ListView.INVALID_POSITION;

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";


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
			this.categoryToLoad = fromArgs.getString("categoryToLoad");
		}
		else
		{
			System.out.println("empty fromArgs!");
		}

		//restore topImg and toolbar prop's
		if (savedInstanceState != null)
		{
			this.topImgYCoord = savedInstanceState.getInt("topImgYCoord");
			this.toolbarYCoord = savedInstanceState.getInt("toolbarYCoord");
			this.initialDistance = savedInstanceState.getInt("initialDistance");

			this.restoreState(savedInstanceState);
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
			ArrayList<ArtInfo> newAllArtsInfo = ArtInfo.restoreAllArtsInfoFromBundle(intent.getExtras(), act);

			if (newAllArtsInfo != null)
			{
				allArtsInfo.clear();
				allArtsInfo.addAll(newAllArtsInfo);
				artsListAdapter.notifyDataSetChanged();

//				((ActivityMain) act).setAllArtsInfo(allArtsInfo);
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
		//		System.out.println("ArticlesListFragment onCreateView");
		//inflate root view
		View v;

		v = inflater.inflate(R.layout.fragment_arts_list, container, false);

		this.artsList = (RecyclerView) v.findViewById(R.id.arts_list_view);

		if (this.allArtsInfo == null)
		{
			this.getAllArtsInfo(this.categoryToLoad);

			ArrayList<ArtInfo> def = ArtInfo.getDefaultAllArtsInfo(act);
			this.allArtsInfo = def;
			
//			((ActivityMain) act).setAllArtsInfo(allArtsInfo);

			this.artsListAdapter = new ArtsListAdapter(act, this.allArtsInfo, artsList);
			this.artsList.setAdapter(artsListAdapter);

			this.artsList.setItemAnimator(new DefaultItemAnimator());
			this.artsList.setLayoutManager(new LinearLayoutManager(act));
		}
		else
		{
//			((ActivityMain) act).setAllArtsInfo(allArtsInfo);

			this.artsListAdapter = new ArtsListAdapter(act, allArtsInfo, artsList);
			this.artsList.setAdapter(artsListAdapter);
			this.artsList.setItemAnimator(new DefaultItemAnimator());
			this.artsList.setLayoutManager(new LinearLayoutManager(act));
			this.artsListAdapter.notifyDataSetChanged();
		}

		///////

		//set onScrollListener
		if (android.os.Build.VERSION.SDK_INT >= 11)
		{
			this.artsList.setOnScrollListener(new RecyclerViewOnScrollListener(act, this));
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
		//		if(CheckIfServiceIsRunning.check(act, GetInfoService.class.getSimpleName()))
		//		{
		//			this.act
		//		}
		this.act.startService(intent);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		//		System.out.println("ArticlesListFragment onViewCreated");
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION))
		{
			setActivatedPosition(savedInstanceState.getInt("position"));
		}
	}

	@Override
	public void onAttach(Activity activity)
	{
		//		System.out.println("ArticlesListFragment onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onDetach()
	{
		//		System.out.println("ArticlesListFragment onDetach");
		super.onDetach();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		//		System.out.println("ArticlesListFragment onSaveInstanceState");
		super.onSaveInstanceState(outState);

		//save topImg and toolbar prop's
		outState.putInt("topImgYCoord", this.topImgYCoord);
		outState.putInt("toolbarYCoord", this.toolbarYCoord);
		outState.putInt("initialDistance", this.initialDistance);

		ArtInfo.writeAllArtsInfoToBundle(outState, allArtsInfo, curArtInfo);
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
		this.position = position;

		if (position != ListView.INVALID_POSITION)
		{
			scrollToActivatedPosition();
		}
		else
		{
			System.out.println("setActivatedPosition ERROR: position=ListView.INVALID_POSITION");
		}
	}

	public void scrollToActivatedPosition()
	{
		this.artsList.smoothScrollToPosition(position);
	}

	public int getMyActivatedPosition()
	{
		return this.position;
	}

	/**
	 * @return the artsListAdapter
	 */
	public ArtsListAdapter getArtsListAdapter()
	{
		return artsListAdapter;
	}

	public void ArtsListAdapter(ArtsListAdapter artsListAdapter)
	{
		this.artsListAdapter = artsListAdapter;
	}

	public RecyclerView getArtsListView()
	{
		return this.artsList;
	}

	////////setters and getters for TopImg and Toolbar position and Alpha
	public int getToolbarYCoord()
	{
		return toolbarYCoord;
	}

	public void setToolbarYCoord(int toolbarYCoord)
	{
		this.toolbarYCoord = toolbarYCoord;
	}
	
	public void setInitialDistance(int initialDistance)
	{
		this.initialDistance=initialDistance;
	}
	
	public int getInitialDistance()
	{
		return initialDistance;
	}

	public int getTopImgYCoord()
	{
		return topImgYCoord;
	}

	public void setTopImgYCoord(int topImgYCoord)
	{
		this.topImgYCoord = topImgYCoord;
	}

	protected void restoreState(Bundle state)
	{
		System.out.println("restoring state from " + this.getClass().getSimpleName());

		if (state.containsKey("curArtInfo"))
		{
			this.curArtInfo = new ArtInfo(state.getStringArray("curArtInfo"));
		}
		else
		{
			System.out.println("this.curArtInfo in Bundle in " + this.getClass().getSimpleName() + " =null");
		}
		if (state.containsKey("position"))
		{
			this.position = state.getInt("position");
		}
		else
		{
			System.out.println("this.position in Bundle in " + this.getClass().getSimpleName() + " =null");
		}
		this.allArtsInfo = ArtInfo.restoreAllArtsInfoFromBundle(state, act);

	}

	
}
