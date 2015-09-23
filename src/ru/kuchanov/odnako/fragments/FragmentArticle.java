/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.DataBaseHelper;
import ru.kuchanov.odnako.db.Msg;
import ru.kuchanov.odnako.lists_and_utils.Actions;
import ru.kuchanov.odnako.lists_and_utils.PagerAdapterArticles;
import ru.kuchanov.odnako.lists_and_utils.RecyclerAdapterArticleFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
//import ru.kuchanov.odnako.custom.view.MyLinearLayoutManager;

public class FragmentArticle extends Fragment implements FragArtUPD
{
	public final static String LOG = FragmentArticle.class.getSimpleName() + "/";

	final public static String ARTICLE_URL = "article_url";

	private AppCompatActivity act;

	SwipeRefreshLayout swipeRef;

	private Article curArticle;
	//	position in all art arr; need to show next/previous arts
	private int position;
	//	ArrayList<Article> allArtsInfo;

	private RecyclerView recycler;
	//	private RecyclerAdapterArticleFragment recyclerAdapter;

	private boolean isSingle = false;

	//	private int[] scroll = new int[]{0, 0};

	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		//Log.i(LOG, "ArticleFragment onCreate");

		this.act = (AppCompatActivity) this.getActivity();

		//restore info
		Bundle stateFromArgs = this.getArguments();
		if (stateFromArgs != null)
		{
			this.curArticle = stateFromArgs.getParcelable(Article.KEY_CURENT_ART);
			this.setPosition(stateFromArgs.getInt("position", 0));
			this.isSingle = stateFromArgs.getBoolean("isSingle", false);
		}
		if (savedState != null)
		{
			this.curArticle = savedState.getParcelable(Article.KEY_CURENT_ART);
			this.setPosition(savedState.getInt("position"));
			this.isSingle = savedState.getBoolean("isSingle", false);
			//			this.scroll = savedState.getIntArray("scroll");
		}

