/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class FavoritesDownload extends AsyncTask<Void, Void, String>
{
	private final static String LOG = FavoritesDownload.class.getSimpleName() + "/";

	Context ctx;

	public FavoritesDownload(Context ctx)
	{
		this.ctx = ctx;
	}

	protected String doInBackground(Void... arg)
	{
		String answer = null;

		String url = "http://kuchanov.ru/odnako/favorites/download.php";

		FormEncodingBuilder builder = new FormEncodingBuilder();
		Request.Builder request = new Request.Builder();

		//		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		//		String authorsFavs = pref.getString(Favorites.KEY_AUTHORS, Const.EMPTY_STRING);
		//		builder.add(Favorites.KEY_AUTHORS, authorsFavs);
		//		String catsFavs = pref.getString(Favorites.KEY_CATEGORIES, Const.EMPTY_STRING);
		//		builder.add(Favorites.KEY_CATEGORIES, catsFavs);
		//		String artsFavs = pref.getString(Favorites.KEY_ARTICLES, Const.EMPTY_STRING);
		//		builder.add(Favorites.KEY_ARTICLES, artsFavs);

//		RequestBody formBody = builder.build();
//		request.post(formBody);

		request.url(url);
		try
		{
			OkHttpClient client = new OkHttpClient();
			Response response = client.newCall(request.build()).execute();
			answer = response.body().string();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return answer;
	}

	protected void onPostExecute(String answer)
	{
		if (answer != null)
		{
			Log.e(LOG, answer);
		}
		else
		{
			Log.e(LOG, "answer=null");
		}
	}
}
//
//<?php
//$user="no";
//$pass="no";
//
//$db="no";
//$host="no";
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
//$table = "favorites";
//
//showAdsOn($pdo, $table);
////show($pdo, $table);
//
//function showAdsOn($pdo, $table)
//{
//	//$sql = "select ads_on from $table";
//	//$rows=$pdo->query($sql);
//	//foreach($rows as $row)
//	//{
//	//	echo $row['ads_on']."<br/>";
//	//}
//	
//	//$sql = "select report_id, ads_on from $table where package_name='ru.kuchanov.odnakopro'";
//	$sql = "SELECT * from $table";
//	$rows=$pdo->query($sql);
//	echo "count:".count($rows, COUNT_NORMAL)."<br/>";
//	foreach($rows as $row)
//	{
//		echo $row['authors']."/ ".$row['categories']."/ ".$row['articles']."<br/>";
//	}
//}
//
//function show($conn, $table)
//{
//	$sql = "SELECT COUNT(*)
//	FROM $table
//	WHERE PACKAGE_NAME='ru.kuchanov.odnako'";
//	$result = $conn->prepare($sql); 
//	$result->execute(); 
//	if ($result)
//	{
//		//Определим количество строк, подходящих под условия выражения SELECT
//		$number_of_rows = $result->fetchColumn(); 
//		if ($number_of_rows > 0) 
//		{
//			echo "count: ".$number_of_rows."<br/>";
//			/* Выполняем реальный SELECT и работаем с его результатами */
//			$sql = "SELECT *
//			FROM $table
//			WHERE PACKAGE_NAME='ru.kuchanov.odnako'";
//			$rows=$conn->query($sql);
//			$adsOn=0;
//			$adsOff=0;
//			foreach ($rows as $row)
//			{
//				if($row['ads_on']==0)
//				{
//					$adsOff++;
//				}
//				else
//				{
//					$adsOn++;
//				}
//			}
//			echo "adsOn/adsOff : ".$adsOn."/".$adsOff."<br/>";
//			$percent=$adsOff*100/$number_of_rows;
//			echo "percent of disabledAds : ".$percent."<br/>";
//		}
//		/* Результатов нет -- делаем что-то другое */
//		else 
//		{
//			print "Нет строк соответствующих запросу.";
//		}
//	}
//	$result = null;
//	$conn = null;
//}
//?>