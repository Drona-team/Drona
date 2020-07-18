package expo.modules.imageloader;

import androidx.annotation.Nullable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SimpleSettableFuture<T>
  implements Future<T>
{
  @Nullable
  private Exception mException;
  private final CountDownLatch mReadyLatch = new CountDownLatch(1);
  @Nullable
  private T mResult;
  
  public SimpleSettableFuture() {}
  
  private void checkNotSet()
  {
    if (mReadyLatch.getCount() != 0L) {
      return;
    }
    throw new RuntimeException("Result has already been set!");
  }
  
  public boolean cancel(boolean paramBoolean)
  {
    throw new UnsupportedOperationException();
  }
  
  public Object get()
    throws InterruptedException, ExecutionException
  {
    mReadyLatch.await();
    if (mException == null) {
      return mResult;
    }
    throw new ExecutionException(mException);
  }
  
  public Object get(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    if (mReadyLatch.await(paramLong, paramTimeUnit))
    {
      if (mException == null) {
        return mResult;
      }
      throw new ExecutionException(mException);
    }
    throw new TimeoutException("Timed out waiting for result");
  }
  
  public Object getOrThrow()
  {
    try
    {
      Object localObject = get();
      return localObject;
    }
    catch (InterruptedException|ExecutionException localInterruptedException)
    {
      throw new RuntimeException(localInterruptedException);
    }
  }
  
  public Object getOrThrow(long paramLong, TimeUnit paramTimeUnit)
  {
    try
    {
      paramTimeUnit = get(paramLong, paramTimeUnit);
      return paramTimeUnit;
    }
    catch (InterruptedException|ExecutionException|TimeoutException paramTimeUnit)
    {
      throw new RuntimeException(paramTimeUnit);
    }
  }
  
  public boolean isCancelled()
  {
    return false;
  }
  
  public boolean isDone()
  {
    return mReadyLatch.getCount() == 0L;
  }
  
  public void setException(Exception paramException)
  {
    checkNotSet();
    mException = paramException;
    mReadyLatch.countDown();
  }
  
  public void track(Object paramObject)
  {
    checkNotSet();
    mResult = paramObject;
    mReadyLatch.countDown();
  }
}
