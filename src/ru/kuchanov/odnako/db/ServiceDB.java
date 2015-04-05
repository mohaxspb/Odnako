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
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.callbacks.AllArtsInfoCallback;
import ru.kuchanov.odnako.callbacks.CallbackAskDBFromBottom;
import ru.kuchanov.odnako.callbacks.CallbackAskDBFromTop;
import ru.kuchanov.odnako.download.HtmlHelper;
import ru.kuchanov.odnako.download.ParsePageForAllArtsInfo;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

//tag:^(?!dalvikvm) tag:^(?!libEGL) tag:^(?!Open) tag:^(?!Google) tag:^(?!resour) tag:^(?!Chore) tag:^(?!EGL) tag:^(?!SocketStream)
public class ServiceDB extends Service implements AllArtsInfoCallback, CallbackAskDBFromTop, CallbackAskDBFromBottom
{
	final private static String LOG = ServiceDB.class.getSimpleName() + "/";

	private DataBaseHelper dataBaseHelper;

	Context ctx;

	private List<ParsePageForAllArtsInfo> currentTasks = new ArrayList<ParsePageForAllArtsInfo>();

	//	List<String> categoriesToCheck;

	public void onCreate()
	{
		Log.d(LOG, "onCreate");
		super.onCreate();

		this.ctx = this;
		this.getHelper();

		//		categoriesToCheck = new ArrayList<String>();
		//		String[] menuUrls = CatData.getMenuLinks(this);
		//		//Onotole
		//		categoriesToCheck.add(menuUrls[2]);
		//		//Ideology
		//		categoriesToCheck.add(menuUrls[4]);
		//		//first author (Olga A Agarcove) as I remember
		//		Author firstAuthorOrderedByName;
		//		try
		//		{
		//			firstAuthorOrderedByName = this.getHelper().getDaoAuthor().queryBuilder()
		//			.orderBy(Author.NAME_FIELD_NAME, true).queryForFirst();
		//			categoriesToCheck.add(Author.getURLwithoutSlashAtTheEnd(firstAuthorOrderedByName.getBlog_url()));
		//		} catch (SQLException e)
		//		{
		//			e.printStackTrace();
		//		}
	}

