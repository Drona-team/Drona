package com.facebook.imagepipeline.cache;

import com.facebook.cache.common.CacheKey;

public class BitmapMemoryCacheFactory
{
  public BitmapMemoryCacheFactory() {}
  
  public static InstrumentedMemoryCache removeBody(CountingMemoryCache paramCountingMemoryCache, ImageCacheStatsTracker paramImageCacheStatsTracker)
  {
    paramImageCacheStatsTracker.registerBitmapMemoryCache(paramCountingMemoryCache);
    new InstrumentedMemoryCache(paramCountingMemoryCache, new MemoryCacheTracker()
    {
      public void onCacheHit(CacheKey paramAnonymousCacheKey)
      {
        val$imageCacheStatsTracker.onBitmapCacheHit(paramAnonymousCacheKey);
      }
      
      public void onCacheMiss()
      {
        val$imageCacheStatsTracker.onBitmapCacheMiss();
      }
      
      public void onCachePut()
      {
        val$imageCacheStatsTracker.onBitmapCachePut();
      }
    });
  }
}
