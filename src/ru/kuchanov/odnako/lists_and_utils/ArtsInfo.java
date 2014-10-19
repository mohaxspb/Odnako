package ru.kuchanov.odnako.lists_and_utils;

import java.util.Comparator;

public class ArtsInfo implements Comparable<ArtsInfo>
{

	public String[] MainInfo;

	public String url;
	public String title;
	public String img;
	public String authorBlogUrl;
	public String authorName;

	public String numOfInfoPages;

	public ArtsInfo(String[] artInfoArr)
	{

		this.url = artInfoArr[0];
		this.title = artInfoArr[1];
		this.img = artInfoArr[2];
		this.authorBlogUrl = artInfoArr[3];
		this.authorName = artInfoArr[4];

		this.MainInfo = artInfoArr;
	}

	@Override
	public String toString()
	{
		return this.title;
	}

	public int compareTo(ArtsInfo other)
	{
		return this.title.compareTo(other.title);
	}

	public static Comparator<ArtsInfo> COMPARE_BY_TITLE = new Comparator<ArtsInfo>()
	{
		public int compare(ArtsInfo one, ArtsInfo other)
		{
			return one.title.compareTo(other.title);
		}
	};

}

