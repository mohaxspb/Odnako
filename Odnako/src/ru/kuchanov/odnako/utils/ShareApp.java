package ru.kuchanov.odnako.utils;

import ru.kuchanov.odnako.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class ShareApp
{
	private final static String APP_TITLE = "Однако";
	//private static String APP_PNAME;

	private final static int DAYS_UNTIL_PROMPT = 5;

	private final static int LAUNCH_UNTIL_PROMPT = 8;

	public static void app_launched(Context mContext)
	{
		//APP_PNAME = mContext.getApplicationInfo().packageName;
		SharedPreferences prefs = mContext.getSharedPreferences("share_app", 0);
		if (prefs.getBoolean("dontshowagain", false))
		{
			return;
		}

		SharedPreferences.Editor editor = prefs.edit();

		//Add to launch Counter
		long launch_count = prefs.getLong("launch_count", 0) + 1;
		editor.putLong("launch_count", launch_count);

		//Get Date of first launch
		Long date_firstLaunch = prefs.getLong("date_first_launch", 0);
		if (date_firstLaunch == 0)
		{
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong("date_first_launch", date_firstLaunch);
		}

		//Wait at least X days to launch
		if (launch_count >= LAUNCH_UNTIL_PROMPT)
		{
			if (System.currentTimeMillis() >= date_firstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000))
			{
				showRateDialog(mContext, editor);
			}
		}

		editor.commit();

	}

	public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor)
	{
		Dialog dialog = new Dialog(mContext);

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		String message = "Если вам нравится " + APP_TITLE + ", пожалуйста расскажите о приложении вашим друзьям. Спасибо вам за поддержку! =)";
		builder.setMessage(message).setTitle("Поделиться " + APP_TITLE).setIcon(R.drawable.ic_action_share_for_ligth/*mContext.getApplicationInfo().icon*/).setCancelable(false)
		.setPositiveButton("Поделиться сейчас", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				editor.putBoolean("dontshowagain", true);
				editor.commit();
				//mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
				////
				final Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				String webLinkToApp="http://play.google.com/store/apps/details?id=ru.kuchanov.odnako";
				intent.putExtra(Intent.EXTRA_TEXT, webLinkToApp);
				try
				{
					mContext.startActivity(Intent.createChooser(intent, "Поделиться статьёй"));
				} catch (android.content.ActivityNotFoundException ex)
				{
					Toast.makeText(mContext.getApplicationContext(), "Ошибка! =(", Toast.LENGTH_SHORT).show();
				}
				////
				dialog.dismiss();
			}
		}).setNeutralButton("Напомнить позже", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				SharedPreferences prefs = mContext.getSharedPreferences("rate_app", 0);
				SharedPreferences.Editor editor = prefs.edit();
				//Add to launch Counter
				long launch_count = 0;
				editor.putLong("launch_count", launch_count);
				Long date_firstLaunch;
				date_firstLaunch = System.currentTimeMillis();
				editor.putLong("date_first_launch", date_firstLaunch);
				editor.commit();
				dialog.dismiss();

			}
		}).setNegativeButton("Не напоминать", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if (editor != null)
				{
					editor.putBoolean("dontshowagain", true);
					editor.commit();
				}
				dialog.dismiss();

			}
		});
		dialog = builder.create();

		dialog.show();
	}
}
// see http://androidsnippets.com/prompt-engaged-users-to-rate-your-app-in-the-android-market-appirater