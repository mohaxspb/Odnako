/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.download;

import java.util.Date;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.DataBaseHelper;
import ru.kuchanov.odnako.db.ServiceArticle;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ParseArticle extends AsyncTask<Void, Void, Article>
{
	private final static String LOG = ParseArticle.class.getSimpleName() + "/";

	private String url;
	private Context ctx;
	private DataBaseHelper h;
	boolean forceDownLoad;

	public ParseArticle(Context ctx, String url, DataBaseHelper h, boolean forceDownLoad)
	{
		this.ctx = ctx;
		this.url = url;
		this.h = h;
		this.forceDownLoad = forceDownLoad;
	}

	protected Article doInBackground(Void... arg)
	{
		Article article = null;
		String link;
		if (this.getUrl().startsWith("http://"))
		{
			link = this.getUrl();
		}
		else
		{
			link = "http://" + this.getUrl();
		}

		try
		{
			//check if we already have article obj with artText!=Const.EMPTY_STRING or we have to forceDownload
			Article artInDB = null;
			artInDB = Article.getArticleByURL(h, this.url);
			if (artInDB == null || artInDB.getArtText().equals(Const.EMPTY_STRING) || forceDownLoad)
			{
				//start download
				HtmlHelper hh = new HtmlHelper(link);
				if (hh.isLoadSuccessfull())
				{
					article = hh.parseArticle(h);

					//here if article with given URL don't exists, we create it.
					if (artInDB == null)
					{
						h.getDaoArticle().create(article);
					}
					else
					{
						Date downLoadedDate = new Date(article.getPubDate().getTime());
						Date dBDate = new Date(artInDB.getPubDate().getTime());

						//preview
						//String previewDown=article.getPreview();
						String previewDB = artInDB.getPreview();

						article.setId(artInDB.getId());
						h.getDaoArticle().update(article);
						//now we can simply check if downloaded date if bigger then inDB 
						//and if it's not we must return bigger date to DB entry
						//This is because downloaded date always bigger then zero date
						//and always smaller, if inDB date has HH:mm bigger then 00:00
						if (downLoadedDate.getTime() < dBDate.getTime())
						{
							Article.updatePubDate(h, artInDB.getId(), dBDate);
						}
						if (!previewDB.equals(Const.EMPTY_STRING))
						{
							Article.updatePreview(h, artInDB.getId(), previewDB);
						}
					}
				}
				else
				{
					//connection (?) error
					return null;
				}
			}
			else
			{
				//send article from DB
				Log.i(LOG, url + " LOADED FROM DB");
				article = artInDB;
			}

		} catch (Exception e)
		{
			//Log.e(LOG + getUrl(), "Catched Exception: " + e.toString());
			e.printStackTrace();
		}
		return article;
	}

	protected void onPostExecute(Article article)
	{
		//check internet
		if (article != null)
		{
			//			if (this.f != null)
			//			{
			//				this.f.update(article);
			//				this.h.close();
			//			}
			//			else
			//			{
			ServiceArticle.sendDownloadedData(ctx, article, url);
			//			}
		}
		//NO internet
		else
		{
			ServiceArticle.sendErrorMsg(ctx, url, Const.Error.CONNECTION_ERROR);
			Log.e(LOG + getUrl(), Const.Error.CONNECTION_ERROR);
		}
	}// Событие по окончанию парсинга

	@Override
	protected void onCancelled()
	{
		Log.d(LOG + getUrl(), String.format("onCancelled"));
		super.onCancelled();
	}

	public String getUrl()
	{
		return url;
	}
}