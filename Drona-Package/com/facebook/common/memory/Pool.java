package com.facebook.common.memory;

import com.facebook.common.references.ResourceReleaser;

public abstract interface Pool<V>
  extends ResourceReleaser<V>, MemoryTrimmable
{
  public abstract Object get(int paramInt);
  
  public abstract void release(Object paramObject);
}
