package com.google.android.gms.common.package_6.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.package_6.Api.Client;
import com.google.android.gms.common.package_6.Sample;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

public final class zaav
  implements zabd
{
  private final zabe zaft;
  
  public zaav(zabe paramZabe)
  {
    zaft = paramZabe;
  }
  
  public final void begin()
  {
    Iterator localIterator = zaft.zagz.values().iterator();
    while (localIterator.hasNext()) {
      ((Api.Client)localIterator.next()).disconnect();
    }
    zaft.zaee.zaha = Collections.emptySet();
  }
  
  public final void connect()
  {
    zaft.zaaz();
  }
  
  public final boolean disconnect()
  {
    return true;
  }
  
  public final BaseImplementation.ApiMethodImpl enqueue(BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    zaft.zaee.zafc.add(paramApiMethodImpl);
    return paramApiMethodImpl;
  }
  
  public final BaseImplementation.ApiMethodImpl execute(BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    throw new IllegalStateException("GoogleApiClient is not connected yet.");
  }
  
  public final void onConnected(Bundle paramBundle) {}
  
  public final void onConnectionSuspended(int paramInt) {}
  
  public final void showProgress(ConnectionResult paramConnectionResult, Sample paramSample, boolean paramBoolean) {}
}
