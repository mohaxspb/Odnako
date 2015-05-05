package ru.kuchanov.odnako.fragments;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityPreference;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

public class FragmentPreferenceDesign extends PreferenceFragment
{
	final static String LOG = FragmentPreferenceDesign.class.getSimpleName();

	PreferenceActivity act;

	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);

		this.act = (PreferenceActivity) this.getActivity();

		addPreferencesFromResource(R.xml.pref_design);

		//version_history
		Preference prefVersionHistory = findPreference("twoPane");
		prefVersionHistory.setOnPreferenceClickListener(this.twoPaneCL);

	}

	protected OnPreferenceClickListener twoPaneCL = new OnPreferenceClickListener()
	{
		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);

			MaterialDialog dialogTwoPaneAlert;
			MaterialDialog.Builder dialogTwoPaneAlertBuilder = new MaterialDialog.Builder(act);
			dialogTwoPaneAlertBuilder.title("Планшетный режим")
			.content(act.getResources().getString(R.string.two_pane_alert))
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