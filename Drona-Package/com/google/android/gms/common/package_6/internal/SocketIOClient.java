package com.google.android.gms.common.package_6.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.collection.ArrayMap;
import androidx.collection.SimpleArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.package_6.Api.AbstractClientBuilder;
import com.google.android.gms.common.package_6.Api.Client;
import com.google.android.gms.common.package_6.Sample;
import com.google.android.gms.common.package_6.Status;
import com.google.android.gms.internal.base.zap;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import javax.annotation.concurrent.GuardedBy;

final class SocketIOClient
  implements zabs
{
  private final Context mContext;
  private final Looper zabj;
  private final zaaw zaee;
  private final zabe zaef;
  private final zabe zaeg;
  private final Map<com.google.android.gms.common.api.Api.AnyClientKey<?>, com.google.android.gms.common.api.internal.zabe> zaeh;
  private final Set<com.google.android.gms.common.api.internal.SignInConnectionListener> zaei = Collections.newSetFromMap(new WeakHashMap());
  private final Api.Client zaej;
  private Bundle zaek;
  private ConnectionResult zael = null;
  private ConnectionResult zaem = null;
  private boolean zaen = false;
  private final Lock zaeo;
  @GuardedBy("mLock")
  private int zaep = 0;
  
  private SocketIOClient(Context paramContext, zaaw paramZaaw, Lock paramLock, Looper paramLooper, GoogleApiAvailabilityLight paramGoogleApiAvailabilityLight, Map paramMap1, Map paramMap2, ClientSettings paramClientSettings, Api.AbstractClientBuilder paramAbstractClientBuilder, Api.Client paramClient, ArrayList paramArrayList1, ArrayList paramArrayList2, Map paramMap3, Map paramMap4)
  {
    mContext = paramContext;
    zaee = paramZaaw;
    zaeo = paramLock;
    zabj = paramLooper;
    zaej = paramClient;
    zaef = new zabe(paramContext, zaee, paramLock, paramLooper, paramGoogleApiAvailabilityLight, paramMap2, null, paramMap4, null, paramArrayList2, new AppUtils(this, null));
    zaeg = new zabe(paramContext, zaee, paramLock, paramLooper, paramGoogleApiAvailabilityLight, paramMap1, paramClientSettings, paramMap3, paramAbstractClientBuilder, paramArrayList1, new DatabaseManager(this, null));
    paramContext = new ArrayMap();
    paramZaaw = paramMap2.keySet().iterator();
    while (paramZaaw.hasNext()) {
      paramContext.put((com.google.android.gms.common.package_6.Api.AnyClientKey)paramZaaw.next(), zaef);
    }
    paramZaaw = paramMap1.keySet().iterator();
    while (paramZaaw.hasNext()) {
      paramContext.put((com.google.android.gms.common.package_6.Api.AnyClientKey)paramZaaw.next(), zaeg);
    }
    zaeh = Collections.unmodifiableMap(paramContext);
  }
  
  private final void cleanup()
  {
    Iterator localIterator = zaei.iterator();
    while (localIterator.hasNext()) {
      ((SignInConnectionListener)localIterator.next()).onComplete();
    }
    zaei.clear();
  }
  
  private final void cleanup(int paramInt, boolean paramBoolean)
  {
    zaee.removeAccount(paramInt, paramBoolean);
    zaem = null;
    zael = null;
  }
  
  private final void connect(Bundle paramBundle)
  {
    if (zaek == null)
    {
      zaek = paramBundle;
      return;
    }
    if (paramBundle != null) {
      zaek.putAll(paramBundle);
    }
  }
  
  private final void disconnect(ConnectionResult paramConnectionResult)
  {
    switch (zaep)
    {
    default: 
      Log.wtf("CompositeGAC", "Attempted to call failure callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new Exception());
      break;
    case 2: 
      zaee.removeAccount(paramConnectionResult);
    case 1: 
      cleanup();
    }
    zaep = 0;
  }
  
  private final boolean encode(BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    paramApiMethodImpl = paramApiMethodImpl.getClientKey();
    Preconditions.checkArgument(zaeh.containsKey(paramApiMethodImpl), "GoogleApiClient is not configured to use the API required for this call.");
    return ((zabe)zaeh.get(paramApiMethodImpl)).equals(zaeg);
  }
  
  private static boolean isConnected(ConnectionResult paramConnectionResult)
  {
    return (paramConnectionResult != null) && (paramConnectionResult.isSuccess());
  }
  
  private final boolean onError()
  {
    return (zaem != null) && (zaem.getErrorCode() == 4);
  }
  
  private final void run()
  {
    if (isConnected(zael))
    {
      if ((!isConnected(zaem)) && (!onError()))
      {
        if (zaem != null)
        {
          if (zaep == 1)
          {
            cleanup();
            return;
          }
          disconnect(zaem);
          zaef.disconnect();
        }
      }
      else
      {
        switch (zaep)
        {
        default: 
          Log.wtf("CompositeGAC", "Attempted to call success callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new AssertionError());
          break;
        case 2: 
          zaee.removeAccount(zaek);
        case 1: 
          cleanup();
        }
        zaep = 0;
      }
    }
    else
    {
      if ((zael != null) && (isConnected(zaem)))
      {
        zaeg.disconnect();
        disconnect(zael);
        return;
      }
      if ((zael != null) && (zaem != null))
      {
        ConnectionResult localConnectionResult = zael;
        if (zaeg.zahs < zaef.zahs) {
          localConnectionResult = zaem;
        }
        disconnect(localConnectionResult);
      }
    }
  }
  
  public static SocketIOClient validate(Context paramContext, zaaw paramZaaw, Lock paramLock, Looper paramLooper, GoogleApiAvailabilityLight paramGoogleApiAvailabilityLight, Map paramMap1, ClientSettings paramClientSettings, Map paramMap2, Api.AbstractClientBuilder paramAbstractClientBuilder, ArrayList paramArrayList)
  {
    ArrayMap localArrayMap1 = new ArrayMap();
    ArrayMap localArrayMap2 = new ArrayMap();
    Object localObject2 = paramMap1.entrySet().iterator();
    paramMap1 = null;
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (Map.Entry)((Iterator)localObject2).next();
      localObject1 = (Api.Client)((Map.Entry)localObject3).getValue();
      if (((Api.Client)localObject1).providesSignIn()) {
        paramMap1 = (Map)localObject1;
      }
      if (((Api.Client)localObject1).requiresSignIn()) {
        localArrayMap1.put((com.google.android.gms.common.package_6.Api.AnyClientKey)((Map.Entry)localObject3).getKey(), localObject1);
      } else {
        localArrayMap2.put((com.google.android.gms.common.package_6.Api.AnyClientKey)((Map.Entry)localObject3).getKey(), localObject1);
      }
    }
    Preconditions.checkState(localArrayMap1.isEmpty() ^ true, "CompositeGoogleApiClient should not be used without any APIs that require sign-in.");
    Object localObject1 = new ArrayMap();
    localObject2 = new ArrayMap();
    Object localObject3 = paramMap2.keySet().iterator();
    Object localObject4;
    while (((Iterator)localObject3).hasNext())
    {
      localObject4 = (Sample)((Iterator)localObject3).next();
      com.google.android.gms.common.package_6.Api.AnyClientKey localAnyClientKey = ((Sample)localObject4).getClientKey();
      if (localArrayMap1.containsKey(localAnyClientKey)) {
        ((Map)localObject1).put(localObject4, (Boolean)paramMap2.get(localObject4));
      } else if (localArrayMap2.containsKey(localAnyClientKey)) {
        ((Map)localObject2).put(localObject4, (Boolean)paramMap2.get(localObject4));
      } else {
        throw new IllegalStateException("Each API in the isOptionalMap must have a corresponding client in the clients map.");
      }
    }
    paramMap2 = new ArrayList();
    localObject3 = new ArrayList();
    paramArrayList = (ArrayList)paramArrayList;
    int j = paramArrayList.size();
    int i = 0;
    while (i < j)
    {
      localObject4 = paramArrayList.get(i);
      i += 1;
      localObject4 = (Logger)localObject4;
      if (((Map)localObject1).containsKey(mApi)) {
        paramMap2.add(localObject4);
      } else if (((Map)localObject2).containsKey(mApi)) {
        ((ArrayList)localObject3).add(localObject4);
      } else {
        throw new IllegalStateException("Each ClientCallbacks must have a corresponding API in the isOptionalMap");
      }
    }
    return new SocketIOClient(paramContext, paramZaaw, paramLock, paramLooper, paramGoogleApiAvailabilityLight, localArrayMap1, localArrayMap2, paramClientSettings, paramAbstractClientBuilder, paramMap1, paramMap2, (ArrayList)localObject3, (Map)localObject1, (Map)localObject2);
  }
  
  private final PendingIntent zaaa()
  {
    if (zaej == null) {
      return null;
    }
    return PendingIntent.getActivity(mContext, System.identityHashCode(zaee), zaej.getSignInIntent(), 134217728);
  }
  
  public final ConnectionResult blockingConnect()
  {
    throw new UnsupportedOperationException();
  }
  
  public final ConnectionResult blockingConnect(long paramLong, TimeUnit paramTimeUnit)
  {
    throw new UnsupportedOperationException();
  }
  
  public final void connect()
  {
    zaep = 2;
    zaen = false;
    zaem = null;
    zael = null;
    zaef.connect();
    zaeg.connect();
  }
  
  public final void disconnect()
  {
    zaem = null;
    zael = null;
    zaep = 0;
    zaef.disconnect();
    zaeg.disconnect();
    cleanup();
  }
  
  public final void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.append(paramString).append("authClient").println(":");
    zaeg.dump(String.valueOf(paramString).concat("  "), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    paramPrintWriter.append(paramString).append("anonClient").println(":");
    zaef.dump(String.valueOf(paramString).concat("  "), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
  }
  
  public final BaseImplementation.ApiMethodImpl enqueue(BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    if (encode(paramApiMethodImpl))
    {
      if (onError())
      {
        paramApiMethodImpl.setFailedResult(new Status(4, null, zaaa()));
        return paramApiMethodImpl;
      }
      return zaeg.enqueue(paramApiMethodImpl);
    }
    return zaef.enqueue(paramApiMethodImpl);
  }
  
  public final BaseImplementation.ApiMethodImpl execute(BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    if (encode(paramApiMethodImpl))
    {
      if (onError())
      {
        paramApiMethodImpl.setFailedResult(new Status(4, null, zaaa()));
        return paramApiMethodImpl;
      }
      return zaeg.execute(paramApiMethodImpl);
    }
    return zaef.execute(paramApiMethodImpl);
  }
  
  public final ConnectionResult getConnectionResult(Sample paramSample)
  {
    if (((zabe)zaeh.get(paramSample.getClientKey())).equals(zaeg))
    {
      if (onError()) {
        return new ConnectionResult(4, zaaa());
      }
      return zaeg.getConnectionResult(paramSample);
    }
    return zaef.getConnectionResult(paramSample);
  }
  
  public final boolean isConnected()
  {
    zaeo.lock();
    try
    {
      boolean bool1 = zaef.isConnected();
      boolean bool2 = true;
      if (bool1)
      {
        boolean bool3 = zaeg.isConnected();
        bool1 = bool2;
        if (bool3) {
          break label69;
        }
        bool3 = onError();
        bool1 = bool2;
        if (bool3) {
          break label69;
        }
        int i = zaep;
        if (i == 1)
        {
          bool1 = bool2;
          break label69;
        }
      }
      bool1 = false;
      label69:
      zaeo.unlock();
      return bool1;
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
      int i = zaep;
      boolean bool;
      if (i == 2) {
        bool = true;
      } else {
        bool = false;
      }
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
      boolean bool = isConnecting();
      if (!bool)
      {
        bool = isConnected();
        if (!bool) {}
      }
      else
      {
        bool = zaeg.isConnected();
        if (!bool)
        {
          zaei.add(paramSignInConnectionListener);
          int i = zaep;
          if (i == 0) {
            zaep = 1;
          }
          zaem = null;
          zaeg.connect();
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
      boolean bool = isConnecting();
      zaeg.disconnect();
      zaem = new ConnectionResult(4);
      if (bool)
      {
        zap localZap = new zap(zabj);
        LayerView.1 local1 = new LayerView.1(this);
        ((Handler)localZap).post(local1);
      }
      else
      {
        cleanup();
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
  
  public final void removeAccount()
  {
    zaef.removeAccount();
    zaeg.removeAccount();
  }
}
