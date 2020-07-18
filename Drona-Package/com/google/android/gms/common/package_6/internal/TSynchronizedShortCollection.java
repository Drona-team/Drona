package com.google.android.gms.common.package_6.internal;

import android.os.RemoteException;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.api.Api.AnyClient;
import com.google.android.gms.common.api.internal.UnregisterListenerMethod;
import com.google.android.gms.common.api.internal.zad;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Map;

public final class TSynchronizedShortCollection
  extends zad<Void>
{
  private final com.google.android.gms.common.api.internal.RegisterListenerMethod<Api.AnyClient, ?> zacp;
  private final UnregisterListenerMethod<Api.AnyClient, ?> zacq;
  
  public TSynchronizedShortCollection(zabw paramZabw, TaskCompletionSource paramTaskCompletionSource)
  {
    super(3, paramTaskCompletionSource);
    zacp = zajx;
    zacq = zajy;
  }
  
  public final void forEach(GoogleApiManager.zaa paramZaa)
    throws RemoteException
  {
    zacp.registerListener(paramZaa.zaab(), zacn);
    if (zacp.getListenerKey() != null) {
      paramZaa.zabk().put(zacp.getListenerKey(), new zabw(zacp, zacq));
    }
  }
  
  public final boolean isEmpty(GoogleApiManager.zaa paramZaa)
  {
    return zacp.shouldAutoResolveMissingFeatures();
  }
  
  public final Feature[] toString(GoogleApiManager.zaa paramZaa)
  {
    return zacp.getRequiredFeatures();
  }
}
