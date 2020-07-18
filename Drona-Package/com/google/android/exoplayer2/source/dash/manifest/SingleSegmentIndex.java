package com.google.android.exoplayer2.source.dash.manifest;

import com.google.android.exoplayer2.source.dash.DashSegmentIndex;

final class SingleSegmentIndex
  implements DashSegmentIndex
{
  private final RangedUri uri;
  
  public SingleSegmentIndex(RangedUri paramRangedUri)
  {
    uri = paramRangedUri;
  }
  
  public long getDurationUs(long paramLong1, long paramLong2)
  {
    return paramLong2;
  }
  
  public long getFirstSegmentNum()
  {
    return 0L;
  }
  
  public int getSegmentCount(long paramLong)
  {
    return 1;
  }
  
  public long getSegmentNum(long paramLong1, long paramLong2)
  {
    return 0L;
  }
  
  public RangedUri getSegmentUrl(long paramLong)
  {
    return uri;
  }
  
  public long getTimeUs(long paramLong)
  {
    return 0L;
  }
  
  public boolean isExplicit()
  {
    return true;
  }
}
