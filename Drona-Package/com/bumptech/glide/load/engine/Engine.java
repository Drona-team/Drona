package com.bumptech.glide.load.engine;

import android.util.Log;
import androidx.annotation.VisibleForTesting;
import androidx.core.util.Pools.Pool;
import com.bumptech.glide.GlideContext;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskCache.Factory;
import com.bumptech.glide.load.engine.cache.DiskCacheAdapter;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.engine.cache.MemoryCache.ResourceRemovedListener;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.request.ResourceCallback;
import com.bumptech.glide.util.Executors;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.pool.FactoryPools;
import com.bumptech.glide.util.pool.FactoryPools.Factory;
import java.util.Map;
import java.util.concurrent.Executor;

public class Engine
  implements EngineJobListener, MemoryCache.ResourceRemovedListener, EngineResource.ResourceListener
{
  private static final int JOB_POOL_SIZE = 150;
  private static final String TAG = "Engine";
  private static final boolean VERBOSE_IS_LOGGABLE = Log.isLoggable("Engine", 2);
  private final ActiveResources activeResources;
  private final MemoryCache cache;
  private final DecodeJobFactory decodeJobFactory;
  private final LazyDiskCacheProvider diskCacheProvider;
  private final EngineJobFactory engineJobFactory;
  private final Jobs jobs;
  private final EngineKeyFactory keyFactory;
  private final ResourceRecycler resourceRecycler;
  
  Engine(MemoryCache paramMemoryCache, DiskCache.Factory paramFactory, GlideExecutor paramGlideExecutor1, GlideExecutor paramGlideExecutor2, GlideExecutor paramGlideExecutor3, GlideExecutor paramGlideExecutor4, Jobs paramJobs, EngineKeyFactory paramEngineKeyFactory, ActiveResources paramActiveResources, EngineJobFactory paramEngineJobFactory, DecodeJobFactory paramDecodeJobFactory, ResourceRecycler paramResourceRecycler, boolean paramBoolean)
  {
    cache = paramMemoryCache;
    diskCacheProvider = new LazyDiskCacheProvider(paramFactory);
    paramFactory = paramActiveResources;
    if (paramActiveResources == null) {
      paramFactory = new ActiveResources(paramBoolean);
    }
    activeResources = paramFactory;
    paramFactory.setListener(this);
    paramFactory = paramEngineKeyFactory;
    if (paramEngineKeyFactory == null) {
      paramFactory = new EngineKeyFactory();
    }
    keyFactory = paramFactory;
    paramFactory = paramJobs;
    if (paramJobs == null) {
      paramFactory = new Jobs();
    }
    jobs = paramFactory;
    paramFactory = paramEngineJobFactory;
    if (paramEngineJobFactory == null) {
      paramFactory = new EngineJobFactory(paramGlideExecutor1, paramGlideExecutor2, paramGlideExecutor3, paramGlideExecutor4, this);
    }
    engineJobFactory = paramFactory;
    paramFactory = paramDecodeJobFactory;
    if (paramDecodeJobFactory == null) {
      paramFactory = new DecodeJobFactory(diskCacheProvider);
    }
    decodeJobFactory = paramFactory;
    paramFactory = paramResourceRecycler;
    if (paramResourceRecycler == null) {
      paramFactory = new ResourceRecycler();
    }
    resourceRecycler = paramFactory;
    paramMemoryCache.setResourceRemovedListener(this);
  }
  
  public Engine(MemoryCache paramMemoryCache, DiskCache.Factory paramFactory, GlideExecutor paramGlideExecutor1, GlideExecutor paramGlideExecutor2, GlideExecutor paramGlideExecutor3, GlideExecutor paramGlideExecutor4, boolean paramBoolean)
  {
    this(paramMemoryCache, paramFactory, paramGlideExecutor1, paramGlideExecutor2, paramGlideExecutor3, paramGlideExecutor4, null, null, null, null, null, null, paramBoolean);
  }
  
  private EngineResource getEngineResourceFromCache(Key paramKey)
  {
    paramKey = cache.remove(paramKey);
    if (paramKey == null) {
      return null;
    }
    if ((paramKey instanceof EngineResource)) {
      return (EngineResource)paramKey;
    }
    return new EngineResource(paramKey, true, true);
  }
  
  private EngineResource loadFromActiveResources(Key paramKey, boolean paramBoolean)
  {
    if (!paramBoolean) {
      return null;
    }
    paramKey = activeResources.loadFromCache(paramKey);
    if (paramKey != null) {
      paramKey.acquire();
    }
    return paramKey;
  }
  
  private EngineResource loadFromCache(Key paramKey, boolean paramBoolean)
  {
    if (!paramBoolean) {
      return null;
    }
    EngineResource localEngineResource = getEngineResourceFromCache(paramKey);
    if (localEngineResource != null)
    {
      localEngineResource.acquire();
      activeResources.activate(paramKey, localEngineResource);
    }
    return localEngineResource;
  }
  
  private static void logWithTimeAndKey(String paramString, long paramLong, Key paramKey)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append(" in ");
    localStringBuilder.append(LogTime.getElapsedMillis(paramLong));
    localStringBuilder.append("ms, key: ");
    localStringBuilder.append(paramKey);
    Log.v("Engine", localStringBuilder.toString());
  }
  
  public void clearDiskCache()
  {
    diskCacheProvider.getDiskCache().clear();
  }
  
  public LoadStatus load(GlideContext paramGlideContext, Object paramObject, Key paramKey, int paramInt1, int paramInt2, Class paramClass1, Class paramClass2, Priority paramPriority, DiskCacheStrategy paramDiskCacheStrategy, Map paramMap, boolean paramBoolean1, boolean paramBoolean2, Options paramOptions, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6, ResourceCallback paramResourceCallback, Executor paramExecutor)
  {
    for (;;)
    {
      try
      {
        if (VERBOSE_IS_LOGGABLE)
        {
          l = LogTime.getLogTime();
          EngineKey localEngineKey = keyFactory.buildKey(paramObject, paramKey, paramInt1, paramInt2, paramMap, paramClass1, paramClass2, paramOptions);
          Object localObject = loadFromActiveResources(localEngineKey, paramBoolean3);
          if (localObject != null)
          {
            paramResourceCallback.onResourceReady((Resource)localObject, DataSource.MEMORY_CACHE);
            if (VERBOSE_IS_LOGGABLE) {
              logWithTimeAndKey("Loaded resource from active resources", l, localEngineKey);
            }
            return null;
          }
          localObject = loadFromCache(localEngineKey, paramBoolean3);
          if (localObject != null)
          {
            paramResourceCallback.onResourceReady((Resource)localObject, DataSource.MEMORY_CACHE);
            if (VERBOSE_IS_LOGGABLE) {
              logWithTimeAndKey("Loaded resource from cache", l, localEngineKey);
            }
            return null;
          }
          localObject = jobs.build(localEngineKey, paramBoolean6);
          if (localObject != null)
          {
            ((EngineJob)localObject).addCallback(paramResourceCallback, paramExecutor);
            if (VERBOSE_IS_LOGGABLE) {
              logWithTimeAndKey("Added to existing load", l, localEngineKey);
            }
            paramGlideContext = new LoadStatus(paramResourceCallback, (EngineJob)localObject);
            return paramGlideContext;
          }
          localObject = engineJobFactory.build(localEngineKey, paramBoolean3, paramBoolean4, paramBoolean5, paramBoolean6);
          paramGlideContext = decodeJobFactory.build(paramGlideContext, paramObject, localEngineKey, paramKey, paramInt1, paramInt2, paramClass1, paramClass2, paramPriority, paramDiskCacheStrategy, paramMap, paramBoolean1, paramBoolean2, paramBoolean6, paramOptions, (DecodeJob.Callback)localObject);
          jobs.put(localEngineKey, (EngineJob)localObject);
          ((EngineJob)localObject).addCallback(paramResourceCallback, paramExecutor);
          ((EngineJob)localObject).start(paramGlideContext);
          if (VERBOSE_IS_LOGGABLE) {
            logWithTimeAndKey("Started new load", l, localEngineKey);
          }
          paramGlideContext = new LoadStatus(paramResourceCallback, (EngineJob)localObject);
          return paramGlideContext;
        }
      }
      catch (Throwable paramGlideContext)
      {
        throw paramGlideContext;
      }
      long l = 0L;
    }
  }
  
  public void onEngineJobCancelled(EngineJob paramEngineJob, Key paramKey)
  {
    try
    {
      jobs.removeIfCurrent(paramKey, paramEngineJob);
      return;
    }
    catch (Throwable paramEngineJob)
    {
      throw paramEngineJob;
    }
  }
  
  public void onEngineJobComplete(EngineJob paramEngineJob, Key paramKey, EngineResource paramEngineResource)
  {
    if (paramEngineResource != null) {}
    try
    {
      paramEngineResource.setResourceListener(paramKey, this);
      if (paramEngineResource.isCacheable()) {
        activeResources.activate(paramKey, paramEngineResource);
      }
      jobs.removeIfCurrent(paramKey, paramEngineJob);
      return;
    }
    catch (Throwable paramEngineJob)
    {
      for (;;) {}
    }
    throw paramEngineJob;
  }
  
  public void onResourceReleased(Key paramKey, EngineResource paramEngineResource)
  {
    try
    {
      activeResources.deactivate(paramKey);
      if (paramEngineResource.isCacheable()) {
        cache.put(paramKey, paramEngineResource);
      } else {
        resourceRecycler.recycle(paramEngineResource);
      }
      return;
    }
    catch (Throwable paramKey)
    {
      throw paramKey;
    }
  }
  
  public void onResourceRemoved(Resource paramResource)
  {
    resourceRecycler.recycle(paramResource);
  }
  
  public void release(Resource paramResource)
  {
    if ((paramResource instanceof EngineResource))
    {
      ((EngineResource)paramResource).release();
      return;
    }
    throw new IllegalArgumentException("Cannot release anything but an EngineResource");
  }
  
  public void shutdown()
  {
    engineJobFactory.shutdown();
    diskCacheProvider.clearDiskCacheIfCreated();
    activeResources.shutdown();
  }
  
  @VisibleForTesting
  static class DecodeJobFactory
  {
    private int creationOrder;
    final DecodeJob.DiskCacheProvider diskCacheProvider;
    final Pools.Pool<DecodeJob<?>> pool = FactoryPools.threadSafe(150, new FactoryPools.Factory()
    {
      public DecodeJob create()
      {
        return new DecodeJob(diskCacheProvider, pool);
      }
    });
    
    DecodeJobFactory(DecodeJob.DiskCacheProvider paramDiskCacheProvider)
    {
      diskCacheProvider = paramDiskCacheProvider;
    }
    
    DecodeJob build(GlideContext paramGlideContext, Object paramObject, EngineKey paramEngineKey, Key paramKey, int paramInt1, int paramInt2, Class paramClass1, Class paramClass2, Priority paramPriority, DiskCacheStrategy paramDiskCacheStrategy, Map paramMap, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Options paramOptions, DecodeJob.Callback paramCallback)
    {
      DecodeJob localDecodeJob = (DecodeJob)Preconditions.checkNotNull((DecodeJob)pool.acquire());
      int i = creationOrder;
      creationOrder = (i + 1);
      return localDecodeJob.init(paramGlideContext, paramObject, paramEngineKey, paramKey, paramInt1, paramInt2, paramClass1, paramClass2, paramPriority, paramDiskCacheStrategy, paramMap, paramBoolean1, paramBoolean2, paramBoolean3, paramOptions, paramCallback, i);
    }
  }
  
  @VisibleForTesting
  static class EngineJobFactory
  {
    final GlideExecutor animationExecutor;
    final GlideExecutor diskCacheExecutor;
    final EngineJobListener listener;
    final Pools.Pool<EngineJob<?>> pool = FactoryPools.threadSafe(150, new FactoryPools.Factory()
    {
      public EngineJob create()
      {
        return new EngineJob(diskCacheExecutor, sourceExecutor, sourceUnlimitedExecutor, animationExecutor, listener, pool);
      }
    });
    final GlideExecutor sourceExecutor;
    final GlideExecutor sourceUnlimitedExecutor;
    
    EngineJobFactory(GlideExecutor paramGlideExecutor1, GlideExecutor paramGlideExecutor2, GlideExecutor paramGlideExecutor3, GlideExecutor paramGlideExecutor4, EngineJobListener paramEngineJobListener)
    {
      diskCacheExecutor = paramGlideExecutor1;
      sourceExecutor = paramGlideExecutor2;
      sourceUnlimitedExecutor = paramGlideExecutor3;
      animationExecutor = paramGlideExecutor4;
      listener = paramEngineJobListener;
    }
    
    EngineJob build(Key paramKey, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
    {
      return ((EngineJob)Preconditions.checkNotNull((EngineJob)pool.acquire())).init(paramKey, paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4);
    }
    
    void shutdown()
    {
      Executors.shutdownAndAwaitTermination(diskCacheExecutor);
      Executors.shutdownAndAwaitTermination(sourceExecutor);
      Executors.shutdownAndAwaitTermination(sourceUnlimitedExecutor);
      Executors.shutdownAndAwaitTermination(animationExecutor);
    }
  }
  
  private static class LazyDiskCacheProvider
    implements DecodeJob.DiskCacheProvider
  {
    private volatile DiskCache diskCache;
    private final DiskCache.Factory factory;
    
    LazyDiskCacheProvider(DiskCache.Factory paramFactory)
    {
      factory = paramFactory;
    }
    
    void clearDiskCacheIfCreated()
    {
      try
      {
        DiskCache localDiskCache = diskCache;
        if (localDiskCache == null) {
          return;
        }
        diskCache.clear();
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    public DiskCache getDiskCache()
    {
      if (diskCache == null) {
        try
        {
          if (diskCache == null) {
            diskCache = factory.build();
          }
          if (diskCache == null) {
            diskCache = new DiskCacheAdapter();
          }
        }
        catch (Throwable localThrowable)
        {
          throw localThrowable;
        }
      }
      return diskCache;
    }
  }
  
  public class LoadStatus
  {
    private final ResourceCallback cb;
    private final EngineJob<?> engineJob;
    
    LoadStatus(ResourceCallback paramResourceCallback, EngineJob paramEngineJob)
    {
      cb = paramResourceCallback;
      engineJob = paramEngineJob;
    }
    
    public void cancel()
    {
      Engine localEngine = Engine.this;
      try
      {
        engineJob.removeCallback(cb);
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  }
}
