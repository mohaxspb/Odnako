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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateParse
{
	final static String LOG = DateParse.class.getSimpleName() + "/";
	//test values, that we can recive from site
	static final String dateStrEng = "Fri, 12 Dec 2014 22:06:00 +0400";
	static final String dateStrRu = "2 июня 2009";

	public static Date parse(String stringDate)
	{
		DateFormat fRu = new SimpleDateFormat("d MMMM yyyy", new Locale("ru"));
		DateFormat fEng = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z", new Locale("en"));
		DateFormat today = new SimpleDateFormat("HH:mm", new Locale("ru"));
		DateFormat thisYear = new SimpleDateFormat("dd/MM", new Locale("ru"));
		DateFormat prevYear = new SimpleDateFormat("dd/MM/yy", new Locale("ru"));

		DateFormat[] dateFormatArr = new DateFormat[5];
		dateFormatArr[0] = fRu;
		dateFormatArr[1] = fEng;
		dateFormatArr[2] = prevYear;
		dateFormatArr[3] = thisYear;
		dateFormatArr[4] = today;
		Date d = null;
		for (int i = 0; i < dateFormatArr.length && d == null; i++)
		{
			try
			{
				d = dateFormatArr[i].parse(stringDate);
				//here set year, month, day
				//create calendar with curent Y, M, D and MOSCOW TZ
				Calendar calNow = Calendar.getInstance(TimeZone.getTimeZone("GMT+03:00"), new Locale("ru"));
				calNow.set(calNow.get(Calendar.YEAR), calNow.get(Calendar.MONTH),
				calNow.get(Calendar.DAY_OF_MONTH));
				//create calendar with given date
				Calendar calGiven = Calendar.getInstance();
				calGiven.setTime(d);
				switch (i)
				{
					case 0:
					//set only time zone
					break;
					case 1:
					//set only locale
					break;
					case 4:
						//set Year, month and day and TimeZone
						//set to calendar with given date Y, M, D and TZ from calendar above
						calGiven.set(calNow.get(Calendar.YEAR), calNow.get(Calendar.MONTH),
						calNow.get(Calendar.DAY_OF_MONTH));
						calGiven.setTimeZone(calNow.getTimeZone());
						//finally set new date
						d.setTime(calGiven.getTimeInMillis());
					break;
					case 3:
						//set year and timeZone
						//set to calendar with given date Y and TZ from calendar above
						calGiven.set(Calendar.YEAR, calNow.get(Calendar.YEAR));
						calGiven.setTimeZone(calNow.getTimeZone());
						//finally set new date
						d.setTime(calGiven.getTimeInMillis());
					break;
					case 2:
						//set timeZone
						//set to calendar with given date TZ from calendar above
						calGiven.setTimeZone(calNow.getTimeZone());
						//finally set new date
						d.setTime(calGiven.getTimeInMillis());
//						Log.e(LOG, "case 2 in DateParse, dd/MM/yy: " + calGiven.toString());
					break;
				}
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
