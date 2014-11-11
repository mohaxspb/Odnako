package ru.kuchanov.odnako.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
{
	ActionBarActivity act;
	String imgAdress;

	public DownloadImageTask(ActionBarActivity act)
	{
		this.act = act;
	}

	protected Bitmap doInBackground(String... urls)
	{
		String urldisplay = "default";
		this.imgAdress = urls[0];
		Bitmap mIcon11 = null;

		if (imgAdress.startsWith("/"))
		{
			urldisplay = "http://www.odnako.org" + imgAdress;
		}

		else
		{
			urldisplay = imgAdress;
		}
		
		System.out.println("imgAdress: " + imgAdress);

		try
		{
			InputStream in = new java.net.URL(urldisplay).openStream();
			mIcon11 = BitmapFactory.decodeStream(in);
		} catch (Exception e)
		{
			//System.out.println("Bitmap doInBackground(String... urls)" + e.getLocalizedMessage());
		}

		return mIcon11;
	}

	protected void onPostExecute(Bitmap result)
	{
		if (result != null)
		{
			write(result);
		}
	}

	protected void write(Bitmap result)
	{
		String dirToWrite = act.getFilesDir().getAbsolutePath();
		String formatedImgAdress;
		formatedImgAdress = this.imgAdress.substring(imgAdress.lastIndexOf("/") + 1);
		formatedImgAdress=formatedImgAdress.replace("-", "_");
		File dirToWriteFile = new File(dirToWrite + "/images/");
		if(!dirToWriteFile.exists())
		{
			dirToWriteFile.mkdirs();
		}
		File blogsPageFile = new File(dirToWriteFile + "/" + formatedImgAdress);
		

		if (!blogsPageFile.exists())
		{
			try
			{
				blogsPageFile.createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				System.out.println("IMG SAVE: " + formatedImgAdress);
				FileOutputStream out = new FileOutputStream(blogsPageFile);
				result.compress(Bitmap.CompressFormat.PNG, 90, out);
				out.close();
			} catch (Exception e)
			{
				System.out.println("ERROR iN IMG SAVE: " + e.getLocalizedMessage());
			}
		}
	}
}