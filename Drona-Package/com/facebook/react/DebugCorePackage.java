package com.facebook.react;

import com.facebook.react.bridge.ModuleSpec;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.devsupport.JSCHeapCapture;
import com.facebook.react.module.model.ReactModuleInfoProvider;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Provider;

class DebugCorePackage
  extends LazyReactPackage
{
  DebugCorePackage() {}
  
  public List getNativeModules(final ReactApplicationContext paramReactApplicationContext)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(ModuleSpec.nativeModuleSpec(JSCHeapCapture.class, new Provider()
    {
      public NativeModule get()
      {
        return new JSCHeapCapture(paramReactApplicationContext);
      }
    }));
    return localArrayList;
  }
  
  public ReactModuleInfoProvider getReactModuleInfoProvider()
  {
    return LazyReactPackage.getReactModuleInfoProviderViaReflection(this);
  }
}
