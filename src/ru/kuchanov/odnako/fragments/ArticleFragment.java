/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.lists_and_utils.Actions;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.utils.UniversalImageLoader;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
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

public class ArticleFragment extends Fragment
{
	private ActionBarActivity act;
	private LayoutInflater inflater;
	private SharedPreferences pref;
	private boolean twoPane;

	private ImageLoader imageLoader;

	private TextView artTextView;

	private ScrollView scroll;

	private TextView artTitleTV;
	private TextView artAuthorTV;
	private TextView artAuthorDescriptionTV;

	private TextView artDateTV;
	private TextView artTagsMainTV;

	private ImageView artAuthorIV;
	private ImageView artAuthorDescriptionIV;
	private ImageView artAuthorArticlesIV;

	LinearLayout bottomPanel;
	CardView shareCard;
	CardView commentsBottomBtn;
	CardView allTegsCard;
	CardView alsoByThemeCard;
	CardView alsoToReadCard;

	private ArtInfo curArtInfo;
	int position;/* position in all art arr; need to show next/previous arts */
	ArrayList<ArtInfo> allArtsInfo;

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
		else if (savedState != null)
		{
			this.restoreState(savedState);
		}
		//all is null, so start request for info
		else
		{
			// TODO
			System.out.println("ActivityArticle: all bundles are null, so make request for info");
		}

		this.imageLoader = UniversalImageLoader.get(act);

		this.curArtInfo = new ArtInfo(this.getArguments().getStringArray("curArtInfo"));
		this.position = this.getArguments().getInt("position");
		//restore AllArtsInfo
		this.allArtsInfo = new ArrayList<ArtInfo>();
		Set<String> keySet = this.getArguments().keySet();
		ArrayList<String> keySetSortedArrList = new ArrayList<String>(keySet);
		Collections.sort(keySetSortedArrList);
		for (int i = 0; i < keySetSortedArrList.size(); i++)
		{
			if (keySetSortedArrList.get(i).startsWith("allArtsInfo_"))
			{
				if (i < 10)
				{
					this.allArtsInfo.add(new ArtInfo(this.getArguments().getStringArray(
					"allArtsInfo_0" + String.valueOf(i))));
				}
				else
				{
					this.allArtsInfo.add(new ArtInfo(this.getArguments().getStringArray(
					"allArtsInfo_" + String.valueOf(i))));
				}

			}
			else
			{
				break;
			}
		}

		pref = PreferenceManager.getDefaultSharedPreferences(act);
		this.twoPane = pref.getBoolean("twoPane", false);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//		System.out.println("ArticleFragment onCreateView");
		View v = inflater.inflate(R.layout.fragment_art, container, false);

		this.inflater = inflater;

		//find all views
		this.findViews(v);

		this.fillFielsdsWithInfo(v);

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

