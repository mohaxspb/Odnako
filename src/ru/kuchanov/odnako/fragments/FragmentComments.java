/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.util.ArrayList;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.animations.SpacesItemDecoration;
import ru.kuchanov.odnako.custom.view.MyLinearLayoutManager;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.lists_and_utils.CommentInfo;
import ru.kuchanov.odnako.lists_and_utils.RecyclerAdapterCommentsFragment;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentComments extends Fragment
{
	public final static String LOG = FragmentComments.class.getSimpleName() + "/";

	private ActionBarActivity act;

	private Article article;

	private ArrayList<CommentInfo> commentsInfoList;

	private SwipeRefreshLayout swipeRef;

	private RecyclerView recycler;
	private RecyclerAdapterCommentsFragment recyclerAdapter;

	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		//System.out.println("CommentsFragment onCreate");

		this.act = (ActionBarActivity) this.getActivity();

		if (this.getArguments() != null && this.getArguments().containsKey(Article.KEY_CURENT_ART))
		{
			this.article = this.getArguments().getParcelable(Article.KEY_CURENT_ART);
		}
		if (savedState != null && savedState.containsKey(Article.KEY_CURENT_ART))
		{
			this.article = this.getArguments().getParcelable(Article.KEY_CURENT_ART);
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//Log.i(LOG, "onCreateView");
		View v = inflater.inflate(R.layout.fragment_comments_list, container, false);

		//find all views
		this.swipeRef = (SwipeRefreshLayout) v;

		TypedValue typed_value = new TypedValue();
		getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
		this.swipeRef.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));

		this.swipeRef.setProgressViewEndTarget(false, getResources().getDimensionPixelSize(typed_value.resourceId));

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
				//TODO
			}
		});

		this.recycler = (RecyclerView) v.findViewById(R.id.recycler_view);
		//end of find all views

		this.commentsInfoList = CommentInfo.getDefaultArtsCommentsInfo(20);

		this.recycler.setItemAnimator(new DefaultItemAnimator());
		this.recycler.setLayoutManager(new MyLinearLayoutManager(act));
		this.recycler.addItemDecoration(new SpacesItemDecoration(20));
		this.recyclerAdapter = new RecyclerAdapterCommentsFragment(act, article, commentsInfoList);
		this.recycler.setAdapter(recyclerAdapter);

		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		//System.out.println("CommentsFragment onViewCreated");
		super.onViewCreated(view, savedInstanceState);

	}

	@Override
	public void onAttach(Activity activity)
	{
		//System.out.println("CommentsFragment onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onDetach()
	{
		//System.out.println("CommentsFragment onDetach");
		super.onDetach();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		//System.out.println("CommentsFragment onSaveInstanceState");
		super.onSaveInstanceState(outState);
		outState.putParcelable(Article.KEY_CURENT_ART, this.article);
	}
}
