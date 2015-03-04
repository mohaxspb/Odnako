/*
 09.11.2014
Actions.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityArticle;
import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.activities.ActivityComments;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.fragments.FragmentArtsListRecycler;
import ru.kuchanov.odnako.lists_and_utils.PagerListenerAllAuthors;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

public class Actions
{
	private static String LOG = Actions.class.getSimpleName() + "/";

	/**
	 * We can have such situations:
	 * <ul>
	 * <li>on ActivityMain
	 * <ul>
	 * <li>twoPane</li>
	 * </ul>
	 * </ul>
	 * 
	 * @param authorBlogUrl
	 *            to show list or position in list and pager
	 * @param act
	 *            for switching action depend on activity
	 */
	public static void showAllAuthorsArticles(String authorBlogUrlFUCK, final ActionBarActivity act)
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
				if (act.getClass().getSimpleName().equals("ActivityMain"))
				{
					final ActivityMain mainActivity = (ActivityMain) act;
					//check if we show allAuthors frag
					if (mainActivity.getCurentCategoryPosition() == 3)
					{
						//switch to frag in right pager
						ViewPager rightPager = (ViewPager) mainActivity.findViewById(R.id.pager_right);
						PagerAdapterAllAuthors adapter = (PagerAdapterAllAuthors) rightPager.getAdapter();
						int position = 0;
						for (int i = 0; i < adapter.getAllAuthorsList().size(); i++)
						{
							if (authorBlogUrl.equals(adapter.getAllAuthorsList().get(i).getBlog_url()))//.blogLink))
							{
								position = i;
								break;
							}
						}
						rightPager.setCurrentItem(position, true);
					}
					else if (mainActivity.getCurentCategoryPosition() == 13)
					{
						//TODO
					}
					else
					{
						//else we must show authors list.
						//we can switch here if we have him in autList or not
						//if so we can show arts list in allAuthor frag or not
						ViewPager leftPager = (ViewPager) act.findViewById(R.id.pager_left);
						PagerAdapterAllAuthors pagerAllAut = new PagerAdapterAllAuthors(
						act.getSupportFragmentManager(), mainActivity);
						boolean weFindIt = false;
						int position = 0;
						for (int i = 0; i < pagerAllAut.getAllAuthorsList().size(); i++)
						{
							if (authorBlogUrl.equals(pagerAllAut.getAllAuthorsList().get(i).getBlog_url()))
							{
								weFindIt = true;
								position = i;
								break;
							}
						}
						if (weFindIt)
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
						}
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
						}
					}//if NOT all authors or categories
				}
			}
			else
			{
				//if not twoPane
				//TODO
			}
		}
		else
		{
			System.out.println("p.authorBlogUrl.equals('empty' || ''): WTF?!");
		}
	}//showAllAuthorsArticles

	//TODO
	public static void showAllCategoriesArticles(String categoryUrlFUCK, final ActionBarActivity act)
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
				if (act.getClass().getSimpleName().equals("ActivityMain"))
				{
					final ActivityMain mainActivity = (ActivityMain) act;
					//check if we show allAuthors frag
					if (mainActivity.getCurentCategoryPosition() == 3)
					{
						//switch to frag in right pager
						ViewPager rightPager = (ViewPager) mainActivity.findViewById(R.id.pager_right);
						PagerAdapterAllAuthors adapter = (PagerAdapterAllAuthors) rightPager.getAdapter();
						int position = 0;
						for (int i = 0; i < adapter.getAllAuthorsList().size(); i++)
						{
							if (categoryUrl.equals(adapter.getAllAuthorsList().get(i).getBlog_url()))//.blogLink))
							{
								position = i;
								break;
							}
						}
						rightPager.setCurrentItem(position, true);
					}
					else if (mainActivity.getCurentCategoryPosition() == 13)
					{
						//TODO
					}
					else
					{
						//else we must show authors list.
						//we can switch here if we have him in autList or not
						//if so we can show arts list in allAuthor frag or not
						ViewPager leftPager = (ViewPager) act.findViewById(R.id.pager_left);
						PagerAdapterAllAuthors pagerAllAut = new PagerAdapterAllAuthors(
						act.getSupportFragmentManager(), mainActivity);
						boolean weFindIt = false;
						int position = 0;
						for (int i = 0; i < pagerAllAut.getAllAuthorsList().size(); i++)
						{
							if (categoryUrl.equals(pagerAllAut.getAllAuthorsList().get(i).getBlog_url()))
							{
								weFindIt = true;
								position = i;
								break;
							}
						}
						if (weFindIt)
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
					}//if NOT all authors or categories
				}
			}
			else
			{
				//if not twoPane
				//TODO
			}
		}
		else
		{
			System.out.println("p.authorBlogUrl.equals('empty' || ''): WTF?!");
		}
	}//showAllCategoriesArticles

	public static void markAsRead(String url, Context ctx)
	{
		Toast.makeText(ctx, "readed!", Toast.LENGTH_SHORT).show();
	}

	public static void shareUrl(String url, Context ctx)
	{
		Toast.makeText(ctx, "share!", Toast.LENGTH_SHORT).show();
	}

	public static void showComments(ArrayList<ArtInfo> allArtsInfo, int position, ActionBarActivity act)
	{
		Toast.makeText(act, "comments!", Toast.LENGTH_SHORT).show();

		//light clicked card if we can find frag from @param act 
		if (act.getClass().getSimpleName().equals("ActivityMain"))
		{
			FragmentArtsListRecycler artsListFrag = (FragmentArtsListRecycler) act.getSupportFragmentManager()
			.findFragmentById(R.id.pager_left);
			artsListFrag.setActivatedPosition(position);
		}

		//check if it's large screen
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
		boolean twoPane = pref.getBoolean("twoPane", false);

		if (twoPane)
		{

			ViewPager pager = (ViewPager) act.findViewById(R.id.pager_right);
			if (pager.getAdapter().getClass().getSimpleName().equals(CommentsViewPagerAdapter.class.getSimpleName()))
			{
				pager.setCurrentItem(position, true);
			}
			//so it's comments adapter and need to switch to artAdapter
			else
			{
				PagerAdapter pagerAdapter = new CommentsViewPagerAdapter(act.getSupportFragmentManager(),
				allArtsInfo, CommentInfo.getDefaultAllArtsCommentsInfo(30, 10), act);
				pager.setAdapter(pagerAdapter);
				pager.setCurrentItem(position, true);
			}

		}
		else
		{
			Intent intent = new Intent(act, ActivityComments.class);
			Bundle b = new Bundle();
			b.putInt("position", position);
			ArtInfo.writeAllArtsInfoToBundle(b, allArtsInfo, allArtsInfo.get(position));
			b.putIntArray("groupChildPosition", ((ActivityBase) act).getGroupChildPosition());
			intent.putExtras(b);
			act.startActivity(intent);
		}
	}

	public static void showArticle(ArrayList<ArtInfo> allArtsInfo, int positionOfArticle, final ActionBarActivity act)
	{
		Log.d(LOG, "showArticle!");

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
		boolean twoPane = pref.getBoolean("twoPane", false);
		//check if it's large screen
		//		if (act.getClass().getSimpleName().equals(ActivityMain.class.getSimpleName()))
		if (act instanceof ActivityMain)
		{
			final ActivityMain mainActivity = (ActivityMain) act;
			if (twoPane)
			{
				final ViewPager rightPager = (ViewPager) act.findViewById(R.id.pager_right);
				//check if we are showing allAuthors (curCatPosition=3) or allCategories (curCatPosition=13) 
				if (rightPager.getAdapter() instanceof PagerAdapterAllAuthors)
				{

					//if so we must change adapters to all ViewPagers
					final ViewPager leftPager = (ViewPager) act.findViewById(R.id.pager_left);
					PagerAdapterAllAuthors pagerAllAut = new PagerAdapterAllAuthors(
					act.getSupportFragmentManager(), mainActivity);
					AllAuthorsListAdapter adAllAut = new AllAuthorsListAdapter(mainActivity, null);
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
					boolean weFindIt = false;
					int positionInLeftPager = 0;
					String authorBlogUrl = Author
					.getURLwithoutSlashAtTheEnd(allArtsInfo.get(positionOfArticle).authorBlogUrl);
					for (int i = 0; i < pagerAllAut.getAllAuthorsList().size(); i++)
					{
						if (authorBlogUrl.equals(pagerAllAut.getAllAuthorsList().get(i).getBlog_url()))//.blogLink))
						{
							weFindIt = true;
							positionInLeftPager = i;
							break;
						}
					}
					if (weFindIt)
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
					}
				}
				//				else if(true)
				//				{
				//					//TODO
				//				}
				//TODO CHECK IT so it's comments adapter and need to switch to artAdapter
				else
				{
					rightPager.setCurrentItem(positionOfArticle, true);
				}
			}
			else
			{
				Intent intent = new Intent(act, ActivityArticle.class);
				Bundle b = new Bundle();
				b.putInt("position", positionOfArticle);
				ArtInfo.writeAllArtsInfoToBundle(b, allArtsInfo, allArtsInfo.get(positionOfArticle));
				b.putIntArray("groupChildPosition", ((ActivityBase) act).getGroupChildPosition());
				intent.putExtras(b);

				act.startActivity(intent);
			}
		}
		else if (act.getClass().getSimpleName().equals(ActivityArticle.class.getSimpleName()))
		{
			//TODO
		}
		else
		{
			//TODO
		}
	}
}
