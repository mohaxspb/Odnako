/*
 09.03.2015
ImgLoadListenerBigSmall.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImgLoadListenerBigSmall implements ImageLoadingListener
{

	ImageLoader imageLoader;
	DisplayImageOptions options;
	ImageView imgView;

	/**
	 * 
	 */
	public ImgLoadListenerBigSmall(ImageLoader imageLoader, DisplayImageOptions options, ImageView imgView)
	{
		this.imageLoader = imageLoader;
		this.options = options;
		this.imgView = imgView;
	}

	@Override
	public void onLoadingStarted(String imageUri, View view)
	{
		//							        ...
	}

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
	{
		int width = imgView.getMeasuredWidth();
		int newHeight = (int) (width / (1.7f));
		LayoutParams params = (LayoutParams) imgView.getLayoutParams();
		params.height = newHeight;
		imgView.setLayoutParams(params);
		Log.e(imageUri, "width: " + width);
		Log.e(imageUri, "newHeight: " + newHeight);
		//							        ...
	}

	@Override
	public void onLoadingCancelled(String imageUri, View view)
	{
		//							        ...
	}

	@Override
	public void onLoadingFailed(String imageUri, View arg1, FailReason arg2)
	{
		String newURL = imageUri.replace("/450_240/", "/120_72/");
		imageLoader.displayImage(newURL, this.imgView,
		options);
	}

}
