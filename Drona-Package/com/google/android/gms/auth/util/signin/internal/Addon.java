package com.google.android.gms.auth.util.signin.internal;

import android.content.Context;
import com.google.android.gms.auth.util.signin.GoogleSignInAccount;
import com.google.android.gms.auth.util.signin.GoogleSignInOptions;
import com.google.android.gms.common.util.VisibleForTesting;

public final class Addon
{
  private static Addon zzbn;
  @VisibleForTesting
  private Storage zzbo;
  @VisibleForTesting
  private GoogleSignInAccount zzbp;
  @VisibleForTesting
  private GoogleSignInOptions zzbq;
  
  private Addon(Context paramContext)
  {
    zzbo = Storage.getInstance(paramContext);
    zzbp = zzbo.getSavedDefaultGoogleSignInAccount();
    zzbq = zzbo.getSavedDefaultGoogleSignInOptions();
  }
  
  public static Addon get(Context paramContext)
  {
    try
    {
      paramContext = init(paramContext.getApplicationContext());
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  private static Addon init(Context paramContext)
  {
    try
    {
      if (zzbn == null) {
        zzbn = new Addon(paramContext);
      }
      paramContext = zzbn;
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  public final void clear()
  {
    try
    {
      zzbo.clear();
      zzbp = null;
      zzbq = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final GoogleSignInOptions getName()
  {
    try
    {
      GoogleSignInOptions localGoogleSignInOptions = zzbq;
      return localGoogleSignInOptions;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final GoogleSignInAccount getRating()
  {
    try
    {
      GoogleSignInAccount localGoogleSignInAccount = zzbp;
      return localGoogleSignInAccount;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final void makeCall(GoogleSignInOptions paramGoogleSignInOptions, GoogleSignInAccount paramGoogleSignInAccount)
  {
    try
    {
      zzbo.saveDefaultGoogleSignInAccount(paramGoogleSignInAccount, paramGoogleSignInOptions);
      zzbp = paramGoogleSignInAccount;
      zzbq = paramGoogleSignInOptions;
      return;
    }
    catch (Throwable paramGoogleSignInOptions)
    {
      throw paramGoogleSignInOptions;
    }
  }
}
