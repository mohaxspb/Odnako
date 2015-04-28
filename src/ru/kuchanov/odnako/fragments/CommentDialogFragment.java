/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.download.CommentInfo;
import ru.kuchanov.odnako.utils.MyUIL;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommentDialogFragment extends DialogFragment
{
	private AppCompatActivity act;

	private CommentInfo curCommentInfo;

	public static CommentDialogFragment newInstance(CommentInfo curCommentInfo)
	{
		CommentDialogFragment frag = new CommentDialogFragment();
		Bundle args = new Bundle();
		args.putParcelable(CommentInfo.KEY_COMMENT, curCommentInfo);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		System.out.println("CommentDialogFragment onCreate");

		this.act = (AppCompatActivity) this.getActivity();

		this.curCommentInfo = this.getArguments().getParcelable(CommentInfo.KEY_COMMENT);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		System.out.println("CommentDialogFragment onCreateDialog");
		
		boolean wrapInScrollView = true;
		MaterialDialog dialog = new MaterialDialog.Builder(act)
		.title("Комментарий")// + (position + 1) + "/" + allComm.size())
		.customView(R.layout.comment_card_view, wrapInScrollView)
		.positiveText(R.string.close).build();
		

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);

		String scaleFactorCommentsString = pref.getString("scale_comments", "1");
		float scaleFactorComments = Float.valueOf(scaleFactorCommentsString);

		final CommentInfo p = curCommentInfo;

		TextView name;
		TextView txt;
		ImageView flag;
		TextView time_city;
		TextView like;
		TextView dislike;
		ImageView avaImg;

		name = (TextView) dialog.findViewById(R.id.name);
		txt = (TextView) dialog.findViewById(R.id.comm_text);
		flag = (ImageView) dialog.findViewById(R.id.flag);
		time_city = (TextView) dialog.findViewById(R.id.time_city);
		like = (TextView) dialog.findViewById(R.id.like);
		dislike = (TextView) dialog.findViewById(R.id.dislike);
		avaImg = (ImageView) dialog.findViewById(R.id.ava);

		name.setTextSize(23 * scaleFactorComments);
		txt.setTextSize(21 * scaleFactorComments);
		time_city.setTextSize(17 * scaleFactorComments);
		like.setTextSize(17 * scaleFactorComments);
		dislike.setTextSize(17 * scaleFactorComments);

		//set name
		Spanned spannedContentName = Html.fromHtml(p.name);
		name.setText(spannedContentName);

		//setText
		String commentText;
		commentText = p.txt;// "<p>" + p.txt + "</p>";
		Spanned spannedContent = Html.fromHtml(commentText);
		txt.setText(spannedContent);
		txt.setMovementMethod(LinkMovementMethod.getInstance());

		// FLAG
		ImageLoader imageLoader = MyUIL.get(act);
		imageLoader.displayImage(p.flag, flag);

		// AVA
		imageLoader.displayImage(p.avaImg, avaImg);

		time_city.setText(p.time + " " + p.city);

		// Karma
		//set karma's parent gravity to bottom, for known issue in Dialog
		LinearLayout vg = (LinearLayout) like.getParent();
		android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
		android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
		android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
		vg.setLayoutParams(params);
		vg.setGravity(Gravity.RIGHT);
		like.setText(p.like);
		dislike.setText(p.dislike);
		return dialog;
	}
}