package com.bumptech.glide.load.engine;

import androidx.core.util.Pools.Pool;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.pool.FactoryPools;
import com.bumptech.glide.util.pool.FactoryPools.Factory;
import com.bumptech.glide.util.pool.FactoryPools.Poolable;
import com.bumptech.glide.util.pool.StateVerifier;

final class LockedResource<Z>
  implements Resource<Z>, FactoryPools.Poolable
{
  private static final Pools.Pool<LockedResource<?>> POOL = FactoryPools.threadSafe(20, new FactoryPools.Factory()
  {
    public LockedResource create()
    {
      return new LockedResource();
    }
  });
  private boolean isLocked;
  private boolean isRecycled;
  private final StateVerifier stateVerifier = StateVerifier.newInstance();
  private Resource<Z> toWrap;
  
  LockedResource() {}
  
  private void init(Resource paramResource)
  {
    isRecycled = false;
    isLocked = true;
    toWrap = paramResource;
  }
  
  static LockedResource obtain(Resource paramResource)
  {
    LockedResource localLockedResource = (LockedResource)Preconditions.checkNotNull((LockedResource)POOL.acquire());
    localLockedResource.init(paramResource);
    return localLockedResource;
  }
  
  private void release()
  {
    toWrap = null;
    POOL.release(this);
  }
  
  public Object get()
  {
    return toWrap.get();
  }
  
  public Class getResourceClass()
  {
    return toWrap.getResourceClass();
  }
  
  public int getSize()
  {
    return toWrap.getSize();
  }
  
  public StateVerifier getVerifier()
  {
    return stateVerifier;
  }
  
  public void recycle()
  {
    try
    {
      stateVerifier.throwIfRecycled();
      isRecycled = true;
      if (!isLocked)
      {
        toWrap.recycle();
        release();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  void unlock()
  {
    try
    {
      stateVerifier.throwIfRecycled();
      if (isLocked)
      {
        isLocked = false;
        if (isRecycled) {
          recycle();
        }
        return;
      }
      throw new IllegalStateException("Already unlocked");
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}
