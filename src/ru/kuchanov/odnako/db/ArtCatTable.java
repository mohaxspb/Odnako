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

@DatabaseTable(tableName = "art_cat_table")
public class ArtCatTable
{
	public final static String ARTICLE_ID_FIELD_NAME="article_id";
	public final static String CATEGORY_ID_FIELD_NAME="category_id"; 

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(dataType = DataType.INTEGER, canBeNull = false, index = true, columnName = ARTICLE_ID_FIELD_NAME)
	private int article_id;

	@DatabaseField(dataType = DataType.INTEGER, canBeNull = false, index = true, columnName = CATEGORY_ID_FIELD_NAME)
	private int category_id;
	
	public ArtCatTable()
	{
		// TODO need empty constructor for ORMlite
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
		return category_id;
	}

	public void setCategory_id(int category_id)
	{
		this.category_id = category_id;
	}

}
