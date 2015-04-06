/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.callbacks;

import java.util.ArrayList;

import ru.kuchanov.odnako.db.Article;

public interface CallbackWriteFromBottom
{
	/**
	 * called from OnPostExecute method of asyncTask
	 * 
	 * @param answer
	 */
	public void onDoneWritingFromBottom(String[] resultMessage, ArrayList<Article> dataFromWeb, String categoryToLoad,
	int pageToLoad);
}
