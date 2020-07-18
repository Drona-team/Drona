package com.google.android.gms.package_7;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.VisibleForTesting;
import com.google.android.gms.common.util.concurrent.NamedThreadFactory;
import com.google.android.gms.internal.gcm.zzf;
import com.google.android.gms.internal.gcm.zzg;
import com.google.android.gms.stats.GCoreWakefulBroadcastReceiver;
import java.util.concurrent.ExecutorService;

public abstract class IRCService
  extends Service
{
  private final Object lock = new Object();
  @VisibleForTesting
  final ExecutorService zzbb = zzg.zzaa().zzd(new NamedThreadFactory("EnhancedIntentService"), 9);
  private Binder zzbc;
  private int zzbd;
  private int zzbe = 0;
  
  public IRCService() {}
  
  private final void showNotification(Intent paramIntent)
  {
    if (paramIntent != null) {
      GCoreWakefulBroadcastReceiver.completeWakefulIntent(paramIntent);
    }
    paramIntent = lock;
    try
    {
      zzbe -= 1;
      if (zzbe == 0) {
        stopSelfResult(zzbd);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public abstract void handleIntent(Intent paramIntent);
  
  public final IBinder onBind(Intent paramIntent)
  {
    try
    {
      if (Log.isLoggable("EnhancedIntentService", 3)) {
        Log.d("EnhancedIntentService", "Service received bind request");
      }
      if (zzbc == null) {
        zzbc = new SocketIOClient(this);
      }
      paramIntent = zzbc;
      return paramIntent;
    }
    catch (Throwable paramIntent)
    {
      throw paramIntent;
    }
  }
  
  public final int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    Object localObject = lock;
    try
    {
      zzbd = paramInt2;
      zzbe += 1;
      if (paramIntent == null)
      {
        showNotification(paramIntent);
        return 2;
      }
      zzbb.execute(new Marker.1(this, paramIntent, paramIntent));
      return 3;
    }
    catch (Throwable paramIntent)
    {
      throw paramIntent;
    }
  }
}
