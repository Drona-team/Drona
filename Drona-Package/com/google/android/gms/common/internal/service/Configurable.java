package com.google.android.gms.common.internal.service;

import android.os.IInterface;
import android.os.RemoteException;

public abstract interface Configurable
  extends IInterface
{
  public abstract void setConfig(PacketListener paramPacketListener)
    throws RemoteException;
}
