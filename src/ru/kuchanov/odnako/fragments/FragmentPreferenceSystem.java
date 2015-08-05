package ru.kuchanov.odnako.fragments;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityPreference;
import ru.kuchanov.odnako.db.ArtAutTable;
import ru.kuchanov.odnako.db.ArtCatTable;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.AsyncTaskDeleteArticlesText;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.db.Category;
import ru.kuchanov.odnako.db.DataBaseHelper;
import ru.kuchanov.odnako.utils.MyUIL;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;

public class FragmentPreferenceSystem extends PreferenceFragment
{
	private final static String LOG = FragmentPreferenceSystem.class.getSimpleName();

	private PreferenceActivity act;

	public final static String LINK_TO_PRO = "ru.kuchanov.odnakopro";

	SharedPreferences pref;
	private boolean isPro;

	ListPreference prefMaxArtsToStore;
	Preference dbClear;
	Preference cacheInfo;
	Preference cacheClear;

	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);

		this.act = (PreferenceActivity) this.getActivity();

		this.pref = PreferenceManager.getDefaultSharedPreferences(act);
		this.isPro = pref.getBoolean(ActivityPreference.PREF_KEY_IS_PRO, false) == true;

		addPreferencesFromResource(R.xml.pref_system);

		//max_arts_to_save
		prefMaxArtsToStore = (ListPreference) findPreference(ActivityPreference.PREF_KEY_MAX_ARTICLES_TO_STORE);
		prefMaxArtsToStore.setOnPreferenceChangeListener(prefCLmaxArtsToStore);

		dbClear = (Preference) findPreference(ActivityPreference.PREF_KEY_DB_CLEAR);
		dbClear.setOnPreferenceClickListener(prefCLDBClear);

		cacheInfo = (Preference) findPreference(ActivityPreference.PREF_KEY_IMAGE_CACHE_INFO);
		cacheInfo.setOnPreferenceClickListener(prefCLCacheInfo);
	}

	Preference.OnPreferenceChangeListener prefCLmaxArtsToStore = new Preference.OnPreferenceChangeListener()
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
				else if (index == 0)
				{
					return true;
				}
				return false;
			}
		}
	};

	private OnPreferenceClickListener prefCLDBClear = new OnPreferenceClickListener()
	{
		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			final DataBaseHelper h = new DataBaseHelper(act);

			final MaterialDialog dialogDBClear;
			MaterialDialog.Builder dialogDBClearBuilder = new MaterialDialog.Builder(act);

			dialogDBClearBuilder.title("Операции с базой данных")
			.customView(R.layout.dialog_db, true)
			.cancelListener(new OnCancelListener()
			{
				@Override
				public void onCancel(DialogInterface dialog)
				{
					h.close();
				}
			})
			.positiveText("Закрыть");
			dialogDBClear = dialogDBClearBuilder.build();

			TextView numOfSavedArts = (TextView) dialogDBClear.getCustomView().findViewById(R.id.num_of_saved_arts);
			TextView deleteSavedArts = (TextView) dialogDBClear.getCustomView().findViewById(R.id.delete_saved_arts);

			try
			{
				QueryBuilder<Article, Integer> qb;
				qb = h.getDaoArticle().queryBuilder();
				qb.where().ne(Article.FIELD_NAME_ART_TEXT, Const.EMPTY_STRING);
				qb.orderBy(Article.FIELD_NAME_REFRESHED_DATE, false);

				ArrayList<Article> allDownloadedArts = (ArrayList<Article>) qb.query();

				numOfSavedArts.setText("Всего загруженных статей: " + allDownloadedArts.size());
				deleteSavedArts.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						MaterialDialog dialog;
						MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(act);

						dialogBuilder.title("Подтверждение удаления")
						.content("Вы уверены, что хотите удалить текст всех загруженны статей?")
						.positiveText("Да!")
						.negativeText("Отмена")
						.callback(new MaterialDialog.ButtonCallback()
						{
							@Override
							public void onPositive(MaterialDialog dialog)
							{
								Log.i(LOG, "Delete downloaded arts");

								AsyncTaskDeleteArticlesText deleteTask = new AsyncTaskDeleteArticlesText(act,
								AsyncTaskDeleteArticlesText.REQUEST_TYPE_DELETE_ALL);
								deleteTask.execute();

								dialogDBClear.cancel();
							}
						});
						dialog = dialogBuilder.build();
						dialog.show();
					}
				});
			}
			catch (Exception e)
			{
				e.printStackTrace();
				numOfSavedArts.setText("не удалось получить данные");
			}

			TextView dbSizeTV = (TextView) dialogDBClear.getCustomView().findViewById(R.id.db_size);
			File f = act.getDatabasePath(DataBaseHelper.DATABASE_NAME);
			long dbSize = f.length();
			Log.i(LOG, "DB file size is: " + dbSize + " bait");
			float dbSizeInMB = (float) dbSize / 1048576;
			dbSizeTV.setText("Размер базы данных: " + String.format("%.2f", dbSizeInMB) + " Мбайт");
			TextView dbDropTV = (TextView) dialogDBClear.getCustomView().findViewById(R.id.drop_db);
			dbDropTV.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					MaterialDialog dialog;
					MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(act);

					dialogBuilder
					.title("Подтверждение очистки базы данных")
					.content(
					"Вы уверены, что хотите полностью очистить базу данных? Разработчик рекомендует сделать это если, например, в связи с какой-то ужасной ошибкой база данных начала неконтролируемо увеличиваться в объёме. =)")
					.positiveText("Да!")
					.negativeText("Отмена")
					.callback(new MaterialDialog.ButtonCallback()
					{
						@Override
						public void onPositive(MaterialDialog dialog)
						{
							Log.i(LOG, "Drop DB");
							//clear and ReFill all tables
							try
							{
								TableUtils.clearTable(h.getConnectionSource(), Category.class);
								TableUtils.clearTable(h.getConnectionSource(), Author.class);
								TableUtils.clearTable(h.getConnectionSource(), Article.class);
								TableUtils.clearTable(h.getConnectionSource(), ArtCatTable.class);
								TableUtils.clearTable(h.getConnectionSource(), ArtAutTable.class);

								h.fillTables();
							} catch (SQLException e)
							{
								e.printStackTrace();
								Toast.makeText(act, "Произошла какая-то ошибка при сбросе базы данных... =(",
								Toast.LENGTH_SHORT).show();
							}
							dialogDBClear.cancel();
						}
					});
					dialog = dialogBuilder.build();
					dialog.show();
				}
			});

			dialogDBClear.show();
			return false;
		}
	};

	private OnPreferenceClickListener prefCLCacheInfo = new OnPreferenceClickListener()
	{
		@SuppressWarnings("deprecation")
		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			final MaterialDialog dialogImgCache;
			MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(act);

			dialogBuilder.title("Кэш изображений")
			.customView(R.layout.dialog_image_cache, true)
			.positiveText("Закрыть");
			dialogImgCache = dialogBuilder.build();

			TextView imgCacheInfo = (TextView) dialogImgCache.getCustomView().findViewById(R.id.img_cache_info);
			TextView imgCacheDelete = (TextView) dialogImgCache.getCustomView().findViewById(R.id.img_cache_delete);

			File cache = MyUIL.get(act).getDiskCache().getDirectory();
			File[] cached = cache.listFiles();

			long length = 0;
			for (File f : cached)
			{
				length += f.length();
			}
			float cahceSizeInMB = (float) length / 1048576;

			String content = "Размер кэша на устройстве: \n" + String.format("%.2f", cahceSizeInMB) + " Мбайт"
			+ "\n\n" + "Расположение кэша: \n" + MyUIL.get(act).getDiskCache().getDirectory().getAbsolutePath();

			imgCacheInfo.setText(content);
			imgCacheDelete.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					MaterialDialog dialogWarning;
					MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(act);

					dialogBuilder.title("Подтверждение удаления")
					.content("Вы уверены, что хотите очистить кэш изображений?")
					.positiveText("Да!")
					.negativeText("Отмена")
					.callback(new MaterialDialog.ButtonCallback()
					{
						@Override
						public void onPositive(MaterialDialog dialog)
						{
							Log.i(LOG, "Image cache clear!");
							MyUIL.get(act).getDiskCache().clear();
							dialogImgCache.cancel();
						}
					});
					dialogWarning = dialogBuilder.build();
					dialogWarning.show();
				}
			});

			dialogImgCache.show();
			return false;
		}
	};
}