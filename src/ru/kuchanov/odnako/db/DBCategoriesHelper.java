/*
 07.12.2014
DBHelper.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import ru.kuchanov.odnako.lists_and_utils.CatData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBCategoriesHelper extends SQLiteOpenHelper
{

	String DB_TAG = "DBCategories_LOG";

	Context ctx;

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public DBCategoriesHelper(Context context, String name, CursorFactory factory, int version)
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
	public DBCategoriesHelper(Context context, String name, CursorFactory factory, int version,
	DatabaseErrorHandler errorHandler)
	{
		super(context, name, factory, version, errorHandler);
		// TODO Auto-generated constructor stub
	}

	public DBCategoriesHelper(Context context)
	{
		// конструктор суперкласса
		super(context, "myDB", null, 1);

		this.ctx = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		Log.d(DB_TAG, "DBCategories onCreate called");

		// создаем таблицу с полями
		db.execSQL("create table categoriestable ("
		+ "id integer primary key autoincrement,"
		+ "url text,"
		+ "title text,"
		+ "categoryimgurl text,"
		+ "categoryimgfilename text,"

		+ "preview text,"

		+ "timestamp integer" + ");");

		//fill with initial values

		//all tags
		for (int i = 0; i < CatData.getAllTagsNames(ctx).length; i++)
		{
			ContentValues initialValues = new ContentValues();
			initialValues.put("url", CatData.getAllTagsLinks(ctx)[i]);
			initialValues.put("title", CatData.getAllTagsNames(ctx)[i]);
			initialValues.put("categoryimgurl", CatData.getAllTagsImgsURLs(ctx)[i]);
			initialValues.put("categoryimgurl", CatData.getAllTagsImgsFILEnames(ctx)[i]);

			initialValues.put("timestamp", 0);

			long rowID = db.insert("categoriestable", null, initialValues);
			Log.d(DB_TAG, "row inserted, iD = " + rowID);
//			String[] from = { "title" };
//			String where = "id" + "=?";
//			String[] whereArgs = new String[] { rowID + "" };
//			Cursor c = db.query("categoriestable", from, where, whereArgs, null, null, null, null);
//			if (c != null)
//			{
//				while (c.moveToNext())
//				{
//					String title = c.getString(c.getColumnIndex("title"));
//					Log.d(DB_TAG, "row inserted, ID = " + rowID + "title: " + title);
//				}
//			}
		}

		//menu categories
		for (int i = 0; i < CatData.getAllCategoriesMenuLinks(ctx).length; i++)
		{
			ContentValues initialValues = new ContentValues();
			initialValues.put("url", CatData.getAllCategoriesMenuLinks(ctx)[i]);
			initialValues.put("title", CatData.getAllCategoriesMenuNames(ctx)[i]);

			initialValues.put("timestamp", 0);

			long rowiD = db.insert("categoriestable", null, initialValues);
			Log.d(DB_TAG, "row inserted, iD = " + rowiD);
		}

		//all authors
		for (int a = 0; a < CatData.getAllAuthorsBlogsURLs(ctx).length; a++)
		{
			ContentValues initialValues = new ContentValues();
			initialValues.put("url", CatData.getAllAuthorsBlogsURLs(ctx)[a]);
			initialValues.put("title", CatData.getAllAuthorsNames(ctx)[a]);

			initialValues.put("timestamp", 0);

			long rowID = db.insert("categoriestable", null, initialValues);
			Log.d(DB_TAG, "row inserted, ID = " + rowID);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub

	}

}
