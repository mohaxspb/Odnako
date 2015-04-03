/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.lists_and_utils.CommentInfo;
import ru.kuchanov.odnako.utils.MyUIL;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommentDialogFragment extends DialogFragment
{
	private ActionBarActivity act;
	private SharedPreferences pref;
	private boolean twoPane;

	private CommentInfo curCommentInfo;

	private TextView name;
	private ImageView flag;
	private TextView time_city;
	private ImageView ava;
	private TextView commText;
	private TextView like;
	private TextView dislike;

	//	String[] defaultCommInfo;

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

		this.act = (ActionBarActivity) this.getActivity();

		pref = PreferenceManager.getDefaultSharedPreferences(act);
		this.twoPane = pref.getBoolean("twoPane", false);

		this.curCommentInfo = new CommentInfo(this.getArguments().getStringArray("CommentInfo"));
		if (this.twoPane)
		{

		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		System.out.println("CommentDialogFragment onCreateDialog");
		Dialog dialog = new Dialog(act);

		LayoutInflater inflater = this.getActivity().getLayoutInflater();

		FrameLayout linlay = (FrameLayout) inflater.inflate(R.layout.comment_card_view, new FrameLayout(act), false);

		dialog.setContentView(linlay);

		//find all views
		name = (TextView) linlay.findViewById(R.id.name);
		flag = (ImageView) linlay.findViewById(R.id.flag);
		time_city = (TextView) linlay.findViewById(R.id.time_city);
		ava = (ImageView) linlay.findViewById(R.id.ava);
		commText = (TextView) linlay.findViewById(R.id.comm_text);
		like = (TextView) linlay.findViewById(R.id.like);
		dislike = (TextView) linlay.findViewById(R.id.dislike);
		//end of find all views

		//set name
		Spanned spannedContentName = Html.fromHtml(this.curCommentInfo.name);
		name.setText(spannedContentName);

		//setText
		String commentText = this.curCommentInfo.txt;
		Spanned spannedContent = Html.fromHtml(commentText);
		commText.setText(spannedContent);
		commText.setMovementMethod(LinkMovementMethod.getInstance());

		// FLAG
		ImageLoader imageLoader = MyUIL.get(act);
		imageLoader.displayImage(this.curCommentInfo.flag, flag);
		//end of FLAG

		// AVA
		imageLoader.displayImage(this.curCommentInfo.avaImg, ava);
		// AVA

		time_city.setText(this.curCommentInfo.time + " " + this.curCommentInfo.city);

		// Karma
		//		String colored = "<font color=\'#00FF00\'>" + this.curCommentInfo.like + "</font> | "
		//		+ "<font color=\'#FF0000\'>" + this.curCommentInfo.dislike + "</font>";
		//		Spanned spannedContentKarma = Html.fromHtml(colored);
		//		karma.setText(spannedContentKarma);
		//		karma.setText("gfhgfjhfkjhgkj");

		//		LinearLayout vg=(LinearLayout) like.getParent();
		//		LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT, 30);
		//		vg.setLayoutParams(params);
		//		vg.setGravity(Gravity.RIGHT);
		like.setText(this.curCommentInfo.like);
		dislike.setText(this.curCommentInfo.dislike);

		//karma isn't working with spanned content. FUCK!!!!!!!!!!!!
		//		
		//		LinearLayout vg=(LinearLayout) karma.getParent();
		//		LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT, 500);
		//		vg.setLayoutParams(params);
		//		
		//		dialog.setContentView(linlay);

		showDialogWithNoTopSpace(linlay, dialog, false);
		return dialog;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		System.out.println("CommentDialogFragment onViewCreated");
		super.onViewCreated(view, savedInstanceState);

	}

	@Override
	public void onAttach(Activity activity)
	{
		System.out.println("CommentDialogFragment onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onDetach()
	{
		System.out.println("CommentDialogFragment onDetach");
		super.onDetach();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		System.out.println("CommentDialogFragment onSaveInstanceState");
		super.onSaveInstanceState(outState);

	}

	/**
	 * Show a Dialog with the extra title/top padding collapsed.
	 * 
	 * @param customView
	 *            The custom view that you added to the dialog
	 * @param dialog
	 *            The dialog to display without top spacing
	 * @param show
	 *            Whether or not to call dialog.show() at the end.
	 */
	public static void showDialogWithNoTopSpace(final View customView, final Dialog dialog,
	boolean show)
	{
		// Now we setup a listener to detect as soon as the dialog has shown.
		customView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{

			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout()
			{
				// Check if your view has been laid out yet
				if (customView.getHeight() > 0)
				{
					// If it has been, we will search the view hierarchy for the view that is responsible for the extra space. 
					LinearLayout dialogLayout = findDialogLinearLayout(customView);
					if (dialogLayout == null)
					{
						// Could find it. Unexpected.

					}
					else
					{
						// Found it, now remove the height of the title area
						//						View child = dialogLayout.getChildAt(0);
						//						if (child != customView)
						//						{
						//							// remove height
						//							LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child
						//							.getLayoutParams();
						//							lp.height = 0;
						//							child.setLayoutParams(lp);
						//
						//						}
						//						else
						//						{
						//							// Could find it. Unexpected.
						//						}
						//remove divider
						View child1 = dialogLayout.getChildAt(1);
						if (child1 != customView)
						{
							// remove height
							LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child1
							.getLayoutParams();
							lp.height = 0;
							child1.setLayoutParams(lp);
						}
						else
						{
							System.out.println("customView, Fuck");
						}
						//remove background
						dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
					}

					// Done with the listener
					customView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			}

		});

		// Show the dialog
		if (show)
			dialog.show();
	}

	/**
	 * Searches parents for a LinearLayout
	 * 
	 * @param view
	 *            to search the search from
	 * @return the first parent view that is a LinearLayout or null if none was
	 *         found
	 */
	public static LinearLayout findDialogLinearLayout(View view)
	{
		ViewParent parent = (ViewParent) view.getParent();
		if (parent != null)
		{
			if (parent instanceof LinearLayout)
			{
				// Found it
				return (LinearLayout) parent;

			}
			else if (parent instanceof View)
			{
				// Keep looking
				return findDialogLinearLayout((View) parent);

			}
		}

		// Couldn't find it
		return null;
	}

}
