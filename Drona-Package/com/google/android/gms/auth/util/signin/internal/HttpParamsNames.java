package com.google.android.gms.auth.util.signin.internal;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.auth.util.signin.GoogleSignInOptions;

public abstract interface HttpParamsNames
  extends IInterface
{
  public abstract void getNames(Context paramContext, GoogleSignInOptions paramGoogleSignInOptions)
    throws RemoteException;
  
  public abstract void showLoadingDialog(Context paramContext, GoogleSignInOptions paramGoogleSignInOptions)
    throws RemoteException;
  
  public abstract void showToast(Context paramContext, GoogleSignInOptions paramGoogleSignInOptions)
    throws RemoteException;
}
