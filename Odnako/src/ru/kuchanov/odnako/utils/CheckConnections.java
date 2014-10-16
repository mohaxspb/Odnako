package ru.kuchanov.odnako.utils;

import android.content.Context;
import android.net.ConnectivityManager;


public class CheckConnections
{
	Context ctx;
	
	public static boolean isInternetByWiFiOn(Context ctx)
	{
	  ConnectivityManager connec = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

	  // ARE WE CONNECTED TO THE NET
	  if ( connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == android.net.NetworkInfo.State.CONNECTED)
	  {
	    return true;
	  }
	  else if (connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == android.net.NetworkInfo.State.DISCONNECTED)
	  {
	    return false;
	  }
	  return false;
	}

}
