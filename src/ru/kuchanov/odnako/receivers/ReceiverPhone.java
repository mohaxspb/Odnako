/*
 07.04.2015
TimerReceiver.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.receivers;

import ru.kuchanov.odnako.utils.ServiceTTS;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class ReceiverPhone extends BroadcastReceiver
{
	static final String LOG = ReceiverPhone.class.getSimpleName();
	private Context ctx;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		//Log.d(LOG, "onReceive " + intent.getAction());

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
			Intent intentTTS = new Intent(ctx.getApplicationContext(), ServiceTTS.class);
			switch (state)
			{
				case TelephonyManager.CALL_STATE_IDLE:
					//Log.d(LOG, "IDLE");
					intentTTS.setAction("play");
				break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					//Log.d(LOG, "OFFHOOK");
					intentTTS.setAction("pause");
				break;
				case TelephonyManager.CALL_STATE_RINGING:
					//Log.d(LOG, "RINGING");
					intentTTS.setAction("pause");
				break;
			}
			ctx.startService(intentTTS);
		}
	}
}