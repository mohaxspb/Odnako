/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.util.ArrayList;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.custom.view.FlowLayout;
import ru.kuchanov.odnako.custom.view.JBTextView;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Article.Tag;
import ru.kuchanov.odnako.db.Msg;
import ru.kuchanov.odnako.db.ServiceArticle;
import ru.kuchanov.odnako.lists_and_utils.Actions;
import ru.kuchanov.odnako.utils.DateParse;
import ru.kuchanov.odnako.utils.DipToPx;
import ru.kuchanov.odnako.utils.HtmlTextFormatting;
import ru.kuchanov.odnako.utils.ImgLoadListenerBigSmall;
import ru.kuchanov.odnako.utils.MyUIL;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
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
	//	private ImageView artAuthorArticlesIV;

	//	private TextView artTagsMainTV;
	private FlowLayout artTagsMain;

	private LinearLayout bottomPanel;
	private CardView shareCard;
	private CardView commentsBottomBtn;
	private CardView allTagsCard;
	private CardView alsoByThemeCard;
	private CardView alsoToReadCard;

	private Article curArticle;
	//	position in all art arr; need to show next/previous arts
	private int position;
	//	ArrayList<Article> allArtsInfo;

	private boolean artAuthorDescrIsShown = false;

	private DisplayImageOptions options;

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
			this.setPosition(stateFromArgs.getInt("position"));
			//			this.allArtsInfo = stateFromArgs.getParcelableArrayList(Article.KEY_ALL_ART_INFO);
		}
		if (savedState != null)
		{
			this.curArticle = savedState.getParcelable(Article.KEY_CURENT_ART);
			this.setPosition(savedState.getInt("position"));
			//			this.allArtsInfo = savedState.getParcelableArrayList(Article.KEY_ALL_ART_INFO);
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
		}
	}

	private BroadcastReceiver articleReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.e(LOG + curArticle.getUrl(), "articleReceiver onReceive");
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
				Log.i(LOG, a.getTitle() + " have been loaded");
				long beforeTime = System.currentTimeMillis();
				Log.e(LOG, "start fill fragment with info");
				checkCurArtInfo(null, (ViewGroup) getView());
				Log.e(LOG,
				"END fill fragment with info. TIME: " + String.valueOf((System.currentTimeMillis() - beforeTime)));
			}
		}
	};

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//Log.i(LOG, "ArticleFragment onCreateView");
		this.inflater = inflater;
		View v = this.inflater.inflate(R.layout.fragment_art, container, false);

		//find all views
		this.findViews(v);

		//check for existing article's text in ArtInfo obj. If it's null or empty - start download
		this.update(curArticle);

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

				this.fillFielsdsWithInfo();

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

		LinearLayout mainLinLayInScroll = (LinearLayout) v.findViewById(R.id.main_lin_lay_article_frags_scroll);
		CardView artCard;
		if (android.os.Build.VERSION.SDK_INT != 16)
		{
			artCard = (CardView) this.inflater.inflate(R.layout.article_card_art_frag, mainLinLayInScroll, false);
		}
		else
		{
			artCard = (CardView) this.inflater.inflate(R.layout.article_card_art_frag_jb, mainLinLayInScroll, false);
		}
		mainLinLayInScroll.addView(artCard, 1);

		this.articlesTextContainer = (LinearLayout) v.findViewById(R.id.articles_text_container);
		this.artTextView = (TextView) v.findViewById(R.id.art_text);

		this.scroll = (ScrollView) v.findViewById(R.id.art_scroll);

		this.artImageIV = (ImageView) v.findViewById(R.id.art_card_img);

		this.artTitleTV = (TextView) v.findViewById(R.id.art_title);

		this.authorLayout = (ViewGroup) v.findViewById(R.id.art_author_lin);
		this.artAuthorTV = (TextView) v.findViewById(R.id.art_author);
		this.artAuthorDescriptionTV = (TextView) v.findViewById(R.id.art_author_description);

		this.artDateTV = (TextView) v.findViewById(R.id.pub_date);
		//		this.artTagsMainTV = (TextView) v.findViewById(R.id.art_tags_main);

		this.artAuthorIV = (ImageView) v.findViewById(R.id.art_author_img);
		//		this.artAuthorArticlesIV = (ImageView) v.findViewById(R.id.art_author_all_arts_btn);
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

		this.commentsBottomBtn = (CardView) inflater.inflate(R.layout.comments_bottom_btn_layout, bottomPanel, false);
		//set onClickListener

		this.bottomPanel.addView(this.commentsBottomBtn);

		this.artTagsMain = (FlowLayout) v.findViewById(R.id.art_tags_main);
		this.allTagsCard = (CardView) inflater.inflate(R.layout.all_tegs_layout, bottomPanel, false);
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
		//		this.artTagsMainTV.setTextSize(21 * scaleFactor);

		//images
		final float scale = getResources().getDisplayMetrics().density;
		int pixels = (int) (75 * scaleFactor * scale + 0.5f);
		LayoutParams params = new LayoutParams(pixels, pixels);

		//		int iconPxels = (int) (50 * scaleFactor * scale + 0.5f);
		//		LayoutParams iconsParams = new LayoutParams(iconPxels, iconPxels);

		this.artAuthorIV.setLayoutParams(params);
		//		this.artAuthorArticlesIV.setLayoutParams(params);
		this.artAuthorDescriptionIV.setLayoutParams(params);

		LayoutParams zeroHeightParams = new LayoutParams(LayoutParams.WRAP_CONTENT, 0);
		LayoutParams zeroAllParams = new LayoutParams(0, 0);

		//removing tags field if it's empty
		if (this.curArticle.getTegsMain().equals(Const.EMPTY_STRING) || this.curArticle.getTegsMain().equals(""))
		{
			this.artTagsMain.setLayoutParams(zeroHeightParams);
		}
		else
		{
			this.setUpMainTags();
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

	//set text, tegs, author etc
	private void fillFielsdsWithInfo()
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

		this.artTitleTV.setText(Html.fromHtml(this.curArticle.getTitle()));

		String dateToShow = DateParse.formatDateByCurTime(this.curArticle.getPubDate());
		this.artDateTV.setText(Html.fromHtml(dateToShow));
		LayoutParams zeroHeightParams = new LayoutParams(LayoutParams.WRAP_CONTENT, 0);
		if (this.curArticle.getTegsMain().equals(Const.EMPTY_STRING) || this.curArticle.getTegsMain().equals(""))
		{
			this.artTagsMain.setLayoutParams(zeroHeightParams);
		}
		else
		{
			this.setUpMainTags();
		}

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
		String articleString = this.curArticle.getArtText();//.replaceAll("<br />", "\n");
		HtmlCleaner cleaner = new HtmlCleaner();
		TagNode articleTextTag = cleaner.clean(articleString);
		//it's unexpectable, but this TagNode have "head" and "body" tags...
		//So we only need innerHTML from "body" tag;
		TagNode[] articlesTags = articleTextTag.findElementByName("body", true).getChildTags();

		TagNode formatedArticle = HtmlTextFormatting.format(articlesTags);

		if (!this.curArticle.getArtText().equals(Const.EMPTY_STRING))
		{
			articlesTextContainer.removeView(artTextView);
			for (int i = 0; i < formatedArticle.getChildTags().length; i++)
			{
				TagNode a = formatedArticle.getChildTags()[i];
				if (a.getName().equals("img"))
				{
					ImageView iV = new ImageView(act);
					LinearLayout.LayoutParams params = new LayoutParams(100, 100);
					params.height = 200;
					params.width = LinearLayout.LayoutParams.MATCH_PARENT;
					params.setMargins(5, 5, 5, 5);
					iV.setLayoutParams(params);
					articlesTextContainer.addView(iV);
					imageLoader.displayImage(a.getAttributeByName("src"), iV, options);
				}
				else
				{
					//check if previous tag was img and create new TextView, else append text to existing
					if (i == 0 || (i != 0 && formatedArticle.getChildTags()[i - 1].getName().equals("img")))
					{
						TextView tV;
						if (android.os.Build.VERSION.SDK_INT != 16)
						{
							tV = new TextView(act);
						}
						else
						{
							tV = new JBTextView(act);
						}
						LinearLayout.LayoutParams params = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
						params.setMargins(5, 5, 5, 5);
						tV.setLayoutParams(params);

						tV.setAutoLinkMask(Linkify.ALL);
						tV.setLinksClickable(true);
						tV.setMovementMethod(LinkMovementMethod.getInstance());

						tV.setText(Html.fromHtml("<" + a.getName() + ">" + a.getText().toString() + "</" + a.getName()
						+ ">"));
						tV.setTextSize(19);
						articlesTextContainer.addView(tV);
					}
					else
					{
						TextView tV;
						if (android.os.Build.VERSION.SDK_INT != 16)
						{
							tV = (TextView) articlesTextContainer
							.getChildAt(articlesTextContainer.getChildCount() - 1);
						}
						else
						{
							tV = (JBTextView) articlesTextContainer
							.getChildAt(articlesTextContainer.getChildCount() - 1);
						}

						tV.append(Html.fromHtml("<" + a.getName() + ">" + a.getText().toString() + "</" + a.getName()
						+ ">"));
					}
				}//if not img tag
			}//loop trough articles tags
		}//if Article.getArtText is not empty
	}

	private void setUpMainTags()
	{
		this.artTagsMain.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		String scaleFactorString = pref.getString("scale", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);

		artTagsMain.removeAllViews();
		ArrayList<Tag> allTagsList = this.curArticle.getTags(this.curArticle.getTegsMain());
		if (allTagsList.size() != 0)
		{
			for (int i = 0; i < allTagsList.size(); i++)
			{
				Tag tag = allTagsList.get(i);
				View tagCard = this.inflater.inflate(R.layout.item, artTagsMain, false);
				TextView tV = (TextView) tagCard.findViewById(R.id.tag);
				tV.setTextSize(21 * scaleFactor);
				tV.setText(tag.title);
				artTagsMain.addView(tagCard);
			}
		}
	}

	private void setUpAllTegsLayout()
	{
		ArrayList<Tag> allTagsList = this.curArticle.getTags(this.curArticle.getTagsAll());

		if (allTagsList.size() != 0)
		{
			this.bottomPanel.addView(this.allTagsCard);
			FlowLayout flowLay = (FlowLayout) allTagsCard.findViewById(R.id.flow);
			for (int i = 0; i < allTagsList.size(); i++)
			{
				Tag tag = allTagsList.get(i);
				View tagCard = this.inflater.inflate(R.layout.item, flowLay, false);
				TextView tV = (TextView) tagCard.findViewById(R.id.tag);
				String scaleFactorString = pref.getString("scale_art", "1");
				float scaleFactor = Float.valueOf(scaleFactorString);
				tV.setTextSize(21 * scaleFactor);
				tV.setText(tag.title);
				flowLay.addView(tagCard);
			}
		}
	}//setUpAllTagsLayout

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		//System.out.println("ArticleFragment onSaveInstanceState");
		super.onSaveInstanceState(outState);

		//save scrollView position
		outState.putIntArray("ARTICLE_SCROLL_POSITION", new int[] { scroll.getScrollX(), scroll.getScrollY() });

		outState.putInt("position", this.getPosition());
		outState.putParcelable(Article.KEY_CURENT_ART, curArticle);
		//		outState.putParcelableArrayList(Article.KEY_ALL_ART_INFO, allArtsInfo);
	}

	@Override
	//	public void update(ArrayList<Article> allArtInfo)
	public void update(Article allArtInfo)
	{
		this.curArticle = allArtInfo;
		//Log.i(LOG + curArticle.getUrl(), "update called");
		LocalBroadcastManager.getInstance(act).unregisterReceiver(articleReceiver);
		LocalBroadcastManager.getInstance(this.act).registerReceiver(articleReceiver,
		new IntentFilter(this.curArticle.getUrl()));

		long beforeTime = System.currentTimeMillis();
		Log.e(LOG, "start fill fragment with info");
		checkCurArtInfo(null, (ViewGroup) getView());
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
		// Must always call the super method at the end.
		super.onDestroy();
	}
}