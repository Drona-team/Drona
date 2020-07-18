package com.airbnb.lottie;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import com.airbnb.lottie.utils.Logger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class LottieTask<T>
{
  public static Executor EXECUTOR = ;
  private final Set<LottieListener<Throwable>> failureListeners = new LinkedHashSet(1);
  private final Handler handler = new Handler(Looper.getMainLooper());
  @Nullable
  private volatile LottieResult<T> result = null;
  private final Set<LottieListener<T>> successListeners = new LinkedHashSet(1);
  
  public LottieTask(Callable paramCallable)
  {
    this(paramCallable, false);
  }
  
  LottieTask(Callable paramCallable, boolean paramBoolean)
  {
    if (paramBoolean) {
      try
      {
        setResult((LottieResult)paramCallable.call());
        return;
      }
      catch (Throwable paramCallable)
      {
        setResult(new LottieResult(paramCallable));
        return;
      }
    }
    EXECUTOR.execute(new LottieFutureTask(paramCallable));
  }
  
  private void notifyFailureListeners(Throwable paramThrowable)
  {
    try
    {
      Object localObject = new ArrayList(failureListeners);
      if (((List)localObject).isEmpty())
      {
        Logger.warning("Lottie encountered an error but no failure listener was added:", paramThrowable);
        return;
      }
      localObject = ((List)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        ((LottieListener)((Iterator)localObject).next()).onResult(paramThrowable);
      }
      return;
    }
    catch (Throwable paramThrowable)
    {
      throw paramThrowable;
    }
  }
  
  private void notifyListeners()
  {
    handler.post(new Runnable()
    {
      public void run()
      {
        if (result == null) {
          return;
        }
        LottieResult localLottieResult = result;
        if (localLottieResult.getValue() != null)
        {
          LottieTask.this.notifySuccessListeners(localLottieResult.getValue());
          return;
        }
        LottieTask.this.notifyFailureListeners(localLottieResult.getException());
      }
    });
  }
  
  private void notifySuccessListeners(Object paramObject)
  {
    try
    {
      Iterator localIterator = new ArrayList(successListeners).iterator();
      while (localIterator.hasNext()) {
        ((LottieListener)localIterator.next()).onResult(paramObject);
      }
      return;
    }
    catch (Throwable paramObject)
    {
      throw paramObject;
    }
  }
  
  private void setResult(LottieResult paramLottieResult)
  {
    if (result == null)
    {
      result = paramLottieResult;
      notifyListeners();
      return;
    }
    throw new IllegalStateException("A task may only be set once.");
  }
  
  public LottieTask addFailureListener(LottieListener paramLottieListener)
  {
    try
    {
      if ((result != null) && (result.getException() != null)) {
        paramLottieListener.onResult(result.getException());
      }
      failureListeners.add(paramLottieListener);
      return this;
    }
    catch (Throwable paramLottieListener)
    {
      throw paramLottieListener;
    }
  }
  
  public LottieTask addListener(LottieListener paramLottieListener)
  {
    try
    {
      if ((result != null) && (result.getValue() != null)) {
        paramLottieListener.onResult(result.getValue());
      }
      successListeners.add(paramLottieListener);
      return this;
    }
    catch (Throwable paramLottieListener)
    {
      throw paramLottieListener;
    }
  }
  
  public LottieTask removeFailureListener(LottieListener paramLottieListener)
  {
    try
    {
      failureListeners.remove(paramLottieListener);
      return this;
    }
    catch (Throwable paramLottieListener)
    {
      throw paramLottieListener;
    }
  }
  
  public LottieTask removeListener(LottieListener paramLottieListener)
  {
    try
    {
      successListeners.remove(paramLottieListener);
      return this;
    }
    catch (Throwable paramLottieListener)
    {
      throw paramLottieListener;
    }
  }
  
  private class LottieFutureTask
    extends FutureTask<LottieResult<T>>
  {
    LottieFutureTask(Callable paramCallable)
    {
      super();
    }
    
    protected void done()
    {
      if (isCancelled()) {
        return;
      }
      LottieTask localLottieTask = LottieTask.this;
      try
      {
        Object localObject = get();
        localObject = (LottieResult)localObject;
        localLottieTask.setResult((LottieResult)localObject);
        return;
      }
      catch (InterruptedException|ExecutionException localInterruptedException)
      {
        LottieTask.this.setResult(new LottieResult(localInterruptedException));
      }
    }
  }
}
