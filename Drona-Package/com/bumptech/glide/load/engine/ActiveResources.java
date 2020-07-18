package com.bumptech.glide.load.engine;

import android.os.Process;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.Preconditions;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

final class ActiveResources
{
  @VisibleForTesting
  final Map<Key, ResourceWeakReference> activeEngineResources = new HashMap();
  private final boolean isActiveResourceRetentionAllowed;
  private volatile boolean isShutdown;
  private EngineResource.ResourceListener listener;
  private final Executor monitorClearedResourcesExecutor;
  private final ReferenceQueue<EngineResource<?>> resourceReferenceQueue = new ReferenceQueue();
  @Nullable
  private volatile DequeuedResourceCallback srv;
  
  ActiveResources(boolean paramBoolean)
  {
    this(paramBoolean, java.util.concurrent.Executors.newSingleThreadExecutor(new ThreadFactory()
    {
      public Thread newThread(final Runnable paramAnonymousRunnable)
      {
        new Thread(new Runnable()
        {
          public void run()
          {
            Process.setThreadPriority(10);
            paramAnonymousRunnable.run();
          }
        }, "glide-active-resources");
      }
    }));
  }
  
  ActiveResources(boolean paramBoolean, Executor paramExecutor)
  {
    isActiveResourceRetentionAllowed = paramBoolean;
    monitorClearedResourcesExecutor = paramExecutor;
    paramExecutor.execute(new Runnable()
    {
      public void run()
      {
        cleanReferenceQueue();
      }
    });
  }
  
  void activate(Key paramKey, EngineResource paramEngineResource)
  {
    try
    {
      paramEngineResource = new ResourceWeakReference(paramKey, paramEngineResource, resourceReferenceQueue, isActiveResourceRetentionAllowed);
      paramKey = (ResourceWeakReference)activeEngineResources.put(paramKey, paramEngineResource);
      if (paramKey != null) {
        paramKey.reset();
      }
      return;
    }
    catch (Throwable paramKey)
    {
      throw paramKey;
    }
  }
  
