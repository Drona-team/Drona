package com.google.android.gms.common.package_6.internal;

import com.google.android.gms.common.api.internal.zai;
import com.google.android.gms.tasks.TaskCompletionSource;

final class zaaf
{
  private final zai<?> zafq;
  private final TaskCompletionSource<Boolean> zafr = new TaskCompletionSource();
  
  public zaaf(Msg paramMsg)
  {
    zafq = paramMsg;
  }
  
  public final Msg getCacheKey()
  {
    return zafq;
  }
  
  public final TaskCompletionSource zaal()
  {
    return zafr;
  }
}
