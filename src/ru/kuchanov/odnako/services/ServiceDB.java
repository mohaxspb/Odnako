/*
 07.12.2014
ServiceDB.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.UpdateBuilder;

import ru.kuchanov.odnako.db.ArtAutTable;
import ru.kuchanov.odnako.db.ArtCatTable;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.db.Category;
import ru.kuchanov.odnako.db.DataBaseHelper;
import ru.kuchanov.odnako.download.ParsePageForAllArtsInfo;
import ru.kuchanov.odnako.fragments.callbacks.AllArtsInfoCallback;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import android.util.Log;
import android.widget.Toast;

public class ServiceDB extends Service implements AllArtsInfoCallback
{
	SharedPreferences pref;

	final private static String LOG_TAG = "ServiceDB";

	final public static String DB_ANSWER_NEVER_REFRESHED = "never refreshed";
	final public static String DB_ANSWER_REFRESH_BY_PERIOD = "refresh by period";
	final public static String DB_ANSWER_INFO_SENDED_TO_FRAG = "we have already send info from DB to frag";
	final public static String DB_ANSWER_NO_ENTRY_OF_ARTS = "no_entry_in_db";
	final public static String DB_ANSWER_SQLEXCEPTION_ARTCAT = "SQLException in artCat.query()";
	final public static String DB_ANSWER_SQLEXCEPTION_CAT = "SQLException in cat.query()";
	final public static String DB_ANSWER_SQLEXCEPTION_ARTS = "SQLException in arts.query()";
	final public static String DB_ANSWER_SQLEXCEPTION_AUTHOR = "SQLException in aut.query()";
	final public static String DB_ANSWER_UNKNOWN_CATEGORY = "no entries in Category and Author";

	///////////
	private DataBaseHelper dataBaseHelper = null;

	private DataBaseHelper getHelper()
	{
		if (dataBaseHelper == null)
		{
			//			dataBaseHelper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
			dataBaseHelper = new DataBaseHelper(this, DataBaseHelper.DATABASE_NAME, null, 11);
			//			this.dataBaseHelper.clearArticleTable();
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

		//pref
		this.pref = PreferenceManager.getDefaultSharedPreferences(this);
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
			//get pageToLoad
			int pageToLoad;
			pageToLoad = intent.getIntExtra("pageToLoad", 1);
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

			//if there is frag to download we do not need to go to DB
			//we simply start download
			if (startDownload)
			{
				//TODO start download
			}
			else
			{
				//get info from appDB. 
				//if it's null - start load and notify caller about it
				//if not, check timeStamp of appBD; 
				//if it's more than 15 min less then given timeStamp return appDB info and notify, that download starts
				//otherwise return appDB and notify that it is upToDate
				//maybe we need FLAG here to start downLoad in all cases
				//				this.getInfoFromDB(catToLoad, timeStamp/* , startDownload */, pageToLoad);
				//method getInfoFromDB will return result of searching throw DB
				String DBRezult = this.getInfoFromDB(catToLoad, cal, pageToLoad);
				Log.d(LOG_TAG, DBRezult);

				switch (DBRezult)
				{
					case DB_ANSWER_SQLEXCEPTION_CAT:
						Toast.makeText(this, "Ошибка чтения Базы Данных, КАТЕГОРИЯ", Toast.LENGTH_LONG).show();
					break;
					case DB_ANSWER_SQLEXCEPTION_AUTHOR:
						Toast.makeText(this, "Ошибка чтения Базы Данных, АВТОР", Toast.LENGTH_LONG).show();
					break;
					case DB_ANSWER_SQLEXCEPTION_ARTS:
						Toast.makeText(this, "Ошибка чтения Базы Данных, Статья", Toast.LENGTH_LONG).show();
					break;
					case DB_ANSWER_SQLEXCEPTION_ARTCAT:
						Toast.makeText(this, "Ошибка чтения Базы Данных, СТАТЬЯ_КАТЕГОРИЯ", Toast.LENGTH_LONG).show();
					break;
					case DB_ANSWER_NEVER_REFRESHED:
						//TODO was never refreshed, so start to refresh
						//so start download category with 1-st page
						this.startDownLoad(catToLoad, 1);
					break;
					case DB_ANSWER_REFRESH_BY_PERIOD:
						//TODO was refreshed more than max period, so start to refresh
						//so start download category with 1-st page
						//but firstly we must show old articles
						this.startDownLoad(catToLoad, 1);
					break;

					case DB_ANSWER_NO_ENTRY_OF_ARTS:
						//TODO no arts in DB (why?)
						//we get it if there is no need to refresh by period, so we have one successful load in past...
						//but no art's in db... that's realy strange! =)
						//so start download from web
						this.startDownLoad(catToLoad, 1);
					break;
					case DB_ANSWER_UNKNOWN_CATEGORY:
					//TODO here we must create new entry in Category (or Author) table
					//and start download arts of this category

					break;
					case DB_ANSWER_INFO_SENDED_TO_FRAG:
					//TODO here we have nothing to do... Cause there is no need to load somthing from web,
					//and arts have been already sended to frag
					break;
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	//	private String getInfoFromDB(String catToLoad, Long timeStamp, /*boolean startDownload,*/ int pageToLoad)
	private String getInfoFromDB(String catToLoad, Calendar cal, int pageToLoad)
	{
		Long lastRefreshedMills;
		//try to find db entry for given catToLoad
		//first in Category
		Category cat = null;
		try
		{
			cat = this.getHelper().getDaoCategory().queryBuilder().where().eq(Category.URL_FIELD_NAME, catToLoad)
			.queryForFirst();

			//Log.d(LOG_TAG, "cat != null: " + String.valueOf(cat != null));
			//it can be null, if we have no entry for given catToLoad. Yes, we test it!
			if (cat != null)
			{
				//first try to know when was last sink
				lastRefreshedMills = cat.getRefreshed().getTime();
				if (lastRefreshedMills == 0)
				{
					//was never refreshed, so start to refresh
					return DB_ANSWER_NEVER_REFRESHED;
				}
				else
				{
					//check period from last sink
					int secondsInMills = 1000;
					int minutes = secondsInMills * 60;
					int testCheckPeriod = 1;
					int checkPeriod = this.pref.getInt("checkPeriod", testCheckPeriod);
					long refreshed = cat.getRefreshed().getTime();
					int givenMinutes = (int) (cal.getTimeInMillis() / minutes);
					int refreshedMinutes = (int) (refreshed / minutes);
					int periodFromRefreshedInMinutes = givenMinutes - refreshedMinutes;
					if (periodFromRefreshedInMinutes > checkPeriod)
					{
						return DB_ANSWER_REFRESH_BY_PERIOD;
					}
					else
					{
						//we do not have to refresh by period,
						//so go to check if there is info in db
						//						return "do not refresh by period";
					}

				}
				int catId = cat.getId();
				List<ArtCatTable> artCat = null;
				try
				{
					artCat = this.getHelper().getDaoArtCatTable().queryBuilder().where()
					.eq(ArtCatTable.CATEGORY_ID_FIELD_NAME, catId).query();
					Log.d(LOG_TAG, "artCat.size(): " + artCat.size());
					if (artCat.size() != 0)
					{
						//TODO so there is some arts in DB by category, that we can send to frag and show
						//sending...
						return DB_ANSWER_INFO_SENDED_TO_FRAG;
						//						return "there_are_entries_in_db";
					}
					else
					{
						//there are no arts of given category in DB, so start to load it
						//but firstly we must notify frag about it (may be we do not need it) 0_o
						//						startDownload = true;
						//						this.startDownLoad(catToLoad, pageToLoad);
						return DB_ANSWER_NO_ENTRY_OF_ARTS;
					}
				} catch (SQLException e)
				{
					e.printStackTrace();
					return DB_ANSWER_SQLEXCEPTION_ARTCAT;
				}
			}//if( cat!=null)
			else
			{
				Log.d(LOG_TAG, "cat = null, so goto search in Author");
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
			return DB_ANSWER_SQLEXCEPTION_CAT;
		}
		//if there is no entry in Category
		//try to find in Author
		Author aut = null;
		try
		{
			aut = this.getHelper().getDaoAuthor().queryBuilder().where().eq(Author.URL_FIELD_NAME, catToLoad)
			.queryForFirst();
			if (aut != null)
			{
				//first try to know when was last sink
				lastRefreshedMills = aut.getRefreshed().getTime();
				if (lastRefreshedMills == 0)
				{
					//was never refreshed, so start to refresh
					return DB_ANSWER_NEVER_REFRESHED;
				}
				else
				{
					//check period from last sink
					int secondsInMills = 1000;
					int minutes = secondsInMills * 60;
					int testCheckPeriod = 1;
					int checkPeriod = this.pref.getInt("checkPeriod", testCheckPeriod);
					long refreshed = aut.getRefreshed().getTime();
					int givenMinutes = (int) (cal.getTimeInMillis() / minutes);
					int refreshedMinutes = (int) (refreshed / minutes);
					int periodFromRefreshedInMinutes = givenMinutes - refreshedMinutes;
					if (periodFromRefreshedInMinutes > checkPeriod)
					{
						return DB_ANSWER_REFRESH_BY_PERIOD;
					}
					else
					{
						//we do not have to refresh by period,
						//so go to check if there is info in db
						//						return "do not refresh by period";
					}
				}
				//				int catId = aut.getId();
				List<ArtAutTable> arts = null;
				try
				{
					arts = this.getHelper().getDaoArtAutTable().queryBuilder().where()
					.eq(ArtAutTable.AUTHOR_ID_FIELD_NAME, aut.getId()).query();
					Log.d(LOG_TAG, "arts.size(): " + arts.size());
					if (arts.size() != 0)
					{
						//TODO so there is some arts in category, that we can send to frag and show
						//sending...
						return DB_ANSWER_INFO_SENDED_TO_FRAG;
					}
					else
					{
						//there are no arts of given category in bd, so start to load it
						//but firstly we must notify frag about it (maybe not)
						//						startDownload = true;
						return DB_ANSWER_NO_ENTRY_OF_ARTS;
					}
				} catch (SQLException e)
				{
					//Auto-generated catch block
					e.printStackTrace();
					return DB_ANSWER_SQLEXCEPTION_ARTS;
				}
			}
			else
			{
				//no entries in Category and Author... WTF?!
				Log.e(LOG_TAG, "no entries in Category and Author... WTF?!");
				return DB_ANSWER_UNKNOWN_CATEGORY;
			}
		} catch (SQLException e)
		{
			//Auto-generated catch block
			e.printStackTrace();
			return DB_ANSWER_SQLEXCEPTION_AUTHOR;
		}
	}

	private void startDownLoad(String catToLoad, int pageToLoad)
	{
		System.out.println("startDownLoad " + catToLoad + "/page-" + pageToLoad);
		Context context = getApplicationContext();
		ParsePageForAllArtsInfo parse = new ParsePageForAllArtsInfo(catToLoad, pageToLoad, context, this);
		parse.execute();
	}

	// Send an Intent with an action named "custom-event-name". The Intent sent should 
	// be received by the ReceiverActivity.
	private void sendMessage(ArrayList<ArtInfo> someResult, String categoryToLoad)
	{
		//		Log.d("sender", "Broadcasting message");
		Intent intent = new Intent(categoryToLoad);
		Bundle b = new Bundle();
		if (someResult.size() != 0)
		{
			ArtInfo.writeAllArtsInfoToBundle(b, someResult, someResult.get(0));
			//before sending message to listener (frag) we must write gained info to DB
			this.writeArtsToDB(someResult, categoryToLoad);
		}
		else
		{
			//			ArrayList<ArtInfo> empty = new ArrayList<ArtInfo>();
			String[] artInfoArr = new String[] { "empty", "Ни одной статьи не обнаружено.", "empty", "empty", "empty" };
			//			empty.add(new ArtInfo(artInfoArr));
			someResult.add(new ArtInfo(artInfoArr));
			ArtInfo.writeAllArtsInfoToBundle(b, someResult, someResult.get(0));
		}
		intent.putExtras(b);

		//now update REFRESHED field of Category or Author entry in table
		this.updateRefreshedDate(categoryToLoad);
		
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void writeArtsToDB(ArrayList<ArtInfo> someResult, String categoryToLoad)
	{
		//here we'll write gained arts to Article table

		///////////////
		for (ArtInfo a : someResult)
		{
			//check if there is no already exsisted arts in DB by queryForURL
			Article existingArt = null;
			try
			{
				existingArt = this.getHelper().getDaoArticle().queryBuilder().where().eq(Article.URL_FIELD_NAME, a.url)
				.queryForFirst();
			} catch (SQLException e1)
			{
				e1.printStackTrace();
			}
			if (existingArt == null)
			{
				//get author obj if it is in ArtInfo and Author table
				Author aut = null;
				try
				{
					aut = this.getHelper().getDaoAuthor().queryBuilder().where()
					.eq(Author.URL_FIELD_NAME, Author.getURLwithoutSlashAtTheEnd(a.authorBlogUrl)).queryForFirst();

				} catch (SQLException e)
				{
					e.printStackTrace();
				}
				//crate Article obj to pass it to DB
				Article art = new Article(a.getArtInfoAsStringArray(), new Date(System.currentTimeMillis()), aut);
				try
				{
					this.getHelper().getDaoArticle().create(art);
				} catch (SQLException e)
				{
					Log.e(LOG_TAG, art.getTitle() + " error while INSERT");
				}
			}
			else
			{
				//entry already exists... So what we must do in that case? Need to think about it... =)
				//Actually nothing to do, cause we only get here initial ArtInfo from site list
				//in other cases we can update artText or preview and so on, but not here
			}

		}

		//and fill ArtCatTable with entries of arts
		//TODO check if it's loading from top (new) of from bottom (previous)
		//TODO if previous so check how many matches by id in ArtCat(ArtAut) and insert AFTER last category article
		//
		/////check if there are arts of given category
		//{
		//if so ...
		//firstly check how many of gained arts are new by calculating how many dismatches with table entries 
		//(match by id, that we gain from Article table by gain Arts by url)
		//
		//if(new=0) there is no new arts, so mark category sinked and do NO inserts in DB table
		//else if(new<30) set Category SINKED and insert Arts in front of this category entries 
		//so we must increment all arts ids for arts, that have id>=id of 1-st art of this category by "new"
		//else if(new>=30) set Category UNSINKED and insert Arts in front of this category entries 
				//so we must increment all arts ids for arts, that have id>=id of 1-st art of this category by "new"
		//}
		//if not just insert entries (with art id and cat id)
		// and set given category sink to true;
		//{
		//}

	}

	//write refreshed date to entry of given category
	public void updateRefreshedDate(String categoryToLoad)
	{
		if (this.isCategory(categoryToLoad))
		{
			try
			{
				UpdateBuilder<Category, Integer> updateBuilder = this.getHelper().getDaoCategory().updateBuilder();
				updateBuilder.updateColumnValue(Category.REFRESHED_FIELD_NAME, new Date(System.currentTimeMillis()));
				updateBuilder.where().eq(Category.URL_FIELD_NAME, categoryToLoad);
				updateBuilder.update();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				UpdateBuilder<Author, Integer> updateBuilder = this.getHelper().getDaoAuthor().updateBuilder();
				updateBuilder.updateColumnValue(Author.REFRESHED_FIELD_NAME, new Date(System.currentTimeMillis()));
				//DO NOT forget, that we can recive categoryToLoad with or without "/" at the and!
				updateBuilder.where().eq(Author.URL_FIELD_NAME, Author.getURLwithoutSlashAtTheEnd(categoryToLoad));
				updateBuilder.update();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void doSomething(ArrayList<ArtInfo> someResult, String categoryToLoad)
	{
		this.sendMessage(someResult, categoryToLoad);
	}

	public IBinder onBind(Intent intent)
	{
		Log.d(LOG_TAG, "onBind");
		return null;
	}

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

	public boolean isCategory(String url)
	{
		boolean isCategory = false;
		try
		{
			Category cat = this.getHelper().getDaoCategory().queryBuilder().where().eq(Category.URL_FIELD_NAME, url)
			.queryForFirst();
			if (cat != null)
			{
				isCategory = true;
			}
			else
			{
				isCategory = false;
			}
		} catch (SQLException e)
		{
			Log.e("err", "SQLException isCategory");
		}
		return isCategory;

	}
}
