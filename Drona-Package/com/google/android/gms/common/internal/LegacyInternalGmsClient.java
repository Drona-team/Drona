package com.google.android.gms.common.internal;

import android.content.Context;
import android.os.IInterface;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.package_6.GoogleApiClient.OnConnectionFailedListener;

@Deprecated
public abstract class LegacyInternalGmsClient<T extends IInterface>
  extends GmsClient<T>
{
  private final GmsClientEventManager zags = new GmsClientEventManager(paramContext.getMainLooper(), this);
  
  public LegacyInternalGmsClient(Context paramContext, int paramInt, ClientSettings paramClientSettings, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    super(paramContext, paramContext.getMainLooper(), paramInt, paramClientSettings);
    zags.registerConnectionCallbacks(paramConnectionCallbacks);
    zags.registerConnectionFailedListener(paramOnConnectionFailedListener);
  }
  
  public void checkAvailabilityAndConnect()
  {
    zags.enableCallbacks();
    super.checkAvailabilityAndConnect();
  }
  
  public void disconnect()
  {
    zags.disableCallbacks();
    super.disconnect();
  }
  
  public int getMinApkVersion()
  {
    return super.getMinApkVersion();
  }
  
  public boolean isConnectionCallbacksRegistered(GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    return zags.isConnectionCallbacksRegistered(paramConnectionCallbacks);
  }
  
  public boolean isConnectionFailedListenerRegistered(GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    return zags.isConnectionFailedListenerRegistered(paramOnConnectionFailedListener);
  }
  
  public void onConnectedLocked(IInterface paramIInterface)
  {
    super.onConnectedLocked(paramIInterface);
    zags.onConnectionSuccess(getConnectionHint());
  }
  
  public void onConnectionFailed(ConnectionResult paramConnectionResult)
  {
    super.onConnectionFailed(paramConnectionResult);
    zags.onConnectionFailure(paramConnectionResult);
  }
  
  public void onConnectionSuspended(int paramInt)
  {
    super.onConnectionSuspended(paramInt);
    zags.onUnintentionalDisconnection(paramInt);
  }
  
  public void registerConnectionCallbacks(GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    zags.registerConnectionCallbacks(paramConnectionCallbacks);
  }
  
  public void registerConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    zags.registerConnectionFailedListener(paramOnConnectionFailedListener);
  }
  
  public void unregisterConnectionCallbacks(GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    zags.unregisterConnectionCallbacks(paramConnectionCallbacks);
  }
  
  public void unregisterConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    zags.unregisterConnectionFailedListener(paramOnConnectionFailedListener);
  }
}
