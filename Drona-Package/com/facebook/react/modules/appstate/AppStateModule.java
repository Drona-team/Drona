package com.facebook.react.modules.appstate;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.WindowFocusChangeListener;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import java.util.HashMap;
import java.util.Map;

@ReactModule(name="AppState")
public class AppStateModule
  extends ReactContextBaseJavaModule
  implements LifecycleEventListener, WindowFocusChangeListener
{
  public static final String APP_STATE_ACTIVE = "active";
  public static final String APP_STATE_BACKGROUND = "background";
  private static final String INITIAL_STATE = "initialAppState";
  public static final String NAME = "AppState";
  private String mAppState;
  
  public AppStateModule(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
    paramReactApplicationContext.addLifecycleEventListener(this);
    paramReactApplicationContext.addWindowFocusChangeListener(this);
    if (paramReactApplicationContext.getLifecycleState() == LifecycleState.RESUMED) {
      paramReactApplicationContext = "active";
    } else {
      paramReactApplicationContext = "background";
    }
    mAppState = paramReactApplicationContext;
  }
  
  private WritableMap createAppStateEventMap()
  {
    WritableMap localWritableMap = Arguments.createMap();
    localWritableMap.putString("app_state", mAppState);
    return localWritableMap;
  }
  
  private void sendAppStateChangeEvent()
  {
    ((DeviceEventManagerModule.RCTDeviceEventEmitter)getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit("appStateDidChange", createAppStateEventMap());
  }
  
  public Map getConstants()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("initialAppState", mAppState);
    return localHashMap;
  }
  
  public void getCurrentAppState(Callback paramCallback1, Callback paramCallback2)
  {
    paramCallback1.invoke(new Object[] { createAppStateEventMap() });
  }
  
  public String getName()
  {
    return "AppState";
  }
  
  public void onHostDestroy() {}
  
  public void onHostPause()
  {
    mAppState = "background";
    sendAppStateChangeEvent();
  }
  
  public void onHostResume()
  {
    mAppState = "active";
    sendAppStateChangeEvent();
  }
  
  public void onWindowFocusChange(boolean paramBoolean)
  {
    ((DeviceEventManagerModule.RCTDeviceEventEmitter)getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit("appStateFocusChange", Boolean.valueOf(paramBoolean));
  }
}