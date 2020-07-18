package com.bugsnag.android;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

class ThreadState
  implements JsonStream.Streamable
{
  private static final String THREAD_TYPE = "android";
  private final CachedThread[] cachedThreads;
  
  public ThreadState(Configuration paramConfiguration, Thread paramThread, Map paramMap, Throwable paramThrowable)
  {
    if (!paramMap.containsKey(paramThread)) {
      paramMap.put(paramThread, paramThread.getStackTrace());
    }
    if (paramThrowable != null) {
      paramMap.put(paramThread, paramThrowable.getStackTrace());
    }
    long l1 = paramThread.getId();
    paramThread = sortThreadsById(paramMap);
    cachedThreads = new CachedThread[paramThread.length];
    int i = 0;
    while (i < paramThread.length)
    {
      paramThrowable = paramThread[i];
      CachedThread[] arrayOfCachedThread = cachedThreads;
      long l2 = paramThrowable.getId();
      String str = paramThrowable.getName();
      boolean bool;
      if (paramThrowable.getId() == l1) {
        bool = true;
      } else {
        bool = false;
      }
      arrayOfCachedThread[i] = new CachedThread(paramConfiguration, l2, str, "android", bool, (StackTraceElement[])paramMap.get(paramThrowable));
      i += 1;
    }
  }
  
  ThreadState(CachedThread[] paramArrayOfCachedThread)
  {
    cachedThreads = paramArrayOfCachedThread;
  }
  
  private Thread[] sortThreadsById(Map paramMap)
  {
    paramMap = (Thread[])paramMap.keySet().toArray(new Thread[0]);
    Arrays.sort(paramMap, new Comparator()
    {
      public int compare(Thread paramAnonymousThread1, Thread paramAnonymousThread2)
      {
        return Long.valueOf(paramAnonymousThread1.getId()).compareTo(Long.valueOf(paramAnonymousThread2.getId()));
      }
    });
    return paramMap;
  }
  
  public void toStream(JsonStream paramJsonStream)
    throws IOException
  {
    paramJsonStream.beginArray();
    CachedThread[] arrayOfCachedThread = cachedThreads;
    int j = arrayOfCachedThread.length;
    int i = 0;
    while (i < j)
    {
      paramJsonStream.value(arrayOfCachedThread[i]);
      i += 1;
    }
    paramJsonStream.endArray();
  }
}
