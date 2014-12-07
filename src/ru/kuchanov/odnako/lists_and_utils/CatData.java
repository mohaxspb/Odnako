/*
 14.11.2014
CatData.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ru.kuchanov.odnako.R;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;

public class CatData
{
	HashMap<String, ArrayList<ArtInfo>> allCatArtsInfo;

	static String[] authorsMenuNames;
	static String[] authorsMenuLinks;

	static String[] categoriesMenuNames;
	static String[] categoriesMenuLinks;

	/**
	 * 
	 */
	public CatData()
	{
		// TODO Auto-generated constructor stub
	}

//	public static HashMap<String, ArrayList<ArtInfo>> getAllCatArtsInfoFromDB(long requestTime, ActionBarActivity act)
//	{
//		//init arrays from /res
//		authorsMenuNames = act.getResources().getStringArray(R.array.authors);
//		authorsMenuLinks = act.getResources().getStringArray(R.array.authors_links);
//
//		categoriesMenuNames = act.getResources().getStringArray(R.array.categories);
//		categoriesMenuLinks = act.getResources().getStringArray(R.array.categories_links);
//
//		//here we'll do request for database and get data from it.
//		//Then, if data in requestTime-DB_sinchTime bigger then some constant
//		//we'll start url-request;
//		//NOW DUMMY DATA FOR TEST
//		HashMap<String, ArrayList<ArtInfo>> getAllCatArtsInfoFromDB = new HashMap<String, ArrayList<ArtInfo>>();
//		//-1, because last is all authors
//		for (int i = 0; i < authorsMenuLinks.length - 1; i++)
//		{
//			getAllCatArtsInfoFromDB.put(authorsMenuLinks[i], ArtInfo.getDefaultAllArtsInfo(act));
//		}
//		for (int i = 0; i < categoriesMenuLinks.length - 1; i++)
//		{
//			getAllCatArtsInfoFromDB.put(categoriesMenuLinks[i], ArtInfo.getDefaultAllArtsInfo(act));
//		}
//
//		return getAllCatArtsInfoFromDB;
//	}
	
	public static HashMap<String, ArrayList<ArtInfo>> getAllCatArtsInfoFromDB(long requestTime, ActionBarActivity act)
	{
		//init arrays from /res
		authorsMenuNames = act.getResources().getStringArray(R.array.authors);
		authorsMenuLinks = act.getResources().getStringArray(R.array.authors_links);

		categoriesMenuNames = act.getResources().getStringArray(R.array.categories);
		categoriesMenuLinks = act.getResources().getStringArray(R.array.categories_links);

		//here we'll do request for database and get data from it.
		//Then, if data in requestTime-DB_sinchTime bigger then some constant
		//we'll start url-request;
		//NOW DUMMY DATA FOR TEST
		HashMap<String, ArrayList<ArtInfo>> getAllCatArtsInfoFromDB = new HashMap<String, ArrayList<ArtInfo>>();
		for (int i = 0; i < authorsMenuLinks.length; i++)
		{
			//allAutors, so pass null to check it later and show all Authors
			if(i==authorsMenuLinks.length-1)
			{
				getAllCatArtsInfoFromDB.put(authorsMenuLinks[i], null);
			}
			else
			{
				getAllCatArtsInfoFromDB.put(authorsMenuLinks[i], ArtInfo.getDefaultAllArtsInfo(act));
			}
		}
		for (int i = 0; i < categoriesMenuLinks.length - 1; i++)
		{
//			getAllCatArtsInfoFromDB.put(categoriesMenuLinks[i], ArtInfo.getDefaultAllArtsInfo(act));
			//allCategories, so pass null to check it later and show allCategories
			if(i==categoriesMenuLinks.length-1)
			{
				getAllCatArtsInfoFromDB.put(categoriesMenuLinks[i], null);
			}
			else
			{
				getAllCatArtsInfoFromDB.put(categoriesMenuLinks[i], ArtInfo.getDefaultAllArtsInfo(act));
			}
		}

		return getAllCatArtsInfoFromDB;
	}

//	public static String[] getAllCategoriesMenuNames(ActionBarActivity act)
	public static String[] getAllCategoriesMenuNames(Context act)
	{
		authorsMenuNames = act.getResources().getStringArray(R.array.authors);
		authorsMenuLinks = act.getResources().getStringArray(R.array.authors_links);

		categoriesMenuNames = act.getResources().getStringArray(R.array.categories);
		categoriesMenuLinks = act.getResources().getStringArray(R.array.categories_links);

		String[] allCategoriesMenuNames = CatData.concatArrays(authorsMenuNames, categoriesMenuNames);
		return allCategoriesMenuNames;
	}

//	public static String[] getAllCategoriesMenuLinks(ActionBarActivity act)
	public static String[] getAllCategoriesMenuLinks(Context act)
	{
		authorsMenuLinks = act.getResources().getStringArray(R.array.authors_links);

		categoriesMenuLinks = act.getResources().getStringArray(R.array.categories_links);

		String[] allCategoriesMenuLinks = CatData.concatArrays(authorsMenuLinks, categoriesMenuLinks);
		return allCategoriesMenuLinks;
	}
	
	public static String[] getAllTagsLinks(Context act)
	{
		String[] categoriesLinks = act.getResources().getStringArray(R.array.all_categories_urls);
		return categoriesLinks;
	}
	
	public static String[] getAllTagsNames(Context act)
	{
		String[] categoriesLinks = act.getResources().getStringArray(R.array.all_categories);
		return categoriesLinks;
	}
	
	public static String[] getAllTagsImgsURLs(Context act)
	{
		String[] categoriesLinks = act.getResources().getStringArray(R.array.all_categories_imgs);
		return categoriesLinks;
	}
	
	public static String[] getAllTagsImgsFILEnames(Context act)
	{
		String[] categoriesLinks = act.getResources().getStringArray(R.array.all_categories_imgs_files_names);
		return categoriesLinks;
	}
	
	//////authors
	public static String[] getAllAuthorsNames(Context act)
	{
		String[] categoriesLinks = act.getResources().getStringArray(R.array.all_authors_names);
		return categoriesLinks;
	}
	
	public static String[] getAllAuthorsBlogsURLs(Context act)
	{
		String[] categoriesLinks = act.getResources().getStringArray(R.array.all_authors_urls);
		return categoriesLinks;
	}

	public static String[] concatArrays(String[] first, String[] second)
	{
		List<String> both = new ArrayList<String>(first.length + second.length);
		Collections.addAll(both, first);
		Collections.addAll(both, second);
		return both.toArray(new String[both.size()]);
	}
}
