/*
 21.02.2015
ArtsDataReciever.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;

public class ArtsDataReceiver extends BroadcastReceiver
{
	static String LOG = ArtsDataReceiver.class.getSimpleName() + "/";

	String category;

	PagerArticlesAdapter pagerArtsAdapter;

	ActionBarActivity act;

	public ArtsDataReceiver(String category, PagerArticlesAdapter pagerArtsAdapter, ActionBarActivity act)
	{
		this.category = category;
		this.act = act;
		this.pagerArtsAdapter = pagerArtsAdapter;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
//		//check action to prevent bug...
//		if (intent.getAction().equals(category))
//		{
//			Log.i(LOG + category, "artsDataReceiver onReceive called");
//		}
//		else
//		{
//			Log.e(LOG + category, "artsDataReceiver onReceive with action: " + intent.getAction());
//			return;
//		}
//
//		//get result message
//		String[] msg = intent.getStringArrayExtra(Msg.MSG);
//		int page = intent.getIntExtra("pageToLoad", 1);
//
//		Log.i(LOG + category, "msg[0]: " + msg[0]);
//		Log.i(LOG + category,
//		String.valueOf(intent.getParcelableArrayListExtra(ArtInfo.KEY_ALL_ART_INFO).size()));
//
//		ViewPager pager;
//		switch (msg[0])
//		{
//			case (Msg.NO_NEW):
//				Log.d(LOG + "/" + category, "Новых статей не обнаружено!");
//				pagerArtsAdapter.updateAdapter(intent, page);
//				if (act instanceof ActivityArticle)
//				{
//					Toast.makeText(act, "Новых статей не обнаружено!", Toast.LENGTH_SHORT).show();
//
//					//this is ActivityArticle
//					ActivityArticle artActivity = (ActivityArticle) act;
//					//set toolbar title as Article x/y, where x is curentArt and y is allArtsSize()
//					Toolbar rightToolbar = (Toolbar) artActivity.findViewById(R.id.toolbar);
//					int selectedArt = artActivity.getCurArtPosition() + 1;
//					int allArtsSize = pagerArtsAdapter.getCount();
//					rightToolbar.setTitle("Статья " + selectedArt + "/" + allArtsSize);
//				}
//				else
//				{
//					//this is ActivityMain
//					ActivityMain mainActivity = (ActivityMain) act;
//					//set toolbar title as Article x/y, where x is curentArt and y is allArtsSize()
//					Toolbar rightToolbar = (Toolbar) mainActivity.findViewById(R.id.toolbar_right);
//					int selectedArt = mainActivity.getAllCatListsSelectedArtPosition().get(category) + 1;
//					int allArtsSize = pagerArtsAdapter.getCount();
//					rightToolbar.setTitle("Статья " + selectedArt + "/" + allArtsSize);
//				}
//			break;
//			case (Msg.NEW_QUONT):
//				Log.d(LOG + "/" + category, "Обнаружено " + msg[1] + " новых статей");
//
//				((ActivityMain) act).getAllCatListsSelectedArtPosition().put(category, 0);
//
//				pagerArtsAdapter.updateAdapter(intent, page);
//
//				if (act instanceof ActivityMain)
//				{
//					pager = (ViewPager) act.findViewById(R.id.pager_right);
//
//					//this is ActivityMain
//					ActivityMain mainActivity = (ActivityMain) act;
//					//set toolbar title as Article x/y, where x is curentArt and y is allArtsSize()
//					Toolbar rightToolbar = (Toolbar) mainActivity.findViewById(R.id.toolbar_right);
//					int selectedArt = mainActivity.getAllCatListsSelectedArtPosition().get(category) + 1;
//					int allArtsSize = pagerArtsAdapter.getCount();
//					rightToolbar.setTitle("Статья " + selectedArt + "/" + allArtsSize);
//				}
//				else
//				{
//					//TODO
//					pager = (ViewPager) act.findViewById(R.id.article_container);
//					Toast.makeText(act, "Обнаружено " + msg[1] + " новых статей", Toast.LENGTH_SHORT).show();
//				}
//				pager.setCurrentItem(0);
//			break;
//			case (Msg.DB_ANSWER_WRITE_FROM_TOP_NO_MATCHES):
//				Log.d(LOG + "/" + category, "Обнаружено " + msg[1] + " новых статей");
//
//				((ActivityMain) act).getAllCatListsSelectedArtPosition().put(category, 0);
//
//				pagerArtsAdapter.updateAdapter(intent, page);
//
//				if (act instanceof ActivityMain)
//				{
//					pager = (ViewPager) act.findViewById(R.id.pager_right);
//					//this is ActivityMain
//					ActivityMain mainActivity = (ActivityMain) act;
//					//set toolbar title as Article x/y, where x is curentArt and y is allArtsSize()
//					Toolbar rightToolbar = (Toolbar) mainActivity.findViewById(R.id.toolbar_right);
//					int selectedArt = mainActivity.getAllCatListsSelectedArtPosition().get(category) + 1;
//					int allArtsSize = pagerArtsAdapter.getCount();
//					rightToolbar.setTitle("Статья " + selectedArt + "/" + allArtsSize);
//				}
//				else
//				{
//					pager = (ViewPager) act.findViewById(R.id.article_container);
//					Toast.makeText(act, "Обнаружено более 30 новых статей", Toast.LENGTH_SHORT).show();
//				}
//				pager.setCurrentItem(0);
//			break;
//			case (Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT):
//				pagerArtsAdapter.updateAdapter(intent, page);
//
//				if (act instanceof ActivityMain)
//				{
//					pager = (ViewPager) act.findViewById(R.id.pager_right);
//
//					//this is ActivityMain
//					ActivityMain mainActivity = (ActivityMain) act;
//					//set toolbar title as Article x/y, where x is curentArt and y is allArtsSize()
//					Toolbar rightToolbar = (Toolbar) mainActivity.findViewById(R.id.toolbar_right);
//					int selectedArt = mainActivity.getAllCatListsSelectedArtPosition().get(category) + 1;
//					int allArtsSize = pagerArtsAdapter.getCount();
//					rightToolbar.setTitle("Статья " + selectedArt + "/" + allArtsSize);
//				}
//				else
//				{
//					//TODO
//					pager = (ViewPager) act.findViewById(R.id.article_container);
//					Toast.makeText(act, "Обнаружено " + msg[1] + " новых статей", Toast.LENGTH_SHORT).show();
//				}
//			break;
//			case (Msg.DB_ANSWER_WRITE_FROM_BOTTOM_EXCEPTION):
//				//we catch publishing lag from bottom, so we'll toast unsinked status
//				//and start download from top (pageToLoad=1)
//
//				page = 1;
//				//TODO check it
//				pagerArtsAdapter.allArtsInfo = null;
//
//				((ActivityMain) act).getAllCatListsSelectedArtPosition().put(category, 0);
//
//				pagerArtsAdapter.notifyDataSetChanged();
//				if (act instanceof ActivityMain)
//				{
//					pager = (ViewPager) act.findViewById(R.id.pager_right);
//					//this is ActivityMain
//					ActivityMain mainActivity = (ActivityMain) act;
//					//set toolbar title as Article x/y, where x is curentArt and y is allArtsSize()
//					Toolbar rightToolbar = (Toolbar) mainActivity.findViewById(R.id.toolbar_right);
//					int selectedArt = mainActivity.getAllCatListsSelectedArtPosition().get(category) + 1;
//					int allArtsSize = pagerArtsAdapter.getCount();
//					rightToolbar.setTitle("Статья " + selectedArt + "/" + allArtsSize);
//				}
//				else
//				{
//					pager = (ViewPager) act.findViewById(R.id.article_container);
//					Toast.makeText(act, "Синхронизирую базу данных. Загружаю новые статьи", Toast.LENGTH_SHORT)
//					.show();
//				}
//				pager.setCurrentItem(0);
//
//			//					getAllArtsInfo(true);
//			break;
//			case (Msg.DB_ANSWER_NO_ARTS_IN_CATEGORY):
//				Toast.makeText(act, "Ни одной статьи не обнаружено!", Toast.LENGTH_SHORT).show();
//				pagerArtsAdapter.updateAdapter(intent, page);
//			break;
//			case (Msg.ERROR):
//				Toast.makeText(act, msg[1], Toast.LENGTH_SHORT).show();
//				//check if there was error while loading from bottom, if so, decrement pageToLoad
//				if (page != 1)
//				{
//					page--;
//					//setOnScrollListener();
//				}
//			break;
//			default:
//				Log.e(LOG, "непредвиденный ответ базы данных");
//				Toast.makeText(act, "непредвиденный ответ базы данных", Toast.LENGTH_SHORT).show();
//		}
	}
}
