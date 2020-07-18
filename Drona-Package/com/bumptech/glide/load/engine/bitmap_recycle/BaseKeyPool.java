package com.bumptech.glide.load.engine.bitmap_recycle;

import com.bumptech.glide.util.Util;
import java.util.Queue;

abstract class BaseKeyPool<T extends Poolable>
{
  private static final int MAX_SIZE = 20;
  private final Queue<T> keyPool = Util.createQueue(20);
  
  BaseKeyPool() {}
  
  abstract Poolable create();
  
  Poolable get()
  {
    Poolable localPoolable2 = (Poolable)keyPool.poll();
    Poolable localPoolable1 = localPoolable2;
    if (localPoolable2 == null) {
      localPoolable1 = create();
    }
    return localPoolable1;
  }
  
  public void offer(Poolable paramPoolable)
  {
    if (keyPool.size() < 20) {
      keyPool.offer(paramPoolable);
    }
  }
}
