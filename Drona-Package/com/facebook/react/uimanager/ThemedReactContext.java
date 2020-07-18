package com.facebook.react.uimanager;

import android.app.Activity;
import android.content.Context;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;

public class ThemedReactContext
  extends ReactContext
{
  private final ReactApplicationContext mReactApplicationContext;
  
  public ThemedReactContext(ReactApplicationContext paramReactApplicationContext, Context paramContext)
  {
    super(paramContext);
    if (paramReactApplicationContext.hasCatalystInstance()) {
      initializeWithInstance(paramReactApplicationContext.getCatalystInstance());
    }
    mReactApplicationContext = paramReactApplicationContext;
  }
  
  public void addLifecycleEventListener(LifecycleEventListener paramLifecycleEventListener)
  {
    mReactApplicationContext.addLifecycleEventListener(paramLifecycleEventListener);
  }
  
  public Activity getCurrentActivity()
  {
    return mReactApplicationContext.getCurrentActivity();
  }
  
  public boolean hasCurrentActivity()
  {
    return mReactApplicationContext.hasCurrentActivity();
  }
  
  public void removeLifecycleEventListener(LifecycleEventListener paramLifecycleEventListener)
  {
    mReactApplicationContext.removeLifecycleEventListener(paramLifecycleEventListener);
  }
}