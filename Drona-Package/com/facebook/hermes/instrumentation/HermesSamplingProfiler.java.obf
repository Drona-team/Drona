package com.facebook.hermes.instrumentation;

import com.facebook.soloader.SoLoader;

public class HermesSamplingProfiler
{
  static
  {
    SoLoader.loadLibrary("jsijniprofiler");
  }
  
  private HermesSamplingProfiler() {}
  
  public static native void disable();
  
  public static native void dumpSampledTraceToFile(String paramString);
  
  public static native void enable();
}
