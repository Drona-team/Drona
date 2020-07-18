package com.facebook.datasource;

public abstract class BaseDataSubscriber<T>
  implements DataSubscriber<T>
{
  public BaseDataSubscriber() {}
  
  public void onCancellation(DataSource paramDataSource) {}
  
  public void onFailure(DataSource paramDataSource)
  {
    try
    {
      onFailureImpl(paramDataSource);
      paramDataSource.close();
      return;
    }
    catch (Throwable localThrowable)
    {
      paramDataSource.close();
      throw localThrowable;
    }
  }
  
  protected abstract void onFailureImpl(DataSource paramDataSource);
  
  public void onNewResult(DataSource paramDataSource)
  {
    boolean bool = paramDataSource.isFinished();
    try
    {
      onNewResultImpl(paramDataSource);
      if (bool)
      {
        paramDataSource.close();
        return;
      }
    }
    catch (Throwable localThrowable)
    {
      if (bool) {
        paramDataSource.close();
      }
      throw localThrowable;
    }
  }
  
  protected abstract void onNewResultImpl(DataSource paramDataSource);
  
  public void onProgressUpdate(DataSource paramDataSource) {}
}
