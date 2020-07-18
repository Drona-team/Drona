package com.google.android.exoplayer2.util;

public final class LibraryLoader
{
  private boolean isAvailable;
  private boolean loadAttempted;
  private String[] nativeLibraries;
  
  public LibraryLoader(String... paramVarArgs)
  {
    nativeLibraries = paramVarArgs;
  }
  
  public boolean isAvailable()
  {
    for (;;)
    {
      try
      {
        if (loadAttempted)
        {
          bool = isAvailable;
          return bool;
        }
        loadAttempted = true;
        String[] arrayOfString = nativeLibraries;
        int j = arrayOfString.length;
        i = 0;
        if (i < j) {
          str = arrayOfString[i];
        }
      }
      catch (Throwable localThrowable)
      {
        boolean bool;
        int i;
        String str;
        throw localThrowable;
      }
      try
      {
        System.loadLibrary(str);
        i += 1;
      }
      catch (UnsatisfiedLinkError localUnsatisfiedLinkError) {}
    }
    isAvailable = true;
    bool = isAvailable;
    return bool;
  }
  
  public void setLibraries(String... paramVarArgs)
  {
    try
    {
      Assertions.checkState(loadAttempted ^ true, "Cannot set libraries after loading");
      nativeLibraries = paramVarArgs;
      return;
    }
    catch (Throwable paramVarArgs)
    {
      throw paramVarArgs;
    }
  }
}
