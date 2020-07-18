package com.google.android.exoplayer2.util;

import android.os.Handler.Callback;
import android.os.Looper;

public abstract interface Clock
{
  public static final Clock DEFAULT = new SystemClock();
  
  public abstract HandlerWrapper createHandler(Looper paramLooper, Handler.Callback paramCallback);
  
  public abstract long elapsedRealtime();
  
  public abstract void sleep(long paramLong);
  
  public abstract long uptimeMillis();
}
