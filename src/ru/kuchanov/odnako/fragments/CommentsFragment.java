/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.lists_and_utils.ArtsListAdapter;
import ru.kuchanov.odnako.lists_and_utils.CommentInfo;
import ru.kuchanov.odnako.lists_and_utils.CommentsListAdapter;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.utils.ReadUnreadRegister;
import ru.kuchanov.odnako.utils.UniversalImageLoader;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

public class CommentsFragment extends Fragment
{
	private ActionBarActivity act;
	private SharedPreferences pref;
	boolean twoPane;

	ArtInfo curArtInfo;
	ArrayList<ArtInfo> allArtsInfo;

	int position;

	ArrayList<CommentInfo> curArtCommentsInfoList;
	ArrayList<ArrayList<CommentInfo>> allArtsCommentsInfo;

	private ListView commentsListView;

	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		System.out.println("CommentsFragment onCreate");

		this.act = (ActionBarActivity) this.getActivity();

		pref = PreferenceManager.getDefaultSharedPreferences(act);
		this.twoPane = pref.getBoolean("twoPane", false);

		this.restoreState(this.getArguments());
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		System.out.println("CommentsFragment onCreateView");
		View v = inflater.inflate(R.layout.fragment_comments_list, new LinearLayout(act));

		//find all views
		this.commentsListView = (ListView) v.findViewById(R.id.comments_list_view);
		//end of find all views

		//fill Arraylist with artsInfo
		//		this.curArtCommentsInfoList= new ArrayList<CommentInfo>();
		//		int sampleNum = 30;
		//		for (int i = 0; i < sampleNum; i++)
		//		{
		//			defaultCommInfo=new String[12];
		//			this.defaultCommInfo[0] = "name_"+String.valueOf(i);
		//			this.defaultCommInfo[1] = "comment_text_"+String.valueOf(i);
		//			this.defaultCommInfo[2] = "https://pp.vk.me/c9733/u77102/151125793/w_91f2635a.jpg";
		//			this.defaultCommInfo[3] = "1 сентября 1939";
		//			this.defaultCommInfo[4] = "Saint-Petersburg";
		//			this.defaultCommInfo[5] = String.valueOf(i);
		//			this.defaultCommInfo[6] = String.valueOf(i);
		//			this.defaultCommInfo[7] = "https://pp.vk.me/c9733/u77102/151125793/w_91f2635a.jpg";
		//			this.defaultCommInfo[8] = "data_pid";
		//			this.defaultCommInfo[9] = "id";
		//			this.defaultCommInfo[10] = "0";
		//			this.defaultCommInfo[11] = "5";
		//			CommentInfo commentInfo=new CommentInfo(this.defaultCommInfo);
		//			
		//			this.curArtCommentsInfoList.add(commentInfo);
		//		}
		//		
		//		CommentInfo artInfoTEST=new CommentInfo("Юрий", "Тестовы коммент", "https://pp.vk.me/c9733/u77102/151125793/w_91f2635a.jpg", "1 сентября 1939", "Saint-Petersburg", "100", "0", "https://pp.vk.me/c9733/u77102/151125793/w_91f2635a.jpg", "100", "1000", "0", "5");
		//		
		//		curArtCommentsInfoList.set(1, artInfoTEST);
		//		//sample data now
		this.curArtCommentsInfoList = CommentInfo.getDefaultArtsCommentsInfo(20);

		CommentsListAdapter commentsListAdapter = new CommentsListAdapter(this.act, R.layout.comment_card_view,
		this.curArtCommentsInfoList, this.commentsListView);

		//add Header in not twoPane mode
		if (this.twoPane == false)
		{
			this.addHeader(inflater);
		}////End of test Header
		this.commentsListView.setAdapter(commentsListAdapter);
		///////

		//get info from activity
		////End of get info from activity

