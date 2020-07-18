package com.bumptech.glide.load.engine.cache;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.LruCache;

public class LruResourceCache
  extends LruCache<Key, Resource<?>>
  implements MemoryCache
{
  private MemoryCache.ResourceRemovedListener listener;
  
  public LruResourceCache(long paramLong)
  {
    super(paramLong);
  }
  
  protected int getSize(Resource paramResource)
  {
    if (paramResource == null) {
      return super.getSize(null);
    }
    return paramResource.getSize();
  }
  
  protected void onItemEvicted(Key paramKey, Resource paramResource)
  {
    if ((listener != null) && (paramResource != null)) {
      listener.onResourceRemoved(paramResource);
    }
  }
  
  public void setResourceRemovedListener(MemoryCache.ResourceRemovedListener paramResourceRemovedListener)
  {
    listener = paramResourceRemovedListener;
  }
  
  public void trimMemory(int paramInt)
  {
    if (paramInt >= 40)
    {
      clearMemory();
      return;
    }
    if ((paramInt >= 20) || (paramInt == 15)) {
      trimToSize(getMaxSize() / 2L);
    }
  }
}
