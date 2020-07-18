package com.bumptech.glide.manager;

import android.content.Context;
import android.util.Log;
import androidx.core.content.ContextCompat;

public class DefaultConnectivityMonitorFactory
  implements ConnectivityMonitorFactory
{
  private static final String NETWORK_PERMISSION = "android.permission.ACCESS_NETWORK_STATE";
  private static final String SQL_UPDATE_6_4 = "ConnectivityMonitor";
  
  public DefaultConnectivityMonitorFactory() {}
  
  public ConnectivityMonitor build(Context paramContext, ConnectivityMonitor.ConnectivityListener paramConnectivityListener)
  {
    int i;
    if (ContextCompat.checkSelfPermission(paramContext, "android.permission.ACCESS_NETWORK_STATE") == 0) {
      i = 1;
    } else {
      i = 0;
    }
    if (Log.isLoggable("ConnectivityMonitor", 3))
    {
      String str;
      if (i != 0) {
        str = "ACCESS_NETWORK_STATE permission granted, registering connectivity monitor";
      } else {
        str = "ACCESS_NETWORK_STATE permission missing, cannot register connectivity monitor";
      }
      Log.d("ConnectivityMonitor", str);
    }
    if (i != 0) {
      return new DefaultConnectivityMonitor(paramContext, paramConnectivityListener);
    }
    return new NullConnectivityMonitor();
  }
}
