package com.amplitude.upgrade;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;

class AmplitudeCallbacks
  implements Application.ActivityLifecycleCallbacks
{
  private static final String NULLMSG = "Need to initialize AmplitudeCallbacks with AmplitudeClient instance";
  public static final String PAGE_KEY = "com.amplitude.api.AmplitudeCallbacks";
  private static AmplitudeLog logger = ;
  private AmplitudeClient clientInstance = null;
  
  public AmplitudeCallbacks(AmplitudeClient paramAmplitudeClient)
  {
    if (paramAmplitudeClient == null)
    {
      logger.e("com.amplitude.api.AmplitudeCallbacks", "Need to initialize AmplitudeCallbacks with AmplitudeClient instance");
      return;
    }
    clientInstance = paramAmplitudeClient;
    paramAmplitudeClient.useForegroundTracking();
  }
  
  protected long getCurrentTimeMillis()
  {
    return System.currentTimeMillis();
  }
  
  public void onActivityCreated(Activity paramActivity, Bundle paramBundle) {}
  
  public void onActivityDestroyed(Activity paramActivity) {}
  
  public void onActivityPaused(Activity paramActivity)
  {
    if (clientInstance == null)
    {
      logger.e("com.amplitude.api.AmplitudeCallbacks", "Need to initialize AmplitudeCallbacks with AmplitudeClient instance");
      return;
    }
    clientInstance.onExitForeground(getCurrentTimeMillis());
  }
  
  public void onActivityResumed(Activity paramActivity)
  {
    if (clientInstance == null)
    {
      logger.e("com.amplitude.api.AmplitudeCallbacks", "Need to initialize AmplitudeCallbacks with AmplitudeClient instance");
      return;
    }
    clientInstance.onEnterForeground(getCurrentTimeMillis());
  }
  
  public void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle) {}
  
  public void onActivityStarted(Activity paramActivity) {}
  
  public void onActivityStopped(Activity paramActivity) {}
}
