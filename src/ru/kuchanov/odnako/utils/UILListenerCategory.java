/*
 09.03.2015
ImgLoadListenerBigSmall.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import ru.kuchanov.odnako.R;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class UILListenerCategory implements ImageLoadingListener
{

	ImageLoader imageLoader;
	DisplayImageOptions options;
	final ImageView imgView;

	public UILListenerCategory(ImageLoader imageLoader, DisplayImageOptions options, ImageView imgView)
	{
		this.imageLoader = imageLoader;
		this.options = options;
		this.imgView = imgView;
	}

	@Override
	public void onLoadingStarted(String imageUri, View view)
	{

	}

	@Override
	public void onLoadingComplete(String imageUri, final View view, Bitmap loadedImage)
	{

	}

	@Override
	public void onLoadingCancelled(String imageUri, View view)
	{
		//							        ...
	}

	@Override
	public void onLoadingFailed(String imageUri, View arg1, FailReason arg2)
	{
		String newURL = "drawable://" + R.drawable.odnako;
		imageLoader.displayImage(newURL, this.imgView, options);
	}
}