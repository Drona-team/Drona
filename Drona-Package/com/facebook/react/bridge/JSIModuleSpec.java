package com.facebook.react.bridge;

public abstract interface JSIModuleSpec<T extends JSIModule>
{
  public abstract JSIModuleProvider getJSIModuleProvider();
  
  public abstract JSIModuleType getJSIModuleType();
}
