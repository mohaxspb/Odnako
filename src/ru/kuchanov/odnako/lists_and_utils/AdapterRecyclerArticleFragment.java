package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.custom.view.FlowLayout;
import ru.kuchanov.odnako.custom.view.JBTextView;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Article.Tag;
import ru.kuchanov.odnako.fragments.FragmentArticle;
import ru.kuchanov.odnako.utils.DateParse;
import ru.kuchanov.odnako.utils.HtmlTextFormatting;
import ru.kuchanov.odnako.utils.ImgLoadListenerBigSmall;
import ru.kuchanov.odnako.utils.MyUIL;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
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

	public static final int HEADER = 0;
	public static final int CARD_ARTICLE_TITLE = 1;
	public static final int TEXT = 2;
	public static final int IMAGE = 3;
	public static final int CARD_COMMENTS = 4;
	public static final int CARD_SHARE = 5;
	public static final int CARD_TAGS_ALL = 6;
	public static final int CARD_ALSO_TO_READ = 7;

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
		if (this.article != null)//!this.article.getArtText().equals(Const.EMPTY_STRING))
		{
			this.articlesTags = this.getArticlesTags();
		}
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
		if (this.article == null)
		{
			return 1;
		}
		else if (this.article.getArtText().equals(Const.EMPTY_STRING))
		{
			//show only header and titleCard
			return 2;
		}
		else
		{
			int itemCount = this.articlesTags.length;
			//add fakeHeader and titleCard
			itemCount += 2;
			//add commentsBrn and sharePanel
			itemCount += 2;
			//tagsAll
			itemCount += this.article.getTagsAll().equals(Const.EMPTY_STRING) ? 0 : 1;
			//toReadMore
			itemCount += this.article.getToReadMore().equals(Const.EMPTY_STRING) ? 0 : 1;
			return itemCount;
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
	{
		String scaleFactorString = pref.getString("scale", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);

		String scaleFactorArticleString = pref.getString("scale_art", "1");
		float scaleFactorArticle = Float.valueOf(scaleFactorArticleString);

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
			case TEXT:
				final HolderText hT = (HolderText) holder;
				//calculate position by minusing header and titleCard
				int positionInArticlesTags = position - 1 - 1;

				hT.text.setPadding(10, 10, 10, 0);
				hT.text.setTextSize(21 * scaleFactorArticle);

				hT.text.setTextIsSelectable(true);

				//For no reason (?) this not work
				//hT.text.setAutoLinkMask(Linkify.ALL);

				hT.text.setLinksClickable(true);
				hT.text.setMovementMethod(LinkMovementMethod.getInstance());

				hT.text.setText(Html.fromHtml("<" + articlesTags[positionInArticlesTags].getName() + ">"
				+ articlesTags[positionInArticlesTags].getText().toString() + "</"
				+ articlesTags[positionInArticlesTags].getName()
				+ ">"));
			break;
			case IMAGE:
				final HolderImage hI = (HolderImage) holder;
				//calculate position by minusing header and titleCard
				positionInArticlesTags = position - 1 - 1;

				int width = act.getResources().getDisplayMetrics().widthPixels;
				if (twoPane)
				{
					//so 2/3 of width
					width = width / 3 * 2;
				}
				//style="height:697px; width:500px"
				String style = articlesTags[positionInArticlesTags].getAttributeByName("style");
				int imgW = Integer.parseInt(style.substring(style.indexOf("width") + 6, style.lastIndexOf("px")));
				int imgH = Integer.parseInt(style.substring(style.indexOf("height") + 7, style.indexOf("px")));
				float imgScale = (float) (imgH) / (float) (imgW);
				int height = (int) (width * imgScale);
				android.view.ViewGroup.LayoutParams params = (android.view.ViewGroup.LayoutParams) hI.img
				.getLayoutParams();
				params.height = height;
				hI.img.setLayoutParams(params);
				String HDimgURL = articlesTags[positionInArticlesTags].getAttributeByName("src");

				imageLoader.displayImage(HDimgURL, hI.img, options, new ImgLoadListenerBigSmall(
				imageLoader, options, hI.img));
			break;
			case CARD_TAGS_ALL:
				final HolderTagsAll hTA = (HolderTagsAll) holder;
				//remove all views to avoid dublicates and add description TV
				hTA.flow.removeAllViews();
				TextView description = (TextView) this.act.getLayoutInflater().inflate(
				R.layout.card_description_text_view, hTA.flow, false);
				description.setText(R.string.tags);
				hTA.flow.addView(description);

				ArrayList<Tag> allTagsList = article.getTags(article.getTagsAll());
				for (int i = 0; i < allTagsList.size(); i++)
				{
					final Tag tag = allTagsList.get(i);
					View tagCard = this.act.getLayoutInflater().inflate(R.layout.card_tag, hTA.flow, false);
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
					hTA.flow.addView(tagCard);
				}
			break;
			case CARD_ALSO_TO_READ:
				final HolderAlsoToRead hATR = (HolderAlsoToRead) holder;
				//remove all views to avoid dublicates and add description TV
				hATR.mainLin.removeAllViews();
				TextView descriptionAlsoToRead = (TextView) this.act.getLayoutInflater().inflate(
				R.layout.card_description_text_view, hATR.mainLin, false);
				descriptionAlsoToRead.setText(R.string.also_to_read);
				hATR.mainLin.addView(descriptionAlsoToRead);

				final Article.AlsoToRead alsoToRead = article.getAlsoToReadMore();
				if (alsoToRead != null)
				{
					LayoutInflater inflater = act.getLayoutInflater();
					for (int i = 0; i < alsoToRead.titles.length; i++)
					{
						final int iterator = i;

						CardView c = (CardView) inflater.inflate(R.layout.also_to_read_art_lay, hATR.mainLin, false);
						LinearLayout lin = (LinearLayout) c.findViewById(R.id.main_lin);
						lin.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								//ViewGroup vg=(ViewGroup) act.findViewById(R.id.container_right);
								Fragment newFragment = new FragmentArticle();
								Article a = new Article();
								a.setUrl(alsoToRead.urls[iterator]);
								a.setTitle(alsoToRead.titles[iterator]);
								a.setPubDate(DateParse.parse(alsoToRead.dates[iterator]));
								Bundle b = new Bundle();
								b.putParcelable(Article.KEY_CURENT_ART, a);
								newFragment.setArguments(b);
								FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction();
								ft.replace(R.id.container_right, newFragment, FragmentArticle.LOG);
								ft.commit();

								final Toolbar toolbar;
								if (act instanceof ActivityMain)
								{
									toolbar = (Toolbar) act.findViewById(R.id.toolbar_right);
								}
								else
								{
									toolbar = (Toolbar) act.findViewById(R.id.toolbar);
								}
								//set arrowDownIcon by theme
								int[] attrs = new int[] { R.attr.arrowBackIcon };
								TypedArray ta = act.obtainStyledAttributes(attrs);
								Drawable drawableArrowBack = ta.getDrawable(0);
								ta.recycle();
								toolbar.setNavigationIcon(drawableArrowBack);
								toolbar.setNavigationOnClickListener(new View.OnClickListener()
								{
									@Override
									public void onClick(View v)
									{
										toolbar.setNavigationIcon(null);
										act.onBackPressed();
									}
								});
							}
						});
						TextView title = (TextView) c.findViewById(R.id.title);
						title.setText(alsoToRead.titles[i]);
						TextView date = (TextView) c.findViewById(R.id.date);
						date.setText(alsoToRead.dates[i]);
						hATR.mainLin.addView(c);
					}
				}
			break;
			case CARD_COMMENTS:
				HolderComments hC = (HolderComments) holder;
				hC.mainLin.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						//TODO
						//Actions.showComments(allArtsInfo, getPosition(), act);
					}
				});
			break;
			case CARD_SHARE:
				HolderShare hS = (HolderShare) holder;
				hS.mainLin.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						Actions.shareUrl(article.getUrl(), act);
					}
				});
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
			case (TEXT):
				if (android.os.Build.VERSION.SDK_INT != android.os.Build.VERSION_CODES.JELLY_BEAN)
				{
					itemLayoutView = new TextView(act);
				}
				else
				{
					itemLayoutView = new JBTextView(act);
				}
				return new HolderText(itemLayoutView);
			case (IMAGE):
				itemLayoutView = new ImageView(act);
				return new HolderImage(itemLayoutView);
			case (CARD_COMMENTS):
				itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_bottom_btn_layout,
				parent,
				false);
				return new HolderComments(itemLayoutView);
			case (CARD_SHARE):
				DisplayMetrics displayMetrics = act.getResources().getDisplayMetrics();
				int width = displayMetrics.widthPixels;
				int minWidth = 800;
				//if(twoPane) we must set width to width/4*3 
				if (twoPane)
				{
					width = displayMetrics.widthPixels / 3 * 2;
				}
				if (width <= minWidth)
				{
					itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_panel,
					parent,
					false);
				}
				else
				{
					itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_panel_landscape,
					parent,
					false);
				}
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
			//			this.mainLin = (ViewGroup) itemLayoutView;//.findViewById(R.id.art_comments_bottom_btn);
			this.mainLin = (ViewGroup) itemLayoutView.findViewById(R.id.main_lin);
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