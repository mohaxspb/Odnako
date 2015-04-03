/*
 03.04.2015
DownloadComments.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.download;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import ru.kuchanov.odnako.fragments.FragmentArticle;
import ru.kuchanov.odnako.fragments.FragmentComments;
import ru.kuchanov.odnako.lists_and_utils.CommentInfo;

public class DownloadComments extends AsyncTaskLoader<ArrayList<CommentInfo>>
{
	public final static String LOG = FragmentArticle.class.getSimpleName() + "/";

	public int pageToLoad;

	public DownloadComments(Context context, Bundle b)
	{
		super(context);
		this.pageToLoad = b.getInt(FragmentComments.KEY_PAGE_TO_LOAD);
	}

	@Override
	protected void onForceLoad()
	{
		super.onForceLoad();
		Log.d(LOG, hashCode() + " onForceLoad");
	}

	@Override
	public ArrayList<CommentInfo> loadInBackground()
	{
		Log.d(LOG, hashCode() + " loadInBackground start");
		try
		{
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e)
		{
			return null;
		}
		return CommentInfo.getDefaultArtsCommentsInfo(20);
	}
}