package com.google.android.gms.auth.account;

import android.accounts.Account;
import android.os.IInterface;
import android.os.RemoteException;

public abstract interface LocationCallback
  extends IInterface
{
  public abstract void doOperation(IExtensionHost paramIExtensionHost, Account paramAccount)
    throws RemoteException;
  
  public abstract void selectRenderer(IExtensionHost paramIExtensionHost, String paramString)
    throws RemoteException;
  
  public abstract void writeValue(boolean paramBoolean)
    throws RemoteException;
}
