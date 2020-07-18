package com.google.android.gms.common.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.package_6.GoogleApiClient.OnConnectionFailedListener;

public class SimpleClientAdapter<T extends IInterface>
  extends GmsClient<T>
{
  private final com.google.android.gms.common.api.Api.SimpleClient<T> zapg;
  
  public SimpleClientAdapter(Context paramContext, Looper paramLooper, int paramInt, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener, ClientSettings paramClientSettings, com.google.android.gms.common.package_6.Api.SimpleClient paramSimpleClient)
  {
    super(paramContext, paramLooper, paramInt, paramClientSettings, paramConnectionCallbacks, paramOnConnectionFailedListener);
    zapg = paramSimpleClient;
  }
  
  protected IInterface createServiceInterface(IBinder paramIBinder)
  {
    return zapg.createServiceInterface(paramIBinder);
  }
  
  public com.google.android.gms.common.package_6.Api.SimpleClient getClient()
  {
    return zapg;
  }
  
  public int getMinApkVersion()
  {
    return super.getMinApkVersion();
  }
  
  protected String getServiceDescriptor()
  {
    return zapg.getServiceDescriptor();
  }
  
  protected String getStartServiceAction()
  {
    return zapg.getStartServiceAction();
  }
  
  protected void onSetConnectState(int paramInt, IInterface paramIInterface)
  {
    zapg.setState(paramInt, paramIInterface);
  }
}
