package ru.kuchanov.odnako.lists_and_utils;

import java.util.Arrays;
import java.util.Comparator;

public class ArtInfo implements Comparable<ArtInfo>
{

	private String[] AllInfo = new String[17];

	//get it from blogs page
	public String url, title, img_art, authorBlogUrl, authorName;

	//get it from RSS page
	public String preview, pubDate = "empty";
	//public String pubDate = "empty";

	//get it from article page
	public int numOfComments, numOfSharings = 0;
	public String artText, authorDescr, tegs_main, tegs_all, share_quont, to_read_main, to_read_more,
	img_author = "empty";

	/**
	 * 
	 * @param artInfoArr
	 *            16 parameters, including: url, title, img, authorBlogUrl,
	 *            authorName, preview, pubDate, numOfComments, numOfSharings,
	 *            artText, authorDescr, tegs_main, tegs_all, share_quont,
	 *            to_read_main, to_read_more
	 */
	public ArtInfo(String[] artInfoArr)
	{
		//from blogs page
		if (artInfoArr.length == 5)
		{
			this.url = artInfoArr[0];
			this.title = artInfoArr[1];
			this.img_art = artInfoArr[2];
			this.authorBlogUrl = artInfoArr[3];
			this.authorName = artInfoArr[4];

			this.preview = "empty";
			this.pubDate = "empty";

			this.numOfComments = 0;
			this.numOfSharings = 0;
			this.artText = "empty";
			this.authorDescr = "empty";
			this.tegs_main = "empty";
			this.tegs_all = "empty";
			this.share_quont = "empty";
			this.to_read_main = "empty";
			this.to_read_more = "empty";

			this.AllInfo[0] = url;
			this.AllInfo[1] = title;
			this.AllInfo[2] = img_art;
			this.AllInfo[3] = authorBlogUrl;
			this.AllInfo[4] = authorName;

			this.AllInfo[5] = "empty";
			this.AllInfo[6] = "empty";

			this.AllInfo[7] = "0";
			this.AllInfo[8] = "0";
			this.AllInfo[9] = "empty";
			this.AllInfo[10] = "empty";
			this.AllInfo[11] = "empty";
			this.AllInfo[12] = "empty";
			this.AllInfo[13] = "empty";
			this.AllInfo[14] = "empty";
			this.AllInfo[15] = "empty";
			this.AllInfo[16] = "empty";
		}
		//from RSS
		else if (artInfoArr.length == 7)
		{
			this.url = artInfoArr[0];
			this.title = artInfoArr[1];
			this.img_art = artInfoArr[2];
			this.authorBlogUrl = artInfoArr[3];
			this.authorName = artInfoArr[4];

			this.preview = artInfoArr[5];
			this.pubDate = artInfoArr[6];

			this.numOfComments = 0;
			this.numOfSharings = 0;
			this.artText = "empty";
			this.authorDescr = "empty";
			this.tegs_main = "empty";
			this.tegs_all = "empty";
			this.share_quont = "empty";
			this.to_read_main = "empty";
			this.to_read_more = "empty";

			this.AllInfo[0] = url;
			this.AllInfo[1] = title;
			this.AllInfo[2] = img_art;
			this.AllInfo[3] = authorBlogUrl;
			this.AllInfo[4] = authorName;

			this.AllInfo[5] = preview;
			this.AllInfo[6] = pubDate;

			this.AllInfo[7] = "0";
			this.AllInfo[8] = "0";

			this.AllInfo[9] = "empty";
			this.AllInfo[10] = "empty";
			this.AllInfo[11] = "empty";
			this.AllInfo[12] = "empty";
			this.AllInfo[13] = "empty";
			this.AllInfo[14] = "empty";
			this.AllInfo[15] = "empty";
			this.AllInfo[16] = "empty";
		}
		//from article
		else if (artInfoArr.length == 17)
		{
			this.url = artInfoArr[0];
			this.title = artInfoArr[1];
			this.img_art = artInfoArr[2];
			this.authorBlogUrl = artInfoArr[3];
			this.authorName = artInfoArr[4];

			this.preview = artInfoArr[5];
			this.pubDate = artInfoArr[6];

			this.numOfComments = Integer.parseInt(artInfoArr[7]);
			this.numOfSharings = Integer.parseInt(artInfoArr[8]);
			this.artText = artInfoArr[9];
			this.authorDescr = artInfoArr[10];
			this.tegs_main = artInfoArr[11];
			this.tegs_all = artInfoArr[12];
			this.share_quont = artInfoArr[13];
			this.to_read_main = artInfoArr[14];
			this.to_read_more = artInfoArr[15];
			this.img_author = artInfoArr[16];

			this.AllInfo[0] = url;
			this.AllInfo[1] = title;
			this.AllInfo[2] = img_art;
			this.AllInfo[3] = authorBlogUrl;
			this.AllInfo[4] = authorName;

			this.AllInfo[5] = preview;
			this.AllInfo[6] = pubDate;

			this.AllInfo[7] = String.valueOf(numOfComments);
			this.AllInfo[8] = String.valueOf(numOfSharings);
			this.AllInfo[9] = this.artText;
			this.AllInfo[10] = this.authorDescr;
			this.AllInfo[11] = this.tegs_main;
			this.AllInfo[12] = this.tegs_all;
			this.AllInfo[13] = this.share_quont;
			this.AllInfo[14] = this.to_read_main;
			this.AllInfo[15] = this.to_read_more;
			this.AllInfo[16] = this.img_author;

		}
		else
		{
			System.out
			.println("ArtInfo construcror. Invalid arr lenght. It can't be, yes? In other case: WTF, MOTHERFUCKER?!");
		}
	}

