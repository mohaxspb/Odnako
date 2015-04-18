/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.callbacks;

import java.util.ArrayList;

import ru.kuchanov.odnako.download.CommentInfo;

public interface CallbackComments
{
	/**
	 * called from OnPostExecute method of asyncTask
	 * 
	 * @param answer
	 */
	public void onDoneLoadingComments(String resultMessage, ArrayList<CommentInfo> dataFromWeb, String articlesUrl,
	int pageToLoad);
}
