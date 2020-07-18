package com.facebook.react.uimanager;

import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.WritableMap;

public abstract interface StateWrapper
{
  public abstract ReadableNativeMap getState();
  
  public abstract void updateState(WritableMap paramWritableMap);
}
