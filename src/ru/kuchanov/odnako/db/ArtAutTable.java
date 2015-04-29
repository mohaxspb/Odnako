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
@DatabaseTable(tableName = "art_aut_table")
public class ArtAutTable
{
	final private static String LOG = ArtAutTable.class.getSimpleName();

	public final static String ID_FIELD_NAME = "id";
	public final static String ARTICLE_ID_FIELD_NAME = "article_id";
	public final static String AUTHOR_ID_FIELD_NAME = "author_id";
	public static final String NEXT_ART_URL_FIELD_NAME = "nextArtUrl";
	public static final String PREVIOUS_ART_URL_FIELD_NAME = "previousArtUrl";
	public static final String IS_TOP_FIELD_NAME = "isTop";

	@DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
	private int id;

	@DatabaseField(dataType = DataType.INTEGER, canBeNull = false, index = true, columnName = ARTICLE_ID_FIELD_NAME)
	private int article_id;

	@DatabaseField(dataType = DataType.INTEGER, canBeNull = false, index = true, columnName = AUTHOR_ID_FIELD_NAME)
	private int author_id;

	@DatabaseField(dataType = DataType.STRING, columnName = NEXT_ART_URL_FIELD_NAME)
	private String nextArtUrl;

	@DatabaseField(dataType = DataType.STRING, columnName = PREVIOUS_ART_URL_FIELD_NAME)
	private String previousArtUrl;

	/**
	 * boolean isTop for the most top article in list. May be true for top,
	 * false for very bottom (initial in category) or null for others
	 */
	@DatabaseField(dataType = DataType.BOOLEAN, columnName = IS_TOP_FIELD_NAME)
	private boolean isTop;

	public ArtAutTable()
	{
		//need empty constructor for ORMlite
	}

