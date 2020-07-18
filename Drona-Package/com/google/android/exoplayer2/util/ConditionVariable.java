package com.google.android.exoplayer2.util;

import android.os.SystemClock;

public final class ConditionVariable
{
  private boolean isOpen;
  
  public ConditionVariable() {}
  
  public void block()
    throws InterruptedException
  {
    try
    {
      while (!isOpen) {
        wait();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean block(long paramLong)
    throws InterruptedException
  {
    try
    {
      long l2 = SystemClock.elapsedRealtime();
      long l1 = l2;
      paramLong += l2;
      while ((!isOpen) && (l1 < paramLong))
      {
        wait(paramLong - l1);
        l1 = SystemClock.elapsedRealtime();
      }
      boolean bool = isOpen;
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean close()
  {
    try
    {
      boolean bool = isOpen;
      isOpen = false;
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean open()
  {
    try
    {
      boolean bool = isOpen;
      if (bool) {
        return false;
      }
      isOpen = true;
      notifyAll();
      return true;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}
