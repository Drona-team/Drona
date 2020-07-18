package com.google.android.gms.auth.util.signin.internal;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import com.google.android.gms.auth.api.signin.internal.zzu;
import com.google.android.gms.auth.util.signin.GoogleSignInOptions;
import com.google.android.gms.auth.util.signin.GoogleSignInOptions.Builder;
import com.google.android.gms.common.internal.BaseGmsClient;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.GmsClient;
import com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.package_6.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.package_6.Scope;
import java.util.Iterator;
import java.util.Set;

public final class Configurable
  extends GmsClient<zzu>
{
  private final GoogleSignInOptions zzbi;
  
  public Configurable(Context paramContext, Looper paramLooper, ClientSettings paramClientSettings, GoogleSignInOptions paramGoogleSignInOptions, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    super(paramContext, paramLooper, 91, paramClientSettings, paramConnectionCallbacks, paramOnConnectionFailedListener);
    if (paramGoogleSignInOptions == null) {
      paramGoogleSignInOptions = new GoogleSignInOptions.Builder().build();
    }
    paramContext = paramGoogleSignInOptions;
    if (!paramClientSettings.getAllRequestedScopes().isEmpty())
    {
      paramContext = new GoogleSignInOptions.Builder(paramGoogleSignInOptions);
      paramLooper = paramClientSettings.getAllRequestedScopes().iterator();
      while (paramLooper.hasNext()) {
        paramContext.requestScopes((Scope)paramLooper.next(), new Scope[0]);
      }
      paramContext = paramContext.build();
    }
    zzbi = paramContext;
  }
  
  public final GoogleSignInOptions getBaseContext()
  {
    return zzbi;
  }
  
  public final int getMinApkVersion()
  {
    return 12451000;
  }
  
  protected final String getServiceDescriptor()
  {
    return "com.google.android.gms.auth.api.signin.internal.ISignInService";
  }
  
  public final Intent getSignInIntent()
  {
    return Bus.getInstance(getContext(), zzbi);
  }
  
  protected final String getStartServiceAction()
  {
    return "com.google.android.gms.auth.api.signin.service.START";
  }
  
  public final boolean providesSignIn()
  {
    return true;
  }
}