	public ArtAutTable(Integer id, int article_id, int author_id, String nextArtUrl, String previousArtUrl)
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
		this.author_id = author_id;
		this.setNextArtUrl(nextArtUrl);
		this.setPreviousArtUrl(previousArtUrl);
	}

	public ArtAutTable(int id, int articleId, int authorId)
	{
		this.id = id;
		this.article_id = articleId;
		this.author_id = authorId;
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

	public void setArticleId(int articleId)
	{
		this.article_id = articleId;
	}

	public int getAuthorId()
	{
		return author_id;
	}

	public void setAuthorId(int authorId)
	{
		this.author_id = authorId;
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

	/**
	 * updates isTop value to TRUE, FALSE or NULL
	 */
	public static void updateIsTop(DataBaseHelper h, int id, Boolean isTop)
	{
		UpdateBuilder<ArtAutTable, Integer> updateBuilder;
		try
		{
			updateBuilder = h.getDaoArtAutTable().updateBuilder();
			updateBuilder.where().eq(ArtAutTable.ID_FIELD_NAME, id);
			updateBuilder.updateColumnValue(ArtAutTable.IS_TOP_FIELD_NAME, isTop);
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
		UpdateBuilder<ArtAutTable, Integer> updateBuilder;
		try
		{
			updateBuilder = h.getDaoArtAutTable().updateBuilder();
			updateBuilder.where().eq(ArtAutTable.ID_FIELD_NAME, id);
			updateBuilder.updateColumnValue(ArtAutTable.NEXT_ART_URL_FIELD_NAME, url);
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
		UpdateBuilder<ArtAutTable, Integer> updateBuilder;
		try
		{
			updateBuilder = h.getDaoArtAutTable().updateBuilder();
			updateBuilder.where().eq(ArtAutTable.ID_FIELD_NAME, id);
			updateBuilder.updateColumnValue(ArtAutTable.PREVIOUS_ART_URL_FIELD_NAME, url);
			updateBuilder.update();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	//////
	/**
	 * Searches trough ArtCatTable for entry with given categoryId and TRUE
	 * isTop value so, on success we return true (there are arts of category in
	 * DB) and false on fail (there are NO arts of category in DB)
	 * 
	 * @param h
	 * @param authorId
	 * @return
	 */
	public static boolean authorArtsExists(DataBaseHelper h, int authorId)
	{
		boolean exists = false;
		try
		{
			ArtAutTable topArt = h.getDaoArtAutTable().queryBuilder().where().eq(AUTHOR_ID_FIELD_NAME, authorId)
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
	 *            ID of category
	 * @param isTop
	 *            true for top art, false for initial
	 * @return
	 */
	public static ArtAutTable getTopArt(DataBaseHelper h, int authorId, boolean isTop)
	{
		ArtAutTable a = null;
		try
		{
			a = h.getDaoArtAutTable().queryBuilder().where().eq(IS_TOP_FIELD_NAME, isTop).and()
			.eq(AUTHOR_ID_FIELD_NAME, authorId).queryForFirst();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return a;
	}

	public static List<ArtAutTable> getListFromTop(DataBaseHelper h, int authorId, int pageToLoad)
	{
		ArtAutTable topArt = ArtAutTable.getTopArt(h, authorId, true);
		List<ArtAutTable> allArtAutList = new ArrayList<ArtAutTable>();
		for (int i = 0; i < pageToLoad; i++)
		{
			if (i == 0)
			{
				allArtAutList.addAll(ArtAutTable.getArtAutTableListByAuthorIdFromGivenId(h,
				authorId, topArt.getId(), true));
			}
			else
			{
				allArtAutList.addAll(ArtAutTable.getArtAutTableListByAuthorIdFromGivenId(h,
				authorId, topArt.getId(), false));
			}
			topArt = allArtAutList.get(allArtAutList.size() - 1);
		}
		return allArtAutList;
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
	 * @return List of ArtCat with <=30 entries
	 */
	public static List<ArtAutTable> getArtAutTableListByAuthorIdFromGivenId(DataBaseHelper h, int authorId, int id,
	boolean fromGivenId)
	{
		List<ArtAutTable> artAutTableListByAuthorIdFromGivenId = null;
		try
		{
			Integer firstArtAutID = null;
			if (fromGivenId)
			{
				firstArtAutID = id;
			}
			else
			{
				firstArtAutID = ArtAutTable.getNextArtAutId(h, id);
			}

			if (firstArtAutID != null)
			{
				ArtAutTable firstArtAut = h.getDaoArtAutTable().queryForId(firstArtAutID);
				artAutTableListByAuthorIdFromGivenId = new ArrayList<ArtAutTable>();
				artAutTableListByAuthorIdFromGivenId.add(firstArtAut);

				for (int i = 1; i < 30; i++)
				{
					Integer nextArtAutId = ArtAutTable.getNextArtAutId(h,
					artAutTableListByAuthorIdFromGivenId.get(i - 1).getId());
					if (nextArtAutId != null)
					{
						ArtAutTable nextArtAut = h.getDaoArtAutTable().queryForId(nextArtAutId);
						artAutTableListByAuthorIdFromGivenId.add(nextArtAut);
					}
					else
					{
						return artAutTableListByAuthorIdFromGivenId;
					}
				}
			}
			else
			{
				return artAutTableListByAuthorIdFromGivenId;
			}
		} catch (SQLException e)
		{
			//e.printStackTrace();
		}
		return artAutTableListByAuthorIdFromGivenId;
	}

	/**
	 * 
	 * @return id of the next article of given artCat id or null if !exists
	 *         (empty nextArtUrl field)
	 */
	public static Integer getNextArtAutId(DataBaseHelper h, int id)
	{
		Integer nextArtAutId = null;
		try
		{
			ArtAutTable a = h.getDaoArtAutTable().queryForId(id);
			String nextArtUrl = a.getNextArtUrl();

			Article nextArt = h.getDaoArticle().queryBuilder().where().eq(Article.URL_FIELD_NAME, nextArtUrl)
			.queryForFirst();
			nextArtAutId = h.getDaoArtAutTable().queryBuilder().where().eq(ARTICLE_ID_FIELD_NAME, nextArt.getId())
			.and().eq(AUTHOR_ID_FIELD_NAME, a.getAuthorId()).queryForFirst().getId();
		} catch (SQLException e)
		{
			//e.printStackTrace();
		}
		catch(NullPointerException e)
		{
			//e.printStackTrace();
		}
		return nextArtAutId;
	}

	/**
	 * this is used to create ArtInfo objects from given ArtCatTable objects
	 * 
	 * @param h
	 * @param dBObjects
	 * @return
	 */
	public static ArrayList<Article> getArtInfoListFromArtAutList(DataBaseHelper h, List<ArtAutTable> dBObjects)
	{
		ArrayList<Article> data = new ArrayList<Article>();
		for (ArtAutTable a : dBObjects)
		{
			Article art = Article.getArticleById(h, a.getArticleId());
			//			ArtInfo artInfoObj = new ArtInfo(art.getAsStringArray());
			data.add(art);
		}
		return data;
	}

	/**
	 * 
	 * @param artToWrite
	 *            list from web to made list of ArtCatTable objects
	 * @param authorId
	 * @return list of ArtCatTable made from ArtInfo list
	 */
	public static List<ArtAutTable> getArtAutListFromArtInfoList(DataBaseHelper h, List<Article> artToWrite,
	int authorId)
	{
		List<ArtAutTable> artAutTableList = new ArrayList<ArtAutTable>();
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

			}
			//get previous Article url by asking gained from web list
			String previousArtUrl = null;
			try
			{
				previousArtUrl = artToWrite.get(u - 1).getUrl();
			} catch (Exception e)
			{

			}
			artAutTableList.add(new ArtAutTable(null, articleId, authorId, nextArtUrl, previousArtUrl));
		}
		return artAutTableList;
	}

	//	public static List<ArtAutTable> getArtAutListFromArtInfoList(DataBaseHelper h, List<ArtInfo> artToWrite,
	//	int authorId)
	//	{
	//		List<ArtAutTable> artAutTableList = new ArrayList<ArtAutTable>();
	//		for (int u = 0; u < artToWrite.size(); u++)
	//		{
	//			//get Article id by url
	//			int articleId = Article.getArticleIdByURL(h, artToWrite.get(u).url);
	//			//get next Article url by asking gained from web list
	//			String nextArtUrl = null;
	//			try
	//			{
	//				nextArtUrl = artToWrite.get(u + 1).url;
	//			} catch (Exception e)
	//			{
	//
	//			}
	//			//get previous Article url by asking gained from web list
	//			String previousArtUrl = null;
	//			try
	//			{
	//				previousArtUrl = artToWrite.get(u - 1).url;
	//			} catch (Exception e)
	//			{
	//
	//			}
	//			artAutTableList.add(new ArtAutTable(null, articleId, authorId, nextArtUrl, previousArtUrl));
	//		}
	//		return artAutTableList;
	//	}

	/**
	 * 
	 * @param h
	 * @param authorId
	 * @return
	 */
	public static List<ArtAutTable> getAllRowsWithoutPrevArt(DataBaseHelper h, int authorId)
	{
		List<ArtAutTable> allRowsWithoutPrevArt = null;

		try
		{
			h.getDaoArtAutTable().queryForEq(ArtAutTable.PREVIOUS_ART_URL_FIELD_NAME, null);
		} catch (SQLException e)
		{
			//			e.printStackTrace();
		}

		return allRowsWithoutPrevArt;
	}

	public static void write(DataBaseHelper h, List<ArtAutTable> dataToWrite)
	{
		for (ArtAutTable a : dataToWrite)
		{
			try
			{
				h.getDaoArtAutTable().create(a);
			} catch (SQLException e)
			{
				Log.e(LOG, "error while inserting ArtCatTable entry");
			}
		}
	}

	/**
	 * Get's ids of given list and delete by them;
	 * 
	 * @param h
	 * @param rowsToDelete
	 */
	public static void delete(DataBaseHelper h, List<ArtAutTable> rowsToDelete)
	{
		try
		{
			h.getDaoArtAutTable().delete(rowsToDelete);
			//			DeleteBuilder<ArtAutTable, Integer> dB = h.getDaoArtAutTable().deleteBuilder();
			//			Where<ArtAutTable, Integer> where = dB.where();
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

	//methods for content provider
	public String[] getAsStringArray()
	{
		String[] allInfo = new String[6];

		allInfo[0] = String.valueOf(id);
		allInfo[1] = String.valueOf(article_id);
		allInfo[2] = String.valueOf(author_id);
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
		String[] arrStr1 = { "id", "article_id", "author_id", "nextArtUrl", "previousArtUrl", "isTop" };
		return arrStr1;
	}

	/**
	 * return found rows or empty list on exception or if can't find
	 * 
	 * @param h
	 * @param authorId
	 * @return
	 */
	public static List<ArtAutTable> getAllRowsByCategoryId(DataBaseHelper h, int authorId)
	{
		List<ArtAutTable> objs = new ArrayList<ArtAutTable>();
		try
		{
			objs = h.getDaoArtAutTable().queryBuilder().where().eq(AUTHOR_ID_FIELD_NAME, authorId).query();
		} catch (SQLException e)
		{
			e.printStackTrace();
			objs = new ArrayList<ArtAutTable>();
		}
		return objs;
	}
}
