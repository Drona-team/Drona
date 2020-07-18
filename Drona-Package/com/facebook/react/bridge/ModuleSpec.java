package com.facebook.react.bridge;

import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.react.module.annotations.ReactModule;
import javax.inject.Provider;

public class ModuleSpec
{
  private static final String PAGE_KEY = "ModuleSpec";
  private final String mName;
  private final Provider<? extends NativeModule> mProvider;
  @Nullable
  private final Class<? extends NativeModule> mType = null;
  
  private ModuleSpec(Provider paramProvider)
  {
    mProvider = paramProvider;
    mName = null;
  }
  
  private ModuleSpec(Provider paramProvider, String paramString)
  {
    mProvider = paramProvider;
    mName = paramString;
  }
  
  public static ModuleSpec nativeModuleSpec(Class paramClass, Provider paramProvider)
  {
    Object localObject = (ReactModule)paramClass.getAnnotation(ReactModule.class);
    if (localObject == null)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Could not find @ReactModule annotation on ");
      ((StringBuilder)localObject).append(paramClass.getName());
      ((StringBuilder)localObject).append(". So creating the module eagerly to get the name. Consider adding an annotation to make this Lazy");
      FLog.warn("ModuleSpec", ((StringBuilder)localObject).toString());
      return new ModuleSpec(paramProvider, ((NativeModule)paramProvider.get()).getName());
    }
    return new ModuleSpec(paramProvider, ((ReactModule)localObject).name());
  }
  
  public static ModuleSpec nativeModuleSpec(String paramString, Provider paramProvider)
  {
    return new ModuleSpec(paramProvider, paramString);
  }
  
  public static ModuleSpec viewManagerSpec(Provider paramProvider)
  {
    return new ModuleSpec(paramProvider);
  }
  
  public String getName()
  {
    return mName;
  }
  
  public Provider getProvider()
  {
    return mProvider;
  }
  
  public Class getType()
  {
    return mType;
  }
}
