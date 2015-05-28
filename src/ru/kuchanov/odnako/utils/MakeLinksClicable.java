/*
 27.05.2015
MakeLinksClicable.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.activities.ActivityPreference;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.fragments.FragmentArticle;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class MakeLinksClicable
{
	private final static String LOG = MakeLinksClicable.class.getSimpleName();

	public static class CustomerTextClick extends ClickableSpan
	{
		String mUrl;

		public CustomerTextClick(String url)
		{
			mUrl = url;
		}

		@Override
		public void onClick(View widget)
		{
			//TODO add check by url
			if (this.mUrl.contains("odnako.org/blogs/"))
			{
				final AppCompatActivity act = (AppCompatActivity) widget.getContext();

				Log.i(LOG, "url clicked: " + this.mUrl);

				Fragment newFragment = new FragmentArticle();
				Article a = new Article();
				a.setUrl(this.mUrl);
				a.setTitle(this.mUrl);
				Bundle b = new Bundle();
				b.putParcelable(Article.KEY_CURENT_ART, a);
				b.putBoolean("isSingle", true);
				newFragment.setArguments(b);

				FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.container_right, newFragment, FragmentArticle.LOG);
				ft.addToBackStack(null);
				ft.commit();

				//setBackButton to toolbar and its title
				Toolbar toolbar;
				boolean twoPane = PreferenceManager.getDefaultSharedPreferences(act).getBoolean(
				ActivityPreference.PREF_KEY_TWO_PANE, false);
				if (!twoPane)
				{
					//So it's article activity
					((ActivityBase) act).mDrawerToggle.setDrawerIndicatorEnabled(false);
					toolbar = (Toolbar) act.findViewById(R.id.toolbar);
					toolbar.setTitle("Статья");
				}
				else
				{
					//we are on main activity, so we must set toggle to rightToolbar
					toolbar = (Toolbar) act.findViewById(R.id.toolbar_right);
					toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
					toolbar.setNavigationOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							act.onBackPressed();
						}
					});
					toolbar.setTitle("Статья");
				}
			}
			else
			{
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(mUrl));
				widget.getContext().startActivity(i);
			}
			//			Toast.makeText(widget.getContext(), mUrl, Toast.LENGTH_LONG).show();
		}

	}
}