package ru.kuchanov.odnako.onboot;

import java.io.File;
import java.net.URL;

import org.htmlcleaner.TagNode;

import ru.kuchanov.odnako.HtmlHelper;
import ru.kuchanov.odnako.MainActivityNew;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.download.WriteFileService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;

public class ParceCacheForNew extends AsyncTask<Void, Void, String[][]>
{
	final static String EXTRA_NUM_OF_UNREADED_ARTS = "quontityOfUnreadedArts";

	NotificationManager notificationManager;
	Context context;
	String categoryToLoad;
	int curPageToLoad;

	String[][] loadedOutput;

	final String LOG_TAG = "myLogs";

	SharedPreferences pref;

	String link;
	String data;

	public ParceCacheForNew(Context context, String categoryToLoad, int curPageToLoad, String[][] loadedOutput, String link, String data)
	{
		this.context = context;
		this.categoryToLoad = categoryToLoad;
		this.curPageToLoad = curPageToLoad;
		this.loadedOutput = loadedOutput;
		this.link = link;
		this.data = data;
	}

	protected void onPreExecute()
	{
		Log.d(LOG_TAG, "ParceCacheForNew onPreExecute");
		System.out.println("ParceCacheForNew: onPreExecute");
		////
		pref = PreferenceManager.getDefaultSharedPreferences(context);
		categoryToLoad = context.getResources().getStringArray(R.array.categories_to_load_links)[0];
		curPageToLoad = 1;
		////
	}

