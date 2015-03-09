/*
 09.12.2014
Article.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

//tags for logCat
//tag:^(?!dalvikvm) tag:^(?!libEGL) tag:^(?!Open) tag:^(?!Google) tag:^(?!resour) tag:^(?!Chore) tag:^(?!EGL)

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo.AlsoToRead;
import ru.kuchanov.odnako.utils.DateParse;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.table.DatabaseTable;

/**
 * id, url, title, img_art, authorBlogUrl, authorName, preview, pubDate,
 * refreshed, numOfComments, numOfSharings, artText, authorDescr, tegs_main,
 * tegs_all, share_quont, to_read_main, to_read_more, img_author, author
 */
@DatabaseTable(tableName = "article")
public class Article implements Parcelable
{
	private static final String LOG = Article.class.getSimpleName();

	public final static String ID_FIELD_NAME = "id";
	public final static String AUTHOR_FIELD_NAME = "author";
	public final static String URL_FIELD_NAME = "url";
	public final static String FIELD_NAME_PUB_DATE = "pubDate";

	@DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
	private int id;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false, columnName = URL_FIELD_NAME)
	private String url;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false)
	private String title;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String imgArt;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String authorBlogUrl;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String authorName;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String preview;

	@DatabaseField(dataType = DataType.DATE, canBeNull = false, columnName = FIELD_NAME_PUB_DATE)
	private Date pubDate;

	@DatabaseField(dataType = DataType.DATE, canBeNull = false)
	private Date refreshed = new Date(0);

	@DatabaseField(dataType = DataType.INTEGER, canBeNull = true)
	private int numOfComments = 0;

	@DatabaseField(dataType = DataType.INTEGER, canBeNull = true)
	private int numOfSharings = 0;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String artText = "empty";

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String authorDescr = "empty";

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String tegs_main = "empty";

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String tegs_all = "empty";

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String share_quont = "empty";

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String to_read_main = "empty";

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String to_read_more = "empty";

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String img_author = "empty";

	//foreignKeys
	@DatabaseField(foreign = true, columnName = AUTHOR_FIELD_NAME, canBeNull = true)
	private Author author;

	/**
	 * we need empty constructor for OrmLite
	 */
	public Article()
	{

	}

	/**
	 * 
	 * @param artInfoArr
	 *            String[17] with article data from ArtInfo object. We get these
	 *            info from web
	 * @param refreshed
	 *            date when article was refreshed (loaded text of it) it's must
	 *            be null || 0 if we set initial article info from list of arts
	 *            from site
	 * @param author
	 *            Author object of article. Can be null;
	 */
	public Article(String[] artInfoArr, Date refreshed, Author author)
	{
		this.url = artInfoArr[0];
		this.title = artInfoArr[1];
		this.imgArt = artInfoArr[2];
		this.authorBlogUrl = artInfoArr[3];
		this.authorName = artInfoArr[4];

		this.preview = artInfoArr[5];

		//date; here can be 2 different ways of storing date, that we recive from site, so get them from util
		this.pubDate = DateParse.parse(artInfoArr[6]);
		//end of date

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

		//refreshed date
		//set it only if we have not null or "empty" artText value
		//and, ofcours not null given Date refreshed
		if (this.artText != null)
		{
			if (!this.artText.equals("empty"))
			{
				if (refreshed != null)
				{
					this.refreshed = refreshed;
				}
				else
				{
					this.refreshed = new Date(0);
				}
			}
			else
			{
				this.refreshed = new Date(0);
			}
		}
		else
		{
			this.refreshed = new Date(0);
		}

		//author object
		if (author != null)
		{
			this.author = author;
		}
	}

	public int getId()
	{
		return id;
	}

	//	public void setId(int id)
	//	{
	//		this.id = id;
	//	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getImgArt()
	{
		return imgArt;
	}

	public void setImgArt(String imgArt)
	{
		this.imgArt = imgArt;
	}

	public String getAuthorBlogUrl()
	{
		return authorBlogUrl;
	}

	public void setAuthorBlogUrl(String authorBlogUrl)
	{
		this.authorBlogUrl = authorBlogUrl;
	}

	public String getAuthorName()
	{
		return authorName;
	}

	public void setAuthorName(String authorName)
	{
		this.authorName = authorName;
	}

	public String getPreview()
	{
		return preview;
	}

	public void setPreview(String preview)
	{
		this.preview = preview;
	}

	public Date getPubDate()
	{
		return pubDate;
	}

	public void setPubDate(Date pubDate)
	{
		this.pubDate = pubDate;
	}

	public Date getRefreshed()
	{
		return refreshed;
	}

	public void setRefreshed(Date refreshed)
	{
		this.refreshed = refreshed;
	}

	public int getNumOfComments()
	{
		return numOfComments;
	}

	public void setNumOfComments(int numOfComments)
	{
		this.numOfComments = numOfComments;
	}

	public int getNumOfSharings()
	{
		return numOfSharings;
	}

	public void setNumOfSharings(int numOfSharings)
	{
		this.numOfSharings = numOfSharings;
	}

	public String getArtText()
	{
		return artText;
	}

	public void setArtText(String artText)
	{
		this.artText = artText;
	}

	public String getAuthorDescr()
	{
		return authorDescr;
	}

	public void setAuthorDescr(String authorDescr)
	{
		this.authorDescr = authorDescr;
	}

	public String getTegs_main()
	{
		return tegs_main;
	}

	public void setTegs_main(String tegs_main)
	{
		this.tegs_main = tegs_main;
	}

	public String getTegs_all()
	{
		return tegs_all;
	}

	public void setTegs_all(String tegs_all)
	{
		this.tegs_all = tegs_all;
	}

	public String getShare_quont()
	{
		return share_quont;
	}

	public void setShare_quont(String share_quont)
	{
		this.share_quont = share_quont;
	}

	public String getTo_read_main()
	{
		return to_read_main;
	}

	public void setTo_read_main(String to_read_main)
	{
		this.to_read_main = to_read_main;
	}

	public String getTo_read_more()
	{
		return to_read_more;
	}

	public void setTo_read_more(String to_read_more)
	{
		this.to_read_more = to_read_more;
	}

	public String getImg_author()
	{
		return img_author;
	}

	public void setImg_author(String img_author)
	{
		this.img_author = img_author;
	}

	public Author getAuthor()
	{
		return author;
	}

	public void setAuthor(Author author)
	{
		this.author = author;
	}

	/**
	 * method for getting Article data as String[] to set it to ArtInfo object
	 * 
	 * @return String[17] without ID, refreshed Date and Author ID
	 * @see Article.getAsStringArrayWithAuthorIdIfIs()
	 */
	public String[] getAsStringArray()
	{
		String[] allInfo = new String[17];

		allInfo[0] = url;
		allInfo[1] = title;
		allInfo[2] = imgArt;
		allInfo[3] = authorBlogUrl;
		allInfo[4] = authorName;

		allInfo[5] = preview;
		allInfo[6] = pubDate.toString();

		allInfo[7] = String.valueOf(numOfComments);
		allInfo[8] = String.valueOf(numOfSharings);
		allInfo[9] = artText;
		allInfo[10] = authorDescr;
		allInfo[11] = tegs_main;
		allInfo[12] = tegs_all;
		allInfo[13] = share_quont;
		allInfo[14] = to_read_main;
		allInfo[15] = to_read_more;
		allInfo[16] = img_author;

		return allInfo;
	}

	/**
	 * 
	 * @return String[20] with full data, including ID, refreshed Date and
	 *         Author ID
	 * 
	 * @see Article.getAsStringArray()
	 */
	public String[] getAsStringArrayWithAuthorIdIfIs()
	{
		String[] allInfo = new String[20];

		allInfo[0] = String.valueOf(id);
		allInfo[1] = url;
		allInfo[2] = title;
		allInfo[3] = imgArt;
		allInfo[4] = authorBlogUrl;
		allInfo[5] = authorName;

		allInfo[6] = preview;
		allInfo[7] = pubDate.toString();

		allInfo[8] = this.refreshed.toString();

		allInfo[9] = String.valueOf(numOfComments);
		allInfo[10] = String.valueOf(numOfSharings);
		allInfo[11] = artText;
		allInfo[12] = authorDescr;
		allInfo[13] = tegs_main;
		allInfo[14] = tegs_all;
		allInfo[15] = share_quont;
		allInfo[16] = to_read_main;
		allInfo[17] = to_read_more;
		allInfo[18] = img_author;

		String author;
		if (this.getAuthor() != null)
		{
			author = String.valueOf(this.getAuthor().getId());
		}
		else
		{
			author = "null";
		}

		allInfo[19] = author;

		return allInfo;
	}

	public static String getTitleById(DataBaseHelper h, int id)
	{
		String title = null;

		try
		{
			title = h.getDaoArticle().queryForId(id).getTitle();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		return title;
	}

	/**
	 * 
	 * @param h
	 * @param url
	 * @return id or null on SQLException or if art with this URL do not exists
	 */
	public static Integer getArticleIdByURL(DataBaseHelper h, String url)
	{
		Integer id = null;
		try
		{
			id = h.getDaoArticle().queryBuilder().where().eq(URL_FIELD_NAME, url).queryForFirst().getId();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * 
	 * @param h
	 * @param id
	 * @return url of Article or null on SQLException
	 */
	public static String getArticleUrlById(DataBaseHelper h, int id)
	{
		String url = null;

		try
		{
			url = h.getDaoArticle().queryForId(id).getUrl();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		return url;
	}

	public static Article getArticleById(DataBaseHelper h, int id)
	{
		Article art = null;
		try
		{
			art = h.getDaoArticle().queryForId(id);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		return art;
	}

	/**
	 * 
	 * @param h
	 * @param url
	 * @return Article obj or null on SQLException or if Atr do not exists
	 */
	public static Article getArticleByURL(DataBaseHelper h, String url)
	{
		Article art = null;
		try
		{
			art = h.getDaoArticle().queryBuilder().where().eq(URL_FIELD_NAME, url).queryForFirst();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		return art;
	}

	public static ArrayList<Article> writeArtInfoToArticleTable(DataBaseHelper h, ArrayList<Article> data)
	{
		ArrayList<Article> updatedData = new ArrayList<Article>();

		int quontOfWrittenArticles = 0;

		Article existingArt;
		for (Article a : data)
		{
			//check if there is no already existing arts in DB by queryForURL
			existingArt = Article.getArticleByURL(h, a.url);
			if (existingArt == null)
			{
				//get author obj if it is in Article and Author table
				Author aut = null;
				try
				{
					aut = h.getDaoAuthor().queryBuilder().where()
					.eq(Author.URL_FIELD_NAME, Author.getURLwithoutSlashAtTheEnd(a.authorBlogUrl)).queryForFirst();

				} catch (SQLException e)
				{
					e.printStackTrace();
				}
				//create Article obj to pass it to DB
				existingArt = a;

				existingArt.setRefreshed(new Date(System.currentTimeMillis()));
				existingArt.setAuthor(aut);
				//set author image URL for articles
				if (aut != null)
				{
					existingArt.setImg_author(aut.getAvatar());
				}
				try
				{
					updatedData.add(h.getDaoArticle().createIfNotExists(existingArt));//.create(existingArt);
					quontOfWrittenArticles++;
				} catch (SQLException e)
				{
					Log.i(LOG, "quontOfWrittenArticles: " + String.valueOf(quontOfWrittenArticles));
					Log.e(LOG, existingArt.getTitle() + " error while INSERT");
				}
			}//article do not exists
			else
			{
				//check if date of existing ==0
				if (existingArt.getPubDate().getTime() == 0 && a.pubDate.getTime() != 0)
				{
					Article.updatePubDate(h, existingArt.getId(), a.pubDate);
				}
				updatedData.add(existingArt);
			}
		}
		//		Log.i(LOG, "quontOfWrittenArticles: " + String.valueOf(quontOfWrittenArticles));
		return updatedData;
	}

	//	public static ArrayList<ArtInfo> writeArtInfoToArticleTable(DataBaseHelper h, ArrayList<ArtInfo> data)
	//	{
	//		ArrayList<ArtInfo> updatedData = new ArrayList<ArtInfo>();
	//
	//		int quontOfWrittenArticles = 0;
	//
	//		Article existingArt;
	//		for (ArtInfo a : data)
	//		{
	//			//check if there is no already existing arts in DB by queryForURL
	//			existingArt = Article.getArticleByURL(h, a.url);
	//			if (existingArt == null)
	//			{
	//				//get author obj if it is in ArtInfo and Author table
	//				Author aut = null;
	//				try
	//				{
	//					aut = h.getDaoAuthor().queryBuilder().where()
	//					.eq(Author.URL_FIELD_NAME, Author.getURLwithoutSlashAtTheEnd(a.authorBlogUrl)).queryForFirst();
	//
	//				} catch (SQLException e)
	//				{
	//					e.printStackTrace();
	//				}
	//				//create Article obj to pass it to DB
	//				existingArt = new Article(a.getArtInfoAsStringArray(), new Date(
	//				System.currentTimeMillis()), aut);
	//				//set author image URL for articles
	//				if (aut != null)
	//				{
	//					existingArt.setImg_author(aut.getAvatar());
	//				}
	//				try
	//				{
	//					h.getDaoArticle().create(existingArt);
	//					quontOfWrittenArticles++;
	//				} catch (SQLException e)
	//				{
	//					Log.i(LOG, "quontOfWrittenArticles: " + String.valueOf(quontOfWrittenArticles));
	//					Log.e(LOG, existingArt.getTitle() + " error while INSERT");
	//				}
	//			}//article do not exists
	//			else
	//			{
	//				//check if date of existing ==0
	//				if(existingArt.getPubDate().getTime()==0 && !a.pubDate.equals("empty"))
	//				{
	//					Article.updatePubDate(h, existingArt.getId(), DateParse.parse(a.pubDate));
	//				}
	//			}
	//
	//			updatedData.add(new ArtInfo(existingArt.getAsStringArray()));
	//		}
	////		Log.i(LOG, "quontOfWrittenArticles: " + String.valueOf(quontOfWrittenArticles));
	//		return updatedData;
	//	}

	public static void updatePubDate(DataBaseHelper h, int articleId, Date d)
	{
		UpdateBuilder<Article, Integer> updateBuilder;
		try
		{
			updateBuilder = h.getDaoArticle().updateBuilder();
			updateBuilder.where().eq(Article.ID_FIELD_NAME, articleId);
			updateBuilder.updateColumnValue(Article.FIELD_NAME_PUB_DATE, d);
			updateBuilder.update();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * returns String arr with names of all Table columns
	 */
	public static String[] getFieldsNames()
	{
		String[] arrStr1 = { "id", "url", "title", "img_art", "authorBlogUrl",
				"authorName", "preview", "pubDate", "refreshed", "numOfComments",
				"numOfSharings", "artText", "authorDescr", "tegs_main", "tegs_all",
				"share_quont", "to_read_main", "to_read_more", "img_author", "author" };
		return arrStr1;
	}

	/**
	 * returns URL field value
	 */
	@Override
	public String toString()
	{
		return this.getAsStringArray()[1];
	}

	//////PARCEL implementation
	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(url);
		dest.writeString(title);
		dest.writeString(imgArt);
		dest.writeString(authorBlogUrl);
		dest.writeString(authorName);

		dest.writeString(preview);
		dest.writeLong(pubDate.getTime());

		dest.writeInt(numOfComments);
		dest.writeInt(numOfSharings);

		dest.writeString(artText);
		dest.writeString(authorDescr);
		dest.writeString(tegs_main);
		dest.writeString(tegs_all);
		dest.writeString(share_quont);
		dest.writeString(to_read_main);
		dest.writeString(to_read_more);
		dest.writeString(img_author);

		dest.writeLong(refreshed.getTime());
		dest.writeParcelable(author, flags);
	}

	private Article(Parcel in)
	{
		this.url = in.readString();
		this.title = in.readString();
		this.imgArt = in.readString();
		this.authorBlogUrl = in.readString();
		this.authorName = in.readString();

		this.preview = in.readString();
		this.pubDate = new Date(in.readLong());

		this.numOfComments = in.readInt();
		this.numOfSharings = in.readInt();
		this.artText = in.readString();
		this.authorDescr = in.readString();
		this.tegs_main = in.readString();
		this.tegs_all = in.readString();
		this.share_quont = in.readString();
		this.to_read_main = in.readString();
		this.to_read_more = in.readString();
		this.img_author = in.readString();

		refreshed = new Date(in.readLong());
		author = (Author) in.readParcelable(Author.class.getClassLoader());
	}

	public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>()
	{

		@Override
		public Article createFromParcel(Parcel source)
		{
			return new Article(source);
		}

		@Override
		public Article[] newArray(int size)
		{
			return new Article[size];
		}
	};	
	/////////////////////////////
	public String[] getAllTegsArr()
	{
		String[] allTegsArr;

		if (!this.tegs_all.equals("empty"))
		{
			allTegsArr = this.tegs_all.split(" !!!! ");
		}
		else
		{
			//System.out.println("AllTegs var is empty!");
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
