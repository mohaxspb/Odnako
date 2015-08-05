/*
 09.12.2014
Author.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.Date;

import ru.kuchanov.odnako.Const;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.table.DatabaseTable;

/**
 * id, blog_url, name, description, who, avatar, avatarBig, refreshed,
 * lastArticleDate, firstArticleURL
 */
@DatabaseTable(tableName = "author")
public class Author implements Comparable<Author>, Parcelable
{
	public final static String FIELD_ID = "id";
	public final static String FIELD_URL = "blogUrl";
	public final static String FIELD_NAME = "name";
	public final static String FIELD_REFRESHED = "refreshed";
	public static final String FIELD_FIRST_ARTICLE_URL = "firstArticleURL";

	@DatabaseField(generatedId = true, columnName = FIELD_ID)
	private int id;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false, columnName = FIELD_URL)
	private String blogUrl;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false, columnName = FIELD_NAME)
	private String name;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String description;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String who;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String avatar;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String avatarBig;

	@DatabaseField(dataType = DataType.DATE, canBeNull = false, columnName = FIELD_REFRESHED)
	private Date refreshed = new Date(0);

	@DatabaseField(dataType = DataType.DATE, canBeNull = false)
	private Date lastArticleDate = new Date(0);

	//we need this to check if we have all arts at the end of category's list
	//we need it to prevent loading arts from web, when category is synked, and we have less than 30 arts at all,
	//or in same case while requesting arts from bottom
	@DatabaseField(dataType = DataType.STRING, columnName = FIELD_FIRST_ARTICLE_URL)
	private String firstArticleURL;

	/**
	 * need empty constructor for OrmLite
	 */
	public Author()
	{

	}

	public Author(String blog_url, String name, String descr, String who, String ava_url, String ava_big, Date ref,
	Date lastArt)
	{
		this.blogUrl = blog_url;
		this.name = name;
		this.avatar = ava_url;
		this.avatarBig = ava_big;
		this.who = who;
		this.description = descr;
		this.refreshed = ref;
		this.lastArticleDate = lastArt;
	}

	public Author(String[] stringData, Date[] dateData)
	{
		this.blogUrl = stringData[0];
		this.name = stringData[1];
		this.avatar = stringData[2];
		this.avatarBig = stringData[3];
		this.who = stringData[4];
		this.description = stringData[5];
		this.refreshed = dateData[0];
		this.lastArticleDate = dateData[1];
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getBlogUrl()
	{
		return blogUrl;
	}

	public void setBlog_url(String blog_url)
	{
		this.blogUrl = blog_url;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getWho()
	{
		return who;
	}

	public void setWho(String who)
	{
		this.who = who;
	}

	public String getAvatar()
	{
		return avatar;
	}

	public void setAvatar(String avatar)
	{
		this.avatar = avatar;
	}

	public Date getRefreshed()
	{
		return refreshed;
	}

	public void setRefreshed(Date refreshed)
	{
		this.refreshed = refreshed;
	}

	public Date getLastArticleDate()
	{
		return lastArticleDate;
	}

	public void setLastArticleDate(Date lastArticleDate)
	{
		this.lastArticleDate = lastArticleDate;
	}

	public String getFirstArticleURL()
	{
		return firstArticleURL;
	}

	public void setFirstArticleURL(String firstArticleURL)
	{
		this.firstArticleURL = firstArticleURL;
	}

	/**
	 * @return URL of initial article in category or null if can't find
	 */
	public static String getFirstArticleURLById(DataBaseHelper h, int authorId)
	{
		String firstArtUrl = null;
		try
		{
			firstArtUrl = h.getDaoAuthor().queryBuilder().where().eq(FIELD_ID, authorId).queryForFirst()
			.getFirstArticleURL();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return firstArtUrl;
	}

	/**
	 * 
	 * @param h
	 * @param url
	 * @return Author if can find or (null if can't or on SQLException)
	 */
	public static Author getAuthorByURL(DataBaseHelper h, String url)
	{
		Author a = null;
		try
		{
			a = h.getDaoAuthor().queryBuilder().where().eq(FIELD_URL, Author.getURLwithoutSlashAtTheEnd(url))
			.queryForFirst();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return a;
	}
	
	/**
	 * 
	 * @param h
	 * @param url
	 * @return Author if can find or (null if can't or on SQLException)
	 */
	public static Author getAuthorByName(DataBaseHelper h, String name)
	{
		Author a = null;
		try
		{
			a = h.getDaoAuthor().queryBuilder().where().eq(FIELD_NAME, name)
			.queryForFirst();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return a;
	}

	/**
	 * 
	 * @param h
	 * @param url
	 * @return id or null on SQLException and if can't find
	 */
	public static int getAuthorIdByURL(DataBaseHelper h, String url)
	{
		Integer id = null;
		try
		{
			id = h.getDaoAuthor().queryBuilder().where()
			.eq(Author.FIELD_URL, Author.getURLwithoutSlashAtTheEnd(url)).queryForFirst().getId();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return id;
	}

	public static String getAvatarUrlByName(DataBaseHelper h, String name)
	{
		String avatarUrl = Const.EMPTY_STRING;
		try
		{
			avatarUrl = h.getDaoAuthor().queryBuilder().where().eq(FIELD_NAME, name).queryForFirst().getAvatar();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return avatarUrl;
	}

	public static String getAvatarUrlByUrl(DataBaseHelper h, String url)
	{
		String avatarUrl = Const.EMPTY_STRING;
		try
		{
			avatarUrl = h.getDaoAuthor().queryBuilder().where()
			.eq(FIELD_URL, Author.getURLwithoutSlashAtTheEnd(url)).queryForFirst().getAvatar();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return avatarUrl;
	}

	public static String getNameByUrl(DataBaseHelper h, String url)
	{
		String name;
		try
		{
			name = h.getDaoAuthor().queryBuilder().where().eq(FIELD_URL, Author.getURLwithoutSlashAtTheEnd(url))
			.queryForFirst().getName();
		} catch (SQLException e)
		{
			name = url;
			e.printStackTrace();
		}
		return name;
	}

	/**
	 * 
	 * @param url
	 *            URL to format (can be with or without "/" at the end)
	 * @return URL without "/" at the end if it is
	 */
	public static String getURLwithoutSlashAtTheEnd(String url)
	{
		//delete last char "/" if it is, as in res/ we have author blog_url without it
		if (url.endsWith("/"))
		{
			url = url.substring(0, url.length() - 1);
		}
		return url;
	}

	public String[] getAsStringArray()
	{
		String[] allInfo = new String[10];

		allInfo[0] = String.valueOf(id);
		allInfo[1] = blogUrl;
		allInfo[2] = name;
		allInfo[3] = description;
		allInfo[4] = who;
		allInfo[5] = avatar;

		allInfo[6] = avatarBig;
		allInfo[7] = refreshed.toString();
		allInfo[8] = lastArticleDate.toString();
		allInfo[9] = getFirstArticleURL();

		return allInfo;
	}

	/**
	 * returns String arr with names of all Table columns
	 */
	public static String[] getFieldsNames()
	{
		String[] arrStr1 = { "id", "blog_url", "name", "description", "who", "avatar", "avatarBig", "refreshed",
				"lastArticleDate", "firstArticleURL" };
		return arrStr1;
	}

	public static void setInitialArtsUrl(DataBaseHelper h, int authorId, String initialArtsUrl)
	{
		UpdateBuilder<Author, Integer> updateBuilder;
		try
		{
			updateBuilder = h.getDaoAuthor().updateBuilder();
			updateBuilder.where().eq(Author.FIELD_ID, authorId);
			updateBuilder.updateColumnValue(Author.FIELD_FIRST_ARTICLE_URL, initialArtsUrl);
			updateBuilder.update();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public int compareTo(Author other)
	{
		return this.getName().compareToIgnoreCase(other.getName());
	}

	public static class AuthorNameComparator implements Comparator<Author>
	{
		public int compare(Author chair1, Author chair2)
		{
			return chair1.getName().compareToIgnoreCase(chair2.getName());
		}
	}

	public static class AuthorDescriptionComparator implements Comparator<Author>
	{
		public int compare(Author chair1, Author chair2)
		{
			return chair1.getDescription().compareToIgnoreCase(chair2.getDescription());
		}
	}

	public static class AuthorWhoComparator implements Comparator<Author>
	{
		public int compare(Author chair1, Author chair2)
		{
			return chair1.getWho().compareToIgnoreCase(chair2.getWho());
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
		dest.writeInt(id);
		dest.writeString(blogUrl);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeString(who);
		dest.writeString(avatar);
		dest.writeString(avatarBig);
		dest.writeLong(refreshed.getTime());
		dest.writeLong(lastArticleDate.getTime());
		dest.writeString(firstArticleURL);
	}

	private Author(Parcel in)
	{
		id = in.readInt();
		blogUrl = in.readString();
		name = in.readString();
		description = in.readString();
		who = in.readString();
		avatar = in.readString();
		avatarBig = in.readString();
		refreshed = new Date(in.readLong());
		lastArticleDate = new Date(in.readLong());
		firstArticleURL = in.readString();
	}

	public static final Parcelable.Creator<Author> CREATOR = new Parcelable.Creator<Author>()
	{

		@Override
		public Author createFromParcel(Parcel source)
		{
			return new Author(source);
		}

		@Override
		public Author[] newArray(int size)
		{
			return new Author[size];
		}
	};
}