  void cleanReferenceQueue()
  {
    while (!isShutdown)
    {
      Object localObject = resourceReferenceQueue;
      try
      {
        localObject = ((ReferenceQueue)localObject).remove();
        localObject = (ResourceWeakReference)localObject;
        cleanupActiveReference((ResourceWeakReference)localObject);
        localObject = srv;
        if (localObject == null) {
          continue;
        }
        ((DequeuedResourceCallback)localObject).onResourceDequeued();
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;) {}
      }
      Thread.currentThread().interrupt();
    }
  }
  
  /* Error */
  void cleanupActiveReference(ResourceWeakReference paramResourceWeakReference)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 117	com/bumptech/glide/load/engine/ActiveResources:listener	Lcom/bumptech/glide/load/engine/EngineResource$ResourceListener;
    //   4: astore_2
    //   5: aload_2
    //   6: monitorenter
    //   7: aload_0
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 54	com/bumptech/glide/load/engine/ActiveResources:activeEngineResources	Ljava/util/Map;
    //   13: aload_1
    //   14: getfield 121	com/bumptech/glide/load/engine/ActiveResources$ResourceWeakReference:id	Lcom/bumptech/glide/load/Key;
    //   17: invokeinterface 124 2 0
    //   22: pop
    //   23: aload_1
    //   24: getfield 127	com/bumptech/glide/load/engine/ActiveResources$ResourceWeakReference:isCacheable	Z
    //   27: ifeq +58 -> 85
    //   30: aload_1
    //   31: getfield 131	com/bumptech/glide/load/engine/ActiveResources$ResourceWeakReference:resource	Lcom/bumptech/glide/load/engine/Resource;
    //   34: ifnonnull +6 -> 40
    //   37: goto +48 -> 85
    //   40: new 133	com/bumptech/glide/load/engine/EngineResource
    //   43: dup
    //   44: aload_1
    //   45: getfield 131	com/bumptech/glide/load/engine/ActiveResources$ResourceWeakReference:resource	Lcom/bumptech/glide/load/engine/Resource;
    //   48: iconst_1
    //   49: iconst_0
    //   50: invokespecial 136	com/bumptech/glide/load/engine/EngineResource:<init>	(Lcom/bumptech/glide/load/engine/Resource;ZZ)V
    //   53: astore_3
    //   54: aload_3
    //   55: aload_1
    //   56: getfield 121	com/bumptech/glide/load/engine/ActiveResources$ResourceWeakReference:id	Lcom/bumptech/glide/load/Key;
    //   59: aload_0
    //   60: getfield 117	com/bumptech/glide/load/engine/ActiveResources:listener	Lcom/bumptech/glide/load/engine/EngineResource$ResourceListener;
    //   63: invokevirtual 140	com/bumptech/glide/load/engine/EngineResource:setResourceListener	(Lcom/bumptech/glide/load/Key;Lcom/bumptech/glide/load/engine/EngineResource$ResourceListener;)V
    //   66: aload_0
    //   67: getfield 117	com/bumptech/glide/load/engine/ActiveResources:listener	Lcom/bumptech/glide/load/engine/EngineResource$ResourceListener;
    //   70: aload_1
    //   71: getfield 121	com/bumptech/glide/load/engine/ActiveResources$ResourceWeakReference:id	Lcom/bumptech/glide/load/Key;
    //   74: aload_3
    //   75: invokeinterface 145 3 0
    //   80: aload_0
    //   81: monitorexit
    //   82: aload_2
    //   83: monitorexit
    //   84: return
    //   85: aload_0
    //   86: monitorexit
    //   87: aload_2
    //   88: monitorexit
    //   89: return
    //   90: astore_1
    //   91: aload_0
    //   92: monitorexit
    //   93: aload_1
    //   94: athrow
    //   95: astore_1
    //   96: aload_2
    //   97: monitorexit
    //   98: aload_1
    //   99: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	100	0	this	ActiveResources
    //   0	100	1	paramResourceWeakReference	ResourceWeakReference
    //   4	93	2	localResourceListener	EngineResource.ResourceListener
    //   53	22	3	localEngineResource	EngineResource
    // Exception table:
    //   from	to	target	type
    //   9	37	90	java/lang/Throwable
    //   40	82	90	java/lang/Throwable
    //   85	87	90	java/lang/Throwable
    //   91	93	90	java/lang/Throwable
    //   7	9	95	java/lang/Throwable
    //   82	84	95	java/lang/Throwable
    //   87	89	95	java/lang/Throwable
    //   93	95	95	java/lang/Throwable
    //   96	98	95	java/lang/Throwable
  }
  
  void deactivate(Key paramKey)
  {
    try
    {
      paramKey = (ResourceWeakReference)activeEngineResources.remove(paramKey);
      if (paramKey != null) {
        paramKey.reset();
      }
      return;
    }
    catch (Throwable paramKey)
    {
      throw paramKey;
    }
  }
  
  EngineResource loadFromCache(Key paramKey)
  {
    try
    {
      paramKey = (ResourceWeakReference)activeEngineResources.get(paramKey);
      if (paramKey == null) {
        return null;
      }
      EngineResource localEngineResource = (EngineResource)paramKey.get();
      if (localEngineResource == null) {
        cleanupActiveReference(paramKey);
      }
      return localEngineResource;
    }
    catch (Throwable paramKey)
    {
      throw paramKey;
    }
  }
  
  void setDequeuedResourceCallback(DequeuedResourceCallback paramDequeuedResourceCallback)
  {
    srv = paramDequeuedResourceCallback;
  }
  
  /* Error */
  void setListener(EngineResource.ResourceListener paramResourceListener)
  {
    // Byte code:
    //   0: aload_1
    //   1: monitorenter
    //   2: aload_0
    //   3: monitorenter
    //   4: aload_0
    //   5: aload_1
    //   6: putfield 117	com/bumptech/glide/load/engine/ActiveResources:listener	Lcom/bumptech/glide/load/engine/EngineResource$ResourceListener;
    //   9: aload_0
    //   10: monitorexit
    //   11: aload_1
    //   12: monitorexit
    //   13: return
    //   14: astore_2
    //   15: aload_0
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    //   19: astore_2
    //   20: aload_1
    //   21: monitorexit
    //   22: aload_2
    //   23: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	24	0	this	ActiveResources
    //   0	24	1	paramResourceListener	EngineResource.ResourceListener
    //   14	4	2	localThrowable1	Throwable
    //   19	4	2	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   4	11	14	java/lang/Throwable
    //   15	17	14	java/lang/Throwable
    //   2	4	19	java/lang/Throwable
    //   11	13	19	java/lang/Throwable
    //   17	19	19	java/lang/Throwable
    //   20	22	19	java/lang/Throwable
  }
  
  void shutdown()
  {
    isShutdown = true;
    if ((monitorClearedResourcesExecutor instanceof ExecutorService)) {
      com.bumptech.glide.util.Executors.shutdownAndAwaitTermination((ExecutorService)monitorClearedResourcesExecutor);
    }
  }
  
  @VisibleForTesting
  static abstract interface DequeuedResourceCallback
  {
    public abstract void onResourceDequeued();
  }
  
  @VisibleForTesting
  static final class ResourceWeakReference
    extends WeakReference<EngineResource<?>>
  {
    final Key id;
    final boolean isCacheable;
    @Nullable
    Resource<?> resource;
    
    ResourceWeakReference(Key paramKey, EngineResource paramEngineResource, ReferenceQueue paramReferenceQueue, boolean paramBoolean)
    {
      super(paramReferenceQueue);
      id = ((Key)Preconditions.checkNotNull(paramKey));
      if ((paramEngineResource.isCacheable()) && (paramBoolean)) {
        paramKey = (Resource)Preconditions.checkNotNull(paramEngineResource.getResource());
      } else {
        paramKey = null;
      }
      resource = paramKey;
      isCacheable = paramEngineResource.isCacheable();
    }
    
    void reset()
    {
      resource = null;
      clear();
    }
  }
}
