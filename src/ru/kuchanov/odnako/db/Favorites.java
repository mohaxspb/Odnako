/*
 06.06.2015
Favorites.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.util.ArrayList;
import java.util.HashMap;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.activities.ActivityPreference;
import ru.kuchanov.odnako.fragments.FragmentDialogDownloads;
import ru.kuchanov.odnako.utils.FavoritesDownload;
import ru.kuchanov.odnako.utils.FavoritesUpload;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;

/**
 * Model for parsing and manipulating of favorites data.
 * 
 * We store it as pairs url-title in sharedPreferences and webServer (in DB)
 */
public class Favorites
{
	static final String LOG = Favorites.class.getSimpleName();

	public static final String DIVIDER = " !!!! ";
	public static final String DIVIDER_GROUP = " !!__!! ";
	public static final String DIVIDER_CATEGORIES = "!_!_!_!";

	public static final String KEY_AUTHORS = "authors";
	public static final String KEY_ARTICLES = "articles";
	public static final String KEY_CATEGORIES = "categories";

	public static final String KEY_LOGIN = "login";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_LOG_PASS = "logPass";

	public static final String KEY_REFRESHED = "favsRefreshed";

	public static final int MAX_FAVS_AUTHORS = 1;
	public static final int MAX_FAVS_CATEGORIES = 1;
	public static final int MAX_FAVS_ARTICLES = 3;

	private String authors, categories, articles;

	private Favorites(String authors, String categories, String articles)
	{
		this.articles = articles;
		this.categories = categories;
		this.authors = authors;
	}

	public HashMap<String, ArrayList<String>> getData()
	{
		HashMap<String, ArrayList<String>> data = new HashMap<String, ArrayList<String>>();

		ArrayList<String> authors = new ArrayList<String>();
		if (!Const.EMPTY_STRING.equals(authors))
		{
			for (String s : this.authors.split(DIVIDER_GROUP))
			{
				authors.add(s);
			}
		}
		data.put(KEY_AUTHORS, authors);
		ArrayList<String> categories = new ArrayList<String>();
		if (!Const.EMPTY_STRING.equals(categories))
		{
			for (String s : this.categories.split(DIVIDER_GROUP))
			{
				categories.add(s);
			}
		}
		data.put(KEY_CATEGORIES, categories);
		ArrayList<String> articles = new ArrayList<String>();
		if (!Const.EMPTY_STRING.equals(articles))
		{
			for (String s : this.articles.split(DIVIDER_GROUP))
			{
				articles.add(s);
			}
		}
		data.put(KEY_ARTICLES, articles);

		return data;
	}

