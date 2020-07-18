package com.bugsnag.android;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

class DeviceData
{
  private static final String INSTALL_ID_KEY = "install.iud";
  private static final String[] ROOT_INDICATORS = { "/system/xbin/su", "/system/bin/su", "/system/app/Superuser.apk", "/system/app/SuperSU.apk", "/system/app/Superuser", "/system/app/SuperSU", "/system/xbin/daemonsu", "/su/bin" };
  private final Context appContext;
  private final Connectivity connectivity;
  @NonNull
  final String[] cpuAbi;
  private final DisplayMetrics displayMetrics;
  private final boolean emulator;
  @NonNull
  final String locale;
  private final String mId;
  @Nullable
  final Integer orientation;
  private final Resources resources;
  private final boolean rooted;
  @Nullable
  final Float screenDensity;
  @Nullable
  final String screenResolution;
  private final SharedPreferences sharedPrefs;
  
  DeviceData(Connectivity paramConnectivity, Context paramContext, Resources paramResources, SharedPreferences paramSharedPreferences)
  {
    connectivity = paramConnectivity;
    appContext = paramContext;
    resources = paramResources;
    sharedPrefs = paramSharedPreferences;
    if (paramResources != null) {
      displayMetrics = paramResources.getDisplayMetrics();
    } else {
      displayMetrics = null;
    }
    screenDensity = getScreenDensity();
    orientation = getScreenDensityDpi();
    screenResolution = getScreenResolution();
    locale = getLocale();
    cpuAbi = getCpuAbi();
    emulator = isEmulator();
    mId = retrieveUniqueInstallId();
    rooted = isRooted();
  }
  
  private long calculateFreeMemory()
  {
    Runtime localRuntime = Runtime.getRuntime();
    if (localRuntime.maxMemory() != Long.MAX_VALUE) {
      return localRuntime.maxMemory() - localRuntime.totalMemory() + localRuntime.freeMemory();
    }
    return localRuntime.freeMemory();
  }
  
  private String calculateOrientation()
  {
    if (resources != null) {
      switch (resources.getConfiguration().orientation)
      {
      default: 
        break;
      case 2: 
        return "landscape";
      case 1: 
        return "portrait";
      }
    }
    return null;
  }
  
  static long calculateTotalMemory()
  {
    Runtime localRuntime = Runtime.getRuntime();
    if (localRuntime.maxMemory() != Long.MAX_VALUE) {
      return localRuntime.maxMemory();
    }
    return localRuntime.totalMemory();
  }
  
