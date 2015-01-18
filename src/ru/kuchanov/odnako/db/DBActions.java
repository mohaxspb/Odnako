/*
 10.01.2015
DBActions.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.UpdateBuilder;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.utils.DateParse;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Class with actions with DB
 */
public class DBActions
{
	final private static String LOG_TAG = DBActions.class.getSimpleName();

	final public static String DB_ANSWER_NEVER_REFRESHED = "never refreshed";
	final public static String DB_ANSWER_REFRESH_BY_PERIOD = "refresh by period";
	final public static String DB_ANSWER_INFO_SENDED_TO_FRAG = "we have already send info from DB to frag";
	final public static String DB_ANSWER_NO_ENTRY_OF_ARTS = "no_entry_in_db";
	final public static String DB_ANSWER_SQLEXCEPTION_ARTCAT = "SQLException in artCat.query()";
	final public static String DB_ANSWER_SQLEXCEPTION_CAT = "SQLException in cat.query()";
	final public static String DB_ANSWER_SQLEXCEPTION_ARTS = "SQLException in arts.query()";
	final public static String DB_ANSWER_SQLEXCEPTION_AUTHOR = "SQLException in aut.query()";
	final public static String DB_ANSWER_UNKNOWN_CATEGORY = "no entries in Category and Author";

	private DataBaseHelper dataBaseHelper;
	private Context ctx;

	public DBActions(Context ctx, DataBaseHelper dataBaseHelper)
	{
		this.ctx = ctx;
		this.dataBaseHelper = dataBaseHelper;
	}

	private DataBaseHelper getHelper()
	{
		return dataBaseHelper;
	}

