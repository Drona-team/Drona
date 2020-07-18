package com.google.android.gms.common.internal;

import com.google.android.gms.common.package_6.ApiException;
import com.google.android.gms.common.package_6.Status;

final class Response
  implements PendingResultUtil.zaa
{
  Response() {}
  
  public final ApiException getException(Status paramStatus)
  {
    return ApiExceptionUtil.fromStatus(paramStatus);
  }
}
