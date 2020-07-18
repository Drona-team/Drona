package com.google.android.gms.common.package_6.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AnyClientKey;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.package_6.Sample;
import com.google.android.gms.internal.base.zap;
import com.google.android.gms.signin.SignInOptions;
import com.google.android.gms.signin.zad;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public final class zabe
  implements zabs, BlockingQueue
{
  private final Context mContext;
  private final com.google.android.gms.common.api.Api.AbstractClientBuilder<? extends zad, SignInOptions> zace;
  final zaaw zaee;
  private final Lock zaeo;
  private final ClientSettings zaet;
  private final Map<Api<?>, Boolean> zaew;
  private final GoogleApiAvailabilityLight zaey;
  final Map<Api.AnyClientKey<?>, com.google.android.gms.common.api.Api.Client> zagz;
  private final Condition zahn;
  private final zabg zaho;
  final Map<Api.AnyClientKey<?>, ConnectionResult> zahp = new HashMap();
  private volatile zabd zahq;
  private ConnectionResult zahr = null;
  int zahs;
  final zabt zaht;
  
  public zabe(Context paramContext, zaaw paramZaaw, Lock paramLock, Looper paramLooper, GoogleApiAvailabilityLight paramGoogleApiAvailabilityLight, Map paramMap1, ClientSettings paramClientSettings, Map paramMap2, com.google.android.gms.common.package_6.Api.AbstractClientBuilder paramAbstractClientBuilder, ArrayList paramArrayList, zabt paramZabt)
  {
    mContext = paramContext;
    zaeo = paramLock;
    zaey = paramGoogleApiAvailabilityLight;
    zagz = paramMap1;
    zaet = paramClientSettings;
    zaew = paramMap2;
    zace = paramAbstractClientBuilder;
    zaee = paramZaaw;
    zaht = paramZabt;
    paramContext = (ArrayList)paramArrayList;
    int j = paramContext.size();
    int i = 0;
    while (i < j)
    {
      paramZaaw = paramContext.get(i);
      i += 1;
      ((Logger)paramZaaw).v(this);
    }
    zaho = new zabg(this, paramLooper);
    zahn = paramLock.newCondition();
    zahq = new zaav(this);
  }
  
  public final ConnectionResult blockingConnect()
  {
    connect();
    for (;;)
    {
      if (!isConnecting()) {
        break label42;
      }
      Condition localCondition = zahn;
      try
      {
        localCondition.await();
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;) {}
      }
    }
    Thread.currentThread().interrupt();
    return new ConnectionResult(15, null);
    label42:
    if (isConnected()) {
      return ConnectionResult.RESULT_SUCCESS;
    }
    if (zahr != null) {
      return zahr;
    }
    return new ConnectionResult(13, null);
  }
  
  public final ConnectionResult blockingConnect(long paramLong, TimeUnit paramTimeUnit)
  {
    connect();
    for (paramLong = paramTimeUnit.toNanos(paramLong);; paramLong = paramTimeUnit.awaitNanos(paramLong))
    {
      if ((!isConnecting()) || (paramLong <= 0L)) {}
      try
      {
        disconnect();
        paramTimeUnit = new ConnectionResult(14, null);
        return paramTimeUnit;
      }
      catch (InterruptedException paramTimeUnit)
      {
        for (;;) {}
      }
      paramTimeUnit = zahn;
    }
    Thread.currentThread().interrupt();
    return new ConnectionResult(15, null);
    if (isConnected()) {
      return ConnectionResult.RESULT_SUCCESS;
    }
    if (zahr != null) {
      return zahr;
    }
    return new ConnectionResult(13, null);
  }
  
  public final void connect()
  {
    zahq.connect();
  }
  
  public final void disconnect()
  {
    if (zahq.disconnect()) {
      zahp.clear();
    }
  }
  
  public final void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    String str = String.valueOf(paramString).concat("  ");
    paramPrintWriter.append(paramString).append("mState=").println(zahq);
    Iterator localIterator = zaew.keySet().iterator();
    while (localIterator.hasNext())
    {
      Sample localSample = (Sample)localIterator.next();
      paramPrintWriter.append(paramString).append(localSample.getName()).println(":");
      ((com.google.android.gms.common.package_6.Api.Client)zagz.get(localSample.getClientKey())).dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  public final BaseImplementation.ApiMethodImpl enqueue(BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    paramApiMethodImpl.put();
    return zahq.enqueue(paramApiMethodImpl);
  }
  
  final void enqueue(zabf paramZabf)
  {
    paramZabf = zaho.obtainMessage(1, paramZabf);
    zaho.sendMessage(paramZabf);
  }
  
  final void enqueue(RuntimeException paramRuntimeException)
  {
    paramRuntimeException = zaho.obtainMessage(2, paramRuntimeException);
    zaho.sendMessage(paramRuntimeException);
  }
  
  public final BaseImplementation.ApiMethodImpl execute(BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    paramApiMethodImpl.put();
    return zahq.execute(paramApiMethodImpl);
  }
  
  public final ConnectionResult getConnectionResult(Sample paramSample)
  {
    paramSample = paramSample.getClientKey();
    if (zagz.containsKey(paramSample))
    {
      if (((com.google.android.gms.common.package_6.Api.Client)zagz.get(paramSample)).isConnected()) {
        return ConnectionResult.RESULT_SUCCESS;
      }
      if (zahp.containsKey(paramSample)) {
        return (ConnectionResult)zahp.get(paramSample);
      }
    }
    return null;
  }
  
  public final boolean isConnected()
  {
    return zahq instanceof zaah;
  }
  
  public final boolean isConnecting()
  {
    return zahq instanceof zaak;
  }
  
  public final boolean maybeSignIn(SignInConnectionListener paramSignInConnectionListener)
  {
    return false;
  }
  
  public final void maybeSignOut() {}
  
  public final void onConnected(Bundle paramBundle)
  {
    zaeo.lock();
    try
    {
      zahq.onConnected(paramBundle);
      zaeo.unlock();
      return;
    }
    catch (Throwable paramBundle)
    {
      zaeo.unlock();
      throw paramBundle;
    }
  }
  
  public final void onConnectionSuspended(int paramInt)
  {
    zaeo.lock();
    try
    {
      zahq.onConnectionSuspended(paramInt);
      zaeo.unlock();
      return;
    }
    catch (Throwable localThrowable)
    {
      zaeo.unlock();
      throw localThrowable;
    }
  }
  
  public final void removeAccount()
  {
    if (isConnected()) {
      ((zaah)zahq).zaam();
    }
  }
  
  public final void startLoading(ConnectionResult paramConnectionResult, Sample paramSample, boolean paramBoolean)
  {
    zaeo.lock();
    try
    {
      zahq.showProgress(paramConnectionResult, paramSample, paramBoolean);
      zaeo.unlock();
      return;
    }
    catch (Throwable paramConnectionResult)
    {
      zaeo.unlock();
      throw paramConnectionResult;
    }
  }
  
  final void wakeup(ConnectionResult paramConnectionResult)
  {
    zaeo.lock();
    try
    {
      zahr = paramConnectionResult;
      zahq = new zaav(this);
      zahq.begin();
      zahn.signalAll();
      zaeo.unlock();
      return;
    }
    catch (Throwable paramConnectionResult)
    {
      zaeo.unlock();
      throw paramConnectionResult;
    }
  }
  
  final void zaaz()
  {
    zaeo.lock();
    try
    {
      zahq = new zaak(this, zaet, zaew, zaey, zace, zaeo, mContext);
      zahq.begin();
      zahn.signalAll();
      zaeo.unlock();
      return;
    }
    catch (Throwable localThrowable)
    {
      zaeo.unlock();
      throw localThrowable;
    }
  }
  
  final void zaba()
  {
    zaeo.lock();
    try
    {
      zaee.zaaw();
      zahq = new zaah(this);
      zahq.begin();
      zahn.signalAll();
      zaeo.unlock();
      return;
    }
    catch (Throwable localThrowable)
    {
      zaeo.unlock();
      throw localThrowable;
    }
  }
}
