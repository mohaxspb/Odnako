/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.util.ArrayList;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.animations.RecyclerViewOnScrollListener;
import ru.kuchanov.odnako.animations.RecyclerViewOnScrollListenerPreHONEYCOMB;
import ru.kuchanov.odnako.animations.ScaleInOutItemAnimator;
import ru.kuchanov.odnako.db.DBActions;
import ru.kuchanov.odnako.db.ServiceDB;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
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

/**
 * Fragment for artsList. We use it as main Fragment for menu categories instead
 * of allAuthors and -Categories
 */
public class FragmentArtsRecyclerList extends Fragment
{
	static String LOG_TAG = FragmentArtsRecyclerList.class.getSimpleName() + "/";

//	private int topImgYCoord = 0;
//	private int toolbarYCoord = 0;
//	private int initialDistance;
	
	int pageToLoad=1;

	SwipeRefreshLayout swipeRef;

	private RecyclerView artsList;
	private ArtsListAdapter artsListAdapter;

	ActionBarActivity act;
	SharedPreferences pref;

	private String categoryToLoad;
	ArrayList<ArtInfo> allArtsInfo;
	ArtInfo curArtInfo;
	private int position = 0;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		//Log.d(LOG_TAG+categoryToLoad, "onCreate");
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
//			this.topImgYCoord = savedInstanceState.getInt("topImgYCoord");
//			this.toolbarYCoord = savedInstanceState.getInt("toolbarYCoord");
//			this.initialDistance = savedInstanceState.getInt("initialDistance");
			
			this.pageToLoad=savedInstanceState.getInt("pageToLoad");

			this.categoryToLoad = savedInstanceState.getString("categoryToLoad");
			this.curArtInfo = savedInstanceState.getParcelable(ArtInfo.KEY_CURENT_ART);
			this.allArtsInfo = savedInstanceState.getParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO);
			this.position = savedInstanceState.getInt("position");
		}

		LocalBroadcastManager.getInstance(this.act).registerReceiver(artsDataReceiver,
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
			String msg = intent.getStringExtra(DBActions.Msg.MSG);
			switch (msg)
			{
				case (DBActions.Msg.NO_NEW):
					Toast.makeText(act, "Новых статей не обнаружено!", Toast.LENGTH_SHORT).show();
				break;
				case (DBActions.Msg.NEW_QUONT):
					int quont = intent.getIntExtra(DBActions.Msg.QUONT, 0);
					Toast.makeText(act, "Обнаружено " + quont + " новых статей", Toast.LENGTH_SHORT).show();
				break;
				case DBActions.Msg.DB_ANSWER_SQLEXCEPTION_CAT:
					Toast.makeText(act, DBActions.Msg.DB_ANSWER_SQLEXCEPTION_CAT, Toast.LENGTH_LONG).show();
				break;
				case DBActions.Msg.DB_ANSWER_SQLEXCEPTION_AUTHOR:
					Toast.makeText(act, DBActions.Msg.DB_ANSWER_SQLEXCEPTION_AUTHOR, Toast.LENGTH_LONG).show();
				break;
				case DBActions.Msg.DB_ANSWER_SQLEXCEPTION_ARTS:
					Toast.makeText(act, DBActions.Msg.DB_ANSWER_SQLEXCEPTION_ARTS, Toast.LENGTH_LONG).show();
				break;
				case DBActions.Msg.DB_ANSWER_SQLEXCEPTION_ARTCAT:
					Toast.makeText(act, DBActions.Msg.DB_ANSWER_SQLEXCEPTION_ARTCAT, Toast.LENGTH_LONG).show();
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

	private BroadcastReceiver artsDataReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			//			Log.i(categoryToLoad, "artsDataReceiver onReceive called");
			ArrayList<ArtInfo> newAllArtsInfo;
			newAllArtsInfo = intent.getParcelableArrayListExtra(ArtInfo.KEY_ALL_ART_INFO);

			if (newAllArtsInfo != null)
			{
				if(pageToLoad==1)
				{
					allArtsInfo.clear();
				}
				allArtsInfo.addAll(newAllArtsInfo);
				artsListAdapter.notifyDataSetChanged();

				((ActivityBase) act).updateAllCatArtsInfo(categoryToLoad, newAllArtsInfo);
			}
			else
			{
				System.out.println("ArrayList<ArtInfo> someResult=NULL!!!");
			}

			if (swipeRef.isRefreshing())
			{
				swipeRef.setRefreshing(false);
			}

		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//		Log.d(LOG_TAG, "onCreateView");
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
				pageToLoad=1;
				getAllArtsInfo(true);
			}
		});

		this.artsList = (RecyclerView) v.findViewById(R.id.arts_list_view);
		this.artsList.setItemAnimator(new DefaultItemAnimator());
