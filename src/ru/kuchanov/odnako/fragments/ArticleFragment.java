/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import ru.kuchanov.odnako.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ArticleFragment extends Fragment
{
	private ActionBarActivity act;
	private SharedPreferences pref;
	
	private TextView artTextView;
	
	private TextView artTitleTV;
	private TextView artAuthorTV;
	private TextView artAuthorDescription;
	
	private ImageView artAuthorIV;
	private ImageView artAuthorDescriptionIV;
	private ImageView artAuthorArticlesIV;
	
	
	
	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		System.out.println("ArticleFragment onCreate");
		
		this.act=(ActionBarActivity) this.getActivity();
		
		this.setRetainInstance(true);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		System.out.println("ArticleFragment onCreateView");
		View v=inflater.inflate(R.layout.fragment_art, new LinearLayout(act));
		
		this.setRetainInstance(true);
		
		//find all views
		this.artTextView=(TextView)v.findViewById(R.id.art_text);
		this.artTextView.setText(R.string.version_history);
		
		this.artTitleTV=(TextView)v.findViewById(R.id.art_title);
		this.artAuthorTV=(TextView)v.findViewById(R.id.art_author);
		this.artAuthorDescription=(TextView)v.findViewById(R.id.art_author_description);
		
		this.artAuthorIV=(ImageView)v.findViewById(R.id.art_author_img);
		this.artAuthorArticlesIV=(ImageView)v.findViewById(R.id.art_author_all_arts_btn);
		this.artAuthorDescriptionIV=(ImageView)v.findViewById(R.id.art_author_description_btn);
		
		this.setSizeAndTheme();
		return v;
	}
	
	private void setSizeAndTheme()
	{
		pref = PreferenceManager.getDefaultSharedPreferences(act);
		String scaleFactorString = pref.getString("scale_art", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);
		
		this.artTextView.setTextSize(21 * scaleFactor);
		
		this.artTitleTV.setTextSize(25 * scaleFactor);
		this.artAuthorTV.setTextSize(21 * scaleFactor);
		this.artAuthorDescription.setTextSize(21 * scaleFactor);
		
		//images
		final float scale = getResources().getDisplayMetrics().density;
		int pixels = (int) (75 * scaleFactor * scale + 0.5f);
		LayoutParams params = new LayoutParams(pixels, pixels);
		
		this.artAuthorIV.setPadding(5, 5, 5, 5);
		this.artAuthorIV.setScaleType(ScaleType.FIT_XY);
		this.artAuthorIV.setLayoutParams(params);
		
		this.artAuthorArticlesIV.setPadding(5, 5, 5, 5);
		this.artAuthorArticlesIV.setScaleType(ScaleType.FIT_XY);
		this.artAuthorArticlesIV.setLayoutParams(params);
		
		this.artAuthorDescriptionIV.setPadding(5, 5, 5, 5);
		this.artAuthorDescriptionIV.setScaleType(ScaleType.FIT_XY);
		this.artAuthorDescriptionIV.setLayoutParams(params);
		
		
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		System.out.println("ArticleFragment onViewCreated");
		super.onViewCreated(view, savedInstanceState);
		
		this.setRetainInstance(true);
	}

	@Override
	public void onAttach(Activity activity)
	{
		System.out.println("ArticleFragment onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onDetach()
	{
		System.out.println("ArticleFragment onDetach");
		super.onDetach();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		System.out.println("ArticleFragment onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}
}
