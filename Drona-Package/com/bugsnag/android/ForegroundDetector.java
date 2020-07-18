package com.bugsnag.android;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Process;
import java.util.Iterator;
import java.util.List;

class ForegroundDetector
{
  private final ActivityManager activityManager;
  
  ForegroundDetector(Context paramContext)
  {
    activityManager = ((ActivityManager)paramContext.getSystemService("activity"));
  }
  
  private ActivityManager.RunningAppProcessInfo getProcessInfo()
  {
    if (Build.VERSION.SDK_INT >= 16)
    {
      ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
      ActivityManager.getMyMemoryState(localRunningAppProcessInfo);
      return localRunningAppProcessInfo;
    }
    return getProcessInfoPreApi16();
  }
  
  private ActivityManager.RunningAppProcessInfo getProcessInfoPreApi16()
  {
    Object localObject = activityManager.getRunningAppProcesses();
    if (localObject != null)
    {
      int i = Process.myPid();
      localObject = ((List)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = (ActivityManager.RunningAppProcessInfo)((Iterator)localObject).next();
        if (i == pid) {
          return localRunningAppProcessInfo;
        }
      }
    }
    return null;
  }
  
  Boolean isInForeground()
  {
    try
    {
      ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = getProcessInfo();
      if (localRunningAppProcessInfo != null)
      {
        int i = importance;
        boolean bool;
        if (i <= 100) {
          bool = true;
        } else {
          bool = false;
        }
        return Boolean.valueOf(bool);
      }
      return null;
    }
    catch (RuntimeException localRuntimeException) {}
    return null;
  }
}