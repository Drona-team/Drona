package com.google.android.exoplayer2;

import java.util.HashSet;

public final class ExoPlayerLibraryInfo
{
  public static final boolean ASSERTIONS_ENABLED = true;
  public static final boolean GL_ASSERTIONS_ENABLED = false;
  public static final String TAG = "ExoPlayer";
  public static final boolean TRACE_ENABLED = true;
  public static final String VERSION = "2.9.2";
  public static final int VERSION_INT = 2009002;
  public static final String VERSION_SLASHY = "ExoPlayerLib/2.9.2";
  private static final HashSet<String> registeredModules = new HashSet();
  private static String registeredModulesString = "goog.exo.core";
  
  private ExoPlayerLibraryInfo() {}
  
  public static void registerModule(String paramString)
  {
    try
    {
      if (registeredModules.add(paramString))
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(registeredModulesString);
        localStringBuilder.append(", ");
        localStringBuilder.append(paramString);
        registeredModulesString = localStringBuilder.toString();
      }
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public static String registeredModules()
  {
    try
    {
      String str = registeredModulesString;
      return str;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}
