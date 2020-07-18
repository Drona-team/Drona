package com.bumptech.glide.util;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public final class Executors
{
  private static final Executor DIRECT_EXECUTOR = new Executor()
  {
    public void execute(Runnable paramAnonymousRunnable)
    {
      paramAnonymousRunnable.run();
    }
  };
  private static final Executor MAIN_THREAD_EXECUTOR = new Executor()
  {
    private final Handler handler = new Handler(Looper.getMainLooper());
    
    public void execute(Runnable paramAnonymousRunnable)
    {
      handler.post(paramAnonymousRunnable);
    }
  };
  
  private Executors() {}
  
  public static Executor directExecutor()
  {
    return DIRECT_EXECUTOR;
  }
  
  public static Executor mainThreadExecutor()
  {
    return MAIN_THREAD_EXECUTOR;
  }
  
  public static void shutdownAndAwaitTermination(ExecutorService paramExecutorService)
  {
    paramExecutorService.shutdownNow();
    Object localObject = TimeUnit.SECONDS;
    try
    {
      boolean bool = paramExecutorService.awaitTermination(5L, (TimeUnit)localObject);
      if (!bool)
      {
        paramExecutorService.shutdownNow();
        localObject = TimeUnit.SECONDS;
        bool = paramExecutorService.awaitTermination(5L, (TimeUnit)localObject);
        if (bool) {
          return;
        }
        localObject = new RuntimeException("Failed to shutdown");
        throw ((Throwable)localObject);
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      paramExecutorService.shutdownNow();
      Thread.currentThread().interrupt();
      throw new RuntimeException(localInterruptedException);
    }
  }
}
