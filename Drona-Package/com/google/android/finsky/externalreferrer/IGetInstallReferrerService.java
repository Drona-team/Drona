package com.google.android.finsky.externalreferrer;

import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.json.IExtensionHost.Stub;
import com.google.android.json.IStatusBarCustomTileHolder.Stub.Proxy;
import com.google.android.json.JSONObject;

public abstract interface IGetInstallReferrerService
  extends IInterface
{
  public abstract Bundle getData(Bundle paramBundle)
    throws RemoteException;
  
  public static abstract class Stub
    extends IExtensionHost.Stub
    implements IGetInstallReferrerService
  {
    public Stub()
    {
      super();
    }
    
    public static IGetInstallReferrerService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder != null)
      {
        IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.finsky.externalreferrer.IGetInstallReferrerService");
        if ((localIInterface instanceof IGetInstallReferrerService)) {
          return (IGetInstallReferrerService)localIInterface;
        }
        return new Proxy(paramIBinder);
      }
      return null;
    }
    
    public static class Proxy
      extends IStatusBarCustomTileHolder.Stub.Proxy
      implements IGetInstallReferrerService
    {
      Proxy(IBinder paramIBinder)
      {
        super("com.google.android.finsky.externalreferrer.IGetInstallReferrerService");
      }
      
      public final Bundle getData(Bundle paramBundle)
        throws RemoteException
      {
        Object localObject = get();
        JSONObject.writeValue((Parcel)localObject, paramBundle);
        paramBundle = get((Parcel)localObject);
        localObject = (Bundle)JSONObject.get(paramBundle, Bundle.CREATOR);
        paramBundle.recycle();
        return localObject;
      }
    }
  }
}
