/*
 09.12.2014
Article.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import ru.kuchanov.odnako.Const;
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

	public static final String DIVIDER = " !!!! ";
	public static final String DIVIDER_GROUP = " !!__!! ";

	public static final String KEY_CURENT_ART = "curArtInfo";
	public static final String KEY_ALL_ART_INFO = "allArtInfo";

	public final static String FIELD_NAME_ID = "id";
	public final static String FIELD_NAME_AUTHOR = "author";
	public final static String FIELD_NAME_URL = "url";
	public final static String FIELD_NAME_PUB_DATE = "pubDate";
	public final static String FIELD_NAME_PREVIEW = "preview";
	public final static String FIELD_NAME_ART_TEXT = "artText";
	public final static String FIELD_NAME_IS_READEN = "isReaden";
	public final static String FIELD_NAME_REFRESHED_DATE = "refreshed";

	@DatabaseField(generatedId = true, columnName = FIELD_NAME_ID)
	private int id;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false, columnName = FIELD_NAME_URL)
	private String url;

	@DatabaseField(dataType = DataType.STRING)
	private String title = Const.EMPTY_STRING;

	@DatabaseField(dataType = DataType.STRING)
	private String imgArt = Const.EMPTY_STRING;

	@DatabaseField(dataType = DataType.STRING)
	private String authorBlogUrl = Const.EMPTY_STRING;

	@DatabaseField(dataType = DataType.STRING)
	private String authorName = Const.EMPTY_STRING;

	@DatabaseField(dataType = DataType.STRING, columnName = FIELD_NAME_PREVIEW)
	private String preview = Const.EMPTY_STRING;

	@DatabaseField(dataType = DataType.DATE, columnName = FIELD_NAME_PUB_DATE)
	private Date pubDate = new Date(0);

	@DatabaseField(dataType = DataType.DATE)
	private Date refreshed = new Date(0);

	@DatabaseField(dataType = DataType.INTEGER)
	private int numOfComments = 0;

	@DatabaseField(dataType = DataType.INTEGER)
	private int numOfSharings = 0;

	@DatabaseField(dataType = DataType.STRING, columnName = FIELD_NAME_ART_TEXT)
	private String artText = Const.EMPTY_STRING;

	@DatabaseField(dataType = DataType.STRING)
	private String authorDescr = Const.EMPTY_STRING;

	@DatabaseField(dataType = DataType.STRING)
	private String tegsMain = Const.EMPTY_STRING;

	@DatabaseField(dataType = DataType.STRING)
	private String tagsAll = Const.EMPTY_STRING;

	@DatabaseField(dataType = DataType.STRING)
	private String shareQuont = Const.EMPTY_STRING;

	//XXX to delete because it's too hard and we do not need it really
	@DatabaseField(dataType = DataType.STRING)
	private String toReadMain = Const.EMPTY_STRING;

	@DatabaseField(dataType = DataType.STRING)
	private String toReadMore = Const.EMPTY_STRING;

	@DatabaseField(dataType = DataType.STRING)
	private String imgAuthor = Const.EMPTY_STRING;

	@DatabaseField(dataType = DataType.BOOLEAN, columnName = FIELD_NAME_IS_READEN)
	private boolean isReaden = false;

	//foreignKeys
	@DatabaseField(foreign = true, columnName = FIELD_NAME_AUTHOR, canBeNull = true)
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

		//date; here can be 2 different ways of storing date, that we receive from site, so get them from util
		this.pubDate = DateParse.parse(artInfoArr[6]);

		this.numOfComments = Integer.parseInt(artInfoArr[7]);
		this.numOfSharings = Integer.parseInt(artInfoArr[8]);
		this.artText = artInfoArr[9];
		this.authorDescr = artInfoArr[10];
		this.tegsMain = artInfoArr[11];
		this.tagsAll = artInfoArr[12];
		this.shareQuont = artInfoArr[13];
		this.toReadMain = artInfoArr[14];
		this.toReadMore = artInfoArr[15];
		this.imgAuthor = artInfoArr[16];

		//refreshed date
		//set it only if we have not null or Const.EMPTY_STRING artText value
		//and, ofcours not null given Date refreshed
		if (!this.artText.equals(Const.EMPTY_STRING))
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

	public void setId(int newId)
	{
		this.id = newId;
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

	public void setTagsMain(String tegsMain)
	{
		this.tegsMain = tegsMain;
	}

	public String getTagsAll()
	{
		return tagsAll;
	}

	public void setTagsAll(String tagsAll)
	{
		this.tagsAll = tagsAll;
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
		allInfo[12] = tagsAll;
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
		allInfo[14] = tagsAll;
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
			//TODO CHECK IT!!! IT must be "empty"!!!
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
			id = h.getDaoArticle().queryBuilder().where().eq(FIELD_NAME_URL, url).queryForFirst().getId();
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
	 * @return Article obj or null on SQLException or if Article don't exists
	 */
	public static Article getArticleByURL(DataBaseHelper h, String url)
	{
		Article art = null;
		try
		{
			art = h.getDaoArticle().queryBuilder().where().eq(FIELD_NAME_URL, url).queryForFirst();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		return art;
	}

	public static void updatePubDate(DataBaseHelper h, int articleId, Date d)
	{
		UpdateBuilder<Article, Integer> updateBuilder;
		try
		{
			updateBuilder = h.getDaoArticle().updateBuilder();
			updateBuilder.where().eq(Article.FIELD_NAME_ID, articleId);
			updateBuilder.updateColumnValue(Article.FIELD_NAME_PUB_DATE, d);
			updateBuilder.update();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void updateRefreshedDate(DataBaseHelper h, int articleId, Date d)
	{
		UpdateBuilder<Article, Integer> updateBuilder;
		try
		{
			updateBuilder = h.getDaoArticle().updateBuilder();
			updateBuilder.where().eq(Article.FIELD_NAME_ID, articleId);
			updateBuilder.updateColumnValue(Article.FIELD_NAME_REFRESHED_DATE, d);
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
			updateBuilder.where().eq(Article.FIELD_NAME_ID, articleId);
			updateBuilder.updateColumnValue(Article.FIELD_NAME_PREVIEW, preview);
			updateBuilder.update();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static void updateArtText(DataBaseHelper h, int articleId, String artText)
	{
		UpdateBuilder<Article, Integer> updateBuilder;
		try
		{
			updateBuilder = h.getDaoArticle().updateBuilder();
			updateBuilder.where().eq(Article.FIELD_NAME_ID, articleId);
			updateBuilder.updateColumnValue(Article.FIELD_NAME_ART_TEXT, artText);
			updateBuilder.update();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static void updateIsReaden(DataBaseHelper h, int articleId, boolean isReaden)
	{
		UpdateBuilder<Article, Integer> updateBuilder;
		try
		{
			updateBuilder = h.getDaoArticle().updateBuilder();
			updateBuilder.where().eq(Article.FIELD_NAME_ID, articleId);
			updateBuilder.updateColumnValue(Article.FIELD_NAME_IS_READEN, isReaden);
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

	/**
	 * Prints all class fields info in logs
	 */
	public void printAllInfo()
	{
		Log.d(LOG, "PRINT_ALL_INFO");
		Field[] f = this.getClass().getDeclaredFields();
		for (int i = 0; i < f.length; i++)
		{
			try
			{
				if (!f[i].getName().equals("id"))
				{
					if (!f[i].getName().equals("artText"))
					{
						Log.i(f[i].getName(), f[i].get(this).toString());
					}
					else
					{
						Log.i(f[i].getName(), f[i].get(this).toString().substring(0, 100));
					}
				}
				else
				{
					Log.i(f[i].getName(), f[i].get(this).toString());
				}
			} catch (IllegalAccessException e)
			{
				Log.e("printAllInfo", "IllegalAccessException");
			} catch (IllegalArgumentException e)
			{
				Log.e("printAllInfo", "IllegalArgumentException");
			} catch (NullPointerException e)
			{
				Log.e(f[i].getName(), "THIS FIELD IS NULL!");
			} catch (Exception e)
			{
				Log.d("printAllInfo", "WE CATCH E IN PRINT: " + e.toString());
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
		dest.writeString(tagsAll);
		dest.writeString(shareQuont);
		dest.writeString(toReadMain);
		dest.writeString(toReadMore);
		dest.writeString(imgAuthor);

		dest.writeLong(refreshed.getTime());
		dest.writeParcelable(author, flags);

		dest.writeByte((byte) (isReaden ? 1 : 0)); //if myBoolean == true, byte == 1
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
		this.tagsAll = in.readString();
		this.shareQuont = in.readString();
		this.toReadMain = in.readString();
		this.toReadMore = in.readString();
		this.imgAuthor = in.readString();

		this.refreshed = new Date(in.readLong());
		this.author = (Author) in.readParcelable(Author.class.getClassLoader());

		this.isReaden = in.readByte() != 0; //myBoolean == true if byte != 0
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
	public String[] getAllTagsArr()
	{
		String[] allTegsArr;
		if (!this.tagsAll.equals(Const.EMPTY_STRING))
		{
			allTegsArr = this.tagsAll.split(DIVIDER);
		}
		else
		{
			allTegsArr = null;
		}
		return allTegsArr;
	}

	public AlsoToRead getAlsoByTheme()
	{
		AlsoToRead alsoToRead;
		String[] allInfo;

		if (!this.toReadMain.equals(Const.EMPTY_STRING))
		{
			allInfo = this.toReadMain.split(DIVIDER);
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
			alsoToRead = null;
		}
		return alsoToRead;
	}

	public AlsoToRead getAlsoToReadMore()
	{
		AlsoToRead alsoToRead;
		String[] allInfo;

		if (!this.toReadMore.equals(Const.EMPTY_STRING))
		{
			allInfo = this.toReadMore.split(DIVIDER);
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
			alsoToRead = null;
		}
		return alsoToRead;
	}

	public class AlsoToRead
	{
		public String[] urls;
		public String[] titles;
		public String[] dates;

		public AlsoToRead(String[] titles, String[] urls, String[] dates)
		{
			this.urls = urls;
			this.titles = titles;
			this.dates = dates;
		}
	}

	public class Tag
	{
		public String url = Const.EMPTY_STRING;
		public String title = Const.EMPTY_STRING;

		public Tag(String url, String title)
		{
			this.url = url;
			this.title = title;
		}
	}

	public ArrayList<Tag> getTags(String parsedTags)
	{
		ArrayList<Tag> tags = new ArrayList<Tag>();
		if (!parsedTags.equals(Const.EMPTY_STRING))
		{
			String[] allTagsArr = parsedTags.split(DIVIDER_GROUP);
			for (int i = 0; i < allTagsArr.length; i++)
			{
				String[] curTag = allTagsArr[i].split(DIVIDER);
				Tag tag = new Tag(curTag[0], curTag[1]);
				tags.add(tag);
			}
		}
		return tags;
	}

	/**
	 * Checks DB records fields for "empty", '0' or null and updates it by info
	 * from given artilce. In case of pubDate checks also if we have non "00:00"
	 * for "HH:mm" and update it only if it is so and given date have HH:mm
	 * 
	 * @param h
	 * @param id2
	 * @param article
	 */
	public static void updateEmptyFields(DataBaseHelper h, Article inDB, Article loaded)
	{
		//id, url, title, img_art, authorBlogUrl, authorName, preview, pubDate,
		//refreshed, numOfComments, numOfSharings, artText, authorDescr, tegs_main,
		//tegs_all, share_quont, to_read_main, to_read_more, img_author, author
		//h.getDaoArticle().u
		//id is autogenerated
		//url can't be null in all cases... It's right, isn't it? =)
		if ((inDB.getArtText().equals(Const.EMPTY_STRING)) && (!loaded.getArtText().equals(Const.EMPTY_STRING)))
		{
			updateArtText(h, inDB.getId(), loaded.getArtText());
		}

		if ((inDB.getArtText().equals(Const.EMPTY_STRING)) && (!loaded.getArtText().equals(Const.EMPTY_STRING)))
		{
			updateArtText(h, inDB.getId(), loaded.getArtText());
		}

	}

	public Boolean isReaden()
	{
		return isReaden;
	}

	public void setReaden(Boolean isReaden)
	{
		this.isReaden = isReaden;
	}
}
