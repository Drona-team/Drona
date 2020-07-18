package com.bumptech.glide.load.engine.cache;

import android.content.Context;
import java.io.File;

@Deprecated
public final class ExternalCacheDiskCacheFactory
  extends DiskLruCacheFactory
{
  public ExternalCacheDiskCacheFactory(Context paramContext)
  {
    this(paramContext, "image_manager_disk_cache", 262144000);
  }
  
  public ExternalCacheDiskCacheFactory(Context paramContext, int paramInt)
  {
    this(paramContext, "image_manager_disk_cache", paramInt);
  }
  
  public ExternalCacheDiskCacheFactory(Context paramContext, final String paramString, int paramInt)
  {
    super(new DiskLruCacheFactory.CacheDirectoryGetter()
    {
      public File getCacheDirectory()
      {
        File localFile = getExternalCacheDir();
        if (localFile == null) {
          return null;
        }
        if (paramString != null) {
          return new File(localFile, paramString);
        }
        return localFile;
      }
    }, paramInt);
  }
}
