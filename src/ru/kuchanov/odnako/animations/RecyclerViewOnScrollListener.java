/*
 08.11.2014
RecyclerViewOnScrollListener.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.animations;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.fragments.FragmentArtsRecyclerList;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.widget.ImageView;

public class RecyclerViewOnScrollListener extends OnScrollListener
{
	ActionBarActivity act;

	FragmentArtsRecyclerList frag;

	int initialDistance = -100000;
	int curentDistance = -1;

	LinearLayoutManager manager;
	Toolbar toolbar;
	ImageView topImg;

	/**
	 * 
	 */
	public RecyclerViewOnScrollListener(ActionBarActivity act, FragmentArtsRecyclerList frag)
	{
		this.act = act;
		toolbar = (Toolbar) act.findViewById(R.id.toolbar);
		topImg = (ImageView) act.findViewById(R.id.top_img);

		this.frag = frag;
	}

	public void onScrollStateChanged(RecyclerView recyclerView, int newState)
	{
		manager = (LinearLayoutManager) recyclerView.getLayoutManager();
		toolbar = (Toolbar) act.findViewById(R.id.toolbar);
		topImg = (ImageView) act.findViewById(R.id.top_img);

		switch (newState)
		{
			case (RecyclerView.SCROLL_STATE_DRAGGING):
//				System.out.println("dragging");
				//mesuring initialDistance between actionBar and 1-st item
				if (initialDistance == -100000)
				{
					if (manager.findFirstVisibleItemPosition() == 0)
					{
						initialDistance = (int) (manager.findViewByPosition(1).getY() - toolbar.getHeight());
					}
				}
			break;
			//scroll finished
			case (RecyclerView.SCROLL_STATE_IDLE):
//				System.out.println("SCROLL_STATE_IDLE");
				if (topImg.getY() > 0)
				{
					topImg.setY(0);
				}
				if (manager.findFirstVisibleItemPosition() == 0)
				{
					if (manager.findViewByPosition(0).getY() == 0)
					{
						topImg.setY(0);
					}
				}
				//save position to frag
				this.frag.setTopImgYCoord((int) this.topImg.getY());
				this.frag.setToolbarYCoord((int) this.toolbar.getY());
				this.frag.setInitialDistance(this.initialDistance);
				//save coord to ActivityMain
				ActivityMain actM = (ActivityMain) act;
				actM.updateAllCatToolbarTopImgYCoord(frag.getCategoryToLoad(),
				new int[] { (int) toolbar.getY(), (int) topImg.getY(), initialDistance, curentDistance });
			break;
			case (RecyclerView.SCROLL_STATE_SETTLING):
//				System.out.println("SCROLL_STATE_SETTLING");
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
			if (manager.findFirstVisibleItemPosition() <= 1)
			{
				if (topImg.getY() < 0)
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
			//on the very top of list
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
			//from any other position
			else
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
			//UNlight actionBar UP
			//do it only while it's not moved
			//and we are on the top of our list
			//					System.out.println("firstVisPos ==0: "+String.valueOf(manager.findFirstVisibleItemPosition()==0));
			if (toolbar.getY() == 0)// && manager.findFirstVisibleItemPosition()==0)
			{
				if (manager.findFirstVisibleItemPosition() == 0)
				{
					curentDistance = (int) (manager.findViewByPosition(1).getY() - toolbar.getHeight());
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
			if (toolbar.getY() < 0)
			{
				toolbar.setY(toolbar.getY() - y);
			}
			else
			{
				toolbar.setY(0);
			}

			//light actionBar
			if (toolbar.getY() == 0)// && manager.findFirstVisibleItemPosition()==0)
			{
				if (manager.findFirstVisibleItemPosition() == 0)
				{
					curentDistance = (int) (manager.findViewByPosition(1).getY() - toolbar.getHeight());
					float percent = (float) this.curentDistance / (float) this.initialDistance;
					if (percent > 0)
					{
						float gradient = 1f - percent;
						int newAlpha = (int) (255 * gradient);
						toolbar.getBackground().setAlpha(newAlpha);
					}
				}
				else
				// if(toolbar.getBackground().getAlpha()<1)
				{
					toolbar.getBackground().setAlpha(255);
				}
			}

		}
	}
}