		if (this.curArticle != null)
		{
			//Log.e(LOG, curArticle.getUrl());

			LocalBroadcastManager.getInstance(this.act).registerReceiver(articleReceiver,
			new IntentFilter(this.curArticle.getUrl()));

			////////////////
			LocalBroadcastManager.getInstance(this.act).registerReceiver(fragSelectedReceiver,
			new IntentFilter(this.curArticle.getUrl() + "frag_selected"));
		}
	}

	private BroadcastReceiver fragSelectedReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.e(LOG, "fragSelectedReceiver onReceive");

			if (!isAdded())
			{
				return;
			}
			//			((MyLinearLayoutManager) recycler.getLayoutManager()).setFirstScroll(true);
			if (curArticle != null)
			{
				//				LocalBroadcastManager.getInstance(act).unregisterReceiver(articleReceiver);
				//				LocalBroadcastManager.getInstance(act).registerReceiver(articleReceiver,
				//				new IntentFilter(curArticle.getUrl()));
				//
				//				LocalBroadcastManager.getInstance(act).unregisterReceiver(fragSelectedReceiver);
				//				LocalBroadcastManager.getInstance(act).registerReceiver(fragSelectedReceiver,
				//				new IntentFilter(curArticle.getUrl() + "frag_selected"));

				update(curArticle);
			}
		}
	};

	private BroadcastReceiver articleReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.e(LOG, "articleReceiver onReceive");
			if (!isAdded())
			{
				Log.e(LOG + curArticle.getUrl(), "fragment not added! RETURN!");
				return;
			}
			if (intent.getExtras().containsKey(Msg.ERROR))
			{
				Toast.makeText(act, intent.getStringExtra(Msg.ERROR), Toast.LENGTH_SHORT).show();
				swipeRef.setRefreshing(false);
				return;
			}
			else
			{
				Article a = intent.getParcelableExtra(Article.KEY_CURENT_ART);
				update(a);

				Log.i(LOG,
				"a.getText().equals(Const.EMPTY_STRING): " + String.valueOf(a.getArtText().equals(Const.EMPTY_STRING)));

				//if it's currently displayed frag, we can notify, that it is readen now
				ViewPager pager = (ViewPager) act.findViewById(R.id.pager_right);
				if (a.isReaden() == false && position == pager.getCurrentItem())
				{
					a.setReaden(true);
					DataBaseHelper h = new DataBaseHelper(act);
					//Log.e(a.getTitle(), "a.getId(): " + a.getId());
					Article.updateIsReaden(h, Article.getArticleIdByURL(h, a.getUrl()), true);
					h.close();

					Intent intentGlobal = new Intent(Const.Action.ARTICLE_CHANGED);
					intentGlobal.putExtra(Article.KEY_CURENT_ART, a);
					intentGlobal.putExtra(Const.Action.ARTICLE_CHANGED, Const.Action.ARTICLE_READ);
					LocalBroadcastManager.getInstance(act).sendBroadcast(intentGlobal);
				}
			}
		}
	};

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//Log.i(LOG, "ArticleFragment onCreateView");
		View v = inflater.inflate(R.layout.fragment_art, container, false);

		this.swipeRef = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);
		//workaround to fix issue with not showing refreshing indicator before swipeRef.onMesure() was called
		//as I understand before onResume of Activity
		this.swipeRef.setColorSchemeColors(R.color.material_red_300,
		R.color.material_red_500,
		R.color.material_red_500,
		R.color.material_red_500);

		TypedValue typed_value = new TypedValue();
		getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
		this.swipeRef.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));

		this.swipeRef.setProgressViewEndTarget(false, getResources().getDimensionPixelSize(typed_value.resourceId));
		////set on swipe listener
		this.swipeRef.setOnRefreshListener(new OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				loadArticle(true);
			}
		});

		this.recycler = (RecyclerView) v.findViewById(R.id.recycler_view);
		this.recycler.setItemAnimator(new DefaultItemAnimator());
		//		this.recycler.setLayoutManager(new MyLinearLayoutManager(act));
		this.recycler.setLayoutManager(new LinearLayoutManager(act));

		//check for existing article's text in ArtInfo obj. If it's null or empty - start download
		this.update(curArticle);

		return v;
	}

	private void loadArticle(boolean startDownload)
	{
		this.swipeRef.setRefreshing(true);

		Actions.startDownLoadArticle(this.curArticle.getUrl(), this.act, startDownload);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putInt("position", this.getPosition());
		outState.putParcelable(Article.KEY_CURENT_ART, curArticle);
		outState.putBoolean("isSingle", isSingle);

		//		LinearLayoutManager manager = ((LinearLayoutManager) this.recycler.getLayoutManager());
		//		int firstVisiblePos = manager.findFirstVisibleItemPosition();
		//
		//		outState.putIntArray("scroll", new int[] { firstVisiblePos, manager.findViewByPosition(firstVisiblePos)
		//		.getTop() });
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (this.isSingle)
		{
			//setBackButton to toolbar and its title
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
			if (pref.getBoolean("twoPane", false))
			{
				Toolbar toolbar = (Toolbar) act.findViewById(R.id.toolbar_right);
				toolbar.setTitle("Статья");
			}
			else
			{
				Toolbar toolbar = (Toolbar) act.findViewById(R.id.toolbar);
				toolbar.setTitle("Статья");
			}
		}
	}

	@Override
	public void update(Article article)
	{
		this.curArticle = article;
		Log.d(LOG, "update called");

		if (this.curArticle != null && !this.isSingle && !this.curArticle.getArtText().equals(Const.EMPTY_STRING))
		{
			Log.d(LOG, "update called, url: " + article.getUrl());
			try
			{
				ViewPager pagerRight = (ViewPager) this.act.findViewById(R.id.pager_right);
				PagerAdapterArticles pagerAdapter = (PagerAdapterArticles) pagerRight.getAdapter();
				String urlFromPager = pagerAdapter.getAllArtsInfo().get(position).getUrl();
				if (!this.curArticle.getUrl().equals(urlFromPager))
				{
					this.curArticle = pagerAdapter.getAllArtsInfo().get(position);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			Log.e(LOG, "update called but article is NULL!!!!");
		}

		if (this.curArticle != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(articleReceiver);
			LocalBroadcastManager.getInstance(this.act).registerReceiver(articleReceiver,
			new IntentFilter(this.curArticle.getUrl()));

			LocalBroadcastManager.getInstance(act).unregisterReceiver(fragSelectedReceiver);
			LocalBroadcastManager.getInstance(this.act).registerReceiver(fragSelectedReceiver,
			new IntentFilter(this.curArticle.getUrl() + "frag_selected"));
		}

		if (this.curArticle == null)
		{
			//show load...
			this.swipeRef.setRefreshing(true);
		}
		else
		{
			if (this.curArticle.getArtText().equals(Const.EMPTY_STRING))
			{
				//load...
				this.loadArticle(false);
			}
			else
			{
				//stop refreshing animation
				if (this.swipeRef.isRefreshing())
				{
					this.swipeRef.setRefreshing(false);
				}
			}
		}

		if (this.recycler.getAdapter() != null)
		{
			((RecyclerAdapterArticleFragment)this.recycler.getAdapter()).updateArticle(curArticle);
			this.recycler.getAdapter().notifyDataSetChanged();
		}
		else
		{
			this.recycler.setAdapter(new RecyclerAdapterArticleFragment(act, curArticle));
			this.recycler.getAdapter().notifyDataSetChanged();
		}

		//long beforeTime = System.currentTimeMillis();
		//		this.recyclerAdapter = new RecyclerAdapterArticleFragment(act, curArticle);

		//		this.recycler.setAdapter(recyclerAdapter);
		//		this.recycler.getAdapter().notifyDataSetChanged();
		//		Log.d(LOG, "scroll: " + this.scroll[0] + "/" + this.scroll[1]);
		//		this.recycler.getLayoutManager().scrollToPosition(this.scroll[0]);
		//		((LinearLayoutManager) this.recycler.getLayoutManager()).scrollVerticallyBy(this.scroll[1], recycler,
		//		new RecyclerView.State());
		//Log.e(LOG, "update fragment. TIME: " + String.valueOf((System.currentTimeMillis() - beforeTime)));
	}

	public int getPosition()
	{
		return position;
	}

	public void setPosition(int position)
	{
		this.position = position;
	}

	@Override
	public void onDestroy()
	{
		// If the DownloadStateReceiver still exists, unregister it and set it to null
		if (articleReceiver != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(articleReceiver);
			articleReceiver = null;
		}
		if (fragSelectedReceiver != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(fragSelectedReceiver);
			fragSelectedReceiver = null;
		}
		// Must always call the super method at the end.
		try
		{
			super.onDestroy();
		} catch (Exception e)
		{

		}
	}

	public Article getArticle()
	{
		return this.curArticle;
	}
}