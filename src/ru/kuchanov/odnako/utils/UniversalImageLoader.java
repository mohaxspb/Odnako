/*
 26.10.2014
ImageLoader.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import java.io.File;

import ru.kuchanov.odnako.R;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.L;

public class UniversalImageLoader
{

	public static ImageLoader get(ActionBarActivity act)
	{
		//UniversalImageLoader
		File cacheDir = new File(Environment.getExternalStorageDirectory(), "Odnako/Cache");

		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.displayer(new RoundedBitmapDisplayer(10))
		.showImageOnLoading(R.drawable.ic_autorenew_grey600_48dp)
		.showImageForEmptyUri(R.drawable.ic_crop_original_grey600_48dp)
		.showImageOnFail(R.drawable.ic_crop_original_grey600_48dp)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();

		L.writeLogs(false);

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(act)
		.diskCache(new UnlimitedDiscCache(cacheDir))
		.defaultDisplayImageOptions(options)
		.build();

		ImageLoader imageLoader = ImageLoader.getInstance();

		if (!imageLoader.isInited())
		{
			imageLoader.init(config);
		}

		return imageLoader;

	}

	public static DisplayImageOptions getDarkOptions()
	{
		return new DisplayImageOptions.Builder()

		.displayer(new RoundedBitmapDisplayer(10))
		.showImageOnLoading(R.drawable.ic_autorenew_white_48dp)
		.showImageForEmptyUri(R.drawable.ic_crop_original_white_48dp)
		.showImageOnFail(R.drawable.ic_crop_original_white_48dp)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}

	public static DisplayImageOptions getTransparentBackgroundOptions()
	{
		return new DisplayImageOptions.Builder()

		.displayer(new RoundedBitmapDisplayer(10))
		.showImageOnLoading(R.drawable.top_img_cover_grey_dark)
		.showImageForEmptyUri(R.drawable.ic_crop_original_white_48dp)
		.showImageOnFail(R.drawable.ic_crop_original_white_48dp)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}
	
	public static DisplayImageOptions getTransparentBackgroundROUNDOptions(ActionBarActivity act)
	{
		return new DisplayImageOptions.Builder()

		.displayer(new RoundedBitmapDisplayer((int) DipToPx.convert(35, act)))
		.showImageOnLoading(R.drawable.top_img_cover_grey_dark)
		.showImageForEmptyUri(R.drawable.ic_crop_original_white_48dp)
		.showImageOnFail(R.drawable.ic_crop_original_white_48dp)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}
}
