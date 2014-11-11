package ru.kuchanov.odnako.download;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;

public class WriteFile extends AsyncTask<String, Void, String>
{
	String pathToFile = "";
	ActionBarActivity act;
	String data;
	String dirToWrite;
	String fileName;

	public WriteFile(String data, String dirToWrite, String fileName, ActionBarActivity act)
	{
		this.data = data;
		this.dirToWrite = dirToWrite;
		this.fileName = fileName;
		this.act = act;
	}

	protected String doInBackground(String... str)
	{
		String storagePath;// = Environment.getExternalStorageDirectory().getPath();
		storagePath = act.getFilesDir().getAbsolutePath();
		File dirToWriteFile = new File(storagePath + "/" + dirToWrite);
		if (!dirToWriteFile.exists())
		{
			dirToWriteFile.mkdirs();
		}
		System.out.println("Saving " + fileName);
//		System.out.println("data " + data);
		pathToFile = dirToWriteFile + "/" + fileName;
		File writenedFile = new File(pathToFile);
		try
		{
//			BufferedWriter bw = new BufferedWriter(new FileWriter(writenedFile, "UTF-8"));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writenedFile, true), "UTF8"));
			bw.write(data);
			bw.close();

		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return storagePath;
	}

	protected void onPostExecute(String storagePath)
	{
		System.out.println("Saved " + fileName);
	}
}