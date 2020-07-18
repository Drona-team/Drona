package com.google.android.gms.auth.util.signin.internal;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.auth.api.signin.internal.SignInHubActivity;
import com.google.android.gms.auth.util.signin.GoogleSignInAccount;
import com.google.android.gms.auth.util.signin.GoogleSignInOptions;
import com.google.android.gms.auth.util.signin.GoogleSignInResult;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.logging.Logger;
import com.google.android.gms.common.package_6.GoogleApiClient;
import com.google.android.gms.common.package_6.OptionalPendingResult;
import com.google.android.gms.common.package_6.PendingResult;
import com.google.android.gms.common.package_6.PendingResults;
import com.google.android.gms.common.package_6.Result;
import com.google.android.gms.common.package_6.Status;
import com.google.android.gms.common.package_6.internal.GoogleApiManager;
import com.google.android.gms.common.package_6.internal.OptionalPendingResultImpl;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class Bus
{
  private static Logger zzbd = new Logger("GoogleSignInCommon", new String[0]);
  
  public static Intent create(Context paramContext, GoogleSignInOptions paramGoogleSignInOptions)
  {
    zzbd.d("getNoImplementationSignInIntent()", new Object[0]);
    paramContext = getInstance(paramContext, paramGoogleSignInOptions);
    paramContext.setAction("com.google.android.gms.auth.NO_IMPL");
    return paramContext;
  }
  
  public static OptionalPendingResult get(GoogleApiClient paramGoogleApiClient, Context paramContext, GoogleSignInOptions paramGoogleSignInOptions, boolean paramBoolean)
  {
    zzbd.d("silentSignIn()", new Object[0]);
    zzbd.d("getEligibleSavedSignInResult()", new Object[0]);
    Preconditions.checkNotNull(paramGoogleSignInOptions);
    Object localObject = Addon.get(paramContext).getName();
    if (localObject != null)
    {
      Account localAccount1 = ((GoogleSignInOptions)localObject).getAccount();
      Account localAccount2 = paramGoogleSignInOptions.getAccount();
      boolean bool;
      if (localAccount1 == null)
      {
        if (localAccount2 == null) {
          bool = true;
        } else {
          bool = false;
        }
      }
      else {
        bool = localAccount1.equals(localAccount2);
      }
      if ((bool) && (!paramGoogleSignInOptions.isServerAuthCodeRequested()) && ((!paramGoogleSignInOptions.isIdTokenRequested()) || ((((GoogleSignInOptions)localObject).isIdTokenRequested()) && (paramGoogleSignInOptions.getServerClientId().equals(((GoogleSignInOptions)localObject).getServerClientId())))) && (new HashSet(((GoogleSignInOptions)localObject).getScopes()).containsAll(new HashSet(paramGoogleSignInOptions.getScopes()))))
      {
        localObject = Addon.get(paramContext).getRating();
        if ((localObject != null) && (!((GoogleSignInAccount)localObject).isExpired()))
        {
          localObject = new GoogleSignInResult((GoogleSignInAccount)localObject, Status.RESULT_SUCCESS);
          break label202;
        }
      }
    }
    localObject = null;
    label202:
    if (localObject != null)
    {
      zzbd.d("Eligible saved sign in result found", new Object[0]);
      return PendingResults.immediatePendingResult((Result)localObject, paramGoogleApiClient);
    }
    if (paramBoolean) {
      return PendingResults.immediatePendingResult(new GoogleSignInResult(null, new Status(4)), paramGoogleApiClient);
    }
    zzbd.d("trySilentSignIn()", new Object[0]);
    return new OptionalPendingResultImpl(paramGoogleApiClient.enqueue(new InternalHttpClient(paramGoogleApiClient, paramContext, paramGoogleSignInOptions)));
  }
  
  public static PendingResult get(GoogleApiClient paramGoogleApiClient, Context paramContext, boolean paramBoolean)
  {
    zzbd.d("Signing out", new Object[0]);
    unregister(paramContext);
    if (paramBoolean) {
      return PendingResults.immediatePendingResult(Status.RESULT_SUCCESS, paramGoogleApiClient);
    }
    return paramGoogleApiClient.execute(new AbstractHttpClient(paramGoogleApiClient));
  }
  
  public static Intent getInstance(Context paramContext, GoogleSignInOptions paramGoogleSignInOptions)
  {
    zzbd.d("getSignInIntent()", new Object[0]);
    paramGoogleSignInOptions = new SignInConfiguration(paramContext.getPackageName(), paramGoogleSignInOptions);
    Intent localIntent = new Intent("com.google.android.gms.auth.GOOGLE_SIGN_IN");
    localIntent.setPackage(paramContext.getPackageName());
    localIntent.setClass(paramContext, SignInHubActivity.class);
    paramContext = new Bundle();
    paramContext.putParcelable("config", paramGoogleSignInOptions);
    localIntent.putExtra("config", paramContext);
    return localIntent;
  }
  
  public static GoogleSignInResult getSignInResultFromIntent(Intent paramIntent)
  {
    if ((paramIntent != null) && ((paramIntent.hasExtra("googleSignInStatus")) || (paramIntent.hasExtra("googleSignInAccount"))))
    {
      GoogleSignInAccount localGoogleSignInAccount = (GoogleSignInAccount)paramIntent.getParcelableExtra("googleSignInAccount");
      paramIntent = (Status)paramIntent.getParcelableExtra("googleSignInStatus");
      if (localGoogleSignInAccount != null) {
        paramIntent = Status.RESULT_SUCCESS;
      }
      return new GoogleSignInResult(localGoogleSignInAccount, paramIntent);
    }
    return null;
  }
  
  public static PendingResult register(GoogleApiClient paramGoogleApiClient, Context paramContext, boolean paramBoolean)
  {
    zzbd.d("Revoking access", new Object[0]);
    String str = Storage.getInstance(paramContext).getSavedRefreshToken();
    unregister(paramContext);
    if (paramBoolean) {
      return ReentrantLock.unlock(str);
    }
    return paramGoogleApiClient.execute(new Hashtable4(paramGoogleApiClient));
  }
  
  public static Intent unregister(Context paramContext, GoogleSignInOptions paramGoogleSignInOptions)
  {
    zzbd.d("getFallbackSignInIntent()", new Object[0]);
    paramContext = getInstance(paramContext, paramGoogleSignInOptions);
    paramContext.setAction("com.google.android.gms.auth.APPAUTH_SIGN_IN");
    return paramContext;
  }
  
  private static void unregister(Context paramContext)
  {
    Addon.get(paramContext).clear();
    paramContext = GoogleApiClient.getAllClients().iterator();
    while (paramContext.hasNext()) {
      ((GoogleApiClient)paramContext.next()).maybeSignOut();
    }
    GoogleApiManager.reportSignOut();
  }
}
