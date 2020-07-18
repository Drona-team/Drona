package com.google.android.gms.common.package_6.internal;

import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.internal.BaseGmsClient.ConnectionProgressReportCallbacks;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.package_6.GoogleApiClient;
import com.google.android.gms.common.package_6.Sample;
import java.lang.ref.WeakReference;
import java.util.concurrent.locks.Lock;

final class zaam
  implements BaseGmsClient.ConnectionProgressReportCallbacks
{
  private final Api<?> mApi;
  private final boolean zaec;
  private final WeakReference<com.google.android.gms.common.api.internal.zaak> zagk;
  
  public zaam(zaak paramZaak, Sample paramSample, boolean paramBoolean)
  {
    zagk = new WeakReference(paramZaak);
    mApi = paramSample;
    zaec = paramBoolean;
  }
  
  public final void onReportServiceBinding(ConnectionResult paramConnectionResult)
  {
    zaak localZaak = (zaak)zagk.get();
    if (localZaak == null) {
      return;
    }
    boolean bool;
    if (Looper.myLooper() == itemszaee.getLooper()) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkState(bool, "onReportServiceBinding must be called on the GoogleApiClient handler thread");
    zaak.getLock(localZaak).lock();
    try
    {
      bool = zaak.b(localZaak, 0);
      if (!bool)
      {
        zaak.getLock(localZaak).unlock();
        return;
      }
      bool = paramConnectionResult.isSuccess();
      if (!bool) {
        zaak.setSetting(localZaak, paramConnectionResult, mApi, zaec);
      }
      bool = zaak.reportException(localZaak);
      if (bool) {
        zaak.setEventListener(localZaak);
      }
      zaak.getLock(localZaak).unlock();
      return;
    }
    catch (Throwable paramConnectionResult)
    {
      zaak.getLock(localZaak).unlock();
      throw paramConnectionResult;
    }
  }
}
