/*
 05.04.2015
AskDBFromTop.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.util.ArrayList;
import java.util.List;

import ru.kuchanov.odnako.callbacks.CallbackAskDBFromBottom;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncTaskAskDBFromBottom extends AsyncTask<Void, Void, String>
{
	final private static String LOG = AsyncTaskAskDBFromBottom.class.getSimpleName();

	private Context ctx;
	private DataBaseHelper h;
	private String categoryToLoad;
	private int pageToLoad;
	private CallbackAskDBFromBottom callback;

	public AsyncTaskAskDBFromBottom(Context ctx, DataBaseHelper dataBaseHelper, String categoryToLoad,
	int pageToLoad, CallbackAskDBFromBottom callback)
	{
		this.ctx = ctx;
		this.h = dataBaseHelper;
		this.categoryToLoad = categoryToLoad;
		this.pageToLoad = pageToLoad;
		this.callback = callback;
	}

	protected String doInBackground(Void... args)
	{
		//switch by category or author
		if (Category.isCategory(h, categoryToLoad))
		{
			//aks db for arts
			int categoryId = Category.getCategoryIdByURL(h, categoryToLoad);
			List<ArtCatTable> allArtsFromFirst;
			//pageToLoad-1 because here we do not need next arts, only arts, that already showed
			allArtsFromFirst = ArtCatTable.getListFromTop(h, categoryId, pageToLoad - 1);

			//firstly, if we have <30 arts from top, there is initial art in this list, so we must DO NOTHING!
			if (allArtsFromFirst.size() < 30)
			{
				//DO NOTHING, return, that it's start of list
				return Msg.DB_ANSWER_FROM_BOTTOM_LESS_THEN_30_FROM_TOP;
			}
			//so now we have first 30*pageToLoad-1 arts. Now get next 30 by passing last id to same method
			List<ArtCatTable> allArts;
			int lastId = allArtsFromFirst.get(allArtsFromFirst.size() - 1).getId();
			allArts = ArtCatTable.getArtCatTableListByCategoryIdFromGivenId(h, categoryId, lastId, false);

			//if we have no arts, we check if last shown art isVeryBottomOfCategory
			//we can check it both by URL or isTop value of ArtCat
			//if so we do nothing
			//else we load them from web
			if (allArts == null)
			{
				String initialCatArtUrl = Category.getFirstArticleURLById(h, categoryId);
				if (initialCatArtUrl != null)
				{
					int lastShownArtsId = allArtsFromFirst.get(allArtsFromFirst.size() - 1).getArticleId();
					String lastShownArtsUrl = Article.getArticleUrlById(h, lastShownArtsId);

					if (initialCatArtUrl.equals(lastShownArtsUrl))
					{
						//if matched we do not need to load/show more because we've already show initial art
						return Msg.DB_ANSWER_FROM_BOTTOM_INITIAL_ART_ALREADY_SHOWN;
					}
					else
					{
						//load from web
						Log.d(LOG, "No arts at all and no match to initial with already shown, so load from web");
						return Msg.DB_ANSWER_FROM_BOTTOM_NO_ARTS_AT_ALL;
					}
				}
				else
				{
					//load from web
					Log.d(LOG, "No arts at all and no initial art, so load from web");
					return Msg.DB_ANSWER_FROM_BOTTOM_NO_ARTS_AT_ALL;
				}
			}
			else
			{
				if (allArts.size() == 30)
				{
					//if we have 30, so we pass 30 to fragment
					//Log.d(LOG_TAG, "we have 30, so we pass 30 to fragment");
					ArrayList<Article> data = ArtCatTable.getArticleListFromArtCatList(h, allArts);
					String[] resultMessage = new String[] { Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT, null };
					ServiceDB.sendBroadcastWithResult(ctx, resultMessage, data, categoryToLoad, pageToLoad);
					return Msg.DB_ANSWER_FROM_BOTTOM_INFO_SENDED_TO_FRAG;
				}
				else
				{
					//else we ask category if it has firstArtURL
					String firstArtInCatURL = null;
					firstArtInCatURL = Category.getFirstArticleURLById(h, categoryId);
					if (firstArtInCatURL == null)
					{
						//if so we load from web
						//Log.d(LOG_TAG, "we have LESS than 30, but no initial art. So load from web");
						return Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_NO_INITIAL;
					}
					else
					{
						//check matching last of gained URL with initial (first)
						String lastInListArtsUrl;
						lastInListArtsUrl = Article.getArticleUrlById(h, allArts.get(allArts.size() - 1)
						.getArticleId());
						if (firstArtInCatURL.equals(lastInListArtsUrl))
						{
							//so it is real end of all arts in category and we can send arts to frag
							ArrayList<Article> data = ArtCatTable.getArticleListFromArtCatList(h, allArts);
							String[] resultMessage = new String[] { Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT, null };
							ServiceDB.sendBroadcastWithResult(ctx, resultMessage, data, categoryToLoad, pageToLoad);

							return Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_HAVE_MATCH_TO_INITIAL;
						}
						else
						{
							//else we must load arts from web
							//Log.d(LOG_TAG, "we have LESS than 30, and have NO match to initial. So load from web");
							return Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_NO_MATCH_TO_INITIAL;
						}//check matching to initial
					}//we have initial art
				}//we have !30 bottom arts
			}//we have bottom arts
		}//this is category
		else
		{
			//this is author, so...
			//aks db for arts
			int authorId = Author.getAuthorIdByURL(h, Author.getURLwithoutSlashAtTheEnd(categoryToLoad));
			List<ArtAutTable> allArtsFromFirst;
			//pageToLoad-1 because here we do not need next arts, only arts, that already showed
			allArtsFromFirst = ArtAutTable.getListFromTop(h, authorId, pageToLoad - 1);

			//firstly, if we have <30 arts from top, there is initial art in this list, so we must DO NOTHING!
			if (allArtsFromFirst.size() < 30)
			{
				//DO NOTHING, return, that it's start of list
				return Msg.DB_ANSWER_FROM_BOTTOM_LESS_THEN_30_FROM_TOP;
			}
			//so now we have first 30*pageToLoad-1 arts. Now get next 30 by passing last id to same method
			List<ArtAutTable> allArts;
			int lastId = allArtsFromFirst.get(allArtsFromFirst.size() - 1).getId();
			allArts = ArtAutTable.getArtAutTableListByAuthorIdFromGivenId(h, authorId, lastId, false);

			//if we have no arts, we check if last shown art isVeryBottomOfAuthor
			//we can check it both by URL or isTop value of ArtCat
			//if so we do nothing
			//else we load them from web
			if (allArts == null)
			{
				//				String initialAutArtUrl = Category.getFirstArticleURLById(h, authorId);
				String initialAutArtUrl = Author.getFirstArticleURLById(h, authorId);
				if (initialAutArtUrl != null)
				{
					int lastShownArtsId = allArtsFromFirst.get(allArtsFromFirst.size() - 1).getArticleId();
					String lastShownArtsUrl = Article.getArticleUrlById(h, lastShownArtsId);

					if (initialAutArtUrl.equals(lastShownArtsUrl))
					{
						//if matched we do not need to load/show more because we've already show initial art
						return Msg.DB_ANSWER_FROM_BOTTOM_INITIAL_ART_ALREADY_SHOWN;
					}
					else
					{
						//load from web
						//Log.d(LOG_TAG, "No arts at all and no match to initial with already shown, so load from web");
						return Msg.DB_ANSWER_FROM_BOTTOM_NO_ARTS_AT_ALL;
					}
				}
				else
				{
					//load from web
					//Log.d(LOG_TAG, "No arts at all and no initial art, so load from web");
					return Msg.DB_ANSWER_FROM_BOTTOM_NO_ARTS_AT_ALL;
				}
			}
			else
			{
				if (allArts.size() == 30)
				{
					//if we have 30, so we pass 30 to fragment
					//Log.d(LOG_TAG, "we have 30, so we pass 30 to fragment");
					ArrayList<Article> data = ArtAutTable.getArtInfoListFromArtAutList(h, allArts);
					String[] resultMessage = new String[] { Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT, null };
					ServiceDB.sendBroadcastWithResult(ctx, resultMessage, data, categoryToLoad, pageToLoad);

					return Msg.DB_ANSWER_FROM_BOTTOM_INFO_SENDED_TO_FRAG;
				}
				else
				{
					//else we ask Author if it has firstArtURL
					String firstArtInAutURL = null;
					firstArtInAutURL = Author.getFirstArticleURLById(h, authorId);
					if (firstArtInAutURL == null)
					{
						//if so we load from web
						//Log.d(LOG_TAG, "we have LESS than 30, but no initial art. So load from web");
						return Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_NO_INITIAL;
					}
					else
					{
						//check matching last of gained URL with initial (first)
						String lastInListArtsUrl;
						lastInListArtsUrl = Article.getArticleUrlById(h, allArts.get(allArts.size() - 1)
						.getArticleId());
						if (firstArtInAutURL.equals(lastInListArtsUrl))
						{
							//so it is real end of all arts in Author and we can send arts to frag
							ArrayList<Article> data = ArtAutTable.getArtInfoListFromArtAutList(h, allArts);
							String[] resultMessage = new String[] { Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT, null };
							ServiceDB.sendBroadcastWithResult(ctx, resultMessage, data, categoryToLoad, pageToLoad);

							return Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_HAVE_MATCH_TO_INITIAL;
						}
						else
						{
							//else we must load arts from web
							//Log.d(LOG_TAG, "we have LESS than 30, and have NO match to initial. So load from web");
							return Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_NO_MATCH_TO_INITIAL;
						}//check matching to initial
					}//we have initial art
				}//we have !30 bottom arts
			}//we have bottom arts
		}//this is author
	}

	protected void onPostExecute(String result)
	{
		this.callback.onAnswerFromDBFromBottom(result, categoryToLoad, pageToLoad);
	}
}