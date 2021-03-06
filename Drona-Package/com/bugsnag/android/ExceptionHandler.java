package com.bugsnag.android;

import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

class ExceptionHandler
  implements Thread.UncaughtExceptionHandler
{
  private static final String STRICT_MODE_KEY = "Violation";
  private static final String STRICT_MODE_TAB = "StrictMode";
  final Map<Client, Boolean> clientMap = new WeakHashMap();
  private final Thread.UncaughtExceptionHandler originalHandler;
  private final StrictModeHandler strictModeHandler = new StrictModeHandler();
  
  ExceptionHandler(Thread.UncaughtExceptionHandler paramUncaughtExceptionHandler)
  {
    originalHandler = paramUncaughtExceptionHandler;
  }
  
  static void disable(Client paramClient)
  {
    Object localObject = Thread.getDefaultUncaughtExceptionHandler();
    if ((localObject instanceof ExceptionHandler))
    {
      localObject = (ExceptionHandler)localObject;
      clientMap.remove(paramClient);
      if (clientMap.isEmpty()) {
        Thread.setDefaultUncaughtExceptionHandler(originalHandler);
      }
    }
  }
  
  static void enable(Client paramClient)
  {
    Object localObject = Thread.getDefaultUncaughtExceptionHandler();
    if ((localObject instanceof ExceptionHandler))
    {
      localObject = (ExceptionHandler)localObject;
    }
    else
    {
      localObject = new ExceptionHandler((Thread.UncaughtExceptionHandler)localObject);
      Thread.setDefaultUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)localObject);
    }
    clientMap.put(paramClient, Boolean.valueOf(true));
  }
  
  public void uncaughtException(Thread paramThread, Throwable paramThrowable)
  {
    boolean bool = strictModeHandler.isStrictModeThrowable(paramThrowable);
    Iterator localIterator = clientMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Client localClient = (Client)localIterator.next();
      MetaData localMetaData = new MetaData();
      String str1;
      if (bool)
      {
        str1 = strictModeHandler.getViolationDescription(paramThrowable.getMessage());
        localMetaData = new MetaData();
        localMetaData.addToTab("StrictMode", "Violation", str1);
      }
      else
      {
        str1 = null;
      }
      if (bool) {}
      for (String str2 = "strictMode";; str2 = "unhandledException") {
        break;
      }
      if (bool)
      {
        StrictMode.ThreadPolicy localThreadPolicy = StrictMode.getThreadPolicy();
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);
        localClient.cacheAndNotify(paramThrowable, Severity.ERROR, localMetaData, str2, str1, paramThread);
        StrictMode.setThreadPolicy(localThreadPolicy);
      }
      else
      {
        localClient.cacheAndNotify(paramThrowable, Severity.ERROR, localMetaData, str2, str1, paramThread);
      }
    }
    if (originalHandler != null)
    {
      originalHandler.uncaughtException(paramThread, paramThrowable);
      return;
    }
    System.err.printf("Exception in thread \"%s\" ", new Object[] { paramThread.getName() });
    Logger.warn("Exception", paramThrowable);
  }
}
