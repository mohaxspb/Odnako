package ru.kuchanov.odnako;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;



public class WriteFile extends AsyncTask<String, Void, String>
{
	String pathToFile = "";
	Activity act;
	String data;
	String dirToWrite;
	String fileName;
	ProgressDialog pd;
	Integer operationNum;
	Integer artsSum;
	String[] allLinks;

	public WriteFile(String data, String dirToWrite, String fileName, ProgressDialog pd, Integer operationNum, Integer artsSum, Activity act, String[] allLinks)
	{
		this.data = data;
		this.dirToWrite = dirToWrite;
		this.fileName = fileName;
		this.pd = pd;
		this.operationNum = operationNum;
		this.artsSum=artsSum;
		this.act = act;
		this.allLinks=allLinks;
	}

	@Override
	protected void onPreExecute()
	{}

	@Override
	protected String doInBackground(String... str)
	{
		String storagePath = Environment.getExternalStorageDirectory().getPath();
		storagePath = act.getFilesDir().getAbsolutePath();
		File dirToWriteFile = new File(storagePath + "/" + dirToWrite);
		if (!dirToWriteFile.exists())
		{
			dirToWriteFile.mkdirs();
		}

		// формируем объект File, который содержит путь к файлу

		fileName = fileName.replace("-", "_");
		//fileName = fileName.substring(28, fileName.length() - 1) + ".txt";
		System.out.println("Save " + fileName);
		pathToFile = dirToWriteFile + "/" + fileName;
		// File writenedFile =new File(dirToWriteFile, fileName);
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
		pathToFile = dirToWriteFile + "/" + fileName;
		return pathToFile;
	}

	@Override
	protected void onPostExecute(String output)
	{
		System.out.println("Save " + fileName);
		//System.out.println(operationNum);
		// operationNum += 1;
		Integer progPers = operationNum * 100 / artsSum;
		if(pd!=null)
		{
			pd.setMessage("Статей загружено: " + String.valueOf(operationNum) + "/" + String.valueOf(artsSum));
			pd.setProgress(progPers);
			
		}
		SharedPreferences pref = act.getSharedPreferences("arrays", Context.MODE_PRIVATE);
		pref.edit().putString("saved_art_link_" + String.valueOf(operationNum - 1), fileName).commit();
		
		//test
		/*String[] newArtsArr=new String[artsSum];
		for (int u=0; u<=operationNum; u++)
		{
			newArtsArr[u]=allLinks[u];
		}*/
//		for(int i=0; i<30-operationNum; i++)
//		{
//			String savedFileName;
//			savedFileName = allLinks[i].replace("-", "_");
//			savedFileName = savedFileName.substring(28, savedFileName.length() - 1) + ".txt";
//			pref.edit().putString("saved_art_link_" + String.valueOf(operationNum+i), allLinks[i]).commit();
//		}
		//test
		
		if (operationNum == artsSum && pd!=null)
		{
			pd.dismiss();
			pd.cancel();
			pd = null;
			Toast.makeText(act, "Всё готово! Спасибо за ожидание)", Toast.LENGTH_LONG).show();
		}
	}
}