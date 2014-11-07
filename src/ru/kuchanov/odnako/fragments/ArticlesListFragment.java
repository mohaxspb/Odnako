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
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.ArtsListRecyclerViewAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.support.v7.widget.RecyclerView;

public class ArticlesListFragment extends Fragment
{

	//	private ListView artsList;
	private RecyclerView artsList;

	ActionBarActivity act;
	Context ctx;

	//////

	int mLastFirstVisibleItem = 0;

	//	private ArtsListAdapter artsListAdapter;
	private ArtsListRecyclerViewAdapter artsListAdapter;
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	ArrayList<ArtInfo> allArtsInfo;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ArticlesListFragment onCreate");
		super.onCreate(savedInstanceState);

		this.act = (ActionBarActivity) this.getActivity();
		this.ctx = this.act;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		System.out.println("ArticlesListFragment onCreateView");
		//inflate root view
		View v = inflater.inflate(R.layout.fragment_arts_list, new LinearLayout(this.getActivity()));

		///////
		//		this.artsList = (ListView) v.findViewById(R.id.arts_list_view);
		this.artsList = (RecyclerView) v.findViewById(R.id.arts_list_view);

		//fill Arraylist with artsInfo
		//sample data now
		allArtsInfo = new ArrayList<ArtInfo>();
		int sampleNum = 30;
		for (int i = 0; i < sampleNum; i++)
		{
			ArtInfo artInfo = new ArtInfo("url_" + String.valueOf(i), "title_" + String.valueOf(i), "",
			"author_blog_link_" + String.valueOf(i), "author_name_" + String.valueOf(i));
			artInfo.updateArtInfoFromRSS("preview_" + String.valueOf(i), "date_" + String.valueOf(i));
			artInfo.updateArtInfoFromARTICLE(
			i,
			i,
			"art_text_" + String.valueOf(i),
			"author_description_" + String.valueOf(i),
			"tegs_main_" + String.valueOf(i),
			"tegs_all_" + String.valueOf(i) + " !!!! " + "tegs_all_" + String.valueOf(i) + " !!!! " + "tegs_all_"
			+ String.valueOf(i) + " !!!! " + "tegs_all_" + String.valueOf(i) + " !!!! " + "tegs_all_"
			+ String.valueOf(i) + " !!!! ",
			String.valueOf(i) + " !!!! " + String.valueOf(i) + " !!!! " + String.valueOf(i) + " !!!! "
			+ String.valueOf(i) + " !!!! " + String.valueOf(i) + " !!!! " + String.valueOf(i) + " !!!! ",
			"to_read_main_" + String.valueOf(i), "to_read_more_" + String.valueOf(i), "empty");
			allArtsInfo.add(artInfo);
		}

		ArtInfo artInfoTEST = new ArtInfo(
		"http://www.odnako.org/blogs/cifrovoy-front-latviyskiy-blickrig-i-nash-otvet/", "Заголовок статьи",
		"https://pp.vk.me/c9733/u77102/151125793/w_91f2635a.jpg", "http://yuriykuchanov.odnako.org/",
		"Разработчик testetsetstetstestetstestetstetstetsetstetstetste setstestet");
		artInfoTEST.updateArtInfoFromRSS(act.getResources().getString(R.string.preview), "1 сентября 1939");
		artInfoTEST.updateArtInfoFromARTICLE(0, 0, act.getResources().getString(R.string.version_history),
		"Описание автора", "Интернет", "Интернет !!!! Андроид", "10 !!!! 10 !!!! 10 !!!! 10 !!!! 10 !!!! 10",
		"url !!!! title !!!! date !!!! url !!!! title !!!! date !!!! url !!!! title !!!! date",
		"url !!!! title !!!! date !!!! url !!!! title !!!! date",
		"https://pp.vk.me/c9733/u77102/151125793/w_91f2635a.jpg");
		allArtsInfo.set(1, artInfoTEST);
		//one more
		ArtInfo artInfoTEST2 = new ArtInfo("", "Заголовок статьи", "", "empty", "Разработчик");
		artInfoTEST2.updateArtInfoFromRSS("test_preview", "2 сентября 1945");
		artInfoTEST2.updateArtInfoFromARTICLE(0, 0, act.getResources().getString(R.string.version_history), "empty",
		"empty", "empty", "10 !!!! 10 !!!! 10 !!!! 10 !!!! 10 !!!! 10", "empty", "empty",
		"https://pp.vk.me/c9733/u77102/151125793/w_91f2635a.jpg");
		allArtsInfo.set(2, artInfoTEST2);

