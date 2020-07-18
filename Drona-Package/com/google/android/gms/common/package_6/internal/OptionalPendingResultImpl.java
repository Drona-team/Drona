package com.google.android.gms.common.package_6.internal;

import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.package_6.PendingResult;
import com.google.android.gms.common.package_6.PendingResult.StatusListener;
import com.google.android.gms.common.package_6.ResultCallback;
import com.google.android.gms.common.package_6.ResultTransform;
import com.google.android.gms.common.package_6.TransformedResult;
import java.util.concurrent.TimeUnit;

@KeepForSdk
public final class OptionalPendingResultImpl<R extends com.google.android.gms.common.api.Result>
  extends com.google.android.gms.common.api.OptionalPendingResult<R>
{
  private final com.google.android.gms.common.api.internal.BasePendingResult<R> zajq;
  
  public OptionalPendingResultImpl(PendingResult paramPendingResult)
  {
    zajq = ((BasePendingResult)paramPendingResult);
  }
  
  public final void addStatusListener(PendingResult.StatusListener paramStatusListener)
  {
    zajq.addStatusListener(paramStatusListener);
  }
  
  public final com.google.android.gms.common.package_6.Result await()
  {
    return zajq.await();
  }
  
  public final com.google.android.gms.common.package_6.Result await(long paramLong, TimeUnit paramTimeUnit)
  {
    return zajq.await(paramLong, paramTimeUnit);
  }
  
  public final void cancel()
  {
    zajq.cancel();
  }
  
  public final com.google.android.gms.common.package_6.Result getResponses()
  {
    if (isDone()) {
      return await(0L, TimeUnit.MILLISECONDS);
    }
    throw new IllegalStateException("Result is not available. Check that isDone() returns true before calling get().");
  }
  
  public final Integer getValue()
  {
    return zajq.getValue();
  }
  
  public final boolean isCanceled()
  {
    return zajq.isCanceled();
  }
  
  public final boolean isDone()
  {
    return zajq.isReady();
  }
  
  public final void setResultCallback(ResultCallback paramResultCallback)
  {
    zajq.setResultCallback(paramResultCallback);
  }
  
  public final void setResultCallback(ResultCallback paramResultCallback, long paramLong, TimeUnit paramTimeUnit)
  {
    zajq.setResultCallback(paramResultCallback, paramLong, paramTimeUnit);
  }
  
  public final TransformedResult then(ResultTransform paramResultTransform)
  {
    return zajq.then(paramResultTransform);
  }
}
