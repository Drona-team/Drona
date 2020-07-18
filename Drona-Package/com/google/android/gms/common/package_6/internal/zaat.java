package com.google.android.gms.common.package_6.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.package_6.GoogleApiClient.OnConnectionFailedListener;
import java.util.concurrent.locks.Lock;

final class zaat
  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
  private zaat(zaak paramZaak) {}
  
  public final void onConnected(Bundle paramBundle)
  {
    if (zaak.get0(zagj).isSignInClientDisconnectFixEnabled())
    {
      zaak.getLock(zagj).lock();
      try
      {
        paramBundle = zaak.createConnection(zagj);
        if (paramBundle == null)
        {
          zaak.getLock(zagj).unlock();
          return;
        }
        paramBundle = zaak.createConnection(zagj);
        paramBundle.zaa((com.google.android.gms.signin.internal.zad)new zaar(zagj));
        zaak.getLock(zagj).unlock();
        return;
      }
      catch (Throwable paramBundle)
      {
        zaak.getLock(zagj).unlock();
        throw paramBundle;
      }
    }
    zaak.createConnection(zagj).zaa((com.google.android.gms.signin.internal.zad)new zaar(zagj));
  }
  
  public final void onConnectionFailed(ConnectionResult paramConnectionResult)
  {
    zaak.getLock(zagj).lock();
    try
    {
      boolean bool = zaak.f(zagj, paramConnectionResult);
      if (bool)
      {
        zaak.ignore(zagj);
        zaak.setEventListener(zagj);
      }
      else
      {
        zaak.setEventListener(zagj, paramConnectionResult);
      }
      zaak.getLock(zagj).unlock();
      return;
    }
    catch (Throwable paramConnectionResult)
    {
      zaak.getLock(zagj).unlock();
      throw paramConnectionResult;
    }
  }
  
  public final void onConnectionSuspended(int paramInt) {}
}
