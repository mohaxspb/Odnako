package ru.kuchanov.odnako.fragments;

import ru.kuchanov.odnako.R;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;

public class FragmentPreferenceAbout extends PreferenceFragment
{
	private final static String LOG = FragmentPreferenceAbout.class.getSimpleName();

	private PreferenceActivity act;

	private static String APP_NAME;
	private static String APP_TITLE;

	public final static String LINK_TO_PRO = "ru.kuchanov.odnakopro";

	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);

		this.act = (PreferenceActivity) this.getActivity();

		APP_TITLE = act.getResources().getString(R.string.app_name);
		APP_NAME = this.act.getApplicationInfo().packageName;

		addPreferencesFromResource(R.xml.pref_about);

		//link_to_market
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

		//version_history
		Preference prefVersionHistory = findPreference("version_history");
		prefVersionHistory.setOnPreferenceClickListener(this.versionHistoryCL);

		//version_history
		Preference prefUsedLibs = findPreference("used_libs");
		prefUsedLibs.setOnPreferenceClickListener(this.usedLibsCL);
	}

	protected OnPreferenceClickListener linkToMarket = new OnPreferenceClickListener()
	{
		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			try
			{
				act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_NAME)));
			} catch (Exception e)
			{
				String marketErrMsg = "Должен был запуститься Play Market, но что-то пошло не так...";
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
			MaterialDialog dialogGoPro;
			MaterialDialog.Builder dialogGoProBuilder = new MaterialDialog.Builder(act);

			dialogGoProBuilder.title(R.string.go_pro_title)
			.content(Html.fromHtml(act.getResources().getString(R.string.pro_ver_adv)))
			.positiveText(R.string.go_pro_buy)
			.callback(new MaterialDialog.ButtonCallback()
			{
				@Override
				public void onPositive(MaterialDialog dialog)
				{
					try
					{
						act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
						+ LINK_TO_PRO)));
					} catch (Exception e)
					{
						String marketErrMsg = "Должен был запуститься Play Market, но что-то пошло не так...";
						Log.e(LOG, marketErrMsg);
						e.printStackTrace();
						Toast.makeText(act, marketErrMsg, Toast.LENGTH_SHORT).show();
					}
				}
			});
			dialogGoPro = dialogGoProBuilder.build();
			int textColor = act.getResources().getColor(R.color.black);
			((MDButton) dialogGoPro.getActionButton(DialogAction.POSITIVE)).setTextColor(textColor);
			dialogGoPro.getActionButton(DialogAction.POSITIVE).setBackgroundResource(R.drawable.md_btn_shape_green);
			dialogGoPro.show();
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
			String sendingApp = "Отправлено из приложения 'Однако. Новости и Аналитика' для Android.";
			intent.putExtra(Intent.EXTRA_TEXT, webLinkToApp + " " + sendingApp);
			intent.putExtra(Intent.EXTRA_TEXT, webLinkToApp);
			try
			{
				startActivity(Intent.createChooser(intent, "Поделиться приложением"));
			} catch (android.content.ActivityNotFoundException ex)
			{
				Toast.makeText(act, "Ошибка! =(", Toast.LENGTH_SHORT).show();
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

			return false;
		}
	};

	protected OnPreferenceClickListener versionHistoryCL = new OnPreferenceClickListener()
	{
		@Override
		public boolean onPreferenceClick(Preference preference)
		{
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

			String allVersionsDescriptionString;
			String[] allVerArr = act.getResources().getStringArray(R.array.version_history_arr);
			StringBuilder sb = new StringBuilder();
			for (String s : allVerArr)
			{
				sb.append(s);
			}
			allVersionsDescriptionString = sb.toString();

			MaterialDialog dialogVersionHistory;
			MaterialDialog.Builder dialogGoProBuilder = new MaterialDialog.Builder(act);
			dialogGoProBuilder.title(APP_TITLE + ", версия " + app_ver)
			.content(Html.fromHtml(allVersionsDescriptionString))
			.positiveText("Это было интересно!");
			dialogVersionHistory = dialogGoProBuilder.build();
			//getColor
			//			int[] textSizeAttr = new int[] { android.R.attr.textColorPrimary };
			//			int indexOfAttrTextSize = 0;
			//			TypedValue typedValue = new TypedValue();
			//			TypedArray a = act.obtainStyledAttributes(typedValue.data, textSizeAttr);
			//			int textColor = a.getColor(indexOfAttrTextSize, 0);
			//			a.recycle();
			//			((MDButton) dialogVersionHistory.getActionButton(DialogAction.POSITIVE)).setTextColor(textColor);

			dialogVersionHistory.show();

			return false;
		}
	};

	protected OnPreferenceClickListener usedLibsCL = new OnPreferenceClickListener()
	{
		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			MaterialDialog dialogVersionHistory;
			MaterialDialog.Builder dialogGoProBuilder = new MaterialDialog.Builder(act);
			dialogGoProBuilder.title("Список использованных в разработке библиотек")
			.content(Html.fromHtml(act.getResources().getString(R.string.used_libs)))
			.positiveText("Это может пригодится...");
			dialogVersionHistory = dialogGoProBuilder.build();

			dialogVersionHistory.show();

			return false;
		}
	};
}