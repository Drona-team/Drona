package com.google.android.gms.common.internal;

import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.package_6.ApiException;
import com.google.android.gms.common.package_6.PendingResult;
import com.google.android.gms.common.package_6.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

@KeepForSdk
public class PendingResultUtil
{
  private static final zaa zaou = new Response();
  
  public PendingResultUtil() {}
  
  public static Task toResponseTask(PendingResult paramPendingResult, com.google.android.gms.common.package_6.Response paramResponse)
  {
    return toTask(paramPendingResult, new Converter(paramResponse));
  }
  
  public static Task toTask(PendingResult paramPendingResult, ResultConverter paramResultConverter)
  {
    zaa localZaa = zaou;
    TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    paramPendingResult.addStatusListener(new LoginActivity.1(paramPendingResult, localTaskCompletionSource, paramResultConverter, localZaa));
    return localTaskCompletionSource.getTask();
  }
  
  public static Task toVoidTask(PendingResult paramPendingResult)
  {
    return toTask(paramPendingResult, new TypeConverter());
  }
  
  @KeepForSdk
  public static abstract interface ResultConverter<R extends com.google.android.gms.common.api.Result, T>
  {
    public abstract Object convert(com.google.android.gms.common.package_6.Result paramResult);
  }
  
  public static abstract interface zaa
  {
    public abstract ApiException getException(Status paramStatus);
  }
}
