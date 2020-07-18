package com.google.android.gms.common.package_6.internal;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.util.PlatformVersion;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.concurrent.GuardedBy;

@KeepForSdk
public final class BackgroundDetector
  implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2
{
  private static final BackgroundDetector zzat = new BackgroundDetector();
  private final AtomicBoolean zzau = new AtomicBoolean();
  private final AtomicBoolean zzav = new AtomicBoolean();
  @GuardedBy("sInstance")
  private final ArrayList<com.google.android.gms.common.api.internal.BackgroundDetector.BackgroundStateChangeListener> zzaw = new ArrayList();
  @GuardedBy("sInstance")
  private boolean zzax = false;
  
  private BackgroundDetector() {}
  
  public static BackgroundDetector getInstance()
  {
    return zzat;
  }
  
  public static void initialize(Application paramApplication)
  {
    BackgroundDetector localBackgroundDetector = zzat;
    try
    {
      if (!zzatzzax)
      {
        paramApplication.registerActivityLifecycleCallbacks(zzat);
        paramApplication.registerComponentCallbacks(zzat);
        zzatzzax = true;
      }
      return;
    }
    catch (Throwable paramApplication)
    {
      throw paramApplication;
    }
  }
  
  private final void onBackgroundStateChanged(boolean paramBoolean)
  {
    BackgroundDetector localBackgroundDetector = zzat;
    try
    {
      ArrayList localArrayList = (ArrayList)zzaw;
      int j = localArrayList.size();
      int i = 0;
      while (i < j)
      {
        Object localObject = localArrayList.get(i);
        i += 1;
        ((BackgroundStateChangeListener)localObject).onBackgroundStateChanged(paramBoolean);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final void addListener(BackgroundStateChangeListener paramBackgroundStateChangeListener)
  {
    BackgroundDetector localBackgroundDetector = zzat;
    try
    {
      zzaw.add(paramBackgroundStateChangeListener);
      return;
    }
    catch (Throwable paramBackgroundStateChangeListener)
    {
      throw paramBackgroundStateChangeListener;
    }
  }
  
  public final boolean isInBackground()
  {
    return zzau.get();
  }
  
  public final void onActivityCreated(Activity paramActivity, Bundle paramBundle)
  {
    boolean bool = zzau.compareAndSet(true, false);
    zzav.set(true);
    if (bool) {
      onBackgroundStateChanged(false);
    }
  }
  
  public final void onActivityDestroyed(Activity paramActivity) {}
  
  public final void onActivityPaused(Activity paramActivity) {}
  
  public final void onActivityResumed(Activity paramActivity)
  {
    boolean bool = zzau.compareAndSet(true, false);
    zzav.set(true);
    if (bool) {
      onBackgroundStateChanged(false);
    }
  }
  
  public final void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle) {}
  
  public final void onActivityStarted(Activity paramActivity) {}
  
  public final void onActivityStopped(Activity paramActivity) {}
  
  public final void onConfigurationChanged(Configuration paramConfiguration) {}
  
  public final void onLowMemory() {}
  
  public final void onTrimMemory(int paramInt)
  {
    if ((paramInt == 20) && (zzau.compareAndSet(false, true)))
    {
      zzav.set(true);
      onBackgroundStateChanged(true);
    }
  }
  
  public final boolean readCurrentStateIfPossible(boolean paramBoolean)
  {
    if (!zzav.get()) {
      if (PlatformVersion.isAtLeastJellyBean())
      {
        ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(localRunningAppProcessInfo);
        if ((!zzav.getAndSet(true)) && (importance > 100)) {
          zzau.set(true);
        }
      }
      else
      {
        return paramBoolean;
      }
    }
    return isInBackground();
  }
  
  @KeepForSdk
  public abstract interface BackgroundStateChangeListener
  {
    public abstract void onBackgroundStateChanged(boolean paramBoolean);
  }
}
