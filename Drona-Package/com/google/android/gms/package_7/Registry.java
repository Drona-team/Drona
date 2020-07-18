package com.google.android.gms.package_7;

import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

final class Registry
{
  final Messenger zzad;
  final MessengerCompat zzco;
  
  Registry(IBinder paramIBinder)
    throws RemoteException
  {
    String str = paramIBinder.getInterfaceDescriptor();
    if ("android.os.IMessenger".equals(str))
    {
      zzad = new Messenger(paramIBinder);
      zzco = null;
      return;
    }
    if ("com.google.android.gms.iid.IMessengerCompat".equals(str))
    {
      zzco = new MessengerCompat(paramIBinder);
      zzad = null;
      return;
    }
    paramIBinder = String.valueOf(str);
    if (paramIBinder.length() != 0) {
      paramIBinder = "Invalid interface descriptor: ".concat(paramIBinder);
    } else {
      paramIBinder = new String("Invalid interface descriptor: ");
    }
    Log.w("MessengerIpcClient", paramIBinder);
    throw new RemoteException();
  }
}
