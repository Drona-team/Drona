package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;

public abstract class BaseMediaChunk
  extends MediaChunk
{
  public final long clippedEndTimeUs;
  public final long clippedStartTimeUs;
  private int[] firstSampleIndices;
  private BaseMediaChunkOutput output;
  
  public BaseMediaChunk(DataSource paramDataSource, DataSpec paramDataSpec, Format paramFormat, int paramInt, Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5)
  {
    super(paramDataSource, paramDataSpec, paramFormat, paramInt, paramObject, paramLong1, paramLong2, paramLong5);
    clippedStartTimeUs = paramLong3;
    clippedEndTimeUs = paramLong4;
  }
  
  public final int getFirstSampleIndex(int paramInt)
  {
    return firstSampleIndices[paramInt];
  }
  
  protected final BaseMediaChunkOutput getOutput()
  {
    return output;
  }
  
  public void init(BaseMediaChunkOutput paramBaseMediaChunkOutput)
  {
    output = paramBaseMediaChunkOutput;
    firstSampleIndices = paramBaseMediaChunkOutput.getWriteIndices();
  }
}
