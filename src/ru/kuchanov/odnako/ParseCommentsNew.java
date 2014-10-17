package ru.kuchanov.odnako;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.htmlcleaner.TagNode;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class ParseCommentsNew extends AsyncTask<String, Integer, String[][]>
{

	ActionBarActivity act;
	ProgressDialog pd;
	PullToRefreshListView pullToRefreshView;
	boolean fromArrow;
	Parcelable state;
	
	int pageToLoad;

	public ParseCommentsNew()
	{
	}

	public void setVars(ActionBarActivity act, PullToRefreshListView pullToRefreshView, boolean fromArrow, Parcelable state, int pageToLoad)
	{
		this.act = act;
		this.pullToRefreshView = pullToRefreshView;
		this.fromArrow = fromArrow;
		this.state = state;
		this.pageToLoad=pageToLoad;
	}

	@Override
	protected void onPreExecute()
	{
		LockOrientation lock = new LockOrientation(act);
		lock.lock();
	}

	@Override
	protected String[][] doInBackground(String... arg)
	{
		System.out.print("ParseComments: doInBackground \n");
		System.out.print("ParseComments: doInBackground " + arg[0]);
		String[][] output = null;
		try
		{
			HtmlHelper hh = new HtmlHelper(new URL(arg[0]));
			List<TagNode> commEl = hh.getComments();
			TagNode ulComm = commEl.get(0);
			TagNode arrEl[] = ulComm.getChildTags();

			if (arrEl.length != 0)
			{
				output = new String[arrEl.length][CommentsActivityNew.QUONTITY_OF_VALUES_IN_ONE_COMM_ARR];

				TagNode commLastPage = commEl.get(1);
				int numOfCommPagesInt = 1;
				if (commLastPage.getName().equals("a"))
				{
					String numOfCommPages = commLastPage.getAttributeByName("href");
					numOfCommPages = numOfCommPages.substring(numOfCommPages.indexOf("page-") + 5);
					numOfCommPages = numOfCommPages.replace("/", "");
					numOfCommPagesInt = Integer.valueOf(numOfCommPages);
				}

				for (int i = 0; i < arrEl.length; i++)
				{
					String commText = "";
					// text
					//commText = arrEl[i].findElementByAttValue("class", "comment-body", true, false).getText().toString();
					TagNode commentText = arrEl[i].findElementByAttValue("class", "comment-body", true, false);
					FormatHtmlText format = new FormatHtmlText();
					output[i][0] = format.formatNode(commentText);

					TagNode meta = arrEl[i].getElementsByAttValue("class", "meta", true, false)[0];
					// name
					commText = meta.getElementsByName("span", true)[0].getText().toString();
					commText = arrEl[i].findElementByAttValue("class", "name", true, false).getText().toString();
					output[i][1] = commText;

					// flag
					if (meta.getElementsByName("span", true)[1].getElementsByName("img", true).length > 0)
					{
						commText = meta.getElementsByName("span", true)[1].findElementByName("img", true).getAttributeByName("src").toString();
						output[i][2] = commText;
					}
					else
					{
						output[i][2] = "default";
					}

					// time
					//commText = meta.getElementsByName("span", true)[2].findElementByName("a", true).getText().toString();
					commText = meta.findElementByAttValue("class", "time", true, false).findElementByName("a", true).getText().toString();
					output[i][3] = commText;

					// city
					commText = meta.getElementsByName("span", true)[3].getText().toString();
					output[i][4] = commText;

					// karma
					TagNode karma = arrEl[i].findElementByAttValue("class", "karma", true, false);
					// like
					commText = karma.findElementByAttValue("class", "good-count", true, false).findElementByName("span", true).getText().toString();
					output[i][5] = commText;
					// dislike
					commText = karma.findElementByAttValue("class", "bad-count", true, false).findElementByName("span", true).getText().toString();
					output[i][6] = commText;

					// AVATAR
					Boolean avaImgExists = arrEl[i].getElementsByAttValue("class", "image", true, false).length != 0;

					if (avaImgExists)
					{
						String avaImg = arrEl[i].findElementByAttValue("class", "image", true, false).findElementByName("img", true).getAttributeByName("src");
						output[i][7] = avaImg;
					}
					else
					{
						output[i][7] = "default";
					}
					// data-pid
					commText = arrEl[i].getAttributeByName("data-pid");
					output[i][8] = commText;
					// id
					commText = arrEl[i].getAttributeByName("id");
					output[i][9] = commText;
					// padding
					commText = arrEl[i].getAttributeByName("style");
					commText = commText.substring(commText.indexOf(":") + 1, commText.indexOf("em"));
					output[i][10] = commText;
					// numOfCommsPages
					commText = String.valueOf(numOfCommPagesInt);
					output[i][11] = commText;

				}
			}
			else
			{
				output = new String[1][CommentsActivityNew.QUONTITY_OF_VALUES_IN_ONE_COMM_ARR];
				for (int i = 0; i < CommentsActivityNew.QUONTITY_OF_VALUES_IN_ONE_COMM_ARR; i++)
				{
					output[0][i] = "no_comm";
				}
				output[0][10] = "1.875";
				output[0][11] = "1";
			}

		} catch (Exception e)
		{

		}
		return output;
	}

	// Событие по окончанию парсинга
	@Override
	protected void onPostExecute(String[][] output)
	{
		System.out.print("ParseComments: onPostExecute \n");

		act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

		this.pullToRefreshView.onRefreshComplete();
		this.pullToRefreshView.setMode(Mode.PULL_FROM_END);
		//this.pullToRefreshView.onRefreshComplete();
		//remove pd
//		if (this.fromArrow || CommentsActivityNew.ALL_COMMS_ARR==null)
//		{
//			try
//			{
//				pd.dismiss();
//				pd.cancel();
//				pd = null;
//			} catch (Exception e)
//			{
//
//			}
//		}
		//remove pd

		//if Internet
		if (output != null)
		{
			CommentsActivityNew.QUONTITY_OF_COMM_PAGES = Integer.valueOf(output[0][11]);
			act.getSupportActionBar().setTitle("Комментарии(" + CommentsActivityNew.CURRENT_PAGE + "/" + CommentsActivityNew.QUONTITY_OF_COMM_PAGES + ")");

			if (CommentsActivityNew.ALL_COMMS_ARR == null)
			{
				CommentsActivityNew.ALL_COMMS_ARR = new String[CommentsActivityNew.QUONTITY_OF_COMM_PAGES][][];
			}
			//CommentsActivityNew.ALL_COMMS_ARR[CommentsActivityNew.CURRENT_PAGE - 1] = output;
			CommentsActivityNew.ALL_COMMS_ARR[this.pageToLoad-1] = output;

			fillData(this.pageToLoad - 1);
			CommentsListAdapter commListAdapter = new CommentsListAdapter(act, CommentsActivityNew.COMMS_INFO_ARR_LIST);
			if (this.fromArrow)
			{
				
				//CommentsActivityNew.CUR_PAGE_TO_LOAD+=1;
				pullToRefreshView.setAdapter(commListAdapter);
				//this.pullToRefreshView.onRefreshComplete();
				CommentsActivityNew.CUR_PAGE_TO_LOAD=1;
			}
			else
			{
				pullToRefreshView.setAdapter(commListAdapter);
				this.pullToRefreshView.onRefreshComplete();
				if(state!=null)
				{
					pullToRefreshView.getRefreshableView().onRestoreInstanceState(state);
				}
			}
			System.out.print("ParseComments: " + CommentsActivityNew.COMMS_INFO_ARR_LIST.get(0).name + "\n");
		}
		//if NO Internet
		else
		{
			Toast.makeText(act, "Ошибка интернет соединения", Toast.LENGTH_SHORT).show();
			this.pullToRefreshView.onRefreshComplete();
			if (CommentsActivityNew.QUONTITY_OF_COMM_PAGES != null)
			{
				act.getSupportActionBar().setTitle("Комментарии(" + CommentsActivityNew.CURRENT_PAGE + "/" + CommentsActivityNew.QUONTITY_OF_COMM_PAGES + ")");
			}
			else
			{
				act.getSupportActionBar().setTitle("Комментарии");
			}
			if (this.fromArrow)
			{
				CommentsActivityNew.CURRENT_PAGE -= 1;
				if (CommentsActivityNew.QUONTITY_OF_COMM_PAGES != null)
				{
					act.getSupportActionBar().setTitle("Комментарии(" + CommentsActivityNew.CURRENT_PAGE + "/" + CommentsActivityNew.QUONTITY_OF_COMM_PAGES + ")");
				}
			}
			else
			{
				if(state!=null)
				{
					pullToRefreshView.getRefreshableView().onRestoreInstanceState(state);
				}
			}
		}
	}

	// генерируем данные для адаптера
	void fillData(Integer curPage)
	{
		System.out.println("parse COMMS fillData start");
		if (CommentsActivityNew.COMMS_INFO_ARR_LIST == null)
		{
			CommentsActivityNew.COMMS_INFO_ARR_LIST = new ArrayList<CommentInfo>();
		}
		if (this.fromArrow)
		{
			//CommentsActivityNew.COMMS_INFO_ARR_LIST.clear();
			CommentsActivityNew.COMMS_INFO_ARR_LIST = new ArrayList<CommentInfo>();
		}
		for (int i = 0; i < CommentsActivityNew.ALL_COMMS_ARR[curPage].length; i++)
		{
			CommentsActivityNew.COMMS_INFO_ARR_LIST.add(new CommentInfo(CommentsActivityNew.ALL_COMMS_ARR[curPage][i]));
		}
		System.out.println("parse COMMS fillData end");
	}

}