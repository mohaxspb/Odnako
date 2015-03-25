/*
 04.01.2015
FragArtUPD.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import android.os.Bundle;
import ru.kuchanov.odnako.db.Article;

/**
 * this is used to update fragment in ViewPager and not to recreate it by
 * returning POSITION_NONE in getItem() (?) method of ViewPager, which is
 * called, whrn notifyDataSetChanged() called
 */
public interface FragArtUPD
{
	/**
	 * Interface method for updating FragmentArticle
	 */
	public void update(Article allArtInfo, Bundle savedInstanceState);
//	public void update(Article allArtInfo);
	//	public void update(ArrayList<Article> allArtInfo);
}
