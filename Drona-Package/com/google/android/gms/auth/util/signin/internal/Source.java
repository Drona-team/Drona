package com.google.android.gms.auth.util.signin.internal;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.auth.util.signin.GoogleSignInAccount;
import com.google.android.gms.common.package_6.Status;
import com.google.android.gms.internal.auth-api.zzd;
import com.google.android.gms.internal.auth-api.zze;

public abstract class Source
  extends zzd
  implements Context
{
  public Source()
  {
    super("com.google.android.gms.auth.api.signin.internal.ISignInCallbacks");
  }
  
  protected final boolean dispatchTransaction(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    switch (paramInt1)
    {
    default: 
      return false;
    case 103: 
      zzf((Status)zze.zzc(paramParcel1, Status.CREATOR));
      break;
    case 102: 
      zze((Status)zze.zzc(paramParcel1, Status.CREATOR));
      break;
    case 101: 
      zzc((GoogleSignInAccount)zze.zzc(paramParcel1, GoogleSignInAccount.CREATOR), (Status)zze.zzc(paramParcel1, Status.CREATOR));
    }
    paramParcel2.writeNoException();
    return true;
  }
}
