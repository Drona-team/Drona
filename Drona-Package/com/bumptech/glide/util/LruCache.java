package com.bumptech.glide.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LruCache<T, Y>
{
  private final Map<T, Y> cache = new LinkedHashMap(100, 0.75F, true);
  private long currentSize;
  private final long initialMaxSize;
  private long maxSize;
  
  public LruCache(long paramLong)
  {
    initialMaxSize = paramLong;
    maxSize = paramLong;
  }
  
  private void evict()
  {
    trimToSize(maxSize);
  }
  
  public void clearMemory()
  {
    trimToSize(0L);
  }
  
  public boolean contains(Object paramObject)
  {
    try
    {
      boolean bool = cache.containsKey(paramObject);
      return bool;
    }
    catch (Throwable paramObject)
    {
      throw paramObject;
    }
  }
  
  protected int getCount()
  {
    try
    {
      int i = cache.size();
      return i;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public long getCurrentSize()
  {
    try
    {
      long l = currentSize;
      return l;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public long getMaxSize()
  {
    try
    {
      long l = maxSize;
      return l;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  protected int getSize(Object paramObject)
  {
    return 1;
  }
  
  protected void onItemEvicted(Object paramObject1, Object paramObject2) {}
  
  public Object put(Object paramObject)
  {
    try
    {
      paramObject = cache.get(paramObject);
      return paramObject;
    }
    catch (Throwable paramObject)
    {
      throw paramObject;
    }
  }
  
  public Object put(Object paramObject1, Object paramObject2)
  {
    try
    {
      long l = getSize(paramObject2);
      if (l >= maxSize)
      {
        onItemEvicted(paramObject1, paramObject2);
        return null;
      }
      if (paramObject2 != null) {
        currentSize += l;
      }
      Object localObject = cache.put(paramObject1, paramObject2);
      if (localObject != null)
      {
        currentSize -= getSize(localObject);
        if (!localObject.equals(paramObject2)) {
          onItemEvicted(paramObject1, localObject);
        }
      }
      evict();
      return localObject;
    }
    catch (Throwable paramObject1)
    {
      throw paramObject1;
    }
  }
  
  public Object remove(Object paramObject)
  {
    try
    {
      paramObject = cache.remove(paramObject);
      if (paramObject != null) {
        currentSize -= getSize(paramObject);
      }
      return paramObject;
    }
    catch (Throwable paramObject)
    {
      throw paramObject;
    }
  }
  
  public void setSizeMultiplier(float paramFloat)
  {
    if (paramFloat >= 0.0F) {}
    try
    {
      maxSize = Math.round((float)initialMaxSize * paramFloat);
      evict();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
    throw new IllegalArgumentException("Multiplier must be >= 0");
  }
  
  protected void trimToSize(long paramLong)
  {
    try
    {
      while (currentSize > paramLong)
      {
        Iterator localIterator = cache.entrySet().iterator();
        Object localObject2 = (Map.Entry)localIterator.next();
        Object localObject1 = ((Map.Entry)localObject2).getValue();
        currentSize -= getSize(localObject1);
        localObject2 = ((Map.Entry)localObject2).getKey();
        localIterator.remove();
        onItemEvicted(localObject2, localObject1);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}
