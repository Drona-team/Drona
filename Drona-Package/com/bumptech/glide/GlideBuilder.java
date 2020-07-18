package com.bumptech.glide;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter;
import com.bumptech.glide.load.engine.bitmap_recycle.LruArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskCache.Factory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator.Builder;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.manager.ConnectivityMonitorFactory;
import com.bumptech.glide.manager.DefaultConnectivityMonitorFactory;
import com.bumptech.glide.manager.RequestManagerRetriever;
import com.bumptech.glide.manager.RequestManagerRetriever.RequestManagerFactory;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class GlideBuilder
{
  private GlideExecutor animationExecutor;
  private ArrayPool arrayPool;
  private BitmapPool bitmapPool;
  private ConnectivityMonitorFactory connectivityMonitorFactory;
  @Nullable
  private List<RequestListener<Object>> defaultRequestListeners;
  private RequestOptions defaultRequestOptions = new RequestOptions();
  private final Map<Class<?>, TransitionOptions<?, ?>> defaultTransitionOptions = new ArrayMap();
  private GlideExecutor diskCacheExecutor;
  private DiskCache.Factory diskCacheFactory;
  private Engine engine;
  private boolean isActiveResourceRetentionAllowed;
  private boolean isLoggingRequestOriginsEnabled;
  private int logLevel = 4;
  private MemoryCache memoryCache;
  private MemorySizeCalculator memorySizeCalculator;
  @Nullable
  private RequestManagerRetriever.RequestManagerFactory requestManagerFactory;
  private GlideExecutor sourceExecutor;
  
  public GlideBuilder() {}
  
  public GlideBuilder addGlobalRequestListener(RequestListener paramRequestListener)
  {
    if (defaultRequestListeners == null) {
      defaultRequestListeners = new ArrayList();
    }
    defaultRequestListeners.add(paramRequestListener);
    return this;
  }
  
  Glide build(Context paramContext)
  {
    if (sourceExecutor == null) {
      sourceExecutor = GlideExecutor.newSourceExecutor();
    }
    if (diskCacheExecutor == null) {
      diskCacheExecutor = GlideExecutor.newDiskCacheExecutor();
    }
    if (animationExecutor == null) {
      animationExecutor = GlideExecutor.newAnimationExecutor();
    }
    if (memorySizeCalculator == null) {
      memorySizeCalculator = new MemorySizeCalculator.Builder(paramContext).build();
    }
    if (connectivityMonitorFactory == null) {
      connectivityMonitorFactory = new DefaultConnectivityMonitorFactory();
    }
    if (bitmapPool == null)
    {
      int i = memorySizeCalculator.getBitmapPoolSize();
      if (i > 0) {
        bitmapPool = new LruBitmapPool(i);
      } else {
        bitmapPool = new BitmapPoolAdapter();
      }
    }
    if (arrayPool == null) {
      arrayPool = new LruArrayPool(memorySizeCalculator.getArrayPoolSizeInBytes());
    }
    if (memoryCache == null) {
      memoryCache = new LruResourceCache(memorySizeCalculator.getMemoryCacheSize());
    }
    if (diskCacheFactory == null) {
      diskCacheFactory = new InternalCacheDiskCacheFactory(paramContext);
    }
    if (engine == null) {
      engine = new Engine(memoryCache, diskCacheFactory, diskCacheExecutor, sourceExecutor, GlideExecutor.newUnlimitedSourceExecutor(), GlideExecutor.newAnimationExecutor(), isActiveResourceRetentionAllowed);
    }
    if (defaultRequestListeners == null) {
      defaultRequestListeners = Collections.emptyList();
    } else {
      defaultRequestListeners = Collections.unmodifiableList(defaultRequestListeners);
    }
    RequestManagerRetriever localRequestManagerRetriever = new RequestManagerRetriever(requestManagerFactory);
    return new Glide(paramContext, engine, memoryCache, bitmapPool, arrayPool, localRequestManagerRetriever, connectivityMonitorFactory, logLevel, (RequestOptions)defaultRequestOptions.lock(), defaultTransitionOptions, defaultRequestListeners, isLoggingRequestOriginsEnabled);
  }
  
  public GlideBuilder setAnimationExecutor(GlideExecutor paramGlideExecutor)
  {
    animationExecutor = paramGlideExecutor;
    return this;
  }
  
  public GlideBuilder setArrayPool(ArrayPool paramArrayPool)
  {
    arrayPool = paramArrayPool;
    return this;
  }
  
  public GlideBuilder setBitmapPool(BitmapPool paramBitmapPool)
  {
    bitmapPool = paramBitmapPool;
    return this;
  }
  
  public GlideBuilder setConnectivityMonitorFactory(ConnectivityMonitorFactory paramConnectivityMonitorFactory)
  {
    connectivityMonitorFactory = paramConnectivityMonitorFactory;
    return this;
  }
  
  public GlideBuilder setDefaultRequestOptions(RequestOptions paramRequestOptions)
  {
    defaultRequestOptions = paramRequestOptions;
    return this;
  }
  
  public GlideBuilder setDefaultTransitionOptions(Class paramClass, TransitionOptions paramTransitionOptions)
  {
    defaultTransitionOptions.put(paramClass, paramTransitionOptions);
    return this;
  }
  
  public GlideBuilder setDiskCache(DiskCache.Factory paramFactory)
  {
    diskCacheFactory = paramFactory;
    return this;
  }
  
  public GlideBuilder setDiskCacheExecutor(GlideExecutor paramGlideExecutor)
  {
    diskCacheExecutor = paramGlideExecutor;
    return this;
  }
  
  GlideBuilder setEngine(Engine paramEngine)
  {
    engine = paramEngine;
    return this;
  }
  
  public GlideBuilder setIsActiveResourceRetentionAllowed(boolean paramBoolean)
  {
    isActiveResourceRetentionAllowed = paramBoolean;
    return this;
  }
  
  public GlideBuilder setLogLevel(int paramInt)
  {
    if ((paramInt >= 2) && (paramInt <= 6))
    {
      logLevel = paramInt;
      return this;
    }
    throw new IllegalArgumentException("Log level must be one of Log.VERBOSE, Log.DEBUG, Log.INFO, Log.WARN, or Log.ERROR");
  }
  
  public GlideBuilder setLogRequestOrigins(boolean paramBoolean)
  {
    isLoggingRequestOriginsEnabled = paramBoolean;
    return this;
  }
  
  public GlideBuilder setMemoryCache(MemoryCache paramMemoryCache)
  {
    memoryCache = paramMemoryCache;
    return this;
  }
  
  public GlideBuilder setMemorySizeCalculator(MemorySizeCalculator.Builder paramBuilder)
  {
    return setMemorySizeCalculator(paramBuilder.build());
  }
  
  public GlideBuilder setMemorySizeCalculator(MemorySizeCalculator paramMemorySizeCalculator)
  {
    memorySizeCalculator = paramMemorySizeCalculator;
    return this;
  }
  
  void setRequestManagerFactory(RequestManagerRetriever.RequestManagerFactory paramRequestManagerFactory)
  {
    requestManagerFactory = paramRequestManagerFactory;
  }
  
  public GlideBuilder setResizeExecutor(GlideExecutor paramGlideExecutor)
  {
    return setSourceExecutor(paramGlideExecutor);
  }
  
  public GlideBuilder setSourceExecutor(GlideExecutor paramGlideExecutor)
  {
    sourceExecutor = paramGlideExecutor;
    return this;
  }
}
