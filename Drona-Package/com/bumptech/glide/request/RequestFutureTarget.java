package com.bumptech.glide.request;

import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Util;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RequestFutureTarget<R>
  implements FutureTarget<R>, RequestListener<R>
{
  private static final Waiter DEFAULT_WAITER = new Waiter();
  private final boolean assertBackgroundThread;
  @Nullable
  private GlideException exception;
  private final int height;
  private boolean isCancelled;
  private boolean loadFailed;
  @Nullable
  private Request request;
  @Nullable
  private R resource;
  private boolean resultReceived;
  private final Waiter waiter;
  private final int width;
  
  public RequestFutureTarget(int paramInt1, int paramInt2)
  {
    this(paramInt1, paramInt2, true, DEFAULT_WAITER);
  }
  
  RequestFutureTarget(int paramInt1, int paramInt2, boolean paramBoolean, Waiter paramWaiter)
  {
    width = paramInt1;
    height = paramInt2;
    assertBackgroundThread = paramBoolean;
    waiter = paramWaiter;
  }
  
  private Object doGet(Long paramLong)
    throws ExecutionException, InterruptedException, TimeoutException
  {
    Object localObject = this;
    try
    {
      boolean bool = assertBackgroundThread;
      RequestFutureTarget localRequestFutureTarget = this;
      if (bool)
      {
        localObject = localRequestFutureTarget;
        if (!localRequestFutureTarget.isDone())
        {
          localObject = localRequestFutureTarget;
          Util.assertBackgroundThread();
        }
      }
      localObject = localRequestFutureTarget;
      bool = isCancelled;
      if (!bool)
      {
        localObject = localRequestFutureTarget;
        bool = loadFailed;
        if (!bool)
        {
          localObject = localRequestFutureTarget;
          bool = resultReceived;
          if (bool)
          {
            localObject = localRequestFutureTarget;
            paramLong = resource;
            return paramLong;
          }
          if (paramLong == null)
          {
            localObject = localRequestFutureTarget;
            waiter.waitForTimeout(localRequestFutureTarget, 0L);
          }
          else
          {
            localObject = localRequestFutureTarget;
            if (paramLong.longValue() > 0L)
            {
              localObject = localRequestFutureTarget;
              long l2 = System.currentTimeMillis();
              long l1 = l2;
              localObject = localRequestFutureTarget;
              l2 = paramLong.longValue() + l2;
              for (;;)
              {
                localObject = localRequestFutureTarget;
                if ((localRequestFutureTarget.isDone()) || (l1 >= l2)) {
                  break;
                }
                localObject = localRequestFutureTarget;
                waiter.waitForTimeout(localRequestFutureTarget, l2 - l1);
                localObject = localRequestFutureTarget;
                l1 = System.currentTimeMillis();
              }
            }
          }
          localObject = localRequestFutureTarget;
          if (!Thread.interrupted())
          {
            localObject = localRequestFutureTarget;
            bool = loadFailed;
            if (!bool)
            {
              localObject = localRequestFutureTarget;
              bool = isCancelled;
              if (!bool)
              {
                localObject = localRequestFutureTarget;
                bool = resultReceived;
                if (bool)
                {
                  localObject = localRequestFutureTarget;
                  paramLong = resource;
                  return paramLong;
                }
                localObject = localRequestFutureTarget;
                throw new TimeoutException();
              }
              localObject = localRequestFutureTarget;
              throw new CancellationException();
            }
            localObject = localRequestFutureTarget;
            throw new ExecutionException(exception);
          }
          localObject = localRequestFutureTarget;
          throw new InterruptedException();
        }
        localObject = localRequestFutureTarget;
        throw new ExecutionException(exception);
      }
      localObject = localRequestFutureTarget;
      throw new CancellationException();
    }
    catch (Throwable paramLong)
    {
      throw paramLong;
    }
  }
  
  public boolean cancel(boolean paramBoolean)
  {
    try
    {
      boolean bool = isDone();
      if (bool) {
        return false;
      }
      isCancelled = true;
      waiter.notifyAll(this);
      if ((paramBoolean) && (request != null))
      {
        request.clear();
        request = null;
      }
      return true;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public Object get()
    throws InterruptedException, ExecutionException
  {
    try
    {
      Object localObject = doGet(null);
      return localObject;
    }
    catch (TimeoutException localTimeoutException)
    {
      throw new AssertionError(localTimeoutException);
    }
  }
  
  public Object get(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    return doGet(Long.valueOf(paramTimeUnit.toMillis(paramLong)));
  }
  
  public Request getRequest()
  {
    try
    {
      Request localRequest = request;
      return localRequest;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void getSize(SizeReadyCallback paramSizeReadyCallback)
  {
    paramSizeReadyCallback.onSizeReady(width, height);
  }
  
  public boolean isCancelled()
  {
    try
    {
      boolean bool = isCancelled;
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean isDone()
  {
    try
    {
      if ((!isCancelled) && (!resultReceived))
      {
        bool = loadFailed;
        if (!bool)
        {
          bool = false;
          break label35;
        }
      }
      boolean bool = true;
      label35:
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void onDestroy() {}
  
  public void onLoadCleared(Drawable paramDrawable) {}
  
  public void onLoadFailed(Drawable paramDrawable) {}
  
  public boolean onLoadFailed(GlideException paramGlideException, Object paramObject, Target paramTarget, boolean paramBoolean)
  {
    try
    {
      loadFailed = true;
      exception = paramGlideException;
      waiter.notifyAll(this);
      return false;
    }
    catch (Throwable paramGlideException)
    {
      throw paramGlideException;
    }
  }
  
  public void onLoadStarted(Drawable paramDrawable) {}
  
  public void onResourceReady(Object paramObject, Transition paramTransition) {}
  
  public boolean onResourceReady(Object paramObject1, Object paramObject2, Target paramTarget, DataSource paramDataSource, boolean paramBoolean)
  {
    try
    {
      resultReceived = true;
      resource = paramObject1;
      waiter.notifyAll(this);
      return false;
    }
    catch (Throwable paramObject1)
    {
      throw paramObject1;
    }
  }
  
  public void onStart() {}
  
  public void onStop() {}
  
  public void removeCallback(SizeReadyCallback paramSizeReadyCallback) {}
  
  public void setRequest(Request paramRequest)
  {
    try
    {
      request = paramRequest;
      return;
    }
    catch (Throwable paramRequest)
    {
      throw paramRequest;
    }
  }
  
  @VisibleForTesting
  static class Waiter
  {
    Waiter() {}
    
    void notifyAll(Object paramObject)
    {
      paramObject.notifyAll();
    }
    
    void waitForTimeout(Object paramObject, long paramLong)
      throws InterruptedException
    {
      paramObject.wait(paramLong);
    }
  }
}
