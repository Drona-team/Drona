package com.google.android.gms.auth.account;

import android.accounts.Account;
import android.os.IInterface;
import android.os.RemoteException;

public abstract interface IExtensionHost
  extends IInterface
{
  public abstract void zza(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void zzc(Account paramAccount)
    throws RemoteException;
}
