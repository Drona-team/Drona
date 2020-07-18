package com.google.android.gms.auth.util.signin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.util.Auth;
import com.google.android.gms.auth.util.signin.internal.Bus;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.internal.PendingResultUtil;
import com.google.android.gms.common.internal.PendingResultUtil.ResultConverter;
import com.google.android.gms.common.package_6.GoogleApiClient;
import com.google.android.gms.common.package_6.internal.ApiExceptionMapper;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.tasks.Task;

public class GoogleSignInClient
  extends com.google.android.gms.common.api.GoogleApi<com.google.android.gms.auth.api.signin.GoogleSignInOptions>
{
  private static final zzc zzar = new zzc(null);
  @VisibleForTesting
  private static int zzas = zzd.zzau;
  
  GoogleSignInClient(Activity paramActivity, GoogleSignInOptions paramGoogleSignInOptions)
  {
    super(paramActivity, Auth.GOOGLE_SIGN_IN_API, paramGoogleSignInOptions, new ApiExceptionMapper());
  }
  
  GoogleSignInClient(Context paramContext, GoogleSignInOptions paramGoogleSignInOptions)
  {
    super(paramContext, Auth.GOOGLE_SIGN_IN_API, paramGoogleSignInOptions, new ApiExceptionMapper());
  }
  
  private final int onVCardReceived()
  {
    try
    {
      if (zzas == zzd.zzau)
      {
        Context localContext = getApplicationContext();
        GoogleApiAvailability localGoogleApiAvailability = GoogleApiAvailability.getInstance();
        i = localGoogleApiAvailability.isGooglePlayServicesAvailable(localContext, 12451000);
        if (i == 0) {
          zzas = zzd.zzax;
        } else if ((localGoogleApiAvailability.getErrorResolutionIntent(localContext, i, null) == null) && (DynamiteModule.getLocalVersion(localContext, "com.google.android.gms.auth.api.fallback") != 0)) {
          zzas = zzd.zzaw;
        } else {
          zzas = zzd.zzav;
        }
      }
      int i = zzas;
      return i;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public Intent getSignInIntent()
  {
    Context localContext = getApplicationContext();
    switch (Orientation.1.zzat[(onVCardReceived() - 1)])
    {
    default: 
      return Bus.create(localContext, (GoogleSignInOptions)getApiOptions());
    case 2: 
      return Bus.getInstance(localContext, (GoogleSignInOptions)getApiOptions());
    }
    return Bus.unregister(localContext, (GoogleSignInOptions)getApiOptions());
  }
  
  public Task revokeAccess()
  {
    GoogleApiClient localGoogleApiClient = asGoogleApiClient();
    Context localContext = getApplicationContext();
    boolean bool;
    if (onVCardReceived() == zzd.zzaw) {
      bool = true;
    } else {
      bool = false;
    }
    return PendingResultUtil.toVoidTask(Bus.register(localGoogleApiClient, localContext, bool));
  }
  
  public Task signOut()
  {
    GoogleApiClient localGoogleApiClient = asGoogleApiClient();
    Context localContext = getApplicationContext();
    boolean bool;
    if (onVCardReceived() == zzd.zzaw) {
      bool = true;
    } else {
      bool = false;
    }
    return PendingResultUtil.toVoidTask(Bus.get(localGoogleApiClient, localContext, bool));
  }
  
  public Task silentSignIn()
  {
    GoogleApiClient localGoogleApiClient = asGoogleApiClient();
    Context localContext = getApplicationContext();
    GoogleSignInOptions localGoogleSignInOptions = (GoogleSignInOptions)getApiOptions();
    boolean bool;
    if (onVCardReceived() == zzd.zzaw) {
      bool = true;
    } else {
      bool = false;
    }
    return PendingResultUtil.toTask(Bus.get(localGoogleApiClient, localContext, localGoogleSignInOptions, bool), zzar);
  }
  
  final class zzc
    implements PendingResultUtil.ResultConverter<GoogleSignInResult, GoogleSignInAccount>
  {
    private zzc() {}
  }
  
  @VisibleForTesting
  final class zzd
  {
    public static int[] values$50KLMJ33DTMIUPRFDTJMOP9FC5N68SJFD5I2UPRDECNM2TBKD0NM2S395TPMIPRED5N2UHRFDTJMOPAJD5JMSIBE8DM6IPBEEGI4IRBGDHIMQPBEEHGN8QBFDOTG____0()
    {
      return (int[])zzay.clone();
    }
  }
}
