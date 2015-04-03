/*
 08.11.2014
RecyclerViewOnScrollListener.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.animations;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;

public abstract class RecyclerCommentsOnScrollListener extends OnScrollListener
{
	static final String TAG = RecyclerCommentsOnScrollListener.class.getSimpleName();

	//	private ActionBarActivity act;

	private LinearLayoutManager manager;

	private boolean loading = true; // True if we are still waiting for the last set of data to load.
	private int previousTotal = 0; // The total number of items in the dataset after the last load
	// The minimum amount of items to have below your current scroll position before loading more.
	private int visibleThreshold = 5;
	private int firstVisibleItem, visibleItemCount, totalItemCount;

	public RecyclerCommentsOnScrollListener(/* ActionBarActivity act */)
	{
		//		this.act = act;
	}

	public void onScrollStateChanged(RecyclerView recyclerView, int newState)
	{
		manager = (LinearLayoutManager) recyclerView.getLayoutManager();

		switch (newState)
		{
			case (RecyclerView.SCROLL_STATE_DRAGGING):
			//System.out.println("dragging");
			break;
			//scroll finished
			case (RecyclerView.SCROLL_STATE_IDLE):
			//System.out.println("SCROLL_STATE_IDLE");
			break;
			case (RecyclerView.SCROLL_STATE_SETTLING):
			//System.out.println("SCROLL_STATE_SETTLING");
			break;
		}
	}

	@Override
	public void onScrolled(RecyclerView recyclerView, int x, int y)
	{
		manager = (LinearLayoutManager) recyclerView.getLayoutManager();

		visibleItemCount = manager.getChildCount();
		totalItemCount = manager.getItemCount();
		firstVisibleItem = manager.findFirstVisibleItemPosition();

		if (loading)
		{
			if (totalItemCount > previousTotal)
			{
				loading = false;
				previousTotal = totalItemCount;
			}
		}
		if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold))
		{
			// End has been reached
			//check if totaItemCount -2 (cause of header and titleCrd) a multiple of 20
			if ((totalItemCount - 2) % 20 == 0)
			{
				// TODO if so we can load more from bottom
				//CHECK here situation when total quont of arts on are multiple of 30
				//to prevent a lot of requests
				onLoadMore();
				loading = true;
			}
			else
			{
				//if so, we have reached onSiteVeryBottomOfArtsList
				//so we do not need to start download
			}
		}
	}

	public abstract void onLoadMore();
}
