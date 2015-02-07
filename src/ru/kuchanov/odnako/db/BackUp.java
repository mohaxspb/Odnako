package ru.kuchanov.odnako.db;


public class BackUp
{

	public BackUp()
	{
	}
//	
//	/**
//	 * 
//	 * @param dataFromWeb
//	 *            downloaded arts to write to DB
//	 * @param categoryToLoad
//	 *            site category from witch we download data
//	 * @param pageToLoad
//	 *            page of site category, from witch we download each 30 arts
//	 * 
//	 * @return some process result message, i.e. quont of new arts
//	 */
//	public String[] writeArtsToDB(ArrayList<ArtInfo> dataFromWeb, String categoryToLoad, int pageToLoad)
//	{
//		//here we'll write gained arts to Article table
//		Article.writeArtInfoToArticleTable(getHelper(), dataFromWeb);
//
//		//here if we recive less then 30 arts (const quont of arts on page)
//		//we KNOW that last of them is initial art in category (author)
//		//so WRITE it to DB!
//		if (dataFromWeb.size() < 30)
//		{
//			if (Category.isCategory(this.getHelper(), categoryToLoad))
//			{
//				int categoryId = Category.getCategoryIdByURL(getHelper(), categoryToLoad);
//				String initialArtsUrl = dataFromWeb.get(dataFromWeb.size() - 1).url;
//				Category.setInitialArtsUrl(this.getHelper(), categoryId, initialArtsUrl);
//			}
//			else
//			{
//				int authorId = Author.getAuthorIdByURL(getHelper(), categoryToLoad);
//				String initialArtsUrl = dataFromWeb.get(dataFromWeb.size() - 1).url;
//				Author.setInitialArtsUrl(this.getHelper(), authorId, initialArtsUrl);
//			}
//		}
//
//		//and fill ArtCatTable with entries of arts
//		//check if it was loading from top (new) of from bottom (previous)
//		if (pageToLoad == 1)
//		{
//			//from top
//			//check if there are arts of given category
//			//switch by Category or Author				
//			if (Category.isCategory(this.getHelper(), categoryToLoad))
//			{
//				//this is Category, so...
//				//get Category id
//				int categoryId = Category.getCategoryIdByURL(getHelper(), categoryToLoad);
//
//				//check if there are any arts in category by searching TOP art
//				if (ArtCatTable.categoryArtsExists(getHelper(), categoryId))
//				{
//					//match url of IS_TOP ArtCatTable with given list and calculate quont of new
//					////new=0 => check if there is new inner arts and update ArtCatTable if is;
//					////new<30 => write them to DB with prev/next art URL; change IS_TOP to null and set TRUE to first of given list
//					////new>30 => write them to DB with prev/next art URL; change IS_TOP to null and set TRUE to first of given list
//
//					//match url of IS_TOP ArtCatTable with given list and calculate quont of new
//					ArtCatTable topArtCat = ArtCatTable.getTopArtCat(getHelper(), categoryId, true);
//					String topArtUrl = Article.getArticleUrlById(getHelper(), topArtCat.getArticleId());
//
//					for (int i = 0; i < dataFromWeb.size(); i++)
//					{
//						if (dataFromWeb.get(i).url.equals(topArtUrl))
//						{
//							//check if there is no new arts
//							if (i == 0)
//							{
//								//here we can check if there were new art between 1st
//								//and last (publishing on site lag) and if so delete artCat in DB and replace them
//								//with loaded from web and update articles order
//								//so...
//								//we can detect changes by matching last arts URL of first 30 arts from top
//								//and, of course their quont must be equal (in case of <30 arts at all
//								List<ArtCatTable> first30FromTop = ArtCatTable.getListFromTop(getHelper(), categoryId,
//								pageToLoad);
//								String lastInFirst30Url = Article.getArticleUrlById(getHelper(),
//								first30FromTop.get(first30FromTop.size() - 1).getArticleId());
//								if (dataFromWeb.size() == first30FromTop.size() &&
//								dataFromWeb.get(dataFromWeb.size() - 1).url.equals(lastInFirst30Url))
//								{
//									//if so lists of loaded from web and stored in DB equals
//									//do nothing, send result as NO_NEW
//									return new String[] { Msg.NO_NEW, null };
//								}
//								else
//								{
//									//we have some changes, so we must calculate quont of arts to delete
//									//and change them to new
//									//we'll delete from top to def quont on page - new qount
//									//write gained, set nextArt of it's last to def quont on page - new qount URL
//									//and update def quont on page - new qount URL by last of gained
//									//SO CALCULATE QUONT OF NEW BY MATCHING ALL TO ALL
//									int newQuont = 0;
//									for (int u = 0; u < dataFromWeb.size(); u++)
//									{
//										String loadedArtsURL = dataFromWeb.get(u).url;
//										for (int y = 0; y < first30FromTop.size(); y++)
//										{
//											int artCatArtsID = first30FromTop.get(y).getArticleId();
//											String DBArtsURL = Article.getArticleUrlById(getHelper(), artCatArtsID);
//											if (loadedArtsURL.equals(DBArtsURL))
//											{
//												//matched, so everithing is OK, do noting
//											}
//											else
//											{
//												//check if it's last iteration
//												if (y == first30FromTop.size() - 1)
//												{
//													//it's last iteration and we have no match
//													//so it's really new art!
//													//so increment newQuont value
//													newQuont++;
//												}
//											}
//										}
//									}//calculating newQount
//										//before deleting, that may cause changing of list we get nextArtUrl for new rows
//									String nextArtUrl = Article.getArticleUrlById(getHelper(),
//									first30FromTop.get(first30FromTop.size() - newQuont - 1).getArticleId());
//									//also before deleting get id of new firstArtOfNextPage to update
//									//it's prevArtsUrl
//									int firstArtOfNextPageId = first30FromTop.get(first30FromTop.size() - newQuont - 1)
//									.getId();
//									//THEN delete ArtCatTable rows
//									ArtCatTable.delete(getHelper(),
//									first30FromTop.subList(0, first30FromTop.size() - newQuont));
//									//create ArtCat list from ArtInfo
//									List<ArtCatTable> dataToWrite = ArtCatTable.getArtCatListFromArtInfoList(
//									getHelper(), dataFromWeb, categoryId);
//									//set nextArt of it's last to def quont on page - new qount URL
//									dataToWrite.get(dataToWrite.size() - 1).setNextArtUrl(nextArtUrl);
//									//and update def quont on page - new qount prev URL by last of gained
//									ArtCatTable.updatePreviousArt(getHelper(), firstArtOfNextPageId,
//									dataFromWeb.get(dataFromWeb.size() - 1).url);
//									//Finally write new rows!
//									ArtCatTable.write(getHelper(), dataToWrite);
//
//									return new String[] { Msg.NEW_QUONT, Integer.toString(i) };
//								}
//							}
//							else
//							{
//								//TODO here we can also check publishing lag, but fuck it now!
//								////new<30 => write them to DB with prev/next art URL; change IS_TOP to null for old Top Art
//								//and set isTop to TRUE to first of loaded list
//								//We must push not whole list, but only part of it!
//								List<ArtCatTable> artCatDataToWrite = ArtCatTable.getArtCatListFromArtInfoList(
//								this.getHelper(), dataFromWeb.subList(0, i), categoryId);
//								//update isTop to null for old entry
//								ArtCatTable.updateIsTop(getHelper(), topArtCat.getId(), null);
//								//update previous url for old TOP_ART
//								int prevOldTopArtsId = artCatDataToWrite.get(artCatDataToWrite.size() - 1)
//								.getArticleId();
//								String prevArtUrlOfOldTopArt = Article.getArticleUrlById(getHelper(), prevOldTopArtsId);
//								ArtCatTable.updatePreviousArt(getHelper(), topArtCat.getId(), prevArtUrlOfOldTopArt);
//								//set new TOP_ART for first of given from web
//								artCatDataToWrite.get(0).isTop(true);
//								//setNextArtsURL for last in new arts list, cause we can't do it in ArtCat method
//								String nextArtUrl = Article.getArticleUrlById(getHelper(), topArtCat.getArticleId());
//								artCatDataToWrite.get(artCatDataToWrite.size() - 1).setNextArtUrl(nextArtUrl);
//								//FINALLY write new entries to ArtCatTable
//								ArtCatTable.write(getHelper(), artCatDataToWrite);
//
//								return new String[] { Msg.NEW_QUONT, Integer.toString(i) };
//							}
//						}
//						else
//						{
//							//check if it's last iteration
//							if (i == dataFromWeb.size() - 1)
//							{
//								//new>30 => write them to DB with prev/next art URL; change IS_TOP to null and
//								//set TRUE to first of given list
//								List<ArtCatTable> artCatDataToWrite = ArtCatTable.getArtCatListFromArtInfoList(
//								this.getHelper(), dataFromWeb, categoryId);
//								//update isTop to null for old entry
//								ArtCatTable.updateIsTop(getHelper(), topArtCat.getId(), null);
//								//set new TOP_ART for first of given from web
//								artCatDataToWrite.get(0).isTop(true);
//								//FINALLY write new entries to ArtCatTable
//								ArtCatTable.write(getHelper(), artCatDataToWrite);
//								return new String[] { Msg.DB_ANSWER_WRITE_FROM_TOP_NO_MATCHES, null };
//							}
//						}
//					}
//					return new String[] { null, null };
//				}
//				else
//				{
//					//there are no arts of given category in ArtCatTable, so just write them!
//					//I mean write all arts from someResult List<ArtInfo>, that we gained from web
//					List<ArtCatTable> artCatDataToWrite = ArtCatTable.getArtCatListFromArtInfoList(this.getHelper(),
//					dataFromWeb, categoryId);
//					//set IS_TOP to true for first in list
//					artCatDataToWrite.get(0).isTop(true);
//					//FINALLY write new entries with new Arts to ArtCatTable
//					ArtCatTable.write(getHelper(), artCatDataToWrite);
//					return new String[] { Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_WRITE, null };
//				}
//			}
//			else
//			{
//				//this is Author, so...
//				//get Author id
//				int authorId = Author.getAuthorIdByURL(getHelper(), categoryToLoad);
//
//				//check if there are any arts in category by searching TOP art
//				if (ArtAutTable.authorArtsExists(getHelper(), authorId))
//				{
//					//match url of IS_TOP ArtAutTable with given list and calculate quont of new
//					////new=0 => check if there is new inner arts and update ArtAutTable if is;
//					////new<30 => write them to DB with prev/next art URL; change IS_TOP to null and set TRUE to first of given list
//					////new>30 => write them to DB with prev/next art URL; change IS_TOP to null and set TRUE to first of given list
//
//					//match url of IS_TOP ArtAutTable with given list and calculate quont of new
//					ArtAutTable topArtAut = ArtAutTable.getTopArt(getHelper(), authorId, true);
//					String topArtUrl = Article.getArticleUrlById(getHelper(), topArtAut.getArticleId());
//
//					for (int i = 0; i < dataFromWeb.size(); i++)
//					{
//						if (dataFromWeb.get(i).url.equals(topArtUrl))
//						{
//							//check if there is no new arts
//							if (i == 0)
//							{
//								//here we can check if there were new art between 1st
//								//and last (publishing on site lag) and if so delete artAut in DB and replace them
//								//with loaded from web and update articles order
//								//so...
//								//we can detect changes by matching last arts URL of first 30 arts from top
//								//and, of course their quont must be equal (in case of <30 arts at all
//								List<ArtAutTable> first30FromTop = ArtAutTable.getListFromTop(getHelper(), authorId,
//								pageToLoad);
//								String lastInFirst30Url = Article.getArticleUrlById(getHelper(),
//								first30FromTop.get(first30FromTop.size() - 1).getArticleId());
//								if (dataFromWeb.size() == first30FromTop.size() &&
//								dataFromWeb.get(dataFromWeb.size() - 1).url.equals(lastInFirst30Url))
//								{
//									//if so lists of loaded from web and stored in DB equals
//									//do nothing, send result as NO_NEW
//									return new String[] { Msg.NO_NEW, null };
//								}
//								else
//								{
//									//we have some changes, so we must calculate quont of arts to delete
//									//and change them to new
//									//we'll delete from top to def quont on page - new qount
//									//write gained, set nextArt of it's last to def quont on page - new qount URL
//									//and update def quont on page - new qount URL by last of gained
//									//SO CALCULATE QUONT OF NEW BY MATCHING ALL TO ALL
//									int newQuont = 0;
//									for (int u = 0; u < dataFromWeb.size(); u++)
//									{
//										String loadedArtsURL = dataFromWeb.get(u).url;
//										for (int y = 0; y < first30FromTop.size(); y++)
//										{
//											int artAutArtsID = first30FromTop.get(y).getArticleId();
//											String DBArtsURL = Article.getArticleUrlById(getHelper(), artAutArtsID);
//											if (loadedArtsURL.equals(DBArtsURL))
//											{
//												//matched, so everithing is OK, do noting
//											}
//											else
//											{
//												//check if it's last iteration
//												if (y == first30FromTop.size() - 1)
//												{
//													//it's last iteration and we have no match
//													//so it's really new art!
//													//so increment newQuont value
//													newQuont++;
//												}
//											}
//										}
//									}//calculating newQount
//										//before deleting, that may cause changing of list we get nextArtUrl for new rows
//									String nextArtUrl = Article.getArticleUrlById(getHelper(),
//									first30FromTop.get(first30FromTop.size() - newQuont - 1).getArticleId());
//									//also before deleting get id of new firstArtOfNextPage to update
//									//it's prevArtsUrl
//									int firstArtOfNextPageId = first30FromTop.get(first30FromTop.size() - newQuont - 1)
//									.getId();
//									//THEN delete ArtAutTable rows
//									ArtAutTable.delete(getHelper(),
//									first30FromTop.subList(0, first30FromTop.size() - newQuont));
//									//create ArtAut list from ArtInfo
//									List<ArtAutTable> dataToWrite = ArtAutTable.getArtAutListFromArtInfoList(
//									getHelper(), dataFromWeb, authorId);
//									//set nextArt of it's last to def quont on page - new qount URL
//									dataToWrite.get(dataToWrite.size() - 1).setNextArtUrl(nextArtUrl);
//									//and update def quont on page - new qount prev URL by last of gained
//									ArtAutTable.updatePreviousArt(getHelper(), firstArtOfNextPageId,
//									dataFromWeb.get(dataFromWeb.size() - 1).url);
//									//Finally write new rows!
//									ArtAutTable.write(getHelper(), dataToWrite);
//
//									return new String[] { Msg.NEW_QUONT, Integer.toString(i) };
//								}
//							}
//							else
//							{
//								//TODO here we can also check publishing lag, but fuck it now!
//								//new<30 => write them to DB with prev/next art URL; change IS_TOP to null for old Top Art
//								//and set isTop to TRUE to first of loaded list
//								List<ArtAutTable> artAutDataToWrite = ArtAutTable.getArtAutListFromArtInfoList(
//								this.getHelper(), dataFromWeb.subList(0, i), authorId);
//								//update isTop to null for old entry
//								ArtAutTable.updateIsTop(getHelper(), topArtAut.getId(), null);
//								//update previous url for old TOP_ART
//								int prevOldTopArtsId = artAutDataToWrite.get(artAutDataToWrite.size() - 1)
//								.getArticleId();
//								String prevArtUrlOfOldTopArt = Article.getArticleUrlById(getHelper(), prevOldTopArtsId);
//								ArtCatTable.updatePreviousArt(getHelper(), topArtAut.getId(), prevArtUrlOfOldTopArt);
//								//set new TOP_ART for first of given from web
//								artAutDataToWrite.get(0).isTop(true);
//								//setNextArtsURL for last in new arts list, cause we can't do it in ArtCat method
//								String nextArtUrl = Article.getArticleUrlById(getHelper(), topArtAut.getArticleId());
//								artAutDataToWrite.get(artAutDataToWrite.size() - 1).setNextArtUrl(nextArtUrl);
//								//FINALLY write new entries to ArtAutTable
//								ArtAutTable.write(getHelper(), artAutDataToWrite);
//
//								return new String[] { Msg.NEW_QUONT, Integer.toString(i) };
//							}
//						}
//						else
//						{
//							//check if it's last iteration
//							if (i == dataFromWeb.size() - 1)
//							{
//								//new>30 => write them to DB with prev/next art URL; change IS_TOP to null and
//								//set TRUE to first of given list
//								List<ArtAutTable> artAutDataToWrite = ArtAutTable.getArtAutListFromArtInfoList(
//								this.getHelper(), dataFromWeb, authorId);
//								//update isTop to null for old entry
//								ArtAutTable.updateIsTop(getHelper(), topArtAut.getId(), null);
//								//set new TOP_ART for first of given from web
//								artAutDataToWrite.get(0).isTop(true);
//								//FINALLY write new entries to ArtAutTable
//								ArtAutTable.write(getHelper(), artAutDataToWrite);
//								return new String[] { Msg.DB_ANSWER_WRITE_FROM_TOP_NO_MATCHES, null };
//							}
//						}
//					}
//					return new String[] { null, null };
//				}
//				else
//				{
//					//there are no arts of given category in ArtAutTable, so just write them!
//					//I mean write all arts from someResult List<ArtInfo>, that we gained from web
//					List<ArtAutTable> artAutDataToWrite = ArtAutTable.getArtAutListFromArtInfoList(this.getHelper(),
//					dataFromWeb, authorId);
//					//set IS_TOP to true for first in list
//					artAutDataToWrite.get(0).isTop(true);
//					//FINALLY write new entries with new Arts to ArtAutTable
//					ArtAutTable.write(getHelper(), artAutDataToWrite);
//					return new String[] { Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_WRITE, null };
//				}
//			}//this is author
//		}//pageToLoad=1
//		else
//		{
//			//from bottom
//			if (Category.isCategory(this.getHelper(), categoryToLoad))
//			{
//				//this.isCategory, so...
//				//here we can have some variants:
//				//1) we load as we have no next arts
//				//2) we load as we have less than 30 arts
//				//anyway we must find our previous last artCat and change its nextArtUrl
//				int categoryId = Category.getCategoryIdByURL(getHelper(), categoryToLoad);
//				ArtCatTable lastArtCat = null;
//				List<ArtCatTable> allArtCatList = ArtCatTable.getListFromTop(getHelper(), categoryId, pageToLoad - 1);
//				lastArtCat = allArtCatList.get(allArtCatList.size() - 1);
//
//				//check here situation, when publishing new art on site during our request
//				//or since we load from top
//				//so we can receive first art in someResult same as last in DB
//
//				//firstly find ordered list of BD pageToLoad arts
//				List<ArtCatTable> dBarts = ArtCatTable.getArtCatTableListByCategoryIdFromGivenId(getHelper(),
//				categoryId, lastArtCat.getId(), false);
//				if (dBarts != null)
//				{
//					//check matching loadedData url with first in dBarts
//					String firstInDBArtsUrl = Article.getArticleUrlById(getHelper(), dBarts.get(0).getArticleId());
//					if (dataFromWeb.get(0).url.equals(firstInDBArtsUrl))
//					{
//						//matched! So there are no lag published arts
//						//now we can write new arts after last in DB
//						ArtCatTable lastInDB = dBarts.get(dBarts.size() - 1);
//						//so we'll have to update lastInDBArt by real next.
//						ArtCatTable.updateNextArt(getHelper(), lastInDB.getId(), dataFromWeb.get(dBarts.size()).url);
//
//						//then we must match each art with all artCat, that have no previousArtUrl
//						//get list of all artCat without previous art
//						List<ArtCatTable> withoutPrev = ArtCatTable.getAllRowsWithoutPrevArt(this.getHelper(),
//						categoryId);
//						//we must match only with new arts, no with all
//						List<ArtInfo> newArtInfo = dataFromWeb.subList(dBarts.size(), dataFromWeb.size());
//						if (withoutPrev != null)
//						{
//							//"dataFromWeb.size()-1" because there is no nextArt for last, so we can't check matching
//							for (int i = 0; i < newArtInfo.size() - 1; i++)
//							{
//								for (int u = 0; u < withoutPrev.size(); u++)
//								{
//									//get url of checking ArtCat entry
//									String url = Article.getArticleUrlById(getHelper(), withoutPrev.get(u)
//									.getArticleId());
//									if (newArtInfo.get(i + 1).url.equals(url))
//									{
//										//matched! So we write only previous of matched (+matched)
//										//and update entry, that matched, by previousArtUrl
//										List<ArtInfo> subListArtInfo = newArtInfo.subList(0, i);
//										List<ArtCatTable> dataToWrite = ArtCatTable.getArtCatListFromArtInfoList(
//										this.getHelper(), subListArtInfo, categoryId);
//										//set prevArtUrl for first entry
//										String prevArtUrl = Article.getArticleUrlById(getHelper(),
//										lastArtCat.getArticleId());
//										dataToWrite.get(0).setPreviousArtUrl(prevArtUrl);
//										//update previousArtUrl for matched ArtCat row
//										int id = withoutPrev.get(u).getId();
//										String prevArtUrlToUpdate = subListArtInfo.get(i).url;
//										ArtCatTable.updatePreviousArt(getHelper(), id, prevArtUrlToUpdate);
//										//setNextArtsURL for last in new arts list, cause we can't do it in ArtCat method
//										String nextArtUrl = Article.getArticleUrlById(getHelper(), withoutPrev.get(u)
//										.getArticleId());
//										dataToWrite.get(dataToWrite.size() - 1).setNextArtUrl(nextArtUrl);
//										//write new entries with new Arts to ArtCatTable
//										ArtCatTable.write(getHelper(), dataToWrite);
//										return new String[] { Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_WRITE, null };
//									}
//									else
//									{
//										//check if it's last iteration and so we didn't find any matches
//										if (i == newArtInfo.size() - 1 - 1 && u == withoutPrev.size() - 1)
//										{
//											//if we can't find any, we simply write all artCats
//											List<ArtCatTable> dataToWrite = ArtCatTable.getArtCatListFromArtInfoList(
//											this.getHelper(), newArtInfo, categoryId);
//											//set prevArtUrl for first entry
//											String prevArtUrl = Article.getArticleUrlById(getHelper(),
//											lastArtCat.getArticleId());
//											dataToWrite.get(0).setPreviousArtUrl(prevArtUrl);
//											//write new entries with new Arts to ArtCatTable
//											ArtCatTable.write(getHelper(), dataToWrite);
//											return new String[] { Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_WRITE, null };
//										}
//									}
//								}
//							}
//							return new String[] { null, null };
//						}
//						else
//						{
//							//there are no arts without missing prev art, so
//							//just write them all!!!11 ARGHHH!!!
//							List<ArtCatTable> dataToWrite = ArtCatTable.getArtCatListFromArtInfoList(this.getHelper(),
//							newArtInfo, categoryId);
//							//set prevArtUrl for first entry
//							String prevArtUrl = Article.getArticleUrlById(getHelper(), lastArtCat.getArticleId());
//							dataToWrite.get(0).setPreviousArtUrl(prevArtUrl);
//							//write new entries with new Arts to ArtCatTable
//							ArtCatTable.write(getHelper(), dataToWrite);
//							return new String[] { Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_WRITE, null };
//						}
//					}
//					else
//					{
//						//first not matched, so it's publishing lag, so we'll load from top
//						return new String[] { Msg.DB_ANSWER_WRITE_FROM_BOTTOM_EXCEPTION, null };
//						//						//first not matched, so we must start loop
//						//						for(int i=0; i<dataFromWeb.size(); i++)
//						//						{
//						//							if(dataFromWeb.get(i).url.equals(firstInDBArtsUrl))
//						//							{
//						//								//so we have "i" new LAG arts
//						//								//XXX start from here!!!!
//						//								//now we can write new arts after last in DB
//						//								ArtCatTable lastInDB=dBarts.get(dBarts.size()-1);
//						//								//so we'll have to update lastInDBArt by real next.
//						//								//we'll catch Exception here in case of IndexOfBounds
//						//								//and if so - start load from top, instead of showing loaded!
//						//								try
//						//								{
//						//								ArtCatTable.updateNextArt(getHelper(), lastInDB.getId(), dataFromWeb.get(dBarts.size()+i).url);
//						//								
//						//								//then we must match each art with all artCat, that have no previousArtUrl
//						//								//get list of all artCat without previous art
//						//								List<ArtCatTable> withoutPrev = ArtCatTable.getAllRowsWithoutPrevArt(this.getHelper(), categoryId);
//						//								//we must match only with new arts, no with all
//						//								List<ArtInfo> newArtInfo=dataFromWeb.subList(dBarts.size()+i, dataFromWeb.size());
//						//								if (withoutPrev != null)
//						//								{
//						//									//"dataFromWeb.size()-1" because there is no nextArt for last, so we can't check matching
//						//									for (int y = 0; y < newArtInfo.size() - 1; y++)
//						//									{
//						//										for (int u = 0; u < withoutPrev.size(); u++)
//						//										{
//						//											//get url of checking ArtCat entry
//						//											String url = Article.getArticleUrlById(getHelper(), withoutPrev.get(u).getArticleId());
//						//											if (newArtInfo.get(y + 1).url.equals(url))
//						//											{
//						//												//matched! So we write only previous of matched (+matched)
//						//												//and update entry, that matched, by prevyousArtUrl
//						//												List<ArtInfo> subLystArtInfo = newArtInfo.subList(0, y);
//						//												List<ArtCatTable> dataToWryte = ArtCatTable.getArtCatListFromArtInfoList(
//						//												this.getHelper(), subLystArtInfo, categoryId);
//						//												//set prevArtUrl for first entry
//						//												String prevArtUrl = Article.getArticleUrlById(getHelper(), lastArtCat.getArticleId());
//						//												dataToWryte.get(0).setPreviousArtUrl(prevArtUrl);
//						//												//update prevyousArtUrl for matched ArtCat row
//						//												int id = withoutPrev.get(u).getId();
//						//												String prevArtUrlToUpdate = subLystArtInfo.get(y).url;
//						//												ArtCatTable.updatePreviousArt(getHelper(), id, prevArtUrlToUpdate);
//						//												//setNextArtsURL for last in new arts list, cause we can't do it in ArtCat method
//						//												String nextArtUrl = Article.getArticleUrlById(getHelper(), withoutPrev.get(u)
//						//												.getArticleId());
//						//												dataToWryte.get(dataToWryte.size() - 1).setNextArtUrl(nextArtUrl);
//						//												//write new entries with new Arts to ArtCatTable
//						//												ArtCatTable.write(getHelper(), dataToWryte);
//						//												return new String[]{Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_WRITE, null};
//						//											}
//						//											else
//						//											{
//						//												//check if it's last iteration and so we didn't find any matches
//						//												if (y == newArtInfo.size() - 1 - 1 && u == withoutPrev.size() - 1)
//						//												{
//						//													//yf we can't fynd any, we symply wryte all artCats
//						//													List<ArtCatTable> dataToWryte = ArtCatTable.getArtCatListFromArtInfoList(
//						//													this.getHelper(), newArtInfo, categoryId);
//						//													//set prevArtUrl for first entry
//						//													String prevArtUrl = Article.getArticleUrlById(getHelper(),
//						//													lastArtCat.getArticleId());
//						//													dataToWryte.get(0).setPreviousArtUrl(prevArtUrl);
//						//													//wryte new entryes wyth new Arts to ArtCatTable
//						//													ArtCatTable.write(getHelper(), dataToWryte);
//						//													return new String[]{Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_WRITE, null};
//						//												}
//						//											}
//						//										}
//						//									}
//						//								}
//						//								else
//						//								{
//						//									//there are no arts without missing prev art, so
//						//									//just write them all!!!11 ARGHHH!!!
//						//									List<ArtCatTable> dataToWrite = ArtCatTable.getArtCatListFromArtInfoList(this.getHelper(),
//						//									newArtInfo, categoryId);
//						//									//set prevArtUrl for first entry
//						//									String prevArtUrl = Article.getArticleUrlById(getHelper(), lastArtCat.getArticleId());
//						//									dataToWrite.get(0).setPreviousArtUrl(prevArtUrl);
//						//									//write new entries with new Arts to ArtCatTable
//						//									ArtCatTable.write(getHelper(), dataToWrite);
//						//									return new String[]{Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_WRITE, null};
//						//								}
//						//								}
//						//								catch(Exception e)
//						//								{
//						//									Log.e(LOG_TAG, e.getClass().getSimpleName());
//						//									//here we can receive IndexOfBoundsException
//						//									//if so we fuck off any catching it and simply start load from top
//						//									return new String[]{Msg.DB_ANSWER_WRITE_FROM_BOTTOM_EXCEPTION, null};
//						//								}
//						//							}
//						//							else
//						//							{
//						//								//not matched
//						//								if(i==dataFromWeb.size()-1)
//						//								{
//						//									//it's last iteration and we do not find matches, so start load from top!
//						//									return new String[]{Msg.DB_ANSWER_WRITE_FROM_BOTTOM_EXCEPTION, null};
//						//								}
//						//							}
//						//						}
//					}
//				}
//				else
//				{
//					//TODO there are no arts of this pagetoLoad in DB
//					//so we can check if gained arts matched with arts on previous page
//					//if we have match it's pub lag, so start load from top!
//					//else - just write arts to db
//					return new String[] { Msg.DB_ANSWER_WRITE_FROM_BOTTOM_EXCEPTION, null };
//				}
//			}
//			else
//			{
//				//TODO this.isAuthor, so...
//				return new String[] { Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_WRITE, null };
//			}
//		}//from bottom
//	}
//
}
