package com.google.android.gms.common.package_6.internal;

import android.content.Context;
import android.os.Looper;
import androidx.collection.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.internal.zai;
import com.google.android.gms.common.api.internal.zaw;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.ClientSettings.OptionalApiSettings;
import com.google.android.gms.common.package_6.Api.AbstractClientBuilder;
import com.google.android.gms.common.package_6.Api.BaseClientBuilder;
import com.google.android.gms.common.package_6.Api.Client;
import com.google.android.gms.common.package_6.GoogleApi;
import com.google.android.gms.common.package_6.PendingResult;
import com.google.android.gms.common.package_6.Sample;
import com.google.android.gms.common.package_6.Status;
import com.google.android.gms.common.util.concurrent.HandlerExecutor;
import com.google.android.gms.tasks.Task;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import javax.annotation.concurrent.GuardedBy;

public final class TaskManager
  implements zabs
{
  private final Looper zabj;
  private final GoogleApiManager zabm;
  private final Lock zaeo;
  private final ClientSettings zaet;
  private final Map<com.google.android.gms.common.api.Api.AnyClientKey<?>, zaw<?>> zaeu = new HashMap();
  private final Map<com.google.android.gms.common.api.Api.AnyClientKey<?>, zaw<?>> zaev = new HashMap();
  private final Map<Api<?>, Boolean> zaew;
  private final zaaw zaex;
  private final GoogleApiAvailabilityLight zaey;
  private final Condition zaez;
  private final boolean zafa;
  private final boolean zafb;
  private final Queue<com.google.android.gms.common.api.internal.BaseImplementation.ApiMethodImpl<?, ?>> zafc = new LinkedList();
  @GuardedBy("mLock")
  private boolean zafd;
  @GuardedBy("mLock")
  private Map<zai<?>, ConnectionResult> zafe;
  @GuardedBy("mLock")
  private Map<zai<?>, ConnectionResult> zaff;
  @GuardedBy("mLock")
  private zaaa zafg;
  @GuardedBy("mLock")
  private ConnectionResult zafh;
  
  public TaskManager(Context paramContext, Lock paramLock, Looper paramLooper, GoogleApiAvailabilityLight paramGoogleApiAvailabilityLight, Map paramMap1, ClientSettings paramClientSettings, Map paramMap2, Api.AbstractClientBuilder paramAbstractClientBuilder, ArrayList paramArrayList, zaaw paramZaaw, boolean paramBoolean)
  {
    zaeo = paramLock;
    zabj = paramLooper;
    zaez = paramLock.newCondition();
    zaey = paramGoogleApiAvailabilityLight;
    zaex = paramZaaw;
    zaew = paramMap2;
    zaet = paramClientSettings;
    zafa = paramBoolean;
    paramLock = new HashMap();
    paramGoogleApiAvailabilityLight = paramMap2.keySet().iterator();
    while (paramGoogleApiAvailabilityLight.hasNext())
    {
      paramMap2 = (Sample)paramGoogleApiAvailabilityLight.next();
      paramLock.put(paramMap2.getClientKey(), paramMap2);
    }
    paramGoogleApiAvailabilityLight = new HashMap();
    paramMap2 = (ArrayList)paramArrayList;
    int j = paramMap2.size();
    int i = 0;
    while (i < j)
    {
      paramArrayList = paramMap2.get(i);
      i += 1;
      paramArrayList = (Logger)paramArrayList;
      paramGoogleApiAvailabilityLight.put(mApi, paramArrayList);
    }
    paramMap1 = paramMap1.entrySet().iterator();
    paramBoolean = true;
    int k = 0;
    j = 1;
    i = 0;
    while (paramMap1.hasNext())
    {
      paramMap2 = (Map.Entry)paramMap1.next();
      paramZaaw = (Sample)paramLock.get(paramMap2.getKey());
      paramArrayList = (Api.Client)paramMap2.getValue();
      if (paramArrayList.requiresGooglePlayServices())
      {
        if (!((Boolean)zaew.get(paramZaaw)).booleanValue()) {
          i = 1;
        }
        k = 1;
      }
      else
      {
        j = 0;
      }
      paramZaaw = new Errors(paramContext, paramZaaw, paramLooper, paramArrayList, (Logger)paramGoogleApiAvailabilityLight.get(paramZaaw), paramClientSettings, paramAbstractClientBuilder);
      zaeu.put((com.google.android.gms.common.package_6.Api.AnyClientKey)paramMap2.getKey(), paramZaaw);
      if (paramArrayList.requiresSignIn()) {
        zaev.put((com.google.android.gms.common.package_6.Api.AnyClientKey)paramMap2.getKey(), paramZaaw);
      }
    }
    if ((k == 0) || (j != 0) || (i != 0)) {
      paramBoolean = false;
    }
    zafb = paramBoolean;
    zabm = GoogleApiManager.zabc();
  }
  
  private final boolean addTask(BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    com.google.android.gms.common.package_6.Api.AnyClientKey localAnyClientKey = paramApiMethodImpl.getClientKey();
    ConnectionResult localConnectionResult = getTask(localAnyClientKey);
    if ((localConnectionResult != null) && (localConnectionResult.getErrorCode() == 4))
    {
      paramApiMethodImpl.setFailedResult(new Status(4, null, zabm.getIntent(((Errors)zaeu.get(localAnyClientKey)).get(), System.identityHashCode(zaex))));
      return true;
    }
    return false;
  }
  
  private final boolean count(Errors paramErrors, ConnectionResult paramConnectionResult)
  {
    return (!paramConnectionResult.isSuccess()) && (!paramConnectionResult.hasResolution()) && (((Boolean)zaew.get(paramErrors.getApi())).booleanValue()) && (paramErrors.zaab().requiresGooglePlayServices()) && (zaey.isUserResolvableError(paramConnectionResult.getErrorCode()));
  }
  
  private final ConnectionResult getTask(com.google.android.gms.common.package_6.Api.AnyClientKey paramAnyClientKey)
  {
    zaeo.lock();
    try
    {
      paramAnyClientKey = (Errors)zaeu.get(paramAnyClientKey);
      Map localMap = zafe;
      if ((localMap != null) && (paramAnyClientKey != null))
      {
        paramAnyClientKey = (ConnectionResult)zafe.get(paramAnyClientKey.get());
        zaeo.unlock();
        return paramAnyClientKey;
      }
      zaeo.unlock();
      return null;
    }
    catch (Throwable paramAnyClientKey)
    {
      zaeo.unlock();
      throw paramAnyClientKey;
    }
  }
  
  private final boolean zaac()
  {
    zaeo.lock();
    try
    {
      boolean bool = zafd;
      if (bool)
      {
        bool = zafa;
        if (bool)
        {
          Iterator localIterator = zaev.keySet().iterator();
          do
          {
            bool = localIterator.hasNext();
            if (!bool) {
              break label94;
            }
            ConnectionResult localConnectionResult = getTask((com.google.android.gms.common.package_6.Api.AnyClientKey)localIterator.next());
            if (localConnectionResult == null) {
              break;
            }
            bool = localConnectionResult.isSuccess();
          } while (bool);
          zaeo.unlock();
          return false;
          label94:
          zaeo.unlock();
          return true;
        }
      }
      zaeo.unlock();
      return false;
    }
    catch (Throwable localThrowable)
    {
      zaeo.unlock();
      throw localThrowable;
    }
  }
  
  private final void zaad()
  {
    if (zaet == null)
    {
      zaex.zaha = Collections.emptySet();
      return;
    }
    HashSet localHashSet = new HashSet(zaet.getRequiredScopes());
    Map localMap = zaet.getOptionalApiSettings();
    Iterator localIterator = localMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Sample localSample = (Sample)localIterator.next();
      ConnectionResult localConnectionResult = getConnectionResult(localSample);
      if ((localConnectionResult != null) && (localConnectionResult.isSuccess())) {
        localHashSet.addAll(getmScopes);
      }
    }
    zaex.zaha = localHashSet;
  }
  
  private final void zaae()
  {
    while (!zafc.isEmpty()) {
      execute((BaseImplementation.ApiMethodImpl)zafc.remove());
    }
    zaex.removeAccount(null);
  }
  
  private final ConnectionResult zaaf()
  {
    Iterator localIterator = zaeu.values().iterator();
    Object localObject2 = null;
    Object localObject1 = null;
    int j = 0;
    int i = 0;
    while (localIterator.hasNext())
    {
      Object localObject3 = (Errors)localIterator.next();
      Sample localSample = ((GoogleApi)localObject3).getApi();
      localObject3 = ((GoogleApi)localObject3).get();
      localObject3 = (ConnectionResult)zafe.get(localObject3);
      if ((!((ConnectionResult)localObject3).isSuccess()) && ((!((Boolean)zaew.get(localSample)).booleanValue()) || (((ConnectionResult)localObject3).hasResolution()) || (zaey.isUserResolvableError(((ConnectionResult)localObject3).getErrorCode()))))
      {
        int k;
        if ((((ConnectionResult)localObject3).getErrorCode() == 4) && (zafa))
        {
          k = localSample.getValue().getPriority();
          if ((localObject1 == null) || (i > k))
          {
            localObject1 = localObject3;
            i = k;
          }
        }
        else
        {
          k = localSample.getValue().getPriority();
          if ((localObject2 == null) || (j > k))
          {
            localObject2 = localObject3;
            j = k;
          }
        }
      }
    }
    if ((localObject2 != null) && (localObject1 != null) && (j > i)) {
      return localObject1;
    }
    return localObject2;
  }
  
  public final ConnectionResult blockingConnect()
  {
    connect();
    for (;;)
    {
      if (!isConnecting()) {
        break label42;
      }
      Condition localCondition = zaez;
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
    if (zafh != null) {
      return zafh;
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
      paramTimeUnit = zaez;
    }
    Thread.currentThread().interrupt();
    return new ConnectionResult(15, null);
    if (isConnected()) {
      return ConnectionResult.RESULT_SUCCESS;
    }
    if (zafh != null) {
      return zafh;
    }
    return new ConnectionResult(13, null);
  }
  
  public final void connect()
  {
    zaeo.lock();
    try
    {
      boolean bool = zafd;
      if (bool)
      {
        zaeo.unlock();
        return;
      }
      zafd = true;
      zafe = null;
      zaff = null;
      zafg = null;
      zafh = null;
      zabm.close();
      zabm.call(zaeu.values()).addOnCompleteListener(new HandlerExecutor(zabj), new LoginActivity.1(this, null));
      zaeo.unlock();
      return;
    }
    catch (Throwable localThrowable)
    {
      zaeo.unlock();
      throw localThrowable;
    }
  }
  
  public final void disconnect()
  {
    zaeo.lock();
    try
    {
      zafd = false;
      zafe = null;
      zaff = null;
      Object localObject = zafg;
      if (localObject != null)
      {
        zafg.cancel();
        zafg = null;
      }
      zafh = null;
      for (;;)
      {
        boolean bool = zafc.isEmpty();
        if (bool) {
          break;
        }
        localObject = (BaseImplementation.ApiMethodImpl)zafc.remove();
        ((BasePendingResult)localObject).remove(null);
        ((PendingResult)localObject).cancel();
      }
      zaez.signalAll();
      zaeo.unlock();
      return;
    }
    catch (Throwable localThrowable)
    {
      zaeo.unlock();
      throw localThrowable;
    }
  }
  
  public final void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {}
  
  public final BaseImplementation.ApiMethodImpl enqueue(BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    if ((zafa) && (addTask(paramApiMethodImpl))) {
      return paramApiMethodImpl;
    }
    if (!isConnected())
    {
      zafc.add(paramApiMethodImpl);
      return paramApiMethodImpl;
    }
    zaex.zahf.close(paramApiMethodImpl);
    return ((Errors)zaeu.get(paramApiMethodImpl.getClientKey())).doRead(paramApiMethodImpl);
  }
  
  public final BaseImplementation.ApiMethodImpl execute(BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    com.google.android.gms.common.package_6.Api.AnyClientKey localAnyClientKey = paramApiMethodImpl.getClientKey();
    if ((zafa) && (addTask(paramApiMethodImpl))) {
      return paramApiMethodImpl;
    }
    zaex.zahf.close(paramApiMethodImpl);
    return ((Errors)zaeu.get(localAnyClientKey)).doWrite(paramApiMethodImpl);
  }
  
  public final ConnectionResult getConnectionResult(Sample paramSample)
  {
    return getTask(paramSample.getClientKey());
  }
  
  public final boolean isConnected()
  {
    zaeo.lock();
    try
    {
      Object localObject = zafe;
      if (localObject != null)
      {
        localObject = zafh;
        if (localObject == null)
        {
          bool = true;
          break label34;
        }
      }
      boolean bool = false;
      label34:
      zaeo.unlock();
      return bool;
    }
    catch (Throwable localThrowable)
    {
      zaeo.unlock();
      throw localThrowable;
    }
  }
  
  public final boolean isConnecting()
  {
    zaeo.lock();
    try
    {
      Map localMap = zafe;
      if (localMap == null)
      {
        bool = zafd;
        if (bool)
        {
          bool = true;
          break label34;
        }
      }
      boolean bool = false;
      label34:
      zaeo.unlock();
      return bool;
    }
    catch (Throwable localThrowable)
    {
      zaeo.unlock();
      throw localThrowable;
    }
  }
  
  public final boolean maybeSignIn(SignInConnectionListener paramSignInConnectionListener)
  {
    zaeo.lock();
    try
    {
      boolean bool = zafd;
      if (bool)
      {
        bool = zaac();
        if (!bool)
        {
          zabm.close();
          zafg = new zaaa(this, paramSignInConnectionListener);
          zabm.call(zaev.values()).addOnCompleteListener(new HandlerExecutor(zabj), zafg);
          zaeo.unlock();
          return true;
        }
      }
      zaeo.unlock();
      return false;
    }
    catch (Throwable paramSignInConnectionListener)
    {
      zaeo.unlock();
      throw paramSignInConnectionListener;
    }
  }
  
  public final void maybeSignOut()
  {
    zaeo.lock();
    try
    {
      zabm.maybeSignOut();
      Object localObject = zafg;
      if (localObject != null)
      {
        zafg.cancel();
        zafg = null;
      }
      localObject = zaff;
      if (localObject == null) {
        zaff = new ArrayMap(zaev.size());
      }
      localObject = new ConnectionResult(4);
      Iterator localIterator = zaev.values().iterator();
      for (;;)
      {
        boolean bool = localIterator.hasNext();
        if (!bool) {
          break;
        }
        Errors localErrors = (Errors)localIterator.next();
        zaff.put(localErrors.get(), localObject);
      }
      localObject = zafe;
      if (localObject != null) {
        zafe.putAll(zaff);
      }
      zaeo.unlock();
      return;
    }
    catch (Throwable localThrowable)
    {
      zaeo.unlock();
      throw localThrowable;
    }
  }
  
  public final void removeAccount() {}
}
