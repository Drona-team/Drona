package com.google.android.exoplayer2.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.NetworkRequest.Builder;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.RequiresApi;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

public final class RequirementsWatcher
{
  private static final String PAGE_KEY = "RequirementsWatcher";
  private final Context context;
  private final Listener listener;
  private CapabilityValidatedCallback networkCallback;
  private DeviceStatusChangeReceiver receiver;
  private final Requirements requirements;
  private boolean requirementsWereMet;
  
  public RequirementsWatcher(Context paramContext, Listener paramListener, Requirements paramRequirements)
  {
    requirements = paramRequirements;
    listener = paramListener;
    context = paramContext.getApplicationContext();
    paramContext = new StringBuilder();
    paramContext.append(this);
    paramContext.append(" created");
    logd(paramContext.toString());
  }
  
  private void checkRequirements()
  {
    boolean bool = requirements.checkRequirements(context);
    if (bool == requirementsWereMet)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("requirementsAreMet is still ");
      localStringBuilder.append(bool);
      logd(localStringBuilder.toString());
      return;
    }
    requirementsWereMet = bool;
    if (bool)
    {
      logd("start job");
      listener.requirementsMet(this);
      return;
    }
    logd("stop job");
    listener.requirementsNotMet(this);
  }
  
  private static void logd(String paramString) {}
  
  private void registerNetworkCallbackV23()
  {
    ConnectivityManager localConnectivityManager = (ConnectivityManager)context.getSystemService("connectivity");
    NetworkRequest localNetworkRequest = new NetworkRequest.Builder().addCapability(16).build();
    networkCallback = new CapabilityValidatedCallback(null);
    localConnectivityManager.registerNetworkCallback(localNetworkRequest, networkCallback);
  }
  
  private void unregisterNetworkCallback()
  {
    if (Util.SDK_INT >= 21)
    {
      ((ConnectivityManager)context.getSystemService("connectivity")).unregisterNetworkCallback(networkCallback);
      networkCallback = null;
    }
  }
  
  public Requirements getRequirements()
  {
    return requirements;
  }
  
  public void start()
  {
    Assertions.checkNotNull(Looper.myLooper());
    requirementsWereMet = requirements.checkRequirements(context);
    Object localObject = new IntentFilter();
    if (requirements.getRequiredNetworkType() != 0) {
      if (Util.SDK_INT >= 23) {
        registerNetworkCallbackV23();
      } else {
        ((IntentFilter)localObject).addAction("android.net.conn.CONNECTIVITY_CHANGE");
      }
    }
    if (requirements.isChargingRequired())
    {
      ((IntentFilter)localObject).addAction("android.intent.action.ACTION_POWER_CONNECTED");
      ((IntentFilter)localObject).addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
    }
    if (requirements.isIdleRequired()) {
      if (Util.SDK_INT >= 23)
      {
        ((IntentFilter)localObject).addAction("android.os.action.DEVICE_IDLE_MODE_CHANGED");
      }
      else
      {
        ((IntentFilter)localObject).addAction("android.intent.action.SCREEN_ON");
        ((IntentFilter)localObject).addAction("android.intent.action.SCREEN_OFF");
      }
    }
    receiver = new DeviceStatusChangeReceiver(null);
    context.registerReceiver(receiver, (IntentFilter)localObject, null, new Handler());
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append(this);
    ((StringBuilder)localObject).append(" started");
    logd(((StringBuilder)localObject).toString());
  }
  
  public void stop()
  {
    context.unregisterReceiver(receiver);
    receiver = null;
    if (networkCallback != null) {
      unregisterNetworkCallback();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(this);
    localStringBuilder.append(" stopped");
    logd(localStringBuilder.toString());
  }
  
  public String toString()
  {
    return super.toString();
  }
  
  @RequiresApi(api=21)
  private final class CapabilityValidatedCallback
    extends ConnectivityManager.NetworkCallback
  {
    private CapabilityValidatedCallback() {}
    
    public void onAvailable(Network paramNetwork)
    {
      super.onAvailable(paramNetwork);
      paramNetwork = new StringBuilder();
      paramNetwork.append(RequirementsWatcher.this);
      paramNetwork.append(" NetworkCallback.onAvailable");
      RequirementsWatcher.logd(paramNetwork.toString());
      RequirementsWatcher.this.checkRequirements();
    }
    
    public void onLost(Network paramNetwork)
    {
      super.onLost(paramNetwork);
      paramNetwork = new StringBuilder();
      paramNetwork.append(RequirementsWatcher.this);
      paramNetwork.append(" NetworkCallback.onLost");
      RequirementsWatcher.logd(paramNetwork.toString());
      RequirementsWatcher.this.checkRequirements();
    }
  }
  
  private class DeviceStatusChangeReceiver
    extends BroadcastReceiver
  {
    private DeviceStatusChangeReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if (!isInitialStickyBroadcast())
      {
        paramContext = new StringBuilder();
        paramContext.append(RequirementsWatcher.this);
        paramContext.append(" received ");
        paramContext.append(paramIntent.getAction());
        RequirementsWatcher.logd(paramContext.toString());
        RequirementsWatcher.this.checkRequirements();
      }
    }
  }
  
  public static abstract interface Listener
  {
    public abstract void requirementsMet(RequirementsWatcher paramRequirementsWatcher);
    
    public abstract void requirementsNotMet(RequirementsWatcher paramRequirementsWatcher);
  }
}
