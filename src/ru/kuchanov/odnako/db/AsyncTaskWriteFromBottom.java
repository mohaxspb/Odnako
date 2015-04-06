/*
 05.04.2015
AskDBFromTop.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.util.ArrayList;
import java.util.List;

import ru.kuchanov.odnako.callbacks.CallbackWriteFromBottom;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncTaskWriteFromBottom extends AsyncTask<Void, Void, String[]>
{
	final private static String LOG = AsyncTaskWriteFromBottom.class.getSimpleName();

	private DataBaseHelper h;
	private String categoryToLoad;
	private int pageToLoad;
	private CallbackWriteFromBottom callback;
	ArrayList<Article> dataFromWeb;

	public AsyncTaskWriteFromBottom(DataBaseHelper dataBaseHelper, ArrayList<Article> dataFromWeb,
	String categoryToLoad, int pageToLoad, CallbackWriteFromBottom callback)
	{
		this.h = dataBaseHelper;
		this.categoryToLoad = categoryToLoad;
		this.pageToLoad = pageToLoad;
		this.dataFromWeb = dataFromWeb;
		this.callback = callback;
	}

	protected String[] doInBackground(Void... args)
	{
		if (Category.isCategory(h, categoryToLoad))
		{
			//this.isCategory, so...
			//here we can have some variants:
			//1) we load as we have no next arts
			//2) we load as we have less than 30 arts
			//anyway we must find our previous last artCat and change its nextArtUrl
			int categoryId = Category.getCategoryIdByURL(h, categoryToLoad);
			ArtCatTable lastArtCat = null;
			List<ArtCatTable> allArtCatList = ArtCatTable.getListFromTop(h, categoryId, pageToLoad - 1);
			lastArtCat = allArtCatList.get(allArtCatList.size() - 1);

			//check here situation, when publishing new art on site during our request
			//or since we load from top
			//so we can receive first art in someResult same as last in DB

			//firstly find ordered list of BD pageToLoad arts
			List<ArtCatTable> dBarts = ArtCatTable.getArtCatTableListByCategoryIdFromGivenId(h,
			categoryId, lastArtCat.getId(), false);
			if (dBarts != null)
			{
				//check matching loadedData url with first in dBarts
				String firstInDBArtsUrl = Article.getArticleUrlById(h, dBarts.get(0).getArticleId());
				if (dataFromWeb.get(0).getUrl().equals(firstInDBArtsUrl))
				{
					//matched! So there are no lag published arts
					//now we can write new arts after last in DB
					ArtCatTable lastInDB = dBarts.get(dBarts.size() - 1);
					//so we'll have to update lastInDBArt by real next.
					ArtCatTable.updateNextArt(h, lastInDB.getId(), dataFromWeb.get(dBarts.size()).getUrl());

					//then we must match each art with all artCat, that have no previousArtUrl
					//get list of all artCat without previous art
					List<ArtCatTable> withoutPrev = ArtCatTable.getAllRowsWithoutPrevArt(h,
					categoryId);
					//we must match only with new arts, no with all
					List<Article> newArtInfo = dataFromWeb.subList(dBarts.size(), dataFromWeb.size());
					if (withoutPrev != null)
					{
						for (int i = 0; i < newArtInfo.size(); i++)
						{
							for (int u = 0; u < withoutPrev.size(); u++)
							{
								//get url of checking ArtCat entry
								String url = Article.getArticleUrlById(h, withoutPrev.get(u)
								.getArticleId());
								if (newArtInfo.get(i).getUrl().equals(url))
								{
									//matched! So we write only previous of matched (without matched)
									if (i == 0)
									{
										//there are NO really new arts, we only catch separated blocks of arts
										//so we'll only update prevArtUrl for matched withoutPrevArt
										String prevArtUrl = Article.getArticleUrlById(h,
										lastArtCat.getArticleId());
										int id = withoutPrev.get(u).getId();
										ArtCatTable.updatePreviousArt(h, id, prevArtUrl);
									}
									else
									{
										//there ARE new real arts, so write them all!
										//and update entry, that matched, by previousArtUrl
										List<Article> subListArtInfo = newArtInfo.subList(0, i - 1);
										List<ArtCatTable> dataToWrite = ArtCatTable.getArtCatListFromArticleList(
										h, subListArtInfo, categoryId);
										//set prevArtUrl for first entry
										String prevArtUrl = Article.getArticleUrlById(h,
										lastArtCat.getArticleId());
										dataToWrite.get(0).setPreviousArtUrl(prevArtUrl);
										//update previousArtUrl for matched ArtCat row
										int id = withoutPrev.get(u).getId();
										String prevArtUrlToUpdate = subListArtInfo.get(i).getUrl();
										ArtCatTable.updatePreviousArt(h, id, prevArtUrlToUpdate);
										//setNextArtsURL for last in new arts list, cause we can't do it in ArtCat method
										String nextArtUrl = Article.getArticleUrlById(h, withoutPrev.get(u)
										.getArticleId());
										dataToWrite.get(dataToWrite.size() - 1).setNextArtUrl(nextArtUrl);
										//write new entries with new Arts to ArtCatTable
										ArtCatTable.write(h, dataToWrite);
									}
									Log.d(LOG, "done writing artCat to empty previous entry");
								}
								else
								{
									//check if it's last iteration and so we didn't find any matches
									if (i == newArtInfo.size() - 1 - 1 && u == withoutPrev.size() - 1)
									{
										//if we can't find any, we simply write all artCats
										List<ArtCatTable> dataToWrite = ArtCatTable.getArtCatListFromArticleList(
										h, newArtInfo, categoryId);
										//set prevArtUrl for first entry
										String prevArtUrl = Article.getArticleUrlById(h,
										lastArtCat.getArticleId());
										dataToWrite.get(0).setPreviousArtUrl(prevArtUrl);
										//write new entries with new Arts to ArtCatTable
										ArtCatTable.write(h, dataToWrite);

										Log.d(LOG, "done writing artCat. NO match to empty previous entry");
									}
								}
							}
						}
						return new String[] { Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT, null };
					}
					else
					{
						//there are no arts without missing prev art, so
						//just write them all!!!11 ARGHHH!!!
						List<ArtCatTable> dataToWrite = ArtCatTable.getArtCatListFromArticleList(h,
						newArtInfo, categoryId);
						//set prevArtUrl for first entry
						String prevArtUrl = Article.getArticleUrlById(h, lastArtCat.getArticleId());
						dataToWrite.get(0).setPreviousArtUrl(prevArtUrl);
						//write new entries with new Arts to ArtCatTable
						ArtCatTable.write(h, dataToWrite);
						return new String[] { Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT, null };
					}
				}
				else
				{
					//first not matched, so it's publishing lag, so we'll load from top
					//but firstly we'll delete all category arts
					//to prevent hardcoding to solve all posible situations
					List<ArtCatTable> rowsToDelete = ArtCatTable.getAllRowsByCategoryId(h, categoryId);
					ArtCatTable.delete(h, rowsToDelete);
					return new String[] { Msg.DB_ANSWER_WRITE_FROM_BOTTOM_EXCEPTION, null };
				}
			}
			else
			{
				//there are no arts of this pagetoLoad in DB
				//so we can check if gained arts matched with arts on previous page
				//if we have match it's pub lag, so start load from top!
				//else - just write arts to db
				String artsUrlToMatch = dataFromWeb.get(0).getUrl();
				for (int i = 0; i < allArtCatList.size(); i++)
				{
					String prevUrlToMatch = Article.getArticleUrlById(h, allArtCatList.get(i).getArticleId());
					if (artsUrlToMatch.equals(prevUrlToMatch))
					{
						//matched! So it's publishing lag, so start download from top!
						//but firstly we'll delete all category arts
						//to prevent hardcoding to solve all posible situations
						List<ArtCatTable> rowsToDelete = ArtCatTable.getAllRowsByCategoryId(h, categoryId);
						ArtCatTable.delete(h, rowsToDelete);
						return new String[] { Msg.DB_ANSWER_WRITE_FROM_BOTTOM_EXCEPTION, null };
					}
				}
				//TODO this is same as previous block. Refactor it!
				ArtCatTable lastInDB = allArtCatList.get(allArtCatList.size() - 1);
				//so we'll have to update lastInDBArt by real next.
				ArtCatTable.updateNextArt(h, lastInDB.getId(), dataFromWeb.get(0).getUrl());

				//then we must match each art with all artCat, that have no previousArtUrl
				//get list of all artCat without previous art
				List<ArtCatTable> withoutPrev = ArtCatTable.getAllRowsWithoutPrevArt(h,
				categoryId);
				//we must match only with new arts, not with all
				List<Article> newArtInfo = dataFromWeb;
				if (withoutPrev != null)
				{
					for (int i = 0; i < newArtInfo.size(); i++)
					{
						for (int u = 0; u < withoutPrev.size(); u++)
						{
							//get url of checking ArtCat entry
							String url = Article.getArticleUrlById(h, withoutPrev.get(u)
							.getArticleId());
							if (newArtInfo.get(i).getUrl().equals(url))
							{
								//matched! So we write only previous of matched (without matched)
								if (i == 0)
								{
									//there are NO really new arts, we only catch separated blocks of arts
									//so we'll only update prevArtUrl for matched withoutPrevArt
									String prevArtUrl = Article.getArticleUrlById(h,
									lastArtCat.getArticleId());
									int id = withoutPrev.get(u).getId();
									ArtCatTable.updatePreviousArt(h, id, prevArtUrl);
								}
								else
								{
									//there ARE new real arts, so write them all!
									//and update entry, that matched, by previousArtUrl
									List<Article> subListArtInfo = newArtInfo.subList(0, i - 1);
									List<ArtCatTable> dataToWrite = ArtCatTable.getArtCatListFromArticleList(
									h, subListArtInfo, categoryId);
									//set prevArtUrl for first entry
									String prevArtUrl = Article.getArticleUrlById(h,
									lastArtCat.getArticleId());
									dataToWrite.get(0).setPreviousArtUrl(prevArtUrl);
									//update previousArtUrl for matched ArtCat row
									int id = withoutPrev.get(u).getId();
									String prevArtUrlToUpdate = subListArtInfo.get(i).getUrl();
									ArtCatTable.updatePreviousArt(h, id, prevArtUrlToUpdate);
									//setNextArtsURL for last in new arts list, cause we can't do it in ArtCat method
									String nextArtUrl = Article.getArticleUrlById(h, withoutPrev.get(u)
									.getArticleId());
									dataToWrite.get(dataToWrite.size() - 1).setNextArtUrl(nextArtUrl);
									//write new entries with new Arts to ArtCatTable
									ArtCatTable.write(h, dataToWrite);
								}
								Log.d(LOG, "done writing artCat to empty previous entry");
							}
							else
							{
								//check if it's last iteration and so we didn't find any matches
								if (i == newArtInfo.size() - 1 - 1 && u == withoutPrev.size() - 1)
								{
									//if we can't find any, we simply write all artCats
									List<ArtCatTable> dataToWrite = ArtCatTable.getArtCatListFromArticleList(
									h, newArtInfo, categoryId);
									//set prevArtUrl for first entry
									String prevArtUrl = Article.getArticleUrlById(h,
									lastArtCat.getArticleId());
									dataToWrite.get(0).setPreviousArtUrl(prevArtUrl);
									//write new entries with new Arts to ArtCatTable
									ArtCatTable.write(h, dataToWrite);

									Log.d(LOG, "done writing artCat. NO match to empty previous entry");
								}
							}
						}
					}
					return new String[] { Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT, null };
				}
				else
				{
					//there are no arts without missing prev art, so
					//just write them all!!!11 ARGHHH!!!
					List<ArtCatTable> dataToWrite = ArtCatTable.getArtCatListFromArticleList(h,
					newArtInfo, categoryId);
					//set prevArtUrl for first entry
					String prevArtUrl = Article.getArticleUrlById(h, lastArtCat.getArticleId());
					dataToWrite.get(0).setPreviousArtUrl(prevArtUrl);
					//write new entries with new Arts to ArtCatTable
					ArtCatTable.write(h, dataToWrite);
					return new String[] { Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT, null };
				}
			}
		}
		else
		{
			//this.isAuthor, so...
			//here we can have some variants:
			//1) we load as we have no next arts
			//2) we load as we have less than 30 arts
			//anyway we must find our previous last artAut and change its nextArtUrl
			int authorId = Author.getAuthorIdByURL(h, categoryToLoad);
			ArtAutTable lastArtAut = null;
			List<ArtAutTable> allArtAutList = ArtAutTable.getListFromTop(h, authorId, pageToLoad - 1);
			lastArtAut = allArtAutList.get(allArtAutList.size() - 1);

			//check here situation, when publishing new art on site during our request
			//or since we load from top
			//so we can receive first art in someResult same as last in DB

			//firstly find ordered list of BD pageToLoad arts
			List<ArtAutTable> dBarts = ArtAutTable.getArtAutTableListByAuthorIdFromGivenId(h,
			authorId, lastArtAut.getId(), false);
			if (dBarts != null)
			{
				//check matching loadedData url with first in dBarts
				String firstInDBArtsUrl = Article.getArticleUrlById(h, dBarts.get(0).getArticleId());
				if (dataFromWeb.get(0).getUrl().equals(firstInDBArtsUrl))
				{
					//matched! So there are no lag published arts
					//now we can write new arts after last in DB
					ArtAutTable lastInDB = dBarts.get(dBarts.size() - 1);
					//so we'll have to update lastInDBArt by real next.
					ArtAutTable.updateNextArt(h, lastInDB.getId(), dataFromWeb.get(dBarts.size()).getUrl());

					//then we must match each art with all artAut, that have no previousArtUrl
					//get list of all artAut without previous art
					List<ArtAutTable> withoutPrev = ArtAutTable.getAllRowsWithoutPrevArt(h,
					authorId);
					//we must match only with new arts, no with all
					List<Article> newArtInfo = dataFromWeb.subList(dBarts.size(), dataFromWeb.size());
					if (withoutPrev != null)
					{
						for (int i = 0; i < newArtInfo.size(); i++)
						{
							for (int u = 0; u < withoutPrev.size(); u++)
							{
								//get url of checking ArtAut entry
								String url = Article.getArticleUrlById(h, withoutPrev.get(u)
								.getArticleId());
								if (newArtInfo.get(i).getUrl().equals(url))
								{
									//matched! So we write only previous of matched (without matched)
									if (i == 0)
									{
										//there are NO really new arts, we only catch separated blocks of arts
										//so we'll only update prevArtUrl for matched withoutPrevArt
										String prevArtUrl = Article.getArticleUrlById(h,
										lastArtAut.getArticleId());
										int id = withoutPrev.get(u).getId();
										ArtAutTable.updatePreviousArt(h, id, prevArtUrl);
									}
									else
									{
										//there ARE new real arts, so write them all!
										//and update entry, that matched, by previousArtUrl
										List<Article> subListArtInfo = newArtInfo.subList(0, i - 1);
										List<ArtAutTable> dataToWrite = ArtAutTable.getArtAutListFromArtInfoList(
										h, subListArtInfo, authorId);
										//set prevArtUrl for first entry
										String prevArtUrl = Article.getArticleUrlById(h,
										lastArtAut.getArticleId());
										dataToWrite.get(0).setPreviousArtUrl(prevArtUrl);
										//update previousArtUrl for matched ArtAut row
										int id = withoutPrev.get(u).getId();
										String prevArtUrlToUpdate = subListArtInfo.get(i).getUrl();
										ArtAutTable.updatePreviousArt(h, id, prevArtUrlToUpdate);
										//setNextArtsURL for last in new arts list, cause we can't do it in ArtAut method
										String nextArtUrl = Article.getArticleUrlById(h, withoutPrev.get(u)
										.getArticleId());
										dataToWrite.get(dataToWrite.size() - 1).setNextArtUrl(nextArtUrl);
										//write new entries with new Arts to ArtAutTable
										ArtAutTable.write(h, dataToWrite);
									}
									Log.d(LOG, "done writing artAut to empty previous entry");
								}
								else
								{
									//check if it's last iteration and so we didn't find any matches
									if (i == newArtInfo.size() - 1 - 1 && u == withoutPrev.size() - 1)
									{
										//if we can't find any, we simply write all artAuts
										List<ArtAutTable> dataToWrite = ArtAutTable.getArtAutListFromArtInfoList(
										h, newArtInfo, authorId);
										//set prevArtUrl for first entry
										String prevArtUrl = Article.getArticleUrlById(h,
										lastArtAut.getArticleId());
										dataToWrite.get(0).setPreviousArtUrl(prevArtUrl);
										//write new entries with new Arts to ArtAutTable
										ArtAutTable.write(h, dataToWrite);

										Log.d(LOG, "done writing artAut. NO match to empty previous entry");
									}
								}
							}
						}
						return new String[] { Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT, null };
					}
					else
					{
						//there are no arts without missing prev art, so
						//just write them all!!!11 ARGHHH!!!
						List<ArtAutTable> dataToWrite = ArtAutTable.getArtAutListFromArtInfoList(h,
						newArtInfo, authorId);
						//set prevArtUrl for first entry
						String prevArtUrl = Article.getArticleUrlById(h, lastArtAut.getArticleId());
						dataToWrite.get(0).setPreviousArtUrl(prevArtUrl);
						//write new entries with new Arts to ArtAutTable
						ArtAutTable.write(h, dataToWrite);
						return new String[] { Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT, null };
					}
				}
				else
				{
					//first not matched, so it's publishing lag, so we'll load from top
					//but firstly we'll delete all category arts
					//to prevent hardcoding to solve all posible situations
					List<ArtAutTable> rowsToDelete = ArtAutTable.getAllRowsByCategoryId(h, authorId);
					ArtAutTable.delete(h, rowsToDelete);
					return new String[] { Msg.DB_ANSWER_WRITE_FROM_BOTTOM_EXCEPTION, null };
				}
			}
			else
			{
				//there are no arts of this pagetoLoad in DB
				//so we can check if gained arts matched with arts on previous page
				//if we have match it's pub lag, so start load from top!
				//else - just write arts to db
				String artsUrlToMatch = dataFromWeb.get(0).getUrl();
				for (int i = 0; i < allArtAutList.size(); i++)
				{
					String prevUrlToMatch = Article.getArticleUrlById(h, allArtAutList.get(i).getArticleId());
					if (artsUrlToMatch.equals(prevUrlToMatch))
					{
						//matched! So it's publishing lag, so start download from top!
						//but firstly we'll delete all category arts
						//to prevent hardcoding to solve all posible situations
						List<ArtAutTable> rowsToDelete = ArtAutTable.getAllRowsByCategoryId(h, authorId);
						ArtAutTable.delete(h, rowsToDelete);
						return new String[] { Msg.DB_ANSWER_WRITE_FROM_BOTTOM_EXCEPTION, null };
					}
				}
				//TODO this is same as previous block. Refactor it!
				ArtAutTable lastInDB = allArtAutList.get(allArtAutList.size() - 1);
				//so we'll have to update lastInDBArt by real next.
				ArtAutTable.updateNextArt(h, lastInDB.getId(), dataFromWeb.get(0).getUrl());

				//then we must match each art with all artAut, that have no previousArtUrl
				//get list of all artAut without previous art
				List<ArtAutTable> withoutPrev = ArtAutTable.getAllRowsWithoutPrevArt(h,
				authorId);
				//we must match only with new arts, not with all
				List<Article> newArtInfo = dataFromWeb;
				if (withoutPrev != null)
				{
					for (int i = 0; i < newArtInfo.size(); i++)
					{
						for (int u = 0; u < withoutPrev.size(); u++)
						{
							//get url of checking ArtAut entry
							String url = Article.getArticleUrlById(h, withoutPrev.get(u)
							.getArticleId());
							if (newArtInfo.get(i).getUrl().equals(url))
							{
								//matched! So we write only previous of matched (without matched)
								if (i == 0)
								{
									//there are NO really new arts, we only catch separated blocks of arts
									//so we'll only update prevArtUrl for matched withoutPrevArt
									String prevArtUrl = Article.getArticleUrlById(h,
									lastArtAut.getArticleId());
									int id = withoutPrev.get(u).getId();
									ArtAutTable.updatePreviousArt(h, id, prevArtUrl);
								}
								else
								{
									//there ARE new real arts, so write them all!
									//and update entry, that matched, by previousArtUrl
									List<Article> subListArtInfo = newArtInfo.subList(0, i - 1);
									List<ArtAutTable> dataToWrite = ArtAutTable.getArtAutListFromArtInfoList(
									h, subListArtInfo, authorId);
									//set prevArtUrl for first entry
									String prevArtUrl = Article.getArticleUrlById(h,
									lastArtAut.getArticleId());
									dataToWrite.get(0).setPreviousArtUrl(prevArtUrl);
									//update previousArtUrl for matched ArtAut row
									int id = withoutPrev.get(u).getId();
									String prevArtUrlToUpdate = subListArtInfo.get(i).getUrl();
									ArtAutTable.updatePreviousArt(h, id, prevArtUrlToUpdate);
									//setNextArtsURL for last in new arts list, cause we can't do it in ArtAut method
									String nextArtUrl = Article.getArticleUrlById(h, withoutPrev.get(u)
									.getArticleId());
									dataToWrite.get(dataToWrite.size() - 1).setNextArtUrl(nextArtUrl);
									//write new entries with new Arts to ArtAutTable
									ArtAutTable.write(h, dataToWrite);
								}
								Log.d(LOG, "done writing artAut to empty previous entry");
							}
							else
							{
								//check if it's last iteration and so we didn't find any matches
								if (i == newArtInfo.size() - 1 - 1 && u == withoutPrev.size() - 1)
								{
									//if we can't find any, we simply write all artAuts
									List<ArtAutTable> dataToWrite = ArtAutTable.getArtAutListFromArtInfoList(
									h, newArtInfo, authorId);
									//set prevArtUrl for first entry
									String prevArtUrl = Article.getArticleUrlById(h,
									lastArtAut.getArticleId());
									dataToWrite.get(0).setPreviousArtUrl(prevArtUrl);
									//write new entries with new Arts to ArtAutTable
									ArtAutTable.write(h, dataToWrite);

									Log.d(LOG, "done writing artAut. NO match to empty previous entry");
								}
							}
						}
					}
					return new String[] { Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT, null };
				}
				else
				{
					//there are no arts without missing prev art, so
					//just write them all!!!11 ARGHHH!!!
					List<ArtAutTable> dataToWrite = ArtAutTable.getArtAutListFromArtInfoList(h,
					newArtInfo, authorId);
					//set prevArtUrl for first entry
					String prevArtUrl = Article.getArticleUrlById(h, lastArtAut.getArticleId());
					dataToWrite.get(0).setPreviousArtUrl(prevArtUrl);
					//write new entries with new Arts to ArtAutTable
					ArtAutTable.write(h, dataToWrite);
					return new String[] { Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT, null };
				}
			}
		}
	}

	protected void onPostExecute(String[] result)
	{
		this.callback.onDoneWritingFromBottom(result, this.dataFromWeb, this.categoryToLoad, this.pageToLoad);
	}
}