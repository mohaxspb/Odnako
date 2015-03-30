/*
 30.03.2015
DynamicImageView.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.custom.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * @see http://stackoverflow.com/a/17424313/3212712
 */
public class DynamicImageView extends ImageView
{

	static final String LOG = DynamicImageView.class.getSimpleName();

	public DynamicImageView(final Context context, final AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec)
	{
		final Drawable d = this.getDrawable();

		if (d != null)
		{
			// ceil not round - avoid thin vertical gaps along the left/right edges
			final int width = MeasureSpec.getSize(widthMeasureSpec);
			        final int height = (int) Math.ceil(width * (float) d.getIntrinsicHeight() / d.getIntrinsicWidth());
//			final int height = (int) Math.ceil(width / (1.7f));
			this.setMeasuredDimension(width, height);
		}
		else
		{
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}
