/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.util.ArrayList;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.ArtsListAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ArticlesListFragment extends Fragment
{

	private ListView artsList;

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ArticlesListFragment onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		System.out.println("ArticlesListFragment onCreateView");
		//inflate root view
		View v=inflater.inflate(R.layout.fragment_arts_list, new LinearLayout(this.getActivity()));

		///////
		this.artsList = (ListView) v.findViewById(R.id.arts_list_view);
				
		//fill Arraylist with artsInfo
		//sample data now
		ArrayList<ArtInfo> artsInfo = new ArrayList<ArtInfo>();
		int sampleNum = 30;
		for (int i = 0; i < sampleNum; i++)
		{
			artsInfo.add(new ArtInfo("url_"+String.valueOf(i), "title_"+String.valueOf(i), "default", "author_blog_link_"+String.valueOf(i), "author_name_"+String.valueOf(i)));
		}
		ArtsListAdapter artsListAdapter = new ArtsListAdapter((ActionBarActivity) getActivity(), R.layout.arts_list_card_view, artsInfo, artsList);

		this.artsList.setAdapter(artsListAdapter);
		///////

		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		System.out.println("ArticlesListFragment onViewCreated");
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION))
		{
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity)
	{
		System.out.println("ArticlesListFragment onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onDetach()
	{
		System.out.println("ArticlesListFragment onDetach");
		super.onDetach();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		System.out.println("ArticlesListFragment onSaveInstanceState");
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION)
		{
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick)
	{
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		artsList.setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
	}

	public void setActivatedPosition(int position)
	{
		if (position == ListView.INVALID_POSITION)
		{
			artsList.setItemChecked(mActivatedPosition, false);
		}
		else
		{
			artsList.setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
	
	public int getMyActivatedPosition()
	{
		return this.mActivatedPosition;
	}
}
