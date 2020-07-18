package com.google.android.gms.common.package_6.internal;

import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.package_6.Api.Client;
import java.util.Collections;
import java.util.Map;

final class zabo
  implements Runnable
{
  zabo(GoogleApiManager.zac paramZac, ConnectionResult paramConnectionResult) {}
  
  public final void run()
  {
    if (zaiz.isSuccess())
    {
      GoogleApiManager.zac.updateButton(zajg, true);
      if (GoogleApiManager.zac.getLinkTarget(zajg).requiresSignIn())
      {
        GoogleApiManager.zac.Zip(zajg);
        return;
      }
      GoogleApiManager.zac localZac = zajg;
      try
      {
        GoogleApiManager.zac.getLinkTarget(localZac).getRemoteService(null, Collections.emptySet());
        return;
      }
      catch (SecurityException localSecurityException)
      {
        Log.e("GoogleApiManager", "Failed to get service from broker. ", localSecurityException);
        ((GoogleApiManager.zaa)GoogleApiManager.isIgnored(zajg.zaim).get(GoogleApiManager.zac.readMessage(zajg))).onConnectionFailed(new ConnectionResult(10));
        return;
      }
    }
    ((GoogleApiManager.zaa)GoogleApiManager.isIgnored(zajg.zaim).get(GoogleApiManager.zac.readMessage(zajg))).onConnectionFailed(zaiz);
  }
}
