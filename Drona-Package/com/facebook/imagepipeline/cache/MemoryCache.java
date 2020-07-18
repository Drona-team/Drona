package com.facebook.imagepipeline.cache;

import com.facebook.common.internal.Predicate;
import com.facebook.common.references.CloseableReference;

public abstract interface MemoryCache<K, V>
{
  public abstract CloseableReference cache(Object paramObject);
  
  public abstract CloseableReference cache(Object paramObject, CloseableReference paramCloseableReference);
  
  public abstract boolean contains(Predicate paramPredicate);
  
  public abstract boolean contains(Object paramObject);
  
  public abstract int removeAll(Predicate paramPredicate);
}
