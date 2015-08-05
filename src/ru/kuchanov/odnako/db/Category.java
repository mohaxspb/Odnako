/*
 09.12.2014
Category.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.sql.SQLException;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
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
public class Category implements Parcelable
{
	private static final String LOG = Category.class.getSimpleName() + "/";

	public final static String FIELD_ID = "id";
	public final static String FIELD_URL = "url";
	public final static String FIELD_TITLE = "title";
	public final static String FIELD_REFRESHED = "refreshed";
	public static final String FIELD_FIRST_ARTICLE_URL = "firstArticleURL";
	public static final String FIELD_IMAGE_URL = "imgUrl";

	@DatabaseField(generatedId = true, columnName = FIELD_ID)
	private int id;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false, columnName = FIELD_URL)
	private String url;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false, columnName = FIELD_TITLE)
	private String title;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String description;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false, columnName = FIELD_IMAGE_URL)
	private String imgUrl;

	@DatabaseField(dataType = DataType.DATE, canBeNull = false, columnName = FIELD_REFRESHED)
	private Date refreshed;

	@DatabaseField(dataType = DataType.DATE, canBeNull = false)
	private Date lastArticleDate;

	//we need this to check if we have all arts at the end of category's list
	//we need it to prevent loading arts from web, when category is synked, and we have less than 30 arts at all,
	//or in same case while requesting arts from bottom
	@DatabaseField(dataType = DataType.STRING, columnName = FIELD_FIRST_ARTICLE_URL)
	private String firstArticleURL;

	/**
	 * empty constructor witch is need for OrmLite
	 */
	public Category()
	{

	}

	public Category(String url, String title, String description, String img_url, Date refreshed,
	Date lastArticleDate)
	{
		this.url = url;
		this.title = title;
		this.description = description;
		this.imgUrl = img_url;
		this.refreshed = refreshed;
		this.lastArticleDate = lastArticleDate;
	}

	public Category(String[] stringData, Date[] dateData)
	{
		this.url = stringData[0];
		this.title = stringData[1];
		this.description = stringData[2];
		this.imgUrl = stringData[3];
		this.refreshed = dateData[0];
		this.lastArticleDate = dateData[1];
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

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getImgUrl()
	{
		return imgUrl;
	}

	public void setImgUrl(String img_url)
	{
		this.imgUrl = img_url;
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

	/**
	 * 
	 * @param h
	 * @param url
	 * @return Category if can find or (null on SQLException or if can't find)
	 */
	public static Category getCategoryByURL(DataBaseHelper h, String url)
	{
		Category c = null;
		try
		{
			c = h.getDaoCategory().queryBuilder().where().eq(FIELD_URL, url).queryForFirst();
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
			id = h.getDaoCategory().queryBuilder().where().eq(Category.FIELD_URL, url).queryForFirst().getId();
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
			firstArtUrl = h.getDaoCategory().queryBuilder().where().eq(FIELD_ID, categoryId).queryForFirst()
			.getFirstArticleURL();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return firstArtUrl;
	}

	public static String getNameByUrl(DataBaseHelper h, String url)
	{
		String name;
		try
		{
			name = h.getDaoCategory().queryBuilder().where().eq(FIELD_URL, url).queryForFirst().getTitle();
		} catch (SQLException e)
		{
			name = url;
			e.printStackTrace();
		}
		return name;
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
			Category cat = h.getDaoCategory().queryBuilder().where().eq(Category.FIELD_URL, url)
			.queryForFirst();
			if (cat != null)
			{
				isCategory = true;
				return isCategory;
			}
			else
			{
				//try find in Author
				Author aut = h.getDaoAuthor().queryBuilder().where()
				.eq(Author.FIELD_URL, Author.getURLwithoutSlashAtTheEnd(url))
				.queryForFirst();
				if (aut != null)
				{
					isCategory = false;
					return isCategory;
				}
				else
				{
					//we can't find url both in Category and Author, so it's unknown Category; return null;
					//					//try without slash at the end
					cat = h.getDaoCategory().queryBuilder().where()
					.eq(Category.FIELD_URL, Author.getURLwithoutSlashAtTheEnd(url))
					.queryForFirst();
					if (cat != null)
					{
						isCategory = true;
						return isCategory;
					}
					isCategory = (cat == null) ? null : true;//isCategory(h, Author.getURLwithoutSlashAtTheEnd(url));
					if (isCategory == null)
					{
						//try with slash...
						cat = h.getDaoCategory().queryBuilder().where().eq(Category.FIELD_URL, url + "/")
						.queryForFirst();
						isCategory = (cat == null) ? null : true;
						if (cat != null)
						{
							isCategory = true;
							return isCategory;
						}
					}
					isCategory = null;
					return isCategory;
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
		String[] allInfo = new String[8];

		allInfo[0] = String.valueOf(id);
		allInfo[1] = url;
		allInfo[2] = title;
		allInfo[3] = description;
		allInfo[4] = imgUrl;

		allInfo[5] = refreshed.toString();
		allInfo[6] = lastArticleDate.toString();

		allInfo[7] = firstArticleURL;

		return allInfo;
	}

	/**
	 * returns String arr with names of all Table columns
	 */
	public static String[] getFieldsNames()
	{
		String[] arrStr1 = { "id", "url", "title", "description", "img_url",
				"refreshed", "lastArticleDate", "firstArticleURL" };
		return arrStr1;
	}

	public static void setInitialArtsUrl(DataBaseHelper h, int categoryId, String initialArtsUrl)
	{
		UpdateBuilder<Category, Integer> updateBuilder;
		try
		{
			updateBuilder = h.getDaoCategory().updateBuilder();
			updateBuilder.where().eq(Category.FIELD_ID, categoryId);
			updateBuilder.updateColumnValue(Category.FIELD_FIRST_ARTICLE_URL, initialArtsUrl);
			updateBuilder.update();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	//////PARCEL implementation
	@Override
	public int describeContents()
	{
		return 0;
	}

	//"id", "url", "title", "description", "img_url",
	//"refreshed", "lastArticleDate", "firstArticleURL"
	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(id);
		dest.writeString(url);
		dest.writeString(title);
		dest.writeString(description);
		dest.writeString(imgUrl);
		dest.writeLong(refreshed.getTime());
		dest.writeLong(lastArticleDate.getTime());
		dest.writeString(firstArticleURL);
	}

	private Category(Parcel in)
	{
		id = in.readInt();
		url = in.readString();
		title = in.readString();
		description = in.readString();
		imgUrl = in.readString();
		refreshed = new Date(in.readLong());
		lastArticleDate = new Date(in.readLong());
		firstArticleURL = in.readString();
	}

	public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>()
	{
		@Override
		public Category createFromParcel(Parcel source)
		{
			return new Category(source);
		}

		@Override
		public Category[] newArray(int size)
		{
			return new Category[size];
		}
	};
}