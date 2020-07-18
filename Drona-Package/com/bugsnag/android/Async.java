package com.bugsnag.android;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class Async
{
  private static final int CORE_POOL_SIZE;
  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
  private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 30L, TimeUnit.SECONDS, POOL_WORK_QUEUE, THREAD_FACTORY);
  private static final int KEEP_ALIVE_SECONDS = 30;
  private static final int MAXIMUM_POOL_SIZE;
  static final BlockingQueue<Runnable> POOL_WORK_QUEUE;
  private static final ThreadFactory THREAD_FACTORY;
  
  static
  {
    CORE_POOL_SIZE = Math.max(1, Math.min(CPU_COUNT - 1, 4));
    MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    POOL_WORK_QUEUE = new LinkedBlockingQueue(128);
    THREAD_FACTORY = new ThreadFactory()
    {
      private final AtomicInteger count = new AtomicInteger(1);
      
      public Thread newThread(Runnable paramAnonymousRunnable)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Bugsnag Thread #");
        localStringBuilder.append(count.getAndIncrement());
        return new Thread(paramAnonymousRunnable, localStringBuilder.toString());
      }
    };
  }
  
  Async() {}
  
  static void cancelTasks()
    throws InterruptedException
  {
    Logger.info("Cancelling tasks");
    EXECUTOR.shutdown();
    EXECUTOR.awaitTermination(2000L, TimeUnit.MILLISECONDS);
    Logger.info("Finishing cancelling tasks");
  }
  
  static void runAsync(Runnable paramRunnable)
    throws RejectedExecutionException
  {
    EXECUTOR.execute(paramRunnable);
  }
}
