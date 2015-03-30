/*
 31.03.2015
MyLinearLayoutManager.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.custom.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class MyLinearLayoutManager extends LinearLayoutManager
{
	public static String LOG = MyLinearLayoutManager.class.getSimpleName();

	public static String KEY_INDEX = "index";
	public static String KEY_TOP_COORD = "top coord";


	boolean firstScroll=true;
	
	public void setFirstScroll(boolean firstScroll)
	{
		this.firstScroll=firstScroll;
	}

	public MyLinearLayoutManager(Context context)
	{
		super(context);
	}

//	@Override
//	public Parcelable onSaveInstanceState()
//	{
//		outState = new Bundle();
//
//		int index = this.findFirstVisibleItemPosition();
//		View view = this.getChildAt(0);
//		int top = (view == null) ? 0 : (view.getTop() - this.getPaddingTop());
//
//		outState.putInt(KEY_INDEX, index);
//		outState.putInt(KEY_TOP_COORD, top);
//
//		return outState;
//	}
//
//	@Override
//	public void onRestoreInstanceState(Parcelable savedState)
//	{
//		this.outState = (Bundle) savedState;
//		int index = outState.getInt(KEY_INDEX, 0);
//		int top = outState.getInt(KEY_TOP_COORD, 0);
//
//		Log.e("index", "index: " + index);
//		Log.e("top", "top: " + top);
//
//		this.scrollToPositionWithOffset(index, top);
//	}


	@Override
	public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state)
	{
		//		if (this.outState != null)
		//		{
		Log.e(LOG, "scrollVerticallyBy: " + dy);
		//			return super.scrollVerticallyBy(dy, recycler, state);
		//		}

		//		Log.e(LOG, "state.getTargetScrollPosition(): " + state.getTargetScrollPosition());
		//		Log.e(LOG, "state.getTargetScrollPosition(): " + state.);
		//		return super.scrollVerticallyBy(dy, recycler, state);
		if (this.firstScroll)
		{
			this.firstScroll=false;
			return 0;
		}
		else
		{
//			this.firstScroll=true;
			return super.scrollVerticallyBy(dy, recycler, state);
		}
	}

	@Override
	public void scrollToPosition(int position)
	{
		Log.e(LOG, "Scroll to position: " + position);
		super.scrollToPosition(position);
	}
}
