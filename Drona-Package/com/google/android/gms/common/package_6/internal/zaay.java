package com.google.android.gms.common.package_6.internal;

import android.os.Bundle;
import com.google.android.gms.common.package_6.GoogleApiClient;
import com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks;
import java.util.concurrent.atomic.AtomicReference;

final class zaay
  implements GoogleApiClient.ConnectionCallbacks
{
  zaay(zaaw paramZaaw, AtomicReference paramAtomicReference, StatusPendingResult paramStatusPendingResult) {}
  
  public final void onConnected(Bundle paramBundle)
  {
    zaaw.writeToFile(zahh, (GoogleApiClient)zahi.get(), zahj, true);
  }
  
  public final void onConnectionSuspended(int paramInt) {}
}
