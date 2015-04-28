package ru.kuchanov.odnako.download;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

public class ParseForAllAuthors extends AsyncTask<Void, Integer, ArrayList<ArrayList<String>>>
{

	Context ctx;

	public ParseForAllAuthors(Context ctx)
	{
		this.ctx = ctx;
	}

	@Override
	protected ArrayList<ArrayList<String>> doInBackground(Void... args)
	{
		ArrayList<ArrayList<String>> output = null;
		try
		{
			HtmlHelper hh = new HtmlHelper("http://odnako.org/authors/");
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
				WriteFile write = new WriteFile(dataToWrite, "allAuthors", "all_authors_names.txt", ctx);
				write.execute();
				////
				//url
				String dataToWrite1 = "<html name='all_authors_urls' date='" + date + "'>\n";
				for (int i = 0; i < output.size(); i++)
				{
					dataToWrite1 = dataToWrite1.concat("<item><![CDATA[" + output.get(i).get(1) + "]]></item>\n");
				}
				dataToWrite1 = dataToWrite1.concat("</html>");
				WriteFile write1 = new WriteFile(dataToWrite1, "allAuthors", "all_authors_urls.txt", ctx);
				write1.execute();
				////
				//img
				String dataToWrite2 = "<html name='all_authors_imgs' date='" + date + "'>\n";
				for (int i = 0; i < output.size(); i++)
				{
					dataToWrite2 = dataToWrite2.concat("<item><![CDATA[" + output.get(i).get(2) + "]]></item>\n");
				}
				dataToWrite2 = dataToWrite2.concat("</html>");
				WriteFile write2 = new WriteFile(dataToWrite2, "allAuthors", "all_authors_imgs.txt", ctx);
				write2.execute();
				////
				//description
				String dataToWrite3 = "<html name='all_authors_descriptions' date='" + date + "'>\n";
				for (int i = 0; i < output.size(); i++)
				{
					dataToWrite3 = dataToWrite3.concat("<item><![CDATA[" + output.get(i).get(3) + "]]></item>\n");
				}
				dataToWrite3 = dataToWrite3.concat("</html>");
				WriteFile write3 = new WriteFile(dataToWrite3, "allAuthors", "all_authors_descriptions.txt", ctx);
				write3.execute();
				////

				//start downLoad all Big img and WHO of all Authors
				String[] allAuthorsUrls = new String[output.size()];
				for(int i=0; i<allAuthorsUrls.length; i++)
				{
					allAuthorsUrls[i]=output.get(i).get(1);
				}
				ParseAuthorForBigImgAndWho parse = new ParseAuthorForBigImgAndWho(ctx, allAuthorsUrls);
				parse.execute();
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