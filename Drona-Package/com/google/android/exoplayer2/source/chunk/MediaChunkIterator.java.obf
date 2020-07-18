package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.upstream.DataSpec;
import java.util.NoSuchElementException;

public abstract interface MediaChunkIterator
{
  public static final MediaChunkIterator EMPTY = new MediaChunkIterator()
  {
    public long getChunkEndTimeUs()
    {
      throw new NoSuchElementException();
    }
    
    public long getChunkStartTimeUs()
    {
      throw new NoSuchElementException();
    }
    
    public DataSpec getDataSpec()
    {
      throw new NoSuchElementException();
    }
    
    public boolean isEnded()
    {
      return true;
    }
    
    public boolean next()
    {
      return false;
    }
  };
  
  public abstract long getChunkEndTimeUs();
  
  public abstract long getChunkStartTimeUs();
  
  public abstract DataSpec getDataSpec();
  
  public abstract boolean isEnded();
  
  public abstract boolean next();
}
