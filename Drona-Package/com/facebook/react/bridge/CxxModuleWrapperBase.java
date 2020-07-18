package com.facebook.react.bridge;

import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.upgrade.HybridData;

@DoNotStrip
public class CxxModuleWrapperBase
  implements NativeModule
{
  @DoNotStrip
  private HybridData mHybridData;
  
  static {}
  
  protected CxxModuleWrapperBase(HybridData paramHybridData)
  {
    mHybridData = paramHybridData;
  }
  
  public boolean canOverrideExistingModule()
  {
    return false;
  }
  
  public native String getName();
  
  public void initialize() {}
  
  public void onCatalystInstanceDestroy()
  {
    mHybridData.resetNative();
  }
  
  protected void resetModule(HybridData paramHybridData)
  {
    if (paramHybridData != mHybridData)
    {
      mHybridData.resetNative();
      mHybridData = paramHybridData;
    }
  }
}
