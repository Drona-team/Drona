package com.google.android.gms.common.internal.service;

import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AbstractClientBuilder;
import com.google.android.gms.common.api.Api.ApiOptions.NoOptions;
import com.google.android.gms.common.package_6.Sample;

public final class Common
{
  @KeepForSdk
  public static final com.google.android.gms.common.api.Api.ClientKey<zai> CLIENT_KEY = new com.google.android.gms.common.package_6.Api.ClientKey();
  @KeepForSdk
  public static final Api<Api.ApiOptions.NoOptions> packageName = new Sample("Common.API", zaph, CLIENT_KEY);
  private static final Api.AbstractClientBuilder<zai, Api.ApiOptions.NoOptions> zaph = new BackupWrapper.FroyoAndBeyond();
  public static final RuntimeExceptionDao zapi = new Provider();
  
  public Common() {}
}
