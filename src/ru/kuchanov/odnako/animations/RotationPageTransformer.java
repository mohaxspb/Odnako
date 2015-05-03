/*
 27.10.2014
ZoomOutPageTransformer.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.animations;

import android.support.v4.view.ViewPager;
import android.view.View;

public class RotationPageTransformer implements ViewPager.PageTransformer
{
	@Override
	public void transformPage(View page, float position)
	{
		page.setRotationY(position * -30);
	}
}