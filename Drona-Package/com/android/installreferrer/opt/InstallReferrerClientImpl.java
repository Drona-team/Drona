package com.android.installreferrer.opt;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.installreferrer.commons.InstallReferrerCommons;
import com.google.android.finsky.externalreferrer.IGetInstallReferrerService;
import com.google.android.finsky.externalreferrer.IGetInstallReferrerService.Stub;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

class InstallReferrerClientImpl
  extends InstallReferrerClient
{
  private static final String PAGE_KEY = "InstallReferrerClient";
  private static final int PLAY_STORE_MIN_APP_VER = 80837300;
  private static final String SERVICE_ACTION_NAME = "com.google.android.finsky.BIND_GET_INSTALL_REFERRER_SERVICE";
  private static final String SERVICE_NAME = "com.google.android.finsky.externalreferrer.GetInstallReferrerService";
  private static final String SERVICE_PACKAGE_NAME = "com.android.vending";
  private int clientState = 0;
  private final Context mApplicationContext;
  private IGetInstallReferrerService service;
  private ServiceConnection serviceConnection;
  
  public InstallReferrerClientImpl(Context paramContext)
  {
    mApplicationContext = paramContext.getApplicationContext();
  }
  
  private boolean isPlayStoreCompatible()
  {
    Object localObject = mApplicationContext.getPackageManager();
    try
    {
      localObject = ((PackageManager)localObject).getPackageInfo("com.android.vending", 128);
      return versionCode >= 80837300;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
    return false;
  }
  
  public void endConnection()
  {
    clientState = 3;
    if (serviceConnection != null)
    {
      InstallReferrerCommons.logVerbose("InstallReferrerClient", "Unbinding from service.");
      mApplicationContext.unbindService(serviceConnection);
      serviceConnection = null;
    }
    service = null;
  }
  
  public ReferrerDetails getInstallReferrer()
    throws RemoteException
  {
    if (isReady())
    {
      Object localObject = new Bundle();
      ((BaseBundle)localObject).putString("package_name", mApplicationContext.getPackageName());
      IGetInstallReferrerService localIGetInstallReferrerService = service;
      try
      {
        localObject = new ReferrerDetails(localIGetInstallReferrerService.getData((Bundle)localObject));
        return localObject;
      }
      catch (RemoteException localRemoteException)
      {
        InstallReferrerCommons.logWarn("InstallReferrerClient", "RemoteException getting install referrer information");
        clientState = 0;
        throw localRemoteException;
      }
    }
    throw new IllegalStateException("Service not connected. Please start a connection before using the service.");
  }
  
  public boolean isReady()
  {
    return (clientState == 2) && (service != null) && (serviceConnection != null);
  }
  
  public void startConnection(InstallReferrerStateListener paramInstallReferrerStateListener)
  {
    if (!isReady())
    {
      int i = clientState;
      if (i == 1)
      {
        InstallReferrerCommons.logWarn("InstallReferrerClient", "Client is already in the process of connecting to the service.");
        paramInstallReferrerStateListener.onInstallReferrerSetupFinished(3);
        return;
      }
      if (i != 3)
      {
        InstallReferrerCommons.logVerbose("InstallReferrerClient", "Starting install referrer service setup.");
        Intent localIntent = new Intent("com.google.android.finsky.BIND_GET_INSTALL_REFERRER_SERVICE");
        localIntent.setComponent(new ComponentName("com.android.vending", "com.google.android.finsky.externalreferrer.GetInstallReferrerService"));
        Object localObject1 = mApplicationContext.getPackageManager().queryIntentServices(localIntent, 0);
        if ((localObject1 != null) && (!((List)localObject1).isEmpty()))
        {
          Object localObject2 = (ResolveInfo)((List)localObject1).get(0);
          if (serviceInfo != null)
          {
            localObject1 = serviceInfo.packageName;
            localObject2 = serviceInfo.name;
            if (("com.android.vending".equals(localObject1)) && (localObject2 != null) && (isPlayStoreCompatible()))
            {
              localIntent = new Intent(localIntent);
              serviceConnection = new InstallReferrerServiceConnection(paramInstallReferrerStateListener, null);
              if (mApplicationContext.bindService(localIntent, serviceConnection, 1))
              {
                InstallReferrerCommons.logVerbose("InstallReferrerClient", "Service was bonded successfully.");
                return;
              }
              InstallReferrerCommons.logWarn("InstallReferrerClient", "Connection to service is blocked.");
              clientState = 0;
              paramInstallReferrerStateListener.onInstallReferrerSetupFinished(1);
              return;
            }
            InstallReferrerCommons.logWarn("InstallReferrerClient", "Play Store missing or incompatible. Version 8.3.73 or later required.");
            clientState = 0;
            paramInstallReferrerStateListener.onInstallReferrerSetupFinished(2);
            return;
          }
        }
        clientState = 0;
        InstallReferrerCommons.logVerbose("InstallReferrerClient", "Install Referrer service unavailable on device.");
        paramInstallReferrerStateListener.onInstallReferrerSetupFinished(2);
        return;
      }
      InstallReferrerCommons.logWarn("InstallReferrerClient", "Client was already closed and can't be reused. Please create another instance.");
      paramInstallReferrerStateListener.onInstallReferrerSetupFinished(3);
      return;
    }
    InstallReferrerCommons.logVerbose("InstallReferrerClient", "Service connection is valid. No need to re-initialize.");
    paramInstallReferrerStateListener.onInstallReferrerSetupFinished(0);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public @interface ClientState
  {
    public static final int CLOSED = 3;
    public static final int CONNECTED = 2;
    public static final int CONNECTING = 1;
    public static final int DISCONNECTED = 0;
  }
  
  final class InstallReferrerServiceConnection
    implements ServiceConnection
  {
    private final InstallReferrerStateListener mListener;
    
    private InstallReferrerServiceConnection(InstallReferrerStateListener paramInstallReferrerStateListener)
    {
      if (paramInstallReferrerStateListener != null)
      {
        mListener = paramInstallReferrerStateListener;
        return;
      }
      throw new RuntimeException("Please specify a listener to know when setup is done.");
    }
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      InstallReferrerCommons.logVerbose("InstallReferrerClient", "Install Referrer service connected.");
      InstallReferrerClientImpl.access$102(InstallReferrerClientImpl.this, IGetInstallReferrerService.Stub.asInterface(paramIBinder));
      InstallReferrerClientImpl.access$202(InstallReferrerClientImpl.this, 2);
      mListener.onInstallReferrerSetupFinished(0);
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      InstallReferrerCommons.logWarn("InstallReferrerClient", "Install Referrer service disconnected.");
      InstallReferrerClientImpl.access$102(InstallReferrerClientImpl.this, null);
      InstallReferrerClientImpl.access$202(InstallReferrerClientImpl.this, 0);
      mListener.onInstallReferrerServiceDisconnected();
    }
  }
}
