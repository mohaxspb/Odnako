package ru.kuchanov.odnako.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import ru.kuchanov.odnako.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
{
	ImageView iv;
	
	CardView card;
	ActionBarActivity act;
	ProgressDialog pd;
	String imgAdress;

	public DownloadImageTask(ImageView iv, ActionBarActivity act)
	{
		this.act = act;
		this.iv = iv;
	}
	
	public DownloadImageTask(CardView card, ActionBarActivity act)
	{
		this.act = act;
		this.card = card;
	}

	protected void onPreExecute()
	{
	}

	protected Bitmap doInBackground(String... urls)
	{
		String urldisplay = "default";
		String imgAdress = urls[0];
		this.imgAdress = imgAdress;
		Bitmap mIcon11 = null;

		//
		String dirToWrite = act.getFilesDir().getAbsolutePath();
		String formatedImgAdress;
		formatedImgAdress = this.imgAdress.replace("-", "_");
		formatedImgAdress = formatedImgAdress.replace("/", "_");
		formatedImgAdress = formatedImgAdress.replace(":", "_");
		File dirToWriteFile = new File(dirToWrite + "/images/");
		File blogsPageFile = new File(dirToWriteFile + "/" + formatedImgAdress);
		//System.out.println("DownImgs blogsPageFile.exists() "+blogsPageFile.exists());
		//System.out.println("DownImgs blogsPageFile.getPath() "+blogsPageFile.getPath());
		if (blogsPageFile.exists())
		{
			//System.out.println("doInBackground blogsPageFile.getPath() "+blogsPageFile.getPath());
			mIcon11 = BitmapFactory.decodeFile(blogsPageFile.getPath());
		}
		else
		{
			try
			{
				if (!urls[0].equals("default"))
				{
					if (imgAdress.startsWith("/"))
					{
						urldisplay = "http://www.odnako.org" + imgAdress;
					}

					else
					{
						urldisplay = imgAdress;
					}

					try
					{
						InputStream in = new java.net.URL(urldisplay).openStream();
						mIcon11 = BitmapFactory.decodeStream(in);
					} catch (Exception e)
					{
						//System.out.println("Bitmap doInBackground(String... urls)" + e.getLocalizedMessage());
					}
				}
			} catch (Exception e)
			{
			}
		}
		//

		return mIcon11;
	}

	protected void onPostExecute(Bitmap result)
	{
//		if(this.iv==null)
//		{
//			if (result != null)
//			{
//				write(result);
//				this.card.setBackgroundDrawable(new BitmapDrawable(act.getResources(), result));
//			}
//			else
//			{
//				this.card.setBackgroundDrawable(act.getResources().getDrawable(R.drawable.ic_crop_original_grey600_48dp));
//			}
//			return;
//		}

		if (result != null)
		{
			write(result);
			iv.setImageBitmap(result);
		}
		else
		{
			SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(act);
			if(pref.getString("theme", "dark").equals("dark"))
			{
				iv.setImageResource(R.drawable.ic_crop_original_white_48dp);
			}
			else
			{
				iv.setImageResource(R.drawable.ic_crop_original_grey600_48dp);
			}
		}
	}

	protected void write(Bitmap result)
	{
		//TEST_WRITE
		String dirToWrite = act.getFilesDir().getAbsolutePath();
		this.imgAdress = this.imgAdress.replace("-", "_");
		this.imgAdress = this.imgAdress.replace("/", "_");
		this.imgAdress = this.imgAdress.replace(":", "_");
		File dirToWriteFile = new File(dirToWrite + "/images/");
		dirToWriteFile.mkdirs();
		File blogsPageFile = new File(dirToWriteFile + "/" + this.imgAdress);

		if (!blogsPageFile.exists())
		{
			try
			{
				blogsPageFile.createNewFile();
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			try
			{
				FileOutputStream out = new FileOutputStream(blogsPageFile);
				result.compress(Bitmap.CompressFormat.PNG, 90, out);
				out.close();
			} catch (Exception e)
			{
				System.out.println("ERROR iN IMG SAVE: " + e.getLocalizedMessage());
			}
		}
		else
		{
			//System.out.println("Already exists");
		}

		//TEST_WRITE
	}
}