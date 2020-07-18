package com.facebook.react.turbomodule.core;

import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.react.bridge.JSIModule;
import com.facebook.react.bridge.JavaScriptContextHolder;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.turbomodule.core.interfaces.JSCallInvokerHolder;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
import com.facebook.react.turbomodule.core.interfaces.TurboModuleRegistry;
import com.facebook.soloader.SoLoader;
import com.facebook.upgrade.HybridData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TurboModuleManager
  implements JSIModule, TurboModuleRegistry
{
  @DoNotStrip
  private final HybridData mHybridData = initHybrid(paramJavaScriptContextHolder.getIdentity(), (JSCallInvokerHolderImpl)paramJSCallInvokerHolder, paramTurboModuleManagerDelegate);
  private final Map<String, TurboModule> mTurboModules = new HashMap();
  private final TurboModuleManagerDelegate mTurbomoduleManagerDelegate;
  
  static
  {
    SoLoader.loadLibrary("turbomodulejsijni");
  }
  
  public TurboModuleManager(JavaScriptContextHolder paramJavaScriptContextHolder, TurboModuleManagerDelegate paramTurboModuleManagerDelegate, JSCallInvokerHolder paramJSCallInvokerHolder)
  {
    mTurbomoduleManagerDelegate = paramTurboModuleManagerDelegate;
  }
  
  private native HybridData initHybrid(long paramLong, JSCallInvokerHolderImpl paramJSCallInvokerHolderImpl, TurboModuleManagerDelegate paramTurboModuleManagerDelegate);
  
  private native void installJSIBindings();
  
  protected TurboModule getJavaModule(String paramString)
  {
    if (!mTurboModules.containsKey(paramString))
    {
      TurboModule localTurboModule = mTurbomoduleManagerDelegate.getModule(paramString);
      if (localTurboModule != null)
      {
        ((NativeModule)localTurboModule).initialize();
        mTurboModules.put(paramString, localTurboModule);
      }
    }
    return (TurboModule)mTurboModules.get(paramString);
  }
  
  public TurboModule getModule(String paramString)
  {
    return getJavaModule(paramString);
  }
  
  public Collection getModules()
  {
    return mTurboModules.values();
  }
  
  public boolean hasModule(String paramString)
  {
    return mTurboModules.containsKey(paramString);
  }
  
  public void initialize() {}
  
  public void installBindings()
  {
    installJSIBindings();
  }
  
  public void onCatalystInstanceDestroy()
  {
    Iterator localIterator = mTurboModules.values().iterator();
    while (localIterator.hasNext()) {
      ((NativeModule)localIterator.next()).onCatalystInstanceDestroy();
    }
    mTurboModules.clear();
    mHybridData.resetNative();
  }
}
