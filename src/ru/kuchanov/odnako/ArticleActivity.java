//test comment on github

//test comment to github from eclipse
package ru.kuchanov.odnako;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.ads.AdView;

import ru.kuchanov.odnako.utils.AddAds;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import ru.kuchanov.odnako.FillMenuList;

@SuppressWarnings("deprecation")
public class ArticleActivity extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener, OnUtteranceCompletedListener
{
	ActionBarActivity act;
	Context ctx;
	
	public final static String EXTRA_MESSAGE = "extra_message";
	public static String EXTRA_MESSAGE_TO_MAIN = "extra_message_to_main";

	final int MENU_SPEECH_TEXT = 0;
	final int MENU_SHARE_TEXT = 1;
	final int MENU_SHOW_COMMENTS = 2;
	final int MENU_OPEN_IN_BROWSER = 3;

	public static String NAG_DIALOG_IMG_SOURSE = null;
	public static String NAG_DIALOG_IMG_SOURSE_URL = null;

	public TextView tV;
	public TextView artTitle;
	public TextView artAuthor;
	public TextView artAuthorDescription;;

	ImageView artAuthorDescriptionBtn;

	public static String ART_TEXT;
	String title = null;
	public static String[] artInfo;
	public static String[] ART_AUTHOR_INFO;
	ProgressDialog pd;
	ParseArticle parse = new ParseArticle();

	Boolean fromBrowser = false;

	private DrawerLayout mDrawerLayout;
	LinearLayout layout;
	private ExpandableListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	ArrayList<ArrayList<String>> groups = new ArrayList<ArrayList<String>>();
	ArrayList<String> children1 = new ArrayList<String>();
	ArrayList<String> children2 = new ArrayList<String>();
	ArrayList<String> children3 = new ArrayList<String>();
	ArrayList<String> children4 = new ArrayList<String>();

	ArrayList<ArrayList<String>> groupsLinks = new ArrayList<ArrayList<String>>();
	ArrayList<String> children1Links = new ArrayList<String>();
	ArrayList<String> children2Links = new ArrayList<String>();

	SharedPreferences pref;

	ScrollView scroll;

	///test speech
	TextToSpeech ttobj;
	int ttsIntForReadedStrings = 0;
	public int indexOfFirstPoint = 0;

	
	AdView adView;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("Article onCreate");
		super.onCreate(savedInstanceState);
		
		this.act=this;
		this.ctx=act;

