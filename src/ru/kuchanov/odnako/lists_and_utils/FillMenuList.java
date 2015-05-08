package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.R;

import android.content.Context;

public class FillMenuList
{
	ArrayList<ArrayList<String>> groupsLinks = new ArrayList<ArrayList<String>>();
	ArrayList<String> children1Links = new ArrayList<String>();
	ArrayList<String> children2Links = new ArrayList<String>();

	Context ctx;

	public static ArrayList<ArrayList<String>> getGroups(Context ctx)
	{
		ArrayList<ArrayList<String>> groups = new ArrayList<ArrayList<String>>();
		ArrayList<String> children1 = new ArrayList<String>();
		ArrayList<String> children2 = new ArrayList<String>();
		ArrayList<String> children3 = new ArrayList<String>();
		ArrayList<String> children4 = new ArrayList<String>();

		String[] menuCat = ctx.getResources().getStringArray(R.array.menu_items);
		for (int i = 0; i < menuCat.length; i++)
		{
			switch (i)
			{
				case 0:
					String[] firstCat = ctx.getResources().getStringArray(R.array.authors);
					for (int ii = 0; ii < firstCat.length; ii++)
					{
						children1.add(firstCat[ii]);
					}
					groups.add(children1);
				break;
				case 1:
					String[] secondCat = ctx.getResources().getStringArray(R.array.categories);
					for (int ii = 0; ii < secondCat.length; ii++)
					{
						children2.add(secondCat[ii]);
					}
					groups.add(children2);
				break;
				case 2:
					groups.add(children3);
				break;
				case 3:
					groups.add(children4);
				break;
			}
		}
		return groups;
	}

	public ArrayList<ArrayList<String>> getGroupsLinks()
	{
		String[] menuCatLinks = ctx.getResources().getStringArray(R.array.menu_items);
		for (int i = 0; i < menuCatLinks.length; i++)
		{
			if (i == 0)
			{
				String[] firstCat = ctx.getResources().getStringArray(R.array.authors_links);
				for (int ii = 0; ii < firstCat.length; ii++)
				{
					children1Links.add(firstCat[ii]);
				}
				groupsLinks.add(children1Links);
			}
			else if (i == 1)
			{
				String[] secondCat = ctx.getResources().getStringArray(R.array.categories_links);
				for (int ii = 0; ii < secondCat.length; ii++)
				{
					children2Links.add(secondCat[ii]);
				}
				groupsLinks.add(children2Links);
			}
		}
		return groupsLinks;
	}
}