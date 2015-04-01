/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.custom.view.MyLinearLayoutManager;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Msg;
import ru.kuchanov.odnako.db.ServiceArticle;
import ru.kuchanov.odnako.lists_and_utils.Actions;
import ru.kuchanov.odnako.lists_and_utils.AdapterRecyclerArticleFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentArticle extends Fragment implements FragArtUPD
{
	public final static String LOG = FragmentArticle.class.getSimpleName() + "/";

	final public static String ARTICLE_URL = "article_url";

	private ActionBarActivity act;
	private LayoutInflater inflater;
	private SharedPreferences pref;
	private boolean twoPane;


	LinearLayout articlesTextContainer;
	private TextView artTextView;

	SwipeRefreshLayout swipeRef;


	private TextView artTitleTV;
	private TextView artDateTV;

	private TextView artAuthorTV;
	private TextView artAuthorDescriptionTV;
	private ImageView artAuthorIV;
	private ImageView artAuthorDescriptionIV;

	private LinearLayout bottomPanel;
	private CardView shareCard;
	private CardView commentsBottomBtn;

	private Article curArticle;
	//	position in all art arr; need to show next/previous arts
	private int position;
	//	ArrayList<Article> allArtsInfo;

	private boolean artAuthorDescrIsShown = false;

	private RecyclerView recycler;
	private AdapterRecyclerArticleFragment recyclerAdapter;

	//	SpacesItemDecoration zeroDecor=;
	//	SpacesItemDecoration defaultDecor;

	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		//Log.i(LOG, "ArticleFragment onCreate");

		this.act = (ActionBarActivity) this.getActivity();

		//restore info
		Bundle stateFromArgs = this.getArguments();
		if (stateFromArgs != null)
		{
			this.curArticle = stateFromArgs.getParcelable(Article.KEY_CURENT_ART);
			this.setPosition(stateFromArgs.getInt("position", 0));
		}
		if (savedState != null)
		{
			this.curArticle = savedState.getParcelable(Article.KEY_CURENT_ART);
			this.setPosition(savedState.getInt("position"));
		}

		pref = PreferenceManager.getDefaultSharedPreferences(act);
		this.twoPane = pref.getBoolean("twoPane", false);

		if (this.curArticle != null)
		{
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
			//Log.e(LOG, "articleReceiver onReceive");
			((MyLinearLayoutManager)recycler.getLayoutManager()).setFirstScroll(true);
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
				curArticle = a;
				//show
				swipeRef.setRefreshing(false);
				recyclerAdapter = new AdapterRecyclerArticleFragment(act, curArticle);
				recycler.setAdapter(recyclerAdapter);
				//				update(a, null);
			}
		}
	};

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//Log.i(LOG, "ArticleFragment onCreateView");
		this.inflater = inflater;
		View v = this.inflater.inflate(R.layout.fragment_art, container, false);

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

		this.recycler = (RecyclerView) v.findViewById(R.id.article_recycler_view);
		this.recycler.setItemAnimator(new DefaultItemAnimator());
		this.recycler.setLayoutManager(new MyLinearLayoutManager(act));
		this.recyclerAdapter = new AdapterRecyclerArticleFragment(act, curArticle);
		this.recycler.setAdapter(recyclerAdapter);

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
				//show
				this.recyclerAdapter = new AdapterRecyclerArticleFragment(act, curArticle);
				this.recycler.setAdapter(recyclerAdapter);
			}
		}

		//check for existing article's text in ArtInfo obj. If it's null or empty - start download
		//		this.update(curArticle, savedInstanceState);

		return v;
	}

	private void checkCurArtInfo(Bundle savedInstanceState, ViewGroup rootView)
	{
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

				//setting size of Images and text
				this.setSizeAndTheme();
			}
			else
			{
				//show it...
				if (this.swipeRef.isRefreshing())
				{
					this.swipeRef.setRefreshing(false);
				}

				this.shareCard.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						Actions.shareUrl(curArticle.getUrl(), act);
					}
				});
				this.commentsBottomBtn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						//TODO
						//						Actions.showComments(allArtsInfo, getPosition(), act);
					}
				});
				//setting size of Images and text
				this.setSizeAndTheme();
				//End of setting size of Images and text
			}
		}
	}//check cur Article

	private void loadArticle(boolean startDownload)
	{
		this.swipeRef.setRefreshing(true);

		Intent intent = new Intent(this.act, ServiceArticle.class);
		intent.setAction(Const.Action.DATA_REQUEST);
		intent.putExtra(ARTICLE_URL, this.curArticle.getUrl());
		intent.putExtra("startDownload", startDownload);
		this.act.startService(intent);
	}

	private void findViews(View v)
	{
		//inflate bottom panels
		DisplayMetrics displayMetrics = act.getResources().getDisplayMetrics();
		int width = displayMetrics.widthPixels;
		int minWidth = 800;
		//if(twoPane) we must set width to width/4*3 
		if (twoPane)
		{
			width = displayMetrics.widthPixels / 3 * 2;
		}
		if (width <= minWidth)
		{
			this.shareCard = (CardView) inflater.inflate(R.layout.share_panel, bottomPanel, false);
			this.bottomPanel.addView(this.shareCard);
		}
		else
		{
			this.shareCard = (CardView) inflater.inflate(R.layout.share_panel_landscape, bottomPanel, false);
			this.bottomPanel.addView(this.shareCard);
		}
	}

	private void setSizeAndTheme()
	{
		String scaleFactorString = pref.getString("scale_art", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);

		this.artTextView.setTextSize(21 * scaleFactor);

		this.artTitleTV.setTextSize(25 * scaleFactor);
		this.artAuthorTV.setTextSize(21 * scaleFactor);
		this.artAuthorDescriptionTV.setTextSize(21 * scaleFactor);
		this.artDateTV.setTextSize(17 * scaleFactor);

		//images
		final float scale = getResources().getDisplayMetrics().density;
		int pixels = (int) (75 * scaleFactor * scale + 0.5f);
		LayoutParams params = new LayoutParams(pixels, pixels);

		this.artAuthorIV.setLayoutParams(params);
		this.artAuthorDescriptionIV.setLayoutParams(params);

		LayoutParams zeroHeightParams = new LayoutParams(LayoutParams.WRAP_CONTENT, 0);
		LayoutParams zeroAllParams = new LayoutParams(0, 0);

		//set descr of author btn
		//set descrTV height to 0 by default
		this.artAuthorDescriptionTV.setLayoutParams(zeroHeightParams);

		if (this.curArticle.getAuthorDescr().equals(Const.EMPTY_STRING) || this.curArticle.getAuthorDescr().equals(""))
		{
			this.artAuthorDescriptionTV.setText(null);
			this.artAuthorDescriptionTV.setLayoutParams(zeroHeightParams);
			this.artAuthorDescriptionIV.setLayoutParams(zeroAllParams);
		}
		else
		{
			this.artAuthorDescriptionTV.setText(Html.fromHtml(this.curArticle.getAuthorDescr()));
			//restore size
			this.artAuthorDescriptionIV.setLayoutParams(params);
			this.artAuthorDescriptionIV.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					artAuthorDescrBehavior();
				}
			});
		}
		//set allArsList OnClick
		((View) this.artAuthorIV.getParent()).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Actions.showAllAuthorsArticles(curArticle.getAuthorBlogUrl(), act);
			}
		});
	}//setSizeAndTheme

	private void artAuthorDescrBehavior()
	{
		//set arrowDownIcon by theme
		int[] attrs = new int[] { R.attr.arrowDownIcon };
		TypedArray ta = act.obtainStyledAttributes(attrs);
		Drawable drawableArrowDown = ta.getDrawable(0);
		ta.recycle();
		attrs = new int[] { R.attr.arrowUpIcon };
		ta = act.obtainStyledAttributes(attrs);
		Drawable drawableArrowUp = ta.getDrawable(0);
		ta.recycle();
		if (!artAuthorDescrIsShown)
		{
			//set and show text
			LayoutParams descrParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			artAuthorDescriptionTV.setLayoutParams(descrParams);
			//set btn image
			artAuthorDescriptionIV.setImageDrawable(drawableArrowUp);
			artAuthorDescrIsShown = !artAuthorDescrIsShown;
		}
		else
		{
			LayoutParams descrParams0 = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
			artAuthorDescriptionTV.setLayoutParams(descrParams0);
			//set btn image
			artAuthorDescriptionIV.setImageDrawable(drawableArrowDown);
			artAuthorDescrIsShown = !artAuthorDescrIsShown;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putInt("position", this.getPosition());
		outState.putParcelable(Article.KEY_CURENT_ART, curArticle);
	}

	@Override
	public void update(Article allArtInfo, Bundle b)
	{
		this.curArticle = allArtInfo;
		//Log.i(LOG + curArticle.getUrl(), "update called");
		if (this.curArticle != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(articleReceiver);
			LocalBroadcastManager.getInstance(this.act).registerReceiver(articleReceiver,
			new IntentFilter(this.curArticle.getUrl()));
		}

		long beforeTime = System.currentTimeMillis();
		checkCurArtInfo(b, (ViewGroup) getView());
		Log.e(LOG,
		"END fill fragment with info. TIME: " + String.valueOf((System.currentTimeMillis() - beforeTime)));
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
		super.onDestroy();
	}
}