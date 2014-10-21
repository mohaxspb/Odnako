package ru.kuchanov.odnako.lists_and_utils;

import java.util.Arrays;
import java.util.Comparator;

public class ArtInfo implements Comparable<ArtInfo>
{

	private String[] AllInfo=new String[9];

	public String url, title, img, authorBlogUrl, authorName;
	
	public String preview="empty";
	public String pubDate="empty";
	
	public int numOfComments=0;
	public int numOfSharings=0;
	
	//public String numOfInfoPages;

	public ArtInfo(String[] artInfoArr)
	{

		this.url = artInfoArr[0];
		this.title = artInfoArr[1];
		this.img = artInfoArr[2];
		this.authorBlogUrl = artInfoArr[3];
		this.authorName = artInfoArr[4];
		
		this.AllInfo[0]=url;
		this.AllInfo[1]=title;
		this.AllInfo[2]=img;
		this.AllInfo[3]=authorBlogUrl;
		this.AllInfo[4]=authorName;
		this.AllInfo[5]="empty";
		this.AllInfo[6]="empty";
		this.AllInfo[7]="0";
		this.AllInfo[8]="0";
	}
	
	public ArtInfo(String url, String title, String img,String authorBlogUrl,String authorName)
	{

		this.url = url;
		this.title = title;
		this.img = img;
		this.authorBlogUrl = authorBlogUrl;
		this.authorName = authorName;
		
		this.AllInfo[0]=url;
		this.AllInfo[1]=title;
		this.AllInfo[2]=img;
		this.AllInfo[3]=authorBlogUrl;
		this.AllInfo[4]=authorName;
		this.AllInfo[5]="empty";
		this.AllInfo[6]="empty";
		this.AllInfo[7]="0";
		this.AllInfo[8]="0";
	}
	
	public void updateArtInfo(String preView, String pubDate, int numOfComments, int numOfSharings)
	{
		this.preview=preView;
		this.pubDate=pubDate;
		this.numOfComments=numOfComments;
		this.numOfSharings=numOfSharings;
		
		this.AllInfo[5]=preView;
		this.AllInfo[6]=pubDate;
		this.AllInfo[7]=String.valueOf(numOfComments);
		this.AllInfo[8]=String.valueOf(numOfSharings);
	}

	@Override
	public String toString()
	{
		return Arrays.toString(this.AllInfo);
	}

	public int compareTo(ArtInfo other)
	{
		return this.title.compareTo(other.title);
	}

	public static Comparator<ArtInfo> COMPARE_BY_TITLE = new Comparator<ArtInfo>()
	{
		public int compare(ArtInfo one, ArtInfo other)
		{
			return one.title.compareTo(other.title);
		}
	};

}

