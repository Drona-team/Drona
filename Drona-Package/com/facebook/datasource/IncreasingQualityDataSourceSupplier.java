package com.facebook.datasource;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.internal.Objects;
import com.facebook.common.internal.Objects.ToStringHelper;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Supplier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class IncreasingQualityDataSourceSupplier<T>
  implements Supplier<DataSource<T>>
{
  private final boolean mDataSourceLazy;
  private final List<Supplier<DataSource<T>>> mDataSourceSuppliers;
  
  private IncreasingQualityDataSourceSupplier(List paramList, boolean paramBoolean)
  {
    Preconditions.checkArgument(paramList.isEmpty() ^ true, "List of suppliers is empty!");
    mDataSourceSuppliers = paramList;
    mDataSourceLazy = paramBoolean;
  }
  
  public static IncreasingQualityDataSourceSupplier create(List paramList)
  {
    return create(paramList, false);
  }
  
  public static IncreasingQualityDataSourceSupplier create(List paramList, boolean paramBoolean)
  {
    return new IncreasingQualityDataSourceSupplier(paramList, paramBoolean);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof IncreasingQualityDataSourceSupplier)) {
      return false;
    }
    paramObject = (IncreasingQualityDataSourceSupplier)paramObject;
    return Objects.equal(mDataSourceSuppliers, mDataSourceSuppliers);
  }
  
  public DataSource getFolder()
  {
    return new IncreasingQualityDataSource();
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
  private class IncreasingQualityDataSource
    extends AbstractDataSource<T>
  {
    @Nullable
    @GuardedBy("IncreasingQualityDataSource.this")
    private ArrayList<DataSource<T>> mDataSources;
    @Nullable
    private Throwable mDelayedError;
    private AtomicInteger mFinishedDataSources;
    @GuardedBy("IncreasingQualityDataSource.this")
    private int mIndexOfDataSourceWithResult;
    private int mNumberOfDataSources;
    
    public IncreasingQualityDataSource()
    {
      if (!mDataSourceLazy) {
        ensureDataSourceInitialized();
      }
    }
    
    private void closeSafely(DataSource paramDataSource)
    {
      if (paramDataSource != null) {
        paramDataSource.close();
      }
    }
    
    private void ensureDataSourceInitialized()
    {
      Object localObject = mFinishedDataSources;
      IncreasingQualityDataSource localIncreasingQualityDataSource = this;
      if (localObject != null) {
        return;
      }
      for (;;)
      {
        int i;
        try
        {
          localObject = mFinishedDataSources;
          if (localObject == null)
          {
            i = 0;
            mFinishedDataSources = new AtomicInteger(0);
            int j = this$0.mDataSourceSuppliers.size();
            mNumberOfDataSources = j;
            mIndexOfDataSourceWithResult = j;
            mDataSources = new ArrayList(j);
            if (i < j)
            {
              localObject = (DataSource)((Supplier)this$0.mDataSourceSuppliers.get(i)).getFolder();
              mDataSources.add(localObject);
              ((DataSource)localObject).subscribe(new InternalDataSubscriber(localIncreasingQualityDataSource, i), CallerThreadExecutor.getInstance());
              if (!((DataSource)localObject).hasResult()) {
                break label157;
              }
            }
          }
          return;
        }
        catch (Throwable localThrowable)
        {
          throw localThrowable;
        }
        label157:
        i += 1;
      }
    }
    
    private DataSource getAndClearDataSource(int paramInt)
    {
      try
      {
        ArrayList localArrayList = mDataSources;
        Object localObject2 = null;
        Object localObject1 = localObject2;
        if (localArrayList != null)
        {
          localObject1 = localObject2;
          if (paramInt < mDataSources.size()) {
            localObject1 = (DataSource)mDataSources.set(paramInt, null);
          }
        }
        return localObject1;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    private DataSource getDataSource(int paramInt)
    {
      try
      {
        DataSource localDataSource;
        if ((mDataSources != null) && (paramInt < mDataSources.size())) {
          localDataSource = (DataSource)mDataSources.get(paramInt);
        } else {
          localDataSource = null;
        }
        return localDataSource;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    private DataSource getDataSourceWithResult()
    {
      try
      {
        DataSource localDataSource = getDataSource(mIndexOfDataSourceWithResult);
        return localDataSource;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    private void maybeSetFailure()
    {
      if ((mFinishedDataSources.incrementAndGet() == mNumberOfDataSources) && (mDelayedError != null)) {
        setFailure(mDelayedError);
      }
    }
    
    private void maybeSetIndexOfDataSourceWithResult(int paramInt, DataSource paramDataSource, boolean paramBoolean)
    {
      for (;;)
      {
        int j;
        try
        {
          int i = mIndexOfDataSourceWithResult;
          j = mIndexOfDataSourceWithResult;
          if ((paramDataSource == getDataSource(paramInt)) && (paramInt != mIndexOfDataSourceWithResult))
          {
            if (getDataSourceWithResult() != null) {
              if ((!paramBoolean) || (paramInt >= mIndexOfDataSourceWithResult)) {
                break label97;
              }
            }
            mIndexOfDataSourceWithResult = paramInt;
            if (i > paramInt)
            {
              closeSafely(getAndClearDataSource(i));
              i -= 1;
              continue;
            }
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
        label97:
        paramInt = j;
      }
    }
    
    private void onDataSourceFailed(int paramInt, DataSource paramDataSource)
    {
      closeSafely(tryGetAndClearDataSource(paramInt, paramDataSource));
      if (paramInt == 0) {
        mDelayedError = paramDataSource.getFailureCause();
      }
      maybeSetFailure();
    }
    
    private void onDataSourceNewResult(int paramInt, DataSource paramDataSource)
    {
      maybeSetIndexOfDataSourceWithResult(paramInt, paramDataSource, paramDataSource.isFinished());
      if (paramDataSource == getDataSourceWithResult())
      {
        boolean bool;
        if ((paramInt == 0) && (paramDataSource.isFinished())) {
          bool = true;
        } else {
          bool = false;
        }
        setResult(null, bool);
      }
      maybeSetFailure();
    }
    
    private DataSource tryGetAndClearDataSource(int paramInt, DataSource paramDataSource)
    {
      try
      {
        DataSource localDataSource = getDataSourceWithResult();
        if (paramDataSource == localDataSource) {
          return null;
        }
        if (paramDataSource == getDataSource(paramInt))
        {
          paramDataSource = getAndClearDataSource(paramInt);
          return paramDataSource;
        }
        return paramDataSource;
      }
      catch (Throwable paramDataSource)
      {
        throw paramDataSource;
      }
    }
    
    public boolean close()
    {
      if (mDataSourceLazy) {
        ensureDataSourceInitialized();
      }
      try
      {
        boolean bool = super.close();
        int i = 0;
        if (!bool) {
          return false;
        }
        ArrayList localArrayList = mDataSources;
        mDataSources = null;
        if (localArrayList != null) {
          while (i < localArrayList.size())
          {
            closeSafely((DataSource)localArrayList.get(i));
            i += 1;
          }
        }
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
        if (mDataSourceLazy) {
          ensureDataSourceInitialized();
        }
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
        if (mDataSourceLazy) {
          ensureDataSourceInitialized();
        }
        DataSource localDataSource = getDataSourceWithResult();
        if (localDataSource != null)
        {
          bool = localDataSource.hasResult();
          if (bool)
          {
            bool = true;
            break label43;
          }
        }
        boolean bool = false;
        label43:
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
      private int mIndex;
      
      public InternalDataSubscriber(int paramInt)
      {
        mIndex = paramInt;
      }
      
      public void onCancellation(DataSource paramDataSource) {}
      
      public void onFailure(DataSource paramDataSource)
      {
        IncreasingQualityDataSourceSupplier.IncreasingQualityDataSource.this.onDataSourceFailed(mIndex, paramDataSource);
      }
      
      public void onNewResult(DataSource paramDataSource)
      {
        if (paramDataSource.hasResult())
        {
          IncreasingQualityDataSourceSupplier.IncreasingQualityDataSource.this.onDataSourceNewResult(mIndex, paramDataSource);
          return;
        }
        if (paramDataSource.isFinished()) {
          IncreasingQualityDataSourceSupplier.IncreasingQualityDataSource.this.onDataSourceFailed(mIndex, paramDataSource);
        }
      }
      
      public void onProgressUpdate(DataSource paramDataSource)
      {
        if (mIndex == 0) {
          setProgress(paramDataSource.getProgress());
        }
      }
    }
  }
}
