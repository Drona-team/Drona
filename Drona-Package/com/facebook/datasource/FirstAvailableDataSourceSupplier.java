package com.facebook.datasource;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.internal.Objects;
import com.facebook.common.internal.Objects.ToStringHelper;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Supplier;
import java.util.List;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class FirstAvailableDataSourceSupplier<T>
  implements Supplier<DataSource<T>>
{
  private final List<Supplier<DataSource<T>>> mDataSourceSuppliers;
  
  private FirstAvailableDataSourceSupplier(List paramList)
  {
    Preconditions.checkArgument(paramList.isEmpty() ^ true, "List of suppliers is empty!");
    mDataSourceSuppliers = paramList;
  }
  
  public static FirstAvailableDataSourceSupplier create(List paramList)
  {
    return new FirstAvailableDataSourceSupplier(paramList);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof FirstAvailableDataSourceSupplier)) {
      return false;
    }
    paramObject = (FirstAvailableDataSourceSupplier)paramObject;
    return Objects.equal(mDataSourceSuppliers, mDataSourceSuppliers);
  }
  
  public DataSource getFolder()
  {
    return new FirstAvailableDataSource();
  }
  
  public int hashCode()
  {
    return mDataSourceSuppliers.hashCode();
  }
  
  public String toString()
  {
    return Objects.toStringHelper(this).addValue("list", mDataSourceSuppliers).toString();
  }
  
  @ThreadSafe
  private class FirstAvailableDataSource
    extends AbstractDataSource<T>
  {
    private DataSource<T> mCurrentDataSource = null;
    private DataSource<T> mDataSourceWithResult = null;
    private int mIndex = 0;
    
    public FirstAvailableDataSource()
    {
      if (!startNextDataSource()) {
        setFailure(new RuntimeException("No data source supplier or supplier returned null."));
      }
    }
    
    private boolean clearCurrentDataSource(DataSource paramDataSource)
    {
      try
      {
        if ((!isClosed()) && (paramDataSource == mCurrentDataSource))
        {
          mCurrentDataSource = null;
          return true;
        }
        return false;
      }
      catch (Throwable paramDataSource)
      {
        throw paramDataSource;
      }
    }
    
    private void closeSafely(DataSource paramDataSource)
    {
      if (paramDataSource != null) {
        paramDataSource.close();
      }
    }
    
    private DataSource getDataSourceWithResult()
    {
      try
      {
        DataSource localDataSource = mDataSourceWithResult;
        return localDataSource;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    private Supplier getNextSupplier()
    {
      try
      {
        if ((!isClosed()) && (mIndex < mDataSourceSuppliers.size()))
        {
          Object localObject = mDataSourceSuppliers;
          int i = mIndex;
          mIndex = (i + 1);
          localObject = (Supplier)((List)localObject).get(i);
          return localObject;
        }
        return null;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    private void maybeSetDataSourceWithResult(DataSource paramDataSource, boolean paramBoolean)
    {
      for (;;)
      {
        try
        {
          if ((paramDataSource == mCurrentDataSource) && (paramDataSource != mDataSourceWithResult))
          {
            if (mDataSourceWithResult != null) {
              if (!paramBoolean) {
                break label63;
              }
            }
            DataSource localDataSource = mDataSourceWithResult;
            mDataSourceWithResult = paramDataSource;
            paramDataSource = localDataSource;
            closeSafely(paramDataSource);
          }
          else
          {
            return;
          }
        }
        catch (Throwable paramDataSource)
        {
          throw paramDataSource;
        }
        label63:
        paramDataSource = null;
      }
    }
    
    private void onDataSourceFailed(DataSource paramDataSource)
    {
      if (!clearCurrentDataSource(paramDataSource)) {
        return;
      }
      if (paramDataSource != getDataSourceWithResult()) {
        closeSafely(paramDataSource);
      }
      if (!startNextDataSource()) {
        setFailure(paramDataSource.getFailureCause());
      }
    }
    
    private void onDataSourceNewResult(DataSource paramDataSource)
    {
      maybeSetDataSourceWithResult(paramDataSource, paramDataSource.isFinished());
      if (paramDataSource == getDataSourceWithResult()) {
        setResult(null, paramDataSource.isFinished());
      }
    }
    
    private boolean setCurrentDataSource(DataSource paramDataSource)
    {
      try
      {
        boolean bool = isClosed();
        if (bool) {
          return false;
        }
        mCurrentDataSource = paramDataSource;
        return true;
      }
      catch (Throwable paramDataSource)
      {
        throw paramDataSource;
      }
    }
    
    private boolean startNextDataSource()
    {
      Object localObject = getNextSupplier();
      if (localObject != null) {
        localObject = (DataSource)((Supplier)localObject).getFolder();
      } else {
        localObject = null;
      }
      if ((setCurrentDataSource((DataSource)localObject)) && (localObject != null))
      {
        ((DataSource)localObject).subscribe(new InternalDataSubscriber(null), CallerThreadExecutor.getInstance());
        return true;
      }
      closeSafely((DataSource)localObject);
      return false;
    }
    
    public boolean close()
    {
      try
      {
        if (!super.close()) {
          return false;
        }
        DataSource localDataSource1 = mCurrentDataSource;
        mCurrentDataSource = null;
        DataSource localDataSource2 = mDataSourceWithResult;
        mDataSourceWithResult = null;
        closeSafely(localDataSource2);
        closeSafely(localDataSource1);
        return true;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    public Object getResult()
    {
      try
      {
        Object localObject = getDataSourceWithResult();
        if (localObject != null) {
          localObject = ((DataSource)localObject).getResult();
        } else {
          localObject = null;
        }
        return localObject;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    public boolean hasResult()
    {
      try
      {
        DataSource localDataSource = getDataSourceWithResult();
        if (localDataSource != null)
        {
          bool = localDataSource.hasResult();
          if (bool)
          {
            bool = true;
            break label29;
          }
        }
        boolean bool = false;
        label29:
        return bool;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    private class InternalDataSubscriber
      implements DataSubscriber<T>
    {
      private InternalDataSubscriber() {}
      
      public void onCancellation(DataSource paramDataSource) {}
      
      public void onFailure(DataSource paramDataSource)
      {
        FirstAvailableDataSourceSupplier.FirstAvailableDataSource.this.onDataSourceFailed(paramDataSource);
      }
      
      public void onNewResult(DataSource paramDataSource)
      {
        if (paramDataSource.hasResult())
        {
          FirstAvailableDataSourceSupplier.FirstAvailableDataSource.this.onDataSourceNewResult(paramDataSource);
          return;
        }
        if (paramDataSource.isFinished()) {
          FirstAvailableDataSourceSupplier.FirstAvailableDataSource.this.onDataSourceFailed(paramDataSource);
        }
      }
      
      public void onProgressUpdate(DataSource paramDataSource)
      {
        float f = getProgress();
        setProgress(Math.max(f, paramDataSource.getProgress()));
      }
    }
  }
}
