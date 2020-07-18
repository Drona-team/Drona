package com.google.android.gms.package_7;

import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.gcm.zzd;
import com.google.android.gms.internal.gcm.zze;

public final class Email
  extends zzd
  implements HttpRequest
{
  Email(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.iid.IMessengerCompat");
  }
  
  public final void send(Message paramMessage)
    throws RemoteException
  {
    Parcel localParcel = zzd();
    zze.zzd(localParcel, paramMessage);
    zze(1, localParcel);
  }
}
