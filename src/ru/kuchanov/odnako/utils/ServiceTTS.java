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
import ru.kuchanov.odnako.db.Msg;
import ru.kuchanov.odnako.fragments.FragmentArticle;
import ru.kuchanov.odnako.lists_and_utils.Actions;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class ServiceTTS extends Service implements TextToSpeech.OnInitListener
{
	final private static String LOG = ServiceTTS.class.getSimpleName() + "/";

	public final static int NOTIFICATION_TTS_ID = 99;

	public final static int MAX_TTS_STRING_LENGTH = 250;
	public final static int MAX_TITLE_LENGTH = 30;

	private Context ctx;

	private NotificationManager mNotifyManager;

	private ArrayList<Article> artList;
	private int currentArtPosition = 0;
	private ArrayList<String> curArtTextList = new ArrayList<String>();
	private int curArtTextListPosition = 0;

	private TextToSpeech mTTS;

	private boolean isPaused = true;

	protected int askToClose = 0;

	@Override
	public void onCreate()
	{
		Log.d(LOG, "onCreate");
		super.onCreate();

		this.ctx = this;

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
			//Log.i(LOG, "onStart");
		}

		@Override
		@Deprecated
		public void onError(String utteranceId)
		{
			//Log.e(LOG, "onError");
		}

		@Override
		public void onDone(String utteranceId)
		{
			//Log.i(LOG, "onDone");
			onCompletePartReading();
			mNotifyManager.notify(NOTIFICATION_TTS_ID, getNotification().build());
		}
	};

	OnUtteranceCompletedListener oUCL = new OnUtteranceCompletedListener()
	{
		@Override
		public void onUtteranceCompleted(String utteranceId)
		{
			//Log.i(LOG, "onUtteranceCompleted");
			onCompletePartReading();
			mNotifyManager.notify(NOTIFICATION_TTS_ID, getNotification().build());
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		String action = intent.getAction();
		if (action == null)
		{
			Log.i(LOG, "action = null");
			this.mTTS.stop();
			this.mTTS.shutdown();
			this.mTTS = null;
			this.stopForeground(true);
			this.stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}

		try
		{
			switch (action)
			{
				case "init":
					Log.i(LOG, "init");
					ArrayList<Article> artList = intent.getParcelableArrayListExtra(FragmentArticle.ARTICLE_URL);

					this.artList = artList;
					this.currentArtPosition = intent.getIntExtra("position", 0);

					this.curArtTextList = this.createArtTextListFromArticle(this.artList.get(this.currentArtPosition));
					this.curArtTextListPosition = 0;

					this.isPaused = true;

					this.startForeground(NOTIFICATION_TTS_ID, this.getNotification().build());

					//And start playing
					Intent playIntent = new Intent(this, ServiceTTS.class);
					playIntent.setAction("play");
					this.startService(playIntent);
				break;
				case "play":
					Log.i(LOG, "play");
					this.isPaused = false;
					mNotifyManager.notify(NOTIFICATION_TTS_ID, this.getNotification().build());
					//if article is not loaded, notif shows progress and starts to download it
					//so check it and speek only if we have text
					if (!this.artList.get(currentArtPosition).getArtText().equals(Const.EMPTY_STRING))
					{
						this.speekPart();
					}
				break;
				case "pause":
					Log.i(LOG, "pause");
					this.isPaused = true;
					mTTS.stop();

					mNotifyManager.notify(NOTIFICATION_TTS_ID, this.getNotification().build());
				break;
				case "forward":
					Log.i(LOG, "forward");
					if (this.currentArtPosition != this.artList.size() - 1)
					{
						this.isPaused = true;
						mTTS.stop();
						this.currentArtPosition++;
						this.curArtTextList = this.createArtTextListFromArticle(this.artList
						.get(this.currentArtPosition));
						this.curArtTextListPosition = 0;
						mNotifyManager.notify(NOTIFICATION_TTS_ID, this.getNotification().build());
					}
					else
					{
						Toast.makeText(this, "Дальше не выйдет - это последняя статья в списке!", Toast.LENGTH_SHORT)
						.show();
					}
				break;
				case "rewind":
					Log.i(LOG, "rewind");
					if (this.currentArtPosition != 0)
					{
						this.isPaused = true;
						mTTS.stop();
						this.currentArtPosition--;
						this.curArtTextList = this.createArtTextListFromArticle(this.artList
						.get(this.currentArtPosition));
						this.curArtTextListPosition = 0;
						mNotifyManager.notify(NOTIFICATION_TTS_ID, this.getNotification().build());
					}
					else
					{
						Toast.makeText(this, "Дальше не выйдет - это первая статья в списке!", Toast.LENGTH_SHORT)
						.show();
					}
				break;
				case "close":
					Log.i(LOG, "close");
					this.mTTS.stop();
					this.mTTS.shutdown();
					this.mTTS = null;
					
					mNotifyManager.cancel(NOTIFICATION_TTS_ID);
					
					this.stopForeground(true);
					this.stopSelf();
				break;
				case "restore":
					Log.i(LOG, "restore");
					mNotifyManager.notify(NOTIFICATION_TTS_ID, this.getNotification().build());
				break;
				case "askToClose":
					Log.i(LOG, "askToClose");

					if (askToClose == 0)
					{
						askToClose++;
						Toast.makeText(ctx, "Нажмите ещё раз, чтобы прекратить озвучивание статей", Toast.LENGTH_SHORT)
						.show();
					}
					else
					{
						this.mTTS.stop();
						this.mTTS.shutdown();
						this.mTTS = null;
						
						mNotifyManager.cancel(NOTIFICATION_TTS_ID);
						
						this.stopForeground(true);
						this.stopSelf();
					}

					//Обнуление счётчика через 5 секунд
					final Handler handler = new Handler();
					handler.postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							// Do something after 7s = 7000ms
							askToClose = 0;
						}
					}, 7000);
				break;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			Toast
			.makeText(ctx, "Произошла какая-то жуткая ошибка при попытке озвучить статью... =(",
			Toast.LENGTH_SHORT).show();
			
			this.mTTS.stop();
			this.mTTS.shutdown();
			this.mTTS = null;
			
			mNotifyManager.cancel(NOTIFICATION_TTS_ID);
			
			this.stopForeground(true);
			this.stopSelf();
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
					//update notification to "paused" state;
					this.mNotifyManager.notify(NOTIFICATION_TTS_ID, this.getNotification().build());
				}
				else
				{
					this.currentArtPosition++;
					this.curArtTextListPosition = 0;
					this.mNotifyManager.notify(NOTIFICATION_TTS_ID, this.getNotification().build());
					//check if next article has text and speek it
					if (!this.artList.get(currentArtPosition).getArtText().equals(Const.EMPTY_STRING))
					{
						this.curArtTextList = this.createArtTextListFromArticle(this.artList.get(currentArtPosition));
						//update notif and play new article
						this.speekPart();
					}
				}
			}
			else
			{
				//we are still reading article
				this.curArtTextListPosition++;
				this.mNotifyManager.notify(NOTIFICATION_TTS_ID, this.getNotification().build());
				this.speekPart();
			}
		}
	}

	private void speekPart()
	{
		String text = this.curArtTextList.get(this.curArtTextListPosition);
		//		Log.i(LOG, text);

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

	private BroadcastReceiver articleReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.e(LOG, "articleReceiver onReceive");

			try
			{
				if (intent.getExtras().containsKey(Msg.ERROR))
				{
					//set to paused state, update notif to default state and Toast error
					isPaused = true;
					mNotifyManager.notify(NOTIFICATION_TTS_ID, getNotification().build());
					Toast.makeText(ctx, intent.getStringExtra(Msg.ERROR), Toast.LENGTH_SHORT).show();
				}
				else
				{
					Article a = intent.getParcelableExtra(Article.KEY_CURENT_ART);
					artList.get(currentArtPosition).setArtText(a.getArtText());
					curArtTextList = createArtTextListFromArticle(artList.get(currentArtPosition));
					mNotifyManager.notify(NOTIFICATION_TTS_ID, getNotification().build());
					if (!isPaused)
					{
						speekPart();
					}
					LocalBroadcastManager.getInstance(ctx).unregisterReceiver(articleReceiver);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	};

	private NotificationCompat.Builder getNotification()
	{
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.ic_play_arrow_grey600_24dp);
		builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_play_arrow_grey600_48dp));

		//builder.setAutoCancel(true);
		//Close button
		Intent closeIntent = new Intent(this, ServiceTTS.class);
		closeIntent.setAction("close");
		PendingIntent piClose = PendingIntent.getService(this, 0, closeIntent,
		PendingIntent.FLAG_UPDATE_CURRENT);

		Intent askToCloseIntent = new Intent(this, ServiceTTS.class);
		askToCloseIntent.setAction("askToClose");
		PendingIntent piAskToClose = PendingIntent.getService(this, 0, askToCloseIntent,
		PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(piAskToClose);
		//builder.setDeleteIntent(piClose);

		Article curArt = this.artList.get(currentArtPosition);
		if (!isPaused && curArt.getArtText().equals(Const.EMPTY_STRING))
		{
			//load article and show progress
			//We must create progress notification
			builder.setContentTitle(curArt.getTitle());
			builder.setContentText("Загружаю...");
			builder.setProgress(0, 0, true);

			//here we must register receiver for cur art url and start to load it;
			//in onReceive we must unregister receiver, update artList with artText,
			//fill articles parts and if(!isPaused) start to speekText();
			LocalBroadcastManager.getInstance(this).unregisterReceiver(articleReceiver);
			LocalBroadcastManager.getInstance(this)
			.registerReceiver(articleReceiver, new IntentFilter(curArt.getUrl()));

			Actions.startDownLoadArticle(curArt.getUrl(), ctx, false);
		}
		else
		{
			NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
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
				inboxStyle.addLine(Html.fromHtml(s));
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

			//rewind btn
			if (this.currentArtPosition == 0)
			{
				//Close button
				builder.addAction(R.drawable.ic_highlight_remove_grey600_48dp, "", piClose);
			}
			else
			{
				Intent rewindIntent = new Intent(this, ServiceTTS.class);
				rewindIntent.setAction("rewind");
				PendingIntent piRewind = PendingIntent.getService(this, 0, rewindIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
				builder.addAction(R.drawable.ic_fast_rewind_grey600_48dp, "", piRewind);
			}
			//Play/pause btn
			if (this.isPaused)
			{
				Intent playIntent = new Intent(this, ServiceTTS.class);
				playIntent.setAction("play");
				PendingIntent piPlay = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				builder.addAction(R.drawable.ic_play_arrow_grey600_48dp, "", piPlay);
			}
			else
			{
				Intent pauseIntent = new Intent(this, ServiceTTS.class);
				pauseIntent.setAction("pause");
				PendingIntent piPause = PendingIntent.getService(this, 0, pauseIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
				builder.addAction(R.drawable.ic_pause_grey600_48dp, "", piPause);
			}

			if (this.currentArtPosition == this.artList.size() - 1)
			{
				//Close button
				builder.addAction(R.drawable.ic_highlight_remove_grey600_48dp, "", piClose);
			}
			else
			{
				//forward btn
				Intent forwardIntent = new Intent(this, ServiceTTS.class);
				forwardIntent.setAction("forward");
				PendingIntent piForward = PendingIntent.getService(this, 0, forwardIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
				builder.addAction(R.drawable.ic_fast_forward_grey600_48dp, "", piForward);
			}
		}
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
		Log.i(LOG, "TTS on init");
		if (status == TextToSpeech.SUCCESS)
		{
			Locale locale = new Locale("ru");

			int result = mTTS.setLanguage(locale);

			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
			{
				Log.e("TTS", "Извините, этот язык не поддерживается");
				Toast
				.makeText(
				ctx,
				"Озвучивание на русском не поддерживается на сём устройстве. Установите синтез речи в настройках телефона",
				Toast.LENGTH_SHORT).show();

				Log.i(LOG, "close");
				this.mTTS.stop();
				this.mTTS.shutdown();
				this.mTTS = null;
				
				mNotifyManager.cancel(NOTIFICATION_TTS_ID);
				
				this.stopForeground(true);
				this.stopSelf();
			}

			if (!this.isPaused)
			{
				//And start playing
				Intent playIntent = new Intent(this, ServiceTTS.class);
				playIntent.setAction("play");
				this.startService(playIntent);
			}
		}
		else
		{
			Log.e("TTS", "Ошибка!");

			Toast
			.makeText(ctx, "Произошла какая-то жуткая ошибка при попытке озвучить статью... =(",
			Toast.LENGTH_SHORT).show();

			Log.i(LOG, "close");
			this.mTTS.stop();
			this.mTTS.shutdown();
			this.mTTS = null;
			this.stopForeground(true);
			this.stopSelf();
		}
	}

	private ArrayList<String> createArtTextListFromArticle(Article article)
	{
		ArrayList<String> artTextAsList = new ArrayList<String>();

		artTextAsList.add(Html.fromHtml(article.getTitle() + " \n").toString());

		TextView tv = new TextView(this);
		tv.setText(Html.fromHtml(article.getArtText()));
		String articlesTextWithoutHtml = tv.getText().toString();

		if (articlesTextWithoutHtml.length() <= MAX_TTS_STRING_LENGTH)
		{
			artTextAsList.add(articlesTextWithoutHtml);
			return artTextAsList;
		}
		while (articlesTextWithoutHtml.length() > MAX_TTS_STRING_LENGTH)
		{
			String maxPart = articlesTextWithoutHtml.substring(0, MAX_TTS_STRING_LENGTH);
			int lastSpaceIndexInPart = maxPart.lastIndexOf(" ");
			String part = maxPart.substring(0, lastSpaceIndexInPart);
			artTextAsList.add(part);
			articlesTextWithoutHtml = articlesTextWithoutHtml.replace(part, "");
		}
		if (articlesTextWithoutHtml.length() != 0)
		{
			artTextAsList.add(articlesTextWithoutHtml);
		}
		return artTextAsList;
	}

	@Override
	public void onDestroy()
	{
		Log.d(LOG, "onDestroy");
		if (this.mTTS != null)
		{
			this.mTTS.stop();
			this.mTTS.shutdown();
			this.mTTS = null;
			
			mNotifyManager.cancel(NOTIFICATION_TTS_ID);
		}
		if (articleReceiver != null)
		{
			LocalBroadcastManager.getInstance(ctx).unregisterReceiver(articleReceiver);
			articleReceiver = null;
		}
		super.onDestroy();
	}
}