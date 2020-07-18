package com.google.android.exoplayer2.source.configurations;

import android.util.SparseArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;

public final class TimestampAdjusterProvider
{
  private final SparseArray<TimestampAdjuster> timestampAdjusters = new SparseArray();
  
  public TimestampAdjusterProvider() {}
  
  public TimestampAdjuster getAdjuster(int paramInt)
  {
    TimestampAdjuster localTimestampAdjuster2 = (TimestampAdjuster)timestampAdjusters.get(paramInt);
    TimestampAdjuster localTimestampAdjuster1 = localTimestampAdjuster2;
    if (localTimestampAdjuster2 == null)
    {
      localTimestampAdjuster1 = new TimestampAdjuster(Long.MAX_VALUE);
      timestampAdjusters.put(paramInt, localTimestampAdjuster1);
    }
    return localTimestampAdjuster1;
  }
  
  public void reset()
  {
    timestampAdjusters.clear();
  }
}
