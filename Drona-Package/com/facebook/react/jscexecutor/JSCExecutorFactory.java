package com.facebook.react.jscexecutor;

import com.facebook.react.bridge.JavaScriptExecutor;
import com.facebook.react.bridge.JavaScriptExecutorFactory;
import com.facebook.react.bridge.WritableNativeMap;

public class JSCExecutorFactory
  implements JavaScriptExecutorFactory
{
  private final String mAppName;
  private final String mDeviceName;
  
  public JSCExecutorFactory(String paramString1, String paramString2)
  {
    mAppName = paramString1;
    mDeviceName = paramString2;
  }
  
  public JavaScriptExecutor create()
    throws Exception
  {
    WritableNativeMap localWritableNativeMap = new WritableNativeMap();
    localWritableNativeMap.putString("OwnerIdentity", "ReactNative");
    localWritableNativeMap.putString("AppIdentity", mAppName);
    localWritableNativeMap.putString("DeviceIdentity", mDeviceName);
    return new JSCExecutor(localWritableNativeMap);
  }
  
  public void startSamplingProfiler()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Starting sampling profiler not supported on ");
    localStringBuilder.append(toString());
    throw new UnsupportedOperationException(localStringBuilder.toString());
  }
  
  public void stopSamplingProfiler(String paramString)
  {
    paramString = new StringBuilder();
    paramString.append("Stopping sampling profiler not supported on ");
    paramString.append(toString());
    throw new UnsupportedOperationException(paramString.toString());
  }
  
  public String toString()
  {
    return "JSIExecutor+JSCRuntime";
  }
}
