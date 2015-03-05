/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.animations.RecyclerViewOnScrollListener;
import ru.kuchanov.odnako.db.Msg;
import ru.kuchanov.odnako.db.ServiceDB;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.ArtsListAdapter;
import ru.kuchanov.odnako.lists_and_utils.CatData;
import ru.kuchanov.odnako.lists_and_utils.PagerAdapterAllAuthors;
import ru.kuchanov.odnako.lists_and_utils.PagerListenerArticle;
import ru.kuchanov.odnako.utils.MyUniversalImageLoader;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Fragment for artsList. We use it as main Fragment for menu categories instead
 * of allAuthors and -Categories
 */
public class FragmentArtsListRecycler extends Fragment
{
	private static String LOG = FragmentArtsListRecycler.class.getSimpleName() + "/";

	private int pageToLoad = 1;

	private SwipeRefreshLayout swipeRef;

	/**
	 * art's list top image
	 */
	private ImageView topImg;
	private float topImgCoord;

	private int toolbarId = R.id.toolbar;
	private boolean isInLeftPager = true;

	private RecyclerView artsList;
	private ArtsListAdapter artsListAdapter;

	private ActivityMain act;
	private SharedPreferences pref;

	private String categoryToLoad;
	private ArrayList<ArtInfo> allArtsInfo;
	//TODO check if we need it
	//	private ArtInfo curArtInfo;
	private int position = 0;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		//Log.d(LOG_TAG+categoryToLoad, "onCreate");
		super.onCreate(savedInstanceState);

		this.act = (ActivityMain) this.getActivity();
		this.pref = PreferenceManager.getDefaultSharedPreferences(act);

		Bundle fromArgs = this.getArguments();
		if (fromArgs != null)
		{
			this.setCategoryToLoad(fromArgs.getString("categoryToLoad"));
		}

		//restore topImg and toolbar prop's
		if (savedInstanceState != null)
		{
			this.topImgCoord = savedInstanceState.getFloat("topImgYCoord");

			this.pageToLoad = savedInstanceState.getInt("pageToLoad");

			this.categoryToLoad = savedInstanceState.getString("categoryToLoad");
			//			this.curArtInfo = savedInstanceState.getParcelable(ArtInfo.KEY_CURENT_ART);
			this.allArtsInfo = savedInstanceState.getParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO);
			this.position = savedInstanceState.getInt("position");
		}
		else
		{
			if (act.getAllCatListsSelectedArtPosition().containsKey(categoryToLoad))
			{
				this.position = act.getAllCatListsSelectedArtPosition().get(categoryToLoad);
			}
			else
			{
				this.position = 0;
				act.getAllCatListsSelectedArtPosition().put(categoryToLoad, this.position);
				act.getAllCatArtsInfo().put(categoryToLoad, null);
			}
		}

		LocalBroadcastManager.getInstance(this.act).registerReceiver(artsDataReceiver,
		new IntentFilter(this.getCategoryToLoad()));

		//reciver for scrolling and highligting selected position
		LocalBroadcastManager.getInstance(this.act).registerReceiver(artSelectedReceiver,
		new IntentFilter(this.getCategoryToLoad() + "art_position"));

		//reciver for scrolling and highligting selected position
		LocalBroadcastManager.getInstance(this.act).registerReceiver(categoryIsLoadingReceiver,
		new IntentFilter(this.getCategoryToLoad() + Const.Action.IS_LOADING));
	}

	private BroadcastReceiver categoryIsLoadingReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
