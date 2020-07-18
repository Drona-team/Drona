package com.google.android.exoplayer2.util;

import java.util.Arrays;

public final class TimedValueQueue<V>
{
  private static final int INITIAL_BUFFER_SIZE = 10;
  private int first;
  private int size;
  private long[] timestamps;
  private V[] values;
  
  public TimedValueQueue()
  {
    this(10);
  }
  
  public TimedValueQueue(int paramInt)
  {
    timestamps = new long[paramInt];
    values = newArray(paramInt);
  }
  
  private void addUnchecked(long paramLong, Object paramObject)
  {
    int i = (first + size) % values.length;
    timestamps[i] = paramLong;
    values[i] = paramObject;
    size += 1;
  }
  
  private void clearBufferOnTimeDiscontinuity(long paramLong)
  {
    if (size > 0)
    {
      int i = first;
      int j = size;
      int k = values.length;
      if (paramLong <= timestamps[((i + j - 1) % k)]) {
        clear();
      }
    }
  }
  
  private void doubleCapacityIfFull()
  {
    int i = values.length;
    if (size < i) {
      return;
    }
    int j = i * 2;
    long[] arrayOfLong = new long[j];
    Object[] arrayOfObject = newArray(j);
    i -= first;
    System.arraycopy(timestamps, first, arrayOfLong, 0, i);
    System.arraycopy(values, first, arrayOfObject, 0, i);
    if (first > 0)
    {
      System.arraycopy(timestamps, 0, arrayOfLong, i, first);
      System.arraycopy(values, 0, arrayOfObject, i, first);
    }
    timestamps = arrayOfLong;
    values = arrayOfObject;
    first = 0;
  }
  
  private static Object[] newArray(int paramInt)
  {
    return (Object[])new Object[paramInt];
  }
  
  private Object poll(long paramLong, boolean paramBoolean)
  {
    long l1 = Long.MAX_VALUE;
    Object localObject = null;
    while (size > 0)
    {
      long l2 = paramLong - timestamps[first];
      if (l2 < 0L)
      {
        if (paramBoolean) {
          break;
        }
        if (-l2 >= l1) {
          return localObject;
        }
      }
      localObject = values[first];
      values[first] = null;
      first = ((first + 1) % values.length);
      size -= 1;
      l1 = l2;
    }
    return localObject;
  }
  
  public void clear()
  {
    try
    {
      first = 0;
      size = 0;
      Arrays.fill(values, null);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void concatenate(long paramLong, Object paramObject)
  {
    try
    {
      clearBufferOnTimeDiscontinuity(paramLong);
      doubleCapacityIfFull();
      addUnchecked(paramLong, paramObject);
      return;
    }
    catch (Throwable paramObject)
    {
      throw paramObject;
    }
  }
  
  public Object poll(long paramLong)
  {
    try
    {
      Object localObject = poll(paramLong, false);
      return localObject;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public Object pollFloor(long paramLong)
  {
    try
    {
      Object localObject = poll(paramLong, true);
      return localObject;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int size()
  {
    try
    {
      int i = size;
      return i;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}
