package com.google.android.exoplayer2.upstream;

import android.os.Handler;

public abstract interface BandwidthMeter
{
  public abstract void addEventListener(Handler paramHandler, EventListener paramEventListener);
  
  public abstract long getBitrateEstimate();
  
  public abstract TransferListener getTransferListener();
  
  public abstract void removeEventListener(EventListener paramEventListener);
  
  public static abstract interface EventListener
  {
    public abstract void onBandwidthSample(int paramInt, long paramLong1, long paramLong2);
  }
}
