package com.bumptech.glide.load.model;

import androidx.annotation.VisibleForTesting;
import com.bumptech.glide.util.LruCache;
import com.bumptech.glide.util.Util;
import java.util.Queue;

public class ModelCache<A, B>
{
  private static final int DEFAULT_SIZE = 250;
  private final LruCache<ModelKey<A>, B> cache;
  
  public ModelCache()
  {
    this(250L);
  }
  
  public ModelCache(long paramLong)
  {
    cache = new LruCache(paramLong)
    {
      protected void onItemEvicted(ModelCache.ModelKey paramAnonymousModelKey, Object paramAnonymousObject)
      {
        paramAnonymousModelKey.release();
      }
    };
  }
  
  public void clear()
  {
    cache.clearMemory();
  }
  
  public Object put(Object paramObject, int paramInt1, int paramInt2)
  {
    paramObject = ModelKey.get(paramObject, paramInt1, paramInt2);
    Object localObject = cache.put(paramObject);
    paramObject.release();
    return localObject;
  }
  
  public void put(Object paramObject1, int paramInt1, int paramInt2, Object paramObject2)
  {
    paramObject1 = ModelKey.get(paramObject1, paramInt1, paramInt2);
    cache.put(paramObject1, paramObject2);
  }
  
  @VisibleForTesting
  static final class ModelKey<A>
  {
    private static final Queue<ModelKey<?>> KEY_QUEUE = Util.createQueue(0);
    private int height;
    private A model;
    private int width;
    
    private ModelKey() {}
    
    static ModelKey get(Object paramObject, int paramInt1, int paramInt2)
    {
      Object localObject = KEY_QUEUE;
      try
      {
        ModelKey localModelKey = (ModelKey)KEY_QUEUE.poll();
        localObject = localModelKey;
        if (localModelKey == null) {
          localObject = new ModelKey();
        }
        ((ModelKey)localObject).init(paramObject, paramInt1, paramInt2);
        return localObject;
      }
      catch (Throwable paramObject)
      {
        throw paramObject;
      }
    }
    
    private void init(Object paramObject, int paramInt1, int paramInt2)
    {
      model = paramObject;
      width = paramInt1;
      height = paramInt2;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof ModelKey))
      {
        paramObject = (ModelKey)paramObject;
        if ((width == width) && (height == height) && (model.equals(model))) {
          return true;
        }
      }
      return false;
    }
    
    public int hashCode()
    {
      return (height * 31 + width) * 31 + model.hashCode();
    }
    
    public void release()
    {
      Queue localQueue = KEY_QUEUE;
      try
      {
        KEY_QUEUE.offer(this);
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  }
}
