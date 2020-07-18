package com.bumptech.glide.util.pool;

import android.util.Log;
import androidx.core.util.Pools.Pool;
import androidx.core.util.Pools.SimplePool;
import androidx.core.util.Pools.SynchronizedPool;
import java.util.ArrayList;
import java.util.List;

public final class FactoryPools
{
  private static final int DEFAULT_POOL_SIZE = 20;
  private static final Resetter<Object> EMPTY_RESETTER = new Resetter()
  {
    public void reset(Object paramAnonymousObject) {}
  };
  private static final String PAGE_KEY = "FactoryPools";
  
  private FactoryPools() {}
  
  private static Pools.Pool build(Pools.Pool paramPool, Factory paramFactory)
  {
    return build(paramPool, paramFactory, emptyResetter());
  }
  
  private static Pools.Pool build(Pools.Pool paramPool, Factory paramFactory, Resetter paramResetter)
  {
    return new FactoryPool(paramPool, paramFactory, paramResetter);
  }
  
  private static Resetter emptyResetter()
  {
    return EMPTY_RESETTER;
  }
  
  public static Pools.Pool simple(int paramInt, Factory paramFactory)
  {
    return build(new Pools.SimplePool(paramInt), paramFactory);
  }
  
  public static Pools.Pool threadSafe(int paramInt, Factory paramFactory)
  {
    return build(new Pools.SynchronizedPool(paramInt), paramFactory);
  }
  
  public static Pools.Pool threadSafeList()
  {
    return threadSafeList(20);
  }
  
  public static Pools.Pool threadSafeList(int paramInt)
  {
    build(new Pools.SynchronizedPool(paramInt), new Factory()new Resetter
    {
      public List create()
      {
        return new ArrayList();
      }
    }, new Resetter()
    {
      public void reset(List paramAnonymousList)
      {
        paramAnonymousList.clear();
      }
    });
  }
  
  public static abstract interface Factory<T>
  {
    public abstract Object create();
  }
  
  private static final class FactoryPool<T>
    implements Pools.Pool<T>
  {
    private final FactoryPools.Factory<T> factory;
    private final Pools.Pool<T> pool;
    private final FactoryPools.Resetter<T> resetter;
    
    FactoryPool(Pools.Pool paramPool, FactoryPools.Factory paramFactory, FactoryPools.Resetter paramResetter)
    {
      pool = paramPool;
      factory = paramFactory;
      resetter = paramResetter;
    }
    
    public Object acquire()
    {
      Object localObject1 = pool.acquire();
      Object localObject2 = localObject1;
      if (localObject1 == null)
      {
        Object localObject3 = factory.create();
        localObject1 = localObject3;
        localObject2 = localObject1;
        if (Log.isLoggable("FactoryPools", 2))
        {
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append("Created new ");
          ((StringBuilder)localObject2).append(localObject3.getClass());
          Log.v("FactoryPools", ((StringBuilder)localObject2).toString());
          localObject2 = localObject1;
        }
      }
      if ((localObject2 instanceof FactoryPools.Poolable)) {
        ((FactoryPools.Poolable)localObject2).getVerifier().setRecycled(false);
      }
      return localObject2;
    }
    
    public boolean release(Object paramObject)
    {
      if ((paramObject instanceof FactoryPools.Poolable)) {
        ((FactoryPools.Poolable)paramObject).getVerifier().setRecycled(true);
      }
      resetter.reset(paramObject);
      return pool.release(paramObject);
    }
  }
  
  public static abstract interface Poolable
  {
    public abstract StateVerifier getVerifier();
  }
  
  public static abstract interface Resetter<T>
  {
    public abstract void reset(Object paramObject);
  }
}
