/*
 07.12.2014
ServiceDB.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.services;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import ru.kuchanov.odnako.db.Category;
import ru.kuchanov.odnako.db.DataBaseHelper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ServiceDB extends Service
{

	final String LOG_TAG = "ServiceDB";

	///////////
	private DataBaseHelper dataBaseHelper = null;

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.d(LOG_TAG, "onDestroy");
		if (dataBaseHelper != null)
		{
			OpenHelperManager.releaseHelper();
			dataBaseHelper = null;
		}
	}

	private DataBaseHelper getHelper()
	{
		if (dataBaseHelper == null)
		{
			dataBaseHelper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
		}
		return dataBaseHelper;
	}

	///////////

	public void onCreate()
	{
		super.onCreate();
		Log.d(LOG_TAG, "onCreate");
		//get access to my_awersomeDB
		this.getHelper();
	}

	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.d(LOG_TAG, "onStartCommand");
		if (intent != null)
		{
			//gain category
			String catToLoad;
			catToLoad = intent.getStringExtra("categoryToLoad");
			Log.d(LOG_TAG, catToLoad);
			//get timeStamp
			Long timeStamp;
			timeStamp = intent.getLongExtra("timeStamp", System.currentTimeMillis());
			Calendar cal = Calendar.getInstance(TimeZone.getDefault(), new Locale("ru"));
			cal.setTimeInMillis(timeStamp);
			//get startDownload flag
			boolean startDownload;
			startDownload = intent.getBooleanExtra("startDownload", false);
			//			Log.d(LOG_TAG,
			//			cal.get(Calendar.YEAR) + "." + cal.get(Calendar.MONTH) + "." + cal.get(Calendar.DAY_OF_MONTH) + "."
			//			+ cal.get(Calendar.HOUR_OF_DAY) + "." + cal.get(Calendar.MINUTE));
			//			Log.d(LOG_TAG, cal.getTime().toString());
			//			Log.d(LOG_TAG,
			//			cal.get(Calendar.DAY_OF_MONTH) + " " + cal.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("ru"))
			//			+ " " + cal.get(Calendar.YEAR));

			//get info from appDB. 
			//if it's null - start load and notify caller about it
			//if not, check timeStamp of appBD; 
			//if it's more than 15 min less then given timeStamp return appDB info and notify, that download starts
			//otherwise return appDB and notify that it is upToDate
			//maybe we need FLAG here to start downLoad in all cases
			this.getInfoFromDB(catToLoad, timeStamp, startDownload);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void getInfoFromDB(String catToLoad, Long timeStamp, boolean startDownload)
	{
		List<Category> allCatsListFromDB=null;
		
		try
		{
			allCatsListFromDB=this.getHelper().getDaoCategory().queryForAll();
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(allCatsListFromDB!=null)
		{
			for(Category c: allCatsListFromDB)
			{
				Log.d(LOG_TAG, c.getTitle()+" url: "+c.getUrl());
			}
		}
		else
		{
			Log.d(LOG_TAG, "allCatsListFromDB=null");
		}
		

	}

	//delete
	//		DBCategoriesHelper dbCategoriesHelper1=new DBCategoriesHelper(this);
	//	    SQLiteDatabase db1 = dbCategoriesHelper1.getWritableDatabase();
	//	    db1.delete("categoriestable", null, null);
	//		this.deleteDatabase("myDB");

	//TODO request to DB, now return default data with zero timeStamp
	// подключаемся к БД
	//		DBCategoriesHelper dbCategoriesHelper=new DBCategoriesHelper(this);
	//	    SQLiteDatabase db = dbCategoriesHelper.getWritableDatabase();

	//		String[] from = { "title" };
	//		String where = "id" + "=?";
	//		String[] whereArgs = new String[] { rowID + "" };
	//		Cursor c = db.query("categoriestable", from, where, whereArgs, null, null, null, null);
	//		if (c != null)
	//		{
	//			while (c.moveToNext())
	//			{
	//				String title = c.getString(c.getColumnIndex("title"));
	//				Log.d(DB_TAG, "row inserted, ID = " + rowID + "title: " + title);
	//			}
	//		}

	public IBinder onBind(Intent intent)
	{
		Log.d(LOG_TAG, "onBind");
		return null;
	}

}
