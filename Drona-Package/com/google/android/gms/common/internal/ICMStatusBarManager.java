package com.google.android.gms.common.internal;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.common.Command;
import com.google.android.gms.dynamic.IObjectWrapper;

public abstract interface ICMStatusBarManager
  extends IInterface
{
  public abstract boolean registerListener(Command paramCommand, IObjectWrapper paramIObjectWrapper)
    throws RemoteException;
}
