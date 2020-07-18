package com.google.android.gms.common.package_6.internal;

import android.os.Bundle;
import android.os.DeadObjectException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.SimpleClientAdapter;
import com.google.android.gms.common.package_6.Api.AnyClient;
import com.google.android.gms.common.package_6.Api.Client;
import com.google.android.gms.common.package_6.Sample;
import com.google.android.gms.common.package_6.Status;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class zaah
  implements zabd
{
  private final zabe zaft;
  private boolean zafu = false;
  
  public zaah(zabe paramZabe)
  {
    zaft = paramZabe;
  }
  
  public final void begin() {}
  
  public final void connect()
  {
    if (zafu)
    {
      zafu = false;
      zaft.enqueue(new zaaj(this, this));
    }
  }
  
  public final boolean disconnect()
  {
    if (zafu) {
      return false;
    }
    if (zaft.zaee.zaax())
    {
      zafu = true;
      Iterator localIterator = zaft.zaee.zahe.iterator();
      while (localIterator.hasNext()) {
        ((zacm)localIterator.next()).zabv();
      }
      return false;
    }
    zaft.wakeup(null);
    return true;
  }
  
  public final BaseImplementation.ApiMethodImpl enqueue(BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    return execute(paramApiMethodImpl);
  }
  
  public final BaseImplementation.ApiMethodImpl execute(BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    Object localObject1 = zaft.zaee.zahf;
    try
    {
      ((zacp)localObject1).close(paramApiMethodImpl);
      Object localObject2 = zaft.zaee;
      localObject1 = paramApiMethodImpl.getClientKey();
      localObject2 = zagz;
      localObject1 = ((Map)localObject2).get(localObject1);
      localObject2 = (Api.Client)localObject1;
      Preconditions.checkNotNull(localObject2, "Appropriate Api was not requested.");
      localObject1 = (Api.Client)localObject2;
      boolean bool = ((Api.Client)localObject1).isConnected();
      if (!bool)
      {
        localObject1 = zaft.zahp;
        bool = ((Map)localObject1).containsKey(paramApiMethodImpl.getClientKey());
        if (bool)
        {
          paramApiMethodImpl.setFailedResult(new Status(17));
          return paramApiMethodImpl;
        }
      }
      localObject1 = localObject2;
      if ((localObject2 instanceof SimpleClientAdapter))
      {
        localObject1 = (SimpleClientAdapter)localObject2;
        localObject1 = ((SimpleClientAdapter)localObject1).getClient();
      }
      paramApiMethodImpl.mkdir((Api.AnyClient)localObject1);
      return paramApiMethodImpl;
    }
    catch (DeadObjectException localDeadObjectException)
    {
      for (;;) {}
    }
    zaft.enqueue(new zaai(this, this));
    return paramApiMethodImpl;
  }
  
  public final void onConnected(Bundle paramBundle) {}
  
  public final void onConnectionSuspended(int paramInt)
  {
    zaft.wakeup(null);
    zaft.zaht.removeAccount(paramInt, zafu);
  }
  
  public final void showProgress(ConnectionResult paramConnectionResult, Sample paramSample, boolean paramBoolean) {}
  
  final void zaam()
  {
    if (zafu)
    {
      zafu = false;
      zaft.zaee.zahf.release();
      disconnect();
    }
  }
}
