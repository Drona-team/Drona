package com.google.android.gms.common.package_6.internal;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.package_6.Sample;

public abstract interface BlockingQueue
  extends GoogleApiClient.ConnectionCallbacks
{
  public abstract void startLoading(ConnectionResult paramConnectionResult, Sample paramSample, boolean paramBoolean);
}
