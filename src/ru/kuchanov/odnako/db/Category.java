/*
 09.12.2014
Category.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "category")
public class Category
{

	@DatabaseField(generatedId = true/*allowGeneratedIdInsert=true*/)
	private int id;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false)
	private String url;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false)
	private String title;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String description;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false)
	private String img_url;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false)
	private String img_file_name;

	@DatabaseField(dataType = DataType.DATE, canBeNull = false)
	private Date refreshed;

	@DatabaseField(dataType = DataType.DATE, canBeNull = false)
	private Date lastArticleDate;

	public Category()
	{
		// TODO Auto-generated constructor stub
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

	public void setId(int id)
	{
		this.id = id;
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

}
