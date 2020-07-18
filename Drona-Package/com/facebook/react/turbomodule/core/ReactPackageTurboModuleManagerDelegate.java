package com.facebook.react.turbomodule.core;

import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.ReactPackage;
import com.facebook.react.TurboReactPackage;
import com.facebook.react.bridge.CxxModuleWrapper;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class ReactPackageTurboModuleManagerDelegate
  extends TurboModuleManagerDelegate
{
  private final Map<String, TurboModule> mModules = new HashMap();
  private final List<TurboReactPackage> mPackages = new ArrayList();
  private final ReactApplicationContext mReactApplicationContext;
  
  protected ReactPackageTurboModuleManagerDelegate(ReactApplicationContext paramReactApplicationContext, List paramList)
  {
    mReactApplicationContext = paramReactApplicationContext;
    paramReactApplicationContext = paramList.iterator();
    while (paramReactApplicationContext.hasNext())
    {
      paramList = (ReactPackage)paramReactApplicationContext.next();
      if ((paramList instanceof TurboReactPackage)) {
        mPackages.add((TurboReactPackage)paramList);
      }
    }
  }
  
  private TurboModule resolveModule(String paramString)
  {
    if (mModules.containsKey(paramString)) {
      return (TurboModule)mModules.get(paramString);
    }
    Iterator localIterator = mPackages.iterator();
    Object localObject1 = null;
    for (;;)
    {
      Object localObject2;
      ReactApplicationContext localReactApplicationContext;
      if (localIterator.hasNext())
      {
        localObject2 = (TurboReactPackage)localIterator.next();
        localReactApplicationContext = mReactApplicationContext;
      }
      try
      {
        localObject2 = ((TurboReactPackage)localObject2).getModule(paramString, localReactApplicationContext);
        if (localObject1 != null)
        {
          if (localObject2 == null) {
            continue;
          }
          boolean bool = ((NativeModule)localObject2).canOverrideExistingModule();
          if (!bool) {
            continue;
          }
        }
        localObject1 = localObject2;
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
      if ((localObject1 instanceof TurboModule)) {
        mModules.put(paramString, (TurboModule)localObject1);
      } else {
        mModules.put(paramString, null);
      }
      return (TurboModule)mModules.get(paramString);
    }
  }
  
  public CxxModuleWrapper getLegacyCxxModule(String paramString)
  {
    paramString = resolveModule(paramString);
    if (paramString == null) {
      return null;
    }
    if (!(paramString instanceof CxxModuleWrapper)) {
      return null;
    }
    return (CxxModuleWrapper)paramString;
  }
  
  public TurboModule getModule(String paramString)
  {
    paramString = resolveModule(paramString);
    if (paramString == null) {
      return null;
    }
    if ((paramString instanceof CxxModuleWrapper)) {
      return null;
    }
    return paramString;
  }
  
  public static abstract class Builder
  {
    @Nullable
    private ReactApplicationContext mContext;
    @Nullable
    private List<ReactPackage> mPackages;
    
    public Builder() {}
    
    public ReactPackageTurboModuleManagerDelegate build()
    {
      Assertions.assertNotNull(mContext, "The ReactApplicationContext must be provided to create ReactPackageTurboModuleManagerDelegate");
      Assertions.assertNotNull(mPackages, "A set of ReactPackages must be provided to create ReactPackageTurboModuleManagerDelegate");
      return build(mContext, mPackages);
    }
    
    protected abstract ReactPackageTurboModuleManagerDelegate build(ReactApplicationContext paramReactApplicationContext, List paramList);
    
    public Builder setPackages(List paramList)
    {
      mPackages = new ArrayList(paramList);
      return this;
    }
    
    public Builder setReactApplicationContext(ReactApplicationContext paramReactApplicationContext)
    {
      mContext = paramReactApplicationContext;
      return this;
    }
  }
}
