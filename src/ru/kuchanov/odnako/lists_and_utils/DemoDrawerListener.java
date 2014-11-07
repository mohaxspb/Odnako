/*
 04.11.2014
DemoDrawerListener.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;

/**
 * A drawer listener can be used to respond to drawer events such as becoming
 * fully opened or closed. You should always prefer to perform expensive
 * operations such as drastic relayout when no animation is currently in
 * progress, either before or after the drawer animates.
 * 
 * When using ActionBarDrawerToggle, all DrawerLayout listener methods should be
 * forwarded if the ActionBarDrawerToggle is not used as the DrawerLayout
 * listener directly.
 */
public class DemoDrawerListener implements DrawerLayout.DrawerListener
{

//	protected ActionBarHelper mActionBar;
//	protected ActionBarDrawerToggle mDrawerToggle;
//
//	public DemoDrawerListener(ActionBarHelper mActionBar, ActionBarDrawerToggle mDrawerToggle)
//	{
//		this.mActionBar = mActionBar;
//		this.mDrawerToggle = mDrawerToggle;
//	}
//
//	@Override
//	public void onDrawerOpened(View drawerView)
//	{
//		mDrawerToggle.onDrawerOpened(drawerView);
//		mActionBar.onDrawerOpened();
//	}
//
//	@Override
//	public void onDrawerClosed(View drawerView)
//	{
//		mDrawerToggle.onDrawerClosed(drawerView);
//		mActionBar.onDrawerClosed();
//	}
//
//	@Override
//	public void onDrawerSlide(View drawerView, float slideOffset)
//	{
//		mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
//	}
//
//	@Override
//	public void onDrawerStateChanged(int newState)
//	{
//		mDrawerToggle.onDrawerStateChanged(newState);
//	}
	
	/////////////test
	protected ActionBarDrawerToggle mDrawerToggle;

	public DemoDrawerListener(ActionBarDrawerToggle mDrawerToggle)
	{
		this.mDrawerToggle = mDrawerToggle;
	}

	@Override
	public void onDrawerOpened(View drawerView)
	{
		mDrawerToggle.onDrawerOpened(drawerView);
	}

	@Override
	public void onDrawerClosed(View drawerView)
	{
		mDrawerToggle.onDrawerClosed(drawerView);
	}

	@Override
	public void onDrawerSlide(View drawerView, float slideOffset)
	{
		mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
	}

	@Override
	public void onDrawerStateChanged(int newState)
	{
		mDrawerToggle.onDrawerStateChanged(newState);
	}
}