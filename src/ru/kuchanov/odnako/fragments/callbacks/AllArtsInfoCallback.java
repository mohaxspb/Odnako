/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments.callbacks;

import java.util.ArrayList;

import ru.kuchanov.odnako.lists_and_utils.ArtInfo;

public interface AllArtsInfoCallback
{
	/**
	 * We use it to call service methods from asynkTask
	 * 
	 * @param someResult downloaded data to send
	 * @param categoryToLoad category on site from where we get data
	 * @param pageToLoad number of page, that contains each 30 arts
	 */
	public void sendDownloadedData(ArrayList<ArtInfo> someResult, String categoryToLoad, int pageToLoad);
	
	/**
	 * We use it to say user, that we have some error in asynkTask
	 * 
	 * @param e message to show to user
	 */
	public void onError(String e, String categoryToLoad, int pageToLoad);
}
