package com.facebook.common.executors;

import com.facebook.common.logging.FLog;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConstrainedExecutorService
  extends AbstractExecutorService
{
  private static final Class<?> TAG = ConstrainedExecutorService.class;
  private final Executor mExecutor;
  private volatile int mMaxConcurrency;
  private final AtomicInteger mMaxQueueSize;
  private final String mName;
  private final AtomicInteger mPendingWorkers;
  private final Worker mTaskRunner;
  private final BlockingQueue<Runnable> mWorkQueue;
  
  public ConstrainedExecutorService(String paramString, int paramInt, Executor paramExecutor, BlockingQueue paramBlockingQueue)
  {
    if (paramInt > 0)
    {
      mName = paramString;
      mExecutor = paramExecutor;
      mMaxConcurrency = paramInt;
      mWorkQueue = paramBlockingQueue;
      mTaskRunner = new Worker(null);
      mPendingWorkers = new AtomicInteger(0);
      mMaxQueueSize = new AtomicInteger(0);
      return;
    }
    throw new IllegalArgumentException("max concurrency must be > 0");
  }
  
  public static ConstrainedExecutorService newConstrainedExecutor(String paramString, int paramInt1, int paramInt2, Executor paramExecutor)
  {
    return new ConstrainedExecutorService(paramString, paramInt1, paramExecutor, new LinkedBlockingQueue(paramInt2));
  }
  
  private void startWorkerIfNeeded()
  {
    for (int i = mPendingWorkers.get(); i < mMaxConcurrency; i = mPendingWorkers.get())
    {
      int j = i + 1;
      if (mPendingWorkers.compareAndSet(i, j))
      {
        FLog.v(TAG, "%s: starting worker %d of %d", mName, Integer.valueOf(j), Integer.valueOf(mMaxConcurrency));
        mExecutor.execute(mTaskRunner);
        return;
      }
      FLog.v(TAG, "%s: race in startWorkerIfNeeded; retrying", mName);
    }
  }
  
  public boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }
  
  public void execute(Runnable paramRunnable)
  {
    if (paramRunnable != null)
    {
      if (mWorkQueue.offer(paramRunnable))
      {
        int i = mWorkQueue.size();
        int j = mMaxQueueSize.get();
        if ((i > j) && (mMaxQueueSize.compareAndSet(j, i))) {
          FLog.v(TAG, "%s: max pending work in queue = %d", mName, Integer.valueOf(i));
        }
        startWorkerIfNeeded();
        return;
      }
      paramRunnable = new StringBuilder();
      paramRunnable.append(mName);
      paramRunnable.append(" queue is full, size=");
      paramRunnable.append(mWorkQueue.size());
      throw new RejectedExecutionException(paramRunnable.toString());
    }
    throw new NullPointerException("runnable parameter is null");
  }
  
  public boolean isIdle()
  {
    return (mWorkQueue.isEmpty()) && (mPendingWorkers.get() == 0);
  }
  
  public boolean isShutdown()
  {
    return false;
  }
  
  public boolean isTerminated()
  {
    return false;
  }
  
  public void shutdown()
  {
    throw new UnsupportedOperationException();
  }
  
  public List shutdownNow()
  {
    throw new UnsupportedOperationException();
  }
  
  private class Worker
    implements Runnable
  {
    private Worker() {}
    
    public void run()
    {
      try
      {
        Runnable localRunnable = (Runnable)mWorkQueue.poll();
        if (localRunnable != null) {
          localRunnable.run();
        } else {
          FLog.v(ConstrainedExecutorService.TAG, "%s: Worker has nothing to run", mName);
        }
        i = mPendingWorkers.decrementAndGet();
        if (!mWorkQueue.isEmpty())
        {
          ConstrainedExecutorService.this.startWorkerIfNeeded();
          return;
        }
        FLog.v(ConstrainedExecutorService.TAG, "%s: worker finished; %d workers left", mName, Integer.valueOf(i));
        return;
      }
      catch (Throwable localThrowable)
      {
        int i = mPendingWorkers.decrementAndGet();
        if (!mWorkQueue.isEmpty()) {
          ConstrainedExecutorService.this.startWorkerIfNeeded();
        } else {
          FLog.v(ConstrainedExecutorService.TAG, "%s: worker finished; %d workers left", mName, Integer.valueOf(i));
        }
        throw localThrowable;
      }
    }
  }
}
