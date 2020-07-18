package com.google.android.exoplayer2.scheduler;

public abstract interface Scheduler
{
  public static final boolean DEBUG = false;
  
  public abstract boolean cancel();
  
  public abstract boolean schedule(Requirements paramRequirements, String paramString1, String paramString2);
}
