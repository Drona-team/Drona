package com.google.android.gms.auth.util;

import com.google.android.gms.auth.api.AuthProxyOptions;
import com.google.android.gms.auth.util.proxy.ProxyApi;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AbstractClientBuilder;
import com.google.android.gms.common.package_6.Sample;
import com.google.android.gms.internal.auth.zzak;
import com.google.android.gms.internal.auth.zzar;

@KeepForSdk
public final class AuthProxy
{
  @KeepForSdk
  public static final Api<AuthProxyOptions> BOTH = new Sample("Auth.PROXY_API", zzai, zzah);
  @KeepForSdk
  public static final ProxyApi ProxyApi = (ProxyApi)new zzar();
  private static final com.google.android.gms.common.api.Api.ClientKey<zzak> zzah = new com.google.android.gms.common.package_6.Api.ClientKey();
  private static final Api.AbstractClientBuilder<zzak, AuthProxyOptions> zzai = new ASN1OctetString();
  
  public AuthProxy() {}
}
