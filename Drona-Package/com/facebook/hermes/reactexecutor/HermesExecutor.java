package com.facebook.hermes.reactexecutor;

import com.facebook.hermes.instrumentation.HermesMemoryDumper;
import com.facebook.react.bridge.JavaScriptExecutor;
import com.facebook.soloader.SoLoader;
import com.facebook.upgrade.HybridData;

public class HermesExecutor
  extends JavaScriptExecutor
{
  private static String mode_ = "Debug";
  
  static
  {
    SoLoader.loadLibrary("hermes");
    try
    {
      SoLoader.loadLibrary("hermes-executor-release");
      mode_ = "Release";
      return;
    }
    catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
    {
      for (;;) {}
    }
    SoLoader.loadLibrary("hermes-executor-debug");
  }
  
  HermesExecutor(RuntimeConfig paramRuntimeConfig)
  {
    super(paramRuntimeConfig);
  }
  
  public static native boolean canLoadFile(String paramString);
  
  private static native HybridData initHybrid(long paramLong1, boolean paramBoolean1, int paramInt, boolean paramBoolean2, HermesMemoryDumper paramHermesMemoryDumper, long paramLong2, long paramLong3);
  
  private static native HybridData initHybridDefaultConfig();
  
  public String getName()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("HermesExecutor");
    localStringBuilder.append(mode_);
    return localStringBuilder.toString();
  }
}
