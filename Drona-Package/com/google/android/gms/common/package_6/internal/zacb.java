package com.google.android.gms.common.package_6.internal;

import android.os.RemoteException;
import com.google.android.gms.common.package_6.Api.AnyClient;
import com.google.android.gms.tasks.TaskCompletionSource;

final class zacb
  extends com.google.android.gms.common.api.internal.UnregisterListenerMethod<A, L>
{
  zacb(RegistrationMethods.Builder paramBuilder, ListenerHolder.ListenerKey paramListenerKey)
  {
    super(paramListenerKey);
  }
  
  protected final void unregisterListener(Api.AnyClient paramAnyClient, TaskCompletionSource paramTaskCompletionSource)
    throws RemoteException
  {
    RegistrationMethods.Builder.removeOnChangeListener(zakh).accept(paramAnyClient, paramTaskCompletionSource);
  }
}
