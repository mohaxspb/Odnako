package ru.kuchanov.odnako.download;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;


public class WriteFileService extends AsyncTask<Void, Void, String>
{
	String url;
	String data;
	String category;
	Integer operationNum;
	Integer numOfOperations;
	
	SharedPreferences pref;
	
	Context context;

	public WriteFileService(Context context)
	{
		this.context=context;
	}
	public void setVars(String url, String data, String category, Integer operationNum, Integer numOfOperations)
	{
		this.url=url;
		this.data = data;
		this.category = category;
		this.operationNum = operationNum;
		this.numOfOperations=numOfOperations;
	}

	@Override
	protected void onPreExecute()
	{}

	@Override
	protected String doInBackground(Void... arg)
	{
		System.out.println("Save doInBackground ");
		pref=PreferenceManager.getDefaultSharedPreferences(context);
		String storagePath;
		storagePath =pref.getString("filesDir", "");
		
		
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

		// формируем объект File, который содержит путь к файлу

		String fileName;
		fileName = this.url.replace("-", "_");
		fileName = fileName.replace("/", "_");
		fileName = fileName.replace(":", "_");
		fileName = fileName.replace(".", "_");
		
		System.out.println("Save " + fileName);
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
		return pathToFile;
	}

	@Override
	protected void onPostExecute(String output)
	{
		System.out.println("Save onPostExecute");
		
		if (operationNum == this.numOfOperations)
		{
			Toast.makeText(context, "Всё готово! Спасибо за ожидание. =)", Toast.LENGTH_LONG).show();
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			int notifyID = 1;
			notificationManager.cancel(notifyID);
		}
	}
}