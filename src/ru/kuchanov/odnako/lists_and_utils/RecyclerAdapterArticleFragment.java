package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.activities.ActivityPreference;
import ru.kuchanov.odnako.custom.view.FlowLayout;
import ru.kuchanov.odnako.custom.view.JBTextView;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Article.Tag;
import ru.kuchanov.odnako.fragments.FragmentArticle;
import ru.kuchanov.odnako.utils.DateParse;
import ru.kuchanov.odnako.utils.DialogShare;
import ru.kuchanov.odnako.utils.ImgLoadListenerBigSmall;
import ru.kuchanov.odnako.utils.MakeLinksClicable.CustomerTextClick;
import ru.kuchanov.odnako.utils.MyHtmlTagHandler;
import ru.kuchanov.odnako.utils.MyUIL;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RecyclerAdapterArticleFragment extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	final static String LOG = RecyclerAdapterArticleFragment.class.getSimpleName();

	public static final int HEADER = 0;
	public static final int CARD_ARTICLE_TITLE = 1;
	public static final int TEXT = 2;
	public static final int IMAGE = 3;
	public static final int CARD_COMMENTS = 4;
	public static final int CARD_SHARE = 5;
	public static final int CARD_TAGS_ALL = 6;
	public static final int CARD_ALSO_TO_READ = 7;

	private AppCompatActivity act;

	private Article article;

	private ImageLoader imageLoader;
	private final DisplayImageOptions options;
	private SharedPreferences pref;

	private boolean twoPane;

	private boolean artAuthorDescrIsShown = false;

	private TagNode[] articlesTags;

	public RecyclerAdapterArticleFragment(AppCompatActivity act, Article article)
	{
		this.act = act;

		this.article = article;
		if (this.article != null)//!this.article.getArtText().equals(Const.EMPTY_STRING))
		{
			this.articlesTags = DialogShare.getArticlesTags(article);
		}
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

	public void updateArticle(Article a)
	{
		this.article = a;
		if (this.article != null)//!this.article.getArtText().equals(Const.EMPTY_STRING))
		{
			this.articlesTags = DialogShare.getArticlesTags(article);
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
					//that can be reached....
					return HEADER;
				}
		}
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
		float scaleFactor=pref.getFloat(ActivityPreference.PREF_KEY_SCALE_UI, 0.75f);
		float scaleFactorArticle = pref.getFloat(ActivityPreference.PREF_KEY_SCALE_ARTICLE, 0.75f);

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
				boolean showImages = this.pref.getBoolean(ActivityPreference.PREF_KEY_IMAGE_SHOW, true) == true;
				if (!article.getImgArt().equals(Const.EMPTY_STRING) && !article.getImgArt().contains("/75_75/")
				&& showImages)
				{
					android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) h.artImg
					.getLayoutParams();
					int width = act.getResources().getDisplayMetrics().widthPixels;
					if (twoPane)
					{
						//so 2/3 of width
						width = width / 3 * 2;
					}
					if (!this.pref.getString(ActivityPreference.PREF_KEY_IMAGE_POSITION,
					ActivityPreference.PREF_VALUE_IMAGE_POSITION_UP).equals(
					ActivityPreference.PREF_VALUE_IMAGE_POSITION_UP))
					{
						width = width / 4;
						params.weight = 1;
					}
					int height = (int) (width / (1.7f));

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
					if (!this.pref.getString(ActivityPreference.PREF_KEY_IMAGE_POSITION,
					ActivityPreference.PREF_VALUE_IMAGE_POSITION_UP).equals(
					ActivityPreference.PREF_VALUE_IMAGE_POSITION_UP))
					{
						params.width = 0;
						params.weight = 0;
					}
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

				//Try to fix crushing by 
				//java.lang.IndexOutOfBoundsException: setSpan (-1 ... -1) starts before 0
				//http://stackoverflow.com/questions/25880638/rotating-android-device-while-viewing-dialog-preference-with-timepicker-or-numbe
				hT.text.setSaveFromParentEnabled(false);
				hT.text.setSaveEnabled(true);

				hT.text.setTextIsSelectable(true);

				//For no reason (?) this not work
				//hT.text.setAutoLinkMask(Linkify.ALL);

				hT.text.setText(null);

				HtmlCleaner hc = new HtmlCleaner();

				TagNode[] tags = articlesTags[positionInArticlesTags].getChildTags();

				String full = "<html><body>";

				for (TagNode tag : tags)
				{
					String text1 = "<" + tag.getName() + ">";
					String text2 = Html.fromHtml(hc.getInnerHtml(tag), null, new MyHtmlTagHandler()).toString();
					//					String text2 = hc.getInnerHtml(tag);
					String text3 = "</" + tag + ">";
					String text = text1 + text2 + text3;//(String) TextUtils.concat(text1, text2, text3);
					full += text;
					//Log.i(LOG, text);
				}

				full += "</body></html>";
				hT.text.setText(Html.fromHtml(full, null, new MyHtmlTagHandler()));

				hT.text.setLinksClickable(true);
				hT.text.setMovementMethod(LinkMovementMethod.getInstance());

				CharSequence text = hT.text.getText();
				if (text instanceof Spannable)
				{
					int end = text.length();
					Spannable sp = (Spannable) hT.text.getText();
					URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
					SpannableStringBuilder style = new SpannableStringBuilder(text);
					//					style.clearSpans();//should clear old spans
					for (URLSpan url : urls)
					{
						style.removeSpan(url);
						CustomerTextClick click = new CustomerTextClick(url.getURL());
						style.setSpan(click, sp.getSpanStart(url), sp.getSpanEnd(url),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					hT.text.setText(style);
				}
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
				//Log.e(LOG, style);
				style = style.replaceAll(" ", "").replaceAll(":", "").replaceAll("px", "");
				String[] widthHeightArr = style.split(";");
				int imgW;
				int imgH;
				if (widthHeightArr[0].contains("width"))
				{
					widthHeightArr[0] = widthHeightArr[0].replaceAll("width", "");
					widthHeightArr[1] = widthHeightArr[1].replaceAll("height", "");
					imgW = Integer.parseInt(widthHeightArr[0]);
					imgH = Integer.parseInt(widthHeightArr[1]);
				}
				else
				{
					widthHeightArr[1] = widthHeightArr[1].replaceAll("width", "");
					widthHeightArr[0] = widthHeightArr[0].replaceAll("height", "");
					imgW = Integer.parseInt(widthHeightArr[1]);
					imgH = Integer.parseInt(widthHeightArr[0]);
				}
				float imgScale = (float) (imgH) / (float) (imgW);
				int height = (int) (width * imgScale);
				android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT, height);
				params.setMargins(10, 10, 10, 10);
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
								Fragment newFragment = new FragmentArticle();
								Article a = new Article();
								a.setUrl(alsoToRead.urls[iterator]);
								a.setTitle(alsoToRead.titles[iterator]);
								a.setPubDate(DateParse.parse(alsoToRead.dates[iterator]));
								Bundle b = new Bundle();
								b.putParcelable(Article.KEY_CURENT_ART, a);
								b.putBoolean("isSingle", true);
								newFragment.setArguments(b);

								FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction();
								ft.replace(R.id.container_right, newFragment, FragmentArticle.LOG);
								ft.addToBackStack(null);
								ft.commit();

								//setBackButton to toolbar and its title
								Toolbar toolbar;
								if (!twoPane)
								{
									//So it's article activity
									((ActivityBase) act).mDrawerToggle.setDrawerIndicatorEnabled(false);
									toolbar = (Toolbar) act.findViewById(R.id.toolbar);
									toolbar.setTitle("Статья");
								}
								else
								{
									//we are on main activity, so we must set toggle to rightToolbar
									toolbar = (Toolbar) act.findViewById(R.id.toolbar_right);
									toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
									toolbar.setNavigationOnClickListener(new OnClickListener()
									{
										@Override
										public void onClick(View v)
										{
											act.onBackPressed();
										}
									});
									toolbar.setTitle("Статья");
								}
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
						Actions.addCommentsFrgament(article, act);
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
						DialogShare.showChoiceDialog(act, article, DialogShare.SHARE_TYPE_ALL);
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
				String imgPos = this.pref.getString(ActivityPreference.PREF_KEY_IMAGE_POSITION,
				ActivityPreference.PREF_VALUE_IMAGE_POSITION_UP);
				int layoutId = R.layout.article_card_image_top;
				switch (imgPos)
				{
					case ActivityPreference.PREF_VALUE_IMAGE_POSITION_UP:
						layoutId = R.layout.article_card_art_frag_image_top;
					break;
					case ActivityPreference.PREF_VALUE_IMAGE_POSITION_LEFT:
						layoutId = R.layout.article_card_art_frag_image_left;
					break;
					case ActivityPreference.PREF_VALUE_IMAGE_POSITION_RIGHT:
						layoutId = R.layout.article_card_art_frag_image_right;
					break;
				}
				itemLayoutView = act.getLayoutInflater().inflate(layoutId,
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