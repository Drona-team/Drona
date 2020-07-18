package com.facebook.react.turbomodule.core;

import com.facebook.react.bridge.CxxModuleWrapper;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
import com.facebook.soloader.SoLoader;
import com.facebook.upgrade.HybridData;

public abstract class TurboModuleManagerDelegate
{
  private final HybridData mHybridData = initHybrid();
  
  static
  {
    SoLoader.loadLibrary("turbomodulejsijni");
  }
  
  protected TurboModuleManagerDelegate() {}
  
  public abstract CxxModuleWrapper getLegacyCxxModule(String paramString);
  
  public abstract TurboModule getModule(String paramString);
  
  protected abstract HybridData initHybrid();
}
