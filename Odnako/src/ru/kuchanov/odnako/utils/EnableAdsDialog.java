package ru.kuchanov.odnako.utils;

import ru.kuchanov.odnako.download.Downloadings;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

public class EnableAdsDialog
{
	private final static String APP_TITLE = "Однако";

	private final static int DAYS_UNTIL_PROMPT = 7;

	public static void app_launched(Context mContext)
	{
		System.out.println("EnableAdsDialog.app_launched(Context mContext)");
		
		//return if it's PRO version
		//So won't show any dialogs and change any settings
		if(Downloadings.IS_PRO)
		{
			System.out.println("it's PRO version, so won't show any dialogs and change any settings");
			return;
		}

		SharedPreferences prefsShowAds = mContext.getSharedPreferences("showAds", 0);
		SharedPreferences.Editor editor = prefsShowAds.edit();
		//Get Date of first launch
		Long date_firstLaunch = prefsShowAds.getLong("date_first_launch", 0);
		if (date_firstLaunch == 0)
		{
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong("date_first_launch", date_firstLaunch);
		}

		SharedPreferences prefs = mContext.getSharedPreferences("new_version", 0);
		if (prefs.getBoolean("dontshowagain", false))
		{
			System.out.println("new_version exists, so it's old's user Device!");
			System.out.println("Let's check if it's a first time launch...");
			//System.out.println("As so we will show him dialog, where ask him if he want some ads)))");

			//check if it's first time
			if (prefsShowAds.getBoolean("firstTime", true))
			{
				System.out.println("As so we will show him dialog, where ask him if he want some ads)))");
				System.out.println("But firstly, we'll turn all ads OFF");
				//turn ads off, because it's an old user, that launch this version for the first time
				SharedPreferences.Editor prefToOffAdsEditor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
//				prefToOffAdsEditor.putBoolean("adsOnMain", false);
//				prefToOffAdsEditor.putBoolean("adsOnArticle", false);
//				prefToOffAdsEditor.putBoolean("adsOnComments", false);
//				prefToOffAdsEditor.putBoolean("adsOnDownloadings", false);
				prefToOffAdsEditor.putBoolean("adsOn", false);
				prefToOffAdsEditor.commit();
				//END OF turn ads off, because it's an old user, that launch this version for the first time
				showAdsOnDialog(mContext, editor);
				editor.putBoolean("firstTime", false);
				editor.commit();
			}
			else
			{
				System.out.println("It's not a first time, so check if ads is ON");
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
				if (pref.getBoolean("adsOn", false))
				{
					System.out.println("ads is ON, so do noting");
					//return;
				}
				else
				{
					System.out.println("ads is OFF, so check if it's time to show new dialog");
					if (System.currentTimeMillis() >= date_firstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000))
					{
						System.out.println("It's time to show new dialog!");
						showAdsOnDialog(mContext, editor);
						editor.commit();
					}
					else
					{
						System.out.println("It's NOT time to show new dialog!");
					}
				}

			}

		}
		else
		{
			System.out.println("new_version isn't exists, so it a first launch or app's data was cleared!");
			System.out.println("As so we will show him all Ads!!! AHAHA!!!11");
			return;
		}
		//end of Ads option in this version

		editor.commit();
	}

	public static void showAdsOnDialog(final Context mContext, final SharedPreferences.Editor editor)
	{
		Dialog dialog = new Dialog(mContext);

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		String message = "Если вам нравится "
		+ APP_TITLE
		+ ", пожалуйста включите рекламу в приложении (см. 'Поддержать разработчика' в Настройках). Включив её вы получите возможность загружать не только 5-ть последних статей из 'Ленты обновлений', но ещё две категории и 10-ть статей станут доступны для загрузки! Спасибо вам за поддержку! =)";
		builder.setMessage(message).setTitle("Поддержать разработчика").setIcon(mContext.getApplicationInfo().icon).setCancelable(false)
		/*.setPositiveButton("Открыть настройки", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Intent intent = new Intent(mContext, PrefActivity.class);
				mContext.startActivity(intent);
				dialog.dismiss();
			}
		})*/.setPositiveButton("Включить рекламу", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						SharedPreferences.Editor prefToOffAdsEditor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
						prefToOffAdsEditor.putBoolean("adsOn", true);
						prefToOffAdsEditor.commit();
						dialog.dismiss();
					}
				}).setNeutralButton("Купить полную версию", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				final String linkToPro = "ru.kuchanov.odnakopro";
				try
				{
					mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + linkToPro)));
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				dialog.dismiss();

			}
		}).setNegativeButton("Может позже...", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				//date first launch
				Long date_firstLaunch;
				date_firstLaunch = System.currentTimeMillis();
				editor.putLong("date_first_launch", date_firstLaunch);
				editor.commit();
				dialog.dismiss();
			}
		});
		dialog = builder.create();

		dialog.show();
	}
}
// see http://androidsnippets.com/prompt-engaged-users-to-rate-your-app-in-the-android-market-appirater