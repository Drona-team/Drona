package com.google.android.gms.common.package_6.internal;

import android.os.Looper;
import android.util.Log;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.package_6.Releasable;
import com.google.android.gms.common.package_6.Status;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;

public final class zacm<R extends com.google.android.gms.common.api.Result>
  extends com.google.android.gms.common.api.TransformedResult<R>
  implements ResultCallback<R>
{
  private final Object zado = new Object();
  private final WeakReference<com.google.android.gms.common.api.GoogleApiClient> zadq;
  private com.google.android.gms.common.api.ResultTransform<? super R, ? extends com.google.android.gms.common.api.Result> zako = null;
  private com.google.android.gms.common.api.internal.zacm<? extends com.google.android.gms.common.api.Result> zakp = null;
  private volatile com.google.android.gms.common.api.ResultCallbacks<? super R> zakq = null;
  private com.google.android.gms.common.api.PendingResult<R> zakr = null;
  private Status zaks = null;
  private final com.google.android.gms.common.api.internal.zaco zakt;
  private boolean zaku = false;
  
  public zacm(WeakReference paramWeakReference)
  {
    Preconditions.checkNotNull(paramWeakReference, "GoogleApiClient reference must not be null");
    zadq = paramWeakReference;
    paramWeakReference = (com.google.android.gms.common.package_6.GoogleApiClient)zadq.get();
    if (paramWeakReference != null) {
      paramWeakReference = paramWeakReference.getLooper();
    } else {
      paramWeakReference = Looper.getMainLooper();
    }
    zakt = new zaco(this, paramWeakReference);
  }
  
  private final void loadResource(Status paramStatus)
  {
    Object localObject = zado;
    try
    {
      if (zako != null)
      {
        paramStatus = zako.onFailure(paramStatus);
        Preconditions.checkNotNull(paramStatus, "onFailure must not return null");
        zakp.printStackTrace(paramStatus);
      }
      else if (zabw())
      {
        zakq.onFailure(paramStatus);
      }
      return;
    }
    catch (Throwable paramStatus)
    {
      throw paramStatus;
    }
  }
  
  private static void parse(com.google.android.gms.common.package_6.Result paramResult)
  {
    if ((paramResult instanceof Releasable)) {
      try
      {
        ((Releasable)paramResult).release();
        return;
      }
      catch (RuntimeException localRuntimeException)
      {
        paramResult = String.valueOf(paramResult);
        StringBuilder localStringBuilder = new StringBuilder(String.valueOf(paramResult).length() + 18);
        localStringBuilder.append("Unable to release ");
        localStringBuilder.append(paramResult);
        Log.w("TransformedResultImpl", localStringBuilder.toString(), localRuntimeException);
      }
    }
  }
  
  private final void printStackTrace(Status paramStatus)
  {
    Object localObject = zado;
    try
    {
      zaks = paramStatus;
      loadResource(zaks);
      return;
    }
    catch (Throwable paramStatus)
    {
      throw paramStatus;
    }
  }
  
  private final void zabu()
  {
    if ((zako == null) && (zakq == null)) {
      return;
    }
    com.google.android.gms.common.package_6.GoogleApiClient localGoogleApiClient = (com.google.android.gms.common.package_6.GoogleApiClient)zadq.get();
    if ((!zaku) && (zako != null) && (localGoogleApiClient != null))
    {
      localGoogleApiClient.ensureInitialized(this);
      zaku = true;
    }
    if (zaks != null)
    {
      loadResource(zaks);
      return;
    }
    if (zakr != null) {
      zakr.setResultCallback(this);
    }
  }
  
  private final boolean zabw()
  {
    com.google.android.gms.common.package_6.GoogleApiClient localGoogleApiClient = (com.google.android.gms.common.package_6.GoogleApiClient)zadq.get();
    return (zakq != null) && (localGoogleApiClient != null);
  }
  
  public final void andFinally(com.google.android.gms.common.package_6.ResultCallbacks paramResultCallbacks)
  {
    Object localObject = zado;
    for (;;)
    {
      try
      {
        com.google.android.gms.common.package_6.ResultCallbacks localResultCallbacks = zakq;
        boolean bool2 = false;
        if (localResultCallbacks == null)
        {
          bool1 = true;
          Preconditions.checkState(bool1, "Cannot call andFinally() twice.");
          bool1 = bool2;
          if (zako == null) {
            bool1 = true;
          }
          Preconditions.checkState(bool1, "Cannot call then() and andFinally() on the same TransformedResult.");
          zakq = paramResultCallbacks;
          zabu();
          return;
        }
      }
      catch (Throwable paramResultCallbacks)
      {
        throw paramResultCallbacks;
      }
      boolean bool1 = false;
    }
  }
  
  public final void onResult(com.google.android.gms.common.package_6.Result paramResult)
  {
    Object localObject = zado;
    try
    {
      if (paramResult.getStatus().isSuccess())
      {
        if (zako != null) {
          zacc.zabb().submit(new zacn(this, paramResult));
        } else if (zabw()) {
          zakq.onSuccess(paramResult);
        }
      }
      else
      {
        printStackTrace(paramResult.getStatus());
        parse(paramResult);
      }
      return;
    }
    catch (Throwable paramResult)
    {
      throw paramResult;
    }
  }
  
  public final void onResultReceived(com.google.android.gms.common.package_6.PendingResult paramPendingResult)
  {
    Object localObject = zado;
    try
    {
      zakr = paramPendingResult;
      zabu();
      return;
    }
    catch (Throwable paramPendingResult)
    {
      throw paramPendingResult;
    }
  }
  
  public final com.google.android.gms.common.package_6.TransformedResult then(com.google.android.gms.common.package_6.ResultTransform paramResultTransform)
  {
    Object localObject = zado;
    for (;;)
    {
      try
      {
        com.google.android.gms.common.package_6.ResultTransform localResultTransform = zako;
        boolean bool2 = false;
        if (localResultTransform == null)
        {
          bool1 = true;
          Preconditions.checkState(bool1, "Cannot call then() twice.");
          bool1 = bool2;
          if (zakq == null) {
            bool1 = true;
          }
          Preconditions.checkState(bool1, "Cannot call then() and andFinally() on the same TransformedResult.");
          zako = paramResultTransform;
          paramResultTransform = new zacm(zadq);
          zakp = paramResultTransform;
          zabu();
          return paramResultTransform;
        }
      }
      catch (Throwable paramResultTransform)
      {
        throw paramResultTransform;
      }
      boolean bool1 = false;
    }
  }
  
  final void zabv()
  {
    zakq = null;
  }
}
