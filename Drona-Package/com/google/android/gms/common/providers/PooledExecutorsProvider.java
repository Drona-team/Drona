package com.google.android.gms.common.providers;

import com.google.android.gms.common.annotation.KeepForSdk;
import java.util.concurrent.ScheduledExecutorService;

@KeepForSdk
public class PooledExecutorsProvider
{
  private static PooledExecutorFactory zzey;
  
  private PooledExecutorsProvider() {}
  
  public static PooledExecutorFactory getInstance()
  {
    try
    {
      if (zzey == null) {
        zzey = new UnsignedInteger();
      }
      PooledExecutorFactory localPooledExecutorFactory = zzey;
      return localPooledExecutorFactory;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public static abstract interface PooledExecutorFactory
  {
    public abstract ScheduledExecutorService newSingleThreadScheduledExecutor();
  }
}
