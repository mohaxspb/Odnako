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
//	public void doSomething(ArrayList<ArtInfo> someResult, String categoryToLoad);

	public void doSomething(ArrayList<ArtInfo> someResult, String categoryToLoad, int pageToLoad);
}
