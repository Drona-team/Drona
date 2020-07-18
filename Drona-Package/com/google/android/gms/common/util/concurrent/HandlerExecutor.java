package com.google.android.gms.common.util.concurrent;

import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.internal.common.zze;
import java.util.concurrent.Executor;

@KeepForSdk
public class HandlerExecutor
  implements Executor
{
  private final Handler handler;
  
  public HandlerExecutor(Looper paramLooper)
  {
    handler = ((Handler)new zze(paramLooper));
  }
  
  public void execute(Runnable paramRunnable)
  {
    handler.post(paramRunnable);
  }
}
