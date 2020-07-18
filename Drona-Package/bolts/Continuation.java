package bolts;

public abstract interface Continuation<TTaskResult, TContinuationResult>
{
  public abstract Object then(Task paramTask)
    throws Exception;
}
