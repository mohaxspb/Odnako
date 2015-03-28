package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.custom.view.FlowLayout;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Article.Tag;
import ru.kuchanov.odnako.utils.DateParse;
import ru.kuchanov.odnako.utils.HtmlTextFormatting;
import ru.kuchanov.odnako.utils.ImgLoadListenerBigSmall;
import ru.kuchanov.odnako.utils.MyUIL;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

public class AdapterRecyclerArticleFragment extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	final static String LOG = AdapterRecyclerArticleFragment.class.getSimpleName();

	private static final int HEADER = 0;
	private static final int CARD_ARTICLE_TITLE = 1;
	private static final int TEXT = 2;
	private static final int IMAGE = 3;
	private static final int CARD_COMMENTS = 4;
	private static final int CARD_SHARE = 5;
	private static final int CARD_TAGS_ALL = 6;
	private static final int CARD_ALSO_TO_READ = 7;

	private ActionBarActivity act;

	Article article;

	ImageLoader imageLoader;
	final DisplayImageOptions options;
	private SharedPreferences pref;

	boolean twoPane;

	private boolean artAuthorDescrIsShown = false;
	
	private TagNode[] articlesTags;

	public AdapterRecyclerArticleFragment(ActionBarActivity act, Article article)
	{
		this.act = act;

		this.article = article;
		this.articlesTags=this.getArticlesTags();

		pref = PreferenceManager.getDefaultSharedPreferences(act);
		twoPane = pref.getBoolean("twoPane", false);

		imageLoader = MyUIL.get(act);

		if (this.pref.getString("theme", "dark").equals("dark"))
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
				return CARD_ARTICLE_TITLE;
			default:
				if (this.article == null)
				{
					//show textHolder with default text (loading, wait);
					return TEXT;
				}
				if (this.article.getArtText().equals(Const.EMPTY_STRING))
				{
					return TEXT;
				}
//				TagNode[] articlesTags = this.getArticlesTags();
				int curPosition = position - 2;

				if (curPosition < articlesTags.length)
				{
					if (articlesTags[curPosition].getName().equals("img"))
					{
						return IMAGE;
					}
					else
					{
						return TEXT;
					}
				}
				if (curPosition == articlesTags.length)
				{
					return CARD_COMMENTS;
				}
				if (curPosition == articlesTags.length + 1)
				{
					return CARD_SHARE;
				}
				//XXX
				if (curPosition == articlesTags.length + 2)
				{
					if (!this.article.getTagsAll().equals(Const.EMPTY_STRING))
					{
						return CARD_TAGS_ALL;
					}
					else if (!this.article.getToReadMore().equals(Const.EMPTY_STRING))
					{
						return CARD_ALSO_TO_READ;
					}
				}
				if (curPosition == articlesTags.length + 3
				&& !this.article.getToReadMore().equals(Const.EMPTY_STRING))
				{
					return CARD_ALSO_TO_READ;
				}
				else
				{
					//XXX that can be reached....
					return HEADER;
				}
		}
	}

	private TagNode[] getArticlesTags()
	{
		String articleString = this.article.getArtText();
		HtmlCleaner cleaner = new HtmlCleaner();
		TagNode articleTextTag = cleaner.clean(articleString);
		//it's unexpectable, but this TagNode have "head" and "body" tags...
		//So we only need innerHTML from "body" tag;
		TagNode[] articlesTags = articleTextTag.findElementByName("body", true).getChildTags();
		TagNode formatedArticle = HtmlTextFormatting.format(articlesTags);
		return formatedArticle.getChildTags();
	}

	@Override
	public int getItemCount()
	{
		if (this.article.getArtText().equals(Const.EMPTY_STRING))
		{
			//show only header and titleCard
			return 2;
		}
		else
		{
			int numOfArticleNodes = this.articlesTags.length;
			//add commentsBrn and sharePanel
			numOfArticleNodes += 2;
			//tagsAll
			numOfArticleNodes += this.article.getTagsAll().equals(Const.EMPTY_STRING) ? 0 : 1;
			//toReadMore
			numOfArticleNodes += this.article.getToReadMore().equals(Const.EMPTY_STRING) ? 0 : 1;

			return numOfArticleNodes;
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
	{
		String scaleFactorString = pref.getString("scale", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);

		final float scale = act.getResources().getDisplayMetrics().density;
		int pixels = (int) (75 * scaleFactor * scale + 0.5f);

		//		final RecyclerView.ViewHolder h;

		switch (getItemViewType(position))
		{
			case (HEADER):
			break;
			case (CARD_ARTICLE_TITLE):
				//fill with info
				final HolderArticleTitle h = (HolderArticleTitle) holder;

				h.title.setTextSize(25 * scaleFactor);
				h.authorName.setTextSize(21 * scaleFactor);
				h.authorDescription.setTextSize(21 * scaleFactor);
				//h.authorWho.setTextSize(21 * scaleFactor);
				h.date.setTextSize(17 * scaleFactor);

				// ART_IMG
				if (!article.getImgArt().equals(Const.EMPTY_STRING) && !article.getImgArt().contains("/75_75/"))
				{
					int width = act.getResources().getDisplayMetrics().widthPixels;
					if (twoPane)
					{
						//so 2/3 of width
						width = width / 3 * 2;
					}
					int height = (int) (width / (1.7f));
					android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) h.artImg
					.getLayoutParams();
					params.height = height;
					h.artImg.setLayoutParams(params);
					String HDimgURL = article.getImgArt().replace("/120_72/", "/450_240/");

					imageLoader.displayImage(HDimgURL, h.artImg, options, new ImgLoadListenerBigSmall(
					imageLoader, options, h.artImg));
				}
				else
				{
					android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) h.artImg
					.getLayoutParams();
					params.height = 0;
					h.artImg.setLayoutParams(params);
				}
				//end of ART_IMG
				h.title.setText(Html.fromHtml(article.getTitle()));

				String dateToShow = DateParse.formatDateByCurTime(article.getPubDate());
				h.date.setText(Html.fromHtml(dateToShow));
				android.widget.LinearLayout.LayoutParams zeroHeightParams = new android.widget.LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 0);
				if (article.getTegsMain().equals(Const.EMPTY_STRING) || article.getTegsMain().equals(""))
				{
					h.tagsMain.setLayoutParams(zeroHeightParams);
				}
				else
				{
					h.tagsMain.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
					android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
					android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));

					//artTagsMain.removeAllViews();
					ArrayList<Tag> allTagsList = article.getTags(article.getTegsMain());
					if (allTagsList.size() != 0)
					{
						for (int i = 0; i < allTagsList.size(); i++)
						{
							final Tag tag = allTagsList.get(i);
							View tagCard = act.getLayoutInflater().inflate(R.layout.card_tag,
							h.tagsMain, false);

							TextView tV = (TextView) tagCard.findViewById(R.id.tag);
							tV.setOnClickListener(new OnClickListener()
							{
								@Override
								public void onClick(View v)
								{
									Actions.showAllCategoriesArticles(tag.url, act);
								}
							});
							tV.setTextSize(21 * scaleFactor);
							tV.setText(tag.title);
							h.tagsMain.addView(tagCard);
						}
					}
				}
				//AUTHOR
				if (!article.getAuthorName().equals(Const.EMPTY_STRING))
				{
					LayoutParams p = (LayoutParams) h.authorLin.getLayoutParams();
					p.height = LayoutParams.MATCH_PARENT;
					p.width = LayoutParams.MATCH_PARENT;
					p.setMargins(0, 0, 0, 0);
					h.authorLin.setLayoutParams(p);

					h.authorName.setText(article.getAuthorName());
					//author description
					h.authorDescription.setLayoutParams(zeroHeightParams);

					if (article.getAuthorDescr().equals(Const.EMPTY_STRING) || article.getAuthorDescr().equals(""))
					{
						h.authorDescription.setText(null);
						h.authorDescription.setLayoutParams(zeroHeightParams);
					}
					else
					{
						h.authorDescription.setText(Html.fromHtml(article.getAuthorDescr()));
						//restore size
						//h.authorDescrArrow.setLayoutParams(params);
						h.authorDescrArrow.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								artAuthorDescrBehavior(h);
							}
						});
					}
					////////
					if (!article.getImgAuthor().equals(Const.EMPTY_STRING))
					{
						android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) h.authorImg
						.getLayoutParams();
						params.height = pixels;
						params.width = pixels;
						params.setMargins(5, 5, 5, 5);
						h.authorImg.setLayoutParams(params);
						Log.e(LOG, "CARD_ARTICLE_TITLE AUTHOR_1");

						this.imageLoader.displayImage(article.getImgAuthor(), h.authorImg,
						MyUIL.getTransparentBackgroundROUNDOptions(act));
					}
					else
					{
						android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) h.authorImg
						.getLayoutParams();
						params.height = 0;
						params.width = 0;
						params.setMargins(0, 0, 0, 0);
						h.authorImg.setLayoutParams(params);
					}

					//set allArsList OnClick
					h.authorLin.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							Actions.showAllAuthorsArticles(article.getAuthorBlogUrl(), act);
						}
					});
				}
				else
				{
					LayoutParams params = (LayoutParams) h.authorLin.getLayoutParams();
					params.height = 0;
					params.width = 0;
					params.setMargins(0, 0, 0, 0);
					h.authorLin.setLayoutParams(params);
				}
			break;
			case TEXT:
				final HolderText hT = (HolderText) holder;
