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
import android.preference.PreferenceManager;
import android.text.Html;

import com.afollestad.materialdialogs.MaterialDialog;

public class NewVersionFeachersDialog
{
	private final static String APP_TITLE = "Однако";

	public static void appLaunched(Context ctx)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
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

		String message = ctx.getResources().getStringArray(R.array.version_history_arr)[0];

		MaterialDialog dialognewVersion;
		MaterialDialog.Builder dialognewVersionBuilder = new MaterialDialog.Builder(ctx);
		dialognewVersionBuilder.title(APP_TITLE + ", версия " + app_ver)
		.content(Html.fromHtml(message))
		.positiveText("Ура!");
		dialognewVersion = dialognewVersionBuilder.build();

		dialognewVersion.show();
	}
}