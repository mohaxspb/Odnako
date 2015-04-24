/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.animations.RecyclerViewOnScrollListener;
import ru.kuchanov.odnako.animations.SpacesItemDecoration;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Msg;
import ru.kuchanov.odnako.db.ServiceDB;
import ru.kuchanov.odnako.db.ServiceRSS;
import ru.kuchanov.odnako.lists_and_utils.RecyclerAdapterArtsListFragment;
import ru.kuchanov.odnako.lists_and_utils.CatData;
import ru.kuchanov.odnako.lists_and_utils.PagerAdapterAllAuthors;
import ru.kuchanov.odnako.lists_and_utils.PagerAdapterAllCategories;
import ru.kuchanov.odnako.lists_and_utils.PagerListenerArticle;
import ru.kuchanov.odnako.utils.MyUIL;
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
	private final static String LOG = FragmentArtsListRecycler.class.getSimpleName() + "/";

	ImageLoader imgLoader;

	private int pageToLoad = 1;

	private SwipeRefreshLayout swipeRef;

	/**
	 * art's list top image
	 */
	private ImageView topImg;
	private float topImgCoord;

	private int toolbarId = R.id.toolbar;
	private boolean isInLeftPager = true;

	private RecyclerView recycler;
	private RecyclerAdapterArtsListFragment recyclerAdapter;

	private ActivityMain act;
	private SharedPreferences pref;

	private String categoryToLoad;
	private ArrayList<Article> allArtsInfo;
	//TODO check if we need it
	//	private ArtInfo curArtInfo;
	private int position = 0;

	private final static String KEY_IS_LOADING = "isLoading";
	private boolean isLoading = false;
	private final static String KEY_IS_LOADING_FROM_TOP = "isLoadingFromTop";
	private boolean isLoadingFromTop = true;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		//Log.d(LOG_TAG+categoryToLoad, "onCreate");
		super.onCreate(savedInstanceState);

		this.act = (ActivityMain) this.getActivity();
		this.imgLoader = MyUIL.get(act);
		this.pref = PreferenceManager.getDefaultSharedPreferences(act);

		Bundle fromArgs = this.getArguments();
		if (fromArgs != null)
		{
			this.setCategoryToLoad(fromArgs.getString("categoryToLoad"));
			this.position = fromArgs.getInt("position");
		}

		//restore topImg and toolbar prop's
		if (savedInstanceState != null)
		{
			this.topImgCoord = savedInstanceState.getFloat("topImgYCoord");
			this.pageToLoad = savedInstanceState.getInt("pageToLoad");
			this.categoryToLoad = savedInstanceState.getString("categoryToLoad");
			this.position = savedInstanceState.getInt("position");
			this.isLoading = savedInstanceState.getBoolean(KEY_IS_LOADING);
			this.isLoadingFromTop = savedInstanceState.getBoolean(KEY_IS_LOADING_FROM_TOP);
		}

		LocalBroadcastManager.getInstance(this.act).registerReceiver(artsDataReceiver,
		new IntentFilter(this.getCategoryToLoad()));

		//reciver for scrolling and highligting selected position
		LocalBroadcastManager.getInstance(this.act).registerReceiver(artSelectedReceiver,
		new IntentFilter(this.getCategoryToLoad() + "art_position"));

		//reciver for scrolling and highligting selected position
		LocalBroadcastManager.getInstance(this.act).registerReceiver(receiverForRSS,
		new IntentFilter(this.getCategoryToLoad() + "_rss"));

		//receiver for updating savedState (if artsText is loaded)
		LocalBroadcastManager.getInstance(this.act).registerReceiver(receiverArticleLoaded,
		new IntentFilter(Const.Action.ARTICLE_CHANGED));
	}

	private void setLoading(boolean isLoading)
	{
		this.isLoading = isLoading;
		if (isLoading)
		{
			//			if (pageToLoad == 1)
			if (this.isLoadingFromTop)
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
				int[] textSizeAttr = new int[] { android.R.attr.actionBarSize };
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
			int[] textSizeAttr = new int[] { android.R.attr.actionBarSize };
			int indexOfAttrTextSize = 0;
			TypedValue typedValue = new TypedValue();
			TypedArray a = act.obtainStyledAttributes(typedValue.data, textSizeAttr);
			int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, 100);
			a.recycle();
			//			this.swipeRef.setProgressViewOffset(false, 0, actionBarSize);
			swipeRef.setProgressViewEndTarget(false, actionBarSize);
			swipeRef.setRefreshing(false);
		}
	}

	private BroadcastReceiver artSelectedReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			//Log.i(LOG + categoryToLoad, "artSelectedReceiver onReceive called");
			position = intent.getIntExtra("position", 0);

			setActivatedPosition(position);
			recyclerAdapter.notifyDataSetChanged();
		}
	};

	private BroadcastReceiver receiverForRSS = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.i(LOG + categoryToLoad, "receiverForRSS onReceive()");
			ArrayList<Article> rssData = intent.getParcelableArrayListExtra(Article.KEY_ALL_ART_INFO);
			//update activities artList
			//TODO update all lists
			ArrayList<Article> activitiesData = act.getAllCatArtsInfo().get(categoryToLoad);
			for (Article a : rssData)
			{
				boolean findIt = false;
				for (int i = 0; i < activitiesData.size() && (findIt == false); i++)
				{
					Article b = activitiesData.get(i);
					if (a.getUrl().equals(b.getUrl()))
					{
						findIt = true;
						b.setPreview(a.getPreview());
						b.setPubDate(a.getPubDate());
					}
				}
			}
			//updateLists in Service
			if (act.getServiceDB() != null)
			{

				Set<String> keySet = act.getServiceDB().getAllCatArtsInfo().keySet();
				for (String key : keySet)
				{
					ArrayList<Article> artsList = act.getServiceDB().getAllCatArtsInfo().get(key);
					if (artsList != null)
					{
						for (Article artFromRss : rssData)
						{
							boolean notFound = true;
							for (int i = 0; i < artsList.size() && notFound; i++)
							{
								Article artInList = artsList.get(i);
								if (artInList.getUrl().equals(artFromRss.getUrl()))
								{
									artInList.setPreview(artFromRss.getPreview());
									artInList.setPubDate(artFromRss.getPubDate());
									notFound = false;
								}
							}
						}
					}
				}
			}
			//after  updating Articles from activities HashMap
			//we update adapter
			recycler.getAdapter().notifyDataSetChanged();
		}
	};

	/**
	 * receives intent with Articles data and updates list, toolbar and toast in
	 * some cases, based on message from DB. Also, if this is main list
	 * (odnako.org/blogs) and we load from top it starts loading data from rss
	 */
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
			ViewPager pagerLeft = (ViewPager) act.findViewById(R.id.pager_left);
			ViewPager pagerRight = (ViewPager) act.findViewById(R.id.pager_right);
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
							PagerAdapterAllCategories allCategoriesAdapter = (PagerAdapterAllCategories) pagerRight
							.getAdapter();
							List<String> alllCategoriesUrls = allCategoriesAdapter.getAllCategoriesURLsList();
							int selectedArtPosOfAlllCategoriesFrag = act.getAllCatListsSelectedArtPosition().get(
							allMenuLinks[3]);
							if (categoryToLoad.equals(alllCategoriesUrls.get(selectedArtPosOfAlllCategoriesFrag)))
							{
								//so it's currently displayed fragment
								isDisplayed = true;
							}
							else
							{
								isDisplayed = false;
							}
						}
					}
					else
					{
						isDisplayed = false;
					}
				break;
				case ActivityMain.PAGER_TYPE_AUTHORS:
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
					PagerAdapterAllCategories allCategoriesAdapter = (PagerAdapterAllCategories) pagerLeft.getAdapter();
					List<String> allCategoriesUrls = allCategoriesAdapter.getAllCategoriesURLsList();
					if (categoryToLoad.equals(allCategoriesUrls.get(currentCategoryPosition)) && isInLeftPager)
					{
						//so it's currently displayed fragment
						isDisplayed = true;
					}
					else
					{
						isDisplayed = false;
					}
				break;
				case ActivityMain.PAGER_TYPE_SINGLE:
					//it's the only one fragment, so isDisplayed is always true
					isDisplayed = true;
					//try setting title to toolbar
					ArrayList<Article> allArts = intent.getParcelableArrayListExtra(Article.KEY_ALL_ART_INFO);
					Toolbar toolbar = (Toolbar) act.findViewById(toolbarId);
					if (allArts != null)
					{
						if (allArts.get(0).getAuthorName().equals("empty"))
						{
							toolbar.setTitle(categoryToLoad);
						}
						else
						{
							toolbar.setTitle(allArts.get(0).getAuthorName());
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
				case Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_HAVE_MATCH_TO_INITIAL:
				case Msg.DB_ANSWER_FROM_BOTTOM_INFO_SENDED_TO_FRAG:
				case Msg.DB_ANSWER_INFO_SENDED_TO_FRAG:
					Log.d(LOG + categoryToLoad, "Msg.DB_ANSWER_INFO_SENDED_TO_FRAG");
					updateAdapter(intent, page);
					setLoading(false);
				break;
				case (Msg.NO_NEW):
					Log.d(LOG + categoryToLoad, "Новых статей не обнаружено!");
					if (isDisplayed)
					{
						Toast.makeText(act, "Новых статей не обнаружено!", Toast.LENGTH_SHORT).show();
					}
					updateAdapter(intent, page);
					setLoading(false);
					//TODO remove RSS to ServiceDB
					if (getCategoryToLoad().contains("odnako.org/blogs"))
					{
						Intent intentRSS = new Intent(act, ServiceRSS.class);
						intentRSS.putExtra("categoryToLoad", getCategoryToLoad());
						act.startService(intentRSS);
					}
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
					setLoading(false);
					//TODO remove RSS to ServiceDB
					if (getCategoryToLoad().contains("odnako.org/blogs"))
					{
						Intent intentRSS = new Intent(act, ServiceRSS.class);
						intentRSS.putExtra("categoryToLoad", getCategoryToLoad());
						act.startService(intentRSS);
					}
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
					setLoading(false);
				break;
				case (Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT):
					Log.d(LOG + categoryToLoad, "Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT");
					updateAdapter(intent, page);
					setLoading(false);
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
					pageToLoad = 1;
					position = 0;
					((ActivityMain) act).getAllCatListsSelectedArtPosition().put(categoryToLoad, position);
					allArtsInfo.clear();
					ArrayList<Article> def = new ArrayList<Article>();
					Article a = new Article();
					a.setTitle("Статьи загружаются, подождите пожалуйста");
					def.add(a);
					allArtsInfo = def;
					((ActivityMain) act).getAllCatArtsInfo().put(categoryToLoad, allArtsInfo);
					recycler.getAdapter().notifyDataSetChanged();
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
					setLoading(false);
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
					setLoading(false);
				break;
				default:
					Log.e(LOG + categoryToLoad, "непредвиденный ответ базы данных");
					if (isDisplayed)
					{
						Toast.makeText(act, "непредвиденный ответ базы данных", Toast.LENGTH_SHORT).show();
					}
					setLoading(false);
				break;
			}

			setOnScrollListener();

			//			if (swipeRef.isRefreshing())
			//			{
			//				TypedValue typed_value = new TypedValue();
			//				act.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize,
			//				typed_value, true);
			//				swipeRef.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
			//
			//				swipeRef.setProgressViewEndTarget(false, getResources().getDimensionPixelSize(typed_value.resourceId));
			//				swipeRef.setRefreshing(false);
			//			}

			boolean refreshRightToolbarAndPager = isInLeftPager && pref.getBoolean("twoPane", false) && isDisplayed;
			if (refreshRightToolbarAndPager)
			{
				updateRightPagerAndToolbar(msg);
			}
		}//onReceive
	};//artsDataReceiver

	private void updateAdapter(Intent intent, int page)
	{
		if (this.act.getServiceDB().getAllCatArtsInfo().get(this.categoryToLoad) != null)
		{
			if (page == 1)
			{
				allArtsInfo = this.act.getServiceDB().getAllCatArtsInfo().get(this.categoryToLoad);
				this.recyclerAdapter = new RecyclerAdapterArtsListFragment(act, allArtsInfo, this);
				this.recycler.setAdapter(recyclerAdapter);
			}
			else
			{
				recyclerAdapter.notifyDataSetChanged();
			}
			((ActivityBase) act).getAllCatArtsInfo().put(categoryToLoad, allArtsInfo);
		}
		else
		{
			System.out.println("ArrayList<Article> someResult=NULL!!!");
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
		boolean findImgFile = false;

		for (int i = 0; i < categoriesUrls.length && !findImgFile; i++)
		{
			if (this.categoryToLoad.equals(categoriesUrls[i]))
			{
				findImgFile = true;
				String fullResName = catImgsFilesNames[i];
				String resName = fullResName.substring(0, fullResName.length() - 4);
				int resId = act.getResources().getIdentifier(resName, "drawable", defPackage);
				//imgLoader.displayImage("drawable://" + resId, topImg, MyUIL.getTransparentBackgroundOptions(this.act));
				topImg.setImageResource(resId);
				break;
			}
		}
		if (!findImgFile)
		{
			catImgsFilesNames = CatData.getAllTagsImgsFILEnames(act);
			categoriesUrls = CatData.getAllTagsLinks(act);
			for (int i = 0; i < categoriesUrls.length && !findImgFile; i++)
			{
				if (this.categoryToLoad.equals(categoriesUrls[i]))
				{
					findImgFile = true;
					String fullResName = catImgsFilesNames[i];
					String resName = fullResName.substring(0, fullResName.length() - 4);
					int resId = act.getResources().getIdentifier(resName, "drawable", defPackage);
					imgLoader.displayImage("drawable://" + resId, topImg,
					MyUIL.getTransparentBackgroundOptions(this.act));
					break;
				}
			}
		}
		if (!findImgFile)
		{
			int resId = R.drawable.odnako;
			imgLoader.displayImage("drawable://" + resId, topImg, MyUIL.getTransparentBackgroundOptions(this.act));
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

		this.recycler = (RecyclerView) v.findViewById(R.id.arts_list_view);
		this.recycler.setItemAnimator(new DefaultItemAnimator());
		this.recycler.addItemDecoration(new SpacesItemDecoration(25));
		this.recycler.setLayoutManager(new LinearLayoutManager(act));

		//animate loading state if we load something
		this.setLoading(this.isLoading);

		if (this.act.getServiceDB() != null)
		{
			//Log.e(LOG, "this.act.getServiceDB() != null");
			this.allArtsInfo = this.act.getServiceDB().getAllCatArtsInfo().get(this.categoryToLoad);
		}
		else
		{
			//Log.e(LOG, "this.act.getServiceDB() == null");
			this.allArtsInfo = this.act.getAllCatArtsInfo().get(this.categoryToLoad);
		}

		if (this.allArtsInfo == null)
		{
			//Log.e(LOG, "this.allArtsInfo == null");
			if (this.isLoading == false)
			{
				this.getAllArtsInfo(false);
			}
		}
		else
		{
			//Log.e(LOG, "this.allArtsInfo != null");
			this.recyclerAdapter = new RecyclerAdapterArtsListFragment(act, allArtsInfo, this);
			this.recycler.setAdapter(recyclerAdapter);
			//this.recyclerAdapter.notifyDataSetChanged();
		}

		//set onScrollListener
		setOnScrollListener();

		return v;
	}

	private void setOnScrollListener()
	{
		this.recycler.setOnScrollListener(new RecyclerViewOnScrollListener(act, this.categoryToLoad, this.topImg,
		this.toolbarId)
		{
			public void onLoadMore()
			{
				//				if (!swipeRef.isRefreshing())
				if (isLoading == false)
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
		if (this.pageToLoad == 1)
		{
			this.isLoadingFromTop = true;
		}
		else
		{
			this.isLoadingFromTop = false;
		}
		setLoading(true);

		Intent intent = new Intent(this.act, ServiceDB.class);
		intent.setAction(Const.Action.DATA_REQUEST);
		intent.putExtra("categoryToLoad", this.getCategoryToLoad());
		intent.putExtra("pageToLoad", this.pageToLoad);
		intent.putExtra("timeStamp", System.currentTimeMillis());
		intent.putExtra("startDownload", startDownload);
		this.act.startService(intent);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		//Log.d(LOG_TAG + categoryToLoad, "onSaveInstanceState called");
		super.onSaveInstanceState(outState);

		outState.putFloat("topImgYCoord", this.topImg.getY());
		outState.putString("categoryToLoad", categoryToLoad);
		outState.putInt("pageToLoad", this.pageToLoad);
		outState.putInt("position", this.position);
		outState.putBoolean(KEY_IS_LOADING, isLoading);
		outState.putBoolean(KEY_IS_LOADING_FROM_TOP, isLoadingFromTop);
	}

	public void setActivatedPosition(int position)
	{
		//System.out.println("setActivatedPosition(int position: " + position);
		this.position = position;

		this.recycler.scrollToPosition(RecyclerAdapterArtsListFragment.getPositionInRecyclerView(position));
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
		if (receiverForRSS != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(receiverForRSS);
			receiverForRSS = null;
		}
		if (receiverArticleLoaded != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(receiverArticleLoaded);
			receiverArticleLoaded = null;
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

	protected BroadcastReceiver receiverArticleLoaded = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.i(LOG, "receiverArticleLoaded onReceive called");
			if (allArtsInfo == null)
			{
				Log.e(LOG + categoryToLoad, "allArtsInfo==null in onReceive");
				return;
			}
			if (intent.getParcelableExtra(Article.KEY_CURENT_ART) == null)
			{
				Log.e(LOG + categoryToLoad, "intent.getParcelableExtra(Article.KEY_CURENT_ART)==null in onReceive");
				return;
			}

			Article a = intent.getParcelableExtra(Article.KEY_CURENT_ART);
			
			//Log.e(LOG, a.getUrl());
			
			boolean notFound = true;
			switch (intent.getStringExtra(Const.Action.ARTICLE_CHANGED))
			{
				case Const.Action.ARTICLE_READ:
					//loop through all arts in activity and update them and adapters
					for (int i = 0; i < allArtsInfo.size() && notFound; i++)
					{
						Article artInList = allArtsInfo.get(i);
						if (artInList.getUrl().equals(a.getUrl()))
						{
							allArtsInfo.get(i).setReaden(a.isReaden());
							//allArtsInfo.set(i, a);
							recyclerAdapter.updateArticle(allArtsInfo.get(i), i);
							notFound = false;
						}
					}
				break;
				case Const.Action.ARTICLE_LOADED:
					//loop through all arts in activity and update them and adapters
					for (int i = 0; i < allArtsInfo.size() && notFound; i++)
					{
						Article artInList = allArtsInfo.get(i);
						if (artInList.getUrl().equals(a.getUrl()))
						{
							allArtsInfo.get(i).setArtText(a.getArtText());
							//allArtsInfo.set(i, a);
							recyclerAdapter.updateArticle(allArtsInfo.get(i), i);
							notFound = false;
						}
					}
				break;
			}
		}
	};
}