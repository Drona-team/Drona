package com.facebook.react.fabric;

import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.upgrade.HybridData;

@DoNotStrip
public class ComponentFactoryDelegate
{
  @DoNotStrip
  private final HybridData mHybridData = initHybrid();
  
  static {}
  
  public ComponentFactoryDelegate() {}
  
  private static native HybridData initHybrid();
}