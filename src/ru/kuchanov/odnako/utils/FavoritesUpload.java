/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import java.io.IOException;

import ru.kuchanov.odnako.Const;
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

public class FavoritesUpload extends AsyncTask<Void, Void, String>
{
	private final static String LOG = FavoritesUpload.class.getSimpleName() + "/";

	private final static String SERVER_ANSWER_UPDATE_OK = "row successfully updated!";
	private final static String SERVER_ANSWER_INSERT_OK = "row successfully added!";
	private final static String SERVER_ANSWER_WRONG_PASS = "wrong password!";

	ActivityBase act;
	String login, password;

	public FavoritesUpload(ActivityBase act, String login, String password)
	{
		this.act = act;
		this.login = login;
		this.password = password;
	}

	protected String doInBackground(Void... arg)
	{
		String answer = null;

		String url = "http://kuchanov.ru/odnako/favorites/upload.php";

		FormEncodingBuilder builder = new FormEncodingBuilder();
		Request.Builder request = new Request.Builder();

		builder.add(Favorites.KEY_LOGIN, this.login);
		builder.add(Favorites.KEY_PASSWORD, this.password);

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
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
			char zeroSizeSpace = Character.toChars(65279)[0];
			answer = answer.replaceAll(String.valueOf(zeroSizeSpace), "");
			Log.e(LOG, answer);
			String msg;
			switch (answer)
			{
				case SERVER_ANSWER_INSERT_OK:
					msg = "Ваше избранное сохранено на сервере!";
				break;
				case SERVER_ANSWER_UPDATE_OK:
					msg = "Ваше избранное обновлено на сервере!";
				break;
				case SERVER_ANSWER_WRONG_PASS:
					msg = "Бездушный сервер ругается на неверный пароль! =(";
				break;
				default:
					msg = "Сервер ответил что-то невнятное и никто не знает в чём дело( Но виноват, скорее всего, разработчик.";
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