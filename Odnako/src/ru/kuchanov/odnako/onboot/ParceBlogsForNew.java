package ru.kuchanov.odnako.onboot;

import java.net.URL;

import org.htmlcleaner.TagNode;

import ru.kuchanov.odnako.HtmlHelper;
import ru.kuchanov.odnako.MainActivityNew;
import ru.kuchanov.odnako.R;
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

public class ParceBlogsForNew extends AsyncTask<Void, Void, String[][]>
{

	NotificationManager notificationManager;
	Context context;
	String categoryToLoad;
	int curPageToLoad;

	final String LOG_TAG = "myLogs";

	String data;
	String link;

	SharedPreferences pref;

	public ParceBlogsForNew(Context context, String categoryToLoad, int curPageToLoad)
	{
		this.context = context;
		this.categoryToLoad = categoryToLoad;
		this.curPageToLoad = curPageToLoad;
	}

	protected void onPreExecute()
	{
		Log.d(LOG_TAG, "ParceBlogsForNew onPreExecute");
		System.out.println("ParceBlogsForNew: onPreExecute");
		////
		pref = PreferenceManager.getDefaultSharedPreferences(context);
		categoryToLoad = context.getResources().getStringArray(R.array.categories_to_load_links)[0];
		curPageToLoad = 1;
		////
	}

	protected String[][] doInBackground(Void... arg)
	{
		Log.d(LOG_TAG, "ParceBlogsForNew doInBackground");
		System.out.println("ParceBlogsForNew: doInBackground");
		String[][] output = null;
		link = "http://" + this.categoryToLoad + "/page-" + String.valueOf(this.curPageToLoad) + "/";
		System.out.println(link);
		try
		{
			HtmlHelper hh = new HtmlHelper(new URL(link));
			TagNode[] liElemsArr = hh.getBlogsInfo();

			data = hh.htmlString;

			output = new String[MainActivityNew.DEFAULT_NUM_OF_ARTS_ON_PAGE][MainActivityNew.NUM_OF_ELEMS_IN_DIV];
			for (int i = 0; i < liElemsArr.length; i++)
			{
				TagNode element = liElemsArr[i];
				TagNode element1 = element.findElementByAttValue("class", "m-news-info", true, true);
				TagNode element2 = element1.findElementByAttValue("class", "m-news-text", true, true);
				TagNode element3 = element2.findElementByName("a", true);

				TagNode[] imgEl = element.getElementsByName("img", true);

				TagNode author = element.findElementByAttValue("class", "m-news-author-wrap", true, false);
				TagNode[] author1 = author.getElementsByName("a", true);

				output[i][0] = element3.getAttributeByName("href").toString();
				output[i][1] = Html.fromHtml(element3.getAttributeByName("title").toString()).toString();

				if (imgEl.length == 0)
				{
					output[i][2] = "default";
				}
				else
				{
					output[i][2] = imgEl[0].getAttributeByName("src").toString();
				}
				if (author1.length == 0)
				{
					output[i][3] = "default";
					output[i][4] = "default";
				}
				else
				{
					output[i][3] = author1[0].getAttributeByName("href");
					output[i][4] = Html.fromHtml(author1[0].getAttributeByName("title")).toString();
				}
			}
			

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return output;
	}

	protected void onPostExecute(String[][] output)
	{
		System.out.println("ParceBlogsForNew: onPostExecute");
		if (output != null)
		{
			ParceCacheForNew parce = new ParceCacheForNew(context, categoryToLoad, curPageToLoad, output, link, data);
			parce.execute();
		}

	}// Событие по окончанию парсинга

	@SuppressWarnings("deprecation")
	void notificate()
	{
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;
		CharSequence notiText = "Загрузка статей";
		long meow = System.currentTimeMillis();

		Notification notification = new Notification(icon, notiText, meow);
		//Context context = context.getApplicationContext();
		CharSequence contentTitle = "Загрузка статей";
		CharSequence contentText = "Статьи загружаются...";
		Intent notificationIntent = new Intent(context, MainActivityNew.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		int SERVER_DATA_RECEIVED = 1;
		notificationManager.notify(SERVER_DATA_RECEIVED, notification);
	}
}