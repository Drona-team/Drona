package com.google.android.exoplayer2.source;

public class CompositeSequenceableLoader
  implements SequenceableLoader
{
  protected final SequenceableLoader[] loaders;
  
  public CompositeSequenceableLoader(SequenceableLoader[] paramArrayOfSequenceableLoader)
  {
    loaders = paramArrayOfSequenceableLoader;
  }
  
  public boolean continueLoading(long paramLong)
  {
    boolean bool3 = false;
    boolean bool1;
    boolean bool4;
    do
    {
      long l1 = getNextLoadPositionUs();
      if (l1 == Long.MIN_VALUE) {
        return bool3;
      }
      SequenceableLoader[] arrayOfSequenceableLoader = loaders;
      int k = arrayOfSequenceableLoader.length;
      int i = 0;
      boolean bool2;
      for (bool1 = false; i < k; bool1 = bool2)
      {
        SequenceableLoader localSequenceableLoader = arrayOfSequenceableLoader[i];
        long l2 = localSequenceableLoader.getNextLoadPositionUs();
        int j;
        if ((l2 != Long.MIN_VALUE) && (l2 <= paramLong)) {
          j = 1;
        } else {
          j = 0;
        }
        if (l2 != l1)
        {
          bool2 = bool1;
          if (j == 0) {}
        }
        else
        {
          bool2 = bool1 | localSequenceableLoader.continueLoading(paramLong);
        }
        i += 1;
      }
      bool4 = bool3 | bool1;
      bool3 = bool4;
    } while (bool1);
    return bool4;
  }
  
  public final long getBufferedPositionUs()
  {
    SequenceableLoader[] arrayOfSequenceableLoader = loaders;
    int j = arrayOfSequenceableLoader.length;
    int i = 0;
    long l2;
    for (long l1 = Long.MAX_VALUE; i < j; l1 = l2)
    {
      long l3 = arrayOfSequenceableLoader[i].getBufferedPositionUs();
      l2 = l1;
      if (l3 != Long.MIN_VALUE) {
        l2 = Math.min(l1, l3);
      }
      i += 1;
    }
    if (l1 == Long.MAX_VALUE) {
      return Long.MIN_VALUE;
    }
    return l1;
  }
  
  public final long getNextLoadPositionUs()
  {
    SequenceableLoader[] arrayOfSequenceableLoader = loaders;
    int j = arrayOfSequenceableLoader.length;
    int i = 0;
    long l2;
    for (long l1 = Long.MAX_VALUE; i < j; l1 = l2)
    {
      long l3 = arrayOfSequenceableLoader[i].getNextLoadPositionUs();
      l2 = l1;
      if (l3 != Long.MIN_VALUE) {
        l2 = Math.min(l1, l3);
      }
      i += 1;
    }
    if (l1 == Long.MAX_VALUE) {
      return Long.MIN_VALUE;
    }
    return l1;
  }
  
  public final void reevaluateBuffer(long paramLong)
  {
    SequenceableLoader[] arrayOfSequenceableLoader = loaders;
    int j = arrayOfSequenceableLoader.length;
    int i = 0;
    while (i < j)
    {
      arrayOfSequenceableLoader[i].reevaluateBuffer(paramLong);
      i += 1;
    }
  }
}
