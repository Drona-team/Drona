package com.google.android.gms.common.package_6;

import com.google.android.gms.common.api.PendingResult;

public abstract class OptionalPendingResult<R extends com.google.android.gms.common.api.Result>
  extends PendingResult<R>
{
  public OptionalPendingResult() {}
  
  public abstract Result getResponses();
  
  public abstract boolean isDone();
}