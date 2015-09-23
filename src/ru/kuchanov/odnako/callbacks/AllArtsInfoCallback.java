/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.callbacks;

import java.util.ArrayList;

import ru.kuchanov.odnako.db.Article;

public interface AllArtsInfoCallback
{
	/**
	 * We use it to call service methods from asynkTask
	 * 
	 * @param dataFromWeb downloaded data to send
	 * @param categoryToLoad category on site from where we get data
	 * @param pageToLoad number of page, that contains each 30 arts
	 */
	public void sendDownloadedData(ArrayList<Article> dataFromWeb, String categoryToLoad, int pageToLoad);
	
	/**
	 * We use it to say user, that we have some error in asynkTask.
	 * Now we (as i know, and who knows more?..) use it only in 2 cases:
	 * 1) On result=null in onPostExecute in AsyncTask
	 * 2) When we canceling AsyncTask from service when their quantity>4
	 * 
	 * @param e message to show to user
	 */
	public void onError(String e, String categoryToLoad, int pageToLoad);
}
