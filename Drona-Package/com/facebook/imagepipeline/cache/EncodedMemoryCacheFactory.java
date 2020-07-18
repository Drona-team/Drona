package com.facebook.imagepipeline.cache;

import com.facebook.cache.common.CacheKey;

public class EncodedMemoryCacheFactory
{
  public EncodedMemoryCacheFactory() {}
  
  public static InstrumentedMemoryCache removeBody(CountingMemoryCache paramCountingMemoryCache, ImageCacheStatsTracker paramImageCacheStatsTracker)
  {
    paramImageCacheStatsTracker.registerEncodedMemoryCache(paramCountingMemoryCache);
    new InstrumentedMemoryCache(paramCountingMemoryCache, new MemoryCacheTracker()
    {
      public void onCacheHit(CacheKey paramAnonymousCacheKey)
      {
        val$imageCacheStatsTracker.onMemoryCacheHit(paramAnonymousCacheKey);
      }
      
      public void onCacheMiss()
      {
        val$imageCacheStatsTracker.onMemoryCacheMiss();
      }
      
      public void onCachePut()
      {
        val$imageCacheStatsTracker.onMemoryCachePut();
      }
    });
  }
}
