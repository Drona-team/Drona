package com.google.android.gms.common.package_6.internal;

import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api.AnyClientKey;
import com.google.android.gms.common.package_6.PendingResult;
import com.google.android.gms.common.package_6.Status;
import com.google.android.gms.common.util.VisibleForTesting;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class zacp
{
  public static final Status zakx = new Status(8, "The connection to Google Play services was lost");
  private static final com.google.android.gms.common.api.internal.BasePendingResult<?>[] zaky = new BasePendingResult[0];
  private final Map<Api.AnyClientKey<?>, com.google.android.gms.common.api.Api.Client> zagz;
  @VisibleForTesting
  final Set<com.google.android.gms.common.api.internal.BasePendingResult<?>> zakz = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
  private final zacs zala = new zacq(this);
  
  public zacp(Map paramMap)
  {
    zagz = paramMap;
  }
  
  final void close(BasePendingResult paramBasePendingResult)
  {
    zakz.add(paramBasePendingResult);
    paramBasePendingResult.remove(zala);
  }
  
  public final void release()
  {
    BasePendingResult[] arrayOfBasePendingResult = (BasePendingResult[])zakz.toArray(zaky);
    int j = arrayOfBasePendingResult.length;
    int i = 0;
    while (i < j)
    {
      BasePendingResult localBasePendingResult = arrayOfBasePendingResult[i];
      localBasePendingResult.remove(null);
      IBinder localIBinder;
      zacr localZacr;
      if (localBasePendingResult.getValue() == null)
      {
        if (localBasePendingResult.compareAndSet()) {
          zakz.remove(localBasePendingResult);
        }
      }
      else
      {
        localBasePendingResult.setResultCallback(null);
        localIBinder = ((com.google.android.gms.common.package_6.Api.Client)zagz.get(((BaseImplementation.ApiMethodImpl)localBasePendingResult).getClientKey())).getServiceBrokerBinder();
        if (localBasePendingResult.isReady())
        {
          localBasePendingResult.remove(new zacr(localBasePendingResult, null, localIBinder, null));
        }
        else if ((localIBinder != null) && (localIBinder.isBinderAlive()))
        {
          localZacr = new zacr(localBasePendingResult, null, localIBinder, null);
          localBasePendingResult.remove(localZacr);
        }
      }
      try
      {
        localIBinder.linkToDeath(localZacr, 0);
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
      localBasePendingResult.cancel();
      localBasePendingResult.getValue().intValue();
      throw new NullPointerException("This statement would have triggered an Exception: virtualinvoke $u5#8.<com.google.android.gms.common.api.zac: void remove(int)>($u-1#32)");
      localBasePendingResult.remove(null);
      localBasePendingResult.cancel();
      localBasePendingResult.getValue().intValue();
      throw new NullPointerException("This statement would have triggered an Exception: virtualinvoke $u5#8.<com.google.android.gms.common.api.zac: void remove(int)>($u-1#36)");
      zakz.remove(localBasePendingResult);
      i += 1;
    }
  }
  
  public final void zabx()
  {
    BasePendingResult[] arrayOfBasePendingResult = (BasePendingResult[])zakz.toArray(zaky);
    int j = arrayOfBasePendingResult.length;
    int i = 0;
    while (i < j)
    {
      arrayOfBasePendingResult[i].await(zakx);
      i += 1;
    }
  }
}
