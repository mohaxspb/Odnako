package ru.kuchanov.odnako.utils;

import android.content.Context;

public class FormatURLToFileName
{
	Context ctx;

	public FormatURLToFileName(Context ctx)
	{
		this.ctx=ctx;
		
	}
	public FormatURLToFileName()
	{
	}
	
	public static String format(String URL)
	{
		String formatedURL;
		
		formatedURL = URL;
		formatedURL = formatedURL.replace("-", "_");
		formatedURL = formatedURL.replace("/", "_");
		formatedURL = formatedURL.replace(":", "_");
		formatedURL = formatedURL.replace(".", "_");
		
		return formatedURL;
	}

}
