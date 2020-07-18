package androidx.lifecycle;

import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public abstract class ComputableLiveData<T>
{
  final AtomicBoolean mComputing = new AtomicBoolean(false);
  final Executor mExecutor;
  final AtomicBoolean mInvalid = new AtomicBoolean(true);
  @VisibleForTesting
  final Runnable mInvalidationRunnable = new Runnable()
  {
    public void run()
    {
      boolean bool = mLiveData.hasActiveObservers();
      if ((mInvalid.compareAndSet(false, true)) && (bool)) {
        mExecutor.execute(mRefreshRunnable);
      }
    }
  };
  final LiveData<T> mLiveData;
  @VisibleForTesting
  final Runnable mRefreshRunnable = new Runnable()
  {
    public void run()
    {
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
              if (!bool) {
                break;
              }
              localObject = compute();
              i = 1;
            }
            if (i != 0) {
              mLiveData.postValue(localObject);
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
  
  public ComputableLiveData()
  {
    this(ArchTaskExecutor.getIOThreadExecutor());
  }
  
  public ComputableLiveData(Executor paramExecutor)
  {
    mExecutor = paramExecutor;
    mLiveData = new LiveData()
    {
      protected void onActive()
      {
        mExecutor.execute(mRefreshRunnable);
      }
    };
  }
  
  protected abstract Object compute();
  
  public LiveData getLiveData()
  {
    return mLiveData;
  }
  
  public void invalidate()
  {
    ArchTaskExecutor.getInstance().executeOnMainThread(mInvalidationRunnable);
  }
}
