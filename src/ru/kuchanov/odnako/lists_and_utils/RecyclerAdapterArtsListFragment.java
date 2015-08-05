package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.activities.ActivityPreference;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.DataBaseHelper;
import ru.kuchanov.odnako.db.Favorites;
import ru.kuchanov.odnako.fragments.FragmentArticle;
import ru.kuchanov.odnako.fragments.FragmentArtsListRecycler;
import ru.kuchanov.odnako.utils.DateParse;
import ru.kuchanov.odnako.utils.DialogShare;
import ru.kuchanov.odnako.utils.DipToPx;
import ru.kuchanov.odnako.utils.ImgLoadListenerBigSmall;
import ru.kuchanov.odnako.utils.MyUIL;
import ru.kuchanov.odnako.utils.ServiceTTS;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RecyclerAdapterArtsListFragment extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	final static String LOG = RecyclerAdapterArtsListFragment.class.getSimpleName();

	private static final int HEADER = 0;
	private static final int ARTICLE = 1;

	private AppCompatActivity act;

	private ImageLoader imageLoader;
	private final DisplayImageOptions options;

	private ArrayList<Article> artsInfo;
	
	private SharedPreferences pref;
	private boolean nightMode;
	private boolean twoPane;
	
	private boolean isInLeftPager;

	private FragmentArtsListRecycler artsListFrag;

	public RecyclerAdapterArtsListFragment(AppCompatActivity act, ArrayList<Article> artsInfo,
	FragmentArtsListRecycler artsListFrag)
	{
		this.act = act;
		this.artsInfo = artsInfo;

		this.artsListFrag = artsListFrag;
		this.isInLeftPager = this.artsListFrag.isInLeftPager();

		pref = PreferenceManager.getDefaultSharedPreferences(act);
		twoPane = pref.getBoolean(ActivityPreference.PREF_KEY_TWO_PANE, false);

		imageLoader = MyUIL.get(act);

		nightMode = this.pref.getBoolean(ActivityPreference.PREF_KEY_NIGHT_MODE, false);
		if (nightMode == true)
		{
			options = MyUIL.getDarkOptions();
		}
		else
		{
			options = MyUIL.getLightOptions();
		}
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
			return ARTICLE;
		}
	}

	@Override
	public int getItemCount()
	{
		//this for all cats and authors
		if (this.artsInfo == null)
		{
			return 1;
		}
		int header = 1;
		return this.artsInfo.size() + header;
	}

	public Article getArtInfoByPosition(int position)
	{
		return this.artsInfo.get(position - 1);
	}

	public static int getPositionInAllArtsInfo(int recyclerViewPosition)
	{
		return recyclerViewPosition - 1;
	}

	public static int getPositionInRecyclerView(int artsListPosition)
	{
		return artsListPosition + 1;
	}

	public void updateArticle(Article a, int positionInList)
	{
		this.artsInfo.set(positionInList, a);
		//+1 because of fakeHeader
		this.notifyItemChanged(positionInList + 1);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
	{
		switch (getItemViewType(position))
		{
			case (HEADER):

			break;
			case (ARTICLE):
				//catch all cat author frags and return;
				final Article p;
				try
				{
					p = this.getArtInfoByPosition(position);
					//Log.d(LOG, p.getTitle());

					final int positionInAllArtsInfo = RecyclerAdapterArtsListFragment
					.getPositionInAllArtsInfo(position);

					final ArticleHolder holderMain = (ArticleHolder) holder;

					//variables for scaling text and icons and images from settings
					String scaleFactorString = pref.getString(ActivityPreference.PREF_KEY_UI_SCALE, "1");
					float scaleFactor = Float.valueOf(scaleFactorString);

					final float scale = act.getResources().getDisplayMetrics().density;
					int pixels = (int) (75 * scaleFactor * scale + 0.5f);
					////End of variables for scaling text and icons and images from settings

					//light checked item in listView
					//TODO getIt from Activity; Remove reference to fragment
					ViewGroup vg = (ViewGroup) holderMain.card.getParent();
					if (this.twoPane && isInLeftPager)
					{
						if (artsListFrag.getMyActivatedPosition() == positionInAllArtsInfo)
						{
							vg.setBackgroundColor(act.getResources().getColor(R.color.blue));
						}
						else
						{
							vg.setBackgroundColor(Color.TRANSPARENT);
						}
					}
					else
					{
						vg.setBackgroundColor(Color.TRANSPARENT);
					}

					////////
					// ART_IMG
					boolean showImages = this.pref.getBoolean(ActivityPreference.PREF_KEY_IMAGE_SHOW, true) == true;
					if (!p.getImgArt().equals(Const.EMPTY_STRING) && !p.getImgArt().contains("/75_75/") && showImages)
					{
						LayoutParams params = (LayoutParams) holderMain.art_img.getLayoutParams();
						int width = act.getResources().getDisplayMetrics().widthPixels;
						if (twoPane)
						{
							if (isInLeftPager)
							{
								//so 1/3 of width
								width = width / 3;
							}
							else
							{
								//so 2/3 of width
								width = width / 3 * 2;
							}
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
						holderMain.art_img.setLayoutParams(params);
						String HDimgURL = p.getImgArt().replace("/120_72/", "/450_240/");
						imageLoader.displayImage(HDimgURL, holderMain.art_img, options, new ImgLoadListenerBigSmall(
						imageLoader, options, holderMain.art_img));
					}
					else
					{
						//no image
						LayoutParams params = (LayoutParams) holderMain.art_img.getLayoutParams();
						params.height = 0;
						if (!this.pref.getString(ActivityPreference.PREF_KEY_IMAGE_POSITION,
						ActivityPreference.PREF_VALUE_IMAGE_POSITION_UP).equals(
						ActivityPreference.PREF_VALUE_IMAGE_POSITION_UP))
						{
							params.width = 0;
							params.weight = 0;
						}
						holderMain.art_img.setLayoutParams(params);
					}
					//end of ART_IMG

					//Title of article
					Spanned spannedContentTitle = Html.fromHtml(p.getTitle());
					holderMain.title.setText(spannedContentTitle);

					holderMain.top_lin_lay.setOnClickListener(new OnClickListener()
					{
						public void onClick(View v)
						{
							Actions.showArticle(artsInfo, positionInAllArtsInfo, artsListFrag.getCategoryToLoad(), act);
						}
					});

					holderMain.title.setTextSize(21 * scaleFactor);

					//Date
					if (p.getPubDate().getTime() != 0)
					{
						String dateToShow = DateParse.formatDateByCurTime(p.getPubDate());
						holderMain.date.setText(Html.fromHtml(dateToShow), TextView.BufferType.SPANNABLE);

						holderMain.date.setTextSize(19 * scaleFactor);
						LayoutParams params = (LayoutParams) holderMain.date.getLayoutParams();
						params.height = LayoutParams.WRAP_CONTENT;
						holderMain.date.setLayoutParams(params);
					}
					else
					{
						holderMain.date.setText(null);
						holderMain.date.setTextSize(19 * scaleFactor);
						LayoutParams params = (LayoutParams) holderMain.date.getLayoutParams();
						params.height = 0;
						holderMain.date.setLayoutParams(params);
					}
					////End of Date

					//popUp menu in cardView
					holderMain.settings.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							PopupMenu popup = new PopupMenu(act, v);
							MenuInflater inflater = popup.getMenuInflater();
							inflater.inflate(R.menu.art_card_menu, popup.getMenu());
							if (p.isReaden())
							{
								popup.getMenu().findItem(R.id.mark_as_read).setTitle("Отметить НЕ прочитанной");
							}
							else
							{
								popup.getMenu().findItem(R.id.mark_as_read).setTitle("Отметить прочитанной");
							}
							popup.show();
							popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
							{
								public boolean onMenuItemClick(MenuItem item)
								{
									switch (item.getItemId())
									{
										case R.id.mark_as_read:
											p.setReaden(!p.isReaden());
											DataBaseHelper h = new DataBaseHelper(act);
											Article.updateIsReaden(h, Article.getArticleIdByURL(h, p.getUrl()),
											!p.isReaden());
											h.close();

											Intent intentGlobal = new Intent(Const.Action.ARTICLE_CHANGED);
											intentGlobal.putExtra(Article.KEY_CURENT_ART, p);
											intentGlobal.putExtra(Const.Action.ARTICLE_CHANGED,
											Const.Action.ARTICLE_READ);
											LocalBroadcastManager.getInstance(act).sendBroadcast(intentGlobal);
											//Actions.markAsRead(p.getUrl(), act);
											return true;
										case R.id.share_link:
											DialogShare.showChoiceDialog(act, p, DialogShare.SHARE_TYPE_ALL);
											return true;
										case R.id.show_comments:
											Actions.showComments(artsInfo, positionInAllArtsInfo,
											artsListFrag.getCategoryToLoad(), act);
											return true;
										case R.id.speeck:
											Intent intentTTS = new Intent(act.getApplicationContext(), ServiceTTS.class);
											intentTTS.setAction("init");
											intentTTS
											.putParcelableArrayListExtra(FragmentArticle.ARTICLE_URL, artsInfo);
											intentTTS.putExtra("position", positionInAllArtsInfo);
											act.startService(intentTTS);
											return true;
										case R.id.favorites:
											Favorites.addFavorite(act, Favorites.KEY_ARTICLES, p.getUrl(), p.getTitle());
											ActivityBase actBase = ((ActivityBase) act);
											actBase.drawerRightRecyclerView.getAdapter().notifyDataSetChanged();
											return true;
										default:
											return false;
									}
								}
							});
						}
					});
					////End of popUp menu in cardView

					//preview
					boolean showPreview = this.pref.getBoolean(ActivityPreference.PREF_KEY_PREVIEW_SHOW, false) == true;
					if (!p.getPreview().equals(Const.EMPTY_STRING) && showPreview)
					{
						LayoutParams params = (LayoutParams) holderMain.preview.getLayoutParams();
						params.height = LayoutParams.WRAP_CONTENT;
						params.setMargins(5, 5, 5, 5);
						holderMain.preview.setLayoutParams(params);
						holderMain.preview.setTextSize(21 * scaleFactor);
						Spanned spannedContentPreview = Html.fromHtml(p.getPreview());
						holderMain.preview.setText(spannedContentPreview);
					}
					else
					{
						LayoutParams params = (LayoutParams) holderMain.preview.getLayoutParams();
						params.height = 0;
						params.setMargins(0, 0, 0, 0);
						holderMain.preview.setLayoutParams(params);
						holderMain.preview.setText(null);
						holderMain.preview.setTextSize(21 * scaleFactor);
					}
					////end of preview

					//name and image of author
					boolean showAuthorImage = this.pref.getBoolean(ActivityPreference.PREF_KEY_AUTHOR_IMAGE_SHOW, true) == true;
					if (!p.getImgArt().equals(Const.EMPTY_STRING) && p.getImgArt().contains("/75_75/")
					&& showAuthorImage)
					{
						LayoutParams params = (LayoutParams) holderMain.author_img.getLayoutParams();
						params.height = pixels;
						params.width = pixels;
						params.setMargins(5, 5, 5, 5);
						holderMain.author_img.setLayoutParams(params);

						this.imageLoader.displayImage(p.getImgArt(), holderMain.author_img,
						MyUIL.getTransparentBackgroundROUNDOptions(act));
					}
					else if (!p.getImgAuthor().equals(Const.EMPTY_STRING) && showAuthorImage)
					{
						LayoutParams params = (LayoutParams) holderMain.author_img.getLayoutParams();
						params.height = pixels;
						params.width = pixels;
						params.setMargins(5, 5, 5, 5);
						holderMain.author_img.setLayoutParams(params);

						this.imageLoader.displayImage(p.getImgAuthor(), holderMain.author_img,
						MyUIL.getTransparentBackgroundROUNDOptions(act));
					}
					else
					{
						LayoutParams params = (LayoutParams) holderMain.author_img.getLayoutParams();
						params.height = 0;
						params.width = 0;
						params.setMargins(0, 0, 0, 0);
						holderMain.author_img.setLayoutParams(params);
					}
					if (!p.getAuthorName().equals(Const.EMPTY_STRING))
					{
						LayoutParams params = (LayoutParams) holderMain.author_name.getLayoutParams();
						params.height = LayoutParams.WRAP_CONTENT;
						params.setMargins(5, 5, 5, 5);
						params.width = 0;
						Spanned spannedContent = Html.fromHtml("<b>" + p.getAuthorName() + "</b>");
						holderMain.author_name.setText(spannedContent);
						holderMain.author_name.setTextSize(21 * scaleFactor);
					}
					else
					{
						holderMain.author_name.setText(null);
						LayoutParams params = (LayoutParams) holderMain.author_name.getLayoutParams();
						params.height = 0;
						params.width = 0;
						params.setMargins(0, 0, 0, 0);
						holderMain.author_name.setLayoutParams(params);
					}

					holderMain.author_lin.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							Actions.showAllAuthorsArticles(p.getAuthorBlogUrl(), act);
						}
					});
					holderMain.author_lin.setOnLongClickListener(new OnLongClickListener()
					{
						@Override
						public boolean onLongClick(View v)
						{
							Log.e(LOG, "OnLongClick called!");
							Favorites.addFavorite(act, Favorites.KEY_AUTHORS, p.getAuthorBlogUrl(), p.getAuthorName());
							ActivityBase actBase = ((ActivityBase) act);
							actBase.drawerRightRecyclerView.getAdapter().notifyDataSetChanged();
							return true;
						}
					});
					//end of name of author

					//SaveImg
					if (!p.getArtText().equals(Const.EMPTY_STRING))
					{
						//Log.e(LOG, p.getUrl()+" /text is NOT empty");
						holderMain.save.setContentDescription(act.getResources().getString(R.string.get_art_text));
						if (nightMode == true)
						{
							holderMain.save.setImageResource(R.drawable.ic_save_white_48dp);
						}
						else
						{
							holderMain.save.setImageResource(R.drawable.ic_save_grey600_48dp);
						}
						holderMain.save.setOnClickListener(new OnClickListener()
						{
							public void onClick(View v)
							{
								DialogShare.showChoiceDialog(act, p, DialogShare.SHARE_TYPE_TEXT);
							}
						});
					}
					else
					{
						//Log.e(LOG, p.getUrl()+" /text IS empty");
						holderMain.save.setContentDescription(act.getResources().getString(R.string.download_article));
						if (nightMode == true)
						{
							holderMain.save.setImageResource(R.drawable.ic_file_download_white_48dp);
						}
						else
						{
							holderMain.save.setImageResource(R.drawable.ic_file_download_grey600_48dp);
						}
						holderMain.save.setOnClickListener(new OnClickListener()
						{
							public void onClick(View v)
							{
								Actions.startDownLoadArticle(p.getUrl(), act, true);
							}
						});
					}
					////end SaveImg
					//XXX
					//read Img
					//Log.d(LOG, "p.isReaden(): " + String.valueOf(p.isReaden()));
					boolean artBackgroundIsReaden = this.pref.getBoolean(
					ActivityPreference.PREF_KEY_ARTICLE_IS_READEN_BACKGROUND, true) == true;
					//set arrowDownIcon by theme
					int[] attrs = new int[] { ru.kuchanov.odnako.R.attr.cardBackGroundColor };
					TypedArray ta = act.obtainStyledAttributes(attrs);
					int defaultBackgroundColor = ta.getColor(0, Color.WHITE);
					ta.recycle();
					attrs = new int[] { R.attr.cardBackGroundColorDark };
					ta = act.obtainStyledAttributes(attrs);
					int readenBackgroundColor = ta.getColor(0, Color.WHITE);
					ta.recycle();
					attrs = new int[] { ru.kuchanov.odnako.R.attr.readenIcon };
					ta = act.obtainStyledAttributes(attrs);
					int readenIconId = ta.getResourceId(0, R.drawable.ic_drafts_white_48dp);
					ta.recycle();
					attrs = new int[] { ru.kuchanov.odnako.R.attr.unreadenIcon };
					ta = act.obtainStyledAttributes(attrs);
					int unreadenIconId = ta.getResourceId(0, R.drawable.ic_markunread_white_48dp);
					ta.recycle();
					if (p.isReaden())
					{
						if (artBackgroundIsReaden)
						{
							holderMain.card.setCardBackgroundColor(readenBackgroundColor);
						}
						else
						{
							holderMain.card.setCardBackgroundColor(defaultBackgroundColor);
						}
						holderMain.read.setImageResource(readenIconId);
					}
					else
					{
						holderMain.card.setCardBackgroundColor(defaultBackgroundColor);
						holderMain.read.setImageResource(unreadenIconId);
					}
					holderMain.read.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							p.setReaden(!p.isReaden());
							DataBaseHelper h = new DataBaseHelper(act);
							Article.updateIsReaden(h, Article.getArticleIdByURL(h, p.getUrl()),
							!p.isReaden());
							h.close();

							Intent intentGlobal = new Intent(Const.Action.ARTICLE_CHANGED);
							intentGlobal.putExtra(Article.KEY_CURENT_ART, p);
							intentGlobal.putExtra(Const.Action.ARTICLE_CHANGED,
							Const.Action.ARTICLE_READ);
							LocalBroadcastManager.getInstance(act).sendBroadcast(intentGlobal);
						}
					});
					////end read Img

					//share btn
					//holderMain.share.setLayoutParams(paramsForIcons);
					holderMain.share.setOnClickListener(new OnClickListener()
					{
						public void onClick(View v)
						{
							Actions.shareUrl(p.getUrl(), act);
						}
					});
					////end of share btn

					//comments btn
					//holderMain.comms.setLayoutParams(paramsForIcons);
					holderMain.comms.setOnClickListener(new OnClickListener()
					{
						public void onClick(View v)
						{
							Actions.showComments(artsInfo, positionInAllArtsInfo, artsListFrag.getCategoryToLoad(), act);
						}
					});
				} catch (Exception e)
				{
					return;
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

			case (ARTICLE):
				String imgPos = this.pref.getString(ActivityPreference.PREF_KEY_IMAGE_POSITION,
				ActivityPreference.PREF_VALUE_IMAGE_POSITION_UP);
				int layoutId = R.layout.article_card_image_top;
				switch (imgPos)
				{
					case ActivityPreference.PREF_VALUE_IMAGE_POSITION_UP:
						layoutId = R.layout.article_card_image_top;
					break;
					case ActivityPreference.PREF_VALUE_IMAGE_POSITION_LEFT:
						layoutId = R.layout.article_card_image_left;
					break;
					case ActivityPreference.PREF_VALUE_IMAGE_POSITION_RIGHT:
						layoutId = R.layout.article_card_image_right;
					break;
				}
				itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(layoutId,
				parent,
				false);
				// create ViewHolder
				holder = new ArticleHolder(itemLayoutView);
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

	static class ArticleHolder extends RecyclerView.ViewHolder
	{
		TextView title;
		TextView author_name;
		ImageView author_img;
		ViewGroup author_lin;
		ImageView art_img;
		ImageView save;
		ImageView read;
		ImageView comms;
		ImageView share;
		TextView date;
		TextView preview;
		ImageView settings;
		ViewGroup top_lin_lay;
		CardView card;

		ArticleHolder(View itemLayoutView)
		{
			super(itemLayoutView);
			//top panel
			this.top_lin_lay = (ViewGroup) itemLayoutView.findViewById(R.id.art_card_top_lin);
			this.art_img = (ImageView) itemLayoutView.findViewById(R.id.art_card_img);
			this.title = (TextView) itemLayoutView.findViewById(R.id.art_card_title_tv);
			this.date = (TextView) itemLayoutView.findViewById(R.id.art_card_date_tv);
			this.settings = (ImageView) itemLayoutView.findViewById(R.id.art_card_settings);
			this.preview = (TextView) itemLayoutView.findViewById(R.id.art_card_preview_tv);
			//author
			this.author_name = (TextView) itemLayoutView.findViewById(R.id.author_name);
			this.author_img = (ImageView) itemLayoutView.findViewById(R.id.art_card_author_img);
			this.author_lin = (ViewGroup) itemLayoutView.findViewById(R.id.art_card_author_lin);
			//bottom panel
			this.save = (ImageView) itemLayoutView.findViewById(R.id.save_img);
			this.read = (ImageView) itemLayoutView.findViewById(R.id.read_img);
			this.comms = (ImageView) itemLayoutView.findViewById(R.id.comments_img);
			this.share = (ImageView) itemLayoutView.findViewById(R.id.share_img);

			this.card = (CardView) itemLayoutView.findViewById(R.id.cardView);
		}
	}
}