		return v;
	}

	private void findViews(View v)
	{
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
		//		if(twoPane) we must set width to width/4*3 
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
		this.shareCard.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Toast.makeText(act, "share!", Toast.LENGTH_SHORT).show();
			}
		});
		//setShareIcon
		ImageView shareIcon = (ImageView) this.shareCard.findViewById(R.id.art_share_all);
		System.out.println("shareIcon==null: "+String.valueOf(shareIcon==null));
		if (this.pref.getString("theme", "dark").equals("dark"))
		{
//			this.imageLoader.displayImage("drawable://" + R.drawable.ic_share_white_48dp, shareIcon);
			shareIcon.setImageResource(R.drawable.ic_share_white_48dp);//.setImageDrawable(this.act.getDrawable(R.drawable.ic_share_white_48dp));
		}
		else
		{
//			this.imageLoader.displayImage("drawable://" + R.drawable.ic_share_grey600_48dp, shareIcon);
			shareIcon.setImageResource(R.drawable.ic_share_grey600_48dp);
		}

		this.commentsBottomBtn = (CardView) inflater.inflate(R.layout.comments_bottom_btn_layout, bottomPanel, false);
		//set onClickListener
		this.commentsBottomBtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Actions.showComments(allArtsInfo, position, act);
			}
		});
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

	private void setUpAlsoByTheme()
	{
		ArtInfo.AlsoToRead alsoToRead = this.curArtInfo.getAlsoByTheme();
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
		ArtInfo.AlsoToRead alsoToRead = this.curArtInfo.getAlsoToReadMore();
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
		if (this.curArtInfo.tegs_main.equals("empty") || this.curArtInfo.tegs_main.equals(""))
		{
			this.artTagsMainTV.setText(null);
			this.artTagsMainTV.setLayoutParams(zeroHeightParams);
		}
		else
		{
			this.artTagsMainTV.setText(this.curArtInfo.tegs_main);
			this.artTagsMainTV.setLayoutParams(normalParams);
		}
		//set descr of author btn
		//set descrTV height to 0 by default
		this.artAuthorDescriptionTV.setLayoutParams(zeroHeightParams);

		if (this.curArtInfo.authorDescr.equals("empty") || this.curArtInfo.authorDescr.equals(""))
		{
			this.artAuthorDescriptionTV.setText(null);
			this.artAuthorDescriptionTV.setLayoutParams(zeroHeightParams);
			//
			this.artAuthorDescriptionIV.setLayoutParams(zeroAllParams);
		}
		else
		{
			this.artAuthorDescriptionTV.setText(this.curArtInfo.authorDescr);
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
		if (this.curArtInfo.authorBlogUrl.equals("empty") || this.curArtInfo.authorBlogUrl.equals(""))
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
					Actions.showAllAuthorsArticles(curArtInfo, act);
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
//				imageLoader.displayImage("drawable://" + R.drawable.ic_keyboard_arrow_up_white_48dp,
//				artAuthorDescriptionIV);
				artAuthorDescriptionIV.setImageResource(R.drawable.ic_keyboard_arrow_up_white_48dp);
			}
			else
			{
//				imageLoader.displayImage("drawable://" + R.drawable.ic_keyboard_arrow_up_grey600_48dp,
//				artAuthorDescriptionIV);
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
		this.artTextView.setText(this.curArtInfo.artText);

		this.artTitleTV.setText(this.curArtInfo.title);
		this.artAuthorTV.setText(this.curArtInfo.authorName);
		this.artAuthorDescriptionTV.setText(this.curArtInfo.authorDescr);
		this.artDateTV.setText(this.curArtInfo.pubDate);
		this.artTagsMainTV.setText(this.curArtInfo.tegs_main);

		//down images
		if (this.pref.getString("theme", "dark").equals("dark"))
		{
			imageLoader.displayImage(this.curArtInfo.img_art, this.artAuthorIV, UniversalImageLoader.getDarkOptions());
//			imageLoader.displayImage("drawable://" + R.drawable.ic_list_white_48dp, this.artAuthorArticlesIV);
			this.artAuthorArticlesIV.setImageResource(R.drawable.ic_list_white_48dp);
//			imageLoader.displayImage("drawable://" + R.drawable.ic_keyboard_arrow_down_white_48dp,
//			this.artAuthorDescriptionIV);
			this.artAuthorDescriptionIV.setImageResource(R.drawable.ic_keyboard_arrow_down_white_48dp);
		}
		else
		{
			imageLoader.displayImage(this.curArtInfo.img_art, this.artAuthorIV);
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

	@Override
	public void onAttach(Activity activity)
	{
		//		System.out.println("ArticleFragment onAttach position: "+this.position);
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

		outState.putInt("position", this.position);
		ArtInfo.writeAllArtsInfoToBundle(outState, allArtsInfo, getCurArtInfo());
	}

	private void restoreState(Bundle state)
	{
		this.curArtInfo = new ArtInfo(state.getStringArray("curArtInfo"));
		this.position = state.getInt("position");
		//restore AllArtsInfo
		this.allArtsInfo = new ArrayList<ArtInfo>();
		Set<String> keySet = state.keySet();
		ArrayList<String> keySetSortedArrList = new ArrayList<String>(keySet);
		Collections.sort(keySetSortedArrList);
		for (int i = 0; i < keySetSortedArrList.size(); i++)
		{
			if (keySetSortedArrList.get(i).startsWith("allArtsInfo_"))
			{
				if (i < 10)
				{
					this.allArtsInfo.add(new ArtInfo(state.getStringArray("allArtsInfo_0"
					+ String.valueOf(i))));
				}
				else
				{
					this.allArtsInfo.add(new ArtInfo(state.getStringArray("allArtsInfo_"
					+ String.valueOf(i))));
				}

			}
			else
			{
				break;
			}
		}
	}

	public ArtInfo getCurArtInfo()
	{
		this.curArtInfo = new ArtInfo(this.getArguments().getStringArray("curArtInfo"));
		return this.curArtInfo;
	}

}
