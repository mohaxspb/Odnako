package ru.kuchanov.odnako.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import ru.kuchanov.odnako.utils.FilenameUtilTest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html.ImageGetter;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

public class URLImageParser implements ImageGetter
{
	//ActionBarActivity act;
	Context context;
	View textView;

	String imgAdress;

	/***
	 * Construct the URLImageParser which will execute AsyncTask and refresh the
	 * container
	 * 
	 * @param t
	 * @param c
	 */
//	public URLImageParser(View t, ActionBarActivity act)
//	{
//		this.act = act;
//		this.textView = t;
//	}
	public URLImageParser(View t, Context context)
	{
		this.context = context;
		this.textView = t;
	}

	public Drawable getDrawable(String source)
	{
		URLDrawable urlDrawable = new URLDrawable();
		
		System.out.println("getDrawable "+source);
		ru.kuchanov.odnako.utils.FilenameUtilTest test=new FilenameUtilTest();
//		test.main(source);
//		System.out.println("getDrawable "+test.main(source));
		//source="http://ru.wikipedia.org/wiki/���������_����������_���������";
		//source="http://kuchanov.ru/upload/������_������_(2).png";
		source=test.main(source);
		System.out.println("getDrawable "+source);

		// get the actual source
		ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask(urlDrawable);

		asyncTask.execute(source);

		// return reference to URLDrawable where I will change with actual image from
		// the src tag
		return urlDrawable;
	}

	public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable>
	{
		URLDrawable urlDrawable;

		public ImageGetterAsyncTask(URLDrawable d)
		{
			this.urlDrawable = d;
		}

		@Override
		protected Drawable doInBackground(String... params)
		{
			String source = params[0];
			imgAdress = source;
			System.out.println("IMAGEGETTER back "+imgAdress);
			return fetchDrawable(source);
		}

		@Override
		protected void onPostExecute(Drawable result)
		{
			if(result!=null)
			{
				DisplayMetrics displaymetrics = new DisplayMetrics();
				//act.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
				((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
				int width = displaymetrics.widthPixels;
				////
				// set the correct bound according to the result from HTTP call
				if (urlDrawable.getIntrinsicWidth() > width)
				{
					urlDrawable.setBounds(0, 0, 0 + width - 20, 0 + result.getIntrinsicHeight());
				}
				else
				{
					urlDrawable.setBounds(0, 0, 0 + result.getIntrinsicWidth(), 0 + result.getIntrinsicHeight());
				}

				// change the reference of the current drawable to the result
				// from the HTTP call
				urlDrawable.drawable = result;

				// redraw the image by invalidating the container
				URLImageParser.this.textView.invalidate();
				// For ICS
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				{
					((TextView) URLImageParser.this.textView).setHeight((URLImageParser.this.textView.getHeight() + result.getIntrinsicHeight()));
				}
				else
				{
					((TextView) URLImageParser.this.textView).setEllipsize(null);
				}
			}
			else
			{
				System.out.println("result of imgLoad to TextView is null!");
			}
		}

		/***
		 * Get the Drawable from URL
		 * 
		 * @param urlString
		 * @return
		 */
		public Drawable fetchDrawable(String urlString)
		{
			System.out.println("fetchDrawable start");
			Drawable drawable;

			//String dirToWrite = act.getFilesDir().getAbsolutePath();
			String dirToWrite = context.getFilesDir().getAbsolutePath();
			String formatedImgAdress;
			//formatedImgAdress = imgAdress.replace("-", "_");
			formatedImgAdress=urlString.replace("-", "_");
			formatedImgAdress = formatedImgAdress.replace("/", "_");
			formatedImgAdress = formatedImgAdress.replace(":", "_");
			File dirToWriteFile = new File(dirToWrite + "/images/");
			File blogsPageFile = new File(dirToWriteFile + "/" + formatedImgAdress);

			if (!blogsPageFile.exists())
			{
				try
				{
					InputStream is = fetch(urlString);
					drawable = Drawable.createFromStream(is, "src");
					DisplayMetrics displaymetrics = new DisplayMetrics();
					//act.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
					((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
					int width = displaymetrics.widthPixels;
					if (drawable.getIntrinsicWidth() > width)
					{
						drawable.setBounds(0, 0, 0 + width - 20, 0 + drawable.getIntrinsicHeight());
					}
					else
					{
						drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(), 0 + drawable.getIntrinsicHeight());
					}
					
					Bitmap resultAsBitmap = ((BitmapDrawable) drawable).getBitmap();
					System.out.println("IMAGEGETTER post "+imgAdress);
					write(resultAsBitmap);

					return drawable;
				} catch (Exception e)
				{
					System.out.println("fetchDrawable error in loadImg: "+e.getLocalizedMessage());
					return null;
				}
			}
			else
			{
				try
				{
					System.out.println("fetchDrawable from file");
					//drawable = BitmapDrawable.createFromPath(blogsPageFile.getPath());
					drawable=Drawable.createFromPath(blogsPageFile.getPath());
					DisplayMetrics displaymetrics = new DisplayMetrics();
					//act.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
					((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
					int width = displaymetrics.widthPixels;
					if (drawable.getIntrinsicWidth() > width)
					{
						drawable.setBounds(0, 0, 0 + width - 20, 0 + drawable.getIntrinsicHeight());
					}
					else
					{
						drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(), 0 + drawable.getIntrinsicHeight());
					}
					return drawable;
				} catch (Exception e)
				{
					System.out.println("fetchDrawable from file catch");
					return null;
				}
				
			}
			
		}

		private InputStream fetch(String urlString) throws MalformedURLException, IOException
		{
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(urlString);
			HttpResponse response = httpClient.execute(request);
			return response.getEntity().getContent();
		}
	}

	protected void write(Bitmap result)
	{
		//TEST_WRITE
		//String dirToWrite = act.getFilesDir().getAbsolutePath();
		String dirToWrite = context.getFilesDir().getAbsolutePath();
		String formatedImgAdress;
		formatedImgAdress = imgAdress.replace("-", "_");
		formatedImgAdress = formatedImgAdress.replace("/", "_");
		formatedImgAdress = formatedImgAdress.replace(":", "_");
		File dirToWriteFile = new File(dirToWrite + "/images/");
		dirToWriteFile.mkdirs();
		File blogsPageFile = new File(dirToWriteFile + "/" + formatedImgAdress);
		//File blogsPageFile = new File(dirToWriteFile +  formatedImgAdress);
		System.out.println("IMAGEGETTER"+formatedImgAdress);
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
