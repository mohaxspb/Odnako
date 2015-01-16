/*
 09.12.2014
ArtCatTable.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
* id,article_id,category_id,nextArtUrl,previousArtUrl,isTop
*/
@DatabaseTable(tableName = "art_aut_table")
public class ArtAutTable
{
	public final static String ARTICLE_ID_FIELD_NAME = "article_id";
	public final static String AUTHOR_ID_FIELD_NAME = "author_id";
	public static final String NEXT_ART_URL_FIELD_NAME = "nextArtUrl";
	public static final String PREVIOUS_ART_URL_FIELD_NAME = "previousArtUrl";
	public static final String IS_TOP_FIELD_NAME = "isTop";

	@DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
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
	 * boolean isTop for the most top article in list. May be true for top, false for very bottom (initial in category) or null for others
	 */
	@DatabaseField(dataType = DataType.BOOLEAN, columnName = IS_TOP_FIELD_NAME)
	private boolean isTop;

	public ArtAutTable()
	{
		// TODO need empty constructor for ORMlite
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

	public int getArticle_id()
	{
		return article_id;
	}

	public void setArticle_id(int article_id)
	{
		this.article_id = article_id;
	}

	public int getCategory_id()
	{
		return author_id;
	}

	public void setCategory_id(int category_id)
	{
		this.author_id = category_id;
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

}
