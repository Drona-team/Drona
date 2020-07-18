package com.google.android.gms.common.package_6.internal;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import androidx.collection.ArrayMap;
import androidx.collection.ArraySet;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.internal.zab;
import com.google.android.gms.common.api.internal.zai;
import com.google.android.gms.common.api.internal.zak;
import com.google.android.gms.common.api.internal.zar;
import com.google.android.gms.common.internal.BaseGmsClient.ConnectionProgressReportCallbacks;
import com.google.android.gms.common.internal.GoogleApiAvailabilityCache;
import com.google.android.gms.common.internal.IAccountAccessor;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Objects.ToStringHelper;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.SimpleClientAdapter;
import com.google.android.gms.common.package_6.Api.AnyClient;
import com.google.android.gms.common.package_6.Api.Client;
import com.google.android.gms.common.package_6.GoogleApi;
import com.google.android.gms.common.package_6.Sample;
import com.google.android.gms.common.package_6.Status;
import com.google.android.gms.common.package_6.UnsupportedApiCallException;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.common.util.PlatformVersion;
import com.google.android.gms.internal.base.zap;
import com.google.android.gms.signin.zad;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.concurrent.GuardedBy;

@KeepForSdk
public class GoogleApiManager
  implements Handler.Callback
{
  private static final Object lock = new Object();
  public static final Status zahx = new Status(4, "Sign-out occurred while this API call was in progress.");
  private static final Status zahy = new Status(4, "The user must be signed in to make this API call.");
  @GuardedBy("lock")
  private static GoogleApiManager zaic;
  private final Handler handler;
  private long zahz = 5000L;
  private long zaia = 120000L;
  private long zaib = 10000L;
  private final Context zaid;
  private final GoogleApiAvailability zaie;
  private final GoogleApiAvailabilityCache zaif;
  private final AtomicInteger zaig = new AtomicInteger(1);
  private final AtomicInteger zaih = new AtomicInteger(0);
  private final Map<zai<?>, com.google.android.gms.common.api.internal.GoogleApiManager.zaa<?>> zaii = new ConcurrentHashMap(5, 0.75F, 1);
  @GuardedBy("lock")
  private zaae zaij = null;
  @GuardedBy("lock")
  private final Set<zai<?>> zaik = new ArraySet();
  private final Set<zai<?>> zail = new ArraySet();
  
  private GoogleApiManager(Context paramContext, Looper paramLooper, GoogleApiAvailability paramGoogleApiAvailability)
  {
    zaid = paramContext;
    handler = ((Handler)new zap(paramLooper, this));
    zaie = paramGoogleApiAvailability;
    zaif = new GoogleApiAvailabilityCache(paramGoogleApiAvailability);
    handler.sendMessage(handler.obtainMessage(6));
  }
  
  private final void getSources(GoogleApi paramGoogleApi)
  {
    Msg localMsg = paramGoogleApi.get();
    zaa localZaa2 = (zaa)zaii.get(localMsg);
    zaa localZaa1 = localZaa2;
    if (localZaa2 == null)
    {
      localZaa1 = new zaa(paramGoogleApi);
      zaii.put(localMsg, localZaa1);
    }
    if (localZaa1.requiresSignIn()) {
      zail.add(localMsg);
    }
    localZaa1.connect();
  }
  
  public static GoogleApiManager open(Context paramContext)
  {
    Object localObject1 = lock;
    try
    {
      if (zaic == null)
      {
        Object localObject2 = new HandlerThread("GoogleApiHandler", 9);
        ((Thread)localObject2).start();
        localObject2 = ((HandlerThread)localObject2).getLooper();
        zaic = new GoogleApiManager(paramContext.getApplicationContext(), (Looper)localObject2, GoogleApiAvailability.getInstance());
      }
      paramContext = zaic;
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  public static void reportSignOut()
  {
    Object localObject = lock;
    try
    {
      if (zaic != null)
      {
        GoogleApiManager localGoogleApiManager = zaic;
        zaih.incrementAndGet();
        handler.sendMessageAtFrontOfQueue(handler.obtainMessage(10));
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public static GoogleApiManager zabc()
  {
    Object localObject = lock;
    try
    {
      Preconditions.checkNotNull(zaic, "Must guarantee manager is non-null before using getInstance");
      GoogleApiManager localGoogleApiManager = zaic;
      return localGoogleApiManager;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final Task call(Iterable paramIterable)
  {
    paramIterable = new LogItem(paramIterable);
    handler.sendMessage(handler.obtainMessage(2, paramIterable));
    return paramIterable.getTask();
  }
  
  public final void clear(GoogleApi paramGoogleApi, int paramInt, TaskApiCall paramTaskApiCall, TaskCompletionSource paramTaskCompletionSource, StatusExceptionMapper paramStatusExceptionMapper)
  {
    paramTaskApiCall = new ByteArrayBuffer(paramInt, paramTaskApiCall, paramTaskCompletionSource, paramStatusExceptionMapper);
    handler.sendMessage(handler.obtainMessage(4, new zabv(paramTaskApiCall, zaih.get(), paramGoogleApi)));
  }
  
  public final void close()
  {
    handler.sendMessage(handler.obtainMessage(3));
  }
  
  public final void close(ConnectionResult paramConnectionResult, int paramInt)
  {
    if (!ignore(paramConnectionResult, paramInt)) {
      handler.sendMessage(handler.obtainMessage(5, paramInt, 0, paramConnectionResult));
    }
  }
  
  final PendingIntent getIntent(Msg paramMsg, int paramInt)
  {
    paramMsg = (zaa)zaii.get(paramMsg);
    if (paramMsg == null) {
      return null;
    }
    paramMsg = paramMsg.zabq();
    if (paramMsg == null) {
      return null;
    }
    return PendingIntent.getActivity(zaid, paramInt, paramMsg.getSignInIntent(), 134217728);
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    int i = what;
    long l = 300000L;
    Object localObject1;
    Object localObject2;
    label675:
    Object localObject3;
    switch (i)
    {
    default: 
      i = what;
      paramMessage = new StringBuilder(31);
      paramMessage.append("Unknown message id: ");
      paramMessage.append(i);
      Log.w("GoogleApiManager", paramMessage.toString());
      return false;
    case 16: 
      paramMessage = (zab)obj;
      if (zaii.containsKey(zab.getCacheKey(paramMessage)))
      {
        zaa.trackChanged((zaa)zaii.get(zab.getCacheKey(paramMessage)), paramMessage);
        return true;
      }
      break;
    case 15: 
      paramMessage = (zab)obj;
      if (zaii.containsKey(zab.getCacheKey(paramMessage)))
      {
        zaa.access$getShowHint((zaa)zaii.get(zab.getCacheKey(paramMessage)), paramMessage);
        return true;
      }
      break;
    case 14: 
      paramMessage = (zaaf)obj;
      localObject1 = paramMessage.getCacheKey();
      if (!zaii.containsKey(localObject1))
      {
        paramMessage.zaal().setResult(Boolean.valueOf(false));
        return true;
      }
      boolean bool = zaa.refresh((zaa)zaii.get(localObject1), false);
      paramMessage.zaal().setResult(Boolean.valueOf(bool));
      return true;
    case 12: 
      if (zaii.containsKey(obj))
      {
        ((zaa)zaii.get(obj)).zabp();
        return true;
      }
      break;
    case 11: 
      if (zaii.containsKey(obj))
      {
        ((zaa)zaii.get(obj)).zaav();
        return true;
      }
      break;
    case 10: 
      paramMessage = zail.iterator();
      while (paramMessage.hasNext())
      {
        localObject1 = (Msg)paramMessage.next();
        ((zaa)zaii.remove(localObject1)).zabj();
      }
      zail.clear();
      return true;
    case 9: 
      if (zaii.containsKey(obj))
      {
        ((zaa)zaii.get(obj)).resume();
        return true;
      }
      break;
    case 7: 
      getSources((GoogleApi)obj);
      return true;
    case 6: 
      if ((PlatformVersion.isAtLeastIceCreamSandwich()) && ((zaid.getApplicationContext() instanceof Application)))
      {
        BackgroundDetector.initialize((Application)zaid.getApplicationContext());
        BackgroundDetector.getInstance().addListener(new zabi(this));
        if (!BackgroundDetector.getInstance().readCurrentStateIfPossible(true))
        {
          zaib = 300000L;
          return true;
        }
      }
      break;
    case 5: 
      i = arg1;
      localObject1 = (ConnectionResult)obj;
      localObject2 = zaii.values().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        paramMessage = (zaa)((Iterator)localObject2).next();
        if (paramMessage.getInstanceId() == i) {
          break label675;
        }
      }
      paramMessage = null;
      if (paramMessage != null)
      {
        localObject2 = zaie.getErrorString(((ConnectionResult)localObject1).getErrorCode());
        localObject1 = ((ConnectionResult)localObject1).getErrorMessage();
        localObject3 = new StringBuilder(String.valueOf(localObject2).length() + 69 + String.valueOf(localObject1).length());
        ((StringBuilder)localObject3).append("Error resolution was canceled by the user, original error message: ");
        ((StringBuilder)localObject3).append((String)localObject2);
        ((StringBuilder)localObject3).append(": ");
        ((StringBuilder)localObject3).append((String)localObject1);
        paramMessage.execute(new Status(17, ((StringBuilder)localObject3).toString()));
        return true;
      }
      paramMessage = new StringBuilder(76);
      paramMessage.append("Could not find API instance ");
      paramMessage.append(i);
      paramMessage.append(" while trying to fail enqueued calls.");
      Log.wtf("GoogleApiManager", paramMessage.toString(), new Exception());
      return true;
    case 4: 
    case 8: 
    case 13: 
      localObject2 = (zabv)obj;
      localObject1 = (zaa)zaii.get(zajt.get());
      paramMessage = (Message)localObject1;
      if (localObject1 == null)
      {
        getSources(zajt);
        paramMessage = (zaa)zaii.get(zajt.get());
      }
      if ((paramMessage.requiresSignIn()) && (zaih.get() != zajs))
      {
        zajr.toString(zahx);
        paramMessage.zabj();
        return true;
      }
      paramMessage.ls(zajr);
      return true;
    case 3: 
      paramMessage = zaii.values().iterator();
    case 2: 
    case 1: 
      while (paramMessage.hasNext())
      {
        localObject1 = (zaa)paramMessage.next();
        ((zaa)localObject1).zabl();
        ((zaa)localObject1).connect();
        continue;
        paramMessage = (LogItem)obj;
        localObject1 = paramMessage.getTypes().iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (Msg)((Iterator)localObject1).next();
          localObject3 = (zaa)zaii.get(localObject2);
          if (localObject3 == null)
          {
            paramMessage.setTimestamp((Msg)localObject2, new ConnectionResult(13), null);
            return true;
          }
          if (((zaa)localObject3).isConnected())
          {
            paramMessage.setTimestamp((Msg)localObject2, ConnectionResult.RESULT_SUCCESS, ((zaa)localObject3).zaab().getEndpointPackageName());
          }
          else if (((zaa)localObject3).zabm() != null)
          {
            paramMessage.setTimestamp((Msg)localObject2, ((zaa)localObject3).zabm(), null);
          }
          else
          {
            ((zaa)localObject3).setTags(paramMessage);
            ((zaa)localObject3).connect();
            continue;
            if (((Boolean)obj).booleanValue()) {
              l = 10000L;
            }
            zaib = l;
            handler.removeMessages(12);
            paramMessage = zaii.keySet().iterator();
            while (paramMessage.hasNext())
            {
              localObject1 = (Msg)paramMessage.next();
              handler.sendMessageDelayed(handler.obtainMessage(12, localObject1), zaib);
            }
          }
        }
      }
    }
    return true;
  }
  
  final boolean ignore(ConnectionResult paramConnectionResult, int paramInt)
  {
    return zaie.setCurrentTheme(zaid, paramConnectionResult, paramInt);
  }
  
  final void maybeSignOut()
  {
    zaih.incrementAndGet();
    handler.sendMessage(handler.obtainMessage(10));
  }
  
  public final void read(zaae paramZaae)
  {
    Object localObject = lock;
    try
    {
      if (zaij != paramZaae)
      {
        zaij = paramZaae;
        zaik.clear();
      }
      zaik.addAll(paramZaae.zaaj());
      return;
    }
    catch (Throwable paramZaae)
    {
      throw paramZaae;
    }
  }
  
  public final void respondWith(GoogleApi paramGoogleApi)
  {
    handler.sendMessage(handler.obtainMessage(7, paramGoogleApi));
  }
  
  final void setDisplayMode(zaae paramZaae)
  {
    Object localObject = lock;
    try
    {
      if (zaij == paramZaae)
      {
        zaij = null;
        zaik.clear();
      }
      return;
    }
    catch (Throwable paramZaae)
    {
      throw paramZaae;
    }
  }
  
  public final Task then(GoogleApi paramGoogleApi)
  {
    paramGoogleApi = new zaaf(paramGoogleApi.get());
    handler.sendMessage(handler.obtainMessage(14, paramGoogleApi));
    return paramGoogleApi.zaal().getTask();
  }
  
  public final Task then(GoogleApi paramGoogleApi, ListenerHolder.ListenerKey paramListenerKey)
  {
    TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    paramListenerKey = new TCharFloatHashMap.TValueView(paramListenerKey, localTaskCompletionSource);
    handler.sendMessage(handler.obtainMessage(13, new zabv(paramListenerKey, zaih.get(), paramGoogleApi)));
    return localTaskCompletionSource.getTask();
  }
  
  public final Task then(GoogleApi paramGoogleApi, RegisterListenerMethod paramRegisterListenerMethod, UnregisterListenerMethod paramUnregisterListenerMethod)
  {
    TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    paramRegisterListenerMethod = new TSynchronizedShortCollection(new zabw(paramRegisterListenerMethod, paramUnregisterListenerMethod), localTaskCompletionSource);
    handler.sendMessage(handler.obtainMessage(8, new zabv(paramRegisterListenerMethod, zaih.get(), paramGoogleApi)));
    return localTaskCompletionSource.getTask();
  }
  
  public final void write(GoogleApi paramGoogleApi, int paramInt, BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    paramApiMethodImpl = new Frame(paramInt, paramApiMethodImpl);
    handler.sendMessage(handler.obtainMessage(4, new zabv(paramApiMethodImpl, zaih.get(), paramGoogleApi)));
  }
  
  public final int zabd()
  {
    return zaig.getAndIncrement();
  }
  
  public final class zaa<O extends Api.ApiOptions>
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, zar
  {
    private final zai<O> zafq;
    private final Queue<zab> zain = new LinkedList();
    private final Api.Client zaio;
    private final Api.AnyClient zaip;
    private final zaab zaiq;
    private final Set<zak> zair = new HashSet();
    private final Map<com.google.android.gms.common.api.internal.ListenerHolder.ListenerKey<?>, com.google.android.gms.common.api.internal.zabw> zais = new HashMap();
    private final int zait;
    private final zace zaiu;
    private boolean zaiv;
    private final List<com.google.android.gms.common.api.internal.GoogleApiManager.zab> zaiw = new ArrayList();
    private ConnectionResult zaix = null;
    
    public zaa(GoogleApi paramGoogleApi)
    {
      zaio = paramGoogleApi.showToast(GoogleApiManager.access$getHandler(GoogleApiManager.this).getLooper(), this);
      if ((zaio instanceof SimpleClientAdapter)) {
        zaip = ((SimpleClientAdapter)zaio).getClient();
      } else {
        zaip = zaio;
      }
      zafq = paramGoogleApi.get();
      zaiq = new zaab();
      zait = paramGoogleApi.getInstanceId();
      if (zaio.requiresSignIn())
      {
        zaiu = paramGoogleApi.showToast(GoogleApiManager.resolve(GoogleApiManager.this), GoogleApiManager.access$getHandler(GoogleApiManager.this));
        return;
      }
      zaiu = null;
    }
    
    private final void doTransform(ConnectionResult paramConnectionResult)
    {
      Iterator localIterator = zair.iterator();
      while (localIterator.hasNext())
      {
        LogItem localLogItem = (LogItem)localIterator.next();
        String str = null;
        if (Objects.equal(paramConnectionResult, ConnectionResult.RESULT_SUCCESS)) {
          str = zaio.getEndpointPackageName();
        }
        localLogItem.setTimestamp(zafq, paramConnectionResult, str);
      }
      zair.clear();
    }
    
    private final void execute(GoogleApiManager.zab paramZab)
    {
      if (zaiw.remove(paramZab))
      {
        GoogleApiManager.access$getHandler(GoogleApiManager.this).removeMessages(15, paramZab);
        GoogleApiManager.access$getHandler(GoogleApiManager.this).removeMessages(16, paramZab);
        paramZab = GoogleApiManager.zab.mapFileName(paramZab);
        ArrayList localArrayList = new ArrayList(zain.size());
        Object localObject = zain.iterator();
        while (((Iterator)localObject).hasNext())
        {
          Location localLocation = (Location)((Iterator)localObject).next();
          if ((localLocation instanceof Loader))
          {
            Feature[] arrayOfFeature = ((Loader)localLocation).toString(this);
            if ((arrayOfFeature != null) && (ArrayUtils.contains(arrayOfFeature, paramZab))) {
              localArrayList.add(localLocation);
            }
          }
        }
        localArrayList = (ArrayList)localArrayList;
        int j = localArrayList.size();
        int i = 0;
        while (i < j)
        {
          localObject = localArrayList.get(i);
          i += 1;
          localObject = (Location)localObject;
          zain.remove(localObject);
          ((Location)localObject).toString(new UnsupportedApiCallException(paramZab));
        }
      }
    }
    
    private final boolean execute(Location paramLocation)
    {
      if (!(paramLocation instanceof Loader))
      {
        readFrom(paramLocation);
        return true;
      }
      Loader localLoader = (Loader)paramLocation;
      Feature localFeature = parse(localLoader.toString(this));
      if (localFeature == null)
      {
        readFrom(paramLocation);
        return true;
      }
      if (localLoader.isEmpty(this))
      {
        paramLocation = new GoogleApiManager.zab(zafq, localFeature, null);
        int i = zaiw.indexOf(paramLocation);
        if (i >= 0)
        {
          paramLocation = (GoogleApiManager.zab)zaiw.get(i);
          GoogleApiManager.access$getHandler(GoogleApiManager.this).removeMessages(15, paramLocation);
          GoogleApiManager.access$getHandler(GoogleApiManager.this).sendMessageDelayed(Message.obtain(GoogleApiManager.access$getHandler(GoogleApiManager.this), 15, paramLocation), GoogleApiManager.unwrap(GoogleApiManager.this));
        }
        else
        {
          zaiw.add(paramLocation);
          GoogleApiManager.access$getHandler(GoogleApiManager.this).sendMessageDelayed(Message.obtain(GoogleApiManager.access$getHandler(GoogleApiManager.this), 15, paramLocation), GoogleApiManager.unwrap(GoogleApiManager.this));
          GoogleApiManager.access$getHandler(GoogleApiManager.this).sendMessageDelayed(Message.obtain(GoogleApiManager.access$getHandler(GoogleApiManager.this), 16, paramLocation), GoogleApiManager.getMetadataId(GoogleApiManager.this));
          paramLocation = new ConnectionResult(2, null);
          if (!putAll(paramLocation)) {
            ignore(paramLocation, zait);
          }
        }
      }
      else
      {
        localLoader.toString(new UnsupportedApiCallException(localFeature));
      }
      return false;
    }
    
    private final void getSources(GoogleApiManager.zab paramZab)
    {
      if (!zaiw.contains(paramZab)) {
        return;
      }
      if (!zaiv)
      {
        if (!zaio.isConnected())
        {
          connect();
          return;
        }
        zabi();
      }
    }
    
    private final Feature parse(Feature[] paramArrayOfFeature)
    {
      if (paramArrayOfFeature != null)
      {
        if (paramArrayOfFeature.length == 0) {
          return null;
        }
        Object localObject2 = zaio.getAvailableFeatures();
        Object localObject1 = localObject2;
        int j = 0;
        if (localObject2 == null) {
          localObject1 = new Feature[0];
        }
        localObject2 = new ArrayMap(localObject1.length);
        int k = localObject1.length;
        int i = 0;
        while (i < k)
        {
          Object localObject3 = localObject1[i];
          ((Map)localObject2).put(localObject3.getName(), Long.valueOf(localObject3.getVersion()));
          i += 1;
        }
        k = paramArrayOfFeature.length;
        i = j;
        while (i < k)
        {
          localObject1 = paramArrayOfFeature[i];
          if (((Map)localObject2).containsKey(((Feature)localObject1).getName()))
          {
            if (((Long)((Map)localObject2).get(((Feature)localObject1).getName())).longValue() < ((Feature)localObject1).getVersion()) {
              return localObject1;
            }
            i += 1;
          }
          else
          {
            return localObject1;
          }
        }
      }
      return null;
    }
    
    private final boolean putAll(ConnectionResult paramConnectionResult)
    {
      Object localObject = GoogleApiManager.zabe();
      try
      {
        if ((GoogleApiManager.cast(GoogleApiManager.this) != null) && (GoogleApiManager.access$getMProducts(GoogleApiManager.this).contains(zafq)))
        {
          GoogleApiManager.cast(GoogleApiManager.this).next(paramConnectionResult, zait);
          return true;
        }
        return false;
      }
      catch (Throwable paramConnectionResult)
      {
        throw paramConnectionResult;
      }
    }
    
    private final void readFrom(Location paramLocation)
    {
      paramLocation.readFrom(zaiq, requiresSignIn());
      try
      {
        paramLocation.readFrom(this);
        return;
      }
      catch (DeadObjectException paramLocation)
      {
        for (;;) {}
      }
      onConnectionSuspended(1);
      zaio.disconnect();
    }
    
    private final boolean updateConnection(boolean paramBoolean)
    {
      Preconditions.checkHandlerThread(GoogleApiManager.access$getHandler(GoogleApiManager.this));
      if ((zaio.isConnected()) && (zais.size() == 0)) {
        if (zaiq.zaag())
        {
          if (paramBoolean)
          {
            zabo();
            return false;
          }
        }
        else
        {
          zaio.disconnect();
          return true;
        }
      }
      return false;
    }
    
    private final void zabg()
    {
      zabl();
      doTransform(ConnectionResult.RESULT_SUCCESS);
      zabn();
      Iterator localIterator = zais.values().iterator();
      while (localIterator.hasNext())
      {
        Object localObject = (zabw)localIterator.next();
        Api.AnyClient localAnyClient;
        if (parse(zajx.getRequiredFeatures()) != null)
        {
          localIterator.remove();
        }
        else
        {
          localObject = zajx;
          localAnyClient = zaip;
        }
        try
        {
          ((RegisterListenerMethod)localObject).registerListener(localAnyClient, new TaskCompletionSource());
        }
        catch (DeadObjectException localDeadObjectException)
        {
          for (;;) {}
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
        localIterator.remove();
        continue;
        onConnectionSuspended(1);
        zaio.disconnect();
      }
      zabi();
      zabo();
    }
    
    private final void zabh()
    {
      zabl();
      zaiv = true;
      zaiq.zaai();
      GoogleApiManager.access$getHandler(GoogleApiManager.this).sendMessageDelayed(Message.obtain(GoogleApiManager.access$getHandler(GoogleApiManager.this), 9, zafq), GoogleApiManager.unwrap(GoogleApiManager.this));
      GoogleApiManager.access$getHandler(GoogleApiManager.this).sendMessageDelayed(Message.obtain(GoogleApiManager.access$getHandler(GoogleApiManager.this), 11, zafq), GoogleApiManager.getMetadataId(GoogleApiManager.this));
      GoogleApiManager.getOutput(GoogleApiManager.this).flush();
    }
    
    private final void zabi()
    {
      ArrayList localArrayList = (ArrayList)new ArrayList(zain);
      int k = localArrayList.size();
      int i = 0;
      while (i < k)
      {
        Object localObject = localArrayList.get(i);
        int j = i + 1;
        localObject = (Location)localObject;
        if (!zaio.isConnected()) {
          break;
        }
        i = j;
        if (execute((Location)localObject))
        {
          zain.remove(localObject);
          i = j;
        }
      }
    }
    
    private final void zabn()
    {
      if (zaiv)
      {
        GoogleApiManager.access$getHandler(GoogleApiManager.this).removeMessages(11, zafq);
        GoogleApiManager.access$getHandler(GoogleApiManager.this).removeMessages(9, zafq);
        zaiv = false;
      }
    }
    
    private final void zabo()
    {
      GoogleApiManager.access$getHandler(GoogleApiManager.this).removeMessages(12, zafq);
      GoogleApiManager.access$getHandler(GoogleApiManager.this).sendMessageDelayed(GoogleApiManager.access$getHandler(GoogleApiManager.this).obtainMessage(12, zafq), GoogleApiManager.createId(GoogleApiManager.this));
    }
    
    public final void connect()
    {
      Preconditions.checkHandlerThread(GoogleApiManager.access$getHandler(GoogleApiManager.this));
      if (!zaio.isConnected())
      {
        if (zaio.isConnecting()) {
          return;
        }
        int i = GoogleApiManager.getOutput(GoogleApiManager.this).getClientAvailability(GoogleApiManager.resolve(GoogleApiManager.this), zaio);
        if (i != 0)
        {
          onConnectionFailed(new ConnectionResult(i, null));
          return;
        }
        GoogleApiManager.zac localZac = new GoogleApiManager.zac(GoogleApiManager.this, zaio, zafq);
        if (zaio.requiresSignIn()) {
          zaiu.retrieveToken(localZac);
        }
        zaio.connect(localZac);
      }
    }
    
    public final void execute(Status paramStatus)
    {
      Preconditions.checkHandlerThread(GoogleApiManager.access$getHandler(GoogleApiManager.this));
      Iterator localIterator = zain.iterator();
      while (localIterator.hasNext()) {
        ((Location)localIterator.next()).toString(paramStatus);
      }
      zain.clear();
    }
    
    public final int getInstanceId()
    {
      return zait;
    }
    
    final boolean isConnected()
    {
      return zaio.isConnected();
    }
    
    public final void ls(Location paramLocation)
    {
      Preconditions.checkHandlerThread(GoogleApiManager.access$getHandler(GoogleApiManager.this));
      if (zaio.isConnected())
      {
        if (execute(paramLocation))
        {
          zabo();
          return;
        }
        zain.add(paramLocation);
        return;
      }
      zain.add(paramLocation);
      if ((zaix != null) && (zaix.hasResolution()))
      {
        onConnectionFailed(zaix);
        return;
      }
      connect();
    }
    
    public final void onConnected(Bundle paramBundle)
    {
      if (Looper.myLooper() == GoogleApiManager.access$getHandler(GoogleApiManager.this).getLooper())
      {
        zabg();
        return;
      }
      GoogleApiManager.access$getHandler(GoogleApiManager.this).post(new zabj(this));
    }
    
    public final void onConnectionFailed(ConnectionResult paramConnectionResult)
    {
      Preconditions.checkHandlerThread(GoogleApiManager.access$getHandler(GoogleApiManager.this));
      if (zaiu != null) {
        zaiu.zabs();
      }
      zabl();
      GoogleApiManager.getOutput(GoogleApiManager.this).flush();
      doTransform(paramConnectionResult);
      if (paramConnectionResult.getErrorCode() == 4)
      {
        execute(GoogleApiManager.zabf());
        return;
      }
      if (zain.isEmpty())
      {
        zaix = paramConnectionResult;
        return;
      }
      if (putAll(paramConnectionResult)) {
        return;
      }
      if (!ignore(paramConnectionResult, zait))
      {
        if (paramConnectionResult.getErrorCode() == 18) {
          zaiv = true;
        }
        if (zaiv)
        {
          GoogleApiManager.access$getHandler(GoogleApiManager.this).sendMessageDelayed(Message.obtain(GoogleApiManager.access$getHandler(GoogleApiManager.this), 9, zafq), GoogleApiManager.unwrap(GoogleApiManager.this));
          return;
        }
        paramConnectionResult = zafq.get();
        StringBuilder localStringBuilder = new StringBuilder(String.valueOf(paramConnectionResult).length() + 38);
        localStringBuilder.append("API: ");
        localStringBuilder.append(paramConnectionResult);
        localStringBuilder.append(" is not available on this device.");
        execute(new Status(17, localStringBuilder.toString()));
      }
    }
    
    public final void onConnectionSuspended(int paramInt)
    {
      if (Looper.myLooper() == GoogleApiManager.access$getHandler(GoogleApiManager.this).getLooper())
      {
        zabh();
        return;
      }
      GoogleApiManager.access$getHandler(GoogleApiManager.this).post(new zabk(this));
    }
    
    public final void putChannel(ConnectionResult paramConnectionResult)
    {
      Preconditions.checkHandlerThread(GoogleApiManager.access$getHandler(GoogleApiManager.this));
      zaio.disconnect();
      onConnectionFailed(paramConnectionResult);
    }
    
    public final boolean requiresSignIn()
    {
      return zaio.requiresSignIn();
    }
    
    public final void resume()
    {
      Preconditions.checkHandlerThread(GoogleApiManager.access$getHandler(GoogleApiManager.this));
      if (zaiv) {
        connect();
      }
    }
    
    public final void setTags(LogItem paramLogItem)
    {
      Preconditions.checkHandlerThread(GoogleApiManager.access$getHandler(GoogleApiManager.this));
      zair.add(paramLogItem);
    }
    
    public final void startLoading(ConnectionResult paramConnectionResult, Sample paramSample, boolean paramBoolean)
    {
      if (Looper.myLooper() == GoogleApiManager.access$getHandler(GoogleApiManager.this).getLooper())
      {
        onConnectionFailed(paramConnectionResult);
        return;
      }
      GoogleApiManager.access$getHandler(GoogleApiManager.this).post(new zabl(this, paramConnectionResult));
    }
    
    public final Api.Client zaab()
    {
      return zaio;
    }
    
    public final void zaav()
    {
      Preconditions.checkHandlerThread(GoogleApiManager.access$getHandler(GoogleApiManager.this));
      if (zaiv)
      {
        zabn();
        Status localStatus;
        if (GoogleApiManager.get0(GoogleApiManager.this).isGooglePlayServicesAvailable(GoogleApiManager.resolve(GoogleApiManager.this)) == 18) {
          localStatus = new Status(8, "Connection timed out while waiting for Google Play services update to complete.");
        } else {
          localStatus = new Status(8, "API failed to connect while resuming due to an unknown error.");
        }
        execute(localStatus);
        zaio.disconnect();
      }
    }
    
    public final void zabj()
    {
      Preconditions.checkHandlerThread(GoogleApiManager.access$getHandler(GoogleApiManager.this));
      execute(GoogleApiManager.zahx);
      zaiq.zaah();
      ListenerHolder.ListenerKey[] arrayOfListenerKey = (ListenerHolder.ListenerKey[])zais.keySet().toArray(new ListenerHolder.ListenerKey[zais.size()]);
      int j = arrayOfListenerKey.length;
      int i = 0;
      while (i < j)
      {
        ls(new TCharFloatHashMap.TValueView(arrayOfListenerKey[i], new TaskCompletionSource()));
        i += 1;
      }
      doTransform(new ConnectionResult(4));
      if (zaio.isConnected()) {
        zaio.onUserSignOut(new zabm(this));
      }
    }
    
    public final Map zabk()
    {
      return zais;
    }
    
    public final void zabl()
    {
      Preconditions.checkHandlerThread(GoogleApiManager.access$getHandler(GoogleApiManager.this));
      zaix = null;
    }
    
    public final ConnectionResult zabm()
    {
      Preconditions.checkHandlerThread(GoogleApiManager.access$getHandler(GoogleApiManager.this));
      return zaix;
    }
    
    public final boolean zabp()
    {
      return updateConnection(true);
    }
    
    final zad zabq()
    {
      if (zaiu == null) {
        return null;
      }
      return zaiu.zabq();
    }
  }
  
  final class zab
  {
    private final Feature zajc;
    
    private zab(Feature paramFeature)
    {
      zajc = paramFeature;
    }
    
    public final boolean equals(Object paramObject)
    {
      if ((paramObject != null) && ((paramObject instanceof zab)))
      {
        paramObject = (zab)paramObject;
        if ((Objects.equal(GoogleApiManager.this, zajb)) && (Objects.equal(zajc, zajc))) {
          return true;
        }
      }
      return false;
    }
    
    public final int hashCode()
    {
      return Objects.hashCode(new Object[] { GoogleApiManager.this, zajc });
    }
    
    public final String toString()
    {
      return Objects.toStringHelper(this).add("key", GoogleApiManager.this).add("feature", zajc).toString();
    }
  }
  
  final class zac
    implements zach, BaseGmsClient.ConnectionProgressReportCallbacks
  {
    private final zai<?> zafq;
    private final Api.Client zaio;
    private IAccountAccessor zajd = null;
    private Set<Scope> zaje = null;
    private boolean zajf = false;
    
    public zac(Api.Client paramClient, Msg paramMsg)
    {
      zaio = paramClient;
      zafq = paramMsg;
    }
    
    private final void zabr()
    {
      if ((zajf) && (zajd != null)) {
        zaio.getRemoteService(zajd, zaje);
      }
    }
    
    public final void ignore(ConnectionResult paramConnectionResult)
    {
      ((GoogleApiManager.zaa)GoogleApiManager.isIgnored(GoogleApiManager.this).get(zafq)).putChannel(paramConnectionResult);
    }
    
    public final void onReportServiceBinding(ConnectionResult paramConnectionResult)
    {
      GoogleApiManager.access$getHandler(GoogleApiManager.this).post(new zabo(this, paramConnectionResult));
    }
    
    public final void startSession(IAccountAccessor paramIAccountAccessor, Set paramSet)
    {
      if ((paramIAccountAccessor != null) && (paramSet != null))
      {
        zajd = paramIAccountAccessor;
        zaje = paramSet;
        zabr();
        return;
      }
      Log.wtf("GoogleApiManager", "Received null response from onSignInSuccess", new Exception());
      ignore(new ConnectionResult(4));
    }
  }
}
