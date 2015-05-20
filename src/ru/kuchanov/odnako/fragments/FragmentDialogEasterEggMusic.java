/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.io.IOException;
import java.util.Calendar;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.utils.MyPlayer;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;

//import android.support.v4.app.DialogFragment;

public class FragmentDialogEasterEggMusic extends DialogFragment
{
	final static String LOG = FragmentDialogEasterEggMusic.class.getSimpleName();

	final static String KEY_ANSWER = "KEY_ANSWER";
	final static String KEY_IS_PAUSED = "KEY_IS_PAUSED";

	String[] answer;

	//	private AppCompatActivity act;
	private Context ctx;

	private Boolean isPaused;// = true;

	public static FragmentDialogEasterEggMusic newInstance(String[] answer)
	{
		FragmentDialogEasterEggMusic frag = new FragmentDialogEasterEggMusic();
		Bundle args = new Bundle();
		args.putStringArray(KEY_ANSWER, answer);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		Log.i(LOG, "onCreate");
		this.ctx = this.getActivity();

		Bundle args = this.getArguments();

		this.answer = args.getStringArray(KEY_ANSWER);

		if (savedState != null && savedState.containsKey(KEY_IS_PAUSED))
		{
			this.isPaused = savedState.getBoolean(KEY_IS_PAUSED, true);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		outState.putBoolean(KEY_IS_PAUSED, isPaused);
		super.onSaveInstanceState(outState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Log.i(LOG, "onCreateDialog");

		char zeroSizeSpace = Character.toChars(65279)[0];
		answer[0] = answer[0].replaceAll(String.valueOf(zeroSizeSpace), "");
		final String songUrl = "http://kuchanov.ru/odnako/" + answer[0] + ".mp3";
		Log.e(LOG, "songUrl: " + songUrl);

		int[] attr = new int[] { R.attr.arrowPlayIcon };
		TypedArray a = ctx.getTheme().obtainStyledAttributes(attr);
		final int playIconId = a.getResourceId(0, 0);
		a.recycle();

		attr = new int[] { R.attr.pauseIcon };
		a = ctx.getTheme().obtainStyledAttributes(attr);
		final int pauseIconId = a.getResourceId(0, 0);
		a.recycle();

		final MediaPlayer mp = MyPlayer.getMediaPlayer();

		final MaterialDialog dialogPlayer;

		MaterialDialog.Builder dialogPlayerBuilder = new MaterialDialog.Builder(ctx);
		dialogPlayerBuilder.title(answer[1])
		.cancelable(false)
		.positiveText("Закрыть")
		.callback(new ButtonCallback()
		{
			@Override
			public void onPositive(MaterialDialog dialog)
			{
				mp.pause();
				mp.stop();
				//				mp.release();
			}
		})
		.customView(R.layout.easter_egg_dialog, false);

		dialogPlayer = dialogPlayerBuilder.build();

		try
		{
			final SeekBar seekbar = (SeekBar) dialogPlayer.getCustomView().findViewById(R.id.seekbar);
			final TextView value = (TextView) dialogPlayer.getCustomView().findViewById(R.id.value);
			final ImageView downloadBtn = (ImageView) dialogPlayer.getCustomView().findViewById(R.id.download);
			downloadBtn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(songUrl));
					startActivity(i);
				}
			});
			final ImageView playBtn = (ImageView) dialogPlayer.getCustomView().findViewById(R.id.play_pause);
			playBtn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (mp.isPlaying())
					{
						playBtn.setBackgroundResource(playIconId);
						mp.pause();
						isPaused = true;
					}
					else
					{
						playBtn.setBackgroundResource(pauseIconId);
						mp.start();
						isPaused = false;
					}
				}
			});
			seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
			{
				@Override
				public void onStopTrackingTouch(SeekBar seekBar)
				{
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar)
				{
				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
				{
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(progress);
					String seconds = (cal.get(Calendar.SECOND) < 10) ? "0" + cal.get(Calendar.SECOND) : String
					.valueOf(cal.get(Calendar.SECOND));
					String progressStr = cal.get(Calendar.MINUTE) + ":" + seconds + "/";
					cal.setTimeInMillis(seekBar.getMax());
					seconds = (cal.get(Calendar.SECOND) < 10) ? "0" + cal.get(Calendar.SECOND) : String
					.valueOf(cal.get(Calendar.SECOND));
					progressStr += cal.get(Calendar.MINUTE) + ":" + seconds;
					value.setText(progressStr);
					if (fromUser)
					{
						mp.seekTo(progress);
					}
				}
			});

			mp.setOnPreparedListener(new OnPreparedListener()
			{
				@Override
				public void onPrepared(MediaPlayer mp)
				{
					if (!mp.isPlaying())
					{
						seekbar.setProgress(0);
						seekbar.setMax(mp.getDuration());

						playBtn.setBackgroundResource(pauseIconId);

						dialogPlayer.show();

						mp.start();

						isPaused = false;
					}
					else
					{
						playBtn.setBackgroundResource(playIconId);

						mp.pause();
						isPaused = true;
					}
				}
			});
			mp.setOnBufferingUpdateListener(new OnBufferingUpdateListener()
			{
				@Override
				public void onBufferingUpdate(MediaPlayer mp, int percent)
				{
					Log.e(LOG, "BUFFERING_UPDATE!");
					if (mp.isPlaying())
					{
						playBtn.setBackgroundResource(pauseIconId);
					}
					else
					{
						playBtn.setBackgroundResource(playIconId);
					}

					seekbar.setMax(mp.getDuration());

					seekbar.setSecondaryProgress(mp.getDuration() / 100 * percent);

					seekbar.setProgress(mp.getCurrentPosition());

					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(seekbar.getProgress());
					String seconds = (cal.get(Calendar.SECOND) < 10) ? "0" + cal.get(Calendar.SECOND) : String
					.valueOf(cal.get(Calendar.SECOND));
					String progressStr = cal.get(Calendar.MINUTE) + ":" + seconds + "/";
					cal.setTimeInMillis(seekbar.getMax());
					seconds = (cal.get(Calendar.SECOND) < 10) ? "0" + cal.get(Calendar.SECOND) : String
					.valueOf(cal.get(Calendar.SECOND));
					progressStr += cal.get(Calendar.MINUTE) + ":" + seconds;
					value.setText(progressStr);
				}
			});

			if (mp.isPlaying())
			{
				Log.e(LOG, "mp.isPlaying()");

				seekbar.setProgress(mp.getCurrentPosition());

				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(seekbar.getProgress());
				String seconds = (cal.get(Calendar.SECOND) < 10) ? "0" + cal.get(Calendar.SECOND) : String
				.valueOf(cal.get(Calendar.SECOND));
				String progressStr = cal.get(Calendar.MINUTE) + ":" + seconds + "/";
				cal.setTimeInMillis(seekbar.getMax());
				seconds = (cal.get(Calendar.SECOND) < 10) ? "0" + cal.get(Calendar.SECOND) : String
				.valueOf(cal.get(Calendar.SECOND));
				progressStr += cal.get(Calendar.MINUTE) + ":" + seconds;
				value.setText(progressStr);
			}
			else
			{
				Log.e(LOG, "mp.isPlaying() IS FALSE");

				if (this.isPaused == null)
				{
					Log.e(LOG, "mp.getTrackInfo().length=0");
					mp.reset();
					mp.setDataSource(songUrl);
					mp.prepare();
				}
				else
				{
					Log.e(LOG, "mp.getTrackInfo().length!=0");
					playBtn.setBackgroundResource(playIconId);

					seekbar.setMax(mp.getDuration());
					seekbar.setProgress(mp.getCurrentPosition());

					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(seekbar.getProgress());
					String seconds = (cal.get(Calendar.SECOND) < 10) ? "0" + cal.get(Calendar.SECOND) : String
					.valueOf(cal.get(Calendar.SECOND));
					String progressStr = cal.get(Calendar.MINUTE) + ":" + seconds + "/";
					cal.setTimeInMillis(seekbar.getMax());
					seconds = (cal.get(Calendar.SECOND) < 10) ? "0" + cal.get(Calendar.SECOND) : String
					.valueOf(cal.get(Calendar.SECOND));
					progressStr += cal.get(Calendar.MINUTE) + ":" + seconds;
					value.setText(progressStr);
				}
			}
		} catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e)
		{
			e.printStackTrace();
		}

		return dialogPlayer;
	}
}