package com.google.android.exoplayer2.upstream;

import java.io.IOException;

public abstract interface LoadErrorHandlingPolicy
{
  public abstract long getBlacklistDurationMsFor(int paramInt1, long paramLong, IOException paramIOException, int paramInt2);
  
  public abstract int getMinimumLoadableRetryCount(int paramInt);
  
  public abstract long getRetryDelayMsFor(int paramInt1, long paramLong, IOException paramIOException, int paramInt2);
}