		return v;
	}

	@SuppressLint("NewApi")
	private void addHeader(LayoutInflater inflater)
	{
		View vg = inflater.inflate(R.layout.arts_list_card_view, commentsListView, false);

		TextView title = (TextView) vg.findViewById(R.id.art_card_title_tv);
		TextView author_name = (TextView) vg.findViewById(R.id.author_name);
		ImageView art_img = (ImageView) vg.findViewById(R.id.art_card_img);
		ImageView save = (ImageView) vg.findViewById(R.id.save_img);
		ImageView read = (ImageView) vg.findViewById(R.id.read_img);
		ImageView comms = (ImageView) vg.findViewById(R.id.comments_img);
		ImageView share = (ImageView) vg.findViewById(R.id.share_img);
		TextView num_of_comms = (TextView) vg.findViewById(R.id.num_of_comms);
		TextView num_of_shares = (TextView) vg.findViewById(R.id.num_of_sharings);
		TextView date = (TextView) vg.findViewById(R.id.art_card_date_tv);
		TextView preview = (TextView) vg.findViewById(R.id.art_card_preview_tv);
		ImageView settings = (ImageView) vg.findViewById(R.id.art_card_settings);
		ViewGroup top_lin_lay = (ViewGroup) vg.findViewById(R.id.art_card_top_lin_lay);

		//Title of article
		Spanned spannedContentTitle = Html.fromHtml(this.curArtInfo.title);
		title.setText(spannedContentTitle);
		top_lin_lay.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ArtsListAdapter.showArticle(allArtsInfo, position, act);
			}
		});

		String scaleFactorString = pref.getString("scale", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);
		title.setTextSize(21 * scaleFactor);

		//preview
		Spanned spannedContentPreview = Html.fromHtml(this.curArtInfo.preview);
		preview.setText(spannedContentPreview);
		preview.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ArtsListAdapter.showArticle(allArtsInfo, position, act);
			}
		});

		preview.setTextSize(21 * scaleFactor);

		// ART_IMG
		final float scale = act.getResources().getDisplayMetrics().density;
		int pixels = (int) (75 * scaleFactor * scale + 0.5f);
		LayoutParams params = new LayoutParams(pixels, pixels);
		params.setMargins(5, 5, 5, 5);
		art_img.setLayoutParams(params);
		art_img.setPadding(5, 5, 5, 5);

		ImageLoader imageLoader = UniversalImageLoader.get(act);
		imageLoader.displayImage(this.curArtInfo.img_art, art_img);
		//end of ART_IMG

		//SaveImg
		String appDir;
		appDir = pref.getString("filesDir", "");

		String formatedCategory;
		//				formatedCategory = MainActivityNew.CATEGORY_TO_LOAD.replace("-", "_");
		String TO_DELETE = "odnako.org/blogs";
		formatedCategory = TO_DELETE.replace("-", "_");
		formatedCategory = formatedCategory.replace("/", "_");
		formatedCategory = formatedCategory.replace(":", "_");
		formatedCategory = formatedCategory.replace(".", "_");

		String formatedLink;
		formatedLink = this.curArtInfo.url.replace("-", "_");
		formatedLink = formatedLink.replace("/", "_");
		formatedLink = formatedLink.replace(":", "_");
		formatedLink = formatedLink.replace(".", "_");

		File currentArticleFile = new File(appDir + "/" + formatedCategory + "/"
		+ formatedLink);
		//System.out.println("Try load from file: " + currentArticleFile.getAbsolutePath());
		int pixelsForIcons = (int) (35 * scaleFactor * scale + 0.5f);
		LayoutParams paramsForIcons = new LayoutParams(pixelsForIcons, pixelsForIcons);
		paramsForIcons.setMargins(5, 5, 5, 5);
		paramsForIcons.gravity = Gravity.CENTER;

		save.setScaleType(ScaleType.FIT_XY);
		save.setLayoutParams(paramsForIcons);

		if (currentArticleFile.exists())
		{

		}
		if (this.curArtInfo.url != null)
		{
			if (pref.getString("theme", "dark").equals("dark"))
			{
				save.setImageResource(R.drawable.ic_save_white_48dp);
			}
			else
			{
				save.setImageResource(R.drawable.ic_save_grey600_48dp);
			}

		}
		else
		{
			if (pref.getString("theme", "dark").equals("dark"))
			{
				save.setImageResource(android.R.color.transparent);
			}
			else
			{
				save.setImageResource(android.R.color.transparent);
			}
		}
		////end SaveImg

		//read Img
		ReadUnreadRegister readReg = new ReadUnreadRegister(act);
		read.setScaleType(ScaleType.FIT_XY);
		read.setLayoutParams(paramsForIcons);

		if (readReg.check(this.curArtInfo.url))
		{
			if (pref.getString("theme", "dark").equals("dark"))
			{
				read.setImageResource(R.drawable.ic_drafts_white_48dp);
			}
			else
			{
				read.setImageResource(R.drawable.ic_drafts_grey600_48dp);
			}
		}
		else
		{
			if (pref.getString("theme", "dark").equals("dark"))
			{
				read.setImageResource(R.drawable.ic_markunread_white_48dp);
			}
			else
			{
				read.setImageResource(R.drawable.ic_markunread_grey600_48dp);
			}
		}
		////end read Img

		//comments btn
		LayoutParams zeroParams = new LayoutParams(0, 0);
		comms.setLayoutParams(zeroParams);
		num_of_comms.setText(null);
		////end of comments btn
		//share btn
		share.setLayoutParams(zeroParams);
		num_of_shares.setText(null);
		////end of share btn
		//settingsBtn
		settings.setLayoutParams(zeroParams);

		//name of author
		if (!this.curArtInfo.authorName.equals("default"))
		{
			Spanned spannedContent = Html.fromHtml("<b>" + this.curArtInfo.authorName + "</b>");
			author_name.setText(spannedContent);

		}
		else if (this.curArtInfo.authorName.equals("default") || this.curArtInfo.authorName.equals("")
		|| this.curArtInfo.authorName.equals("empty"))
		{
			author_name.setText(null);
		}
		author_name.setTextSize(21 * scaleFactor);

		author_name.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				ArtsListAdapter.showAllAuthorsArticles(curArtInfo, act);
			}
		});
		//end of name of author

		//Date
		date.setText(this.curArtInfo.pubDate);
		author_name.setTextSize(21 * scaleFactor);
		////End of Date

		this.commentsListView.addHeaderView(vg, this.curArtInfo, true);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		//		System.out.println("CommentsFragment onViewCreated");
		super.onViewCreated(view, savedInstanceState);

	}

	@Override
	public void onAttach(Activity activity)
	{
		//		System.out.println("CommentsFragment onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onDetach()
	{
		//		System.out.println("CommentsFragment onDetach");
		super.onDetach();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		//		System.out.println("CommentsFragment onSaveInstanceState");
		super.onSaveInstanceState(outState);
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

}