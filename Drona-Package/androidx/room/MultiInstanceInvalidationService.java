package androidx.room;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.RestrictTo;
import androidx.collection.SparseArrayCompat;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public class MultiInstanceInvalidationService
  extends Service
{
  private final IMultiInstanceInvalidationService.Stub mBinder = new IMultiInstanceInvalidationService.Stub()
  {
    public void broadcastInvalidation(int paramAnonymousInt, String[] paramAnonymousArrayOfString)
    {
      RemoteCallbackList localRemoteCallbackList = mCallbackList;
      try
      {
        String str = (String)mClientNames.get(paramAnonymousInt);
        if (str == null)
        {
          Log.w("ROOM", "Remote invalidation client ID not registered");
          return;
        }
        int j = mCallbackList.beginBroadcast();
        int i = 0;
        while (i < j) {
          try
          {
            int k = ((Integer)mCallbackList.getBroadcastCookie(i)).intValue();
            Object localObject = (String)mClientNames.get(k);
            if (paramAnonymousInt != k)
            {
              boolean bool = str.equals(localObject);
              if (bool)
              {
                localObject = mCallbackList;
                try
                {
                  localObject = ((RemoteCallbackList)localObject).getBroadcastItem(i);
                  localObject = (IMultiInstanceInvalidationCallback)localObject;
                  ((IMultiInstanceInvalidationCallback)localObject).onInvalidation(paramAnonymousArrayOfString);
                }
                catch (RemoteException localRemoteException)
                {
                  Log.w("ROOM", "Error invoking a remote callback", localRemoteException);
                }
              }
            }
            i += 1;
          }
          catch (Throwable paramAnonymousArrayOfString)
          {
            mCallbackList.finishBroadcast();
            throw paramAnonymousArrayOfString;
          }
        }
        mCallbackList.finishBroadcast();
        return;
      }
      catch (Throwable paramAnonymousArrayOfString)
      {
        throw paramAnonymousArrayOfString;
      }
    }
    
    public int registerCallback(IMultiInstanceInvalidationCallback paramAnonymousIMultiInstanceInvalidationCallback, String paramAnonymousString)
    {
      if (paramAnonymousString == null) {
        return 0;
      }
      RemoteCallbackList localRemoteCallbackList = mCallbackList;
      try
      {
        MultiInstanceInvalidationService localMultiInstanceInvalidationService = MultiInstanceInvalidationService.this;
        int i = mMaxClientId + 1;
        mMaxClientId = i;
        if (mCallbackList.register(paramAnonymousIMultiInstanceInvalidationCallback, Integer.valueOf(i)))
        {
          mClientNames.append(i, paramAnonymousString);
          return i;
        }
        paramAnonymousIMultiInstanceInvalidationCallback = MultiInstanceInvalidationService.this;
        mMaxClientId -= 1;
        return 0;
      }
      catch (Throwable paramAnonymousIMultiInstanceInvalidationCallback)
      {
        throw paramAnonymousIMultiInstanceInvalidationCallback;
      }
    }
    
    public void unregisterCallback(IMultiInstanceInvalidationCallback paramAnonymousIMultiInstanceInvalidationCallback, int paramAnonymousInt)
    {
      RemoteCallbackList localRemoteCallbackList = mCallbackList;
      try
      {
        mCallbackList.unregister(paramAnonymousIMultiInstanceInvalidationCallback);
        mClientNames.remove(paramAnonymousInt);
        return;
      }
      catch (Throwable paramAnonymousIMultiInstanceInvalidationCallback)
      {
        throw paramAnonymousIMultiInstanceInvalidationCallback;
      }
    }
  };
  final RemoteCallbackList<IMultiInstanceInvalidationCallback> mCallbackList = new RemoteCallbackList()
  {
    public void onCallbackDied(IMultiInstanceInvalidationCallback paramAnonymousIMultiInstanceInvalidationCallback, Object paramAnonymousObject)
    {
      mClientNames.remove(((Integer)paramAnonymousObject).intValue());
    }
  };
  final SparseArrayCompat<String> mClientNames = new SparseArrayCompat();
  int mMaxClientId = 0;
  
  public MultiInstanceInvalidationService() {}
  
  public IBinder onBind(Intent paramIntent)
  {
    return mBinder;
  }
}
