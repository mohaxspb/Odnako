/*
 17.11.2014
AllAuthorsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import ru.kuchanov.odnako.R;
import android.support.v7.app.ActionBarActivity;

public class AllAuthorsInfo
{
	ActionBarActivity act;

	String[] allNames;
	String[] allBlogLinks;
	String[] allAvaImgsUrls;
	String[] allDescription;
	String[] allWho;
	/**
	 * 
	 */
	public AllAuthorsInfo()
	{
		// TODO Auto-generated constructor stub
	}
	
	private void init()
	{
		this.allAvaImgsUrls=this.act.getResources().getStringArray(R.array.all_authors_imgs);
		this.allBlogLinks=this.act.getResources().getStringArray(R.array.all_authors_urls);
		this.allWho=this.act.getResources().getStringArray(R.array.all_authors_descriptions);
		this.allNames=this.act.getResources().getStringArray(R.array.all_authors_names);
	}

	class AuthorInfo
	{
		String name;
		String blogLink;
		String avaImg;
		String description;
		String who;
	}
}
