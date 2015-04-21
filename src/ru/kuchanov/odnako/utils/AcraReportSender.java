/*
 22.04.2015
AcraReportSender.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import java.io.IOException;
import java.util.ArrayList;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class AcraReportSender implements ReportSender
{
	private final static String LOG = AcraReportSender.class.getSimpleName() + "/";

	public AcraReportSender()
	{
		// initialize your sender with needed parameters
	}

	@Override
	public void send(Context ctx, CrashReportData report) throws ReportSenderException
	{
		Log.e(LOG, "send called");
		// Iterate over the CrashReportData instance and do whatever
		// you need with each pair of ReportField key / String value
		FormEncodingBuilder builder = new FormEncodingBuilder();

		ArrayList<ReportField> rfs = new ArrayList<ReportField>(report.keySet());
		ArrayList<String> props = new ArrayList<String>();
		for (int i = 0; i < rfs.size(); i++)
		{
			ReportField rf = rfs.get(i);
			String prop = report.getProperty(rf);
			props.add(prop);
			builder.add(rf.toString(), prop);
		}

		RequestBody formBody = builder.build();

		Request request = new Request.Builder()
		.url(ACRA.getConfig().formUri())
		.post(formBody)
		.build();

		try
		{
			OkHttpClient client = new OkHttpClient();
			Response response;
			response = client.newCall(request).execute();
			Log.e(LOG, response.body().string());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}