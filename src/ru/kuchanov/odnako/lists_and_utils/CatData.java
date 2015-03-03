/*
 14.11.2014
CatData.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.db.Author;
import android.content.Context;

public class CatData
{
	public static String[] getMenuNames(Context act)
	{
		String[] authorsMenuNames = act.getResources().getStringArray(R.array.authors);
		String[] categoriesMenuNames = act.getResources().getStringArray(R.array.categories);

		String[] allCategoriesMenuNames = CatData.concatArrays(authorsMenuNames, categoriesMenuNames);
		return allCategoriesMenuNames;
	}

	/**
	 * 
	 * @param act
	 * @return String array filled with links (urls) of catagories (and authors) represented in drawer menu
	 */
	public static String[] getMenuLinks(Context act)
	{
		String[] authorsMenuLinks = act.getResources().getStringArray(R.array.authors_links);

		String[] categoriesMenuLinks = act.getResources().getStringArray(R.array.categories_links);

		String[] allCategoriesMenuLinks = CatData.concatArrays(authorsMenuLinks, categoriesMenuLinks);
		return allCategoriesMenuLinks;
	}
	
	public static String[] getMenuDescriptions(Context act)
	{
		// TODO need to load it
		String[] categoriesLinks = new String[CatData.getAllTagsLinks(act).length];
		for(int i=0; i<categoriesLinks.length; i++)
		{
			categoriesLinks[i]="empty";
		}
		return categoriesLinks;
	}
	
	public static String[] getMenuImgsUrls(Context act)
	{
		String[] arr = act.getResources().getStringArray(R.array.authors_imgs_links);

		String[] arr2 = act.getResources().getStringArray(R.array.categories_imgs_urls);

		String[] allCategoriesMenuLinks = CatData.concatArrays(arr, arr2);
		return allCategoriesMenuLinks;
	}
	public static String[] getMenuImgsFilesNames(Context act)
	{
		String[] arr = act.getResources().getStringArray(R.array.authors_imgs_files_names);

		String[] arr2 = act.getResources().getStringArray(R.array.categories_imgs_files_names);

		String[] allCategoriesMenuLinks = CatData.concatArrays(arr, arr2);
		return allCategoriesMenuLinks;
	}
	
	public static String[] getAllTagsLinks(Context ctx)
	{
		String[] categoriesLinks = ctx.getResources().getStringArray(R.array.all_categories_urls);
		return categoriesLinks;
	}
	
	public static String[] getAllTagsNames(Context act)
	{
		String[] categoriesLinks = act.getResources().getStringArray(R.array.all_categories);
		return categoriesLinks;
	}
	
	public static String[] getAllTagsDescriptions(Context act)
	{
		// TODO need to load it
		String[] categoriesLinks = new String[CatData.getAllTagsLinks(act).length];
		for(int i=0; i<categoriesLinks.length; i++)
		{
			categoriesLinks[i]="empty";
		}
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
		//remove existing '/' at the end
		for(int i=0; i<categoriesLinks.length; i++)
		{
			categoriesLinks[i]=Author.getURLwithoutSlashAtTheEnd(categoriesLinks[i]);
		}
		return categoriesLinks;
	}
	
	public static String[] getAllAuthorsDescriptions(Context act)
	{
		String[] categoriesLinks = act.getResources().getStringArray(R.array.all_authors_descriptions);
		return categoriesLinks;
	}
	
	public static String[] getAllAuthorsWhos(Context act)
	{
		String[] categoriesLinks = act.getResources().getStringArray(R.array.all_authors_who);
		return categoriesLinks;
	}
	
	public static String[] getAllAuthorsAvatars(Context act)
	{
		String[] categoriesLinks = act.getResources().getStringArray(R.array.all_authors_imgs);
		return categoriesLinks;
	}
	
	public static String[] getAllAuthorsAvatarsBig(Context act)
	{
		String[] categoriesLinks = act.getResources().getStringArray(R.array.all_authors_big_imgs);
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
