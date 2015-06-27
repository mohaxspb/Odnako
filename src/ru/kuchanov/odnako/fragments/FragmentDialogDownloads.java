/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.util.ArrayList;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.activities.ActivityPreference;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.db.Category;
import ru.kuchanov.odnako.db.ServiceDB;
import ru.kuchanov.odnako.lists_and_utils.CatData;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;

public class FragmentDialogDownloads extends DialogFragment
{
	final static String LOG = FragmentDialogDownloads.class.getSimpleName();

	final static String KEY_CATEGORIES = "KEY_CATEGORIES";
	final static String KEY_AUTHORS = "KEY_AUTHORS";
	final static String KEY_POSITION = "KEY_POSITION";
	final static String KEY_IS_CATEGORY = "KEY_IS_CATEGORY";

	final static String LINK_TO_PRO = "ru.kuchanov.odnakopro";

	private AppCompatActivity act;

	private ArrayList<Category> allCategories;
	private ArrayList<Author> allAuthors;
	private int position;
	private boolean isCategory;

	private SharedPreferences pref;

	private ArrayList<String> urls;

	public static FragmentDialogDownloads newInstance(ArrayList<Category> allCategories, ArrayList<Author> allAuthors,
	boolean isCategory, int positionInList)
	{
		FragmentDialogDownloads frag = new FragmentDialogDownloads();
		Bundle args = new Bundle();
		args.putParcelableArrayList(KEY_CATEGORIES, allCategories);
		args.putParcelableArrayList(KEY_AUTHORS, allAuthors);
		args.putInt(KEY_POSITION, positionInList);
		args.putBoolean(KEY_IS_CATEGORY, isCategory);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		Log.i(LOG, "onCreate");
		this.act = (AppCompatActivity) this.getActivity();

		Bundle args = this.getArguments();

		this.allAuthors = args.getParcelableArrayList(KEY_AUTHORS);
		this.allCategories = args.getParcelableArrayList(KEY_CATEGORIES);
		this.isCategory = args.getBoolean(KEY_IS_CATEGORY, false);
		this.position = args.getInt(KEY_POSITION, 0);

		this.pref = PreferenceManager.getDefaultSharedPreferences(act);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Log.i(LOG, "onCreateDialog");

		final ArrayList<String> quonts = new ArrayList<String>();
		quonts.add("5");
		quonts.add("10");
		quonts.add("15");
		quonts.add("20");
		quonts.add("30");
		//quonts.add("Все");

		boolean wrapInScrollView = true;
		MaterialDialog dialog;
		MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(act);
		dialogBuilder.title("Загрузка статей")
		.customView(R.layout.fragment_dialog_downloads, wrapInScrollView);

		if (this.pref.getBoolean(ActivityPreference.PREF_KEY_IS_PRO, false) == true)
		{
			dialogBuilder.positiveText(R.string.download)
			.callback(new MaterialDialog.ButtonCallback()
			{
				@Override
				public void onPositive(MaterialDialog dialog)
				{
					Spinner spinnerQuont = (Spinner) dialog.getCustomView().findViewById(R.id.spiner_quont);
					Spinner spinnerCategory = (Spinner) dialog.getCustomView().findViewById(R.id.spiner_category);
					int quont = 10;
					quont = Integer.parseInt(quonts.get(spinnerQuont.getSelectedItemPosition()));
					String url = urls.get(spinnerCategory.getSelectedItemPosition());
					Log.e(LOG, "Download url/quont: " + url + "/" + quont);

					Intent intent = new Intent(act, ServiceDB.class);
					String action = Const.Action.DATA_DOWNLOAD;
					intent.setAction(action);
					intent.putExtra("categoryToLoad", url);
					intent.putExtra("pageToLoad", 1);
					intent.putExtra("quont", quont);
					intent.putExtra("timeStamp", System.currentTimeMillis());
					intent.putExtra("startDownload", true);
					act.startService(intent);
				}
			});
			dialog = dialogBuilder.build();
		}
		else
		{
			dialogBuilder.positiveText(R.string.go_pro)
			.negativeText(R.string.download)
			.callback(new MaterialDialog.ButtonCallback()
			{
				@Override
				public void onPositive(MaterialDialog dialog)
				{
					Log.e(LOG, "Go PRO!");
					
					showGoProDialog(act);
				}

				@Override
				public void onNegative(MaterialDialog dialog)
				{
					Spinner spinnerQuont = (Spinner) dialog.getCustomView().findViewById(R.id.spiner_quont);
					Spinner spinnerCategory = (Spinner) dialog.getCustomView().findViewById(R.id.spiner_category);
					int quont = 10;
					quont = Integer.parseInt(quonts.get(spinnerQuont.getSelectedItemPosition()));
					String url = urls.get(spinnerCategory.getSelectedItemPosition());
					Log.e(LOG, "Download url/quont: " + url + "/" + quont);

					Intent intent = new Intent(act, ServiceDB.class);
					String action = Const.Action.DATA_DOWNLOAD;
					intent.setAction(action);
					intent.putExtra("categoryToLoad", url);
					intent.putExtra("pageToLoad", 1);
					intent.putExtra("quont", quont);
					intent.putExtra("timeStamp", System.currentTimeMillis());
					intent.putExtra("startDownload", true);
					act.startService(intent);
				}
			});
			dialog = dialogBuilder.build();
			int textColor = act.getResources().getColor(R.color.black);
			((MDButton) dialog.getActionButton(DialogAction.POSITIVE)).setTextColor(textColor);
			dialog.getActionButton(DialogAction.POSITIVE).setBackgroundResource(R.drawable.md_btn_shape_green);
		}

		final Spinner spinnerCategory = (Spinner) dialog.getCustomView().findViewById(R.id.spiner_category);
		ArrayAdapter<String> adapter;

		if (this.allAuthors.size() == 0 && this.allCategories.size() == 0)
		{
			//so it's main or single pager
			//single
			if (((ActivityMain) this.act).getPagerType() == ActivityMain.PAGER_TYPE_SINGLE)
			{
				ArrayList<String> urls = new ArrayList<String>();
				urls.add(((ActivityMain) this.act).getCurrentCategory());

				this.urls = urls;

				adapter = new ArrayAdapter<String>(this.act, android.R.layout.simple_spinner_item, urls);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerCategory.setAdapter(adapter);
				spinnerCategory.setSelection(this.position);
			}
			//main
			else
			{
				ArrayList<String> urls = CatData.getMenuLinksWithoutSystem(act);
				ArrayList<String> names = CatData.getMenuNamesWithoutSystem(act);

				this.urls = urls;

				adapter = new ArrayAdapter<String>(this.act, android.R.layout.simple_spinner_item, names);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerCategory.setAdapter(adapter);
				//as we have system frags in MenuPager we must correct position
				int correctedPosition = 10;
				if (this.position < 3)
				{
					correctedPosition = this.position;
				}
				else if (this.position > urls.size())
				{
					correctedPosition = 10;
				}
				else
				{
					correctedPosition = this.position - 1;
				}
				spinnerCategory.setSelection(correctedPosition);
			}
		}
		else
		{

			ArrayList<String> allCatUrls = new ArrayList<String>();
			ArrayList<String> allCatTitles = new ArrayList<String>();
			for (int i = 0; i < this.allCategories.size(); i++)
			{
				allCatUrls.add(allCategories.get(i).getUrl());
				allCatTitles.add(allCategories.get(i).getTitle());
			}

			ArrayList<String> allAutUrls = new ArrayList<String>();
			ArrayList<String> allAutTitles = new ArrayList<String>();
			for (int i = 0; i < this.allAuthors.size(); i++)
			{
				allAutUrls.add(allAuthors.get(i).getBlogUrl());
				allAutTitles.add(allAuthors.get(i).getName());
			}

			if (this.isCategory)
			{
				adapter = new ArrayAdapter<String>(this.act, android.R.layout.simple_spinner_item, allCatTitles);
				this.urls = allCatUrls;
			}
			else
			{
				adapter = new ArrayAdapter<String>(this.act, android.R.layout.simple_spinner_item, allAutTitles);
				this.urls = allAutUrls;
			}
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerCategory.setAdapter(adapter);
			spinnerCategory.setSelection(this.position);
		}

		final Spinner spinnerQuont = (Spinner) dialog.getCustomView().findViewById(R.id.spiner_quont);
		ArrayAdapter<String> adapterQuont = new ArrayAdapter<String>(this.act, android.R.layout.simple_spinner_item,
		quonts);
		adapterQuont.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerQuont.setAdapter(adapterQuont);
		spinnerQuont.setSelection(1);

		spinnerQuont.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				if (pref.getBoolean(ActivityPreference.PREF_KEY_IS_PRO, false) == false)
				{
					if (position > 1)
					{
						Toast.makeText(act, "Только в Однако+ версии", Toast.LENGTH_SHORT).show();
						spinnerQuont.setSelection(1);
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
				spinnerQuont.setSelection(1);
			}
		});