	public static Favorites getInstanceOfFavorites(Context ctx)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);

		String authors = pref.getString(KEY_AUTHORS, Const.EMPTY_STRING);
		String categories = pref.getString(KEY_CATEGORIES, Const.EMPTY_STRING);
		String articles = pref.getString(KEY_ARTICLES, Const.EMPTY_STRING);

		return new Favorites(authors, categories, articles);
	}

	public static ArrayList<String> getFavoriteUrls(Context ctx, String type)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);

		ArrayList<String> urls = new ArrayList<String>();

		String articles = pref.getString(type, Const.EMPTY_STRING);
		ArrayList<String> articlesGroups = new ArrayList<String>();
		if (!Const.EMPTY_STRING.equals(articles))
		{
			for (String s : articles.split(DIVIDER_GROUP))
			{
				articlesGroups.add(s);
			}
			for (String s : articlesGroups)
			{
				String[] urlsAndTitles = s.split(DIVIDER);
				urls.add(urlsAndTitles[0]);
			}
		}
		return urls;
	}

	public static void writeFavorites(Context ctx, String favorites)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);

		String[] favs = favorites.split(DIVIDER_CATEGORIES);

		pref.edit().putString(KEY_AUTHORS, favs[0]).commit();
		pref.edit().putString(KEY_CATEGORIES, favs[1]).commit();
		pref.edit().putString(KEY_ARTICLES, favs[2]).commit();

		pref.edit().putLong(KEY_REFRESHED, System.currentTimeMillis()).commit();
	}

	public static void addFavorite(Context ctx, String type, String url, String title)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);

		//check if it's not PRO version and favs limit already reached!
		String existedFavs = pref.getString(type, Const.EMPTY_STRING);
		if (pref.getBoolean(ActivityPreference.PREF_KEY_IS_PRO, false) == false)
		{
			if (!Const.EMPTY_STRING.equals(existedFavs))
			{
				String[] favs = existedFavs.split(DIVIDER_GROUP);
				int size = favs.length;
				boolean limitReached = false;
				String msg = "Только в Однако+ версии!";
				switch (type)
				{
					case KEY_AUTHORS:
						if (size >= MAX_FAVS_AUTHORS)
						{
							limitReached = true;
							msg = "В бесплатной версии не более одного автора можно добавить в избранное!";
						}
					break;
					case KEY_CATEGORIES:
						if (size >= MAX_FAVS_CATEGORIES)
						{
							limitReached = true;
							msg = "В бесплатной версии не более одного раздела можно добавить в избранное!";
						}
					break;
					case KEY_ARTICLES:
						if (size >= MAX_FAVS_ARTICLES)
						{
							limitReached = true;
							msg = "В бесплатной версии не более 3 статей можно добавить в избранное!";
						}
					break;
				}
				if (limitReached)
				{
					Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
					FragmentDialogDownloads.showGoProDialog(ctx);
					return;
				}
			}
		}
		///////////////////
		boolean alreadyExisted = false;

		for (String s : Favorites.getFavoriteUrls(ctx, type))
		{
			if (s.equals(url))
			{
				alreadyExisted = true;
				break;
			}
		}

		if (!alreadyExisted)
		{
			if (Const.EMPTY_STRING.equals(existedFavs))
			{
				existedFavs = "";
			}
			else
			{
				existedFavs += Favorites.DIVIDER_GROUP;
			}
			pref.edit().putString(type, existedFavs + url + Favorites.DIVIDER + title).commit();

			pref.edit().putLong(KEY_REFRESHED, System.currentTimeMillis()).commit();

			Log.i(LOG, type + " " + url + " successfully add to favorites");
			String msg = "";
			switch (type)
			{
				case KEY_AUTHORS:
					msg = "Автор добавлен в избранное!";
				break;
				case KEY_CATEGORIES:
					msg = "Раздел добавлен в избранное!";
				break;
				case KEY_ARTICLES:
					msg = "Статья добавлена в избранное!";
				break;
			}
			Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Log.i(LOG, "article " + url + " IS ALREDAY add to favorites");
			Toast.makeText(ctx, "Уже в избранном!", Toast.LENGTH_SHORT).show();
		}
	}

	public static void removeFavorite(Context ctx, String type, String url)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);

		boolean alreadyExisted = false;
		int numInList = -1;
		ArrayList<String> urls = Favorites.getFavoriteUrls(ctx, type);
		for (int i = 0; i < urls.size(); i++)
		{
			String s = urls.get(i);
			if (s.equals(url))
			{
				alreadyExisted = true;
				numInList = i;
				break;
			}
		}

		if (alreadyExisted)
		{
			String existedFavs = pref.getString(type, Const.EMPTY_STRING);
			if (urls.size() == 1)
			{
				pref.edit().putString(type, Const.EMPTY_STRING).commit();
			}
			else
			{
				//if it's first row we have no divider at the start
				//if last, we have no divider in the end
				//and else we have divider both at the start and end
				String partToDelete;
				if (numInList == 0)
				{
					int indexOfFirstGroupDivider = existedFavs.indexOf(DIVIDER_GROUP);
					partToDelete = existedFavs.substring(0, indexOfFirstGroupDivider + DIVIDER_GROUP.length());
					Log.d(LOG, "partToDelete: " + partToDelete);
				}
				else if (numInList == urls.size() - 1)
				{
					int indexOfLastGroupDivider = existedFavs.lastIndexOf(DIVIDER_GROUP);
					partToDelete = existedFavs.substring(indexOfLastGroupDivider, existedFavs.length());
					Log.d(LOG, "partToDelete: " + partToDelete);
				}
				else
				{
					String[] artFavs = existedFavs.split(DIVIDER_GROUP);
					partToDelete = artFavs[numInList] + DIVIDER_GROUP;
					Log.d(LOG, "partToDelete: " + partToDelete);
				}
				String resultedFavs = existedFavs.replace(partToDelete, "");
				pref.edit().putString(type, resultedFavs).commit();
			}
			pref.edit().putLong(KEY_REFRESHED, System.currentTimeMillis()).commit();

			Log.i(LOG, "article " + url + "successfully removed from favorites");
		}
		else
		{
			Log.i(LOG, "article " + url + "DON'T FOUND IN favorites");
		}
	}

	public static void uploadFavs(final ActivityBase act)
	{
		final String login, password;
		//firstly check if we store some log/pass in prefs
		final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
		String loginPass = pref.getString(Favorites.KEY_LOG_PASS, Const.EMPTY_STRING);
		String[] logPassArr = (Const.EMPTY_STRING.equals(loginPass)) ? null : loginPass.split(DIVIDER);
		if (logPassArr != null)
		{
			login = logPassArr[0];
			password = logPassArr[1];

			FavoritesUpload favsUpload = new FavoritesUpload(act, login, password);
			favsUpload.execute();
		}
		else
		//no logPass in prefs, so let user write it
		{
			showFavsLogPassDialog(act);
		}
	}

	public static void downloadFavs(final ActivityBase act)
	{
		final String login, password;
		//firstly check if we store some log/pass in prefs
		final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
		String loginPass = pref.getString(Favorites.KEY_LOG_PASS, Const.EMPTY_STRING);
		String[] logPassArr = (Const.EMPTY_STRING.equals(loginPass)) ? null : loginPass.split(DIVIDER);
		if (logPassArr != null)
		{
			login = logPassArr[0];
			password = logPassArr[1];

			FavoritesDownload favsDownload = new FavoritesDownload(act, login, password);
			favsDownload.execute();
		}
		else
		//no logPass in prefs, so let user write it
		{
			showFavsLogPassDialog(act);
		}
	}

	public static void showFavsLogPassDialog(final ActivityBase act)
	{
		final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);

		MaterialDialog dialogShare;
		MaterialDialog.Builder dialogShareBuilder = new MaterialDialog.Builder(act);
		dialogShareBuilder.title(R.string.favs_log_pass_title);
		dialogShareBuilder.customView(R.layout.fragment_dialog_favorites_log_pass, true);
		dialogShareBuilder.positiveText("Сохранить");
		dialogShareBuilder.negativeText("Отмена");
		dialogShareBuilder.callback(new ButtonCallback()
		{
			@Override
			public void onPositive(MaterialDialog dialog)
			{
				EditText loginET = (EditText) dialog.getCustomView().findViewById(R.id.login);
				String login = loginET.getText().toString();
				EditText passET = (EditText) dialog.getCustomView().findViewById(R.id.password);
				String pass = passET.getText().toString();

				if ("".equals(login))
				{
					Toast.makeText(act, "Надо бы какой-нибудь логин задать...", Toast.LENGTH_SHORT).show();
					return;
				}
				if ("".equals(pass))
				{
					Toast.makeText(act, "Без пароля ничего не получится!", Toast.LENGTH_SHORT).show();
					return;
				}

				pref.edit().putString(Favorites.KEY_LOG_PASS, login + DIVIDER + pass).commit();
				act.drawerRightRecyclerView.getAdapter().notifyDataSetChanged();
			}
		});
		dialogShareBuilder.cancelable(false);
		dialogShare = dialogShareBuilder.build();

		String logPass = pref.getString(KEY_LOG_PASS, Const.EMPTY_STRING);
		boolean logPassExists = (Const.EMPTY_STRING.equals(logPass)) ? false : true;

		if (logPassExists)
		{
			EditText loginET = (EditText) dialogShare.getCustomView().findViewById(R.id.login);
			String login = logPass.split(DIVIDER)[0];
			loginET.setText(login);

			EditText passET = (EditText) dialogShare.getCustomView().findViewById(R.id.password);
			String pass = logPass.split(DIVIDER)[1];
			passET.setText(pass);
		}

		dialogShare.show();
	}

	public static void showFavsToFromServerDialog(final ActivityBase act)
	{
		MaterialDialog dialogShare;
		MaterialDialog.Builder dialogShareBuilder = new MaterialDialog.Builder(act);
		String[] options = new String[] { "Отправить на сервер", "Загрузить с сервера" };
		dialogShareBuilder.items(options);
		dialogShareBuilder.title("На сервер или с него?");
		dialogShareBuilder.itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
		{
			@Override
			public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text)
			{
				switch (which)
				{
					case 0:
						Log.i(LOG, "Отправить на сервер");
						Favorites.uploadFavs(act);
						act.drawerRightSwipeRefreshLayout.setRefreshing(true);
					break;
					case 1:
						Log.i(LOG, "Загрузить с сервера");
						Favorites.downloadFavs(act);
						act.drawerRightSwipeRefreshLayout.setRefreshing(true);
					break;
				}
				return true;
			}
		});
		dialogShareBuilder.cancelListener(new DialogInterface.OnCancelListener()
		{
			@Override
			public void onCancel(DialogInterface dialog)
			{
				act.drawerRightSwipeRefreshLayout.setRefreshing(false);
			}
		});
		dialogShare = dialogShareBuilder.build();
		dialogShare.show();
	}
}