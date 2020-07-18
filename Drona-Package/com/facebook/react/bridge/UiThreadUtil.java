package com.facebook.react.bridge;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;

public class UiThreadUtil
{
  @Nullable
  private static Handler sMainHandler;
  
  public UiThreadUtil() {}
  
  public static void assertNotOnUiThread()
  {
    SoftAssertions.assertCondition(isOnUiThread() ^ true, "Expected not to run on UI thread!");
  }
  
  public static void assertOnUiThread()
  {
    SoftAssertions.assertCondition(isOnUiThread(), "Expected to run on UI thread!");
  }
  
  public static boolean isOnUiThread()
  {
    return Looper.getMainLooper().getThread() == Thread.currentThread();
  }
  
  public static void runOnUiThread(Runnable paramRunnable)
  {
    runOnUiThread(paramRunnable, 0L);
  }
  
  public static void runOnUiThread(Runnable paramRunnable, long paramLong)
  {
    try
    {
      if (sMainHandler == null) {
        sMainHandler = new Handler(Looper.getMainLooper());
      }
      sMainHandler.postDelayed(paramRunnable, paramLong);
      return;
    }
    catch (Throwable paramRunnable)
    {
      throw paramRunnable;
    }
  }
}
