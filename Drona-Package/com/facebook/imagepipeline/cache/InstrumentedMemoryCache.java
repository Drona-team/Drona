package com.facebook.imagepipeline.cache;

import com.facebook.common.internal.Predicate;
import com.facebook.common.references.CloseableReference;

public class InstrumentedMemoryCache<K, V>
  implements MemoryCache<K, V>
{
  private final MemoryCache<K, V> mDelegate;
  private final MemoryCacheTracker mTracker;
  
  public InstrumentedMemoryCache(MemoryCache paramMemoryCache, MemoryCacheTracker paramMemoryCacheTracker)
  {
    mDelegate = paramMemoryCache;
    mTracker = paramMemoryCacheTracker;
  }
  
  public CloseableReference cache(Object paramObject)
  {
    CloseableReference localCloseableReference = mDelegate.cache(paramObject);
    if (localCloseableReference == null)
    {
      mTracker.onCacheMiss();
      return localCloseableReference;
    }
    mTracker.onCacheHit(paramObject);
    return localCloseableReference;
  }
  
  public CloseableReference cache(Object paramObject, CloseableReference paramCloseableReference)
  {
    mTracker.onCachePut();
    return mDelegate.cache(paramObject, paramCloseableReference);
  }
  
  public boolean contains(Predicate paramPredicate)
  {
    return mDelegate.contains(paramPredicate);
  }
  
  public boolean contains(Object paramObject)
  {
    return mDelegate.contains(paramObject);
  }
  
  public int removeAll(Predicate paramPredicate)
  {
    return mDelegate.removeAll(paramPredicate);
  }
}
