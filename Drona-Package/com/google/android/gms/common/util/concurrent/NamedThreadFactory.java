package com.google.android.gms.common.util.concurrent;

import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.Preconditions;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@KeepForSdk
public class NamedThreadFactory
  implements ThreadFactory
{
  private final String name;
  private final int priority;
  private final ThreadFactory zzhr = Executors.defaultThreadFactory();
  
  public NamedThreadFactory(String paramString)
  {
    this(paramString, 0);
  }
  
  private NamedThreadFactory(String paramString, int paramInt)
  {
    name = ((String)Preconditions.checkNotNull(paramString, "Name must not be null"));
    priority = 0;
  }
  
  public Thread newThread(Runnable paramRunnable)
  {
    paramRunnable = zzhr.newThread(new EngineRunnable(paramRunnable, 0));
    paramRunnable.setName(name);
    return paramRunnable;
  }
}
