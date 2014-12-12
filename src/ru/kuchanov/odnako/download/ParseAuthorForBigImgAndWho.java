package ru.kuchanov.odnako.download;

import java.net.URL;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;

public class ParseAuthorForBigImgAndWho extends AsyncTask<Void, Integer, String[][]>
{

	ActionBarActivity act;
	String[] allAuthorsUrls;

	public ParseAuthorForBigImgAndWho(ActionBarActivity act, String[] allAuthorsUrls)
	{
		this.act = act;
		this.allAuthorsUrls = allAuthorsUrls;
	}

	@Override
	protected String[][] doInBackground(Void... args)
	{
		String[][] output = null;

		output = new String[2][allAuthorsUrls.length];
		for (int i = 0; i < allAuthorsUrls.length; i++)
		{
			try
			{
				HtmlHelper hh = new HtmlHelper(new URL(allAuthorsUrls[i]));

				output[0][i] = hh.getAuthorsWho();
				output[1][i] = hh.getAuthorsBigImg();
			} catch (Exception e)
			{
				e.printStackTrace();
				output[0][i] = "empty";
				output[1][i] = "empty";
			}
		}
		return output;
	}

	protected void onPostExecute(String[][] output)
	{
		System.out.println("ParseAuthor onPostExecute");
		//check if there is no internet and so nothing to parse
		if (output != null)
		{
			String date = (String.valueOf(System.currentTimeMillis()));

			String dataToWrite = "<html name='all_authors_who' date='" + date + "'>\n";
			for (int i = 0; i < output[0].length; i++)
			{
				dataToWrite = dataToWrite.concat("<item><![CDATA[" + output[0][i] + "]]></item>\n");
			}
			dataToWrite = dataToWrite.concat("</html>");
			WriteFile write = new WriteFile(dataToWrite, "allAuthors", "all_authors_who.txt", act);
			write.execute();

			String dataToWrite1 = "<html name='all_authors_big_imgs' date='" + date + "'>\n";
			for (int i = 0; i < output[1].length; i++)
			{
				dataToWrite1 = dataToWrite1.concat("<item><![CDATA[" + output[1][i] + "]]></item>\n");
			}
			dataToWrite1 = dataToWrite1.concat("</html>");
			WriteFile write1 = new WriteFile(dataToWrite1, "allAuthors", "all_authors_big_imgs.txt", act);
			write1.execute();
		}
		else
		{
			System.out.println("output=null  So  no internet!");
		}

	}

}