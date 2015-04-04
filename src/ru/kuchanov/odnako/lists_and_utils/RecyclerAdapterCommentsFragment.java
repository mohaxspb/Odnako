package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.custom.view.FlowLayout;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Article.Tag;
import ru.kuchanov.odnako.fragments.CommentDialogFragment;
import ru.kuchanov.odnako.utils.DateParse;
import ru.kuchanov.odnako.utils.ImgLoadListenerBigSmall;
import ru.kuchanov.odnako.utils.MyUIL;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

public class RecyclerAdapterCommentsFragment extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	final static String LOG = RecyclerAdapterCommentsFragment.class.getSimpleName();

	public static final int HEADER = 0;
	public static final int CARD_ARTICLE_TITLE = 1;
	public static final int COMMENT = 2;

	private ActionBarActivity act;

	private Article article;
	private ArrayList<CommentInfo> commentsInfoList;

	private ImageLoader imageLoader;
	private final DisplayImageOptions options;
	private SharedPreferences pref;

	private boolean twoPane;

	private boolean artAuthorDescrIsShown = false;

	public RecyclerAdapterCommentsFragment(ActionBarActivity act, Article article,
	ArrayList<CommentInfo> commentsInfoList)
	{
		this.act = act;

		this.article = article;
		this.commentsInfoList = commentsInfoList;

		this.pref = PreferenceManager.getDefaultSharedPreferences(act);
		this.twoPane = pref.getBoolean("twoPane", false);

		this.imageLoader = MyUIL.get(act);

		if (this.pref.getString("theme", "dark").equals("dark"))
		{
			this.options = MyUIL.getDarkOptions();
		}
		else
		{
			this.options = MyUIL.getLightOptions();
		}
	}

	public void setCommentsInfo(ArrayList<CommentInfo> commentsInfoList)
	{
		this.commentsInfoList = commentsInfoList;
	}

	public void addCommentsInfo(ArrayList<CommentInfo> commentsInfoList)
	{
		this.commentsInfoList.addAll(commentsInfoList);
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
				return COMMENT;
		}
	}

	@Override
	public int getItemCount()
	{
		if (this.article == null)
		{
			return 1;
		}
		else if (this.commentsInfoList == null)
		{
			//show only header and titleCard
			return 2;
		}
		else
		{
			int itemCount = this.commentsInfoList.size();
			//add fakeHeader and titleCard
			itemCount += 2;
			return itemCount;
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
	{
		String scaleFactorString = pref.getString("scale", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);

		final float scale = act.getResources().getDisplayMetrics().density;
		int pixels = (int) (75 * scaleFactor * scale + 0.5f);

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
					params.height = android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
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

					h.tagsMain.removeAllViews();
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
				//				h.topLin.setOnClickListener(new OnClickListener()
				//				{
				//					@Override
				//					public void onClick(View v)
				//					{
				//						FragmentComments fragComm = (FragmentComments) act.getSupportFragmentManager()
				//						.findFragmentByTag(FragmentComments.LOG);
				//						fragComm.pageToLoad = 2;
				//						fragComm.startDownload();
				//					}
				//				});

				//AUTHOR
				if (!article.getAuthorName().equals(Const.EMPTY_STRING))
				{
					LayoutParams p = (LayoutParams) h.authorLin.getLayoutParams();
					p.height = LayoutParams.WRAP_CONTENT;
					p.width = LayoutParams.MATCH_PARENT;
					p.setMargins(0, 0, 0, 0);
					h.authorLin.setLayoutParams(p);

					h.authorName.setText(article.getAuthorName());
					//author description
					h.authorDescription.setLayoutParams(zeroHeightParams);

					if (article.getAuthorDescr().equals(Const.EMPTY_STRING) || article.getAuthorDescr().equals(""))
					{
						h.authorDescription.setLayoutParams(zeroHeightParams);
						h.authorDescrArrow.setImageDrawable(null);
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
					params.setMargins(0, 0, 0, 0);
					h.authorLin.setLayoutParams(params);
					android.widget.LinearLayout.LayoutParams paramsLin = (android.widget.LinearLayout.LayoutParams) h.authorDescription
					.getLayoutParams();
					paramsLin.height = 0;
					h.authorDescription.setLayoutParams(paramsLin);
					h.authorDescrArrow.setLayoutParams(paramsLin);
				}
			break;
			case COMMENT:
				final HolderComment holderMain = (HolderComment) holder;

				//minus header and titleCard
				final CommentInfo p = this.commentsInfoList.get(position - 1 - 1);

				int padding = (Math.round(Float.valueOf(p.padding) / Float.valueOf("1.875")) - 1) * 50;

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
				LayoutParams cardParams = (LayoutParams) holderMain.card.getLayoutParams();//new LayoutParams(newWidth, LayoutParams.WRAP_CONTENT);
				cardParams.width = newWidth;
				holderMain.card.setLayoutParams(cardParams);
//				Log.e(LOG, "maxWidth/newWidth/padding: " + maxWidth + "/ " + newWidth + "/ " + padding);
				//End of set width to card

				//set name
				Spanned spannedContentName = Html.fromHtml(p.name);
				holderMain.name.setText(spannedContentName);

				//setText
				String commentText;
				commentText = "<p>" + p.txt + "</p>";
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

				ImageLoader imageLoader = MyUIL.get(act);
				imageLoader.displayImage(p.flag, holderMain.flag);
				//end of FLAG

				// AVA
				imageLoader.displayImage(p.avaImg, holderMain.avaImg);
				// AVA

				holderMain.time_city.setText(p.time + " " + p.city);

				// Karma
				//set karma's parent gravity to bottom, for known issue in Dialog
				LinearLayout vg = (LinearLayout) holderMain.like.getParent();
				android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
				vg.setLayoutParams(params);
				vg.setGravity(Gravity.RIGHT);
				holderMain.like.setText(p.like);
				holderMain.dislike.setText(p.dislike);
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
				itemLayoutView = act.getLayoutInflater().inflate(R.layout.fake_header_small,
				parent,
				false);
				return new HolderHeader(itemLayoutView);
			case (CARD_ARTICLE_TITLE):
				itemLayoutView = act.getLayoutInflater().inflate(R.layout.article_card_art_frag,
				parent,
				false);
				return new HolderArticleTitle(itemLayoutView);
			case (COMMENT):
				itemLayoutView = act.getLayoutInflater().inflate(R.layout.comment_card_view,
				parent,
				false);
				return new HolderComment(itemLayoutView);
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

	static class HolderArticleTitle extends RecyclerView.ViewHolder
	{
		ViewGroup topLin;

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
			this.topLin = (ViewGroup) itemLayoutView.findViewById(R.id.art_card_top_lin_lay);
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

	static class HolderComment extends RecyclerView.ViewHolder
	{
		CardView card;
		TextView name;
		TextView txt;
		ImageView flag;
		TextView time_city;
		TextView like;
		TextView dislike;
		ImageView avaImg;

		HolderComment(View itemLayoutView)
		{
			super(itemLayoutView);
			this.card = (CardView) itemLayoutView.findViewById(R.id.card);
			this.name = (TextView) itemLayoutView.findViewById(R.id.name);
			this.txt = (TextView) itemLayoutView.findViewById(R.id.comm_text);
			this.flag = (ImageView) itemLayoutView.findViewById(R.id.flag);
			this.time_city = (TextView) itemLayoutView.findViewById(R.id.time_city);
			this.like = (TextView) itemLayoutView.findViewById(R.id.like);
			this.dislike = (TextView) itemLayoutView.findViewById(R.id.dislike);
			this.avaImg = (ImageView) itemLayoutView.findViewById(R.id.ava);
		}
	}

	public static void showCommView(ActionBarActivity act, CommentInfo p)
	{
		CommentDialogFragment newFragment = CommentDialogFragment.newInstance(p);
		newFragment.show(act.getSupportFragmentManager(), "dialog");
	}
}