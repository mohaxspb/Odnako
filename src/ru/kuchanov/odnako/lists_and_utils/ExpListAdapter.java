package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;
import java.util.Arrays;

import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityArticle;
import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.activities.ActivityComments;
import ru.kuchanov.odnako.activities.ActivityDownloads;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.db.DataBaseHelper;
import ru.kuchanov.odnako.utils.DipToPx;
import ru.kuchanov.odnako.utils.MyUIL;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ExpListAdapter extends BaseExpandableListAdapter
{

	private ArrayList<ArrayList<String>> mGroups;
	ActionBarActivity act;
	String[] cat;
	SharedPreferences pref;
	private Drawable drawableArrrowDown;
	private Drawable drawableArrowUp;
	Drawable drawableArrrowLeft;
	Drawable drawableArrrowRight;
	private Drawable drawableDownload;
	private Drawable drawableSettings;
	Drawable drawableAsList;
	private Drawable drawableSubject;
	private Drawable drawableCategoriesMore;
	Drawable drawableAuthor;
	private Drawable drawableAuthorsMore;
	

	public ExpListAdapter(ActionBarActivity act, ArrayList<ArrayList<String>> groups)
	{
		this.act = act;
		pref = PreferenceManager.getDefaultSharedPreferences(this.act);
		mGroups = groups;
		cat = this.act.getResources().getStringArray(R.array.menu_items);

		this.setThemeDependedDrawables();

	}

	private void setThemeDependedDrawables()
	{
		//set arrowDownIcon by theme
		int[] attrs = new int[] { R.attr.arrowDownIcon };
		TypedArray ta = this.act.obtainStyledAttributes(attrs);
		drawableArrrowDown = ta.getDrawable(0);
		ta.recycle();
		//set arrowDownIcon by theme
		attrs = new int[] { R.attr.arrowUpIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableArrowUp = ta.getDrawable(0);
		ta.recycle();
		//set arrowLeftIcon by theme
		attrs = new int[] { R.attr.arrowLeftIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableArrrowLeft = ta.getDrawable(0);
		ta.recycle();
		//set arrowRightIcon by theme
		attrs = new int[] { R.attr.arrowRightIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableArrrowRight = ta.getDrawable(0);
		ta.recycle();
		//set downloadIcon by theme
		attrs = new int[] { R.attr.downloadIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableDownload = ta.getDrawable(0);
		ta.recycle();
		//set settingsIcon by theme
		attrs = new int[] { R.attr.settingsIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableSettings = ta.getDrawable(0);
		ta.recycle();
		//set asListIcon by theme
		attrs = new int[] { R.attr.asListIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableAsList = ta.getDrawable(0);
		ta.recycle();
		//set subjectIcon by theme
		attrs = new int[] { R.attr.subjectIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableSubject = ta.getDrawable(0);
		ta.recycle();
		//set categoriesMoreIcon by theme
		attrs = new int[] { R.attr.categoriesMoreIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableCategoriesMore = ta.getDrawable(0);
		ta.recycle();
		//set personIcon by theme
		attrs = new int[] { R.attr.authorIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableAuthor = ta.getDrawable(0);
		ta.recycle();
		//set authorsMoreIcon by theme
		attrs = new int[] { R.attr.authorsMoreIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableAuthorsMore = ta.getDrawable(0);
		ta.recycle();
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
		//test changing background of whole listView
		Resources.Theme themes1 = act.getTheme();
		TypedValue storedValueInTheme1 = new TypedValue();
		if (themes1.resolveAttribute(R.attr.colorPrimary, storedValueInTheme1, true))
		{
			parent.setBackgroundColor(storedValueInTheme1.data);
		}
		
		///light checked item
		if (this.act instanceof ActivityDownloads && groupPosition == 2)
		{
			Resources.Theme themes = act.getTheme();
			TypedValue storedValueInTheme = new TypedValue();
			if (themes.resolveAttribute(R.attr.selectorColor, storedValueInTheme, true))
			{
				view.setBackgroundColor(storedValueInTheme.data);
			}
		}
		else
		{
			Resources.Theme themes = act.getTheme();
			TypedValue storedValueInTheme = new TypedValue();
			if (themes.resolveAttribute(R.attr.colorPrimary, storedValueInTheme, true))
			{
				view.setBackgroundColor(storedValueInTheme.data);
			}
		}
		///////

		holderMain.text.setText(this.cat[groupPosition]);

		String scaleFactorString = pref.getString("scale", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);
		holderMain.text.setTextSize(21 * scaleFactor);

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
				if (isExpanded)
				{
					holderMain.right.setImageDrawable(drawableArrowUp);
				}
				else
				{
					holderMain.right.setImageDrawable(drawableArrrowDown);
				}

			break;
			//categories
			case 1:
				//Left img
				holderMain.left.setImageDrawable(drawableSubject);

				//Right img
				if (isExpanded)
				{
					holderMain.right.setImageDrawable(drawableArrowUp);
				}
				else
				{
					holderMain.right.setImageDrawable(drawableArrrowDown);
				}
			break;
			//downloads
			case 2:
				//Left img
				holderMain.left.setImageDrawable(drawableDownload);

				//Right img
				holderMain.right.setImageDrawable(null);
			break;
			//settings
			case 3:
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
		//		light selected
		if (act instanceof ActivityMain || act instanceof ActivityArticle || act instanceof ActivityComments)
		{
			int[] curGrChPos = new int[] { groupPosition, childPosition };
			int[] activityGrChPos = ((ActivityBase) this.act).getGroupChildPosition();
			if (Arrays.equals(curGrChPos, activityGrChPos))
			{
				Resources.Theme themes = act.getTheme();
				TypedValue storedValueInTheme = new TypedValue();
				if (themes.resolveAttribute(R.attr.selectorColor, storedValueInTheme, true))
				{
					view.setBackgroundColor(storedValueInTheme.data);
				}
			}
			else
			{
				Resources.Theme themes = act.getTheme();
				TypedValue storedValueInTheme = new TypedValue();
				if (themes.resolveAttribute(R.attr.colorPrimary, storedValueInTheme, true))
				{
					view.setBackgroundColor(storedValueInTheme.data);
				}
//				view.setBackgroundColor(Color.TRANSPARENT);
			}
		}
		else
		{
			Resources.Theme themes = act.getTheme();
			TypedValue storedValueInTheme = new TypedValue();
			if (themes.resolveAttribute(R.attr.colorPrimary, storedValueInTheme, true))
			{
				view.setBackgroundColor(storedValueInTheme.data);
			}
//			view.setBackgroundColor(Color.TRANSPARENT);
		}

		//test
		//		ExpandableListView ELV = (ExpandableListView) act.findViewById(R.id.start_drawer);
		//		final long groupChildPositionSelected = /* ((ExpandableListView)parent) */ELV.getSelectedPosition();
		//		switch (ExpandableListView.getPackedPositionType(groupChildPositionSelected))
		//		{
		//			case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
		//				System.out.println("position type: child");
		//			break;
		//
		//			case ExpandableListView.PACKED_POSITION_TYPE_GROUP:
		//				System.out.println("position type: group");
		//			break;
		//
		//			case ExpandableListView.PACKED_POSITION_TYPE_NULL:
		//				System.out.println("position type: null");
		//			break;
		//		}
		//		final int groupPos1 = ExpandableListView.getPackedPositionGroup(groupChildPositionSelected);//(groupChildFlatPosition);
		//		final int childPos1 = ExpandableListView.getPackedPositionChild(groupChildPositionSelected);
		//		System.out.println("FROM_ADAPTER_groupPos1: " + groupPos1 + "/ childPos1: " + childPos1);

		/////////
		//text and it's size
		String drawerItemTitle=mGroups.get(groupPosition).get(childPosition);
		holderMain.text.setText(drawerItemTitle);
		String scaleFactorString = pref.getString("scale", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);
		holderMain.text.setTextSize(21 * scaleFactor);

		//left and right imgs
//		holderMain.right.setImageDrawable(drawableArrrowRight);
		holderMain.right.setImageDrawable(null);
		switch (groupPosition)
		{
			case (0):
				if (isLastChild)
				{
					holderMain.left.setImageDrawable(drawableAuthorsMore);
				}
				else
				{
//					holderMain.left.setImageDrawable(drawableAuthor);
					//get and display author's avatar
					ImageLoader imageLoader = MyUIL.get(act);
					DataBaseHelper helper=new DataBaseHelper(act);
					String imageUri=Author.getAvatarUrlByName(helper, drawerItemTitle);
					imageLoader.displayImage(imageUri, holderMain.left, MyUIL.getTransparentBackgroundROUNDOptions(act));
				}

			break;
			case (1):
				if (isLastChild)
				{
					holderMain.left.setImageDrawable(drawableCategoriesMore);
				}
				else
				{
//					holderMain.left.setImageDrawable(drawableAsList);
					holderMain.left.setImageDrawable(null);
				}
			break;
		}
		LinearLayout.LayoutParams lp = (LayoutParams) holderMain.left.getLayoutParams();
		lp.setMargins((int) DipToPx.convert(30, act), 0, 0, 0);
		holderMain.left.setLayoutParams(lp);

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
