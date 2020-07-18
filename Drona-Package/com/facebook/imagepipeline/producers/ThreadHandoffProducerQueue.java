package com.facebook.imagepipeline.producers;

import com.facebook.common.internal.Preconditions;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executor;

public class ThreadHandoffProducerQueue
{
  private final Executor mExecutor;
  private boolean mQueueing = false;
  private final Deque<Runnable> mRunnableList;
  
  public ThreadHandoffProducerQueue(Executor paramExecutor)
  {
    mExecutor = ((Executor)Preconditions.checkNotNull(paramExecutor));
    mRunnableList = new ArrayDeque();
  }
  
  private void execInQueue()
  {
    while (!mRunnableList.isEmpty()) {
      mExecutor.execute((Runnable)mRunnableList.pop());
    }
    mRunnableList.clear();
  }
  
  public void addToQueueOrExecute(Runnable paramRunnable)
  {
    try
    {
      if (mQueueing) {
        mRunnableList.add(paramRunnable);
      } else {
        mExecutor.execute(paramRunnable);
      }
      return;
    }
    catch (Throwable paramRunnable)
    {
      throw paramRunnable;
    }
  }
  
  public boolean isQueueing()
  {
    try
    {
      boolean bool = mQueueing;
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void remove(Runnable paramRunnable)
  {
    try
    {
      mRunnableList.remove(paramRunnable);
      return;
    }
    catch (Throwable paramRunnable)
    {
      throw paramRunnable;
    }
  }
  
  public void startQueueing()
  {
    try
    {
      mQueueing = true;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void stopQueuing()
  {
    try
    {
      mQueueing = false;
      execInQueue();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}
