package com.bumptech.glide.load.engine.cache;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.Resource;

public class MemoryCacheAdapter
  implements MemoryCache
{
  private MemoryCache.ResourceRemovedListener listener;
  
  public MemoryCacheAdapter() {}
  
  public void clearMemory() {}
  
  public long getCurrentSize()
  {
    return 0L;
  }
  
  public long getMaxSize()
  {
    return 0L;
  }
  
  public Resource put(Key paramKey, Resource paramResource)
  {
    if (paramResource != null) {
      listener.onResourceRemoved(paramResource);
    }
    return null;
  }
  
  public Resource remove(Key paramKey)
  {
    return null;
  }
  
  public void setResourceRemovedListener(MemoryCache.ResourceRemovedListener paramResourceRemovedListener)
  {
    listener = paramResourceRemovedListener;
  }
  
  public void setSizeMultiplier(float paramFloat) {}
  
  public void trimMemory(int paramInt) {}
}
