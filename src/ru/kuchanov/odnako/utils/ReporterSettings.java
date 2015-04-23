/*
 22.04.2015
ReporterSettings.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import org.acra.ACRA;
import ru.kuchanov.odnako.activities.ActivityPreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Checks if this app was updated from 2.30 versions and sends prefs to server;
 */
public class ReporterSettings
{
	static final String LOG = ReporterSettings.class.getSimpleName();

	public static final String THROWABLE_ADS_ON = "Report settings after updating from ver 230";
	static final String SETTINGS_REPORT_SENDED = "settings_report_sended";
	public static final String FORM_URI_ADS_ON = "http://kuchanov.ru/acra/adsOn.php";

	/**
	 * In old versions (before 3.x.x) we have adsOn pref. In newer versions we
	 * haven't it. So we can check if there is key "adsOn" in prefs, then check
	 * if we never send report (by preferences too, i.e.
	 * SETTINGS_REPORT_SENDED). And, if all is "true" - it's situation of
	 * upgrading from 2.xxx version to 3.x.x and so we report to our server
	 * about it. On server we put all info to DB.
	 * 
	 * WARNING: as I understand we do not need to check DEVICE_ID or
	 * INSTALLATION_ID on server, because if there is no SETTINGS_REPORT_SENDED
	 * user never send report or he do not have adsOn pref as he initial install
	 * 3.x.x or have already cleared apps data (so there aren't both prefs)
	 * 
	 * @param ctx
	 */
	public static void checkIsUpdatedFromOldVer(Context ctx)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		//		Map<String, ?> prefsMap = prefs.getAll();
		//		ArrayList<String> keys = new ArrayList<String>(prefsMap.keySet());
		//		for (String key : keys)
		//		{
		//			Log.i(LOG, key + ": " + prefsMap.get(key).toString());
		//		}

		
		//For test we add adsOn pref to prefs
//		prefs.edit().putBoolean(ActivityPreference.PREF_KEY_ADS_IS_ON, false).commit();
//		prefs.edit().putBoolean(SETTINGS_REPORT_SENDED, false).commit();

		boolean isUpdatedFromVersion230 = (prefs.contains(ActivityPreference.PREF_KEY_ADS_IS_ON));

		if (isUpdatedFromVersion230)
		{
			Log.i(LOG, "IS updated from version 230");
			Toast.makeText(ctx, "IS updated from version 230", Toast.LENGTH_LONG).show();
			reportSettings(ctx);
		}
		else
		{
			Toast.makeText(ctx, "Is NOT updated from version 230", Toast.LENGTH_LONG).show();
			Log.i(LOG, "Is NOT updated from version 230");
		}
	}

	private static void reportSettings(Context ctx)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		if (prefs.getBoolean(SETTINGS_REPORT_SENDED, false) == true)
		{
			Toast.makeText(ctx, "SETTINGS REPORT already SENDED", Toast.LENGTH_LONG).show();
			Log.i(LOG, "SETTINGS REPORT already SENDED");
		}
		else
		{
			Toast.makeText(ctx, "SETTINGS REPORT not SENDED so send it", Toast.LENGTH_LONG).show();
			Log.i(LOG, "SETTINGS REPORT not SENDED so send it");
			boolean adsOn = prefs.getBoolean(ActivityPreference.PREF_KEY_ADS_IS_ON, false) == true;
			String adsOnValue = (adsOn) ? "1" : "0";
			ACRA.getErrorReporter().putCustomData("ads_on", adsOnValue);

			ACRA.getErrorReporter().handleSilentException(new Throwable(THROWABLE_ADS_ON));

			prefs.edit().putBoolean(SETTINGS_REPORT_SENDED, true).commit();
		}
	}
}

//<?php
//$user="p211659_db";
//$pass="NO_WAY";
//
//$db="p211659_db";
//$host="p211659.mysql.ihc.ru";
//$charset="utf-8";
//
//$dsn = "mysql:host=$host;dbname=$db;charset=$charset";
//$opt = array
//(
//	PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
//	PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
//);
//$pdo = new PDO($dsn, $user, $pass, $opt);
//
//$table = "acra_reports_ads_on";
//
//	 $sql ="CREATE TABLE IF NOT EXISTS $table (
//	 ID INT( 11 ) AUTO_INCREMENT PRIMARY KEY,";
//	 
//	$adsOnArr=explode(" = ", $_POST['CUSTOM_DATA']);
//	$sql.=" $adsOnArr[0] INT( 1 ), ";
//	
//	foreach($_POST as $key => $value)
//	{
//		$sql.=" $key TEXT( 2000 ), ";
//	}
//	$sql = substr($sql, 0, -2);
//	$sql.=" );";
//	echo $sql;
//	$pdo->exec($sql);
//	print("Created $table Table.\n");
//
//$sql = "INSERT INTO $table (";
//$sql.=" $adsOnArr[0],";
//foreach($_POST as $key => $value)
//{
//	$sql.=" $key,";
//}
//$sql = substr($sql, 0, -1);
//$sql.=" ) VALUES (";
//$sql.=" :$adsOnArr[0],";
//foreach($_POST as $key => $value)
//{
//	$sql.=" :$key,";
//}
//$sql = substr($sql, 0, -1);
//$sql.=" )";
////echo $sql;
//                                      
//$stmt = $pdo->prepare($sql);
//
//$var=":".$adsOnArr[0];
//$stmt->bindParam($var, $adsOnArr[1], PDO::PARAM_INT);
//foreach($_POST as $key => $value)
//{
//	$var=":".$key;
//	$stmt->bindParam($var, $_POST[$key], PDO::PARAM_STR);
//}
//$stmt->execute(); 
//?>