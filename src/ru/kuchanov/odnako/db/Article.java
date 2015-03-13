/*
 09.12.2014
Article.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

//tags for logCat
//tag:^(?!dalvikvm) tag:^(?!libEGL) tag:^(?!Open) tag:^(?!Google) tag:^(?!resour) tag:^(?!Chore) tag:^(?!EGL)

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

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

	public static final String KEY_CURENT_ART = "curArtInfo";
	public static final String KEY_ALL_ART_INFO = "allArtInfo";

	public final static String ID_FIELD_NAME = "id";
	public final static String AUTHOR_FIELD_NAME = "author";
	public final static String URL_FIELD_NAME = "url";
	public final static String FIELD_NAME_PUB_DATE = "pubDate";
	public final static String FIELD_NAME_PREVIEW = "preview";

	@DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
	private int id;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false, columnName = URL_FIELD_NAME)
	private String url;

	@DatabaseField(dataType = DataType.STRING)
	private String title = "empty";

	@DatabaseField(dataType = DataType.STRING)
	private String imgArt = "empty";

	@DatabaseField(dataType = DataType.STRING)
	private String authorBlogUrl = "empty";

	@DatabaseField(dataType = DataType.STRING)
	private String authorName = "empty";

	@DatabaseField(dataType = DataType.STRING, columnName = FIELD_NAME_PREVIEW)
	private String preview = "empty";

	@DatabaseField(dataType = DataType.DATE, columnName = FIELD_NAME_PUB_DATE)
	private Date pubDate = new Date(0);

	@DatabaseField(dataType = DataType.DATE)
	private Date refreshed = new Date(0);

	@DatabaseField(dataType = DataType.INTEGER)
	private int numOfComments = 0;

	@DatabaseField(dataType = DataType.INTEGER)
	private int numOfSharings = 0;

	@DatabaseField(dataType = DataType.STRING)
	private String artText = "empty";

	@DatabaseField(dataType = DataType.STRING)
	private String authorDescr = "empty";

	@DatabaseField(dataType = DataType.STRING)
	private String tegsMain = "empty";

	@DatabaseField(dataType = DataType.STRING)
	private String tegsAll = "empty";

	@DatabaseField(dataType = DataType.STRING)
	private String shareQuont = "empty";

	@DatabaseField(dataType = DataType.STRING)
	private String toReadMain = "empty";

	@DatabaseField(dataType = DataType.STRING)
	private String toReadMore = "empty";

	@DatabaseField(dataType = DataType.STRING)
	private String imgAuthor = "empty";

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
		this.tegsMain = artInfoArr[11];
		this.tegsAll = artInfoArr[12];
		this.shareQuont = artInfoArr[13];
		this.toReadMain = artInfoArr[14];
		this.toReadMore = artInfoArr[15];
		this.imgAuthor = artInfoArr[16];

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

	public String getTegsMain()
	{
		return tegsMain;
	}

	public void setTegsMain(String tegsMain)
	{
		this.tegsMain = tegsMain;
	}

	public String getTegsAll()
	{
		return tegsAll;
	}

	public void setTegsAll(String tegsAll)
	{
		this.tegsAll = tegsAll;
	}

	public String getShareQuont()
	{
		return shareQuont;
	}

	public void setShareQuont(String shareQuont)
	{
		this.shareQuont = shareQuont;
	}

	public String getToReadMain()
	{
		return toReadMain;
	}

	public void setToReadMain(String toReadMain)
	{
		this.toReadMain = toReadMain;
	}

	public String getToReadMore()
	{
		return toReadMore;
	}

	public void setToReadMore(String toReadMore)
	{
		this.toReadMore = toReadMore;
	}

	public String getImgAuthor()
	{
		return imgAuthor;
	}

	public void setImgAuthor(String imgAuthor)
	{
		this.imgAuthor = imgAuthor;
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
		allInfo[11] = tegsMain;
		allInfo[12] = tegsAll;
		allInfo[13] = shareQuont;
		allInfo[14] = toReadMain;
		allInfo[15] = toReadMore;
		allInfo[16] = imgAuthor;

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
		allInfo[13] = tegsMain;
		allInfo[14] = tegsAll;
		allInfo[15] = shareQuont;
		allInfo[16] = toReadMain;
		allInfo[17] = toReadMore;
		allInfo[18] = imgAuthor;

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

	public static ArrayList<Article> writeArticleToArticleTable(DataBaseHelper h, ArrayList<Article> data)
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
					existingArt.setImgAuthor(aut.getAvatar());
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

	public static void updatePreview(DataBaseHelper h, int articleId, String preview)
	{
		UpdateBuilder<Article, Integer> updateBuilder;
		try
		{
			updateBuilder = h.getDaoArticle().updateBuilder();
			updateBuilder.where().eq(Article.ID_FIELD_NAME, articleId);
			updateBuilder.updateColumnValue(Article.FIELD_NAME_PREVIEW, preview);
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
		return this.getTitle();
	}

	public void printAllInfo()
	{
		//XXX
		Log.i(LOG, "PRINT_ALL_INFO");
		//		for(String s: this.getAsStringArrayWithAuthorIdIfIs())
		//		{
		//			Log.e(this.artText.get, s);
		//		}
		Field[] f = this.getClass().getDeclaredFields();//.getFields();//
		for (int i = 0; i < f.length; i++)
		{
			try
			{
				Log.e(f[i].getName(), f[i].get(this).toString());
			} catch (IllegalAccessException e)
			{
				e.printStackTrace();
			} catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			}
			catch (NullPointerException e)
			{
				Log.e(f[i].getName(), "THIS FIELD IS NULL!");
//				e.printStackTrace();
			}
		}
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
		dest.writeString(tegsMain);
		dest.writeString(tegsAll);
		dest.writeString(shareQuont);
		dest.writeString(toReadMain);
		dest.writeString(toReadMore);
		dest.writeString(imgAuthor);

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
		this.tegsMain = in.readString();
		this.tegsAll = in.readString();
		this.shareQuont = in.readString();
		this.toReadMain = in.readString();
		this.toReadMore = in.readString();
		this.imgAuthor = in.readString();

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

		if (!this.tegsAll.equals("empty"))
		{
			allTegsArr = this.tegsAll.split(" !!!! ");
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

		if (!this.toReadMain.equals("empty"))
		{
			allInfo = this.toReadMain.split(" !!!! ");
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

		if (!this.toReadMore.equals("empty"))
		{
			allInfo = this.toReadMore.split(" !!!! ");
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
