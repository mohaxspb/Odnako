package ru.kuchanov.odnako.fragments;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityPreference;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;

public class FragmentPreferenceDesign extends PreferenceFragment
{
	final static String LOG = FragmentPreferenceDesign.class.getSimpleName();

	PreferenceActivity act;
	SharedPreferences pref;

	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);

		this.act = (PreferenceActivity) this.getActivity();
		PreferenceManager.setDefaultValues(this.act, R.xml.pref_design, true);
		this.pref = PreferenceManager.getDefaultSharedPreferences(act);

		addPreferencesFromResource(R.xml.pref_design);

		//twoPane
		Preference prefTwoPane = findPreference(ActivityPreference.PREF_KEY_TWO_PANE);
		prefTwoPane.setOnPreferenceClickListener(this.twoPaneCL);

		//theme
		final ListPreference prefTheme = (ListPreference) findPreference(ActivityPreference.PREF_KEY_THEME);
		prefTheme.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				Log.i(LOG, newValue.toString());
				int index = prefTheme.findIndexOfValue(newValue.toString());
				if (index == -1)
				{
					//Toast.makeText(act.getBaseContext(), prefTheme.getEntries()[index], Toast.LENGTH_LONG).show();
					return false;
				}
				boolean isPro = pref.getBoolean(ActivityPreference.PREF_KEY_IS_PRO, false) == true;
				if (isPro)
				{
					return true;
				}
				if (index > 1)
				{
					pref.edit().putString(ActivityPreference.PREF_KEY_THEME, ActivityPreference.THEME_GREY).commit();
					MaterialDialog dialogGoPro;
					MaterialDialog.Builder dialogGoProBuilder = new MaterialDialog.Builder(act);

					dialogGoProBuilder.title("Эта тема доступна в полной версии приложения")
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
								+ FragmentPreferenceAbout.LINK_TO_PRO)));
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
					dialogGoPro.getActionButton(DialogAction.POSITIVE).setBackgroundResource(
					R.drawable.md_btn_shape_green);
					dialogGoPro.show();

					return false;
				}
				return true;
			}
		});
	}

	protected OnPreferenceClickListener twoPaneCL = new OnPreferenceClickListener()
	{
		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			MaterialDialog dialogTwoPaneAlert;
			MaterialDialog.Builder dialogTwoPaneAlertBuilder = new MaterialDialog.Builder(act);
			dialogTwoPaneAlertBuilder.title("Планшетный режим")
			.content(Html.fromHtml(act.getResources().getString(R.string.two_pane_alert)))
			.positiveText("Всё равно включить")
			.negativeText("Тогда не надо")
			.callback(new MaterialDialog.ButtonCallback()
			{
				@Override
				public void onPositive(MaterialDialog dialog)
				{
					pref.edit().putBoolean(ActivityPreference.PREF_KEY_TWO_PANE, true).commit();
					Toast.makeText(act, "Изменения вступуят в силу после поворота экрана", Toast.LENGTH_SHORT).show();
					act.recreate();
				}

				@Override
				public void onNegative(MaterialDialog dialog)
				{
					pref.edit().putBoolean(ActivityPreference.PREF_KEY_TWO_PANE, false).commit();
					Toast.makeText(act, "Изменения вступуят в силу после поворота экрана", Toast.LENGTH_SHORT).show();
					act.recreate();
				}
			});
			dialogTwoPaneAlert = dialogTwoPaneAlertBuilder.build();
			dialogTwoPaneAlert.show();
			return false;
		}
	};
}