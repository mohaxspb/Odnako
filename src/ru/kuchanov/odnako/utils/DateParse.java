/*
 13.12.2014
DateParse.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class DateParse
{
	//test values, that we can recive from site
	static final String dateStrEng = "Fri, 12 Dec 2014 22:06:00 +0400";
	static final String dateStrRu = "2 июня 2009";

	public DateParse()
	{
		Log.d("", "DateParse constructor called");
	}

	public static Date parse(String stringDate)
	{
		DateFormat fRu = new SimpleDateFormat("d MMMM yyyy", new Locale("ru"));
		DateFormat fEng = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z", new Locale("en"));
		Date d = new Date();
		try
		{
			d = fRu.parse(stringDate);
		} catch (ParseException e)
		{
			try
			{
				d = fEng.parse(stringDate);

			} catch (ParseException e1)
			{
				//				Log.d("parse_date", "ParseException in Date parsing");
				d.setTime(0);
			}
		} finally
		{
			//			Log.d("parse_date", d.toString());
			//			Locale locale = new Locale("ru","RU");
			//			DateFormat full = DateFormat.getDateInstance(DateFormat.LONG, locale);
			//			Log.d("parse_date", full.format(d));/
		}

		return d;
	}

}
