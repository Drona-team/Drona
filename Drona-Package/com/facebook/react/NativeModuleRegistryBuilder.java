package com.facebook.react;

import com.facebook.react.bridge.ModuleHolder;
import com.facebook.react.bridge.NativeModuleRegistry;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.config.ReactFeatureFlags;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NativeModuleRegistryBuilder
{
  private final Map<String, ModuleHolder> mModules = new HashMap();
  private final ReactApplicationContext mReactApplicationContext;
  private final ReactInstanceManager mReactInstanceManager;
  
  public NativeModuleRegistryBuilder(ReactApplicationContext paramReactApplicationContext, ReactInstanceManager paramReactInstanceManager)
  {
    mReactApplicationContext = paramReactApplicationContext;
    mReactInstanceManager = paramReactInstanceManager;
  }
  
  public NativeModuleRegistry build()
  {
    return new NativeModuleRegistry(mReactApplicationContext, mModules);
  }
  
  public void processPackage(ReactPackage paramReactPackage)
  {
    if ((paramReactPackage instanceof LazyReactPackage)) {
      paramReactPackage = ((LazyReactPackage)paramReactPackage).getNativeModuleIterator(mReactApplicationContext);
    } else if ((paramReactPackage instanceof TurboReactPackage)) {
      paramReactPackage = ((TurboReactPackage)paramReactPackage).getNativeModuleIterator(mReactApplicationContext);
    } else {
      paramReactPackage = ReactPackageHelper.getNativeModuleIterator(paramReactPackage, mReactApplicationContext, mReactInstanceManager);
    }
    Object localObject = paramReactPackage.iterator();
    while (((Iterator)localObject).hasNext())
    {
      ModuleHolder localModuleHolder2 = (ModuleHolder)((Iterator)localObject).next();
      paramReactPackage = localModuleHolder2.getName();
      if (mModules.containsKey(paramReactPackage))
      {
        ModuleHolder localModuleHolder1 = (ModuleHolder)mModules.get(paramReactPackage);
        if (localModuleHolder2.getCanOverrideExistingModule())
        {
          mModules.remove(localModuleHolder1);
        }
        else
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("Native module ");
          ((StringBuilder)localObject).append(paramReactPackage);
          ((StringBuilder)localObject).append(" tried to override ");
          ((StringBuilder)localObject).append(localModuleHolder1.getClassName());
          ((StringBuilder)localObject).append(". Check the getPackages() method in MainApplication.java, it might be that module is being created twice. If this was your intention, set canOverrideExistingModule=true. This error may also be present if the package is present only once in getPackages() but is also automatically added later during build time by autolinking. Try removing the existing entry and rebuild.");
          throw new IllegalStateException(((StringBuilder)localObject).toString());
        }
      }
      if ((!ReactFeatureFlags.useTurboModules) || (!localModuleHolder2.isTurboModule())) {
        mModules.put(paramReactPackage, localModuleHolder2);
      }
    }
  }
}
