package com.google.android.gms.common.package_6.internal;

import com.google.android.gms.common.package_6.GoogleApiClient;
import com.google.android.gms.common.package_6.Result;
import com.google.android.gms.common.package_6.ResultTransform;
import com.google.android.gms.internal.base.zap;
import java.lang.ref.WeakReference;

final class zacn
  implements Runnable
{
  zacn(zacm paramZacm, Result paramResult) {}
  
  public final void run()
  {
    Object localObject = BasePendingResult.zadn;
    GoogleApiClient localGoogleApiClient1;
    try
    {
      ((ThreadLocal)localObject).set(Boolean.valueOf(true));
      localObject = zacm.getBasePath(zakw).onSuccess(zakv);
      zacm.access$getMMsgHandler(zakw).sendMessage(zacm.access$getMMsgHandler(zakw).obtainMessage(0, localObject));
      BasePendingResult.zadn.set(Boolean.valueOf(false));
      zacm.handleResult(zakw, zakv);
      localObject = (GoogleApiClient)zacm.access$getRootView(zakw).get();
      if (localObject == null) {
        return;
      }
      ((GoogleApiClient)localObject).removeAccount(zakw);
      return;
    }
    catch (Throwable localThrowable) {}catch (RuntimeException localRuntimeException)
    {
      zacm.access$getMMsgHandler(zakw).sendMessage(zacm.access$getMMsgHandler(zakw).obtainMessage(1, localRuntimeException));
      BasePendingResult.zadn.set(Boolean.valueOf(false));
      zacm.handleResult(zakw, zakv);
      localGoogleApiClient1 = (GoogleApiClient)zacm.access$getRootView(zakw).get();
      if (localGoogleApiClient1 == null) {
        return;
      }
    }
    localGoogleApiClient1.removeAccount(zakw);
    return;
    BasePendingResult.zadn.set(Boolean.valueOf(false));
    zacm.handleResult(zakw, zakv);
    GoogleApiClient localGoogleApiClient2 = (GoogleApiClient)zacm.access$getRootView(zakw).get();
    if (localGoogleApiClient2 != null) {
      localGoogleApiClient2.removeAccount(zakw);
    }
    throw localGoogleApiClient1;
  }
}
