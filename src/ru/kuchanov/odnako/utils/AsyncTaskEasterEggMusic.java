/*
 05.04.2015
AskDBFromTop.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import java.io.IOException;

import ru.kuchanov.odnako.callbacks.CallbackEasterEggMusic;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class AsyncTaskEasterEggMusic extends AsyncTask<Void, Void, String[]>
{
	final static String LOG = AsyncTaskEasterEggMusic.class.getSimpleName();

	public final static String SERVER_ADRESS = "http://kuchanov.ru/odnako/";
	public final static String FILE_END = ".mp3";

	CallbackEasterEggMusic callback;
	String name;

	public AsyncTaskEasterEggMusic(CallbackEasterEggMusic callback, String name)
	{
		this.callback = callback;
		this.name = name;
	}

	protected String[] doInBackground(Void... args)
	{
		String[] answer;

		FormEncodingBuilder builder = new FormEncodingBuilder();
		Request.Builder request = new Request.Builder();

		//key, value
		builder.add("name", this.name);
		RequestBody formBody = builder.build();
		request.post(formBody);

		request.url("http://kuchanov.ru/odnako/names.php");

		try
		{
			OkHttpClient client = new OkHttpClient();
			Response response;
			response = client.newCall(request.build()).execute();
			answer = response.body().string().split(" !!!! ");
			Log.e(LOG, response.body().string());
		} catch (IOException e)
		{
			e.printStackTrace();
			answer = null;
		}

		return answer;
	}

	protected void onPostExecute(String[] answer)
	{
		if (answer != null)
		{
			if (answer[0].contains("no-no-no"))
			{
				this.callback.onError("Вы не угадали!");
			}
			else
			{
				this.callback.onAnswerFromServer(answer);
			}
		}
		else
		{
			this.callback.onError("Сервер не отвечает. Может нема интернета?..");
		}
	}
}