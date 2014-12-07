/*
 07.12.2014
DBHelper.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper
{

	String DB_TAG = "DB_LOG";

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public DBHelper(Context context, String name, CursorFactory factory, int version)
	{
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 * @param errorHandler
	 */
	public DBHelper(Context context, String name, CursorFactory factory, int version, DatabaseErrorHandler errorHandler)
	{
		super(context, name, factory, version, errorHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		Log.d(DB_TAG, "DB onCreate called");

		// создаем таблицу с полями
		db.execSQL("create table artstable ("
		+ "id integer primary key autoincrement,"
		+ "category text,"
		+ "url text,"
		+ "title text,"
		+ "authorname text,"
		+ "authorblogurl text,"
		+ "authorblogimg text,"
		+ "artimg text,"

		+ "preview text,"
		+ "pubdate text,"

		+ "numofcomments integer,"
		+ "numofshares integer,"

		+ "arttext text,"
		+ "authordescr text,"
		+ "tegsmain text,"
		+ "tegsall text,"
		+ "sharequont text,"
		+ "toreadmain text,"
		+ "toreadmore text,"
		+ "timestamp integer,"+ ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub

	}

}
