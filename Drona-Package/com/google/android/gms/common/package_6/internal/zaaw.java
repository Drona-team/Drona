package com.google.android.gms.common.package_6.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.fragment.package_5.FragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.internal.zaq;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.GmsClientEventManager;
import com.google.android.gms.common.internal.GmsClientEventManager.GmsClientEventState;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.common.internal.service.RuntimeExceptionDao;
import com.google.android.gms.common.package_6.GoogleApiClient;
import com.google.android.gms.common.package_6.GoogleApiClient.Builder;
import com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.package_6.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.package_6.PendingResult;
import com.google.android.gms.common.package_6.Sample;
import com.google.android.gms.common.package_6.Status;
import com.google.android.gms.common.util.ClientLibraryUtils;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.internal.base.zap;
import com.google.android.gms.signin.SignInOptions;
import com.google.android.gms.signin.zad;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

public final class zaaw
  extends GoogleApiClient
  implements zabt
{
  private final Context mContext;
  private final Looper zabj;
  private final int zacb;
  private final GoogleApiAvailability zacd;
  private final com.google.android.gms.common.api.Api.AbstractClientBuilder<? extends zad, SignInOptions> zace;
  private boolean zach;
  private final Lock zaeo;
  private final ClientSettings zaet;
  private final Map<Api<?>, Boolean> zaew;
  @VisibleForTesting
  final Queue<com.google.android.gms.common.api.internal.BaseImplementation.ApiMethodImpl<?, ?>> zafc = new LinkedList();
  private final GmsClientEventManager zags;
  private zabs zagt = null;
  private volatile boolean zagu;
  private long zagv;
  private long zagw;
  private final zabb zagx;
  @VisibleForTesting
  private zabq zagy;
  final Map<com.google.android.gms.common.api.Api.AnyClientKey<?>, com.google.android.gms.common.api.Api.Client> zagz;
  Set<Scope> zaha;
  private final ListenerHolders zahb;
  private final ArrayList<zaq> zahc;
  private Integer zahd;
  Set<com.google.android.gms.common.api.internal.zacm> zahe;
  final zacp zahf;
  private final GmsClientEventManager.GmsClientEventState zahg;
  
  public zaaw(Context paramContext, Lock paramLock, Looper paramLooper, ClientSettings paramClientSettings, GoogleApiAvailability paramGoogleApiAvailability, com.google.android.gms.common.package_6.Api.AbstractClientBuilder paramAbstractClientBuilder, Map paramMap1, List paramList1, List paramList2, Map paramMap2, int paramInt1, int paramInt2, ArrayList paramArrayList, boolean paramBoolean)
  {
    long l;
    if (ClientLibraryUtils.isPackageSide()) {
      l = 10000L;
    } else {
      l = 120000L;
    }
    zagv = l;
    zagw = 5000L;
    zaha = new HashSet();
    zahb = new ListenerHolders();
    zahd = null;
    zahe = null;
    zahg = new zaax(this);
    mContext = paramContext;
    zaeo = paramLock;
    zach = false;
    zags = new GmsClientEventManager(paramLooper, zahg);
    zabj = paramLooper;
    zagx = new zabb(this, paramLooper);
    zacd = paramGoogleApiAvailability;
    zacb = paramInt1;
    if (zacb >= 0) {
      zahd = Integer.valueOf(paramInt2);
    }
    zaew = paramMap1;
    zagz = paramMap2;
    zahc = paramArrayList;
    zahf = new zacp(zagz);
    paramContext = paramList1.iterator();
    while (paramContext.hasNext())
    {
      paramLock = (GoogleApiClient.ConnectionCallbacks)paramContext.next();
      zags.registerConnectionCallbacks(paramLock);
    }
    paramContext = paramList2.iterator();
    while (paramContext.hasNext())
    {
      paramLock = (GoogleApiClient.OnConnectionFailedListener)paramContext.next();
      zags.registerConnectionFailedListener(paramLock);
    }
    zaet = paramClientSettings;
    zace = paramAbstractClientBuilder;
  }
  
  private final void onMessage(int paramInt)
  {
    if (zahd == null) {
      zahd = Integer.valueOf(paramInt);
    } else {
      if (zahd.intValue() != paramInt) {
        break label406;
      }
    }
    if (zagt != null) {
      return;
    }
    Object localObject1 = zagz.values().iterator();
    int i = 0;
    paramInt = 0;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (com.google.android.gms.common.package_6.Api.Client)((Iterator)localObject1).next();
      int j = i;
      if (((com.google.android.gms.common.package_6.Api.Client)localObject2).requiresSignIn()) {
        j = 1;
      }
      i = j;
      if (((com.google.android.gms.common.package_6.Api.Client)localObject2).providesSignIn())
      {
        paramInt = 1;
        i = j;
      }
    }
    switch (zahd.intValue())
    {
    default: 
      break;
    case 2: 
      if (i != 0)
      {
        if (zach)
        {
          zagt = new TaskManager(mContext, zaeo, zabj, zacd, zagz, zaet, zaew, zace, zahc, this, true);
          return;
        }
        zagt = SocketIOClient.validate(mContext, this, zaeo, zabj, zacd, zagz, zaet, zaew, zace, zahc);
        return;
      }
      break;
    case 1: 
      if (i != 0)
      {
        if (paramInt != 0) {
          throw new IllegalStateException("Cannot use SIGN_IN_MODE_REQUIRED with GOOGLE_SIGN_IN_API. Use connect(SIGN_IN_MODE_OPTIONAL) instead.");
        }
      }
      else {
        throw new IllegalStateException("SIGN_IN_MODE_REQUIRED cannot be used on a GoogleApiClient that does not contain any authenticated APIs. Use connect() instead.");
      }
      break;
    }
    if ((zach) && (paramInt == 0))
    {
      zagt = new TaskManager(mContext, zaeo, zabj, zacd, zagz, zaet, zaew, zace, zahc, this, false);
      return;
    }
    zagt = new zabe(mContext, this, zaeo, zabj, zacd, zagz, zaet, zaew, zace, zahc, this);
    return;
    label406:
    localObject1 = toString(paramInt);
    Object localObject2 = toString(zahd.intValue());
    StringBuilder localStringBuilder = new StringBuilder(String.valueOf(localObject1).length() + 51 + String.valueOf(localObject2).length());
    localStringBuilder.append("Cannot use sign-in mode: ");
    localStringBuilder.append((String)localObject1);
    localStringBuilder.append(". Mode was already set to ");
    localStringBuilder.append((String)localObject2);
    throw new IllegalStateException(localStringBuilder.toString());
  }
  
  private final void resume()
  {
    zaeo.lock();
    try
    {
      boolean bool = zagu;
      if (bool) {
        zaau();
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
  
  private static String toString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN";
    case 3: 
      return "SIGN_IN_MODE_NONE";
    case 2: 
      return "SIGN_IN_MODE_OPTIONAL";
    }
    return "SIGN_IN_MODE_REQUIRED";
  }
  
  public static int transform(Iterable paramIterable, boolean paramBoolean)
  {
    paramIterable = paramIterable.iterator();
    int j = 0;
    int i = 0;
    while (paramIterable.hasNext())
    {
      com.google.android.gms.common.package_6.Api.Client localClient = (com.google.android.gms.common.package_6.Api.Client)paramIterable.next();
      int k = j;
      if (localClient.requiresSignIn()) {
        k = 1;
      }
      j = k;
      if (localClient.providesSignIn())
      {
        i = 1;
        j = k;
      }
    }
    if (j != 0)
    {
      if (i != 0)
      {
        if (paramBoolean) {
          return 2;
        }
      }
      else {
        return 1;
      }
    }
    else {
      return 3;
    }
    return 1;
  }
  
  private final void writeToFile(GoogleApiClient paramGoogleApiClient, StatusPendingResult paramStatusPendingResult, boolean paramBoolean)
  {
    Common.zapi.query(paramGoogleApiClient).setResultCallback(new zaba(this, paramStatusPendingResult, paramBoolean, paramGoogleApiClient));
  }
  
  private final void zaau()
  {
    zags.enableCallbacks();
    zagt.connect();
  }
  
  private final void zaav()
  {
    zaeo.lock();
    try
    {
      boolean bool = zaaw();
      if (bool) {
        zaau();
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
  
  public final ConnectionResult blockingConnect()
  {
    Object localObject = Looper.myLooper();
    Looper localLooper = Looper.getMainLooper();
    boolean bool2 = true;
    boolean bool1;
    if (localObject != localLooper) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkState(bool1, "blockingConnect must not be called on the UI thread");
    zaeo.lock();
    try
    {
      int i = zacb;
      if (i >= 0)
      {
        localObject = zahd;
        if (localObject != null) {
          bool1 = bool2;
        } else {
          bool1 = false;
        }
        Preconditions.checkState(bool1, "Sign-in mode should have been set explicitly by auto-manage.");
      }
      else
      {
        localObject = zahd;
        if (localObject == null)
        {
          zahd = Integer.valueOf(transform(zagz.values(), false));
        }
        else
        {
          i = zahd.intValue();
          if (i == 2) {
            break label167;
          }
        }
      }
      onMessage(zahd.intValue());
      zags.enableCallbacks();
      localObject = zagt.blockingConnect();
      zaeo.unlock();
      return localObject;
      label167:
      throw new IllegalStateException("Cannot call blockingConnect() when sign-in mode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
    }
    catch (Throwable localThrowable)
    {
      zaeo.unlock();
      throw localThrowable;
    }
  }
  
  public final ConnectionResult blockingConnect(long paramLong, TimeUnit paramTimeUnit)
  {
    boolean bool;
    if (Looper.myLooper() != Looper.getMainLooper()) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkState(bool, "blockingConnect must not be called on the UI thread");
    Preconditions.checkNotNull(paramTimeUnit, "TimeUnit must not be null");
    zaeo.lock();
    try
    {
      Integer localInteger = zahd;
      if (localInteger == null)
      {
        zahd = Integer.valueOf(transform(zagz.values(), false));
      }
      else
      {
        int i = zahd.intValue();
        if (i == 2) {
          break label133;
        }
      }
      onMessage(zahd.intValue());
      zags.enableCallbacks();
      paramTimeUnit = zagt.blockingConnect(paramLong, paramTimeUnit);
      zaeo.unlock();
      return paramTimeUnit;
      label133:
      throw new IllegalStateException("Cannot call blockingConnect() when sign-in mode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
    }
    catch (Throwable paramTimeUnit)
    {
      zaeo.unlock();
      throw paramTimeUnit;
    }
  }
  
  public final PendingResult clearDefaultAccountAndReconnect()
  {
    Preconditions.checkState(isConnected(), "GoogleApiClient is not connected yet.");
    boolean bool;
    if (zahd.intValue() != 2) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkState(bool, "Cannot use clearDefaultAccountAndReconnect with GOOGLE_SIGN_IN_API");
    StatusPendingResult localStatusPendingResult = new StatusPendingResult(this);
    if (zagz.containsKey(Common.CLIENT_KEY))
    {
      writeToFile(this, localStatusPendingResult, false);
      return localStatusPendingResult;
    }
    AtomicReference localAtomicReference = new AtomicReference();
    Object localObject = new zaay(this, localAtomicReference, localStatusPendingResult);
    zaaz localZaaz = new zaaz(this, localStatusPendingResult);
    localObject = new GoogleApiClient.Builder(mContext).addApi(Common.packageName).addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks)localObject).addOnConnectionFailedListener(localZaaz).setHandler((Handler)zagx).build();
    localAtomicReference.set(localObject);
    ((GoogleApiClient)localObject).connect();
    return localStatusPendingResult;
  }
  
  public final void connect()
  {
    zaeo.lock();
    try
    {
      int i = zacb;
      boolean bool = false;
      Integer localInteger;
      if (i >= 0)
      {
        localInteger = zahd;
        if (localInteger != null) {
          bool = true;
        }
        Preconditions.checkState(bool, "Sign-in mode should have been set explicitly by auto-manage.");
      }
      else
      {
        localInteger = zahd;
        if (localInteger == null)
        {
          zahd = Integer.valueOf(transform(zagz.values(), false));
        }
        else
        {
          i = zahd.intValue();
          if (i == 2) {
            break label107;
          }
        }
      }
      connect(zahd.intValue());
      zaeo.unlock();
      return;
      label107:
      throw new IllegalStateException("Cannot call connect() when SignInMode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
    }
    catch (Throwable localThrowable)
    {
      zaeo.unlock();
      throw localThrowable;
    }
  }
  
  public final void connect(int paramInt)
  {
    zaeo.lock();
    boolean bool2 = true;
    boolean bool1 = bool2;
    if (paramInt != 3)
    {
      bool1 = bool2;
      if (paramInt != 1) {
        if (paramInt == 2) {
          bool1 = bool2;
        } else {
          bool1 = false;
        }
      }
    }
    try
    {
      StringBuilder localStringBuilder = new StringBuilder(33);
      localStringBuilder.append("Illegal sign-in mode: ");
      localStringBuilder.append(paramInt);
      Preconditions.checkArgument(bool1, localStringBuilder.toString());
      onMessage(paramInt);
      zaau();
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
      zahf.release();
      Object localObject = zagt;
      if (localObject != null) {
        zagt.disconnect();
      }
      zahb.release();
      localObject = zafc.iterator();
      for (;;)
      {
        boolean bool = ((Iterator)localObject).hasNext();
        if (!bool) {
          break;
        }
        BaseImplementation.ApiMethodImpl localApiMethodImpl = (BaseImplementation.ApiMethodImpl)((Iterator)localObject).next();
        localApiMethodImpl.remove(null);
        localApiMethodImpl.cancel();
      }
      zafc.clear();
      localObject = zagt;
      if (localObject == null)
      {
        zaeo.unlock();
        return;
      }
      zaaw();
      zags.disableCallbacks();
      zaeo.unlock();
      return;
    }
    catch (Throwable localThrowable)
    {
      zaeo.unlock();
      throw localThrowable;
    }
  }
  
  public final void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.append(paramString).append("mContext=").println(mContext);
    paramPrintWriter.append(paramString).append("mResuming=").print(zagu);
    paramPrintWriter.append(" mWorkQueue.size()=").print(zafc.size());
    zacp localZacp = zahf;
    paramPrintWriter.append(" mUnconsumedApiCalls.size()=").println(zakz.size());
    if (zagt != null) {
      zagt.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  public final BaseImplementation.ApiMethodImpl enqueue(BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    if (paramApiMethodImpl.getClientKey() != null) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool, "This task can not be enqueued (it's probably a Batch or malformed)");
    boolean bool = zagz.containsKey(paramApiMethodImpl.getClientKey());
    Object localObject;
    if (paramApiMethodImpl.getApi() != null) {
      localObject = paramApiMethodImpl.getApi().getName();
    } else {
      localObject = "the API";
    }
    StringBuilder localStringBuilder = new StringBuilder(String.valueOf(localObject).length() + 65);
    localStringBuilder.append("GoogleApiClient is not configured to use ");
    localStringBuilder.append((String)localObject);
    localStringBuilder.append(" required for this call.");
    Preconditions.checkArgument(bool, localStringBuilder.toString());
    zaeo.lock();
    try
    {
      localObject = zagt;
      if (localObject == null)
      {
        zafc.add(paramApiMethodImpl);
        zaeo.unlock();
        return paramApiMethodImpl;
      }
      paramApiMethodImpl = zagt.enqueue(paramApiMethodImpl);
      zaeo.unlock();
      return paramApiMethodImpl;
    }
    catch (Throwable paramApiMethodImpl)
    {
      zaeo.unlock();
      throw paramApiMethodImpl;
    }
  }
  
  public final void ensureInitialized(zacm paramZacm)
  {
    zaeo.lock();
    try
    {
      Set localSet = zahe;
      if (localSet == null) {
        zahe = new HashSet();
      }
      zahe.add(paramZacm);
      zaeo.unlock();
      return;
    }
    catch (Throwable paramZacm)
    {
      zaeo.unlock();
      throw paramZacm;
    }
  }
  
  public final BaseImplementation.ApiMethodImpl execute(BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    if (paramApiMethodImpl.getClientKey() != null) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool, "This task can not be executed (it's probably a Batch or malformed)");
    boolean bool = zagz.containsKey(paramApiMethodImpl.getClientKey());
    Object localObject;
    if (paramApiMethodImpl.getApi() != null) {
      localObject = paramApiMethodImpl.getApi().getName();
    } else {
      localObject = "the API";
    }
    StringBuilder localStringBuilder = new StringBuilder(String.valueOf(localObject).length() + 65);
    localStringBuilder.append("GoogleApiClient is not configured to use ");
    localStringBuilder.append((String)localObject);
    localStringBuilder.append(" required for this call.");
    Preconditions.checkArgument(bool, localStringBuilder.toString());
    zaeo.lock();
    try
    {
      localObject = zagt;
      if (localObject != null)
      {
        bool = zagu;
        if (bool)
        {
          zafc.add(paramApiMethodImpl);
          for (;;)
          {
            bool = zafc.isEmpty();
            if (bool) {
              break;
            }
            localObject = (BaseImplementation.ApiMethodImpl)zafc.remove();
            zahf.close((BasePendingResult)localObject);
            ((BaseImplementation.ApiMethodImpl)localObject).setFailedResult(Status.RESULT_INTERNAL_ERROR);
          }
          zaeo.unlock();
          return paramApiMethodImpl;
        }
        paramApiMethodImpl = zagt.execute(paramApiMethodImpl);
        zaeo.unlock();
        return paramApiMethodImpl;
      }
      throw new IllegalStateException("GoogleApiClient is not connected yet.");
    }
    catch (Throwable paramApiMethodImpl)
    {
      zaeo.unlock();
      throw paramApiMethodImpl;
    }
  }
  
  public final com.google.android.gms.common.package_6.Api.Client getClient(com.google.android.gms.common.package_6.Api.AnyClientKey paramAnyClientKey)
  {
    paramAnyClientKey = (com.google.android.gms.common.package_6.Api.Client)zagz.get(paramAnyClientKey);
    Preconditions.checkNotNull(paramAnyClientKey, "Appropriate Api was not requested.");
    return paramAnyClientKey;
  }
  
  public final ConnectionResult getConnectionResult(Sample paramSample)
  {
    zaeo.lock();
    try
    {
      boolean bool = isConnected();
      if (!bool)
      {
        bool = zagu;
        if (!bool) {
          throw new IllegalStateException("Cannot invoke getConnectionResult unless GoogleApiClient is connected");
        }
      }
      bool = zagz.containsKey(paramSample.getClientKey());
      if (bool)
      {
        ConnectionResult localConnectionResult = zagt.getConnectionResult(paramSample);
        if (localConnectionResult == null)
        {
          bool = zagu;
          if (bool)
          {
            paramSample = ConnectionResult.RESULT_SUCCESS;
            zaeo.unlock();
            return paramSample;
          }
          Log.w("GoogleApiClientImpl", zaay());
          Log.wtf("GoogleApiClientImpl", String.valueOf(paramSample.getName()).concat(" requested in getConnectionResult is not connected but is not present in the failed  connections map"), new Exception());
          paramSample = new ConnectionResult(8, null);
          zaeo.unlock();
          return paramSample;
        }
        zaeo.unlock();
        return localConnectionResult;
      }
      throw new IllegalArgumentException(String.valueOf(paramSample.getName()).concat(" was never registered with GoogleApiClient"));
    }
    catch (Throwable paramSample)
    {
      zaeo.unlock();
      throw paramSample;
    }
  }
  
  public final Context getContext()
  {
    return mContext;
  }
  
  public final Looper getLooper()
  {
    return zabj;
  }
  
  public final boolean hasApi(Sample paramSample)
  {
    return zagz.containsKey(paramSample.getClientKey());
  }
  
  public final boolean hasConnectedApi(Sample paramSample)
  {
    if (!isConnected()) {
      return false;
    }
    paramSample = (com.google.android.gms.common.package_6.Api.Client)zagz.get(paramSample.getClientKey());
    return (paramSample != null) && (paramSample.isConnected());
  }
  
  public final boolean isConnected()
  {
    return (zagt != null) && (zagt.isConnected());
  }
  
  public final boolean isConnecting()
  {
    return (zagt != null) && (zagt.isConnecting());
  }
  
  public final boolean isConnectionCallbacksRegistered(GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    return zags.isConnectionCallbacksRegistered(paramConnectionCallbacks);
  }
  
  public final boolean isConnectionFailedListenerRegistered(GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    return zags.isConnectionFailedListenerRegistered(paramOnConnectionFailedListener);
  }
  
  public final boolean maybeSignIn(SignInConnectionListener paramSignInConnectionListener)
  {
    return (zagt != null) && (zagt.maybeSignIn(paramSignInConnectionListener));
  }
  
  public final void maybeSignOut()
  {
    if (zagt != null) {
      zagt.maybeSignOut();
    }
  }
  
  public final void reconnect()
  {
    disconnect();
    connect();
  }
  
  public final void registerConnectionCallbacks(GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    zags.registerConnectionCallbacks(paramConnectionCallbacks);
  }
  
  public final void registerConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    zags.registerConnectionFailedListener(paramOnConnectionFailedListener);
  }
  
  public final ListenerHolder registerListener(Object paramObject)
  {
    zaeo.lock();
    try
    {
      paramObject = zahb.addOnChangeListener(paramObject, zabj, "NO_TYPE");
      zaeo.unlock();
      return paramObject;
    }
    catch (Throwable paramObject)
    {
      zaeo.unlock();
      throw paramObject;
    }
  }
  
  public final void removeAccount(int paramInt, boolean paramBoolean)
  {
    if ((paramInt == 1) && (!paramBoolean) && (!zagu))
    {
      zagu = true;
      if ((zagy == null) && (!ClientLibraryUtils.isPackageSide())) {
        zagy = zacd.register(mContext.getApplicationContext(), new zabc(this));
      }
      zagx.sendMessageDelayed(zagx.obtainMessage(1), zagv);
      zagx.sendMessageDelayed(zagx.obtainMessage(2), zagw);
    }
    zahf.zabx();
    zags.onUnintentionalDisconnection(paramInt);
    zags.disableCallbacks();
    if (paramInt == 2) {
      zaau();
    }
  }
  
  public final void removeAccount(Bundle paramBundle)
  {
    while (!zafc.isEmpty()) {
      execute((BaseImplementation.ApiMethodImpl)zafc.remove());
    }
    zags.onConnectionSuccess(paramBundle);
  }
  
  public final void removeAccount(ConnectionResult paramConnectionResult)
  {
    if (!zacd.isPlayServicesPossiblyUpdating(mContext, paramConnectionResult.getErrorCode())) {
      zaaw();
    }
    if (!zagu)
    {
      zags.onConnectionFailure(paramConnectionResult);
      zags.disableCallbacks();
    }
  }
  
  public final void removeAccount(zacm paramZacm)
  {
    zaeo.lock();
    try
    {
      Set localSet = zahe;
      if (localSet == null)
      {
        Log.wtf("GoogleApiClientImpl", "Attempted to remove pending transform when no transforms are registered.", new Exception());
      }
      else
      {
        boolean bool = zahe.remove(paramZacm);
        if (!bool)
        {
          Log.wtf("GoogleApiClientImpl", "Failed to remove pending transform - this may lead to memory leaks!", new Exception());
        }
        else
        {
          bool = zaax();
          if (!bool) {
            zagt.removeAccount();
          }
        }
      }
      zaeo.unlock();
      return;
    }
    catch (Throwable paramZacm)
    {
      zaeo.unlock();
      throw paramZacm;
    }
  }
  
  public final void stopAutoManage(FragmentActivity paramFragmentActivity)
  {
    paramFragmentActivity = new LifecycleActivity(paramFragmentActivity);
    if (zacb >= 0)
    {
      Elements.select(paramFragmentActivity).remove(zacb);
      return;
    }
    throw new IllegalStateException("Called stopAutoManage but automatic lifecycle management is not enabled.");
  }
  
  public final void unregisterConnectionCallbacks(GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    zags.unregisterConnectionCallbacks(paramConnectionCallbacks);
  }
  
  public final void unregisterConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    zags.unregisterConnectionFailedListener(paramOnConnectionFailedListener);
  }
  
  final boolean zaaw()
  {
    if (!zagu) {
      return false;
    }
    zagu = false;
    zagx.removeMessages(2);
    zagx.removeMessages(1);
    if (zagy != null)
    {
      zagy.unregister();
      zagy = null;
    }
    return true;
  }
  
  final boolean zaax()
  {
    zaeo.lock();
    try
    {
      Set localSet = zahe;
      if (localSet == null)
      {
        zaeo.unlock();
        return false;
      }
      boolean bool = zahe.isEmpty();
      zaeo.unlock();
      return bool ^ true;
    }
    catch (Throwable localThrowable)
    {
      zaeo.unlock();
      throw localThrowable;
    }
  }
  
  final String zaay()
  {
    StringWriter localStringWriter = new StringWriter();
    dump("", null, new PrintWriter(localStringWriter), null);
    return localStringWriter.toString();
  }
}
