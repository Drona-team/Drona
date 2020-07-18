package com.bugsnag.android;

import android.util.Log;

final class Logger
{
  private static final String LOG_TAG = "Bugsnag";
  private static volatile boolean enabled;
  
  private Logger() {}
  
  static boolean getEnabled()
  {
    return enabled;
  }
  
  static void info(String paramString)
  {
    if (enabled) {
      Log.i("Bugsnag", paramString);
    }
  }
  
  static void setEnabled(boolean paramBoolean)
  {
    enabled = paramBoolean;
  }
  
  static void warn(String paramString)
  {
    if (enabled) {
      Log.w("Bugsnag", paramString);
    }
  }
  
  static void warn(String paramString, Throwable paramThrowable)
  {
    if (enabled) {
      Log.w("Bugsnag", paramString, paramThrowable);
    }
  }
}
