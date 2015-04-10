package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.fragments.FragmentArtsListRecycler;
import ru.kuchanov.odnako.utils.DateParse;
import ru.kuchanov.odnako.utils.DipToPx;
import ru.kuchanov.odnako.utils.ImgLoadListenerBigSmall;
import ru.kuchanov.odnako.utils.MyUIL;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class RecyclerAdapterArtsListFragment extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	final static String LOG = RecyclerAdapterArtsListFragment.class.getSimpleName();

	private static final int HEADER = 0;
	private static final int ARTICLE = 1;

	private ActionBarActivity act;

	private ImageLoader imageLoader;
	private final DisplayImageOptions options;
	boolean nightMode;

	private ArrayList<Article> artsInfo;
	private SharedPreferences pref;

	private boolean twoPane;
	private boolean isInLeftPager;

	private FragmentArtsListRecycler artsListFrag;

	public RecyclerAdapterArtsListFragment(ActionBarActivity act, ArrayList<Article> artsInfo,
	FragmentArtsListRecycler artsListFrag)
	{
		this.act = act;
		this.artsInfo = artsInfo;

		this.artsListFrag = artsListFrag;
		this.isInLeftPager = this.artsListFrag.isInLeftPager();

		pref = PreferenceManager.getDefaultSharedPreferences(act);
		twoPane = pref.getBoolean("twoPane", false);

		imageLoader = MyUIL.get(act);

		nightMode = this.pref.getBoolean("night_mode", false);
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
					String scaleFactorString = pref.getString("scale", "1");
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
					if (!p.getImgArt().equals(Const.EMPTY_STRING) && !p.getImgArt().contains("/75_75/"))
					{
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
						int height = (int) (width / (1.7f));

						LayoutParams params = (LayoutParams) holderMain.art_img.getLayoutParams();
						params.height = height;
						holderMain.art_img.setLayoutParams(params);
						String HDimgURL = p.getImgArt().replace("/120_72/", "/450_240/");
						imageLoader.displayImage(HDimgURL, holderMain.art_img, options, new ImgLoadListenerBigSmall(
						imageLoader, options, holderMain.art_img));
					}
					else
					{
						LayoutParams params = (LayoutParams) holderMain.art_img.getLayoutParams();
						params.height = 0;
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
							popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
							{

								public boolean onMenuItemClick(MenuItem item)
								{
									switch (item.getItemId())
									{
										case R.id.mark_as_read:
											//TODO
											Actions.markAsRead(p.getUrl(), act);
											return true;
										case R.id.share_link:
											//TODO
											Actions.shareUrl(p.getUrl(), act);
											return true;
										case R.id.show_comments:
											Actions.showComments(artsInfo, positionInAllArtsInfo,
											artsListFrag.getCategoryToLoad(), act);
											return true;
										default:
											return false;
									}
								}
							});
							MenuInflater inflater = popup.getMenuInflater();
							inflater.inflate(R.menu.art_card_menu_ligth, popup.getMenu());
							popup.show();
						}
					});
					////End of popUp menu in cardView

					//preview
					if (!p.getPreview().equals(Const.EMPTY_STRING))
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
					if (!p.getImgArt().equals(Const.EMPTY_STRING) && p.getImgArt().contains("/75_75/"))
					{
						LayoutParams params = (LayoutParams) holderMain.author_img.getLayoutParams();
						params.height = pixels;
						params.width = pixels;
						params.setMargins(5, 5, 5, 5);
						holderMain.author_img.setLayoutParams(params);

						this.imageLoader.displayImage(p.getImgArt(), holderMain.author_img,
						MyUIL.getTransparentBackgroundROUNDOptions(act));
					}
					else if (!p.getImgAuthor().equals(Const.EMPTY_STRING))
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
					//end of name of author

					//SaveImg
					if (!p.getArtText().equals(Const.EMPTY_STRING))
					{
						LayoutParams params = (LayoutParams) holderMain.save.getLayoutParams();
						params.height = (int) DipToPx.convert(25, act);
						params.width = (int) DipToPx.convert(25, act);
						holderMain.save.setLayoutParams(params);
						if (nightMode == true)
						{
							holderMain.save.setImageResource(R.drawable.ic_save_white_48dp);
						}
						else
						{
							holderMain.save.setImageResource(R.drawable.ic_save_grey600_48dp);
						}
					}
					else
					{
						LayoutParams params = (LayoutParams) holderMain.save.getLayoutParams();
						params.height = 0;
						params.width = 0;
						holderMain.save.setLayoutParams(params);
						holderMain.save.setImageResource(android.R.color.transparent);
					}
					////end SaveImg

					//read Img
					//Log.d(LOG, "p.isReaden(): " + String.valueOf(p.isReaden()));
					if (p.isReaden())
					{
						if (nightMode == true)
						{
							holderMain.read.setImageResource(R.drawable.ic_drafts_white_48dp);
						}
						else
						{
							holderMain.read.setImageResource(R.drawable.ic_drafts_grey600_48dp);
						}
					}
					else
					{
						if (nightMode == true)
						{
							holderMain.read.setImageResource(R.drawable.ic_markunread_white_48dp);
						}
						else
						{
							holderMain.read.setImageResource(R.drawable.ic_markunread_grey600_48dp);
						}
					}
					////end read Img

					//share btn
					//holderMain.share.setLayoutParams(paramsForIcons);
					holderMain.share.setOnClickListener(new OnClickListener()
					{
						public void onClick(View v)
						{
							//TODO
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
				// create a new view
				itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_card,
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