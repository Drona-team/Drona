package com.amplitude.upgrade;

import android.util.Log;

public class AmplitudeLog
{
  protected static AmplitudeLog instance = new AmplitudeLog();
  private volatile boolean enableLogging = true;
  private volatile int logLevel = 4;
  
  private AmplitudeLog() {}
  
  public static AmplitudeLog getLogger()
  {
    return instance;
  }
  
  int d(String paramString1, String paramString2)
  {
    if ((enableLogging) && (logLevel <= 3)) {
      return Log.d(paramString1, paramString2);
    }
    return 0;
  }
  
  int d(String paramString1, String paramString2, Throwable paramThrowable)
  {
    if ((enableLogging) && (logLevel <= 3)) {
      return Log.d(paramString1, paramString2, paramThrowable);
    }
    return 0;
  }
  
  int e(String paramString1, String paramString2)
  {
    if ((enableLogging) && (logLevel <= 6)) {
      return Log.e(paramString1, paramString2);
    }
    return 0;
  }
  
  int e(String paramString1, String paramString2, Throwable paramThrowable)
  {
    if ((enableLogging) && (logLevel <= 6)) {
      return Log.e(paramString1, paramString2, paramThrowable);
    }
    return 0;
  }
  
  String getStackTraceString(Throwable paramThrowable)
  {
    return Log.getStackTraceString(paramThrowable);
  }
  
  int i(String paramString1, String paramString2)
  {
    if ((enableLogging) && (logLevel <= 4)) {
      return Log.i(paramString1, paramString2);
    }
    return 0;
  }
  
  int i(String paramString1, String paramString2, Throwable paramThrowable)
  {
    if ((enableLogging) && (logLevel <= 4)) {
      return Log.i(paramString1, paramString2, paramThrowable);
    }
    return 0;
  }
  
  boolean isLoggable(String paramString, int paramInt)
  {
    return Log.isLoggable(paramString, paramInt);
  }
  
  int log(String paramString1, String paramString2)
  {
    if ((enableLogging) && (logLevel <= 7)) {
      return Log.wtf(paramString1, paramString2);
    }
    return 0;
  }
  
  int log(String paramString1, String paramString2, Throwable paramThrowable)
  {
    if ((enableLogging) && (logLevel <= 7)) {
      return Log.wtf(paramString1, paramString2, paramThrowable);
    }
    return 0;
  }
  
  int log(String paramString, Throwable paramThrowable)
  {
    if ((enableLogging) && (logLevel <= 7)) {
      return Log.wtf(paramString, paramThrowable);
    }
    return 0;
  }
  
  int println(int paramInt, String paramString1, String paramString2)
  {
    return Log.println(paramInt, paramString1, paramString2);
  }
  
  AmplitudeLog setEnableLogging(boolean paramBoolean)
  {
    enableLogging = paramBoolean;
    return instance;
  }
  
  AmplitudeLog setLogLevel(int paramInt)
  {
    logLevel = paramInt;
    return instance;
  }
  
  int v(String paramString1, String paramString2)
  {
    if ((enableLogging) && (logLevel <= 2)) {
      return Log.v(paramString1, paramString2);
    }
    return 0;
  }
  
  int v(String paramString1, String paramString2, Throwable paramThrowable)
  {
    if ((enableLogging) && (logLevel <= 2)) {
      return Log.v(paramString1, paramString2, paramThrowable);
    }
    return 0;
  }
  
  int w(String paramString1, String paramString2)
  {
    if ((enableLogging) && (logLevel <= 5)) {
      return Log.w(paramString1, paramString2);
    }
    return 0;
  }
  
  int w(String paramString1, String paramString2, Throwable paramThrowable)
  {
    if ((enableLogging) && (logLevel <= 5)) {
      return Log.w(paramString1, paramString2, paramThrowable);
    }
    return 0;
  }
  
  int w(String paramString, Throwable paramThrowable)
  {
    if ((enableLogging) && (logLevel <= 5)) {
      return Log.w(paramString, paramThrowable);
    }
    return 0;
  }
}
