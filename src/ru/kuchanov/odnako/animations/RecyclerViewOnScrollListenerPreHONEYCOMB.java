/*
 08.11.2014
RecyclerViewOnScrollListener.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.animations;

import ru.kuchanov.odnako.R;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

public class RecyclerViewOnScrollListenerPreHONEYCOMB extends OnScrollListener
{
	ActionBarActivity act;

	int initialDistance = -100000;
	int curentDistance = -1;

	LinearLayoutManager manager;
	Toolbar toolbar;
	ImageView topImg;

	/**
	 * 
	 */
	public RecyclerViewOnScrollListenerPreHONEYCOMB(ActionBarActivity act)
	{
		this.act = act;
		toolbar = (Toolbar) act.findViewById(R.id.toolbar);
		topImg = (ImageView) act.findViewById(R.id.top_img);
	}

	public void onScrollStateChanged(RecyclerView recyclerView, int newState)
	{

		manager = (LinearLayoutManager) recyclerView.getLayoutManager();
		toolbar = (Toolbar) act.findViewById(R.id.toolbar);
		topImg = (ImageView) act.findViewById(R.id.top_img);
		
		switch (newState)
		{
			case (RecyclerView.SCROLL_STATE_DRAGGING):
				//						System.out.println("dragging");
				//mesuring initialDistance between actionBar and 1-st item
				if (initialDistance == -100000)
				{
					initialDistance = (int) (manager.findViewByPosition(1).getTop() - toolbar.getHeight());
				}
			break;
			case (RecyclerView.SCROLL_STATE_IDLE):
				//						System.out.println("SCROLL_STATE_IDLE");
				if (topImg.getTop() > 0)
				{
					LayoutParams lp=(LayoutParams) topImg.getLayoutParams();
					lp.setMargins(0, 0, 0, 0);
					topImg.setLayoutParams(lp);
				}
				if (manager.findFirstVisibleItemPosition() == 0)
				{
					if (manager.findViewByPosition(0).getTop() == 0)
					{
						LayoutParams lp=(LayoutParams) topImg.getLayoutParams();
						lp.setMargins(0, 0, 0, 0);
						topImg.setLayoutParams(lp);
					}
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
		manager = (LinearLayoutManager) recyclerView.getLayoutManager();
		boolean scrollToUp = y > 0;
		//move picture
		if (scrollToUp)
		{
			if (topImg.getTop() + topImg.getHeight() > 0)
			{
				LayoutParams lp=(LayoutParams) topImg.getLayoutParams();
				lp.setMargins(0, topImg.getTop() - y / 2, 0, 0);
				topImg.setLayoutParams(lp);
//				topImg.setY(topImg.getTop() - y / 2);
			}
			else
			{
				LayoutParams lp=(LayoutParams) topImg.getLayoutParams();
				lp.setMargins(0, -topImg.getHeight(), 0, 0);
				topImg.setLayoutParams(lp);
//				topImg.setY(-topImg.getHeight());
			}
		}
		else
		{
			if (manager.findFirstVisibleItemPosition() <= 1)
			{
				if (topImg.getTop() < 0)
				{
					LayoutParams lp=(LayoutParams) topImg.getLayoutParams();
					lp.setMargins(0, topImg.getTop() - y / 2, 0, 0);
					topImg.setLayoutParams(lp);
//					topImg.setY(topImg.getTop() - y / 2);
				}
				else if (topImg.getTop() > 0)
				{
					LayoutParams lp=(LayoutParams) topImg.getLayoutParams();
					lp.setMargins(0, 0, 0, 0);
					topImg.setLayoutParams(lp);
//					topImg.setY(0);
				}

			}
		}
		////End of move picture

		//move light actionBar
		if (scrollToUp)
		{
			//move actionBar UP
			//on the very top of list
			if (manager.findFirstVisibleItemPosition() == 0)
			{
				if (manager.findViewByPosition(1).getTop() < toolbar.getHeight())
				{
					if (toolbar.getTop() > -toolbar.getHeight())
					{
						LayoutParams lp=(LayoutParams) toolbar.getLayoutParams();
						lp.setMargins(0, toolbar.getTop() - y, 0, 0);
						toolbar.setLayoutParams(lp);
//						toolbar.setY(toolbar.getTop() - y);
					}
					else
					{
						LayoutParams lp=(LayoutParams) toolbar.getLayoutParams();
						lp.setMargins(0, -toolbar.getHeight(), 0, 0);
						toolbar.setLayoutParams(lp);
//						toolbar.setY(-toolbar.getHeight());
					}
				}
			}
			//from any other position
			else
			{
				if (toolbar.getTop() > -toolbar.getHeight())
				{
					LayoutParams lp=(LayoutParams) toolbar.getLayoutParams();
					lp.setMargins(0, toolbar.getTop() - y, 0, 0);
					toolbar.setLayoutParams(lp);
//					toolbar.setY(toolbar.getTop() - y);
				}
				else
				{
					LayoutParams lp=(LayoutParams) toolbar.getLayoutParams();
					lp.setMargins(0, -toolbar.getHeight(), 0, 0);
					toolbar.setLayoutParams(lp);
//					toolbar.setY(-toolbar.getHeight());
				}
			}
			//UNlight actionBar UP
			//do it only while it's not moved
			//and we are on the top of our list
			//					System.out.println("firstVisPos ==0: "+String.valueOf(manager.findFirstVisibleItemPosition()==0));
			if (toolbar.getTop() == 0)// && manager.findFirstVisibleItemPosition()==0)
			{
				if (manager.findFirstVisibleItemPosition() == 0)
				{
					curentDistance = (int) (manager.findViewByPosition(1).getTop() - toolbar.getHeight());
					float percent = (float) this.curentDistance / (float) this.initialDistance;
					float gradient = 1f - percent;
					int newAlpha = (int) (255 * gradient);
					toolbar.getBackground().setAlpha(newAlpha);
				}
				else
				// if(toolbar.getBackground().getAlpha()<1)
				{
					toolbar.getBackground().setAlpha(255);
				}
			}
		}
		else
		{
			//move actionBar
			if (toolbar.getTop() < 0)
			{
				LayoutParams lp=(LayoutParams) toolbar.getLayoutParams();
				lp.setMargins(0, toolbar.getTop() - y, 0, 0);
				toolbar.setLayoutParams(lp);
//				toolbar.setY(toolbar.getTop() - y);
			}
			else
			{
				LayoutParams lp=(LayoutParams) toolbar.getLayoutParams();
				lp.setMargins(0, 0, 0, 0);
				toolbar.setLayoutParams(lp);
//				toolbar.setY(0);
			}

			//light actionBar
			if (toolbar.getTop() == 0)// && manager.findFirstVisibleItemPosition()==0)
			{
				if (manager.findFirstVisibleItemPosition() == 0)
				{
					curentDistance = (int) (manager.findViewByPosition(1).getTop() - toolbar.getHeight());
					float percent = (float) this.curentDistance / (float) this.initialDistance;
					if (percent > 0)
					{
						float gradient = 1f - percent;
						int newAlpha = (int) (255 * gradient);
						toolbar.getBackground().setAlpha(newAlpha);
					}

				}
				else
				{
					toolbar.getBackground().setAlpha(255);
				}
			}

		}
	}
}
