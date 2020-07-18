package com.google.android.gms.auth.util.signin;

import android.content.Intent;
import com.google.android.gms.common.package_6.GoogleApiClient;
import com.google.android.gms.common.package_6.OptionalPendingResult;
import com.google.android.gms.common.package_6.PendingResult;

public abstract interface GoogleSignInApi
{
  public static final String EXTRA_SIGN_IN_ACCOUNT = "signInAccount";
  
  public abstract Intent getSignInIntent(GoogleApiClient paramGoogleApiClient);
  
  public abstract GoogleSignInResult getSignInResultFromIntent(Intent paramIntent);
  
  public abstract PendingResult revokeAccess(GoogleApiClient paramGoogleApiClient);
  
  public abstract PendingResult signOut(GoogleApiClient paramGoogleApiClient);
  
  public abstract OptionalPendingResult silentSignIn(GoogleApiClient paramGoogleApiClient);
}
