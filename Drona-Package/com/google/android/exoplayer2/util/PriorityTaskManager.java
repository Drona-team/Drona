package com.google.android.exoplayer2.util;

import java.io.IOException;
import java.util.Collections;
import java.util.PriorityQueue;

public final class PriorityTaskManager
{
  private int highestPriority = Integer.MIN_VALUE;
  private final Object lock = new Object();
  private final PriorityQueue<Integer> queue = new PriorityQueue(10, Collections.reverseOrder());
  
  public PriorityTaskManager() {}
  
  public void add(int paramInt)
  {
    Object localObject = lock;
    try
    {
      queue.add(Integer.valueOf(paramInt));
      highestPriority = Math.max(highestPriority, paramInt);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void proceed(int paramInt)
    throws InterruptedException
  {
    Object localObject = lock;
    try
    {
      while (highestPriority != paramInt) {
        lock.wait();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean proceedNonBlocking(int paramInt)
  {
    Object localObject = lock;
    for (;;)
    {
      try
      {
        if (highestPriority == paramInt)
        {
          bool = true;
          return bool;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      boolean bool = false;
    }
  }
  
  public void proceedOrThrow(int paramInt)
    throws PriorityTaskManager.PriorityTooLowException
  {
    Object localObject = lock;
    try
    {
      if (highestPriority == paramInt) {
        return;
      }
      throw new PriorityTooLowException(paramInt, highestPriority);
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void remove(int paramInt)
  {
    Object localObject = lock;
    try
    {
      queue.remove(Integer.valueOf(paramInt));
      if (queue.isEmpty()) {
        paramInt = Integer.MIN_VALUE;
      } else {
        paramInt = ((Integer)Util.castNonNull(queue.peek())).intValue();
      }
      highestPriority = paramInt;
      lock.notifyAll();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public static class PriorityTooLowException
    extends IOException
  {
    public PriorityTooLowException(int paramInt1, int paramInt2)
    {
      super();
    }
  }
}
