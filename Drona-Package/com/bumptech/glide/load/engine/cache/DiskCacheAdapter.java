package com.bumptech.glide.load.engine.cache;

import com.bumptech.glide.load.Key;
import java.io.File;

public class DiskCacheAdapter
  implements DiskCache
{
  public DiskCacheAdapter() {}
  
  public void clear() {}
  
  public void delete(Key paramKey) {}
  
  public File get(Key paramKey)
  {
    return null;
  }
  
  public void put(Key paramKey, DiskCache.Writer paramWriter) {}
  
  public static final class Factory
    implements DiskCache.Factory
  {
    public Factory() {}
    
    public DiskCache build()
    {
      return new DiskCacheAdapter();
    }
  }
}
