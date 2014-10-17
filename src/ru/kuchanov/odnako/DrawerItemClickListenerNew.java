package ru.kuchanov.odnako;

import java.util.ArrayList;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ExpandableListView;

public class DrawerItemClickListenerNew implements ExpandableListView.OnChildClickListener
{
	private DrawerLayout mDrawerLayout;
	private ExpandableListView mDrawerList;
	ActionBarActivity act;
	
	ArrayList<ArrayList<String>> groups;
	ArrayList<ArrayList<String>> groupsLinks;
	
	public void setVars(DrawerLayout mDrawerLayout, ExpandableListView mDrawerList, ArrayList<ArrayList<String>> groups, ArrayList<ArrayList<String>> groupsLinks, ActionBarActivity act)
	{
		this.mDrawerLayout = mDrawerLayout;
		this.mDrawerList = mDrawerList;
		this.act = act;
		this.groups=groups;
		this.groupsLinks=groupsLinks;
	}
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
	{
		mDrawerList.setItemChecked(childPosition, true);
		mDrawerLayout.closeDrawer(mDrawerList);
		Intent intentToMain = new Intent(act, MainActivityNew.class);
		String[] infoToMain = new String[2];
		infoToMain[0] = groupsLinks.get(groupPosition).get(childPosition);
		infoToMain[1] = groups.get(groupPosition).get(childPosition);
		intentToMain.putExtra(ArticleActivity.EXTRA_MESSAGE_TO_MAIN, infoToMain);
		act.startActivity(intentToMain);
		return true;
	}
}
