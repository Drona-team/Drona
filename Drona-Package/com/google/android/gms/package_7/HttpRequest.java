package com.google.android.gms.package_7;

import android.os.IInterface;
import android.os.Message;
import android.os.RemoteException;

public abstract interface HttpRequest
  extends IInterface
{
  public abstract void send(Message paramMessage)
    throws RemoteException;
}
