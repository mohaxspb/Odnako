/*
 09.11.2014
Actions.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityArticle;
import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.db.Category;
import ru.kuchanov.odnako.db.ServiceArticle;
import ru.kuchanov.odnako.fragments.FragmentArticle;
import ru.kuchanov.odnako.fragments.FragmentComments;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class Actions
{
	private static String LOG = Actions.class.getSimpleName() + "/";

	/**
	 * @param authorBlogUrl
	 *            to show list or position in list and pager
	 * @param act
	 *            for switching action depend on activity
	 */
	public static void showAllAuthorsArticles(String authorBlogUrlFUCK, final AppCompatActivity act)
	{
		final String authorBlogUrl = Author.getURLwithoutSlashAtTheEnd(authorBlogUrlFUCK);
		if (!authorBlogUrl.equals("empty") && !authorBlogUrl.equals(""))
		{
			Log.d(LOG, "show all AuthorsArticles!");
			//check if it's large screen
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
			boolean twoPane = pref.getBoolean("twoPane", false);
			if (twoPane)
			{
				if (act instanceof ActivityMain)
				{
					final ActivityMain mainActivity = (ActivityMain) act;
					//check if we show allAuthors frag
					if (mainActivity.getCurentCategoryPosition() == 3
					&& mainActivity.getPagerType() == ActivityMain.PAGER_TYPE_MENU)
					{
						//switch to frag in right pager
						ViewPager rightPager = (ViewPager) mainActivity.findViewById(R.id.pager_right);
						PagerAdapterAllAuthors adapter = (PagerAdapterAllAuthors) rightPager.getAdapter();
						int position = 0;
						for (int i = 0; i < adapter.getAllAuthorsList().size(); i++)
						{
							if (authorBlogUrl.equals(adapter.getAllAuthorsList().get(i).getBlog_url()))
							{
								position = i;
								break;
							}
						}
						rightPager.setCurrentItem(position, true);
					}
					else
					{
						//here we check if we are at AllCategories fragment or pager and if is
						//we must close searchView if it's expanded to prevent submiting categories queue
						//to authors Pager
						if (mainActivity.getPagerType() == ActivityMain.PAGER_TYPE_MENU
						&& mainActivity.getCurentCategoryPosition() == 13)
						{
							//we are at allCategories frag so collapse searchView if it's expanded
							Menu menu = ((Toolbar) mainActivity.findViewById(R.id.toolbar)).getMenu();
							MenuItem search = menu.findItem(R.id.action_search);
							if (search != null)
							{
								search.collapseActionView();
							}
						}
						else if (mainActivity.getPagerType() == ActivityMain.PAGER_TYPE_CATEGORIES)
						{
							//we are at allCategories frag so collapse searchView if it's expanded
							Menu menu = ((Toolbar) mainActivity.findViewById(R.id.toolbar)).getMenu();
							MenuItem search = menu.findItem(R.id.action_search);
							if (search != null)
							{
								search.collapseActionView();
							}
						}
						//else we must show authors list.
						//we can switch here if we have him in autList or not
						//if so we can show arts list in allAuthor frag or not
						ViewPager leftPager = (ViewPager) act.findViewById(R.id.pager_left);
						PagerAdapterAllAuthors pagerAllAut = new PagerAdapterAllAuthors(
						act.getSupportFragmentManager(), mainActivity);
						int position = searchForAuthorInDB(pagerAllAut, authorBlogUrl);
						if (position != -1)
						{
							mainActivity.setPagerType(ActivityMain.PAGER_TYPE_AUTHORS);
							mainActivity.setCurentCategoryPosition(position);

							leftPager.setAdapter(pagerAllAut);
							OnPageChangeListener listener = new PagerListenerAllAuthors(mainActivity, pagerAllAut
							.getAllAuthorsList());
							leftPager.setOnPageChangeListener(listener);
							leftPager.setCurrentItem(position);
							if (position == 0)
							{
								listener.onPageSelected(0);
							}
						}//we find author, so show it in AllAuthors pager
						else
						{
							//we can't find this author in DB, so show it in singleCategoryPager
							mainActivity.setPagerType(ActivityMain.PAGER_TYPE_SINGLE);
							mainActivity.setCurentCategoryPosition(0);
							mainActivity.setCurrentCategory(authorBlogUrl);

							leftPager.setAdapter(new PagerAdapterSingleCategory(act.getSupportFragmentManager(), act,
							authorBlogUrl));
							OnPageChangeListener listener = new PagerListenerSingleCategory(mainActivity);
							leftPager.setOnPageChangeListener(listener);
							leftPager.setCurrentItem(0);

							//try notify pager that item selected if it's 0 item
							if (leftPager.getCurrentItem() == 0)
							{
								listener.onPageSelected(0);
							}
						}//can't find author in DB, so show SinglePager
					}//if NOT all authors or categories
				}//on activity main
				else
				{
					//TODO open ActivityMain via intent, that contains pager type and authors url
					Log.e(LOG, "TwoPane && NOT ActivityMain");
				}
			}
			else
			{
				//if not twoPane
				//check if it ActivityMain and show AllAuthors pager or start activity with it
				if (act instanceof ActivityMain)
				{
					ActivityMain mainActivity = (ActivityMain) act;
					//we must show authors list.
					//we can switch here if we have him in autList or not
					//if so we can show arts list in allAuthor frag or not
					ViewPager leftPager = (ViewPager) act.findViewById(R.id.pager_left);
					PagerAdapterAllAuthors pagerAllAut = new PagerAdapterAllAuthors(
					act.getSupportFragmentManager(), mainActivity);
					int position = searchForAuthorInDB(pagerAllAut, authorBlogUrl);
					if (position != -1)
					{
						mainActivity.setPagerType(ActivityMain.PAGER_TYPE_AUTHORS);
						mainActivity.setCurentCategoryPosition(position);

						leftPager.setAdapter(pagerAllAut);
						OnPageChangeListener listener = new PagerListenerAllAuthors(mainActivity, pagerAllAut
						.getAllAuthorsList());
						leftPager.setOnPageChangeListener(listener);
						leftPager.setCurrentItem(position);
						if (position == 0)
						{
							listener.onPageSelected(0);
						}
					}//we find author, so show it in AllAuthors pager
					else
					{
						//we can't find this author in DB, so show it in singleCategoryPager
						mainActivity.setPagerType(ActivityMain.PAGER_TYPE_SINGLE);
						mainActivity.setCurentCategoryPosition(0);
						mainActivity.setCurrentCategory(authorBlogUrl);

						leftPager.setAdapter(new PagerAdapterSingleCategory(act.getSupportFragmentManager(), act,
						authorBlogUrl));
						OnPageChangeListener listener = new PagerListenerSingleCategory(mainActivity);
						leftPager.setOnPageChangeListener(listener);
						leftPager.setCurrentItem(0);

						//try notify pager that item selected if it's 0 item
						if (leftPager.getCurrentItem() == 0)
						{
							listener.onPageSelected(0);
						}
					}//can't find author in DB, so show SinglePager
				}//if ActivityMain
				else
				{
					Intent intent = new Intent(act, ActivityMain.class);

					PagerAdapterAllAuthors pagerAllAut = new PagerAdapterAllAuthors(
					act.getSupportFragmentManager(), null);
					ActivityBase activityBase = (ActivityBase) act;
					pagerAllAut.updateData((ArrayList<Author>) activityBase.getAllAuthorsList());
					int position = searchForAuthorInDB(pagerAllAut, authorBlogUrl);
					if (position != -1)
					{
						intent.putExtra(ActivityMain.KEY_PAGER_TYPE, ActivityMain.PAGER_TYPE_AUTHORS);
						intent.putExtra(ActivityBase.KEY_CURRENT_CATEGORY_POSITION, position);
					}
					else
					{
						intent.putExtra(ActivityMain.KEY_PAGER_TYPE, ActivityMain.PAGER_TYPE_SINGLE);
						intent.putExtra(ActivityBase.KEY_CURRENT_CATEGORY, authorBlogUrl);
					}

					//set flags to prevent restoring activity from backStack and create really new instance
					//with given categories number
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					act.startActivity(intent);
				}
			}
		}
		else
		{
			System.out.println("p.authorBlogUrl.equals('empty' || ''): WTF?!");
		}
	}//showAllAuthorsArticles

	/**
	 * returns position of given author in AllAuthorsPager's Authors list or -1
	 * if can't find it
	 * 
	 * @param pagerAllAut
	 * @param authorBlogUrl
	 * @return
	 */
	public static int searchForAuthorInDB(PagerAdapterAllAuthors pagerAllAut, String authorBlogUrl)
	{
		boolean weFindIt = false;
		int position = 0;
		for (int i = 0; i < pagerAllAut.getAllAuthorsList().size(); i++)
		{
			String curAutUrl = Author.getURLwithoutSlashAtTheEnd(pagerAllAut.getAllAuthorsList()
			.get(i)
			.getBlog_url());
			if (authorBlogUrl.equals(curAutUrl))
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

	public static void showAllCategoriesArticles(String categoryUrlFUCK, final AppCompatActivity act)
	{
		Log.d(LOG + categoryUrlFUCK, "show all categories articles!");
		final String categoryUrl = Author.getURLwithoutSlashAtTheEnd(categoryUrlFUCK);
		if (!categoryUrl.equals("empty") && !categoryUrl.equals(""))
		{
			//check if it's large screen
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
			boolean twoPane = pref.getBoolean("twoPane", false);
			if (twoPane)
			{
				if (act instanceof ActivityMain)
				{
					final ActivityMain mainActivity = (ActivityMain) act;
					//check if we show allAuthors frag
					if (mainActivity.getCurentCategoryPosition() == 13)
					{
						//switch to frag in right pager
						ViewPager rightPager = (ViewPager) mainActivity.findViewById(R.id.pager_right);
						PagerAdapterAllCategories adapter = (PagerAdapterAllCategories) rightPager.getAdapter();
						int position = 0;
						for (int i = 0; i < adapter.getAllCategoriesList().size(); i++)
						{
							String curCatUrl = Author.getURLwithoutSlashAtTheEnd(adapter.getAllCategoriesList().get(i)
							.getUrl());
							if (categoryUrl.equals(curCatUrl))
							{
								position = i;
								break;
							}
						}
						rightPager.setCurrentItem(position, true);
					}
					else
					{
						//else we must show categories list.
						//we can switch here if we have him in autList or not
						//if so we can show arts list in allAuthor frag or not
						ViewPager leftPager = (ViewPager) act.findViewById(R.id.pager_left);
						PagerAdapterAllCategories pagerAllCat = new PagerAdapterAllCategories(
						act.getSupportFragmentManager(), mainActivity);
						int position = searchForCategoryInDB(pagerAllCat, categoryUrl);
						if (position != -1)
						{
							mainActivity.setPagerType(ActivityMain.PAGER_TYPE_CATEGORIES);
							mainActivity.setCurentCategoryPosition(position);

							leftPager.setAdapter(pagerAllCat);
							OnPageChangeListener listener = new PagerListenerAllCategories(mainActivity, pagerAllCat
							.getAllCategoriesList());
							leftPager.setOnPageChangeListener(listener);
							leftPager.setCurrentItem(position);
							if (position == 0)
							{
								listener.onPageSelected(0);
							}
						}
						else
						{
							//we can't find this author in DB, so show it in singleCategoryPager
							mainActivity.setPagerType(ActivityMain.PAGER_TYPE_SINGLE);
							mainActivity.setCurentCategoryPosition(0);
							mainActivity.setCurrentCategory(categoryUrl);

							leftPager.setAdapter(new PagerAdapterSingleCategory(act.getSupportFragmentManager(), act,
							categoryUrl));
							OnPageChangeListener listener = new PagerListenerSingleCategory(mainActivity);
							leftPager.setOnPageChangeListener(listener);
							leftPager.setCurrentItem(0);

							//try notify pager that item selected if it's 0 item
							if (leftPager.getCurrentItem() == 0)
							{
								listener.onPageSelected(0);
							}
						}//we don't find, so open in singlePager
					}//if NOT all authors or categories
				}//on activity main
				else
				{
					//TODO open ActivityMain via intent, that contains pager type and authors url
					Log.e(LOG, "twoPane && NOT ActivityMain");
				}
			}
			else
			{
				//if not twoPane
				//check if it's ActivityMain and show AllAuthors pager or start activity with it
				if (act instanceof ActivityMain)
				{
					ActivityMain mainActivity = (ActivityMain) act;
					//we must show categories list.
					//we can switch here if we have him in autList or not
					//if so we can show arts list in allAuthor frag or not
					ViewPager leftPager = (ViewPager) act.findViewById(R.id.pager_left);
					PagerAdapterAllCategories pagerAllCat = new PagerAdapterAllCategories(
					act.getSupportFragmentManager(), mainActivity);
					int position = searchForCategoryInDB(pagerAllCat, categoryUrl);
					if (position != -1)
					{
						mainActivity.setPagerType(ActivityMain.PAGER_TYPE_CATEGORIES);
						mainActivity.setCurentCategoryPosition(position);

						leftPager.setAdapter(pagerAllCat);
						OnPageChangeListener listener = new PagerListenerAllCategories(mainActivity, pagerAllCat
						.getAllCategoriesList());
						leftPager.setOnPageChangeListener(listener);
						leftPager.setCurrentItem(position);
						if (position == 0)
						{
							listener.onPageSelected(0);
						}
					}
					else
					{
						//we can't find this author in DB, so show it in singleCategoryPager
						mainActivity.setPagerType(ActivityMain.PAGER_TYPE_SINGLE);
						mainActivity.setCurentCategoryPosition(0);
						mainActivity.setCurrentCategory(categoryUrl);

						leftPager.setAdapter(new PagerAdapterSingleCategory(act.getSupportFragmentManager(), act,
						categoryUrl));
						OnPageChangeListener listener = new PagerListenerSingleCategory(mainActivity);
						leftPager.setOnPageChangeListener(listener);
						leftPager.setCurrentItem(0);

						//try notify pager that item selected if it's 0 item
						if (leftPager.getCurrentItem() == 0)
						{
							listener.onPageSelected(0);
						}
					}
				}//if (act instanceof ActivityMain)
				else
				{
					Intent intent = new Intent(act, ActivityMain.class);

					PagerAdapterAllCategories pagerAllCat = new PagerAdapterAllCategories(
					act.getSupportFragmentManager(), null);
					ActivityBase activityBase = (ActivityBase) act;
					pagerAllCat.updateData((ArrayList<Category>) activityBase.getAllCategoriesList());
					int position = searchForCategoryInDB(pagerAllCat, categoryUrl);
					if (position != -1)
					{
						intent.putExtra(ActivityMain.KEY_PAGER_TYPE, ActivityMain.PAGER_TYPE_CATEGORIES);
						intent.putExtra(ActivityBase.KEY_CURRENT_CATEGORY_POSITION, position);
					}
					else
					{
						intent.putExtra(ActivityMain.KEY_PAGER_TYPE, ActivityMain.PAGER_TYPE_SINGLE);
						intent.putExtra(ActivityBase.KEY_CURRENT_CATEGORY, categoryUrl);
					}

					//set flags to prevent restoring activity from backStack and create really new instance
					//with given categories number
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					act.startActivity(intent);
				}
			}
		}
		else
		{
			System.out.println("p.authorBlogUrl.equals('empty' || ''): WTF?!");
		}
	}//showAllCategoriesArticles

	private static int searchForCategoryInDB(PagerAdapterAllCategories pagerAllCat, String categoryUrl)
	{
		boolean weFindIt = false;
		int position = 0;
		for (int i = 0; i < pagerAllCat.getAllCategoriesList().size(); i++)
		{
			String curCatUrl = Author.getURLwithoutSlashAtTheEnd(pagerAllCat.getAllCategoriesList()
			.get(i)
			.getUrl());
			if (categoryUrl.equals(curCatUrl))
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

	public static void shareUrl(String url, Context ctx)
	{
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, url);
		sendIntent.setType("text/plain");
		ctx.startActivity(Intent.createChooser(sendIntent, ctx.getResources().getText(R.string.share_link)));
	}

	public static void showComments(ArrayList<Article> allArtsInfo, int positionOfArticle, String categoryToLoad,
	AppCompatActivity act)
	{
		Log.d(LOG, "showComments!");
		//check if it's large screen
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
		boolean twoPane = pref.getBoolean("twoPane", false);

		if (twoPane)
		{
			final ActivityMain mainActivity = (ActivityMain) act;

			final ViewPager leftPager = (ViewPager) act.findViewById(R.id.pager_left);
			final ViewPager rightPager = (ViewPager) act.findViewById(R.id.pager_right);
			//check if we are showing allAuthors (curCatPosition=3) or allCategories (curCatPosition=13) 
			if (rightPager.getAdapter() instanceof PagerAdapterAllAuthors)
			{
				//if so we must change adapters to all ViewPagers
				PagerAdapterAllAuthors pagerAllAut = new PagerAdapterAllAuthors(
				act.getSupportFragmentManager(), mainActivity);
				AllAuthorsListAdapter adAllAut = new AllAuthorsListAdapter(mainActivity, null);
				//we also must setFilter to adapter by query. saved in Activity
				String curFilter = mainActivity.getSearchText();
				if (curFilter != null)
				{
					adAllAut.setFilter(mainActivity.getSearchText());
				}
				else
				{
					adAllAut.flushFilter();
				}

				pagerAllAut.updateData(adAllAut.getCurAllAuthorsList());
				String authorBlogUrl = Author
				.getURLwithoutSlashAtTheEnd(allArtsInfo.get(positionOfArticle).getAuthorBlogUrl());
				int positionInLeftPager = searchForAuthorInDB(pagerAllAut, authorBlogUrl);
				if (positionInLeftPager != -1)
				{
					mainActivity.setPagerType(ActivityMain.PAGER_TYPE_AUTHORS);
					mainActivity.setCurentCategoryPosition(positionInLeftPager);
					mainActivity.getAllCatListsSelectedArtPosition().put(authorBlogUrl, positionOfArticle);
					//and also set selectedArtPosition for allAuthors fragment;
					mainActivity.getAllCatListsSelectedArtPosition().put(
					CatData.getMenuLinks(mainActivity)[3], positionInLeftPager);
					leftPager.setAdapter(pagerAllAut);
					OnPageChangeListener listener = new PagerListenerAllAuthors(mainActivity,
					pagerAllAut.getAllAuthorsList());
					leftPager.setOnPageChangeListener(listener);
					leftPager.setCurrentItem(positionInLeftPager);
					//try notify pager that item selected if it's 0 item
					if (positionInLeftPager == 0)
					{
						listener.onPageSelected(0);
					}
				}
				else
				{
					//we can't find this author in DB, so show it in singleCategoryPager
					//that's really strange, because we gave author from DB
					mainActivity.setPagerType(ActivityMain.PAGER_TYPE_SINGLE);
					mainActivity.setCurentCategoryPosition(0);
					mainActivity.setCurrentCategory(authorBlogUrl);

					leftPager.setAdapter(new PagerAdapterSingleCategory(act.getSupportFragmentManager(), act,
					authorBlogUrl));
					OnPageChangeListener listener = new PagerListenerSingleCategory(mainActivity);
					leftPager.setOnPageChangeListener(listener);
					leftPager.setCurrentItem(0);

					//try notify pager that item selected if it's 0 item
					if (leftPager.getCurrentItem() == 0)
					{
						listener.onPageSelected(0);
					}
				}//cant't find
			}//if rightPager.adapter() is instanceof AllAuthors
			else if (rightPager.getAdapter() instanceof PagerAdapterAllCategories)
			{
				//if so we must change adapters to all ViewPagers
				PagerAdapterAllCategories pagerAllCat = new PagerAdapterAllCategories(
				act.getSupportFragmentManager(), mainActivity);
				AllCategoriesListAdapter adAllCat = new AllCategoriesListAdapter(mainActivity, null);
				//we also must setFilter to adapter by query. saved in Activity
				String curFilter = mainActivity.getSearchText();
				if (curFilter != null)
				{
					adAllCat.setFilter(mainActivity.getSearchText());
				}
				else
				{
					adAllCat.flushFilter();
				}

				pagerAllCat.updateData(adAllCat.getCurAllCategoriesList());

				int positionInLeftPager = mainActivity.getAllCatListsSelectedArtPosition().get(
				CatData.getMenuLinks(mainActivity)[13]);
				String categoryUrl = pagerAllCat.getAllCategoriesURLsList().get(positionInLeftPager);
				mainActivity.setPagerType(ActivityMain.PAGER_TYPE_CATEGORIES);
				mainActivity.setCurentCategoryPosition(positionInLeftPager);
				mainActivity.getAllCatListsSelectedArtPosition().put(categoryUrl, positionOfArticle);
				//and also set selectedArtPosition for allCategories fragment;
				leftPager.setAdapter(pagerAllCat);
				OnPageChangeListener listener = new PagerListenerAllCategories(mainActivity,
				pagerAllCat.getAllCategoriesList());
				leftPager.setOnPageChangeListener(listener);
				leftPager.setCurrentItem(positionInLeftPager);
				//try notify pager that item selected if it's 0 item
				if (positionInLeftPager == 0)
				{
					listener.onPageSelected(0);
				}
			}
			else
			{
				//it's Articles pager Adapter, so just set current fragment
				rightPager.setCurrentItem(positionOfArticle, true);
			}
			Actions.addCommentsFrgament(allArtsInfo.get(positionOfArticle), act);
		}//if(twoPane)
		else
		{
			if (act instanceof ActivityMain)
			{
				Intent intent = new Intent(act, ActivityArticle.class);
				intent.putExtra("position", positionOfArticle);
				intent.putExtra("categoryToLoad", categoryToLoad);
				intent.putExtra(Article.KEY_ALL_ART_INFO, allArtsInfo);
				intent.putExtra("groupChildPosition", ((ActivityBase) act).getGroupChildPosition());
				intent.putExtra(FragmentComments.LOG, true);
				act.startActivity(intent);
			}
			else
			{
				Actions.addCommentsFrgament(allArtsInfo.get(positionOfArticle), act);
			}
		}
	}

	public static void addCommentsFrgament(Article article, final AppCompatActivity act)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
		boolean twoPane = pref.getBoolean("twoPane", false);

		FragmentComments newFragment = new FragmentComments();
		Bundle b = new Bundle();
		b.putParcelable(Article.KEY_CURENT_ART, article);
		newFragment.setArguments(b);

		FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction();
		//		ft.add(R.id.container_right, newFragment, FragmentComments.LOG);
		ft.replace(R.id.container_right, newFragment, FragmentComments.LOG);
		ft.addToBackStack(null);
		ft.commit();

		if (!twoPane)
		{
			//So it's article activity
			//replace hamburger icon to backArrow
			((ActivityBase) act).mDrawerToggle.setDrawerIndicatorEnabled(false);
			act.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		else
		{
			//we are on main activity, so we must set toggle to rightToolbar
			final Toolbar toolbar;
			toolbar = (Toolbar) act.findViewById(R.id.toolbar_right);
			toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
			toolbar.setNavigationOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					act.onBackPressed();
				}
			});
		}
	}

	public static void showArticle(ArrayList<Article> allArtsInfo, int positionOfArticle, String categoryToLoad,
	final AppCompatActivity act)
	{
		Log.d(LOG, "showArticle!");
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
		boolean twoPane = pref.getBoolean("twoPane", false);
		//check if it's large screen
		final ActivityMain mainActivity = (ActivityMain) act;
		if (twoPane)
		{
			final ViewPager leftPager = (ViewPager) act.findViewById(R.id.pager_left);
			final ViewPager rightPager = (ViewPager) act.findViewById(R.id.pager_right);
			//check if we are showing allAuthors (curCatPosition=3) or allCategories (curCatPosition=13) 
			if (rightPager.getAdapter() instanceof PagerAdapterAllAuthors)
			{
				//if so we must change adapters to all ViewPagers
				PagerAdapterAllAuthors pagerAllAut = new PagerAdapterAllAuthors(
				act.getSupportFragmentManager(), mainActivity);
				AllAuthorsListAdapter adAllAut = new AllAuthorsListAdapter(mainActivity, null);
				//we also must setFilter to adapter by query. saved in Activity
				String curFilter = mainActivity.getSearchText();
				if (curFilter != null)
				{
					adAllAut.setFilter(mainActivity.getSearchText());
				}
				else
				{
					adAllAut.flushFilter();
				}

				pagerAllAut.updateData(adAllAut.getCurAllAuthorsList());
				String authorBlogUrl = Author
				.getURLwithoutSlashAtTheEnd(allArtsInfo.get(positionOfArticle).getAuthorBlogUrl());
				int positionInLeftPager = searchForAuthorInDB(pagerAllAut, authorBlogUrl);
				if (positionInLeftPager != -1)
				{
					mainActivity.setPagerType(ActivityMain.PAGER_TYPE_AUTHORS);
					mainActivity.setCurentCategoryPosition(positionInLeftPager);
					mainActivity.getAllCatListsSelectedArtPosition().put(authorBlogUrl, positionOfArticle);
					//and also set selectedArtPosition for allAuthors fragment;
					mainActivity.getAllCatListsSelectedArtPosition().put(
					CatData.getMenuLinks(mainActivity)[3], positionInLeftPager);
					leftPager.setAdapter(pagerAllAut);
					OnPageChangeListener listener = new PagerListenerAllAuthors(mainActivity,
					pagerAllAut.getAllAuthorsList());
					leftPager.setOnPageChangeListener(listener);
					leftPager.setCurrentItem(positionInLeftPager);
					//try notify pager that item selected if it's 0 item
					if (positionInLeftPager == 0)
					{
						listener.onPageSelected(0);
					}
				}
				else
				{
					//we can't find this author in DB, so show it in singleCategoryPager
					//that's really strange, because we gave author from DB
					mainActivity.setPagerType(ActivityMain.PAGER_TYPE_SINGLE);
					mainActivity.setCurentCategoryPosition(0);
					mainActivity.setCurrentCategory(authorBlogUrl);

					leftPager.setAdapter(new PagerAdapterSingleCategory(act.getSupportFragmentManager(), act,
					authorBlogUrl));
					OnPageChangeListener listener = new PagerListenerSingleCategory(mainActivity);
					leftPager.setOnPageChangeListener(listener);
					leftPager.setCurrentItem(0);

					//try notify pager that item selected if it's 0 item
					if (leftPager.getCurrentItem() == 0)
					{
						listener.onPageSelected(0);
					}
				}//cant't find
			}//if rightPager.adapter() isinstanceof AllAuthors
			else if (rightPager.getAdapter() instanceof PagerAdapterAllCategories)
			{
				//if so we must change adapters to all ViewPagers
				PagerAdapterAllCategories pagerAllCat = new PagerAdapterAllCategories(
				act.getSupportFragmentManager(), mainActivity);
				AllCategoriesListAdapter adAllCat = new AllCategoriesListAdapter(mainActivity, null);
				//we also must setFilter to adapter by query. saved in Activity
				String curFilter = mainActivity.getSearchText();
				if (curFilter != null)
				{
					adAllCat.setFilter(mainActivity.getSearchText());
				}
				else
				{
					adAllCat.flushFilter();
				}

				pagerAllCat.updateData(adAllCat.getCurAllCategoriesList());

				int positionInLeftPager = mainActivity.getAllCatListsSelectedArtPosition().get(
				CatData.getMenuLinks(mainActivity)[13]);
				String categoryUrl = pagerAllCat.getAllCategoriesURLsList().get(positionInLeftPager);
				mainActivity.setPagerType(ActivityMain.PAGER_TYPE_CATEGORIES);
				mainActivity.setCurentCategoryPosition(positionInLeftPager);
				mainActivity.getAllCatListsSelectedArtPosition().put(categoryUrl, positionOfArticle);
				//and also set selectedArtPosition for allCategories fragment;
				//					mainActivity.getAllCatListsSelectedArtPosition().put(CatData.getMenuLinks(mainActivity)[13], positionInLeftPager);
				leftPager.setAdapter(pagerAllCat);
				OnPageChangeListener listener = new PagerListenerAllCategories(mainActivity,
				pagerAllCat.getAllCategoriesList());
				leftPager.setOnPageChangeListener(listener);
				leftPager.setCurrentItem(positionInLeftPager);
				//try notify pager that item selected if it's 0 item
				if (positionInLeftPager == 0)
				{
					listener.onPageSelected(0);
				}
			}
			else
			{
				//it's Articles pager Adapter, so just select cur fragment
				rightPager.setCurrentItem(positionOfArticle, true);
			}
		}
		else
		{
			Intent intent = new Intent(act, ActivityArticle.class);
			Bundle b = new Bundle();
			b.putInt("position", positionOfArticle);
			b.putString("categoryToLoad", categoryToLoad);
			b.putParcelableArrayList(Article.KEY_ALL_ART_INFO, allArtsInfo);
			b.putIntArray("groupChildPosition", ((ActivityBase) act).getGroupChildPosition());
			intent.putExtras(b);

			act.startActivity(intent);
		}
	}

	public static void shareArtText(String textToShare, Context ctx)
	{
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
		sendIntent.setType("text/plain");
		ctx.startActivity(Intent.createChooser(sendIntent, ctx.getResources().getText(R.string.get_art_text)));
	}

	public static void startDownLoadArticle(String url, Context ctx, boolean startDownload)
	{
		Toast.makeText(ctx, "Начинаю загрузку", Toast.LENGTH_SHORT).show();

		Intent intent = new Intent(ctx, ServiceArticle.class);
		intent.setAction(Const.Action.DATA_REQUEST);
		intent.putExtra(FragmentArticle.ARTICLE_URL, url);
		intent.putExtra("startDownload", startDownload);
		ctx.startService(intent);
	}
}