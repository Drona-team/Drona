package com.google.android.gms.common.package_6;

import com.google.android.gms.common.package_6.internal.zacd;

public abstract class ResultTransform<R extends com.google.android.gms.common.api.Result, S extends com.google.android.gms.common.api.Result>
{
  public ResultTransform() {}
  
  public final PendingResult createFailedResult(Status paramStatus)
  {
    return new zacd(paramStatus);
  }
  
  public Status onFailure(Status paramStatus)
  {
    return paramStatus;
  }
  
  public abstract PendingResult onSuccess(Result paramResult);
}
