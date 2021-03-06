package com.clipsub.RNShake;

import android.hardware.SensorManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;

@ReactModule(name="RNShakeEvent")
public class RNShakeEventModule
  extends ReactContextBaseJavaModule
{
  private final CustomShakeDetector mShakeDetector;
  
  public RNShakeEventModule(final ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
    mShakeDetector = new CustomShakeDetector(new CustomShakeDetector.ShakeListener()
    {
      public void onShake()
      {
        RNShakeEventModule.this.sendEvent(paramReactApplicationContext, "ShakeEvent", null);
      }
    }, 1);
    mShakeDetector.start((SensorManager)paramReactApplicationContext.getSystemService("sensor"));
  }
  
  private void sendEvent(ReactContext paramReactContext, String paramString, WritableMap paramWritableMap)
  {
    if (paramReactContext.hasActiveCatalystInstance()) {
      ((DeviceEventManagerModule.RCTDeviceEventEmitter)paramReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit(paramString, paramWritableMap);
    }
  }
  
  public String getName()
  {
    return "RNShakeEvent";
  }
}
