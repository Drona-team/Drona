package com.google.android.gms.common.internal.service;

import android.os.IInterface;
import android.os.RemoteException;

public abstract interface PacketListener
  extends IInterface
{
  public abstract void zaj(int paramInt)
    throws RemoteException;
}
