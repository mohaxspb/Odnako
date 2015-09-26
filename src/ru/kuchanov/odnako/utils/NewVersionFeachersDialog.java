/*
 05.05.2015
NewVersionFeachersDialog.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityPreference;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.Html;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

public class NewVersionFeachersDialog
{
	public static void appLaunched(Context ctx)
	{
		PackageManager pm = ctx.getPackageManager();
		String app_ver = "";
		try
		{
			app_ver = pm.getPackageInfo(ctx.getPackageName(), 0).versionName;

			Log.i("app_ver", app_ver);

			SharedPreferences prefs = ctx.getSharedPreferences(app_ver, Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(ctx);
			if (prefs.getBoolean(ActivityPreference.PREF_KEY_FIRST_LAUNCH, false) == true)
			{
				return;
			}
			else
			{
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean(ActivityPreference.PREF_KEY_FIRST_LAUNCH, true);
				editor.commit();
				showNewVersionDialog(ctx);
			}

		} catch (NameNotFoundException e)
		{
			throw new AssertionError();
		}
	}

	public static void showNewVersionDialog(final Context ctx)
	{
		PackageManager pm = ctx.getPackageManager();
		String app_ver = "";
		try
		{
			app_ver = pm.getPackageInfo(ctx.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e)
		{
			throw new AssertionError();
		}

		String message;
		if ("3.35.arctic.hotfix".equals(app_ver))
		{
			message = ctx.getResources().getStringArray(R.array.version_history_arr)[0];
			message += ctx.getResources().getStringArray(R.array.version_history_arr)[1];
		}
		else
		{
			message = ctx.getResources().getStringArray(R.array.version_history_arr)[0];
		}

		MaterialDialog dialognewVersion;
		MaterialDialog.Builder dialognewVersionBuilder = new MaterialDialog.Builder(ctx);
		dialognewVersionBuilder.title(ctx.getString(R.string.app_name) + ", версия " + app_ver)
		.content(Html.fromHtml(message))
		.positiveText("Ура!");
		dialognewVersion = dialognewVersionBuilder.build();

		dialognewVersion.show();
	}
}