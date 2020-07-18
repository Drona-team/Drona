package com.google.android.gms.package_7;

import android.content.BroadcastReceiver.PendingResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.GuardedBy;
import com.google.android.gms.common.stats.ConnectionTracker;
import com.google.android.gms.common.util.concurrent.NamedThreadFactory;
import com.google.android.gms.iid.zzg;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public final class ServiceHelper
  implements ServiceConnection
{
  private final Context myContext;
  private final Intent zzbp;
  private final ScheduledExecutorService zzbq;
  private final Queue<zzg> zzbr = new ArrayDeque();
  private SocketIOClient zzbs;
  @GuardedBy("this")
  private boolean zzbt = false;
  
  public ServiceHelper(Context paramContext, String paramString)
  {
    this(paramContext, paramString, new ScheduledThreadPoolExecutor(0, new NamedThreadFactory("EnhancedIntentService")));
  }
  
  private ServiceHelper(Context paramContext, String paramString, ScheduledExecutorService paramScheduledExecutorService)
  {
    myContext = paramContext.getApplicationContext();
    zzbp = new Intent(paramString).setPackage(myContext.getPackageName());
    zzbq = paramScheduledExecutorService;
  }
  
  private final void connect()
  {
    Object localObject1 = this;
    try
    {
      if (Log.isLoggable("EnhancedIntentService", 3))
      {
        localObject1 = this;
        Log.d("EnhancedIntentService", "flush queue called");
      }
      for (;;)
      {
        localObject1 = this;
        if (zzbr.isEmpty()) {
          break label299;
        }
        localObject1 = this;
        if (Log.isLoggable("EnhancedIntentService", 3))
        {
          localObject1 = this;
          Log.d("EnhancedIntentService", "found intent to be delivered");
        }
        localObject1 = this;
        if (zzbs == null) {
          break;
        }
        localObject1 = this;
        if (!zzbs.isBinderAlive()) {
          break;
        }
        localObject1 = this;
        if (Log.isLoggable("EnhancedIntentService", 3))
        {
          localObject1 = this;
          Log.d("EnhancedIntentService", "binder is alive, sending the intent.");
        }
        localObject1 = this;
        localObject2 = (Request)zzbr.poll();
        localObject1 = this;
        zzbs.connect((Request)localObject2);
      }
      Object localObject2 = this;
      localObject1 = this;
      Object localObject3;
      if (Log.isLoggable("EnhancedIntentService", 3))
      {
        localObject1 = this;
        bool = zzbt;
        localObject2 = this;
        localObject1 = localObject2;
        localObject3 = new StringBuilder(39);
        localObject1 = localObject2;
        ((StringBuilder)localObject3).append("binder is dead. start connection? ");
        localObject1 = localObject2;
        ((StringBuilder)localObject3).append(bool ^ true);
        localObject1 = localObject2;
        Log.d("EnhancedIntentService", ((StringBuilder)localObject3).toString());
      }
      localObject1 = localObject2;
      boolean bool = zzbt;
      if (!bool)
      {
        localObject1 = localObject2;
        zzbt = true;
        localObject1 = localObject2;
        try
        {
          localObject3 = ConnectionTracker.getInstance();
          Context localContext = myContext;
          Intent localIntent = zzbp;
          localObject1 = localObject2;
          bool = ((ConnectionTracker)localObject3).bindService(localContext, localIntent, (ServiceConnection)localObject2, 65);
          if (bool) {
            return;
          }
          localObject1 = localObject2;
          Log.e("EnhancedIntentService", "binding to the service failed");
        }
        catch (SecurityException localSecurityException)
        {
          localObject1 = localObject2;
          Log.e("EnhancedIntentService", "Exception while binding the service", localSecurityException);
        }
        localObject1 = localObject2;
        zzbt = false;
        localObject1 = localObject2;
        ((ServiceHelper)localObject2).execute();
      }
      return;
      label299:
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private final void execute()
  {
    while (!zzbr.isEmpty()) {
      ((Request)zzbr.poll()).finish();
    }
  }
  
  public final void connect(Intent paramIntent, BroadcastReceiver.PendingResult paramPendingResult)
  {
    try
    {
      if (Log.isLoggable("EnhancedIntentService", 3)) {
        Log.d("EnhancedIntentService", "new intent queued in the bind-strategy delivery");
      }
      zzbr.add(new Request(paramIntent, paramPendingResult, zzbq));
      connect();
      return;
    }
    catch (Throwable paramIntent)
    {
      throw paramIntent;
    }
  }
  
  public final void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
  {
    try
    {
      zzbt = false;
      zzbs = ((SocketIOClient)paramIBinder);
      if (Log.isLoggable("EnhancedIntentService", 3))
      {
        paramComponentName = String.valueOf(paramComponentName);
        StringBuilder localStringBuilder = new StringBuilder(String.valueOf(paramComponentName).length() + 20);
        localStringBuilder.append("onServiceConnected: ");
        localStringBuilder.append(paramComponentName);
        Log.d("EnhancedIntentService", localStringBuilder.toString());
      }
      if (paramIBinder == null)
      {
        Log.e("EnhancedIntentService", "Null service connection");
        execute();
      }
      else
      {
        connect();
      }
      return;
    }
    catch (Throwable paramComponentName)
    {
      throw paramComponentName;
    }
  }
  
  public final void onServiceDisconnected(ComponentName paramComponentName)
  {
    if (Log.isLoggable("EnhancedIntentService", 3))
    {
      paramComponentName = String.valueOf(paramComponentName);
      StringBuilder localStringBuilder = new StringBuilder(String.valueOf(paramComponentName).length() + 23);
      localStringBuilder.append("onServiceDisconnected: ");
      localStringBuilder.append(paramComponentName);
      Log.d("EnhancedIntentService", localStringBuilder.toString());
    }
    connect();
  }
}
