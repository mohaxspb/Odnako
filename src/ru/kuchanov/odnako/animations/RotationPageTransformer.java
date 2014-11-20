/*
 27.10.2014
ZoomOutPageTransformer.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.animations;

import android.view.View;
import android.support.v4.view.ViewPager;

public class RotationPageTransformer implements ViewPager.PageTransformer
{
	@Override
	public void transformPage(View page, float position)
	{
		page.setRotationY(position * -30);
	}
}