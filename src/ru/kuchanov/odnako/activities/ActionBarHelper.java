/*
 04.11.2014
ActinBarHelper.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ExpandableListView;


/**
 * Action bar helper for use on ICS and newer devices.
 */
public class ActionBarHelper
{
	private ActionBarActivity act;
	
	private final ActionBar mActionBar;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	

	protected ExpandableListView mDrawer;
	protected boolean drawerOpened = false;
	

	public ActionBarHelper(ActionBarActivity act, ExpandableListView mDrawer)
	{
		this.act=act;
		this.mDrawer=mDrawer;
		mActionBar = this.act.getSupportActionBar();
	}

	public void init()
	{
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(false);
		mTitle = mDrawerTitle = this.act.getTitle();
	}

	/**
	 * When the drawer is closed we restore the action bar state reflecting
	 * the specific contents in view.
	 */
	public void onDrawerClosed()
	{
		mActionBar.setTitle(mTitle);
		drawerOpened = false;
	}

	/**
	 * When the drawer is open we set the action bar to a generic title. The
	 * action bar should only contain data relevant at the top level of the
	 * nav hierarchy represented by the drawer, as the rest of your content
	 * will be dimmed down and non-interactive.
	 */
	public void onDrawerOpened()
	{
		mActionBar.setTitle(mDrawerTitle);
		drawerOpened = true;
	}

	public void setTitle(CharSequence title)
	{
		mTitle = title;
	}
}