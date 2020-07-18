package com.google.android.gms.auth.account;

import android.app.Activity;
import android.content.Context;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AbstractClientBuilder;
import com.google.android.gms.common.api.Api.ApiOptions.NoOptions;
import com.google.android.gms.common.package_6.Sample;
import com.google.android.gms.internal.auth.zzh;
import com.google.android.gms.internal.auth.zzr;

public class WorkAccount
{
  private static final Api.AbstractClientBuilder<zzr, Api.ApiOptions.NoOptions> CLIENT_BUILDER = new AccountTable();
  private static final com.google.android.gms.common.api.Api.ClientKey<zzr> CLIENT_KEY = new com.google.android.gms.common.package_6.Api.ClientKey();
  public static final Api<Api.ApiOptions.NoOptions> IOERR = new Sample("WorkAccount.API", CLIENT_BUILDER, CLIENT_KEY);
  @Deprecated
  public static final WorkAccountApi WorkAccountApi = (WorkAccountApi)new zzh();
  
  private WorkAccount() {}
  
  public static WorkAccountClient getClient(Activity paramActivity)
  {
    return new WorkAccountClient(paramActivity);
  }
  
  public static WorkAccountClient getClient(Context paramContext)
  {
    return new WorkAccountClient(paramContext);
  }
}