	/**
	 * update ArtCatTable to test it's behavior. I.e. we chage IsTop article,
	 * add some new, so we can test behavior on loading from top if we have new
	 * arts from web
	 */
	public void test(String catToLoad)
	{
		PreparedQuery<Category> pQ;
		Integer categoryId = null;
		try
		{
			pQ = this.getHelper().getDaoCategory().queryBuilder().where()
			.eq(Category.URL_FIELD_NAME, catToLoad).prepare();
			categoryId = this.getHelper().getDaoCategory().queryForFirst(pQ).getId();
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//delete some entries in artCat
		try
		{
			//this is first. We'll delete it
			//			String firstArtUrl = "http://www.odnako.org/blogs/k-dosrochnim-viboram-v-serbii-vozmozhnosti-dlya-rossii/";
			//and this is second. We'll mark it as first
			String newFirstArtUrl = "http://www.odnako.org/blogs/eshchyo-nikomu-ne-udavalos-dogovoritsya-s-nacistami/";

			//			List<ArtCatTable> artCatList = this.getHelper().getDaoArtCatTable()
			//			.queryForEq(ArtCatTable.CATEGORY_ID_FIELD_NAME, categoryId);

			ArtCatTable firstArtCatTable = ArtCatTable.getTopArtCat(getHelper(), categoryId, true);

			DeleteBuilder<ArtCatTable, Integer> dB = this.getHelper().getDaoArtCatTable().deleteBuilder();
			dB.where().eq(ArtCatTable.ID_FIELD_NAME, firstArtCatTable.getId());
			dB.delete();

			Article newFirstArticle = this.getHelper().getDaoArticle().queryBuilder().where()
			.eq(Article.URL_FIELD_NAME, newFirstArtUrl).queryForFirst();

			UpdateBuilder<ArtCatTable, Integer> uB = this.getHelper().getDaoArtCatTable().updateBuilder();
			uB.where().eq(ArtCatTable.ARTICLE_ID_FIELD_NAME, newFirstArticle.getId()).and()
			.eq(ArtCatTable.CATEGORY_ID_FIELD_NAME, categoryId);
			uB.updateColumnValue(ArtCatTable.IS_TOP_FIELD_NAME, true);
			uB.updateColumnValue(ArtCatTable.PREVIOUS_ART_URL_FIELD_NAME, null);
			uB.update();

		} catch (SQLException e)
		{
			Log.e(LOG_TAG, "Error in test. delete");
		}
		//write some other entries
		//this is first on page-2, so we we'll set it as last of 1st page
		try
		{
			String lastArtUrl = "http://www.odnako.org/blogs/rossiya-perenapravit-ukrainskiy-gaz-na-novuyu-trubu-v-turciyu-evrope-pridetsya-stroit-svoy-gazoprovod/";

			//update next art of now last
			//NOW WE GET SIMPLY 30th art in list MAY BE ERROR HERE!!!
			List<ArtCatTable> artCatList = this.getHelper().getDaoArtCatTable()
			.queryForEq(ArtCatTable.CATEGORY_ID_FIELD_NAME, categoryId);
			ArtCatTable nowLastArtCat = artCatList.get(artCatList.size() - 1);
			ArtCatTable.updateNextArt(getHelper(), nowLastArtCat.getId(), lastArtUrl);

			Article lastArticle;
			//			String[] artInfoArr = { url, title, img_art, authorBlogUrl, authorName, preview, pubDate, numOfComments, numOfSharings, artText, authorDescr, tegs_main, tegs_all, share_quont, to_read_main, to_read_more, img_author };
			String[] artInfoArr = { lastArtUrl, "последняя тестовая статья", "empty", "empty", "empty", "empty", "0", "0", "0", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty" };
			lastArticle = new Article(artInfoArr, new Date(0), null);
			this.getHelper().getDaoArticle().create(lastArticle);
			//			lastArticle = this.getHelper().getDaoArticle().queryBuilder().where()
			//			.eq(Article.URL_FIELD_NAME, lastArtUrl).queryForFirst();

			String previousArtUrl = this.getHelper().getDaoArticle().queryBuilder().where()
			.eq(Article.ID_FIELD_NAME, nowLastArtCat.getArticleId()).queryForFirst().getUrl();
			ArtCatTable lastArtCat = new ArtCatTable(null, lastArticle.getId(), categoryId, null, previousArtUrl);
			this.getHelper().getDaoArtCatTable().create(lastArtCat);
		} catch (SQLException e)
		{
			Log.e(LOG_TAG, "Error in test. add");
		}
	}

	public String getInfoFromDB(String catToLoad, Calendar cal, int pageToLoad)
	{
		Long lastRefreshedMills;
		//try to find db entry for given catToLoad
		//first in Category
		Category cat = null;
		try
		{
			cat = this.getHelper().getDaoCategory().queryBuilder().where().eq(Category.URL_FIELD_NAME, catToLoad)
			.queryForFirst();

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
					int millsInSecond = 1000;
					long millsInMinute = millsInSecond * 60;
					//time in Minutes. Default period between refreshing.
					int checkPeriod = ctx.getResources().getInteger(R.integer.checkPeriod);
					long refreshed = cat.getRefreshed().getTime();
					int givenMinutes = (int) (cal.getTimeInMillis() / millsInMinute);
					int refreshedMinutes = (int) (refreshed / millsInMinute);
					int periodFromRefreshedInMinutes = givenMinutes - refreshedMinutes;
					if (periodFromRefreshedInMinutes > checkPeriod)
					{
						return DB_ANSWER_REFRESH_BY_PERIOD;
					}
					else
					{
						//we do not have to refresh by period,
						//so go to check if there is info in db
					}

				}
				int catId = cat.getId();
				List<ArtCatTable> artCat = null;
				try
				{
					artCat = this.getHelper().getDaoArtCatTable().queryBuilder().where()
					.eq(ArtCatTable.CATEGORY_ID_FIELD_NAME, catId).query();
					if (artCat.size() != 0)
					{
						//TODO
						//so there is some arts in DB by category, that we can send to frag and show
						//sending...
						ArrayList<ArtInfo> data = new ArrayList<ArtInfo>();
						//calculate initial art in list to send
						int from = 30 * (pageToLoad - 1);
						List<ArtCatTable> dataFromDBToSend = artCat.subList(from, (30 - 1));
						//set ArtCatTable obj to ArtInfo
						//firstly get Article by id then create new ArtInfo obj and add it to list, that we'll send
						for (ArtCatTable a : dataFromDBToSend)
						{
							Article art = this.getHelper().getDaoArticle().queryForId(a.getArticleId());
							ArtInfo artInfoObj = new ArtInfo(art.getAsStringArray());
							data.add(artInfoObj);
						}
						//send directly, cause it's from DB and we do not need to do something with this data
						Intent intent = new Intent(catToLoad);
						intent.putParcelableArrayListExtra(ArtInfo.KEY_ALL_ART_INFO, data);

						LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);

						return DB_ANSWER_INFO_SENDED_TO_FRAG;
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
					//check period from last sink
					int millsInSecond = 1000;
					long millsInMinute = millsInSecond * 60;
					//time in Minutes. Default period between refreshing.
					int checkPeriod = ctx.getResources().getInteger(R.integer.checkPeriod);
					long refreshed = aut.getRefreshed().getTime();
					int givenMinutes = (int) (cal.getTimeInMillis() / millsInMinute);
					int refreshedMinutes = (int) (refreshed / millsInMinute);
					int periodFromRefreshedInMinutes = givenMinutes - refreshedMinutes;
					if (periodFromRefreshedInMinutes > checkPeriod)
					{
						return DB_ANSWER_REFRESH_BY_PERIOD;
					}
					else
					{
						//we do not have to refresh by period,
						//so go to check if there is info in db
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

	public void writeArtsToDB(ArrayList<ArtInfo> someResult, String categoryToLoad, int pageToLoad)
	{
		//here we'll write gained arts to Article table
		for (ArtInfo a : someResult)
		{
			//check if there is no already existing arts in DB by queryForURL
			Article existingArt = null;
			try
			{
				existingArt = this.getHelper().getDaoArticle().queryBuilder().where().eq(Article.URL_FIELD_NAME, a.url)
				.queryForFirst();
			} catch (SQLException e)
			{
				e.printStackTrace();
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
		//check if it was loading from top (new) of from bottom (previous)
		if (pageToLoad == 1)
		{
			//from top
			/////check if there are arts of given category
			try
			{
				//switch by Category or Author				
				if (this.isCategory(categoryToLoad))
				{
					//this is Category, so...
					//get Category id
					int categoryId = Category.getCategoryIdByURL(getHelper(), categoryToLoad);
					//get all ArtCat entries with given Category id
					List<ArtCatTable> artCatEntries = ArtCatTable.getArtCatTableListByCategoryId(getHelper(),
					categoryId);
					//check if there are any arts in category
					if (artCatEntries.size() != 0)
					{
						//TODO
						//if so ...

						//firstly check how many of gained arts are new by calculating how many dismatches with table entries 
						//(match by id, that we gain from Article table by gain Arts by url)
						//
						//if(new=0) there is no new arts, so mark category SINKED and do NO inserts in DB table
						//else if(new<30) set Category SINKED and insert Arts in front of this category entries 
						//so we must increment all arts ids for arts, that have id>=id of 1-st art of this category by "new"
						//else if(new>=30) set Category UNSINKED and insert Arts in front of this category entries 
						//so we must increment all arts ids for arts, that have id>=id of 1-st art of this category by "new"
						//}
						//if not just insert entries (with art id and cat id)
						// and set given category sink to true;
						//{
						//}

						//match url of IS_TOP ArtCatTable with given list and calculate quont of new
						////new=0 =>do noting
						////new<30 => write them to DB with prev/next art URL; change IS_TOP to null and set TRUE to first of given list
						////new>30 => write them to DB with prev/next art URL; change IS_TOP to null and set TRUE to first of given list

						//match url of IS_TOP ArtCatTable with given list and calculate quont of new
						ArtCatTable topArtCat = ArtCatTable.getTopArtCat(getHelper(), categoryId, true);
						String topArtUrl = Article.getArticleUrlById(getHelper(), topArtCat.getArticleId());

						for (int i = 0; i < someResult.size(); i++)
						{
							if (someResult.get(i).url.equals(topArtUrl))
							{
								Category.setCategorySinchronized(getHelper(), categoryToLoad, false);

								//check if there is no new arts
								if (i == 0)
								{
									////new=0 =>do noting
									Intent intent = new Intent(categoryToLoad + "msg");
									intent.putExtra("msg", Msg.NO_NEW);
									LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
								}
								else
								{
									//if not - Toast how many new arts gained
									Log.d(categoryToLoad, "Обнаружено " + i + " новых статей");
									Intent intent = new Intent(categoryToLoad + "msg");
									intent.putExtra("msg", Msg.NEW_QUONT);
									intent.putExtra(Msg.QUONT, i);
									LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);

									////new<30 => write them to DB with prev/next art URL; change IS_TOP to null and set TRUE to first of given list
									List<ArtCatTable> artCatTableList = new ArrayList<ArtCatTable>();

									for (int u = 0; u < i; u++)
									{
										//get Article id by url
										int articleId = Article.getArticleIdByURL(getHelper(), someResult.get(u).url);
										//get next Article url by asking gained from web list
										String nextArtUrl = null;
										try
										{
											nextArtUrl = someResult.get(u + 1).url;
										} catch (Exception e)
										{

										}
										//get previous Article url by asking gained from web list
										String previousArtUrl = null;
										try
										{
											previousArtUrl = someResult.get(u - 1).url;
										} catch (Exception e)
										{

										}
										ArtCatTable tr = new ArtCatTable(null, articleId, categoryId, nextArtUrl,
										previousArtUrl);
										artCatTableList.add(tr);
									}
									//update isTop to null for old entry
									//									ArtCatTable.updateIsTop(getHelper(), topArtCat, null);
									ArtCatTable.updateIsTop(getHelper(), topArtCat.getId(), null);
									//update previous url for old TOP_ART
									String nextArtUrlOfLastInList = artCatTableList.get(artCatTableList.size() - 1)
									.getNextArtUrl();
									//									ArtCatTable.updatePreviousArt(getHelper(), topArtCat, nextArtUrlOfLastInList);
									ArtCatTable.updatePreviousArt(getHelper(), topArtCat.getId(),
									nextArtUrlOfLastInList);
									//set new TOP_ART for first of given from web
									artCatTableList.get(0).isTop(true);
									//FINALLY write new entries to ArtCatTable
									for (ArtCatTable a : artCatTableList)
									{
										this.getHelper().getDaoArtCatTable().create(a);
									}
								}
								//break loop on matching
								break;
							}
							else
							{
								//check if it's last iteration
								if (i == someResult.size() - 1)
								{
									//no matches, so mark Category unsinked and write new artCat entries to db
									Log.d(categoryToLoad,
									"no matches, so mark Category unsinked and write new artCat entries to db");

									Category.setCategorySinchronized(getHelper(), categoryToLoad, false);

									////new>30 => write them to DB with prev/next art URL; change IS_TOP to null and set TRUE to first of given list
									List<ArtCatTable> artCatTableList = new ArrayList<ArtCatTable>();

									for (int u = 0; u < i; u++)
									{
										//get Article id by url
										int articleId = Article.getArticleIdByURL(getHelper(), someResult.get(u).url);
										//get next Article url by asking gained from web list
										String nextArtUrl = null;
										try
										{
											nextArtUrl = someResult.get(u + 1).url;
										} catch (Exception e)
										{

										}
										//get previous Article url by asking gained from web list
										String previousArtUrl = null;
										try
										{
											previousArtUrl = someResult.get(u - 1).url;
										} catch (Exception e)
										{

										}
										ArtCatTable tr = new ArtCatTable(null, articleId, categoryId, nextArtUrl,
										previousArtUrl);
										artCatTableList.add(tr);
									}
									//update isTop to null for old entry
									//									ArtCatTable.updateIsTop(getHelper(), topArtCat, null);
									ArtCatTable.updateIsTop(getHelper(), topArtCat.getId(), null);
									//set new TOP_ART for first of given from web
									artCatTableList.get(0).isTop(true);
									//FINALLY write new entries to ArtCatTable
									for (ArtCatTable a : artCatTableList)
									{
										this.getHelper().getDaoArtCatTable().create(a);
									}
								}
							}
						}
					}
					else
					{
						//there are no arts of given category in ArtCatTable, so just write them!
						//I mean write all arts from someResult List<ArtInfo>, that we gained from web

						//List<ArtCatTable> artCatTableList of new arts that will be written to DB
						List<ArtCatTable> artCatTableList = new ArrayList<ArtCatTable>();

						for (int u = 0; u < someResult.size(); u++)
						{
							//get Article id by url
							int articleId = Article.getArticleIdByURL(getHelper(), someResult.get(u).url);
							//get next Article url by asking gained from web list
							String nextArtUrl = null;
							try
							{
								nextArtUrl = someResult.get(u + 1).url;
							} catch (Exception e)
							{

							}
							//get previous Article url by asking gained from web list
							String previousArtUrl = null;
							try
							{
								previousArtUrl = someResult.get(u - 1).url;
							} catch (Exception e)
							{

							}
							//we do not set ID manually, cause it's initial arts of given category, so we do not need to specify it
							artCatTableList
							.add(new ArtCatTable(null, articleId, categoryId, nextArtUrl, previousArtUrl));
						}
						//So here we have list<T> with new Arts...			
						//set IS_TOP to true for first in list
						artCatTableList.get(0).isTop(true);
						//FINALLY write new entries with new Arts to ArtCatTable
						for (ArtCatTable a : artCatTableList)
						{
							this.getHelper().getDaoArtCatTable().create(a);
						}
					}

				}
				else
				{
					//TODO this is Author, so...
					//					artsIds=this.getHelper().getDaoArtAutTable()
				}

			} catch (SQLException e)
			{

			}

		}
		else
		{
			//from bottom
			//so check how many matches by id in ArtCat(ArtAut) and insert AFTER last category article
		}

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

	public class Msg
	{
		public final static String MSG = "msg";

		public final static String DB_ANSWER_SQLEXCEPTION_CAT = "Ошибка чтения Базы Данных, КАТЕГОРИЯ";
		public final static String DB_ANSWER_SQLEXCEPTION_AUTHOR = "Ошибка чтения Базы Данных, АВТОР";
		public final static String DB_ANSWER_SQLEXCEPTION_ARTS = "Ошибка чтения Базы Данных, СТАТЬЯ";
		public final static String DB_ANSWER_SQLEXCEPTION_ARTCAT = "Ошибка чтения Базы Данных, СТАТЬЯ_КАТЕГОРИЯ";

		public final static String NO_NEW = "no new";
		public final static String QUONT = "quont";
		public final static String NEW_QUONT = "new quont";
	}

}

//	//get first 30 (max num of arts on page) category's arts ids
//	for (int i = 0; i < 30 && i < artCatEntries.size(); i++)
//	{
//		ArtCatTable a = artCatEntries.get(i);
//		artsIds.add(a.getArticleId());
//	}
//	//now get first 30 (max num of arts on page) Article objects of category by id
//	//TODO I think we do NOT need more then 1-st Article... We'll match only with first!
//	for (int id : artsIds)
//	{
//		catArtsList.add(this.getHelper().getDaoArticle().queryForId(id));
//	}
//	//and match their urls with loaded art's urls
//	for (int i = 0; i < someResult.size(); i++)
//	{
//		if (someResult.get(i).url.equals(catArtsList.get(0).getUrl()))
//		{
//			// matched! So we can mark category as sinked
//			Category.setCategorySinchronized(getHelper(), categoryToLoad, true);
//
//			//check if there is no new arts (if(i==0))
//			if (i == 0)
//			{
//				//if so - Toast it and finish operation
//				Intent intent = new Intent(categoryToLoad + "msg");
//				intent.putExtra("msg", Msg.NO_NEW);
//				LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
//			}
//			else
//			{
//				//if not - Toast how many new arts gained
//				Log.d(categoryToLoad, "Обнаружено " + i + " новых статей");
//				Intent intent = new Intent(categoryToLoad + "msg");
//				intent.putExtra("msg", Msg.NEW_QUONT);
//				intent.putExtra(Msg.QUONT, i);
//				LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
//				//and write not matched arts in ArtCatTable in front of category arts
//				//i.e. i=5, so we have 5 new arts (0,1,2,3,4)
//				//so we must get their id's from Article and create new ArtCatTable obj
//				//and write them to db
//				//List<ArtCatTable> artCatTableList of new arts and all other, that will be written to DB
//				List<ArtCatTable> artCatTableList = new ArrayList<ArtCatTable>();
//
//				for (int u = 0; u < i; u++)
//				{
//					//get Article id by url
//					int articleId = Article.getArticleIdByURL(getHelper(), someResult.get(u).url);
//					//get next Article url by asking gained from web list
//					String nextArtUrl = null;
//					try
//					{
//						nextArtUrl = someResult.get(u + 1).url;
//					} catch (Exception e)
//					{
//
//					}
//					//get previous Article url by asking gained from web list
//					String previousArtUrl = null;
//					try
//					{
//						previousArtUrl = someResult.get(u - 1).url;
//					} catch (Exception e)
//					{
//
//					}
//					//now calculate id for new entry
//					//here we must gain first ActCatTable entry id for given category
//					//because it's loading from top
//					int id = ArtCatTable.getIdForFirstArticleInCategory(getHelper(), categoryId)
//					+ u;
//					artCatTableList.add(new ArtCatTable(id, articleId, categoryId, nextArtUrl,
//					previousArtUrl));
//				}
//				//So here we have list<T> with new Arts...
//				//then find other arts from firstId to the end...
//				List<ArtCatTable> artCatTableListFromGivenId = ArtCatTable
//				.getArtCatTableListByCategoryIdFromFirstId(getHelper(), categoryId);
//				//and change their id's (increment them by new arts quont (size of existed list))...
//				for (ArtCatTable a : artCatTableListFromGivenId)
//				{
//					a.setId(a.getId() + artCatTableList.size());
//				}
//				//and set previous Article of matched Article
//				int lastNewArtId = artCatTableList.get(artCatTableList.size() - 1).getArticleId();
//				String lastNewArtUrl = Article.getArticleUrlById(getHelper(), lastNewArtId);
//				artCatTableListFromGivenId.get(0).setPreviousArtUrl(lastNewArtUrl);
//				//and add them to list
//				artCatTableList.addAll(artCatTableListFromGivenId);
//
//				//and now we must delete all entries from firstId of category and write our list to db
//				ArtCatTable.deleteEntriesFromGivenIdToEnd(getHelper(), artCatTableList.get(0)
//				.getId());
//
//				//FINALLY write new enrties with updated ids and new Arts to ArtCatTable
//				for (ArtCatTable a : artCatTableList)
//				{
//					this.getHelper().getDaoArtCatTable().create(a);
//				}
//			}
//			//break loop on matching
//			break;
//		}
//		else
//		{
//			//check if it's last iteration
//			if (i == someResult.size() - 1)
//			{
//				//no matches, so mark Category unsinked and write new artCat entries to db in front of other entries
//				Log.d(categoryToLoad,
//				"no matches, so mark Category unsinked and write new artCat entries to db");
//
//				Category.setCategorySinchronized(getHelper(), categoryToLoad, false);
//
//				//create ArtCatTable objects with id>=first found entry of given category
//				List<ArtCatTable> artCatTableList = new ArrayList<ArtCatTable>();
//				for (int u = 0; u < someResult.size(); u++)
//				{
//					//get Article id by url
//					int articleId = Article.getArticleIdByURL(getHelper(), someResult.get(u).url);
//					//get next Article url by asking gained from web list
//					String nextArtUrl = null;
//					try
//					{
//						nextArtUrl = someResult.get(u + 1).url;
//					} catch (Exception e)
//					{
//
//					}
//					//get previous Article url by asking gained from web list
//					String previousArtUrl = null;
//					try
//					{
//						previousArtUrl = someResult.get(u - 1).url;
//					} catch (Exception e)
//					{
//
//					}
//					//now calculate id for new entry
//					//here we must gain first ActCatTable entry id for given category
//					//because it's loading from top
//					int id = ArtCatTable.getIdForFirstArticleInCategory(getHelper(), categoryId)
//					+ u;
//					artCatTableList.add(new ArtCatTable(id, articleId, categoryId, nextArtUrl,
//					previousArtUrl));
//				}
//				//add to their list all entries, that have id>=first found entry of given category,
//				//with incrementing their ID's by num of new entries
//				//find arts from firstId to the end...
//				List<ArtCatTable> artCatTableListFromFirstIdOfCategory = ArtCatTable
//				.getArtCatTableListByCategoryIdFromFirstId(getHelper(), categoryId);
//				//and change their id's (increment them by new arts quont (size of existed list))...
//				for (ArtCatTable a : artCatTableListFromFirstIdOfCategory)
//				{
//					a.setId(a.getId() + artCatTableList.size());
//				}
//				//and add them to list
//				artCatTableList.addAll(artCatTableListFromFirstIdOfCategory);
//				////
//				//delete all entries, that have id>=first found entry of given category
//				ArtCatTable.deleteEntriesFromGivenIdToEnd(getHelper(), artCatTableList.get(0)
//				.getId());
//				//finally write full list to db
//				for (ArtCatTable a : artCatTableList)
//				{
//					this.getHelper().getDaoArtCatTable().create(a);
//				}
//			}
//		}
//	}
