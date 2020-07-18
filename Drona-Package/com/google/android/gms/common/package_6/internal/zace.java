package com.google.android.gms.common.package_6.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ResolveAccountResponse;
import com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.package_6.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.signin.SignInOptions;
import com.google.android.gms.signin.internal.zac;
import com.google.android.gms.signin.internal.zaj;
import com.google.android.gms.signin.zaa;
import java.util.Set;

public final class zace
  extends zac
  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
  private static com.google.android.gms.common.api.Api.AbstractClientBuilder<? extends com.google.android.gms.signin.zad, SignInOptions> zaki = zaa.zaph;
  private final Context mContext;
  private final Handler mHandler;
  private Set<Scope> mScopes;
  private final com.google.android.gms.common.api.Api.AbstractClientBuilder<? extends com.google.android.gms.signin.zad, SignInOptions> zaau;
  private ClientSettings zaet;
  private com.google.android.gms.signin.zad zagb;
  private zach zakj;
  
  public zace(Context paramContext, Handler paramHandler, ClientSettings paramClientSettings)
  {
    this(paramContext, paramHandler, paramClientSettings, zaki);
  }
  
  public zace(Context paramContext, Handler paramHandler, ClientSettings paramClientSettings, com.google.android.gms.common.package_6.Api.AbstractClientBuilder paramAbstractClientBuilder)
  {
    mContext = paramContext;
    mHandler = paramHandler;
    zaet = ((ClientSettings)Preconditions.checkNotNull(paramClientSettings, "ClientSettings must not be null"));
    mScopes = paramClientSettings.getRequiredScopes();
    zaau = paramAbstractClientBuilder;
  }
  
  private final void doSync(zaj paramZaj)
  {
    Object localObject = paramZaj.getConnectionResult();
    if (((ConnectionResult)localObject).isSuccess())
    {
      localObject = paramZaj.zacx();
      paramZaj = ((ResolveAccountResponse)localObject).getConnectionResult();
      if (!paramZaj.isSuccess())
      {
        localObject = String.valueOf(paramZaj);
        StringBuilder localStringBuilder = new StringBuilder(String.valueOf(localObject).length() + 48);
        localStringBuilder.append("Sign-in succeeded with resolve account failure: ");
        localStringBuilder.append((String)localObject);
        Log.wtf("SignInCoordinator", localStringBuilder.toString(), new Exception());
        zakj.ignore(paramZaj);
        zagb.disconnect();
        return;
      }
      zakj.startSession(((ResolveAccountResponse)localObject).getAccountAccessor(), mScopes);
    }
    else
    {
      zakj.ignore((ConnectionResult)localObject);
    }
    zagb.disconnect();
  }
  
  public final void TakePhoto(zaj paramZaj)
  {
    mHandler.post(new zacg(this, paramZaj));
  }
  
  public final void onConnected(Bundle paramBundle)
  {
    zagb.zaa((com.google.android.gms.signin.internal.zad)this);
  }
  
  public final void onConnectionFailed(ConnectionResult paramConnectionResult)
  {
    zakj.ignore(paramConnectionResult);
  }
  
  public final void onConnectionSuspended(int paramInt)
  {
    zagb.disconnect();
  }
  
  public final void retrieveToken(zach paramZach)
  {
    if (zagb != null) {
      zagb.disconnect();
    }
    zaet.setClientSessionId(Integer.valueOf(System.identityHashCode(this)));
    zagb = ((com.google.android.gms.signin.zad)zaau.buildClient(mContext, mHandler.getLooper(), zaet, zaet.getSignInOptions(), this, this));
    zakj = paramZach;
    if ((mScopes != null) && (!mScopes.isEmpty()))
    {
      zagb.connect();
      return;
    }
    mHandler.post(new zacf(this));
  }
  
  public final com.google.android.gms.signin.zad zabq()
  {
    return zagb;
  }
  
  public final void zabs()
  {
    if (zagb != null) {
      zagb.disconnect();
    }
  }
}
