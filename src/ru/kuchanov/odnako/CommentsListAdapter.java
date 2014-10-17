package ru.kuchanov.odnako;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentsListAdapter extends BaseAdapter
{
	ActionBarActivity act;
	LayoutInflater lInflater;
	ArrayList<CommentInfo> objects;
	SharedPreferences pref;
			

	CommentsListAdapter(ActionBarActivity act, ArrayList<CommentInfo> commentInfo)
	{
		this.act = act;
		objects = commentInfo;
		lInflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// кол-во элементов
	@Override
	public int getCount()
	{
		return objects.size();
	}

	// элемент по позиции
	@Override
	public Object getItem(int position)
	{
		return objects.get(position);
	}

	// id по позиции
	@Override
	public long getItemId(int position)
	{
		return position;
	}

	// пункт списка
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final CommentInfo p = getComment(position);
		//System.out.println("Redraw view in getView of Adapter");
		int type = getItemViewType(position);
		View view = null;
		switch (type)
		{
			case 0:

				NoCommHolder holder;
				if (convertView == null)
				{
					ViewGroup viewGroup;
					pref = PreferenceManager.getDefaultSharedPreferences(act);
					if (pref.getString("theme", "dark").equals("dark"))
					{
						viewGroup = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.no_comm_dark, null);
					}
					else
					{
						viewGroup = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.no_comm_ligth, null);
					}
					
					holder = new NoCommHolder((TextView) viewGroup.findViewById(R.id.no_comm));
					viewGroup.setTag(holder);
					view = viewGroup;
				}
				else
				{
					view = convertView;
					holder = (NoCommHolder) convertView.getTag();
				}

				holder.noComm.setText("Сию статью ещё никто не прокомментировал");
				holder.noComm.setTextSize(30);

				return view;
			case 1:
				MoreCommHolder holder1;
				if (convertView == null)
				{
					ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.more_comments, null);
					holder1 = new MoreCommHolder((TextView) viewGroup.findViewById(R.id.more_comm_btn));
					viewGroup.setTag(holder1);
					view = viewGroup;
				}
				else
				{
					view = convertView;
					holder1 = (MoreCommHolder) convertView.getTag();
				}

				holder1.moreComm.setText(R.string.more_comm);
				holder1.moreComm.setHeight(100);
				holder1.moreComm.setTextSize(30);

				return view;
			case 2:
				CommHolder holderMain;
				if (convertView == null)
				{
					ViewGroup viewGroup;
					pref = PreferenceManager.getDefaultSharedPreferences(act);
					if (pref.getString("theme", "dark").equals("dark"))
					{
						viewGroup = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.comm_dark, null);
					}
					else
					{
						viewGroup = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.comm_ligth, null);
					}
					holderMain = new CommHolder((TextView) viewGroup.findViewById(R.id.name), (ImageView) viewGroup.findViewById(R.id.flag), (TextView) viewGroup.findViewById(R.id.time_city),
					(ImageView) viewGroup.findViewById(R.id.ava), (TextView) viewGroup.findViewById(R.id.comm_text), (TextView) viewGroup.findViewById(R.id.karma));
					viewGroup.setTag(holderMain);
					view = viewGroup;
				}
				else
				{
					view = convertView;
					holderMain = (CommHolder) convertView.getTag();
				}
				int padding = (Math.round(Float.valueOf(p.padding) / Float.valueOf("1.875")) - 1) * 25;
				if (padding < 0)
				{
					padding = 0;
				}
				else if (padding > 200)
				{
					padding = 200;
				}
				view.setPadding(padding, 0, 0, 0);

				String b = "<b>" + p.name + "</b>";
				Spanned spannedContentName = Html.fromHtml(b);
				holderMain.name.setText(spannedContentName);

				Spanned spannedContent = Html.fromHtml("<p>" + p.txt + "</p>");
				holderMain.txt.setText(spannedContent);
				holderMain.txt.setMovementMethod(LinkMovementMethod.getInstance());
				///set showDialog with comm
				holderMain.txt.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						showCommView(act, p);
					}
				});

				// FLAG
				DownloadImageTask downFlag = new DownloadImageTask((ImageButton) null, holderMain.flag, (Activity) act);
				downFlag.execute(p.flag);
				// FLAG

				// AVA
				DownloadImageTask downAva = new DownloadImageTask((ImageButton) null, holderMain.avaImg, (Activity) act);
				downAva.execute(p.avaImg);
				// AVA

				holderMain.time_city.setText(p.time + p.city);

				// Karma
				String colored = "<font color=\'#00FF00\'>" + p.like + "</font> | " + "<font color=\'#FF0000\'>" + p.dislike + "</font>";
				Spanned spannedContentKarma = Html.fromHtml(colored);
				holderMain.like_dislike.setText(spannedContentKarma);
				holderMain.like_dislike.setGravity(Gravity.RIGHT);

				return view;
		}

		return view;

	}

	@Override
	public int getItemViewType(int position)
	{
		if (objects.get(position).data_pid.equals("no_comm"))
		{
			return 0;
		}
		else if (objects.get(position).data_pid.equals("more_comm"))
		{
			return 1;
		}
		else
		{
			return 2;
		}
	}

	@Override
	public int getViewTypeCount()
	{
		return 3;
	}

	// товар по позиции
	CommentInfo getComment(int position)
	{
		return ((CommentInfo) getItem(position));
	}

	static class CommHolder
	{
		TextView name;
		TextView txt;
		ImageView flag;
		TextView time_city;
		TextView like_dislike;
		ImageView avaImg;

		CommHolder(TextView name, ImageView flag, TextView time_city, ImageView avaImg, TextView txt, TextView like_dislike)
		{
			this.name = name;
			this.txt = txt;
			this.flag = flag;
			this.time_city = time_city;
			this.like_dislike = like_dislike;
			this.avaImg = avaImg;
		}
	}

	static class NoCommHolder
	{
		TextView noComm;

		NoCommHolder(TextView noComm)
		{
			this.noComm = noComm;
		}
	}

	static class MoreCommHolder
	{
		TextView moreComm;

		MoreCommHolder(TextView moreComm)
		{
			this.moreComm = moreComm;
		}
	}
	
	public static void showCommView(Context context, CommentInfo p)
	{
		final Dialog commDialog = new Dialog(context);
		commDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		commDialog.setCancelable(true);
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		if (pref.getString("theme", "dark").equals("dark"))
		{
			commDialog.setContentView(R.layout.comm_dark);
		}
		else
		{
			commDialog.setContentView(R.layout.comm_ligth);
		}
		
		commDialog.show();
		
		//FIND Views 
		TextView name=(TextView) commDialog.findViewById(R.id.name);
		ImageView flag=(ImageView) commDialog.findViewById(R.id.flag);
		TextView time_city= (TextView) commDialog.findViewById(R.id.time_city);
		ImageView ava=(ImageView) commDialog.findViewById(R.id.ava);
		TextView commText= (TextView) commDialog.findViewById(R.id.comm_text);
		TextView karma= (TextView) commDialog.findViewById(R.id.karma);
		////////////
		
		String b = "<b>" + p.name + "</b>";
		Spanned spannedContentName = Html.fromHtml(b);
		name.setText(spannedContentName);

		Spanned spannedContent = Html.fromHtml("<p>" + p.txt + "</p>");
		commText.setText(spannedContent);
		commText.setMovementMethod(LinkMovementMethod.getInstance());
		commText.setPadding(5, 0, 5, 0);

		// FLAG
		DownloadImageTask downFlag = new DownloadImageTask((ImageButton) null, flag, (Activity) context);
		downFlag.execute(p.flag);
		// FLAG

		// AVA
		DownloadImageTask downAva = new DownloadImageTask((ImageButton) null, ava, (Activity) context);
		downAva.execute(p.avaImg);
		// AVA

		time_city.setText(p.time + p.city);

		// Karma
		String colored = "<font color=\'#00FF00\'>" + p.like + "</font> | " + "<font color=\'#FF0000\'>" + p.dislike + "</font>";
		Spanned spannedContentKarma = Html.fromHtml(colored);
		karma.setText(spannedContentKarma);
		karma.setGravity(Gravity.RIGHT);
	}

}