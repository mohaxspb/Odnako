/*
 12.04.2015
ServiceTTS.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class ServiceTTS extends Service implements TextToSpeech.OnInitListener
{
	final private static String LOG = ServiceTTS.class.getSimpleName() + "/";

	public final static int NOTIFICATION_TTS_ID = 99;

	public final static int MAX_TTS_STRING_LENGTH = 250;
	public final static int MAX_TITLE_LENGTH = 30;

	NotificationManager mNotifyManager;

	private ArrayList<Article> artList;
	private int currentArtPosition = 0;
	private ArrayList<String> curArtTextList;
	private int curArtTextListPosition = 0;

	private TextToSpeech mTTS;

	private boolean isPaused = true;

	@Override
	public void onCreate()
	{
		Log.d(LOG, "onCreate");
		super.onCreate();

		mTTS = new TextToSpeech(this, this);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2)
		{
			mTTS.setOnUtteranceProgressListener(uPL);
		}
		else
		{
			mTTS.setOnUtteranceCompletedListener(oUCL);
		}

		mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	UtteranceProgressListener uPL = new UtteranceProgressListener()
	{
		@Override
		public void onStart(String utteranceId)
		{
			Log.i(LOG, "onStart");
		}

		@Override
		@Deprecated
		public void onError(String utteranceId)
		{
			Log.e(LOG, "onError");
		}

		@Override
		public void onDone(String utteranceId)
		{
			Log.i(LOG, "onDone");
			onCompletePartReading();
			mNotifyManager.notify(NOTIFICATION_TTS_ID, getNotification().build());
		}
	};

	OnUtteranceCompletedListener oUCL = new OnUtteranceCompletedListener()
	{
		@Override
		public void onUtteranceCompleted(String utteranceId)
		{
			Log.i(LOG, "onUtteranceCompleted");
			onCompletePartReading();
			mNotifyManager.notify(NOTIFICATION_TTS_ID, getNotification().build());
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		String action = intent.getAction();
		switch (action)
		{
			case "init":
				ArrayList<Article> artList = intent.getParcelableArrayListExtra(FragmentArticle.ARTICLE_URL);

				//XXX for test cut arts text to 270 chars
				for (Article a : artList)
				{
					TextView tv = new TextView(this);
					tv.setText(Html.fromHtml(a.getArtText()));
					String articlesTextWithoutHtml = tv.getText().toString();
					String cutedText = articlesTextWithoutHtml.substring(0, 270);
					a.setArtText(cutedText);
				}

				this.artList = artList;
				this.currentArtPosition = 0;

				this.curArtTextList = this.createArtTextListFromArticle(this.artList.get(this.currentArtPosition));
				this.curArtTextListPosition = 0;

				this.isPaused = true;

				this.startForeground(NOTIFICATION_TTS_ID, this.getNotification().build());
			break;
			case "play":
				Log.e(LOG, "play");
				this.isPaused = false;
				this.speekPart();
				mNotifyManager.notify(NOTIFICATION_TTS_ID, this.getNotification().build());
			break;
			case "pause":
				Log.e(LOG, "pause");
				this.isPaused = true;
				mTTS.stop();
				mNotifyManager.notify(NOTIFICATION_TTS_ID, this.getNotification().build());
			break;
			case "close":
				this.stopForeground(true);
			break;
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void onCompletePartReading()
	{
		if (this.isPaused)
		{
			//Nothing to do...
		}
		else
		{
			if (this.curArtTextListPosition == this.curArtTextList.size() - 1)
			{
				//so we've already read last part of article
				if (this.currentArtPosition == this.artList.size() - 1)
				{
					//so we've already read the last article from given list
					//set text to the start
					this.curArtTextListPosition = 0;
					this.isPaused = true;
					//TODO update notification to "paused" state;
					this.mNotifyManager.notify(NOTIFICATION_TTS_ID, this.getNotification().build());
				}
				else
				{
					this.currentArtPosition++;
					this.curArtTextList = this.createArtTextListFromArticle(this.artList.get(currentArtPosition));
					this.curArtTextListPosition = 0;
					//TODO update notif and play new article
					this.mNotifyManager.notify(NOTIFICATION_TTS_ID, this.getNotification().build());
					this.speekPart();
				}
			}
			else
			{
				this.curArtTextListPosition++;
				this.speekPart();
			}
		}
	}

	private void speekPart()
	{
		String text = this.curArtTextList.get(this.curArtTextListPosition);
		Log.i(LOG, text);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
		{
			mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, "test");
		}
		else
		{
			HashMap<String, String> params = new HashMap<String, String>();
			params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "test");
			mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, params);
		}
	}

	private NotificationCompat.Builder getNotification()
	{
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.ic_play_arrow_grey600_24dp);
		builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_play_arrow_grey600_48dp));
		builder.setAutoCancel(false);

		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		Article curArt = this.artList.get(currentArtPosition);
		if (curArt.getArtText().equals(Const.EMPTY_STRING))
		{
			//TODO load article and show progress
		}
		else
		{
			//split title to parts with lenght==30 to fit notif lines
			ArrayList<String> titlePartsList = new ArrayList<String>();
			int numOfParts = (curArt.getTitle().length() / MAX_TITLE_LENGTH) + 1;
			int startChar = 0;
			int endChar = MAX_TITLE_LENGTH;

			for (int i = 0; i < numOfParts; i++)
			{
				//check if it's last iteration
				if (i == numOfParts - 1)
				{
					String part = curArt.getTitle().substring(startChar, curArt.getTitle().length());
					titlePartsList.add(part);
				}
				else
				{
					String part = curArt.getTitle().substring(startChar, endChar);
					titlePartsList.add(part);
					startChar = MAX_TITLE_LENGTH * (i + 1);
					endChar = (MAX_TITLE_LENGTH * (i + 2));
				}
			}
			for (String s : titlePartsList)
			{
				inboxStyle.addLine(s);
			}
			builder.setStyle(inboxStyle);

			int curPos = this.curArtTextListPosition;
			int size = this.curArtTextList.size() - 1;
			size = (size == 0) ? 1 : size;
			int percent = curPos * 100;
			percent = percent / size;
			builder.setSubText("Прочитано: " + percent + "%");
			builder.setContentTitle("Озвучивание статей " + String.valueOf(this.currentArtPosition + 1) + "/"
			+ this.artList.size());

			//Sets up the action buttons that will appear in the big view of the notification.
			if (this.isPaused)
			{
				Intent playIntent = new Intent(this, ServiceTTS.class);
				playIntent.setAction("play");
				PendingIntent piPlay = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				builder.addAction(R.drawable.ic_play_arrow_grey600_24dp, "", piPlay);
			}
			else
			{
				Intent pauseIntent = new Intent(this, ServiceTTS.class);
				pauseIntent.setAction("pause");
				PendingIntent piPause = PendingIntent.getService(this, 0, pauseIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
				builder.addAction(R.drawable.ic_pause_grey600_24dp, "", piPause);
			}

			Intent closeIntent = new Intent(this, ServiceTTS.class);
			closeIntent.setAction("close");
			PendingIntent piClose = PendingIntent.getService(this, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.addAction(R.drawable.ic_launcher, "", piClose);
		}
		// Displays the progress bar for the first time.
		//		mNotifyManager.notify(NOTIFICATION_TTS_ID, builder.build());
		return builder;
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

	private ArrayList<String> createArtTextListFromArticle(Article article)
	{
		ArrayList<String> artTextAsList = new ArrayList<String>();

		TextView tv = new TextView(this);
		tv.setText(Html.fromHtml(article.getArtText()));
		String articlesTextWithoutHtml = tv.getText().toString();

		int numOfParts = (articlesTextWithoutHtml.length() / MAX_TTS_STRING_LENGTH) + 1;
		int startChar = 0;
		int endChar = MAX_TTS_STRING_LENGTH;
		//		Log.d(LOG, "startChar/endChar: " + startChar + "/" + endChar);
		for (int i = 0; i < numOfParts; i++)
		{
			//check if it's last iteration
			if (i == numOfParts - 1)
			{
				String part = articlesTextWithoutHtml.substring(startChar, articlesTextWithoutHtml.length());
				//				Log.d(LOG, "part: " + part);
				artTextAsList.add(part);
				//				Log.d(LOG, "startChar/endChar: " + startChar + "/" + String.valueOf(articlesTextWithoutHtml.length()));
			}
			else
			{
				String part = articlesTextWithoutHtml.substring(startChar, endChar);
				//				Log.d(LOG, "part: " + part);
				artTextAsList.add(part);
				startChar = MAX_TTS_STRING_LENGTH * (i + 1);
				endChar = (MAX_TTS_STRING_LENGTH * (i + 2));

				//				Log.d(LOG, "startChar/endChar: " + startChar + "/" + endChar);
			}
		}

		return artTextAsList;
	}
}