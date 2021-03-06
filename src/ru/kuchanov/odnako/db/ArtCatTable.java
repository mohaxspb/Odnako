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

import android.util.Log;

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
	final private static String LOG = ArtCatTable.class.getSimpleName();

	public final static String ID_FIELD_NAME = "id";
	public final static String ARTICLE_ID_FIELD_NAME = "article_id";
	public final static String CATEGORY_ID_FIELD_NAME = "category_id";
	public static final String NEXT_ART_URL_FIELD_NAME = "nextArtUrl";
	public static final String PREVIOUS_ART_URL_FIELD_NAME = "previousArtUrl";
	public static final String IS_TOP_FIELD_NAME = "isTop";

	@DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
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

	/**
	 * 
	 * @return true if this is newest art in category and null otherwise
	 */
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
	 * Searches trough ArtCatTable for entry with given categoryId and TRUE
	 * isTop value so, on success we return true (there are arts of category in
	 * DB) and false on fail (there are NO arts of category in DB)
	 * 
	 * @param h
	 * @param categoryId
	 * @return
	 */
	public static boolean categoryArtsExists(DataBaseHelper h, int categoryId)
	{
		boolean exists = false;
		try
		{
			ArtCatTable topArt = h.getDaoArtCatTable().queryBuilder().where().eq(CATEGORY_ID_FIELD_NAME, categoryId)
			.and().eq(IS_TOP_FIELD_NAME, true).queryForFirst();
			if (topArt != null)
			{
				exists = true;
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return exists;
	}

	/**
	 * 
	 * @param h
	 * @param categoryId
	 * @param id
	 *            of ArtCat from witch we calculate first ArtCat of returning
	 *            list
	 * @param fromGivenId
	 *            if false, returning list will start from next of given id
	 * @return List of ArtCat with <=30 entries or null
	 */
	public static List<ArtCatTable> getArtCatTableListByCategoryIdFromGivenId(DataBaseHelper h, int categoryId, int id,
	boolean fromGivenId)
	{
		List<ArtCatTable> artCatTableListByCategoryIdFromGivenId = null;
		try
		{
			Integer firstArtCatID = null;
			if (fromGivenId)
			{
				firstArtCatID = id;
			}
			else
			{
				firstArtCatID = ArtCatTable.getNextArtCatId(h, id);
			}

			if (firstArtCatID != null)
			{
				ArtCatTable firstArtCat = h.getDaoArtCatTable().queryForId(firstArtCatID);
				artCatTableListByCategoryIdFromGivenId = new ArrayList<ArtCatTable>();
				artCatTableListByCategoryIdFromGivenId.add(firstArtCat);

				for (int i = 1; i < 30; i++)
				{
					Integer nextArtCatId = ArtCatTable.getNextArtCatId(h,
					artCatTableListByCategoryIdFromGivenId.get(i - 1).getId());
					if (nextArtCatId != null)
					{
						ArtCatTable nextArtCat = h.getDaoArtCatTable().queryForId(nextArtCatId);
						artCatTableListByCategoryIdFromGivenId.add(nextArtCat);
					}
					else
					{
						return artCatTableListByCategoryIdFromGivenId;
					}
				}
			}
			else
			{
				return artCatTableListByCategoryIdFromGivenId;
			}
		} catch (SQLException e)
		{
			//e.printStackTrace();
		}
		return artCatTableListByCategoryIdFromGivenId;
	}

	/**
	 * 
	 * @param h
	 * @param categoryId
	 *            ID of category
	 * @param isTop
	 *            true for top art, false for initial
	 * @return
	 */
	public static ArtCatTable getTopArtCat(DataBaseHelper h, int categoryId, boolean isTop)
	{
		ArtCatTable a = null;
		try
		{
			a = h.getDaoArtCatTable().queryBuilder().where().eq(IS_TOP_FIELD_NAME, isTop).and()
			.eq(CATEGORY_ID_FIELD_NAME, categoryId).queryForFirst();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return a;
	}

	/**
	 * updates isTop value to TRUE, FALSE or NULL
	 */
	public static void updateIsTop(DataBaseHelper h, int id, Boolean isTop)
	{
		UpdateBuilder<ArtCatTable, Integer> updateBuilder;
		try
		{
			updateBuilder = h.getDaoArtCatTable().updateBuilder();
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
	public static void updateNextArt(DataBaseHelper h, int id, String url)
	{
		UpdateBuilder<ArtCatTable, Integer> updateBuilder;
		try
		{
			updateBuilder = h.getDaoArtCatTable().updateBuilder();
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
	public static void updatePreviousArt(DataBaseHelper h, int id, String url)
	{
		UpdateBuilder<ArtCatTable, Integer> updateBuilder;
		try
		{
			updateBuilder = h.getDaoArtCatTable().updateBuilder();
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

			//Log.e(LOG, nextArtUrl);

			Article nextArt = h.getDaoArticle().queryBuilder().where().eq(Article.FIELD_NAME_URL, nextArtUrl)
			.queryForFirst();
			nextArtCatId = h.getDaoArtCatTable().queryBuilder().where().eq(ARTICLE_ID_FIELD_NAME, nextArt.getId())
			.and().eq(CATEGORY_ID_FIELD_NAME, a.getCategoryId()).queryForFirst().getId();
		} catch (SQLException e)
		{
			//e.printStackTrace();
		}
		return nextArtCatId;
	}

	public static List<ArtCatTable> getListFromTop(DataBaseHelper h, int categoryId, int pageToLoad)
	{
		List<ArtCatTable> allArtCatList = new ArrayList<ArtCatTable>();
		try
		{
			ArtCatTable topArt = ArtCatTable.getTopArtCat(h, categoryId, true);

			for (int i = 0; i < pageToLoad; i++)
			{
				if (i == 0)
				{
					allArtCatList.addAll(ArtCatTable.getArtCatTableListByCategoryIdFromGivenId(h,
					categoryId, topArt.getId(), true));
				}
				else
				{
					allArtCatList.addAll(ArtCatTable.getArtCatTableListByCategoryIdFromGivenId(h,
					categoryId, topArt.getId(), false));
				}
				topArt = allArtCatList.get(allArtCatList.size() - 1);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return allArtCatList;
	}

	/**
	 * @param h
	 * @param categoryId
	 * @param pageToLoad
	 *            need this for looping through each 30 rows
	 * @return ArtCatTable object that is last in list of entries one by one
	 */
	public static ArtCatTable getLastEntryFromTop(DataBaseHelper h, int categoryId, int pageToLoad)
	{
		ArtCatTable topArt = ArtCatTable.getTopArtCat(h, categoryId, true);
		ArtCatTable lastArtCat = null;
		List<ArtCatTable> allArtCatList = new ArrayList<ArtCatTable>();
		for (int i = 0; i < pageToLoad; i++)
		{
			allArtCatList.addAll(ArtCatTable.getArtCatTableListByCategoryIdFromGivenId(h,
			categoryId, topArt.getId(), false));
			topArt = allArtCatList.get(allArtCatList.size() - 1);
		}
		Log.e(LOG, "allArtCatList.size(): " + allArtCatList.size());
		lastArtCat = allArtCatList.get(allArtCatList.size() - 1);

		return lastArtCat;
	}

	public static void write(DataBaseHelper h, List<ArtCatTable> dataToWrite)
	{
		for (ArtCatTable a : dataToWrite)
		{
			try
			{
				h.getDaoArtCatTable().create(a);
			} catch (SQLException e)
			{
				Log.e(LOG, "error while inserting ArtCatTable entry");
			}
		}
	}

	/**
	 * 
	 * @param h
	 * @param categoryId
	 * @return
	 */
	public static List<ArtCatTable> getAllRowsWithoutPrevArt(DataBaseHelper h, int categoryId)
	{
		List<ArtCatTable> allRowsWithoutPrevArt = null;

		try
		{
			h.getDaoArtCatTable().queryForEq(ArtCatTable.PREVIOUS_ART_URL_FIELD_NAME, null);
		} catch (SQLException e)
		{
			//			e.printStackTrace();
		}
		return allRowsWithoutPrevArt;
	}

	/**
	 * this is used to create ArtInfo objects from given ArtCatTable objects
	 * 
	 * @param h
	 * @param dBObjects
	 * @return
	 */
	public static ArrayList<Article> getArticleListFromArtCatList(DataBaseHelper h, List<ArtCatTable> dBObjects)
	{
		ArrayList<Article> data = new ArrayList<Article>();
		for (ArtCatTable a : dBObjects)
		{
			Article art = Article.getArticleById(h, a.getArticleId());
			data.add(art);
		}
		return data;
	}

	/**
	 * 
	 * @param artToWrite
	 *            list from web to made list of ArtCatTable objects
	 * @param categoryId
	 * @return list of ArtCatTable made from ArtInfo list
	 */
	public static List<ArtCatTable> getArtCatListFromArticleList(DataBaseHelper h, List<Article> artToWrite,
	int categoryId)
	{
		List<ArtCatTable> artCatTableList = new ArrayList<ArtCatTable>();
		for (int u = 0; u < artToWrite.size(); u++)
		{
			//get Article id by url
			int articleId = artToWrite.get(u).getId();
			//get next Article url by asking gained from web list
			String nextArtUrl = null;
			try
			{
				nextArtUrl = artToWrite.get(u + 1).getUrl();
			} catch (Exception e)
			{
//				e.printStackTrace();
			}
			//get previous Article url by asking gained from web list
			String previousArtUrl = null;
			try
			{
				previousArtUrl = artToWrite.get(u - 1).getUrl();
			} catch (Exception e)
			{
//				e.printStackTrace();
			}
			artCatTableList.add(new ArtCatTable(null, articleId, categoryId, nextArtUrl, previousArtUrl));
		}
		return artCatTableList;
	}

	/**
	 * Searches through DB for artCatTable rows with given id
	 * 
	 * @param h
	 * @param categoryId
	 * @return List<ArtCatTable> or empty list on SQLException or if can't find
	 */
	public static List<ArtCatTable> getAllRowsByCategoryId(DataBaseHelper h, int categoryId)
	{
		List<ArtCatTable> objs;//new ArrayList<ArtCatTable>();
		try
		{
			objs = h.getDaoArtCatTable().queryBuilder().where().eq(CATEGORY_ID_FIELD_NAME, categoryId).query();
		} catch (SQLException e)
		{
			e.printStackTrace();
			objs = new ArrayList<ArtCatTable>();
		}
		return objs;
	}

	/**
	 * Get's ids of given list and delete by them;
	 * 
	 * @param h
	 * @param rowsToDelete
	 */
	public static void delete(DataBaseHelper h, List<ArtCatTable> rowsToDelete)
	{
		try
		{
			h.getDaoArtCatTable().delete(rowsToDelete);
			//			DeleteBuilder<ArtCatTable, Integer> dB = h.getDaoArtCatTable().deleteBuilder();
			//			Where<ArtCatTable, Integer> where = dB.where();
			//			for (int i = 0; i < rowsToDelete.size(); i++)
			//			{
			//				where.eq(ID_FIELD_NAME, rowsToDelete.get(i).getId());
			//				if (i != rowsToDelete.size() - 1)
			//				{
			//					where.and();
			//				}
			//			}
			//			dB.delete();
		} catch (SQLException e)
		{
			Log.e(LOG, "error while deleting");
		}
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

	@Override
	public String toString()
	{
		String s = "id: " + this.id + " artId: " + this.article_id + " catId: " + this.category_id + " prevUrl: "
		+ this.previousArtUrl + " nextUrl: " + this.nextArtUrl;
		return s;
	}
}