package com.google.android.gms.auth.util;

import android.os.BaseBundle;
import android.os.Bundle;
import com.google.android.gms.auth.api.AuthProxyOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.zzg;
import com.google.android.gms.auth.util.credentials.CredentialsApi;
import com.google.android.gms.auth.util.proxy.ProxyApi;
import com.google.android.gms.auth.util.signin.GoogleSignInApi;
import com.google.android.gms.auth.util.signin.internal.Primitive;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AbstractClientBuilder;
import com.google.android.gms.common.package_6.Api.ApiOptions.Optional;
import com.google.android.gms.common.package_6.Sample;
import com.google.android.gms.internal.auth-api.zzi;
import com.google.android.gms.internal.auth-api.zzr;

public final class Auth
{
  public static final Api<com.google.android.gms.auth.api.Auth.AuthCredentialsOptions> CREDENTIALS_API;
  public static final CredentialsApi CredentialsApi = (CredentialsApi)new zzi();
  public static final Api<GoogleSignInOptions> GOOGLE_SIGN_IN_API;
  public static final GoogleSignInApi GoogleSignInApi = new Primitive();
  private static final Api.AbstractClientBuilder<zzg, GoogleSignInOptions> MIN;
  private static final Api.AbstractClientBuilder<zzr, com.google.android.gms.auth.api.Auth.AuthCredentialsOptions> MINUS;
  public static final com.google.android.gms.common.api.Api.ClientKey<zzg> PLUS;
  @Deprecated
  @KeepForSdk
  public static final Api<AuthProxyOptions> PROXY_API;
  @Deprecated
  @KeepForSdk
  public static final ProxyApi ProxyApi;
  public static final com.google.android.gms.common.api.Api.ClientKey<zzr> UNINITIALIZED = new com.google.android.gms.common.package_6.Api.ClientKey();
  
  static
  {
    PLUS = new com.google.android.gms.common.package_6.Api.ClientKey();
    MINUS = new MathArrays.OrderDirection();
    MIN = new ASN1Null();
    PROXY_API = AuthProxy.BOTH;
    CREDENTIALS_API = new Sample("Auth.CREDENTIALS_API", MINUS, UNINITIALIZED);
    GOOGLE_SIGN_IN_API = new Sample("Auth.GOOGLE_SIGN_IN_API", MIN, PLUS);
    ProxyApi = AuthProxy.ProxyApi;
  }
  
  private Auth() {}
  
  @Deprecated
  public class AuthCredentialsOptions
    implements Api.ApiOptions.Optional
  {
    private static final AuthCredentialsOptions magenta = new Builder().fromUri();
    private final String error = null;
    private final boolean notify;
    
    public AuthCredentialsOptions()
    {
      notify = mSortAscending.booleanValue();
    }
    
    public final Bundle toBundle()
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("consumer_package", null);
      localBundle.putBoolean("force_save_dialog", notify);
      return localBundle;
    }
    
    @Deprecated
    public class Builder
    {
      protected Boolean mSortAscending = Boolean.valueOf(false);
      
      public Builder() {}
      
      public Builder forceEnableSaveDialog()
      {
        mSortAscending = Boolean.valueOf(true);
        return this;
      }
      
      public Auth.AuthCredentialsOptions fromUri()
      {
        return new Auth.AuthCredentialsOptions(this);
      }
    }
  }
}
