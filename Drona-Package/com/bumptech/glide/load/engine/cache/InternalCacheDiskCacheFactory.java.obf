package com.bumptech.glide.load.engine.cache;

import android.content.Context;
import java.io.File;

public final class InternalCacheDiskCacheFactory
  extends DiskLruCacheFactory
{
  public InternalCacheDiskCacheFactory(Context paramContext)
  {
    this(paramContext, "image_manager_disk_cache", 262144000L);
  }
  
  public InternalCacheDiskCacheFactory(Context paramContext, long paramLong)
  {
    this(paramContext, "image_manager_disk_cache", paramLong);
  }
  
  public InternalCacheDiskCacheFactory(Context paramContext, final String paramString, long paramLong)
  {
    super(new DiskLruCacheFactory.CacheDirectoryGetter()
    {
      public File getCacheDirectory()
      {
        File localFile = getCacheDir();
        if (localFile == null) {
          return null;
        }
        if (paramString != null) {
          return new File(localFile, paramString);
        }
        return localFile;
      }
    }, paramLong);
  }
}