  private Float getBatteryLevel()
  {
    try
    {
      Object localObject = new IntentFilter("android.intent.action.BATTERY_CHANGED");
      Context localContext = appContext;
      localObject = localContext.registerReceiver(null, (IntentFilter)localObject);
      int i = ((Intent)localObject).getIntExtra("level", -1);
      float f = i;
      i = ((Intent)localObject).getIntExtra("scale", -1);
      f /= i;
      return Float.valueOf(f);
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
    Logger.warn("Could not get batteryLevel");
    return null;
  }
  
  private String[] getCpuAbi()
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return SupportedAbiWrapper.getSupportedAbis();
    }
    return Abi2Wrapper.getAbi1andAbi2();
  }
  
  private String getLocale()
  {
    return Locale.getDefault().toString();
  }
  
  private String getLocationStatus()
  {
    Object localObject = appContext;
    try
    {
      localObject = Settings.Secure.getString(((Context)localObject).getContentResolver(), "location_providers_allowed");
      if (localObject != null)
      {
        int i = ((String)localObject).length();
        if (i > 0) {
          return "allowed";
        }
      }
      return "disallowed";
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
    Logger.warn("Could not get locationStatus");
    return null;
  }
  
  private String getNetworkAccess()
  {
    return connectivity.retrieveNetworkAccessState();
  }
  
  private Float getScreenDensity()
  {
    if (displayMetrics != null) {
      return Float.valueOf(displayMetrics.density);
    }
    return null;
  }
  
  private Integer getScreenDensityDpi()
  {
    if (displayMetrics != null) {
      return Integer.valueOf(displayMetrics.densityDpi);
    }
    return null;
  }
  
  private String getScreenResolution()
  {
    if (displayMetrics != null)
    {
      int i = Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels);
      int j = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
      return String.format(Locale.US, "%dx%d", new Object[] { Integer.valueOf(i), Integer.valueOf(j) });
    }
    return null;
  }
  
  private String getTime()
  {
    return DateUtils.toIso8601(new Date());
  }
  
  private Boolean isCharging()
  {
    try
    {
      IntentFilter localIntentFilter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
      Context localContext = appContext;
      int i = localContext.registerReceiver(null, localIntentFilter).getIntExtra("status", -1);
      boolean bool;
      if ((i != 2) && (i != 5)) {
        bool = false;
      } else {
        bool = true;
      }
      return Boolean.valueOf(bool);
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
    Logger.warn("Could not get charging status");
    return null;
  }
  
  private boolean isEmulator()
  {
    String str = Build.FINGERPRINT;
    return (str.startsWith("unknown")) || (str.contains("generic")) || (str.contains("vbox"));
  }
  
  private boolean isRooted()
  {
    if ((Build.TAGS != null) && (Build.TAGS.contains("test-keys"))) {
      return true;
    }
    String[] arrayOfString = ROOT_INDICATORS;
    int j = arrayOfString.length;
    int i = 0;
    for (;;)
    {
      String str;
      if (i < j) {
        str = arrayOfString[i];
      }
      try
      {
        boolean bool = new File(str).exists();
        if (bool) {
          return true;
        }
        i += 1;
      }
      catch (Exception localException) {}
    }
    return false;
    return false;
  }
  
  private String retrieveUniqueInstallId()
  {
    String str2 = sharedPrefs.getString("install.iud", null);
    String str1 = str2;
    if (str2 == null)
    {
      str1 = UUID.randomUUID().toString();
      sharedPrefs.edit().putString("install.iud", str1).apply();
    }
    return str1;
  }
  
  long calculateFreeDisk()
  {
    return Environment.getDataDirectory().getUsableSpace();
  }
  
  Map getDeviceData()
  {
    Map localMap = getDeviceDataSummary();
    localMap.put("id", mId);
    localMap.put("freeMemory", Long.valueOf(calculateFreeMemory()));
    localMap.put("totalMemory", Long.valueOf(calculateTotalMemory()));
    localMap.put("freeDisk", Long.valueOf(calculateFreeDisk()));
    localMap.put("orientation", calculateOrientation());
    return localMap;
  }
  
  Map getDeviceDataSummary()
  {
    HashMap localHashMap1 = new HashMap();
    localHashMap1.put("manufacturer", Build.MANUFACTURER);
    localHashMap1.put("model", Build.MODEL);
    localHashMap1.put("jailbroken", Boolean.valueOf(rooted));
    localHashMap1.put("osName", "android");
    localHashMap1.put("osVersion", Build.VERSION.RELEASE);
    localHashMap1.put("cpuAbi", cpuAbi);
    HashMap localHashMap2 = new HashMap();
    localHashMap2.put("androidApiLevel", Integer.valueOf(Build.VERSION.SDK_INT));
    localHashMap2.put("osBuild", Build.DISPLAY);
    localHashMap1.put("runtimeVersions", localHashMap2);
    return localHashMap1;
  }
  
  Map getDeviceMetaData()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("batteryLevel", getBatteryLevel());
    localHashMap.put("charging", isCharging());
    localHashMap.put("locationStatus", getLocationStatus());
    localHashMap.put("networkAccess", getNetworkAccess());
    localHashMap.put("time", getTime());
    localHashMap.put("brand", Build.BRAND);
    localHashMap.put("locale", locale);
    localHashMap.put("screenDensity", screenDensity);
    localHashMap.put("dpi", orientation);
    localHashMap.put("emulator", Boolean.valueOf(emulator));
    localHashMap.put("screenResolution", screenResolution);
    return localHashMap;
  }
  
  String getId()
  {
    return mId;
  }
  
  static class Abi2Wrapper
  {
    Abi2Wrapper() {}
    
    static String[] getAbi1andAbi2()
    {
      return new String[] { Build.CPU_ABI, Build.CPU_ABI2 };
    }
  }
  
  static class SupportedAbiWrapper
  {
    SupportedAbiWrapper() {}
    
    static String[] getSupportedAbis()
    {
      return Build.SUPPORTED_ABIS;
    }
  }
}
