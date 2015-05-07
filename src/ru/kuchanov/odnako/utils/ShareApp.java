/*
 08.05.2015
ShareApp.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import ru.kuchanov.odnako.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

public class ShareApp
{
	private final static String APP_TITLE = "Однако";
	private final static int DAYS_UNTIL_PROMPT = 10;

	public static void appLaunched(Context ctx)
	{
		SharedPreferences prefs = ctx.getSharedPreferences("share_app", 0);
		SharedPreferences.Editor editor = prefs.edit();

		//Get Date of first launch
		Long date_firstLaunch = prefs.getLong("date_first_launch", 0);
		if (date_firstLaunch == 0)
		{
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong("date_first_launch", date_firstLaunch);
			editor.commit();
		}

		//Wait at least X days to launch
		if (System.currentTimeMillis() >= date_firstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000))
		{
			showRateDialog(ctx);
		}
	}

	public static void showRateDialog(final Context ctx)
	{
		SharedPreferences prefs = ctx.getSharedPreferences("share_app", 0);
		final SharedPreferences.Editor editor = prefs.edit();

		MaterialDialog dialogGoPro;
		MaterialDialog.Builder dialogGoProBuilder = new MaterialDialog.Builder(ctx);

		String message = "Если вам нравится " + APP_TITLE
		+ ", пожалуйста расскажите о приложении вашим друзьям. Спасибо вам за поддержку! =)";

		dialogGoProBuilder.title("Поделиться приложением")
		.content(message)
		.positiveText("Конечно!")
		.negativeText("Не сейчас")
		.callback(new MaterialDialog.ButtonCallback()
		{
			@Override
			public void onPositive(MaterialDialog dialog)
			{
				Long dateFirstLaunch = System.currentTimeMillis();
				editor.putLong("date_first_launch", dateFirstLaunch);
				editor.commit();
				
				final Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				String webLinkToApp = "http://play.google.com/store/apps/details?id=ru.kuchanov.odnako";
				intent.putExtra(Intent.EXTRA_TEXT, webLinkToApp);
				try
				{
					ctx.startActivity(Intent.createChooser(intent, "Поделиться через:"));
				} catch (android.content.ActivityNotFoundException ex)
				{
					Toast.makeText(ctx.getApplicationContext(), "Что-то пошло не так... =(", Toast.LENGTH_SHORT)
					.show();
				}
			}

			@Override
			public void onNegative(MaterialDialog dialog)
			{
				Long dateFirstLaunch = System.currentTimeMillis() - (DAYS_UNTIL_PROMPT - 3 * 24 * 60 * 60 * 1000);
				editor.putLong("date_first_launch", dateFirstLaunch);
				editor.commit();
			}
		});

		//set themeDependedIconsIDs
		int[] attrs = new int[] { R.attr.shareIcon };
		TypedArray ta = ctx.obtainStyledAttributes(attrs);
		int themeIconId = ta.getResourceId(0, R.attr.shareIcon);
		ta.recycle();

		dialogGoProBuilder.iconRes(themeIconId);

		dialogGoPro = dialogGoProBuilder.build();
		dialogGoPro.show();
	}
}