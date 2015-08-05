/*
 05.04.2015
AskDBFromTop.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.stmt.UpdateBuilder;

import ru.kuchanov.odnako.callbacks.CallbackWriteFromTop;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncTaskWriteFromTop extends AsyncTask<Void, Void, String[]>
{
	final static String LOG = AsyncTaskWriteFromTop.class.getSimpleName();

	private DataBaseHelper h;
	private String categoryToLoad;
	private int pageToLoad;
	private CallbackWriteFromTop callback;
	ArrayList<Article> dataFromWeb;

	public AsyncTaskWriteFromTop(DataBaseHelper dataBaseHelper, ArrayList<Article> dataFromWeb,
	String categoryToLoad, int pageToLoad, CallbackWriteFromTop callback)
	{
		this.h = dataBaseHelper;
		this.categoryToLoad = categoryToLoad;
		this.pageToLoad = pageToLoad;
		this.dataFromWeb = dataFromWeb;
		this.callback = callback;
	}

	protected String[] doInBackground(Void... args)
	{
		String[] result;
		//check if there are arts of given category
		//switch by Category or Author				
		if (Category.isCategory(h, categoryToLoad))
		{
			//this is Category, so...
			//get Category id
			int categoryId = Category.getCategoryIdByURL(h, categoryToLoad);

			//check if there are any arts in category by searching TOP art
			if (ArtCatTable.categoryArtsExists(h, categoryId))
			{
				//match url of IS_TOP ArtCatTable with given list and calculate quont of new
				////new=0 => check if there is new inner arts and update ArtCatTable if is;
				////new<30 => write them to DB with prev/next art URL; change IS_TOP to null and set TRUE to first of given list
				////new>30 => write them to DB with prev/next art URL; change IS_TOP to null and set TRUE to first of given list

				//match url of IS_TOP ArtCatTable with given list and calculate quont of new
				ArtCatTable topArtCat = ArtCatTable.getTopArtCat(h, categoryId, true);
				String topArtUrl = Article.getArticleUrlById(h, topArtCat.getArticleId());

				for (int i = 0; i < dataFromWeb.size(); i++)
				{
					if (dataFromWeb.get(i).getUrl().equals(topArtUrl))
					{
						//check if there is no new arts
						if (i == 0)
						{
							//here we can check if there were new art between 1st
							//and last (publishing on site lag) and if so delete artCat in DB and replace them
							//with loaded from web and update articles order
							//so...
							//we can detect changes by matching last arts URL of first 30 arts from top
							//and, of course their quont must be equal (in case of <30 arts at all
							List<ArtCatTable> first30FromTop = ArtCatTable.getListFromTop(h, categoryId,
							1);

							String lastInFirst30Url = Article.getArticleUrlById(h,
							first30FromTop.get(first30FromTop.size() - 1).getArticleId());

							if ((dataFromWeb.size() == first30FromTop.size()) &&
							(dataFromWeb.get(dataFromWeb.size() - 1).getUrl().equals(lastInFirst30Url)))
							{
								//if so lists of loaded from web and stored in DB equals
								//do nothing, send result as NO_NEW
								/* return */result = new String[] { Msg.NO_NEW, null };
							}
							else
							{
								//we have some changes, so we must calculate quont of arts to delete
								//and change them to new
								//we'll delete from top to def quont on page - new qount
								//write gained, set nextArt of it's last to def quont on page - new qount URL
								//and update def quont on page - new qount URL by last of gained
								//SO CALCULATE QUONT OF NEW BY MATCHING ALL TO ALL
								int newQuont = 0;
								for (int u = 0; u < dataFromWeb.size(); u++)
								{
									String loadedArtsURL = dataFromWeb.get(u).getUrl();
									boolean notMatched = false;
									for (int y = 0; y < first30FromTop.size() && notMatched == false; y++)
									{
										int artCatArtsID = first30FromTop.get(y).getArticleId();
										String DBArtsURL = Article.getArticleUrlById(h, artCatArtsID);
										if (loadedArtsURL.equals(DBArtsURL))
										{
											//matched, so everything is OK, close this loop
											notMatched = true;
										}
										else
										{
											//check if it's last iteration
											if (y == first30FromTop.size() - 1)
											{
												//it's last iteration and we have no match
												//so it's really new art!
												//so increment newQuont value
												newQuont++;
											}
										}
									}
								}//calculating newQount
									//before deleting, that may cause changing of list we get nextArtUrl for new rows
									//TODO ErrOR hERE
								String nextArtUrl = Article.getArticleUrlById(h,
								first30FromTop.get(first30FromTop.size() - newQuont - 1).getArticleId());
								//also before deleting get id of new firstArtOfNextPage to update
								//it's prevArtsUrl
								int firstArtOfNextPageId = first30FromTop.get(first30FromTop.size() - newQuont - 1)
								.getId();
								//THEN delete ArtCatTable rows
								ArtCatTable.delete(h,
								first30FromTop.subList(0, first30FromTop.size() - newQuont));
								//create ArtCat list from ArtInfo
								List<ArtCatTable> dataToWrite = ArtCatTable.getArtCatListFromArticleList(
								h, dataFromWeb, categoryId);
								//set nextArt of it's last to def quont on page - new qount URL
								dataToWrite.get(dataToWrite.size() - 1).setNextArtUrl(nextArtUrl);
								//and update def quont on page - new qount prev URL by last of gained
								ArtCatTable.updatePreviousArt(h, firstArtOfNextPageId,
								dataFromWeb.get(dataFromWeb.size() - 1).getUrl());
								
								//update isTop to null for old entry
								ArtCatTable.updateIsTop(h, topArtCat.getId(), null);
								//set new topArtCatRow
								dataToWrite.get(0).isTop(true);
								//Finally write new rows!
								ArtCatTable.write(h, dataToWrite);

								/* return */result = new String[] { Msg.NEW_QUONT, Integer.toString(newQuont) };
							}
						}
						else
						{
							//TODO here we can also check publishing lag, but fuck it now!
							////new<30 => write them to DB with prev/next art URL; change IS_TOP to null for old Top Art
							//and set isTop to TRUE to first of loaded list
							//We must push not whole list, but only part of it!
							List<ArtCatTable> artCatDataToWrite = ArtCatTable.getArtCatListFromArticleList(
							h, dataFromWeb.subList(0, i), categoryId);
							//update isTop to null for old entry
							ArtCatTable.updateIsTop(h, topArtCat.getId(), null);
							//update previous url for old TOP_ART
							int prevOldTopArtsId = artCatDataToWrite.get(artCatDataToWrite.size() - 1)
							.getArticleId();
							String prevArtUrlOfOldTopArt = Article.getArticleUrlById(h, prevOldTopArtsId);
							ArtCatTable.updatePreviousArt(h, topArtCat.getId(), prevArtUrlOfOldTopArt);
							//set new TOP_ART for first of given from web
							artCatDataToWrite.get(0).isTop(true);
							//setNextArtsURL for last in new arts list, cause we can't do it in ArtCat method
							String nextArtUrl = Article.getArticleUrlById(h, topArtCat.getArticleId());
							artCatDataToWrite.get(artCatDataToWrite.size() - 1).setNextArtUrl(nextArtUrl);
							//FINALLY write new entries to ArtCatTable
							ArtCatTable.write(h, artCatDataToWrite);

							/* return */result = new String[] { Msg.NEW_QUONT, Integer.toString(i) };
						}
					}
					else
					{
						//check if it's last iteration
						if (i == dataFromWeb.size() - 1)
						{
							//new>30 => write them to DB with prev/next art URL; change IS_TOP to null and
							//set TRUE to first of given list
							List<ArtCatTable> artCatDataToWrite = ArtCatTable.getArtCatListFromArticleList(
							h, dataFromWeb, categoryId);
							//update isTop to null for old entry
							ArtCatTable.updateIsTop(h, topArtCat.getId(), null);
							//set new TOP_ART for first of given from web
							artCatDataToWrite.get(0).isTop(true);
							//FINALLY write new entries to ArtCatTable
							ArtCatTable.write(h, artCatDataToWrite);
							/* return */result = new String[] { Msg.DB_ANSWER_WRITE_FROM_TOP_NO_MATCHES, "более 30" };
						}
					}
				}
				/* return */result = new String[] { Msg.ERROR, "Непредвиденная ошибка" };
			}
			else
			{
				//there are no arts of given category in ArtCatTable, so just write them!
				//I mean write all arts from someResult List<ArtInfo>, that we gained from web
				List<ArtCatTable> artCatDataToWrite = ArtCatTable.getArtCatListFromArticleList(h,
				dataFromWeb, categoryId);
				//set IS_TOP to true for first in list
				artCatDataToWrite.get(0).isTop(true);
				//FINALLY write new entries with new Arts to ArtCatTable
				ArtCatTable.write(h, artCatDataToWrite);
				/* return */result = new String[] { Msg.NO_NEW, null };
			}
		}
		else
		{
			//this is Author, so...
			//get Author id
			int authorId = Author.getAuthorIdByURL(h, categoryToLoad);

			//check if there are any arts in category by searching TOP art
			if (ArtAutTable.authorArtsExists(h, authorId))
			{
				//match url of IS_TOP ArtAutTable with given list and calculate quont of new
				////new=0 => check if there is new inner arts and update ArtAutTable if is;
				////new<30 => write them to DB with prev/next art URL; change IS_TOP to null and set TRUE to first of given list
				////new>30 => write them to DB with prev/next art URL; change IS_TOP to null and set TRUE to first of given list

				//match url of IS_TOP ArtAutTable with given list and calculate quont of new
				ArtAutTable topArtAut = ArtAutTable.getTopArt(h, authorId, true);
				String topArtUrl = Article.getArticleUrlById(h, topArtAut.getArticleId());

				for (int i = 0; i < dataFromWeb.size(); i++)
				{
					if (dataFromWeb.get(i).getUrl().equals(topArtUrl))
					{
						//check if there is no new arts
						if (i == 0)
						{
							//here we can check if there were new art between 1st
							//and last (publishing on site lag) and if so delete artAut in DB and replace them
							//with loaded from web and update articles order
							//so...
							//we can detect changes by matching last arts URL of first 30 arts from top
							//and, of course their quont must be equal (in case of <30 arts at all
							List<ArtAutTable> first30FromTop = ArtAutTable.getListFromTop(h, authorId,
							1);
							String lastInFirst30Url = Article.getArticleUrlById(h,
							first30FromTop.get(first30FromTop.size() - 1).getArticleId());
							if (dataFromWeb.size() == first30FromTop.size() &&
							dataFromWeb.get(dataFromWeb.size() - 1).getUrl().equals(lastInFirst30Url))
							{
								//if so lists of loaded from web and stored in DB equals
								//do nothing, send result as NO_NEW
								/* return */result = new String[] { Msg.NO_NEW, null };
							}
							else
							{
								//we have some changes, so we must calculate quont of arts to delete
								//and change them to new
								//we'll delete from top to def quont on page - new qount
								//write gained, set nextArt of it's last to def quont on page - new qount URL
								//and update def quont on page - new qount URL by last of gained
								//SO CALCULATE QUONT OF NEW BY MATCHING ALL TO ALL
								int newQuont = 0;
								for (int u = 0; u < dataFromWeb.size(); u++)
								{
									boolean notMatched = false;
									String loadedArtsURL = dataFromWeb.get(u).getUrl();
									for (int y = 0; y < first30FromTop.size() && notMatched == false; y++)
									{

										int artAutArtsID = first30FromTop.get(y).getArticleId();
										String DBArtsURL = Article.getArticleUrlById(h, artAutArtsID);
										if (loadedArtsURL.equals(DBArtsURL))
										{
											//matched, so everithing is OK, do noting
											notMatched = true;
										}
										else
										{
											//check if it's last iteration
											if (y == first30FromTop.size() - 1)
											{
												//it's last iteration and we have no match
												//so it's really new art!
												//so increment newQuont value
												newQuont++;
											}
										}
									}
								}//calculating newQount
									//before deleting, that may cause changing of list we get nextArtUrl for new rows
								//XXX some problem... fix it with hardcoding erasing data and rewriting it
								if ((first30FromTop.size() - newQuont - 1) < 0)
								{
									//create ArtAut list from ArtInfo
									List<ArtAutTable> dataToWrite = ArtAutTable.getArtAutListFromArtInfoList(
									h, dataFromWeb, authorId);
									//update isTop to null for old entry
									ArtCatTable.updateIsTop(h, topArtAut.getId(), null);
									//set new topArtCatRow
									dataToWrite.get(0).isTop(true);
									//Finally write new rows!
									ArtAutTable.write(h, dataToWrite);

									/* return */result = new String[] { Msg.NEW_QUONT, Integer.toString(newQuont) };
								}
								else
								{
									String nextArtUrl = Article.getArticleUrlById(h,
									first30FromTop.get(first30FromTop.size() - newQuont - 1).getArticleId());
									//also before deleting get id of new firstArtOfNextPage to update
									//it's prevArtsUrl
									int firstArtOfNextPageId = first30FromTop.get(first30FromTop.size() - newQuont - 1)
									.getId();
									//THEN delete ArtAutTable rows
									ArtAutTable.delete(h,
									first30FromTop.subList(0, first30FromTop.size() - newQuont));
									//create ArtAut list from ArtInfo
									List<ArtAutTable> dataToWrite = ArtAutTable.getArtAutListFromArtInfoList(
									h, dataFromWeb, authorId);
									//set nextArt of it's last to def quont on page - new qount URL
									dataToWrite.get(dataToWrite.size() - 1).setNextArtUrl(nextArtUrl);
									//and update def quont on page - new qount prev URL by last of gained
									ArtAutTable.updatePreviousArt(h, firstArtOfNextPageId,
									dataFromWeb.get(dataFromWeb.size() - 1).getUrl());
									
									//update isTop to null for old entry
									ArtCatTable.updateIsTop(h, topArtAut.getId(), null);
									//set new topArtCatRow
									dataToWrite.get(0).isTop(true);
									//Finally write new rows!
									ArtAutTable.write(h, dataToWrite);

									/* return */result = new String[] { Msg.NEW_QUONT, Integer.toString(newQuont) };
								}
							}
						}
						else
						{
							//TODO here we can also check publishing lag, but fuck it now!
							//new<30 => write them to DB with prev/next art URL; change IS_TOP to null for old Top Art
							//and set isTop to TRUE to first of loaded list
							List<ArtAutTable> artAutDataToWrite = ArtAutTable.getArtAutListFromArtInfoList(
							h, dataFromWeb.subList(0, i), authorId);
							//update isTop to null for old entry
							ArtAutTable.updateIsTop(h, topArtAut.getId(), null);
							//update previous url for old TOP_ART
							int prevOldTopArtsId = artAutDataToWrite.get(artAutDataToWrite.size() - 1)
							.getArticleId();
							String prevArtUrlOfOldTopArt = Article.getArticleUrlById(h, prevOldTopArtsId);
							ArtCatTable.updatePreviousArt(h, topArtAut.getId(), prevArtUrlOfOldTopArt);
							//set new TOP_ART for first of given from web
							artAutDataToWrite.get(0).isTop(true);
							//setNextArtsURL for last in new arts list, cause we can't do it in ArtCat method
							String nextArtUrl = Article.getArticleUrlById(h, topArtAut.getArticleId());
							artAutDataToWrite.get(artAutDataToWrite.size() - 1).setNextArtUrl(nextArtUrl);
							//FINALLY write new entries to ArtAutTable
							ArtAutTable.write(h, artAutDataToWrite);

							/* return */result = new String[] { Msg.NEW_QUONT, Integer.toString(i) };
						}
					}
					else
					{
						//check if it's last iteration
						if (i == dataFromWeb.size() - 1)
						{
							//new>30 => write them to DB with prev/next art URL; change IS_TOP to null and
							//set TRUE to first of given list
							List<ArtAutTable> artAutDataToWrite = ArtAutTable.getArtAutListFromArtInfoList(
							h, dataFromWeb, authorId);
							//update isTop to null for old entry
							ArtAutTable.updateIsTop(h, topArtAut.getId(), null);
							//set new TOP_ART for first of given from web
							artAutDataToWrite.get(0).isTop(true);
							//FINALLY write new entries to ArtAutTable
							ArtAutTable.write(h, artAutDataToWrite);
							/* return */result = new String[] { Msg.DB_ANSWER_WRITE_FROM_TOP_NO_MATCHES, "более 30" };
						}
					}
				}
				/* return */result = new String[] { Msg.ERROR, "Непредвиденная ошибка" };
			}
			else
			{
				//there are no arts of given category in ArtAutTable, so just write them!
				//I mean write all arts from someResult List<ArtInfo>, that we gained from web
				List<ArtAutTable> artAutDataToWrite = ArtAutTable.getArtAutListFromArtInfoList(h,
				dataFromWeb, authorId);
				//set IS_TOP to true for first in list
				artAutDataToWrite.get(0).isTop(true);
				//FINALLY write new entries with new Arts to ArtAutTable
				ArtAutTable.write(h, artAutDataToWrite);
				/* return */result = new String[] { Msg.NO_NEW, null };
			}
		}//this is author

		switch (result[0])
		{
			case (Msg.NO_NEW):
			case (Msg.NEW_QUONT):
			case (Msg.DB_ANSWER_WRITE_FROM_TOP_NO_MATCHES):
				if (Category.isCategory(h, categoryToLoad))
				{
					try
					{
						UpdateBuilder<Category, Integer> updateBuilder = h.getDaoCategory().updateBuilder();
						updateBuilder.updateColumnValue(Category.FIELD_REFRESHED, new Date(System.currentTimeMillis()));
						updateBuilder.where().eq(Category.FIELD_URL, categoryToLoad);
						updateBuilder.update();
					} catch (SQLException e)
					{
						Log.e(LOG, "SQLException updateRefreshedDate CATEGORY");
					}
				}
				else
				{
					try
					{
						UpdateBuilder<Author, Integer> updateBuilder = h.getDaoAuthor().updateBuilder();
						updateBuilder.updateColumnValue(Author.FIELD_REFRESHED, new Date(System.currentTimeMillis()));
						//DO NOT forget, that we can receive categoryToLoad with or without "/" at the and!
						updateBuilder.where().eq(Author.FIELD_URL, Author.getURLwithoutSlashAtTheEnd(categoryToLoad));
						updateBuilder.update();
					} catch (SQLException e)
					{
						Log.e(LOG, "SQLException updateRefreshedDate AUTHOR");
					}
				}
			break;
		}
		return result;
	}

	protected void onPostExecute(String[] result)
	{
		this.callback.onDoneWritingFromTop(result, this.dataFromWeb, this.categoryToLoad, this.pageToLoad);
	}
}