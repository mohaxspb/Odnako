package ru.kuchanov.odnako;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import org.htmlcleaner.TagNode;

import ru.kuchanov.odnako.download.WriteFileService;
import ru.kuchanov.odnako.utils.FormatURLToFileName;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.widget.Toast;

public class ParseBlogsPage extends AsyncTask<Void, Void, String[][]>
{
	ActionBarActivity act;
	ProgressDialog pd;
	Boolean lastRefreshedArtListExists;

	String categoryToLoad;
	int pageToLoad;
	Integer INITIALpageToLoad;
	boolean fromBottomPull;

	String link;
	String data;

	PullToRefreshListView pullToRefreshView;

	SharedPreferences pref;

	public ParseBlogsPage(ActionBarActivity act, String categoryToLoad, int pageToLoad, PullToRefreshListView pullToRefreshView, boolean fromBottomPull)
	{
		this.act = act;
		this.categoryToLoad = categoryToLoad;
		this.pageToLoad = pageToLoad;
		this.pullToRefreshView = pullToRefreshView;
		this.fromBottomPull = fromBottomPull;
		
		pref = PreferenceManager.getDefaultSharedPreferences(act);
	}

	protected void onPreExecute()
	{
		System.out.println("ParceBlogsPageNew: onPreExecute");
	}

