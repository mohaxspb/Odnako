/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.callbacks;

import java.util.ArrayList;

import ru.kuchanov.odnako.db.Article;

public interface CallbackWriteArticles
{
	/**
	 * called from OnPostExecute method of asyncTask
	 * 
	 * @param answer
	 */
	public void onDoneWritingArticles(ArrayList<Article> dataFromDB, String categoryToLoad, int pageToLoad);
}
