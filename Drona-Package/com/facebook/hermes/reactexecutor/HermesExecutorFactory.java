package com.facebook.hermes.reactexecutor;

import com.facebook.react.bridge.JavaScriptExecutor;
import com.facebook.react.bridge.JavaScriptExecutorFactory;

public class HermesExecutorFactory
  implements JavaScriptExecutorFactory
{
  private static final String PAGE_KEY = "Hermes";
  private final RuntimeConfig mConfig;
  
  public HermesExecutorFactory()
  {
    this(null);
  }
  
  public HermesExecutorFactory(RuntimeConfig paramRuntimeConfig)
  {
    mConfig = paramRuntimeConfig;
  }
  
  public JavaScriptExecutor create()
  {
    return new HermesExecutor(mConfig);
  }
  
  public void startSamplingProfiler() {}
  
  public void stopSamplingProfiler(String paramString) {}
  
  public String toString()
  {
    return "JSIExecutor+HermesRuntime";
  }
}
