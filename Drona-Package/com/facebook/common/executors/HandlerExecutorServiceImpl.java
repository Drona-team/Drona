package com.facebook.common.executors;

import android.os.Handler;
import android.os.Looper;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HandlerExecutorServiceImpl
  extends AbstractExecutorService
  implements HandlerExecutorService
{
  private final Handler mHandler;
  
  public HandlerExecutorServiceImpl(Handler paramHandler)
  {
    mHandler = paramHandler;
  }
  
  public boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }
  
  public void execute(Runnable paramRunnable)
  {
    mHandler.post(paramRunnable);
  }
  
  public boolean isHandlerThread()
  {
    return Thread.currentThread() == mHandler.getLooper().getThread();
  }
  
  public boolean isShutdown()
  {
    return false;
  }
  
  public boolean isTerminated()
  {
    return false;
  }
  
  protected ScheduledFutureImpl newTaskFor(Runnable paramRunnable, Object paramObject)
  {
    return new ScheduledFutureImpl(mHandler, paramRunnable, paramObject);
  }
  
  protected ScheduledFutureImpl newTaskFor(Callable paramCallable)
  {
    return new ScheduledFutureImpl(mHandler, paramCallable);
  }
  
  public void quit()
  {
    mHandler.getLooper().quit();
  }
  
  public ScheduledFuture schedule(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit)
  {
    paramRunnable = newTaskFor(paramRunnable, null);
    mHandler.postDelayed(paramRunnable, paramTimeUnit.toMillis(paramLong));
    return paramRunnable;
  }
  
  public ScheduledFuture schedule(Callable paramCallable, long paramLong, TimeUnit paramTimeUnit)
  {
    paramCallable = newTaskFor(paramCallable);
    mHandler.postDelayed(paramCallable, paramTimeUnit.toMillis(paramLong));
    return paramCallable;
  }
  
  public ScheduledFuture scheduleAtFixedRate(Runnable paramRunnable, long paramLong1, long paramLong2, TimeUnit paramTimeUnit)
  {
    throw new UnsupportedOperationException();
  }
  
  public ScheduledFuture scheduleWithFixedDelay(Runnable paramRunnable, long paramLong1, long paramLong2, TimeUnit paramTimeUnit)
  {
    throw new UnsupportedOperationException();
  }
  
  public void shutdown()
  {
    throw new UnsupportedOperationException();
  }
  
  public List shutdownNow()
  {
    throw new UnsupportedOperationException();
  }
  
  public ScheduledFuture submit(Runnable paramRunnable)
  {
    return submit(paramRunnable, null);
  }
  
  public ScheduledFuture submit(Runnable paramRunnable, Object paramObject)
  {
    if (paramRunnable != null)
    {
      paramRunnable = newTaskFor(paramRunnable, paramObject);
      execute(paramRunnable);
      return paramRunnable;
    }
    throw new NullPointerException();
  }
  
  public ScheduledFuture submit(Callable paramCallable)
  {
    if (paramCallable != null)
    {
      paramCallable = newTaskFor(paramCallable);
      execute(paramCallable);
      return paramCallable;
    }
    throw new NullPointerException();
  }
}
