package androidx.room;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;
import androidx.lifecycle.LiveData;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

class RoomTrackingLiveData<T>
  extends LiveData<T>
{
  final Callable<T> mComputeFunction;
  final AtomicBoolean mComputing = new AtomicBoolean(false);
  private final InvalidationLiveDataContainer mContainer;
  final RoomDatabase mDatabase;
  final boolean mInTransaction;
  final AtomicBoolean mInvalid = new AtomicBoolean(true);
  final Runnable mInvalidationRunnable = new Runnable()
  {
    public void run()
    {
      boolean bool = hasActiveObservers();
      if ((mInvalid.compareAndSet(false, true)) && (bool)) {
        getQueryExecutor().execute(mRefreshRunnable);
      }
    }
  };
  final InvalidationTracker.Observer mObserver;
  final Runnable mRefreshRunnable = new Runnable()
  {
    public void run()
    {
      if (mRegisteredObserver.compareAndSet(false, true)) {
        mDatabase.getInvalidationTracker().addWeakObserver(mObserver);
      }
      int i;
      do
      {
        if (mComputing.compareAndSet(false, true))
        {
          Object localObject = null;
          i = 0;
          try
          {
            for (;;)
            {
              boolean bool = mInvalid.compareAndSet(true, false);
              if (bool)
              {
                localObject = mComputeFunction;
                try
                {
                  localObject = ((Callable)localObject).call();
                  i = 1;
                }
                catch (Exception localException)
                {
                  throw new RuntimeException("Exception while computing database live data.", localException);
                }
              }
            }
            if (i != 0) {
              postValue(localException);
            }
            mComputing.set(false);
          }
          catch (Throwable localThrowable)
          {
            mComputing.set(false);
            throw localThrowable;
          }
        }
        i = 0;
      } while ((i != 0) && (mInvalid.get()));
    }
  };
  final AtomicBoolean mRegisteredObserver = new AtomicBoolean(false);
  
  RoomTrackingLiveData(RoomDatabase paramRoomDatabase, InvalidationLiveDataContainer paramInvalidationLiveDataContainer, boolean paramBoolean, Callable paramCallable, String[] paramArrayOfString)
  {
    mDatabase = paramRoomDatabase;
    mInTransaction = paramBoolean;
    mComputeFunction = paramCallable;
    mContainer = paramInvalidationLiveDataContainer;
    mObserver = new InvalidationTracker.Observer(paramArrayOfString)
    {
      public void onInvalidated(Set paramAnonymousSet)
      {
        ArchTaskExecutor.getInstance().executeOnMainThread(mInvalidationRunnable);
      }
    };
  }
  
  Executor getQueryExecutor()
  {
    if (mInTransaction) {
      return mDatabase.getTransactionExecutor();
    }
    return mDatabase.getQueryExecutor();
  }
  
  protected void onActive()
  {
    super.onActive();
    mContainer.onActive(this);
    getQueryExecutor().execute(mRefreshRunnable);
  }
  
  protected void onInactive()
  {
    super.onInactive();
    mContainer.onInactive(this);
  }
}
