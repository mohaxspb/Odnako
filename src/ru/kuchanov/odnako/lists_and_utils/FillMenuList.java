package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.R;

import android.support.v7.app.ActionBarActivity;

public class FillMenuList
{	
//	ArrayList<ArrayList<String>> groups = new ArrayList<ArrayList<String>>();
//	ArrayList<String> children1 = new ArrayList<String>();
//	ArrayList<String> children2 = new ArrayList<String>();
//	ArrayList<String> children3 = new ArrayList<String>();
//	ArrayList<String> children4 = new ArrayList<String>();
//	ArrayList<String> children5 = new ArrayList<String>();
//	
//	ArrayList<String> children6 = new ArrayList<String>();

	ArrayList<ArrayList<String>> groupsLinks = new ArrayList<ArrayList<String>>();
	ArrayList<String> children1Links = new ArrayList<String>();
	ArrayList<String> children2Links = new ArrayList<String>();

	ActionBarActivity act;

//	public void setActivity(ActionBarActivity act)
//	{
//		this.act = act;
//	}

	public static ArrayList<ArrayList<String>> getGroups(ActionBarActivity act)
	{
		ArrayList<ArrayList<String>> groups = new ArrayList<ArrayList<String>>();
		ArrayList<String> children1 = new ArrayList<String>();
		ArrayList<String> children2 = new ArrayList<String>();
		ArrayList<String> children3 = new ArrayList<String>();
		ArrayList<String> children4 = new ArrayList<String>();
		ArrayList<String> children5 = new ArrayList<String>();
		
		ArrayList<String> children6 = new ArrayList<String>();
		
		String[] menuCat = act.getResources().getStringArray(R.array.menu_items);
		for (int i = 0; i < menuCat.length; i++)
		{
			if (i == 0)
			{
				String[] firstCat = act.getResources().getStringArray(R.array.authors);
				for (int ii = 0; ii < firstCat.length; ii++)
				{
					children1.add(firstCat[ii]);
				}
				groups.add(children1);
			}
			else if (i == 1)
			{
				String[] secondCat = act.getResources().getStringArray(R.array.categories);
				for (int ii = 0; ii < secondCat.length; ii++)
				{
					children2.add(secondCat[ii]);
				}
				groups.add(children2);
			}
			else if (i == 2)
			{
				groups.add(children3);
			}
			else if (i == 3)
			{
				groups.add(children4);
			}
			else if (i==4)
			{
				groups.add(children5);
			}
			///test
			else if (i==5)
			{
				groups.add(children6);
			}
			///test
		}
		return groups;
	}

	public ArrayList<ArrayList<String>> getGroupsLinks()
	{       
		String[] menuCatLinks = act.getResources().getStringArray(R.array.menu_items);
		for (int i = 0; i < menuCatLinks.length; i++)
		{
			if (i == 0)
			{
				String[] firstCat = act.getResources().getStringArray(R.array.authors_links);
				for (int ii = 0; ii < firstCat.length; ii++)
				{
					children1Links.add(firstCat[ii]);
				}
				groupsLinks.add(children1Links);
			}
			else if (i == 1)
			{
				String[] secondCat = act.getResources().getStringArray(R.array.categories_links);
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
