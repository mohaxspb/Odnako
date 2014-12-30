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
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.ArtsListAdapter;
import ru.kuchanov.odnako.services.ServiceDB;
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
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class FragmentArtsRecyclerList extends Fragment
{
	private int topImgYCoord = 0;
	private int toolbarYCoord = 0;
	private int initialDistance;

	SwipeRefreshLayout swipeRef;

	private RecyclerView artsList;
	private ArtsListAdapter artsListAdapter;

	ActionBarActivity act;
	SharedPreferences pref;

	private String categoryToLoad;
	ArrayList<ArtInfo> allArtsInfo;
	ArtInfo curArtInfo;
	private int position = 0;

	static String LOG_TAG = FragmentArtsRecyclerList.class.getSimpleName() + "/";

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		//						System.out.println("ArticlesListFragment onCreate");
		super.onCreate(savedInstanceState);

		this.act = (ActionBarActivity) this.getActivity();
		this.pref = PreferenceManager.getDefaultSharedPreferences(act);

		Bundle fromArgs = this.getArguments();
		if (fromArgs != null)
		{
			//			Log.i(categoryToLoad, "fromArgs != null");
			this.setCategoryToLoad(fromArgs.getString("categoryToLoad"));
		}
		else
		{
			System.out.println("empty fromArgs!");
		}

		//restore topImg and toolbar prop's
		if (savedInstanceState != null)
		{
			//			Log.i(categoryToLoad, "savedInstanceState != null");
			this.topImgYCoord = savedInstanceState.getInt("topImgYCoord");
			this.toolbarYCoord = savedInstanceState.getInt("toolbarYCoord");
			this.initialDistance = savedInstanceState.getInt("initialDistance");

			//			this.position=savedInstanceState.getInt(STATE_ACTIVATED_POSITION);
			this.categoryToLoad = savedInstanceState.getString("categoryToLoad");

			this.restoreState(savedInstanceState);
		}
		// Register to receive messages.
		// We are registering an observer (mMessageReceiver) to receive Intents
		// with actions named "custom-event-name".
		LocalBroadcastManager.getInstance(this.act).registerReceiver(mMessageReceiver,
		new IntentFilter(this.getCategoryToLoad()));

		//reciver for scrolling and highligting selected position
		LocalBroadcastManager.getInstance(this.act).registerReceiver(artSelectedReceiver,
		new IntentFilter(this.getCategoryToLoad() + "art_position"));

		//reciver for notify when frag selected
		LocalBroadcastManager.getInstance(this.act).registerReceiver(fragSelectedReceiver,
		new IntentFilter(this.getCategoryToLoad() + "_notify_that_selected"));

		//reciver for messages from dbService
		LocalBroadcastManager.getInstance(this.act).registerReceiver(dbAnswer,
		new IntentFilter(this.getCategoryToLoad() + "msg"));
	}

	private BroadcastReceiver dbAnswer = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String msg = intent.getStringExtra("msg");
			switch (msg)
			{
				case (ServiceDB.Msg.NO_NEW):
					Toast.makeText(act, "Новых статей не обнаружено!", Toast.LENGTH_SHORT).show();
				break;
				case (ServiceDB.Msg.NEW_QUONT):
					int quont = intent.getIntExtra(ServiceDB.Msg.QUONT, 0);
					Toast.makeText(act, "Обнаружено " + quont + " новых статей", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

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
			//			Log.i(categoryToLoad, "mMessageReceiver onReceive called");
			// Get extra data included in the Intent
			ArrayList<ArtInfo> newAllArtsInfo = ArtInfo.restoreAllArtsInfoFromBundle(intent.getExtras(), LOG_TAG+categoryToLoad);

			if (newAllArtsInfo != null)
			{
				//				allArtsInfo.clear();
				allArtsInfo = new ArrayList<ArtInfo>();
				allArtsInfo.addAll(newAllArtsInfo);
				artsListAdapter.notifyDataSetChanged();

				((ActivityMain) act).updateAllCatArtsInfo(categoryToLoad, newAllArtsInfo);
			}
			else
			{
				System.out.println("ArrayList<ArtInfo> someResult=NNULL!!!");
			}

			if (swipeRef.isRefreshing())
			{
				swipeRef.setRefreshing(false);
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
		if (dbAnswer != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(dbAnswer);
			dbAnswer = null;
		}
		// Must always call the super method at the end.
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//						System.out.println("ArticlesListFragment onCreateView");
		//inflate root view
		View v;

		v = inflater.inflate(R.layout.fragment_arts_list, container, false);

		this.swipeRef = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);
		//workaround to fix issue with not showing refreshing indicator before swipeRef.onMesure() was called
		//as I understand before onResume of Activity
		this.swipeRef.setColorSchemeColors(R.color.material_red_300,
		R.color.material_red_500,
		R.color.material_red_500,
		R.color.material_red_500);

		TypedValue typed_value = new TypedValue();
		getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
		this.swipeRef.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));

		this.swipeRef.setProgressViewEndTarget(false, getResources().getDimensionPixelSize(typed_value.resourceId));
		////set on swipe listener
		this.swipeRef.setOnRefreshListener(new OnRefreshListener()
		{

			@Override
			public void onRefresh()
			{
				getAllArtsInfo(true);
			}
		});

		////

		this.artsList = (RecyclerView) v.findViewById(R.id.arts_list_view);
		this.artsList.setItemAnimator(new DefaultItemAnimator());
		this.artsList.setLayoutManager(new LinearLayoutManager(act));

		if (this.allArtsInfo == null)
		{
			Log.i(categoryToLoad, "this.allArtsInfo=NULL");

			this.getAllArtsInfo(false);

			//			ArrayList<ArtInfo> def = ArtInfo.getDefaultAllArtsInfo(act);
			//			this.allArtsInfo = def;
			//
			//			this.artsListAdapter = new ArtsListAdapter(act, this.allArtsInfo, artsList, this);
			//			
			//			this.artsList.setAdapter(artsListAdapter);
			try
			{
				Log.e(categoryToLoad, "savedInstanceState==null: "
				+ String.valueOf(savedInstanceState == null));
				Log.e(categoryToLoad, "savedInstanceState.containsKey('STATE_ACTIVATED_POSITION'): "
				+ String.valueOf(savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)));
			} catch (Exception e)
			{
				Log.e("catched exception", "catched exception");
			}

			ArrayList<ArtInfo> def = new ArrayList<ArtInfo>();
			def.add(new ArtInfo("empty", "Статьи загружаются, подождите пожалуйста", "empty", "empty", "empty"));
			this.allArtsInfo = def;

			this.artsListAdapter = new ArtsListAdapter(act, this.allArtsInfo, artsList, this);

			this.artsList.setAdapter(artsListAdapter);

		}
		else
		{
			Log.i(categoryToLoad, "this.allArtsInfo!=NULL");
			this.artsListAdapter = new ArtsListAdapter(act, allArtsInfo, artsList, this);
			this.artsList.setAdapter(artsListAdapter);

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

	private void getAllArtsInfo(boolean startDownload)
	{
		Log.i(categoryToLoad, "getAllArtsInfo called");
		// TODO Auto-generated method stub
		//		Intent intent = new Intent(this.act, GetInfoService.class);
		//		Bundle b = new Bundle();
		//		b.putString("categoryToLoad", this.getCategoryToLoad());
		//		b.putInt("pageToLaod", 1);
		//		intent.putExtras(b);
		//		this.act.startService(intent);

		//		this.swipeRef.setProgressViewEndTarget(false, (int) DipToPx.convert(56, act));
		//		this.swipeRef.setProgressBackgroundColor(R.color.odnako);
		//		this.swipeRef.setColorSchemeColors(R.color.material_grey_300,
		//		R.color.material_grey_500,
		//		R.color.material_grey_700,
		//		R.color.material_grey_900);
		//////////////
		this.swipeRef.setRefreshing(true);

		Intent intent = new Intent(this.act, ServiceDB.class);
		Bundle b = new Bundle();
		b.putString("categoryToLoad", this.getCategoryToLoad());
		b.putInt("pageToLoad", 1);
		b.putLong("timeStamp", System.currentTimeMillis());
		b.putBoolean("startDownload", startDownload);
		intent.putExtras(b);
		this.act.startService(intent);
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
		//				System.out.println("ArticlesListFragment onSaveInstanceState");
		Log.d(LOG_TAG + categoryToLoad, "onSaveInstanceState called");
		super.onSaveInstanceState(outState);

		//category saving
		outState.putString("categoryToLoad", categoryToLoad);

		//save topImg and toolbar prop's
		outState.putInt("topImgYCoord", this.topImgYCoord);
		outState.putInt("toolbarYCoord", this.toolbarYCoord);
		outState.putInt("initialDistance", this.initialDistance);

		outState.putInt(STATE_ACTIVATED_POSITION, this.position);
		ArtInfo.writeAllArtsInfoToBundle(outState, allArtsInfo, curArtInfo);
		Log.d(LOG_TAG + categoryToLoad, "onSaveInstanceState finished");
	}

	private void restoreState(Bundle state)
	{
		Log.d(LOG_TAG + categoryToLoad, "restoreState called");

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
		this.allArtsInfo = ArtInfo.restoreAllArtsInfoFromBundle(state, LOG_TAG+categoryToLoad);
		Log.d(LOG_TAG + categoryToLoad, "restoreState finished");
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
		this.initialDistance = initialDistance;
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

	public String getCategoryToLoad()
	{
		return categoryToLoad;
	}

	public void setCategoryToLoad(String categoryToLoad)
	{
		this.categoryToLoad = categoryToLoad;
	}
}
