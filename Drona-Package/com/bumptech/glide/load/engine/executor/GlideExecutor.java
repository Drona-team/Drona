package com.bumptech.glide.load.engine.executor;

import android.os.Process;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.util.Log;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class GlideExecutor
  implements ExecutorService
{
  private static final String ANIMATION_EXECUTOR_NAME = "animation";
  private static final String DEFAULT_DISK_CACHE_EXECUTOR_NAME = "disk-cache";
  private static final int DEFAULT_DISK_CACHE_EXECUTOR_THREADS = 1;
  private static final String DEFAULT_SOURCE_EXECUTOR_NAME = "source";
  private static final long KEEP_ALIVE_TIME_MS = TimeUnit.SECONDS.toMillis(10L);
  private static final int MAXIMUM_AUTOMATIC_THREAD_COUNT = 4;
  private static final String PAGE_KEY = "GlideExecutor";
  private static final String SOURCE_UNLIMITED_EXECUTOR_NAME = "source-unlimited";
  private static volatile int bestThreadCount;
  private final ExecutorService delegate;
  
  GlideExecutor(ExecutorService paramExecutorService)
  {
    delegate = paramExecutorService;
  }
  
  public static int calculateBestThreadCount()
  {
    if (bestThreadCount == 0) {
      bestThreadCount = Math.min(4, RuntimeCompat.availableProcessors());
    }
    return bestThreadCount;
  }
  
  public static GlideExecutor newAnimationExecutor()
  {
    int i;
    if (calculateBestThreadCount() >= 4) {
      i = 2;
    } else {
      i = 1;
    }
    return newAnimationExecutor(i, UncaughtThrowableStrategy.DEFAULT);
  }
  
  public static GlideExecutor newAnimationExecutor(int paramInt, UncaughtThrowableStrategy paramUncaughtThrowableStrategy)
  {
    return new GlideExecutor(new ThreadPoolExecutor(0, paramInt, KEEP_ALIVE_TIME_MS, TimeUnit.MILLISECONDS, new PriorityBlockingQueue(), new DefaultThreadFactory("animation", paramUncaughtThrowableStrategy, true)));
  }
  
  public static GlideExecutor newDiskCacheExecutor()
  {
    return newDiskCacheExecutor(1, "disk-cache", UncaughtThrowableStrategy.DEFAULT);
  }
  
  public static GlideExecutor newDiskCacheExecutor(int paramInt, String paramString, UncaughtThrowableStrategy paramUncaughtThrowableStrategy)
  {
    return new GlideExecutor(new ThreadPoolExecutor(paramInt, paramInt, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue(), new DefaultThreadFactory(paramString, paramUncaughtThrowableStrategy, true)));
  }
  
  public static GlideExecutor newDiskCacheExecutor(UncaughtThrowableStrategy paramUncaughtThrowableStrategy)
  {
    return newDiskCacheExecutor(1, "disk-cache", paramUncaughtThrowableStrategy);
  }
  
  public static GlideExecutor newSourceExecutor()
  {
    return newSourceExecutor(calculateBestThreadCount(), "source", UncaughtThrowableStrategy.DEFAULT);
  }
  
  public static GlideExecutor newSourceExecutor(int paramInt, String paramString, UncaughtThrowableStrategy paramUncaughtThrowableStrategy)
  {
    return new GlideExecutor(new ThreadPoolExecutor(paramInt, paramInt, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue(), new DefaultThreadFactory(paramString, paramUncaughtThrowableStrategy, false)));
  }
  
  public static GlideExecutor newSourceExecutor(UncaughtThrowableStrategy paramUncaughtThrowableStrategy)
  {
    return newSourceExecutor(calculateBestThreadCount(), "source", paramUncaughtThrowableStrategy);
  }
  
  public static GlideExecutor newUnlimitedSourceExecutor()
  {
    return new GlideExecutor(new ThreadPoolExecutor(0, Integer.MAX_VALUE, KEEP_ALIVE_TIME_MS, TimeUnit.MILLISECONDS, new SynchronousQueue(), new DefaultThreadFactory("source-unlimited", UncaughtThrowableStrategy.DEFAULT, false)));
  }
  
  public boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    return delegate.awaitTermination(paramLong, paramTimeUnit);
  }
  
  public void execute(Runnable paramRunnable)
  {
    delegate.execute(paramRunnable);
  }
  
  public List invokeAll(Collection paramCollection)
    throws InterruptedException
  {
    return delegate.invokeAll(paramCollection);
  }
  
  public List invokeAll(Collection paramCollection, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    return delegate.invokeAll(paramCollection, paramLong, paramTimeUnit);
  }
  
  public Object invokeAny(Collection paramCollection)
    throws InterruptedException, ExecutionException
  {
    return delegate.invokeAny(paramCollection);
  }
  
  public Object invokeAny(Collection paramCollection, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    return delegate.invokeAny(paramCollection, paramLong, paramTimeUnit);
  }
  
  public boolean isShutdown()
  {
    return delegate.isShutdown();
  }
  
  public boolean isTerminated()
  {
    return delegate.isTerminated();
  }
  
  public void shutdown()
  {
    delegate.shutdown();
  }
  
  public List shutdownNow()
  {
    return delegate.shutdownNow();
  }
  
  public Future submit(Runnable paramRunnable)
  {
    return delegate.submit(paramRunnable);
  }
  
  public Future submit(Runnable paramRunnable, Object paramObject)
  {
    return delegate.submit(paramRunnable, paramObject);
  }
  
  public Future submit(Callable paramCallable)
  {
    return delegate.submit(paramCallable);
  }
  
  public String toString()
  {
    return delegate.toString();
  }
  
  private static final class DefaultThreadFactory
    implements ThreadFactory
  {
    private static final int DEFAULT_PRIORITY = 9;
    private final String name;
    final boolean preventNetworkOperations;
    private int threadNum;
    final GlideExecutor.UncaughtThrowableStrategy uncaughtThrowableStrategy;
    
    DefaultThreadFactory(String paramString, GlideExecutor.UncaughtThrowableStrategy paramUncaughtThrowableStrategy, boolean paramBoolean)
    {
      name = paramString;
      uncaughtThrowableStrategy = paramUncaughtThrowableStrategy;
      preventNetworkOperations = paramBoolean;
    }
    
    public Thread newThread(Runnable paramRunnable)
    {
      try
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("glide-");
        localStringBuilder.append(name);
        localStringBuilder.append("-thread-");
        localStringBuilder.append(threadNum);
        paramRunnable = new Thread(paramRunnable, localStringBuilder.toString())
        {
          public void run()
          {
            Process.setThreadPriority(9);
            if (preventNetworkOperations) {
              StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().penaltyDeath().build());
            }
            try
            {
              super.run();
              return;
            }
            catch (Throwable localThrowable)
            {
              uncaughtThrowableStrategy.handle(localThrowable);
            }
          }
        };
        threadNum += 1;
        return paramRunnable;
      }
      catch (Throwable paramRunnable)
      {
        throw paramRunnable;
      }
    }
  }
  
  public static abstract interface UncaughtThrowableStrategy
  {
    public static final UncaughtThrowableStrategy DEFAULT = LOG;
    public static final UncaughtThrowableStrategy IGNORE = new UncaughtThrowableStrategy()
    {
      public void handle(Throwable paramAnonymousThrowable) {}
    };
    public static final UncaughtThrowableStrategy LOG = new UncaughtThrowableStrategy()
    {
      public void handle(Throwable paramAnonymousThrowable)
      {
        if ((paramAnonymousThrowable != null) && (Log.isLoggable("GlideExecutor", 6))) {
          Log.e("GlideExecutor", "Request threw uncaught throwable", paramAnonymousThrowable);
        }
      }
    };
    public static final UncaughtThrowableStrategy THROW = new UncaughtThrowableStrategy()
    {
      public void handle(Throwable paramAnonymousThrowable)
      {
        if (paramAnonymousThrowable == null) {
          return;
        }
        throw new RuntimeException("Request threw uncaught throwable", paramAnonymousThrowable);
      }
    };
    
    public abstract void handle(Throwable paramThrowable);
  }
}
