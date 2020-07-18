package com.facebook.react.modules.debug.interfaces;

public abstract interface DeveloperSettings
{
  public abstract boolean isAnimationFpsDebugEnabled();
  
  public abstract boolean isElementInspectorEnabled();
  
  public abstract boolean isFpsDebugEnabled();
  
  public abstract boolean isJSDevModeEnabled();
  
  public abstract boolean isJSMinifyEnabled();
  
  public abstract boolean isNuclideJSDebugEnabled();
  
  public abstract boolean isRemoteJSDebugEnabled();
  
  public abstract boolean isStartSamplingProfilerOnInit();
  
  public abstract void setRemoteJSDebugEnabled(boolean paramBoolean);
}
