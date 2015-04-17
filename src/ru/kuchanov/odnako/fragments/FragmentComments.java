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
import ru.kuchanov.odnako.download.CommentInfo;
import ru.kuchanov.odnako.download.DownloadCommentsLoader;
import ru.kuchanov.odnako.lists_and_utils.RecyclerAdapterCommentsFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
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

public class FragmentComments extends Fragment implements LoaderCallbacks<ArrayList<CommentInfo>>
{
	public final static String LOG = FragmentComments.class.getSimpleName() + "/";

	private static final int LOADER_COMMENTS_ID = 1;

	private ActionBarActivity act;

	private Article article;

	private ArrayList<CommentInfo> commentsInfoList;

	public final static String KEY_URL_TO_LOAD = "categoryToLoad";
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

		this.act = (ActionBarActivity) this.getActivity();

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

		Bundle b = new Bundle();
		b.putInt("pageToLoad", pageToLoad);
		b.putString(KEY_URL_TO_LOAD, this.article.getUrl());
		getLoaderManager().initLoader(LOADER_COMMENTS_ID, b, this);
	}

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
			startDownload();
		}

		if (this.isLoading)
		{
			this.swipeRef.setRefreshing(true);
		}

		this.recycler.setOnScrollListener(new RecyclerCommentsOnScrollListener()
		{
			@Override
			public void onLoadMore()
			{
				if (!swipeRef.isRefreshing())
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
			Loader<ArrayList<CommentInfo>> loader;
			Bundle b = new Bundle();
			b.putInt(KEY_PAGE_TO_LOAD, pageToLoad);
			b.putString(KEY_URL_TO_LOAD, this.article.getUrl());
			loader = getLoaderManager().restartLoader(LOADER_COMMENTS_ID, b, this);
			loader.forceLoad();
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
	public Loader<ArrayList<CommentInfo>> onCreateLoader(int id, Bundle b)
	{
		Loader<ArrayList<CommentInfo>> loader = null;
		if (id == LOADER_COMMENTS_ID)
		{
			loader = new DownloadCommentsLoader(this.act, b);
			Log.d(LOG, "onCreateLoader: " + loader.hashCode());
		}
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<CommentInfo>> loader, ArrayList<CommentInfo> downloadedComments)
	{
		Log.d(LOG, "onLoadFinished: " + loader.hashCode());

		if (downloadedComments != null)
		{
			if (((DownloadCommentsLoader) loader).pageToLoad == 1)
			{
				this.commentsInfoList = downloadedComments;
				this.recyclerAdapter = new RecyclerAdapterCommentsFragment(act, article, commentsInfoList);
				this.recycler.setAdapter(recyclerAdapter);
			}
			else
			{
				this.commentsInfoList.addAll(downloadedComments);
				((RecyclerAdapterCommentsFragment) this.recycler.getAdapter()).addCommentsInfo(commentsInfoList);
				this.recycler.getAdapter().notifyItemRangeInserted(
				this.recycler.getAdapter().getItemCount() - downloadedComments.size(), downloadedComments.size());
			}
		}
		else
		{
			Toast.makeText(act, Const.Error.CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
			if (this.pageToLoad != 1)
			{
				this.pageToLoad--;
			}
		}

		this.swipeRef.setRefreshing(false);
		this.isLoading = false;

		//workaround to fix issue with not showing refreshing indicator before swipeRef.onMesure() was called
		//as I understand before onResume of Activity
		TypedValue typed_value = new TypedValue();
		getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);

		//this.swipeRef.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
		this.swipeRef
		.setProgressViewEndTarget(false, getResources().getDimensionPixelSize(typed_value.resourceId) + 15);
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<CommentInfo>> arg0)
	{

	}
}