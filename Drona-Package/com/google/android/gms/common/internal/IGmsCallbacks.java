package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.common.zzb;
import com.google.android.gms.internal.common.zzc;

public abstract interface IGmsCallbacks
  extends IInterface
{
  public abstract void onPostInitComplete(int paramInt, IBinder paramIBinder, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void zza(int paramInt, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void zza(int paramInt, IBinder paramIBinder, Entry paramEntry)
    throws RemoteException;
  
  public static abstract class zza
    extends zzb
    implements IGmsCallbacks
  {
    public zza()
    {
      super();
    }
    
    protected final boolean get(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      switch (paramInt1)
      {
      default: 
        return false;
      case 3: 
        zza(paramParcel1.readInt(), paramParcel1.readStrongBinder(), (Entry)zzc.zza(paramParcel1, Entry.CREATOR));
        break;
      case 2: 
        zza(paramParcel1.readInt(), (Bundle)zzc.zza(paramParcel1, Bundle.CREATOR));
        break;
      case 1: 
        onPostInitComplete(paramParcel1.readInt(), paramParcel1.readStrongBinder(), (Bundle)zzc.zza(paramParcel1, Bundle.CREATOR));
      }
      paramParcel2.writeNoException();
      return true;
    }
  }
}
