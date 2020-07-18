package bolts;

class UnobservedErrorNotifier
{
  private Task<?> task;
  
  public UnobservedErrorNotifier(Task paramTask)
  {
    task = paramTask;
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      Task localTask = task;
      if (localTask != null)
      {
        Task.UnobservedExceptionHandler localUnobservedExceptionHandler = Task.getUnobservedExceptionHandler();
        if (localUnobservedExceptionHandler != null) {
          localUnobservedExceptionHandler.unobservedException(localTask, new UnobservedTaskException(localTask.getError()));
        }
      }
      super.finalize();
      return;
    }
    catch (Throwable localThrowable)
    {
      super.finalize();
      throw localThrowable;
    }
  }
  
  public void setObserved()
  {
    task = null;
  }
}
