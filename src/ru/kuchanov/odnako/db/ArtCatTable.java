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
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.table.DatabaseTable;

/**
 * id,article_id,category_id,nextArtUrl,previousArtUrl,isTop
 */
@DatabaseTable(tableName = "art_cat_table")
public class ArtCatTable
{
	public final static String ID_FIELD_NAME = "id";
	public final static String ARTICLE_ID_FIELD_NAME = "article_id";
	public final static String CATEGORY_ID_FIELD_NAME = "category_id";
	public static final String NEXT_ART_URL_FIELD_NAME = "nextArtUrl";
	public static final String PREVIOUS_ART_URL_FIELD_NAME = "previousArtUrl";
	public static final String IS_TOP_FIELD_NAME = "isTop";

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

	/**
	 * boolean isTop for the most top article in list. May be true for top,
	 * false for very bottom (initial in category) or null for others
	 */
	@DatabaseField(dataType = DataType.BOOLEAN_OBJ, columnName = IS_TOP_FIELD_NAME, canBeNull = true)
	private Boolean isTop;

	public ArtCatTable()
	{
		//need empty constructor for ORMlite
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

	public boolean isTop()
	{
		return isTop;
	}

	public void isTop(boolean isTop)
	{
		this.isTop = isTop;
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
//	public static List<ArtCatTable> getArtCatTableListByCategoryIdFromFirstId(DataBaseHelper h, int categoryId)
//	{
//		List<ArtCatTable> artCatTableListByCategoryIdFromGivenId = null;
//
//		int id = getIdForFirstArticleInCategory(h, categoryId);
//
//		try
//		{
//			artCatTableListByCategoryIdFromGivenId = h.getDaoArtCatTable().queryBuilder().where()
//			.ge(ArtCatTable.ID_FIELD_NAME, id).query();
//		} catch (SQLException e)
//		{
//			e.printStackTrace();
//		}
//
//		return artCatTableListByCategoryIdFromGivenId;
//	}
	
	public static List<ArtCatTable> getArtCatTableListByCategoryIdFromGivenId(DataBaseHelper h, int categoryId, int id)
	{
		List<ArtCatTable> artCatTableListByCategoryIdFromGivenId = null;

		try
		{
			ArtCatTable firstArtCat=h.getDaoArtCatTable().queryForId(id);
			artCatTableListByCategoryIdFromGivenId.add(firstArtCat);
			
			for(int i=1; i<30; i++)
			{
				int nextArtCatId=ArtCatTable.getNextArtCatId(h, artCatTableListByCategoryIdFromGivenId.get(i-1).getId());
				ArtCatTable nextArtCat=h.getDaoArtCatTable().queryForId(nextArtCatId);
				artCatTableListByCategoryIdFromGivenId.add(nextArtCat);							
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		return artCatTableListByCategoryIdFromGivenId;
	}

	/**
	 * 
	 * @param h
	 * @param categoryId ID of category
	 * @param isTop
	 *            true for top art, false for initial
	 * @return
	 */
	public static ArtCatTable getTopArtCat(DataBaseHelper h, int categoryId, boolean isTop)
	{
		ArtCatTable a = null;

		try
		{
			a = h.getDaoArtCatTable().queryBuilder().where().eq(IS_TOP_FIELD_NAME, isTop).and().eq(CATEGORY_ID_FIELD_NAME, categoryId).queryForFirst();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		return a;
	}

	/**
	 * updates isTop value to TRUE, FALSE or NULL
	 */
	//	public static void updateIsTop(DataBaseHelper h, ArtCatTable a, Boolean isTop)
	public static void updateIsTop(DataBaseHelper h, int id, Boolean isTop)
	{
		UpdateBuilder<ArtCatTable, Integer> updateBuilder;
		try
		{
			updateBuilder = h.getDaoArtCatTable().updateBuilder();
			//			updateBuilder.where().equals(a);
			updateBuilder.where().eq(ArtCatTable.ID_FIELD_NAME, id);
			updateBuilder.updateColumnValue(ArtCatTable.IS_TOP_FIELD_NAME, isTop);
			updateBuilder.update();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * updates nextUrl
	 */
	//	public static void updateNextArt(DataBaseHelper h, ArtCatTable a, String url)
	public static void updateNextArt(DataBaseHelper h, int id, String url)
	{
		UpdateBuilder<ArtCatTable, Integer> updateBuilder;
		try
		{
			updateBuilder = h.getDaoArtCatTable().updateBuilder();
			//			updateBuilder.where().equals(a);
			updateBuilder.where().eq(ArtCatTable.ID_FIELD_NAME, id);
			updateBuilder.updateColumnValue(ArtCatTable.NEXT_ART_URL_FIELD_NAME, url);
			updateBuilder.update();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * updates previousUrl
	 */
	//	public static void updatePreviousArt(DataBaseHelper h, ArtCatTable a, String url)
	public static void updatePreviousArt(DataBaseHelper h, int id, String url)
	{
		UpdateBuilder<ArtCatTable, Integer> updateBuilder;
		try
		{
			updateBuilder = h.getDaoArtCatTable().updateBuilder();
			//			updateBuilder.where().equals(a);
			updateBuilder.where().eq(ArtCatTable.ID_FIELD_NAME, id);
			updateBuilder.updateColumnValue(ArtCatTable.PREVIOUS_ART_URL_FIELD_NAME, url);
			updateBuilder.update();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return id of the next article of given artCat id or null if !exists
	 *         (empty nextArtUrl field)
	 */
	public static Integer getNextArtCatId(DataBaseHelper h, int id)
	{
		Integer nextArtCatId = null;
		try
		{
			ArtCatTable a = h.getDaoArtCatTable().queryForId(id);
			String nextArtUrl = a.getNextArtUrl();
			Article nextArt = h.getDaoArticle().queryBuilder().where().eq(Article.URL_FIELD_NAME, nextArtUrl)
			.queryForFirst();
			nextArtCatId = h.getDaoArtCatTable().queryBuilder().where().eq(ARTICLE_ID_FIELD_NAME, nextArt.getId())
			.and().eq(CATEGORY_ID_FIELD_NAME, a.getCategoryId()).queryForFirst().getId();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return nextArtCatId;
	}

	public String[] getAsStringArray()
	{
		String[] allInfo = new String[6];

		allInfo[0] = String.valueOf(id);
		allInfo[1] = String.valueOf(article_id);
		allInfo[2] = String.valueOf(category_id);
		allInfo[3] = nextArtUrl;
		allInfo[4] = previousArtUrl;
		allInfo[5] = String.valueOf(isTop);

		return allInfo;
	}

	/**
	 * returns String arr with names of all Table columns
	 */
	public static String[] getFieldsNames()
	{
		String[] arrStr1 = { "id", "article_id", "category_id", "nextArtUrl", "previousArtUrl", "isTop" };
		return arrStr1;
	}
}
