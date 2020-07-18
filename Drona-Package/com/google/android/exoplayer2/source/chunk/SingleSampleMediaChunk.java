package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.DefaultExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.StatsDataSource;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

public final class SingleSampleMediaChunk
  extends BaseMediaChunk
{
  private boolean loadCompleted;
  private long nextLoadPosition;
  private final Format sampleFormat;
  private final int trackType;
  
  public SingleSampleMediaChunk(DataSource paramDataSource, DataSpec paramDataSpec, Format paramFormat1, int paramInt1, Object paramObject, long paramLong1, long paramLong2, long paramLong3, int paramInt2, Format paramFormat2)
  {
    super(paramDataSource, paramDataSpec, paramFormat1, paramInt1, paramObject, paramLong1, paramLong2, -9223372036854775807L, -9223372036854775807L, paramLong3);
    trackType = paramInt2;
    sampleFormat = paramFormat2;
  }
  
  public void cancelLoad() {}
  
  public boolean isLoadCompleted()
  {
    return loadCompleted;
  }
  
  public void load()
    throws IOException, InterruptedException
  {
    Object localObject1 = dataSpec.subrange(nextLoadPosition);
    try
    {
      long l2 = dataSource.open((DataSpec)localObject1);
      long l1 = l2;
      if (l2 != -1L)
      {
        l1 = nextLoadPosition;
        l1 = l2 + l1;
      }
      localObject1 = new DefaultExtractorInput(dataSource, nextLoadPosition, l1);
      Object localObject2 = getOutput();
      ((BaseMediaChunkOutput)localObject2).setSampleOffsetUs(0L);
      int j = trackType;
      int i = 0;
      localObject2 = ((BaseMediaChunkOutput)localObject2).track(0, j);
      ((TrackOutput)localObject2).format(sampleFormat);
      while (i != -1)
      {
        l1 = nextLoadPosition;
        l2 = i;
        nextLoadPosition = (l1 + l2);
        i = ((TrackOutput)localObject2).sampleData((ExtractorInput)localObject1, Integer.MAX_VALUE, true);
      }
      i = (int)nextLoadPosition;
      ((TrackOutput)localObject2).sampleMetadata(startTimeUs, 1, i, 0, null);
      Util.closeQuietly(dataSource);
      loadCompleted = true;
      return;
    }
    catch (Throwable localThrowable)
    {
      Util.closeQuietly(dataSource);
      throw localThrowable;
    }
  }
}
