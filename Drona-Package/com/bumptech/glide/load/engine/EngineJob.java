package com.bumptech.glide.load.engine;

import androidx.annotation.VisibleForTesting;
import androidx.core.util.Pools.Pool;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.request.ResourceCallback;
import com.bumptech.glide.util.Executors;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.pool.FactoryPools.Poolable;
import com.bumptech.glide.util.pool.StateVerifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

class EngineJob<R>
  implements DecodeJob.Callback<R>, FactoryPools.Poolable
{
  private static final EngineResourceFactory DEFAULT_FACTORY = new EngineResourceFactory();
  private final GlideExecutor animationExecutor;
  DataSource dataSource;
  private DecodeJob<R> decodeJob;
  private final GlideExecutor diskCacheExecutor;
  EngineResource<?> engineResource;
  private final EngineResourceFactory engineResourceFactory;
  GlideException exception;
  private boolean hasLoadFailed;
  private boolean hasResource;
  private boolean isCacheable;
  private volatile boolean isCancelled;
  private Key key;
  private final EngineJobListener listener;
  private boolean onlyRetrieveFromCache;
  private final AtomicInteger pendingCallbacks = new AtomicInteger();
  private final Pools.Pool<EngineJob<?>> pool;
  private Resource<?> resource;
  private final GlideExecutor sourceExecutor;
  private final GlideExecutor sourceUnlimitedExecutor;
  private final StateVerifier stateVerifier = StateVerifier.newInstance();
  final ResourceCallbacksAndExecutors this$0 = new ResourceCallbacksAndExecutors();
  private boolean useAnimationPool;
  private boolean useUnlimitedSourceGeneratorPool;
  
  EngineJob(GlideExecutor paramGlideExecutor1, GlideExecutor paramGlideExecutor2, GlideExecutor paramGlideExecutor3, GlideExecutor paramGlideExecutor4, EngineJobListener paramEngineJobListener, Pools.Pool paramPool)
  {
    this(paramGlideExecutor1, paramGlideExecutor2, paramGlideExecutor3, paramGlideExecutor4, paramEngineJobListener, paramPool, DEFAULT_FACTORY);
  }
  
  EngineJob(GlideExecutor paramGlideExecutor1, GlideExecutor paramGlideExecutor2, GlideExecutor paramGlideExecutor3, GlideExecutor paramGlideExecutor4, EngineJobListener paramEngineJobListener, Pools.Pool paramPool, EngineResourceFactory paramEngineResourceFactory)
  {
    diskCacheExecutor = paramGlideExecutor1;
    sourceExecutor = paramGlideExecutor2;
    sourceUnlimitedExecutor = paramGlideExecutor3;
    animationExecutor = paramGlideExecutor4;
    listener = paramEngineJobListener;
    pool = paramPool;
    engineResourceFactory = paramEngineResourceFactory;
  }
  
  private GlideExecutor getActiveSourceExecutor()
  {
    if (useUnlimitedSourceGeneratorPool) {
      return sourceUnlimitedExecutor;
    }
    if (useAnimationPool) {
      return animationExecutor;
    }
    return sourceExecutor;
  }
  
  private boolean isDone()
  {
    return (hasLoadFailed) || (hasResource) || (isCancelled);
  }
  
  private void release()
  {
    try
    {
      if (key != null)
      {
        this$0.clear();
        key = null;
        engineResource = null;
        resource = null;
        hasLoadFailed = false;
        isCancelled = false;
        hasResource = false;
        decodeJob.release(false);
        decodeJob = null;
        exception = null;
        dataSource = null;
        pool.release(this);
        return;
      }
      throw new IllegalArgumentException();
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  void addCallback(ResourceCallback paramResourceCallback, Executor paramExecutor)
  {
    try
    {
      stateVerifier.throwIfRecycled();
      this$0.addCallback(paramResourceCallback, paramExecutor);
      if (hasResource)
      {
        incrementPendingCallbacks(1);
        paramExecutor.execute(new CallResourceReady(paramResourceCallback));
      }
      else if (hasLoadFailed)
      {
        incrementPendingCallbacks(1);
        paramExecutor.execute(new CallLoadFailed(paramResourceCallback));
      }
      else
      {
        Preconditions.checkArgument(isCancelled ^ true, "Cannot add callbacks to a cancelled EngineJob");
      }
      return;
    }
    catch (Throwable paramResourceCallback)
    {
      throw paramResourceCallback;
    }
  }
  
  /* Error */
  void callCallbackOnLoadFailed(ResourceCallback paramResourceCallback)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_1
    //   3: aload_0
    //   4: getfield 144	com/bumptech/glide/load/engine/EngineJob:exception	Lcom/bumptech/glide/load/engine/GlideException;
    //   7: invokeinterface 191 2 0
    //   12: aload_0
    //   13: monitorexit
    //   14: return
    //   15: astore_1
    //   16: goto +13 -> 29
    //   19: astore_1
    //   20: new 193	com/bumptech/glide/load/engine/CallbackException
    //   23: dup
    //   24: aload_1
    //   25: invokespecial 196	com/bumptech/glide/load/engine/CallbackException:<init>	(Ljava/lang/Throwable;)V
    //   28: athrow
    //   29: aload_0
    //   30: monitorexit
    //   31: aload_1
    //   32: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	33	0	this	EngineJob
    //   0	33	1	paramResourceCallback	ResourceCallback
    // Exception table:
    //   from	to	target	type
    //   20	29	15	java/lang/Throwable
    //   2	12	19	java/lang/Throwable
  }
  
  /* Error */
  void callCallbackOnResourceReady(ResourceCallback paramResourceCallback)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_1
    //   3: aload_0
    //   4: getfield 133	com/bumptech/glide/load/engine/EngineJob:engineResource	Lcom/bumptech/glide/load/engine/EngineResource;
    //   7: aload_0
    //   8: getfield 146	com/bumptech/glide/load/engine/EngineJob:dataSource	Lcom/bumptech/glide/load/DataSource;
    //   11: invokeinterface 201 3 0
    //   16: aload_0
    //   17: monitorexit
    //   18: return
    //   19: astore_1
    //   20: goto +13 -> 33
    //   23: astore_1
    //   24: new 193	com/bumptech/glide/load/engine/CallbackException
    //   27: dup
    //   28: aload_1
    //   29: invokespecial 196	com/bumptech/glide/load/engine/CallbackException:<init>	(Ljava/lang/Throwable;)V
    //   32: athrow
    //   33: aload_0
    //   34: monitorexit
    //   35: aload_1
    //   36: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	37	0	this	EngineJob
    //   0	37	1	paramResourceCallback	ResourceCallback
    // Exception table:
    //   from	to	target	type
    //   24	33	19	java/lang/Throwable
    //   2	16	23	java/lang/Throwable
  }
  
  void cancel()
  {
    if (isDone()) {
      return;
    }
    isCancelled = true;
    decodeJob.cancel();
    listener.onEngineJobCancelled(this, key);
  }
  
  void decrementPendingCallbacks()
  {
    for (;;)
    {
      try
      {
        stateVerifier.throwIfRecycled();
        Preconditions.checkArgument(isDone(), "Not yet complete!");
        int i = pendingCallbacks.decrementAndGet();
        if (i >= 0)
        {
          bool = true;
          Preconditions.checkArgument(bool, "Can't decrement below 0");
          if (i == 0)
          {
            if (engineResource != null) {
              engineResource.release();
            }
            release();
          }
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      boolean bool = false;
    }
  }
  
  public StateVerifier getVerifier()
  {
    return stateVerifier;
  }
  
  void incrementPendingCallbacks(int paramInt)
  {
    try
    {
      Preconditions.checkArgument(isDone(), "Not yet complete!");
      if ((pendingCallbacks.getAndAdd(paramInt) == 0) && (engineResource != null)) {
        engineResource.acquire();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  EngineJob init(Key paramKey, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    try
    {
      key = paramKey;
      isCacheable = paramBoolean1;
      useUnlimitedSourceGeneratorPool = paramBoolean2;
      useAnimationPool = paramBoolean3;
      onlyRetrieveFromCache = paramBoolean4;
      return this;
    }
    catch (Throwable paramKey)
    {
      throw paramKey;
    }
  }
  
  boolean isCancelled()
  {
    try
    {
      boolean bool = isCancelled;
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  void notifyCallbacksOfException()
  {
    try
    {
      stateVerifier.throwIfRecycled();
      if (isCancelled)
      {
        release();
        return;
      }
      if (!this$0.isEmpty())
      {
        if (!hasLoadFailed)
        {
          hasLoadFailed = true;
          Object localObject1 = key;
          Object localObject2 = this$0.copy();
          incrementPendingCallbacks(((ResourceCallbacksAndExecutors)localObject2).size() + 1);
          listener.onEngineJobComplete(this, (Key)localObject1, null);
          localObject1 = ((ResourceCallbacksAndExecutors)localObject2).iterator();
          while (((Iterator)localObject1).hasNext())
          {
            localObject2 = (ResourceCallbackAndExecutor)((Iterator)localObject1).next();
            executor.execute(new CallLoadFailed(task));
          }
          decrementPendingCallbacks();
          return;
        }
        throw new IllegalStateException("Already failed once");
      }
      throw new IllegalStateException("Received an exception without any callbacks to notify");
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  void notifyCallbacksOfResult()
  {
    try
    {
      stateVerifier.throwIfRecycled();
      if (isCancelled)
      {
        resource.recycle();
        release();
        return;
      }
      if (!this$0.isEmpty())
      {
        if (!hasResource)
        {
          engineResource = engineResourceFactory.build(resource, isCacheable);
          hasResource = true;
          Object localObject1 = this$0.copy();
          incrementPendingCallbacks(((ResourceCallbacksAndExecutors)localObject1).size() + 1);
          Object localObject2 = key;
          EngineResource localEngineResource = engineResource;
          listener.onEngineJobComplete(this, (Key)localObject2, localEngineResource);
          localObject1 = ((ResourceCallbacksAndExecutors)localObject1).iterator();
          while (((Iterator)localObject1).hasNext())
          {
            localObject2 = (ResourceCallbackAndExecutor)((Iterator)localObject1).next();
            executor.execute(new CallResourceReady(task));
          }
          decrementPendingCallbacks();
          return;
        }
        throw new IllegalStateException("Already have resource");
      }
      throw new IllegalStateException("Received a resource without any callbacks to notify");
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void onLoadFailed(GlideException paramGlideException)
  {
    try
    {
      exception = paramGlideException;
      notifyCallbacksOfException();
      return;
    }
    catch (Throwable paramGlideException)
    {
      throw paramGlideException;
    }
  }
  
  public void onResourceReady(Resource paramResource, DataSource paramDataSource)
  {
    try
    {
      resource = paramResource;
      dataSource = paramDataSource;
      notifyCallbacksOfResult();
      return;
    }
    catch (Throwable paramResource)
    {
      throw paramResource;
    }
  }
  
  boolean onlyRetrieveFromCache()
  {
    return onlyRetrieveFromCache;
  }
  
  void removeCallback(ResourceCallback paramResourceCallback)
  {
    for (;;)
    {
      try
      {
        stateVerifier.throwIfRecycled();
        this$0.remove(paramResourceCallback);
        if (this$0.isEmpty())
        {
          cancel();
          if (hasResource) {
            break label79;
          }
          if (!hasLoadFailed) {
            break label74;
          }
          break label79;
          if ((i != 0) && (pendingCallbacks.get() == 0)) {
            release();
          }
        }
        return;
      }
      catch (Throwable paramResourceCallback)
      {
        throw paramResourceCallback;
      }
      label74:
      int i = 0;
      continue;
      label79:
      i = 1;
    }
  }
  
  public void reschedule(DecodeJob paramDecodeJob)
  {
    getActiveSourceExecutor().execute(paramDecodeJob);
  }
  
  public void start(DecodeJob paramDecodeJob)
  {
    try
    {
      decodeJob = paramDecodeJob;
      GlideExecutor localGlideExecutor;
      if (paramDecodeJob.willDecodeFromCache()) {
        localGlideExecutor = diskCacheExecutor;
      } else {
        localGlideExecutor = getActiveSourceExecutor();
      }
      localGlideExecutor.execute(paramDecodeJob);
      return;
    }
    catch (Throwable paramDecodeJob)
    {
      throw paramDecodeJob;
    }
  }
  
  private class CallLoadFailed
    implements Runnable
  {
    private final ResourceCallback val$object;
    
    CallLoadFailed(ResourceCallback paramResourceCallback)
    {
      val$object = paramResourceCallback;
    }
    
    public void run()
    {
      EngineJob localEngineJob = EngineJob.this;
      try
      {
        if (this$0.contains(val$object)) {
          callCallbackOnLoadFailed(val$object);
        }
        decrementPendingCallbacks();
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  }
  
  private class CallResourceReady
    implements Runnable
  {
    private final ResourceCallback val$object;
    
    CallResourceReady(ResourceCallback paramResourceCallback)
    {
      val$object = paramResourceCallback;
    }
    
    public void run()
    {
      EngineJob localEngineJob = EngineJob.this;
      try
      {
        if (this$0.contains(val$object))
        {
          engineResource.acquire();
          callCallbackOnResourceReady(val$object);
          removeCallback(val$object);
        }
        decrementPendingCallbacks();
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  }
  
  @VisibleForTesting
  static class EngineResourceFactory
  {
    EngineResourceFactory() {}
    
    public EngineResource build(Resource paramResource, boolean paramBoolean)
    {
      return new EngineResource(paramResource, paramBoolean, true);
    }
  }
  
  static final class ResourceCallbackAndExecutor
  {
    final Executor executor;
    final ResourceCallback task;
    
    ResourceCallbackAndExecutor(ResourceCallback paramResourceCallback, Executor paramExecutor)
    {
      task = paramResourceCallback;
      executor = paramExecutor;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof ResourceCallbackAndExecutor))
      {
        paramObject = (ResourceCallbackAndExecutor)paramObject;
        return task.equals(task);
      }
      return false;
    }
    
    public int hashCode()
    {
      return task.hashCode();
    }
  }
  
  static final class ResourceCallbacksAndExecutors
    implements Iterable<EngineJob.ResourceCallbackAndExecutor>
  {
    private final List<EngineJob.ResourceCallbackAndExecutor> callbacksAndExecutors;
    
    ResourceCallbacksAndExecutors()
    {
      this(new ArrayList(2));
    }
    
    ResourceCallbacksAndExecutors(List paramList)
    {
      callbacksAndExecutors = paramList;
    }
    
    private static EngineJob.ResourceCallbackAndExecutor defaultCallbackAndExecutor(ResourceCallback paramResourceCallback)
    {
      return new EngineJob.ResourceCallbackAndExecutor(paramResourceCallback, Executors.directExecutor());
    }
    
    void addCallback(ResourceCallback paramResourceCallback, Executor paramExecutor)
    {
      callbacksAndExecutors.add(new EngineJob.ResourceCallbackAndExecutor(paramResourceCallback, paramExecutor));
    }
    
    void clear()
    {
      callbacksAndExecutors.clear();
    }
    
    boolean contains(ResourceCallback paramResourceCallback)
    {
      return callbacksAndExecutors.contains(defaultCallbackAndExecutor(paramResourceCallback));
    }
    
    ResourceCallbacksAndExecutors copy()
    {
      return new ResourceCallbacksAndExecutors(new ArrayList(callbacksAndExecutors));
    }
    
    boolean isEmpty()
    {
      return callbacksAndExecutors.isEmpty();
    }
    
    public Iterator iterator()
    {
      return callbacksAndExecutors.iterator();
    }
    
    void remove(ResourceCallback paramResourceCallback)
    {
      callbacksAndExecutors.remove(defaultCallbackAndExecutor(paramResourceCallback));
    }
    
    int size()
    {
      return callbacksAndExecutors.size();
    }
  }
}
