package bolts;

import java.io.Closeable;

public class CancellationTokenRegistration
  implements Closeable
{
  private Runnable action;
  private boolean closed;
  private final Object lock = new Object();
  private CancellationTokenSource tokenSource;
  
  CancellationTokenRegistration(CancellationTokenSource paramCancellationTokenSource, Runnable paramRunnable)
  {
    tokenSource = paramCancellationTokenSource;
    action = paramRunnable;
  }
  
  private void throwIfClosed()
  {
    if (!closed) {
      return;
    }
    throw new IllegalStateException("Object already closed");
  }
  
  public void close()
  {
    Object localObject = lock;
    try
    {
      if (closed) {
        return;
      }
      closed = true;
      tokenSource.unregister(this);
      tokenSource = null;
      action = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  void runAction()
  {
    Object localObject = lock;
    try
    {
      throwIfClosed();
      action.run();
      close();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}
