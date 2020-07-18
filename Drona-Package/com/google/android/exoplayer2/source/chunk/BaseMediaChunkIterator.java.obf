package com.google.android.exoplayer2.source.chunk;

import java.util.NoSuchElementException;

public abstract class BaseMediaChunkIterator
  implements MediaChunkIterator
{
  private long currentIndex;
  private final long fromIndex;
  private final long toIndex;
  
  public BaseMediaChunkIterator(long paramLong1, long paramLong2)
  {
    fromIndex = paramLong1;
    toIndex = paramLong2;
    currentIndex = (paramLong1 - 1L);
  }
  
  protected void checkInBounds()
  {
    if ((currentIndex >= fromIndex) && (currentIndex <= toIndex)) {
      return;
    }
    throw new NoSuchElementException();
  }
  
  protected long getCurrentIndex()
  {
    return currentIndex;
  }
  
  public boolean isEnded()
  {
    return currentIndex > toIndex;
  }
  
  public boolean next()
  {
    currentIndex += 1L;
    return isEnded() ^ true;
  }
}
