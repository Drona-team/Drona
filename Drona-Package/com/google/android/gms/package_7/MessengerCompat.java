package com.google.android.gms.package_7;

import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.google.android.gms.common.internal.ReflectedParcelable;

public class MessengerCompat
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<com.google.android.gms.iid.MessengerCompat> CREATOR = new Server.1();
  private Messenger zzad;
  private HttpRequest zzcd;
  
  public MessengerCompat(IBinder paramIBinder)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      zzad = new Messenger(paramIBinder);
      return;
    }
    if (paramIBinder == null)
    {
      paramIBinder = null;
    }
    else
    {
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.iid.IMessengerCompat");
      if ((localIInterface instanceof HttpRequest)) {
        paramIBinder = (HttpRequest)localIInterface;
      } else {
        paramIBinder = new Email(paramIBinder);
      }
    }
    zzcd = paramIBinder;
  }
  
  private final IBinder getBinder()
  {
    if (zzad != null) {
      return zzad.getBinder();
    }
    return zzcd.asBinder();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    try
    {
      boolean bool = getBinder().equals(((MessengerCompat)paramObject).getBinder());
      return bool;
    }
    catch (ClassCastException paramObject) {}
    return false;
  }
  
  public int hashCode()
  {
    return getBinder().hashCode();
  }
  
  public final void send(Message paramMessage)
    throws RemoteException
  {
    if (zzad != null)
    {
      zzad.send(paramMessage);
      return;
    }
    zzcd.send(paramMessage);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (zzad != null)
    {
      paramParcel.writeStrongBinder(zzad.getBinder());
      return;
    }
    paramParcel.writeStrongBinder(zzcd.asBinder());
  }
}
