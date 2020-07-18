package com.facebook.react.modules.core;

import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseJavaModule;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.JavascriptException;
import com.facebook.react.devsupport.interfaces.DevSupportManager;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.util.ExceptionDataHelper;
import com.facebook.react.util.JSStackTrace;

@ReactModule(name="ExceptionsManager")
public class ExceptionsManagerModule
  extends BaseJavaModule
{
  public static final String NAME = "ExceptionsManager";
  private final DevSupportManager mDevSupportManager;
  
  public ExceptionsManagerModule(DevSupportManager paramDevSupportManager)
  {
    mDevSupportManager = paramDevSupportManager;
  }
  
  public void dismissRedbox()
  {
    if (mDevSupportManager.getDevSupportEnabled()) {
      mDevSupportManager.hideRedboxDialog();
    }
  }
  
  public String getName()
  {
    return "ExceptionsManager";
  }
  
  public void reportException(ReadableMap paramReadableMap)
  {
    String str;
    if (paramReadableMap.hasKey("message")) {
      str = paramReadableMap.getString("message");
    } else {
      str = "";
    }
    Object localObject;
    if (paramReadableMap.hasKey("stack")) {
      localObject = paramReadableMap.getArray("stack");
    } else {
      localObject = Arguments.createArray();
    }
    int i;
    if (paramReadableMap.hasKey("id")) {
      i = paramReadableMap.getInt("id");
    } else {
      i = -1;
    }
    boolean bool;
    if (paramReadableMap.hasKey("isFatal")) {
      bool = paramReadableMap.getBoolean("isFatal");
    } else {
      bool = false;
    }
    if (mDevSupportManager.getDevSupportEnabled())
    {
      mDevSupportManager.showNewJSError(str, (ReadableArray)localObject, i);
      return;
    }
    paramReadableMap = ExceptionDataHelper.getExtraDataAsJson(paramReadableMap);
    if (!bool)
    {
      FLog.e("ReactNative", JSStackTrace.format(str, (ReadableArray)localObject));
      if (paramReadableMap != null) {
        FLog.d("ReactNative", "extraData: %s", paramReadableMap);
      }
    }
    else
    {
      throw new JavascriptException(JSStackTrace.format(str, (ReadableArray)localObject)).setExtraDataAsJson(paramReadableMap);
    }
  }
  
  public void reportFatalException(String paramString, ReadableArray paramReadableArray, int paramInt)
  {
    JavaOnlyMap localJavaOnlyMap = new JavaOnlyMap();
    localJavaOnlyMap.putString("message", paramString);
    localJavaOnlyMap.putArray("stack", paramReadableArray);
    localJavaOnlyMap.putInt("id", paramInt);
    localJavaOnlyMap.putBoolean("isFatal", true);
    reportException(localJavaOnlyMap);
  }
  
  public void reportSoftException(String paramString, ReadableArray paramReadableArray, int paramInt)
  {
    JavaOnlyMap localJavaOnlyMap = new JavaOnlyMap();
    localJavaOnlyMap.putString("message", paramString);
    localJavaOnlyMap.putArray("stack", paramReadableArray);
    localJavaOnlyMap.putInt("id", paramInt);
    localJavaOnlyMap.putBoolean("isFatal", false);
    reportException(localJavaOnlyMap);
  }
  
  public void updateExceptionMessage(String paramString, ReadableArray paramReadableArray, int paramInt)
  {
    if (mDevSupportManager.getDevSupportEnabled()) {
      mDevSupportManager.updateJSError(paramString, paramReadableArray, paramInt);
    }
  }
}
