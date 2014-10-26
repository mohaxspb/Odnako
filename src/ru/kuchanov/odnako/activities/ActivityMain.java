/*
 19.10.2014
ActivityMain.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import com.google.android.gms.ads.AdView;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.fragments.ArticlesListFragment;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.utils.AddAds;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;

public class ActivityMain extends ActionBarActivity
{
	public static final String EXTRA_MESSAGE_FROM_MAIN_TO_ARTICLE_CUR_ART_INFO = "extra_message_from_main_to_article_cur_art_info";
	public static final String EXTRA_MESSAGE_FROM_MAIN_TO_ARTICLE_POSITION = "extra_message_from_main_to_article_position";
	
	public boolean twoPane;
	SharedPreferences pref;
	AdView adView;

	private ArrayList<ArtInfo> allArtsInfo;
	private ArtInfo curArtInfo;

	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ActivityMain onCreate");
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.layout_activity_main);

		//get default settings to get all settings later
		PreferenceManager.setDefaultValues(this, R.xml.pref, true);
		//end of get default settings to get all settings later

		//Set unreaded num of arts to zero
		//it's for new arts motification
		SharedPreferences prefsNumOfArts = this.getSharedPreferences("saveNumOfUnReadedArts", 0);
		SharedPreferences.Editor editor = prefsNumOfArts.edit();
		editor.putInt("quontityOfUnreadedArts", 0);
		editor.commit();
		//end of Set unreaded num of arts to zero

		//check if there is two fragments. If so, set flag (twoPane) to true
		if (findViewById(R.id.article) != null)
		{
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			twoPane = true;

			//save it to pref, to be able to read it without calling Activity
			this.pref = PreferenceManager.getDefaultSharedPreferences(this);
			this.pref.edit().putBoolean("twoPane", twoPane).commit();

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((ArticlesListFragment) getSupportFragmentManager().findFragmentById(R.id.articles_list)).setActivateOnItemClick(true);
		}

		//adMob
		adView = (AdView) this.findViewById(R.id.adView);
		AddAds addAds = new AddAds(this, this.adView);
		addAds.addAd();
		//end of adMob
	}
	
	@Override
	public void onPause()
	{
		adView.pause();
		super.onPause();
	}

	@Override
	public void onDestroy()
	{
		adView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onResume()
	{
		System.out.println("ActivityMain onResume");
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		System.out.println("ActivityMain: onSaveInstanceState");

		//save allArtsInfo
		if (this.allArtsInfo != null)
		{
			for (int i = 0; i < this.allArtsInfo.size(); i++)
			{
				if (i < 10)
				{
					outState.putStringArray("ALL_ARTS_INFO_0" + String.valueOf(i), this.allArtsInfo.get(i).getArtInfoAsStringArray());
				}
				else
				{
					outState.putStringArray("ALL_ARTS_INFO_" + String.valueOf(i), this.allArtsInfo.get(i).getArtInfoAsStringArray());
				}
			}
		}
		else
		{
			System.out.println("ActivityMain: onSaveInstanceState. ActivityMain.ALL_ARTS_INFO=null");
		}
		//save curArtInfo
		if (this.curArtInfo != null)
		{
			outState.putStringArray("CUR_ART_INFO", this.curArtInfo.getArtInfoAsStringArray());
		}
		else
		{
			System.out.println("ActivityMain: onSaveInstanceState. ActivityMain.CUR_ART_INFO=null");
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		System.out.println("ActivityMain onRestoreInstanceState");

		//restore All_arts_info
		Set<String> keySet = savedInstanceState.keySet();
		ArrayList<String> keySetSortedArrList = new ArrayList<String>(keySet);
		Collections.sort(keySetSortedArrList);
		if (keySet.contains("ALL_ARTS_INFO_00"))
		{
			this.allArtsInfo = new ArrayList<ArtInfo>();
			for (int i = 0; i < keySetSortedArrList.size(); i++)
			{
				String s = keySetSortedArrList.get(i);
				if (s.startsWith("ALL_ARTS_INFO_"))
				{
					if (i < 10)
					{
						this.allArtsInfo.add(new ArtInfo(savedInstanceState.getStringArray("ALL_ARTS_INFO_0" + String.valueOf(i))));
					}
					else
					{
						this.allArtsInfo.add(new ArtInfo(savedInstanceState.getStringArray("ALL_ARTS_INFO_" + String.valueOf(i))));
					}
				}
			}
		}
		else
		{
			System.out.println("ActivityMain: onRestoreInstanceState. ActivityMain.ALL_ARTS_INFO=null");
		}

		//restore curArtInfo
		if (keySet.contains("CUR_ART_INFO"))
		{
			this.curArtInfo = new ArtInfo(savedInstanceState.getStringArray("CUR_ART_INFO"));
		}
		else
		{
			System.out.println("ActivityMain: onRestoreInstanceState. ActivityMain.CUR_ART_INFO=null");
		}
	}

//	/**
//	 * @return the curArtInfo
//	 */
//	public static ArrayList<ArtInfo> getAllArtsInfo()
//	{
//		return allArtsInfo;
//	}
//
//	/**
//	 * @param curArtInfo
//	 *            the curArtInfo to set
//	 */
//	public static void setAllArtsInfo(ArrayList<ArtInfo> allArtsInfo)
//	{
//		ActivityMain.allArtsInfo = allArtsInfo;
//	}
//
//	/**
//	 * @return the cUR_ARTS_INFO
//	 */
//	public static ArtInfo getCUR_ART_INFO()
//	{
//		return CUR_ART_INFO;
//	}
//
//	/**
//	 * @param cUR_ARTS_INFO
//	 *            the cUR_ARTS_INFO to set
//	 */
//	public static void setCUR_ART_INFO(ArtInfo cUR_ARTS_INFO)
//	{
//		CUR_ART_INFO = cUR_ARTS_INFO;
//	}
	
	/**
	 * @return the curArtInfo
	 */
	public ArrayList<ArtInfo> getAllArtsInfo()
	{
		return this.allArtsInfo;
	}

	/**
	 * @param curArtInfo
	 *            the curArtInfo to set
	 */
	public void setAllArtsInfo(ArrayList<ArtInfo> allArtsInfo)
	{
		this.allArtsInfo = allArtsInfo;
	}

	/**
	 * @return the cUR_ARTS_INFO
	 */
	public ArtInfo getCUR_ART_INFO()
	{
		return curArtInfo;
	}

	/**
	 * @param cUR_ARTS_INFO
	 *            the cUR_ARTS_INFO to set
	 */
	public void setCUR_ART_INFO(ArtInfo cUR_ARTS_INFO)
	{
		this.curArtInfo = cUR_ARTS_INFO;
	}
}
