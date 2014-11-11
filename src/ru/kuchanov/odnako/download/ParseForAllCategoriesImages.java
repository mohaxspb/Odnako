package ru.kuchanov.odnako.download;

import java.net.URL;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;

public class ParseForAllCategoriesImages extends AsyncTask<String, Integer, String>
{

	ActionBarActivity act;
	
	String catUrl;
	String cataImgUrl;

	public ParseForAllCategoriesImages(ActionBarActivity act)
	{
		this.act = act;
	}

	@Override
	protected String doInBackground(String... url)
	{
		String output = null;
		try
		{
			catUrl=url[0];
			HtmlHelper hh = new HtmlHelper(new URL(url[0]));
			output=hh.getCategoryImage();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return output;
	}

	protected void onPostExecute(String output)
	{
//		System.out.println("ParseForAllCategories onPostExecute");
		//check if there is no internet and so nothing to parse
		if(output!=null)
		{
			if (output.startsWith("/"))
			{
				this.cataImgUrl = "http://odnako.org" + output;
			}

			else
			{
				this.cataImgUrl = output;
			}
			
			
			String dataToWrite = "<item><![CDATA[" + output + "]]></item>\n";
			WriteFile write = new WriteFile(dataToWrite, "allCategoriesImgs", "all_category_imgs.txt", act);
			write.execute();
			
			DownloadImageTask downImg=new DownloadImageTask(act);
			downImg.execute(output);
		}
		else
		{
			System.out.println("output=null  So  no internet!");
		}
	}
}