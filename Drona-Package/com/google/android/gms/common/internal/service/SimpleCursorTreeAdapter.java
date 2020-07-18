package com.google.android.gms.common.internal.service;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.GmsClient;
import com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.package_6.GoogleApiClient.OnConnectionFailedListener;

public final class SimpleCursorTreeAdapter
  extends GmsClient<zal>
{
  public SimpleCursorTreeAdapter(Context paramContext, Looper paramLooper, ClientSettings paramClientSettings, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    super(paramContext, paramLooper, 39, paramClientSettings, paramConnectionCallbacks, paramOnConnectionFailedListener);
  }
  
  protected final String getServiceDescriptor()
  {
    return "com.google.android.gms.common.internal.service.ICommonService";
  }
  
  public final String getStartServiceAction()
  {
    return "com.google.android.gms.common.service.START";
  }
}
