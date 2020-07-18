package com.google.android.gms.common.package_6.internal;

import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.package_6.ApiException;
import com.google.android.gms.common.package_6.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

@KeepForSdk
public class TaskUtil
{
  public TaskUtil() {}
  
  public static void setResultOrApiException(Status paramStatus, TaskCompletionSource paramTaskCompletionSource)
  {
    setResultOrApiException(paramStatus, null, paramTaskCompletionSource);
  }
  
  public static void setResultOrApiException(Status paramStatus, Object paramObject, TaskCompletionSource paramTaskCompletionSource)
  {
    if (paramStatus.isSuccess())
    {
      paramTaskCompletionSource.setResult(paramObject);
      return;
    }
    paramTaskCompletionSource.setException(new ApiException(paramStatus));
  }
  
  public static Task toVoidTaskThatFailsOnFalse(Task paramTask)
  {
    return paramTask.continueWith(new zacl());
  }
}
