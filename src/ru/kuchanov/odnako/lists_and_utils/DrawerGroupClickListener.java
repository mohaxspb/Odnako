package ru.kuchanov.odnako.lists_and_utils;

import ru.kuchanov.odnako.activities.ActivityPreference;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;

public class DrawerGroupClickListener implements ExpandableListView.OnGroupClickListener
{
	private DrawerLayout mDrawerLayout;
	private ExpandableListView mDrawerList;
	AppCompatActivity act;

	public DrawerGroupClickListener(DrawerLayout mDrawerLayout, ExpandableListView mDrawerList, AppCompatActivity act)
	{
		this.mDrawerLayout = mDrawerLayout;
		this.mDrawerList = mDrawerList;
		this.act = act;
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
	{
		//test
		//		final long packedPosition = parent.getExpandableListPosition(groupPosition);
		//		final int groupPosition1 = ExpandableListView.getPackedPositionGroup(packedPosition);
		//		System.out.println("onGroupClick_groupPosition1: " + groupPosition1);
		//settings
		if (groupPosition == 2)
		{
			mDrawerLayout.closeDrawer(mDrawerList);
			Intent intent = new Intent(act, ActivityPreference.class);
			act.startActivity(intent);
		}
		else
		{
			if (parent.isGroupExpanded(groupPosition))
			{
				parent.collapseGroup(groupPosition);
			}
			else
			{
				parent.expandGroup(groupPosition);
			}
		}
		return true;
	}
}