package ru.kuchanov.odnako.download;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class WriteFile
{
	String url;
	String data;
	String category;

	SharedPreferences pref;

	Context context;

	public WriteFile(Context context)
	{
		this.context = context;
	}

	public void setVars(String url, String data, String category)
	{
		this.url = url;
		this.data = data;
		this.category = category;
	}

	protected void write()
	{
		System.out.println("write");
		pref = PreferenceManager.getDefaultSharedPreferences(context);
		String storagePath;
		storagePath = pref.getString("filesDir", "");

		String formatedCategory;
		formatedCategory = this.category.replace("-", "_");
		formatedCategory = formatedCategory.replace("/", "_");
		formatedCategory = formatedCategory.replace(":", "_");
		formatedCategory = formatedCategory.replace(".", "_");
		File dirToWriteFile = new File(storagePath + "/" + formatedCategory);

		if (!dirToWriteFile.exists())
		{
			dirToWriteFile.mkdirs();
		}
		String fileName;
		fileName = this.url.replace("-", "_");
		fileName = fileName.replace("/", "_");
		fileName = fileName.replace(":", "_");
		fileName = fileName.replace(".", "_");

		System.out.println("Saving " + fileName);
		String pathToFile = dirToWriteFile + "/" + fileName;
		File writenedFile = new File(pathToFile);
		try
		{
			// открываем поток для записи
			BufferedWriter bw = new BufferedWriter(new FileWriter(writenedFile));
			// пишем данные
			bw.write(data);
			// закрываем поток
			bw.close();

		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}