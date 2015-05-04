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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

//public class PagerAdapterArticles extends FragmentStatePagerAdapter
public class PagerAdapterArticles extends FragmentPagerAdapter
{
	static String LOG = PagerAdapterArticles.class.getSimpleName() + "/";

	private ArrayList<Article> allArtsInfo;

	private String category;

	AppCompatActivity act;

	public PagerAdapterArticles(FragmentManager fm, String category, AppCompatActivity act)
	{
		super(fm);
		//Log.i(LOG_TAG + category, "PagerAdapterArticles CONSTRUCTOR called");
		this.category = category;
		this.act = act;

		this.setAllArtsInfo(((ActivityBase) act).getAllCatArtsInfo().get(category));
	}

	@Override
	public void notifyDataSetChanged()
	{
		Log.e(LOG + category, "notifyDataSetChanged called");

		ArrayList<Article> artList = ((ActivityBase) act).getAllCatArtsInfo().get(getCategoryToLoad());
		//Log.e(LOG + category, "artList==null: " + String.valueOf(artList == null));
		this.setAllArtsInfo(artList);
		super.notifyDataSetChanged();
	}

	@Override
	public Fragment getItem(int position)
	{
		FragmentArticle artFrag = new FragmentArticle();
		Bundle b = new Bundle();
		if (this.getAllArtsInfo() == null)
		{
			b.putParcelableArrayList(Article.KEY_ALL_ART_INFO, null);
			b.putParcelable(Article.KEY_CURENT_ART, null);

			ArrayList<Article> def = new ArrayList<Article>();
			Article a = new Article();
			a.setTitle("Статьи загружаются, подождите пожалуйста");
			this.setAllArtsInfo(def);
			this.notifyDataSetChanged();
		}
		else
		{
			b.putParcelableArrayList(Article.KEY_ALL_ART_INFO, this.getAllArtsInfo());
			b.putParcelable(Article.KEY_CURENT_ART, this.getAllArtsInfo().get(position));
		}

		b.putInt("position", position);
		artFrag.setArguments(b);

		return artFrag;
	}

	@Override
	public int getCount()
	{
		if (this.getAllArtsInfo() == null)
		{
			return 1;
		}
		else
		{
			return this.getAllArtsInfo().size();
		}
	}

	@Override
	public int getItemPosition(Object object)
	{
		if (this.getAllArtsInfo() == null)
		{
			return POSITION_NONE;
		}
		//XXX test
		//				else
		//				{
		//					return POSITION_NONE;
		//				}
		if (object instanceof FragArtUPD)
		{
			FragmentArticle artFrag = (FragmentArticle) object;
			if (artFrag.isAdded())
			{
				if (artFrag.getPosition() >= this.getAllArtsInfo().size())
				{
					//Log.e(LOG_TAG + category, "(FragmentArticle) object).getPosition()>=this.allArtsInfo.size()");
					return POSITION_NONE;
				}
				else
				{
					try
					{

						((FragArtUPD) object).update(this.getAllArtsInfo().get(artFrag.getPosition()));
					} catch (NullPointerException e)
					{
						Log.e(LOG,
						"CATCHED NULLPOINTEREXCEPTION AT ARTICLE FRAG APDATION ON PAGER NOTIFYDATASETCHANGED!!!");
						e.printStackTrace();
						return POSITION_NONE;
					}
				}
			}
			else
			{
				//Log.e(LOG_TAG, "Fragment not added");
				String articleUrl = this.getAllArtsInfo().get(artFrag.getPosition()).getUrl();
				Intent intentToArticleFrag = new Intent(articleUrl + "frag_selected");
				LocalBroadcastManager.getInstance(act).sendBroadcast(intentToArticleFrag);

				return POSITION_NONE;
			}
		}
		//don't return POSITION_NONE, avoid fragment recreation. 
		return super.getItemPosition(object);
	}

	public String getCategoryToLoad()
	{
		return category;
	}

	public ArrayList<Article> getAllArtsInfo()
	{
		return allArtsInfo;
	}

	public void setAllArtsInfo(ArrayList<Article> allArtsInfo)
	{
		this.allArtsInfo = allArtsInfo;
	}
}