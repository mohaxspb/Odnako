/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.callbacks;

import java.util.ArrayList;

import ru.kuchanov.odnako.db.Article;

public interface CallbackAskDBFromBottom
{
	/**
	 * called from OnPostExecute method of asyncTask, that's ask DB from top
	 * 
	 * @param answer
	 */
	//	public void onAnswerFromDBFromBottom(String answer, String categoryToLoad, int pageToLoad);
	public void onAnswerFromDBFromBottom(String answer, String categoryToLoad, int pageToLoad,
	ArrayList<Article> dataToSend);
}
