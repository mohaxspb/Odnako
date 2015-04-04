package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;
import java.util.Locale;

import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.db.Category;
import ru.kuchanov.odnako.fragments.FragmentAllCategories;
import ru.kuchanov.odnako.utils.DipToPx;
import ru.kuchanov.odnako.utils.MyUIL;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
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

public class AllCategoriesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	final static String LOG = AllCategoriesListAdapter.class.getSimpleName() + "/";

	private final Drawable drawableArrowDown;
	private final Drawable drawableArrowUp;

	private static final int HEADER = 0;
	private static final int CATEGORY = 2;

	private ActivityMain act;

	private ImageLoader imageLoader;
	private SharedPreferences pref;

	private boolean twoPane;

	private FragmentAllCategories frag;

	private ArrayList<Category> allCategoriesInfoList;
	private ArrayList<Category> orig;

	private String currentFilter = null;

	public AllCategoriesListAdapter(ActivityMain act, FragmentAllCategories artsListFrag)
	{
		this.act = act;

		this.frag = artsListFrag;

		pref = PreferenceManager.getDefaultSharedPreferences(act);
		twoPane = pref.getBoolean("twoPane", false);

		imageLoader = MyUIL.get(act);
		this.allCategoriesInfoList = (ArrayList<Category>) act.getAllCategoriesList();
		this.orig = this.allCategoriesInfoList;

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

	public ArrayList<Category> getCurAllCategoriesList()
	{
		return this.orig;
	}

	public ArrayList<Category> getAllCategoriesList()
	{
		return this.allCategoriesInfoList;
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
			return CATEGORY;
		}
	}

	@Override
	public int getItemCount()
	{
		int header = 1;
		return this.orig.size() + header;
	}

	public Category getCategoryByPosition(int position)
	{
		Category author = this.orig.get(position - 1);
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
			case (CATEGORY):
				final Category p = this.getCategoryByPosition(position);
				final int positionInAllArtsInfo = AllCategoriesListAdapter.getPositionInAllArtsInfo(position);
				final CategoryHolder holderMain = (CategoryHolder) holder;

				//variables for scaling text and icons and images from settings
				String scaleFactorString = pref.getString("scale", "1");
				float scaleFactor = Float.valueOf(scaleFactorString);
				////End of variables for scaling text and icons and images from settings

				//light checked item in listView
				if (this.twoPane)
				{
					//TODO we can get it from activity, so we actually don't need to pass fragment to adapters constructor
					if (frag.getMyActivatedPosition() == positionInAllArtsInfo)
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
				holderMain.topLin.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						Actions.showAllCategoriesArticles(p.getUrl(), act);
					}
				});

				//Category img
				String link = "";
				if (p.getImgUrl().startsWith("/"))
				{
					link = "http://odnako.org" + p.getImgUrl();
				}
				boolean nightModeIsOn = this.pref.getBoolean("night_mode", false);
				if (nightModeIsOn)
				{
					imageLoader.displayImage(link, holderMain.categoryImg, MyUIL.getDarkOptions());
				}
				else
				{
					imageLoader.displayImage(link, holderMain.categoryImg);
				}
				//end of ART_IMG

				//title 
				Spanned spannedContentTitle = Html.fromHtml(p.getTitle());
				holderMain.title.setText(spannedContentTitle);
				holderMain.title.setTextSize(35 * scaleFactor);

				//description
				if (!p.getDescription().equals("empty") && !p.getDescription().equals(""))
				{
					Spanned spannedContentPreview = Html.fromHtml(p.getDescription());
					holderMain.description.setText(spannedContentPreview);
					holderMain.description.setTextSize(21 * scaleFactor);
					holderMain.more_icon.setImageDrawable(drawableArrowDown);
				}
				else
				{
					holderMain.description.setText(null);
					holderMain.more_icon.setImageDrawable(null);
				}

				//descr onClick
				if (!p.getDescription().equals("empty") && !p.getDescription().equals(""))
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
			case (CATEGORY):
				// create a new view
				itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card,
				parent,
				false);
				// create ViewHolder
				holder = new CategoryHolder(itemLayoutView);
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

	static class CategoryHolder extends RecyclerView.ViewHolder
	{
		View container;

		ViewGroup topLin;

		ImageView categoryImg;
		TextView title;

		ViewGroup bottom_lin;

		TextView description;
		ImageView more_icon;

		CategoryHolder(View itemLayoutView)
		{
			super(itemLayoutView);
			this.container = itemLayoutView;

			this.topLin = (ViewGroup) itemLayoutView.findViewById(R.id.category_top_lin);

			this.categoryImg = (ImageView) itemLayoutView.findViewById(R.id.category_img);
			this.title = (TextView) itemLayoutView.findViewById(R.id.category_title);

			this.bottom_lin = (ViewGroup) itemLayoutView.findViewById(R.id.category_card_bottom_lin);

			this.description = (TextView) itemLayoutView.findViewById(R.id.category_description);
			this.more_icon = (ImageView) itemLayoutView.findViewById(R.id.more_icon);
		}
	}

	public void flushFilter()
	{
		this.currentFilter = null;
		/* visibleObjects */orig = new ArrayList<>();
		orig.addAll(allCategoriesInfoList);
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
		orig = new ArrayList<Category>();
		for (int i = 0; i < allCategoriesInfoList.size(); i++)
		{
			Category item = allCategoriesInfoList.get(i);
			if (item.getTitle().toLowerCase(new Locale("RU_ru")).contains(queryText.toLowerCase(new Locale("RU_ru"))))
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
}