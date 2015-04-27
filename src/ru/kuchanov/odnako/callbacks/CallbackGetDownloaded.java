/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.callbacks;

import java.util.ArrayList;

import ru.kuchanov.odnako.db.Article;

public interface CallbackGetDownloaded
{
	/**
	 * called from OnPostExecute method of asyncTask
	 * 
	 * @param answer
	 */
	public void onGetDownloaded(ArrayList<Article> dataFromDB, String categoryToLoad, int pageToLoad);
}
