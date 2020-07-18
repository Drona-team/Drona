package com.google.android.exoplayer2.extractor.ClickListeners;

import com.google.android.exoplayer2.util.Util;

final class FixedSampleSizeRechunker
{
  private static final int MAX_SAMPLE_SIZE = 8192;
  
  private FixedSampleSizeRechunker() {}
  
  public static Results rechunk(int paramInt, long[] paramArrayOfLong, int[] paramArrayOfInt, long paramLong)
  {
    int i1 = 8192 / paramInt;
    int m = paramArrayOfInt.length;
    int k = 0;
    int i = 0;
    int j = 0;
    while (i < m)
    {
      j += Util.ceilDivide(paramArrayOfInt[i], i1);
      i += 1;
    }
    long[] arrayOfLong1 = new long[j];
    int[] arrayOfInt2 = new int[j];
    long[] arrayOfLong2 = new long[j];
    int[] arrayOfInt3 = new int[j];
    m = 0;
    j = 0;
    int n = 0;
    i = k;
    for (;;)
    {
      int[] arrayOfInt1 = paramArrayOfInt;
      if (i >= arrayOfInt1.length) {
        break;
      }
      k = arrayOfInt1[i];
      long l = paramArrayOfLong[i];
      while (k > 0)
      {
        int i2 = Math.min(i1, k);
        arrayOfLong1[j] = l;
        arrayOfInt2[j] = (paramInt * i2);
        n = Math.max(n, arrayOfInt2[j]);
        arrayOfLong2[j] = (m * paramLong);
        arrayOfInt3[j] = 1;
        l += arrayOfInt2[j];
        m += i2;
        k -= i2;
        j += 1;
      }
      i += 1;
    }
    return new Results(arrayOfLong1, arrayOfInt2, n, arrayOfLong2, arrayOfInt3, paramLong * m, null);
  }
  
  public final class Results
  {
    public final long duration;
    public final int[] flags;
    public final int maximumSize;
    public final int[] sizes;
    public final long[] timestamps;
    
    private Results(int[] paramArrayOfInt1, int paramInt, long[] paramArrayOfLong, int[] paramArrayOfInt2, long paramLong)
    {
      sizes = paramArrayOfInt1;
      maximumSize = paramInt;
      timestamps = paramArrayOfLong;
      flags = paramArrayOfInt2;
      duration = paramLong;
    }
  }
}
