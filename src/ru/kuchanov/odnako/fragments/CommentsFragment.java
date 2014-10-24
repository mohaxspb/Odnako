/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.util.ArrayList;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.lists_and_utils.CommentInfo;
import ru.kuchanov.odnako.lists_and_utils.CommentsListAdapter;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

public class CommentsFragment extends Fragment
{
	private ActionBarActivity act;
	private SharedPreferences pref;
	private boolean twoPane;

	ArtInfo curArtInfo;
	
	private ArrayList<CommentInfo> artCommentsInfoList;
	String[] defaultCommInfo;

	private ListView commentsListView;

	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		System.out.println("ArticleFragment onCreate");

		this.act = (ActionBarActivity) this.getActivity();

		this.curArtInfo = ActivityMain.getCUR_ART_INFO();

		pref = PreferenceManager.getDefaultSharedPreferences(act);
		this.twoPane = pref.getBoolean("twoPane", false);
		if(this.twoPane)
		{
			
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		System.out.println("ArticleFragment onCreateView");
		View v = inflater.inflate(R.layout.fragment_comments_list, new LinearLayout(act));

		//find all views
		this.commentsListView = (ListView) v.findViewById(R.id.comments_list_view);
		//end of find all views

		//fill Arraylist with artsInfo
		this.artCommentsInfoList= new ArrayList<CommentInfo>();
		int sampleNum = 30;
		for (int i = 0; i < sampleNum; i++)
		{
			defaultCommInfo=new String[12];
			this.defaultCommInfo[0] = "name_"+String.valueOf(i);
			this.defaultCommInfo[1] = "comment_text_"+String.valueOf(i);
			this.defaultCommInfo[2] = "https://pp.vk.me/c9733/u77102/151125793/w_91f2635a.jpg";
			this.defaultCommInfo[3] = "1 сентября 1939";
			this.defaultCommInfo[4] = "Saint-Petersburg";
			this.defaultCommInfo[5] = String.valueOf(i);
			this.defaultCommInfo[6] = String.valueOf(i);
			this.defaultCommInfo[7] = "https://pp.vk.me/c9733/u77102/151125793/w_91f2635a.jpg";
			this.defaultCommInfo[8] = "data_pid";
			this.defaultCommInfo[9] = "id";
			this.defaultCommInfo[10] = "0";
			this.defaultCommInfo[11] = "5";
			CommentInfo commentInfo=new CommentInfo(this.defaultCommInfo);
			
			this.artCommentsInfoList.add(commentInfo);
		}
		
		CommentInfo artInfoTEST=new CommentInfo("Юрий", "Тестовы коммент", "https://pp.vk.me/c9733/u77102/151125793/w_91f2635a.jpg", "1 сентября 1939", "Saint-Petersburg", "100", "0", "https://pp.vk.me/c9733/u77102/151125793/w_91f2635a.jpg", "100", "1000", "0", "5");
		
		artCommentsInfoList.set(1, artInfoTEST);
		//sample data now
		

		CommentsListAdapter commentsListAdapter = new CommentsListAdapter(this.act, R.layout.comment_card_view, this.artCommentsInfoList, this.commentsListView);

		this.commentsListView.setAdapter(commentsListAdapter);
		///////

		//get info from activity
		////End of get info from activity

		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		System.out.println("ArticleFragment onViewCreated");
		super.onViewCreated(view, savedInstanceState);

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
