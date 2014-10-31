/*
 21.10.2014
ActivityArticle.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.ArticleViewPagerAdapter;
import ru.kuchanov.odnako.lists_and_utils.ZoomOutPageTransformer;
import ru.kuchanov.odnako.utils.AddAds;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.ads.AdView;

public class ActivityArticle extends ActionBarActivity
{
	ActionBarActivity act;
	SharedPreferences pref;
	
	AdView adView;

	ViewPager pager;
	PagerAdapter pagerAdapter;

	ArtInfo curArtInfo;
	int position;
	ArrayList<ArtInfo> allArtsInfo;

	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ActivityArticle onCreate");

		//get default settings to get all settings later
		PreferenceManager.setDefaultValues(this, R.xml.pref, true);
		this.pref = PreferenceManager.getDefaultSharedPreferences(this);
		//end of get default settings to get all settings later

		//set theme before super and set content to apply it
		if (pref.getString("theme", "dark").equals("dark"))
		{
			this.setTheme(R.style.ThemeDark);
		}
		else
		{
			this.setTheme(R.style.ThemeLight);
		}

		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.layout_activity_article);

		//restore state
		Bundle stateFromIntent = this.getIntent().getExtras();
		if (stateFromIntent != null)
		{
			this.restoreState(stateFromIntent);
		}
		else if (savedInstanceState != null)
		{
			this.restoreState(savedInstanceState);
		}
		//all is null, so start request for info
		else
		{
			// TODO
			System.out.println("ActivityArticle: all bundles are null, so make request for info");
		}

		this.pager = (ViewPager) this.findViewById(R.id.article_container);
		this.pagerAdapter = new ArticleViewPagerAdapter(this.getSupportFragmentManager(), this.allArtsInfo, this);
		this.pager.setAdapter(pagerAdapter);
		this.pager.setCurrentItem(position, true);
		this.pager.setPageTransformer(true, new ZoomOutPageTransformer());

		//find (CREATE NEW ONE) fragment and send it some info from intent 
		//		ArticleFragment curArtFrag = (ArticleFragment) this.getSupportFragmentManager().findFragmentById(R.id.article_container);
		//
		//		ArticleFragment newArtFrag = new ArticleFragment();
		//
		//		Bundle bundle = new Bundle();
		//		bundle.putInt("position", this.position);
		//		bundle.putStringArray("curArtInfo", this.curArtInfo.getArtInfoAsStringArray());
		//		for (int i = 0; i < this.allArtsInfo.size(); i++)
		//		{
		//			if (i < 10)
		//			{
		//				bundle.putStringArray("allArtsInfo_0" + String.valueOf(i),
		//				this.allArtsInfo.get(i).getArtInfoAsStringArray());
		//			}
		//			else
		//			{
		//				bundle.putStringArray("allArtsInfo_" + String.valueOf(i),
		//				this.allArtsInfo.get(i).getArtInfoAsStringArray());
		//			}
		//		}
		//		// set Fragmentclass Arguments
		//		newArtFrag.setArguments(bundle);
		//
		//		FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
		//		//check if there was artFrag and it's not the same as new (compare its ArtInfo.url) and has not empty url
		//		if (curArtFrag != null && !curArtFrag.getCurArtInfo().url.equals(newArtFrag.getCurArtInfo().url)
		//		&& !curArtFrag.getCurArtInfo().url.equals("") && !curArtFrag.getCurArtInfo().url.equals("empty"))
		//		{
		//			System.out.println("!curArtFrag.getCurArtInfo().url.equals(newArtFrag.getCurArtInfo().url): "
		//			+ String.valueOf(!curArtFrag.getCurArtInfo().url.equals(newArtFrag.getCurArtInfo().url)));
		//			System.out.println("!curArtFrag.getCurArtInfo().url.equals(''): "
		//			+ String.valueOf(!curArtFrag.getCurArtInfo().url.equals("")));
		//			System.out.println("!curArtFrag.getCurArtInfo().url.equals('empty'): "
		//			+ String.valueOf(!curArtFrag.getCurArtInfo().url.equals("empty")));
		//			transaction.addToBackStack(null);
		//			transaction.hide(curArtFrag);
		//			
		//		}
		//		if(curArtFrag==null)
		//		{
		//			System.out.println("curArtFrag==null");
		//			transaction.add(R.id.article_container, newArtFrag);
		//		}
		//		
		//		transaction.commit();
		//End of find fragment and send it some info from intent 

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
		System.out.println("ActivityArticle onResume");
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		System.out.println("ActivityArticle: onSaveInstanceState");

		//save allArtsInfo
		ArtInfo.writeAllArtsInfoToBundle(outState, allArtsInfo, curArtInfo);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		System.out.println("ActivityArticle onRestoreInstanceState");

		this.restoreState(savedInstanceState);
	}

	private void restoreState(Bundle state)
	{
		this.curArtInfo = new ArtInfo(state.getStringArray("curArtInfo"));
		this.position = state.getInt("position");
		//restore AllArtsInfo
		this.allArtsInfo = new ArrayList<ArtInfo>();
		Set<String> keySet = state.keySet();
		ArrayList<String> keySetSortedArrList = new ArrayList<String>(keySet);
		Collections.sort(keySetSortedArrList);
		for (int i = 0; i < keySetSortedArrList.size(); i++)
		{
			if (keySetSortedArrList.get(i).startsWith("allArtsInfo_"))
			{
				if (i < 10)
				{
					this.allArtsInfo.add(new ArtInfo(state.getStringArray("allArtsInfo_0"
					+ String.valueOf(i))));
				}
				else
				{
					this.allArtsInfo.add(new ArtInfo(state.getStringArray("allArtsInfo_"
					+ String.valueOf(i))));
				}

			}
			else
			{
				break;
			}
		}
	}
}
