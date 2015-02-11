/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.animations.RecyclerViewOnScrollListener;
import ru.kuchanov.odnako.db.Msg;
import ru.kuchanov.odnako.db.ServiceDB;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.ArtsListAdapter;
import ru.kuchanov.odnako.utils.UniversalImageLoader;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
public class FragmentArtsRecyclerList extends Fragment
{
	private static String LOG_TAG = FragmentArtsRecyclerList.class.getSimpleName() + "/";

	private int pageToLoad = 1;

	private SwipeRefreshLayout swipeRef;

	//art's list top image and it's gradient cover
	private ImageView topImgCover;
	private ImageView topImg;

	private float topImgCoord;

	private int toolbarId = R.id.toolbar;
	private boolean isInLeftPager=true;

	private RecyclerView artsList;
	private ArtsListAdapter artsListAdapter;

	private ActionBarActivity act;
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

		this.act = (ActionBarActivity) this.getActivity();
		this.pref = PreferenceManager.getDefaultSharedPreferences(act);

		Bundle fromArgs = this.getArguments();
		if (fromArgs != null)
		{
			this.setCategoryToLoad(fromArgs.getString("categoryToLoad"));
		}
		else
		{
			System.out.println("empty fromArgs!");
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

		LocalBroadcastManager.getInstance(this.act).registerReceiver(artsDataReceiver,
		new IntentFilter(this.getCategoryToLoad()));

		//reciver for scrolling and highligting selected position
		LocalBroadcastManager.getInstance(this.act).registerReceiver(artSelectedReceiver,
		new IntentFilter(this.getCategoryToLoad() + "art_position"));

		//reciver for notify when frag selected
		LocalBroadcastManager.getInstance(this.act).registerReceiver(fragSelectedReceiver,
		new IntentFilter(this.getCategoryToLoad() + "_notify_that_selected"));
	}

	private BroadcastReceiver fragSelectedReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.i(categoryToLoad, "fragSelectedReceiver onReceive called");
			artsListAdapter.notifyDataSetChanged();
		}
	};

	private BroadcastReceiver artSelectedReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.i(categoryToLoad, "artSelectedReceiver onReceive called");
			position = intent.getIntExtra("position", 0);

			setActivatedPosition(position);
			topImg.setY(0 - topImg.getHeight());
			artsListAdapter.notifyDataSetChanged();
		}
	};

	private BroadcastReceiver artsDataReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.i(LOG_TAG + categoryToLoad, "artsDataReceiver onReceive called");
			//get result message
			String[] msg = intent.getStringArrayExtra(Msg.MSG);
			int page = intent.getIntExtra("pageToLoad", 1);

			if (msg[0] == null)
			{
				Log.e(LOG_TAG + categoryToLoad, "msg[0]==null: " + String.valueOf(msg[0] == null));
			}

			switch (msg[0])
			{
				case (Msg.NO_NEW):
					Log.d(LOG_TAG + "/" + categoryToLoad, "Новых статей не обнаружено!");
					Toast.makeText(act, "Новых статей не обнаружено!", Toast.LENGTH_SHORT).show();
					updateAdapter(intent, page);
				break;
				case (Msg.NEW_QUONT):
					Log.d(LOG_TAG + "/" + categoryToLoad, "Обнаружено " + msg[1] + " новых статей");
					Toast.makeText(act, "Обнаружено " + msg[1] + " новых статей", Toast.LENGTH_SHORT).show();
					updateAdapter(intent, page);
				break;
				case (Msg.DB_ANSWER_WRITE_FROM_TOP_NO_MATCHES):
					Log.d(LOG_TAG + "/" + categoryToLoad, "Обнаружено " + msg[1] + " новых статей");
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
					pageToLoad = 1;
					getAllArtsInfo(true);
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
						pageToLoad--;
						//setOnScrollListener();
					}
				break;
				default:
					Log.e(LOG_TAG, "непредвиденный ответ базы данных");
					Toast.makeText(act, "непредвиденный ответ базы данных", Toast.LENGTH_SHORT).show();
			}

			setOnScrollListener();

			if (swipeRef.isRefreshing())
			{
				TypedValue typed_value = new TypedValue();
				getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize,
				typed_value, true);
				swipeRef.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));

				swipeRef.setProgressViewEndTarget(false, getResources().getDimensionPixelSize(typed_value.resourceId));
				swipeRef.setRefreshing(false);
			}
		}
	};

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//Log.d(LOG_TAG, "onCreateView");
		View v = inflater.inflate(R.layout.fragment_arts_list, container, false);

		//find cur frag's toolbar
		if (container.getId() == R.id.article_comments_container)
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
		else if (container.getId() == R.id.arts_list_container)
		{
			this.toolbarId = R.id.toolbar;
			this.setInLeftPager(true);
		}

		this.topImgCover = (ImageView) v.findViewById(R.id.top_img_cover);
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
				ImageLoader imgLoader = UniversalImageLoader.get(act);
				imgLoader.displayImage("drawable://" + resId, topImg,
				UniversalImageLoader.getTransparentBackgroundOptions());
				break;
			}
		}
		this.topImg.setY(topImgCoord);

		if (this.pref.getString("theme", "dark").equals("dark"))
		{
			topImgCover.setBackgroundResource(R.drawable.top_img_cover_grey_dark);
		}
		else
		{
			topImgCover.setBackgroundResource(R.drawable.top_img_cover_grey_light);
		}

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

		if (this.allArtsInfo == null)
		{
			Log.i(categoryToLoad, "this.allArtsInfo=NULL");

			this.getAllArtsInfo(false);

			ArrayList<ArtInfo> def = new ArrayList<ArtInfo>();
			def.add(new ArtInfo("empty", "Статьи загружаются, подождите пожалуйста", "empty", "empty", "empty"));
			this.allArtsInfo = def;

			this.artsListAdapter = new ArtsListAdapter(act, this.allArtsInfo, artsList, this);
			this.artsList.setAdapter(artsListAdapter);

		}
		else
		{
			this.artsListAdapter = new ArtsListAdapter(act, allArtsInfo, artsList, this);
			this.artsList.setAdapter(artsListAdapter);

			this.artsListAdapter.notifyDataSetChanged();
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
				pageToLoad++;
				getAllArtsInfo(true);
				Log.e(LOG_TAG + categoryToLoad, "Start loading page " + pageToLoad + " from bottom!");
			}
		});
	}

	private void getAllArtsInfo(boolean startDownload)
	{
		Log.i(categoryToLoad, "getAllArtsInfo called");

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
		if (fragSelectedReceiver != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(fragSelectedReceiver);
			fragSelectedReceiver = null;
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
}
