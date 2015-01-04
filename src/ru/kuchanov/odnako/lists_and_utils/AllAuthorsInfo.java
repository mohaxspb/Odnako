/*
 17.11.2014
AllAuthorsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.R;
import android.support.v7.app.ActionBarActivity;

/**
 * Data model of all info of all Authors, storred in resources.
 * 
 * Contains String[]  allNames,	 allBlogLinks, allAvaImgsUrls, allAvaBIGImgsUrls, allDescription, allWho;
 */
public class AllAuthorsInfo
{
	ActionBarActivity act;

	String[] allNames;
	String[] allBlogLinks;
	String[] allAvaImgsUrls;
	String[] allAvaBIGImgsUrls;
	String[] allDescription;
	String[] allWho;

	public static int numOfAuthors;

	/**
	 * 
	 */
	public AllAuthorsInfo(ActionBarActivity act)
	{
		this.act = act;
		this.init();
	}

	private void init()
	{
		this.allAvaImgsUrls = this.act.getResources().getStringArray(R.array.all_authors_imgs);
		this.allAvaBIGImgsUrls = this.act.getResources().getStringArray(R.array.all_authors_big_imgs);
		this.allBlogLinks = this.act.getResources().getStringArray(R.array.all_authors_urls);
		this.allWho = this.act.getResources().getStringArray(R.array.all_authors_descriptions);
		this.allDescription = act.getResources().getStringArray(R.array.all_authors_who);
		this.allNames = this.act.getResources().getStringArray(R.array.all_authors_names);

		numOfAuthors = this.allNames.length;
	}

	class AuthorInfo
	{
		String name;
		String blogLink;
		String avaImg;
		String avaImgBig;
		String description;
		String who;

		AuthorInfo(String name, String blogLink, String avaImg, String avaImgBig, String description, String who)
		{
			this.avaImg = avaImg;
			this.name = name;
			this.blogLink = blogLink;
			this.avaImgBig = avaImgBig;
			this.description = description;
			this.who = who;
		}
	}

	public ArrayList<AuthorInfo> getAllAuthorsInfoAsList()
	{
		ArrayList<AuthorInfo> getAllAuthorsInfoAsList = new ArrayList<AllAuthorsInfo.AuthorInfo>();
		for (int i = 0; i < AllAuthorsInfo.numOfAuthors; i++)
		{
			getAllAuthorsInfoAsList.add(new AuthorInfo(this.allNames[i], this.allBlogLinks[i], this.allAvaImgsUrls[i],
			this.allAvaBIGImgsUrls[i], this.allDescription[i], this.allWho[i]));
		}
		return getAllAuthorsInfoAsList;
	}
}
