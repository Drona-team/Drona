package com.google.android.gms.common.package_6.internal;

import android.os.RemoteException;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.tasks.TaskCompletionSource;

@KeepForSdk
public abstract class RegisterListenerMethod<A extends com.google.android.gms.common.api.Api.AnyClient, L>
{
  private final com.google.android.gms.common.api.internal.ListenerHolder<L> zaju;
  private final Feature[] zajv;
  private final boolean zajw;
  
  protected RegisterListenerMethod(ListenerHolder paramListenerHolder)
  {
    zaju = paramListenerHolder;
    zajv = null;
    zajw = false;
  }
  
  protected RegisterListenerMethod(ListenerHolder paramListenerHolder, Feature[] paramArrayOfFeature, boolean paramBoolean)
  {
    zaju = paramListenerHolder;
    zajv = paramArrayOfFeature;
    zajw = paramBoolean;
  }
  
  public void clearListener()
  {
    zaju.clear();
  }
  
  public ListenerHolder.ListenerKey getListenerKey()
  {
    return zaju.getListenerKey();
  }
  
  public Feature[] getRequiredFeatures()
  {
    return zajv;
  }
  
  protected abstract void registerListener(com.google.android.gms.common.package_6.Api.AnyClient paramAnyClient, TaskCompletionSource paramTaskCompletionSource)
    throws RemoteException;
  
  public final boolean shouldAutoResolveMissingFeatures()
  {
    return zajw;
  }
}
