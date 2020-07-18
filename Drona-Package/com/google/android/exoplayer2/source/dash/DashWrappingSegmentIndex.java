package com.google.android.exoplayer2.source.dash;

import com.google.android.exoplayer2.extractor.ChunkIndex;
import com.google.android.exoplayer2.source.dash.manifest.RangedUri;

public final class DashWrappingSegmentIndex
  implements DashSegmentIndex
{
  private final ChunkIndex chunkIndex;
  private final long timeOffsetUs;
  
  public DashWrappingSegmentIndex(ChunkIndex paramChunkIndex, long paramLong)
  {
    chunkIndex = paramChunkIndex;
    timeOffsetUs = paramLong;
  }
  
  public long getDurationUs(long paramLong1, long paramLong2)
  {
    return chunkIndex.durationsUs[((int)paramLong1)];
  }
  
  public long getFirstSegmentNum()
  {
    return 0L;
  }
  
  public int getSegmentCount(long paramLong)
  {
    return chunkIndex.length;
  }
  
  public long getSegmentNum(long paramLong1, long paramLong2)
  {
    return chunkIndex.getChunkIndex(paramLong1 + timeOffsetUs);
  }
  
  public RangedUri getSegmentUrl(long paramLong)
  {
    long[] arrayOfLong = chunkIndex.offsets;
    int i = (int)paramLong;
    return new RangedUri(null, arrayOfLong[i], chunkIndex.sizes[i]);
  }
  
  public long getTimeUs(long paramLong)
  {
    return chunkIndex.timesUs[((int)paramLong)] - timeOffsetUs;
  }
  
  public boolean isExplicit()
  {
    return true;
  }
}
