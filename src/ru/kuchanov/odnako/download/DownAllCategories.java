package ru.kuchanov.odnako.download;

import java.net.URL;

import org.htmlcleaner.TagNode;

import ru.kuchanov.odnako.HtmlHelper;
import ru.kuchanov.odnako.MainActivityNew;
import ru.kuchanov.odnako.R;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.widget.Toast;

public class DownAllCategories extends AsyncTask<Void, String, String[][][]>
{

	Context ctx;
	Intent intent;

	NotificationManager notificationManager;
	NotificationCompat.Builder builder;

	String[] categorysToLoad;
	int numToLoad;
	int numOfPagesToLoad;

	String linkToWrite;
	String data;

	int count = 0;

	public DownAllCategories(Context ctx, String[] categorysToLoad, int numToLoad, int numOfPagesToLoad, Intent intent)
	{
		this.ctx = ctx;
		this.categorysToLoad = categorysToLoad;
		this.numToLoad = numToLoad;
		this.numOfPagesToLoad = numOfPagesToLoad;
		this.intent = intent;
	}

	@Override
	protected void onPreExecute()
	{
		System.out.println("DownAllCategories: onPreExecute");
		notificateLaunch();
	}

	@Override
	protected String[][][] doInBackground(Void... arg)
	{
		System.out.println("DownAllCategories: doInBackground");
		String[][][] output = null;
		output = new String[categorysToLoad.length][][];

		for (int i = 0; i < categorysToLoad.length; i++)
		{
			output[i] = parseCategoryForLinks(categorysToLoad[i], 1);
			if (output[i] != null)
			{
				for (int ii = 0; ii < output[i].length; ii++)
				{
					String artString = parceArticle(output[i][ii][0]);
					if (artString != null)
					{
						WriteFile write = new WriteFile(ctx);
						write.setVars(output[i][ii][0], artString, categorysToLoad[i]);
						write.write();

						this.count++;
						this.onProgressUpdate(output[i][ii][1]);
					}
					else
					{
						Runnable run = new Runnable()
						{
							Handler handler = new Handler(Looper.getMainLooper());

							@Override
							public void run()
							{
								handler.post(new Runnable()
								{
									@Override
									public void run()
									{
										Toast.makeText(ctx, "Загрузка прервана, проверьте соединение с интернетом", Toast.LENGTH_SHORT).show();
									}
								});
							}
						};
						run.run();
						this.cancel(true);
						ctx.stopService(intent);
						return null;
					}
				}
			}
			else
			{
				Runnable run = new Runnable()
				{
					Handler handler = new Handler(Looper.getMainLooper());

					@Override
					public void run()
					{
						handler.post(new Runnable()
						{
							@Override
							public void run()
							{
								Toast.makeText(ctx, "Загрузка прервана, проверьте соединение с интернетом", Toast.LENGTH_SHORT).show();
							}
						});
					}
				};
				run.run();
				this.cancel(true);
				ctx.stopService(intent);
				return null;
			}
		}
		System.out.println("DownAllCategories: doInBackGround END!!!");
		return output;
	}

	@Override
	protected void onProgressUpdate(String... update)
	{
		if (update != null)
		{
			notificateUpdate(update[0], count);
		}
	}

	@Override
	protected void onPostExecute(String[][][] output)
	{
		System.out.println("DownAllCategories: onPostExecute");
		//this.cancel(true);
		ctx.stopService(intent);
	}// Событие по окончанию парсинга

	public String[][] parseCategoryForLinks(String catToLoad, int curPageToLoad)
	{
		System.out.println("ParseBlogsPageService: doInBackground");
		String[][] output = null;
		String linkToWrite;
		String data;
		String link = "http://" + catToLoad + "/page-" + String.valueOf(curPageToLoad) + "/";
		//System.out.println(link);

		try
		{
			HtmlHelper hh = new HtmlHelper(new URL(link));
			TagNode[] liElemsArr = hh.getBlogsInfo();

			linkToWrite = link;
			data = hh.htmlString;
			//writeBlogsPageToDevice
			WriteFile write = new WriteFile(ctx);
			write.setVars(linkToWrite, data, catToLoad);
			write.write();
			//System.out.println(linkToWrite);
			//System.out.println(data);

			output = new String[MainActivityNew.DEFAULT_NUM_OF_ARTS_ON_PAGE][MainActivityNew.NUM_OF_ELEMS_IN_DIV];
			for (int i = 0; i < liElemsArr.length; i++)
			{
				TagNode element = liElemsArr[i];
				TagNode element1 = element.findElementByAttValue("class", "m-news-info", true, true);
				TagNode element2 = element1.findElementByAttValue("class", "m-news-text", true, true);
				TagNode element3 = element2.findElementByName("a", true);

				TagNode[] imgEl = element.getElementsByName("img", true);

				TagNode author = element.findElementByAttValue("class", "m-news-author-wrap", true, false);
				TagNode[] author1 = author.getElementsByName("a", true);

				output[i][0] = element3.getAttributeByName("href").toString();
				output[i][1] = Html.fromHtml(element3.getAttributeByName("title").toString()).toString();
				//System.out.println(output[i][1]);

				if (imgEl.length == 0)
				{
					output[i][2] = "default";
				}
				else
				{
					output[i][2] = imgEl[0].getAttributeByName("src").toString();
				}
				if (author1.length == 0)
				{
					output[i][3] = "default";
					output[i][4] = "default";
				}
				else
				{
					output[i][3] = author1[0].getAttributeByName("href");
					output[i][4] = Html.fromHtml(author1[0].getAttributeByName("title")).toString();
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return output;
	}

	String parceArticle(String url)
	{
		//		String[] output = null;
		//		String artString = "";
		//		try
		//		{
		//			HtmlHelper hh = new HtmlHelper(new URL(url));
		//			TagNode articleArr = hh.getArcicle();
		//
		//			TagNode arrEl[] = articleArr.getChildTags();
		//			output = new String[arrEl.length];
		//
		//			for (int b = 0; b < arrEl.length; b++)
		//			{
		//				if (arrEl[b].getName().equalsIgnoreCase("aside"))
		//				{
		//					output[b] = "";
		//				}
		//				else
		//				{
		//					FormatHtmlText format = new FormatHtmlText();
		//					output[b] = format.formatNode(arrEl[b]);
		//				}
		//			}
		//
		//			for (String s : output)
		//			{
		//				artString += s;
		//			}
		//		} catch (Exception e)
		//		{
		//			e.printStackTrace();
		//			return null;
		//		}
		//		return artString;

		String artString = "";
		try
		{
			HtmlHelper hh = new HtmlHelper(new URL(url));
			artString=hh.htmlString;
			
		} catch (Exception e)
		{
			artString=null;

		}
		return artString;
	}

	void notificateLaunch()
	{
		notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;
		CharSequence notiText = "Загрузка статей";

		Intent notificationIntent = new Intent(ctx, Downloadings.class);
		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);
		int SERVER_DATA_RECEIVED = 10;

		builder = new NotificationCompat.Builder(ctx);
		builder.setSmallIcon(icon).setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), icon)).setContentTitle(notiText).setContentText(notiText).setContentIntent(contentIntent);

		notificationManager.notify(SERVER_DATA_RECEIVED, builder.build());
	}

	void notificateUpdate(String update, int count)
	{
		int SERVER_DATA_RECEIVED = 10;

		builder.setContentText(update);
		builder.setContentTitle("Загрузка статей: " + count + "/" + 270);

		notificationManager.notify(SERVER_DATA_RECEIVED, builder.build());
	}
}
