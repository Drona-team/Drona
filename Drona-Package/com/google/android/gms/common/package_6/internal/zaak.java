package com.google.android.gms.common.package_6.internal;

import android.content.Context;
import android.os.BaseBundle;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.ClientSettings.OptionalApiSettings;
import com.google.android.gms.common.internal.IAccountAccessor;
import com.google.android.gms.common.internal.ResolveAccountResponse;
import com.google.android.gms.common.package_6.Api.BaseClientBuilder;
import com.google.android.gms.common.package_6.Api.Client;
import com.google.android.gms.common.package_6.GoogleApiClient;
import com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.package_6.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.package_6.Sample;
import com.google.android.gms.signin.SignInOptions;
import com.google.android.gms.signin.internal.zaj;
import com.google.android.gms.signin.zad;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

public final class zaak
  implements zabd
{
  private final Context mContext;
  private final com.google.android.gms.common.api.Api.AbstractClientBuilder<? extends zad, SignInOptions> zace;
  private final Lock zaeo;
  private final ClientSettings zaet;
  private final Map<Api<?>, Boolean> zaew;
  private final GoogleApiAvailabilityLight zaey;
  private ConnectionResult zafh;
  private final zabe zaft;
  private int zafw;
  private int zafx = 0;
  private int zafy;
  private final Bundle zafz = new Bundle();
  private final Set<com.google.android.gms.common.api.Api.AnyClientKey> zaga = new HashSet();
  private zad zagb;
  private boolean zagc;
  private boolean zagd;
  private boolean zage;
  private IAccountAccessor zagf;
  private boolean zagg;
  private boolean zagh;
  private ArrayList<Future<?>> zagi = new ArrayList();
  
  public zaak(zabe paramZabe, ClientSettings paramClientSettings, Map paramMap, GoogleApiAvailabilityLight paramGoogleApiAvailabilityLight, com.google.android.gms.common.package_6.Api.AbstractClientBuilder paramAbstractClientBuilder, Lock paramLock, Context paramContext)
  {
    zaft = paramZabe;
    zaet = paramClientSettings;
    zaew = paramMap;
    zaey = paramGoogleApiAvailabilityLight;
    zace = paramAbstractClientBuilder;
    zaeo = paramLock;
    mContext = paramContext;
  }
  
  private final void f(zaj paramZaj)
  {
    if (!get(0)) {
      return;
    }
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
        Log.wtf("GoogleApiClientConnecting", localStringBuilder.toString(), new Exception());
        isLoggable(paramZaj);
        return;
      }
      zage = true;
      zagf = ((ResolveAccountResponse)localObject).getAccountAccessor();
      zagg = ((ResolveAccountResponse)localObject).getSaveDefaultAccount();
      zagh = ((ResolveAccountResponse)localObject).isFromCrossClientAuth();
      zaap();
      return;
    }
    if (f((ConnectionResult)localObject))
    {
      zaar();
      zaap();
      return;
    }
    isLoggable((ConnectionResult)localObject);
  }
  
  private final boolean f(ConnectionResult paramConnectionResult)
  {
    return (zagc) && (!paramConnectionResult.hasResolution());
  }
  
  private final boolean get(int paramInt)
  {
    if (zafx != paramInt)
    {
      Log.w("GoogleApiClientConnecting", zaft.zaee.zaay());
      Object localObject1 = String.valueOf(this);
      Object localObject2 = new StringBuilder(String.valueOf(localObject1).length() + 23);
      ((StringBuilder)localObject2).append("Unexpected callback in ");
      ((StringBuilder)localObject2).append((String)localObject1);
      Log.w("GoogleApiClientConnecting", ((StringBuilder)localObject2).toString());
      int i = zafy;
      localObject1 = new StringBuilder(33);
      ((StringBuilder)localObject1).append("mRemainingConnections=");
      ((StringBuilder)localObject1).append(i);
      Log.w("GoogleApiClientConnecting", ((StringBuilder)localObject1).toString());
      localObject1 = getType(zafx);
      localObject2 = getType(paramInt);
      StringBuilder localStringBuilder = new StringBuilder(String.valueOf(localObject1).length() + 70 + String.valueOf(localObject2).length());
      localStringBuilder.append("GoogleApiClient connecting is in step ");
      localStringBuilder.append((String)localObject1);
      localStringBuilder.append(" but received callback for step ");
      localStringBuilder.append((String)localObject2);
      Log.wtf("GoogleApiClientConnecting", localStringBuilder.toString(), new Exception());
      isLoggable(new ConnectionResult(8, null));
      return false;
    }
    return true;
  }
  
  private static String getType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN";
    case 1: 
      return "STEP_GETTING_REMOTE_SERVICE";
    }
    return "STEP_SERVICE_BINDINGS_AND_SIGN_IN";
  }
  
  private final void isLoggable(ConnectionResult paramConnectionResult)
  {
    zaas();
    putChannel(paramConnectionResult.hasResolution() ^ true);
    zaft.wakeup(paramConnectionResult);
    zaft.zaht.removeAccount(paramConnectionResult);
  }
  
  private final void putChannel(boolean paramBoolean)
  {
    if (zagb != null)
    {
      if ((zagb.isConnected()) && (paramBoolean)) {
        zagb.zacw();
      }
      zagb.disconnect();
      if (zaet.isSignInClientDisconnectFixEnabled()) {
        zagb = null;
      }
      zagf = null;
    }
  }
  
  private final void setSetting(ConnectionResult paramConnectionResult, Sample paramSample, boolean paramBoolean)
  {
    int m = paramSample.getValue().getPriority();
    int k = 0;
    int j;
    if (paramBoolean)
    {
      if (paramConnectionResult.hasResolution()) {}
      while (zaey.getErrorResolutionIntent(paramConnectionResult.getErrorCode()) != null)
      {
        i = 1;
        break;
      }
      int i = 0;
      j = k;
      if (i == 0) {}
    }
    else if (zafh != null)
    {
      j = k;
      if (m >= zafw) {}
    }
    else
    {
      j = 1;
    }
    if (j != 0)
    {
      zafh = paramConnectionResult;
      zafw = m;
    }
    zaft.zahp.put(paramSample.getClientKey(), paramConnectionResult);
  }
  
  private final boolean zaao()
  {
    zafy -= 1;
    if (zafy > 0) {
      return false;
    }
    if (zafy < 0)
    {
      Log.w("GoogleApiClientConnecting", zaft.zaee.zaay());
      Log.wtf("GoogleApiClientConnecting", "GoogleApiClient received too many callbacks for the given step. Clients may be in an unexpected state; GoogleApiClient will now disconnect.", new Exception());
      isLoggable(new ConnectionResult(8, null));
      return false;
    }
    if (zafh != null)
    {
      zaft.zahs = zafw;
      isLoggable(zafh);
      return false;
    }
    return true;
  }
  
  private final void zaap()
  {
    if (zafy != 0) {
      return;
    }
    if ((!zagd) || (zage))
    {
      ArrayList localArrayList = new ArrayList();
      zafx = 1;
      zafy = zaft.zagz.size();
      Iterator localIterator = zaft.zagz.keySet().iterator();
      while (localIterator.hasNext())
      {
        com.google.android.gms.common.package_6.Api.AnyClientKey localAnyClientKey = (com.google.android.gms.common.package_6.Api.AnyClientKey)localIterator.next();
        if (zaft.zahp.containsKey(localAnyClientKey))
        {
          if (zaao()) {
            zaaq();
          }
        }
        else {
          localArrayList.add((Api.Client)zaft.zagz.get(localAnyClientKey));
        }
      }
      if (!localArrayList.isEmpty()) {
        zagi.add(zabh.zabb().submit(new zaaq(this, localArrayList)));
      }
    }
  }
  
  private final void zaaq()
  {
    zaft.zaba();
    zabh.zabb().execute(new zaal(this));
    if (zagb != null)
    {
      if (zagg) {
        zagb.zaa(zagf, zagh);
      }
      putChannel(false);
    }
    Object localObject = zaft.zahp.keySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      com.google.android.gms.common.package_6.Api.AnyClientKey localAnyClientKey = (com.google.android.gms.common.package_6.Api.AnyClientKey)((Iterator)localObject).next();
      ((Api.Client)zaft.zagz.get(localAnyClientKey)).disconnect();
    }
    if (zafz.isEmpty()) {
      localObject = null;
    } else {
      localObject = zafz;
    }
    zaft.zaht.removeAccount((Bundle)localObject);
  }
  
  private final void zaar()
  {
    zagd = false;
    zaft.zaee.zaha = Collections.emptySet();
    Iterator localIterator = zaga.iterator();
    while (localIterator.hasNext())
    {
      com.google.android.gms.common.package_6.Api.AnyClientKey localAnyClientKey = (com.google.android.gms.common.package_6.Api.AnyClientKey)localIterator.next();
      if (!zaft.zahp.containsKey(localAnyClientKey)) {
        zaft.zahp.put(localAnyClientKey, new ConnectionResult(17, null));
      }
    }
  }
  
  private final void zaas()
  {
    ArrayList localArrayList = (ArrayList)zagi;
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      Object localObject = localArrayList.get(i);
      i += 1;
      ((Future)localObject).cancel(true);
    }
    zagi.clear();
  }
  
  private final Set zaat()
  {
    if (zaet == null) {
      return Collections.emptySet();
    }
    HashSet localHashSet = new HashSet(zaet.getRequiredScopes());
    Map localMap = zaet.getOptionalApiSettings();
    Iterator localIterator = localMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Sample localSample = (Sample)localIterator.next();
      if (!zaft.zahp.containsKey(localSample.getClientKey())) {
        localHashSet.addAll(getmScopes);
      }
    }
    return localHashSet;
  }
  
  public final void begin()
  {
    zaft.zahp.clear();
    zagd = false;
    zafh = null;
    zafx = 0;
    zagc = true;
    zage = false;
    zagg = false;
    HashMap localHashMap = new HashMap();
    Object localObject = zaew.keySet().iterator();
    int i = 0;
    while (((Iterator)localObject).hasNext())
    {
      Sample localSample = (Sample)((Iterator)localObject).next();
      Api.Client localClient = (Api.Client)zaft.zagz.get(localSample.getClientKey());
      int j;
      if (localSample.getValue().getPriority() == 1) {
        j = 1;
      } else {
        j = 0;
      }
      i |= j;
      boolean bool = ((Boolean)zaew.get(localSample)).booleanValue();
      if (localClient.requiresSignIn())
      {
        zagd = true;
        if (bool) {
          zaga.add(localSample.getClientKey());
        } else {
          zagc = false;
        }
      }
      localHashMap.put(localClient, new zaam(this, localSample, bool));
    }
    if (i != 0) {
      zagd = false;
    }
    if (zagd)
    {
      zaet.setClientSessionId(Integer.valueOf(System.identityHashCode(zaft.zaee)));
      localObject = new zaat(this, null);
      zagb = ((zad)zace.buildClient(mContext, zaft.zaee.getLooper(), zaet, zaet.getSignInOptions(), (GoogleApiClient.ConnectionCallbacks)localObject, (GoogleApiClient.OnConnectionFailedListener)localObject));
    }
    zafy = zaft.zagz.size();
    zagi.add(zabh.zabb().submit(new zaan(this, localHashMap)));
  }
  
  public final void connect() {}
  
  public final boolean disconnect()
  {
    zaas();
    putChannel(true);
    zaft.wakeup(null);
    return true;
  }
  
  public final BaseImplementation.ApiMethodImpl enqueue(BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    zaft.zaee.zafc.add(paramApiMethodImpl);
    return paramApiMethodImpl;
  }
  
  public final BaseImplementation.ApiMethodImpl execute(BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    throw new IllegalStateException("GoogleApiClient is not connected yet.");
  }
  
  public final void onConnected(Bundle paramBundle)
  {
    if (!get(1)) {
      return;
    }
    if (paramBundle != null) {
      zafz.putAll(paramBundle);
    }
    if (zaao()) {
      zaaq();
    }
  }
  
  public final void onConnectionSuspended(int paramInt)
  {
    isLoggable(new ConnectionResult(8, null));
  }
  
  public final void showProgress(ConnectionResult paramConnectionResult, Sample paramSample, boolean paramBoolean)
  {
    if (!get(1)) {
      return;
    }
    setSetting(paramConnectionResult, paramSample, paramBoolean);
    if (zaao()) {
      zaaq();
    }
  }
}
