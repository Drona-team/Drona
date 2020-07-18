package com.github.yamill.orientation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import java.util.HashMap;
import java.util.Map;

public class OrientationModule
  extends ReactContextBaseJavaModule
  implements LifecycleEventListener
{
  final BroadcastReceiver receiver;
  
  public OrientationModule(final ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
    receiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        paramAnonymousContext = (Configuration)paramAnonymousIntent.getParcelableExtra("newConfig");
        Log.d("receiver", String.valueOf(orientation));
        if (orientation == 1) {
          paramAnonymousContext = "PORTRAIT";
        } else {
          paramAnonymousContext = "LANDSCAPE";
        }
        paramAnonymousIntent = Arguments.createMap();
        paramAnonymousIntent.putString("orientation", paramAnonymousContext);
        if (paramReactApplicationContext.hasActiveCatalystInstance()) {
          ((DeviceEventManagerModule.RCTDeviceEventEmitter)paramReactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit("orientationDidChange", paramAnonymousIntent);
        }
      }
    };
    paramReactApplicationContext.addLifecycleEventListener(this);
  }
  
  private String getOrientationString(int paramInt)
  {
    if (paramInt == 2) {
      return "LANDSCAPE";
    }
    if (paramInt == 1) {
      return "PORTRAIT";
    }
    if (paramInt == 0) {
      return "UNKNOWN";
    }
    return "null";
  }
  
  public Map getConstants()
  {
    HashMap localHashMap = new HashMap();
    String str = getOrientationString(getReactApplicationContextgetResourcesgetConfigurationorientation);
    if (str == "null")
    {
      localHashMap.put("initialOrientation", null);
      return localHashMap;
    }
    localHashMap.put("initialOrientation", str);
    return localHashMap;
  }
  
  public String getName()
  {
    return "Orientation";
  }
  
  public void getOrientation(Callback paramCallback)
  {
    int i = getReactApplicationContextgetResourcesgetConfigurationorientation;
    String str = getOrientationString(i);
    if (str == "null")
    {
      paramCallback.invoke(new Object[] { Integer.valueOf(i), null });
      return;
    }
    paramCallback.invoke(new Object[] { null, str });
  }
  
  public void lockToLandscape()
  {
    Activity localActivity = getCurrentActivity();
    if (localActivity == null) {
      return;
    }
    localActivity.setRequestedOrientation(6);
  }
  
  public void lockToLandscapeLeft()
  {
    Activity localActivity = getCurrentActivity();
    if (localActivity == null) {
      return;
    }
    localActivity.setRequestedOrientation(0);
  }
  
  public void lockToLandscapeRight()
  {
    Activity localActivity = getCurrentActivity();
    if (localActivity == null) {
      return;
    }
    localActivity.setRequestedOrientation(8);
  }
  
  public void lockToPortrait()
  {
    Activity localActivity = getCurrentActivity();
    if (localActivity == null) {
      return;
    }
    localActivity.setRequestedOrientation(1);
  }
  
  public void onHostDestroy() {}
  
  public void onHostPause()
  {
    Activity localActivity = getCurrentActivity();
    if (localActivity == null) {
      return;
    }
    BroadcastReceiver localBroadcastReceiver = receiver;
    try
    {
      localActivity.unregisterReceiver(localBroadcastReceiver);
      return;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      FLog.e("ReactNative", "receiver already unregistered", localIllegalArgumentException);
    }
  }
  
  public void onHostResume()
  {
    Activity localActivity = getCurrentActivity();
    if (localActivity == null)
    {
      FLog.e("ReactNative", "no activity to register receiver");
      return;
    }
    localActivity.registerReceiver(receiver, new IntentFilter("onConfigurationChanged"));
  }
  
  public void unlockAllOrientations()
  {
    Activity localActivity = getCurrentActivity();
    if (localActivity == null) {
      return;
    }
    localActivity.setRequestedOrientation(4);
  }
}
