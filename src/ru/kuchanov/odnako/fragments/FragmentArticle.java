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
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.lists_and_utils.Actions;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.utils.MyUIL;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
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

public class FragmentArticle extends Fragment implements FragArtUPD
{

	private ActionBarActivity act;
	private LayoutInflater inflater;
	private SharedPreferences pref;
	private boolean twoPane;

	private ImageLoader imageLoader;

	private TextView artTextView;

	private ScrollView scroll;
	SwipeRefreshLayout swipeRef;

	private TextView artTitleTV;
	private TextView artAuthorTV;
	private TextView artAuthorDescriptionTV;

	private TextView artDateTV;
	private TextView artTagsMainTV;

	private ImageView artAuthorIV;
	private ImageView artAuthorDescriptionIV;
	private ImageView artAuthorArticlesIV;

	private LinearLayout bottomPanel;
	private CardView shareCard;
	private CardView commentsBottomBtn;
	private CardView allTegsCard;
	private CardView alsoByThemeCard;
	private CardView alsoToReadCard;

	private Article curArtInfo;
//	position in all art arr; need to show next/previous arts
	private int position;
	ArrayList<Article> allArtsInfo;

	private boolean artAuthorDescrIsShown = false;
	
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
			this.restoreState(stateFromArgs);
		}
		//		else if (savedState != null)
		//		{
		//			this.restoreState(savedState);
		//		}
		//		//all is null, so start request for info
		//		else
		//		{
		//			// TODO
		//			System.out.println("ActivityArticle: all bundles are null, so make request for info");
		//		}
		if (savedState != null)
		{
			this.restoreState(savedState);
		}

		this.imageLoader = MyUIL.get(act);

		pref = PreferenceManager.getDefaultSharedPreferences(act);
		this.twoPane = pref.getBoolean("twoPane", false);

	}

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
		if (this.curArtInfo == null)
		{
			//load...
			this.swipeRef.setRefreshing(true);
		}
		else
		{
			if (this.curArtInfo.getArtText() == null || this.curArtInfo.getArtText().equals("empty"))
			{
				//load...
				this.swipeRef.setRefreshing(true);

				this.fillFielsdsWithInfo(this.getView());

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
						Actions.shareUrl(curArtInfo.getUrl(), act);
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
				this.fillFielsdsWithInfo(this.getView());

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

		this.artTextView = (TextView) v.findViewById(R.id.art_text);
		//		this.artTextView.setText(R.string.version_history);

		this.scroll = (ScrollView) v.findViewById(R.id.art_scroll);

		this.artTitleTV = (TextView) v.findViewById(R.id.art_title);
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

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		//		System.out.println("ArticleFragment onViewCreated");
		super.onViewCreated(view, savedInstanceState);

	}

	private void setUpAlsoByTheme()
	{
		Article.AlsoToRead alsoToRead = this.curArtInfo.getAlsoByTheme();
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
		Article.AlsoToRead alsoToRead = this.curArtInfo.getAlsoToReadMore();
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

		int iconPxels = (int) (50 * scaleFactor * scale + 0.5f);
		LayoutParams iconsParams = new LayoutParams(iconPxels, iconPxels);

		this.artAuthorIV.setLayoutParams(params);
		this.artAuthorArticlesIV.setLayoutParams(iconsParams);
		this.artAuthorDescriptionIV.setLayoutParams(iconsParams);

		LayoutParams zeroHeightParams = new LayoutParams(LayoutParams.WRAP_CONTENT, 0);
		LayoutParams zeroAllParams = new LayoutParams(0, 0);
		LayoutParams normalParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		//removing tags field if it's empty
		if (this.curArtInfo.getTegs_main().equals("empty") || this.curArtInfo.getTegs_main().equals(""))
		{
			this.artTagsMainTV.setText(null);
			this.artTagsMainTV.setLayoutParams(zeroHeightParams);
		}
		else
		{
			this.artTagsMainTV.setText(this.curArtInfo.getTegs_main());
			this.artTagsMainTV.setLayoutParams(normalParams);
		}
		//set descr of author btn
		//set descrTV height to 0 by default
		this.artAuthorDescriptionTV.setLayoutParams(zeroHeightParams);

		if (this.curArtInfo.getAuthorDescr().equals("empty") || this.curArtInfo.getAuthorDescr().equals(""))
		{
			this.artAuthorDescriptionTV.setText(null);
			this.artAuthorDescriptionTV.setLayoutParams(zeroHeightParams);
			//
			this.artAuthorDescriptionIV.setLayoutParams(zeroAllParams);
		}
		else
		{
			this.artAuthorDescriptionTV.setText(this.curArtInfo.getAuthorDescr());
			//restore size
			//			this.artAuthorDescriptionIV.setPadding(5, 5, 5, 5);
			//			this.artAuthorDescriptionIV.setScaleType(ScaleType.FIT_XY);
			this.artAuthorDescriptionIV.setLayoutParams(iconsParams);
			if (this.pref.getString("theme", "dark").equals("dark"))
			{
				//				imageLoader.displayImage("drawable://" + R.drawable.ic_keyboard_arrow_down_white_48dp,
				//				artAuthorDescriptionIV);
				artAuthorDescriptionIV.setImageResource(R.drawable.ic_keyboard_arrow_down_white_48dp);
			}
			else
			{
				//				imageLoader.displayImage("drawable://" + R.drawable.ic_keyboard_arrow_down_grey600_48dp,
				//				artAuthorDescriptionIV);
				artAuthorDescriptionIV.setImageResource(R.drawable.ic_keyboard_arrow_down_grey600_48dp);
			}

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
		if (this.curArtInfo.getAuthorBlogUrl().equals("empty") || this.curArtInfo.getAuthorBlogUrl().equals(""))
		{
			this.artAuthorArticlesIV.setOnClickListener(null);
			this.artAuthorArticlesIV.setLayoutParams(zeroAllParams);
		}
		else
		{
			this.artAuthorArticlesIV.setLayoutParams(iconsParams);
			this.artAuthorArticlesIV.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					Actions.showAllAuthorsArticles(curArtInfo.getAuthorBlogUrl(), act);
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

	//set text, tegs, authoe etc
	private void fillFielsdsWithInfo(View rootView)
	{
		this.artTextView.setText(this.curArtInfo.getArtText());

		this.artTitleTV.setText(this.curArtInfo.getTitle());
		this.artAuthorTV.setText(this.curArtInfo.getAuthorName());
		this.artAuthorDescriptionTV.setText(this.curArtInfo.getAuthorDescr());
		this.artDateTV.setText(this.curArtInfo.getPubDate().toString());
		this.artTagsMainTV.setText(this.curArtInfo.getTegs_main());

		//down images
		if (this.pref.getString("theme", "dark").equals("dark"))
		{
			imageLoader.displayImage(this.curArtInfo.getImgArt(), this.artAuthorIV, MyUIL.getDarkOptions());
			//			imageLoader.displayImage("drawable://" + R.drawable.ic_list_white_48dp, this.artAuthorArticlesIV);
			this.artAuthorArticlesIV.setImageResource(R.drawable.ic_list_white_48dp);
			//			imageLoader.displayImage("drawable://" + R.drawable.ic_keyboard_arrow_down_white_48dp,
			//			this.artAuthorDescriptionIV);
			this.artAuthorDescriptionIV.setImageResource(R.drawable.ic_keyboard_arrow_down_white_48dp);
		}
		else
		{
			imageLoader.displayImage(this.curArtInfo.getImgArt(), this.artAuthorIV);
			//			imageLoader.displayImage("drawable://" + R.drawable.ic_list_grey600_48dp, this.artAuthorArticlesIV);
			this.artAuthorArticlesIV.setImageResource(R.drawable.ic_list_grey600_48dp);
			//			imageLoader.displayImage("drawable://" + R.drawable.ic_keyboard_arrow_down_grey600_48dp,
			//			this.artAuthorDescriptionIV);
			this.artAuthorDescriptionIV.setImageResource(R.drawable.ic_keyboard_arrow_down_grey600_48dp);
		}

		//fill bottom
		this.setUpAllTegsLayout(rootView);
		this.setUpAlsoByTheme();
		this.setUpAlsoToRead();
	}

	private void setUpAllTegsLayout(View rooView)
	{
		String[] allTegs = this.curArtInfo.getAllTegsArr();
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

			int vPad = rooView.getPaddingLeft() + rooView.getPaddingRight();
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
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}

	@Override
	public void onDetach()
	{
		//		System.out.println("ArticleFragment onDetach");
		super.onDetach();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		//		System.out.println("ArticleFragment onSaveInstanceState");
		super.onSaveInstanceState(outState);

		//save scrollView position
		outState.putIntArray("ARTICLE_SCROLL_POSITION", new int[] { scroll.getScrollX(), scroll.getScrollY() });

		outState.putInt("position", this.getPosition());
		outState.putParcelable(ArtInfo.KEY_CURENT_ART, curArtInfo);
		outState.putParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO, allArtsInfo);
	}

	private void restoreState(Bundle state)
	{
		this.curArtInfo = state.getParcelable(ArtInfo.KEY_CURENT_ART);
		this.setPosition(state.getInt("position"));
		this.allArtsInfo = state.getParcelableArrayList(ArtInfo.KEY_ALL_ART_INFO);
	}

	@Override
	public void update(ArrayList<Article> allArtInfo)
	{
		this.curArtInfo = allArtInfo.get(getPosition());
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
}