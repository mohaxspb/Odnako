/*
 16.01.2015
ContentProviderOdnakoDB.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.sql.SQLException;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.stmt.QueryBuilder;

import ru.kuchanov.odnako.R;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class ContentProviderOdnakoDB extends ContentProvider
{
	private static final String TAG = ContentProviderOdnakoDB.class.getSimpleName();

	private DataBaseHelper dataBaseHelper;

	Context ctx;

	/**
	 * Content authority for this provider.
	 */
	private static final String AUTHORITY = "ru.kuchanov.odnako.db.ContentProviderOdnakoDB";

	/**
	 * URI ID for route: /cards
	 */
	public static final int ROUTE_ARTICLE = 1;

	/**
	 * URI ID for route: /cards/{ID}
	 */
	public static final int ROUTE_ARTICLE_ID = 2;

	/**
	 * UriMatcher, used to decode incoming URIs.
	 */
	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static
	{
		sUriMatcher.addURI(AUTHORITY, "article", ROUTE_ARTICLE);
		sUriMatcher.addURI(AUTHORITY, "article/*", ROUTE_ARTICLE_ID);
	}

	@Override
	public boolean onCreate()
	{
		this.ctx = this.getContext();
		this.getHelper();
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		Log.d(TAG, "Cursor query CALLED!");

		int uriMatch = sUriMatcher.match(uri);

		switch (uriMatch)
		{
			case ROUTE_ARTICLE:
				// Return all known entries.
				// Note: Notification URI must be manually set here for loaders to correctly
				// register ContentObservers.
				// build your query
				QueryBuilder<Article, Integer> qb = null;
				try
				{
					qb = this.getHelper().getDaoArticle().queryBuilder();
				} catch (SQLException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// when you are done, prepare your query and build an iterator
				CloseableIterator<Article> iterator = null;
				Cursor cursor = null;
				try
				{
					//qb.query();
					iterator = this.getHelper().getDaoArticle()
					.iterator(qb.where().ge(Article.ID_FIELD_NAME, 0).prepare());
					// get the raw results which can be cast under Android
					AndroidDatabaseResults results = (AndroidDatabaseResults) iterator.getRawResults();
					cursor = results.getRawCursor();
				} catch (SQLException e)
				{
					e.printStackTrace();
				} finally
				{
					//iterator.closeQuietly();
				}
				

				String msg = String.valueOf(cursor.getCount());
				Log.d(TAG, "cursor.getCount(): " + msg);
				//some test
				cursor.moveToFirst();
				//				if (cursor.moveToFirst())
				//				{
				//					while (!cursor.isAfterLast())
				//					{
				//						String data = cursor.getString(cursor.getColumnIndex(Article.URL_FIELD_NAME));
				//						// do what ever you want here
				//						Log.d(TAG, "cursor.getPosition(): "+String.valueOf(cursor.getPosition())+" = " + data);
				//						cursor.moveToNext();
				//					}
				//				}
				if (cursor.moveToFirst())
				{
					String data = cursor.getString(cursor.getColumnIndex(Article.URL_FIELD_NAME));
					Log.d(TAG, "cursor.getPosition(): " + String.valueOf(cursor.getPosition()) + " = " + data);
				}
				
				cursor.setNotificationUri(this.getContext().getContentResolver(), uri);
				return cursor;
		}

		return null;
	}

	@Override
	public String getType(Uri uri)
	{
		//		final int match = sUriMatcher.match(uri);
		//		switch (match)
		//		{
		//			case ROUTE_ARTICLE:
		//				return CardUris.CONTENT_CARDS;
		//			case ROUTE_ARTICLE_ID:
		//				return CardUris.CONTENT_ITEM_CARD;
		//			default:
		//				throw new UnsupportedOperationException("Unknown uri: " + uri);
		//		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
	{
		return 0;
	}

	private DataBaseHelper getHelper()
	{
		if (dataBaseHelper == null)
		{
			int dbVer = this.ctx.getResources().getInteger(R.integer.db_version);
			dataBaseHelper = new DataBaseHelper(this.ctx, DataBaseHelper.DATABASE_NAME, null, dbVer);
		}
		return dataBaseHelper;
	}

}