		return dialog;
	}

	public static void showGoProDialog(final Context ctx)
	{
		MaterialDialog dialogGoPro;
		MaterialDialog.Builder dialogGoProBuilder = new MaterialDialog.Builder(ctx);

		dialogGoProBuilder.title(R.string.go_pro_title)
		.content(Html.fromHtml(ctx.getResources().getString(R.string.pro_ver_adv)))
		.positiveText(R.string.go_pro_buy)
		.callback(new MaterialDialog.ButtonCallback()
		{
			@Override
			public void onPositive(MaterialDialog dialog)
			{
				try
				{
					ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
					+ LINK_TO_PRO)));
				} catch (Exception e)
				{
					String marketErrMsg = "Должен был запуститься Play Market, но что-то пошло не так...";
					Log.e(LOG, marketErrMsg);
					e.printStackTrace();
					Toast.makeText(ctx, marketErrMsg, Toast.LENGTH_SHORT).show();
				}
			}
		});
		dialogGoPro = dialogGoProBuilder.build();
		int textColor = ctx.getResources().getColor(R.color.black);
		((MDButton) dialogGoPro.getActionButton(DialogAction.POSITIVE)).setTextColor(textColor);
		dialogGoPro.getActionButton(DialogAction.POSITIVE).setBackgroundResource(R.drawable.md_btn_shape_green);
		dialogGoPro.show();
	}
}