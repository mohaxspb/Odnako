package ru.kuchanov.odnako.download;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

public class ParseForAllCategories extends AsyncTask<String, Integer, ArrayList<ArrayList<String>>>
{
	Context ctx;

	public ParseForAllCategories(Context ctx)
	{
		this.ctx = ctx;
	}

	@Override
	protected ArrayList<ArrayList<String>> doInBackground(String... url)
	{
		ArrayList<ArrayList<String>> output = null;
		try
		{
			HtmlHelper hh = new HtmlHelper(url[0]);
			output = hh.getAllCategoriesAsList();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return output;
	}

	protected void onPostExecute(ArrayList<ArrayList<String>> output)
	{
		System.out.println("ParseForAllCategories onPostExecute");
		//check if there is no internet and so nothing to parse
		if (output != null)
		{
			if (!output.get(0).get(0).equals("empty"))
			{
				String date = (String.valueOf(System.currentTimeMillis()));

				String dataToWrite = "<html name='all_categories_titles' date='" + date + "'>\n";
				for (int i = 0; i < output.size(); i++)
				{
					dataToWrite = dataToWrite.concat("<item>" + output.get(i).get(0) + "</item>\n");
				}
				dataToWrite = dataToWrite.concat("</html>");
				WriteFile write = new WriteFile(dataToWrite, "allCategories", "all_categories_titles.txt", ctx);
				write.execute();

				String dataToWrite1 = "<html name='all_categories_urls' date='" + date + "'>\n";
				for (int i = 0; i < output.size(); i++)
				{
					dataToWrite1 = dataToWrite1.concat("<item>" + output.get(i).get(1) + "</item>\n");
				}
				dataToWrite1 = dataToWrite1.concat("</html>");
				WriteFile write1 = new WriteFile(dataToWrite1, "allCategories", "all_categories_urls.txt", ctx);
				write1.execute();
			}
			else
			{
				System.out.println("output.get(0).get(0).equals('empty')=true !!!! So Parsing Error!!!");
			}
		}
		else
		{
			System.out.println("output=null  So  no internet!");
		}
	}
}