package com.facebook.react.bridge;

import com.facebook.infer.annotation.Assertions;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class JSIModuleRegistry
{
  private final Map<JSIModuleType, JSIModuleHolder> mModules = new HashMap();
  
  public JSIModuleRegistry() {}
  
  public JSIModule getModule(JSIModuleType paramJSIModuleType)
  {
    Object localObject = (JSIModuleHolder)mModules.get(paramJSIModuleType);
    if (localObject != null) {
      return (JSIModule)Assertions.assertNotNull(((JSIModuleHolder)localObject).getJSIModule());
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Unable to find JSIModule for class ");
    ((StringBuilder)localObject).append(paramJSIModuleType);
    throw new IllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  public void notifyJSInstanceDestroy()
  {
    Iterator localIterator = mModules.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if ((JSIModuleType)localEntry.getKey() != JSIModuleType.TurboModuleManager) {
        ((JSIModuleHolder)localEntry.getValue()).notifyJSInstanceDestroy();
      }
    }
  }
  
  public void registerModules(List paramList)
  {
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      JSIModuleSpec localJSIModuleSpec = (JSIModuleSpec)paramList.next();
      mModules.put(localJSIModuleSpec.getJSIModuleType(), new JSIModuleHolder(localJSIModuleSpec));
    }
  }
}
