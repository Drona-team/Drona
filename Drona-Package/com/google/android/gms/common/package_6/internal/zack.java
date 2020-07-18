package com.google.android.gms.common.package_6.internal;

import android.os.RemoteException;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.package_6.Api.AnyClient;
import com.google.android.gms.tasks.TaskCompletionSource;

final class zack
  extends com.google.android.gms.common.api.internal.TaskApiCall<A, ResultT>
{
  zack(TaskApiCall.Builder paramBuilder, Feature[] paramArrayOfFeature, boolean paramBoolean)
  {
    super(paramArrayOfFeature, paramBoolean, null);
  }
  
  protected final void doExecute(Api.AnyClient paramAnyClient, TaskCompletionSource paramTaskCompletionSource)
    throws RemoteException
  {
    TaskApiCall.Builder.getExecutor(zakn).accept(paramAnyClient, paramTaskCompletionSource);
  }
}
