/*
 09.12.2014
Category.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.sql.SQLException;
import java.util.Date;

import android.util.Log;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.table.DatabaseTable;

/**
 * "id", "url", "title", "description", "img_url", "img_file_name", "refreshed",
 * "lastArticleDate", "firstArticleURL"
 */
@DatabaseTable(tableName = "category")
public class Category
{
	private static final String LOG = Category.class.getSimpleName();

	public final static String ID_FIELD_NAME = "id";
	public final static String URL_FIELD_NAME = "url";
	public final static String REFRESHED_FIELD_NAME = "refreshed";
	//	public static final String SINCHRONISED_FIELD_NAME = "sinhronised";
	public static final String FIRST_ARTICLE_URL_FIELD_NAME = "firstArticleURL";

	@DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
	private int id;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false, columnName = URL_FIELD_NAME)
	private String url;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false)
	private String title;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String description;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false)
	private String img_url;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false)
	private String img_file_name;

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
	//TODO DO NOT NEED THIS!!!s
	//	@DatabaseField(dataType = DataType.BOOLEAN, canBeNull = false, columnName = SINCHRONISED_FIELD_NAME)
	//	private boolean sinhronised = false;

	//we need this to check if we have all arts at the end of category's list
	//we need it to prevent loading arts from web, when category is synked, and we have less than 30 arts at all,
	//or in same case while requesting arts from bottom
	@DatabaseField(dataType = DataType.STRING, columnName = FIRST_ARTICLE_URL_FIELD_NAME)
	private String firstArticleURL;

	/**
	 * empty constructor witch is need for OrmLite
	 */
	public Category()
	{
	}

	public Category(String url, String title, String description, String img_url, String img_file_name, Date refreshed,
	Date lastArticleDate)
	{
		this.url = url;
		this.title = title;
		this.description = description;
		this.img_url = img_url;
		this.img_file_name = img_file_name;
		this.refreshed = refreshed;
		this.lastArticleDate = lastArticleDate;
	}

	public Category(String[] stringData, Date[] dateData)
	{
		this.url = stringData[0];
		this.title = stringData[1];
		this.description = stringData[2];
		this.img_url = stringData[3];
		this.img_file_name = stringData[4];
		this.refreshed = dateData[0];
		this.lastArticleDate = dateData[1];
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

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getImg_url()
	{
		return img_url;
	}

	public void setImg_url(String img_url)
	{
		this.img_url = img_url;
	}

	public String getImg_file_name()
	{
		return img_file_name;
	}

	public void setImg_file_name(String img_file_name)
	{
		this.img_file_name = img_file_name;
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

	public String getFirstArticleURL()
	{
		return firstArticleURL;
	}

	public void setFirstArticleURL(String firstArticleURL)
	{
		this.firstArticleURL = firstArticleURL;
	}

	//static methods for querying
	/**
	 * 
	 * @param h
	 * @param id
	 * @return Category or null on SQLException
	 */
	public static Category getCategoryById(DataBaseHelper h, int id)
	{
		Category c = null;
		try
		{
			c = h.getDaoCategory().queryForId(id);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return c;
	}

	public static Category getCategoryByURL(DataBaseHelper h, String url)
	{
		Category c = null;
		try
		{
			c = h.getDaoCategory().queryBuilder().where().eq(URL_FIELD_NAME, url).queryForFirst();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return c;
	}

	/**
	 * 
	 * @param h
	 * @param url
	 * @return id or null on SQLException and if can't find 
	 */
	public static int getCategoryIdByURL(DataBaseHelper h, String url)
	{
		Integer id = null;
		try
		{
			id = h.getDaoCategory().queryBuilder().where().eq(Category.URL_FIELD_NAME, url).queryForFirst().getId();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * @return URL of initial article in category or null if can't find
	 */
	public static String getFirstArticleURLById(DataBaseHelper h, int categoryId)
	{
		String firstArtUrl = null;
		try
		{
			firstArtUrl = h.getDaoCategory().queryBuilder().where().eq(ID_FIELD_NAME, categoryId).queryForFirst()
			.getFirstArticleURL();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return firstArtUrl;
	}

	/**
	 * @param url
	 *            adress of category/ author on site, witch we search in
	 *            Category table
	 * @return true if we can find given URL in Category table, false if we find
	 *         it in Author and null on SQLException and if we can't find it at
	 *         all
	 */
	public static Boolean isCategory(DataBaseHelper h, String url)
	{
		Boolean isCategory = null;
		try
		{
			Category cat = h.getDaoCategory().queryBuilder().where().eq(Category.URL_FIELD_NAME, url)
			.queryForFirst();
			if (cat != null)
			{
				isCategory = true;
			}
			else
			{
				//try find in Author
				Author aut = h.getDaoAuthor().queryBuilder().where()
				.eq(Author.URL_FIELD_NAME, Author.getURLwithoutSlashAtTheEnd(url))
				.queryForFirst();
				if (aut != null)
				{
					isCategory = false;
				}
				else
				{
					//we can't find url both in Category and Author, so it's unknown Category; return null;
					isCategory = null;
				}
			}
		} catch (SQLException e)
		{
			Log.e(LOG, "SQLException isCategory");
		}
		return isCategory;
	}

	public String[] getAsStringArray()
	{
		String[] allInfo = new String[9];

		allInfo[0] = String.valueOf(id);
		allInfo[1] = url;
		allInfo[2] = title;
		allInfo[3] = description;
		allInfo[4] = img_url;
		allInfo[5] = img_file_name;

		allInfo[6] = refreshed.toString();
		allInfo[7] = lastArticleDate.toString();

		allInfo[8] = firstArticleURL;

		return allInfo;
	}

	/**
	 * returns String arr with names of all Table columns
	 */
	public static String[] getFieldsNames()
	{
		String[] arrStr1 = { "id", "url", "title", "description", "img_url", "img_file_name",
				"refreshed", "lastArticleDate", "firstArticleURL" };
		return arrStr1;
	}

	public static void setInitialArtsUrl(DataBaseHelper h, int categoryId, String initialArtsUrl)
	{
		UpdateBuilder<Category, Integer> updateBuilder;
		try
		{
			updateBuilder = h.getDaoCategory().updateBuilder();
			updateBuilder.where().eq(Category.ID_FIELD_NAME, categoryId);
			updateBuilder.updateColumnValue(Category.FIRST_ARTICLE_URL_FIELD_NAME, initialArtsUrl);
			updateBuilder.update();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
