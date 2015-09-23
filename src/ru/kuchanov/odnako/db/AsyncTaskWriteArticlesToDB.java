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

import ru.kuchanov.odnako.callbacks.CallbackWriteArticles;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Writes articles, that don't exists in DB to DB (search them by URL) and
 * replace articles, that is in DB, by DB arts, that have ID. Also checks for
 * pubDate of existing arts and update it, if DB arts have date==0
 * 
 * @param h
 * @param dataFromWeb
 *            articles odj from web, that have no ID
 * @return list of articles from DB with ID
 */
public class AsyncTaskWriteArticlesToDB extends AsyncTask<Void, Void, ArrayList<Article>>
{
	final private static String LOG = AsyncTaskWriteArticlesToDB.class.getSimpleName();

	private DataBaseHelper h;
	private ArrayList<Article> dataFromWeb;
	CallbackWriteArticles callback;
	String categoryToLoad;
	int pageToLoad;

	public AsyncTaskWriteArticlesToDB(DataBaseHelper h, ArrayList<Article> dataFromWeb, CallbackWriteArticles callback,
	String categoryToLoad, int pageToLoad)
	{
		this.h = h;
		this.dataFromWeb = dataFromWeb;
		this.callback = callback;
		this.categoryToLoad = categoryToLoad;
		this.pageToLoad = pageToLoad;
	}

	//write refreshed date to entry of given category
	protected ArrayList<Article> doInBackground(Void... args)
	{
		ArrayList<Article> updatedData = new ArrayList<Article>();

		int quontOfWrittenArticles = 0;

		Article existingArt;
		for (Article a : dataFromWeb)
		{
			//check if there is no already existing arts in DB by queryForURL
			existingArt = Article.getArticleByURL(h, a.getUrl());
			if (existingArt == null)
			{
				//get author obj if it is in Article and Author table
				Author aut = null;
				try
				{
					aut = h.getDaoAuthor().queryBuilder().where()
					.eq(Author.FIELD_URL, Author.getURLwithoutSlashAtTheEnd(a.getAuthorBlogUrl())).queryForFirst();

				} catch (SQLException e)
				{
					e.printStackTrace();
				}
				//create Article obj to pass it to DB
				existingArt = a;

				existingArt.setRefreshed(new Date(0));
				existingArt.setAuthor(aut);
				//set author image URL for articles
				if (aut != null)
				{
					existingArt.setImgAuthor(aut.getAvatar());
				}
				try
				{
					updatedData.add(h.getDaoArticle().createIfNotExists(existingArt));//.create(existingArt);
					quontOfWrittenArticles++;
				} catch (SQLException e)
				{
					Log.i(LOG, "quontOfWrittenArticles: " + String.valueOf(quontOfWrittenArticles));
					Log.e(LOG, existingArt.getTitle() + " error while INSERT");
				}
			}//article do not exists
			else
			{
				//check if date of existing == 0
//				if (existingArt.getPubDate().getTime() == 0 && a.getPubDate().getTime() != 0)
				if (existingArt.getPubDate().getTime() < a.getPubDate().getTime())
				{
					Article.updatePubDate(h, existingArt.getId(), a.getPubDate());
				}
				updatedData.add(existingArt);
			}
		}
		//Log.i(LOG, "quontOfWrittenArticles: " + String.valueOf(quontOfWrittenArticles));
		return updatedData;
	}

	protected void onPostExecute(ArrayList<Article> updatedData)
	{
		this.callback.onDoneWritingArticles(updatedData, categoryToLoad, pageToLoad);
	}
}