package com.google.android.gms.package_7;

import android.content.BroadcastReceiver.PendingResult;
import android.content.Intent;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

final class Request
{
  final Intent intent;
  private final BroadcastReceiver.PendingResult zzbi;
  private boolean zzbj = false;
  private final ScheduledFuture<?> zzbk;
  
  Request(Intent paramIntent, BroadcastReceiver.PendingResult paramPendingResult, ScheduledExecutorService paramScheduledExecutorService)
  {
    intent = paramIntent;
    zzbi = paramPendingResult;
    zzbk = paramScheduledExecutorService.schedule(new XMPPService.4(this, paramIntent), 9500L, TimeUnit.MILLISECONDS);
  }
  
  final void finish()
  {
    try
    {
      if (!zzbj)
      {
        zzbi.finish();
        zzbk.cancel(false);
        zzbj = true;
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}
