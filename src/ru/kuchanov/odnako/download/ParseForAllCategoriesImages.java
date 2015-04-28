package ru.kuchanov.odnako.download;

import android.content.Context;
import android.os.AsyncTask;

public class ParseForAllCategoriesImages extends AsyncTask<String, Integer, String>
{
	Context ctx;
	
	String catUrl;
	String cataImgUrl;

	public ParseForAllCategoriesImages(Context ctx)
	{
		this.ctx = ctx;
	}

	@Override
	protected String doInBackground(String... url)
	{
		String output = null;
		try
		{
			catUrl=url[0];
			HtmlHelper hh = new HtmlHelper(url[0]);
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
			WriteFile write = new WriteFile(dataToWrite, "allCategoriesImgs", "all_category_imgs.txt", ctx);
			write.execute();
			
			DownloadImageTask downImg=new DownloadImageTask(ctx);
			downImg.execute(output);
		}
		else
		{
			System.out.println("output=null  So  no internet!");
		}
	}
}