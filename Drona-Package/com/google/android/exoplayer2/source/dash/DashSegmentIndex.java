package com.google.android.exoplayer2.source.dash;

import com.google.android.exoplayer2.source.dash.manifest.RangedUri;

public abstract interface DashSegmentIndex
{
  public static final int INDEX_UNBOUNDED = -1;
  
  public abstract long getDurationUs(long paramLong1, long paramLong2);
  
  public abstract long getFirstSegmentNum();
  
  public abstract int getSegmentCount(long paramLong);
  
  public abstract long getSegmentNum(long paramLong1, long paramLong2);
  
  public abstract RangedUri getSegmentUrl(long paramLong);
  
  public abstract long getTimeUs(long paramLong);
  
  public abstract boolean isExplicit();
}
