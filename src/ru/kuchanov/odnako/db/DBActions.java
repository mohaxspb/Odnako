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

import com.j256.ormlite.stmt.UpdateBuilder;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Class with actions with DB, such as writing arts, sending it to listener and
 * so on
 */
public class DBActions
{
	//tag:^(?!dalvikvm) tag:^(?!libEGL) tag:^(?!Open) tag:^(?!Google) tag:^(?!resour) tag:^(?!Chore) tag:^(?!EGL) tag:^(?!SocketStream)

	final private static String LOG_TAG = DBActions.class.getSimpleName();

	final public static String DB_ANSWER_NEVER_REFRESHED = "never refreshed";
	final public static String DB_ANSWER_REFRESH_BY_PERIOD = "refresh by period";
	final public static String DB_ANSWER_INFO_SENDED_TO_FRAG = "we have already send info from DB to frag";
	final public static String DB_ANSWER_NO_ENTRY_OF_ARTS = "no_entry_in_db";
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
	 * Searches trough DB for art, that can be send to caller, or starts
	 * downloading from web if there are no needed arts or no so many as caller
	 * wish or it's time to refresh DB
	 * 
	 * @param categoryToLoad
	 * @param cal
	 *            holder of timeStamp, that is matched to refreshing period
	 * @param pageToLoad
	 *            to switch between from top/bottom loading
	 * @return answer, that is used to switch between next actions (loading from
	 *         web/ get data from DB or some SQLException)
	 */
	public String askDBFromTop(String categoryToLoad, Calendar cal, int pageToLoad)
	{
		//try to find db entry for given catToLoad
		//first in Category
		if (Category.isCategory(this.getHelper(), categoryToLoad))
		{
			Category cat = Category.getCategoryByURL(getHelper(), categoryToLoad);
			//first try to know when was last sink
			long lastRefreshedMills = cat.getRefreshed().getTime();
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
				//				long refreshed = cat.getRefreshed().getTime();
				int givenMinutes = (int) (cal.getTimeInMillis() / millsInMinute);
				int refreshedMinutes = (int) (lastRefreshedMills / millsInMinute);
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
			int categoryId = cat.getId();
			if (ArtCatTable.categoryArtsExists(getHelper(), categoryId))
			{
				//so there is some arts in DB by category, that we can send to frag and show
				List<ArtCatTable> dataFromDBToSend = ArtCatTable.getListFromTop(getHelper(), categoryId, pageToLoad);
				ArrayList<ArtInfo> data = ArtCatTable.getArtInfoListFromArtCatList(getHelper(), dataFromDBToSend);

				//send directly, cause it's from DB and we do not need to do something with this data
				Intent intent = new Intent(categoryToLoad);
				intent.putParcelableArrayListExtra(ArtInfo.KEY_ALL_ART_INFO, data);

				LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);

				return DB_ANSWER_INFO_SENDED_TO_FRAG;
			}
			else
			{
				//there are no arts of given category in DB, so start to load it
				return DB_ANSWER_NO_ENTRY_OF_ARTS;
			}
		}
		else
		{
			//this is Author
			Author aut = Author.getAuthorByURL(getHelper(), Author.getURLwithoutSlashAtTheEnd(categoryToLoad));

			if (aut != null)
			{
				//first try to know when was last sink
				long lastRefreshedMills = aut.getRefreshed().getTime();
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
					int givenMinutes = (int) (cal.getTimeInMillis() / millsInMinute);
					int refreshedMinutes = (int) (lastRefreshedMills / millsInMinute);
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

				int authorId = aut.getId();
				if (ArtAutTable.authorArtsExists(getHelper(), authorId))
				{
					//so there is some arts in DB by category, that we can send to frag and show
					List<ArtAutTable> dataFromDBToSend = ArtAutTable.getListFromTop(getHelper(), authorId, pageToLoad);
					ArrayList<ArtInfo> data = ArtAutTable.getArtInfoListFromArtAutList(getHelper(), dataFromDBToSend);

					//send directly, cause it's from DB and we do not need to do something with this data
					Intent intent = new Intent(categoryToLoad);
					intent.putParcelableArrayListExtra(ArtInfo.KEY_ALL_ART_INFO, data);

					LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);

					return DB_ANSWER_INFO_SENDED_TO_FRAG;
				}
				else
				{
					//there are no arts of given category in DB, so start to load it
					return DB_ANSWER_NO_ENTRY_OF_ARTS;
				}
			}
			else
			{
				Log.e(LOG_TAG, "It isn't category and aut=null, so it's unknown category");
				return DB_ANSWER_UNKNOWN_CATEGORY;
			}
		}
	}

	public String askDBFromBottom(String categoryToLoad, int pageToLoad)
	{
		//TODO switch by category or author

		////aks db for arts
		int categoryId = Category.getCategoryIdByURL(getHelper(), categoryToLoad);
		List<ArtCatTable> allArtsFromFirst;
		//pageToLoad-1 because here we do not need next arts, only arts, that already showed
		allArtsFromFirst = ArtCatTable.getListFromTop(getHelper(), categoryId, pageToLoad - 1);

		//firstly, if we have <30 arts from top, there is initial art in this list, so we must DO NOTHING!
		if (allArtsFromFirst.size() < 30)
		{
			//DO NOTHING, return, that it's start of list
			return Msg.DB_ANSWER_FROM_BOTTOM_LESS_THEN_30_FROM_TOP;
		}
		//so now we have first 30*pageToLoad-1 arts. Now get next 30 by passing last id to same method
		List<ArtCatTable> allArts;
		int lastId = allArtsFromFirst.get(allArtsFromFirst.size() - 1).getId();
		allArts = ArtCatTable.getArtCatTableListByCategoryIdFromGivenId(getHelper(), categoryId, lastId, false);

		//if we have no arts, we check if last shown art isVeryBottomOfCategory
		//we can check it both by URL or isTop value of ArtCat
		//if so we do nothing
		//else we load them from web
		if (allArts == null)
		{
			if (allArtsFromFirst.get(allArtsFromFirst.size() - 1).isTop() == false)
			{
				return Msg.DB_ANSWER_FROM_BOTTOM_INITIAL_ART_ALREADY_SHOWN;
			}
			else
			{
				//load from web
				Log.d(LOG_TAG, "No arts at all, load from web");
				//			this.startDownLoad(categoryToLoad, pageToLoad);
				return Msg.DB_ANSWER_FROM_BOTTOM_NO_ARTS_AT_ALL;
			}
		}
		else
		{
			if (allArts.size() == 30)
			{
				//////if we have 30, so we pass 30 to fragment
				Log.d(LOG_TAG, "we have 30, so we pass 30 to fragment");

				//set ArtCatTable objects to ArtInfo
				ArrayList<ArtInfo> data = ArtCatTable.getArtInfoListFromArtCatList(getHelper(), allArts);
				//send directly, cause it's from DB and we do not need to do something with this data
				Intent intentWithData = new Intent(categoryToLoad);
				intentWithData.putParcelableArrayListExtra(ArtInfo.KEY_ALL_ART_INFO, data);

				LocalBroadcastManager.getInstance(this.ctx).sendBroadcast(intentWithData);
				return Msg.DB_ANSWER_FROM_BOTTOM_INFO_SENDED_TO_FRAG;
			}
			else
			{
				//////else we ask category if it has firstArtURL
				String firstArtInCatURL = null;
				firstArtInCatURL = Category.getFirstArticleURLById(getHelper(), categoryId);
				if (firstArtInCatURL == null)
				{
					////////if so we load from web
					Log.d(LOG_TAG, "we have LESS than 30, but no initial art. So load from web");
					return Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_NO_INITIAL;
					//					this.startDownLoad(categoryToLoad, pageToLoad);
					//TODO if we get <30 we set last art's URL as first art of Category
					//and write arts to db(Article and ArtCat)
					////else simply write arts to db(Article and ArtCat)
				}
				else
				{
					//check matching last of gained URL with initial (first)
					String lastInListArtsUrl = Article.getArticleUrlById(getHelper(),
					allArts.get(allArts.size() - 1).getArticleId());
					if (firstArtInCatURL.equals(lastInListArtsUrl))
					{
						Log.d(LOG_TAG,
						"we have LESS than 30, and have match to initial. So send all and never load more");
						//so it is real end of all arts in category
						//send arts to frag
						//set ArtCatTable objects to ArtInfo
						ArrayList<ArtInfo> data = ArtCatTable.getArtInfoListFromArtCatList(getHelper(), allArts);
						//send directly, cause it's from DB and we do not need to do something with this data
						Intent intentWithData = new Intent(categoryToLoad);
						intentWithData.putParcelableArrayListExtra(ArtInfo.KEY_ALL_ART_INFO, data);

						LocalBroadcastManager.getInstance(this.ctx).sendBroadcast(intentWithData);
						return Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_HAVE_MATCH_TO_INITIAL;
					}
					else
					{
						////////else we must load arts from web
						Log.d(LOG_TAG, "we have LESS than 30, and have NO match to initial. So load from web");
						return Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_NO_MATCH_TO_INITIAL;
//						this.startDownLoad(categoryToLoad, pageToLoad);
						//TODO if we get <30 we set last art's URL as first art of Category
						//and write arts to db(Article and ArtCat)
						////else simply write arts to db(Article and ArtCat)
					}
				}
			}
		}
	}

	public void writeArtsToDB(ArrayList<ArtInfo> dataFromWeb, String categoryToLoad, int pageToLoad)
	{
		//here we'll write gained arts to Article table
		Article.writeArtInfoToArticleTable(getHelper(), dataFromWeb);

		//and fill ArtCatTable with entries of arts
		//check if it was loading from top (new) of from bottom (previous)
		if (pageToLoad == 1)
		{
			//from top
			/////check if there are arts of given category
			//switch by Category or Author				
			if (Category.isCategory(this.getHelper(), categoryToLoad))
			{
				//this is Category, so...
				//get Category id
				int categoryId = Category.getCategoryIdByURL(getHelper(), categoryToLoad);

				//check if there are any arts in category by searching TOP art
				if (ArtCatTable.categoryArtsExists(getHelper(), categoryId))
				{
					//match url of IS_TOP ArtCatTable with given list and calculate quont of new
					////new=0 => TODO check if there is new inner arts and update ArtCatTable if is;
					////new<30 => write them to DB with prev/next art URL; change IS_TOP to null and set TRUE to first of given list
					////new>30 => write them to DB with prev/next art URL; change IS_TOP to null and set TRUE to first of given list

					//match url of IS_TOP ArtCatTable with given list and calculate quont of new
					ArtCatTable topArtCat = ArtCatTable.getTopArtCat(getHelper(), categoryId, true);
					String topArtUrl = Article.getArticleUrlById(getHelper(), topArtCat.getArticleId());

					for (int i = 0; i < dataFromWeb.size(); i++)
					{
						if (dataFromWeb.get(i).url.equals(topArtUrl))
						{
							//check if there is no new arts
							if (i == 0)
							{
								////new=0 =>do noting
								//TODO here we can check if there were new art between 1st
								//and last (publishing on site lag) and if so delete artCat in DB and replace them
								//with loaded from web and update articles order
								//need to think abouts logic of all this shit)
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

								////new<30 => write them to DB with prev/next art URL; change IS_TOP to null for old Top Art
								//and set isTop to TRUE to first of loaded list
								List<ArtCatTable> artCatDataToWrite = ArtCatTable.getArtCatListFromArtInfoList(
								this.getHelper(), dataFromWeb, categoryId);
								//update isTop to null for old entry
								ArtCatTable.updateIsTop(getHelper(), topArtCat.getId(), null);
								//update previous url for old TOP_ART
								String nextArtUrlOfLastInList = artCatDataToWrite.get(artCatDataToWrite.size() - 1)
								.getNextArtUrl();
								ArtCatTable.updatePreviousArt(getHelper(), topArtCat.getId(), nextArtUrlOfLastInList);
								//set new TOP_ART for first of given from web
								artCatDataToWrite.get(0).isTop(true);
								//FINALLY write new entries to ArtCatTable
								ArtCatTable.write(getHelper(), artCatDataToWrite);
							}
							//break loop on matching
							break;
						}
						else
						{
							//check if it's last iteration
							if (i == dataFromWeb.size() - 1)
							{
								Log.d(categoryToLoad,
								"no matches, so write new artCat entries to db and update TOP art");
								////new>30 => write them to DB with prev/next art URL; change IS_TOP to null and
								//set TRUE to first of given list
								List<ArtCatTable> artCatDataToWrite = ArtCatTable.getArtCatListFromArtInfoList(
								this.getHelper(), dataFromWeb, categoryId);
								//update isTop to null for old entry
								ArtCatTable.updateIsTop(getHelper(), topArtCat.getId(), null);
								//set new TOP_ART for first of given from web
								artCatDataToWrite.get(0).isTop(true);
								//FINALLY write new entries to ArtCatTable
								ArtCatTable.write(getHelper(), artCatDataToWrite);
							}
						}
					}
				}
				else
				{
					//there are no arts of given category in ArtCatTable, so just write them!
					//I mean write all arts from someResult List<ArtInfo>, that we gained from web

					//List<ArtCatTable> artCatTableList of new arts that will be written to DB
					List<ArtCatTable> artCatDataToWrite = ArtCatTable.getArtCatListFromArtInfoList(this.getHelper(),
					dataFromWeb, categoryId);
					//set IS_TOP to true for first in list
					artCatDataToWrite.get(0).isTop(true);
					//FINALLY write new entries with new Arts to ArtCatTable
					ArtCatTable.write(getHelper(), artCatDataToWrite);
				}
			}
			else
			{
				//TODO this is Author, so...
			}
		}
		else
		{
			//from bottom
			if (Category.isCategory(this.getHelper(), categoryToLoad))
			{
				//XXX TODO if someResult<30, we can write that we have first art!!!
				//this.isCategory, so...
				//here we can have some variants:
				//1) we load as we have no next arts
				//2) we load as we have less than 30 arts
				//anyway we must find our previous last artCat and change its nextArtUrl
				int categoryId = Category.getCategoryIdByURL(getHelper(), categoryToLoad);
				ArtCatTable lastArtCat = null;
				List<ArtCatTable> allArtCatList = ArtCatTable.getListFromTop(getHelper(), categoryId, pageToLoad - 1);
				lastArtCat = allArtCatList.get(allArtCatList.size() - 1);
				//TODO check here situation, when publishing new art on site during our request
				//so we can recive first art in someResult same as last in DB
				//so we'll have to update lastInDBArt by real next.
				ArtCatTable.updateNextArt(getHelper(), lastArtCat.getId(), dataFromWeb.get(0).url);
				//then we must match each art with all artCat, that have no previousArtUrl
				//get list of all artCat without previous art
				List<ArtCatTable> withoutPrev = ArtCatTable.getAllRowsWithoutPrevArt(this.getHelper(), categoryId);
				if (withoutPrev != null)
				{
					//XXX WARNING!!! "someResult.size()-1" because there is no nextArt for last, so we can't check matching
					for (int i = 0; i < dataFromWeb.size() - 1; i++)
					{
						for (int u = 0; u < withoutPrev.size(); u++)
						{
							//get url of checking ArtCat entry
							String url = Article.getArticleUrlById(getHelper(), withoutPrev.get(u).getArticleId());
							if (dataFromWeb.get(i + 1).url.equals(url))
							{
								//matched! So we write only previous of matched (+matched)
								//and update entry, that matched, by previousArtUrl
								List<ArtInfo> subListArtInfo = dataFromWeb.subList(0, i);
								List<ArtCatTable> dataToWrite = ArtCatTable.getArtCatListFromArtInfoList(
								this.getHelper(), subListArtInfo, categoryId);
								//set prevArtUrl for first entry
								String prevArtUrl = Article.getArticleUrlById(getHelper(), lastArtCat.getArticleId());
								dataToWrite.get(0).setPreviousArtUrl(prevArtUrl);
								//update previousArtUrl for matched ArtCat row
								int id = withoutPrev.get(u).getId();
								String prevArtUrlToUpdate = dataFromWeb.get(i).url;
								ArtCatTable.updatePreviousArt(getHelper(), id, prevArtUrlToUpdate);
								//write new entries with new Arts to ArtCatTable
								ArtCatTable.write(getHelper(), dataToWrite);
							}
							else
							{
								//check if it's last iteration and so we didn't find any matches
								if (i == dataFromWeb.size() - 1 - 1 && u == withoutPrev.size() - 1)
								{
									//if we can't find any, we simply write all artCats
									List<ArtCatTable> dataToWrite = ArtCatTable.getArtCatListFromArtInfoList(
									this.getHelper(), dataFromWeb, categoryId);
									//set prevArtUrl for first entry
									String prevArtUrl = Article.getArticleUrlById(getHelper(),
									lastArtCat.getArticleId());
									dataToWrite.get(0).setPreviousArtUrl(prevArtUrl);
									//write new entries with new Arts to ArtCatTable
									ArtCatTable.write(getHelper(), dataToWrite);
								}
							}
						}
					}
				}
				else
				{
					//there are no arts without missing prev art, so
					//just write them all!!!11 ARGHHH!!!
					List<ArtCatTable> dataToWrite = ArtCatTable.getArtCatListFromArtInfoList(this.getHelper(),
					dataFromWeb, categoryId);
					//set prevArtUrl for first entry
					String prevArtUrl = Article.getArticleUrlById(getHelper(), lastArtCat.getArticleId());
					dataToWrite.get(0).setPreviousArtUrl(prevArtUrl);
					//write new entries with new Arts to ArtCatTable
					ArtCatTable.write(getHelper(), dataToWrite);
				}
			}
			else
			{
				//TODO this.isAuthor, so...
			}
			//so check how many matches by id in ArtCat(ArtAut) and insert AFTER last category article
		}
	}

	//write refreshed date to entry of given category
	public void updateRefreshedDate(String categoryToLoad)
	{
		if (Category.isCategory(this.getHelper(), categoryToLoad))
		{
			try
			{
				UpdateBuilder<Category, Integer> updateBuilder = this.getHelper().getDaoCategory().updateBuilder();
				updateBuilder.updateColumnValue(Category.REFRESHED_FIELD_NAME, new Date(System.currentTimeMillis()));
				updateBuilder.where().eq(Category.URL_FIELD_NAME, categoryToLoad);
				updateBuilder.update();
			} catch (SQLException e)
			{
				Log.e(LOG_TAG, "SQLException updateRefreshedDate CATEGORY");
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
				Log.e(LOG_TAG, "SQLException updateRefreshedDate AUTHOR");
			}
		}
	}

	public class Msg
	{
		public static final String DB_ANSWER_FROM_BOTTOM_LESS_30_HAVE_MATCH_TO_INITIAL = "we have LESS than 30, and have match to initial";
		public static final String DB_ANSWER_FROM_BOTTOM_LESS_30_NO_MATCH_TO_INITIAL = "we have LESS than 30, and have NO match to initial";
		public static final String DB_ANSWER_FROM_BOTTOM_INITIAL_ART_ALREADY_SHOWN = "initial art is already shown, so we must do nothing";
		public static final String DB_ANSWER_FROM_BOTTOM_LESS_30_NO_INITIAL = "we have LESS than 30, but no initial art";
		public static final String DB_ANSWER_FROM_BOTTOM_INFO_SENDED_TO_FRAG = "we have already send bottom info from DB to frag";
		public static final String DB_ANSWER_FROM_BOTTOM_NO_ARTS_AT_ALL = "no arts at all";

		public final static String MSG = "msg";

		public final static String NO_NEW = "no new";
		public final static String QUONT = "quont";
		public final static String NEW_QUONT = "new quont";

		public final static String DB_ANSWER_FROM_BOTTOM_LESS_THEN_30_FROM_TOP = "less then 30 from top";
	}
}