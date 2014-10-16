package ru.kuchanov.odnako;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

public class PrefActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
	PreferenceActivity act;

	CheckBoxPreference allAdsCheckBox;
	
	static String APP_PNAME;
	static String APP_TITLE;

	final static String LINK_TO_PRO = "ru.kuchanov.odnakopro";

	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		this.act = this;
		APP_TITLE = act.getResources().getString(R.string.app_name);

		PreferenceManager.setDefaultValues(this, R.xml.pref, false);
		SharedPreferences pref;
		pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if (pref.getString("theme", "dark").equals("dark"))
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			{
				this.setTheme(android.R.style.Theme_Holo);
			}
			else
			{
				this.setTheme(android.R.style.Theme_Black);
			}
		}
		else
		{
			this.setTheme(R.style.Theme_AppCompat_Light);
		}

		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.pref);
		///ads prefs
		allAdsCheckBox = (CheckBoxPreference) findPreference("adsOn");
		allAdsCheckBox.setOnPreferenceClickListener(onOfAllAds);
		//end of ads prefs

		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

		//link_to_market
		APP_PNAME = this.getApplicationInfo().packageName;
		Preference prefereces = findPreference("link_to_market");
		prefereces.setOnPreferenceClickListener(linkToMarket);

		//link_to_pro
		Preference preferecesPRO = findPreference("link_to_pro");
		preferecesPRO.setOnPreferenceClickListener(linkToPro);

		//share app
		Preference preferencesSHARE = findPreference("share_app");
		preferencesSHARE.setOnPreferenceClickListener(shareApp);

		//mail to me
		Preference prefMailToMe = findPreference("mail_to_me");
		prefMailToMe.setOnPreferenceClickListener(mailToMeCL);

		//mail to me
		Preference prefVersionHistory = findPreference("version_history");
		prefVersionHistory.setOnPreferenceClickListener(this.versionHistoryCL);

	}

	protected OnPreferenceClickListener linkToMarket = new OnPreferenceClickListener()
	{

		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			try
			{
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
			} catch (Exception e)
			{
				String marketErrMsg="Должен был запуститься Play Market, но что-то пошло не так...";
				System.out.println(marketErrMsg);
				Toast.makeText(act, marketErrMsg, Toast.LENGTH_SHORT).show();
			}
			return false;
		}
	};

	protected OnPreferenceClickListener linkToPro = new OnPreferenceClickListener()
	{

		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			try
			{
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + LINK_TO_PRO)));
			} catch (Exception e)
			{
				String marketErrMsg="Должен был запуститься Play Market, но что-то пошло не так...";
				System.out.println(marketErrMsg);
				Toast.makeText(act, marketErrMsg, Toast.LENGTH_SHORT).show();
			}
			return false;
		}
	};

	protected OnPreferenceClickListener shareApp = new OnPreferenceClickListener()
	{

		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			final Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			String webLinkToApp = "http://play.google.com/store/apps/details?id=ru.kuchanov.odnako";
			intent.putExtra(Intent.EXTRA_TEXT, webLinkToApp);
			try
			{
				startActivity(Intent.createChooser(intent, "Поделиться статьёй"));
			} catch (android.content.ActivityNotFoundException ex)
			{
				Toast.makeText(getApplicationContext(), "Ошибка! =(", Toast.LENGTH_SHORT).show();
			}
			return false;
		}
	};

	protected OnPreferenceClickListener mailToMeCL = new OnPreferenceClickListener()
	{

		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			//EmailSending
			Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "mohax.spb@gmail.com", null));
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "От пользователя приложения \"Однако\"");
			startActivity(Intent.createChooser(emailIntent, "Написать письмо..."));
			////
			return false;
		}
	};

	protected OnPreferenceClickListener onOfAllAds = new OnPreferenceClickListener()
	{
		public boolean onPreferenceClick(Preference preference)
		{
			if (!allAdsCheckBox.isChecked())
			{
				Toast.makeText(getApplicationContext(), "Эх, не видать разработчику Крыма!.. ;-(", Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Спасибо за поддержку! И за вклад в развитие туристического сектора экономики Крыма! =)", Toast.LENGTH_LONG).show();
			}
			return false;
		}
	};

//	protected OnPreferenceClickListener askingForAds = new OnPreferenceClickListener()
//	{
//
//		@Override
//		public boolean onPreferenceClick(Preference preference)
//		{
//			boolean allUnChecked = false;
//			if (!main.isChecked() && !art.isChecked() && !comm.isChecked() && !down.isChecked())
//			{
//				allUnChecked = true;
//				CheckBoxPreference preference1 = (CheckBoxPreference) preference;
//				preference1.setChecked(allUnChecked);
//				Toast.makeText(getApplicationContext(), "Хотя бы что-то одно оставьте, пожалуйста! =)", Toast.LENGTH_LONG).show();
//			}
//			return false;
//		}
//	};

	protected OnPreferenceClickListener versionHistoryCL = new OnPreferenceClickListener()
	{

		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			Dialog dialog = new Dialog(act);

			////
			PackageManager m = act.getPackageManager();
			String app_ver = "";
			try
			{
				app_ver = m.getPackageInfo(act.getPackageName(), 0).versionName;
			} catch (NameNotFoundException e)
			{
				// Exception won't be thrown as the current package name is
				// safe to exist on the system.
				throw new AssertionError();
			}
			////
			AlertDialog.Builder builder = new AlertDialog.Builder(act);
			//set TV to message
			final TextView messageTV = new TextView(act);
			System.out.println(act.getResources().getString(R.string.version_history));
//			Spanned message = Html.fromHtml(act.getResources().getString(R.string.version_history));
			String message = act.getResources().getString(R.string.version_history);
			System.out.println(message);
			messageTV.setText(message);

			builder
			.setMessage(message)
			//.setView(messageTV)
			.setTitle(APP_TITLE + ", версия " + app_ver)
			.setIcon(act.getApplicationInfo().icon)
			.setCancelable(true)
			.setPositiveButton("Это было интересно!", new DialogInterface.OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
			dialog = builder.create();

			dialog.show();
			return false;
		}
	};

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		///Запускаем\ отключаем сервис

		if (key.equals("notification"))
		{
			boolean notifOn = sharedPreferences.getBoolean(key, false);

			if (notifOn)
			{
				Intent serviceIntent = new Intent(this, ru.kuchanov.odnako.onboot.CheckNewService.class);
				startService(serviceIntent);
			}
			else
			{
				Intent serviceIntent = new Intent(this, ru.kuchanov.odnako.onboot.CheckNewService.class);
				stopService(serviceIntent);
			}
		}
	}
}