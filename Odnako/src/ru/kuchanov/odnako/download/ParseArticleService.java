package ru.kuchanov.odnako.download;

import java.net.URL;

import org.htmlcleaner.TagNode;

import ru.kuchanov.odnako.FormatHtmlText;
import ru.kuchanov.odnako.HtmlHelper;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class ParseArticleService extends AsyncTask<Void, Integer, String[]>
{

	Context context;
	String urlToLoad;

	String category;
	Integer operationNum;
	Integer numOfOperations;
	
	String htmlToWrite;

	public ParseArticleService(Context context)
	{
		this.context=context;
	}

	public void setVars(String urlToLoad, String category, Integer operationNum, Integer numOfOperations)
	{
		this.urlToLoad = urlToLoad;
		this.category = category;

		this.operationNum = operationNum;
		this.numOfOperations = numOfOperations;
	}

	protected void onPreExecute()
	{
	}

	// Фоновая операция
	protected String[] doInBackground(Void... arg)
	{
		String[] output = null;
		try
		{
			HtmlHelper hh = new HtmlHelper(new URL(this.urlToLoad));
			this.htmlToWrite=hh.htmlString;
			TagNode articleArr = hh.getArcicle();

			TagNode arrEl[] = articleArr.getChildTags();
			output = new String[arrEl.length];

			for (int b = 0; b < arrEl.length; b++)
			{
				if (arrEl[b].getName().equalsIgnoreCase("aside"))
				{
					output[b] = "";
				}
				else
				{
					FormatHtmlText format = new FormatHtmlText();
					output[b] = format.formatNode(arrEl[b]);
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return output;
	}

	// Событие по окончанию парсинга
	protected void onPostExecute(String[] output)
	{
		if (output == null)
		{
			Toast.makeText(context, "Не удалось связаться с odnako.org \n проверьте соединение с интернетом", Toast.LENGTH_LONG).show();
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			int notifyID = 1;
			notificationManager.cancel(notifyID);
		}
		else
		{
//			String artString = "";
//			for (String s : output)
//			{
//				artString += s;
//			}
			//System.out.println(artString);
			WriteFileService write=new WriteFileService(this.context);
			//write.setVars(urlToLoad, artString, category, operationNum, numOfOperations);
			write.setVars(urlToLoad, this.htmlToWrite, category, operationNum, numOfOperations);
			write.execute();
		}

	}
}