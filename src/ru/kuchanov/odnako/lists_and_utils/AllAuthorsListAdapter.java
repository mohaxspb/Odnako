package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.fragments.AllAuthorsListFragment;
import ru.kuchanov.odnako.lists_and_utils.AllAuthorsInfo.AuthorInfo;
import ru.kuchanov.odnako.utils.DipToPx;
import ru.kuchanov.odnako.utils.UniversalImageLoader;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class AllAuthorsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	final Drawable drawableArrowDown;
	final Drawable drawableArrowUp;

	private static final int HEADER = 0;
	private static final int ADS = 1;
	private static final int AUTHOR = 2;
	ActionBarActivity act;

	RecyclerView artsListView;

	ImageLoader imageLoader;
	SharedPreferences pref;

	boolean twoPane;

	AllAuthorsListFragment artsListFrag;

	AllAuthorsInfo allAuthorsInfo;
	ArrayList<AuthorInfo> allAuthrsInfoList;
	ArrayList<AuthorInfo> orig;

	public AllAuthorsListAdapter(ActionBarActivity act, RecyclerView artsListView, AllAuthorsListFragment artsListFrag)
	{
		this.act = act;

		this.artsListView = artsListView;

		this.artsListFrag = artsListFrag;

		pref = PreferenceManager.getDefaultSharedPreferences(act);
		twoPane = pref.getBoolean("twoPane", false);

		imageLoader = UniversalImageLoader.get(act);

		allAuthorsInfo = new AllAuthorsInfo(act);
		allAuthrsInfoList = allAuthorsInfo.getAllAuthorsInfoAsList();

		//set arrowDownIcon by theme
		int[] attrs = new int[] { R.attr.arrowDownIcon };
		TypedArray ta = this.act.obtainStyledAttributes(attrs);
		drawableArrowDown = ta.getDrawable(0);
		ta.recycle();
		//set arrowDownIcon by theme
		attrs = new int[] { R.attr.arrowUpIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableArrowUp = ta.getDrawable(0);
		ta.recycle();
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public int getItemViewType(int position)
	{
		if (position == 0)
		{
			return HEADER;
		}
		else
		{
			return AUTHOR;
		}
	}

	@Override
	public int getItemCount()
	{
		// TODO Auto-generated method stub
		int header = 1;

		return this.allAuthrsInfoList.size() + header;
	}

	public AuthorInfo getArtInfoByPosition(int position)
	{
		AuthorInfo p = this.allAuthrsInfoList.get(position - 1);
		return p;
	}

	public static int getPositionInAllArtsInfo(int recyclerViewPosition)
	{
		return recyclerViewPosition - 1;
	}

	public static int getPositionInRecyclerView(int artsListPosition)
	{
		return artsListPosition + 1;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
	{
		switch (getItemViewType(position))
		{
			case (HEADER):

			break;
			case (ADS):
			//TODO
			break;
			case (AUTHOR):
				final AuthorInfo p;
				p = this.getArtInfoByPosition(position);

				//				final ArtInfo p = this.getArtInfoByPosition(position);
				final int positionInAllArtsInfo = AllAuthorsListAdapter.getPositionInAllArtsInfo(position);

				final AuthorHolder holderMain = (AuthorHolder) holder;

				//variables for scaling text and icons and images from settings
				String scaleFactorString = pref.getString("scale", "1");
				float scaleFactor = Float.valueOf(scaleFactorString);

				final float scale = act.getResources().getDisplayMetrics().density;
				int pixels = (int) (75 * scaleFactor * scale + 0.5f);
				////End of variables for scaling text and icons and images from settings

				//light checked item in listView
				if (this.twoPane)
				{

					if (artsListFrag.getMyActivatedPosition() == positionInAllArtsInfo)
					{
						holderMain.container.setBackgroundColor(act.getResources().getColor(R.color.blue));
					}
					else
					{
						holderMain.container.setBackgroundColor(Color.TRANSPARENT);
					}
				}
				else
				{
					holderMain.container.setBackgroundColor(Color.TRANSPARENT);
				}

				////////
				//setOnclick
				holderMain.container.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						Actions.showAllAuthorsArticles(new ArtInfo(new String[] { "", "", "", p.blogLink, p.name }),
						act);
					}
				});

				// Author ava
				LayoutParams params = (LayoutParams) holderMain.author_img.getLayoutParams();
				params.height = pixels;
				params.width = pixels;
				holderMain.author_img.setLayoutParams(params);
				String link = "";
				if (p.avaImg.startsWith("/"))
				{
					link = "http://odnako.org" + p.avaImg;
				}

				if (this.pref.getString("theme", "dark").equals("dark"))
				{
					imageLoader.displayImage(link, holderMain.author_img, UniversalImageLoader.getDarkOptions());
				}
				else
				{
					imageLoader.displayImage(link, holderMain.author_img);
				}
				//end of ART_IMG

				//name 
				Spanned spannedContentTitle = Html.fromHtml(p.name);
				holderMain.name.setText(spannedContentTitle);
				holderMain.name.setTextSize(21 * scaleFactor);

				//who
				if (!p.who.equals("empty") && !p.who.equals(""))
				{
					Spanned spannedContentPreview = Html.fromHtml(p.who);
					holderMain.who.setText(spannedContentPreview);
					holderMain.who.setTextSize(21 * scaleFactor);
				}
				else
				{
					holderMain.who.setText(null);
				}
				//description
				if (!p.description.equals("empty") && !p.description.equals(""))
				{
					Spanned spannedContentPreview = Html.fromHtml(p.description);
					holderMain.description.setText(spannedContentPreview);
					holderMain.description.setTextSize(21 * scaleFactor);
				}
				else
				{
					holderMain.description.setText(null);
				}
				//fuck it. It ruins layout(((
//				holderMain.description.setLinksClickable(true);
//				holderMain.description.setMovementMethod(LinkMovementMethod.getInstance());
				//descriptionIcon

				if (!p.description.equals("empty") && !p.description.equals(""))
				{
					holderMain.more_icon.setImageDrawable(drawableArrowDown);
				}

				//descr onClick
				if (!p.description.equals("empty") && !p.description.equals(""))
				{
					//set conateiner height to wrapContent
					LayoutParams paramsBottomLin = (LayoutParams) holderMain.bottom_lin.getLayoutParams();
					paramsBottomLin.height = LayoutParams.WRAP_CONTENT;
					holderMain.bottom_lin.setLayoutParams(paramsBottomLin);
					//set on click
					holderMain.bottom_lin.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							LayoutParams params = (LayoutParams) holderMain.description.getLayoutParams();
							if (params.height == LayoutParams.WRAP_CONTENT)
							{
								//so opened need to close
								//changeIcon
								holderMain.more_icon.setImageDrawable(drawableArrowDown);
								params.height = (int) DipToPx.convert(40, act);
								holderMain.description.setLayoutParams(params);
							}
							else
							{
								//closed, need to open
								//changeIcon
								holderMain.more_icon.setImageDrawable(drawableArrowUp);
								params.height = LayoutParams.WRAP_CONTENT;
								holderMain.description.setLayoutParams(params);
							}
						}
					});
				}
				else
				{
					//set conateiner height to ZERO
					LayoutParams paramsBottomLin = (LayoutParams) holderMain.bottom_lin.getLayoutParams();
					paramsBottomLin.height = 0;
					holderMain.bottom_lin.setLayoutParams(paramsBottomLin);
					//set on click
					holderMain.bottom_lin.setOnClickListener(null);
				}

			break;

		}

	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position)
	{
		View itemLayoutView = null;
		RecyclerView.ViewHolder holder = null;
		switch (position)
		{
			case (HEADER):
				itemLayoutView = new LinearLayout(act);
				itemLayoutView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) DipToPx.convert(165,
				act)));

				holder = new HeaderHolder(itemLayoutView);
				return holder;
			case (ADS):
				//TODO
				itemLayoutView = new LinearLayout(act);
				itemLayoutView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 255));

				holder = new HeaderHolder(itemLayoutView);
				return holder;
			case (AUTHOR):
				// create a new view
				itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.author_card,
				parent,
				false);

				// create ViewHolder
				holder = new AuthorHolder(itemLayoutView);

				return holder;
			default:
				return holder;
		}
	}

	static class HeaderHolder extends RecyclerView.ViewHolder
	{

		HeaderHolder(View itemLayoutView)
		{
			super(itemLayoutView);
		}
	}

	static class AuthorHolder extends RecyclerView.ViewHolder
	{
		TextView name;
		TextView who;
		TextView description;
		ImageView author_img;
		ImageView more_icon;
		ViewGroup bottom_lin;
		View container;

		AuthorHolder(View itemLayoutView)
		{
			super(itemLayoutView);
			//author
			this.name = (TextView) itemLayoutView.findViewById(R.id.name);

			this.who = (TextView) itemLayoutView.findViewById(R.id.who);
			this.description = (TextView) itemLayoutView.findViewById(R.id.description);
			this.container = itemLayoutView;

			this.author_img = (ImageView) itemLayoutView.findViewById(R.id.ava_img);
			this.more_icon = (ImageView) itemLayoutView.findViewById(R.id.more_icon);

			this.bottom_lin = (ViewGroup) itemLayoutView.findViewById(R.id.author_card_bottom_lin);
		}
	}
}