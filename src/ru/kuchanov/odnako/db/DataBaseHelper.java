/*
 09.12.2014
DataBaseHelper.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.sql.SQLException;
import java.util.Date;

import ru.kuchanov.odnako.lists_and_utils.CatData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DataBaseHelper extends OrmLiteSqliteOpenHelper
{

	Context ctx;

	// name of the database file for your application
	public static final String DATABASE_NAME = "db_odnako.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 1;

	// the DAO object we use to access the Book table
	private Dao<Category, Integer> daoCategory = null;
	private Dao<Author, Integer> daoAuthor = null;
	private Dao<Article, Integer> daoArticle = null;
	private Dao<ArtCatTable, Integer> daoArtCatTable = null;
	private Dao<ArtAutTable, Integer> daoArtAutTable = null;

	public DataBaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		this.ctx = context;
	}

	/**
	 * @param context
	 * @param databaseName
	 * @param factory
	 * @param databaseVersion
	 */
	public DataBaseHelper(Context context, String databaseName, CursorFactory factory, int databaseVersion)
	{
		super(context, databaseName, null, databaseVersion);
		// TODO Auto-generated constructor stub
		this.ctx = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource)
	{
		try
		{
			Log.i(DataBaseHelper.class.getSimpleName(), "onCreate");
			//create category table
			TableUtils.createTable(connectionSource, Category.class);
			//create author table
			TableUtils.createTable(connectionSource, Author.class);
			//create article table
			TableUtils.createTable(connectionSource, Article.class);
			//create artCatTable table
			TableUtils.createTable(connectionSource, ArtCatTable.class);
			//create artAutTable table
			TableUtils.createTable(connectionSource, ArtAutTable.class);
			Log.i(DataBaseHelper.class.getSimpleName(), "all tables have been created");

			//fill with initial data
			fillTables();
		} catch (SQLException e)
		{
			Log.e(DataBaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	private void fillTables()
	{
		//fill initial category info
		//AllTags
		String[] urls = CatData.getAllTagsLinks(ctx);
		String[] titles = CatData.getAllTagsNames(ctx);
		String[] descriptions = CatData.getAllTagsDescriptions(ctx);
		String[] img_urls = CatData.getAllTagsImgsURLs(ctx);
		String[] img_files_names = CatData.getAllTagsImgsFILEnames(ctx);

		int length = urls.length;

		for (int i = 0; i < length; i++)
		{
			try
			{
				this.daoCategory = this.getDaoCategory();
				String[] stringData = { urls[i], titles[i], descriptions[i], img_urls[i], img_files_names[i] };
				Date[] dateData = { new Date(0), new Date(0) };
				Category category = new Category(stringData, dateData);
				this.daoCategory.create(category);
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		//menuCategories
		String[] urlsM = CatData.getAllCategoriesMenuLinks(ctx);
		String[] titlesM = CatData.getAllCategoriesMenuNames(ctx);
		String[] descriptionsM = CatData.getAllCategoriesMenuDescriptions(ctx);
		String[] img_urlsM = CatData.getAllCategoriesMenuImgsUrls(ctx);
		String[] img_files_namesM = CatData.getAllCategoriesMenuImgsFilesNames(ctx);

		int lengthM = urlsM.length;
		for (int i = 0; i < lengthM; i++)
		{
			try
			{
				this.daoCategory = this.getDaoCategory();
				String[] stringData = { urlsM[i], titlesM[i], descriptionsM[i], img_urlsM[i], img_files_namesM[i] };
				Date[] dateData = { new Date(0), new Date(0) };
				Category category = new Category(stringData, dateData);
				this.daoCategory.create(category);
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		////
		//Authors table
		String[] urlsA = CatData.getAllAuthorsBlogsURLs(ctx);
		String[] namesA = CatData.getAllAuthorsNames(ctx);
		String[] descriptionsA = CatData.getAllAuthorsDescriptions(ctx);
		String[] whoA = CatData.getAllAuthorsWhos(ctx);
		String[] img_urlsA = CatData.getAllAuthorsAvatars(ctx);
		String[] img_urlsABIG = CatData.getAllAuthorsAvatarsBig(ctx);

		int lengthA = urlsA.length;
		for (int i = 0; i < lengthA; i++)
		{
			try
			{
				this.daoAuthor = this.getDaoAuthor();
				String[] stringData = { urlsA[i], namesA[i], descriptionsA[i], whoA[i], img_urlsA[i], img_urlsABIG[i] };
				Date[] dateData = { new Date(0), new Date(0) };
				Author author = new Author(stringData, dateData);
				this.daoAuthor.create(author);
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion)
	{
		// TODO need to write full code here
		try
		{
			Log.i(DataBaseHelper.class.getName(), "onUpgrade");
			//delete all tables
			TableUtils.dropTable(connectionSource, Category.class, true);
			TableUtils.dropTable(connectionSource, Author.class, true);
			TableUtils.dropTable(connectionSource, Article.class, true);
			TableUtils.dropTable(connectionSource, ArtCatTable.class, true);
			TableUtils.dropTable(connectionSource, ArtAutTable.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e)
		{
			Log.e(DataBaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the Database Access Object (DAO) for our Category class. It will
	 * create it or just give the cached value.
	 */
	public Dao<Category, Integer> getDaoCategory() throws SQLException
	{
		if (this.daoCategory == null)
		{
			this.daoCategory = DaoManager.createDao(this.getConnectionSource(), Category.class);
		}
		return this.daoCategory;
	}

	public Dao<Author, Integer> getDaoAuthor() throws SQLException
	{
		if (this.daoAuthor == null)
		{
			this.daoAuthor = DaoManager.createDao(this.getConnectionSource(), Author.class);
		}
		return this.daoAuthor;
	}

	public Dao<Article, Integer> getDaoArticle() throws SQLException
	{
		//		return DaoManager.createDao(connectionSource, Article.class);
		if (this.daoArticle == null)
		{
			//			this.daoAuthor=DaoManager.createDao(connectionSource, Author.class);
			//			this.daoArticle = this.getDao(Article.class);
			this.daoArticle = DaoManager.createDao(this.getConnectionSource(), Article.class);
		}
		return this.daoArticle;
	}

	public Dao<ArtCatTable, Integer> getDaoArtCatTable() throws SQLException
	{
		if (this.daoArtCatTable == null)
		{
			this.daoArtCatTable = DaoManager.createDao(this.getConnectionSource(), ArtCatTable.class);
		}
		return this.daoArtCatTable;
	}

	public Dao<ArtAutTable, Integer> getDaoArtAutTable() throws SQLException
	{
		if (this.daoArtAutTable == null)
		{
			this.daoArtAutTable = DaoManager.createDao(this.getConnectionSource(), ArtAutTable.class);
		}
		return this.daoArtAutTable;
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close()
	{
		super.close();
		this.daoArtCatTable = null;
		this.daoArtAutTable = null;
		this.daoArticle = null;
		this.daoAuthor = null;
		this.daoCategory = null;
	}

	public void clearArticleTable()
	{
		try
		{
			this.getDaoArticle().deleteBuilder().delete();
			Log.d(DATABASE_NAME, "Article table's objects are already DELETED");
			Log.d(DATABASE_NAME, "this.getDaoArticle().queryForAll().size(): "
			+ this.getDaoArticle().queryForAll().size());
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
