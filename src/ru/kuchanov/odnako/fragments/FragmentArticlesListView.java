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
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.ArtsListAdapter;
import ru.kuchanov.odnako.lists_and_utils.ArtsListVIEWAdapter;
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
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Fragment for articles list using ListView instead of RecyclerView
 */
public class FragmentArticlesListView extends Fragment
{
	private static final String LOG_TAG = FragmentArticlesListView.class.getSimpleName();
	private ListView artsList;
	private ArtsListVIEWAdapter artsListAdapter;

	SwipeRefreshLayout swipeRef;

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
			//			this.restoreState(savedInstanceState);

			this.position = savedInstanceState.getInt("position");
			this.allArtsInfo = savedInstanceState.getParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO);
			this.curArtInfo = savedInstanceState.getParcelable(ArtInfo.KEY_CURENT_ART);
		}
		// Register to receive messages.
		// We are registering an observer (mMessageReceiver) to receive Intents
		// with actions named "custom-event-name".
		LocalBroadcastManager.getInstance(this.act).registerReceiver(artsDATAReceiver,
		new IntentFilter(this.getCategoryToLoad()));

		//reciver for scrolling and highligting selected position
		//only if there is twoPane MODE and there is allAuthors list currently selected
//		if (((ActivityMain) act).getCurentCategoryPosition() == 4 && this.pref.getBoolean("twoPane", false))
		if (((ActivityMain) act).getCurentCategoryPosition() == 3 && this.pref.getBoolean("twoPane", false))
		{
			LocalBroadcastManager.getInstance(this.act).registerReceiver(artSelectedReceiver,
			new IntentFilter(this.getCategoryToLoad() + "art_position"));
		}

		//reciver for notify when frag selected
		LocalBroadcastManager.getInstance(this.act).registerReceiver(fragSelectedReceiver,
		new IntentFilter(this.getCategoryToLoad() + "_notify_that_selected"));

		//reciver for messages from dbService
		LocalBroadcastManager.getInstance(this.act).registerReceiver(dbAnswer,
		new IntentFilter(this.getCategoryToLoad() + "msg"));
	}

	private BroadcastReceiver fragSelectedReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.i(LOG_TAG + "/" + categoryToLoad, "fragSelectedReceiver onReceive called");
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

	// Our handler for received Intents. This will be called whenever an Intent
	// with an action named "custom-event-name" is broadcasted.
	private BroadcastReceiver artsDATAReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.i(categoryToLoad, "artsDATAReceiver onReceive called");
			// Get extra data included in the Intent
			//			ArrayList<ArtInfo> newAllArtsInfo = ArtInfo.restoreAllArtsInfoFromBundle(intent.getExtras(), LOG_TAG
			//			+ categoryToLoad);
			ArrayList<ArtInfo> newAllArtsInfo;
			//			ArrayList<ArtInfo> newAllArtsInfo = intent.getParcelableArrayListExtra(ArtInfo.KEY_ALL_ART_INFO);
			newAllArtsInfo = intent.getExtras().getParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO);

			if (newAllArtsInfo != null)
			{
				allArtsInfo.clear();
				allArtsInfo.addAll(newAllArtsInfo);
				Log.i(categoryToLoad, "categoryToLoad artsInfo.size() " + categoryToLoad + ": " + allArtsInfo.size());

				//				((ActivityMain) act).updateAllCatArtsInfo(categoryToLoad, newAllArtsInfo);

				//				artsListAdapter.updateData(allArtsInfo);
				artsListAdapter.notifyDataSetChanged();
			}
			else
			{
				Log.i(categoryToLoad, "ArrayList<ArtInfo> someResult=NULL!!!");
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
		//				System.out.println("ArticlesListFragment onCreateView");
		Log.d(LOG_TAG + "/" + categoryToLoad, "onCreateView called");
		//inflate root view
		View v;

		v = inflater.inflate(R.layout.fragment_arts_list_view, container, false);

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

		this.artsList = (ListView) v.findViewById(R.id.arts_list_view);

		if (this.allArtsInfo == null)
		{
			this.getAllArtsInfo(false);

			//			ArrayList<ArtInfo> def = ArtInfo.getDefaultAllArtsInfo(act);
			//			this.allArtsInfo = def;
			ArrayList<ArtInfo> def = new ArrayList<ArtInfo>();
			def.add(new ArtInfo("empty", "Статьи загружаются, подождите пожалуйста", "empty", "empty", "empty"));
			this.allArtsInfo = def;

			this.artsListAdapter = new ArtsListVIEWAdapter(act, R.layout.article_card, this.allArtsInfo);
			this.artsListAdapter.setArgs(this, artsList);

			this.artsList.setAdapter(artsListAdapter);

		}
		else
		{
			this.artsListAdapter = new ArtsListVIEWAdapter(act, R.layout.article_card, this.allArtsInfo);
			this.artsList.setAdapter(artsListAdapter);
			//			this.artsListAdapter.setArgs(this, artsList);

			this.artsListAdapter.notifyDataSetChanged();
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

	//	private void getAllArtsInfo(String categoryToLoad2)
	private void getAllArtsInfo(boolean startDownload)
	{
		//		Log.i(categoryToLoad, "getAllArtsInfo called");
		// TODO Auto-generated method stub
		Intent intent = new Intent(this.act, ServiceDB.class);
		Bundle b = new Bundle();
		b.putString("categoryToLoad", this.getCategoryToLoad());
		b.putInt("pageToLaod", 1);
		b.putLong("timeStamp", System.currentTimeMillis());
		b.putBoolean("startDownload", startDownload);
		intent.putExtras(b);
		this.act.startService(intent);

		if (!this.swipeRef.isRefreshing())
		{
			this.swipeRef.setRefreshing(true);
		}
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
		//		ArtInfo.writeAllArtsInfoToBundle(outState, allArtsInfo, curArtInfo);
		outState.putParcelable(ArtInfo.KEY_CURENT_ART, this.curArtInfo);
		outState.putParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO, this.allArtsInfo);
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

	//	protected void restoreState(Bundle state)
	//	{
	//		System.out.println("restoring state from " + this.getClass().getSimpleName());

	//		if (state.containsKey("curArtInfo"))
	//		{
	//			this.curArtInfo = new ArtInfo(state.getStringArray("curArtInfo"));
	//		}
	//		else
	//		{
	//			//			System.out.println("this.curArtInfo in Bundle in " + this.getClass().getSimpleName() + " =null");
	//		}
	//		if (state.containsKey("position"))
	//		{
	//			this.position = state.getInt("position");
	//		}
	//		else
	//		{
	//			//			System.out.println("this.position in Bundle in " + this.getClass().getSimpleName() + " =null");
	//		}
	//		this.allArtsInfo = ArtInfo.restoreAllArtsInfoFromBundle(state, LOG_TAG+categoryToLoad);

	//	}

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

	@Override
	public void onDestroy()
	{
		// If the DownloadStateReceiver still exists, unregister it and set it to null
		if (artSelectedReceiver != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(artSelectedReceiver);
			artSelectedReceiver = null;
		}
		if (artsDATAReceiver != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(artsDATAReceiver);
			artsDATAReceiver = null;
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
