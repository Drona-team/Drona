package bolts;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CancellationTokenSource
  implements Closeable
{
  private boolean cancellationRequested;
  private boolean closed;
  private final ScheduledExecutorService executor = BoltsExecutors.scheduled();
  private final Object lock = new Object();
  private final List<CancellationTokenRegistration> registrations = new ArrayList();
  private ScheduledFuture<?> scheduledCancellation;
  
  public CancellationTokenSource() {}
  
  private void cancelAfter(long paramLong, TimeUnit paramTimeUnit)
  {
    boolean bool = paramLong < -1L;
    if (!bool)
    {
      if (paramLong == 0L)
      {
        cancel();
        return;
      }
      Object localObject = lock;
      try
      {
        if (cancellationRequested) {
          return;
        }
        cancelScheduledCancellation();
        if (bool) {
          scheduledCancellation = executor.schedule(new Runnable()
          {
            public void run()
            {
              Object localObject = lock;
              try
              {
                CancellationTokenSource.access$102(CancellationTokenSource.this, null);
                cancel();
                return;
              }
              catch (Throwable localThrowable)
              {
                throw localThrowable;
              }
            }
          }, paramLong, paramTimeUnit);
        }
        return;
      }
      catch (Throwable paramTimeUnit)
      {
        throw paramTimeUnit;
      }
    }
    throw new IllegalArgumentException("Delay must be >= -1");
  }
  
  private void cancelScheduledCancellation()
  {
    if (scheduledCancellation != null)
    {
      scheduledCancellation.cancel(true);
      scheduledCancellation = null;
    }
  }
  
  private void notifyListeners(List paramList)
  {
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      ((CancellationTokenRegistration)paramList.next()).runAction();
    }
  }
  
  private void throwIfClosed()
  {
    if (!closed) {
      return;
    }
    throw new IllegalStateException("Object already closed");
  }
  
  public void cancel()
  {
    Object localObject = lock;
    try
    {
      throwIfClosed();
      if (cancellationRequested) {
        return;
      }
      cancelScheduledCancellation();
      cancellationRequested = true;
      ArrayList localArrayList = new ArrayList(registrations);
      notifyListeners(localArrayList);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void cancelAfter(long paramLong)
  {
    cancelAfter(paramLong, TimeUnit.MILLISECONDS);
  }
  
  public void close()
  {
    Object localObject = lock;
    try
    {
      if (closed) {
        return;
      }
      cancelScheduledCancellation();
      Iterator localIterator = registrations.iterator();
      while (localIterator.hasNext()) {
        ((CancellationTokenRegistration)localIterator.next()).close();
      }
      registrations.clear();
      closed = true;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public CancellationToken getToken()
  {
    Object localObject = lock;
    try
    {
      throwIfClosed();
      CancellationToken localCancellationToken = new CancellationToken(this);
      return localCancellationToken;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean isCancellationRequested()
  {
    Object localObject = lock;
    try
    {
      throwIfClosed();
      boolean bool = cancellationRequested;
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  CancellationTokenRegistration register(Runnable paramRunnable)
  {
    Object localObject = lock;
    try
    {
      throwIfClosed();
      paramRunnable = new CancellationTokenRegistration(this, paramRunnable);
      if (cancellationRequested) {
        paramRunnable.runAction();
      } else {
        registrations.add(paramRunnable);
      }
      return paramRunnable;
    }
    catch (Throwable paramRunnable)
    {
      throw paramRunnable;
    }
  }
  
  void throwIfCancellationRequested()
    throws CancellationException
  {
    Object localObject = lock;
    try
    {
      throwIfClosed();
      if (!cancellationRequested) {
        return;
      }
      throw new CancellationException();
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public String toString()
  {
    return String.format(Locale.US, "%s@%s[cancellationRequested=%s]", new Object[] { getClass().getName(), Integer.toHexString(hashCode()), Boolean.toString(isCancellationRequested()) });
  }
  
  void unregister(CancellationTokenRegistration paramCancellationTokenRegistration)
  {
    Object localObject = lock;
    try
    {
      throwIfClosed();
      registrations.remove(paramCancellationTokenRegistration);
      return;
    }
    catch (Throwable paramCancellationTokenRegistration)
    {
      throw paramCancellationTokenRegistration;
    }
  }
}
