package ru.kuchanov.odnako.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ReadUnreadRegister
{
	Context ctx;
	SharedPreferences readPref;

	public ReadUnreadRegister(Context ctx)
	{
		this.ctx = ctx;
		readPref = ctx.getSharedPreferences("readPref", Context.MODE_PRIVATE);
	}

	public boolean check(String url)
	{

//		FormatURLToFileName format = new FormatURLToFileName();
		String formatedURL = FormatURLToFileName.format(url);

		if (readPref.getString(formatedURL, "").equals(""))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public void register(String url)
	{
		//FormatURLToFileName format = new FormatURLToFileName();
		String formatedURL = FormatURLToFileName.format(url);
		readPref.edit().putString(formatedURL, url).commit();
	}

}
