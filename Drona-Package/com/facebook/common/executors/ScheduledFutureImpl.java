package com.facebook.common.executors;

import android.os.Handler;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ScheduledFutureImpl<V>
  implements RunnableFuture<V>, ScheduledFuture<V>
{
  private final Handler mHandler;
  private final FutureTask<V> mListenableFuture;
  
  public ScheduledFutureImpl(Handler paramHandler, Runnable paramRunnable, Object paramObject)
  {
    mHandler = paramHandler;
    mListenableFuture = new FutureTask(paramRunnable, paramObject);
  }
  
  public ScheduledFutureImpl(Handler paramHandler, Callable paramCallable)
  {
    mHandler = paramHandler;
    mListenableFuture = new FutureTask(paramCallable);
  }
  
  public boolean cancel(boolean paramBoolean)
  {
    return mListenableFuture.cancel(paramBoolean);
  }
  
  public int compareTo(Delayed paramDelayed)
  {
    throw new UnsupportedOperationException();
  }
  
  public Object get()
    throws InterruptedException, ExecutionException
  {
    return mListenableFuture.get();
  }
  
  public Object get(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    return mListenableFuture.get(paramLong, paramTimeUnit);
  }
  
  public long getDelay(TimeUnit paramTimeUnit)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean isCancelled()
  {
    return mListenableFuture.isCancelled();
  }
  
  public boolean isDone()
  {
    return mListenableFuture.isDone();
  }
  
  public void run()
  {
    mListenableFuture.run();
  }
}
