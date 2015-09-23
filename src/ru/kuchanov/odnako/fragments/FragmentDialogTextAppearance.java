/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityPreference;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

public class FragmentDialogTextAppearance extends DialogFragment
{
	final static String LOG = FragmentDialogTextAppearance.class.getSimpleName();

	private Context ctx;

	SharedPreferences pref;

	public static FragmentDialogTextAppearance newInstance()
	{
		FragmentDialogTextAppearance frag = new FragmentDialogTextAppearance();
		return frag;
	}

	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		Log.i(LOG, "onCreate");
		this.ctx = this.getActivity();

		this.pref = PreferenceManager.getDefaultSharedPreferences(ctx);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Log.i(LOG, "onCreateDialog");

		final MaterialDialog dialogTextSize;

		MaterialDialog.Builder dialogTextSizeBuilder = new MaterialDialog.Builder(ctx);
		dialogTextSizeBuilder.title("Настройки размера текста")
		.positiveText(R.string.close)
		.customView(R.layout.dialog_text_size, true);

		dialogTextSize = dialogTextSizeBuilder.build();

		final SeekBar seekbarUI = (SeekBar) dialogTextSize.getCustomView().findViewById(R.id.seekbar_ui);
		final SeekBar seekbarArticle = (SeekBar) dialogTextSize.getCustomView().findViewById(R.id.seekbar_article);
		final SeekBar seekbarComments = (SeekBar) dialogTextSize.getCustomView().findViewById(R.id.seekbar_comments);
		final TextView tvUi = (TextView) dialogTextSize.getCustomView().findViewById(R.id.text_size_ui);
		final TextView tvArticle = (TextView) dialogTextSize.getCustomView().findViewById(R.id.text_size_article);
		final TextView tvComments = (TextView) dialogTextSize.getCustomView().findViewById(R.id.text_size_comments);

		seekbarUI.setMax(150);
		float scaleUI = pref.getFloat(ActivityPreference.PREF_KEY_SCALE_UI, 0.75f);
		int curProgressUI = (int) ((scaleUI - 0.50f) * 100);
		seekbarUI.setProgress(curProgressUI);
		seekbarUI.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
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
				//				Log.d(LOG, "progress = " + String.valueOf(progress));
				float size = (float) (progress / 100f) + 0.50f;
				//				Log.d(LOG, "size = " + String.valueOf(size));
				tvUi.setTextSize(size * 21);
				pref.edit().putFloat(ActivityPreference.PREF_KEY_SCALE_UI, size).commit();
			}
		});

		seekbarArticle.setMax(150);
		float scaleArt = pref.getFloat(ActivityPreference.PREF_KEY_SCALE_ARTICLE, 0.75f);
		int curProgressArt = (int) ((scaleArt - 0.50f) * 100);
		seekbarArticle.setProgress(curProgressArt);
		seekbarArticle.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
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
				float size = (float) (progress / 100f) + 0.50f;
				tvArticle.setTextSize(size * 21);
				pref.edit().putFloat(ActivityPreference.PREF_KEY_SCALE_ARTICLE, size).commit();
			}
		});

		seekbarComments.setMax(150);
		float scaleComments = pref.getFloat(ActivityPreference.PREF_KEY_SCALE_COMMENTS, 0.75f);
		int curProgressComm = (int) ((scaleComments - 0.50f) * 100);
		seekbarComments.setProgress(curProgressComm);
		seekbarComments.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
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
				float size = (float) (progress / 100f) + 0.50f;
				tvComments.setTextSize(size * 21);
				pref.edit().putFloat(ActivityPreference.PREF_KEY_SCALE_COMMENTS, size).commit();
			}
		});

		return dialogTextSize;
	}
}