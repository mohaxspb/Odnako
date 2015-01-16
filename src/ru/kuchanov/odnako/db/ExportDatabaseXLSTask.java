/*
 13.01.2015
ExportDatabaseCSVTask.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//import com.aspose.cells.Cells;
//import com.aspose.cells.Workbook;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import ru.kuchanov.odnako.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class ExportDatabaseXLSTask extends AsyncTask<Void, Void, File[]>
{
	final private static String LOG = ExportDatabaseXLSTask.class.getSimpleName();

	private DataBaseHelper dataBaseHelper;

	String to = "mohax.spb@gmail.com";
	String subj = "odnako, db, ormlite, table, csv, xls, excel, java, android";
	String msg = "test";

	private Context ctx;

	public ExportDatabaseXLSTask(Context ctx)
	{
		//		Log.e(LOG, "constructor");
		this.ctx = ctx;
	}

	@Override
	protected File[] doInBackground(Void... params)
	{
		File dbFile = this.ctx.getDatabasePath(DataBaseHelper.DATABASE_NAME);
		Log.v(LOG, "Db path is: " + dbFile); //get the path of db

		//TODO SIZE!
		File[] output = new File[1];

		output[0] = this.getArticleXLS();
		//		output[1] = this.getCategoryCSV();
		//		output[2] = this.getArtCatTableCSV();
		//		output[3] = this.getAuthorCSV();
		//		output[4] = this.getArtAutTableCSV();

		return output;
	}

	@Override
	protected void onPostExecute(File[] out)
	{
		Log.e(LOG, "onPostExecute");
		//		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		emailIntent.setType("text/html");
		//			TODO test 
		//		emailIntent.setType("application/csv");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { to });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subj);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg);

		ArrayList<Uri> uris = new ArrayList<Uri>();
		for (File f : out)
		{
			//			emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
			uris.add(Uri.fromFile(f));
		}
		emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		//		this.ctx.startActivityForResult(Intent.createChooser(emailIntent, "Sending multiple attachment"), 12345);

		//				emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		this.ctx.startActivity(Intent.createChooser(emailIntent, "Send mail..."));

		if (dataBaseHelper != null)
		{
			OpenHelperManager.releaseHelper();
			dataBaseHelper = null;
		}
	}

//	private File getArtAutTableCSV()
//	{
//	
//	}
//
//	private File getAuthorCSV()
//	{
//		
//	}
//
//	private File getArtCatTableCSV()
//	{
//		
//	}
//
//	private File getCategoryCSV()
//	{
//		
//	}

	private File getArticleXLS()
	{
		return null;//TODO delete me!
//		try
//		{
//			File exportDir = new File(this.ctx.getExternalFilesDir(null), "");
//
//			String path = exportDir.getCanonicalPath();
//
//			//Instantiate a Workbook object.
//			Workbook workbook = new Workbook();
//			//Get the first worksheet's cells in the book.
//			Cells cells = workbook.getWorksheets().get(0).getCells();
//
//			//get column names from DB table
//			String[] columnNames = Article.getFieldsNames();
//
//			//get DB entries
//			List<Article> listdata;
//			try
//			{
//				listdata = this.getHelper().getDaoArticle().queryForAll();
//			} catch (SQLException e)
//			{
//				Log.e(LOG, e.getMessage());
//				return null;
//			}
//
//			//insert table data
//			for (int u = 0; u < listdata.size(); u++)
//			{
//				if(u==0)
//				{
//					//insert column names
//					for (int i = 0; i < columnNames.length; i++)
//					{
//						cells.get(u, i).setValue(columnNames[i]);
//					}
//				}
//				else
//				{
//					//insert data
//					for (int i = 0; i < columnNames.length; i++)
//					{
//						cells.get(u, i).setValue(listdata.get(u).getAsStringArrayWithAuthorIdIfIs()[i]);
//					}
//				}
//			}
//
//			//Save the Excel file.
//			workbook.save(path + "/ArticleXLS.xls");
//
//			File file = new File(path + "/ArticleXLS.xls");
//
//			return file;
//
//		} catch (IOException e)
//		{
//			Log.e(LOG, e.getMessage());
//			return null;
//		} catch (Exception e)
//		{
//			Log.e(LOG, e.getMessage());
//			return null;
//		}
	}

	private DataBaseHelper getHelper()
	{
		if (dataBaseHelper == null)
		{
			int dbVer = this.ctx.getResources().getInteger(R.integer.db_version);
			dataBaseHelper = new DataBaseHelper(this.ctx, DataBaseHelper.DATABASE_NAME, null, dbVer);
		}
		return dataBaseHelper;
	}
}
