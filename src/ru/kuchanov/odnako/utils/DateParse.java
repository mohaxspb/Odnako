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
		DateFormat today = new SimpleDateFormat("HH:mm", new Locale("ru"));
		DateFormat thisYear = new SimpleDateFormat("dd/MM", new Locale("ru"));
		DateFormat prevYear = new SimpleDateFormat("dd/MM/yyyy", new Locale("ru"));

		DateFormat[] dateFormatArr = new DateFormat[5];
		dateFormatArr[0] = fRu;
		dateFormatArr[1] = fEng;
		dateFormatArr[2] = today;
		dateFormatArr[3] = thisYear;
		dateFormatArr[4] = prevYear;
		Date d = null;
		for (int i = 0; i < dateFormatArr.length && d == null; i++)
		{
			try
			{
				d = dateFormatArr[i].parse(stringDate);
				//here set year, month, day
				//TODO
				.
			} catch (ParseException e)
			{

			}
		}
		if (d == null)
		{
			d = new Date(0);
		}

		return d;
	}

}
