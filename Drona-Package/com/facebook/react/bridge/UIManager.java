package com.facebook.react.bridge;

import android.view.View;

public abstract interface UIManager
  extends JSIModule, PerformanceCounter
{
  public abstract int addRootView(View paramView, WritableMap paramWritableMap, String paramString);
  
  public abstract void dispatchCommand(int paramInt1, int paramInt2, ReadableArray paramReadableArray);
  
  public abstract void dispatchCommand(int paramInt, String paramString, ReadableArray paramReadableArray);
  
  public abstract void synchronouslyUpdateViewOnUIThread(int paramInt, ReadableMap paramReadableMap);
  
  public abstract void updateRootLayoutSpecs(int paramInt1, int paramInt2, int paramInt3);
}
