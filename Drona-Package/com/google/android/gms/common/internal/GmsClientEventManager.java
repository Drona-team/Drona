package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.internal.base.zap;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public final class GmsClientEventManager
  implements Handler.Callback
{
  private final Handler mHandler;
  private final Object mLock = new Object();
  private final GmsClientEventState zaol;
  private final ArrayList<com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks> zaom = new ArrayList();
  @VisibleForTesting
  private final ArrayList<com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks> zaon = new ArrayList();
  private final ArrayList<com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener> zaoo = new ArrayList();
  private volatile boolean zaop = false;
  private final AtomicInteger zaoq = new AtomicInteger(0);
  private boolean zaor = false;
  
  public GmsClientEventManager(Looper paramLooper, GmsClientEventState paramGmsClientEventState)
  {
    zaol = paramGmsClientEventState;
    mHandler = ((Handler)new zap(paramLooper, this));
  }
  
  public final boolean areCallbacksEnabled()
  {
    return zaop;
  }
  
  public final void disableCallbacks()
  {
    zaop = false;
    zaoq.incrementAndGet();
  }
  
  public final void enableCallbacks()
  {
    zaop = true;
  }
  
  public final boolean handleMessage(Message paramMessage)
  {
    if (what == 1)
    {
      com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks localConnectionCallbacks = (com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks)obj;
      paramMessage = mLock;
      try
      {
        if ((zaop) && (zaol.isConnected()) && (zaom.contains(localConnectionCallbacks))) {
          localConnectionCallbacks.onConnected(zaol.getConnectionHint());
        }
        return true;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    int i = what;
    paramMessage = new StringBuilder(45);
    paramMessage.append("Don't know how to handle message: ");
    paramMessage.append(i);
    Log.wtf("GmsClientEvents", paramMessage.toString(), new Exception());
    return false;
  }
  
  public final boolean isConnectionCallbacksRegistered(com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    Preconditions.checkNotNull(paramConnectionCallbacks);
    Object localObject = mLock;
    try
    {
      boolean bool = zaom.contains(paramConnectionCallbacks);
      return bool;
    }
    catch (Throwable paramConnectionCallbacks)
    {
      throw paramConnectionCallbacks;
    }
  }
  
  public final boolean isConnectionFailedListenerRegistered(com.google.android.gms.common.package_6.GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    Preconditions.checkNotNull(paramOnConnectionFailedListener);
    Object localObject = mLock;
    try
    {
      boolean bool = zaoo.contains(paramOnConnectionFailedListener);
      return bool;
    }
    catch (Throwable paramOnConnectionFailedListener)
    {
      throw paramOnConnectionFailedListener;
    }
  }
  
  public final void onConnectionFailure(ConnectionResult paramConnectionResult)
  {
    Preconditions.checkHandlerThread(mHandler, "onConnectionFailure must only be called on the Handler thread");
    mHandler.removeMessages(1);
    Object localObject1 = mLock;
    try
    {
      ArrayList localArrayList = new ArrayList(zaoo);
      int k = zaoq.get();
      localArrayList = (ArrayList)localArrayList;
      int m = localArrayList.size();
      int i = 0;
      while (i < m)
      {
        Object localObject2 = localArrayList.get(i);
        int j = i + 1;
        localObject2 = (com.google.android.gms.common.package_6.GoogleApiClient.OnConnectionFailedListener)localObject2;
        if ((zaop) && (zaoq.get() == k))
        {
          i = j;
          if (zaoo.contains(localObject2))
          {
            ((com.google.android.gms.common.package_6.GoogleApiClient.OnConnectionFailedListener)localObject2).onConnectionFailed(paramConnectionResult);
            i = j;
          }
        }
        else
        {
          return;
        }
      }
      return;
    }
    catch (Throwable paramConnectionResult)
    {
      throw paramConnectionResult;
    }
  }
  
  protected final void onConnectionSuccess()
  {
    Object localObject = mLock;
    try
    {
      onConnectionSuccess(zaol.getConnectionHint());
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final void onConnectionSuccess(Bundle paramBundle)
  {
    Preconditions.checkHandlerThread(mHandler, "onConnectionSuccess must only be called on the Handler thread");
    Object localObject1 = mLock;
    for (;;)
    {
      try
      {
        boolean bool2 = zaor;
        bool1 = true;
        Preconditions.checkState(bool2 ^ true);
        mHandler.removeMessages(1);
        zaor = true;
        if (zaon.size() == 0)
        {
          Preconditions.checkState(bool1);
          ArrayList localArrayList = new ArrayList(zaom);
          int k = zaoq.get();
          localArrayList = (ArrayList)localArrayList;
          int m = localArrayList.size();
          int i = 0;
          if (i < m)
          {
            Object localObject2 = localArrayList.get(i);
            int j = i + 1;
            localObject2 = (com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks)localObject2;
            if ((zaop) && (zaol.isConnected()) && (zaoq.get() == k))
            {
              i = j;
              if (zaon.contains(localObject2)) {
                continue;
              }
              ((com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks)localObject2).onConnected(paramBundle);
              i = j;
              continue;
            }
          }
          zaon.clear();
          zaor = false;
          return;
        }
      }
      catch (Throwable paramBundle)
      {
        throw paramBundle;
      }
      boolean bool1 = false;
    }
  }
  
  public final void onUnintentionalDisconnection(int paramInt)
  {
    Preconditions.checkHandlerThread(mHandler, "onUnintentionalDisconnection must only be called on the Handler thread");
    mHandler.removeMessages(1);
    Object localObject1 = mLock;
    try
    {
      zaor = true;
      ArrayList localArrayList = new ArrayList(zaom);
      int k = zaoq.get();
      localArrayList = (ArrayList)localArrayList;
      int m = localArrayList.size();
      int i = 0;
      while (i < m)
      {
        Object localObject2 = localArrayList.get(i);
        int j = i + 1;
        localObject2 = (com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks)localObject2;
        if ((!zaop) || (zaoq.get() != k)) {
          break;
        }
        i = j;
        if (zaom.contains(localObject2))
        {
          ((com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks)localObject2).onConnectionSuspended(paramInt);
          i = j;
        }
      }
      zaon.clear();
      zaor = false;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final void registerConnectionCallbacks(com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    Preconditions.checkNotNull(paramConnectionCallbacks);
    Object localObject = mLock;
    try
    {
      if (zaom.contains(paramConnectionCallbacks))
      {
        String str = String.valueOf(paramConnectionCallbacks);
        StringBuilder localStringBuilder = new StringBuilder(String.valueOf(str).length() + 62);
        localStringBuilder.append("registerConnectionCallbacks(): listener ");
        localStringBuilder.append(str);
        localStringBuilder.append(" is already registered");
        Log.w("GmsClientEvents", localStringBuilder.toString());
      }
      else
      {
        zaom.add(paramConnectionCallbacks);
      }
      if (zaol.isConnected())
      {
        mHandler.sendMessage(mHandler.obtainMessage(1, paramConnectionCallbacks));
        return;
      }
    }
    catch (Throwable paramConnectionCallbacks)
    {
      throw paramConnectionCallbacks;
    }
  }
  
  public final void registerConnectionFailedListener(com.google.android.gms.common.package_6.GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    Preconditions.checkNotNull(paramOnConnectionFailedListener);
    Object localObject = mLock;
    try
    {
      if (zaoo.contains(paramOnConnectionFailedListener))
      {
        paramOnConnectionFailedListener = String.valueOf(paramOnConnectionFailedListener);
        StringBuilder localStringBuilder = new StringBuilder(String.valueOf(paramOnConnectionFailedListener).length() + 67);
        localStringBuilder.append("registerConnectionFailedListener(): listener ");
        localStringBuilder.append(paramOnConnectionFailedListener);
        localStringBuilder.append(" is already registered");
        Log.w("GmsClientEvents", localStringBuilder.toString());
      }
      else
      {
        zaoo.add(paramOnConnectionFailedListener);
      }
      return;
    }
    catch (Throwable paramOnConnectionFailedListener)
    {
      throw paramOnConnectionFailedListener;
    }
  }
  
  public final void unregisterConnectionCallbacks(com.google.android.gms.common.package_6.GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    Preconditions.checkNotNull(paramConnectionCallbacks);
    Object localObject = mLock;
    try
    {
      if (!zaom.remove(paramConnectionCallbacks))
      {
        paramConnectionCallbacks = String.valueOf(paramConnectionCallbacks);
        StringBuilder localStringBuilder = new StringBuilder(String.valueOf(paramConnectionCallbacks).length() + 52);
        localStringBuilder.append("unregisterConnectionCallbacks(): listener ");
        localStringBuilder.append(paramConnectionCallbacks);
        localStringBuilder.append(" not found");
        Log.w("GmsClientEvents", localStringBuilder.toString());
      }
      else if (zaor)
      {
        zaon.add(paramConnectionCallbacks);
      }
      return;
    }
    catch (Throwable paramConnectionCallbacks)
    {
      throw paramConnectionCallbacks;
    }
  }
  
  public final void unregisterConnectionFailedListener(com.google.android.gms.common.package_6.GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    Preconditions.checkNotNull(paramOnConnectionFailedListener);
    Object localObject = mLock;
    try
    {
      if (!zaoo.remove(paramOnConnectionFailedListener))
      {
        paramOnConnectionFailedListener = String.valueOf(paramOnConnectionFailedListener);
        StringBuilder localStringBuilder = new StringBuilder(String.valueOf(paramOnConnectionFailedListener).length() + 57);
        localStringBuilder.append("unregisterConnectionFailedListener(): listener ");
        localStringBuilder.append(paramOnConnectionFailedListener);
        localStringBuilder.append(" not found");
        Log.w("GmsClientEvents", localStringBuilder.toString());
      }
      return;
    }
    catch (Throwable paramOnConnectionFailedListener)
    {
      throw paramOnConnectionFailedListener;
    }
  }
  
  @VisibleForTesting
  public static abstract interface GmsClientEventState
  {
    public abstract Bundle getConnectionHint();
    
    public abstract boolean isConnected();
  }
}
