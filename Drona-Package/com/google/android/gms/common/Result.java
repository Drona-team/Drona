package com.google.android.gms.common;

import java.util.concurrent.Callable;

final class Result
  extends Location
{
  private final Callable<String> zzaf;
  
  private Result(Callable paramCallable)
  {
    super(false, null, null);
    zzaf = paramCallable;
  }
  
  final String getErrorMessage()
  {
    Object localObject = zzaf;
    try
    {
      localObject = ((Callable)localObject).call();
      return (String)localObject;
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }
}
