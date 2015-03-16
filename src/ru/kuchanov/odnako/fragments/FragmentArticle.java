/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.util.ArrayList;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Msg;
import ru.kuchanov.odnako.db.ServiceArticle;
import ru.kuchanov.odnako.lists_and_utils.Actions;
import ru.kuchanov.odnako.utils.DateParse;
import ru.kuchanov.odnako.utils.DipToPx;
import ru.kuchanov.odnako.utils.ImgLoadListenerBigSmall;
import ru.kuchanov.odnako.utils.MyUIL;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentArticle extends Fragment implements FragArtUPD
{
	final static String LOG = FragmentArticle.class.getSimpleName() + "/";

	final public static String ARTICLE_URL = "article_url";

	private ActionBarActivity act;
	private LayoutInflater inflater;
	private SharedPreferences pref;
	private boolean twoPane;

	private ImageLoader imageLoader;

	LinearLayout articlesTextContainer;
	private TextView artTextView;

	private ScrollView scroll;
	SwipeRefreshLayout swipeRef;

	private ImageView artImageIV;

	private TextView artTitleTV;
	private TextView artDateTV;

	private ViewGroup authorLayout;
	private TextView artAuthorTV;
	private TextView artAuthorDescriptionTV;
	private ImageView artAuthorIV;
	private ImageView artAuthorDescriptionIV;
	private ImageView artAuthorArticlesIV;

	private TextView artTagsMainTV;

	private LinearLayout bottomPanel;
	private CardView shareCard;
	private CardView commentsBottomBtn;
	private CardView allTegsCard;
	private CardView alsoByThemeCard;
	private CardView alsoToReadCard;

	private Article curArticle;
	//	position in all art arr; need to show next/previous arts
	private int position;
	ArrayList<Article> allArtsInfo;

	private boolean artAuthorDescrIsShown = false;

	private DisplayImageOptions options;

	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		//		System.out.println("ArticleFragment onCreate");

		this.act = (ActionBarActivity) this.getActivity();

		//restore info
		Bundle stateFromArgs = this.getArguments();
		if (stateFromArgs != null)
		{
			this.curArticle = stateFromArgs.getParcelable(Article.KEY_CURENT_ART);
			this.setPosition(stateFromArgs.getInt("position"));
			this.allArtsInfo = stateFromArgs.getParcelableArrayList(Article.KEY_ALL_ART_INFO);
		}
		if (savedState != null)
		{
			this.curArticle = savedState.getParcelable(Article.KEY_CURENT_ART);
			this.setPosition(savedState.getInt("position"));
			this.allArtsInfo = savedState.getParcelableArrayList(Article.KEY_ALL_ART_INFO);
		}

		this.imageLoader = MyUIL.get(act);

		pref = PreferenceManager.getDefaultSharedPreferences(act);
		this.twoPane = pref.getBoolean("twoPane", false);

		if (this.pref.getString("theme", "dark").equals("dark"))
		{
			options = MyUIL.getDarkOptions();
		}
		else
		{
			options = MyUIL.getLightOptions();
		}

		if (this.curArticle != null)
		{
			LocalBroadcastManager.getInstance(this.act).registerReceiver(articleReceiver,
			new IntentFilter(this.curArticle.getUrl()));

			//reciver for loading status
			LocalBroadcastManager.getInstance(this.act).registerReceiver(categoryIsLoadingReceiver,
			new IntentFilter(curArticle.getUrl() + Const.Action.IS_LOADING));
		}
	}

	private BroadcastReceiver categoryIsLoadingReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			//Log.i(LOG + categoryToLoad, "catgoryIsLoadingReceiver onReceive called");
			boolean isCurrentlyLoading = intent.getBooleanExtra(Const.Action.IS_LOADING, false);
			if (isCurrentlyLoading)
			{
				swipeRef.setRefreshing(true);
			}
			else
			{
				swipeRef.setRefreshing(false);
			}
		}
	};

	private BroadcastReceiver articleReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.i(LOG + curArticle.getUrl(), "articleReceiver onReceive");
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
				Log.i(LOG, a.getTitle() + " have been loaded");
				curArticle = a;
				checkCurArtInfo(null);
			}
		}
	};

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//		System.out.println("ArticleFragment onCreateView");
		this.inflater = inflater;
		View v = inflater.inflate(R.layout.fragment_art, container, false);

		//find all views
		this.findViews(v);

		//check for existing article's text in ArtInfo obj. If it's null or empty - start download
		this.checkCurArtInfo(savedInstanceState);

		return v;
	}

	private void checkCurArtInfo(Bundle savedInstanceState)
	{
		if (this.curArticle == null)
		{
			//load...
			//			this.loadArticle(false);
			this.swipeRef.setRefreshing(true);
		}
		else
		{
			if (/* this.curArticle.getArtText() == null || */this.curArticle.getArtText().equals(Const.EMPTY_STRING))
			{
				//load...
				this.loadArticle(false);

				this.fillFielsdsWithInfo();

				//setting size of Images and text
				this.setSizeAndTheme();
				//End of setting size of Images and text
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
						Actions.showComments(allArtsInfo, getPosition(), act);
					}
				});

				//				this.fillFielsdsWithInfo(v);
				this.fillFielsdsWithInfo();

				//setting size of Images and text
				this.setSizeAndTheme();
				//End of setting size of Images and text

				//scroll to previous position
				if (savedInstanceState != null && savedInstanceState.keySet().contains("ARTICLE_SCROLL_POSITION"))
				{
					final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
					if (position != null)
					{
						if (position != null)
						{
							scroll.post(new Runnable()
							{
								public void run()
								{
									scroll.scrollTo(position[0], position[1]);
								}
							});
						}
					}
				}
			}
		}
	}//check cur Article

	private void loadArticle(boolean startDownload)
	{
		this.swipeRef.setRefreshing(true);

		//		Intent intent = new Intent(this.act, ServiceArticle.class);
		//		intent.setAction(Const.Action.IS_LOADING);
		//		intent.putExtra(ARTICLE_URL, this.curArticle.getUrl());
		//		this.act.startService(intent);

		Intent intent = new Intent(this.act, ServiceArticle.class);
		intent.setAction(Const.Action.DATA_REQUEST);
		intent.putExtra(ARTICLE_URL, this.curArticle.getUrl());
		intent.putExtra("startDownload", startDownload);
		this.act.startService(intent);
	}

	private void findViews(View v)
	{
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
				//TODO
			}
		});

		this.articlesTextContainer=(LinearLayout) v.findViewById(R.id.articles_text_container);
		this.artTextView = (TextView) v.findViewById(R.id.art_text);

		this.scroll = (ScrollView) v.findViewById(R.id.art_scroll);

		this.artImageIV = (ImageView) v.findViewById(R.id.art_card_img);

		this.artTitleTV = (TextView) v.findViewById(R.id.art_title);

		this.authorLayout = (ViewGroup) v.findViewById(R.id.art_author_lin);
		this.artAuthorTV = (TextView) v.findViewById(R.id.art_author);
		this.artAuthorDescriptionTV = (TextView) v.findViewById(R.id.art_author_description);

		this.artDateTV = (TextView) v.findViewById(R.id.pub_date);
		this.artTagsMainTV = (TextView) v.findViewById(R.id.art_tags_main);

		this.artAuthorIV = (ImageView) v.findViewById(R.id.art_author_img);
		this.artAuthorArticlesIV = (ImageView) v.findViewById(R.id.art_author_all_arts_btn);
		this.artAuthorDescriptionIV = (ImageView) v.findViewById(R.id.art_author_description_btn);

		this.bottomPanel = (LinearLayout) v.findViewById(R.id.art_bottom_panel);
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

		//setShareIcon
		ImageView shareIcon = (ImageView) this.shareCard.findViewById(R.id.art_share_all);
		if (this.pref.getString("theme", "dark").equals("dark"))
		{
			shareIcon.setImageResource(R.drawable.ic_share_white_48dp);
		}
		else
		{
			shareIcon.setImageResource(R.drawable.ic_share_grey600_48dp);
		}

		this.commentsBottomBtn = (CardView) inflater.inflate(R.layout.comments_bottom_btn_layout, bottomPanel, false);
		//set onClickListener

		this.bottomPanel.addView(this.commentsBottomBtn);

		this.allTegsCard = (CardView) inflater.inflate(R.layout.all_tegs_layout, bottomPanel, false);
		this.alsoByThemeCard = (CardView) inflater.inflate(R.layout.also_by_theme, bottomPanel, false);
		this.alsoToReadCard = (CardView) inflater.inflate(R.layout.also_to_read, bottomPanel, false);
	}

	private void setUpAlsoByTheme()
	{
		Article.AlsoToRead alsoToRead = this.curArticle.getAlsoByTheme();
		//for test
		//		String[] s1 = new String[] { "title", "title" };
		//		String[] s2 = new String[] { "title", "title" };
		//		String[] s3 = new String[] { "title", "title" };
		//		alsoToRead = this.curArtInfo.new AlsoToRead(s1, s2, s3);
		if (alsoToRead != null)
		{

			this.bottomPanel.addView(this.alsoByThemeCard);
			LinearLayout mainLin = (LinearLayout) this.alsoByThemeCard.findViewById(R.id.also_main);
			LayoutInflater inflater = act.getLayoutInflater();
			for (int i = 0; i < alsoToRead.titles.length; i++)
			{
				CardView c = (CardView) inflater.inflate(R.layout.also_to_read_art_lay, mainLin, false);
				TextView title = (TextView) c.findViewById(R.id.title);
				title.setText(alsoToRead.titles[i]);
				TextView date = (TextView) c.findViewById(R.id.date);
				date.setText(alsoToRead.dates[i]);
				mainLin.addView(c);
			}
		}
	}

	private void setUpAlsoToRead()
	{
		Article.AlsoToRead alsoToRead = this.curArticle.getAlsoToReadMore();
		//for test
		//		String[] s1 = new String[] { "title", "title", "title" };
		//		String[] s2 = new String[] { "url", "url", "url" };
		//		String[] s3 = new String[] { "date", "date", "date" };
		//		alsoToRead = this.curArtInfo.new AlsoToRead(s1, s2, s3);
		if (alsoToRead != null)
		{

			this.bottomPanel.addView(this.alsoToReadCard);
			LinearLayout mainLin = (LinearLayout) this.alsoToReadCard.findViewById(R.id.also_main);
			LayoutInflater inflater = act.getLayoutInflater();
			for (int i = 0; i < alsoToRead.titles.length; i++)
			{
				CardView c = (CardView) inflater.inflate(R.layout.also_to_read_art_lay, mainLin, false);
				TextView title = (TextView) c.findViewById(R.id.title);
				title.setText(alsoToRead.titles[i]);
				TextView date = (TextView) c.findViewById(R.id.date);
				date.setText(alsoToRead.dates[i]);
				mainLin.addView(c);
			}
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
		this.artTagsMainTV.setTextSize(21 * scaleFactor);

		//images
		final float scale = getResources().getDisplayMetrics().density;
		int pixels = (int) (75 * scaleFactor * scale + 0.5f);
		LayoutParams params = new LayoutParams(pixels, pixels);

		//		int iconPxels = (int) (50 * scaleFactor * scale + 0.5f);
		//		LayoutParams iconsParams = new LayoutParams(iconPxels, iconPxels);

		this.artAuthorIV.setLayoutParams(params);
		this.artAuthorArticlesIV.setLayoutParams(params);
		this.artAuthorDescriptionIV.setLayoutParams(params);

		LayoutParams zeroHeightParams = new LayoutParams(LayoutParams.WRAP_CONTENT, 0);
		LayoutParams zeroAllParams = new LayoutParams(0, 0);
		LayoutParams normalParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		//removing tags field if it's empty
		if (this.curArticle.getTegsMain().equals(Const.EMPTY_STRING) || this.curArticle.getTegsMain().equals(""))
		{
			this.artTagsMainTV.setText(null);
			this.artTagsMainTV.setLayoutParams(zeroHeightParams);
		}
		else
		{
			this.artTagsMainTV.setText(this.curArticle.getTegsMain());
			this.artTagsMainTV.setLayoutParams(normalParams);
		}
		//set descr of author btn
		//set descrTV height to 0 by default
		this.artAuthorDescriptionTV.setLayoutParams(zeroHeightParams);

		if (this.curArticle.getAuthorDescr().equals(Const.EMPTY_STRING) || this.curArticle.getAuthorDescr().equals(""))
		{
			this.artAuthorDescriptionTV.setText(null);
			this.artAuthorDescriptionTV.setLayoutParams(zeroHeightParams);
			//
			this.artAuthorDescriptionIV.setLayoutParams(zeroAllParams);
		}
		else
		{
			this.artAuthorDescriptionTV.setText(this.curArticle.getAuthorDescr());
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
		if (this.curArticle.getAuthorBlogUrl().equals(Const.EMPTY_STRING)
		|| this.curArticle.getAuthorBlogUrl().equals(""))
		{
			this.artAuthorArticlesIV.setOnClickListener(null);
			this.artAuthorArticlesIV.setLayoutParams(zeroAllParams);
		}
		else
		{
			this.artAuthorArticlesIV.setLayoutParams(params);
			this.artAuthorArticlesIV.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					Actions.showAllAuthorsArticles(curArticle.getAuthorBlogUrl(), act);
				}
			});
		}

		//set share panel Size&Theme
		//		ImageView[] icons = new ImageView[6];
		//		TextView[] shareQ = new TextView[6];
		//		for (int i = 0; i < 6; i++)
		//		{
		//			icons[i] = (ImageView) this.shareCard.findViewById(this.getIdAssignedByR(act,
		//			"art_share_" + String.valueOf(i)));
		//			LayoutParams iconsParamsWithGravityCV = new LayoutParams(iconPxels, iconPxels);
		//			iconsParamsWithGravityCV.gravity = Gravity.CENTER_VERTICAL;
		//			icons[i].setLayoutParams(iconsParamsWithGravityCV);
		//			shareQ[i] = (TextView) this.shareCard.findViewById(this.getIdAssignedByR(act,
		//			"art_share_quont_" + String.valueOf(i)));
		//			shareQ[i].setTextSize(25 * scaleFactor);
		//		}
	}//setSizeAndTheme

	private void artAuthorDescrBehavior()
	{
		if (!artAuthorDescrIsShown)
		{
			//set and show text
			LayoutParams descrParams = new LayoutParams(LayoutParams.MATCH_PARENT,
			LayoutParams.WRAP_CONTENT);
			artAuthorDescriptionTV.setLayoutParams(descrParams);
			//set btn image
			if (pref.getString("theme", "dark").equals("dark"))
			{
				artAuthorDescriptionIV.setImageResource(R.drawable.ic_keyboard_arrow_up_white_48dp);
			}
			else
			{
				artAuthorDescriptionIV.setImageResource(R.drawable.ic_keyboard_arrow_up_grey600_48dp);
			}
			artAuthorDescrIsShown = !artAuthorDescrIsShown;
		}
		else
		{
			LayoutParams descrParams0 = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
			artAuthorDescriptionTV.setLayoutParams(descrParams0);
			//set btn image
			if (pref.getString("theme", "dark").equals("dark"))
			{
				artAuthorDescriptionIV.setImageResource(R.drawable.ic_keyboard_arrow_down_white_48dp);
			}
			else
			{
				artAuthorDescriptionIV.setImageResource(R.drawable.ic_keyboard_arrow_down_grey600_48dp);

			}
			artAuthorDescrIsShown = !artAuthorDescrIsShown;
		}
	}

	public int getIdAssignedByR(Context pContext, String pIdString)
	{
		// Get the Context's Resources and Package Name
		Resources resources = pContext.getResources();
		String packageName = pContext.getPackageName();

		// Determine the result and return it
		int result = resources.getIdentifier(pIdString, "id", packageName);
		return result;
	}

	//set text, tegs, author etc
	private void fillFielsdsWithInfo(/* View rootView */)
	{
		//variables for scaling text and icons and images from settings
		String scaleFactorString = pref.getString("scale", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);

		final float scale = act.getResources().getDisplayMetrics().density;
		int pixels = (int) (75 * scaleFactor * scale + 0.5f);
		////End of variables for scaling text and icons and images from settings

		// ART_IMG
		if (!curArticle.getImgArt().equals(Const.EMPTY_STRING) && !curArticle.getImgArt().contains("/75_75/"))
		{
			LayoutParams params = (LayoutParams) this.artImageIV.getLayoutParams();
			params.height = (int) DipToPx.convert(120, act);
			this.artImageIV.setLayoutParams(params);
			String HDimgURL = this.curArticle.getImgArt().replace("/120_72/", "/450_240/");

			imageLoader.displayImage(HDimgURL, this.artImageIV, options, new ImgLoadListenerBigSmall(
			imageLoader, options, this.artImageIV));
		}
		else
		{
			LayoutParams params = (LayoutParams) this.artImageIV.getLayoutParams();
			params.height = 0;
			this.artImageIV.setLayoutParams(params);
		}
		//end of ART_IMG

		this.setArticlesText();
		//		this.artTextView.setText(Html.fromHtml(this.curArticle.getArtText()));

		this.artTitleTV.setText(Html.fromHtml(this.curArticle.getTitle()));

		String dateToShow = DateParse.formatDateByCurTime(this.curArticle.getPubDate());
		this.artDateTV.setText(Html.fromHtml(dateToShow));
		this.artTagsMainTV.setText(this.curArticle.getTegsMain());

		//AUTHOR
		if (!this.curArticle.getAuthorName().equals(Const.EMPTY_STRING))
		{
			LayoutParams p = (LayoutParams) this.authorLayout.getLayoutParams();
			p.height = LayoutParams.MATCH_PARENT;
			p.width = LayoutParams.MATCH_PARENT;
			p.setMargins(0, 0, 0, 0);
			this.authorLayout.setLayoutParams(p);

			this.artAuthorTV.setText(this.curArticle.getAuthorName());
			this.artAuthorDescriptionTV.setText(Html.fromHtml(this.curArticle.getAuthorDescr()));
			if (!this.curArticle.getImgAuthor().equals(Const.EMPTY_STRING))
			{
				LayoutParams params = (LayoutParams) this.artAuthorIV.getLayoutParams();
				params.height = pixels;
				params.width = pixels;
				params.setMargins(5, 5, 5, 5);
				this.artAuthorIV.setLayoutParams(params);

				this.imageLoader.displayImage(this.curArticle.getImgAuthor(), this.artAuthorIV,
				MyUIL.getTransparentBackgroundROUNDOptions(act));
			}
			else
			{
				LayoutParams params = (LayoutParams) this.artAuthorIV.getLayoutParams();
				params.height = 0;
				params.width = 0;
				params.setMargins(0, 0, 0, 0);
				this.artAuthorIV.setLayoutParams(params);
			}
		}
		else
		{
			LayoutParams params = (LayoutParams) this.authorLayout.getLayoutParams();
			params.height = 0;
			params.width = 0;
			params.setMargins(0, 0, 0, 0);
			this.authorLayout.setLayoutParams(params);
		}
		//fill bottom
		this.setUpAllTegsLayout();
		this.setUpAlsoByTheme();
		this.setUpAlsoToRead();
	}

	private void setArticlesText()
	{
//		ViewGroup artTvParent = (ViewGroup) this.artTextView.getParent();
		String articleString = this.curArticle.getArtText().replaceAll("<br />", "\n");
		HtmlCleaner cleaner = new HtmlCleaner();
		TagNode articleTextTag = cleaner.clean(articleString);//.getChildTags();//.getAllElements(true);
		//it's unexpectable, but this TagNode have "head" and "body" tags...
		//So we only need innerHTML from "body" tag;
		TagNode[] articlesTags = articleTextTag.findElementByName("body", true).getChildTags();

		TagNode formatedArticle = extractImgTags(articlesTags);
		//		for (TagNode a : formatedArticle.getChildTagList())
		//		{
		//			Log.e(LOG, cleaner.getInnerHtml(a));
		//		}
//		String innerHtml = cleaner.getInnerHtml(formatedArticle);
//		if(formatedArticle.getChildTags().length!=0)
//		{
//			String innerHtml = cleaner.getInnerHtml(formatedArticle.getChildTags()[0]);
//			this.artTextView.setText(Html.fromHtml(innerHtml));
//			this.artTextView.setText(Html.fromHtml(formatedArticle.getText().toString()));
//		}
		if(!this.curArticle.getArtText().equals(Const.EMPTY_STRING))
		{
			articlesTextContainer.removeView(artTextView);
			Log.e(LOG, "formatedArticle.getChildTags().length: "+formatedArticle.getChildTags().length);
			for(TagNode a:formatedArticle.getChildTags())
			{
				Log.e(LOG, a.getName());
				if(a.getName().equals("p"))
				{
					TextView tV=new TextView(act);
					LinearLayout.LayoutParams params=new LayoutParams(100, 100);//(LayoutParams) tV.getLayoutParams();
					params.height=LinearLayout.LayoutParams.WRAP_CONTENT;
					params.width=LinearLayout.LayoutParams.MATCH_PARENT;
					params.setMargins(5, 5, 5, 5);
					tV.setLayoutParams(params);
					tV.setText(Html.fromHtml(a.getText().toString()));
					tV.setTextSize(19);
					articlesTextContainer.addView(tV);
				}
				else if(a.getName().equals("img"))
				{
					ImageView iV=new ImageView(act);
					LinearLayout.LayoutParams params=new LayoutParams(100, 100);//(LayoutParams) iV.getLayoutParams();
					params.height=200;
					params.width=LinearLayout.LayoutParams.MATCH_PARENT;
					params.setMargins(5, 5, 5, 5);
					iV.setLayoutParams(params);
					articlesTextContainer.addView(iV);
					imageLoader.displayImage(a.getAttributeByName("src"), iV, options);
				}
			}
		}		
	}

	private TagNode extractImgTags(TagNode[] tags)
	{
		TagNode formatedArticle = new TagNode("div");
		HtmlCleaner cleaner = new HtmlCleaner();
		for (int i = 0; i < tags.length; i++)
		{
			TagNode curTag = tags[i];
			if (curTag.getName().equals("p"))
			{
				if (curTag.getChildTags().length != 0)
				{
					String tagHtml = cleaner.getInnerHtml(curTag);
					
					
					for (int u = 0; u < curTag.getChildTags().length; u++)
					{
						TagNode curInnerTag = curTag.getChildTags()[u];
						if (curInnerTag.getName().equals("input"))
						{
							//we must here create 2 new TagNodes with text before and after img or input node						
							String subStrStartsWithInput = tagHtml.substring(tagHtml.indexOf("<input "));
							String subStrWithInput = subStrStartsWithInput.substring(0,
							subStrStartsWithInput.indexOf(">") + 1);
//							Log.e("INPUT", subStrWithInput);
							tagHtml = tagHtml.replaceFirst(subStrWithInput, Article.DIVIDER);
							String[] dividedTag = tagHtml.split(Article.DIVIDER);
							//create new p tag with innerHtml of parent p tag from 0 to input tag
							//check if inputTag is not first in parent
							if (dividedTag.length != 0)
							{
								TagNode firstTag = new TagNode("p");
								firstTag.addChild(new ContentNode(dividedTag[0]));
								if (dividedTag.length == 1)
								{
									tagHtml = dividedTag[0];
								}
								else
								{
									tagHtml = dividedTag[1];
								}
								//add them to our formated tag
								formatedArticle.addChild(firstTag);
							}
							else
							{
								tagHtml = "";
							}
							//create img tag with info from input
							TagNode imgTag = new TagNode("img");
							imgTag.addAttribute("src", curInnerTag.getAttributeByName("src"));
							imgTag.addAttribute("style", curInnerTag.getAttributeByName("style"));
							//add them to our formated tag
							formatedArticle.addChild(imgTag);
							//and finally set innerHtml of our parent p tag to second part of our array

						}//if input tag
							//						else
						//						{
						//							TagNode firstTag = new TagNode(curInnerTag.getName());
						//							firstTag.setAttributes(firstTag.getAttributes());
						//							firstTag.addChild(new ContentNode(cleaner.getInnerHtml(curInnerTag)));
						//							formatedArticle.addChild(firstTag);
						//						}
						if (u == curTag.getChildTags().length - 1)
						{
							TagNode firstTag = new TagNode("p");
							firstTag.addChild(new ContentNode(tagHtml));
							formatedArticle.addChild(firstTag);
						}
					}//loop of p tag children
				}//if has children
				else
				{
//					Log.i(LOG, "curTag has no children");
					formatedArticle.addChild(curTag);
				}
			}//if tag is p
			else
			{
				formatedArticle.addChild(curTag);
			}
		}//loop of articles tags
		return formatedArticle;
	}

	private void setUpAllTegsLayout()
	{
		String[] allTegs = this.curArticle.getAllTagsArr();
		//allTegs = new String[] { "ddddddddddddddddddddhhhhhhhhhhhhhhfdhfjgfjfgdddddddddddddddd", "jhdjsdhjsdh", "jhddddddddddddddddjsdhjsdh", "jhdjsdhjsdh", "jhdjsdhjsdh", "jhdjsdhjsdh", "jhdjsdhjsdh", "jhdjsdhjsdh", "jhdjsdhjsdh" };
		if (allTegs != null)
		{
			this.bottomPanel.addView(this.allTegsCard);
			LinearLayout allTegsLin = (LinearLayout) allTegsCard.findViewById(R.id.all_tegs_lin);
			LinearLayout firstLin = (LinearLayout) allTegsCard.findViewById(R.id.first_tegs_lin);
			LayoutInflater inflater = act.getLayoutInflater();
			int curLinId = 0;
			LinearLayout curLinLay = firstLin;

			//max width
			int width;
			DisplayMetrics displayMetrics = act.getResources().getDisplayMetrics();
			if (this.twoPane)
			{
				width = displayMetrics.widthPixels / 4 * 3;
			}
			else
			{
				width = displayMetrics.widthPixels;
			}

			//			int vPad = rooView.getPaddingLeft() + rooView.getPaddingRight();
			int vPad = this.getView().getPaddingLeft() + this.getView().getPaddingRight();
			int bPad = this.bottomPanel.getPaddingLeft() + this.bottomPanel.getPaddingRight();
			int cPad = this.allTegsCard.getPaddingLeft() + this.allTegsCard.getPaddingRight();
			int minusPaddings = vPad + bPad + cPad;
			//		System.out.println("width: " + width);
			//		System.out.println("minusPaddings: "+minusPaddings);
			width -= minusPaddings;
			//		System.out.println("minusPaddings: "+minusPaddings);
			int minHeight = 0;
			//		System.out.println("width: " + width);
			//
			for (int i = 0; i < allTegs.length; i++)
			{

				CardView c = (CardView) inflater.inflate(R.layout.teg_card, curLinLay, false);
				TextView tag = (TextView) c.findViewById(R.id.teg_tv);
				tag.setText(allTegs[i]);
				curLinLay.addView(c);

				//calculate total linLay width
				int curLinChildrenWidth = 0;

				for (int u = 0; u < curLinLay.getChildCount(); u++)
				{
					curLinLay.getChildAt(u).measure(0, 0);
					curLinChildrenWidth += curLinLay.getChildAt(u).getMeasuredWidth();
				}
				//plus 10*2 (2xpaddings of each tag
				curLinChildrenWidth += curLinLay.getChildCount() * 10 * 2;
				if (i == 0)
				{
					curLinLay.getChildAt(1).measure(0, 0);
					minHeight = curLinLay.getChildAt(1).getMeasuredHeight();
				}
				//curLinLay.getChildAt(curLinLay.getChildCount()-1).measure(0, 0);
				int height = curLinLay.getChildAt(curLinLay.getChildCount() - 1).getMeasuredHeight();
				//check if it's too much
				//must check not device, but View width
				//so if it's planshet we must take only 3/4 of device width
				//			System.out.println("curLinChildrenWidth: " + curLinChildrenWidth+"/ width: " + width);
				//			System.out.println("height: " + height+"/ minHeight: " + minHeight);

				if (curLinChildrenWidth >= width || height > minHeight)
				{
					curLinId++;
					LinearLayout nextLin = new LinearLayout(act);
					nextLin.setOrientation(LinearLayout.HORIZONTAL);
					LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					nextLin.setLayoutParams(params);
					nextLin.setId(curLinId);

					//remove previous and add it to next
					curLinLay.removeView(c);
					curLinLay = nextLin;
					curLinLay.addView(c);

					allTegsLin.addView(curLinLay);
				}
			}
		}
	}//setUpAllTagsLayout

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		//		System.out.println("ArticleFragment onSaveInstanceState");
		super.onSaveInstanceState(outState);

		//save scrollView position
		outState.putIntArray("ARTICLE_SCROLL_POSITION", new int[] { scroll.getScrollX(), scroll.getScrollY() });

		outState.putInt("position", this.getPosition());
		outState.putParcelable(Article.KEY_CURENT_ART, curArticle);
		outState.putParcelableArrayList(Article.KEY_ALL_ART_INFO, allArtsInfo);
	}

	@Override
	public void update(ArrayList<Article> allArtInfo)
	{
		this.curArticle = allArtInfo.get(getPosition());
		//Log.i(LOG + curArticle.getUrl(), "update called");
		LocalBroadcastManager.getInstance(act).unregisterReceiver(articleReceiver);
		LocalBroadcastManager.getInstance(this.act).registerReceiver(articleReceiver,
		new IntentFilter(this.curArticle.getUrl()));
		this.checkCurArtInfo(null);
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
		if (categoryIsLoadingReceiver != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(categoryIsLoadingReceiver);
			categoryIsLoadingReceiver = null;
		}
		// Must always call the super method at the end.
		super.onDestroy();
	}
}