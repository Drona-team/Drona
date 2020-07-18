package com.facebook.soloader;

import android.os.StrictMode.ThreadPolicy;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

public abstract class SoSource
{
  public static final int LOAD_FLAG_ALLOW_IMPLICIT_PROVISION = 1;
  public static final int LOAD_FLAG_ALLOW_SOURCE_CHANGE = 2;
  public static final int LOAD_FLAG_MIN_CUSTOM_FLAG = 4;
  public static final int LOAD_RESULT_CORRUPTED_LIB_FILE = 3;
  public static final int LOAD_RESULT_IMPLICITLY_PROVIDED = 2;
  public static final int LOAD_RESULT_LOADED = 1;
  public static final int LOAD_RESULT_NOT_FOUND = 0;
  public static final int PREPARE_FLAG_ALLOW_ASYNC_INIT = 1;
  public static final int PREPARE_FLAG_FORCE_REFRESH = 2;
  
  public SoSource() {}
  
  public void addToLdLibraryPath(Collection paramCollection) {}
  
  public String[] getSoSourceAbis()
  {
    return SysUtil.getSupportedAbis();
  }
  
  public abstract int loadLibrary(String paramString, int paramInt, StrictMode.ThreadPolicy paramThreadPolicy)
    throws IOException;
  
  protected void prepare(int paramInt)
    throws IOException
  {}
  
  public String toString()
  {
    return getClass().getName();
  }
  
  public abstract File unpackLibrary(String paramString)
    throws IOException;
}
