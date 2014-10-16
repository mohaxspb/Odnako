package ru.kuchanov.odnako;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpListAdapter extends BaseExpandableListAdapter
{

	private ArrayList<ArrayList<String>> mGroups;
	private Context mContext;
	String[] cat;
	SharedPreferences pref;

	public ExpListAdapter(Context context, ArrayList<ArrayList<String>> groups)
	{
		mContext = context;
		mGroups = groups;
		cat = this.mContext.getResources().getStringArray(R.array.menu_items);
	}

	@Override
	public int getGroupCount()
	{
		return mGroups.size();
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		return mGroups.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition)
	{
		return mGroups.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition)
	{
		return mGroups.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return childPosition;
	}

	@Override
	public boolean hasStableIds()
	{
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
	{

		if (convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			//Задаёт дефолт настройки из xml и вешает листенер их изменения
			PreferenceManager.setDefaultValues(this.mContext, R.xml.pref, false);
			pref = PreferenceManager.getDefaultSharedPreferences(this.mContext);

			if (pref.getString("theme", "dark").equals("dark"))
			{
				convertView = inflater.inflate(R.layout.nav_draw_group_dark, null);
			}
			else
			{
				convertView = inflater.inflate(R.layout.nav_draw_group_ligth, null);
			}
		}
		TextView textGroup = (TextView) convertView.findViewById(R.id.textGroup);
		textGroup.setText(this.cat[groupPosition]);

		String scaleFactorString = pref.getString("scale", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);
		textGroup.setTextSize(21 * scaleFactor);

		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
	{
		//if(groupPosition==0 && childPosition==4)
		if (convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			//Задаёт дефолт настройки из xml и вешает листенер их изменения
			PreferenceManager.setDefaultValues(this.mContext, R.xml.pref, false);
			pref = PreferenceManager.getDefaultSharedPreferences(this.mContext);

			if (pref.getString("theme", "dark").equals("dark"))
			{
				convertView = inflater.inflate(R.layout.nav_draw_child_dark, null);
			}
			else
			{
				convertView = inflater.inflate(R.layout.nav_draw_child_ligth, null);
			}

		}

		TextView textChild = (TextView) convertView.findViewById(R.id.textChild);
		textChild.setText(mGroups.get(groupPosition).get(childPosition));

		String scaleFactorString = pref.getString("scale", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);
		textChild.setTextSize(21 * scaleFactor);

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		return true;
	}
}