	public int onStartCommand(Intent intent, int flags, int startId)
	{
		//Log.d(LOG, "onStartCommand");
		String catToLoad;
		//firstly: if we load from top or not? Get it by pageToLoad
		int pageToLoad;
		if (intent == null)
		{
			Log.e(LOG, "intent=null!!! WTF?!");
			return super.onStartCommand(intent, flags, startId);
		}
		String action = intent.getAction();
		catToLoad = intent.getStringExtra("categoryToLoad");
		pageToLoad = intent.getIntExtra("pageToLoad", 1);
		if (action.equals(Const.Action.IS_LOADING))
		{
			for (ParsePageForAllArtsInfo a : this.currentTasks)
			{
				if (catToLoad.equals(a.getCategoryToLoad()) && (pageToLoad == a.getPageToLoad())
				&& (a.getStatus() == AsyncTask.Status.RUNNING))
				{
					Intent intentIsLoading = new Intent(catToLoad + Const.Action.IS_LOADING);
					intentIsLoading.putExtra(Const.Action.IS_LOADING, true);
					LocalBroadcastManager.getInstance(this).sendBroadcast(intentIsLoading);
					return super.onStartCommand(intent, flags, startId);
				}
			}
			Intent intentIsLoading = new Intent(catToLoad + Const.Action.IS_LOADING);
			intentIsLoading.putExtra(Const.Action.IS_LOADING, false);
			LocalBroadcastManager.getInstance(this).sendBroadcast(intentIsLoading);
			return super.onStartCommand(intent, flags, startId);
		}

		//get startDownload flag
		boolean startDownload = intent.getBooleanExtra("startDownload", false);

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
				this.startDownLoad(catToLoad, pageToLoad);
			}
			else
			{
				AsyncTaskAskDBFromTop askFromTop = new AsyncTaskAskDBFromTop(this, dataBaseHelper, catToLoad, cal,
				pageToLoad, this);
				askFromTop.execute();
			}
		}
		else
		{
			//if pageToLoad!=1 we load from bottom
			//Log.d(LOG, "LOAD FROM BOTTOM!");

			AsyncTaskAskDBFromBottom askFromBottom = new AsyncTaskAskDBFromBottom(this, dataBaseHelper, catToLoad,
			pageToLoad, this);
			askFromBottom.execute();
		}
		return super.onStartCommand(intent, flags, startId);
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
			ParsePageForAllArtsInfo parse = new ParsePageForAllArtsInfo(catToLoad, pageToLoad, this, this,
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

			ParsePageForAllArtsInfo parseToAdd = new ParsePageForAllArtsInfo(catToLoad, pageToLoad, this, this,
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
		String[] resultMessage;
		if (dataToSend.size() == 0)
		{
			Article a = new Article();
			a.setTitle("Ни одной статьи не обнаружено.");
			dataToSend.add(a);
			resultMessage = new String[] { Msg.DB_ANSWER_NO_ARTS_IN_CATEGORY, null };
		}
		else
		{
			//here we'll write gained arts to Article table 
			dataToSend = Article.writeArticleToArticleTable(getHelper(), dataToSend);

			//here if we recive less then 30 arts (const quont of arts on page)
			//we KNOW that last of them is initial art in category (author)
			//so WRITE it to DB!
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

			//now update REFRESHED field of Category or Author entry in table if we load from top
			//and write downloaded arts to ArtCatTable
			if (pageToLoad == 1)
			{
				new DBActions(this, this.getHelper()).updateRefreshedDate(categoryToLoad);
				resultMessage = new DBActions(this, this.getHelper()).writeArtsToDBFromTop(dataToSend, categoryToLoad);
			}
			else
			{
				//we don't need to update refreshed Date, cause we do it only when loading from top
				resultMessage = new DBActions(this, this.getHelper()).writeArtsToDBFromBottom(dataToSend,
				categoryToLoad,
				pageToLoad);
				Log.d(LOG + categoryToLoad, resultMessage[0]);
			}
		}
		//		Log.d(LOG + "sendDownloadedData", resultMessage[0]/* +"/"+resultMessage[1] */);
		ServiceDB.sendBroadcastWithResult(this, resultMessage, dataToSend, categoryToLoad, pageToLoad);
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
		ServiceDB.sendBroadcastWithResult(this, resultMessage, null, categoryToLoad, pageToLoad);
		//here if we loaded from top and get NO_CONNECTION ERROR we can ask DB for arts.
		//And toast if there are no arts in DB (cache);
		//So ask DB with calendar(time of last refresh);
		Calendar calOfLastRefresh = Calendar.getInstance();
		Boolean isCategory = Category.isCategory(getHelper(), categoryToLoad);
		if (isCategory == null)
		{
			resultMessage = new String[] { Msg.ERROR, "Категория в базе данных не обнаружена. И разработчик очень удивится, узнав, что вы читаете это сообщение" };
			ServiceDB.sendBroadcastWithResult(this, resultMessage, null, categoryToLoad, pageToLoad);
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
			ServiceDB.sendBroadcastWithResult(this, resultMessage, null, categoryToLoad, pageToLoad);
			return;
		}
		//we can now try to get articles, storred in DB;
		if (pageToLoad == 1)
		{
			CallbackAskDBFromTop callback = new CallbackAskDBFromTop()
			{
				@Override
				public void onAnswerFromDBFromTop(String answer, String categoryToLoad, int pageToLoad)
				{
					//					switch (new DBActions(this, this.getHelper()).askDBFromTop(categoryToLoad, calOfLastRefresh, pageToLoad))
					String[] resultMessage;
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
							ServiceDB.sendBroadcastWithResult(ctx, resultMessage, null, categoryToLoad, pageToLoad);
						break;
						case Msg.DB_ANSWER_UNKNOWN_CATEGORY:
						//that's can't be, because we check it before
						break;
						case Msg.DB_ANSWER_INFO_SENDED_TO_FRAG:
							//Everything is OK. We send arts from DB to fragment
							resultMessage = new String[] { Msg.ERROR, "Статьи загружены из кэша" };
							ServiceDB.sendBroadcastWithResult(ctx, resultMessage, null, categoryToLoad, pageToLoad);
						break;
					}//switch
				}
			};
			AsyncTaskAskDBFromTop askFromTop = new AsyncTaskAskDBFromTop(this, dataBaseHelper, categoryToLoad,
			calOfLastRefresh, pageToLoad, callback);
			askFromTop.execute();
		}//if (pageToLoad == 1)
	}//onError

	public IBinder onBind(Intent intent)
	{
		Log.d(LOG, "onBind");
		return null;
	}

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
		if (dataBaseHelper != null)
		{
			OpenHelperManager.releaseHelper();
			dataBaseHelper = null;
		}
	}

	/**
	 * Sends intent with message and data to fragment
	 * 
	 * @param ctx
	 * @param resultMessage
	 * @param dataToSend
	 * @param categoryToLoad
	 * @param pageToLoad
	 */
	public static void sendBroadcastWithResult(Context ctx, String[] resultMessage, ArrayList<Article> dataToSend,
	String categoryToLoad, int pageToLoad)
	{
		//Log.d(LOG + categoryToLoad, "sendBroadcastWithResult");
		Intent intent = new Intent(categoryToLoad);
		intent.putExtra(Msg.MSG, resultMessage);
		intent.putExtra("pageToLoad", pageToLoad);
		intent.putExtra(Article.KEY_ALL_ART_INFO, dataToSend);
		LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
	}

	@Override
	public void onAnswerFromDBFromTop(String answer, String categoryToLoad, int pageToLoad)
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
			break;
		}
	}

	@Override
	public void onAnswerFromDBFromBottom(String answer, String categoryToLoad, int pageToLoad)
	{
		//Log.i(LOG, answer);
		switch (answer)
		{
			case Msg.DB_ANSWER_FROM_BOTTOM_INFO_SENDED_TO_FRAG:
			//all is done, we can go to drink some vodka, Ivan! =)
			break;
			case Msg.DB_ANSWER_FROM_BOTTOM_INITIAL_ART_ALREADY_SHOWN:
			//initial art is shown, do nothing
			break;
			case Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_HAVE_MATCH_TO_INITIAL:
			//arts already send to frag with initial art, do nothing
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
}