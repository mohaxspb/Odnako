/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.download;

import java.util.ArrayList;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.callbacks.CallbackComments;

import android.os.AsyncTask;
import android.util.Log;

public class ParseComments extends AsyncTask<Void, Void, ArrayList<CommentInfo>>
{
	private final static String LOG = ParseComments.class.getSimpleName() + "/";

	public int pageToLoad;
	public String link;
	private CallbackComments callback;

	public ParseComments(String url, int pageToLoad, CallbackComments callback)
	{
		this.link = url;
		this.pageToLoad = pageToLoad;
		this.callback = callback;
	}

	protected ArrayList<CommentInfo> doInBackground(Void... arg)
	{
		Log.d(LOG, "loadInBackground url/page: " + this.link + "/" + this.pageToLoad);
		ArrayList<CommentInfo> output = null;

		String linkToComments;
		linkToComments = this.link + "comments/page-" + this.pageToLoad + "/";

		try
		{
			HtmlCleaner cleaner = new HtmlCleaner();
			HtmlHelper hh = new HtmlHelper(linkToComments);
			TagNode rootNode = hh.rootNode;
			//<ul class="ul-comments">
			boolean isRecursive = true;
			boolean isCaseSensitive = false;
			String clazz = "class";
			TagNode tagUl = rootNode.findElementByAttValue(clazz, "ul-comments", isRecursive, isCaseSensitive);
			//			if (tagUl.hasChildren())
			TagNode[] tagsLi = tagUl.getChildTags();
			if (tagsLi.length != 0)
			{
				output = new ArrayList<CommentInfo>();

				String name, txt, flag, time, like, dislike, avaImg, city, data_pid, id, padding, numOfCommsPages;
				for (int i = 0; i < tagsLi.length; i++)
				{
					TagNode li = tagsLi[i];
					//<span class="name">Воронежец обыкновенный</span>
					name = li.findElementByAttValue(clazz, "name", isRecursive, isCaseSensitive).getText().toString();
					//<div id="comment_body1754687" class="comment-body"><p>Большое спасибо - было очень интересно прочесть.</p>
					//</div>
					txt = cleaner.getInnerHtml(li.findElementByAttValue(clazz, "comment-body", isRecursive,
					isCaseSensitive));
					//<span class="flag"><img src="/images/country/RU.png" alt="Russian Federation" title="Russian Federation" height="11"></span>
					flag = li.findElementByAttValue(clazz, "flag", isRecursive, isCaseSensitive)
					.findElementByName("img", isRecursive).getAttributeByName("src");
					if (flag.startsWith("/"))
					{
						flag = HtmlHelper.DOMAIN_MAIN + flag;
					}
					//<span class="time"><a href="/view_comment/49072/1754687/">03 апреля 2015 19:56</a></span>
					time = li.findElementByAttValue(clazz, "time", isRecursive, isCaseSensitive).getText().toString();
					//<span class="good-count" data-id='1754687'> <span>6 </span>| </span>
					like = li.findElementByAttValue(clazz, "good-count", isRecursive, isCaseSensitive)
					.findElementByName("span", isRecursive).getText().toString().trim();
					//<span class="bad-count" data-id='1754687'> <span>1 </span></span>
					dislike = li.findElementByAttValue(clazz, "bad-count", isRecursive, isCaseSensitive)
					.findElementByName("span", isRecursive).getText().toString().trim();
					//<div class="image">
					//<img src="http://www.odnako.org/i/75_75/users/117605/117605-1481-117605.jpg" alt="" width="51">
					//</div>
					TagNode divImage = li.findElementByAttValue(clazz, "image", isRecursive, isCaseSensitive);
					if (divImage != null)
					{
						avaImg = divImage.findElementByName("img", isRecursive).getAttributeByName("src");
						if (avaImg.startsWith("/"))
						{
							avaImg = HtmlHelper.DOMAIN_MAIN + avaImg;
						}
					}
					else
					{
						avaImg = Const.EMPTY_STRING;
					}
					//<span class="place"> , Novosibirsk </span>
					city = li.findElementByAttValue(clazz, "place", isRecursive, isCaseSensitive).getText().toString();
					//<li data-pid='0' id="comment_1754687" style="padding-left:0em;">
					data_pid = li.getAttributeByName("data-pid");
					id = li.getAttributeByName("id");
					String styleValue = li.getAttributeByName("style");
					padding = styleValue.substring(styleValue.indexOf(":") + 1, styleValue.indexOf("em"));
					//TODO delete it?..
					numOfCommsPages = Const.EMPTY_STRING;

					output.add(new CommentInfo(name, txt, flag, time, city, like, dislike, avaImg, data_pid, id,
					padding, numOfCommsPages));
				}
			}
			else
			{
				//no comments
				output = new ArrayList<CommentInfo>();
				if (this.pageToLoad == 1)
				{
					CommentInfo noCommentsComment = CommentInfo.getDefaultCommentInfo();
					noCommentsComment.flag = "http://www.odnako.org" + "/images/country/RU.png";
					noCommentsComment.time = "Только что";
					noCommentsComment.city = " , Ленинград";
					noCommentsComment.like = "42";
					noCommentsComment.name = "Приложение сообщает";
					noCommentsComment.txt = "Пока что никто ничего тут не написал...";
					output.add(noCommentsComment);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return output;
	}

	protected void onPostExecute(ArrayList<CommentInfo> output)
	{
		//check internet
		if (output != null)
		{
			this.callback.onDoneLoadingComments(Const.EMPTY_STRING, output, link, pageToLoad);
		}
		//NO internet
		else
		{
			this.callback.onDoneLoadingComments(Const.Error.CONNECTION_ERROR, output, link, pageToLoad);
		}
	}
}

//<li data-pid='0' id="comment_1754687" style="padding-left:0em;">
//<a name="1754687"></a>
//<div class="comment" id="comment_block1754687">
//                  <div class="image">
//      <img src="http://www.odnako.org/i/75_75/users/117605/117605-1481-117605.jpg" alt="" width="51">
//  </div>
//  
//  <div class="add">
//      <div class="meta">
//                                <span class="name">Воронежец обыкновенный</span>
//                                <span class="flag"><img src="/images/country/RU.png" alt="Russian Federation" title="Russian Federation" height="11"></span>
//									<span class="time"><a href="/view_comment/49072/1754687/">03 апреля 2015 19:56</a></span>
//          <span class="place"> , Novosibirsk </span>
//          <span class="replied-to"> <i class="icn-replied-to"></i></span>
//          <div class="dropdown-trigger"><a href="#"><i class="icon-chevron-sign-down"></i></a></div>
//
//          <div class="dropdown hide">
//              <ul>
//                  <li><a href="http://www.odnako.org/view_comment/49072/1754687/" target="_blank" class="comment_link">Ссылка на комментарий</a></li>
//                  <li><a href="javascript:void(0);" rel="1754687" class="complaint">Пожаловаться модератору</a></li>
//                  
//                  
//                  
//              </ul>
//          </div>
//      </div>
//
//
//          <div id="comment_body1754687" class="comment-body"><p>Большое спасибо - было очень интересно прочесть.</p>
//</div>
//                                  <div class="actions">
//              <a class="reply-link" data-id='1754687' data-material_id='49072'>Ответить</a>
//              <div class="karma">
//                                                  <a href="#" class="good" data-id='1754687'><i class="icon-thumbs-up"></i></a>
//                  <span class="good-count" data-id='1754687'> <span>6 </span>| </span>
//                  <span class="bad-count" data-id='1754687'> <span>1 </span></span>
//                  <a href="#" class="bad" data-id='1754687'><i class="icon-thumbs-down"></i></a>
//              </div>
//
//      </div>
//
//
//  </div>
//  <div class="clearfix"></div>
//</div>
//
//</li>