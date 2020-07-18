package com.google.android.gms.auth.util.signin.internal;

import android.content.Intent;
import com.google.android.gms.auth.util.Auth;
import com.google.android.gms.auth.util.signin.GoogleSignInApi;
import com.google.android.gms.auth.util.signin.GoogleSignInOptions;
import com.google.android.gms.auth.util.signin.GoogleSignInResult;
import com.google.android.gms.common.package_6.GoogleApiClient;
import com.google.android.gms.common.package_6.OptionalPendingResult;
import com.google.android.gms.common.package_6.PendingResult;

public final class Primitive
  implements GoogleSignInApi
{
  public Primitive() {}
  
  private static GoogleSignInOptions unwrap(GoogleApiClient paramGoogleApiClient)
  {
    return ((Configurable)paramGoogleApiClient.getClient(Auth.PLUS)).getBaseContext();
  }
  
  public final Intent getSignInIntent(GoogleApiClient paramGoogleApiClient)
  {
    return Bus.getInstance(paramGoogleApiClient.getContext(), unwrap(paramGoogleApiClient));
  }
  
  public final GoogleSignInResult getSignInResultFromIntent(Intent paramIntent)
  {
    return Bus.getSignInResultFromIntent(paramIntent);
  }
  
  public final PendingResult revokeAccess(GoogleApiClient paramGoogleApiClient)
  {
    return Bus.register(paramGoogleApiClient, paramGoogleApiClient.getContext(), false);
  }
  
  public final PendingResult signOut(GoogleApiClient paramGoogleApiClient)
  {
    return Bus.get(paramGoogleApiClient, paramGoogleApiClient.getContext(), false);
  }
  
  public final OptionalPendingResult silentSignIn(GoogleApiClient paramGoogleApiClient)
  {
    return Bus.get(paramGoogleApiClient, paramGoogleApiClient.getContext(), unwrap(paramGoogleApiClient), false);
  }
}
