package com.google.android.gms.common.package_6.internal;

import android.os.RemoteException;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.package_6.Api.AnyClient;
import com.google.android.gms.tasks.TaskCompletionSource;

final class zaca
  extends com.google.android.gms.common.api.internal.RegisterListenerMethod<A, L>
{
  zaca(RegistrationMethods.Builder paramBuilder, ListenerHolder paramListenerHolder, Feature[] paramArrayOfFeature, boolean paramBoolean)
  {
    super(paramListenerHolder, paramArrayOfFeature, paramBoolean);
  }
  
  protected final void registerListener(Api.AnyClient paramAnyClient, TaskCompletionSource paramTaskCompletionSource)
    throws RemoteException
  {
    RegistrationMethods.Builder.elementAt(zakh).accept(paramAnyClient, paramTaskCompletionSource);
  }
}
