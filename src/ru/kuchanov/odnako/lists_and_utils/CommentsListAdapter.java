package ru.kuchanov.odnako.lists_and_utils;

import java.io.File;
import java.util.ArrayList;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.fragments.CommentDialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CommentsListAdapter extends ArrayAdapter<CommentInfo> implements Filterable
{

	ActionBarActivity act;
	SharedPreferences pref;
	LayoutInflater lInflater;

	ListView commentsListView;

	ArrayList<CommentInfo> artCommentsInfo;

	public CommentsListAdapter(ActionBarActivity act, int resource,
	ArrayList<CommentInfo> artCmmentsInfo, ListView commentsListView)
	{
		super(act, resource, artCmmentsInfo);
		this.act = act;
		this.artCommentsInfo = artCmmentsInfo;
		lInflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		this.commentsListView = commentsListView;
	}

	public void notifyDataSetChanged()
	{
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount()
	{
		return artCommentsInfo.size();
	}

	@Override
	public CommentInfo getItem(int position)
	{
		return (this.artCommentsInfo.get(position));
	}

	CommentInfo getComment(int position)
	{
		return ((CommentInfo) getItem(position));
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		final CommentInfo p = getComment(position);
		int type = getItemViewType(position);
		View view = null;
		switch (type)
		{
			case 0:
				CommHolder holderMain;
				if (convertView == null)
				{
					ViewGroup vg;
					pref = PreferenceManager.getDefaultSharedPreferences(act);
					if (pref.getString("theme", "dark").equals("dark"))
					{
						vg = (ViewGroup) LayoutInflater.from(act).inflate(
						R.layout.comment_card_view, this.commentsListView, false);
					}
					else
					{
						vg = (ViewGroup) LayoutInflater.from(act).inflate(
						R.layout.comment_card_view, this.commentsListView, false);
					}
					holderMain = new CommHolder((CardView) vg.findViewById(R.id.card),
					(TextView) vg.findViewById(R.id.name), (ImageView) vg.findViewById(R.id.flag),
					(TextView) vg.findViewById(R.id.time_city),
					(ImageView) vg.findViewById(R.id.ava),
					(TextView) vg.findViewById(R.id.comm_text),
					(TextView) vg.findViewById(R.id.like), (TextView) vg.findViewById(R.id.dislike));
					vg.setTag(holderMain);
					view = vg;
				}
				else
				{
					view = convertView;
					holderMain = (CommHolder) convertView.getTag();
				}
				int padding = (Math.round(Float.valueOf(p.padding) / Float.valueOf("1.875")) - 1) * 25;

				DisplayMetrics displayMetrics = act.getResources().getDisplayMetrics();
				int displayWidth = displayMetrics.widthPixels;
				int maxWidth;
				int newWidth;
				boolean twoPane = this.pref.getBoolean("twoPane", false);
				if (twoPane)
				{
					maxWidth = displayWidth / 3 * 2;
				}
				else
				{
					maxWidth = displayWidth;
				}

				if (padding <= 0)
				{
					padding = 0;
					newWidth = LayoutParams.MATCH_PARENT;
				}
				else if (padding > 200)
				{
					padding = 200;
					newWidth = maxWidth - padding;
				}
				else
				{
					newWidth = maxWidth - padding;
				}
				//set width to cardc
				LayoutParams cardParams = new LayoutParams(newWidth, LayoutParams.WRAP_CONTENT);
				holderMain.card.setLayoutParams(cardParams);
				//End of set width to card

				//set name
				Spanned spannedContentName = Html.fromHtml(p.name);
				holderMain.name.setText(spannedContentName);

				//setText
				String commentText;
				commentText="<p>" + p.txt + "</p>";
				Spanned spannedContent = Html.fromHtml(commentText);
				holderMain.txt.setText(spannedContent);
				holderMain.txt.setMovementMethod(LinkMovementMethod.getInstance());
				///set showDialog with comm
				holderMain.txt.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						showCommView(act, p);
					}
				});

				// FLAG
				File cacheDir = new File(Environment.getExternalStorageDirectory(), "Odnako/Cache");

				ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(act)
				.diskCache(new UnlimitedDiscCache(cacheDir))
				.build();

				DisplayImageOptions options = new DisplayImageOptions.Builder()
				.displayer(new RoundedBitmapDisplayer(10))
				.showImageOnLoading(R.drawable.ic_action_refresh_ligth)
				.showImageForEmptyUri(R.drawable.ic_crop_original_grey600_48dp)
				.showImageOnFail(R.drawable.ic_crop_original_grey600_48dp)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();

				ImageLoader imageLoader = ImageLoader.getInstance();
				if (!imageLoader.isInited())
				{
					imageLoader.init(config);
				}
				imageLoader.displayImage(p.flag, holderMain.flag, options);
				//end of FLAG

				// AVA
				if (!imageLoader.isInited())
				{
					imageLoader.init(config);
				}
				imageLoader.displayImage(p.avaImg, holderMain.avaImg, options);
				// AVA

				holderMain.time_city.setText(p.time + " " + p.city);

				// Karma
//				String colored = "<font color=\'#00FF00\'>" + p.like + "</font> | "
//				+ "<font color=\'#FF0000\'>" + p.dislike + "</font>";
//				Spanned spannedContentKarma = Html.fromHtml(colored);
//				holderMain.like_dislike.setText(spannedContentKarma);
				//set karma's parent gravity to bottom, for known issue in Dialog
				LinearLayout vg=(LinearLayout) holderMain.like.getParent();
				LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				vg.setLayoutParams(params);
				vg.setGravity(Gravity.RIGHT);
				holderMain.like.setText(p.like);
				holderMain.dislike.setText(p.dislike);

				return view;
			default:
				return view;

		}
	}

	@Override
	public int getItemViewType(int position)
	{
		return 0;
	}

	@Override
	public int getViewTypeCount()
	{
		return 1;
	}

	static class CommHolder
	{
		CardView card;
		TextView name;
		TextView txt;
		ImageView flag;
		TextView time_city;
		TextView like;
		TextView dislike;
		ImageView avaImg;

		CommHolder(CardView card, TextView name, ImageView flag, TextView time_city,
		ImageView avaImg, TextView txt, TextView like, TextView dislike)
		{
			this.card = card;
			this.name = name;
			this.txt = txt;
			this.flag = flag;
			this.time_city = time_city;
			this.like=like;
			this.dislike = dislike;
			this.avaImg = avaImg;
		}
	}

	public static void showCommView(ActionBarActivity act, CommentInfo p)
	{
		CommentDialogFragment newFragment = CommentDialogFragment.newInstance(p);
		newFragment.show(act.getSupportFragmentManager(), "dialog");
	}
}