	public ArtInfo(String url, String title, String img, String authorBlogUrl, String authorName)
	{

		this.url = url;
		this.title = title;
		this.img_art = img;
		this.authorBlogUrl = authorBlogUrl;
		this.authorName = authorName;

		this.preview = "empty";
		this.pubDate = "empty";

		this.numOfComments = 0;
		this.numOfSharings = 0;
		this.artText = "empty";
		this.authorDescr = "empty";
		this.tegs_main = "empty";
		this.tegs_all = "empty";
		this.share_quont = "empty";
		this.to_read_main = "empty";
		this.to_read_more = "empty";

		this.AllInfo[0] = url;
		this.AllInfo[1] = title;
		this.AllInfo[2] = img;
		this.AllInfo[3] = authorBlogUrl;
		this.AllInfo[4] = authorName;

		this.AllInfo[5] = "empty";
		this.AllInfo[6] = "empty";

		this.AllInfo[7] = "0";
		this.AllInfo[8] = "0";
		this.AllInfo[9] = "empty";
		this.AllInfo[10] = "empty";
		this.AllInfo[11] = "empty";
		this.AllInfo[12] = "empty";
		this.AllInfo[13] = "empty";
		this.AllInfo[14] = "empty";
		this.AllInfo[15] = "empty";
		this.AllInfo[16] = "empty";
	}

	public void updateArtInfoFromRSS(String preView, String pubDate)
	{
		this.preview = preView;
		this.pubDate = pubDate;

		this.AllInfo[5] = preView;
		this.AllInfo[6] = pubDate;
	}

	public void updateArtInfoFromARTICLE(int numOfComments, int numOfSharings, String artText, String authorDescr,
	String tegs_main, String tegs_all, String share_quont, String to_read_main,
	String to_read_more, String img_author)
	{

		this.numOfComments = numOfComments;
		this.numOfSharings = numOfSharings;
		this.artText = artText;
		this.authorDescr = authorDescr;
		this.tegs_main = tegs_main;
		this.tegs_all = tegs_all;
		this.share_quont = share_quont;
		this.to_read_main = to_read_main;
		this.to_read_more = to_read_more;
		this.img_author = img_author;

		this.AllInfo[7] = String.valueOf(this.numOfComments);
		this.AllInfo[8] = String.valueOf(this.numOfSharings);
		this.AllInfo[9] = this.artText;
		this.AllInfo[10] = this.authorDescr;
		this.AllInfo[11] = this.tegs_main;
		this.AllInfo[12] = this.tegs_all;
		this.AllInfo[13] = this.share_quont;
		this.AllInfo[14] = this.to_read_main;
		this.AllInfo[15] = this.to_read_more;
		this.AllInfo[16] = this.img_author;
	}

	public String[] getArtInfoAsStringArray()
	{
		return this.AllInfo;
	}

	public String[] getAllTegsArr()
	{
		String[] allTegsArr;

		if (!this.tegs_all.equals("empty"))
		{
			allTegsArr = this.tegs_all.split(" !!!! ");
		}
		else
		{
			System.out.println("AllTegs var is empty!");
			allTegsArr = null;
		}

		return allTegsArr;
	}

	public AlsoToRead getAlsoByTheme()
	{
		AlsoToRead alsoToRead;
		String[] allInfo;

		if (!this.to_read_main.equals("empty"))
		{
			allInfo = this.to_read_main.split(" !!!! ");
			String[] titles = new String[allInfo.length / 3];
			String[] urls = new String[allInfo.length / 3];
			String[] dates = new String[allInfo.length / 3];
			for (int i = 0; i < allInfo.length / 3; i++)
			{
				titles[i] = allInfo[0 + i * 3];
				urls[i] = allInfo[1 + i * 3];
				dates[i] = allInfo[2 + i * 3];
			}
			alsoToRead = new AlsoToRead(titles, urls, dates);
		}
		else
		{
			System.out.println("alsoToRead (to_read_main) var is empty!");
			alsoToRead = null;
		}

		return alsoToRead;
	}

	public AlsoToRead getAlsoToReadMore()
	{
		AlsoToRead alsoToRead;
		String[] allInfo;

		if (!this.to_read_more.equals("empty"))
		{
			allInfo = this.to_read_more.split(" !!!! ");
			String[] titles = new String[allInfo.length / 3];
			String[] urls = new String[allInfo.length / 3];
			String[] dates = new String[allInfo.length / 3];
			for (int i = 0; i < allInfo.length / 3; i++)
			{
				titles[i] = allInfo[0 + i * 3];
				urls[i] = allInfo[1 + i * 3];
				dates[i] = allInfo[2 + i * 3];
			}
			alsoToRead = new AlsoToRead(titles, urls, dates);
		}
		else
		{
			System.out.println("alsoToRead (to_read_more) var is empty!");
			alsoToRead = null;
		}

		return alsoToRead;
	}

	@Override
	public String toString()
	{
		return Arrays.toString(this.AllInfo);
	}

	@Override
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

	//	public static Comparator<ArtInfo> COMPARE_BY_AUTHOR = new Comparator<ArtInfo>()
	//	{
	//		public int compare(ArtInfo one, ArtInfo other)
	//		{
	//			return one.title.compareTo(other.title);
	//		}
	//	};
	public class AlsoToRead
	{
		public String[] urls;
		public String[] titles;

		public String[] dates;

		public AlsoToRead(String[] urls, String[] titles, String[] dates)
		{
			this.urls = urls;
			this.titles = titles;

			this.dates = dates;
		}
	}

}
