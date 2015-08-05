/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import ru.kuchanov.odnako.R;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

public class FragmentDialogTextAppearance extends DialogFragment
{
	final static String LOG = FragmentDialogTextAppearance.class.getSimpleName();

	private Context ctx;

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
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Log.i(LOG, "onCreateDialog");

		int[] attr = new int[] { R.attr.textSizeIcon };
		TypedArray a = ctx.getTheme().obtainStyledAttributes(attr);
		final int textSizeIconId = a.getResourceId(0, 0);
		a.recycle();

		attr = new int[] { R.attr.textFormatIcon };
		a = ctx.getTheme().obtainStyledAttributes(attr);
		final int textFormatIconId = a.getResourceId(0, 0);
		a.recycle();

		attr = new int[] { R.attr.commentsIcon };
		a = ctx.getTheme().obtainStyledAttributes(attr);
		final int commentsIconId = a.getResourceId(0, 0);
		a.recycle();

		final MaterialDialog dialogTextSize;

		MaterialDialog.Builder dialogTextSizeBuilder = new MaterialDialog.Builder(ctx);
		dialogTextSizeBuilder.title("Настройки размера текста")
		.positiveText("Закрыть")
		.customView(R.layout.dialog_easter_egg, true);

		dialogTextSize = dialogTextSizeBuilder.build();

		final SeekBar seekbar = (SeekBar) dialogTextSize.getCustomView().findViewById(R.id.seekbar);
		final TextView value = (TextView) dialogTextSize.getCustomView().findViewById(R.id.value);
		final ImageView downloadBtn = (ImageView) dialogTextSize.getCustomView().findViewById(R.id.download);
		downloadBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

			}
		});
		final ImageView playBtn = (ImageView) dialogTextSize.getCustomView().findViewById(R.id.play_pause);
		playBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

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
				//TODO
			}
		});

		return dialogTextSize;
	}
}