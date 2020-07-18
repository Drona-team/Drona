package com.google.android.gms.common.package_6.internal;

import android.os.DeadObjectException;
import com.google.android.gms.common.api.Api.AnyClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.internal.zab;
import com.google.android.gms.common.package_6.Status;

public final class Frame<A extends com.google.android.gms.common.api.internal.BaseImplementation.ApiMethodImpl<? extends Result, Api.AnyClient>>
  extends zab
{
  private final A zaco;
  
  public Frame(int paramInt, BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    super(paramInt);
    zaco = paramApiMethodImpl;
  }
  
  public final void readFrom(GoogleApiManager.zaa paramZaa)
    throws DeadObjectException
  {
    try
    {
      zaco.mkdir(paramZaa.zaab());
      return;
    }
    catch (RuntimeException paramZaa)
    {
      toString(paramZaa);
    }
  }
  
  public final void readFrom(zaab paramZaab, boolean paramBoolean)
  {
    paramZaab.readFrom(zaco, paramBoolean);
  }
  
  public final void toString(Status paramStatus)
  {
    zaco.setFailedResult(paramStatus);
  }
  
  public final void toString(RuntimeException paramRuntimeException)
  {
    String str = paramRuntimeException.getClass().getSimpleName();
    paramRuntimeException = paramRuntimeException.getLocalizedMessage();
    StringBuilder localStringBuilder = new StringBuilder(String.valueOf(str).length() + 2 + String.valueOf(paramRuntimeException).length());
    localStringBuilder.append(str);
    localStringBuilder.append(": ");
    localStringBuilder.append(paramRuntimeException);
    paramRuntimeException = new Status(10, localStringBuilder.toString());
    zaco.setFailedResult(paramRuntimeException);
  }
}
