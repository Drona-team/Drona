package com.google.android.gms.common.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.os.SystemClock;

public final class ImageList
{
  private static final IntentFilter filter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
  private static long zzgv;
  private static float zzgw = NaN.0F;
  
  public static int init(Context paramContext)
  {
    if (paramContext != null)
    {
      if (paramContext.getApplicationContext() == null) {
        return -1;
      }
      Intent localIntent = paramContext.getApplicationContext().registerReceiver(null, filter);
      int j = 0;
      int i;
      if (localIntent == null) {
        i = 0;
      } else {
        i = localIntent.getIntExtra("plugged", 0);
      }
      if ((i & 0x7) != 0) {
        i = 1;
      } else {
        i = 0;
      }
      paramContext = (PowerManager)paramContext.getSystemService("power");
      if (paramContext == null) {
        return -1;
      }
      boolean bool;
      if (PlatformVersion.isAtLeastKitKatWatch()) {
        bool = paramContext.isInteractive();
      } else {
        bool = paramContext.isScreenOn();
      }
      if (bool) {
        j = 2;
      }
      return j | i;
    }
    return -1;
  }
  
  public static float load(Context paramContext)
  {
    try
    {
      if ((SystemClock.elapsedRealtime() - zzgv < 60000L) && (!Float.isNaN(zzgw)))
      {
        f = zzgw;
        return f;
      }
      paramContext = paramContext.getApplicationContext().registerReceiver(null, filter);
      if (paramContext != null)
      {
        int i = paramContext.getIntExtra("level", -1);
        int j = paramContext.getIntExtra("scale", -1);
        zzgw = i / j;
      }
      zzgv = SystemClock.elapsedRealtime();
      float f = zzgw;
      return f;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
}
