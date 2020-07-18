package com.google.android.gms.common.package_6.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.package_6.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.package_6.Sample;

public final class Logger
  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
  public final Api<?> mApi;
  private final boolean zaec;
  private BlockingQueue zaed;
  
  public Logger(Sample paramSample, boolean paramBoolean)
  {
    mApi = paramSample;
    zaec = paramBoolean;
  }
  
  private final void clear()
  {
    Preconditions.checkNotNull(zaed, "Callbacks must be attached to a ClientConnectionHelper instance before connecting the client.");
  }
  
  public final void onConnected(Bundle paramBundle)
  {
    clear();
    zaed.onConnected(paramBundle);
  }
  
  public final void onConnectionFailed(ConnectionResult paramConnectionResult)
  {
    clear();
    zaed.startLoading(paramConnectionResult, mApi, zaec);
  }
  
  public final void onConnectionSuspended(int paramInt)
  {
    clear();
    zaed.onConnectionSuspended(paramInt);
  }
  
  public final void v(BlockingQueue paramBlockingQueue)
  {
    zaed = paramBlockingQueue;
  }
}
