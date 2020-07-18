package com.bumptech.glide.load.engine.cache;

import java.io.File;

public class DiskLruCacheFactory
  implements DiskCache.Factory
{
  private final CacheDirectoryGetter cacheDirectoryGetter;
  private final long diskCacheSize;
  
  public DiskLruCacheFactory(CacheDirectoryGetter paramCacheDirectoryGetter, long paramLong)
  {
    diskCacheSize = paramLong;
    cacheDirectoryGetter = paramCacheDirectoryGetter;
  }
  
  public DiskLruCacheFactory(String paramString, long paramLong)
  {
    this(new CacheDirectoryGetter()
    {
      public File getCacheDirectory()
      {
        return new File(DiskLruCacheFactory.this);
      }
    }, paramLong);
  }
  
  public DiskLruCacheFactory(String paramString1, final String paramString2, long paramLong)
  {
    this(new CacheDirectoryGetter()
    {
      public File getCacheDirectory()
      {
        return new File(DiskLruCacheFactory.this, paramString2);
      }
    }, paramLong);
  }
  
  public DiskCache build()
  {
    File localFile = cacheDirectoryGetter.getCacheDirectory();
    if (localFile == null) {
      return null;
    }
    if (!localFile.mkdirs())
    {
      if (!localFile.exists()) {
        break label48;
      }
      if (!localFile.isDirectory()) {
        return null;
      }
    }
    return DiskLruCacheWrapper.create(localFile, diskCacheSize);
    label48:
    return null;
  }
  
  public static abstract interface CacheDirectoryGetter
  {
    public abstract File getCacheDirectory();
  }
}
