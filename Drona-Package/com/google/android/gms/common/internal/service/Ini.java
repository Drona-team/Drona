package com.google.android.gms.common.internal.service;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.base.zaa;
import com.google.android.gms.internal.base.zac;

public final class Ini
  extends zaa
  implements Configurable
{
  Ini(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.common.internal.service.ICommonService");
  }
  
  public final void setConfig(PacketListener paramPacketListener)
    throws RemoteException
  {
    Parcel localParcel = zaa();
    zac.zaa(localParcel, paramPacketListener);
    zac(1, localParcel);
  }
}
