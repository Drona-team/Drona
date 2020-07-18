package com.bumptech.glide.load.engine.cache;

import android.content.Context;
import java.io.File;

public final class ExternalPreferredCacheDiskCacheFactory
  extends DiskLruCacheFactory
{
  public ExternalPreferredCacheDiskCacheFactory(Context paramContext)
  {
    this(paramContext, "image_manager_disk_cache", 262144000L);
  }
  
  public ExternalPreferredCacheDiskCacheFactory(Context paramContext, long paramLong)
  {
    this(paramContext, "image_manager_disk_cache", paramLong);
  }
  
  public ExternalPreferredCacheDiskCacheFactory(Context paramContext, final String paramString, long paramLong)
  {
    super(new DiskLruCacheFactory.CacheDirectoryGetter()
    {
      private File getInternalCacheDirectory()
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
      
      public File getCacheDirectory()
      {
        File localFile1 = getInternalCacheDirectory();
        if ((localFile1 != null) && (localFile1.exists())) {
          return localFile1;
        }
        File localFile2 = getExternalCacheDir();
        if (localFile2 != null)
        {
          if (!localFile2.canWrite()) {
            return localFile1;
          }
          if (paramString != null) {
            return new File(localFile2, paramString);
          }
          return localFile2;
        }
        return localFile1;
      }
    }, paramLong);
  }
}
