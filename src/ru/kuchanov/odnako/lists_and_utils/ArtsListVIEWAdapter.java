package ru.kuchanov.odnako.lists_and_utils;

import java.io.File;
import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.fragments.FragmentArtsListView;
import ru.kuchanov.odnako.utils.DipToPx;
import ru.kuchanov.odnako.utils.ReadUnreadRegister;
import ru.kuchanov.odnako.utils.MyUniversalImageLoader;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class ArtsListVIEWAdapter extends ArrayAdapter<ArtInfo>
implements Filterable
{
	private static final int HEADER = 0;
	private static final int ARTICLE = 1;
	ActionBarActivity act;

	ListView artsListView;

	ImageLoader imageLoader;

	ArrayList<ArtInfo> artsInfo;
	ArrayList<ArtInfo> orig;
	SharedPreferences pref;

	boolean twoPane;

	FragmentArtsListView artsListFrag;

	public ArtsListVIEWAdapter(ActionBarActivity act, int resource, ArrayList<ArtInfo> objects)
	{
		super(act, resource, objects);
		Log.i("ArtsListVIEWAdapter", "ArtsListVIEWAdapter called");
		this.act = act;
		this.artsInfo = objects;

		pref = PreferenceManager.getDefaultSharedPreferences(act);
		twoPane = pref.getBoolean("twoPane", false);

		imageLoader = MyUniversalImageLoader.get(act);
	}

	public void setArgs(FragmentArtsListView artsListFrag, ListView artsListView)
	{
		this.artsListView = artsListView;

		this.artsListFrag = artsListFrag;
	}

	@SuppressLint("DefaultLocale")
	public Filter getFilter()
	{
		return new Filter()
		{

			@Override
			protected FilterResults performFiltering(CharSequence constraint)
			{
				final FilterResults oReturn = new FilterResults();
				final ArrayList<ArtInfo> results = new ArrayList<ArtInfo>();
				if (orig == null)
				{
					orig = artsInfo;
				}

				if (constraint != null)
				{
					if (orig != null && orig.size() > 0)
					{
						for (final ArtInfo g : orig)
						{
							if (g.title.toLowerCase().contains(constraint.toString()))
							{
								results.add(g);
							}
						}
					}
					oReturn.values = results;
				}
				return oReturn;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results)
			{
				artsInfo = (ArrayList<ArtInfo>) results.values;
				notifyDataSetChanged();
			}
		};
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
	public int getViewTypeCount()
	{
		return 2;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		int header = 1;

		return this.artsInfo.size() + header;
	}

	public ArtInfo getArtInfoByPosition(int position)
	{
		//		this.artsInfo = ((ActivityMain)act).getAllCatArtsInfo().get(this.artsListFrag.getCategoryToLoad());
		//		this.artsInfo=this.artsListFrag.getArtsInfo();
		//		Log.i("adapter", "categoryToLoad: " + this.artsListFrag.getCategoryToLoad());
		//		Log.i("adapter", "this.artsInfo.size(): " + this.artsInfo.size());
		//		Log.i("adapter", "position: " + position);
		ArtInfo p = this.artsInfo.get(position - 1);
		return p;
	}

	public static int getPositionInAllArtsInfo(int recyclerViewPosition)
	{
		int header = 1;
		return recyclerViewPosition - header;
	}

	//	public static int getPositionInRecyclerView(int artsListPosition)
	//	{
	//		int header = 1;
	//		return artsListPosition + header;
	//	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		//		Log.i("adapter", "position: " + position);
		switch (getItemViewType(position))
		{
			case (HEADER):
				HeaderHolder holderHeader = null;
				if (convertView == null)
				{
					convertView = new View(act);
					android.widget.AbsListView.LayoutParams paramsABS = new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, (int) DipToPx.convert(165, act));
					convertView.setLayoutParams(paramsABS);
					holderHeader = new HeaderHolder(convertView);
					convertView.setTag(holderHeader);
				}
				else
				{
					holderHeader = (HeaderHolder) convertView.getTag();
				}
			break;
			case (ARTICLE):

				final ArticleHolder holderMain;// = null;
				final ArtInfo p;
				p = this.getArtInfoByPosition(position);
				final int positionInAllArtsInfo = ArtsListVIEWAdapter.getPositionInAllArtsInfo(position);

				if (convertView == null)// || convertView.getTag() instanceof HeaderHolder)
				{
					LayoutInflater vi = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = vi.inflate(R.layout.article_card, parent, false);
					holderMain = new ArticleHolder(convertView);
					convertView.setTag(holderMain);
				}
				else
				{
					holderMain = (ArticleHolder) convertView.getTag();
				}
				//variables for scaling text and icons and images from settings
				String scaleFactorString = pref.getString("scale", "1");
				float scaleFactor = Float.valueOf(scaleFactorString);

				final float scale = act.getResources().getDisplayMetrics().density;
				int pixels = (int) (75 * scaleFactor * scale + 0.5f);
				////End of variables for scaling text and icons and images from settings

				//light checked item in listView
				ViewGroup container = (ViewGroup) holderMain.card.getParent();
				//				if (this.twoPane)
				if (((ActivityMain) act).getCurentCategoryPosition() == 4 && this.pref.getBoolean("twoPane", false))
				{

					if (artsListFrag.getMyActivatedPosition() == positionInAllArtsInfo)
					{
						container.setBackgroundColor(act.getResources().getColor(R.color.blue));
					}
					else
					{
						container.setBackgroundColor(Color.TRANSPARENT);
					}
				}
				else
				{
					container.setBackgroundColor(Color.TRANSPARENT);
				}

				////////

				// ART_IMG
				if (!p.img_art.equals("empty") && !p.img_art.contains("/75_75/"))
				{
					LayoutParams params = (LayoutParams) holderMain.art_img.getLayoutParams();
					params.height = (int) DipToPx.convert(120, act);
					holderMain.art_img.setLayoutParams(params);
					String HDimgURL = p.img_art.replace("/120_72/", "/450_240/");
					//try to load big img.
					//if fails - load default
					if (this.pref.getString("theme", "dark").equals("dark"))
					{
						imageLoader.displayImage(HDimgURL, holderMain.art_img,
						MyUniversalImageLoader.getDarkOptions(),
						new ImageLoadingListener()
						{
							@Override
							public void onLoadingStarted(String imageUri, View view)
							{
								//							        ...
							}

							@Override
							public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
							{
								//							        ...
							}

							@Override
							public void onLoadingCancelled(String imageUri, View view)
							{
								//							        ...
							}

							@Override
							public void onLoadingFailed(String imageUri, View arg1, FailReason arg2)
							{
								String newURL = imageUri.replace("/450_240/", "/120_72/");
								imageLoader.displayImage(newURL, holderMain.art_img,
								MyUniversalImageLoader.getDarkOptions());
							}
						}, new ImageLoadingProgressListener()
						{
							@Override
							public void onProgressUpdate(String imageUri, View view, int current, int total)
							{
								//							        ...
							}
						});
					}
					else
					{
						imageLoader.displayImage(HDimgURL, holderMain.art_img, MyUniversalImageLoader.getLightOptions(),
						new ImageLoadingListener()
						{
							@Override
							public void onLoadingStarted(String imageUri, View view)
							{
								//							        ...
							}

							@Override
							public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
							{
								//							        ...
							}

							@Override
							public void onLoadingCancelled(String imageUri, View view)
							{
								//							        ...
							}

							@Override
							public void onLoadingFailed(String imageUri, View arg1, FailReason arg2)
							{
								String newURL = imageUri.replace("/450_240/", "/120_72/");
								imageLoader.displayImage(newURL, holderMain.art_img,
								MyUniversalImageLoader.getLightOptions());
							}
						}, new ImageLoadingProgressListener()
						{
							@Override
							public void onProgressUpdate(String imageUri, View view, int current, int total)
							{
								//							        ...
							}
						});
					}
				}
				else
				{
					LayoutParams params = (LayoutParams) holderMain.art_img.getLayoutParams();
					params.height = 0;
					holderMain.art_img.setLayoutParams(params);
				}
				//end of ART_IMG

				//Title of article
				Spanned spannedContentTitle = Html.fromHtml(p.title);
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
				if (!p.pubDate.equals("empty"))
				{
					holderMain.date.setText(p.pubDate);
					holderMain.date.setTextSize(19 * scaleFactor);
				}
				else
				{
					holderMain.date.setText("date is empty; Must hide on relize");
					holderMain.date.setTextSize(19 * scaleFactor);
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
										Actions.markAsRead(p.url, act);
										return true;
									case R.id.share_link:
										Actions.shareUrl(p.url, act);
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
				if (!p.preview.equals("empty"))
				{
					Spanned spannedContentPreview = Html.fromHtml(p.preview);
					holderMain.preview.setText(spannedContentPreview);
					holderMain.preview.setTextSize(21 * scaleFactor);
				}
				else
				{
					holderMain.preview.setText("preview is empty; Must hide on relize");
					holderMain.preview.setTextSize(21 * scaleFactor);
				}
				////end of preview

				//name  and img of author
				if (!p.img_art.equals("empty") && p.img_art.contains("/75_75/"))
				{
					LayoutParams params = (LayoutParams) holderMain.author_img.getLayoutParams();
					params.height = pixels;
					params.width = pixels;
					holderMain.author_img.setLayoutParams(params);
					if (this.pref.getString("theme", "dark").equals("dark"))
					{
						imageLoader.displayImage(p.img_art, holderMain.author_img,
						MyUniversalImageLoader.getDarkOptions());
					}
					else
					{
						imageLoader.displayImage(p.img_art, holderMain.author_img);
					}
				}
				else
				{
					LayoutParams params = (LayoutParams) holderMain.author_img.getLayoutParams();
					params.height = 0;
					params.width = 0;
					holderMain.author_img.setLayoutParams(params);
				}
				if (!p.authorName.equals("empty"))
				{
					LayoutParams params = (LayoutParams) holderMain.author_name.getLayoutParams();
					params.height = LayoutParams.WRAP_CONTENT;
					params.width = 0;
					Spanned spannedContent = Html.fromHtml("<b>" + p.authorName + "</b>");
					holderMain.author_name.setText(spannedContent);
					holderMain.author_name.setTextSize(21 * scaleFactor);
				}
				else
				{
					holderMain.author_name.setText(null);
					LayoutParams params = (LayoutParams) holderMain.author_name.getLayoutParams();
					params.height = 0;
					params.width = 0;
					holderMain.author_name.setLayoutParams(params);
				}

				holderMain.author_lin.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						Actions.showAllAuthorsArticles(p.authorBlogUrl, act);
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
				formatedLink = p.url.replace("-", "_");
				formatedLink = formatedLink.replace("/", "_");
				formatedLink = formatedLink.replace(":", "_");
				formatedLink = formatedLink.replace(".", "_");

				File currentArticleFile = new File(appDir + "/" + formatedCategory + "/"
				+ formatedLink);
				//System.out.println("Try load from file: " + currentArticleFile.getAbsolutePath());
				int pixelsForIcons = (int) (35 /** scaleFactor */
				* scale + 0.5f);
				LayoutParams paramsForIcons = new LayoutParams(pixelsForIcons, pixelsForIcons);
				paramsForIcons.setMargins(5, 5, 5, 5);

				holderMain.save.setScaleType(ScaleType.FIT_XY);
				holderMain.save.setLayoutParams(paramsForIcons);

				if (currentArticleFile.exists())
				{

				}
				if (p.url != null)
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
				ReadUnreadRegister read = new ReadUnreadRegister(act);
				holderMain.read.setLayoutParams(paramsForIcons);

				if (read.check(p.url))
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
				holderMain.share.setLayoutParams(paramsForIcons);
				holderMain.share.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						Actions.shareUrl(p.url, act);
					}
				});
				holderMain.num_of_shares.setText(String.valueOf(p.numOfSharings));
				holderMain.num_of_shares.setTextSize(21 * scaleFactor);
				holderMain.num_of_shares.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						Actions.shareUrl(p.url, act);
					}
				});
				////end of share btn

				//comments btn
				holderMain.comms.setLayoutParams(paramsForIcons);
				holderMain.comms.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						Actions.showComments(artsInfo, positionInAllArtsInfo, act);
					}
				});
				holderMain.num_of_comms.setText(String.valueOf(p.numOfComments));
				holderMain.num_of_comms.setTextSize(21 * scaleFactor);
				holderMain.num_of_comms.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						Actions.showComments(artsInfo, positionInAllArtsInfo, act);
					}
				});
			////end of comments btn
			break;

		}

		return convertView;

	}

	static class HeaderHolder
	{

		HeaderHolder(View itemLayoutView)
		{

		}
	}

	static class ArticleHolder
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
}