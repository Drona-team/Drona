package com.facebook.react.bridge;

public class JSIModuleHolder
{
  private JSIModule mModule;
  private final JSIModuleSpec mSpec;
  
  public JSIModuleHolder(JSIModuleSpec paramJSIModuleSpec)
  {
    mSpec = paramJSIModuleSpec;
  }
  
  public JSIModule getJSIModule()
  {
    if (mModule == null) {
      try
      {
        if (mModule != null)
        {
          JSIModule localJSIModule = mModule;
          return localJSIModule;
        }
        mModule = mSpec.getJSIModuleProvider().isFeatureEnabled();
        mModule.initialize();
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    return mModule;
  }
  
  public void notifyJSInstanceDestroy()
  {
    if (mModule != null) {
      mModule.onCatalystInstanceDestroy();
    }
  }
}
