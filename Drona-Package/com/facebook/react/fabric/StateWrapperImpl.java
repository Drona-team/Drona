package com.facebook.react.fabric;

import android.annotation.SuppressLint;
import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.react.bridge.NativeMap;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.StateWrapper;
import com.facebook.upgrade.HybridData;

@SuppressLint({"MissingNativeLoadLibrary"})
public class StateWrapperImpl
  implements StateWrapper
{
  @DoNotStrip
  private final HybridData mHybridData = initHybrid();
  
  static {}
  
  private StateWrapperImpl() {}
  
  private static native HybridData initHybrid();
  
  public native ReadableNativeMap getState();
  
  public void updateState(WritableMap paramWritableMap)
  {
    updateStateImpl((NativeMap)paramWritableMap);
  }
  
  public native void updateStateImpl(NativeMap paramNativeMap);
}
