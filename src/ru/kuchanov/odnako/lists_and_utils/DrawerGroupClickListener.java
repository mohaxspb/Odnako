package ru.kuchanov.odnako.lists_and_utils;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityPreference;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

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
			boolean wrapInScrollView = true;

			MaterialDialog dialogGoPro;
			MaterialDialog.Builder dialogGoProBuilder = new MaterialDialog.Builder(act);

			dialogGoProBuilder.title("Ссылки для связи")
			.positiveText("Всё понятно!")
			//			.content(Html.fromHtml(act.getResources().getString(R.string.contacts)))
			.customView(R.layout.contacts_dialog, wrapInScrollView);

			dialogGoPro = dialogGoProBuilder.build();

			TextView top = (TextView) dialogGoPro.getCustomView().findViewById(R.id.top);
			top.setLinksClickable(true);
			top.setMovementMethod(LinkMovementMethod.getInstance());
			top.setText(Html.fromHtml(act.getResources().getStringArray(R.array.contacts)[0]));

			TextView mail = (TextView) dialogGoPro.getCustomView().findViewById(R.id.mail);
			mail.setText(Html.fromHtml(act.getResources().getStringArray(R.array.contacts)[1]));

			TextView bottom = (TextView) dialogGoPro.getCustomView().findViewById(R.id.bottom);
			bottom.setLinksClickable(true);
			bottom.setMovementMethod(LinkMovementMethod.getInstance());
			bottom.setText(Html.fromHtml(act.getResources().getStringArray(R.array.contacts)[2]));

			dialogGoPro.show();
		}
		else if (groupPosition == 3)
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