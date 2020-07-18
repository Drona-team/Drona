package com.facebook.react.fabric.events;

import android.annotation.SuppressLint;
import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.fabric.FabricSoLoader;
import com.facebook.react.uimanager.events.BatchEventDispatchedListener;
import com.facebook.upgrade.HybridData;

@SuppressLint({"MissingNativeLoadLibrary"})
public class EventBeatManager
  implements BatchEventDispatchedListener
{
  @DoNotStrip
  private final HybridData mHybridData = initHybrid();
  private final ReactApplicationContext mReactApplicationContext;
  
  static {}
  
  public EventBeatManager(ReactApplicationContext paramReactApplicationContext)
  {
    mReactApplicationContext = paramReactApplicationContext;
  }
  
  private native void beat();
  
  private void dispatchEventsAsync()
  {
    if (mReactApplicationContext.isOnJSQueueThread())
    {
      beat();
      return;
    }
    mReactApplicationContext.runOnJSQueueThread(new Runnable()
    {
      public void run()
      {
        EventBeatManager.this.beat();
      }
    });
  }
  
  private static native HybridData initHybrid();
  
  public void onBatchEventDispatched()
  {
    dispatchEventsAsync();
  }
}
