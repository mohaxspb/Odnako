package ru.kuchanov.odnako.download;

import java.net.URL;
import java.util.ArrayList;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;

public class ParseForAllAuthors extends AsyncTask<String, Integer, ArrayList<ArrayList<String>>>
{

	ActionBarActivity act;

	public ParseForAllAuthors(ActionBarActivity act)
	{
		this.act = act;
	}

	@Override
	protected ArrayList<ArrayList<String>> doInBackground(String... url)
	{
		ArrayList<ArrayList<String>> output = null;
		try
		{
			HtmlHelper hh = new HtmlHelper(new URL(url[0]));
			output = hh.getAllAuthorsAsList();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return output;
	}

	protected void onPostExecute(ArrayList<ArrayList<String>> output)
	{
		System.out.println("ParseForAllAuthors: onPostExecute");

		if (output != null)
		{
			if (!output.get(0).get(0).equals("empty"))
			{

				String date = (String.valueOf(System.currentTimeMillis()));
				//name
				String dataToWrite = "<html name='all_authors_names' date='" + date + "'>\n";
				for (int i = 0; i < output.size(); i++)
				{
					dataToWrite = dataToWrite.concat("<item><![CDATA[" + output.get(i).get(0) + "]]></item>\n");
				}
				dataToWrite = dataToWrite.concat("</html>");
				WriteFile write = new WriteFile(dataToWrite, "allAuthors", "all_authors_names.txt", act);
				write.execute();
				////
				//url
				String dataToWrite1 = "<html name='all_authors_urls' date='" + date + "'>\n";
				for (int i = 0; i < output.size(); i++)
				{
					dataToWrite1 = dataToWrite1.concat("<item><![CDATA[" + output.get(i).get(1) + "]]></item>\n");
				}
				dataToWrite1 = dataToWrite1.concat("</html>");
				WriteFile write1 = new WriteFile(dataToWrite1, "allAuthors", "all_authors_urls.txt", act);
				write1.execute();
				////
				//img
				String dataToWrite2 = "<html name='all_authors_imgs' date='" + date + "'>\n";
				for (int i = 0; i < output.size(); i++)
				{
					dataToWrite2 = dataToWrite2.concat("<item><![CDATA[" + output.get(i).get(2) + "]]></item>\n");
				}
				dataToWrite2 = dataToWrite2.concat("</html>");
//				dataToWrite2=dataToWrite2.replaceAll("-", "–");
				WriteFile write2 = new WriteFile(dataToWrite2, "allAuthors", "all_authors_imgs.txt", act);
				write2.execute();
				////
				//description
				String dataToWrite3 = "<html name='all_authors_descriptions' date='" + date + "'>\n";
				for (int i = 0; i < output.size(); i++)
				{
					dataToWrite3 = dataToWrite3.concat("<item><![CDATA[" + output.get(i).get(3) + "]]></item>\n");
				}
				dataToWrite3 = dataToWrite3.concat("</html>");
				WriteFile write3 = new WriteFile(dataToWrite3, "allAuthors", "all_authors_descriptions.txt", act);
				write3.execute();
				////
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