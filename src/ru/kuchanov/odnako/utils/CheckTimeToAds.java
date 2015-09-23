/*
 24.04.2015
CheckTimeToAds.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import java.lang.Thread.UncaughtExceptionHandler;

import ru.kuchanov.odnako.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class CheckTimeToAds
{
	private final static String LOG = CheckTimeToAds.class.getSimpleName() + "/";

	public final static String PREF_KEY_IN_APP_PERIOD = "inAppPeriod";
	public final static String PREF_KEY_MAX_IN_APP_PERIOD = "maxInAppPeriod";
	public final static String PREF_NEED_TO_SHOW_ADS = "needToShowAds";

	private Context ctx;
	private SharedPreferences pref;
	private long timeOnResume;

	private InterstitialAd mInterstitialAd;

	private UncaughtExceptionHandler deafultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
	private UncaughtExceptionHandler myUncaughtExceptionHandler = new UncaughtExceptionHandler()
	{
		public Thread.UncaughtExceptionHandler oldHandler = deafultUncaughtExceptionHandler;

		@Override
		public void uncaughtException(Thread thread, Throwable ex)
		{
			String errMsg = (ex.getLocalizedMessage() != null) ? ex.getLocalizedMessage() : "NULL message";
			Log.e(LOG, "uncaughtException: " + errMsg);
			onPause();
			if (oldHandler != null)
			{
				oldHandler.uncaughtException(thread, ex);
			}
		}
	};

	public CheckTimeToAds(Context ctx, InterstitialAd mInterstitialAd)
	{
		this.ctx = ctx;
		pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		this.mInterstitialAd = mInterstitialAd;
		this.init();
	}

	public void requestNewInterstitial()
	{
		Log.d(LOG, "requestNewInterstitial");
		AdRequest adRequest = new AdRequest.Builder().build();
		mInterstitialAd.loadAd(adRequest);
	}

	public static long getMaxInAppPeriod(Context ctx)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		return pref.getLong(PREF_KEY_MAX_IN_APP_PERIOD, (90L * 60L * 1000L));
	}

	public static void setMaxInAppPeriod(Context ctx, long period)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		pref.edit().putLong(PREF_KEY_MAX_IN_APP_PERIOD, period).commit();
	}

	public static boolean isTimeToShowAds(Context ctx)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		return pref.getBoolean(PREF_NEED_TO_SHOW_ADS, false) == true;
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

		this.setUncaughtExceptionHandler(myUncaughtExceptionHandler);
	}

	public void onPause()
	{
		long timeOnPause = System.currentTimeMillis();
		long inAppPeriod = timeOnPause - this.timeOnResume;

		long alreadyStoredInAppPeriod = this.pref.getLong(PREF_KEY_IN_APP_PERIOD, 0L);

		//check if new inAppPeriod < max
		if (inAppPeriod + alreadyStoredInAppPeriod < getMaxInAppPeriod(this.ctx))
		{
			inAppPeriod += alreadyStoredInAppPeriod;
			this.pref.edit().putLong(PREF_KEY_IN_APP_PERIOD, inAppPeriod).commit();
			//			Log.e(LOG, "onPause, less then max");
		}
		else
		{
			inAppPeriod = 0;//inAppPeriod + alreadyStoredInAppPeriod - getMaxInAppPeriod(this.ctx);
			this.pref.edit().putBoolean(PREF_NEED_TO_SHOW_ADS, true).commit();
			this.pref.edit().putLong(PREF_KEY_IN_APP_PERIOD, inAppPeriod).commit();
			//			Log.e(LOG, "onPause, MORE then max");
		}
		//		Log.e(LOG, "onPause, inAppPeriod: " + inAppPeriod);
		//		Log.e(LOG, "onPause, getMaxInAppPeriod(this.ctx): " + getMaxInAppPeriod(this.ctx));

		this.setUncaughtExceptionHandler(deafultUncaughtExceptionHandler);
	}

	private void setUncaughtExceptionHandler(UncaughtExceptionHandler handler)
	{
		Thread.setDefaultUncaughtExceptionHandler(handler);
	}

	private void init()
	{
		this.setUncaughtExceptionHandler(myUncaughtExceptionHandler);

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
				//				requestNewInterstitial();
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