//			Log.i(LOG + categoryToLoad, "catgoryIsLoadingReceiver onReceive called");
			boolean isCurrentlyLoading = intent.getBooleanExtra(Const.Action.IS_LOADING, false);
			if (isCurrentlyLoading)
			{
				if (pageToLoad == 1)
				{
					int[] textSizeAttr = new int[] { android.R.attr.actionBarSize };
					int indexOfAttrTextSize = 0;
					TypedValue typedValue = new TypedValue();
					TypedArray a = act.obtainStyledAttributes(typedValue.data, textSizeAttr);
					int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, 100);
					a.recycle();
					//			this.swipeRef.setProgressViewOffset(false, 0, actionBarSize);
					swipeRef.setProgressViewEndTarget(false, actionBarSize);
				}
				else
				{
					int[] textSizeAttr = new int[] { android.R.attr.textSize };
					int indexOfAttrTextSize = 0;
					TypedValue typedValue = new TypedValue();
					TypedArray a = act.obtainStyledAttributes(typedValue.data, textSizeAttr);
					int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, 100);
					a.recycle();
					DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
					int height = displayMetrics.heightPixels;
					swipeRef.setProgressViewOffset(false, 0, height - actionBarSize * 2);
					swipeRef.setProgressViewEndTarget(false, height - actionBarSize * 2);
				}
				swipeRef.setRefreshing(true);
			}
			else
			{
				swipeRef.setRefreshing(false);
			}
		}
	};

	private BroadcastReceiver artSelectedReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.i(LOG + categoryToLoad, "artSelectedReceiver onReceive called");
			position = intent.getIntExtra("position", 0);

			setActivatedPosition(position);
			//			topImg.setY(0 - topImg.getHeight());
			artsListAdapter.notifyDataSetChanged();
		}
	};

	private BroadcastReceiver artsDataReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.i(LOG + categoryToLoad, "artsDataReceiver onReceive called");

			if (!isAdded())
			{
				Log.e(LOG + categoryToLoad, "fragment not added! RETURN!");
				return;
			}

			//check if this fragment is currently displayed 
			boolean isDisplayed = false;
			int pagerType = act.getPagerType();
			int currentCategoryPosition = act.getCurentCategoryPosition();
			switch (pagerType)
			{
				case ActivityMain.PAGER_TYPE_MENU:
					String[] allMenuLinks = CatData.getMenuLinks(act);

					if (categoryToLoad.equals(allMenuLinks[currentCategoryPosition]) && isInLeftPager)
					{
						//so it's currently displayed fragment
						isDisplayed = true;
					}
					else if (!isInLeftPager)
					{
						if (currentCategoryPosition == 3)
						{
							ViewPager pagerRight = (ViewPager) act.findViewById(R.id.pager_right);
							PagerAdapterAllAuthors allAuthorsAdapter = (PagerAdapterAllAuthors) pagerRight
							.getAdapter();
							List<String> allAuthorsUrls = allAuthorsAdapter.getAllAuthorsURLsList();
							int selectedArtPosOfAllAuthorsFrag = act.getAllCatListsSelectedArtPosition().get(
							allMenuLinks[3]);
							if (categoryToLoad.equals(allAuthorsUrls.get(selectedArtPosOfAllAuthorsFrag)))
							{
								//so it's currently displayed fragment
								isDisplayed = true;
							}
							else
							{
								isDisplayed = false;
							}
						}
						else if (currentCategoryPosition == 13)
						{
							//TODO
						}
					}
					else
					{
						isDisplayed = false;
					}
				break;
				case ActivityMain.PAGER_TYPE_AUTHORS:
					ViewPager pagerLeft = (ViewPager) act.findViewById(R.id.pager_left);
					PagerAdapterAllAuthors allAuthorsAdapter = (PagerAdapterAllAuthors) pagerLeft.getAdapter();
					List<String> allAuthorsUrls = allAuthorsAdapter.getAllAuthorsURLsList();

					if (categoryToLoad.equals(allAuthorsUrls.get(currentCategoryPosition)) && isInLeftPager)
					{
						//so it's currently displayed fragment
						isDisplayed = true;
					}
					else
					{
						isDisplayed = false;
					}
				break;
				case ActivityMain.PAGER_TYPE_CATEGORIES:
				//TODO
				break;
				case ActivityMain.PAGER_TYPE_SINGLE:
					//it's the only one fragment, so isDisplayed is always true
					isDisplayed = true;
					//try setting title to toolbar
					ArrayList<ArtInfo> allArts = intent.getParcelableArrayListExtra(ArtInfo.KEY_ALL_ART_INFO);
					Toolbar toolbar = (Toolbar) act.findViewById(toolbarId);
					if (allArts != null)
					{
						if (allArts.get(0).authorName.equals("empty"))
						{
							toolbar.setTitle(categoryToLoad);
						}
						else
						{
							toolbar.setTitle(allArts.get(0).authorName);
						}
					}
					else
					{
						toolbar.setTitle(categoryToLoad);
					}
				break;
			}

			//get result message
			String[] msg = intent.getStringArrayExtra(Msg.MSG);
			int page = intent.getIntExtra("pageToLoad", 1);

			switch (msg[0])
			{
				case Msg.DB_ANSWER_INFO_SENDED_TO_FRAG:
					Log.d(LOG + categoryToLoad, "Msg.DB_ANSWER_INFO_SENDED_TO_FRAG");
					updateAdapter(intent, page);
				break;
				case (Msg.NO_NEW):
					Log.d(LOG + categoryToLoad, "Новых статей не обнаружено!");
					if (isDisplayed)
					{
						Toast.makeText(act, "Новых статей не обнаружено!", Toast.LENGTH_SHORT).show();
					}
					updateAdapter(intent, page);
				break;
				case (Msg.NEW_QUONT):
					Log.d(LOG + categoryToLoad, "Обнаружено " + msg[1] + " новых статей");
					//setPosition to zero to avoid bugs
					position = 0;
					((ActivityMain) act).getAllCatListsSelectedArtPosition().put(categoryToLoad, position);
					if (isDisplayed)
					{
						Toast.makeText(act, "Обнаружено " + msg[1] + " новых статей", Toast.LENGTH_SHORT).show();
					}
					updateAdapter(intent, page);
				break;
				case (Msg.DB_ANSWER_WRITE_FROM_TOP_NO_MATCHES):
					Log.d(LOG + categoryToLoad, "Обнаружено " + msg[1] + " новых статей");
					position = 0;
					((ActivityMain) act).getAllCatListsSelectedArtPosition().put(categoryToLoad, position);
					if (isDisplayed)
					{
						Toast.makeText(act, "Обнаружено более 30 новых статей", Toast.LENGTH_SHORT).show();
					}
					updateAdapter(intent, page);
				break;
				case (Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT):
					Log.d(LOG + categoryToLoad, "Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT");
					updateAdapter(intent, page);
				break;
				case (Msg.DB_ANSWER_WRITE_FROM_BOTTOM_EXCEPTION):
					//we catch publishing lag from bottom, so we'll toast unsinked status
					//and start download from top (pageToLoad=1)
					Log.d(LOG + "/" + categoryToLoad, "Синхронизирую базу данных. Загружаю новые статьи");
					if (isDisplayed)
					{
						Toast.makeText(act, "Синхронизирую базу данных. Загружаю новые статьи", Toast.LENGTH_SHORT)
						.show();
					}
					position = 0;
					((ActivityMain) act).getAllCatListsSelectedArtPosition().put(categoryToLoad, position);
					allArtsInfo = null;
					ArrayList<ArtInfo> def = new ArrayList<ArtInfo>();
					def
					.add(new ArtInfo("empty", "Статьи загружаются, подождите пожалуйста", "empty", "empty", "empty"));
					allArtsInfo = def;
					((ActivityMain) act).getAllCatArtsInfo().put(categoryToLoad, allArtsInfo);
					pageToLoad = 1;
					artsList.getAdapter().notifyDataSetChanged();
					getAllArtsInfo(true);
				break;
				case (Msg.DB_ANSWER_NO_ARTS_IN_CATEGORY):
					Log.e(LOG + categoryToLoad, "Ни одной статьи не обнаружено!");
					if (isDisplayed)
					{
						Toast.makeText(act, "Ни одной статьи не обнаружено!", Toast.LENGTH_SHORT).show();
					}
					position = 0;
					((ActivityMain) act).getAllCatListsSelectedArtPosition().put(categoryToLoad, position);
					updateAdapter(intent, page);
				break;
				case (Msg.ERROR):
					Log.e(LOG + categoryToLoad, msg[1]);
					if (isDisplayed)
					{
						Toast.makeText(act, msg[1], Toast.LENGTH_SHORT).show();
					}
					//check if there was error while loading from bottom, if so, decrement pageToLoad
					if (page != 1)
					{
						pageToLoad--;
					}
				break;
				default:
					Log.e(LOG + categoryToLoad, "непредвиденный ответ базы данных");
					if (isDisplayed)
					{
						Toast.makeText(act, "непредвиденный ответ базы данных", Toast.LENGTH_SHORT).show();
					}
				break;
			}

			setOnScrollListener();

			if (swipeRef.isRefreshing())
			{
				TypedValue typed_value = new TypedValue();
				act.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize,
				typed_value, true);
				swipeRef.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));

				swipeRef.setProgressViewEndTarget(false, getResources().getDimensionPixelSize(typed_value.resourceId));
				swipeRef.setRefreshing(false);
			}
			boolean refreshRightToolbarAndPager = isInLeftPager && pref.getBoolean("twoPane", false) && isDisplayed;
			if (refreshRightToolbarAndPager)
			{
				updateRightPagerAndToolbar(msg);
			}
		}//onRexeive
	};//artsDataReceiver

	private void updateAdapter(Intent intent, int page)
	{
		ArrayList<ArtInfo> newAllArtsInfo;
		newAllArtsInfo = intent.getParcelableArrayListExtra(ArtInfo.KEY_ALL_ART_INFO);

		if (newAllArtsInfo != null)
		{
			if (page == 1)
			{
				allArtsInfo.clear();
			}
			allArtsInfo.addAll(newAllArtsInfo);
			artsListAdapter.notifyDataSetChanged();

			((ActivityBase) act).updateAllCatArtsInfo(categoryToLoad, allArtsInfo);
		}
		else
		{
			System.out.println("ArrayList<ArtInfo> someResult=NULL!!!");
		}
	}

	private void updateRightPagerAndToolbar(String[] msg)
	{
		ActivityMain mainActivity = (ActivityMain) this.act;
		ViewPager pagerRight = (ViewPager) mainActivity.findViewById(R.id.pager_right);
		Toolbar rightToolbar = (Toolbar) mainActivity.findViewById(R.id.toolbar_right);
		int selectedArt;
		int allArtsSize;

		ViewPager.OnPageChangeListener listener = new PagerListenerArticle(mainActivity, categoryToLoad);

		switch (msg[0])
		{
			case Msg.DB_ANSWER_INFO_SENDED_TO_FRAG:
				pagerRight.getAdapter().notifyDataSetChanged();

				selectedArt = mainActivity.getAllCatListsSelectedArtPosition().get(this.categoryToLoad) + 1;
				allArtsSize = this.allArtsInfo.size();
				rightToolbar.setTitle("Статья " + selectedArt + "/" + allArtsSize);
			case (Msg.NO_NEW):
				pagerRight.getAdapter().notifyDataSetChanged();

				selectedArt = mainActivity.getAllCatListsSelectedArtPosition().get(this.categoryToLoad) + 1;
				allArtsSize = this.allArtsInfo.size();
				rightToolbar.setTitle("Статья " + selectedArt + "/" + allArtsSize);
			break;
			case (Msg.NEW_QUONT):
				pagerRight.getAdapter().notifyDataSetChanged();
				pagerRight.setOnPageChangeListener(listener);
				listener.onPageSelected(0);
			break;
			case (Msg.DB_ANSWER_WRITE_FROM_TOP_NO_MATCHES):
				pagerRight.getAdapter().notifyDataSetChanged();
				pagerRight.setOnPageChangeListener(listener);
				listener.onPageSelected(0);
			break;
			case (Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT):
				pagerRight.getAdapter().notifyDataSetChanged();

				selectedArt = mainActivity.getAllCatListsSelectedArtPosition().get(this.categoryToLoad) + 1;
				allArtsSize = this.allArtsInfo.size();
				rightToolbar.setTitle("Статья " + selectedArt + "/" + allArtsSize);
			break;
			case (Msg.DB_ANSWER_WRITE_FROM_BOTTOM_EXCEPTION):
				pagerRight.getAdapter().notifyDataSetChanged();

				selectedArt = mainActivity.getAllCatListsSelectedArtPosition().get(this.categoryToLoad) + 1;
				allArtsSize = this.allArtsInfo.size();
				rightToolbar.setTitle("Статья " + selectedArt + "/" + allArtsSize);
			break;
			case (Msg.DB_ANSWER_NO_ARTS_IN_CATEGORY):
				pagerRight.getAdapter().notifyDataSetChanged();
			break;
			case (Msg.ERROR):
			//nothing to do;
			break;
			default:
			//nothing to do;
			break;
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//Log.d(LOG_TAG, "onCreateView");
		View v = inflater.inflate(R.layout.fragment_arts_list, container, false);

		//find cur frag's toolbar
		if (container.getId() == R.id.pager_right)
		{
			this.toolbarId = R.id.toolbar_right;
			//unregister from articles selected events to not highlight items
			if (artSelectedReceiver != null)
			{
				LocalBroadcastManager.getInstance(act).unregisterReceiver(artSelectedReceiver);
				artSelectedReceiver = null;
			}
			this.setInLeftPager(false);
		}
		else if (container.getId() == R.id.pager_left)
		{
			this.toolbarId = R.id.toolbar;
			this.setInLeftPager(true);
		}
		this.topImg = (ImageView) v.findViewById(R.id.top_img);

		String defPackage = act.getPackageName();
		String[] catImgsFilesNames = act.getResources().getStringArray(R.array.categories_imgs_files_names);
		String[] categoriesUrls = act.getResources().getStringArray(R.array.categories_links);

		for (int i = 0; i < categoriesUrls.length; i++)
		{
			if (this.categoryToLoad.equals(categoriesUrls[i]))
			{
				String fullResName = catImgsFilesNames[i];
				String resName = fullResName.substring(0, fullResName.length() - 4);
				int resId = act.getResources().getIdentifier(resName, "drawable", defPackage);
				ImageLoader imgLoader = MyUniversalImageLoader.get(act);
				imgLoader.displayImage("drawable://" + resId, topImg,
				MyUniversalImageLoader.getTransparentBackgroundOptions(this.act));
				break;
			}
		}
		this.topImg.setY(topImgCoord);

		this.swipeRef = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);
		//workaround to fix issue with not showing refreshing indicator before swipeRef.onMesure() was called
		//as I understand before onResume of Activity

		TypedValue typed_value = new TypedValue();
		getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
		this.swipeRef.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));

		this.swipeRef.setProgressViewEndTarget(false, getResources().getDimensionPixelSize(typed_value.resourceId));

		this.swipeRef.setColorSchemeResources(R.color.material_red_300,
		R.color.material_red_500,
		R.color.material_red_500,
		R.color.material_red_500);
		////set on swipe listener
		this.swipeRef.setOnRefreshListener(new OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				pageToLoad = 1;
				getAllArtsInfo(true);
			}
		});

		this.artsList = (RecyclerView) v.findViewById(R.id.arts_list_view);
		this.artsList.setItemAnimator(new DefaultItemAnimator());
		this.artsList.setLayoutManager(new LinearLayoutManager(act));

		//restore allArtsInfo from Activities HashMap
		if (this.allArtsInfo == null && this.act.getAllCatArtsInfo().get(this.getCategoryToLoad()) != null)
		{
			this.allArtsInfo = this.act.getAllCatArtsInfo().get(this.getCategoryToLoad());
		}

		if (this.allArtsInfo == null
		|| this.allArtsInfo.get(0).title.equals("Статьи загружаются, подождите пожалуйста"))
		{
			//Log.i(categoryToLoad, "this.allArtsInfo=NULL");
			this.getAllArtsInfo(false);

			ArrayList<ArtInfo> def = new ArrayList<ArtInfo>();
			def.add(new ArtInfo("empty", "Статьи загружаются, подождите пожалуйста", "empty", "empty", "empty"));
			this.allArtsInfo = def;

			this.artsListAdapter = new ArtsListAdapter(act, this.allArtsInfo, artsList, this);
			this.artsList.setAdapter(artsListAdapter);

		}
		else
		{
			Log.e(categoryToLoad, "this.allArtsInfo!=NULL");
			this.artsListAdapter = new ArtsListAdapter(act, allArtsInfo, artsList, this);
			this.artsList.setAdapter(artsListAdapter);

			this.artsListAdapter.notifyDataSetChanged();

			//ask service if it has working task with this page and category
			Intent intent = new Intent(this.act, ServiceDB.class);
			intent.setAction(Const.Action.IS_LOADING);
			Bundle b = new Bundle();
			b.putString("categoryToLoad", this.getCategoryToLoad());
			b.putInt("pageToLoad", this.pageToLoad);
			intent.putExtras(b);
			this.act.startService(intent);
		}
		//set onScrollListener
		setOnScrollListener();

		return v;
	}

	private void setOnScrollListener()
	{
		this.artsList.setOnScrollListener(new RecyclerViewOnScrollListener(act, this.categoryToLoad, this.topImg,
		this.toolbarId)
		{
			public void onLoadMore()
			{
				if (!swipeRef.isRefreshing())
				{
					pageToLoad++;
					getAllArtsInfo(true);
					Log.e(LOG + categoryToLoad, "Start loading page " + pageToLoad + " from bottom!");
				}
			}
		});
	}

	private void getAllArtsInfo(boolean startDownload)
	{
		//Log.i(categoryToLoad, "getAllArtsInfo called");
		//change circle loading animation depends on pageToLoad
		if (this.pageToLoad == 1)
		{
			int[] textSizeAttr = new int[] { android.R.attr.actionBarSize };
			int indexOfAttrTextSize = 0;
			TypedValue typedValue = new TypedValue();
			TypedArray a = this.act.obtainStyledAttributes(typedValue.data, textSizeAttr);
			int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, 100);
			a.recycle();
			//			this.swipeRef.setProgressViewOffset(false, 0, actionBarSize);
			this.swipeRef.setProgressViewEndTarget(false, actionBarSize);
		}
		else
		{
			int[] textSizeAttr = new int[] { android.R.attr.textSize };
			int indexOfAttrTextSize = 0;
			TypedValue typedValue = new TypedValue();
			TypedArray a = this.act.obtainStyledAttributes(typedValue.data, textSizeAttr);
			int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, 100);
			a.recycle();
			DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
			int height = displayMetrics.heightPixels;
			this.swipeRef.setProgressViewOffset(false, 0, height - actionBarSize * 2);
			this.swipeRef.setProgressViewEndTarget(false, height - actionBarSize * 2);
		}
		this.swipeRef.setRefreshing(true);

		Intent intent = new Intent(this.act, ServiceDB.class);
		intent.setAction(Const.Action.DATA_REQUEST);
		Bundle b = new Bundle();
		b.putString("categoryToLoad", this.getCategoryToLoad());
		b.putInt("pageToLoad", this.pageToLoad);
		b.putLong("timeStamp", System.currentTimeMillis());
		b.putBoolean("startDownload", startDownload);
		intent.putExtras(b);
		this.act.startService(intent);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		//Log.d(LOG_TAG + categoryToLoad, "onSaveInstanceState called");
		super.onSaveInstanceState(outState);

		outState.putFloat("topImgYCoord", this.topImg.getY());

		//category saving
		outState.putString("categoryToLoad", categoryToLoad);

		outState.putInt("pageToLoad", this.pageToLoad);

		outState.putInt("position", this.position);
		outState.putParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO, allArtsInfo);
		//		outState.putParcelable(ArtInfo.KEY_CURENT_ART, allArtsInfo.get(position));

		//		outState.putBoolean(KEY_IS_LOADING, isLoading);
	}

	public void setActivatedPosition(int position)
	{
		//System.out.println("setActivatedPosition(int position: " + position);
		this.position = position;

		this.artsList.scrollToPosition(ArtsListAdapter.getPositionInRecyclerView(position));
	}

	public int getMyActivatedPosition()
	{
		return this.position;
	}

	public String getCategoryToLoad()
	{
		return categoryToLoad;
	}

	public void setCategoryToLoad(String categoryToLoad)
	{
		this.categoryToLoad = categoryToLoad;
	}

	@Override
	public void onDestroy()
	{
		// If the DownloadStateReceiver still exists, unregister it and set it to null
		if (artSelectedReceiver != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(artSelectedReceiver);
			artSelectedReceiver = null;
		}
		if (artsDataReceiver != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(artsDataReceiver);
			artsDataReceiver = null;
		}
		if (categoryIsLoadingReceiver != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(categoryIsLoadingReceiver);
			categoryIsLoadingReceiver = null;
		}
		// Must always call the super method at the end.
		super.onDestroy();
	}

	public boolean isInLeftPager()
	{
		return isInLeftPager;
	}

	public void setInLeftPager(boolean isInLeftPager)
	{
		this.isInLeftPager = isInLeftPager;
	}

	//	public boolean isLoading()
	//	{
	//		return isLoading;
	//	}
	//
	//	public void setLoading(boolean isLoading)
	//	{
	//		this.isLoading = isLoading;
	//	}
}
