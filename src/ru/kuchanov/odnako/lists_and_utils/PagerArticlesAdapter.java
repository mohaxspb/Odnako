/*
 27.10.2014
ViewPagerAdapter.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.db.Msg;
import ru.kuchanov.odnako.fragments.FragArtUPD;
import ru.kuchanov.odnako.fragments.FragmentArticle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

public class PagerArticlesAdapter extends FragmentStatePagerAdapter
{
	static String LOG_TAG = PagerArticlesAdapter.class.getSimpleName() + "/";
	
	ArrayList<ArtInfo> allArtsInfo;

	String category;

	ActionBarActivity act;

	public PagerArticlesAdapter(FragmentManager fm, String category, ActionBarActivity act)
	{
		super(fm);
		Log.i(LOG_TAG + category, "ArticlesPagerAdapter CONSTRUCTOR called");
		this.category = category;
		this.act = act;

		this.allArtsInfo = ((ActivityBase) act).getAllCatArtsInfo().get(category);

		LocalBroadcastManager.getInstance(this.act).registerReceiver(artsDataReceiver, new IntentFilter(category));
	}

	private BroadcastReceiver artsDataReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			//			Log.i(LOG_TAG + category, "artsDataReceiver onReceive called");
			//
			//			String[] msg = intent.getStringArrayExtra(Msg.MSG);
			//			int page = intent.getIntExtra("pageToLoad", 1);
			//			ArrayList<ArtInfo> newAllArtsInfo;
			//
			//			switch (msg[0])
			//			{
			//				case (Msg.NO_NEW):
			//					Toast.makeText(act, "Новых статей не обнаружено!", Toast.LENGTH_SHORT).show();
			//					newAllArtsInfo = intent.getParcelableArrayListExtra(ArtInfo.KEY_ALL_ART_INFO);
			//
			//					if (newAllArtsInfo != null)
			//					{
			//						if (page == 1)
			//						{
			//							if(allArtsInfo==null)
			//							{
			//								allArtsInfo=new ArrayList<ArtInfo>();
			//							}
			//							allArtsInfo.clear();
			//						}
			//						allArtsInfo.addAll(newAllArtsInfo);
			//						notifyDataSetChanged();
			//
			//						//TODO think how realise loading from bootom in ViewPager
			//						//((ActivityBase) act).updateAllCatArtsInfo(categoryToLoad, allArtsInfo);
			//					}
			//					else
			//					{
			//						System.out.println("ArrayList<ArtInfo> someResult=NULL!!!");
			//					}
			//				break;
			//				case (Msg.NEW_QUONT):
			//					Toast.makeText(act, "Обнаружено " + msg[1] + " новых статей", Toast.LENGTH_SHORT).show();
			//
			//					newAllArtsInfo = intent.getParcelableArrayListExtra(ArtInfo.KEY_ALL_ART_INFO);
			//
			//					if (newAllArtsInfo != null)
			//					{
			//						if (page == 1)
			//						{
			//							if(allArtsInfo==null)
			//							{
			//								allArtsInfo=new ArrayList<ArtInfo>();
			//							}
			//							allArtsInfo.clear();
			//						}
			//						allArtsInfo.addAll(newAllArtsInfo);
			//						notifyDataSetChanged();
			//
			//						//TODO think how realise loading from bootom in ViewPager
			//						//((ActivityBase) act).updateAllCatArtsInfo(categoryToLoad, allArtsInfo);
			//					}
			//					else
			//					{
			//						System.out.println("ArrayList<ArtInfo> someResult=NULL!!!");
			//					}
			//				break;
			//				case (Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT):
			//
			//					newAllArtsInfo = intent.getParcelableArrayListExtra(ArtInfo.KEY_ALL_ART_INFO);
			//
			//					if (newAllArtsInfo != null)
			//					{
			//						if (page == 1)
			//						{
			//							if(allArtsInfo==null)
			//							{
			//								allArtsInfo=new ArrayList<ArtInfo>();
			//							}
			//							allArtsInfo.clear();
			//						}
			//						allArtsInfo.addAll(newAllArtsInfo);
			//						notifyDataSetChanged();
			//
			//						//TODO think how realise loading from bootom in ViewPager
			//						//((ActivityBase) act).updateAllCatArtsInfo(categoryToLoad, allArtsInfo);
			//					}
			//					else
			//					{
			//						System.out.println("ArrayList<ArtInfo> someResult=NULL!!!");
			//					}
			//				break;
			//				case (Msg.ERROR):
			//					Toast.makeText(act, msg[1], Toast.LENGTH_SHORT).show();
			//					//check if there was error while loading from bottom, if so, decrement pageToLoad
			//					if (page != 1)
			//					{
			//						//TODO think how realise loading from bootom in ViewPager
			//						page--;
			//					}
			//				break;
			//			}

			Log.i(LOG_TAG + category, "artsDataReceiver onReceive called");

			//get result message
			String[] msg = intent.getStringArrayExtra(Msg.MSG);
			int page = intent.getIntExtra("pageToLoad", 1);

			switch (msg[0])
			{
				case (Msg.NO_NEW):
					Log.d(LOG_TAG + "/" + category, "Новых статей не обнаружено!");
					Toast.makeText(act, "Новых статей не обнаружено!", Toast.LENGTH_SHORT).show();
					updateAdapter(intent, page);
				break;
				case (Msg.NEW_QUONT):
					Log.d(LOG_TAG + "/" + category, "Обнаружено " + msg[1] + " новых статей");
					Toast.makeText(act, "Обнаружено " + msg[1] + " новых статей", Toast.LENGTH_SHORT).show();
					updateAdapter(intent, page);
				break;
				case (Msg.DB_ANSWER_WRITE_FROM_TOP_NO_MATCHES):
					Log.d(LOG_TAG + "/" + category, "Обнаружено " + msg[1] + " новых статей");
					Toast.makeText(act, "Обнаружено более 30 новых статей", Toast.LENGTH_SHORT).show();
					updateAdapter(intent, page);
				break;
				case (Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT):
					updateAdapter(intent, page);
				break;
				case (Msg.DB_ANSWER_WRITE_FROM_BOTTOM_EXCEPTION):
					//we catch publishing lag from bottom, so we'll toast unsinked status
					//and start download from top (pageToLoad=1)
					Toast.makeText(act, "Синхронизирую базу данных. Загружаю новые статьи", Toast.LENGTH_SHORT).show();
					page = 1;
					//TODO check it
					allArtsInfo=null;
					notifyDataSetChanged();
//					getAllArtsInfo(true);
				break;
				case (Msg.DB_ANSWER_NO_ARTS_IN_CATEGORY):
					Toast.makeText(act, "Ни одной статьи не обнаружено!", Toast.LENGTH_SHORT).show();
					updateAdapter(intent, page);
				break;
				case (Msg.ERROR):
					Toast.makeText(act, msg[1], Toast.LENGTH_SHORT).show();
					//check if there was error while loading from bottom, if so, decrement pageToLoad
					if (page != 1)
					{
						page--;
						//setOnScrollListener();
					}
				break;
				default:
					Log.e(LOG_TAG, "непредвиденный ответ базы данных");
					Toast.makeText(act, "непредвиденный ответ базы данных", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private void updateAdapter(Intent intent, int page)
	{
		ArrayList<ArtInfo> newAllArtsInfo;
		newAllArtsInfo = intent.getParcelableArrayListExtra(ArtInfo.KEY_ALL_ART_INFO);

		if (newAllArtsInfo != null)
		{
			if (this.allArtsInfo == null)
			{
				this.allArtsInfo = new ArrayList<ArtInfo>();
			}
			if (page == 1)
			{
				allArtsInfo.clear();
			}
			allArtsInfo.addAll(newAllArtsInfo);
			notifyDataSetChanged();

//			((ActivityBase) act).updateAllCatArtsInfo(category, allArtsInfo);
		}
		else
		{
			System.out.println("ArrayList<ArtInfo> someResult=NULL!!!");
		}
	}

	@Override
	public Fragment getItem(int position)
	{
		FragmentArticle artFrag = new FragmentArticle();
		Bundle b = new Bundle();
		if (this.allArtsInfo == null)
		{
			b.putParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO, null);
			b.putParcelable(ArtInfo.KEY_CURENT_ART, null);

			ArrayList<ArtInfo> def = new ArrayList<ArtInfo>();
			def.add(new ArtInfo("empty", "Статьи загружаются, подождите пожалуйста", "empty", "empty", "empty"));
			this.allArtsInfo = def;
		}
		else
		{
			b.putParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO, this.allArtsInfo);
			b.putParcelable(ArtInfo.KEY_CURENT_ART, this.allArtsInfo.get(position));
		}

		b.putInt("position", position);
		artFrag.setArguments(b);

		return artFrag;
	}

	@Override
	public int getCount()
	{
		if (this.allArtsInfo == null)
		{
			return 1;
		}
		else
		{
			return this.allArtsInfo.size();
		}
	}

	@Override
	public int getItemPosition(Object object)
	{
		if (object instanceof FragArtUPD)
		{
			if (((Fragment) object).isAdded())
			{
				((FragArtUPD) object).update(this.allArtsInfo);
			}
			else
			{
				Log.e(LOG_TAG, "Fragment not added");
			}
		}
		//don't return POSITION_NONE, avoid fragment recreation. 
		return super.getItemPosition(object);
	}
}
