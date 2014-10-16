package ru.kuchanov.odnako.download;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class DeleteService extends Service
{

	public void onCreate()
	{
		System.out.println("DeleteService onCreate");
		super.onCreate();
	}

	public int onStartCommand(Intent intent, int flags, int startId)
	{
		System.out.println("DeleteService onStartCommand");
		startDelete(this);
		return super.onStartCommand(intent, flags, startId);
	}

	public void onDestroy()
	{
		System.out.println("DeleteService onDestroy");
		super.onDestroy();
	}

	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	protected void startDelete(Context context)
	{
		System.out.println("DeleteService startDelete");
		DeleteFilesTask del=new DeleteFilesTask(context);
		del.execute();
		////Set unreaded num ofarts to zero
		SharedPreferences prefsNumOfArts = context.getSharedPreferences("saveNumOfUnReadedArts", 0);

		SharedPreferences.Editor editor = prefsNumOfArts.edit();
		editor.putInt("quontityOfUnreadedArts", 0);
		editor.commit();
	}

}
