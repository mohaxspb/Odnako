/*
 27.10.2014
ViewPagerAdapter.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.fragments.FragArtUPD;
import ru.kuchanov.odnako.fragments.FragmentArticle;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

public class PagerAdapterArticles extends FragmentStatePagerAdapter
{
	static String LOG_TAG = PagerAdapterArticles.class.getSimpleName() + "/";

	ArrayList<Article> allArtsInfo;

	String category;

	ActionBarActivity act;

	public PagerAdapterArticles(FragmentManager fm, String category, ActionBarActivity act)
	{
		super(fm);
		Log.i(LOG_TAG + category, "ArticlesPagerAdapter CONSTRUCTOR called");
		this.category = category;
		this.act = act;

//		this.allArtsInfo = ((ActivityBase) act).getAllCatArtsInfo().get(category);
		this.notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetChanged()
	{
//		Log.e(LOG_TAG + category, "notifyDataSetChanged called");
		this.allArtsInfo = ((ActivityBase) act).getAllCatArtsInfo().get(category);
		super.notifyDataSetChanged();
	}

	@Override
	public Fragment getItem(int position)
	{
		FragmentArticle artFrag = new FragmentArticle();
		Bundle b = new Bundle();
		if (this.allArtsInfo == null)
		{
			b.putParcelableArrayList(Article.KEY_ALL_ART_INFO, null);
			b.putParcelable(Article.KEY_CURENT_ART, null);

			ArrayList<Article> def = new ArrayList<Article>();
			Article a = new Article();
			a.setTitle("Статьи загружаются, подождите пожалуйста");
			this.allArtsInfo = def;
			this.notifyDataSetChanged();
		}
		else
		{
			b.putParcelableArrayList(Article.KEY_ALL_ART_INFO, this.allArtsInfo);
			b.putParcelable(Article.KEY_CURENT_ART, this.allArtsInfo.get(position));
		}

		b.putInt("position", position);
		artFrag.setArguments(b);

		return artFrag;
	}

	@Override
	public int getCount()
	{
		if (this.allArtsInfo == null)
		{
			return 1;
		}
		else
		{
			return this.allArtsInfo.size();
		}
	}

	@Override
	public int getItemPosition(Object object)
	{
		if (this.allArtsInfo == null)
		{
			return POSITION_NONE;
		}
		if (object instanceof FragArtUPD)
		{
			if (((Fragment) object).isAdded())
			{
				if (((FragmentArticle) object).getPosition() >= this.allArtsInfo.size())
				{
					//Log.e(LOG_TAG + category, "(FragmentArticle) object).getPosition()>=this.allArtsInfo.size()");
					return POSITION_NONE;
				}
				else
				{
					try
					{
						((FragArtUPD) object).update(this.allArtsInfo);
					} catch (NullPointerException e)
					{
						Log.e(LOG_TAG,
						"CATCHED NULLPOINTEREXCEPTION AT ARTICLE FRAG APDATION ON PAGER NOTIFYDATASETXHANGED!!!");
						e.printStackTrace();
						return POSITION_NONE;
					}
				}
			}
			else
			{
				//Log.e(LOG_TAG, "Fragment not added");
			}
		}
		//don't return POSITION_NONE, avoid fragment recreation. 
		return super.getItemPosition(object);
	}
}
