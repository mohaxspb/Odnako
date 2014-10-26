package ru.kuchanov.odnako.lists_and_utils;

import java.io.File;
import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityArticle;
import ru.kuchanov.odnako.activities.ActivityComments;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.fragments.ArticleFragment;
import ru.kuchanov.odnako.fragments.ArticlesListFragment;
import ru.kuchanov.odnako.fragments.CommentsFragment;
import ru.kuchanov.odnako.utils.ReadUnreadRegister;
import ru.kuchanov.odnako.utils.UniversalImageLoader;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ArtsListAdapter extends ArrayAdapter<ArtInfo> implements Filterable
{

	ActionBarActivity act;
	LayoutInflater lInflater;

	ListView artsListView;

	ArrayList<ArtInfo> artsInfo;
	ArrayList<ArtInfo> orig;
	SharedPreferences pref;

	public ArtsListAdapter(ActionBarActivity act, int resource, ArrayList<ArtInfo> artsInfo,
	ListView artsListView)
	{
		super(act, resource, artsInfo);
		this.act = act;
		this.artsInfo = artsInfo;
		lInflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		this.artsListView = artsListView;
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

	public void notifyDataSetChanged()
	{
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount()
	{
		return artsInfo.size();
	}

	@Override
	public ArtInfo getItem(int position)
	{
		return (artsInfo.get(position));
	}

	ArtInfo getArticle(int position)
	{
		return ((ArtInfo) getItem(position));
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		final ArtInfo p = getArticle(position);
		int type = getItemViewType(position);
		View view = null;
		switch (type)
		{
			case 0:
				ArticleHolder holderMain;
				if (convertView == null)
				{
					ViewGroup vg;
					vg = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.arts_list_card_view,
					new LinearLayout(act));
					holderMain = new ArticleHolder(
					(TextView) vg.findViewById(R.id.art_card_title_tv),
					(TextView) vg.findViewById(R.id.author_name),
					(ImageView) vg.findViewById(R.id.art_card_img),
					(ImageView) vg.findViewById(R.id.save_img),
					(ImageView) vg.findViewById(R.id.read_img),
					(ImageView) vg.findViewById(R.id.comments_img),
					(ImageView) vg.findViewById(R.id.share_img),
					(TextView) vg.findViewById(R.id.num_of_comms),
					(TextView) vg.findViewById(R.id.num_of_sharings),
					(TextView) vg.findViewById(R.id.art_card_date_tv),
					(TextView) vg.findViewById(R.id.art_card_preview_tv),
					(ImageView) vg.findViewById(R.id.art_card_settings),
					(ViewGroup) vg.findViewById(R.id.art_card_top_lin_lay));
					vg.setTag(holderMain);
					view = vg;

				}
				else
				{
					view = convertView;
					holderMain = (ArticleHolder) convertView.getTag();
				}

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
										ArtsListAdapter.markAsRead(p.url, act);
										return true;
									case R.id.share_link:
										ArtsListAdapter.shareUrl(p.url, act);
										return true;
									case R.id.show_comments:
										ArtsListAdapter.showComments(p, position, act);
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

				//light checked item in listView
				ArticlesListFragment artsListFrag = (ArticlesListFragment) act
				.getSupportFragmentManager().findFragmentById(R.id.articles_list);

				if (artsListFrag.getMyActivatedPosition() == position)
				{
					view.setBackgroundColor(act.getResources().getColor(R.color.blue));
				}
				else
				{
					view.setBackgroundColor(Color.TRANSPARENT);
				}
				////////

				//Title of article
				Spanned spannedContentTitle = Html.fromHtml(p.title);
				holderMain.title.setText(spannedContentTitle);
				holderMain.top_lin_lay.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						ArtsListAdapter.showArticle(p, position, act);
					}
				});

				pref = PreferenceManager.getDefaultSharedPreferences(act);

				String scaleFactorString = pref.getString("scale", "1");
				float scaleFactor = Float.valueOf(scaleFactorString);
				holderMain.title.setTextSize(21 * scaleFactor);

				//preview
				Spanned spannedContentPreview = Html.fromHtml(p.preview);
				holderMain.preview.setText(spannedContentPreview);
				holderMain.preview.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						ArtsListAdapter.showArticle(p, position, act);
					}
				});

				holderMain.preview.setTextSize(21 * scaleFactor);

				// ART_IMG
				final float scale = act.getResources().getDisplayMetrics().density;
				int pixels = (int) (75 * scaleFactor * scale + 0.5f);
				LayoutParams params = new LayoutParams(pixels, pixels);
				params.setMargins(5, 5, 5, 5);
				holderMain.art_img.setLayoutParams(params);
				holderMain.art_img.setPadding(5, 5, 5, 5);

				ImageLoader imageLoader=UniversalImageLoader.get(act);
				imageLoader.displayImage(p.img_art, holderMain.art_img);
				//end of ART_IMG

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
				int pixelsForIcons = (int) (35 * scaleFactor * scale + 0.5f);
				LayoutParams paramsForIcons = new LayoutParams(pixelsForIcons, pixelsForIcons);
				paramsForIcons.setMargins(5, 5, 5, 5);
				paramsForIcons.gravity = Gravity.CENTER;

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
				holderMain.read.setScaleType(ScaleType.FIT_XY);
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

				//comments btn
				holderMain.comms.setScaleType(ScaleType.FIT_XY);
				holderMain.comms.setLayoutParams(paramsForIcons);

				if (pref.getString("theme", "dark").equals("dark"))
				{
					holderMain.comms.setImageResource(R.drawable.ic_comment_white_48dp);
				}
				else
				{
					holderMain.comms.setImageResource(R.drawable.ic_comment_grey600_48dp);
				}

				holderMain.comms.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						ArtsListAdapter.showComments(p, position, act);
					}
				});
				holderMain.num_of_comms.setText(String.valueOf(p.numOfComments));
				holderMain.num_of_comms.setTextSize(21 * scaleFactor);
				holderMain.num_of_comms.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						ArtsListAdapter.showComments(p, position, act);
					}
				});
				////end of comments btn
				//share btn
				holderMain.share.setScaleType(ScaleType.FIT_XY);
				holderMain.share.setLayoutParams(paramsForIcons);

				if (pref.getString("theme", "dark").equals("dark"))
				{
					holderMain.share.setImageResource(R.drawable.ic_share_white_48dp);
				}
				else
				{
					holderMain.share.setImageResource(R.drawable.ic_share_grey600_48dp);
				}

				holderMain.share.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						ArtsListAdapter.shareUrl(p.url, act);
					}
				});

				holderMain.num_of_shares.setText(String.valueOf(p.numOfSharings));
				holderMain.num_of_shares.setTextSize(21 * scaleFactor);
				holderMain.num_of_shares.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						ArtsListAdapter.shareUrl(p.url, act);
					}
				});
				////end of share btn

				//name of author
				if (!p.authorName.equals("default"))
				{
					Spanned spannedContent = Html.fromHtml("<b>" + p.authorName + "</b>");
					holderMain.author_name.setText(spannedContent);

				}
				else if (p.authorName.equals("default"))
				{
					holderMain.author_name.setText(null);
				}

				//				LayoutParams layParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
				//				layParams.setMargins(5, 5, 5, 5);
				//				holderMain.author_name.setLayoutParams(layParams);
				//				holderMain.author_name.setGravity(Gravity.CENTER_VERTICAL);

				holderMain.author_name.setTextSize(21 * scaleFactor);

				holderMain.author_name.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						ArtsListAdapter.showAllAuthorsArticles(p, act);
					}
				});
				//end of name of author

				//Date
				holderMain.date.setText(p.pubDate);
				holderMain.author_name.setTextSize(21 * scaleFactor);
				////End of Date

				//				if (p.authorBlogUrl.equals("allAuthors"))
				//				{
				////					System.out.println("allAutors removing save read imgs");
				//					LayoutParams paramsForIconsEmpty = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				//					holderMain.read.setImageDrawable(null);
				//					holderMain.read.setLayoutParams(paramsForIconsEmpty);
				//					holderMain.save.setImageDrawable(null);
				//					holderMain.save.setLayoutParams(paramsForIconsEmpty);
				//				}
				return view;
			default:
				return view;

		}
	}

	public static void showAllAuthorsArticles(ArtInfo p, ActionBarActivity act)
	{		
		if (!p.authorBlogUrl.equals("empty") && !p.authorBlogUrl.equals(""))
		{
			Toast.makeText(act, "show all AuthorsArticles!", Toast.LENGTH_SHORT).show();
		}
		else
		{
			System.out.println("p.authorBlogUrl.equals('empty') (|| ''): WTF?!");
		}
	}

	@Override
	public int getItemViewType(int position)
	{
		return 0;
	}

	@Override
	public int getViewTypeCount()
	{
		return 1;
	}

	public static void markAsRead(String url, Context ctx)
	{
		Toast.makeText(ctx, "readed!", Toast.LENGTH_SHORT).show();
	}

	public static void shareUrl(String url, Context ctx)
	{
		Toast.makeText(ctx, "share!", Toast.LENGTH_SHORT).show();
	}

	public static void showComments(ArtInfo artInfo, int position, ActionBarActivity act)
	{
		Toast.makeText(act, "comments!", Toast.LENGTH_SHORT).show();

		//check if it's large screen
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
		boolean twoPane = pref.getBoolean("twoPane", false);

		System.out.println("twoPane: " + twoPane);
		if (twoPane)
		{
			//light clicked card
			ArticlesListFragment artsListFrag = (ArticlesListFragment) act
			.getSupportFragmentManager().findFragmentById(R.id.articles_list);
			artsListFrag.setActivatedPosition(position);

			ArticleFragment artFrag = (ArticleFragment) act.getSupportFragmentManager().findFragmentById(R.id.article);

			FragmentTransaction transaction = act.getSupportFragmentManager().beginTransaction();

			if (!artFrag.isHidden())
			{
				transaction.addToBackStack(null);
				transaction.hide(artFrag);
			}
			//check if there is commFrag
			CommentsFragment commFrag = (CommentsFragment) act.getSupportFragmentManager().findFragmentById(
			R.id.article_comments_container);
			if (commFrag != null)
			{
				System.out.println("commFrag!=null");
				transaction.addToBackStack(null);
				transaction.hide(commFrag);
			}
			else
			{
				System.out.println("commFrag=null");
				//do nothing
			}
			transaction.add(R.id.article_comments_container, commFrag);

			//            System.out.println("shownId: "+shownId+"/ count: "+fr.getChildCount());
			// Commit the transaction
			transaction.commit();

		}
		else
		{
			Intent intent = new Intent(act, ActivityComments.class);
			intent.putExtra("curArtInfo", artInfo.getArtInfoAsStringArray());
			intent.putExtra("position", position);
			act.startActivity(intent);
		}
	}

	protected static void showArticle(ArtInfo artInfo, int position, ActionBarActivity act)
	{
		Toast.makeText(act, "showArticle!", Toast.LENGTH_SHORT).show();

		//fill CUR_ART_INFO var 
//		ActivityMain.setCUR_ART_INFO(artInfo);
		((ActivityMain)act).setCUR_ART_INFO(artInfo);

		//check if it's large screen
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
		boolean twoPane = pref.getBoolean("twoPane", false);
		if (twoPane)
		{
			//light clicked card
			ArticlesListFragment artsListFrag = (ArticlesListFragment) act
			.getSupportFragmentManager().findFragmentById(R.id.articles_list);
			artsListFrag.setActivatedPosition(position);
			
			
		}
		else
		{
			Intent intent = new Intent(act, ActivityArticle.class);
			intent.putExtra("curArtInfo", artInfo.getArtInfoAsStringArray());
			intent.putExtra("position", position);
//			ArrayList<ArtInfo> allArtsInfo=ActivityMain.getAllArtsInfo();
			ArrayList<ArtInfo> allArtsInfo=((ActivityMain)act).getAllArtsInfo();
			if (allArtsInfo != null)
			{
				for (int i = 0; i < allArtsInfo.size(); i++)
				{
					if (i < 10)
					{
						intent.getExtras().putStringArray("allArtsInfo_0" + String.valueOf(i),
						allArtsInfo.get(i).getArtInfoAsStringArray());
					}
					else
					{
						intent.getExtras().putStringArray("allArtsInfo_" + String.valueOf(i),
						allArtsInfo.get(i).getArtInfoAsStringArray());
					}
				}
			}
			else
			{
				System.out.println("ActivityArticle: onSaveInstanceState. this.allArtsInfo=null");
			}
			act.startActivity(intent);
		}
	}

	static class ArticleHolder
	{
		TextView title;
		TextView author_name;
		ImageView art_img;
		//		CardView card_img;
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

		ArticleHolder(TextView title, TextView author, /* CardView card_img, */ImageView img,
		ImageView save, ImageView read, ImageView comms, ImageView share, TextView num_of_comms,
		TextView num_of_shares, TextView date, TextView preview, ImageView settings,
		ViewGroup top_lin_lay)
		{
			this.title = title;
			this.author_name = author;
			this.art_img = img;
			//			this.card_img=card_img;
			this.save = save;
			this.read = read;
			this.comms = comms;
			this.share = share;
			this.num_of_comms = num_of_comms;
			this.num_of_shares = num_of_shares;
			this.date = date;
			this.preview = preview;
			this.settings = settings;
			this.top_lin_lay = top_lin_lay;
		}
	}

	//	public static Fragment getTopVisibleFragment(ViewGroup container, ActionBarActivity act)
	//	{
	//		Fragment frag=null;
	//		int fragStackLenght;
	//		int topFragId;
	//		
	//		fragStackLenght=act.getSupportFragmentManager().getBackStackEntryCount();
	//		System.out.println("fragStackLenght: "+fragStackLenght);
	//		fragStackLenght=container.getChildCount();
	//		System.out.println("fragStackLenght: "+fragStackLenght);
	//		
	//		topFragId=container.getChildAt(container.getChildCount()-1).getId();
	//		
	//		frag=act.getSupportFragmentManager().findFragmentById(topFragId);
	//		
	//		
	//		return frag;
	//	}
}