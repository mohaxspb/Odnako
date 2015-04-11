/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.callbacks;

import ru.kuchanov.odnako.db.Article;

public interface CallbackDownloadArticle
{
	/**
	 * called from OnPostExecute method of asyncTask
	 * 
	 * @param answer
	 */
	public void onDoneDownloadingArticle(Article downloadedArticle, boolean isMultipleTask, int iterator, int quontity);

	public void onErrorWhileDownloadingArticle(String error, String url, boolean isMultipleTask, int iterator, int quontity);
}
