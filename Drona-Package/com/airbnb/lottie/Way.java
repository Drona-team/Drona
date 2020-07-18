package com.airbnb.lottie;

import androidx.annotation.RestrictTo;
import androidx.core.opml.TraceCompat;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
public class Way
{
  private static final int MAX_DEPTH = 20;
  public static final String NAME = "LOTTIE";
  private static int depthPastMaxDepth;
  private static String[] sections;
  private static long[] startTimeNs;
  public static boolean tags;
  private static int traceDepth;
  private static boolean traceEnabled;
  
  public Way() {}
  
  public static void beginSection(String paramString)
  {
    if (!traceEnabled) {
      return;
    }
    if (traceDepth == 20)
    {
      depthPastMaxDepth += 1;
      return;
    }
    sections[traceDepth] = paramString;
    startTimeNs[traceDepth] = System.nanoTime();
    TraceCompat.beginSection(paramString);
    traceDepth += 1;
  }
  
  public static float endSection(String paramString)
  {
    if (depthPastMaxDepth > 0)
    {
      depthPastMaxDepth -= 1;
      return 0.0F;
    }
    if (!traceEnabled) {
      return 0.0F;
    }
    traceDepth -= 1;
    if (traceDepth != -1)
    {
      if (paramString.equals(sections[traceDepth]))
      {
        TraceCompat.endSection();
        return (float)(System.nanoTime() - startTimeNs[traceDepth]) / 1000000.0F;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unbalanced trace call ");
      localStringBuilder.append(paramString);
      localStringBuilder.append(". Expected ");
      localStringBuilder.append(sections[traceDepth]);
      localStringBuilder.append(".");
      throw new IllegalStateException(localStringBuilder.toString());
    }
    throw new IllegalStateException("Can't end trace section. There are none.");
  }
  
  public static void setTraceEnabled(boolean paramBoolean)
  {
    if (traceEnabled == paramBoolean) {
      return;
    }
    traceEnabled = paramBoolean;
    if (traceEnabled)
    {
      sections = new String[20];
      startTimeNs = new long[20];
    }
  }
}
