package com.google.android.gms.common.internal.service;

import com.google.android.gms.common.package_6.GoogleApiClient;
import com.google.android.gms.common.package_6.PendingResult;

public final class Provider
  implements RuntimeExceptionDao
{
  public Provider() {}
  
  public final PendingResult query(GoogleApiClient paramGoogleApiClient)
  {
    return paramGoogleApiClient.execute(new DownloadManager(this, paramGoogleApiClient));
  }
}
