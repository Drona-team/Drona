package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.SeekParameters;
import java.io.IOException;
import java.util.List;

public abstract interface ChunkSource
{
  public abstract long getAdjustedSeekPositionUs(long paramLong, SeekParameters paramSeekParameters);
  
  public abstract void getNextChunk(long paramLong1, long paramLong2, List paramList, ChunkHolder paramChunkHolder);
  
  public abstract int getPreferredQueueSize(long paramLong, List paramList);
  
  public abstract void maybeThrowError()
    throws IOException;
  
  public abstract void onChunkLoadCompleted(Chunk paramChunk);
  
  public abstract boolean onChunkLoadError(Chunk paramChunk, boolean paramBoolean, Exception paramException, long paramLong);
}
