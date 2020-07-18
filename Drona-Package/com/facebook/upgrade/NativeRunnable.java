package com.facebook.upgrade;

import com.facebook.proguard.annotations.DoNotStrip;

@DoNotStrip
public class NativeRunnable
  implements Runnable
{
  @DoNotStrip
  private final HybridData mHybridData;
  
  private NativeRunnable(HybridData paramHybridData)
  {
    mHybridData = paramHybridData;
  }
  
  public native void run();
}
