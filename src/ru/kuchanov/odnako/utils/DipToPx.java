/*
 23.10.2014
DipToPx.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;

public class DipToPx
{

	public DipToPx()
	{

	}
/**
 * 
 * @param dip
 * @param act
 * @return convert given pid to px
 */
	public static float convert(int dip, ActionBarActivity act)
	{
		//convert given pid to px
		Resources r = act.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
		return px;
	}
}