	protected String[][] doInBackground(Void... arg)
	{
		Log.d(LOG_TAG, "ParceCacheForNew doInBackground");
		System.out.println("ParceCacheForNew: doInBackground");
		String[][] outputFromCashe = null;
		String storageDir;
		storageDir = pref.getString("filesDir", "");
		String formatedCategory;
		formatedCategory = this.categoryToLoad.replace("-", "_");
		formatedCategory = formatedCategory.replace("/", "_");
		formatedCategory = formatedCategory.replace(":", "_");
		formatedCategory = formatedCategory.replace(".", "_");
		String formatedPageName;
		formatedPageName = this.categoryToLoad + "/page-1/";
		formatedPageName = formatedPageName.replace("-", "_");
		formatedPageName = formatedPageName.replace("/", "_");
		formatedPageName = formatedPageName.replace(":", "_");
		formatedPageName = formatedPageName.replace(".", "_");
		String linkToCashe = "file:///" + storageDir + "/" + formatedCategory + "/" + "http___" + formatedPageName;

		System.out.println(linkToCashe);
		try
		{
			HtmlHelper hh = new HtmlHelper(new URL(linkToCashe));
			TagNode[] liElemsArr = hh.getBlogsInfo();

			outputFromCashe = new String[MainActivityNew.DEFAULT_NUM_OF_ARTS_ON_PAGE][MainActivityNew.NUM_OF_ELEMS_IN_DIV];
			for (int i = 0; i < liElemsArr.length; i++)
			{
				TagNode element = liElemsArr[i];
				TagNode element1 = element.findElementByAttValue("class", "m-news-info", true, true);
				TagNode element2 = element1.findElementByAttValue("class", "m-news-text", true, true);
				TagNode element3 = element2.findElementByName("a", true);

				TagNode[] imgEl = element.getElementsByName("img", true);

				TagNode author = element.findElementByAttValue("class", "m-news-author-wrap", true, false);
				TagNode[] author1 = author.getElementsByName("a", true);

				outputFromCashe[i][0] = element3.getAttributeByName("href").toString();
				outputFromCashe[i][1] = Html.fromHtml(element3.getAttributeByName("title").toString()).toString();

				if (imgEl.length == 0)
				{
					outputFromCashe[i][2] = "default";
				}
				else
				{
					outputFromCashe[i][2] = imgEl[0].getAttributeByName("src").toString();
				}
				if (author1.length == 0)
				{
					outputFromCashe[i][3] = "default";
					outputFromCashe[i][4] = "default";
				}
				else
				{
					outputFromCashe[i][3] = author1[0].getAttributeByName("href");
					outputFromCashe[i][4] = Html.fromHtml(author1[0].getAttributeByName("title")).toString();
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			File cachedBlogsPage=new File(linkToCashe);
			if(!cachedBlogsPage.exists())
			{
				//Write loaded blogs page on device
				WriteFileService write = new WriteFileService(context);
				write.setVars(link, data, categoryToLoad, 0, 1);
				write.execute();
			}
		}

		return outputFromCashe;
	}

	protected void onPostExecute(String[][] outputFromCashe)
	{
		System.out.println("ParceCacheForNew: onPostExecute");
		if (outputFromCashe != null)
		{
			for (int i = 0; i < 30; i++)
			{
				if (i == 0)
				{
					if (this.loadedOutput[0][0].equals(outputFromCashe[0][0]))
					{
						System.out.println("Новых статей не найдено.");
						return; //notificate(i);
					}
				}
				else
				{
					if (loadedOutput[i][0].equals(outputFromCashe[0][0]))
					{
						System.out.println("Найдено " + String.valueOf(i) + " новых статей.");
						notificate(i);
						//Write loaded blogs page on device
						WriteFileService write = new WriteFileService(context);
						write.setVars(link, data, categoryToLoad, 0, 1);
						write.execute();
						return;
					}
					else
					{
						if (i == 30 - 1)
						{
							System.out.println("Найдено более 30-ти новых статей.");
							notificate(31);
							//Write loaded blogs page on device
							WriteFileService write = new WriteFileService(context);
							write.setVars(link, data, categoryToLoad, 0, 1);
							write.execute();
						}
					}
				}
			}
		}

	}// Событие по окончанию парсинга

	@SuppressWarnings("deprecation")
	void notificate(int numOfNewArts)
	{
		//Restore num of unreded arts
		SharedPreferences prefsNumOfArts = context.getSharedPreferences("saveNumOfUnReadedArts", 0);
		int numOfUnreadedArts = prefsNumOfArts.getInt(ParceCacheForNew.EXTRA_NUM_OF_UNREADED_ARTS, 0);
		/////

		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		int icon = R.drawable.ic_launcher;
		CharSequence notiText = "Найдены новые статьи(" + String.valueOf(numOfNewArts + numOfUnreadedArts) + ")";
		long meow = System.currentTimeMillis();

		Notification notification = new Notification(icon, notiText, meow);
		// Cancel the notification after its selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		/////SOUND and vibro
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(context);
		boolean soundOn = pref.getBoolean("notification", false);
		if(soundOn)
		{
			notification.defaults |= Notification.DEFAULT_SOUND;
		}
		boolean vibroOn = pref.getBoolean("notification", false);
		if(vibroOn)
		{
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}
		/////SOUND and vibro
		CharSequence contentTitle = "Новые статьи: " + String.valueOf(numOfNewArts + numOfUnreadedArts);
		CharSequence contentText;
		if (numOfNewArts != 31)
		{
			//contentText = "Найдено " + String.valueOf(numOfNewArts) + " новых статей!";
			contentText = loadedOutput[0][1];
			notification.number = numOfNewArts+ numOfUnreadedArts;
		}
		else
		{
			contentText = "Найдено более 30-ти новых статей!";
		}

		Intent notificationIntent = new Intent(context, MainActivityNew.class);//.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

		int SERVER_DATA_RECEIVED = 1;
		notificationManager.notify(SERVER_DATA_RECEIVED, notification);
		saveNumOfUnReadedArts(numOfNewArts + numOfUnreadedArts);
	}

	//Save num of unreaded arts to prefs. Then get it to update notification/ set to zero when launch MainActivity
	void saveNumOfUnReadedArts(int numOfNewArts)
	{
		SharedPreferences prefsNumOfArts = context.getSharedPreferences("saveNumOfUnReadedArts", 0);

		SharedPreferences.Editor editor = prefsNumOfArts.edit();
		editor.putInt(ParceCacheForNew.EXTRA_NUM_OF_UNREADED_ARTS, numOfNewArts);
		editor.commit();
	}
}