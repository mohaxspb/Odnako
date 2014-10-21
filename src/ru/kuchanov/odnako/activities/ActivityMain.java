/*
 19.10.2014
ActivityMain.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.fragments.ArticlesListFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;

public class ActivityMain extends ActionBarActivity
{
	public boolean twoPane;
	SharedPreferences pref;

	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ActivityMain onCreate");
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.layout_main);

		//get default settings to get all settings later
		PreferenceManager.setDefaultValues(this, R.xml.pref, true);
		//end of get default settings to get all settings later

		//Set unreaded num of arts to zero
		//it's for new arts motification
		SharedPreferences prefsNumOfArts = this.getSharedPreferences("saveNumOfUnReadedArts", 0);
		SharedPreferences.Editor editor = prefsNumOfArts.edit();
		editor.putInt("quontityOfUnreadedArts", 0);
		editor.commit();
		//end of Set unreaded num of arts to zero

		//check if there is two fragments. If so, set flag (twoPane) to true
		if (findViewById(R.id.article) != null)
		{
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			twoPane = true;

			//save it to pref, to be able to read it without calling Activity
			this.pref = PreferenceManager.getDefaultSharedPreferences(this);
			this.pref.edit().putBoolean("twoPane", twoPane).commit();

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((ArticlesListFragment) getSupportFragmentManager().findFragmentById(R.id.articles_list)).setActivateOnItemClick(true);
		}
	}
}
