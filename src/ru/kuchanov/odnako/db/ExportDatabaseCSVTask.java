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

import com.j256.ormlite.android.apptools.OpenHelperManager;
//import com.opencsv.CSVWriter;

import ru.kuchanov.odnako.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class ExportDatabaseCSVTask extends AsyncTask<Void, Void, File[]>
{

	@Override
	protected File[] doInBackground(Void... params)
	{
		// TODO Auto-generated method stub
		return null;
	}
//	final private static String LOG = ExportDatabaseCSVTask.class.getSimpleName();
//
//	private DataBaseHelper dataBaseHelper;
//
//	String to = "mohax.spb@gmail.com";
//	String subj = "odnako, db, ormlite, table, csv, xls, excel, java, android";
//	String msg = "test";
//
//	//	File file = null;
//
//	private Context ctx;
//
//	public ExportDatabaseCSVTask(Context ctx)
//	{
//		//		Log.e(LOG, "constructor");
//		this.ctx = ctx;
//	}
//
//	@Override
//	protected File[] doInBackground(Void... params)
//	{
//		File dbFile = this.ctx.getDatabasePath(DataBaseHelper.DATABASE_NAME);
//		Log.v(LOG, "Db path is: " + dbFile); //get the path of db
//
//		File[] output = new File[5];
//
//		output[0] = this.getArticleCSV();
//		output[1] = this.getCategoryCSV();
//		output[2] = this.getArtCatTableCSV();
//		output[3] = this.getAuthorCSV();
//		output[4] = this.getArtAutTableCSV();
//
//		return output;
//	}
//	
//	@Override
//	protected void onPostExecute(File[] out)
//	{
//		Log.e(LOG, "onPostExecute");
////		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
//		Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//		//		emailIntent.setType("text/html");
//		//			TODO test 
//		emailIntent.setType("application/csv");
//		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { to });
//		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subj);
//		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg);
//
//		ArrayList<Uri> uris = new ArrayList<Uri>();
//		for (File f : out)
//		{
////			emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
//			uris.add(Uri.fromFile(f));
//		}
//		emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
////		this.ctx.startActivityForResult(Intent.createChooser(emailIntent, "Sending multiple attachment"), 12345);
//
////				emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//		this.ctx.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
//
//		if (dataBaseHelper != null)
//		{
//			OpenHelperManager.releaseHelper();
//			dataBaseHelper = null;
//		}
//	}
//
//	private File getArtAutTableCSV()
//	{
//		try
//		{
//			File exportDir = new File(this.ctx.getExternalFilesDir(null), "");
//			File file = new File(exportDir, "ArtAutTableCSV.csv");
//			file.createNewFile();
//			char separator = "~".charAt(0);
//			CSVWriter csvWrite = new CSVWriter(new FileWriter(file), separator, CSVWriter.NO_QUOTE_CHARACTER);
//
//			List<ArtAutTable> listdata;
//			try
//			{
//				listdata = this.getHelper().getDaoArtAutTable().queryForAll();
//				ArtAutTable data = null;
//
//				// this is the Column of the table and same for Header of CSV file
//				String arrStr1[] = ArtAutTable.getFieldsNames();
//				csvWrite.writeNext(arrStr1);
//
//				if (listdata.size() > 1)
//				{
//					for (int index = 0; index < listdata.size(); index++)
//					{
//						data = listdata.get(index);
//						String arrStr[] = data.getAsStringArray();
//						csvWrite.writeNext(arrStr);
//					}
//				}
//				csvWrite.close();
//
//				return file;
//			} catch (SQLException e)
//			{
//				e.printStackTrace();
//				csvWrite.close();
//				return null;
//			}
//
//		} catch (IOException e)
//		{
//			return null;
//		}
//	}
//
//	private File getAuthorCSV()
//	{
//		try
//		{
//			File exportDir = new File(this.ctx.getExternalFilesDir(null), "");
//			File file = new File(exportDir, "AuthorCSV.csv");
//			file.createNewFile();
//			char separator = "~".charAt(0);
//			CSVWriter csvWrite = new CSVWriter(new FileWriter(file), separator, CSVWriter.NO_QUOTE_CHARACTER);
//
//			List<Author> listdata;
//			try
//			{
//				listdata = this.getHelper().getDaoAuthor().queryForAll();
//				Author data = null;
//
//				// this is the Column of the table and same for Header of CSV file
//				String arrStr1[] = Author.getFieldsNames();
//				csvWrite.writeNext(arrStr1);
//
//				if (listdata.size() > 1)
//				{
//					for (int index = 0; index < listdata.size(); index++)
//					{
//						data = listdata.get(index);
//						String arrStr[] = data.getAsStringArray();
//						csvWrite.writeNext(arrStr);
//					}
//				}
//				csvWrite.close();
//
//				return file;
//			} catch (SQLException e)
//			{
//				e.printStackTrace();
//				csvWrite.close();
//				return null;
//			}
//
//		} catch (IOException e)
//		{
//			return null;
//		}
//	}
//
//	private File getArtCatTableCSV()
//	{
//		try
//		{
//			File exportDir = new File(this.ctx.getExternalFilesDir(null), "");
//			File file = new File(exportDir, "ArtCatTableCSV.csv");
//			file.createNewFile();
//			char separator = "~".charAt(0);
//			CSVWriter csvWrite = new CSVWriter(new FileWriter(file), separator, CSVWriter.NO_QUOTE_CHARACTER);
//
//			List<ArtCatTable> listdata;
//			try
//			{
//				listdata = this.getHelper().getDaoArtCatTable().queryForAll();
//				ArtCatTable data = null;
//
//				// this is the Column of the table and same for Header of CSV file
//				String arrStr1[] = ArtCatTable.getFieldsNames();
//				csvWrite.writeNext(arrStr1);
//
//				if (listdata.size() > 1)
//				{
//					for (int index = 0; index < listdata.size(); index++)
//					{
//						data = listdata.get(index);
//						String arrStr[] = data.getAsStringArray();
//						csvWrite.writeNext(arrStr);
//					}
//				}
//				csvWrite.close();
//
//				return file;
//			} catch (SQLException e)
//			{
//				e.printStackTrace();
//				csvWrite.close();
//				return null;
//			}
//
//		} catch (IOException e)
//		{
//			return null;
//		}
//	}
//
//	private File getCategoryCSV()
//	{
//		try
//		{
//			File exportDir = new File(this.ctx.getExternalFilesDir(null), "");
//			File file = new File(exportDir, "CategoryCSV.csv");
//			file.createNewFile();
//			char separator = "~".charAt(0);
//			CSVWriter csvWrite = new CSVWriter(new FileWriter(file), separator, CSVWriter.NO_QUOTE_CHARACTER);
//
//			List<Category> listdata;
//			try
//			{
//				listdata = this.getHelper().getDaoCategory().queryForAll();
//				Category data = null;
//
//				// this is the Column of the table and same for Header of CSV file
//				String arrStr1[] = Category.getFieldsNames();
//				csvWrite.writeNext(arrStr1);
//
//				if (listdata.size() > 1)
//				{
//					for (int index = 0; index < listdata.size(); index++)
//					{
//						data = listdata.get(index);
//						String arrStr[] = data.getAsStringArray();
//						csvWrite.writeNext(arrStr);
//					}
//				}
//				csvWrite.close();
//
//				return file;
//			} catch (SQLException e)
//			{
//				e.printStackTrace();
//				csvWrite.close();
//				return null;
//			}
//
//		} catch (IOException e)
//		{
//			return null;
//		}
//	}
//
//	private File getArticleCSV()
//	{
//		try
//		{
//			File exportDir = new File(this.ctx.getExternalFilesDir(null), "");
//			File file = new File(exportDir, "ArticleCSV.csv");
//			file.createNewFile();
//			char separator = "~".charAt(0);
//			CSVWriter csvWrite = new CSVWriter(new FileWriter(file), separator, CSVWriter.NO_QUOTE_CHARACTER);
//
//			List<Article> listdata;
//			try
//			{
//				listdata = this.getHelper().getDaoArticle().queryForAll();
//				Article article = null;
//
//				// this is the Column of the table and same for Header of CSV file
//				String arrStr1[] = Article.getFieldsNames();
//				csvWrite.writeNext(arrStr1);
//
//				if (listdata.size() > 1)
//				{
//					for (int index = 0; index < listdata.size(); index++)
//					{
//						article = listdata.get(index);
//						String arrStr[] = article.getAsStringArrayWithAuthorIdIfIs();
//						csvWrite.writeNext(arrStr);
//					}
//				}
//				csvWrite.close();
//
//				return file;
//			} catch (SQLException e)
//			{
//				e.printStackTrace();
//				csvWrite.close();
//				return null;
//			}
//
//		} catch (IOException e)
//		{
//			return null;
//		}
//	}
//
//	
//
//	private DataBaseHelper getHelper()
//	{
//		if (dataBaseHelper == null)
//		{
//			int dbVer=this.ctx.getResources().getInteger(R.integer.db_version);
//			dataBaseHelper = new DataBaseHelper(this.ctx, DataBaseHelper.DATABASE_NAME, null, dbVer);
//		}
//		return dataBaseHelper;
//	}
}
