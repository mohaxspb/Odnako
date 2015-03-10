package ru.kuchanov.odnako.lists_and_utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.fragments.FragmentArtsListRecycler;
import ru.kuchanov.odnako.utils.DipToPx;
import ru.kuchanov.odnako.utils.ImgLoadListenerBigSmall;
import ru.kuchanov.odnako.utils.ReadUnreadRegister;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ArtsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	final static String LOG = ArtsListAdapter.class.getSimpleName();

	private static final int HEADER = 0;
	private static final int ARTICLE = 1;

	private ActionBarActivity act;

	private ImageLoader imageLoader;

	private ArrayList<Article> artsInfo;
	private SharedPreferences pref;

	private boolean twoPane;
	private boolean isInLeftPager;

	private FragmentArtsListRecycler artsListFrag;

	public ArtsListAdapter(ActionBarActivity act, ArrayList<Article> artsInfo, FragmentArtsListRecycler artsListFrag)
	{
		this.act = act;
		this.artsInfo = artsInfo;

		this.artsListFrag = artsListFrag;
		this.isInLeftPager = this.artsListFrag.isInLeftPager();

		pref = PreferenceManager.getDefaultSharedPreferences(act);
		twoPane = pref.getBoolean("twoPane", false);

		imageLoader = MyUIL.get(act);
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

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
	{
		final DisplayImageOptions options;
		if (this.pref.getString("theme", "dark").equals("dark"))
		{
			options = MyUIL.getDarkOptions();
		}
		else
		{
			options = MyUIL.getLightOptions();
		}

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

					final int positionInAllArtsInfo = ArtsListAdapter.getPositionInAllArtsInfo(position);

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
					if (!p.getImgArt().equals("empty") && !p.getImgArt().contains("/75_75/"))
					{
						LayoutParams params = (LayoutParams) holderMain.art_img.getLayoutParams();
						params.height = (int) DipToPx.convert(120, act);
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
							Actions.showArticle(artsInfo, positionInAllArtsInfo, act);
						}
					});

					holderMain.title.setTextSize(21 * scaleFactor);

					//Date
					if (p.getPubDate().getTime() != 0)
					{
						//extract date
						Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+03:00"), new Locale("ru"));
						cal.setTime(p.getPubDate());
						String h = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
						if (h.length() != 2)
						{
							h = "0" + h;
						}
						String minute = String.valueOf(cal.get(Calendar.MINUTE));
						if (minute.length() != 2)
						{
							minute = "0" + minute;
						}
						if(h.equals("00") && minute.equals("00"))
						{
							h="";
							minute="";
						}
						String d = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
						if (d.length() != 2)
						{
							d = "0" + d;
						}
						
						String month = String.valueOf(cal.get(Calendar.MONTH)+1);
						if (month.length() != 2)
						{
							month = "0" + month;
						}
						String y = String.valueOf(cal.get(Calendar.YEAR));
						Calendar calNow = Calendar.getInstance();

						String dateToShow = h + minute + d + month + y;

						if (cal.get(Calendar.YEAR) == calNow.get(Calendar.YEAR))
						{
							y = "";
							if(h.equals(""))
							{
								dateToShow = d + "/" + month;
							}
							else
							{
								dateToShow = h + ":" + minute + " " + d + "/" + month;
							}
							holderMain.date.setText(dateToShow);
						}
						else
						{
							if(h.equals(""))
							{
								dateToShow = d + "/" + month + "/" + y;
							}
							else
							{
								dateToShow = h + ":" + minute + " " + d + "/" + month + "/" + y;
							}
							holderMain.date.setText(dateToShow);
						}
						if (cal.get(Calendar.YEAR) == calNow.get(Calendar.YEAR)
						&& cal.get(Calendar.MONTH) == calNow.get(Calendar.MONTH)
						&& cal.get(Calendar.DAY_OF_MONTH) == calNow.get(Calendar.DAY_OF_MONTH))
						{
							d = "";
							month = "";
							y = "";
							if(h.equals(""))
							{
								dateToShow = "<font color='green'>Сегодня</font>";
							}
							else
							{
								dateToShow = "<font color='green'>Сегодня</font> в " + h + ":" + minute;
							}
							holderMain.date.setText(Html.fromHtml(dateToShow), TextView.BufferType.SPANNABLE);
						}
						if (cal.get(Calendar.YEAR) == calNow.get(Calendar.YEAR)
						&& cal.get(Calendar.MONTH) == calNow.get(Calendar.MONTH)
						&& (calNow.get(Calendar.DAY_OF_MONTH) - cal.get(Calendar.DAY_OF_MONTH) == 1))
						{
							d = "";
							month = "";
							y = "";
							if(h.equals(""))
							{
								dateToShow = "<font color='blue'>Вчера</font>";
							}
							else
							{
								dateToShow = "<font color='blue'>Вчера</font> в " + h + ":" + minute;
							}
							
							holderMain.date.setText(Html.fromHtml(dateToShow), TextView.BufferType.SPANNABLE);
						}

						holderMain.date.setTextSize(19 * scaleFactor);
						LayoutParams params = (LayoutParams) holderMain.date.getLayoutParams();
						params.height = LayoutParams.WRAP_CONTENT;
						holderMain.date.setLayoutParams(params);
					}
					else
					{
						//holderMain.date.setText("date is empty; Must hide on relize");
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
											Actions.markAsRead(p.getUrl(), act);
											return true;
										case R.id.share_link:
											Actions.shareUrl(p.getUrl(), act);
											return true;
										case R.id.show_comments:
											Actions.showComments(artsInfo, positionInAllArtsInfo, act);
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
					if (!p.getPreview().equals("empty"))
					{
						Spanned spannedContentPreview = Html.fromHtml(p.getPreview());
						holderMain.preview.setText(spannedContentPreview);
						holderMain.preview.setTextSize(21 * scaleFactor);
						LayoutParams params = (LayoutParams) holderMain.preview.getLayoutParams();
						params.height = LayoutParams.WRAP_CONTENT;
						params.setMargins(5, 5, 5, 5);
						holderMain.preview.setLayoutParams(params);
					}
					else
					{
						holderMain.preview.setText(null);
						holderMain.preview.setTextSize(21 * scaleFactor);
						LayoutParams params = (LayoutParams) holderMain.preview.getLayoutParams();
						params.height = 0;
						params.setMargins(0, 0, 0, 0);
						holderMain.preview.setLayoutParams(params);
					}
					////end of preview

					//name and image of author
					if (!p.getImgArt().equals("empty") && p.getImgArt().contains("/75_75/"))
					{
						LayoutParams params = (LayoutParams) holderMain.author_img.getLayoutParams();
						params.height = pixels;
						params.width = pixels;
						params.setMargins(5, 5, 5, 5);
						holderMain.author_img.setLayoutParams(params);

						this.imageLoader.displayImage(p.getImgArt(), holderMain.author_img,
						MyUIL.getTransparentBackgroundROUNDOptions(act));
					}
					else if (!p.getImg_author().equals("empty"))
					{
						LayoutParams params = (LayoutParams) holderMain.author_img.getLayoutParams();
						params.height = pixels;
						params.width = pixels;
						params.setMargins(5, 5, 5, 5);
						holderMain.author_img.setLayoutParams(params);

						this.imageLoader.displayImage(p.getImg_author(), holderMain.author_img,
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
					if (!p.getAuthorName().equals("empty"))
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
					String appDir;
					appDir = pref.getString("filesDir", "");

					String formatedCategory;
					//				formatedCategory = MainActivityNew.CATEGORY_TO_LOAD.replace("-", "_");
					String TO_DELETE = "odnako.org/blogs";
					formatedCategory = TO_DELETE.replace("-", "_");
					formatedCategory = formatedCategory.replace("/", "_");
					formatedCategory = formatedCategory.replace(":", "_");
					formatedCategory = formatedCategory.replace(".", "_");

					String formatedLink;
					formatedLink = p.getUrl().replace("-", "_");
					formatedLink = formatedLink.replace("/", "_");
					formatedLink = formatedLink.replace(":", "_");
					formatedLink = formatedLink.replace(".", "_");

					File currentArticleFile = new File(appDir + "/" + formatedCategory + "/"
					+ formatedLink);
					//System.out.println("Try load from file: " + currentArticleFile.getAbsolutePath());
					//					LayoutParams paramsForIcons = new LayoutParams((int) DipToPx.convert(25, act),
					//					(int) DipToPx.convert(25, act));
					//					paramsForIcons.setMargins(5, 5, 5, 5);

					holderMain.save.setScaleType(ScaleType.FIT_XY);
					//					holderMain.save.setLayoutParams(paramsForIcons);

					//TODO
					if (currentArticleFile.exists())
					{

					}
					if (p.getUrl() != null)
					{
						if (pref.getString("theme", "dark").equals("dark"))
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
						if (pref.getString("theme", "dark").equals("dark"))
						{
							holderMain.save.setImageResource(android.R.color.transparent);
						}
						else
						{
							holderMain.save.setImageResource(android.R.color.transparent);
						}
					}
					////end SaveImg

					//read Img
					//TODO
					ReadUnreadRegister read = new ReadUnreadRegister(act);
					//					holderMain.read.setLayoutParams(paramsForIcons);

					if (read.check(p.getUrl()))
					{
						if (pref.getString("theme", "dark").equals("dark"))
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
						if (pref.getString("theme", "dark").equals("dark"))
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
					//					holderMain.share.setLayoutParams(paramsForIcons);
					holderMain.share.setOnClickListener(new OnClickListener()
					{
						public void onClick(View v)
						{
							Actions.shareUrl(p.getUrl(), act);
						}
					});
					holderMain.num_of_shares.setText(String.valueOf(p.getNumOfSharings()));
					holderMain.num_of_shares.setTextSize(21 * scaleFactor);
					holderMain.num_of_shares.setOnClickListener(new OnClickListener()
					{
						public void onClick(View v)
						{
							Actions.shareUrl(p.getUrl(), act);
						}
					});
					////end of share btn

					//comments btn
					//					holderMain.comms.setLayoutParams(paramsForIcons);
					holderMain.comms.setOnClickListener(new OnClickListener()
					{
						public void onClick(View v)
						{
							Actions.showComments(artsInfo, positionInAllArtsInfo, act);
						}
					});
					holderMain.num_of_comms.setText(String.valueOf(p.getNumOfComments()));
					holderMain.num_of_comms.setTextSize(21 * scaleFactor);
					holderMain.num_of_comms.setOnClickListener(new OnClickListener()
					{
						public void onClick(View v)
						{
							Actions.showComments(artsInfo, positionInAllArtsInfo, act);
						}
					});
					////end of comments btn

					/////////animation
					// Here you apply the animation when the view is bound
					//					setAnimation(vg, position);
				} catch (Exception e)
				{
					return;
				}

			break;

		}

	}

	/**
	 * Here is the key method to apply the animation
	 */
	protected void setAnimation(View viewToAnimate, int position)
	{
		// If the bound view wasn't previously displayed on screen, it's animated
		//		if (position > lastPosition)
		//		{
		Animation animation = AnimationUtils.loadAnimation(this.act, android.R.anim.slide_in_left);
		//        	Animation animation = AnimationUtils.loadAnimation(this.act, android.R.anim.fade_in);
		viewToAnimate.startAnimation(animation);
		//			lastPosition = position;
		//		}
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
		TextView num_of_comms;
		TextView num_of_shares;
		TextView date;
		TextView preview;
		ImageView settings;
		ViewGroup top_lin_lay;
		CardView card;

		ArticleHolder(View itemLayoutView)
		{
			super(itemLayoutView);
			//			top panel
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
			this.num_of_comms = (TextView) itemLayoutView.findViewById(R.id.num_of_comms);
			this.num_of_shares = (TextView) itemLayoutView.findViewById(R.id.num_of_sharings);

			this.card = (CardView) itemLayoutView.findViewById(R.id.cardView);
		}
	}

	///////
	//	public ArrayList<ArtInfo> getAllArtsInfo()
	//	{
	//		return this.artsInfo;
	//	}
}