//				TagNode[] articlesTags = getArticlesTags();
				//calculate position by minusing header and titleCard
				int positionInArticlesTags = position - 1 - 1;

				hT.text.setAutoLinkMask(Linkify.ALL);
				hT.text.setLinksClickable(true);
				hT.text.setMovementMethod(LinkMovementMethod.getInstance());

				hT.text.setTextIsSelectable(true);

				hT.text.setText(Html.fromHtml("<" + articlesTags[positionInArticlesTags].getName() + ">"
				+ articlesTags[positionInArticlesTags].getText().toString() + "</"
				+ articlesTags[positionInArticlesTags].getName()
				+ ">"));
			break;
		}
	}

	private void artAuthorDescrBehavior(HolderArticleTitle h)
	{
		//set arrowDownIcon by theme
		int[] attrs = new int[] { R.attr.arrowDownIcon };
		TypedArray ta = act.obtainStyledAttributes(attrs);
		Drawable drawableArrowDown = ta.getDrawable(0);
		ta.recycle();
		attrs = new int[] { R.attr.arrowUpIcon };
		ta = act.obtainStyledAttributes(attrs);
		Drawable drawableArrowUp = ta.getDrawable(0);
		ta.recycle();
		if (!artAuthorDescrIsShown)
		{
			//set and show text
			android.widget.LinearLayout.LayoutParams descrParams = new android.widget.LinearLayout.LayoutParams(
			android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
			android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
			h.authorDescription.setLayoutParams(descrParams);
			//set btn image
			h.authorDescrArrow.setImageDrawable(drawableArrowUp);
			artAuthorDescrIsShown = !artAuthorDescrIsShown;
		}
		else
		{
			android.widget.LinearLayout.LayoutParams descrParams0 = new android.widget.LinearLayout.LayoutParams(
			android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 0);
			h.authorDescription.setLayoutParams(descrParams0);
			//set btn image
			h.authorDescrArrow.setImageDrawable(drawableArrowDown);
			artAuthorDescrIsShown = !artAuthorDescrIsShown;
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position)
	{
		View itemLayoutView = null;
		switch (position)
		{
			case (HEADER):
				itemLayoutView = new LinearLayout(act);
				TypedValue typedValue = new TypedValue();
				int[] textSizeAttr = new int[] { android.R.attr.actionBarSize };
				int indexOfAttrTextSize = 0;
				TypedArray a = act.obtainStyledAttributes(typedValue.data, textSizeAttr);
				int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
				a.recycle();
				itemLayoutView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT, actionBarSize));
				return new HolderHeader(itemLayoutView);
			case (CARD_ARTICLE_TITLE):
				itemLayoutView = act.getLayoutInflater().inflate(R.layout.article_card_art_frag,
				parent,
				false);
				return new HolderArticleTitle(itemLayoutView);
			case (TEXT):
				//TODO
				itemLayoutView = new TextView(act);
				itemLayoutView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
				return new HolderText(itemLayoutView);
			case (IMAGE):
				itemLayoutView = new ImageView(act);
				itemLayoutView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
				return new HolderImage(itemLayoutView);
			case (CARD_COMMENTS):
				itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_bottom_btn_layout,
				parent,
				false);
				return new HolderComments(itemLayoutView);
			case (CARD_SHARE):
				//TODO
				itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_panel,
				parent,
				false);
				return new HolderShare(itemLayoutView);
			case (CARD_TAGS_ALL):
				itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_tegs_layout,
				parent,
				false);
				return new HolderTagsAll(itemLayoutView);
			case (CARD_ALSO_TO_READ):
				itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.also_to_read,
				parent,
				false);
				return new HolderAlsoToRead(itemLayoutView);
			default:
				return new HolderImage(itemLayoutView);
		}
	}

	static class HolderHeader extends RecyclerView.ViewHolder
	{
		HolderHeader(View itemLayoutView)
		{
			super(itemLayoutView);
		}
	}

	static class HolderText extends RecyclerView.ViewHolder
	{
		TextView text;

		HolderText(View itemLayoutView)
		{
			super(itemLayoutView);
			this.text = (TextView) itemLayoutView;
		}
	}

	static class HolderImage extends RecyclerView.ViewHolder
	{
		ImageView img;

		HolderImage(View itemLayoutView)
		{
			super(itemLayoutView);
			this.img = (ImageView) itemLayoutView;
		}
	}

	static class HolderArticleTitle extends RecyclerView.ViewHolder
	{
		ImageView artImg;

		TextView title;
		TextView date;

		LinearLayout authorLin;
		ImageView authorImg;
		TextView authorName;
		TextView authorWho;
		TextView authorDescription;
		ImageView authorDescrArrow;

		FlowLayout tagsMain;

		HolderArticleTitle(View itemLayoutView)
		{
			super(itemLayoutView);
			//top panel
			this.artImg = (ImageView) itemLayoutView.findViewById(R.id.art_card_img);
			this.title = (TextView) itemLayoutView.findViewById(R.id.art_title);
			this.date = (TextView) itemLayoutView.findViewById(R.id.pub_date);
			//author
			this.authorLin = (LinearLayout) itemLayoutView.findViewById(R.id.author_arts_lin);
			this.authorImg = (ImageView) itemLayoutView.findViewById(R.id.art_author_img);
			this.authorName = (TextView) itemLayoutView.findViewById(R.id.art_author);
			this.authorWho = null;//TODO
			this.authorDescription = (TextView) itemLayoutView.findViewById(R.id.art_author_description);
			this.authorDescrArrow = (ImageView) itemLayoutView.findViewById(R.id.art_author_description_btn);
			//tagsMain
			this.tagsMain = (FlowLayout) itemLayoutView.findViewById(R.id.art_tags_main);
		}
	}

	static class HolderShare extends RecyclerView.ViewHolder
	{
		ViewGroup mainLin;

		HolderShare(View itemLayoutView)
		{
			super(itemLayoutView);
			this.mainLin = (ViewGroup) itemLayoutView.findViewById(R.id.share_main_lin);
		}
	}

	static class HolderComments extends RecyclerView.ViewHolder
	{
		ViewGroup mainLin;

		HolderComments(View itemLayoutView)
		{
			super(itemLayoutView);
			this.mainLin = (ViewGroup) itemLayoutView.findViewById(R.id.art_comments_bottom_btn);
		}
	}

	static class HolderTagsAll extends RecyclerView.ViewHolder
	{
		FlowLayout flow;

		HolderTagsAll(View itemLayoutView)
		{
			super(itemLayoutView);
			this.flow = (FlowLayout) itemLayoutView.findViewById(R.id.flow);
		}
	}

	static class HolderAlsoToRead extends RecyclerView.ViewHolder
	{
		ViewGroup mainLin;

		HolderAlsoToRead(View itemLayoutView)
		{
			super(itemLayoutView);
			this.mainLin = (ViewGroup) itemLayoutView.findViewById(R.id.also_main);
		}
	}
}