package ru.kuchanov.odnako;

import java.util.Comparator;

public class MainInfo implements Comparable<MainInfo>
{

	public String[] MainInfo;

	public String linkToArt;
	public String title;
	public String img;
	public String authorBlogLink;
	public String authorName;

	public String numOfInfoPages;

	public MainInfo(String[] artInfoArr)
	{

		this.linkToArt = artInfoArr[0];
		this.title = artInfoArr[1];
		this.img = artInfoArr[2];
		this.authorBlogLink = artInfoArr[3];
		this.authorName = artInfoArr[4];

		this.MainInfo = artInfoArr;
	}

	@Override
	public String toString()
	{
		return this.title;
	}

	public int compareTo(MainInfo other)
	{
		return this.title.compareTo(other.title);
	}

	public static Comparator<MainInfo> COMPARE_BY_TITLE = new Comparator<MainInfo>()
	{
		public int compare(MainInfo one, MainInfo other)
		{
			return one.title.compareTo(other.title);
		}
	};

}

