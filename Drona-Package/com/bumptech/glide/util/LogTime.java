package com.bumptech.glide.util;

import android.os.Build.VERSION;
import android.os.SystemClock;

public final class LogTime
{
  private static final double MILLIS_MULTIPLIER;
  
  static
  {
    int i = Build.VERSION.SDK_INT;
    double d = 1.0D;
    if (i >= 17) {
      d = 1.0D / Math.pow(10.0D, 6.0D);
    }
    MILLIS_MULTIPLIER = d;
  }
  
  private LogTime() {}
  
  public static double getElapsedMillis(long paramLong)
  {
    return (getLogTime() - paramLong) * MILLIS_MULTIPLIER;
  }
  
  public static long getLogTime()
  {
    if (Build.VERSION.SDK_INT >= 17) {
      return SystemClock.elapsedRealtimeNanos();
    }
    return SystemClock.uptimeMillis();
  }
}
