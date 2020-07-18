package com.google.android.gms.auth.util.signin.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.auth.util.signin.GoogleSignInOptions;
import com.google.android.gms.internal.auth-api.zzc;
import com.google.android.gms.internal.auth-api.zze;

public final class Start
  extends zzc
  implements HttpParamsNames
{
  Start(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.auth.api.signin.internal.ISignInService");
  }
  
  public final void getNames(Context paramContext, GoogleSignInOptions paramGoogleSignInOptions)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zze.zzc(localParcel, paramContext);
    zze.zzc(localParcel, paramGoogleSignInOptions);
    transactAndReadExceptionReturnVoid(103, localParcel);
  }
  
  public final void showLoadingDialog(Context paramContext, GoogleSignInOptions paramGoogleSignInOptions)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zze.zzc(localParcel, paramContext);
    zze.zzc(localParcel, paramGoogleSignInOptions);
    transactAndReadExceptionReturnVoid(101, localParcel);
  }
  
  public final void showToast(Context paramContext, GoogleSignInOptions paramGoogleSignInOptions)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zze.zzc(localParcel, paramContext);
    zze.zzc(localParcel, paramGoogleSignInOptions);
    transactAndReadExceptionReturnVoid(102, localParcel);
  }
}
