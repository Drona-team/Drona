package com.google.android.gms.common.package_6.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import com.google.android.gms.common.api.internal.zac;
import com.google.android.gms.common.package_6.ApiException;
import com.google.android.gms.common.package_6.Status;
import com.google.android.gms.tasks.TaskCompletionSource;

abstract class AbstractBuffer<T>
  extends zac
{
  protected final TaskCompletionSource<T> zacn;
  
  public AbstractBuffer(int paramInt, TaskCompletionSource paramTaskCompletionSource)
  {
    super(paramInt);
    zacn = paramTaskCompletionSource;
  }
  
  protected abstract void forEach(GoogleApiManager.zaa paramZaa)
    throws RemoteException;
  
  public final void readFrom(GoogleApiManager.zaa paramZaa)
    throws DeadObjectException
  {
    try
    {
      forEach(paramZaa);
      return;
    }
    catch (RuntimeException paramZaa)
    {
      toString(paramZaa);
      return;
    }
    catch (RemoteException paramZaa)
    {
      toString(Location.call(paramZaa));
      return;
    }
    catch (DeadObjectException paramZaa)
    {
      toString(Location.call(paramZaa));
      throw paramZaa;
    }
  }
  
  public void readFrom(zaab paramZaab, boolean paramBoolean) {}
  
  public void toString(Status paramStatus)
  {
    zacn.trySetException(new ApiException(paramStatus));
  }
  
  public void toString(RuntimeException paramRuntimeException)
  {
    zacn.trySetException(paramRuntimeException);
  }
}
