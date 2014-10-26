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
import ru.kuchanov.odnako.fragments.ArticleFragment;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.utils.AddAds;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.ads.AdView;

public class ActivityArticle extends ActionBarActivity
{
	AdView adView;

//	ArticleFragment artFrag;

	ArtInfo curArtInfo;
	int position;/* position in all art arr; need to show next/previous arts */
	ArrayList<ArtInfo> allArtsInfo;

	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ActivityArticle onCreate");
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
			System.out.println("ActivityArticle: all bundles are null, so make request for info");
		}

		//find (CREATE NEW ONE) fragment and send it some info from intent 
		ArticleFragment curArtFrag = (ArticleFragment) this.getSupportFragmentManager().findFragmentById(R.id.article);
//		if(curArtFrag==null)
//		{
//			this.artFrag = new ArticleFragment();
//		}
		ArticleFragment newArtFrag=new ArticleFragment();

		Bundle bundle = new Bundle();
		bundle.putInt("position", this.position);
		bundle.putStringArray("curArtInfo", this.curArtInfo.getArtInfoAsStringArray());
		for (int i = 0; i < this.allArtsInfo.size(); i++)
		{
			if (i < 10)
			{
				bundle.putStringArray("allArtsInfo_0" + String.valueOf(i),
				this.allArtsInfo.get(i).getArtInfoAsStringArray());
			}
			else
			{
				bundle.putStringArray("allArtsInfo_" + String.valueOf(i),
				this.allArtsInfo.get(i).getArtInfoAsStringArray());
			}
		}
		// set Fragmentclass Arguments
		newArtFrag.setArguments(bundle);

		FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
		if(curArtFrag!=null)
		{
			transaction.addToBackStack(null);
			transaction.hide(curArtFrag);
		}
		transaction.add(R.id.article_container, newArtFrag);
		transaction.commit();
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
		if (this.allArtsInfo != null)
		{
			for (int i = 0; i < this.allArtsInfo.size(); i++)
			{
				if (i < 10)
				{
					outState.putStringArray("allArtsInfo_0" + String.valueOf(i),
					this.allArtsInfo.get(i).getArtInfoAsStringArray());
				}
				else
				{
					outState.putStringArray("allArtsInfo_" + String.valueOf(i),
					this.allArtsInfo.get(i).getArtInfoAsStringArray());
				}
			}
		}
		else
		{
			System.out.println("ActivityArticle: onSaveInstanceState. this.allArtsInfo=null");
		}
		//save curArtInfo
		if (this.curArtInfo != null)
		{
			outState.putStringArray("curArtInfo",
			this.curArtInfo.getArtInfoAsStringArray());
		}
		else
		{
			System.out.println("ActivityArticle: onSaveInstanceState. this.curArtInfo=null");
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		System.out.println("ActivityArticle onRestoreInstanceState");

		this.restoreState(savedInstanceState);
	}

	@Override
	public void onBackPressed()
	{
		System.out.println("ActivityArticle onBackPressed");
		//check if there is no fragments in stack, so we need to finish Activity, not only fragment
		System.out.println("this.getSupportFragmentManager().getBackStackEntryCount(): "
		+ this.getSupportFragmentManager().getBackStackEntryCount());
		if (this.getSupportFragmentManager().getBackStackEntryCount() == 0)
		{
			super.onBackPressed();
			this.finish();
		}
		else
		{
			super.onBackPressed();
		}

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
