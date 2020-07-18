package androidx.room;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.Nullable;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

class MultiInstanceInvalidationClient
{
  final IMultiInstanceInvalidationCallback mCallback = new IMultiInstanceInvalidationCallback.Stub()
  {
    public void onInvalidation(final String[] paramAnonymousArrayOfString)
    {
      mExecutor.execute(new Runnable()
      {
        public void run()
        {
          mInvalidationTracker.notifyObserversByTableNames(paramAnonymousArrayOfString);
        }
      });
    }
  };
  int mClientId;
  @Nullable
  Context mContext;
  final Executor mExecutor;
  final InvalidationTracker mInvalidationTracker;
  final String mName;
  final InvalidationTracker.Observer mObserver;
  final Runnable mRemoveObserverRunnable = new Runnable()
  {
    public void run()
    {
      mInvalidationTracker.removeObserver(mObserver);
    }
  };
  @Nullable
  IMultiInstanceInvalidationService mService;
  final ServiceConnection mServiceConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      mService = IMultiInstanceInvalidationService.Stub.asInterface(paramAnonymousIBinder);
      mExecutor.execute(mSetUpRunnable);
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      mExecutor.execute(mRemoveObserverRunnable);
      mService = null;
      mContext = null;
    }
  };
  final Runnable mSetUpRunnable = new Runnable()
  {
    public void run()
    {
      Object localObject1 = mService;
      if (localObject1 != null)
      {
        Object localObject2 = MultiInstanceInvalidationClient.this;
        IMultiInstanceInvalidationCallback localIMultiInstanceInvalidationCallback = mCallback;
        String str = mName;
        try
        {
          int i = ((IMultiInstanceInvalidationService)localObject1).registerCallback(localIMultiInstanceInvalidationCallback, str);
          mClientId = i;
          localObject1 = mInvalidationTracker;
          localObject2 = mObserver;
          ((InvalidationTracker)localObject1).addObserver((InvalidationTracker.Observer)localObject2);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          Log.w("ROOM", "Cannot register multi-instance invalidation callback", localRemoteException);
        }
      }
    }
  };
  final AtomicBoolean mStopped = new AtomicBoolean(false);
  private final Runnable mTearDownRunnable = new Runnable()
  {
    public void run()
    {
      mInvalidationTracker.removeObserver(mObserver);
      IMultiInstanceInvalidationService localIMultiInstanceInvalidationService = mService;
      if (localIMultiInstanceInvalidationService != null)
      {
        IMultiInstanceInvalidationCallback localIMultiInstanceInvalidationCallback = mCallback;
        int i = mClientId;
        try
        {
          localIMultiInstanceInvalidationService.unregisterCallback(localIMultiInstanceInvalidationCallback, i);
        }
        catch (RemoteException localRemoteException)
        {
          Log.w("ROOM", "Cannot unregister multi-instance invalidation callback", localRemoteException);
        }
      }
      if (mContext != null)
      {
        mContext.unbindService(mServiceConnection);
        mContext = null;
      }
    }
  };
  
  MultiInstanceInvalidationClient(Context paramContext, String paramString, InvalidationTracker paramInvalidationTracker, Executor paramExecutor)
  {
    mContext = paramContext.getApplicationContext();
    mName = paramString;
    mInvalidationTracker = paramInvalidationTracker;
    mExecutor = paramExecutor;
    mObserver = new InvalidationTracker.Observer(mTableNames)
    {
      boolean isRemote()
      {
        return true;
      }
      
      public void onInvalidated(Set paramAnonymousSet)
      {
        if (mStopped.get()) {
          return;
        }
        IMultiInstanceInvalidationService localIMultiInstanceInvalidationService = mService;
        int i = mClientId;
        try
        {
          paramAnonymousSet = paramAnonymousSet.toArray(new String[0]);
          paramAnonymousSet = (String[])paramAnonymousSet;
          localIMultiInstanceInvalidationService.broadcastInvalidation(i, paramAnonymousSet);
          return;
        }
        catch (RemoteException paramAnonymousSet)
        {
          Log.w("ROOM", "Cannot broadcast invalidation", paramAnonymousSet);
        }
      }
    };
    paramContext = new Intent(mContext, MultiInstanceInvalidationService.class);
    mContext.bindService(paramContext, mServiceConnection, 1);
  }
  
  void stop()
  {
    if (mStopped.compareAndSet(false, true)) {
      mExecutor.execute(mTearDownRunnable);
    }
  }
}
