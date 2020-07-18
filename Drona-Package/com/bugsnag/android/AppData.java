package com.bugsnag.android;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

class AppData
{
  static final String RELEASE_STAGE_DEVELOPMENT = "development";
  static final String RELEASE_STAGE_PRODUCTION = "production";
  private static final long startTimeMs = ;
  private final Context appContext;
  @Nullable
  final String appName;
  @Nullable
  private ApplicationInfo applicationInfo;
  private String binaryArch = null;
  private final Configuration config;
  @Nullable
  private PackageInfo packageInfo;
  private PackageManager packageManager;
  private final String packageName;
  private final SessionTracker sessionTracker;
  
  AppData(Context paramContext, PackageManager paramPackageManager, Configuration paramConfiguration, SessionTracker paramSessionTracker)
  {
    appContext = paramContext;
    packageManager = paramPackageManager;
    config = paramConfiguration;
    sessionTracker = paramSessionTracker;
    packageName = paramContext.getPackageName();
    packageManager = paramPackageManager;
    paramContext = packageManager;
    paramPackageManager = packageName;
    try
    {
      paramContext = paramContext.getPackageInfo(paramPackageManager, 0);
      packageInfo = paramContext;
      paramContext = packageManager;
      paramPackageManager = packageName;
      paramContext = paramContext.getApplicationInfo(paramPackageManager, 0);
      applicationInfo = paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;) {}
    }
    paramContext = new StringBuilder();
    paramContext.append("Could not retrieve package/application information for ");
    paramContext.append(packageName);
    Logger.warn(paramContext.toString());
    appName = getAppName();
  }
  
  private String calculateNotifierType()
  {
    String str = config.getNotifierType();
    if (str != null) {
      return str;
    }
    return "android";
  }
  
  private Integer calculateVersionCode()
  {
    Integer localInteger = config.getVersionCode();
    if (localInteger != null) {
      return localInteger;
    }
    if (packageInfo != null) {
      return Integer.valueOf(packageInfo.versionCode);
    }
    return null;
  }
  
  private String calculateVersionName()
  {
    String str = config.getAppVersion();
    if (str != null) {
      return str;
    }
    if (packageInfo != null) {
      return packageInfo.versionName;
    }
    return null;
  }
  
  private String getAppName()
  {
    if ((packageManager != null) && (applicationInfo != null)) {
      return packageManager.getApplicationLabel(applicationInfo).toString();
    }
    return null;
  }
  
  static long getDurationMs()
  {
    return SystemClock.elapsedRealtime() - startTimeMs;
  }
  
  private long getMemoryUsage()
  {
    Runtime localRuntime = Runtime.getRuntime();
    return localRuntime.totalMemory() - localRuntime.freeMemory();
  }
  
  private Boolean isLowMemory()
  {
    Object localObject = appContext;
    try
    {
      localObject = ((Context)localObject).getSystemService("activity");
      localObject = (ActivityManager)localObject;
      if (localObject == null) {
        break label49;
      }
      ActivityManager.MemoryInfo localMemoryInfo = new ActivityManager.MemoryInfo();
      ((ActivityManager)localObject).getMemoryInfo(localMemoryInfo);
      boolean bool = lowMemory;
      return Boolean.valueOf(bool);
    }
    catch (Exception localException)
    {
      label49:
      for (;;) {}
    }
    Logger.warn("Could not check lowMemory status");
    return null;
  }
  
  Long calculateDurationInForeground()
  {
    long l = System.currentTimeMillis();
    return sessionTracker.getDurationInForegroundMs(l);
  }
  
  String getActiveScreenClass()
  {
    return sessionTracker.getContextActivity();
  }
  
  Map getAppData()
  {
    Map localMap = getAppDataSummary();
    localMap.put("id", packageName);
    localMap.put("buildUUID", config.getBuildUUID());
    localMap.put("duration", Long.valueOf(getDurationMs()));
    localMap.put("durationInForeground", calculateDurationInForeground());
    localMap.put("inForeground", sessionTracker.isInForeground());
    localMap.put("packageName", packageName);
    localMap.put("binaryArch", binaryArch);
    return localMap;
  }
  
  Map getAppDataMetaData()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("name", appName);
    localHashMap.put("packageName", packageName);
    localHashMap.put("versionName", calculateVersionName());
    localHashMap.put("activeScreen", getActiveScreenClass());
    localHashMap.put("memoryUsage", Long.valueOf(getMemoryUsage()));
    localHashMap.put("lowMemory", isLowMemory());
    return localHashMap;
  }
  
  Map getAppDataSummary()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("type", calculateNotifierType());
    localHashMap.put("releaseStage", guessReleaseStage());
    localHashMap.put("version", calculateVersionName());
    localHashMap.put("versionCode", calculateVersionCode());
    localHashMap.put("codeBundleId", config.getCodeBundleId());
    return localHashMap;
  }
  
  String guessReleaseStage()
  {
    String str = config.getReleaseStage();
    if (str != null) {
      return str;
    }
    if ((applicationInfo != null) && ((applicationInfo.flags & 0x2) != 0)) {
      return "development";
    }
    return "production";
  }
  
  void setBinaryArch(String paramString)
  {
    binaryArch = paramString;
  }
}
