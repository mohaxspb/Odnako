/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.callbacks;

import java.util.ArrayList;

import ru.kuchanov.odnako.db.Article;

public interface CallbackAskDBFromTop
{
	/**
	 * called from OnPostExecute method of asyncTask, that's ask DB from top
	 * 
	 */
	public void onAnswerFromDBFromTop(String answer, String categoryToLoad, int pageToLoad, ArrayList<Article> dataToSend);
}
