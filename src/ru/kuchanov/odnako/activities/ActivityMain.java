/*
 19.10.2014
ActivityMain.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.fragments.ArticlesListFragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class ActivityMain extends ActionBarActivity
{
	public boolean twoPane;

	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ActivityMain onCreate");
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.layout_main);

		//check if there is two fragments. If so, set flag (twoPane) to true
		if (findViewById(R.id.article) != null)
		{
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			twoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			//((ArticlesListFragment) getSupportFragmentManager().findFragmentById(R.id.articles_list)).setActivateOnItemClick(true);
		}
	}
}
