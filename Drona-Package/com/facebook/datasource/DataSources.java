package com.facebook.datasource;

import com.facebook.common.internal.Supplier;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

public class DataSources
{
  private DataSources() {}
  
  public static Supplier getFailedDataSourceSupplier(Throwable paramThrowable)
  {
    new Supplier()
    {
      public DataSource getFolder()
      {
        return DataSources.immediateFailedDataSource(val$failure);
      }
    };
  }
  
  public static DataSource immediateDataSource(Object paramObject)
  {
    SimpleDataSource localSimpleDataSource = SimpleDataSource.create();
    localSimpleDataSource.setResult(paramObject);
    return localSimpleDataSource;
  }
  
  public static DataSource immediateFailedDataSource(Throwable paramThrowable)
  {
    SimpleDataSource localSimpleDataSource = SimpleDataSource.create();
    localSimpleDataSource.setFailure(paramThrowable);
    return localSimpleDataSource;
  }
  
  public static Object waitForFinalResult(DataSource paramDataSource)
    throws Throwable
  {
    final CountDownLatch localCountDownLatch = new CountDownLatch(1);
    ValueHolder localValueHolder1 = new ValueHolder(null);
    final ValueHolder localValueHolder2 = new ValueHolder(null);
    paramDataSource.subscribe(new DataSubscriber()new Executor
    {
      public void onCancellation(DataSource paramAnonymousDataSource)
      {
        localCountDownLatch.countDown();
      }
      
      public void onFailure(DataSource paramAnonymousDataSource)
      {
        try
        {
          localValueHolder2value = paramAnonymousDataSource.getFailureCause();
          localCountDownLatch.countDown();
          return;
        }
        catch (Throwable paramAnonymousDataSource)
        {
          localCountDownLatch.countDown();
          throw paramAnonymousDataSource;
        }
      }
      
      public void onNewResult(DataSource paramAnonymousDataSource)
      {
        if (!paramAnonymousDataSource.isFinished()) {
          return;
        }
        try
        {
          val$resultHolder.value = paramAnonymousDataSource.getResult();
          localCountDownLatch.countDown();
          return;
        }
        catch (Throwable paramAnonymousDataSource)
        {
          localCountDownLatch.countDown();
          throw paramAnonymousDataSource;
        }
      }
      
      public void onProgressUpdate(DataSource paramAnonymousDataSource) {}
    }, new Executor()
    {
      public void execute(Runnable paramAnonymousRunnable)
      {
        paramAnonymousRunnable.run();
      }
    });
    localCountDownLatch.await();
    if (value == null) {
      return value;
    }
    throw ((Throwable)value);
  }
  
  private static class ValueHolder<T>
  {
    @Nullable
    public T value = null;
    
    private ValueHolder() {}
  }
}
