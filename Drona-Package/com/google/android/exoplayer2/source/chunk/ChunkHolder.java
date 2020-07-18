package com.google.android.exoplayer2.source.chunk;

public final class ChunkHolder
{
  public Chunk chunk;
  public boolean endOfStream;
  
  public ChunkHolder() {}
  
  public void clear()
  {
    chunk = null;
    endOfStream = false;
  }
}
