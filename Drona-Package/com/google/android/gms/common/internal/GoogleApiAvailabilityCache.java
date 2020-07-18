package com.google.android.gms.common.internal;

import android.content.Context;
import android.util.SparseIntArray;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.package_6.Api.Client;

public class GoogleApiAvailabilityCache
{
  private final SparseIntArray zaos = new SparseIntArray();
  private GoogleApiAvailabilityLight zaot;
  
  public GoogleApiAvailabilityCache()
  {
    this(GoogleApiAvailability.getInstance());
  }
  
  public GoogleApiAvailabilityCache(GoogleApiAvailabilityLight paramGoogleApiAvailabilityLight)
  {
    Preconditions.checkNotNull(paramGoogleApiAvailabilityLight);
    zaot = paramGoogleApiAvailabilityLight;
  }
  
  public void flush()
  {
    zaos.clear();
  }
  
  public int getClientAvailability(Context paramContext, Api.Client paramClient)
  {
    Preconditions.checkNotNull(paramContext);
    Preconditions.checkNotNull(paramClient);
    if (!paramClient.requiresGooglePlayServices()) {
      return 0;
    }
    int m = paramClient.getMinApkVersion();
    int i = zaos.get(m, -1);
    int j = i;
    if (i != -1) {
      return i;
    }
    int k = 0;
    for (;;)
    {
      i = j;
      if (k >= zaos.size()) {
        break;
      }
      i = zaos.keyAt(k);
      if ((i > m) && (zaos.get(i) == 0))
      {
        i = 0;
        break;
      }
      k += 1;
    }
    j = i;
    if (i == -1) {
      j = zaot.isGooglePlayServicesAvailable(paramContext, m);
    }
    zaos.put(m, j);
    return j;
  }
}
