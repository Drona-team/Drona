package com.facebook.react.bridge;

import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.react.bridge.queue.ReactQueueConfiguration;
import com.facebook.react.turbomodule.core.interfaces.JSCallInvokerHolder;
import java.util.Collection;
import java.util.List;

@DoNotStrip
public abstract interface CatalystInstance
  extends MemoryPressureListener, JSInstance, JSBundleLoaderDelegate
{
  public abstract void addBridgeIdleDebugListener(NotThreadSafeBridgeIdleDebugListener paramNotThreadSafeBridgeIdleDebugListener);
  
  public abstract void addJSIModules(List paramList);
  
  public abstract void callFunction(String paramString1, String paramString2, NativeArray paramNativeArray);
  
  public abstract void destroy();
  
  public abstract void extendNativeModules(NativeModuleRegistry paramNativeModuleRegistry);
  
  public abstract JSCallInvokerHolder getJSCallInvokerHolder();
  
  public abstract JSIModule getJSIModule(JSIModuleType paramJSIModuleType);
  
  public abstract JavaScriptModule getJSModule(Class paramClass);
  
  public abstract JavaScriptContextHolder getJavaScriptContextHolder();
  
  public abstract NativeModule getNativeModule(Class paramClass);
  
  public abstract NativeModule getNativeModule(String paramString);
  
  public abstract Collection getNativeModules();
  
  public abstract ReactQueueConfiguration getReactQueueConfiguration();
  
  public abstract String getSourceURL();
  
  public abstract boolean hasNativeModule(Class paramClass);
  
  public abstract boolean hasRunJSBundle();
  
  public abstract void initialize();
  
  public abstract void invokeCallback(int paramInt, NativeArrayInterface paramNativeArrayInterface);
  
  public abstract boolean isDestroyed();
  
  public abstract void registerSegment(int paramInt, String paramString);
  
  public abstract void removeBridgeIdleDebugListener(NotThreadSafeBridgeIdleDebugListener paramNotThreadSafeBridgeIdleDebugListener);
  
  public abstract void runJSBundle();
  
  public abstract void setGlobalVariable(String paramString1, String paramString2);
  
  public abstract void setTurboModuleManager(JSIModule paramJSIModule);
}