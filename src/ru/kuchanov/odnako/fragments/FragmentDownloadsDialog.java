/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.util.ArrayList;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.db.Category;
import ru.kuchanov.odnako.lists_and_utils.CatData;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;

public class FragmentDownloadsDialog extends DialogFragment
{
	final static String LOG = FragmentDownloadsDialog.class.getSimpleName();

	final static String KEY_CATEGORIES = "KEY_CATEGORIES";
	final static String KEY_AUTHORS = "KEY_AUTHORS";
	final static String KEY_POSITION = "KEY_POSITION";
	final static String KEY_IS_CATEGORY = "KEY_IS_CATEGORY";

	private AppCompatActivity act;

	private ArrayList<Category> allCategories;
	private ArrayList<Author> allAuthors;
	private int position;
	private boolean isCategory;

	public static FragmentDownloadsDialog newInstance(ArrayList<Category> allCategories, ArrayList<Author> allAuthors,
	boolean isCategory, int positionInList)
	{
		FragmentDownloadsDialog frag = new FragmentDownloadsDialog();
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
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Log.i(LOG, "onCreateDialog");

		boolean wrapInScrollView = true;
		MaterialDialog dialog = new MaterialDialog.Builder(act)
		.title("Загрузка статей")
		.customView(R.layout.fragment_dialog_downloads, wrapInScrollView)
		.positiveText(R.string.close).build();

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);

		Spinner spinnerCategory = (Spinner) dialog.getCustomView().findViewById(R.id.spiner_category);
		ArrayAdapter<String> adapter;

		if (this.allAuthors.size() == 0 && this.allCategories.size() == 0)
		{
			//so it's main or single pager
			ArrayList<String> urls = CatData.getMenuLinksWithoutSystem(act);
			ArrayList<String> names = CatData.getMenuNamesWithoutSystem(act);
			adapter = new ArrayAdapter<String>(this.act, android.R.layout.simple_spinner_item, names);
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
				allAutUrls.add(allAuthors.get(i).getBlog_url());
				allAutTitles.add(allAuthors.get(i).getName());
			}

			if (this.isCategory)
			{
				adapter = new ArrayAdapter<String>(this.act, android.R.layout.simple_spinner_item, allCatTitles);
			}
			else
			{
				adapter = new ArrayAdapter<String>(this.act, android.R.layout.simple_spinner_item, allAutTitles);
			}
		}
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCategory.setAdapter(adapter);
		// заголовок
		spinnerCategory.setPrompt("Title");
		// выделяем элемент 
		spinnerCategory.setSelection(this.position);

		return dialog;
	}
}