//		this.artsList.setItemAnimator(new ScaleInOutItemAnimator(artsList));
		this.artsList.setLayoutManager(new LinearLayoutManager(act));

		if (this.allArtsInfo == null)
		{
			Log.i(categoryToLoad, "this.allArtsInfo=NULL");

			this.getAllArtsInfo(false);

			ArrayList<ArtInfo> def = new ArrayList<ArtInfo>();
			def.add(new ArtInfo("empty", "Статьи загружаются, подождите пожалуйста", "empty", "empty", "empty"));
			this.allArtsInfo = def;

			this.artsListAdapter = new ArtsListAdapter(act, this.allArtsInfo, artsList, this);
			this.artsList.setAdapter(artsListAdapter);
			

		}
		else
		{
			this.artsListAdapter = new ArtsListAdapter(act, allArtsInfo, artsList, this);
			this.artsList.setAdapter(artsListAdapter);

			this.artsListAdapter.notifyDataSetChanged();
		}

		///////

		//set onScrollListener
		if (android.os.Build.VERSION.SDK_INT >= 11)
		{
			this.artsList.setOnScrollListener(new RecyclerViewOnScrollListener(act, this.categoryToLoad)
			{
				public void onLoadMore()
				{
//					if(loading)
					pageToLoad++;
					getAllArtsInfo(true);
					Log.e(LOG_TAG, "Start loading from bottom!");
				}
			});
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

		this.swipeRef.setRefreshing(true);

		Intent intent = new Intent(this.act, ServiceDB.class);
		Bundle b = new Bundle();
		b.putString("categoryToLoad", this.getCategoryToLoad());
		b.putInt("pageToLoad", this.pageToLoad);
		b.putLong("timeStamp", System.currentTimeMillis());
		b.putBoolean("startDownload", startDownload);
		intent.putExtras(b);
		this.act.startService(intent);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		//		Log.d(LOG_TAG + categoryToLoad, "onSaveInstanceState called");
		super.onSaveInstanceState(outState);

		//category saving
		outState.putString("categoryToLoad", categoryToLoad);

		//save topImg and toolbar prop's
//		outState.putInt("topImgYCoord", this.topImgYCoord);
//		outState.putInt("toolbarYCoord", this.toolbarYCoord);
//		outState.putInt("initialDistance", this.initialDistance);
		
		;outState.putInt("pageToLoad", this.pageToLoad);

		outState.putInt("position", this.position);
		outState.putParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO, allArtsInfo);
		outState.putParcelable(ArtInfo.KEY_CURENT_ART, allArtsInfo.get(position));
		//		Log.d(LOG_TAG + categoryToLoad, "onSaveInstanceState finished");
	}

	public void setActivatedPosition(int position)
	{
		//		System.out.println("setActivatedPosition(int position: " + position);
		this.position = position;

		this.artsList.scrollToPosition(ArtsListAdapter.getPositionInRecyclerView(position));
	}

	public int getMyActivatedPosition()
	{
		return this.position;
	}

	////////setters and getters for TopImg and Toolbar position and Alpha
//	public int getToolbarYCoord()
//	{
//		return toolbarYCoord;
//	}
//
//	public void setToolbarYCoord(int toolbarYCoord)
//	{
//		this.toolbarYCoord = toolbarYCoord;
//	}
//
//	public void setInitialDistance(int initialDistance)
//	{
//		this.initialDistance = initialDistance;
//	}

//	public int getInitialDistance()
//	{
//		return initialDistance;
//	}
//
//	public int getTopImgYCoord()
//	{
//		return topImgYCoord;
//	}
//
//	public void setTopImgYCoord(int topImgYCoord)
//	{
//		this.topImgYCoord = topImgYCoord;
//	}

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
		if (artsDataReceiver != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(artsDataReceiver);
			artsDataReceiver = null;
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
}
