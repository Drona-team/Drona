package com.google.android.gms.package_7;

import android.os.Binder;
import android.os.Process;
import android.util.Log;
import java.util.concurrent.ExecutorService;

public final class SocketIOClient
  extends Binder
{
  private final IRCService zzbm;
  
  SocketIOClient(IRCService paramIRCService)
  {
    zzbm = paramIRCService;
  }
  
  public final void connect(Request paramRequest)
  {
    if (Binder.getCallingUid() == Process.myUid())
    {
      if (Log.isLoggable("EnhancedIntentService", 3)) {
        Log.d("EnhancedIntentService", "service received new intent via bind strategy");
      }
      if (Log.isLoggable("EnhancedIntentService", 3)) {
        Log.d("EnhancedIntentService", "intent being queued for bg execution");
      }
      zzbm.zzbb.execute(new Local.1(this, paramRequest));
      return;
    }
    throw new SecurityException("Binding only allowed within app");
  }
}
