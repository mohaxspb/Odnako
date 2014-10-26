/*
 21.10.2014
ActivityArticle.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import com.google.android.gms.ads.AdView;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.fragments.ArticleFragment;
import ru.kuchanov.odnako.utils.AddAds;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class ActivityComments extends ActionBarActivity
{
	AdView adView;
	
	ArticleFragment artFrag;

	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ActivityArticle onCreate");
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.layout_activity_comments);
		
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
}
