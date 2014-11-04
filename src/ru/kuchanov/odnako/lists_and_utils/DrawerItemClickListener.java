package ru.kuchanov.odnako.lists_and_utils;

import ru.kuchanov.odnako.activities.ActivityMain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ExpandableListView;

public class DrawerItemClickListener implements ExpandableListView.OnChildClickListener
{
	private DrawerLayout mDrawerLayout;
	private ExpandableListView mDrawerList;
	ActionBarActivity act;

	public DrawerItemClickListener(DrawerLayout mDrawerLayout, ExpandableListView mDrawerList, ActionBarActivity act)
	{
		this.mDrawerLayout = mDrawerLayout;
		this.mDrawerList = mDrawerList;
		this.act = act;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
	{
		//test
		final long groupChildFlatPosition=ExpandableListView.getPackedPositionForChild(groupPosition, childPosition);
		final int groupPos1 = ExpandableListView.getPackedPositionGroup(groupChildFlatPosition);//(groupChildFlatPosition);
		final int childPos1 = ExpandableListView.getPackedPositionChild(groupChildFlatPosition);
		System.out.println("onChildClick_groupPos1: " + groupPos1 + "/ childPos1: "+childPos1);

		if (act instanceof ActivityMain)
		{

			mDrawerList.setSelectedChild(groupPosition, childPosition, true);

			((ActivityMain) this.act).setGroupChildPosition(groupPosition, childPosition);

			((ExpListAdapter) mDrawerList.getExpandableListAdapter()).notifyDataSetChanged();

			mDrawerLayout.closeDrawer(mDrawerList);
			// TODO
		}
		else
		{
			mDrawerList.setSelectedChild(groupPosition, childPosition, true);
			mDrawerLayout.closeDrawer(mDrawerList);
			// TODO
			Intent intent = new Intent(act, ActivityMain.class);
			Bundle b = new Bundle();
			int[] groupChildPosition=new int[]{groupPosition, childPosition};
			b.putIntArray("groupChildPosition", groupChildPosition);
			intent.putExtras(b);
			act.startActivity(intent);
		}

		return true;
	}
}
