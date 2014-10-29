/*
 19.10.2014
ActivityMain.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import com.google.android.gms.ads.AdView;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.fragments.ArticlesListFragment;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.ArticleViewPagerAdapter;
import ru.kuchanov.odnako.lists_and_utils.ArtsListAdapter;
import ru.kuchanov.odnako.lists_and_utils.ZoomOutPageTransformer;
import ru.kuchanov.odnako.utils.AddAds;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class ActivityMain extends ActionBarActivity
{
	//	public static final String EXTRA_MESSAGE_FROM_MAIN_TO_ARTICLE_CUR_ART_INFO = "extra_message_from_main_to_article_cur_art_info";
	//	public static final String EXTRA_MESSAGE_FROM_MAIN_TO_ARTICLE_POSITION = "extra_message_from_main_to_article_position";

	public boolean twoPane;
	SharedPreferences pref;
	AdView adView;

	ViewPager pager;
	PagerAdapter pagerAdapter;

	private ArrayList<ArtInfo> allArtsInfo;
	private ArtInfo curArtInfo;
	int position;

	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ActivityMain onCreate");
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.layout_activity_main);

		//get default settings to get all settings later
		PreferenceManager.setDefaultValues(this, R.xml.pref, true);
		//end of get default settings to get all settings later

		//Set unreaded num of arts to zero
		//it's for new arts motification
		SharedPreferences prefsNumOfArts = this.getSharedPreferences("saveNumOfUnReadedArts", 0);
		SharedPreferences.Editor editor = prefsNumOfArts.edit();
		editor.putInt("quontityOfUnreadedArts", 0);
		editor.commit();
		//end of Set unreaded num of arts to zero

		//check if there is two fragments. If so, set flag (twoPane) to true
		if (findViewById(R.id.article_comments_container) != null)
		{
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			twoPane = true;

			//save it to pref, to be able to read it without calling Activity
			this.pref = PreferenceManager.getDefaultSharedPreferences(this);
			this.pref.edit().putBoolean("twoPane", twoPane).commit();

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((ArticlesListFragment) getSupportFragmentManager().findFragmentById(R.id.articles_list))
			.setActivateOnItemClick(true);
			//
			//
			this.pager = (ViewPager) this.findViewById(R.id.article_comments_container);

			//get position from listFrag
			this.position = ((ArticlesListFragment) getSupportFragmentManager().findFragmentById(R.id.articles_list))
			.getMyActivatedPosition();
			if(this.position==ListView.INVALID_POSITION)
			{
				this.position=0;
			}
			((ArticlesListFragment) getSupportFragmentManager().findFragmentById(R.id.articles_list))
			.setActivatedPosition(position);
			this.pagerAdapter = new ArticleViewPagerAdapter(this.getSupportFragmentManager(), allArtsInfo, this);
			this.pager.setAdapter(pagerAdapter);
			this.pager.setPageTransformer(true, new ZoomOutPageTransformer());
			this.pager.setCurrentItem(position, true);
			this.pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
			{
				@Override
				public void onPageSelected(int position)
				{
					ArticlesListFragment artsListFrag = (ArticlesListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.articles_list);
					//setActivPosition to curItem of pager
					artsListFrag.setActivatedPosition(position);
					artsListFrag.scrollToActivatedPosition();
				}
			});
		}

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
		System.out.println("ActivityMain onResume");
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		System.out.println("ActivityMain: onSaveInstanceState");

		//save allArtsInfo
		ArtInfo.writeAllArtsInfoToBundle(outState, allArtsInfo, curArtInfo);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		System.out.println("ActivityMain onRestoreInstanceState");

		//restore All_arts_info
		Set<String> keySet = savedInstanceState.keySet();
		ArrayList<String> keySetSortedArrList = new ArrayList<String>(keySet);
		Collections.sort(keySetSortedArrList);
		if (keySet.contains("allArtsInfo_00"))
		{
			this.allArtsInfo = new ArrayList<ArtInfo>();
			for (int i = 0; i < keySetSortedArrList.size(); i++)
			{
				String s = keySetSortedArrList.get(i);
				if (s.startsWith("allArtsInfo_"))
				{
					if (i < 10)
					{
						this.allArtsInfo.add(new ArtInfo(savedInstanceState.getStringArray("allArtsInfo_0"
						+ String.valueOf(i))));
					}
					else
					{
						this.allArtsInfo.add(new ArtInfo(savedInstanceState.getStringArray("allArtsInfo_"
						+ String.valueOf(i))));
					}
				}
			}
		}
		else
		{
			System.out.println("ActivityMain: onRestoreInstanceState. allArtsInfo=null");
		}

		//restore curArtInfo
		if (keySet.contains("curArtInfo"))
		{
			this.curArtInfo = new ArtInfo(savedInstanceState.getStringArray("curArtInfo"));
		}
		else
		{
			System.out.println("ActivityMain: onRestoreInstanceState. curArtInfo=null");
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		this.pref=PreferenceManager.getDefaultSharedPreferences(this);
		if (pref.getString("theme", "dark").equals("dark"))
		{
			getMenuInflater().inflate(R.menu.main_dark, menu);
		}
		else
		{
			getMenuInflater().inflate(R.menu.main_light, menu);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		ArticlesListFragment artsListFrag = (ArticlesListFragment) this.getSupportFragmentManager().findFragmentById(
		R.id.articles_list);
		ArtsListAdapter adapter=artsListFrag.getArtsListAdapter();
		
		SharedPreferences prefArtsList = PreferenceManager.getDefaultSharedPreferences(this);
		String curArtsListSize = prefArtsList.getString("scale", "1");
//		System.out.println(curArtsListSize);
		
//		SharedPreferences prefForScale = PreferenceManager.getDefaultSharedPreferences(this);
		switch (item.getItemId())
		{
			case R.id.refresh:
				System.out.println("refresh");
				// TODO
				return true;
			case R.id.action_settings:
				item.setIntent(new Intent(this, ActivityPreference.class));
				return super.onOptionsItemSelected(item);
			case R.id.theme:
//				MenuItem ligthThemeMenuItem = item.getSubMenu().findItem(R.id.theme_ligth);
//				MenuItem darkThemeMenuItem = item.getSubMenu().findItem(R.id.theme_dark);
//				String curTheme = pref.getString("theme", "dark");
//				System.out.println(curTheme);
//				if (!curTheme.equals("dark"))
//				{
//					ligthThemeMenuItem.setChecked(true);
//				}
//				else
//				{
//					darkThemeMenuItem.setChecked(true);
//				}
				return true;
			case R.id.theme_ligth:
				this.pref.edit().putString("theme", "ligth").commit();
				System.out.println("theme_ligth");
				this.myRecreate();
				return true;
			case R.id.theme_dark:
				System.out.println("theme_dark");
				this.pref.edit().putString("theme", "dark").commit();
				
				this.myRecreate();
				return true;
			case R.id.arts_list_size:
				MenuItem artsListItem = item.getSubMenu().findItem(R.id.artslist_05);
				MenuItem artsListaItem = item.getSubMenu().findItem(R.id.artslist_075);
				MenuItem artsListbItem = item.getSubMenu().findItem(R.id.artslist_1);
				MenuItem artsListcItem = item.getSubMenu().findItem(R.id.artslist_125);
				MenuItem artsListdItem = item.getSubMenu().findItem(R.id.artslist_15);
				MenuItem artsListeItem = item.getSubMenu().findItem(R.id.artslist_175);
				MenuItem artsListgItem = item.getSubMenu().findItem(R.id.artslist_2);

				
				if (curArtsListSize.equals("0.5"))
				{
					artsListItem.setChecked(true);
				}
				else if (curArtsListSize.equals("0.75"))
				{
					artsListaItem.setChecked(true);
				}
				else if (curArtsListSize.equals("1"))
				{
					artsListbItem.setChecked(true);
				}
				else if (curArtsListSize.equals("1.25"))
				{
					artsListcItem.setChecked(true);
				}
				else if (curArtsListSize.equals("1.5"))
				{
					artsListdItem.setChecked(true);
				}
				else if (curArtsListSize.equals("1.75"))
				{
					artsListeItem.setChecked(true);
				}
				else if (curArtsListSize.equals("2"))
				{
					artsListgItem.setChecked(true);
				}
				return true;
			case R.id.artslist_05:
				prefArtsList.edit().putString("scale", "0.5").commit();
				try	{adapter.notifyDataSetChanged();}
				catch(Exception e){};
				return true;
			case R.id.artslist_075:
				prefArtsList.edit().putString("scale", "0.75").commit();
				try	{adapter.notifyDataSetChanged();}
				catch(Exception e){};
				return true;
			case R.id.artslist_1:
				prefArtsList.edit().putString("scale", "1").commit();
				try	{adapter.notifyDataSetChanged();}
				catch(Exception e){};
				return true;
			case R.id.artslist_125:
				prefArtsList.edit().putString("scale", "1.25").commit();
				try	{adapter.notifyDataSetChanged();}
				catch(Exception e){};
				return true;
			case R.id.artslist_15:
				prefArtsList.edit().putString("scale", "1.5").commit();
				try	{adapter.notifyDataSetChanged();}
				catch(Exception e){};
				return true;
			case R.id.artslist_175:
				prefArtsList.edit().putString("scale", "1.75").commit();
				try	{adapter.notifyDataSetChanged();}
				catch(Exception e){};
				return true;
			case R.id.artslist_2:
				prefArtsList.edit().putString("scale", "2").commit();
				try	{adapter.notifyDataSetChanged();}
				catch(Exception e){};
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@SuppressLint("NewApi")
	protected void myRecreate()
	{
		if (android.os.Build.VERSION.SDK_INT >= 11)
		{
			super.recreate();
		}
		else
		{
			finish();
			startActivity(getIntent());
		}
	}

	/**
	 * @return the curArtInfo
	 */
	public ArrayList<ArtInfo> getAllArtsInfo()
	{
		return this.allArtsInfo;
	}

	/**
	 * @param curArtInfo
	 *            the curArtInfo to set
	 */
	public void setAllArtsInfo(ArrayList<ArtInfo> allArtsInfo)
	{
		this.allArtsInfo = allArtsInfo;
	}

	/**
	 * @return the cUR_ARTS_INFO
	 */
	public ArtInfo getCUR_ART_INFO()
	{
		return curArtInfo;
	}

	/**
	 * @param cUR_ARTS_INFO
	 *            the cUR_ARTS_INFO to set
	 */
	public void setCUR_ART_INFO(ArtInfo cUR_ARTS_INFO)
	{
		this.curArtInfo = cUR_ARTS_INFO;
	}
}
