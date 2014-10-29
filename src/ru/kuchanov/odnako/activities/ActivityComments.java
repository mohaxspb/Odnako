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

import com.google.android.gms.ads.AdView;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.CommentInfo;
import ru.kuchanov.odnako.lists_and_utils.CommentsViewPagerAdapter;
import ru.kuchanov.odnako.lists_and_utils.ZoomOutPageTransformer;
import ru.kuchanov.odnako.utils.AddAds;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

public class ActivityComments extends ActionBarActivity
{
	ActionBarActivity act;
	
	AdView adView;

//	ArticleFragment artFrag;
	
	ViewPager pager;
	PagerAdapter pagerAdapter;

	ArtInfo curArtInfo;
	int position;
	ArrayList<ArtInfo> allArtsInfo;
	
	ArrayList<CommentInfo> curArtCommentsInfoList;
	ArrayList<ArrayList<CommentInfo>> allArtsCommentsInfo;

	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ActivityArticle onCreate");
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.layout_activity_comments);
		
		this.act=this;

		//find (CREATE NEW ONE) fragment and send it some info from intent 
		//this.artFrag=(ArticleFragment)this.getSupportFragmentManager().findFragmentById(R.id.article);
		//		this.artFrag=new ArticleFragment();
		//		Bundle bundle = new Bundle();
		//		bundle.putString("edttext", "From Activity");
		//		// set Fragmentclass Arguments
		//		artFrag.setArguments(bundle);
		//		
		//		FragmentTransaction transaction=this.getSupportFragmentManager().beginTransaction();
		//		transaction.replace(R.id.article, artFrag);
		//		transaction.addToBackStack(null);
		//		transaction.commit();
		//End of find fragment and send it some info from intent 

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
		
		//def all comms info setting
		this.allArtsCommentsInfo=CommentInfo.getDefaultAllArtsCommentsInfo(this.allArtsInfo.size(), 15);
		////

		this.pager = (ViewPager) this.findViewById(R.id.comments_container);
		this.pagerAdapter = new CommentsViewPagerAdapter(this.getSupportFragmentManager(), this.allArtsInfo, this.allArtsCommentsInfo, act);
		this.pager.setAdapter(pagerAdapter);
		this.pager.setCurrentItem(position, true);
		this.pager.setPageTransformer(true, new ZoomOutPageTransformer());

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
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		System.out.println("ActivityArticle onRestoreInstanceState");
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
