/*
 09.12.2014
ArtCatTable.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "art_cat_table")
public class ArtCatTable
{
	public final static String ID_FIELD_NAME = "id";
	public final static String ARTICLE_ID_FIELD_NAME = "article_id";
	public final static String CATEGORY_ID_FIELD_NAME = "category_id";
	public static final String NEXT_ART_URL_FIELD_NAME = "nextArtUrl";
	public static final String PREVIOUS_ART_URL_FIELD_NAME = "previousArtUrl";

	@DatabaseField(generatedId = true, allowGeneratedIdInsert = true, columnName = ID_FIELD_NAME)
	private int id;

	@DatabaseField(dataType = DataType.INTEGER, canBeNull = false, index = true, columnName = ARTICLE_ID_FIELD_NAME)
	private int article_id;

	@DatabaseField(dataType = DataType.INTEGER, canBeNull = false, index = true, columnName = CATEGORY_ID_FIELD_NAME)
	private int category_id;

	@DatabaseField(dataType = DataType.STRING, columnName = NEXT_ART_URL_FIELD_NAME)
	private String nextArtUrl;

	@DatabaseField(dataType = DataType.STRING, columnName = PREVIOUS_ART_URL_FIELD_NAME)
	private String previousArtUrl;

	public ArtCatTable()
	{
		// TODO need empty constructor for ORMlite
	}

	/**
	 * 
	 * @param id
	 *            id of entry
	 * @param article_id
	 *            id of Article
	 * @param category_id
	 *            id of Category
	 * @param nextArtUrl
	 *            next Article in list, so older
	 * @param previousArtUrl
	 *            previous Article, so newer
	 */
	public ArtCatTable(Integer id, int article_id, int category_id, String nextArtUrl, String previousArtUrl)
	{
		//as I understand, if we do not set any value to id it will be genereted automatically.
		//so set it only if it's (!null);
		if (id != null)
		{
			this.id = id;
		}
		else
		{
			//gained id=null, so it must be set automatically by ORMLite
		}

		this.article_id = article_id;
		this.category_id = category_id;
		this.setNextArtUrl(nextArtUrl);
		this.setPreviousArtUrl(previousArtUrl);
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getArticleId()
	{
		return article_id;
	}

	public void setArticleId(int article_id)
	{
		this.article_id = article_id;
	}

	public int getCategoryId()
	{
		return category_id;
	}

	public void setCategoryId(int category_id)
	{
		this.category_id = category_id;
	}

	public String getPreviousArtUrl()
	{
		return previousArtUrl;
	}

	public void setPreviousArtUrl(String previousArtUrl)
	{
		this.previousArtUrl = previousArtUrl;
	}

	public String getNextArtUrl()
	{
		return nextArtUrl;
	}

	public void setNextArtUrl(String nextArtUrl)
	{
		this.nextArtUrl = nextArtUrl;
	}

	//static methods for querying
	/**
	 * 
	 * @param h
	 * @param id
	 * @return List<ArtCatTable> by given Category id or null on SQLException
	 */
	public static List<ArtCatTable> getArtCatTableListByCategoryId(DataBaseHelper h, int id)
	{
		List<ArtCatTable> l = null;
		try
		{
			l = h.getDaoArtCatTable().queryBuilder().where().eq(ArtCatTable.CATEGORY_ID_FIELD_NAME, id).query();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return l;
	}

	/**
	 * 
	 * @param helper
	 * @param categoryId
	 * @return id for first entry of Article of given category's id or null on
	 *         SQLException
	 */
	public static int getIdForFirstArticleInCategory(DataBaseHelper h, int categoryId)
	{
		Integer id = null;
		try
		{
			id = h.getDaoArtCatTable().queryBuilder().where().eq(CATEGORY_ID_FIELD_NAME, categoryId).queryForFirst()
			.getId();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		return id;

	}

	/**
	 * 
	 * @param helper
	 * @param categoryId
	 * @return
	 */
	public static List<ArtCatTable> getArtCatTableListByCategoryIdFromFirstId(DataBaseHelper h, int categoryId)
	{
		List<ArtCatTable> artCatTableListByCategoryIdFromGivenId = null;

		int id = getIdForFirstArticleInCategory(h, categoryId);

		try
		{
			artCatTableListByCategoryIdFromGivenId = h.getDaoArtCatTable().queryBuilder().where()
			.ge(ArtCatTable.ID_FIELD_NAME, id).query();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		return artCatTableListByCategoryIdFromGivenId;
	}

	/**
	 * 
	 * @param helper
	 * @param id
	 * 
	 *            delete all entries from given id to the end
	 */
	public static void deleteEntriesFromGivenIdToEnd(DataBaseHelper h, int id)
	{
		List<Integer> ids = new ArrayList<Integer>();

		List<ArtCatTable> artCatTableList = new ArrayList<ArtCatTable>();
		try
		{
			artCatTableList = h.getDaoArtCatTable().queryBuilder().where().ge(ArtCatTable.ID_FIELD_NAME, id).query();
		} catch (SQLException e1)
		{
			e1.printStackTrace();
		}
		for (ArtCatTable a : artCatTableList)
		{
			ids.add(a.getId());
		}

		try
		{
			h.getDaoArtCatTable().deleteIds(ids);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

}
