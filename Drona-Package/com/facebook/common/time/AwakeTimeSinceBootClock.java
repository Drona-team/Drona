package com.facebook.common.time;

import android.os.SystemClock;
import com.facebook.common.internal.DoNotStrip;

@DoNotStrip
public class AwakeTimeSinceBootClock
  implements MonotonicClock
{
  @DoNotStrip
  private static final AwakeTimeSinceBootClock INSTANCE = new AwakeTimeSinceBootClock();
  
  private AwakeTimeSinceBootClock() {}
  
  public static AwakeTimeSinceBootClock deepCopy()
  {
    return INSTANCE;
  }
  
  public long now()
  {
    return SystemClock.uptimeMillis();
  }
}