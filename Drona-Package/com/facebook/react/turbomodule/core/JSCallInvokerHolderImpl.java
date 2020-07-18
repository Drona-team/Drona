package com.facebook.react.turbomodule.core;

import com.facebook.react.turbomodule.core.interfaces.JSCallInvokerHolder;
import com.facebook.soloader.SoLoader;
import com.facebook.upgrade.HybridData;

public class JSCallInvokerHolderImpl
  implements JSCallInvokerHolder
{
  private final HybridData mHybridData;
  
  static
  {
    SoLoader.loadLibrary("turbomodulejsijni");
  }
  
  private JSCallInvokerHolderImpl(HybridData paramHybridData)
  {
    mHybridData = paramHybridData;
  }
}
