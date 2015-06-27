/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import java.io.IOException;

import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.db.Favorites;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class FavoritesDownload extends AsyncTask<Void, Void, String>
{
	private final static String LOG = FavoritesDownload.class.getSimpleName() + "/";

	//	private final static String SERVER_ANSWER_DOWNLOAD_OK = "row successfully updated!";
	private final static String SERVER_ANSWER_DOWNLOAD_NO_SUCH_LOGIN = "no such login!";
	private final static String SERVER_ANSWER_WRONG_PASS = "wrong password!";

	ActivityBase act;
	final SharedPreferences pref;

	String login, password;

	public FavoritesDownload(ActivityBase act, String login, String password)
	{
		this.act = act;
		pref = PreferenceManager.getDefaultSharedPreferences(act);
		this.login = login;
		this.password = password;
	}

	protected String doInBackground(Void... arg)
	{
		String answer = null;

		String url = "http://kuchanov.ru/odnako/favorites/download.php";

		//		String loginPass = pref.getString(Favorites.KEY_LOG_PASS, Const.EMPTY_STRING);
		//		boolean logPassExists = Const.EMPTY_STRING.equals(loginPass) == true;
		//		final String[] logPassArr = (logPassExists) ? null : loginPass.split(Favorites.DIVIDER);

		FormEncodingBuilder builder = new FormEncodingBuilder();
		Request.Builder request = new Request.Builder();
		builder.add(Favorites.KEY_LOGIN, login);
		builder.add(Favorites.KEY_PASSWORD, password);

		RequestBody formBody = builder.build();
		request.post(formBody);

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
			char zeroSizeSpace = Character.toChars(65279)[0];
			answer = answer.replaceAll(String.valueOf(zeroSizeSpace), "");
			Log.e(LOG, answer);
			String msg;
			switch (answer)
			{
				case SERVER_ANSWER_DOWNLOAD_NO_SUCH_LOGIN:
					msg = "Сервер говорит, что на нём нет записи о таком логине!";
				break;
				case SERVER_ANSWER_WRONG_PASS:
					msg = "Бездушный сервер ругается на неверный пароль! =(";
				break;
				default:
					//It seems to be what we need to update favorites;
					Favorites.writeFavorites(act, answer);
					try
					{
						act.drawerRightRecyclerView.getAdapter().notifyDataSetChanged();
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					msg = "Ваше избранное загружено!";
				break;
			}
			Toast.makeText(act, msg, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Log.e(LOG, "answer=null");
			String msg = "Не удалось достучаться до сервера. Может с интернетом какие-то проблеммы?..";
			Toast.makeText(act, msg, Toast.LENGTH_SHORT).show();
		}
		
		act.drawerRightSwipeRefreshLayout.setRefreshing(false);
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