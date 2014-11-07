package ru.kuchanov.odnako.lists_and_utils;

import java.io.File;
import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityArticle;
import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.activities.ActivityComments;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.fragments.ArticlesListFragment;
import ru.kuchanov.odnako.utils.ReadUnreadRegister;
import ru.kuchanov.odnako.utils.UniversalImageLoader;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class ArtsListRecyclerViewAdapter extends RecyclerView.Adapter<ArtsListRecyclerViewAdapter.ArticleHolder>
implements Filterable
{

	ActionBarActivity act;
	LayoutInflater lInflater;

	RecyclerView artsListView;

	ArrayList<ArtInfo> artsInfo;
	ArrayList<ArtInfo> orig;
	SharedPreferences pref;

	boolean twoPane;

	public ArtsListRecyclerViewAdapter(ActionBarActivity act, ArrayList<ArtInfo> artsInfo, RecyclerView artsListView)
	{
		this.act = act;
		this.artsInfo = artsInfo;
		lInflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		this.artsListView = artsListView;

		pref = PreferenceManager.getDefaultSharedPreferences(act);
		twoPane = pref.getBoolean("twoPane", false);
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

	public static CardView findShareLinearLayout(View view)
	{
		ViewParent parent = (ViewParent) view.getParent();
		if (parent != null)
		{
			if (parent instanceof CardView)
			{
				// Found it
				return (CardView) parent;

			}
			else if (parent instanceof View)
			{
				// Keep looking
				return findShareLinearLayout((View) parent);

			}
		}

		// Couldn't find it
		return null;
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

	//	@Override
	//	public int getViewTypeCount()
	//	{
	//		return 1;
	//	}

	public static void markAsRead(String url, Context ctx)
	{
		Toast.makeText(ctx, "readed!", Toast.LENGTH_SHORT).show();
	}

	public static void shareUrl(String url, Context ctx)
	{
		Toast.makeText(ctx, "share!", Toast.LENGTH_SHORT).show();
	}

	public static void showComments(ArrayList<ArtInfo> allArtsInfo, int position, ActionBarActivity act)
	{
		Toast.makeText(act, "comments!", Toast.LENGTH_SHORT).show();

		//light clicked card if we can find frag from @param act 
		if (act.getClass().getSimpleName().equals("ActivityMain"))
		{
			ArticlesListFragment artsListFrag = (ArticlesListFragment) act.getSupportFragmentManager()
			.findFragmentById(R.id.articles_list);
			artsListFrag.setActivatedPosition(position);
		}

		//check if it's large screen
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
		boolean twoPane = pref.getBoolean("twoPane", false);

		if (twoPane)
		{

			ViewPager pager = (ViewPager) act.findViewById(R.id.article_comments_container);
			if (pager.getAdapter().getClass().getSimpleName().equals(CommentsViewPagerAdapter.class.getSimpleName()))
			{
				pager.setCurrentItem(position, true);
			}
			//so it's comments adapter and need to switch to artAdapter
			else
			{
				PagerAdapter pagerAdapter = new CommentsViewPagerAdapter(act.getSupportFragmentManager(),
				allArtsInfo, CommentInfo.getDefaultAllArtsCommentsInfo(30, 10), act);
				pager.setAdapter(pagerAdapter);
				pager.setCurrentItem(position, true);
			}

		}
		else
		{
			Intent intent = new Intent(act, ActivityComments.class);
			intent.putExtra("curArtInfo", allArtsInfo.get(position).getArtInfoAsStringArray());
			intent.putExtra("position", position);
			for (int i = 0; i < allArtsInfo.size(); i++)
			{
				if (i < 10)
				{
					intent.putExtra("allArtsInfo_0" + String.valueOf(i),
					allArtsInfo.get(i).getArtInfoAsStringArray());
				}
				else
				{
					intent.putExtra("allArtsInfo_" + String.valueOf(i),
					allArtsInfo.get(i).getArtInfoAsStringArray());
				}
			}
			act.startActivity(intent);
		}
	}

	public static void showArticle(ArrayList<ArtInfo> allArtsInfo, int position, ActionBarActivity act)
	{
		Toast.makeText(act, "showArticle!", Toast.LENGTH_SHORT).show();

		//fill CUR_ART_INFO var 
		//		((ActivityMain) act).setCUR_ART_INFO(artInfo);

		//check if it's large screen
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
		boolean twoPane = pref.getBoolean("twoPane", false);
		if (twoPane)
		{
			ArticlesListFragment artsListFrag = (ArticlesListFragment) ((ActivityMain) act).getSupportFragmentManager()
			.findFragmentById(R.id.articles_list);
			artsListFrag.setActivatedPosition(position);
			ViewPager pager = (ViewPager) act.findViewById(R.id.article_comments_container);
			if (pager.getAdapter().getClass().getSimpleName().equals(ArticleViewPagerAdapter.class.getSimpleName()))
			{
				pager.setCurrentItem(position, true);
			}
			//so it's comments adapter and need to switch to artAdapter
			else
			{
				PagerAdapter pagerAdapter = new ArticleViewPagerAdapter(act.getSupportFragmentManager(),
				((ActivityMain) act).getAllArtsInfo(), act);
				pager.setAdapter(pagerAdapter);
				pager.setCurrentItem(position, true);
			}

		}
		else
		{
			Intent intent = new Intent(act, ActivityArticle.class);
			Bundle b = new Bundle();
			b.putInt("position", position);
			ArtInfo.writeAllArtsInfoToBundle(b, allArtsInfo, allArtsInfo.get(position));
			b.putIntArray("groupChildPosition", ((ActivityBase) act).getGroupChildPosition());
			intent.putExtras(b);

			act.startActivity(intent);

		}
	}

	static class ArticleHolder extends RecyclerView.ViewHolder
	{
		TextView title;
		TextView author_name;
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

		ArticleHolder(View itemLayoutView)
		{
			super(itemLayoutView);
			this.title = (TextView) itemLayoutView.findViewById(R.id.art_card_title_tv);

			this.author_name = (TextView) itemLayoutView.findViewById(R.id.author_name);
			this.art_img = (ImageView) itemLayoutView.findViewById(R.id.art_card_img);
			this.save = (ImageView) itemLayoutView.findViewById(R.id.save_img);
			this.read = (ImageView) itemLayoutView.findViewById(R.id.read_img);
			this.comms = (ImageView) itemLayoutView.findViewById(R.id.comments_img);
			this.share = (ImageView) itemLayoutView.findViewById(R.id.share_img);
			this.num_of_comms = (TextView) itemLayoutView.findViewById(R.id.num_of_comms);
			this.num_of_shares = (TextView) itemLayoutView.findViewById(R.id.num_of_sharings);
			this.date = (TextView) itemLayoutView.findViewById(R.id.art_card_date_tv);
			this.preview = (TextView) itemLayoutView.findViewById(R.id.art_card_preview_tv);
			this.settings = (ImageView) itemLayoutView.findViewById(R.id.art_card_settings);
			this.top_lin_lay = (ViewGroup) itemLayoutView.findViewById(R.id.art_card_top_lin_lay);
		}
	}

	@Override
	public int getItemCount()
	{
		// TODO Auto-generated method stub
		return this.artsInfo.size();
	}

	@Override
	public void onBindViewHolder(ArticleHolder holderMain, final int position)
	{
		final ArtInfo p = this.artsInfo.get(position);

		ImageLoader imageLoader = UniversalImageLoader.get(act);
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
								ArtsListRecyclerViewAdapter.markAsRead(p.url, act);
								return true;
							case R.id.share_link:
								ArtsListRecyclerViewAdapter.shareUrl(p.url, act);
								return true;
							case R.id.show_comments:
								ArtsListRecyclerViewAdapter.showComments(artsInfo, position, act);
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
//		ArticlesListFragment artsListFrag = (ArticlesListFragment) act
//		.getSupportFragmentManager().findFragmentById(R.id.articles_list);
//		if (this.artsListView.getChoiceMode() == ListView.CHOICE_MODE_SINGLE)
//		{
//			if (artsListFrag.getMyActivatedPosition() == position)
//			{
//				view.setBackgroundColor(act.getResources().getColor(R.color.blue));
//			}
//			else
//			{
//				view.setBackgroundColor(Color.TRANSPARENT);
//			}
//		}
//		else
//		{
//			view.setBackgroundColor(Color.TRANSPARENT);
//		}

		////////

		//Title of article
		Spanned spannedContentTitle = Html.fromHtml(p.title);
		holderMain.title.setText(spannedContentTitle);
		holderMain.top_lin_lay.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ArtsListRecyclerViewAdapter.showArticle(artsInfo, position, act);
			}
		});

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
				ArtsListRecyclerViewAdapter.showArticle(artsInfo, position, act);
			}
		});

		holderMain.preview.setTextSize(21 * scaleFactor);
		////end of preview

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

		holderMain.author_name.setTextSize(21 * scaleFactor);

		holderMain.author_name.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				ArtsListRecyclerViewAdapter.showAllAuthorsArticles(p, act);
			}
		});
		//end of name of author

		// ART_IMG
		final float scale = act.getResources().getDisplayMetrics().density;
		int pixels = (int) (75 * scaleFactor * scale + 0.5f);
		LayoutParams params = new LayoutParams(pixels, pixels);
		params.setMargins(5, 5, 5, 5);
		holderMain.art_img.setLayoutParams(params);
		holderMain.art_img.setPadding(5, 5, 5, 5);

		//				ImageLoader imageLoader = UniversalImageLoader.get(act);
		if (this.pref.getString("theme", "dark").equals("dark"))
		{
			imageLoader.displayImage(p.img_art, holderMain.art_img, UniversalImageLoader.getDarkOptions());
		}
		else
		{
			imageLoader.displayImage(p.img_art, holderMain.art_img);
		}
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

		LayoutParams paramsForIconsBottomGravity = new LayoutParams(pixelsForIcons, pixelsForIcons);
		paramsForIconsBottomGravity.setMargins(5, 5, 5, 5);
		paramsForIconsBottomGravity.gravity = Gravity.BOTTOM;

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

		//share btn
		holderMain.share.setLayoutParams(paramsForIconsBottomGravity);
		holderMain.share.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ArtsListRecyclerViewAdapter.shareUrl(p.url, act);
			}
		});
		holderMain.num_of_shares.setLayoutParams(paramsForIconsBottomGravity);
		holderMain.num_of_shares.setGravity(Gravity.BOTTOM);
		holderMain.num_of_shares.setText(String.valueOf(p.numOfSharings));
		holderMain.num_of_shares.setTextSize(21 * scaleFactor);
		holderMain.num_of_shares.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ArtsListRecyclerViewAdapter.shareUrl(p.url, act);
			}
		});
		////end of share btn

		//comments btn
		holderMain.comms.setLayoutParams(paramsForIconsBottomGravity);
		holderMain.comms.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ArtsListRecyclerViewAdapter.showComments(artsInfo, position, act);
			}
		});
		holderMain.num_of_comms.setLayoutParams(paramsForIconsBottomGravity);
		holderMain.num_of_comms.setGravity(Gravity.BOTTOM);
		holderMain.num_of_comms.setText(String.valueOf(p.numOfComments));
		holderMain.num_of_comms.setTextSize(21 * scaleFactor);
		holderMain.num_of_comms.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ArtsListRecyclerViewAdapter.showComments(artsInfo, position, act);
			}
		});
		//				set comm.y coord to share.y coord
		//				ViewGroup testVG=(ViewGroup)view.findViewById(R.id.art_card_save_share_lin);
		//				testVG.measure(0, 0);
		//				int newHeight=testVG.getMeasuredHeight();
		//				System.out.println("newHeight: "+newHeight);
		//								
		//				LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, newHeight);
		//				lp.gravity=Gravity.BOTTOM;
		//				LinearLayout.LayoutParams lp1=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		//				lp1.gravity=Gravity.BOTTOM;
		//				((ViewGroup)holderMain.comms.getParent().getParent()).setLayoutParams(lp);
		//				((ViewGroup)holderMain.comms.getParent()).setLayoutParams(lp1);

		final ViewGroup commReadLin = ((ViewGroup) holderMain.comms.getParent().getParent());
		commReadLin.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{

			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout()
			{
				// Check if your view has been laid out yet
				if (commReadLin.getHeight() > 0)
				{
					// If it has been, we will search the view hierarchy for the view that is responsible for the extra space. 
					CardView dialogLayout = findShareLinearLayout(commReadLin);
					if (dialogLayout == null)
					{
						// Could find it. Unexpected.

					}
					else
					{

						View child1 = dialogLayout.findViewById(R.id.art_card_save_share_lin);
						if (child1 != commReadLin)
						{
							// remove height
							LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child1.getLayoutParams();
							child1.measure(0, 0);
							lp.height = child1.getMeasuredHeight();
							lp.gravity = Gravity.BOTTOM;
							commReadLin.setLayoutParams(lp);
						}
						else
						{
							System.out.println("customView, Fuck");
						}
					}

					// Done with the listener
					commReadLin.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			}

		});
		////end of comments btn

		//Date
		holderMain.date.setText(p.pubDate);
		holderMain.author_name.setTextSize(21 * scaleFactor);
		////End of Date
	}

	@Override
	public ArtsListRecyclerViewAdapter.ArticleHolder onCreateViewHolder(ViewGroup parent, int position)
	{
		// create a new view
		View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.arts_list_card_view, parent, false);

		// create ViewHolder

		ArtsListRecyclerViewAdapter.ArticleHolder viewHolder = new ArtsListRecyclerViewAdapter.ArticleHolder(
		itemLayoutView);
		return viewHolder;
	}
}