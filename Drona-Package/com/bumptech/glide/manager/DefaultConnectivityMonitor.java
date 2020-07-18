package com.bumptech.glide.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.bumptech.glide.util.Preconditions;

final class DefaultConnectivityMonitor
  implements ConnectivityMonitor
{
  private static final String TAG = "ConnectivityMonitor";
  private final BroadcastReceiver connectivityReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      boolean bool = isConnected;
      isConnected = isConnected(paramAnonymousContext);
      if (bool != isConnected)
      {
        if (Log.isLoggable("ConnectivityMonitor", 3))
        {
          paramAnonymousContext = new StringBuilder();
          paramAnonymousContext.append("connectivity changed, isConnected: ");
          paramAnonymousContext.append(isConnected);
          Log.d("ConnectivityMonitor", paramAnonymousContext.toString());
        }
        listener.onConnectivityChanged(isConnected);
      }
    }
  };
  private final Context context;
  boolean isConnected;
  private boolean isRegistered;
  final ConnectivityMonitor.ConnectivityListener listener;
  
  DefaultConnectivityMonitor(Context paramContext, ConnectivityMonitor.ConnectivityListener paramConnectivityListener)
  {
    context = paramContext.getApplicationContext();
    listener = paramConnectivityListener;
  }
  
  private void register()
  {
    if (isRegistered) {
      return;
    }
    isConnected = isConnected(context);
    Context localContext = context;
    BroadcastReceiver localBroadcastReceiver = connectivityReceiver;
    try
    {
      localContext.registerReceiver(localBroadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
      isRegistered = true;
      return;
    }
    catch (SecurityException localSecurityException)
    {
      if (Log.isLoggable("ConnectivityMonitor", 5)) {
        Log.w("ConnectivityMonitor", "Failed to register", localSecurityException);
      }
    }
  }
  
  private void unregister()
  {
    if (!isRegistered) {
      return;
    }
    context.unregisterReceiver(connectivityReceiver);
    isRegistered = false;
  }
  
  boolean isConnected(Context paramContext)
  {
    paramContext = (ConnectivityManager)Preconditions.checkNotNull((ConnectivityManager)paramContext.getSystemService("connectivity"));
    try
    {
      paramContext = paramContext.getActiveNetworkInfo();
      return (paramContext != null) && (paramContext.isConnected());
    }
    catch (RuntimeException paramContext)
    {
      if (Log.isLoggable("ConnectivityMonitor", 5)) {
        Log.w("ConnectivityMonitor", "Failed to determine connectivity status when connectivity changed", paramContext);
      }
    }
    return true;
  }
  
  public void onDestroy() {}
  
  public void onStart()
  {
    register();
  }
  
  public void onStop()
  {
    unregister();
  }
}
