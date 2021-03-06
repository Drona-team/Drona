package bolts;

public class TaskCompletionSource<TResult>
{
  private final Task<TResult> task = new Task();
  
  public TaskCompletionSource() {}
  
  public Task getTask()
  {
    return task;
  }
  
  public void setCancelled()
  {
    if (trySetCancelled()) {
      return;
    }
    throw new IllegalStateException("Cannot cancel a completed task.");
  }
  
  public void setError(Exception paramException)
  {
    if (trySetError(paramException)) {
      return;
    }
    throw new IllegalStateException("Cannot set the error on a completed task.");
  }
  
  public void setResult(Object paramObject)
  {
    if (trySetResult(paramObject)) {
      return;
    }
    throw new IllegalStateException("Cannot set the result of a completed task.");
  }
  
  public boolean trySetCancelled()
  {
    return task.trySetCancelled();
  }
  
  public boolean trySetError(Exception paramException)
  {
    return task.trySetError(paramException);
  }
  
  public boolean trySetResult(Object paramObject)
  {
    return task.trySetResult(paramObject);
  }
}
