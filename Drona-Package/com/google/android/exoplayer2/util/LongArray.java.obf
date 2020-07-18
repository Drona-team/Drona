package com.google.android.exoplayer2.util;

import java.util.Arrays;

public final class LongArray
{
  private static final int DEFAULT_INITIAL_CAPACITY = 32;
  private int size;
  private long[] values;
  
  public LongArray()
  {
    this(32);
  }
  
  public LongArray(int paramInt)
  {
    values = new long[paramInt];
  }
  
  public void add(long paramLong)
  {
    if (size == values.length) {
      values = Arrays.copyOf(values, size * 2);
    }
    long[] arrayOfLong = values;
    int i = size;
    size = (i + 1);
    arrayOfLong[i] = paramLong;
  }
  
  public long get(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < size)) {
      return values[paramInt];
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Invalid index ");
    localStringBuilder.append(paramInt);
    localStringBuilder.append(", size is ");
    localStringBuilder.append(size);
    throw new IndexOutOfBoundsException(localStringBuilder.toString());
  }
  
  public int size()
  {
    return size;
  }
  
  public long[] toArray()
  {
    return Arrays.copyOf(values, size);
  }
}
