/*
 11.11.2014
ParsePageForAllArtsInfo.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import java.io.IOException;


import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class TestPhp extends AsyncTask<Void, Void, String>
{
	private final static String LOG = TestPhp.class.getSimpleName() + "/";

	Context ctx;
	private String url;

	public TestPhp(Context ctx, String url)
	{
		this.ctx = ctx;
		this.url = url;
	}

	protected String doInBackground(Void... arg)
	{
		String answer = null;
		try
		{
			answer = this.post(url);
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return answer;
	}

	OkHttpClient client = new OkHttpClient();

	String post(String url) throws IOException
	{
		FormEncodingBuilder builder = new FormEncodingBuilder();
		for (String key : array)
		{
			builder.add(key, "Lorem Ipsum");
		}
		RequestBody formBody = builder.build();

		Request request = new Request.Builder()
		.url(url)
		.post(formBody)
		.build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	protected void onPostExecute(String answer)
	{
		if (answer != null)
		{
			Log.e(LOG, answer);
		}
		else
		{
			Log.e(LOG, "answer=null");
		}
	}

	String[] array = { "report_id",
			"app_version_code",
			"app_version_name",
			"package_name",
			"file_path",
			"phone_model",
			"android_version",
			"build",
			"brand",
			"product",
			"total_mem_size",
			"available_mem_size",
			"custom_data",
			"stack_trace",
			"initial_configuration",
			"crash_configuration",
			"display",
			"user_comment",
			"user_app_start_date",
			"user_crash_date",
			"dumpsys_meminfo",
			"dropbox",
			"logcat",
			"eventslog",
			"radiolog",
			"is_silent",
			"device_id",
			"installation_id",
			"user_email",
			"device_features",
			"environment",
			"settings_system",
			"settings_secure",
			"shared_preferences" };
}