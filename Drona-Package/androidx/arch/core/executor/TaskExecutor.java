package androidx.arch.core.executor;

import androidx.annotation.RestrictTo;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public abstract class TaskExecutor
{
  public TaskExecutor() {}
  
  public abstract void executeOnDiskIO(Runnable paramRunnable);
  
  public void executeOnMainThread(Runnable paramRunnable)
  {
    if (isMainThread())
    {
      paramRunnable.run();
      return;
    }
    postToMainThread(paramRunnable);
  }
  
  public abstract boolean isMainThread();
  
  public abstract void postToMainThread(Runnable paramRunnable);
}
