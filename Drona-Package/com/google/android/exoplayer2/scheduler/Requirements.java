package com.google.android.exoplayer2.scheduler;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.PowerManager;
import com.google.android.exoplayer2.util.Util;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Requirements
{
  private static final int DEVICE_CHARGING = 16;
  private static final int DEVICE_IDLE = 8;
  public static final int NETWORK_TYPE_ANY = 1;
  private static final int NETWORK_TYPE_MASK = 7;
  public static final int NETWORK_TYPE_METERED = 4;
  public static final int NETWORK_TYPE_NONE = 0;
  public static final int NETWORK_TYPE_NOT_ROAMING = 3;
  private static final String[] NETWORK_TYPE_STRINGS;
  public static final int NETWORK_TYPE_UNMETERED = 2;
  private static final String PAGE_KEY = "Requirements";
  private final int requirements;
  
  public Requirements(int paramInt)
  {
    requirements = paramInt;
  }
  
  public Requirements(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramInt | i | j);
  }
  
  private boolean checkChargingRequirement(Context paramContext)
  {
    if (!isChargingRequired()) {
      return true;
    }
    paramContext = paramContext.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    if (paramContext == null) {
      return false;
    }
    int i = paramContext.getIntExtra("status", -1);
    return (i == 2) || (i == 5);
  }
  
  private boolean checkIdleRequirement(Context paramContext)
  {
    if (!isIdleRequired()) {
      return true;
    }
    paramContext = (PowerManager)paramContext.getSystemService("power");
    if (Util.SDK_INT >= 23) {
      return paramContext.isDeviceIdleMode();
    }
    if (Util.SDK_INT >= 20) {
      if (!paramContext.isInteractive()) {
        return true;
      }
    }
    while (paramContext.isScreenOn()) {
      return false;
    }
    return true;
  }
  
  private static boolean checkInternetConnectivity(ConnectivityManager paramConnectivityManager)
  {
    if (Util.SDK_INT < 23) {
      return true;
    }
    Network localNetwork = paramConnectivityManager.getActiveNetwork();
    boolean bool = false;
    if (localNetwork == null)
    {
      logd("No active network.");
      return false;
    }
    paramConnectivityManager = paramConnectivityManager.getNetworkCapabilities(localNetwork);
    if ((paramConnectivityManager == null) || (!paramConnectivityManager.hasCapability(16))) {
      bool = true;
    }
    paramConnectivityManager = new StringBuilder();
    paramConnectivityManager.append("Network capability validated: ");
    paramConnectivityManager.append(bool);
    logd(paramConnectivityManager.toString());
    return bool ^ true;
  }
  
  private boolean checkNetworkRequirements(Context paramContext)
  {
    int i = getRequiredNetworkType();
    if (i == 0) {
      return true;
    }
    paramContext = (ConnectivityManager)paramContext.getSystemService("connectivity");
    NetworkInfo localNetworkInfo = paramContext.getActiveNetworkInfo();
    if ((localNetworkInfo != null) && (localNetworkInfo.isConnected()))
    {
      if (!checkInternetConnectivity(paramContext)) {
        return false;
      }
      if (i == 1) {
        return true;
      }
      if (i == 3)
      {
        bool = localNetworkInfo.isRoaming();
        paramContext = new StringBuilder();
        paramContext.append("Roaming: ");
        paramContext.append(bool);
        logd(paramContext.toString());
        return bool ^ true;
      }
      boolean bool = isActiveNetworkMetered(paramContext, localNetworkInfo);
      paramContext = new StringBuilder();
      paramContext.append("Metered network: ");
      paramContext.append(bool);
      logd(paramContext.toString());
      if (i == 2) {
        return bool ^ true;
      }
      if (i == 4) {
        return bool;
      }
      throw new IllegalStateException();
    }
    logd("No network info or no connection.");
    return false;
  }
  
  private static boolean isActiveNetworkMetered(ConnectivityManager paramConnectivityManager, NetworkInfo paramNetworkInfo)
  {
    if (Util.SDK_INT >= 16) {
      return paramConnectivityManager.isActiveNetworkMetered();
    }
    int i = paramNetworkInfo.getType();
    return (i != 1) && (i != 7) && (i != 9);
  }
  
  private static void logd(String paramString) {}
  
  public boolean checkRequirements(Context paramContext)
  {
    return (checkNetworkRequirements(paramContext)) && (checkChargingRequirement(paramContext)) && (checkIdleRequirement(paramContext));
  }
  
  public int getRequiredNetworkType()
  {
    return requirements & 0x7;
  }
  
  public int getRequirementsData()
  {
    return requirements;
  }
  
  public boolean isChargingRequired()
  {
    return (requirements & 0x10) != 0;
  }
  
  public boolean isIdleRequired()
  {
    return (requirements & 0x8) != 0;
  }
  
  public String toString()
  {
    return super.toString();
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface NetworkType {}
}
