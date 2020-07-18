package com.google.android.gms.common.package_6.internal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.package_6.Api.Client;
import com.google.android.gms.common.package_6.Sample;
import com.google.android.gms.signin.SignInOptions;
import com.google.android.gms.signin.zad;

public final class Errors<O extends Api.ApiOptions>
  extends com.google.android.gms.common.api.GoogleApi<O>
{
  private final com.google.android.gms.common.api.Api.AbstractClientBuilder<? extends zad, SignInOptions> zace;
  private final Api.Client zaer;
  private final Logger zaes;
  private final ClientSettings zaet;
  
  public Errors(Context paramContext, Sample paramSample, Looper paramLooper, Api.Client paramClient, Logger paramLogger, ClientSettings paramClientSettings, com.google.android.gms.common.package_6.Api.AbstractClientBuilder paramAbstractClientBuilder)
  {
    super(paramContext, paramSample, paramLooper);
    zaer = paramClient;
    zaes = paramLogger;
    zaet = paramClientSettings;
    zace = paramAbstractClientBuilder;
    zabm.respondWith(this);
  }
  
  public final Api.Client showToast(Looper paramLooper, GoogleApiManager.zaa paramZaa)
  {
    zaes.v(paramZaa);
    return zaer;
  }
  
  public final zace showToast(Context paramContext, Handler paramHandler)
  {
    return new zace(paramContext, paramHandler, zaet, zace);
  }
  
  public final Api.Client zaab()
  {
    return zaer;
  }
}
