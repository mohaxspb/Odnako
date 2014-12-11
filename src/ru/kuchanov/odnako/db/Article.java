/*
 09.12.2014
Article.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "article")
public class Article
{
	public final static String AUTHOR_FIELD_NAME="author"; 

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false)
	private String url;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false)
	private String title;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String img_art;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String authorBlogUrl;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String authorName;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String preview;

	@DatabaseField(dataType = DataType.DATE, canBeNull = false)
	private Date pubDate;

	@DatabaseField(dataType = DataType.DATE, canBeNull = false)
	private Date refreshed;

	@DatabaseField(dataType = DataType.INTEGER, canBeNull = false)
	private int numOfComments=0;

	@DatabaseField(dataType = DataType.INTEGER, canBeNull = false)
	private int numOfSharings = 0;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false)
	private String artText;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String authorDescr;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String tegs_main;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String tegs_all;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String share_quont;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String to_read_main;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String to_read_more;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String img_author;

	//foreignKeys
	@DatabaseField(foreign = true, columnName = AUTHOR_FIELD_NAME, foreignAutoRefresh=true, canBeNull=true)
	private Author author;

	public Article()
	{

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

	public String getImg_art()
	{
		return img_art;
	}

	public void setImg_art(String img_art)
	{
		this.img_art = img_art;
	}

	public String getAuthorBlogUrl()
	{
		return authorBlogUrl;
	}

	public void setAuthorBlogUrl(String authorBlogUrl)
	{
		this.authorBlogUrl = authorBlogUrl;
	}

	public String getAuthorName()
	{
		return authorName;
	}

	public void setAuthorName(String authorName)
	{
		this.authorName = authorName;
	}

	public String getPreview()
	{
		return preview;
	}

	public void setPreview(String preview)
	{
		this.preview = preview;
	}

	public Date getPubDate()
	{
		return pubDate;
	}

	public void setPubDate(Date pubDate)
	{
		this.pubDate = pubDate;
	}

	public Date getRefreshed()
	{
		return refreshed;
	}

	public void setRefreshed(Date refreshed)
	{
		this.refreshed = refreshed;
	}

	public int getNumOfComments()
	{
		return numOfComments;
	}

	public void setNumOfComments(int numOfComments)
	{
		this.numOfComments = numOfComments;
	}

	public int getNumOfSharings()
	{
		return numOfSharings;
	}

	public void setNumOfSharings(int numOfSharings)
	{
		this.numOfSharings = numOfSharings;
	}

	public String getArtText()
	{
		return artText;
	}

	public void setArtText(String artText)
	{
		this.artText = artText;
	}

	public String getAuthorDescr()
	{
		return authorDescr;
	}

	public void setAuthorDescr(String authorDescr)
	{
		this.authorDescr = authorDescr;
	}

	public String getTegs_main()
	{
		return tegs_main;
	}

	public void setTegs_main(String tegs_main)
	{
		this.tegs_main = tegs_main;
	}

	public String getTegs_all()
	{
		return tegs_all;
	}

	public void setTegs_all(String tegs_all)
	{
		this.tegs_all = tegs_all;
	}

	public String getShare_quont()
	{
		return share_quont;
	}

	public void setShare_quont(String share_quont)
	{
		this.share_quont = share_quont;
	}

	public String getTo_read_main()
	{
		return to_read_main;
	}

	public void setTo_read_main(String to_read_main)
	{
		this.to_read_main = to_read_main;
	}

	public String getTo_read_more()
	{
		return to_read_more;
	}

	public void setTo_read_more(String to_read_more)
	{
		this.to_read_more = to_read_more;
	}

	public String getImg_author()
	{
		return img_author;
	}

	public void setImg_author(String img_author)
	{
		this.img_author = img_author;
	}

	public Author getAuthor()
	{
		return author;
	}

	public void setAuthor(Author author)
	{
		this.author = author;
	}

}
