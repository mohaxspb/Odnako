package ru.kuchanov.odnako.utils;

import java.util.Locale;

import ru.kuchanov.odnako.R;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.widget.LinearLayout.LayoutParams;

public class AddAds
{
	ActionBarActivity act;
	AdView adView;
	
	boolean isTestDevice;

	public AddAds(ActionBarActivity act, AdView adView)
	{
		this.act = act;
		this.adView = adView;
	}

//	public void addAd(String activityKey)
//	{
//		//adMob
//		SharedPreferences pref;
//		pref = PreferenceManager.getDefaultSharedPreferences(act);
//		//check google play service available
//		int isAv = GooglePlayServicesUtil.isGooglePlayServicesAvailable(act);
//		if (isAv == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED || isAv == ConnectionResult.SERVICE_MISSING || isAv == ConnectionResult.SERVICE_DISABLED
//		|| isAv == ConnectionResult.SERVICE_INVALID)
//		{
//			System.out.println("google PLAY SERCICE CHECK: " + GooglePlayServicesUtil.isGooglePlayServicesAvailable(act));
//			return;
//		}//end of check google play service available
//
//		//get EMULATOR deviceID
//		String android_id = Settings.Secure.getString(act.getContentResolver(), Settings.Secure.ANDROID_ID);
//		String deviceId = DeviceID.md5(android_id).toUpperCase(Locale.ENGLISH);
//		//end of get EMULATOR deviceID
//
//		/////set design
//		AdMobExtras extras;
//		String odnakoColorInString = Integer.toHexString(act.getResources().getColor(R.color.odnako)).substring(2);
//		//System.out.println("COLOR TO STRING: "+Integer.toHexString(act.getResources().getColor(R.color.odnako)).substring(2));
//		if (pref.getString("theme", "ligth").equals("ligth"))
//		{
//			Bundle bundle = new Bundle();
//			bundle.putString("color_bg", "FFFFFF");
//			bundle.putString("color_bg_top", "FFFFFF");
//			bundle.putString("color_text", "000000");
//			bundle.putString("color_border", "000000");
//			bundle.putString("color_link", "#0000EE");
//			bundle.putString("color_url", "#008000");
//			extras = new AdMobExtras(bundle);
//		}
//		else
//		{
//			Bundle bundle = new Bundle();
//			bundle.putString("color_bg", odnakoColorInString);
//			bundle.putString("color_bg_top", odnakoColorInString);
//			bundle.putString("color_text", "FFFFFF");
//			bundle.putString("color_border", "FFFFFF");
//			bundle.putString("color_link", "#0000EE");
//			bundle.putString("color_url", "#008000");
//			extras = new AdMobExtras(bundle);
//		}
//		//to Gain money
//		AdRequest adRequest = new AdRequest.Builder().addNetworkExtras(extras).build();
//		//end of to Gain money
//		//To Test
//		//AdRequest adRequest = new AdRequest.Builder().addTestDevice(deviceId).addNetworkExtras(extras).build();
//		//end of To Test
//		/////////
//
//		boolean isTestDevice = adRequest.isTestDevice(act);
//		System.out.println("is Admob Test Device ? " + deviceId + " " + isTestDevice);
//		// Загрузка adView с объявлением.
//		if (pref.getBoolean(activityKey, false))
//		{
//			if (adView.getLayoutParams().height == 0)
//			{
//				adView.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
//			}
//			adView.loadAd(adRequest);
//		}
//		else
//		{
//			adView.getLayoutParams().height = 0;
//		}
//		//end of adMob
//	}

	/* adding ads to allActivities since ver 2.3 */
	public void addAd()
	{
		//adMob
		SharedPreferences pref;
		pref = PreferenceManager.getDefaultSharedPreferences(act);

		//check google play service available
		int isAv = GooglePlayServicesUtil.isGooglePlayServicesAvailable(act);
		if (isAv == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED || isAv == ConnectionResult.SERVICE_MISSING || isAv == ConnectionResult.SERVICE_DISABLED
		|| isAv == ConnectionResult.SERVICE_INVALID)
		{
//			System.out.println("google PLAY SERCICE CHECK: " + GooglePlayServicesUtil.isGooglePlayServicesAvailable(act));
//			return;
		}
		else if(isAv==ConnectionResult.SUCCESS)
		{
//			System.out.println("google PLAY SERCICE CHECK: ConnectionResult.SUCCESS");
		}
		//end of check google play service available
		
		
		//get EMULATOR deviceID
		String android_id = Settings.Secure.getString(act.getContentResolver(), Settings.Secure.ANDROID_ID);
		String deviceId = DeviceID.md5(android_id).toUpperCase(Locale.ENGLISH);
		//end of get EMULATOR deviceID

		/////set design
		AdMobExtras extras;
		String odnakoColorInString = Integer.toHexString(act.getResources().getColor(R.color.odnako)).substring(2);
		//System.out.println("COLOR TO STRING: "+Integer.toHexString(act.getResources().getColor(R.color.odnako)).substring(2));
		if (pref.getString("theme", "ligth").equals("ligth"))
		{
			Bundle bundle = new Bundle();
			bundle.putString("color_bg", "FFFFFF");
			bundle.putString("color_bg_top", "FFFFFF");
			bundle.putString("color_text", "000000");
			bundle.putString("color_border", "000000");
			bundle.putString("color_link", "#0000EE");
			bundle.putString("color_url", "#008000");
			extras = new AdMobExtras(bundle);
		}
		else
		{
			Bundle bundle = new Bundle();
			bundle.putString("color_bg", odnakoColorInString);
			bundle.putString("color_bg_top", odnakoColorInString);
			bundle.putString("color_text", "FFFFFF");
			bundle.putString("color_border", "FFFFFF");
			bundle.putString("color_link", "#0000EE");
			bundle.putString("color_url", "#008000");
			extras = new AdMobExtras(bundle);
		}
		//to Gain money
//		AdRequest adRequest = new AdRequest.Builder().addNetworkExtras(extras).build();
		//end of to Gain money
		//To Test
		AdRequest adRequest = new AdRequest.Builder().addTestDevice(deviceId).addNetworkExtras(extras).build();
		//end of To Test
		/////////

		isTestDevice = adRequest.isTestDevice(act);
//		System.out.println("is Admob Test Device ? " + deviceId + " " + isTestDevice);
		// Загрузка adView с объявлением.
		if (pref.getBoolean("adsOn", false))
		{
			if (adView.getLayoutParams().height == 0)
			{
				adView.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
			}
			adView.loadAd(adRequest);
		}
		else
		{
			adView.getLayoutParams().height = 0;
		}
		
		//removing now; delete before publish
//		adView.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
//		adView.getLayoutParams().height = 0;
		//end of adMob
	}
}
