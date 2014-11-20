/*
 17.11.2014
OverScreenService.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import ru.kuchanov.odnako.R;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

public class OverScreenService extends Service
{

	private WindowManager windowManager;
	private ImageView chatHead;

	@Override
	public IBinder onBind(Intent intent)
	{
		// Not used
		return null;
	}

	@Override
	public void onCreate()
	{
		System.out.println("OverScreenService onCreate");
		super.onCreate();

		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		chatHead = new ImageView(this);
		chatHead.setImageResource(R.drawable.ic_launcher);

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
		WindowManager.LayoutParams.WRAP_CONTENT,
		WindowManager.LayoutParams.WRAP_CONTENT,
		WindowManager.LayoutParams.TYPE_PHONE,
		WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
		PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 100;

		windowManager.addView(chatHead, params);
	}

//	public int onStartCommand(Intent intent, int flags, int startId)
//	{
//		System.out.println("OverScreenService onStartCommand");
//		
//		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//
//		chatHead = new ImageView(this);
//		chatHead.setImageResource(R.drawable.ic_launcher);
//
//		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//		WindowManager.LayoutParams.WRAP_CONTENT,
//		WindowManager.LayoutParams.WRAP_CONTENT,
//		WindowManager.LayoutParams.TYPE_PHONE,
//		WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//		PixelFormat.TRANSLUCENT);
//
//		params.gravity = Gravity.TOP | Gravity.LEFT;
//		params.x = 0;
//		params.y = 100;
//
//		windowManager.addView(chatHead, params);
//		return super.onStartCommand(intent, flags, startId);
//	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (chatHead != null)
		{
			windowManager.removeView(chatHead);
		}
	}
}