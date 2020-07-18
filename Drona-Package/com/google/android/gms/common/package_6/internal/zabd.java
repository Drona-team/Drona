package com.google.android.gms.common.package_6.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.package_6.Sample;

public abstract interface zabd
{
  public abstract void begin();
  
  public abstract void connect();
  
  public abstract boolean disconnect();
  
  public abstract BaseImplementation.ApiMethodImpl enqueue(BaseImplementation.ApiMethodImpl paramApiMethodImpl);
  
  public abstract BaseImplementation.ApiMethodImpl execute(BaseImplementation.ApiMethodImpl paramApiMethodImpl);
  
  public abstract void onConnected(Bundle paramBundle);
  
  public abstract void onConnectionSuspended(int paramInt);
  
  public abstract void showProgress(ConnectionResult paramConnectionResult, Sample paramSample, boolean paramBoolean);
}
