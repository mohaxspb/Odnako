/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.io.File;
import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.lists_and_utils.Actions;
import ru.kuchanov.odnako.lists_and_utils.CommentInfo;
import ru.kuchanov.odnako.lists_and_utils.CommentsListAdapter;
import ru.kuchanov.odnako.utils.ReadUnreadRegister;
import ru.kuchanov.odnako.utils.MyUIL;
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

	Article curArtInfo;
	ArrayList<Article> allArtsInfo;

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

	private void addHeader(LayoutInflater inflater)
	{
		View vg = inflater.inflate(R.layout.article_card_art_frag, commentsListView, false);

		TextView title = (TextView) vg.findViewById(R.id.art_card_title_tv);
		TextView author_name = (TextView) vg.findViewById(R.id.author_name);
		ImageView art_img = (ImageView) vg.findViewById(R.id.art_card_img);
		ImageView save = (ImageView) vg.findViewById(R.id.save_img);
		ImageView read = (ImageView) vg.findViewById(R.id.read_img);
		ImageView comms = (ImageView) vg.findViewById(R.id.comments_img);
		ImageView share = (ImageView) vg.findViewById(R.id.share_img);
//		TextView num_of_comms = (TextView) vg.findViewById(R.id.num_of_comms);
//		TextView num_of_shares = (TextView) vg.findViewById(R.id.num_of_sharings);
		TextView date = (TextView) vg.findViewById(R.id.art_card_date_tv);
		TextView preview = (TextView) vg.findViewById(R.id.art_card_preview_tv);
		ImageView settings = (ImageView) vg.findViewById(R.id.art_card_settings);
		ViewGroup top_lin_lay = (ViewGroup) vg.findViewById(R.id.art_card_top_lin_lay);

		//Title of article
		Spanned spannedContentTitle = Html.fromHtml(this.curArtInfo.getTitle());
		title.setText(spannedContentTitle);
		top_lin_lay.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
//				Actions.showArticle(allArtsInfo, position, act);
				//TODO
			}
		});

		String scaleFactorString = pref.getString("scale", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);
		title.setTextSize(21 * scaleFactor);

		//preview
		Spanned spannedContentPreview = Html.fromHtml(this.curArtInfo.getPreview());
		preview.setText(spannedContentPreview);
		preview.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
//				Actions.showArticle(allArtsInfo, position, act);
				//TODO
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

		ImageLoader imageLoader = MyUIL.get(act);
		if(this.pref.getString("theme", "dark").equals("dark"))
		{
			imageLoader.displayImage(this.curArtInfo.getImgArt(), art_img, MyUIL.getDarkOptions());
		}
		else
		{
			imageLoader.displayImage(this.curArtInfo.getImgArt(), art_img);
		}
		
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
		formatedLink = this.curArtInfo.getUrl().replace("-", "_");
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
		if (this.curArtInfo.getUrl() != null)
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

		if (readReg.check(this.curArtInfo.getUrl()))
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
//		num_of_comms.setText(null);
		////end of comments btn
		//share btn
		share.setLayoutParams(zeroParams);
//		num_of_shares.setText(null);
		////end of share btn
		//settingsBtn
		settings.setLayoutParams(zeroParams);

		//name of author
		if (!this.curArtInfo.getAuthorName().equals("default"))
		{
			Spanned spannedContent = Html.fromHtml("<b>" + this.curArtInfo.getAuthorName() + "</b>");
			author_name.setText(spannedContent);

		}
		else if (this.curArtInfo.getAuthorName().equals("default") || this.curArtInfo.getAuthorName().equals("")
		|| this.curArtInfo.getAuthorName().equals("empty"))
		{
			author_name.setText(null);
		}
		author_name.setTextSize(21 * scaleFactor);

		author_name.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Actions.showAllAuthorsArticles(curArtInfo.getAuthorBlogUrl(), act);
			}
		});
		//end of name of author

		//Date
		date.setText(this.curArtInfo.getPubDate().toString());
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
		this.curArtInfo = state.getParcelable(Article.KEY_CURENT_ART);
		this.position = state.getInt("position");
		//restore AllArtsInfo
		this.allArtsInfo = state.getParcelableArrayList(Article.KEY_ALL_ART_INFO);
	}

}
