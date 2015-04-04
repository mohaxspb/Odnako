/*
 26.10.2014
ImageLoader.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import java.io.File;

import ru.kuchanov.odnako.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.L;

/**
 * Helper for UniversalImageLoader library
 * <a href="https://github.com/nostra13/Android-Universal-Image-Loader">Link to GitHub</a>
 */
public class MyUIL
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

		//switch to true if you want logging
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
//		.considerExifParams(true)
//		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}

	public static DisplayImageOptions getLightOptions()
	{
		return new DisplayImageOptions.Builder()
		.displayer(new RoundedBitmapDisplayer(10))
		.showImageOnLoading(R.drawable.ic_autorenew_grey600_48dp)
		.showImageForEmptyUri(R.drawable.ic_crop_original_grey600_48dp)
		.showImageOnFail(R.drawable.ic_crop_original_grey600_48dp)
		.cacheInMemory(true)
		.cacheOnDisk(true)
//		.considerExifParams(true)
//		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}

	public static DisplayImageOptions getTransparentBackgroundOptions(Context ctx)
	{
		int imageOnLoading;
		int imageForEmptyUri;
		int imageOnFail;

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		boolean nightModeIsOn = pref.getBoolean("night_mode", false);
		if (nightModeIsOn)
		{
			imageOnLoading = R.drawable.ic_autorenew_white_48dp;
			imageForEmptyUri = R.drawable.ic_crop_original_white_48dp;
			imageOnFail = R.drawable.ic_crop_original_white_48dp;
		}
		else
		{
			imageOnLoading = R.drawable.ic_autorenew_grey600_48dp;
			imageForEmptyUri = R.drawable.ic_crop_original_grey600_48dp;
			imageOnFail = R.drawable.ic_crop_original_grey600_48dp;
		}

		return new DisplayImageOptions.Builder()
		.displayer(new RoundedBitmapDisplayer(10))
		.showImageOnLoading(imageOnLoading)
		.showImageForEmptyUri(imageForEmptyUri)
		.showImageOnFail(imageOnFail)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}

	public static DisplayImageOptions getTransparentBackgroundROUNDOptions(ActionBarActivity act)
	{
		int imageOnLoading;
		int imageForEmptyUri;
		int imageOnFail;

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
		boolean nightModeIsOn = pref.getBoolean("night_mode", false);
		if (nightModeIsOn)
		{
			imageOnLoading = R.drawable.ic_autorenew_white_48dp;
			imageForEmptyUri = R.drawable.ic_crop_original_white_48dp;
			imageOnFail = R.drawable.ic_crop_original_white_48dp;
		}
		else
		{
			imageOnLoading = R.drawable.ic_autorenew_grey600_48dp;
			imageForEmptyUri = R.drawable.ic_crop_original_grey600_48dp;
			imageOnFail = R.drawable.ic_crop_original_grey600_48dp;
		}

		return new DisplayImageOptions.Builder()
		.displayer(new RoundedBitmapDisplayer((int) DipToPx.convert(35, act)))
		.showImageOnLoading(imageOnLoading)
		.showImageForEmptyUri(imageForEmptyUri)
		.showImageOnFail(imageOnFail)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}
}
