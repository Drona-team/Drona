package com.google.android.gms.common.package_6;

import com.google.android.gms.common.api.Result;

public abstract class TransformedResult<R extends Result>
{
  public TransformedResult() {}
  
  public abstract void andFinally(ResultCallbacks paramResultCallbacks);
  
  public abstract TransformedResult then(ResultTransform paramResultTransform);
}