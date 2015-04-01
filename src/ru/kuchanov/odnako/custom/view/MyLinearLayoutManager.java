/*
 31.03.2015
MyLinearLayoutManager.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.custom.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MyLinearLayoutManager extends LinearLayoutManager
{
	public static String LOG = MyLinearLayoutManager.class.getSimpleName();

	boolean firstScroll = true;

	public void setFirstScroll(boolean firstScroll)
	{
		this.firstScroll = firstScroll;
	}

	public MyLinearLayoutManager(Context context)
	{
		super(context);
	}

	@Override
	public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state)
	{
		//Log.e(LOG, "scrollVerticallyBy: " + dy);
		if (this.firstScroll)
		{
			this.firstScroll = false;
			return 0;
		}
		else
		{
			return super.scrollVerticallyBy(dy, recycler, state);
		}
	}
}
