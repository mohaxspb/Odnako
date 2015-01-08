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
import ru.kuchanov.odnako.fragments.FragmentArtsRecyclerList;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

public class Actions
{

	/**
	 * 
	 */
	public Actions()
	{
		// TODO Auto-generated constructor stub
	}

	public static void showAllAuthorsArticles(ArtInfo p, ActionBarActivity act)
	{
		if (!p.authorBlogUrl.equals("empty") && !p.authorBlogUrl.equals(""))
		{
			Toast.makeText(act, "show all AuthorsArticles!", Toast.LENGTH_SHORT).show();
		}
		else
		{
			System.out.println("p.authorBlogUrl.equals('empty') (|| ''): WTF?!");
		}
	}

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
			FragmentArtsRecyclerList artsListFrag = (FragmentArtsRecyclerList) act.getSupportFragmentManager()
			.findFragmentById(R.id.arts_list_container);
			artsListFrag.setActivatedPosition(position);
		}

		//check if it's large screen
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
		boolean twoPane = pref.getBoolean("twoPane", false);

		if (twoPane)
		{

			ViewPager pager = (ViewPager) act.findViewById(R.id.article_comments_container);
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

	public static void showArticle(ArrayList<ArtInfo> allArtsInfo, int position, ActionBarActivity act)
	{
		Toast.makeText(act, "showArticle!", Toast.LENGTH_SHORT).show();

		//fill CUR_ART_INFO var 
		//		((ActivityMain) act).setCUR_ART_INFO(artInfo);

		//check if it's large screen
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
		boolean twoPane = pref.getBoolean("twoPane", false);
		if (twoPane)
		{
			//			ArticlesListFragment artsListFrag = (ArticlesListFragment) ((ActivityMain) act).getSupportFragmentManager()
			//			.findFragmentById(R.id.arts_list_container);
			//			artsListFrag.setActivatedPosition(position);
//			ViewPager artsListPager = (ViewPager) act.findViewById(R.id.arts_list_container);
//			ArtsListsPagerAdapter artsListPagerAdapter = (ArtsListsPagerAdapter) artsListPager.getAdapter();
//			int curentCategoryPosition = ((ActivityMain) act).getCurentCategoryPosition();
//			ArticlesListFragment curArtsListFrag = (ArticlesListFragment) (artsListPagerAdapter)
//			.getRegisteredFragment(curentCategoryPosition);
//			curArtsListFrag.setActivatedPosition(position);
			///////////
			ViewPager pager = (ViewPager) act.findViewById(R.id.article_comments_container);
			if (pager.getAdapter().getClass().getSimpleName().equals(PagerArticlesAdapter.class.getSimpleName()))
			{
				pager.setCurrentItem(position, true);
			}
			//so it's comments adapter and need to switch to artAdapter
			else
			{
				//				PagerAdapter pagerAdapter = new ArticleViewPagerAdapter(act.getSupportFragmentManager(),
				//				((ActivityMain) act).getAllArtsInfo(), act);
//				PagerAdapter pagerAdapter = new ArticlesPagerAdapter(act.getSupportFragmentManager(), allArtsInfo,
//				act);
//				pager.setAdapter(pagerAdapter);
				pager.setCurrentItem(position, true);
			}

		}
		else
		{
			Intent intent = new Intent(act, ActivityArticle.class);
			Bundle b = new Bundle();
			b.putInt("position", position);
			ArtInfo.writeAllArtsInfoToBundle(b, allArtsInfo, allArtsInfo.get(position));
			b.putIntArray("groupChildPosition", ((ActivityBase) act).getGroupChildPosition());
			intent.putExtras(b);

			act.startActivity(intent);
		}
	}

}
