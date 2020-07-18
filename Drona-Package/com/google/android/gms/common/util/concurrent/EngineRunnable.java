package com.google.android.gms.common.util.concurrent;

import android.os.Process;

final class EngineRunnable
  implements Runnable
{
  private final int priority;
  private final Runnable zzhu;
  
  public EngineRunnable(Runnable paramRunnable, int paramInt)
  {
    zzhu = paramRunnable;
    priority = paramInt;
  }
  
  public final void run()
  {
    Process.setThreadPriority(priority);
    zzhu.run();
  }
}
