package com.google.android.gms.common.stats;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.util.ImageList;
import com.google.android.gms.common.util.VisibleForTesting;
import java.util.Arrays;
import java.util.List;

@KeepForSdk
public class WakeLockTracker
{
  private static WakeLockTracker zzgc = new WakeLockTracker();
  private static Boolean zzgd;
  @VisibleForTesting
  private static boolean zzge = false;
  
  public WakeLockTracker() {}
  
  private static boolean fetchData()
  {
    if (zzgd == null) {
      zzgd = Boolean.valueOf(false);
    }
    return zzgd.booleanValue();
  }
  
  public static WakeLockTracker getInstance()
  {
    return zzgc;
  }
  
  private static void updateAppWidget(Context paramContext, WakeLockEvent paramWakeLockEvent)
  {
    try
    {
      Intent localIntent = new Intent();
      ComponentName localComponentName = LoggingConstants.zzfg;
      paramContext.startService(localIntent.setComponent(localComponentName).putExtra("com.google.android.gms.common.stats.EXTRA_LOG_EVENT", paramWakeLockEvent));
      return;
    }
    catch (Exception paramContext)
    {
      Log.wtf("WakeLockTracker", paramContext);
    }
  }
  
  public void registerAcquireEvent(Context paramContext, Intent paramIntent, String paramString1, String paramString2, String paramString3, int paramInt, String paramString4)
  {
    paramString4 = Arrays.asList(new String[] { paramString4 });
    registerEvent(paramContext, paramIntent.getStringExtra("WAKE_LOCK_KEY"), 7, paramString1, paramString2, paramString3, paramInt, paramString4);
  }
  
  public void registerDeadlineEvent(Context paramContext, String paramString1, String paramString2, String paramString3, int paramInt, List paramList, boolean paramBoolean, long paramLong)
  {
    if (!fetchData()) {
      return;
    }
    updateAppWidget(paramContext, new WakeLockEvent(System.currentTimeMillis(), 16, paramString1, paramInt, StatsUtils.init(paramList), null, paramLong, ImageList.init(paramContext), paramString2, StatsUtils.attribute(paramContext.getPackageName()), ImageList.load(paramContext), 0L, paramString3, paramBoolean));
  }
  
  public void registerEvent(Context paramContext, String paramString1, int paramInt1, String paramString2, String paramString3, String paramString4, int paramInt2, List paramList)
  {
    registerEvent(paramContext, paramString1, paramInt1, paramString2, paramString3, paramString4, paramInt2, paramList, 0L);
  }
  
  public void registerEvent(Context paramContext, String paramString1, int paramInt1, String paramString2, String paramString3, String paramString4, int paramInt2, List paramList, long paramLong)
  {
    if (!fetchData()) {
      return;
    }
    if (TextUtils.isEmpty(paramString1))
    {
      paramContext = String.valueOf(paramString1);
      if (paramContext.length() != 0) {
        paramContext = "missing wakeLock key. ".concat(paramContext);
      } else {
        paramContext = new String("missing wakeLock key. ");
      }
      Log.e("WakeLockTracker", paramContext);
      return;
    }
    if ((7 == paramInt1) || (8 == paramInt1) || (10 == paramInt1) || (11 == paramInt1)) {
      updateAppWidget(paramContext, new WakeLockEvent(System.currentTimeMillis(), paramInt1, paramString2, paramInt2, StatsUtils.init(paramList), paramString1, SystemClock.elapsedRealtime(), ImageList.init(paramContext), paramString3, StatsUtils.attribute(paramContext.getPackageName()), ImageList.load(paramContext), paramLong, paramString4, false));
    }
  }
  
  public void registerReleaseEvent(Context paramContext, Intent paramIntent)
  {
    registerEvent(paramContext, paramIntent.getStringExtra("WAKE_LOCK_KEY"), 8, null, null, null, 0, null);
  }
}
