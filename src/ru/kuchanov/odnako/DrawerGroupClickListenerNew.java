package ru.kuchanov.odnako;

import ru.kuchanov.odnako.download.Downloadings;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ExpandableListView;

public class DrawerGroupClickListenerNew implements ExpandableListView.OnGroupClickListener
{
	private DrawerLayout mDrawerLayout;
	private ExpandableListView mDrawerList;
	ActionBarActivity act;

	public void setVars(DrawerLayout mDrawerLayout, ExpandableListView mDrawerList, ActionBarActivity act)
	{
		this.mDrawerLayout = mDrawerLayout;
		this.mDrawerList = mDrawerList;
		this.act = act;
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
	{
		if (groupPosition == 2)
		{
			mDrawerList.setItemChecked(groupPosition, true);
			mDrawerLayout.closeDrawer(mDrawerList);
			Intent intentToMain = new Intent(act, MainActivityNew.class);
			String[] infoToMain = new String[2];
			infoToMain[0] = "www.odnako.org/blogs";
			infoToMain[1] = "Лента обновлений";
			intentToMain.putExtra(ArticleActivity.EXTRA_MESSAGE_TO_MAIN, infoToMain);
			act.startActivity(intentToMain);
		}
		else if (groupPosition == 3)
		{
			mDrawerList.setItemChecked(groupPosition, true);
			mDrawerLayout.closeDrawer(mDrawerList);
			Intent intentToMain = new Intent(act, MainActivityNew.class);
			String[] infoToMain = new String[2];
			infoToMain[0] = "novosti.odnako.org";
			infoToMain[1] = "Новости";
			intentToMain.putExtra(ArticleActivity.EXTRA_MESSAGE_TO_MAIN, infoToMain);
			act.startActivity(intentToMain);
		}
		else if (groupPosition == 4)
		{
			mDrawerList.setItemChecked(groupPosition, true);
			mDrawerLayout.closeDrawer(mDrawerList);
			Intent intentToMain = new Intent(act, Downloadings.class);
			act.startActivity(intentToMain);
		}
		else if (groupPosition == 5)
		{
			mDrawerList.setItemChecked(groupPosition, true);
			mDrawerLayout.closeDrawer(mDrawerList);
			Intent intentToMain = new Intent(act, PrefActivity.class);
			act.startActivity(intentToMain);
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