		//check if imgDialog was opened and if so recreate it
		if (ArticleActivity.NAG_DIALOG_IMG_SOURSE != null)
		{
			final Dialog nagDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
			nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			nagDialog.setCancelable(true);
			nagDialog.setContentView(R.layout.preview_image);
			WebView web = (WebView) nagDialog.findViewById(R.id.web_preview_image);
			WebSettings zoomenable = web.getSettings();
			zoomenable.setBuiltInZoomControls(true);
			if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			{
				makeZoomable(web);
			}
			File blogsPageFile = new File(ArticleActivity.NAG_DIALOG_IMG_SOURSE);
			System.out.println(ArticleActivity.NAG_DIALOG_IMG_SOURSE);
			if (blogsPageFile.exists())
			{
				web.loadUrl("file:///" + blogsPageFile.getAbsolutePath());
			}
			else
			{
				web.loadUrl(ArticleActivity.NAG_DIALOG_IMG_SOURSE_URL);
				System.out.println("NoImageFile while restore dialog on orientation change");
			}
			nagDialog.show();
			nagDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				@Override
				public void onCancel(DialogInterface dialog)
				{
					ArticleActivity.NAG_DIALOG_IMG_SOURSE = null;
					ArticleActivity.NAG_DIALOG_IMG_SOURSE = null;
				}
			});
		}
		//end

		if (ArticleActivity.EXTRA_MESSAGE_TO_MAIN == null)
		{
			ArticleActivity.EXTRA_MESSAGE_TO_MAIN = "extra_message_to_main";
		}

		//Çàäà¸ò äåôîëò íàñòðîéêè èç xml
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		if (pref.getString("theme", "dark").equals("dark"))
		{
			this.setTheme(R.style.Theme_AppCompat_Light_DarkActionBar);
			this.setContentView(R.layout.article_activity_dark);
		}
		else
		{
			this.setTheme(R.style.Theme_AppCompat_Light);
			this.setContentView(R.layout.article_activity_ligth);
		}
		//end
		layout = (LinearLayout) findViewById(R.id.art_lin);
		scroll = (ScrollView) findViewById(R.id.art_scroll);
		artTitle = (TextView) findViewById(R.id.art_title);
		artAuthor = (TextView) findViewById(R.id.art_author);
		artAuthorDescription = (TextView) findViewById(R.id.art_author_description);
		artAuthorDescriptionBtn = (ImageView) this.findViewById(R.id.art_author_description_btn);
		tV = (TextView) findViewById(R.id.art_text);
		registerForContextMenu(tV);

		if (savedInstanceState == null)
		{
			Intent intent = getIntent();
			String[] artInfoNew = intent.getStringArrayExtra(MainActivityNew.EXTRA_MESSAGE);
			if (ArticleActivity.artInfo != null && artInfoNew != null)
			{
				if (!ArticleActivity.artInfo[0].equals(artInfoNew[0]))
				{
					ART_TEXT = null;
				}
			}
			else
			{
				Uri data = getIntent().getData();
				if (data != null)
				{
					String scheme = data.getScheme(); // "http"
					String host = data.getHost(); // "twitter.com"
					List<String> params = data.getPathSegments();
					String first = null;
					String second = null;
					if (params.size() > 0)
					{
						first = params.get(0); // "blogs"
						//try to avoid crashing on blogs page loading from browser
						if (params.size() > 1)
						{
							System.out.println("More than 1 parametr in intent URI");
							second = params.get(1); // "title"
							if (ArticleActivity.artInfo != null)
							{
								if (!ArticleActivity.artInfo[0].equals(scheme + "://" + host + "/" + first + "/" + second + "/"))
								{
									ArticleActivity.ART_TEXT = null;
									ArticleActivity.artInfo = new String[3];
									ArticleActivity.artInfo[0] = scheme + "://" + host + "/" + first + "/" + second + "/";
									ArticleActivity.artInfo[1] = "title from browser";
									ArticleActivity.artInfo[2] = "author from browser";
								}
							}
						}

						fromBrowser = true;
					}
				}
			}
		}
		LockOrientation lock = new LockOrientation(this);
		lock.lock();
		/////////TEST NAVDRAW

		//Çàäà¸ò äåôîëò íàñòðîéêè èç xml è âåøàåò ëèñòåíåð èõ èçìåíåíèÿ
		PreferenceManager.setDefaultValues(this, R.xml.pref, false);
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
		//Çàäà¸ò äåôîëò íàñòðîéêè èç xmlè âåøàåò ëèñòåíåð èõ èçìåíåíèÿ

		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		// Íàõîäèì íàø list 
		mDrawerList = (ExpandableListView) findViewById(R.id.exListView);
		mDrawerList.setCacheColorHint(0);

		//Ñîçäàåì íàáîð äàííûõ äëÿ àäàïòåðà
		FillMenuList fillMenuList = new FillMenuList();
		fillMenuList.setActivity(this);
		this.groups = fillMenuList.getGroups();
		this.groupsLinks = fillMenuList.getGroupsLinks();
		//Ñîçäàåì àäàïòåð è ïåðåäàåì context è ñïèñîê ñ äàííûìè
		ExpListAdapter adapter = new ExpListAdapter(getApplicationContext(), groups);
		mDrawerList.setAdapter(adapter);
		//Set on child click listener
		DrawerItemClickListenerNew drawerItemClickListenerNew = new DrawerItemClickListenerNew();
		drawerItemClickListenerNew.setVars(mDrawerLayout, mDrawerList, groups, groupsLinks, this);
		this.mDrawerList.setOnChildClickListener(drawerItemClickListenerNew);
		//Set on group click listener
		DrawerGroupClickListenerNew drawerGroupClickListenerNew = new DrawerGroupClickListenerNew();
		drawerGroupClickListenerNew.setVars(mDrawerLayout, mDrawerList, this);
		this.mDrawerList.setOnGroupClickListener(drawerGroupClickListenerNew);
		//Expand category group
		mDrawerList.expandGroup(1);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close)
		{
			@Override
			public void onDrawerClosed(View view)
			{
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView)
			{
				getSupportActionBar().setTitle(mDrawerTitle);
				supportInvalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerToggle.setDrawerIndicatorEnabled(false);
		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		pref = PreferenceManager.getDefaultSharedPreferences(this);
		String scaleFactorString = pref.getString("scale_art", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);
		tV.setTextSize(21 * scaleFactor);
		artTitle.setTextSize(25 * scaleFactor);
		artAuthor.setTextSize(21 * scaleFactor);
		artAuthorDescription.setTextSize(21 * scaleFactor);

		//adMob
		adView = (AdView) this.findViewById(R.id.adView);
		AddAds addAds=new AddAds(this, this.adView);
//		addAds.addAd("adsOnArticle"); old ver
		addAds.addAd();
		//end of adMob
	}

	//onCreate
	protected void onResume()
	{
		System.out.println("Article onResume");
		super.onResume();
		//check if there is art's text and if so write art's URL to pref, to show read/unread icon
		
		////end check if there is art's text and if so write art's URL to pref, to show read/unread icon
		Intent intent = getIntent();
		artInfo = intent.getStringArrayExtra(MainActivityNew.EXTRA_MESSAGE);
		if (intent.getStringArrayExtra(MainActivityNew.EXTRA_MESSAGE) == null)
		{
			if (intent.getData() != null)
			{
				System.out.println("Article onResume else if(intent.getData()!=null) 111111");
				Uri data = getIntent().getData();
				String scheme = data.getScheme(); // "http"
				String host = data.getHost(); // "twitter.com"
				List<String> params = data.getPathSegments();
				String first = null;
				String second = null;
				if (params.size() > 0)
				{
					first = params.get(0); // "blogs"
					second = params.get(1); // "title"
					if (parse.title != null)
					{
						title = parse.title;
						System.out.println(parse.title);
					}

					ArticleActivity.artInfo = new String[3];
					ArticleActivity.artInfo[0] = scheme + "://" + host + "/" + first + "/" + second + "/";
					ArticleActivity.artInfo[1] = title;
					ArticleActivity.artInfo[2] = "author";
				}
			}
			else
			{
				artInfo = new String[3];
				artInfo = MainActivityNew.CUR_ART_INFO;
				System.out.println("Article onResume if (intent.getStringArrayExtra(MainActivity.EXTRA_MESSAGE) == null) ");
			}

		}
		else
		{
			artInfo = intent.getStringArrayExtra(MainActivityNew.EXTRA_MESSAGE);
			this.getSupportActionBar().setTitle(ArticleActivity.artInfo[1]);
		}
		////set Art Info Block
		if (ArticleActivity.artInfo != null)
		{
			if (ArticleActivity.artInfo[1] != null)
			{
				this.getSupportActionBar().setTitle(ArticleActivity.artInfo[1]);
				this.artTitle.setText(ArticleActivity.artInfo[1]);
			}
		}
		////set Art Info Block
		this.setArticleInfoBlock();

		if (ART_TEXT == null)
		{
			if (parse.getResult() != null)
			{
				ART_TEXT = parse.getResult();
				this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
				setTitle(ArticleActivity.artInfo[1]);
				new ru.kuchanov.odnako.utils.SetTextViewHTML(this).SetText(tV, ART_TEXT);
				tV.setLinksClickable(true);
				tV.setMovementMethod(LinkMovementMethod.getInstance());
				tV.invalidate();
				this.getSupportActionBar().setTitle(ArticleActivity.artInfo[1]);
				artTitle.setText(ArticleActivity.artInfo[1]);
				artTitle.setTypeface(null, Typeface.BOLD);
				////set Art Info Block
				this.setArticleInfoBlock();
			}
			else
			{
				this.getSupportActionBar().setTitle(ArticleActivity.artInfo[1]);

				////////////TEST LOAD FROM FILE
				String appDir;
				appDir = pref.getString("filesDir", "");
				String formatedCategory;
				formatedCategory = MainActivityNew.CATEGORY_TO_LOAD.replace("-", "_");
				formatedCategory = formatedCategory.replace("/", "_");
				formatedCategory = formatedCategory.replace(":", "_");
				formatedCategory = formatedCategory.replace(".", "_");

				String formatedLink;
				formatedLink = ArticleActivity.artInfo[0].replace("-", "_");
				formatedLink = formatedLink.replace("/", "_");
				formatedLink = formatedLink.replace(":", "_");
				formatedLink = formatedLink.replace(".", "_");

				File currentArticleFile = new File(appDir + "/" + formatedCategory + "/" + formatedLink);
				System.out.println("Try load from file: " + currentArticleFile.getAbsolutePath());
				if (currentArticleFile.exists())
				{
					System.out.println("Try load from file");
					parse = new ParseArticle();
					parse.setVars(ART_TEXT, this, tV);
					parse.setFilePath(true, currentArticleFile.getAbsolutePath());
					parse.execute();
				}
				else
				{
					System.out.println("Try load from file FAILED (FILE NOT FOUND), try load from web");
					parse = new ParseArticle();
					parse.setVars(ART_TEXT, this, tV);
					parse.execute();
				}
			}
		}
		else
		{
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			this.getSupportActionBar().setTitle(ArticleActivity.artInfo[1]);
			new ru.kuchanov.odnako.utils.SetTextViewHTML(this).SetText(tV, ART_TEXT);
			tV.setLinksClickable(true);
			tV.setMovementMethod(LinkMovementMethod.getInstance());
			tV.invalidate();
			artTitle.setText(ArticleActivity.artInfo[1]);
			artTitle.setTypeface(null, Typeface.BOLD);
			////set Art Info Block
			this.setArticleInfoBlock();
		}
	}

	//Launch new activity
	protected void showComments()
	{
		try
		{
			Intent intent = new Intent(this, CommentsActivityNew.class);
			intent.putExtra(ArticleActivity.EXTRA_MESSAGE, ArticleActivity.artInfo[0]);
			startActivity(intent);
		} catch (Exception e)
		{
			Toast.makeText(this, "try showComments error" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	//Launch new activity

	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		System.out.println("Article: onSaveInstanceState");
		if (ART_TEXT == null)
		{
			ART_TEXT = parse.getResult();
			outState.putString("ART_TEXT", ART_TEXT);
		}
		else
		{
			outState.putString("ART_TEXT", ART_TEXT);
		}
		if (this.title == null)
		{
			outState.putString("title", parse.title);
		}
		else
		{
			outState.putString("title", this.title);
		}

		outState.putString("artLink", artInfo[0]);
		outState.putString("artTitle", artInfo[1]);
		outState.putString("artAuthor", artInfo[2]);

		outState.putBoolean("fromBrowser", this.fromBrowser);

		outState.putIntArray("ARTICLE_SCROLL_POSITION", new int[] { scroll.getScrollX(), scroll.getScrollY() });
	}

	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		System.out.println("ArticleActivity onRestoreInstanceState");

		ArticleActivity.ART_TEXT = savedInstanceState.getString("ART_TEXT");
		artInfo = new String[3];
		artInfo[0] = savedInstanceState.getString("artLink");
		artInfo[1] = savedInstanceState.getString("artTitle");
		artInfo[2] = savedInstanceState.getString("artAuthor");

		this.fromBrowser = savedInstanceState.getBoolean("fromBrowser");

		this.title = savedInstanceState.getString("title");

		final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
		if (position != null)
		{
			scroll.post(new Runnable()
			{
				@Override
				public void run()
				{
					scroll.scrollTo(position[0], position[1]);
				}
			});
		}
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		SharedPreferences prefForScale = PreferenceManager.getDefaultSharedPreferences(this);
		switch (item.getItemId())
		{
			case android.R.id.home:
				System.out.println("fromBrowser: " + fromBrowser);
				if (fromBrowser != null)
				{
					if (fromBrowser)
					{
						this.fromBrowser = false;
						ArticleActivity.ART_TEXT = null;
						return super.onOptionsItemSelected(item);
					}
					else
					{
						onBackPressed();
						return true;
					}
				}
				else
				{
					onBackPressed();
					return true;
				}
			case R.id.comments:
				showComments();
				return true;
			case R.id.action_settings:
				item.setIntent(new Intent(this, PrefActivity.class));
				return super.onOptionsItemSelected(item);
			case R.id.menu_item_share:
				final Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, ArticleActivity.artInfo[0]);
				try
				{
					startActivity(Intent.createChooser(intent, "Ïîäåëèòüñÿ ñòàòü¸é"));
				} catch (android.content.ActivityNotFoundException ex)
				{
					Toast.makeText(getApplicationContext(), "Îøèáêà! =(", Toast.LENGTH_SHORT).show();
				}
				return true;
			case R.id.theme:
				MenuItem ligthThemeMenuItem = item.getSubMenu().findItem(R.id.theme_ligth);
				MenuItem darkThemeMenuItem = item.getSubMenu().findItem(R.id.theme_dark);
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
				String curTheme = pref.getString("theme", "ligth");
				System.out.println(curTheme);
				if (curTheme.equals("ligth"))
				{
					ligthThemeMenuItem.setChecked(true);
				}
				else
				{
					darkThemeMenuItem.setChecked(true);
				}
				return true;
			case R.id.theme_ligth:
				SharedPreferences prefLigth = PreferenceManager.getDefaultSharedPreferences(this);
				prefLigth.edit().putString("theme", "ligth").commit();
				this.myRecreate();
				return true;
			case R.id.theme_dark:
				SharedPreferences prefDark = PreferenceManager.getDefaultSharedPreferences(this);
				prefDark.edit().putString("theme", "dark").commit();
				this.myRecreate();
				return true;
			case R.id.arts_list_size:
				MenuItem artsListItem = item.getSubMenu().findItem(R.id.artslist_05);
				MenuItem artsListaItem = item.getSubMenu().findItem(R.id.artslist_075);
				MenuItem artsListbItem = item.getSubMenu().findItem(R.id.artslist_1);
				MenuItem artsListcItem = item.getSubMenu().findItem(R.id.artslist_125);
				MenuItem artsListdItem = item.getSubMenu().findItem(R.id.artslist_15);
				MenuItem artsListeItem = item.getSubMenu().findItem(R.id.artslist_175);
				MenuItem artsListgItem = item.getSubMenu().findItem(R.id.artslist_2);

				SharedPreferences prefArtsList = PreferenceManager.getDefaultSharedPreferences(this);
				String curArtsListSize = prefArtsList.getString("scale_art", "1");
				System.out.println("curArtsListSize: " + curArtsListSize);
				if (curArtsListSize.equals("0.5"))
				{
					artsListItem.setChecked(true);
				}
				else if (curArtsListSize.equals("0.75"))
				{
					artsListaItem.setChecked(true);
				}
				else if (curArtsListSize.equals("1"))
				{
					artsListbItem.setChecked(true);
				}
				else if (curArtsListSize.equals("1.25"))
				{
					artsListcItem.setChecked(true);
				}
				else if (curArtsListSize.equals("1.5"))
				{
					artsListdItem.setChecked(true);
				}
				else if (curArtsListSize.equals("1.75"))
				{
					artsListeItem.setChecked(true);
				}
				else if (curArtsListSize.equals("2"))
				{
					artsListgItem.setChecked(true);
				}
				return true;
			case R.id.artslist_05:
				prefForScale.edit().putString("scale_art", "0.5").commit();
				return true;
			case R.id.artslist_075:
				prefForScale.edit().putString("scale_art", "0.75").commit();
				return true;
			case R.id.artslist_1:
				prefForScale.edit().putString("scale_art", "1").commit();
				return true;
			case R.id.artslist_125:
				prefForScale.edit().putString("scale_art", "1.25").commit();
				return true;
			case R.id.artslist_15:
				prefForScale.edit().putString("scale_art", "1.5").commit();
				return true;
			case R.id.artslist_175:
				prefForScale.edit().putString("scale_art", "1.75").commit();
				return true;
			case R.id.artslist_2:
				prefForScale.edit().putString("scale_art", "2").commit();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@SuppressLint("NewApi")
	protected void myRecreate()
	{
		if (android.os.Build.VERSION.SDK_INT >= 11)
		{
			super.recreate();
		}
		else
		{
			finish();
			startActivity(getIntent());
		}
	}

	/* Called whenever we call supportInvalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		menu.findItem(R.id.comments).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		//Çàäà¸ò äåôîëò íàñòðîéêè èç xml è âåøàåò ëèñòåíåð èõ èçìåíåíèÿ
		SharedPreferences pref;
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		if (pref.getString("theme", "dark").equals("dark"))
		{
			getMenuInflater().inflate(R.menu.article_menu_dark, menu);
		}
		else
		{
			getMenuInflater().inflate(R.menu.article_menu_ligth, menu);
		}
		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		this.finish();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressWarnings({})
	@SuppressLint("NewApi")
	void makeZoomable(WebView web)
	{
		web.canZoomIn();
		web.canZoomOut();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		String scaleFactorString = pref.getString("scale_art", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);
		tV.setTextSize(21 * scaleFactor);
		artTitle.setTextSize(25 * scaleFactor);
		artAuthor.setTextSize(21 * scaleFactor);
		artAuthorDescription.setTextSize(21 * scaleFactor);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
//		SharedPreferences prefForTTS;
//		prefForTTS = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//		boolean isOn = prefForTTS.getBoolean("tts", false);

		switch (v.getId())
		{
			case R.id.art_text:
//				if (isOn)
//				{
					menu.add(0, this.MENU_SPEECH_TEXT, 0, "Ïðî÷èòàòü ñòàòüþ âñëóõ");
//				}
//				else
//				{
//					menu.removeItem(this.MENU_SPEECH_TEXT);
//				}
				menu.add(0, this.MENU_SHARE_TEXT, 0, "Ïîëó÷èòü òåñò ñòàòüè");
				menu.add(0, this.MENU_SHOW_COMMENTS, 0, "Êîììåíòàðèè");
				menu.add(0, this.MENU_OPEN_IN_BROWSER, 0, "Îòêðûòü â áðàóçåðå");
			break;
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case MENU_SPEECH_TEXT:
				/////test speech

				ttobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener()
				{
					@Override
					public void onInit(int status)
					{
						if (status != TextToSpeech.ERROR)
						{
							ArticleActivity.this.ttsIntForReadedStrings = 0;
							ttobj.setOnUtteranceCompletedListener(ArticleActivity.this);
							ttobj.setLanguage(Locale.getDefault());
							if (ttobj.isLanguageAvailable(new Locale("rus", "RUS")) < 0)
							{
								Toast.makeText(ArticleActivity.this, "Òðåáóåòñÿ ïîääåðæêà ðóññêîãî ÿçûêà. \n Ñì. íàñòðîéêè ñèíòåçà ðå÷è âàøåãî óñòðîéñòâà.", Toast.LENGTH_LONG).show();
								return;
							}
							ttobj.setLanguage(new Locale("rus", "RUS"));
							speakText();
						}
						else
						{
							Toast.makeText(ArticleActivity.this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
						}
					}
				});
			break;

			case MENU_SHARE_TEXT:
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				String textOfArt = this.artTitle.getText().toString() + "\n\n" + this.artAuthor.getText().toString() + "\n\n" + this.tV.getText().toString();
				sendIntent.putExtra(Intent.EXTRA_TEXT, textOfArt);
				sendIntent.setType("text/plain");
				startActivity(sendIntent);

			break;
			case MENU_SHOW_COMMENTS:
				this.showComments();
			break;
			case MENU_OPEN_IN_BROWSER:
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ArticleActivity.artInfo[0]));
				ArticleActivity.this.startActivity(browserIntent);
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onPause()
	{
		adView.pause();
		if (ttobj != null)
		{
			ttobj.stop();
			ttobj.shutdown();
		}
		super.onPause();
	}

	@Override
	public void onDestroy()
	{
		adView.destroy();
		super.onDestroy();
	}

	public void speakText()
	{
		String initialStringtoSpeak = this.artTitle.getText().toString() + ".   \n\n\n" + this.artAuthor.getText().toString() + ".   \n\n\n" + this.tV.getText().toString();
		String toSpeak = initialStringtoSpeak;
		if (ttsIntForReadedStrings != -1)
		{
			if (ttsIntForReadedStrings == 0)
			{
				toSpeak = toSpeak.substring(0, toSpeak.indexOf(".", 100));
				indexOfFirstPoint = initialStringtoSpeak.indexOf(".", 100);
				ArticleActivity.this.ttsIntForReadedStrings += 1;
				//System.out.println("indexOfFirstPoint: "+indexOfFirstPoint+"; ttsIntForReadedStrings: "+ttsIntForReadedStrings);
			}
			else
			{
				if (toSpeak.indexOf(".", 100 + indexOfFirstPoint) > toSpeak.length())
				{
					toSpeak = toSpeak.substring(indexOfFirstPoint, toSpeak.length() - 1);
					ttsIntForReadedStrings = -1;
				}
				else
				{
					try
					{
						toSpeak = toSpeak.substring(indexOfFirstPoint, toSpeak.indexOf(".", 100 + indexOfFirstPoint));
						indexOfFirstPoint = initialStringtoSpeak.indexOf(".", 100 + indexOfFirstPoint);
						ArticleActivity.this.ttsIntForReadedStrings += 1;
					} catch (Exception e)
					{
						toSpeak = toSpeak.substring(indexOfFirstPoint, toSpeak.length() - 1);
						ttsIntForReadedStrings = -1;
					}
					//System.out.println("indexOfFirstPoint: "+indexOfFirstPoint+"; ttsIntForReadedStrings: "+ttsIntForReadedStrings);
				}
			}
		}
		else
		{
			ttobj.stop();
			ttobj.shutdown();
			return;
		}
		HashMap<String, String> myHashRender = new HashMap<String, String>();
		myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
		ttobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, myHashRender);
	}

	@Override
	public void onUtteranceCompleted(String utteranceId)
	{
		System.out.println("onUtteranceCompleted");
		speakText();
	}

	protected void setArticleInfoBlock()
	{
		if (ArticleActivity.artInfo[2] != null)
		{
			if (!ArticleActivity.artInfo[2].equals("default"))
			{
				//this.artAuthor.setText(ArticleActivity.artInfo[2]);
				String autorsFieldText = null;
				autorsFieldText = ArticleActivity.artInfo[2];//.autorsInfoArr[2];
				if (ArticleActivity.ART_AUTHOR_INFO != null)
				{
					if (!ArticleActivity.ART_AUTHOR_INFO[4].equals("default"))
					{
						autorsFieldText += "\n" + ArticleActivity.ART_AUTHOR_INFO[4];
					}
				}
				this.artAuthor.setText(autorsFieldText);
			}
			else
			{
				this.artAuthor.setText(null);
				LayoutParams layParams = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 0);
				this.artAuthor.setLayoutParams(layParams);
			}
			//set clickable 
			if (ArticleActivity.ART_AUTHOR_INFO != null)
			{
				String theme = pref.getString("theme", "ligth");
				if (theme.equals("ligth"))
				{
					artAuthor.setTextColor(Color.BLACK);
				}
				else
				{
					artAuthor.setTextColor(Color.WHITE);
				}
				ImageView artAuthorAllArtsImg=(ImageView)act.findViewById(R.id.art_author_all_arts_btn);
				if (!ArticleActivity.ART_AUTHOR_INFO[3].equals("default"))
				{
					artAuthorAllArtsImg.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View arg0)
						{
							if (!ArticleActivity.ART_AUTHOR_INFO[3].equals("default"))
							{
								//Toast.makeText(act, autorsInfoArr[2], Toast.LENGTH_SHORT).show();
								Intent intentToMain = new Intent(ArticleActivity.this, MainActivityNew.class);
								String[] infoToMain = new String[2];
								String linkToAutorsBlog = ArticleActivity.ART_AUTHOR_INFO[3];
								if (linkToAutorsBlog.startsWith("http://"))
								{
									linkToAutorsBlog = linkToAutorsBlog.replace("http://", "");
								}
								infoToMain[0] = linkToAutorsBlog;
								infoToMain[1] = ArticleActivity.ART_AUTHOR_INFO[2];
								intentToMain.putExtra(ArticleActivity.EXTRA_MESSAGE_TO_MAIN, infoToMain);
								ArticleActivity.this.startActivity(intentToMain);
							}
						}
					});
					ParseArticle.setImgViewSize(ctx, artAuthorAllArtsImg);
				}
				else
				{
					artAuthorAllArtsImg.setImageResource(0);
					artAuthorAllArtsImg.setOnClickListener(null);
					LayoutParams params=new LayoutParams(0, 0);
					artAuthorAllArtsImg.setLayoutParams(params);
				}
			}
			//end of set clicable
		}
		//art author description
		this.artAuthorDescription = (TextView) findViewById(R.id.art_author_description);
		this.artAuthorDescriptionBtn = (ImageView) findViewById(R.id.art_author_description_btn);
		this.artAuthorDescription.setText(null);
		LayoutParams layParams = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 0);
		this.artAuthorDescription.setLayoutParams(layParams);
		if (ArticleActivity.ART_AUTHOR_INFO != null)
		{
			if (!ArticleActivity.ART_AUTHOR_INFO[5].equals("default"))
			{
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
				String scaleFactorString = pref.getString("scale_art", "1");
				float scaleFactor = Float.valueOf(scaleFactorString);
				artAuthorDescriptionBtn.setPadding(1, 1, 1, 1);
				final float scale = getResources().getDisplayMetrics().density;
				int pixels = (int) (75 * scaleFactor * scale + 0.5f);
				artAuthorDescriptionBtn.setScaleType(ScaleType.FIT_XY);
				LayoutParams params = new LayoutParams(pixels, pixels);
				artAuthorDescriptionBtn.setLayoutParams(params);
			}
			else
			{
				LayoutParams layParamsforDescrBtn = new LayoutParams(0, 0);
				artAuthorDescriptionBtn.setLayoutParams(layParamsforDescrBtn);
			}
		}

		artAuthorDescriptionBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ArticleActivity.this);
				pref = PreferenceManager.getDefaultSharedPreferences(ArticleActivity.this);
				String theme = pref.getString("theme", "ligth");
				if (ArticleActivity.ART_AUTHOR_INFO != null)
				{
					if (!ArticleActivity.ART_AUTHOR_INFO[5].equals("default"))
					{
						if (artAuthorDescription.getLayoutParams().height == 0)
						{
							LayoutParams layParams = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
							artAuthorDescription.setLayoutParams(layParams);
							String autorsFieldText = null;
							autorsFieldText = ArticleActivity.ART_AUTHOR_INFO[5];
							artAuthorDescription.setText(Html.fromHtml(autorsFieldText));
							if (theme.equals("ligth"))
							{
								v.setBackgroundResource(R.drawable.ic_action_collapse_ligth);
							}
							else
							{
								v.setBackgroundResource(R.drawable.ic_action_collapse_dark);
							}
						}
						else
						{
							artAuthorDescription.setText(null);
							LayoutParams layParams = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 0);
							artAuthorDescription.setLayoutParams(layParams);
							if (theme.equals("ligth"))
							{
								v.setBackgroundResource(R.drawable.ic_action_expand_ligth);
							}
							else
							{
								v.setBackgroundResource(R.drawable.ic_action_expand_dark);
							}
						}
					}
				}
			}
		});
		//end of art author description
		// ART_IMG
		ImageView artAuthorImage = (ImageView) this.findViewById(R.id.art_author_img);
		//ImageView artAuthorAllArtsImg=(ImageView)act.findViewById(R.id.art_author_all_arts_btn);
		if (ArticleActivity.ART_AUTHOR_INFO != null)
		{
			if (!ArticleActivity.ART_AUTHOR_INFO[1].equals("default"))
			{

				DownloadImageTask downFlag = new DownloadImageTask((ImageButton) null, (ImageView) artAuthorImage, (ActionBarActivity) this);
				downFlag.execute(ArticleActivity.ART_AUTHOR_INFO[1]);
				artAuthorImage.setPadding(10, 10, 10, 10);
				ParseArticle.setImgViewSize(ctx, artAuthorImage);
			}
			else
			{
				LayoutParams params = new LayoutParams(0, 0);
				artAuthorImage.setLayoutParams(params);
			}
		}
		//end of ART_IMG
	}

}
