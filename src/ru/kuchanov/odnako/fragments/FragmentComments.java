/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.util.ArrayList;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.animations.RecyclerCommentsOnScrollListener;
import ru.kuchanov.odnako.animations.SpacesItemDecoration;
import ru.kuchanov.odnako.custom.view.MyLinearLayoutManager;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Msg;
import ru.kuchanov.odnako.db.ServiceComments;
import ru.kuchanov.odnako.download.CommentInfo;
import ru.kuchanov.odnako.lists_and_utils.RecyclerAdapterCommentsFragment;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FragmentComments extends Fragment
{
	//	public final static String LOG = FragmentComments.class.getSimpleName() + "/";
	public final static String LOG = "FragmentComments/";

	private AppCompatActivity act;

	public Article article;

	public final static String KEY_COMMENTS_DATA = "commentsData";
	private ArrayList<CommentInfo> commentsInfoList;

	public final static String KEY_URL_TO_LOAD = "urlToLoad";
	public int pageToLoad = 1;
	public final static String KEY_PAGE_TO_LOAD = "pageToLoad";
	public boolean isLoading = false;

	private SwipeRefreshLayout swipeRef;

	private RecyclerView recycler;
	private RecyclerAdapterCommentsFragment recyclerAdapter;

	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		//System.out.println("CommentsFragment onCreate");

		this.act = (AppCompatActivity) this.getActivity();

		if (this.getArguments() != null)
		{
			this.article = this.getArguments().getParcelable(Article.KEY_CURENT_ART);
		}
		if (savedState != null)
		{
			this.article = savedState.getParcelable(Article.KEY_CURENT_ART);
			this.commentsInfoList = savedState.getParcelableArrayList(CommentInfo.KEY_ALL_COMMENTS_LIST);
			this.isLoading = savedState.getBoolean("isLoading");
			this.pageToLoad = savedState.getInt(KEY_PAGE_TO_LOAD);
		}

		LocalBroadcastManager.getInstance(this.act).registerReceiver(commentsDataReceiver,
		new IntentFilter(this.article.getUrl() + LOG));
	}

	//	public void setAdapter()

	private BroadcastReceiver commentsDataReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.i(LOG, "commentsDataReceiver onReceive");

			if (!isAdded())
			{
				Log.e(LOG, "fragment not added! RETURN!");
				return;
			}

			String msg = intent.getStringExtra(Msg.MSG);
			int page = intent.getIntExtra(KEY_PAGE_TO_LOAD, 1);
			pageToLoad = page;
			switch (msg)
			{
				case Const.EMPTY_STRING:
					ArrayList<CommentInfo> downloadedComments = intent.getParcelableArrayListExtra(KEY_COMMENTS_DATA);

					if (pageToLoad == 1)
					{
						commentsInfoList = downloadedComments;
						recyclerAdapter = new RecyclerAdapterCommentsFragment(act, article, commentsInfoList);
						recycler.setAdapter(recyclerAdapter);
					}
					else
					{
						commentsInfoList.addAll(downloadedComments);
						((RecyclerAdapterCommentsFragment) recycler.getAdapter()).addCommentsInfo(downloadedComments);
						recycler.getAdapter().notifyItemRangeInserted(
						recycler.getAdapter().getItemCount() - downloadedComments.size(), downloadedComments.size());
						//we can get 0 comments, so we must decrement page
						if (downloadedComments.size() == 0)
						{
							pageToLoad--;
						}
					}
				break;
				case Const.Error.CONNECTION_ERROR:
					Toast.makeText(act, Const.Error.CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
					if (pageToLoad != 1)
					{
						pageToLoad--;
					}
				break;
			}
			swipeRef.setRefreshing(false);
			isLoading = false;

			//workaround to fix issue with not showing refreshing indicator before swipeRef.onMesure() was called
			//as I understand before onResume of Activity
			TypedValue typed_value = new TypedValue();
			getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value,
			true);

			//this.swipeRef.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
			swipeRef.setProgressViewEndTarget(false, getResources().getDimensionPixelSize(typed_value.resourceId) + 15);
		}
	};

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//Log.i(LOG, "onCreateView");
		View v = inflater.inflate(R.layout.fragment_comments_list, container, false);

		//find all views
		this.swipeRef = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);

		if (this.pageToLoad == 1)
		{
			//workaround to fix issue with not showing refreshing indicator before swipeRef.onMesure() was called
			//as I understand before onResume of Activity

			TypedValue typed_value = new TypedValue();
			getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value,
			true);
			this.swipeRef.setProgressViewOffset(false, 0,
			getResources().getDimensionPixelSize(typed_value.resourceId) + 15);

			this.swipeRef.setProgressViewEndTarget(false,
			getResources().getDimensionPixelSize(typed_value.resourceId) + 15);

		}
		else
		{
			//workaround to fix issue with not showing refreshing indicator before swipeRef.onMesure() was called
			//as I understand before onResume of Activity
			DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
			int height = displayMetrics.heightPixels;

			TypedValue typed_value = new TypedValue();
			getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value,
			true);
			this.swipeRef.setProgressViewOffset(false, 0,
			height - 2 * getResources().getDimensionPixelSize(typed_value.resourceId));

			this.swipeRef.setProgressViewEndTarget(false,
			height - 2 * getResources().getDimensionPixelSize(typed_value.resourceId));
		}
		this.swipeRef.setColorSchemeResources(R.color.material_red_300,
		R.color.material_red_500,
		R.color.material_red_500,
		R.color.material_red_500);

		////set on swipe listener
		this.swipeRef.setOnRefreshListener(new OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				pageToLoad = 1;
				startDownload();
			}
		});

		this.recycler = (RecyclerView) v.findViewById(R.id.recycler_view);
		this.recycler.setItemAnimator(new DefaultItemAnimator());
		this.recycler.setLayoutManager(new MyLinearLayoutManager(act));
		this.recycler.addItemDecoration(new SpacesItemDecoration(15));
		//end of find all views

		if (this.commentsInfoList != null)
		{
			//this.commentsInfoList = CommentInfo.getDefaultArtsCommentsInfo(20);
			this.recyclerAdapter = new RecyclerAdapterCommentsFragment(act, article, commentsInfoList);
			this.recycler.setAdapter(recyclerAdapter);
		}
		else
		{
			this.recyclerAdapter = new RecyclerAdapterCommentsFragment(act, article, commentsInfoList);
			this.recycler.setAdapter(recyclerAdapter);
			startDownload();
		}

		if (this.isLoading)
		{
			this.swipeRef.setRefreshing(true);
		}

		this.recycler.addOnScrollListener(new RecyclerCommentsOnScrollListener()
		{
			@Override
			public void onLoadMore()
			{
				//				if (!swipeRef.isRefreshing())
				if (!isLoading)
				{
					pageToLoad++;
					startDownload();
					Log.e(LOG, "Start loading page " + pageToLoad + " from bottom!");
				}
			}
		});

		return v;
	}

	public void startDownload()
	{
		//workaround to fix issue with not showing refreshing indicator before swipeRef.onMesure() was called
		//as I understand before onResume of Activity
		TypedValue typed_value = new TypedValue();
		getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);

		DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
		int height = displayMetrics.heightPixels;

		if (this.pageToLoad == 1)
		{
			//this.swipeRef.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
			this.swipeRef.setProgressViewEndTarget(false,
			getResources().getDimensionPixelSize(typed_value.resourceId) + 15);
		}
		else
		{
			this.swipeRef.setProgressViewOffset(false, 0,
			height - 2 * getResources().getDimensionPixelSize(typed_value.resourceId));
			this.swipeRef.setProgressViewEndTarget(false,
			height - 2 * getResources().getDimensionPixelSize(typed_value.resourceId));
		}
		this.swipeRef.setColorSchemeResources(R.color.material_red_300,
		R.color.material_red_500,
		R.color.material_red_500,
		R.color.material_red_500);

		this.swipeRef.setRefreshing(true);

		if (this.isLoading)
		{
			Log.e(LOG, "this.isLoading: " + String.valueOf(this.isLoading));
		}
		else
		{
			Intent intent = new Intent(this.act, ServiceComments.class);
			intent.putExtra(KEY_PAGE_TO_LOAD, pageToLoad);
			intent.putExtra(KEY_URL_TO_LOAD, this.article.getUrl());
			this.getActivity().startService(intent);
			this.isLoading = true;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		//System.out.println("CommentsFragment onSaveInstanceState");
		super.onSaveInstanceState(outState);
		outState.putParcelable(Article.KEY_CURENT_ART, this.article);
		outState.putParcelableArrayList(CommentInfo.KEY_ALL_COMMENTS_LIST, this.commentsInfoList);
		outState.putBoolean("isLoading", isLoading);
		outState.putInt(KEY_PAGE_TO_LOAD, pageToLoad);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		this.act.supportInvalidateOptionsMenu();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
		if (pref.getBoolean("twoPane", false))
		{
			Toolbar toolbar = (Toolbar) act.findViewById(R.id.toolbar_right);
			toolbar.setTitle("Комментарии");
		}
		else
		{
			Toolbar toolbar = (Toolbar) act.findViewById(R.id.toolbar);
			toolbar.setTitle("Комментарии");
		}
	}

	@Override
	public void onDestroy()
	{
		Log.e(LOG, "onDestroy");
		// If the DownloadStateReceiver still exists, unregister it and set it to null
		if (commentsDataReceiver != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(commentsDataReceiver);
			commentsDataReceiver = null;
		}
		// Must always call the super method at the end.
		super.onDestroy();
	}
}