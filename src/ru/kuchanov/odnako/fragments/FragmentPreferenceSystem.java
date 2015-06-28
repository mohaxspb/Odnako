package ru.kuchanov.odnako.fragments;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityPreference;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class FragmentPreferenceSystem extends PreferenceFragment
{
	private final static String LOG = FragmentPreferenceSystem.class.getSimpleName();

	private PreferenceActivity act;

	private static String APP_NAME;
	private static String APP_TITLE;

	public final static String LINK_TO_PRO = "ru.kuchanov.odnakopro";

	SharedPreferences pref;
	private boolean isPro;

	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);

		this.act = (PreferenceActivity) this.getActivity();

		this.pref = PreferenceManager.getDefaultSharedPreferences(act);
		this.isPro = pref.getBoolean(ActivityPreference.PREF_KEY_IS_PRO, false) == true;

		APP_TITLE = act.getResources().getString(R.string.app_name);
		APP_NAME = this.act.getApplicationInfo().packageName;

		addPreferencesFromResource(R.xml.pref_system);

		//link_to_market
		final ListPreference prefMaxArtsToStore = (ListPreference) findPreference(ActivityPreference.PREF_KEY_MAX_ARTICLES_TO_STORE);
		prefMaxArtsToStore.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				Log.i(LOG, newValue.toString());
				if (isPro)
				{
					return true;
				}
				else
				{
					int index = prefMaxArtsToStore.findIndexOfValue(newValue.toString());
					if (index == -1)
					{
						return false;
					}
					if (index > 0)
					{
						Toast.makeText(act, "Только в Однако+ версии!", Toast.LENGTH_SHORT).show();
						FragmentDialogDownloads.showGoProDialog(act);
						return false;
					}
					return false;
				}
			}
		});
	}
}