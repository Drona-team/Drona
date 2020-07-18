package com.google.android.gms.common.package_6.internal;

import android.os.RemoteException;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.tasks.TaskCompletionSource;

@KeepForSdk
public abstract class UnregisterListenerMethod<A extends com.google.android.gms.common.api.Api.AnyClient, L>
{
  private final com.google.android.gms.common.api.internal.ListenerHolder.ListenerKey<L> zajl;
  
  protected UnregisterListenerMethod(ListenerHolder.ListenerKey paramListenerKey)
  {
    zajl = paramListenerKey;
  }
  
  public ListenerHolder.ListenerKey getListenerKey()
  {
    return zajl;
  }
  
  protected abstract void unregisterListener(com.google.android.gms.common.package_6.Api.AnyClient paramAnyClient, TaskCompletionSource paramTaskCompletionSource)
    throws RemoteException;
}
