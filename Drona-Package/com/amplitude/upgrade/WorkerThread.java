package com.amplitude.upgrade;

import android.os.Handler;
import android.os.HandlerThread;

public class WorkerThread
  extends HandlerThread
{
  private Handler handler;
  
  public WorkerThread(String paramString)
  {
    super(paramString);
  }
  
  private void waitForInitialization()
  {
    try
    {
      if (handler == null) {
        handler = new Handler(getLooper());
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  Handler getHandler()
  {
    return handler;
  }
  
  void post(Runnable paramRunnable)
  {
    waitForInitialization();
    handler.post(paramRunnable);
  }
  
  void postDelayed(Runnable paramRunnable, long paramLong)
  {
    waitForInitialization();
    handler.postDelayed(paramRunnable, paramLong);
  }
  
  void removeCallbacks(Runnable paramRunnable)
  {
    waitForInitialization();
    handler.removeCallbacks(paramRunnable);
  }
}