	protected String[][] doInBackground(Void... arg)
	{
		System.out.println("ParseBlogsPageNew: doInBackground");
		System.out.println("pageToLoad: " + pageToLoad);
		String[][] output = null;
		if (pageToLoad != 0)
		{
			if (this.categoryToLoad.equals("www.odnako.org/authors"))
			{
				link = "http://" + this.categoryToLoad + "/";
			}
			else
			{
				link = "http://" + this.categoryToLoad + "/page-" + String.valueOf(this.pageToLoad) + "/";
			}
		}
		else
		{
			String storageDir = pref.getString("filesDir", "");
			String formatedCategory = FormatURLToFileName.format(this.categoryToLoad);
			String formatedPageName;
			if (this.categoryToLoad.equals("www.odnako.org/authors"))
			{
				formatedPageName = FormatURLToFileName.format(this.categoryToLoad + "/");
			}
			else
			{
				formatedPageName = FormatURLToFileName.format(this.categoryToLoad + "/page-1/");
			}
			link = "file:///" + storageDir + "/" + formatedCategory + "/" + "http___" + formatedPageName;
			INITIALpageToLoad = pageToLoad;
			pageToLoad = 1;
		}
		try
		{
			//check allAutorsCachedFile

			if (this.categoryToLoad.equals("www.odnako.org/authors"))
			{
				String storageDir;
				storageDir = pref.getString("filesDir", "");
				String formatedCategory = FormatURLToFileName.format(this.categoryToLoad);
				String formatedPageName = FormatURLToFileName.format(this.categoryToLoad + "/");

				String linkToLocalFile;
				linkToLocalFile = storageDir + "/" + formatedCategory + "/" + "http___" + formatedPageName;

				File file = new File(linkToLocalFile);
				System.out.println(file.exists());
				System.out.println(file.getAbsolutePath());
				System.out.println(linkToLocalFile);
				if (file.exists())
				{
					link = "file:///" + linkToLocalFile;
					System.out.println(link);
				}
				else
				{
					System.out.println("File of AllAuthors not found!");
				}
			}
			//check allAutorsCachedFile
			HtmlHelper hh = new HtmlHelper(new URL(link));
			//TagNode[] liElemsArr = hh.getBlogsInfo();

			//TEST
			ArrayList<ArrayList<String>> groupsLinks = new ArrayList<ArrayList<String>>();
			//ArrayList<ArrayList<String>> groups = new ArrayList<ArrayList<String>>();
			FillMenuList fillMenuList = new FillMenuList();
			fillMenuList.setActivity(act);
			groupsLinks = fillMenuList.getGroupsLinks();
			//groups=fillMenuList.getGroups();

			ArrayList<String> allCategotyLinks = new ArrayList<String>();
			allCategotyLinks = groupsLinks.get(1);//+groups.get(2)+groups.get(3);
			String[] menuCatLinks = act.getResources().getStringArray(R.array.menu_items_links);
			allCategotyLinks.add(menuCatLinks[2]);
			allCategotyLinks.add(menuCatLinks[3]);

			TagNode[] liElemsArr = null;

			for (int i = 0; i < allCategotyLinks.size(); i++)
			{
				if (this.categoryToLoad.equals(allCategotyLinks.get(i)))
				{
					liElemsArr = hh.getBlogsInfo();
					i = allCategotyLinks.size();
				}
				else
				{
					if (i == allCategotyLinks.size() - 1)
					{
						//liElemsArr = hh.getBlogsInfo();
						System.out.println("AUTORS category");
						if (this.categoryToLoad.equals("www.odnako.org/authors"))
						{
							System.out.println("AUTORS category ALL");
							liElemsArr = hh.getAllAuthorsInfo();
							output = hh.getAllAuthorsArtsList(liElemsArr, act.getSupportActionBar().getTitle().toString());
						}
						else
						{
							System.out.println("AUTORS category ONE");
							liElemsArr = hh.getAuthorsInfo();
							output = hh.getAuthorsArtsList(liElemsArr, act.getSupportActionBar().getTitle().toString());
						}
					}
				}
			}

			//TagNode[] liElemsArr;//=null;// = hh.getBlogsInfo();
			//TEST
			data = hh.htmlString;

			if (output == null)
			{
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
						output[i][3] = author1[0].getAttributeByName("href").substring(7, author1[0].getAttributeByName("href").length() - 1);
						output[i][4] = Html.fromHtml(author1[0].getAttributeByName("title")).toString();
					}
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return output;
	}

	protected void onPostExecute(String[][] output)
	{
		System.out.println("ParseBlogsPageNew: onPostExecute");
		//Cancel refreshing animation & restore MODE of pullToRefresh view
		this.pullToRefreshView.onRefreshComplete();
		pullToRefreshView.setMode(Mode.BOTH);
		//reset screenLock
		act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

		//check internet
		if (output != null)
		{
			if (this.INITIALpageToLoad == null)
			{
				WriteFileService write = new WriteFileService(act);
				write.setVars(link, data, categoryToLoad, 0, 1);
				write.execute();
			}
			if (MainActivityNew.ALL_ARTS_ARR == null)
			{
				MainActivityNew.ALL_ARTS_ARR = new String[100][][];
			}
			System.out.println("ParseBlogsPageNew: onPostExecute  output.length: " + output.length);
			MainActivityNew.ALL_ARTS_ARR[this.pageToLoad - 1] = output;
			fillData(this.pageToLoad - 1);
			if (MainActivityNew.MAIN_ADAPTER == null)
			{
				MainActivityNew.MAIN_ADAPTER = new MainListAdapterNew(act, R.layout.article_child_view_ligth, MainActivityNew.ARTS_INFO_ARR_LIST);
				this.pullToRefreshView.setAdapter(MainActivityNew.MAIN_ADAPTER);
			}
			else
			{
				MainActivityNew.MAIN_ADAPTER.notifyDataSetChanged();
			}
			if (!this.fromBottomPull)
			{
				this.pullToRefreshView.setAdapter(MainActivityNew.MAIN_ADAPTER);
			}
			else
			{
				MainActivityNew.MAIN_ADAPTER.notifyDataSetChanged();
			}
			MainActivityNew.MAIN_ADAPTER.notifyDataSetChanged();
		}
		//NO internet
		else
		{
			if (this.fromBottomPull)
			{
				MainActivityNew.CUR_PAGE_TO_LOAD -= 1;
			}
			if (INITIALpageToLoad != null)
			{
				if (INITIALpageToLoad == 0)
				{
					Toast.makeText(act, "Списка статей в кэше не обнаружено", Toast.LENGTH_LONG).show();
				}
			}
			else
			{
				ParseBlogsPage parse = new ParseBlogsPage(act, this.categoryToLoad, 0, this.pullToRefreshView, false);
				parse.execute();
				Toast.makeText(act, "Ошибка соединения \n Пробую загрузить список статей из кэша", Toast.LENGTH_LONG).show();
			}
		}

	}// Событие по окончанию парсинга

	void fillData(Integer curPage)
	{
		System.out.println("parseBlogsNew fillData start");

		if (MainActivityNew.ARTS_INFO_ARR_LIST == null || !this.fromBottomPull)
		{
			MainActivityNew.ARTS_INFO_ARR_LIST = new ArrayList<MainInfo>();
		}
		if (MainActivityNew.ALL_ARTS_ARR[curPage].length < MainActivityNew.DEFAULT_NUM_OF_ARTS_ON_PAGE)
		{
			this.pullToRefreshView.setMode(Mode.PULL_FROM_START);
		}
		else if (MainActivityNew.ALL_ARTS_ARR[curPage].length > MainActivityNew.DEFAULT_NUM_OF_ARTS_ON_PAGE)
		{
			this.pullToRefreshView.setMode(Mode.DISABLED);
		}
		else
		{
			this.pullToRefreshView.setMode(Mode.BOTH);
		}
		//test checking for empty arts in array
		System.out.println("MainActivityNew.ALL_ARTS_ARR[curPage].length: "+String.valueOf(MainActivityNew.ALL_ARTS_ARR[curPage].length));
		for (int i = 0; i < MainActivityNew.ALL_ARTS_ARR[curPage].length; i++)
		{
			MainActivityNew.ARTS_INFO_ARR_LIST.add(new MainInfo(MainActivityNew.ALL_ARTS_ARR[curPage][i]));
		}

		System.out.println("parseBlogs fillData end");
	}
}
