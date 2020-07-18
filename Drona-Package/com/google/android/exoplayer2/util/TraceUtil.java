package com.google.android.exoplayer2.util;

import android.os.Trace;

public final class TraceUtil
{
  private TraceUtil() {}
  
  public static void beginSection(String paramString)
  {
    if (Util.SDK_INT >= 18) {
      beginSectionV18(paramString);
    }
  }
  
  private static void beginSectionV18(String paramString)
  {
    Trace.beginSection(paramString);
  }
  
  public static void endSection()
  {
    if (Util.SDK_INT >= 18) {
      endSectionV18();
    }
  }
  
  private static void endSectionV18() {}
}
