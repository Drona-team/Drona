package com.google.android.gms.auth.util.phone;

import android.app.Activity;
import android.content.Context;
import com.google.android.gms.auth.api.phone.SmsRetrieverApi;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AbstractClientBuilder;
import com.google.android.gms.common.api.Api.ApiOptions.NoOptions;
import com.google.android.gms.common.package_6.Sample;
import com.google.android.gms.common.package_6.internal.ApiExceptionMapper;
import com.google.android.gms.internal.auth-api-phone.zzi;
import com.google.android.gms.tasks.Task;

public abstract class SmsRetrieverClient
  extends com.google.android.gms.common.api.GoogleApi<Api.ApiOptions.NoOptions>
  implements SmsRetrieverApi
{
  private static final Api.AbstractClientBuilder<zzi, Api.ApiOptions.NoOptions> CLIENT_BUILDER = new CallerInfo();
  private static final com.google.android.gms.common.api.Api.ClientKey<zzi> CLIENT_KEY = new com.google.android.gms.common.package_6.Api.ClientKey();
  private static final Api<Api.ApiOptions.NoOptions> content = new Sample("SmsRetriever.API", CLIENT_BUILDER, CLIENT_KEY);
  
  public SmsRetrieverClient(Activity paramActivity)
  {
    super(paramActivity, content, null, new ApiExceptionMapper());
  }
  
  public SmsRetrieverClient(Context paramContext)
  {
    super(paramContext, content, null, new ApiExceptionMapper());
  }
  
  public abstract Task startSmsRetriever();
}
