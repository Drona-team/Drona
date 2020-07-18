package com.facebook.common.executors;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class StatefulRunnable<T>
  implements Runnable
{
  protected static final int STATE_CANCELLED = 2;
  protected static final int STATE_CREATED = 0;
  protected static final int STATE_FAILED = 4;
  protected static final int STATE_FINISHED = 3;
  protected static final int STATE_STARTED = 1;
  protected final AtomicInteger mState = new AtomicInteger(0);
  
  public StatefulRunnable() {}
  
  public void cancel()
  {
    if (mState.compareAndSet(0, 2)) {
      onCancellation();
    }
  }
  
  protected void disposeResult(Object paramObject) {}
  
  protected abstract Object getResult()
    throws Exception;
  
  protected void onCancellation() {}
  
  protected void onFailure(Exception paramException) {}
  
  protected void onSuccess(Object paramObject) {}
  
  public final void run()
  {
    if (!mState.compareAndSet(0, 1)) {
      return;
    }
    try
    {
      Object localObject = getResult();
      mState.set(3);
      try
      {
        onSuccess(localObject);
        disposeResult(localObject);
        return;
      }
      catch (Throwable localThrowable)
      {
        disposeResult(localObject);
        throw localThrowable;
      }
      return;
    }
    catch (Exception localException)
    {
      mState.set(4);
      onFailure(localException);
    }
  }
}