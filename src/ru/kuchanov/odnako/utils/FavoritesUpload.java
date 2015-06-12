/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import java.io.IOException;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.db.Favorites;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class FavoritesUpload extends AsyncTask<Void, Void, String>
{
	private final static String LOG = FavoritesUpload.class.getSimpleName() + "/";

	Context ctx;

	public FavoritesUpload(Context ctx)
	{
		this.ctx = ctx;
	}

	protected String doInBackground(Void... arg)
	{
		String answer = null;

		String url = "http://kuchanov.ru/odnako/favorites/upload.php";

		FormEncodingBuilder builder = new FormEncodingBuilder();
		Request.Builder request = new Request.Builder();

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		String authorsFavs = pref.getString(Favorites.KEY_AUTHORS, Const.EMPTY_STRING);
		builder.add(Favorites.KEY_AUTHORS, authorsFavs);
		String catsFavs = pref.getString(Favorites.KEY_CATEGORIES, Const.EMPTY_STRING);
		builder.add(Favorites.KEY_CATEGORIES, catsFavs);
		String artsFavs = pref.getString(Favorites.KEY_ARTICLES, Const.EMPTY_STRING);
		builder.add(Favorites.KEY_ARTICLES, artsFavs);

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
			Log.e(LOG, answer);
		}
		else
		{
			Log.e(LOG, "answer=null");
		}
	}
}

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
////„то здесь про»сходит?
////- в $dsn задаетс¤ тип бд, с которым будем работать (mysql), хост, им¤ базы данных и чарсет.
////- затем идут им¤ пользовател¤ и пароль
////- после которого задаетс¤ массив опций, про который ни в одном из руководств не пишут.
//
//$table = "favorites";
//
//	 $sql ="CREATE TABLE IF NOT EXISTS $table (
//	 ID INT( 11 ) AUTO_INCREMENT PRIMARY KEY,";
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
//foreach($_POST as $key => $value)
//{
//	$sql.=" $key,";
//}
//$sql = substr($sql, 0, -1);
//$sql.=" ) VALUES (";
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
//foreach($_POST as $key => $value)
//{
//	$var=":".$key;
//	$stmt->bindParam($var, $_POST[$key], PDO::PARAM_STR);
//}
//$stmt->execute(); 
//?>