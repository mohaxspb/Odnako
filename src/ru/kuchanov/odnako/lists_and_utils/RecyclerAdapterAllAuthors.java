package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;
import java.util.Locale;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.activities.ActivityPreference;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.fragments.FragmentAllAuthors;
import ru.kuchanov.odnako.utils.DipToPx;
import ru.kuchanov.odnako.utils.MyUIL;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class RecyclerAdapterAllAuthors extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	final static String LOG = RecyclerAdapterAllAuthors.class.getSimpleName() + "/";

	private final Drawable drawableArrowDown;
	private final Drawable drawableArrowUp;

	private static final int HEADER = 0;
	private static final int AUTHOR = 2;
	private AppCompatActivity act;

	private ImageLoader imageLoader;
	private SharedPreferences pref;

	private boolean twoPane;

	private FragmentAllAuthors artsListFrag;

	private ArrayList<Author> allAuthrsInfoList;
	private ArrayList<Author> orig;

	private String currentFilter = null;

	public RecyclerAdapterAllAuthors(ActivityBase act, FragmentAllAuthors artsListFrag)
	{
		this.act = act;

		this.artsListFrag = artsListFrag;

		pref = PreferenceManager.getDefaultSharedPreferences(act);
		twoPane = pref.getBoolean(ActivityPreference.PREF_KEY_TWO_PANE, false);

		imageLoader = MyUIL.get(act);
		this.allAuthrsInfoList = (ArrayList<Author>) act.getAllAuthorsList();
		//XXX
		this.orig = this.allAuthrsInfoList;

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

	public ArrayList<Author> getCurAllAuthorsList()
	{
		return this.orig;
	}

	public ArrayList<Author> getAllAuthorsList()
	{
		return this.allAuthrsInfoList;
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
		int header = 1;

		//		return this.allAuthrsInfoList.size() + header;
		return this.orig.size() + header;
	}

	//	public AuthorInfo getArtInfoByPosition(int position)
	public Author getArtInfoByPosition(int position)
	{
		//		Author author = this.allAuthrsInfoList.get(position - 1);
		//		if(this.orig==null)
		//		{
		//			this.orig=this.allAuthrsInfoList;
		//		}

		Author author = this.orig.get(position - 1);
		return author;
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
			case (AUTHOR):
				final Author p = this.getArtInfoByPosition(position);
				final int positionInAllArtsInfo = RecyclerAdapterAllAuthors.getPositionInAllArtsInfo(position);
				final AuthorHolder holderMain = (AuthorHolder) holder;

				//variables for scaling text and icons and images from settings
//				String scaleFactorString = pref.getFloat(ActivityPreference.PREF_KEY_UI_SCALE, 0.75f);
//				float scaleFactor = Float.valueOf(scaleFactorString);
				float scaleFactor = pref.getFloat(ActivityPreference.PREF_KEY_SCALE_UI, 0.75f);;

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

				//setOnclick
				holderMain.container.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						Actions.showAllAuthorsArticles(p.getBlogUrl(), act);
					}
				});

				// Author ava
				LayoutParams params = (LayoutParams) holderMain.author_img.getLayoutParams();
				params.height = pixels;
				params.width = pixels;
				holderMain.author_img.setLayoutParams(params);

				boolean nightModeIsOn = this.pref.getBoolean(ActivityPreference.PREF_KEY_NIGHT_MODE, false);
				if (nightModeIsOn)
				{
					imageLoader.displayImage(p.getAvatar(), holderMain.author_img,
					MyUIL.getDarkOptions());
				}
				else
				{
					imageLoader.displayImage(p.getAvatar(), holderMain.author_img);
				}
				//end of ART_IMG

				//name 
				Spanned spannedContentTitle = Html.fromHtml(p.getName());
				holderMain.name.setText(spannedContentTitle);
				holderMain.name.setTextSize(21 * scaleFactor);

				//who
				if (!p.getWho().equals(Const.EMPTY_STRING) && !p.getWho().equals(""))
				{
					Spanned spannedContentPreview = Html.fromHtml(p.getWho());
					holderMain.who.setText(spannedContentPreview);
					holderMain.who.setTextSize(21 * scaleFactor);
				}
				else
				{
					holderMain.who.setText(null);
				}
				//description
				if (!p.getDescription().equals(Const.EMPTY_STRING) && !p.getDescription().equals(""))
				{
					holderMain.description.setTextSize(21 * scaleFactor);
					holderMain.description.setText(Html.fromHtml(p.getDescription()));

					holderMain.description.setLinksClickable(true);
					holderMain.description.setMovementMethod(LinkMovementMethod.getInstance());

					holderMain.more_icon.setImageDrawable(drawableArrowDown);

					LayoutParams paramsDescr = (LayoutParams) holderMain.description.getLayoutParams();
					paramsDescr.height = (int) DipToPx.convert(40, act);
					holderMain.description.setLayoutParams(paramsDescr);

					//set container height to wrapContent
					LayoutParams paramsBottomLin = (LayoutParams) holderMain.bottom_lin.getLayoutParams();
					paramsBottomLin.height = LayoutParams.WRAP_CONTENT;
					holderMain.bottom_lin.setLayoutParams(paramsBottomLin);

					//set on click
					holderMain.more_icon.setOnClickListener(new OnClickListener()
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
							//set container height to wrapContent
							LayoutParams paramsBottomLin = (LayoutParams) holderMain.bottom_lin.getLayoutParams();
							paramsBottomLin.height = LayoutParams.WRAP_CONTENT;
							holderMain.bottom_lin.setLayoutParams(paramsBottomLin);
						}
					});
				}
				else
				{
					holderMain.description.setText(null);

					holderMain.more_icon.setImageDrawable(null);

					//set conateiner height to ZERO
					LayoutParams paramsBottomLin = (LayoutParams) holderMain.bottom_lin.getLayoutParams();
					paramsBottomLin.height = 0;
					holderMain.bottom_lin.setLayoutParams(paramsBottomLin);
					//set on click
					//					holderMain.bottom_lin.setOnClickListener(null);
					holderMain.more_icon.setOnClickListener(null);
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
			case (AUTHOR):
				itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.author_card,
				parent,
				false);

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

	public void flushFilter()
	{
		this.currentFilter = null;
		/* visibleObjects */orig = new ArrayList<>();
		orig.addAll(allAuthrsInfoList);
		notifyDataSetChanged();
	}

	public String getFilter()
	{
		return this.currentFilter.toLowerCase(new Locale("RU_ru"));
	}

	public void setFilter(String queryText)
	{
		queryText = queryText.toLowerCase(new Locale("RU_ru"));
		this.currentFilter = queryText;
		orig = new ArrayList<Author>();
		for (int i = 0; i < allAuthrsInfoList.size(); i++)
		{
			Author item = allAuthrsInfoList.get(i);
			if (item.getName().toLowerCase(new Locale("RU_ru")).contains(queryText.toLowerCase(new Locale("RU_ru"))))
			{
				orig.add(item);
			}
			else
			{
				this.notifyItemRemoved(i);
			}
		}
		notifyDataSetChanged();
	}

	//TODO Fix it some day
	public void filterIn(String filter)
	{
		//test adding to orig
		int countToAdd = 0;
		String filterLowerCase = filter.toLowerCase(new Locale("RU_ru"));
		this.currentFilter = filterLowerCase;
		for (int i = 0; i < this.allAuthrsInfoList.size(); i++)
		{
			String autName = this.allAuthrsInfoList.get(i).getName().toLowerCase(new Locale("RU_ru"));
			if (this.orig.size() < i)
			{
				//XXX ERROR HERE
			}
			String autNameInOrig = this.orig.get(i).getName().toLowerCase(new Locale("RU_ru"));
			//firstly check if allAutName is already exists in orig at this location
			if (autName.equals(autNameInOrig))
			{
				//notify about already added if is
				if (countToAdd != 0)
				{
					this.notifyItemRangeInserted(i - countToAdd, countToAdd);
					countToAdd = 0;
				}
			}
			else
			{
				//check if allAut contains filter
				if (autName.contains(filterLowerCase))
				{
					this.orig.add(i, this.allAuthrsInfoList.get(i));
					countToAdd++;
				}
			}
		}
	}

	public void filterOut(String filter)
	{
		filter = filter.toLowerCase(new Locale("RU_ru"));
		Log.e(LOG, filter);
		//check if there were some filtering
		if (this.currentFilter != null)
		{
			//check if previous filter equals to new without new's last char
			if (this.currentFilter.equals(filter.substring(0, filter.length() - 1)))
			{
				//if so we continue filtering this way and set currentFilter
				this.currentFilter = filter;
			}
			else
			{
				//new filter totaly mismatch or have less lenght than previous
				//so we'll filter another way
				//by just setting new "orig"
				this.setFilter(filter);
				//				this.filterIn(filter);
				return;
			}
		}
		else
		{
			//there were no filter here, so set it
			this.currentFilter = filter;
		}

		//		final int size = allAuthrsInfoList.size();
		final int size = orig.size();
		int batchCount = 0; // continuous # of items that are being removed
		for (int i = size - 1; i >= 0; i--)
		{
			//			if (allAuthrsInfoList.get(i).test(filter) == false)
			if (!orig.get(i).getName().toLowerCase(new Locale("RU_ru")).contains(filter))
			{
				Log.e(LOG, orig.get(i).getName().toLowerCase(new Locale("RU_ru")) + "/" + filter);
				orig.remove(i);
				batchCount++;
			}
			else if (batchCount != 0)
			{ // dispatch batch
				notifyItemRangeRemoved(i + 1, batchCount);
				batchCount = 0;
			}
		}
		// notify for remaining
		if (batchCount != 0)
		{ // dispatch remaining
			notifyItemRangeRemoved(0, batchCount);
		}
	}
}