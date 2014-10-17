package ru.kuchanov.odnako.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class NewVersionFeachersDialog
{
	private final static String APP_TITLE = "Однако";
	static String app_ver = "";

	public static void app_launched(Context mContext)
	{
		PackageManager m = mContext.getPackageManager();
		try
		{
			app_ver = m.getPackageInfo(mContext.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e)
		{
			// Exception won't be thrown as the current package name is
			// safe to exist on the system.
			throw new AssertionError();
		}

		SharedPreferences prefs = mContext.getSharedPreferences("new_version_"+app_ver, 0);
		if (prefs.getBoolean("dontshowagain", false))
		{
			return;
		}
		else
		{
			SharedPreferences.Editor editor = prefs.edit();
			showRateDialog(mContext, editor);
			editor.commit();
		}
	}

	public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor)
	{
		Dialog dialog = new Dialog(mContext);

		////
		PackageManager m = mContext.getPackageManager();
		String app_ver = "";
		try
		{
			app_ver = m.getPackageInfo(mContext.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e)
		{
			// Exception won't be thrown as the current package name is
			// safe to exist on the system.
			throw new AssertionError();
		}
		////
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		///Message is main text in dialog, version feachers
		String message = "Новые возможности:\n"+
		"-Добавлен поиск по заголовкам статей и именам авторов(в списке всех авторов).\n"+
		"-Теперь новые и прочитанные статьи обозначены специальным значком.\n"+
		"Улучшения дизайна:\n"+
		"-Теперь доступ к списку всех статей автора с экрана статьи доступен по нажатию на кнопку справа от его имени.\n"+
		"-Сохранённые статьи теперь отображаются цветом соответствующей иконки, а не её наличием.\n"+
		"-Изменён экран настроек.\n";

		builder.setMessage(message).setTitle(APP_TITLE + ", версия " + app_ver).setIcon(mContext.getApplicationInfo().icon).setCancelable(false)
		.setPositiveButton("Ура!", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				editor.putBoolean("dontshowagain", true);
				editor.commit();
				dialog.dismiss();
			}
		});
		dialog = builder.create();

		dialog.show();
	}

}
// see http://androidsnippets.com/prompt-engaged-users-to-rate-your-app-in-the-android-market-appirater