/*
 09.12.2014
Author.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.sql.SQLException;
import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * id, blog_url, name, description, who, avatar, avatarBig, refreshed, lastArticleDate, 
 * firstArticleURL
 */
@DatabaseTable(tableName = "author")
public class Author
{
	public final static String URL_FIELD_NAME = "blog_url";
	public final static String REFRESHED_FIELD_NAME = "refreshed";
//	public static final String SINCHRONISED_FIELD_NAME = "sinhronised";
	public static final String FIRST_ARTICLE_URL_FIELD_NAME = "firstArticleURL";

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false, columnName = URL_FIELD_NAME)
	private String blog_url;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false)
	private String name;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String description;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String who;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String avatar;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String avatarBig;

	@DatabaseField(dataType = DataType.DATE, canBeNull = false, columnName = REFRESHED_FIELD_NAME)
	private Date refreshed;

	@DatabaseField(dataType = DataType.DATE, canBeNull = false)
	private Date lastArticleDate;

	//true if there are full list of arts without intervals
	//false if we have arts, with interval, that we catch while loading arts from web;
	//i.e. on first launch we have no arts at all, so it's false
	//after first load we have it's true
	//then if we'll load MORE arts we search for then in DB
	//if there are no arts (or less then 30). we load from web
	//else from bd
	////
	//if it's no first load (there are arts in DB) and we have no match of loaded arts (i.e. by url) we set it to false
	//and so load MORE arts by web;
	//else (we have matches) we update Article table and ArtCatCable set sink value to true,
	//so next MORE arts we'll get from DB
//	@DatabaseField(dataType = DataType.BOOLEAN, canBeNull = false, columnName = SINCHRONISED_FIELD_NAME)
//	private boolean sinhronised = false;

	//we need this to check if we have all arts at the end of category's list
	//we need it to prevent loading arts from web, when category is synked, and we have less than 30 arts at all,
	//or in same case while requesting arts from bottom
	@DatabaseField(dataType = DataType.STRING, columnName = FIRST_ARTICLE_URL_FIELD_NAME)
	private String firstArticleURL;

	public Author()
	{

	}

	public Author(String blog_url, String name, String descr, String who, String ava_url, String ava_big, Date ref,
	Date lastArt)
	{
		this.blog_url = blog_url;
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
		this.blog_url = stringData[0];
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

	public String getBlog_url()
	{
		return blog_url;
	}

	public void setBlog_url(String blog_url)
	{
		this.blog_url = blog_url;
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

//	public boolean isSinhronised()
//	{
//		return sinhronised;
//	}
//
//	public void setSinhronised(boolean sinhronised)
//	{
//		this.sinhronised = sinhronised;
//	}
	
	public static Author getAuthorByURL(DataBaseHelper h, String url)
	{
		Author a = null;
		try
		{
			a = h.getDaoAuthor().queryBuilder().where().eq(URL_FIELD_NAME, url).queryForFirst();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return a;
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
		allInfo[1] = blog_url;
		allInfo[2] = name;
		allInfo[3] = description;
		allInfo[4] = who;
		allInfo[5] = avatar;

		allInfo[6] = avatarBig;
		allInfo[7] = refreshed.toString();
		allInfo[8] = lastArticleDate.toString();
//		allInfo[9] = String.valueOf(sinhronised);
		allInfo[9] = firstArticleURL;

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

}
