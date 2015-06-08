package ru.kuchanov.odnako.lists_and_utils;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityPreference;
import ru.kuchanov.odnako.custom.view.MaterialRippleLayout;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.db.Category;
import ru.kuchanov.odnako.db.DataBaseHelper;
import ru.kuchanov.odnako.db.Favorites;
import ru.kuchanov.odnako.utils.MyUIL;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RecyclerAdapterDrawerRight extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	final static String LOG = RecyclerAdapterDrawerRight.class.getSimpleName();

	public static final int HEADER = 0;
	public static final int AUTHORS = 1;
	public static final int CATEGORIES = 2;
	public static final int ARTICLES = 3;

	private AppCompatActivity act;

	ImageLoader imageLoader;
	final DisplayImageOptions options;
	private SharedPreferences pref;

	boolean twoPane;

	public RecyclerAdapterDrawerRight(AppCompatActivity act, Article article)
	{
		this.act = act;

		pref = PreferenceManager.getDefaultSharedPreferences(act);
		twoPane = pref.getBoolean(ActivityPreference.PREF_KEY_TWO_PANE, false);

		imageLoader = MyUIL.get(act);

		boolean nightModeIsOn = this.pref.getBoolean(ActivityPreference.PREF_KEY_NIGHT_MODE, false);
		if (nightModeIsOn)
		{
			options = MyUIL.getDarkOptions();
		}
		else
		{
			options = MyUIL.getLightOptions();
		}
	}

	@Override
	public int getItemViewType(int position)
	{
		switch (position)
		{
			case 0:
				return HEADER;
			case 1:
				return AUTHORS;
			case 2:
				return CATEGORIES;
			case 3:
				return ARTICLES;
			default:
				return HEADER;
		}
	}

	@Override
	public int getItemCount()
	{
		return 4;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
	{
		//set arrowDownIcon by theme
		int[] attrs = new int[] { ru.kuchanov.odnako.R.attr.arrowDownIcon };
		TypedArray ta = this.act.obtainStyledAttributes(attrs);
		final int expandIconId = ta.getResourceId(0, R.drawable.ic_action_overflow_dark);
		ta.recycle();
		//set arrowDownIcon by theme
		attrs = new int[] { R.attr.arrowUpIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		final int collapseIconId = ta.getResourceId(0, 0);
		ta.recycle();
		//set arrowDownIcon by theme
		attrs = new int[] { R.attr.authorIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		final int authorIconId = ta.getResourceId(0, 0);
		ta.recycle();

		LinearLayout.LayoutParams params;

		switch (getItemViewType(position))
		{
			case (HEADER):

			break;
			case (AUTHORS):
				final HolderAuthors hA = (HolderAuthors) holder;

				params = (LinearLayout.LayoutParams) hA.topLin
				.getLayoutParams();
				if (params.height != 0)
				{
					hA.expandIcon.setImageResource(collapseIconId);
				}
				else
				{
					hA.expandIcon.setImageResource(expandIconId);
				}

				hA.topLin.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) hA.bottomLin
						.getLayoutParams();
						if (params.height != 0)
						{
							params.height = 0;
							hA.bottomLin.setLayoutParams(params);
							hA.expandIcon.setImageResource(expandIconId);
						}
						else
						{
							params.height = FrameLayout.LayoutParams.MATCH_PARENT;
							hA.bottomLin.setLayoutParams(params);
							hA.expandIcon.setImageResource(collapseIconId);
						}
					}
				});

				//show favorite authors
				String authors = pref.getString(Favorites.KEY_AUTHORS, Const.EMPTY_STRING);
				if (!Const.EMPTY_STRING.equals(authors))
				{
					hA.bottomLin.removeAllViews();
					String[] pairs = authors.split(Favorites.DIVIDER_GROUP);
					for (String pair : pairs)
					{
						final String[] pairArr = pair.split(Favorites.DIVIDER);
						MaterialRippleLayout authorUnit = (MaterialRippleLayout) act.getLayoutInflater().inflate(
						R.layout.drawer_right_author_unit, hA.bottomLin, false);

						TextView label = (TextView) authorUnit.findViewById(R.id.name);
						label.setText(Html.fromHtml(pairArr[1]));
						LinearLayout mainLin = (LinearLayout) authorUnit.findViewById(R.id.main_lin);
						mainLin.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								Log.e(LOG, pairArr[0]);
								//TODO
							}
						});

						ImageView clear = (ImageView) authorUnit.findViewById(R.id.clear);
						clear.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								Favorites.removeFavorite(act, Favorites.KEY_AUTHORS, pairArr[0]);
								notifyDataSetChanged();
							}
						});

						//get and display author's avatar
						ImageView authorAvatar = (ImageView) authorUnit.findViewById(R.id.avatar);
						DataBaseHelper helper = new DataBaseHelper(act);
						if (Category.isCategory(helper, pairArr[0]) == null)
						{
							authorAvatar.setImageResource(authorIconId);
						}
						else
						{
							String imageUri = Author.getAvatarUrlByUrl(helper, pairArr[0]);
							imageLoader
							.displayImage(imageUri, authorAvatar, MyUIL.getTransparentBackgroundROUNDOptions(act));
						}
						helper.close();

						hA.bottomLin.addView(authorUnit);
					}
				}
				else
				{
					hA.bottomLin.removeAllViews();
				}
			break;
			case (CATEGORIES):
				final HolderCategories hC = (HolderCategories) holder;

				params = (LinearLayout.LayoutParams) hC.topLin.getLayoutParams();
				if (params.height != 0)
				{
					hC.expandIcon.setImageResource(collapseIconId);
				}
				else
				{
					hC.expandIcon.setImageResource(expandIconId);
				}

				hC.topLin.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) hC.bottomLin
						.getLayoutParams();
						if (params.height != 0)
						{
							params.height = 0;
							hC.bottomLin.setLayoutParams(params);
							hC.expandIcon.setImageResource(expandIconId);
						}
						else
						{
							params.height = FrameLayout.LayoutParams.MATCH_PARENT;
							hC.bottomLin.setLayoutParams(params);
							hC.expandIcon.setImageResource(collapseIconId);
						}
					}
				});

				//show favorite categories
				String categories = pref.getString(Favorites.KEY_CATEGORIES, Const.EMPTY_STRING);
				if (!Const.EMPTY_STRING.equals(categories))
				{
					hC.bottomLin.removeAllViews();
					String[] pairs = categories.split(Favorites.DIVIDER_GROUP);
					for (String pair : pairs)
					{
						final String[] pairArr = pair.split(Favorites.DIVIDER);
						MaterialRippleLayout articleUnit = (MaterialRippleLayout) act.getLayoutInflater().inflate(
						R.layout.drawer_right_article_unit, hC.bottomLin, false);

						TextView label = (TextView) articleUnit.findViewById(R.id.name);
						label.setText(Html.fromHtml(pairArr[1]));
						LinearLayout mainLin = (LinearLayout) articleUnit.findViewById(R.id.main_lin);
						mainLin.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								Log.e(LOG, pairArr[0]);
								//TODO
							}
						});

						ImageView clear = (ImageView) articleUnit.findViewById(R.id.clear);
						clear.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								Favorites.removeFavorite(act, Favorites.KEY_CATEGORIES, pairArr[0]);
								notifyDataSetChanged();
							}
						});
						hC.bottomLin.addView(articleUnit);
					}
				}
				else
				{
					hC.bottomLin.removeAllViews();
				}

			//				hC.bottomLin.removeAllViews();
			//				LinearLayout categoryUnit = (LinearLayout) act.getLayoutInflater().inflate(
			//				R.layout.drawer_right_category_unit,
			//				hC.bottomLin,
			//				false);
			//				hC.bottomLin.addView(categoryUnit);
			//				categoryUnit = (LinearLayout) act.getLayoutInflater().inflate(
			//				R.layout.drawer_right_category_unit,
			//				hC.bottomLin,
			//				false);
			//				hC.bottomLin.addView(categoryUnit);
			break;
			case (ARTICLES):
				final HolderArticles hArt = (HolderArticles) holder;

				params = (LinearLayout.LayoutParams) hArt.topLin.getLayoutParams();
				if (params.height != 0)
				{
					hArt.expandIcon.setImageResource(collapseIconId);
				}
				else
				{
					hArt.expandIcon.setImageResource(expandIconId);
				}

				hArt.topLin.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) hArt.bottomLin
						.getLayoutParams();
						if (params.height != 0)
						{
							params.height = 0;
							hArt.bottomLin.setLayoutParams(params);
							hArt.expandIcon.setImageResource(expandIconId);
						}
						else
						{
							params.height = FrameLayout.LayoutParams.MATCH_PARENT;
							hArt.bottomLin.setLayoutParams(params);
							hArt.expandIcon.setImageResource(collapseIconId);
						}
					}
				});

				//show favorite arts
				String articles = pref.getString(Favorites.KEY_ARTICLES, Const.EMPTY_STRING);
				if (!Const.EMPTY_STRING.equals(articles))
				{
					hArt.bottomLin.removeAllViews();
					String[] pairs = articles.split(Favorites.DIVIDER_GROUP);
					for (String pair : pairs)
					{
						final String[] pairArr = pair.split(Favorites.DIVIDER);
						MaterialRippleLayout articleUnit = (MaterialRippleLayout) act.getLayoutInflater().inflate(
						R.layout.drawer_right_article_unit, hArt.bottomLin, false);

						TextView label = (TextView) articleUnit.findViewById(R.id.name);
						label.setText(Html.fromHtml(pairArr[1]));
						LinearLayout mainLin = (LinearLayout) articleUnit.findViewById(R.id.main_lin);
						mainLin.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								Log.e(LOG, pairArr[0]);
								//TODO
							}
						});

						ImageView clear = (ImageView) articleUnit.findViewById(R.id.clear);
						clear.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								Favorites.removeFavorite(act, Favorites.KEY_ARTICLES, pairArr[0]);
								notifyDataSetChanged();
							}
						});
						hArt.bottomLin.addView(articleUnit);
					}
				}
				else
				{
					hArt.bottomLin.removeAllViews();
				}
			break;
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position)
	{
		View itemLayoutView = null;
		switch (position)
		{
			case (HEADER):
				itemLayoutView = act.getLayoutInflater().inflate(R.layout.drawer_right_header,
				parent,
				false);
				return new HolderHeader(itemLayoutView);
			case (AUTHORS):
				itemLayoutView = act.getLayoutInflater().inflate(R.layout.drawer_right_author_main,
				parent,
				false);
				return new HolderAuthors(itemLayoutView);
			case (CATEGORIES):
				itemLayoutView = act.getLayoutInflater().inflate(R.layout.drawer_right_category_main,
				parent,
				false);
				return new HolderCategories(itemLayoutView);
			case (ARTICLES):
				itemLayoutView = act.getLayoutInflater().inflate(R.layout.drawer_right_articles_main,
				parent,
				false);
				return new HolderArticles(itemLayoutView);

			default:
				return new HolderHeader(itemLayoutView);
		}
	}

	static class HolderHeader extends RecyclerView.ViewHolder
	{
		HolderHeader(View itemLayoutView)
		{
			super(itemLayoutView);
		}
	}

	static class HolderArticles extends RecyclerView.ViewHolder
	{
		LinearLayout bottomLin;
		MaterialRippleLayout topLin;
		ImageView expandIcon;

		HolderArticles(View itemLayoutView)
		{
			super(itemLayoutView);
			bottomLin = (LinearLayout) itemLayoutView.findViewById(R.id.bottom_lin);
			topLin = (MaterialRippleLayout) itemLayoutView.findViewById(R.id.top_lin);
			expandIcon = (ImageView) itemLayoutView.findViewById(R.id.expand);
		}
	}

	static class HolderAuthors extends RecyclerView.ViewHolder
	{
		LinearLayout bottomLin;
		MaterialRippleLayout topLin;
		ImageView expandIcon;

		HolderAuthors(View itemLayoutView)
		{
			super(itemLayoutView);
			bottomLin = (LinearLayout) itemLayoutView.findViewById(R.id.bottom_lin);
			topLin = (MaterialRippleLayout) itemLayoutView.findViewById(R.id.top_lin);
			expandIcon = (ImageView) itemLayoutView.findViewById(R.id.expand);
		}
	}

	static class HolderCategories extends RecyclerView.ViewHolder
	{
		LinearLayout bottomLin;
		MaterialRippleLayout topLin;
		ImageView expandIcon;

		HolderCategories(View itemLayoutView)
		{
			super(itemLayoutView);
			bottomLin = (LinearLayout) itemLayoutView.findViewById(R.id.bottom_lin);
			topLin = (MaterialRippleLayout) itemLayoutView.findViewById(R.id.top_lin);
			expandIcon = (ImageView) itemLayoutView.findViewById(R.id.expand);
		}
	}
}