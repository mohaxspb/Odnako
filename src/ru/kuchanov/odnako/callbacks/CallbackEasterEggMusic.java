/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.callbacks;


public interface CallbackEasterEggMusic
{
	/**
	 * called from OnPostExecute method of asyncTask, that's ask DB from top
	 * 
	 */
	public void onAnswerFromServer(String[] answer);
}
