package com.google.android.gms.flags;

import com.google.android.gms.common.annotation.KeepForSdk;

@KeepForSdk
public final class Singletons
{
  private static Singletons handler;
  private final FlagRegistry mixed = new FlagRegistry();
  private final FileCache ourInstance = new FileCache();
  
  static
  {
    Singletons localSingletons = new Singletons();
    try
    {
      handler = localSingletons;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private Singletons() {}
  
  public static FileCache Instance()
  {
    return getBackgroundHandlerourInstance;
  }
  
  public static FlagRegistry flagRegistry()
  {
    return getBackgroundHandlermixed;
  }
  
  private static Singletons getBackgroundHandler()
  {
    try
    {
      Singletons localSingletons = handler;
      return localSingletons;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}
