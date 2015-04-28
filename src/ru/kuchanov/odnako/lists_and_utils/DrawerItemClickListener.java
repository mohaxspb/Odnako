package ru.kuchanov.odnako.lists_and_utils;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.activities.ActivityMain;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;

public class DrawerItemClickListener implements ExpandableListView.OnChildClickListener
{
	private DrawerLayout mDrawerLayout;
	private ExpandableListView mDrawerList;
	AppCompatActivity act;

	ViewPager viewPager;

	public DrawerItemClickListener(DrawerLayout mDrawerLayout, ExpandableListView mDrawerList, AppCompatActivity act)
	{
		this.mDrawerLayout = mDrawerLayout;
		this.mDrawerList = mDrawerList;
		this.act = act;
		if (act instanceof ActivityMain)
		{
			this.viewPager = (ViewPager) ((ActivityMain) act).findViewById(R.id.pager_left);
		}
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
	{
		//test
		//		final long groupChildFlatPosition = ExpandableListView.getPackedPositionForChild(groupPosition, childPosition);
		//		final int groupPos1 = ExpandableListView.getPackedPositionGroup(groupChildFlatPosition);//(groupChildFlatPosition);
		//		final int childPos1 = ExpandableListView.getPackedPositionChild(groupChildFlatPosition);
		//		System.out.println("onChildClick_groupPos1: " + groupPos1 + "/ childPos1: " + childPos1);

		if (act instanceof ActivityMain)
		{
			//TODO check twopane
			ActivityMain mainActivity = (ActivityMain) this.act;

			//if pagerType isn't MENU we set it to Menu and change pagers
			if (mainActivity.getPagerType() != ActivityMain.PAGER_TYPE_MENU)
			{
				mainActivity.setPagerType(ActivityMain.PAGER_TYPE_MENU);
				mainActivity.setPagers(ActivityMain.PAGER_TYPE_MENU,
				mainActivity.getCurentPositionByGroupChildPosition(groupPosition, childPosition));
			}
			mDrawerList.setSelectedChild(groupPosition, childPosition, true);

			mainActivity.setGroupChildPosition(groupPosition, childPosition);
			((ExpListAdapter) mDrawerList.getExpandableListAdapter()).notifyDataSetChanged();

			if (groupPosition == 0)
			{
				this.viewPager.setCurrentItem(childPosition, true);
			}
			else
			{
				this.viewPager.setCurrentItem(4 + childPosition, true);
			}

			mDrawerLayout.closeDrawer(mDrawerList);
		}
		else
		{
			mDrawerList.setSelectedChild(groupPosition, childPosition, true);
			mDrawerLayout.closeDrawer(mDrawerList);
			
			Intent intent = new Intent(act, ActivityMain.class);
			int[] groupChildPosition = new int[] { groupPosition, childPosition };
			intent.putExtra(ActivityBase.KEY_GROUP_CHILD_POSITION, groupChildPosition);
			intent.putExtra(ActivityMain.KEY_PAGER_TYPE, ActivityMain.PAGER_TYPE_MENU);
			//set flags to prevent restoring activity from backStack and create really new instance
			//with given categories number
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			act.startActivity(intent);
		}

		return true;
	}
}