		//		ActivityMain.setAllArtsInfo(artsInfo);
		((ActivityMain) act).setAllArtsInfo(allArtsInfo);
		//
		//		setArtsListAdapter(new ArtsListAdapter((ActionBarActivity) getActivity(), R.layout.arts_list_card_view,
		//		allArtsInfo, artsList));
		//
		//		this.artsList.setAdapter(getArtsListAdapter());
		this.artsListAdapter = new ArtsListRecyclerViewAdapter(act, allArtsInfo, artsList);
		this.artsList.setAdapter(artsListAdapter);
		this.artsList.setItemAnimator(new DefaultItemAnimator());
		this.artsList.setLayoutManager(new LinearLayoutManager(act));
		LayoutManager mManager = this.artsList.getLayoutManager();
		ItemAnimator anim = this.artsList.getItemAnimator();
		//		artsListAdapter
		///////

		//test
		this.artsList.setOnScrollListener(new OnScrollListener()
		{
			//			LinearLayoutManager manager;
			//			Toolbar toolbar=(Toolbar) act.findViewById(R.id.toolbar);
			//			ImageView topImg=(ImageView) act.findViewById(R.id.top_img);
			int initialDistance = -1;
			int curentDistance = -1;

			public void onScrollStateChanged(RecyclerView recyclerView, int newState)
			{

				LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
				Toolbar toolbar = (Toolbar) act.findViewById(R.id.toolbar);
				ImageView topImg = (ImageView) act.findViewById(R.id.top_img);
				switch (newState)
				{
					case (RecyclerView.SCROLL_STATE_DRAGGING):
						//						System.out.println("dragging");
						//mesuring initialDistance between actionBar and 1-st item
						if (initialDistance == -1)
						{
							initialDistance = (int) (manager.findViewByPosition(1).getY() - toolbar.getHeight());
						}
					break;
					case (RecyclerView.SCROLL_STATE_IDLE):
						//						System.out.println("SCROLL_STATE_IDLE");
						if (topImg.getY() > 0)
						{
							topImg.setY(0);
						}
					break;
					case (RecyclerView.SCROLL_STATE_SETTLING):
					//						System.out.println("SCROLL_STATE_SETTLING");
					break;
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int x, int y)
			{
				LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
				Toolbar toolbar = (Toolbar) act.findViewById(R.id.toolbar);
				ImageView topImg = (ImageView) act.findViewById(R.id.top_img);

				boolean scrollToUp = y > 0;

				//				android.support.v7.widget.Toolbar.LayoutParams lp=(android.support.v7.widget.Toolbar.LayoutParams) toolbar.getLayoutParams();
				//				android.view.ViewGroup.LayoutParams lp=(android.view.ViewGroup.LayoutParams) toolbar.getLayoutParams();
				//				lp.height-=y;
				//				toolbar.setLayoutParams(lp);
				//				android.view.ViewGroup.LayoutParams lp=(android.view.ViewGroup.LayoutParams) toolbar.getLayoutParams();
				//				if(lp.height<100)
				//				View firstArticleChild=recyclerView.getLayoutManager().getChildAt(0);
				//				System.out.println("firstArticleChild y: "+firstArticleChild.getY());
				//				System.out.println("toolbar y: "+toolbar.getY());
				//				if(firstArticleChild.getY()<=toolbar.getY()-toolbar.getHeight())
				//				{
				//					toolbar.setY(firstArticleChild.getY()+toolbar.getHeight());
				//				}
				//				else if(y<0 && toolbar.getY()<0)
				//				{
				//					toolbar.setY(toolbar.getY()-y );
				//				}
				////				else if(toolbar.getTop()<0 && y>0)
				////				{
				////					toolbar.setY(toolbar.getY()+y);
				////				}
				//				ImageView topImg=(ImageView) act.findViewById(R.id.top_img);
				//				if(topImg.getY()<0 && y>0)
				//				{
				//					topImg.setY(topImg.getY()+y/2);
				//				}
				//				else if(topImg.getY()<0 && y<0)
				//				{
				//					topImg.setY(topImg.getY()-y/2);
				//				}		
				//move picture
				if (scrollToUp)
				{
					if (topImg.getY() + topImg.getHeight() > 0)
					{
						topImg.setY(topImg.getY() - y / 2);
					}
					else
					{
						topImg.setY(-topImg.getHeight());
					}
				}
				else
				{
					//test find main view of recyclerView that holds all articles cards

					if (manager.findFirstVisibleItemPosition() <= 1)
					{
						if (topImg.getY() + topImg.getHeight() >= 0 && topImg.getY() < 0)
						{
							topImg.setY(topImg.getY() - y / 2);
						}
						else if (topImg.getY() > 0)
						{
							topImg.setY(0);
						}

					}
				}
				////End of move picture

				//move light actionBar
				if (scrollToUp)
				{
					//move actionBar UP
					if (manager.findFirstVisibleItemPosition() == 0)
					{
						if (manager.findViewByPosition(1).getY() < toolbar.getHeight())
						{
							if (toolbar.getY() > -toolbar.getHeight())
							{
								toolbar.setY(toolbar.getY() - y);
							}
							else
							{
								toolbar.setY(-toolbar.getHeight());
							}
						}
					}
					//UNlight actionBar UP
					//do it only while it's not moved
					if (toolbar.getY() == 0)
					{
						curentDistance = (int) (manager.findViewByPosition(1).getY() - toolbar.getHeight());
						float percent = (float)this.curentDistance / (float)this.initialDistance;
						float gradient=1f - percent;
						int newAlpha = (int) (255 * gradient);
//						System.out.println("this.curentDistance / this.initialDistance: " + this.curentDistance +"/" +this.initialDistance);
//						System.out.println("percent: " + percent);
//						System.out.println("gradient: " + gradient);
//						System.out.println("newAlpha: " + newAlpha);
						toolbar.getBackground().setAlpha(newAlpha);
					}
				}
				else
				{
					//move actionBar
					if (toolbar.getY() <0)
					{
						toolbar.setY(toolbar.getY() - y);
					}
					else
					{
						toolbar.setY(0);
					}
					
					//light actionBar
					
				}
			}
		});
		////test

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
		//		artsList.setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
		//		this.artsList.seti
	}

	public void setActivatedPosition(int position)
	{
		System.out.println("setActivatedPosition(int position: " + position);
		if (position == ListView.INVALID_POSITION)
		{
			//			artsList.setItemChecked(mActivatedPosition, false);
		}
		else
		{
			//			artsList.setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	public void scrollToActivatedPosition()
	{
		this.artsList.smoothScrollToPosition(mActivatedPosition);
	}

	public int getMyActivatedPosition()
	{
		return this.mActivatedPosition;
	}

	/**
	 * @return the artsListAdapter
	 */
	public ArtsListRecyclerViewAdapter getArtsListAdapter()
	{
		return artsListAdapter;
	}

	/**
	 * @param artsListAdapter
	 *            the artsListAdapter to set
	 */
	public void ArtsListRecyclerViewAdapter(ArtsListRecyclerViewAdapter artsListAdapter)
	{
		this.artsListAdapter = artsListAdapter;
	}

	public RecyclerView getArtsListView()
	{
		return this.artsList;
	}
}
