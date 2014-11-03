package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;
import ru.kuchanov.odnako.R;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpListAdapter extends BaseExpandableListAdapter
{

	private ArrayList<ArrayList<String>> mGroups;
	//	private Context mContext;
	ActionBarActivity act;
	String[] cat;
	SharedPreferences pref;

	public ExpListAdapter(ActionBarActivity act, ArrayList<ArrayList<String>> groups)
	{
		this.act = act;
		pref = PreferenceManager.getDefaultSharedPreferences(this.act);
		mGroups = groups;
		cat = this.act.getResources().getStringArray(R.array.menu_items);
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
		View view = null;
		MyHolder holderMain;
		if (convertView == null)
		{
			ViewGroup vg;
			vg = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.drawer_group, parent, false);
			holderMain = new MyHolder((ImageView) vg.findViewById(R.id.img_left),
			(TextView) vg.findViewById(R.id.text), (ImageView) vg.findViewById(R.id.img_right));
			vg.setTag(holderMain);
			view = vg;
		}
		else
		{
			view = convertView;
			holderMain = (MyHolder) convertView.getTag();
		}
		holderMain.text.setText(this.cat[groupPosition]);

		String scaleFactorString = pref.getString("scale", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);
		holderMain.text.setTextSize(21 * scaleFactor);

		//set arrowDownIcon by theme
		int[] attrs = new int[] { R.attr.arrowDownIcon };
		TypedArray ta = this.act.obtainStyledAttributes(attrs);
		Drawable drawableArrrowDown = ta.getDrawable(0);
		ta.recycle();
		//set arrowLeftIcon by theme
		attrs = new int[] { R.attr.arrowLeftIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		Drawable drawableArrrowLeft = ta.getDrawable(0);
		ta.recycle();
		//set arrowRightIcon by theme
		attrs = new int[] { R.attr.arrowRightIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		Drawable drawableArrrowRight = ta.getDrawable(0);
		ta.recycle();
		//set downloadIcon by theme
		attrs = new int[] { R.attr.downloadIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		Drawable drawableDownload = ta.getDrawable(0);
		ta.recycle();
		//set settingsIcon by theme
		attrs = new int[] { R.attr.settingsIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		Drawable drawableSettings = ta.getDrawable(0);
		ta.recycle();
		//set asListIcon by theme
		attrs = new int[] { R.attr.asListIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		Drawable drawableAsList = ta.getDrawable(0);
		ta.recycle();

		switch (groupPosition)
		{
			//authors
			case 0:
				// Create an array of the attributes we want to resolve
				// using values from a theme
				int[] attrsAutors = new int[] { R.attr.authorsIcon };
				// Obtain the styled attributes. 'themedContext' is a context with a
				// theme, typically the current Activity (i.e. 'this')
				TypedArray taAutors = this.act.obtainStyledAttributes(attrsAutors);
				// To get the value of the 'listItemBackground' attribute that was
				// set in the theme used in 'themedContext'. The parameter is the index
				// of the attribute in the 'attrs' array. The returned Drawable
				// is what you are after
				Drawable drawableAutors = taAutors.getDrawable(0 /* index */);
				// Finally, free the resources used by TypedArray
				taAutors.recycle();
				holderMain.left.setImageDrawable(drawableAutors);

				//Right img

				holderMain.right.setImageDrawable(drawableArrrowDown);

			break;
			//categories
			case 1:
				//Left img
				holderMain.left.setImageDrawable(drawableAsList);

				//Right img
				holderMain.right.setImageDrawable(drawableArrrowDown);
			break;
			case 2:
				//Left img
				holderMain.left.setImageDrawable(drawableArrrowLeft);

				//Right img
				holderMain.right.setImageDrawable(drawableArrrowRight);
			break;
			case 3:
				//Left img
				holderMain.left.setImageDrawable(drawableArrrowLeft);

				//Right img
				holderMain.right.setImageDrawable(drawableArrrowRight);
			break;
			case 4:
				//Left img
				holderMain.left.setImageDrawable(drawableDownload);

				//Right img
				holderMain.right.setImageDrawable(null);
			break;
			case 5:
				//Left img
				holderMain.left.setImageDrawable(drawableSettings);

				//Right img
				holderMain.right.setImageDrawable(null);
			break;
		}
		return view;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
	ViewGroup parent)
	{
		View view = null;
		MyHolder holderMain;
		if (convertView == null)
		{
			ViewGroup vg;
			vg = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.drawer_group, parent, false);
			holderMain = new MyHolder((ImageView) vg.findViewById(R.id.img_left),
			(TextView) vg.findViewById(R.id.text), (ImageView) vg.findViewById(R.id.img_right));
			vg.setTag(holderMain);
			view = vg;
		}
		else
		{
			view = convertView;
			holderMain = (MyHolder) convertView.getTag();
		}
		holderMain.text.setText(mGroups.get(groupPosition).get(childPosition));

		String scaleFactorString = pref.getString("scale", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);
		holderMain.text.setTextSize(21 * scaleFactor);

		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		return true;
	}

	static class MyHolder
	{

		ImageView left;
		TextView text;
		ImageView right;

		MyHolder(ImageView left, TextView text, ImageView right)
		{
			this.left = left;
			this.text = text;
			this.right = right;
		}
	}
}
