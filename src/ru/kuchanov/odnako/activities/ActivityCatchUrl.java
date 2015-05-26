/*
 19.10.2014
ActivityMain.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.db.Category;
import ru.kuchanov.odnako.db.DataBaseHelper;
import ru.kuchanov.odnako.lists_and_utils.Actions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class ActivityCatchUrl extends ActivityBase//AppCompatActivity
{
	private static final String LOG = ActivityCatchUrl.class.getSimpleName();

	AppCompatActivity act;

	/**
	 * List of all authors from DB
	 */
	protected ArrayList<Author> allAuthorsList;

	/**
	 * List of all categories from DB
	 */
	protected ArrayList<Category> allCategoriesList;

	protected void onCreate(Bundle savedInstanceState)
	{
		this.act = this;

		//call super after setTheme to set it 0_0
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_downloads);

		Uri data = this.getIntent().getData();
		Log.d(LOG, "Uri data: " + data);

		String formatedAdress = data.toString().replace("www.", "");
		formatedAdress = Author.getURLwithoutSlashAtTheEnd(formatedAdress);

		DataBaseHelper h = new DataBaseHelper(this);
		Boolean isCategory = null;

		isCategory = Category.isCategory(h, formatedAdress);
		if (isCategory == null)
		{
			isCategory = Category.isCategory(h, Author.getURLwithoutSlashAtTheEnd(formatedAdress));
		}

		h.close();

		if (isCategory == null)
		{
			Log.d(LOG, "UNKNOWN category");
			Intent intent = new Intent(act, ActivityMain.class);

			intent.putExtra(ActivityMain.KEY_PAGER_TYPE, ActivityMain.PAGER_TYPE_SINGLE);
			intent.putExtra(ActivityBase.KEY_CURRENT_CATEGORY, formatedAdress);

			//set flags to prevent restoring activity from backStack and create really new instance
			//with given categories number
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			act.startActivity(intent);

			this.finish();
		}
		else
		{
			if (isCategory)
			{
				Log.d(LOG, "Category!!!");
				int posInDB = this.searchForCategoryInDB(formatedAdress);
				ArrayList<Category> cats = (ArrayList<Category>) this.getAllCategoriesList();
				Log.d(LOG, cats.get(posInDB).getTitle());

				Actions.showAllCategoriesArticles(formatedAdress, act);
				this.finish();
			}
			else
			{
				Log.d(LOG, "Author!!!");
				int posInDB = this.searchForAuthorInDB(formatedAdress);
				ArrayList<Author> auts = (ArrayList<Author>) this.getAllAuthorsList();
				Log.d(LOG, auts.get(posInDB).getName());

				Actions.showAllAuthorsArticles(formatedAdress, act);
				this.finish();
			}
		}

		//		if (data != null)
		//		{
		//			String scheme = data.getScheme(); // "http"
		//			String host = data.getHost(); // "twitter.com"
		//			List<String> params = data.getPathSegments();
		//			String first = null;
		//			String second = null;
		//			if (params.size() > 0)
		//			{
		//				first = params.get(0); // "blogs"
		//				//try to avoid crashing on blogs page loading from browser
		//				if (params.size() > 1)
		//				{
		//					System.out.println("More than 1 parametr in intent URI");
		//					second = params.get(1); // "title"
		//				}
		//			}
		//		}
	}

	/**
	 * 
	 * @return all authors from allAuthorsList variable or from DB if it's null
	 */
	public List<Author> getAllAuthorsList()
	{
		if (this.allAuthorsList == null)
		{
			DataBaseHelper h = new DataBaseHelper(this);
			try
			{
				this.allAuthorsList = (ArrayList<Author>) h.getDaoAuthor().queryBuilder()
				.orderBy(Author.NAME_FIELD_NAME, true).query();
			} catch (SQLException e)
			{
				e.printStackTrace();
			} catch (Exception e)
			{
				e.printStackTrace();
			} finally
			{
				h.close();
			}
		}
		return allAuthorsList;
	}

	public List<Category> getAllCategoriesList()
	{
		if (this.allCategoriesList == null)
		{
			DataBaseHelper h = new DataBaseHelper(this);
			try
			{
				this.allCategoriesList = (ArrayList<Category>) h.getDaoCategory().queryBuilder()
				.orderBy(Category.TITLE_FIELD_NAME, true)
				.query();
			} catch (SQLException e)
			{
				e.printStackTrace();
			} catch (Exception e)
			{
				e.printStackTrace();
			} finally
			{
				h.close();
			}
		}
		return allCategoriesList;
	}

	private int searchForCategoryInDB(String categoryUrl)
	{
		String categoryUrlWithoutSlash = Author.getURLwithoutSlashAtTheEnd(categoryUrl);

		ArrayList<Category> cats = (ArrayList<Category>) this.getAllCategoriesList();

		boolean weFindIt = false;
		int position = 0;
		for (int i = 0; i < cats.size(); i++)
		{

			String curCatUrl = cats.get(i).getUrl();
			if (categoryUrl.equals(curCatUrl) || categoryUrlWithoutSlash.equals(curCatUrl))
			{
				weFindIt = true;
				position = i;
				break;
			}
		}
		if (weFindIt)
		{
			return position;
		}
		else
		{
			return -1;
		}
	}

	/**
	 * returns position of given author in AllAuthorsPager's Authors list or -1
	 * if can't find it
	 * 
	 * @param pagerAllAut
	 * @param authorBlogUrl
	 * @return
	 */
	public int searchForAuthorInDB(String authorBlogUrl)
	{
		String authorBlogUrlWithoutSlash = Author.getURLwithoutSlashAtTheEnd(authorBlogUrl);
		//		authorBlogUrl = Author.getURLwithoutSlashAtTheEnd(authorBlogUrl);

		ArrayList<Author> auts = (ArrayList<Author>) this.getAllAuthorsList();

		boolean weFindIt = false;
		int position = 0;
		for (int i = 0; i < auts.size(); i++)
		{
			String curAutUrl = Author.getURLwithoutSlashAtTheEnd(auts
			.get(i)
			.getBlog_url());
			if (authorBlogUrl.equals(curAutUrl) || authorBlogUrlWithoutSlash.equals(curAutUrl))
			{
				weFindIt = true;
				position = i;
				break;
			}
		}
		if (weFindIt)
		{
			return position;
		}
		else
		{
			return -1;
		}
	}

	@Override
	protected void onResume()
	{
		Log.e(LOG, "onResume");
		super.onResume();
	}

	@Override
	public void onPause()
	{
		Log.e(LOG, "onPause");
		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		System.out.println("ActivityDownloads: onSaveInstanceState");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		System.out.println("ActivityDownloads onRestoreInstanceState");

	}
}