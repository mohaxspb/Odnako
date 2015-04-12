/*
 12.04.2015
ServiceTTS.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import java.util.ArrayList;
import java.util.Locale;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.fragments.FragmentArticle;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

public class ServiceTTS extends Service implements TextToSpeech.OnInitListener
{
	final private static String LOG = ServiceTTS.class.getSimpleName() + "/";

	public final static int NOTIFICATION_TTS_ID = 99;

	ArrayList<Article> artList;

	private TextToSpeech mTTS;

	@Override
	public void onCreate()
	{
		Log.d(LOG, "onCreate");
		super.onCreate();

		mTTS = new TextToSpeech(this, this);
		mTTS.setOnUtteranceProgressListener(new UtteranceProgressListener()
		{

			@Override
			public void onStart(String utteranceId)
			{
				// TODO Auto-generated method stub
				Log.i(LOG, "onStart");
			}

			@Override
			@Deprecated
			public void onError(String utteranceId)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onDone(String utteranceId)
			{
				// TODO Auto-generated method stub
				Log.i(LOG, "onDone");
			}
		});
	}

	@SuppressWarnings("deprecation")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		String action = intent.getAction();
		switch (action)
		{
			case "init":
				ArrayList<Article> artList = intent.getParcelableArrayListExtra(FragmentArticle.ARTICLE_URL);
				this.artList = artList;
				NotificationCompat.Builder builder = this.createNotification(artList.get(0), 0, 1, false);
				this.startForeground(NOTIFICATION_TTS_ID, builder.build());
			break;
			case "play":
				Log.e(LOG, "play");
				this.updateNotification(this.artList.get(0), 0, 1);

				String text = "I'm a TTS service, hurray!";//this.artList.get(0).getArtText()
				text = this.artList.get(0).getArtText();
				text = Html.fromHtml(text.substring(0, 500)).toString();//
				Log.i(LOG, text);
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
				{
					mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, "test");
				}
				else
				{
					mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
				}
			break;
			case "pause":
				mTTS.stop();
				this.updateNotification(this.artList.get(0), 0, 100);
			break;
		}
		return super.onStartCommand(intent, flags, startId);
	}

	//	private NotificationCompat.Builder createNotification(ArrayList<Article> arts)
	private NotificationCompat.Builder createNotification(Article art, int iterator, int quont, boolean isPaying)
	{
		// Use NotificationCompat.Builder to set up our notification.
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

		//icon appears in device notification bar and right hand corner of notification
		builder.setSmallIcon(R.drawable.ic_radio_button_off_white_48dp);

		// Large icon appears on the left of the notification
		builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_play_arrow_grey600_48dp));

		// The subtext, which appears under the text on newer devices.
		// This will show-up in the devices with Android 4.2 and above only
		builder.setSubText("Всего новых статей:");

		builder.setAutoCancel(false);

		///////////////
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

		if (!art.getPreview().equals(Const.EMPTY_STRING))
		{
			inboxStyle.addLine(art.getPreview());
		}
		//			builder.setNumber(art);

		// Moves the expanded layout object into the notification object.
		builder.setStyle(inboxStyle);
		////////////
		// Content title, which appears in large type at the top of the notification
		builder.setContentTitle("Новые статьи: ");

		//Sets up the action buttons that will appear in the big view of the notification.
		//add TTS of new arts
		Intent snoozeIntent = new Intent(this, ServiceTTS.class);
		snoozeIntent.setAction("play");
		PendingIntent piSnooze = PendingIntent.getService(this, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		builder.addAction(R.drawable.ic_play_arrow_grey600_24dp,
		"", piSnooze);
		///////////////

		return builder;
	}

	private void updateNotification(Article art, int iterator, int quontity)
	{
		//update notification
		NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.ic_radio_button_off_white_48dp);
		builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_play_arrow_grey600_48dp));
		builder.setSubText("Всего новых статей:");
		builder.setAutoCancel(false);
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		if (!art.getPreview().equals(Const.EMPTY_STRING))
		{
			inboxStyle.addLine(art.getPreview());
		}
		builder.setStyle(inboxStyle);
		builder.setContentTitle("Новые статьи: ");

		//Sets up the action buttons that will appear in the big view of the notification.
		//add TTS of new arts
		Intent snoozeIntent = new Intent(this, ServiceTTS.class);
		if (quontity == 100)
		{
			snoozeIntent.setAction("play");
		}
		else
		{
			snoozeIntent.setAction("pause");
		}
		PendingIntent piSnooze = PendingIntent.getService(this, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.addAction(R.drawable.ic_pause_grey600_24dp, "", piSnooze);
		// Displays the progress bar for the first time.
		mNotifyManager.notify(NOTIFICATION_TTS_ID, builder.build());
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onInit(int status)
	{
		Log.e(LOG, "TTS on init");
		if (status == TextToSpeech.SUCCESS)
		{
			Locale locale = new Locale("ru");

			int result = mTTS.setLanguage(locale);
			//int result = mTTS.setLanguage(Locale.getDefault());

			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
			{
				Log.e("TTS", "Извините, этот язык не поддерживается");
				locale = new Locale("en");

				result = mTTS.setLanguage(locale);
				if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
				{
					Log.e("TTS", "Извините, этот язык не поддерживается");
				}
				else
				{
					Log.i("TTS", "set colace eng");
				}
			}
		}
		else
		{
			Log.e("TTS", "Ошибка!");
		}
	}
}
