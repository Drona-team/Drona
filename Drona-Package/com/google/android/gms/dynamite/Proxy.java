package com.google.android.gms.dynamite;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.Stub;
import com.google.android.gms.internal.common.zza;
import com.google.android.gms.internal.common.zzc;

public final class Proxy
  extends zza
  implements Notification
{
  Proxy(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.dynamite.IDynamiteLoader");
  }
  
  public final IObjectWrapper getChat(IObjectWrapper paramIObjectWrapper, String paramString, int paramInt)
    throws RemoteException
  {
    Parcel localParcel = zza();
    zzc.zza(localParcel, paramIObjectWrapper);
    localParcel.writeString(paramString);
    localParcel.writeInt(paramInt);
    paramIObjectWrapper = zza(2, localParcel);
    paramString = IObjectWrapper.Stub.asInterface(paramIObjectWrapper.readStrongBinder());
    paramIObjectWrapper.recycle();
    return paramString;
  }
  
  public final int setTime(IObjectWrapper paramIObjectWrapper, String paramString, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zza();
    zzc.zza(localParcel, paramIObjectWrapper);
    localParcel.writeString(paramString);
    zzc.writeBoolean(localParcel, paramBoolean);
    paramIObjectWrapper = zza(5, localParcel);
    int i = paramIObjectWrapper.readInt();
    paramIObjectWrapper.recycle();
    return i;
  }
  
  public final IObjectWrapper setTime(IObjectWrapper paramIObjectWrapper, String paramString, int paramInt)
    throws RemoteException
  {
    Parcel localParcel = zza();
    zzc.zza(localParcel, paramIObjectWrapper);
    localParcel.writeString(paramString);
    localParcel.writeInt(paramInt);
    paramIObjectWrapper = zza(4, localParcel);
    paramString = IObjectWrapper.Stub.asInterface(paramIObjectWrapper.readStrongBinder());
    paramIObjectWrapper.recycle();
    return paramString;
  }
  
  public final int speak(IObjectWrapper paramIObjectWrapper, String paramString, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zza();
    zzc.zza(localParcel, paramIObjectWrapper);
    localParcel.writeString(paramString);
    zzc.writeBoolean(localParcel, paramBoolean);
    paramIObjectWrapper = zza(3, localParcel);
    int i = paramIObjectWrapper.readInt();
    paramIObjectWrapper.recycle();
    return i;
  }
  
  public final int zzak()
    throws RemoteException
  {
    Parcel localParcel = zza(6, zza());
    int i = localParcel.readInt();
    localParcel.recycle();
    return i;
  }
}
