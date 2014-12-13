package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import ru.kuchanov.odnako.R;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

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
			//			System.out.println("alsoToRead (to_read_main) var is empty!");
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
			//			System.out.println("alsoToRead (to_read_more) var is empty!");
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

	public static void writeAllArtsInfoToBundle(Bundle b, ArrayList<ArtInfo> allArtsInfo, ArtInfo curArtInfo)
	{
		//save allArtsInfo
		if (allArtsInfo != null)
		{
			for (int i = 0; i < allArtsInfo.size(); i++)
			{
				if (i < 10)
				{
					b.putStringArray("allArtsInfo_0" + String.valueOf(i), allArtsInfo.get(i)
					.getArtInfoAsStringArray());
				}
				else
				{
					b.putStringArray("allArtsInfo_" + String.valueOf(i), allArtsInfo.get(i)
					.getArtInfoAsStringArray());
				}
			}
		}
		else
		{
			//			System.out.println("ArticleFragment: onSaveInstanceState. allArtsInfo=null");
		}
		//save curArtInfo
		if (curArtInfo != null)
		{
			b.putStringArray("curArtInfo", curArtInfo.getArtInfoAsStringArray());
		}
		else
		{
			//			System.out.println("ArticleFragment: onSaveInstanceState. curArtInfo=null");
		}
	}

	public static ArrayList<ArtInfo> restoreAllArtsInfoFromBundle(Bundle b, ActionBarActivity act)
	{
		ArrayList<ArtInfo> allArtsInfo = null;
		if (b.containsKey("allArtsInfo_00"))
		{
			//restore AllArtsInfo
			allArtsInfo = new ArrayList<ArtInfo>();
			Set<String> keySet = b.keySet();
			ArrayList<String> keySetSortedArrList = new ArrayList<String>(keySet);
			Collections.sort(keySetSortedArrList);
			for (int i = 0; i < keySetSortedArrList.size(); i++)
			{
				if (keySetSortedArrList.get(i).startsWith("allArtsInfo_"))
				{
					if (i < 10)
					{
						allArtsInfo.add(new ArtInfo(b.getStringArray("allArtsInfo_0"
						+ String.valueOf(i))));
					}
					else
					{
						allArtsInfo.add(new ArtInfo(b.getStringArray("allArtsInfo_"
						+ String.valueOf(i))));
					}

				}
				else
				{
					break;
				}
			}
		}
		else
		{
			System.out.println("this.allArtsInfo in Bundle in " + act.getClass().getSimpleName() + " =null");
		}

		return allArtsInfo;
	}

	public static ArtInfo getDefaultArtInfo()
	{
		String[] AllInfo = new String[17];

		AllInfo[0] = "url";
		AllInfo[1] = "title";
		AllInfo[2] = "img_art";
		AllInfo[3] = "authorBlogUrl";
		AllInfo[4] = "authorName";

		AllInfo[5] = "preview";
		AllInfo[6] = "pubDate";

		AllInfo[7] = String.valueOf(10);
		AllInfo[8] = String.valueOf(10);
		AllInfo[9] = "artText";
		AllInfo[10] = "authorDescr";
		AllInfo[11] = "tegs_main";
		AllInfo[12] = "tegs_all";
		AllInfo[13] = "share_quont";
		AllInfo[14] = "to_read_main";
		AllInfo[15] = "to_read_more";
		AllInfo[16] = "img_author";

		ArtInfo defArtInfo = new ArtInfo(AllInfo);

		return defArtInfo;
	}

	public static ArrayList<ArtInfo> getDefaultAllArtsInfo(ActionBarActivity act)
	{
		//fill Arraylist with artsInfo
		//sample data now
		ArrayList<ArtInfo> allArtsInfo = new ArrayList<ArtInfo>();
		int sampleNum = 30;
		for (int i = 0; i < sampleNum; i++)
		{
			ArtInfo artInfo = new ArtInfo("url_" + String.valueOf(i), "title_" + String.valueOf(i),
			"/i/75_75/users/7160/7160-1481-7160.jpg",
			"author_blog_link_" + String.valueOf(i), "author_name_" + String.valueOf(i));
			artInfo.updateArtInfoFromRSS("preview_" + String.valueOf(i), "date_" + String.valueOf(i));
			artInfo.updateArtInfoFromARTICLE(
			i,
			i,
			"art_text_" + String.valueOf(i),
			"author_description_" + String.valueOf(i),
			"tegs_main_" + String.valueOf(i),
			"tegs_all_" + String.valueOf(i) + " !!!! " + "tegs_all_" + String.valueOf(i) + " !!!! " + "tegs_all_"
			+ String.valueOf(i) + " !!!! " + "tegs_all_" + String.valueOf(i) + " !!!! " + "tegs_all_"
			+ String.valueOf(i) + " !!!! ",
			String.valueOf(i) + " !!!! " + String.valueOf(i) + " !!!! " + String.valueOf(i) + " !!!! "
			+ String.valueOf(i) + " !!!! " + String.valueOf(i) + " !!!! " + String.valueOf(i) + " !!!! ",
			"to_read_main_" + String.valueOf(i), "to_read_more_" + String.valueOf(i), "empty");
			allArtsInfo.add(artInfo);
		}

		ArtInfo artInfoTEST = new ArtInfo(
		"http://www.odnako.org/blogs/cifrovoy-front-latviyskiy-blickrig-i-nash-otvet/", "Заголовок статьи",
		"/i/75_75/users/7160/7160-1481-7160.jpg", "http://yuriykuchanov.odnako.org/",
		"Разработчик testetsetstetstestetstestetstetstetsetstetstetste setstestet");
		artInfoTEST.updateArtInfoFromRSS(act.getResources().getString(R.string.preview), "1 сентября 1939");
		artInfoTEST.updateArtInfoFromARTICLE(0, 0, act.getResources().getString(R.string.version_history),
		"Описание автора", "Интернет", "Интернет !!!! Андроид", "10 !!!! 10 !!!! 10 !!!! 10 !!!! 10 !!!! 10",
		"url !!!! title !!!! date !!!! url !!!! title !!!! date !!!! url !!!! title !!!! date",
		"url !!!! title !!!! date !!!! url !!!! title !!!! date",
		"https://pp.vk.me/c9733/u77102/151125793/w_91f2635a.jpg");
		allArtsInfo.set(1, artInfoTEST);
		//one more
		ArtInfo artInfoTEST2 = new ArtInfo("", "Заголовок статьи", "/i/75_75/users/7160/7160-1481-7160.jpg", "empty",
		"Разработчик");
		artInfoTEST2.updateArtInfoFromRSS("test_preview", "2 сентября 1945");
		artInfoTEST2.updateArtInfoFromARTICLE(0, 0, act.getResources().getString(R.string.version_history), "empty",
		"empty", "empty", "10 !!!! 10 !!!! 10 !!!! 10 !!!! 10 !!!! 10", "empty", "empty",
		"https://pp.vk.me/c9733/u77102/151125793/w_91f2635a.jpg");
		allArtsInfo.set(2, artInfoTEST2);

		return allArtsInfo;
	}

	public static void writeAllArtsInfoToBundle(Bundle b, ArrayList<ArtInfo> allArtsInfo, String category)
	{
		if (allArtsInfo != null)
		{
			for (int i = 0; i < allArtsInfo.size(); i++)
			{
				if (i < 10)
				{
					b.putStringArray(category + "_allArtsInfo_0" + String.valueOf(i), allArtsInfo.get(i)
					.getArtInfoAsStringArray());
				}
				else
				{
					b.putStringArray(category + "_allArtsInfo_" + String.valueOf(i), allArtsInfo.get(i)
					.getArtInfoAsStringArray());
				}
			}
		}
		else
		{
			//			System.out.println("ArticleFragment: onSaveInstanceState. allArtsInfo=null");
		}
	}

	public static ArrayList<ArtInfo> restoreAllArtsInfoFromBundle(Bundle b, String category)
	{
		//		System.out.println("category: "+category);
		//		System.out.println("b.containsKey(category+'_allArtsInfo_00'): "+String.valueOf(b.containsKey(category+"_allArtsInfo_00")));
		ArrayList<ArtInfo> allArtsInfo = null;
		if (b.containsKey(category + "_allArtsInfo_00"))
		{
			//restore AllArtsInfo
			allArtsInfo = new ArrayList<ArtInfo>();
			Set<String> keySet = b.keySet();
			ArrayList<String> keySetSortedArrList = new ArrayList<String>(keySet);
			Collections.sort(keySetSortedArrList);
			ArrayList<String> onlyNeededKeysArrList = new ArrayList<String>();
			for (int i = 0; i < keySetSortedArrList.size(); i++)
			{
				if (keySetSortedArrList.get(i).startsWith(category + "_allArtsInfo_"))
				{
					onlyNeededKeysArrList.add(keySetSortedArrList.get(i));
				}
			}
			///////
			for (int i = 0; i < onlyNeededKeysArrList.size(); i++)
			{
				if (onlyNeededKeysArrList.get(i).startsWith(category + "_allArtsInfo_"))
				{
					if (i < 10)
					{
						allArtsInfo.add(new ArtInfo(b.getStringArray(category + "_allArtsInfo_0"
						+ String.valueOf(i))));
					}
					else
					{
						allArtsInfo.add(new ArtInfo(b.getStringArray(category + "_allArtsInfo_"
						+ String.valueOf(i))));
					}
					//					System.out.println("allArtsInfo.get(allArtsInfo.size()-1).url: "
					//					+ allArtsInfo.get(allArtsInfo.size() - 1).url);
				}
			}
		}
		else
		{
			//			System.out.println("this.allArtsInfo in Bundle in " + act.getClass().getSimpleName() + " =null");
		}

		return allArtsInfo;
	}

}
