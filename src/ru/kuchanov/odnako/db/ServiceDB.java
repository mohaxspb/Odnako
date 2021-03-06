/*

 07.12.2014
ServiceDB.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.activities.ActivityPreference;
import ru.kuchanov.odnako.callbacks.AllArtsInfoCallback;
import ru.kuchanov.odnako.callbacks.CallbackAskDBFromBottom;
import ru.kuchanov.odnako.callbacks.CallbackAskDBFromTop;
import ru.kuchanov.odnako.callbacks.CallbackGetDownloaded;
import ru.kuchanov.odnako.callbacks.CallbackWriteArticles;
import ru.kuchanov.odnako.callbacks.CallbackWriteFromBottom;
import ru.kuchanov.odnako.callbacks.CallbackWriteFromTop;
import ru.kuchanov.odnako.download.HtmlHelper;
import ru.kuchanov.odnako.download.ParsePageForAllArtsInfo;
import ru.kuchanov.odnako.fragments.FragmentArticle;
import ru.kuchanov.odnako.lists_and_utils.CatData;
import ru.kuchanov.odnako.utils.ServiceTTS;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.j256.ormlite.android.apptools.OpenHelperManager;

//tag:^(?!dalvikvm) tag:^(?!libEGL) tag:^(?!Open) tag:^(?!Google) tag:^(?!resour) tag:^(?!Chore) tag:^(?!EGL) tag:^(?!SocketStream)
public class ServiceDB extends Service implements AllArtsInfoCallback, CallbackAskDBFromTop, CallbackAskDBFromBottom,
CallbackWriteFromBottom, CallbackWriteFromTop, /* CallbackWriteArticles, */CallbackGetDownloaded
{
	final private static String LOG = ServiceDB.class.getSimpleName() + "/";

	public final static int NOTIFICATION_NEW_ARTICLES_ID = 1;

	private DataBaseHelper dataBaseHelper;

	private boolean notify = false;
	private String catToNotify;
	private int pageToNotify;

	Context ctx;
	SharedPreferences pref;

	public List<ParsePageForAllArtsInfo> currentTasks = new ArrayList<ParsePageForAllArtsInfo>();

	private int quontToDownload = 10;

	public void onCreate()
	{
		Log.d(LOG, "onCreate");
		super.onCreate();

		this.ctx = this;

		//get default settings to get all settings later
		PreferenceManager.setDefaultValues(this, R.xml.pref_design, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_notifications, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_system, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_about, true);
		this.pref = PreferenceManager.getDefaultSharedPreferences(ctx);

		this.getHelper();

		LocalBroadcastManager.getInstance(ctx).registerReceiver(receiverArticleLoaded,
		new IntentFilter(Const.Action.ARTICLE_CHANGED));
	}

	public int onStartCommand(Intent intent, int flags, int startId)
	{
		//Log.d(LOG, "onStartCommand");
		if (intent == null)
		{
			Log.e(LOG, "intent=null!!! WTF?!");
			return super.onStartCommand(intent, flags, startId);
		}
		int pageToLoad = intent.getIntExtra("pageToLoad", 1);
		String categoryToLoad = intent.getStringExtra("categoryToLoad");

		String action = intent.getAction();
		switch (action)
		{
			case Const.Action.DATA_REQUEST:
				boolean startDownload = intent.getBooleanExtra("startDownload", false);
				this.notify = intent.getBooleanExtra("notify", false);
				if (notify)
				{
					this.catToNotify = categoryToLoad;
					this.pageToNotify = pageToLoad;
				}

				if (pageToLoad == 1)
				{
					//if pageToLoad=1 we load from top
					//get timeStamp
					Long timeStamp = intent.getLongExtra("timeStamp", System.currentTimeMillis());
					Calendar cal = Calendar.getInstance(TimeZone.getDefault(), new Locale("ru"));
					cal.setTimeInMillis(timeStamp);

					//if there is flag to download we do not need to go to DB
					//we simply start download
					if (startDownload)
					{
						this.startDownLoad(categoryToLoad, pageToLoad);
					}
					else
					{
						AsyncTaskAskDBFromTop askFromTop = new AsyncTaskAskDBFromTop(this, dataBaseHelper,
						categoryToLoad, cal, pageToLoad, this);
						askFromTop.execute();
					}
				}
				else
				{
					//if pageToLoad!=1 we load from bottom
					//Log.d(LOG, "LOAD FROM BOTTOM!");
					AsyncTaskAskDBFromBottom askFromBottom = new AsyncTaskAskDBFromBottom(dataBaseHelper,
					categoryToLoad,
					pageToLoad, this);
					askFromBottom.execute();
				}
			break;
			case Const.Action.DATA_DOWNLOAD:
				this.quontToDownload = intent.getIntExtra("quont", 10);
				LocalBroadcastManager.getInstance(this).registerReceiver(artsDataReceiver,
				new IntentFilter(categoryToLoad));
				this.startDownLoad(categoryToLoad, 1);
			break;
			case Const.Action.GET_DOWNLOADED:
				AsyncTaskGetDownloaded getDownloaded = new AsyncTaskGetDownloaded(getHelper(), categoryToLoad,
				pageToLoad, this);
				getDownloaded.execute();
			break;
		}

		return super.onStartCommand(intent, flags, startId);
	}

	private BroadcastReceiver artsDataReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.e(LOG, "onReceive artsDataReceiver");
			LocalBroadcastManager.getInstance(ctx).unregisterReceiver(artsDataReceiver);
			//get result message
			String[] msg = intent.getStringArrayExtra(Msg.MSG);

			switch (msg[0])
			{
				case Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_HAVE_MATCH_TO_INITIAL:
				case Msg.DB_ANSWER_FROM_BOTTOM_INFO_SENDED_TO_FRAG:
				//CANT be as we load only 30;
				break;
				case Msg.DB_ANSWER_INFO_SENDED_TO_FRAG:
				case Msg.NO_NEW:
				case Msg.NEW_QUONT:
				case Msg.DB_ANSWER_WRITE_FROM_TOP_NO_MATCHES:
				case Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT:
					//start multiple load of articles
					Intent downloadArtsIntent = new Intent(ctx, ServiceArticle.class);
					downloadArtsIntent.setAction(Const.Action.DATA_REQUEST_MULTIPLE);
					ArrayList<String> urls = new ArrayList<String>();
					ArrayList<Article> dataFromWeb = intent.getParcelableArrayListExtra(Article.KEY_ALL_ART_INFO);
					for (int i = 0; i < quontToDownload; i++)
					{
						urls.add(dataFromWeb.get(i).getUrl());
					}
					downloadArtsIntent.putStringArrayListExtra(FragmentArticle.ARTICLE_URL, urls);
					downloadArtsIntent.putExtra("startDownload", true);
					ctx.startService(downloadArtsIntent);
				break;
				case (Msg.DB_ANSWER_WRITE_FROM_BOTTOM_EXCEPTION):
					//we catch publishing lag from bottom, so we'll toast unsinked status
					//and start download from top (pageToLoad=1)
					Log.d(LOG, "Синхронизирую базу данных. Загружаю новые статьи");
				break;
				case (Msg.DB_ANSWER_NO_ARTS_IN_CATEGORY):
					Log.e(LOG, "Ни одной статьи не обнаружено!");
				break;
				case (Msg.ERROR):
					Log.e(LOG, msg[1]);
				break;
				default:
					Log.e(LOG, "непредвиденный ответ базы данных");
				break;
			}
		}
	};

	@Override
	public void onGetDownloaded(ArrayList<Article> dataFromDB, String categoryToLoad, int pageToLoad)
	{
		String[] result = { Msg.DB_ANSWER_INFO_SENDED_TO_FRAG, null };
		if (dataFromDB != null && dataFromDB.size() != 0)
		{
			this.sendBroadcastWithResult(ctx, result, dataFromDB, categoryToLoad, pageToLoad);
		}
		else
		{
			result = new String[] { Msg.ERROR, "Сохранённых статей не обнаружено" };
			this.sendBroadcastWithResult(ctx, result, dataFromDB, categoryToLoad, pageToLoad);
		}
	}

	public static void doItMotherfucker(DataBaseHelper h, String categoryToLoad, int pageToLoad,
	AllArtsInfoCallback callback, String gainedHtml)
	{
		Log.d(LOG, "doItMotherfucker called");
		ArrayList<Article> parsedArticlesList = null;

		HtmlCleaner hc = new HtmlCleaner();
		TagNode rootNode = hc.clean(gainedHtml);
		HtmlHelper hh = new HtmlHelper(rootNode);

		if (hh.isAuthor())
		{
			parsedArticlesList = hh.getAllArtsInfoFromAUTHORPage();

			//write new Author to DB if it don't exists
			if (Category.isCategory(h, categoryToLoad) == null)
			{
				Author a = new Author(categoryToLoad, hh.getAuthorsName(), hh.getAuthorsDescription(),
				hh.getAuthorsWho(), hh.getAuthorsImage(), hh.getAuthorsBigImg(), new Date(
				System.currentTimeMillis()), new Date(0));
				try
				{
					h.getDaoAuthor().create(a);
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
		else
		{
			parsedArticlesList = hh.getAllArtsInfoFromPage();
			//write new Author if it don't exists
			if (Category.isCategory(h, categoryToLoad) == null)
			{
				Category c = hh.getCategoryFromHtml();
				try
				{
					h.getDaoCategory().create(c);
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
		if (parsedArticlesList != null)
		{
			callback.sendDownloadedData(parsedArticlesList, categoryToLoad, pageToLoad);
		}
		else
		{
			callback.onError(Const.Error.CONNECTION_ERROR, categoryToLoad, pageToLoad);
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void startDownLoad(final String catToLoad, final int pageToLoad)
	{
		//Log.d(LOG, "startDownLoad " + catToLoad + "/page-" + pageToLoad);

		//TODO check quontity and allAuthors situation
		//So, we can have MAX quint of tasks=3 (cur pager frag +left and right)
		//but in case of MenuPager on position=3 && twoPane mode we have 4 tasks -
		//next and prev categories and first 2 authors.
		//And in this case 1-st of these authors is last in queue
		//so we must catch this situation and sort list of tasks

		//THERE ARE UGLY BUG WITH "_" IN URL...
		//WE MUST CHECK IF OUR URL HAS IT
		//AND IF SO LOAD HTML VIA WEBVIEW 
		//AND THEN PARSE IT
		//AND WEBVIEW NEEDS UI THREAD
		if (catToLoad.contains("_"))
		{
			Log.e(LOG + catToLoad, "WARNING!!! '_' in domain!!!!");

			final WebView webView = new WebView(this);

			webView.getSettings().setJavaScriptEnabled(true);

			// intercept calls to console.log
			webView.setWebChromeClient(new WebChromeClient()
			{
				public boolean onConsoleMessage(ConsoleMessage cmsg)
				{
					// check secret prefix					
					if (cmsg.message().startsWith("MAGIC"))
					{
						String msg = cmsg.message().substring(5); // strip off prefix
						/* process HTML */
						doItMotherfucker(getHelper(), catToLoad, pageToLoad, ServiceDB.this, msg);
						return true;
					}
					return false;
				}
			});

			// inject the JavaScript on page load
			webView.setWebViewClient(new WebViewClient()
			{
				public void onPageFinished(WebView view, String address)
				{
					// have the page spill its guts, with a secret prefix
					//Log.e(LOG, "onPageFinished");
					view
					.loadUrl("javascript:console.log('MAGIC'+'<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
				}
			});
			String link;
			if (catToLoad.startsWith("http://"))
			{
				link = catToLoad + "/page-" + String.valueOf(pageToLoad) + "/";
			}
			else
			{
				link = "http://" + catToLoad + "/page-" + String.valueOf(pageToLoad) + "/";
			}
			//link="http://about_denpobedi114.odnako.org/page-1/";
			webView.loadUrl(link);
			return;
		}

		if (this.currentTasks.size() < 4)
		{
			//Everything is OK. Just add it and execute
			ParsePageForAllArtsInfo parse = new ParsePageForAllArtsInfo(catToLoad, pageToLoad, this,
			this.getHelper());
			parse.execute();
			this.currentTasks.add(parse);
		}
		else
		{
			//cancel 1-st and add given to the end
			ParsePageForAllArtsInfo removedParse = this.currentTasks.remove(0);
			//test fixing canceling already finished task
			if (removedParse.getStatus() == AsyncTask.Status.RUNNING)
			{
				Log.e(LOG, removedParse.getCategoryToLoad() + " :" + AsyncTask.Status.RUNNING.toString() + "/RUNNING");
				removedParse.cancel(true);
				this.onError("загрузка прервана", removedParse.getCategoryToLoad(), removedParse.getPageToLoad());
			}

			ParsePageForAllArtsInfo parseToAdd = new ParsePageForAllArtsInfo(catToLoad, pageToLoad, this,
			this.getHelper());
			parseToAdd.execute();

			this.currentTasks.add(parseToAdd);
		}
	}

	@Override
	public void sendDownloadedData(ArrayList<Article> dataToSend, String categoryToLoad, int pageToLoad)
	{
		Log.d(LOG + categoryToLoad, "sendDownloadedData");
		//find and remove finished task from list
		for (int i = 0; i < this.currentTasks.size(); i++)
		{
			if (categoryToLoad.equals(this.currentTasks.get(i).getCategoryToLoad())
			&& pageToLoad == this.currentTasks.get(i).getPageToLoad())
			{
				this.currentTasks.remove(i);
			}
		}

		if (dataToSend.size() == 0)
		{
			Article a = new Article();
			a.setTitle("Ни одной статьи не обнаружено.");
			dataToSend.add(a);
			String[] resultMessage = new String[] { Msg.DB_ANSWER_NO_ARTS_IN_CATEGORY, null };
			sendBroadcastWithResult(this, resultMessage, dataToSend, categoryToLoad, pageToLoad);
		}
		else
		{
			//here if we recive less then 30 arts (const quont of arts on page)
			//we KNOW that last of them is initial art in category (author)
			//so WRITE it to DB!
			//TODO transfer it to AsyncTask!
			if (dataToSend.size() < 30)
			{
				if (Category.isCategory(this.getHelper(), categoryToLoad))
				{
					int categoryId = Category.getCategoryIdByURL(getHelper(), categoryToLoad);
					String initialArtsUrl = dataToSend.get(dataToSend.size() - 1).getUrl();
					Category.setInitialArtsUrl(this.getHelper(), categoryId, initialArtsUrl);
				}
				else
				{
					int authorId = Author.getAuthorIdByURL(getHelper(), categoryToLoad);
					String initialArtsUrl = dataToSend.get(dataToSend.size() - 1).getUrl();
					Author.setInitialArtsUrl(this.getHelper(), authorId, initialArtsUrl);
				}
			}
			//here we'll write gained arts to Article table 
			AsyncTaskWriteArticlesToDB writeArticles = new AsyncTaskWriteArticlesToDB(getHelper(), dataToSend,
			callbackWriteArticles, categoryToLoad, pageToLoad);
			writeArticles.execute();
		}
	}

	@Override
	public void onError(String e, String categoryToLoad, int pageToLoad)
	{
		//find and remove finished task from list
		for (int i = 0; i < this.currentTasks.size(); i++)
		{
			if (categoryToLoad.equals(this.currentTasks.get(i).getCategoryToLoad())
			&& pageToLoad == this.currentTasks.get(i).getPageToLoad())
			{
				this.currentTasks.remove(i);
			}
		}

		String[] resultMessage = new String[] { Msg.ERROR, e };
		sendBroadcastWithResult(this, resultMessage, null, categoryToLoad, pageToLoad);
		//here if we loaded from top and get NO_CONNECTION ERROR we can ask DB for arts.
		//And toast if there are no arts in DB (cache);
		//So ask DB with calendar(time of last refresh);
		Calendar calOfLastRefresh = Calendar.getInstance();
		Boolean isCategory = Category.isCategory(getHelper(), categoryToLoad);
		if (isCategory == null)
		{
			resultMessage = new String[] { Msg.ERROR, "Категория в базе данных не обнаружена. И разработчик очень удивится, узнав, что вы читаете это сообщение" };
			sendBroadcastWithResult(this, resultMessage, null, categoryToLoad, pageToLoad);
			return;
		}
		if (Category.isCategory(getHelper(), categoryToLoad))
		{
			Category c = Category.getCategoryByURL(getHelper(), categoryToLoad);
			calOfLastRefresh.setTime(c.getRefreshed());
		}
		else
		{
			Author a = Author.getAuthorByURL(getHelper(), categoryToLoad);
			calOfLastRefresh.setTime(a.getRefreshed());
		}
		//if category was never refreshed it's=0, so Toast that there are no arts in cache
		if (calOfLastRefresh.getTimeInMillis() == 0)
		{
			resultMessage = new String[] { Msg.ERROR, "Статей в кэше не обнаружено" };
			sendBroadcastWithResult(this, resultMessage, null, categoryToLoad, pageToLoad);
			return;
		}
		//we can now try to get articles, storred in DB;
		if (pageToLoad == 1)
		{
			CallbackAskDBFromTop callback = new CallbackAskDBFromTop()
			{
				@Override
				public void onAnswerFromDBFromTop(String answer, String categoryToLoad, int pageToLoad,
				ArrayList<Article> dataToSend)
				{
					String[] resultMessage;
					Log.e(LOG, "ON_ERROR CALLBACK answer: " + answer);
					switch (answer)
					{
						case Msg.DB_ANSWER_NEVER_REFRESHED:
						//that's can't be, because we check it before
						break;
						case Msg.DB_ANSWER_REFRESH_BY_PERIOD:
						//that's can't be, because we give same time that we retrieve from category
						break;
						case Msg.DB_ANSWER_NO_ENTRY_OF_ARTS:
							//That's strange... We have non zero REFRESHED time and given Category record in DB,
							//but no arts... Anyway we tell about it;
							resultMessage = new String[] { Msg.ERROR, "Статей в кэше не обнаружено" };
							sendBroadcastWithResult(ctx, resultMessage, null, categoryToLoad, pageToLoad);
						//TODO check this
						break;
						case Msg.DB_ANSWER_UNKNOWN_CATEGORY:
						//that's can't be, because we check it before
						break;
						case Msg.DB_ANSWER_INFO_SENDED_TO_FRAG:
							//Everything is OK. We send arts from DB to fragment
							resultMessage = new String[] { /* Msg.ERROR */Msg.DB_ANSWER_INFO_SENDED_TO_FRAG, "Статьи загружены из кэша" };
							sendBroadcastWithResult(ctx, resultMessage, dataToSend, categoryToLoad, pageToLoad);
						break;
					}//switch
				}
			};
			AsyncTaskAskDBFromTop askFromTop = new AsyncTaskAskDBFromTop(this, dataBaseHelper, categoryToLoad,
			calOfLastRefresh, pageToLoad, callback);
			askFromTop.execute();
			//			this.startDownLoad(catToLoad, pageToLoad);
		}//if (pageToLoad == 1)
	}//onError

	/**
	 * this method simply returns connection (?) (and open it if neccesary) to
	 * DB with version gained from resources
	 * 
	 * @return
	 */
	private DataBaseHelper getHelper()
	{
		if (dataBaseHelper == null)
		{
			int dbVer = this.getResources().getInteger(R.integer.db_version);
			dataBaseHelper = new DataBaseHelper(this, DataBaseHelper.DATABASE_NAME, null, dbVer);
		}
		return dataBaseHelper;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.d(LOG, "onDestroy");

		if (this.receiverArticleLoaded != null)
		{
			LocalBroadcastManager.getInstance(ctx).unregisterReceiver(receiverArticleLoaded);
			this.receiverArticleLoaded = null;
		}
		if (this.artsDataReceiver != null)
		{
			LocalBroadcastManager.getInstance(ctx).unregisterReceiver(artsDataReceiver);
			this.artsDataReceiver = null;
		}
		if (dataBaseHelper != null)
		{
			OpenHelperManager.releaseHelper();
			dataBaseHelper = null;
		}
	}

	/**
	 * Sends intent with message and data to fragment
	 * 
	 * Also updates hashMap with all articles
	 * 
	 * @param ctx
	 * @param resultMessage
	 * @param dataToSend
	 * @param categoryToLoad
	 * @param pageToLoad
	 */
	public void sendBroadcastWithResult(Context ctx, String[] resultMessage, ArrayList<Article> dataToSend,
	String categoryToLoad, int pageToLoad)
	{
		//		Log.d(LOG + categoryToLoad, "sendBroadcastWithResult");
		//		Log.e(LOG, Arrays.toString(resultMessage));
		if (!Msg.ERROR.equals(resultMessage[0]) && !Const.Error.CONNECTION_ERROR.equals(resultMessage[1]))
		{
			updateHashMap(dataToSend, categoryToLoad, pageToLoad);
		}
		Intent intent = new Intent(categoryToLoad);
		intent.putExtra(Msg.MSG, resultMessage);
		intent.putExtra("pageToLoad", pageToLoad);
		intent.putExtra(Article.KEY_ALL_ART_INFO, dataToSend);
		LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
	}

	@Override
	public void onAnswerFromDBFromTop(String answer, String categoryToLoad, int pageToLoad,
	ArrayList<Article> dataToSend)
	{
		//Log.i(LOG, answer);
		switch (answer)
		{
			case Msg.DB_ANSWER_NEVER_REFRESHED:
				//was never refreshed, so start to refresh
				//so start download category with 1-st page
				this.startDownLoad(categoryToLoad, pageToLoad);
			break;
			case Msg.DB_ANSWER_REFRESH_BY_PERIOD:
				//was refreshed more than max period, so start to refresh
				//so start download category with 1-st page
				//but firstly we must show old articles
				this.startDownLoad(categoryToLoad, pageToLoad);
			break;

			case Msg.DB_ANSWER_NO_ENTRY_OF_ARTS:
				//no arts in DB (why?)
				//we get it if there is no need to refresh by period, so we have one successful load in past...
				//but no art's in db... that's realy strange! =)
				//so start download from web
				this.startDownLoad(categoryToLoad, pageToLoad);
			break;
			case Msg.DB_ANSWER_UNKNOWN_CATEGORY:
				//start download arts of this category
				this.startDownLoad(categoryToLoad, pageToLoad);
			break;
			case Msg.DB_ANSWER_INFO_SENDED_TO_FRAG:
				//here we have nothing to do... Cause there is no need to load somthing from web,
				//and arts have been already sended to frag
				String[] resultMessage = { Msg.DB_ANSWER_INFO_SENDED_TO_FRAG, null };
				sendBroadcastWithResult(ctx, resultMessage, dataToSend, categoryToLoad, pageToLoad);
			break;
		}
	}

	@Override
	public void onAnswerFromDBFromBottom(String answer, String categoryToLoad, int pageToLoad,
	ArrayList<Article> dataToSend)
	{
		//Log.i(LOG, answer);
		String[] resultMessage = { answer, null };
		switch (answer)
		{
			case Msg.DB_ANSWER_FROM_BOTTOM_INFO_SENDED_TO_FRAG:
				//all is done, we can go to drink some vodka, Ivan! =)
				sendBroadcastWithResult(ctx, resultMessage, dataToSend, categoryToLoad, pageToLoad);
			break;
			case Msg.DB_ANSWER_FROM_BOTTOM_INITIAL_ART_ALREADY_SHOWN:
			//initial art is shown, do nothing
			break;
			case Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_HAVE_MATCH_TO_INITIAL:
				//arts already send to frag with initial art, do nothingf
				sendBroadcastWithResult(ctx, resultMessage, dataToSend, categoryToLoad, pageToLoad);
			break;
			case Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_NO_INITIAL:
				//so load from web
				this.startDownLoad(categoryToLoad, pageToLoad);
			break;
			case Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_NO_MATCH_TO_INITIAL:
				//so load from web
				this.startDownLoad(categoryToLoad, pageToLoad);
			break;
			case Msg.DB_ANSWER_FROM_BOTTOM_LESS_THEN_30_FROM_TOP:
			//initial art is shown (less then 30 in category at all), do nothing
			break;
			case Msg.DB_ANSWER_FROM_BOTTOM_NO_ARTS_AT_ALL:
				//no arts except already shown, so load them from web
				this.startDownLoad(categoryToLoad, pageToLoad);
			break;
		}
	}

	@Override
	public void onDoneWritingFromBottom(String[] resultMessage, ArrayList<Article> dataFromWeb, String categoryToLoad,
	int pageToLoad)
	{
		sendBroadcastWithResult(this, resultMessage, dataFromWeb, categoryToLoad, pageToLoad);
	}

	@Override
	public void onDoneWritingFromTop(String[] resultMessage, ArrayList<Article> dataFromWeb, String categoryToLoad,
	int pageToLoad)
	{
		//here we can check if we start it from notif task and show notification
		if (this.notify)
		{
			if (this.catToNotify.equals(categoryToLoad) && this.pageToNotify == pageToLoad)
			{
				//notify
				Log.i(LOG, "WE CAN NOTIFY!");
				this.notify = false;
				switch (resultMessage[0])
				{
					case (Msg.NO_NEW):
						Log.d(LOG + "NOTIF", "Новых статей не обнаружено!");
					//XXX It's for test only!
					//					sendNotification("15", dataFromWeb);
					//nothing to notify
					break;
					case (Msg.NEW_QUONT):
					case (Msg.DB_ANSWER_WRITE_FROM_TOP_NO_MATCHES):
						Log.d(LOG + "NOTIF", "Обнаружено " + resultMessage[1] + " новых статей");
						sendNotification(resultMessage[1], dataFromWeb);
					break;
				}
				//and start download RSS to update pubDate and preview
				Intent intentRSS = new Intent(this, ServiceRSS.class);
				intentRSS.putExtra("categoryToLoad", categoryToLoad);
				this.startService(intentRSS);
			}
			else
			{
				sendBroadcastWithResult(this, resultMessage, dataFromWeb, categoryToLoad, pageToLoad);
			}
		}
		else
		{
			sendBroadcastWithResult(this, resultMessage, dataFromWeb, categoryToLoad, pageToLoad);
		}
	}

	/**
	 * Send simple notification using the NotificationCompat API.
	 */
	public void sendNotification(String newQuont, ArrayList<Article> dataFromWeb)
	{
		// Use NotificationCompat.Builder to set up our notification.
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

		//icon appears in device notification bar and right hand corner of notification
		builder.setSmallIcon(R.drawable.ic_radio_button_off_white_48dp);

		//Set the text that is displayed in the status bar when the notification first arrives.
		builder.setTicker(dataFromWeb.get(0).getTitle());

		// This intent is fired when notification is clicked
		Intent intent = new Intent(this, ActivityMain.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		// Set the intent that will fire when the user taps the notification.
		builder.setContentIntent(pendingIntent);

		// Large icon appears on the left of the notification
		builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));

		// Content title, which appears in large type at the top of the notification
		//		builder.setContentTitle("Новые статьи");

		// Content text, which appears in smaller text below the title
		//				builder.setContentText("Новые статьи");

		// The subtext, which appears under the text on newer devices.
		// This will show-up in the devices with Android 4.2 and above only
		builder.setSubText(dataFromWeb.get(0).getTitle());//"Всего новых статей:");

		builder.setAutoCancel(true);

		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		if (newQuont.equals("более 30"))
		{
			newQuont = "30";
			String[] events = new String[dataFromWeb.size()];
			inboxStyle.setBigContentTitle("Новые статьи:");
			// Moves events into the expanded layout
			for (int i = 0; i < events.length; i++)
			{
				events[i] = dataFromWeb.get(i).getTitle();
				inboxStyle.addLine(events[i]);
			}
			builder.setNumber(30);
		}
		else
		{
			//to test
			//newQuont = "10";
			String[] events = new String[Integer.parseInt(newQuont)];
			// Sets a title for the Inbox in expanded layout
			inboxStyle.setBigContentTitle("Новые статьи:");
			// Moves events into the expanded layout
			for (int i = 0; i < events.length; i++)
			{
				events[i] = dataFromWeb.get(i).getTitle();
				inboxStyle.addLine(events[i]);
			}
			builder.setNumber(Integer.parseInt(newQuont));
		}

		// Moves the expanded layout object into the notification object.
		builder.setStyle(inboxStyle);
		////////////

		// Content title, which appears in large type at the top of the notification
		builder.setContentTitle("Новые статьи: " + newQuont);

		//Sets up the action buttons that will appear in the big view of the notification.
		//add download new articles action
		Intent downloadArtsIntent = new Intent(this, ServiceArticle.class);
		downloadArtsIntent.setAction(Const.Action.DATA_REQUEST_MULTIPLE);
		ArrayList<String> urls = new ArrayList<String>();
		for (int i = 0; i < Integer.parseInt(newQuont); i++)
		{
			urls.add(dataFromWeb.get(i).getUrl());
		}
		//check if !isPro and size is more then 10
		if ((this.pref.getBoolean(ActivityPreference.PREF_KEY_IS_PRO, false) == false) && (urls.size() > 10))
		{
			urls = new ArrayList<String>(urls.subList(0, 10));
		}
		downloadArtsIntent.putStringArrayListExtra(FragmentArticle.ARTICLE_URL, urls);
		downloadArtsIntent.putExtra("startDownload", true);
		PendingIntent pendingIntentDownloadArts = PendingIntent.getService(this, 0, downloadArtsIntent,
		PendingIntent.FLAG_UPDATE_CURRENT);

		builder.addAction(R.drawable.ic_file_download_grey600_24dp,
		"Загрузить", pendingIntentDownloadArts);
		//add TTS of new arts

		Intent intentTTS = new Intent(this.getApplicationContext(), ServiceTTS.class);
		intentTTS.setAction("init");
		ArrayList<Article> dataToTTS = new ArrayList<Article>(dataFromWeb.subList(0, Integer.parseInt(newQuont)));
		intentTTS.putParcelableArrayListExtra(FragmentArticle.ARTICLE_URL, dataToTTS);
		intentTTS.putExtra("position", 0);
		PendingIntent piSnooze = PendingIntent.getService(this.getApplicationContext(), 0, intentTTS,
		PendingIntent.FLAG_CANCEL_CURRENT);

		builder.addAction(R.drawable.ic_play_arrow_grey600_24dp, "Озвучить", piSnooze);

		//Vibration
		if (this.pref.getBoolean(ActivityPreference.PREF_KEY_NOTIF_VIBRATION, false))
		{
			builder.setVibrate(new long[] { 500, 500, 500, 500, 500 });
		}
		//LED
		builder.setLights(Color.WHITE, 3000, 3000);
		//Sound
		if (this.pref.getBoolean(ActivityPreference.PREF_KEY_NOTIF_SOUND, false))
		{
			Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			builder.setSound(alarmSound);
		}

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// Will display the notification in the notification bar
		notificationManager.notify(NOTIFICATION_NEW_ARTICLES_ID, builder.build());
	}

	private CallbackWriteArticles callbackWriteArticles = new CallbackWriteArticles()
	{
		@Override
		public void onDoneWritingArticles(ArrayList<Article> dataFromDB, String categoryToLoad, int pageToLoad)
		{
			//now update REFRESHED field of Category or Author entry in table if we load from top
			//and write downloaded arts to ArtCatTable
			if (pageToLoad == 1)
			{
				//			AsyncTaskAskUpdateRefreshedDate updateRefreshedDate = new AsyncTaskAskUpdateRefreshedDate(getHelper(),
				//			categoryToLoad);
				//			updateRefreshedDate.execute();
				AsyncTaskWriteFromTop resultWriteFromTop = new AsyncTaskWriteFromTop(getHelper(), dataFromDB,
				categoryToLoad, pageToLoad, ServiceDB.this);
				resultWriteFromTop.execute();
			}
			else
			{
				//we don't need to update refreshed Date, cause we do it only when loading from top
				AsyncTaskWriteFromBottom resultWriteFromBottom = new AsyncTaskWriteFromBottom(getHelper(), dataFromDB,
				categoryToLoad, pageToLoad, ServiceDB.this);
				resultWriteFromBottom.execute();
			}
		}
	};

	/**
	 * map with lists of articles info for all categories and authors, witch
	 * keys gets from BD
	 */
	private HashMap<String, ArrayList<Article>> allCatArtsInfo;

	public HashMap<String, ArrayList<Article>> getAllCatArtsInfo()
	{
		if (this.allCatArtsInfo == null)
		{
			this.allCatArtsInfo = new HashMap<String, ArrayList<Article>>();
		}
		return allCatArtsInfo;
	}

	public void updateHashMap(ArrayList<Article> dataToSend, String categoryToLoad, int pageToLoad)
	{
		if (pageToLoad == 1)
		{
			this.getAllCatArtsInfo().put(categoryToLoad, dataToSend);
		}
		else
		{
			if (this.getAllCatArtsInfo().get(categoryToLoad) != null)
			{
				this.getAllCatArtsInfo().get(categoryToLoad).addAll(dataToSend);
			}
			else
			{
				this.getAllCatArtsInfo().put(categoryToLoad, dataToSend);
			}
		}
	}

	/**
	 * Class used for the client Binder. Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder
	{
		public ServiceDB getService()
		{
			// Return this instance of LocalService so clients can call public methods
			return ServiceDB.this;
		}
	}

	public IBinder onBind(Intent intent)
	{
		Log.d(LOG, "MyService onBind");
		return new LocalBinder();
	}

	public void onRebind(Intent intent)
	{
		super.onRebind(intent);
		Log.d(LOG, "MyService onRebind");
	}

	public boolean onUnbind(Intent intent)
	{
		Log.d(LOG, "MyService onUnbind");
		return super.onUnbind(intent);
	}

	protected BroadcastReceiver receiverArticleLoaded = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			//Log.i(LOG, "receiverArticleLoaded onReceive called");
			String[] menuLinks = CatData.getMenuLinks(ctx);
			AsyncTaskGetDownloaded getDownloaded = new AsyncTaskGetDownloaded(getHelper(),
			menuLinks[menuLinks.length - 1],
			1, ServiceDB.this);
			try
			{
				Article a = intent.getParcelableExtra(Article.KEY_CURENT_ART);
				Set<String> keySet = getAllCatArtsInfo().keySet();
				switch (intent.getStringExtra(Const.Action.ARTICLE_CHANGED))
				{
					case Const.Action.ARTICLE_READ:
						//loop through all arts in activity and update them and adapters
						for (String key : keySet)
						{
							ArrayList<Article> artsList = getAllCatArtsInfo().get(key);
							if (artsList != null)
							{
								boolean notFound = true;
								for (int i = 0; i < artsList.size() && notFound; i++)
								{
									Article artInList = artsList.get(i);
									if (artInList.getUrl().equals(a.getUrl()))
									{
										artsList.get(i).setReaden(a.isReaden());
										notFound = false;
									}
								}
							}
						}
					break;
					case Const.Action.ARTICLE_LOADED:
						//loop through all arts in activity and update them and adapters
						for (String key : keySet)
						{
							ArrayList<Article> artsList = getAllCatArtsInfo().get(key);
							if (artsList != null)
							{
								boolean notFound = true;
								for (int i = 0; i < artsList.size() && notFound; i++)
								{
									Article artInList = artsList.get(i);
									if (artInList.getUrl().equals(a.getUrl()))
									{
										if (!a.getArtText().equals(Const.EMPTY_STRING))
										{
											artsList.get(i).setArtText(a.getArtText());
										}
										//pubDate
										if (artsList.get(i).getPubDate().getTime() < a.getPubDate().getTime())
										{
											artsList.get(i).setPubDate(a.getPubDate());
										}
										//set preview
										artsList.get(i).setPreview(a.getPreview());
										//artsList.set(i, a);
										notFound = false;
									}
								}
							}
						}
						//update all_downloaded category
						getDownloaded.execute();
					break;
					case Const.Action.ARTICLE_DELETED:
						//loop through all arts in activity and update them and adapters
						for (String key : keySet)
						{
							ArrayList<Article> artsList = getAllCatArtsInfo().get(key);
							if (artsList != null)
							{
								boolean notFound = true;
								for (int i = 0; i < artsList.size() && notFound; i++)
								{
									Article artInList = artsList.get(i);
									if (artInList.getUrl().equals(a.getUrl()))
									{
										artsList.get(i).setArtText(Const.EMPTY_STRING);
										artsList.get(i).setRefreshed(new Date(0));
										notFound = false;
									}
								}
							}
						}
						//update all_downloaded category
						getDownloaded.execute();
					break;
				}
			}
			catch (Exception e)
			{
				Log.i(LOG, "Catched error in ArticleChanged receiver of SERVICE_DB");
				//				e.printStackTrace();
			}
		}
	};
}