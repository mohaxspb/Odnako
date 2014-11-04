package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.activities.ActivityBase;
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

	//	ArrayList<ArrayList<String>> groups;
	//	ArrayList<ArrayList<String>> groupsLinks;

	public DrawerItemClickListener(DrawerLayout mDrawerLayout, ExpandableListView mDrawerList, ActionBarActivity act)
	{
		this.mDrawerLayout = mDrawerLayout;
		this.mDrawerList = mDrawerList;
		this.act = act;
		//		this.groups = groups;
		//		this.groupsLinks = groupsLinks;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
	{
		if (this.act instanceof ActivityMain)
		{

			mDrawerList.setSelectedChild(groupPosition, childPosition, true);
			
			//write group &child positions to activity
			Bundle b=((ActivityBase)this.act).getActivityBundleToWriteSomething();
			b.putInt("groupPosition", groupPosition);
			b.putInt("childPosition", childPosition);
			
			mDrawerLayout.closeDrawer(mDrawerList);
			// TODO
		}
		else
		{
			mDrawerList.setItemChecked(childPosition, true);
			mDrawerLayout.closeDrawer(mDrawerList);
			// TODO
			Intent intentToMain = new Intent(act, ActivityMain.class);
			act.startActivity(intentToMain);
		}

		return true;
	}
}
