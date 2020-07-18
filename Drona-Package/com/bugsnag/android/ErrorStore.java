package com.bugsnag.android;

import android.content.Context;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

class ErrorStore
  extends FileStore<Error>
{
  static final Comparator<File> ERROR_REPORT_COMPARATOR = new Comparator()
  {
    public int compare(File paramAnonymousFile1, File paramAnonymousFile2)
    {
      if ((paramAnonymousFile1 == null) && (paramAnonymousFile2 == null)) {
        return 0;
      }
      if (paramAnonymousFile1 == null) {
        return 1;
      }
      if (paramAnonymousFile2 == null) {
        return -1;
      }
      return paramAnonymousFile1.getName().replaceAll("_startupcrash", "").compareTo(paramAnonymousFile2.getName().replaceAll("_startupcrash", ""));
    }
  };
  private static final int LAUNCH_CRASH_POLL_MS = 50;
  private static final long LAUNCH_CRASH_TIMEOUT_MS = 2000L;
  private static final String STARTUP_CRASH = "_startupcrash";
  volatile boolean flushOnLaunchCompleted = false;
  private final Semaphore semaphore = new Semaphore(1);
  
  ErrorStore(Configuration paramConfiguration, Context paramContext, FileStore.Delegate paramDelegate)
  {
    super(paramConfiguration, paramContext, "/bugsnag-errors/", 128, ERROR_REPORT_COMPARATOR, paramDelegate);
  }
  
  private List findLaunchCrashReports(Collection paramCollection)
  {
    ArrayList localArrayList = new ArrayList();
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      File localFile = (File)paramCollection.next();
      if (isLaunchCrashReport(localFile)) {
        localArrayList.add(localFile);
      }
    }
    return localArrayList;
  }
  
  private void flushErrorReport(File paramFile)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a5 = a4\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:552)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:1)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:166)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:331)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:387)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:90)\n\t... 17 more\n");
  }
  
  void flushAsync()
  {
    if (storeDirectory == null) {
      return;
    }
    try
    {
      Async.runAsync(new Runnable()
      {
        public void run()
        {
          flushReports(findStoredFiles());
        }
      });
      return;
    }
    catch (RejectedExecutionException localRejectedExecutionException)
    {
      for (;;) {}
    }
    Logger.warn("Failed to flush all on-disk errors, retaining unsent errors for later.");
  }
  
  void flushOnLaunch()
  {
    long l2 = config.getLaunchCrashThresholdMs();
    long l1 = 0L;
    if (l2 != 0L)
    {
      List localList1 = findStoredFiles();
      final List localList2 = findLaunchCrashReports(localList1);
      localList1.removeAll(localList2);
      cancelQueuedFiles(localList1);
      if (!localList2.isEmpty())
      {
        flushOnLaunchCompleted = false;
        Logger.info("Attempting to send launch crash reports");
        try
        {
          Async.runAsync(new Runnable()
          {
            public void run()
            {
              flushReports(localList2);
              flushOnLaunchCompleted = true;
            }
          });
        }
        catch (RejectedExecutionException localRejectedExecutionException)
        {
          Logger.warn("Failed to flush launch crash reports", localRejectedExecutionException);
          flushOnLaunchCompleted = true;
        }
        while ((!flushOnLaunchCompleted) && (l1 < 2000L))
        {
          try
          {
            Thread.sleep(50L);
            l1 += 50L;
          }
          catch (InterruptedException localInterruptedException)
          {
            for (;;) {}
          }
          Logger.warn("Interrupted while waiting for launch crash report request");
        }
        Logger.info("Continuing with Bugsnag initialisation");
      }
    }
    flushAsync();
  }
  
  void flushReports(Collection paramCollection)
  {
    if ((!paramCollection.isEmpty()) && (semaphore.tryAcquire(1))) {
      try
      {
        Logger.info(String.format(Locale.US, "Sending %d saved error(s) to Bugsnag", new Object[] { Integer.valueOf(paramCollection.size()) }));
        paramCollection = paramCollection.iterator();
        for (;;)
        {
          boolean bool = paramCollection.hasNext();
          if (!bool) {
            break;
          }
          flushErrorReport((File)paramCollection.next());
        }
        semaphore.release(1);
        return;
      }
      catch (Throwable paramCollection)
      {
        semaphore.release(1);
        throw paramCollection;
      }
    }
  }
  
  String getFilename(Object paramObject)
  {
    String str = "";
    if ((paramObject instanceof Error))
    {
      Map localMap = ((Error)paramObject).getAppData();
      paramObject = str;
      if ((localMap instanceof Map))
      {
        paramObject = str;
        if ((localMap.get("duration") instanceof Number))
        {
          paramObject = str;
          if (isStartupCrash(((Number)localMap.get("duration")).longValue())) {
            paramObject = "_startupcrash";
          }
        }
      }
    }
    else
    {
      paramObject = "not-jvm";
    }
    str = UUID.randomUUID().toString();
    long l = System.currentTimeMillis();
    return String.format(Locale.US, "%s%d_%s%s.json", new Object[] { storeDirectory, Long.valueOf(l), str, paramObject });
  }
  
  boolean isLaunchCrashReport(File paramFile)
  {
    return paramFile.getName().endsWith("_startupcrash.json");
  }
  
  boolean isStartupCrash(long paramLong)
  {
    return paramLong < config.getLaunchCrashThresholdMs();
  }
}
