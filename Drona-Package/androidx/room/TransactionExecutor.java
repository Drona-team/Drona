package androidx.room;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;

class TransactionExecutor
  implements Executor
{
  private Runnable mActive;
  private final Executor mExecutor;
  private final ArrayDeque<Runnable> mTasks = new ArrayDeque();
  
  TransactionExecutor(Executor paramExecutor)
  {
    mExecutor = paramExecutor;
  }
  
  public void execute(final Runnable paramRunnable)
  {
    try
    {
      mTasks.offer(new Runnable()
      {
        public void run()
        {
          try
          {
            paramRunnable.run();
            scheduleNext();
            return;
          }
          catch (Throwable localThrowable)
          {
            scheduleNext();
            throw localThrowable;
          }
        }
      });
      if (mActive == null) {
        scheduleNext();
      }
      return;
    }
    catch (Throwable paramRunnable)
    {
      throw paramRunnable;
    }
  }
  
  void scheduleNext()
  {
    try
    {
      Runnable localRunnable = (Runnable)mTasks.poll();
      mActive = localRunnable;
      if (localRunnable != null) {
        mExecutor.execute(mActive);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}
