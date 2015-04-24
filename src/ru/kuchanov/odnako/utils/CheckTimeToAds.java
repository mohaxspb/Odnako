/*
 24.04.2015
CheckTimeToAds.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Locale;

import ru.kuchanov.odnako.R;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

public class CheckTimeToAds
{
	final static String LOG = CheckTimeToAds.class.getSimpleName() + "/";
	private final static String PREF_IN_APP_PERIOD = "inAppPeriod";
	private final static String PREF_MAX_IN_APP_PERIOD = "maxInAppPeriod";
	public final static String PREF_NEED_TO_SHOW_ADS = "needToShowAds";

	Context ctx;
	SharedPreferences pref;
	long timeOnResume;

	InterstitialAd mInterstitialAd;

	UncaughtExceptionHandler deafultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

	public CheckTimeToAds(Context ctx, InterstitialAd mInterstitialAd)
	{
		this.ctx = ctx;
		pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		this.mInterstitialAd = mInterstitialAd;
		this.init();
	}

	public void requestNewInterstitial()
	{
		Log.e(LOG, "requestNewInterstitial");
		//get EMULATOR deviceID
		String android_id = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
		String deviceId = DeviceID.md5(android_id).toUpperCase(Locale.ENGLISH);

		AdRequest adRequest = new AdRequest.Builder()
		.addTestDevice(deviceId)
		.build();

		mInterstitialAd.loadAd(adRequest);
	}

	public static long getMaxInAppPeriod(Context ctx)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		return pref.getLong(PREF_MAX_IN_APP_PERIOD, (90L * 60L * 1000L));
	}

	public static void setMaxInAppPeriod(Context ctx, long period)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		pref.edit().putLong(PREF_MAX_IN_APP_PERIOD, period).commit();
	}

	public static boolean isTimeToShowAds(Context ctx)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		return pref.getBoolean(PREF_NEED_TO_SHOW_ADS, false);
	}

	public static void adsShown(Context ctx)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		pref.edit().putBoolean(PREF_NEED_TO_SHOW_ADS, false).commit();
	}

	public void onResume()
	{
		timeOnResume = System.currentTimeMillis();
		if (CheckTimeToAds.isTimeToShowAds(ctx))
		{
			this.requestNewInterstitial();
		}

		this.setUncaughtExceptionHandler(new UncaughtExceptionHandler()
		{
			public Thread.UncaughtExceptionHandler oldHandler = deafultUncaughtExceptionHandler;//Thread.getDefaultUncaughtExceptionHandler();

			@Override
			public void uncaughtException(Thread thread, Throwable ex)
			{
				Log.e(LOG, "Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()");
				onPause();
				if (oldHandler != null)
				{
					oldHandler.uncaughtException(thread, ex);
				}
			}
		});
	}

	public void onPause()
	{
		long timeOnPause = System.currentTimeMillis();
		long inAppPeriod = timeOnPause - this.timeOnResume;

		long alreadyStoredInAppPeriod = this.pref.getLong(PREF_IN_APP_PERIOD, 0L);

		//check if new inAppPeriod < max
		if (inAppPeriod + alreadyStoredInAppPeriod < getMaxInAppPeriod(this.ctx))
		{
			inAppPeriod += alreadyStoredInAppPeriod;
			this.pref.edit().putLong(PREF_IN_APP_PERIOD, inAppPeriod).commit();
			Log.e(LOG, "onPause, less then max");

			Log.e(LOG, "onPause, inAppPeriod: " + inAppPeriod);
			Log.e(LOG, "onPause, getMaxInAppPeriod(this.ctx): " + getMaxInAppPeriod(this.ctx));
		}
		else
		{
			inAppPeriod = 0;//inAppPeriod + alreadyStoredInAppPeriod - getMaxInAppPeriod(this.ctx);
			this.pref.edit().putBoolean(PREF_NEED_TO_SHOW_ADS, true).commit();
			this.pref.edit().putLong(PREF_IN_APP_PERIOD, inAppPeriod).commit();
			Log.e(LOG, "onPause, MORE then max");
			Log.e(LOG, "onPause, inAppPeriod: " + inAppPeriod);
			Log.e(LOG, "onPause, getMaxInAppPeriod(this.ctx): " + getMaxInAppPeriod(this.ctx));
		}

		this.setUncaughtExceptionHandler(deafultUncaughtExceptionHandler);
	}

	private void setUncaughtExceptionHandler(UncaughtExceptionHandler handler)
	{
		//this.myUncaughtExceptionHandler = handler;
		Thread.setDefaultUncaughtExceptionHandler(handler);
		//		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()
		//		{
		//			public Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
		//
		//			@Override
		//			public void uncaughtException(Thread thread, Throwable ex)
		//			{
		//				Log.e(LOG, "Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()");
		//				onPause();
		//				if (oldHandler != null)
		//				{
		//					oldHandler.uncaughtException(thread, ex);
		//				}
		//			}
		//		});
	}

	private void init()
	{
		this.setUncaughtExceptionHandler(new UncaughtExceptionHandler()
		{
			public Thread.UncaughtExceptionHandler oldHandler = deafultUncaughtExceptionHandler;//Thread.getDefaultUncaughtExceptionHandler();

			@Override
			public void uncaughtException(Thread thread, Throwable ex)
			{
				Log.e(LOG, "Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()");
				onPause();
				if (oldHandler != null)
				{
					oldHandler.uncaughtException(thread, ex);
				}
			}
		});

		//		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()
		//		{
		//			Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
		//
		//			@Override
		//			public void uncaughtException(Thread thread, Throwable ex)
		//			{
		//				Log.e(LOG, "Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()");
		//				onPause();
		//				if (oldHandler != null)
		//				{
		//					oldHandler.uncaughtException(thread, ex);
		//				}
		//			}
		//		});

		mInterstitialAd = new InterstitialAd(ctx);
		mInterstitialAd.setAdUnitId(ctx.getResources().getString(R.string.AD_UNIT_ID_FULL_SCREEN));
		mInterstitialAd.setAdListener(new AdListener()
		{
			@Override
			public void onAdClosed()
			{
				//must reset needToShowAds
				CheckTimeToAds.adsShown(ctx);
			}

			public void onAdLeftApplication()
			{
				//must reset needToShowAds
				CheckTimeToAds.adsShown(ctx);
			}

			@Override
			public void onAdLoaded()
			{
				Log.e(LOG, "onAdLoaded");
				if (CheckTimeToAds.isTimeToShowAds(ctx))
				{
					mInterstitialAd.show();
				}
			}

			public void onAdFailedToLoad(int errorCode)
			{
				Log.e(LOG, "onAdFailedToLoad with errorCode " + errorCode);
				requestNewInterstitial();
			}

			// Сохраняет состояние приложения перед переходом к оверлею объявления.
			@Override
			public void onAdOpened()
			{
				//must reset needToShowAds
				CheckTimeToAds.adsShown(ctx);
			}
		});
		//		if (CheckTimeToAds.isTimeToShowAds(ctx))
		//		{
		//			this.requestNewInterstitial();
		//		}
	}
}