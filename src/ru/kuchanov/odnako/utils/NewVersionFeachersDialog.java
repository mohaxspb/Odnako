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
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.TypedValue;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;

public class NewVersionFeachersDialog
{
	private final static String APP_TITLE = "Однако";
	static String appVer = "";

	public static void appLaunched(Context ctx)
	{
		PackageManager pm = ctx.getPackageManager();
		try
		{
			appVer = pm.getPackageInfo(ctx.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e)
		{
			// Exception won't be thrown as the current package name is
			// safe to exist on the system.
			throw new AssertionError();
		}

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
//		.icon(ctx.getDrawable(ctx.getApplicationInfo().icon))
		.content(Html.fromHtml(message))
		.positiveText("Ура!");
		dialognewVersion = dialognewVersionBuilder.build();
		//getColor
		int[] textSizeAttr = new int[] { android.R.attr.textColorPrimary };
		int indexOfAttrTextSize = 0;
		TypedValue typedValue = new TypedValue();
		TypedArray a = ctx.obtainStyledAttributes(typedValue.data, textSizeAttr);
		int textColor = a.getColor(indexOfAttrTextSize, 0);
		a.recycle();
		((MDButton) dialognewVersion.getActionButton(DialogAction.POSITIVE)).setTextColor(textColor);

		dialognewVersion.show();
	}
}