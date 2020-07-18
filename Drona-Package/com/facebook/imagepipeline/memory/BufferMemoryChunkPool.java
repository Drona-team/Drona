package com.facebook.imagepipeline.memory;

import com.facebook.common.memory.MemoryTrimmableRegistry;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class BufferMemoryChunkPool
  extends MemoryChunkPool
{
  public BufferMemoryChunkPool(MemoryTrimmableRegistry paramMemoryTrimmableRegistry, PoolParams paramPoolParams, PoolStatsTracker paramPoolStatsTracker)
  {
    super(paramMemoryTrimmableRegistry, paramPoolParams, paramPoolStatsTracker);
  }
  
  protected BufferMemoryChunk alloc(int paramInt)
  {
    return new BufferMemoryChunk(paramInt);
  }
}