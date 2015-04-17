/*
 07.04.2015
TimerReceiver.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.receivers;

import ru.kuchanov.odnako.utils.CheckIfServiceIsRunning;
import ru.kuchanov.odnako.utils.ServiceTTS;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class ReceiverPhone extends BroadcastReceiver
{
	private static final String LOG = ReceiverPhone.class.getSimpleName();
	private Context ctx;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.d(LOG, "onReceive " + intent.getAction());

		this.ctx = context;
		MyPhoneStateListener phoneListener = new MyPhoneStateListener();
		TelephonyManager telephony = (TelephonyManager)
		context.getSystemService(Context.TELEPHONY_SERVICE);
		telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	public class MyPhoneStateListener extends PhoneStateListener
	{
		public void onCallStateChanged(int state, String incomingNumber)
		{
			switch (state)
			{
				case TelephonyManager.CALL_STATE_IDLE:
					Log.d("DEBUG", "IDLE");
					//Toast.makeText(ctx, "IDLE", Toast.LENGTH_SHORT).show();
				break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					Log.d("DEBUG", "OFFHOOK");
					//Toast.makeText(ctx, "OFFHOOK", Toast.LENGTH_SHORT).show();
				break;
				case TelephonyManager.CALL_STATE_RINGING:
					Log.d("DEBUG", "RINGING");
					//Toast.makeText(ctx, "RINGING", Toast.LENGTH_SHORT).show();
				break;
			}
			//if we are running sertviceTTS we must pause it
			if (CheckIfServiceIsRunning.check(ctx, "ServiceTTS"))
			{
				Intent intentTTS = new Intent(ctx.getApplicationContext(), ServiceTTS.class);
				intentTTS.setAction("pause");
				ctx.startService(intentTTS);
			}
		}
	}
}