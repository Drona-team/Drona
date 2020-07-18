package com.google.android.gms.auth.util.signin.internal;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.auth.util.signin.GoogleSignInAccount;
import com.google.android.gms.common.package_6.Status;

public abstract interface Context
  extends IInterface
{
  public abstract void zzc(GoogleSignInAccount paramGoogleSignInAccount, Status paramStatus)
    throws RemoteException;
  
  public abstract void zze(Status paramStatus)
    throws RemoteException;
  
  public abstract void zzf(Status paramStatus)
    throws RemoteException;
}
