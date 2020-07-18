package com.google.android.exoplayer2.pc;

public abstract interface TimeBar
{
  public abstract void addListener(OnScrubListener paramOnScrubListener);
  
  public abstract void removeListener(OnScrubListener paramOnScrubListener);
  
  public abstract void setAdGroupTimesMs(long[] paramArrayOfLong, boolean[] paramArrayOfBoolean, int paramInt);
  
  public abstract void setBufferedPosition(long paramLong);
  
  public abstract void setDuration(long paramLong);
  
  public abstract void setEnabled(boolean paramBoolean);
  
  public abstract void setKeyCountIncrement(int paramInt);
  
  public abstract void setKeyTimeIncrement(long paramLong);
  
  public abstract void setPosition(long paramLong);
  
  public abstract interface OnScrubListener
  {
    public abstract void onScrubMove(TimeBar paramTimeBar, long paramLong);
    
    public abstract void onScrubStart(TimeBar paramTimeBar, long paramLong);
    
    public abstract void onScrubStop(TimeBar paramTimeBar, long paramLong, boolean paramBoolean);
  }
}
