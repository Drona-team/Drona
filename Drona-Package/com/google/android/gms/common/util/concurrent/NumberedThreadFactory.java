package com.google.android.gms.common.util.concurrent;

import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.Preconditions;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@KeepForSdk
public class NumberedThreadFactory
  implements ThreadFactory
{
  private final int priority;
  private final ThreadFactory zzhr = Executors.defaultThreadFactory();
  private final String zzhs;
  private final AtomicInteger zzht = new AtomicInteger();
  
  public NumberedThreadFactory(String paramString)
  {
    this(paramString, 0);
  }
  
  private NumberedThreadFactory(String paramString, int paramInt)
  {
    zzhs = ((String)Preconditions.checkNotNull(paramString, "Name must not be null"));
    priority = 0;
  }
  
  public Thread newThread(Runnable paramRunnable)
  {
    paramRunnable = zzhr.newThread(new EngineRunnable(paramRunnable, 0));
    String str = zzhs;
    int i = zzht.getAndIncrement();
    StringBuilder localStringBuilder = new StringBuilder(String.valueOf(str).length() + 13);
    localStringBuilder.append(str);
    localStringBuilder.append("[");
    localStringBuilder.append(i);
    localStringBuilder.append("]");
    paramRunnable.setName(localStringBuilder.toString());
    return paramRunnable;
  }
}
